package org.ofbiz.designer.util;

import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Dimension;
import java.util.Vector;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

public class WFPopup
extends JPopupMenu
implements ActionListener, WFDynamicMenuInterface

{
    // {label, actionCommand}

    public final static String SUBMENU = "Sub-Menu";
    public final static String DYNAMICSUBMENU = "Dynamic-Sub-Menu";
    public static WFPopup lastPopup = null;

    Vector listeners = new Vector();

    JMenuItem[] menuItems = null;
    String[][] menuItemInfo = null;

    public WFPopup(String[][] menuItemInfo, String packageName) {
        super();
        this.menuItemInfo = menuItemInfo;
        menuItems = new JMenuItem[menuItemInfo.length];
        String assignedAccelerators = "";
        for(int i=0; i<menuItemInfo.length; i++) {
            if(menuItemInfo[i][0].equals("")) addSeparator();
            else if(menuItemInfo[i][1].startsWith(SUBMENU)) {
                try {
                    Class tempClass = Class.forName(packageName + "." + menuItemInfo[i][1].substring(SUBMENU.length()));
                    //LOG.println("Class name is: "+menuItemInfo[i][1].substring(SUBMENU.length()));
                    //LOG.println("Class is: "+tempClass);
                    menuItems[i] = (WFSubMenu)tempClass.newInstance();
                } catch(Exception e) {
                    e.printStackTrace();
                    continue;
                }
                menuItems[i].setText(menuItemInfo[i][0]);

                int p=0;
                while(true) {
                    if(p >= menuItemInfo[i][0].length())
                        break;
                    char temp   =   menuItemInfo[i][0].charAt(p);
                    if(temp >= 'A' && temp <= 'Z') {
                        char diff = (char) (temp - 'A');
                        temp = (char)('a' + diff);
                    }
                    if(assignedAccelerators.indexOf(temp) ==    -1) {
                        assignedAccelerators    += temp;
                        menuItems[i].setMnemonic(menuItemInfo[i][0].charAt(p));
                        break;
                    } else
                        p++;
                }

                add(menuItems[i]);
            } else if(menuItemInfo[i][1].startsWith(DYNAMICSUBMENU)) {
                String tempString = menuItemInfo[i][1].substring(DYNAMICSUBMENU.length());
                try {
                    //          Class tempClass = Class.forName(packageName + "." + tempString);
                    Class tempClass = Class.forName("org.ofbiz.designer.util.WFDynamicMenu");
                    menuItems[i] = (WFDynamicMenu)tempClass.newInstance();
                    ((WFDynamicMenu)menuItems[i]).setDynamicMenuName(menuItemInfo[i][0]);
                } catch(Exception e) {
                    e.printStackTrace();
                    continue;
                }
                menuItems[i].setText(menuItemInfo[i][0]);

                int p = 0;
                while(true) {
                    if(p >= menuItemInfo[i][0].length())
                        break;
                    char temp   =   menuItemInfo[i][0].charAt(p);
                    if(temp >= 'A' && temp <= 'Z') {
                        char diff = (char) (temp - 'A');
                        temp = (char)('a' + diff);
                    }
                    if(assignedAccelerators.indexOf(temp) == -1) {
                        assignedAccelerators += temp;
                        menuItems[i].setMnemonic(menuItemInfo[i][0].charAt(p));
                        break;
                    } else
                        p++;
                }

                ((WFDynamicMenuInterface)menuItems[i]).setDynamicMenuName(tempString);
                add(menuItems[i]);
            } else {
                menuItems[i] = new JMenuItem(menuItemInfo[i][0]);
                menuItems[i].setActionCommand(menuItemInfo[i][1]);

                int p=0;
                while(true) {
                    if(p   >= menuItemInfo[i][0].length())
                        break;
                    char    temp    =   menuItemInfo[i][0].charAt(p);
                    if(temp >= 'A' && temp <= 'Z') {
                        char diff = (char) (temp - 'A');
                        temp = (char)('a' + diff);
                    }
                    if(assignedAccelerators.indexOf(temp) ==    -1) {
                        assignedAccelerators    += temp;
                        menuItems[i].setMnemonic(menuItemInfo[i][0].charAt(p));
                        break;
                    } else
                        p++;
                }

                add(menuItems[i]);
            }
        }
        listenToMenus();
    }

    public void addActionListener(ActionListener listener) {
        if(listeners.contains(listener))
            return;
        listeners.addElement(listener);
    }

    public void removeActionListener(ActionListener listener) {
        listeners.removeElement(listener);
    }


    public void updateDynamicMenu(String menuName, String[] menuItemNames, String[] menuItemCommands) {
        for(int i=0; i<menuItems.length; i++)
            if(menuItems[i] instanceof WFDynamicMenuInterface)
                ((WFDynamicMenuInterface)menuItems[i]).updateDynamicMenu(menuName, menuItemNames, menuItemCommands);
    }
    public void setDynamicMenuName(String menuName) {
        return;  // this is not needed here
    }

    private void listenToMenus() {
        for(int j=0; j<menuItems.length; j++)
            if(menuItems[j] != null)
                menuItems[j].addActionListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        for(int i=0; i < listeners.size(); i++) {
            //LOG.println("Notifying listener: "+listeners.elementAt(i));
            ((ActionListener)listeners.elementAt(i)).actionPerformed(e);
        }
    }

    public void setVisible(boolean value) {
        if(lastPopup != null) {
            // This is a bug workaround.  When setting visibility of a popup menu to false, 
            // the current swing API leaves sub-menus visible. 
            MenuElement[] newME = {MenuSelectionManager.defaultManager().getSelectedPath()[0]};
            MenuSelectionManager.defaultManager().setSelectedPath(newME);
        }
        super.setVisible(value);
        if(value == false)
            lastPopup = null;
    }

    public void doClick(String actionCommand) {
        for(int i=0; i<menuItems.length; i++)
            if(menuItems[i].getActionCommand().equals(actionCommand)) {
                menuItems[i].doClick();
                return;
            }
    }

    int x, y;
    public void show(Component comp, int _x, int _y) {
        x = _x;
        y = _y;

        try {
            int screenMargin = 100;
            super.show(comp, _x, _y);
            Dimension screenSize = getToolkit().getScreenSize();
            Dimension bounds = getSize();
            Point location = getLocationOnScreen();
            Point parentLocation = comp.getLocationOnScreen();
            int newx = _x;
            int newy = _y;
            if(screenSize.getHeight() - screenMargin - location.y < bounds.getHeight())
                newy = (int)(screenSize.getHeight() - screenMargin - bounds.getHeight() - parentLocation.y);
            if(screenSize.getWidth() - screenMargin - location.x < bounds.getWidth())
                newx = (int)(screenSize.getWidth() - screenMargin - bounds.getWidth() - parentLocation.x);
            if(_x != newx || _y != newy)
                super.show(comp, newx, newy);
            lastPopup = this;
        } catch(Exception e) {
            // component error
            System.err.println("WFPopup::show - component may be invisible");
            // possibly blinking component
        }
    }
}

/*
  The following class defines a global static list for handling the display of all menus within the application
*/

/*
class AllMenus{
  private static Vector menus = new Vector();
  public static void addMenu(Component menu){
    if (!(menus.contains(menu)))
      menus.addElement(menu);
    }
  
   public static void removeMenu(Component menu){
     if (!(menus.contains(menu)))
       menus.addElement(menu);
   }
   
   public static void hideAll(){
     //for (int i=0; i<menus; i++){
     //}
   }
}
*/