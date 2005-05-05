package org.crosswire.jsword.book.install.sword;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
class FTPMsg extends MsgBase
{
    static final MsgBase AUTH_REFUSED = new FTPMsg("SwordInstaller.AuthRefused"); //$NON-NLS-1$
    static final MsgBase CONNECT_REFUSED = new FTPMsg("SwordInstaller.ConnectRefused"); //$NON-NLS-1$
    static final MsgBase CWD_REFUSED = new FTPMsg("SwordInstaller.CWDRefused"); //$NON-NLS-1$
    static final MsgBase DOWNLOAD_REFUSED = new FTPMsg("SwordInstaller.DownloadRefused"); //$NON-NLS-1$
    static final MsgBase URL_AT_COUNT = new FTPMsg("SwordInstallerFactory.URLAtCount"); //$NON-NLS-1$
    static final MsgBase URL_COLON_COUNT = new FTPMsg("SwordInstallerFactory.URLColonCount"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private FTPMsg(String name)
    {
        super(name);
    }
}