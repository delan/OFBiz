package org.ofbiz.designer.util;

import javax.swing.*;
import java.awt.event.*;

public class PopupMouseListener extends MouseAdapter{
	JPopupMenu popup = null;
	
	public PopupMouseListener(WFPopup popup, ActionListener actionListener){
		this.popup = popup;
		for (int i=0; i<popup.getComponentCount(); i++){
			if (!(popup.getComponent(i) instanceof JMenuItem)) continue;
			JMenuItem menuItem = (JMenuItem)popup.getComponent(i);
			menuItem.addActionListener(actionListener);
		}
		//component.add(popup);
	}
	public PopupMouseListener(String[] menuItems, JComponent component, ActionListener actionListener){
		popup = new JPopupMenu();
		for (int i=0; i<menuItems.length; i++){
			JMenuItem menuItem = new JMenuItem(menuItems[i]);
			popup.add(menuItem);
			menuItem.addActionListener(actionListener);
		}
		component.add(popup);
	}
	
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}
	
	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger())  popup.show(e.getComponent(), e.getX(), e.getY());
	}
}