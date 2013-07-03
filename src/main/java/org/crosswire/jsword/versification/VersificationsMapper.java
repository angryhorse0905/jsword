package org.crosswire.jsword.versification;

import org.crosswire.common.config.ConfigException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.system.Versifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @author chrisburrell
 */
public class VersificationsMapper {
    private static final Versification KJV = Versifications.instance().getVersification(Versifications.DEFAULT_V11N);
    private static final Logger LOGGER = LoggerFactory.getLogger(VersificationsMapper.class);
    private static final Map<Versification,VersificationToKJVMapper> MAPPERS = new HashMap<Versification,VersificationToKJVMapper>();
    private static volatile VersificationsMapper INSTANCE = null;

    /**
     * Prevent instantiation
     */
    private VersificationsMapper() {
        // we have no mapper for the KJV, since everything maps map to the KJV, so we'll simply add an entry
        // in there to avoid ever trying to load it
        MAPPERS.put(KJV, null);
    }

    /**
     * @return a singleton instance of the mapper - 
     */
    public static VersificationsMapper instance() {
        if (INSTANCE == null) {
            synchronized (VersificationsMapper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new VersificationsMapper();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * @param v
     *            the verse
     * @param targetVersification
     *            the final versification that we want
     */
    public Key map(Verse v, Versification targetVersification) {
        if (v.getVersification().equals(targetVersification)) {
            return v;
        }

        ensure(v.getVersification());
        ensure(targetVersification);

        // caution, mappers can be null if they are missing or failed to load.
        // get the source mapper, to get to the KJV
        VersificationToKJVMapper mapper = MAPPERS.get(v.getVersification());

        // mapped verses could be more than 1 verse in KJV
        List<QualifiedKey> kjvVerses;
        if (mapper == null) {
            // we can't map to the KJV, so we're going to take a wild guess and
            // return the equivalent verse
            // and assume that it maps directly on to the KJV, and thereby
            // continue with the process
            kjvVerses = new ArrayList<QualifiedKey>();
            kjvVerses.add(new QualifiedKey(new Verse(KJV, v.getBook(), v.getChapter(), v.getVerse())));
        } else {
            //we need qualified keys back, so as to preserve parts
            kjvVerses = mapper.map(new QualifiedKey(v));
        }

        if (KJV.equals(targetVersification)) {
            // we're done, so simply return the key we have so far.
            return getKeyFromQualifiedKeys(KJV, kjvVerses);
        }

        // we're continuing, so we need to unmap from the KJV qualified key onto
        // the new versification.
        VersificationToKJVMapper targetMapper = MAPPERS.get(targetVersification);
        if (targetMapper == null) {
            // failed to load, so we'll do our wild-guess again, and assume that
            // the KJV keys map to the
            // target
            return guessKeyFromKjvVerses(targetVersification, kjvVerses);
        }

        // we can use the unmap method for that. Since we have a list of
        // qualified keys, we do so for every qualified
        // key in the list - this means that parts would get transported as
        // well.
        Key finalKeys = PassageKeyFactory.instance().createEmptyKeyList(targetVersification);
        for (QualifiedKey qualifiedKey : kjvVerses) {
            finalKeys.addAll(targetMapper.unmap(qualifiedKey));
        }
        return finalKeys;
    }

    /**
     * This is a last attempt at trying to get something, on the basis that
     * something is better than nothing.
     * 
     * @param targetVersification
     *            the target versification
     * @param kjvVerses
     *            the verses in the KJV versification.
     * @return the possible verses in the target versification, no guarantees
     *         made
     */
    private Key guessKeyFromKjvVerses(final Versification targetVersification, final List<QualifiedKey> kjvVerses) {
        final Key finalKeys = PassageKeyFactory.instance().createEmptyKeyList(targetVersification);
        try {
            for (QualifiedKey qualifiedKey : kjvVerses) {
                if (qualifiedKey.getKey() != null) {
                    finalKeys.addAll(PassageKeyFactory.instance().getKey(targetVersification, qualifiedKey.getKey().getOsisRef()));
                }
            }
            return finalKeys;
        } catch (NoSuchKeyException ex) {
            // we swallow the exception, as we've already alerted that we failed
            // to load the missing resources.
            LOGGER.trace(ex.getMessage(), ex);
            return finalKeys;
        }
    }

    /**
     * @param kjvVerses
     *            the list of keys
     * @return the aggregate key
     */
    private Key getKeyFromQualifiedKeys(Versification versification, final List<QualifiedKey> kjvVerses) {
        final Key finalKey = PassageKeyFactory.instance().createEmptyKeyList(versification);
        for (QualifiedKey k : kjvVerses) {
            // we simply ignore everything else at this stage. The other bits
            // and pieces are used while we're converting
            // from one to the other.
            if (k.getKey() != null) {
                finalKey.addAll(k.getKey());
            }
        }
        return finalKey;
    }

    /**
     * Reads the mapping from file if it does not exist
     * 
     * @param versification
     *            the versification we want to look
     */
    private void ensure(final Versification versification) {
        if (MAPPERS.containsKey(versification)) {
            return;
        }
        
        try {
            MAPPERS.put(versification, new VersificationToKJVMapper(versification, new FileVersificationMapping(versification)));
        } catch (IOException e) {
            // we've attempted to load it once, and that's all we'll do.
            LOGGER.error("Failed to load versification mappings for versification [{}]", versification, e);
            MAPPERS.put(versification, null);
        } catch (ConfigException e) {
            // we've attempted to load it once, and that's all we'll do.
            LOGGER.error("Failed to load versification mappings for versification [{}]", versification, e);
            MAPPERS.put(versification, null);
        } catch (MissingResourceException e) {
            // we've attempted to load it once, and that's all we'll do.
            LOGGER.error("Failed to load versification mappings for versification [{}]", versification, e);
            MAPPERS.put(versification, null);
        }
    }
}