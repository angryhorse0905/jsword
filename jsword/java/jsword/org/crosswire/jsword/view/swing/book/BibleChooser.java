
package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.Bibles;
import org.crosswire.jsword.book.BookException;
import org.crosswire.common.util.LogicError;

/**
* BibleChooser is like JFileChooser except that it allows the user to
* select one of the available Bibles.
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
* @version D0.I0.T0
*/
public class BibleChooser extends JPanel
{
    /**
    * Basic constructor
    */
    public BibleChooser()
    {
        jbInit();
    }

    /**
    * Initializa all the GUI components
    */
    private void jbInit()
    {
        pnl_bibles.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnl_bibles.setLayout(new BorderLayout());
        pnl_bibles.add(scr_bibles, BorderLayout.CENTER);
        scr_bibles.setViewportView(lst_bibles);
        lst_bibles.setModel(bmod);
        lst_bibles.setCellRenderer(new BibleListCellRenderer());
        lst_bibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lst_bibles.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent ev) { selection(); }
        });

        btn_ok.setText("OK");
        btn_ok.setMnemonic('o');
        btn_ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { ok(); }
        });
        btn_ok.setEnabled(selected != null);
        btn_ok.setDefaultCapable(true);

        btn_cancel.setText("Cancel");
        btn_cancel.setMnemonic('C');
        btn_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { cancel(); }
        });

        btn_help.setText("Help");
        btn_help.setMnemonic('H');
        btn_help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) { help(); }
        });
        btn_help.setEnabled(false);

        pnl_buttons.setLayout(new FlowLayout());
        pnl_buttons.add(btn_ok);
        pnl_buttons.add(btn_cancel);
        pnl_buttons.add(btn_help);

        this.setLayout(new BorderLayout());
        this.add(pnl_bibles, BorderLayout.CENTER);
        this.add(pnl_buttons, BorderLayout.SOUTH);
    }

    /**
    * Display the BibleChooser in a modal dialog
    */
    public int showDialog(Component parent)
    {
        Frame frame = (parent instanceof Frame)
                      ? (Frame) parent
                      : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        dialog = new JDialog(frame, title, true);

        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent ev) { cancel(); }
        });

        dialog.setVisible(true);

        return reply;
    }

    /**
    * Sets the string that goes in the FileChooser window's title bar.
    * @see #getDialogTitle
    */
    public void setDialogTitle(String title)
    {
        this.title = title;
    }

    /**
    * Gets the string that goes in the FileChooser's titlebar.
    * @see #setDialogTitle
    */
    public String getDialogTitle()
    {
        return title;
    }

    /**
    * Returns the selected Bible.
    * @return the selected Bible
    */
    public Bible getSelectedBible()
    {
        try
        {
            String name = bmod.getBibleName(lst_bibles.getSelectedValue());
            return Bibles.getBible(name);
        }
        catch (BookException ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
    * When the list selection changes
    */
    public void selection()
    {
        selected = (String) lst_bibles.getSelectedValue();
        btn_ok.setEnabled(selected != null);
    }

    /**
    * OK is selected
    */
    public void ok()
    {
        reply = APPROVE_OPTION;
        dialog.setVisible(false);
    }

    /**
    * Cancel is selected
    */
    public void cancel()
    {
        reply = CANCEL_OPTION;
        dialog.setVisible(false);
    }

    /**
    * Not implemented
    */
    public void help()
    {
    }

    /** Return value if cancel is chosen */
    public static final int CANCEL_OPTION = 1;

    /** Return value if approve (yes, ok) is chosen */
    public static final int APPROVE_OPTION = 0;

    /** Return value if an error occured */
    public static final int ERROR_OPTION = -1;

    /** The name of the selected Bible */
    private String selected = null;

    /** The way the dialog was closed */
    private int reply = CANCEL_OPTION;

    /** The title of the dialog */
    private String title = "Select a Bible";

    /** The Bible list model */
    private BiblesListModel bmod = new BiblesListModel();

    /* GUI Componenets */
    private JDialog dialog;
    private JPanel pnl_bibles = new JPanel();
    private JScrollPane scr_bibles = new JScrollPane();
    private JList lst_bibles = new JList();

    private JPanel pnl_buttons = new JPanel();
    private JButton btn_ok = new JButton();
    private JButton btn_cancel = new JButton();
    private JButton btn_help = new JButton();
}