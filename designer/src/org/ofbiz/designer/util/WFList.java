package org.ofbiz.designer.util;

import javax.swing.JList;
import javax.swing.ListModel;
import java.util.Vector;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.AWTEvent;
import java.awt.event.FocusEvent;

public class WFList extends JList{
	private WFPopup popup = null;

	public WFList(Vector listData){
		super(listData.toArray());
	}
	
	public WFList(ListModel model){
		super(model);
	}
	
	public int listSize(){
		return getModel().getSize();
	}
	
	public void processEvent(AWTEvent e){
		if (e instanceof MouseEvent && e.getID() == MouseEvent.MOUSE_PRESSED){
				super.processEvent(e);
		}
		else {
			if (!(e instanceof FocusEvent))
				super.processEvent(e);
		}
	}
	
	public void setSelectedIndex(String prefix){
		int tempIndex = 0;
		while (true){
			//System.err.println("model size  is " + getModel().getSize());
			if (tempIndex >=getModel().getSize()) break;
			//System.err.println("getModel().getElementAt(tempIndex) is " + getModel().getElementAt(tempIndex));
			if (getModel().getElementAt(tempIndex) == null) break;
			String tempStr = getModel().getElementAt(tempIndex).toString();
			//System.err.println("prefix is " + prefix);
			if (tempStr.length() == 0) break;
			if (prefix.toUpperCase().compareTo(tempStr.toUpperCase()) <= 0) break;
			tempIndex++;
		}
		if (tempIndex < getModel().getSize())
			super.setSelectedIndex(tempIndex);
	}

	public void setPopup(WFPopup popup){
		this.popup = popup;
	}

	public WFPopup getPopup(){
		return popup;
	}
	
	public void processMouseEvent(MouseEvent e){
		int id = e.getID();
		switch(id) {
		case MouseEvent.MOUSE_CLICKED:
			int mod = e.getModifiers();
			int x = e.getX();
			int y = e.getY();
			if (mod == 4) { // rightclick
				if (popup != null) {
					//popupDisplayPosition = new Point(x, y);
					popup.show(this, x, y);
				}
				break;
			} else if (popup != null && popup.isVisible()) popup.setVisible(false);
			break;
		}
		//LOG.println(2);	  
		super.processMouseEvent(e);
		validate();
	}

}

