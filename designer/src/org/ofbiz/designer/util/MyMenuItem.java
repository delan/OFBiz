package org.ofbiz.designer.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyMenuItem extends JMenuItem{
	public WFList list = null;
	
	public void processMouseEvent(MouseEvent e){
		list.processMouseEvent(e);
	}
	
	public void processEvent(AWTEvent e){
		if (e instanceof MouseEvent){
		}
		else {
			//super.processEvent(e);
		}
	}
}
