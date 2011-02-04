package bounce;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;



/**
 * loads the the main window, panel, icons, menus, etc
 *
 * @author matthew robertson
 */
public class Bouncer extends JFrame implements ActionListener {
	public static final int WINDOW_X = 1100;
	public static final int WINDOW_Y = 725;


    // widgets
    JLabel status;  // to set status messages
    Table  table;   // to reference the playing table
    /**
     * default constructor.
     * builds the window
     */
    public Bouncer() {
        // always call super
        super();

        // makes window look more natural
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        // set jframe basics
        setTitle("Billiard Table");


        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setSize( WINDOW_X, WINDOW_Y );

        setResizable( true );
        setLocationByPlatform(true);

        // labels
        status = new JLabel("By: Paul George", JLabel.CENTER);
        add(status, BorderLayout.SOUTH);

        // table
        table = new Table(this);
        add(table, BorderLayout.CENTER);


        // menus
        buildMenus();

        // let's see it!
        setVisible(true);
        requestFocus();
    }


    /****************************************
    * private method to build the menus
    ***************************************/
    private void buildMenus() {
        JMenuBar menubar = new JMenuBar();

        // menus
        JMenu file = new JMenu("File"),
              opts = new JMenu("Options"),
              help = new JMenu("Help");

        menubar.add(file);
        menubar.add(opts);
        menubar.add(help);

        // file menu
        addMenuItems(file, "New Game, -, Exit Game");

        // options menu
        addMenuItems(opts, "*Friction, *Aim Help, -, *Anti-Overlap, -");

        // help menu
        addMenuItems(help, "About");

        // lets see it!
        setJMenuBar(menubar);
    }

    /***************************************************************
     * method to add menu items to a menu. it got annoying coding
     * this manually for each item so i wrote this function to do
     * it all in one go. just pass it the menu and a string of the
     * items seperated by commas. use a dash "-" for a seperator,
     * pound "#" for check box, and star "*" for checked check box.
     *
     * @param menu menu to add items too
     * @param items the items
     ***************************************************************/
    private void addMenuItems(JMenu menu, String items) {
        JMenuItem menuitem;

        // loop through items adding them to menu
        for (String s : items.split(", ")) {
            if (s.equals("-"))
                menu.addSeparator();
            else {
                if (s.substring(0, 1).equals("*")) // menu types
                    menuitem = new JCheckBoxMenuItem(s.substring(1), true);
                else if (s.substring(0, 1).equals("#"))
                    menuitem = new JCheckBoxMenuItem(s.substring(1));
                else
                    menuitem = new JMenuItem(s);

                menuitem.addActionListener(this);
                menu.add(menuitem);
            }
        }
    }


    /****************************************
     * sets the label to a status message
     *
     * @param msg message
     ****************************************/
    public void setStatus(String msg) {
        status.setText(msg);
    }



	/***************************************
	*
	*
	*
	***************************************/
	public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();

        if (s.equals("Exit Game"))
            System.exit(0);

        else if (s.equals("New Game"))
           table.populateCircles();

        else if (s.equals("Friction"))
            table.engine.toggleFriction();

        else if (s.equals("Aim Help"))
            table.engine.toggleAimHelp();

        else if (s.equals("Anti-Overlap"))
            table.engine.toggleOverLap();

        else if (s.equals("About")) // about dialog
            JOptionPane.showMessageDialog(this,
                    "Billiard Table, By: Paul George\n" +
                    "Created just for fun\n" +
                    "\n" +
                    "Monday, January 31th, 2011",
                    "About Billiard Table", JOptionPane.DEFAULT_OPTION);

        else
            System.out.println(s);
    }

}
