package org.ofbiz.designer.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JMenuItem;

public class WFDynamicMenu extends WFMenu implements WFDynamicMenuInterface,  ActionListener{
    private Vector listeners = new Vector();
    private String myMenuName = "WFDynamicMenu";
    private JMenuItem[] menuItems = new JMenuItem[0];

    public WFDynamicMenu() {
        super("WFDynamicMenu");
    }

    public void updateDynamicMenu(String menuName, String[] menuItemNames, String[] menuItemCommands) {
        if(menuItemNames.length != menuItemCommands.length) throw new RuntimeException("Bad parameters !!");
        if(!myMenuName.equals(menuName)) {
            for(int i=0; i<menuItems.length; i++) {
                if(menuItems[i] instanceof WFDynamicMenuInterface) {
                    ((WFDynamicMenuInterface)menuItems[i]).updateDynamicMenu(menuName, menuItemNames, menuItemCommands);
                }
            }
            return;
        }
        // remove old listeners
        if(menuItems != null) {
            for(int i=0; i < menuItems.length; i++) {
                if(menuItems[i] == null) continue;
                menuItems[i].removeActionListener(this);
                remove(menuItems[i]);
            }
        }
        menuItems = new JMenuItem[menuItemNames.length];

        String assignedAccelerators = "";
        char tempParentMnemonic = (char)getMnemonic();  
        if(tempParentMnemonic >= 'A' && tempParentMnemonic <= 'Z') {
            char diff = (char) (tempParentMnemonic - 'A');
            tempParentMnemonic = (char)('a' + diff);
        }
        assignedAccelerators += tempParentMnemonic; 
        for(int i=0; i < menuItemNames.length; i++) {
            if(menuItemCommands[i] == null) {
                continue;
            } else if(menuItemCommands[i].startsWith(WFPopup.DYNAMICSUBMENU)) {
                String tempString = menuItemCommands[i].substring(WFPopup.DYNAMICSUBMENU.length());
                try {
                    Class tempClass = Class.forName("org.ofbiz.designer.util.WFDynamicMenu");
                    menuItems[i] = (WFDynamicMenu)tempClass.newInstance();
                    ((WFDynamicMenu)menuItems[i]).setDynamicMenuName(menuItemNames[i]);
                } catch(Exception e) {
                    e.printStackTrace();
                    continue;
                }
                menuItems[i].setText(menuItemNames[i]);
            } else {
                menuItems[i] = new JMenuItem(menuItemNames[i]);
            }

            int p=0;
            while(true) {
                if(p   >= menuItemNames[i].length())
                    break;
                char    temp    =   menuItemNames[i].charAt(p);
                if(temp >= 'A' && temp <= 'Z') {
                    char diff = (char) (temp - 'A');
                    temp = (char)('a' + diff);
                }
                if(assignedAccelerators.indexOf(temp) ==    -1) {
                    assignedAccelerators    += temp;
                    menuItems[i].setMnemonic(menuItemNames[i].charAt(p));
                    break;
                } else
                    p++;
            }
            menuItems[i].setActionCommand(menuItemCommands[i]);
            add(menuItems[i]);
        }
        //		}
        // add new listeners
        for(int i=0; i < menuItems.length; i++) {
            if(menuItems[i] != null) 
                menuItems[i].addActionListener(this);
        }
    }

    public void setDynamicMenuName( String menuName ) {
        myMenuName = menuName;
    }

    public void addActionListener(ActionListener listener) {
        if(!listeners.contains(listener))
            listeners.addElement(listener);
    }

    public void removeActionListener(ActionListener listener) {
        listeners.removeElement(listener);
    }

    public void actionPerformed(ActionEvent e) {
        for(int i=0; i < listeners.size(); i++)
            ((ActionListener)listeners.elementAt(i)).actionPerformed(e);
    }
}


