package org.ofbiz.designer.util;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;

public class WFSubMenu
	extends JMenu
{

	public final static String SUBMENU = "Sub-Menu";
	String[][] menuItemInfo;
	JMenuItem[] menuItems = null;

	public WFSubMenu() {
		super();
	}

	protected void setMenu( String[][] menuItemInfo) {
		menuItems = new JMenuItem[menuItemInfo.length];
		String assignedAccelerators = "";
		for( int i=0; i<menuItems.length; i++){
			if (menuItemInfo[i][0].equals("")) {
				menuItems[i] = null;
				addSeparator();
			} else if (menuItemInfo[i][1].startsWith(SUBMENU)) {
				try {
					menuItems[i]=(JMenu)Class.forName("workflow.org.ofbiz.designer.util."+menuItemInfo[i][1].
																						  substring(SUBMENU.length())).newInstance();
				} catch ( Exception e) {
					e.printStackTrace();
					continue;
				}
				menuItems[i].setText(menuItemInfo[i][0]);
				
				int p=0;
				while	(true){
					if (p	>= menuItemInfo[i][0].length())
						break;
					char	temp	=	menuItemInfo[i][0].charAt(p);
					if (temp >= 'A' && temp <= 'Z'){
						char diff = (char) (temp - 'A');
						temp = (char)('a' + diff);
					}
					if (assignedAccelerators.indexOf(temp) ==	 -1){
						assignedAccelerators	+= temp;
						menuItems[i].setMnemonic(menuItemInfo[i][0].charAt(p));
						break;
					}
					else 
						p++;
				}
				
				add(menuItems[i]);
			} else {
				menuItems[i] = new JMenuItem(menuItemInfo[i][0]);
				menuItems[i].setActionCommand(menuItemInfo[i][1]);
				
				int p=0;
				while	(true){
					if (p	>= menuItemInfo[i][0].length())
						break;
					char	temp	=	menuItemInfo[i][0].charAt(p);
					if (temp >= 'A' && temp <= 'Z'){
						char diff = (char) (temp - 'A');
						temp = (char)('a' + diff);
					}
					if (assignedAccelerators.indexOf(temp) ==	 -1){
						assignedAccelerators	+= temp;
						menuItems[i].setMnemonic(menuItemInfo[i][0].charAt(p));
						break;
					}
					else 
						p++;
				}
				
				add(menuItems[i]);
			}
		}
	}

	public void addActionListener(ActionListener listener){
		for (int i=0; i<menuItems.length; i++)
			if (menuItems[i] != null )
				menuItems[i].addActionListener(listener);
	}
/*
  public void setPopupMenuVisible(boolean setVisibleTrue){
    if (!setVisibleTrue)
      new Throwable().printStackTrace();
    super.setPopupMenuVisible(setVisibleTrue);
  }
  */
  /*
  public void setPopupMenuVisible(boolean value){
    super.setPopupMenuVisible(value);
    for (int i=0; i< menuItems.length; i++){
      if (menuItems[i] instanceof JMenu){
        ((WFSubMenu)menuItems[i]).setPopupMenuVisible(false);
      }
    }
  }
  */
   
}

