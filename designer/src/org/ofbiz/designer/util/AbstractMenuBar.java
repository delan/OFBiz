// comment

package org.ofbiz.designer.util;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class AbstractMenuBar extends JMenuBar implements ActionListener {
	public final static String DYNAMICSUBMENU   = "Dynamic-Sub-Menu";
	public final static String SUBMENU = "Sub-Menu";
	Vector listeners = new Vector();
	JMenuItem[][] menuItems = null;
	protected WFMenu[] menus = null;

	public void setData(String[] menuInfo, String[][][] menuItemInfo) {
		menus = new WFMenu[menuInfo.length];
		menuItems = new JMenuItem[menuInfo.length][];
		String menuAssignedAccelerators = "";
		for (int i=0; i<menuInfo.length; i++) {
			String assignedAccelerators = "";
			menus[i] = new WFMenu(menuInfo[i]);
			{
				int p=0;
				while (true) {
					if (p   >= menuInfo[i].length())
						break;
					char    temp    =   menuInfo[i].charAt(p);
					if (temp >= 'A' && temp <= 'Z') {
						char diff = (char) (temp - 'A');
						temp = (char)('a' + diff);
					}
					if (menuAssignedAccelerators.indexOf(temp) ==    -1) {
						menuAssignedAccelerators    += temp;
						menus[i].setMnemonic(menuInfo[i].charAt(p));
						assignedAccelerators += temp;
						break;
					} else
						p++;
				}
			}
			menuItems[i] = new JMenuItem[menuItemInfo[i].length];
			for (int j=0; j<menuItemInfo[i].length; j++) {
				if (menuItemInfo[i][j][0].equals("")) {
					menuItems[i][j] = null;
					menus[i].addSeparator();
				} else if ( menuItemInfo[i][j][1].startsWith(SUBMENU)) {
					try {
						menuItems[i][j] = (WFSubMenu)Class.forName("workflow.org.ofbiz.designer.util."
																   +menuItemInfo[i][j][1].substring(SUBMENU.length())).newInstance();
					} catch ( Exception e) {
						e.printStackTrace();
						continue;
					}
					menuItems[i][j].setText(menuItemInfo[i][j][0]);

					int p=0;
					while (true) {
						if (p   >= menuItemInfo[i][j][0].length())
							break;
						char    temp    =   menuItemInfo[i][j][0].charAt(p);
						if (temp >= 'A' && temp <= 'Z') {
							char diff = (char) (temp - 'A');
							temp = (char)('a' + diff);
						}
						if (assignedAccelerators.indexOf(temp) ==    -1) {
							assignedAccelerators    += temp;
							menuItems[i][j].setMnemonic(menuItemInfo[i][j][0].charAt(p));
							break;
						} else
							p++;
					}

					menus[i].add(menuItems[i][j]);
				} else if (    menuItemInfo[i][j][1].startsWith(DYNAMICSUBMENU)) {
					String  tempString =    menuItemInfo[i][j][1].substring(DYNAMICSUBMENU.length());
					try {
						Class tempClass = Class.forName("org.ofbiz.designer.util.WFDynamicMenu");
						menuItems[i][j] = (WFDynamicMenu)tempClass.newInstance();
					} catch (   Exception   e) {
						e.printStackTrace();
						continue;
					}
					menuItems[i][j].setText(menuItemInfo[i][j][0]);
					int p=0;
					while (true) {
						if (p   >= menuItemInfo[i][j][0].length())
							break;
						char    temp    =   menuItemInfo[i][j][0].charAt(p);
						if (temp >= 'A' && temp <= 'Z') {
							char diff = (char) (temp - 'A');
							temp = (char)('a' + diff);
						}
						if (assignedAccelerators.indexOf(temp) ==    -1) {
							assignedAccelerators    += temp;
							menuItems[i][j].setMnemonic(menuItemInfo[i][j][0].charAt(p));
							break;
						} else
							p++;
					}
					((WFDynamicMenuInterface)menuItems[i][j]).setDynamicMenuName(tempString);
					menus[i].add(menuItems[i][j]);
				} else {
					menuItems[i][j] = new JMenuItem(menuItemInfo[i][j][0]);
					menuItems[i][j].setActionCommand(menuItemInfo[i][j][1]);

					int p=0;
					while (true) {
						if (p   >= menuItemInfo[i][j][0].length())
							break;
						char    temp    =   menuItemInfo[i][j][0].charAt(p);
						if (temp >= 'A' && temp <= 'Z') {
							char diff = (char) (temp - 'A');
							temp = (char)('a' + diff);
						}
						if (assignedAccelerators.indexOf(temp) ==    -1) {
							assignedAccelerators    += temp;
							menuItems[i][j].setMnemonic(menuItemInfo[i][j][0].charAt(p));
							break;
						} else
							p++;
					}
					menus[i].add(menuItems[i][j]);
				}
			}
			add(menus[i]);
		}
		for (int i=0; i<menuItems.length; i++)
			for (int j=0; j<menuItems[i].length; j++)
				if (menuItems[i][j] != null )
					menuItems[i][j].addActionListener(this);
				//for (int i=0; i<menus.length; i++)
				//menus[i].addActionListener(this);
	}

	public void assignAccelerators(String[] menuInfo, String[][][] menuItemInfo) {
		String menuAssignedAccelerators = "";
		for (int i=0; i<menuInfo.length; i++) {
			String assignedAccelerators = "";
			{
				int p=0;
				while (true) {
					if (p   >= menuInfo[i].length())
						break;
					char    temp    =   menuInfo[i].charAt(p);
					if (temp >= 'A' && temp <= 'Z') {
						char diff = (char) (temp - 'A');
						temp = (char)('a' + diff);
					}
					if (menuAssignedAccelerators.indexOf(temp) ==    -1) {
						menuAssignedAccelerators    += temp;
						menus[i].setMnemonic(menuInfo[i].charAt(p));
						assignedAccelerators += temp;
						break;
					} else
						p++;
				}
			}
			for (int j=0; j<menuItemInfo[i].length; j++) {
				if (menuItemInfo[i][j][1].startsWith(SUBMENU)) {
					int p=0;
					while (true) {
						if (p   >= menuItemInfo[i][j][0].length())
							break;
						char    temp    =   menuItemInfo[i][j][0].charAt(p);
						if (temp >= 'A' && temp <= 'Z') {
							char diff = (char) (temp - 'A');
							temp = (char)('a' + diff);
						}
						if (assignedAccelerators.indexOf(temp) ==    -1) {
							assignedAccelerators    += temp;
							menuItems[i][j].setMnemonic(menuItemInfo[i][j][0].charAt(p));
							break;
						} else
							p++;
					}
				} else if (    menuItemInfo[i][j][1].startsWith(DYNAMICSUBMENU)) {
					String  tempString =    menuItemInfo[i][j][1].substring(DYNAMICSUBMENU.length());
					int p=0;
					while (true) {
						if (p   >= menuItemInfo[i][j][0].length())
							break;
						char    temp    =   menuItemInfo[i][j][0].charAt(p);
						if (temp >= 'A' && temp <= 'Z') {
							char diff = (char) (temp - 'A');
							temp = (char)('a' + diff);
						}
						if (assignedAccelerators.indexOf(temp) ==    -1) {
							assignedAccelerators    += temp;
							menuItems[i][j].setMnemonic(menuItemInfo[i][j][0].charAt(p));
							break;
						} else
							p++;
					}
				} else {
					int p=0;
					while (true) {
						if (p   >= menuItemInfo[i][j][0].length())
							break;
						char    temp    =   menuItemInfo[i][j][0].charAt(p);
						if (temp >= 'A' && temp <= 'Z') {
							char diff = (char) (temp - 'A');
							temp = (char)('a' + diff);
						}
						if (assignedAccelerators.indexOf(temp) ==    -1) {
							assignedAccelerators    += temp;
							menuItems[i][j].setMnemonic(menuItemInfo[i][j][0].charAt(p));
							break;
						} else
							p++;
					}
				}
			}
		}
	}

	public void addActionListener(ActionListener listener) {
		if (listeners.contains(listener))
			return;
		listeners.addElement(listener);
	}

	public void removeActionListener(ActionListener listener) {
		listeners.removeElement(listener);
	}

	public void actionPerformed(ActionEvent e) {
		for ( int i=0; i < listeners.size(); i++) {
			((ActionListener)listeners.elementAt(i)).actionPerformed(e);
		}
	}

	public  void updateDynamicMenu(String   menuName,   String[]    menuItemNames,  String[]    menuItemCommands) {
		for (int i=0; i<menus.length;   i++)
			if (menus[i]    instanceof WFMenu)
				((WFMenu)menus[i]).updateDynamicMenu(menuName,  menuItemNames,  menuItemCommands);
	}


	public void paintComponent(Graphics g) {
//DEBUG		
//for (int i=0; i<menus.length; i++)
//System.err.println("menus[" + i + "] is " + (menus[i].isVisible()?"visible":"not visible"));	
		super.paintComponent(g);
	}
}
