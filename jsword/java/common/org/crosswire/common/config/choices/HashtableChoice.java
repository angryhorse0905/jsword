
package org.crosswire.common.config.choices;

import java.util.Hashtable;

import org.crosswire.common.util.Convert;

/**
* IntegerChoice.
*
* <table border='1' cellPadding='3' cellSpacing='0' width="100%">
* <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
* Distribution Licence:<br />
* Project B is free software; you can redistribute it
* and/or modify it under the terms of the GNU General Public License,
* version 2 as published by the Free Software Foundation.<br />
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.<br />
* The License is available on the internet
* <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
* <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
* MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
* The copyright to this program is held by it's authors.
* </font></td></tr></table>
* @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
* @see <{docs.Licence}>
* @author Joe Walker
*/
public abstract class HashtableChoice extends AbstractChoice
{
    /**
    * Construct an Object Choice
    */
    public HashtableChoice(Class superclass)
    {
        this.superclass = superclass;
    }

    /**
    * Construct an Object Choice
    */
    public HashtableChoice()
    {
        this.superclass = Object.class;
    }

    /**
    * Generalized read Object from the Properties file
    * @return Found int or the default value
    */
    public abstract Hashtable getHashtable();

    /**
    * Generalized set Object to the Properties file
    * @param value The value to enter
    */
    public abstract void setHashtable(Hashtable value);

    /**
    * Generalized read boolean from the Properties file
    * @return Found boolean or the default value
    */
    public String getString()
    {
        return Convert.hashtable2String(getHashtable());
    }

    /**
    * Generalized set boolean to the Properties file
    * @param value The value to enter
    */
    public void setString(String value)
    {
        setHashtable(Convert.string2Hashtable(value));
    }

    /**
    * Override this to check and note any change
    */
    public String getType()
    {
        return "hash";
        // new HashtableField(this, superclass);
    }

    /**
    * The type that we are editing
    */
    public Object getTypeOptions()
    {
        return superclass;
    }

    /** The superclass of any hashtable values */
    private Class superclass = null;
}