package org.ofbiz.designer.util;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


public class DraggableJComponent extends JComponent {

	protected boolean dragEnabled;

	protected int oldMouseX;
	protected int oldMouseY;

	public DraggableJComponent() {

		dragEnabled = true;

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {

				if(dragEnabled) {
                                handleDrag(e);
				}
			}});

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				oldMouseX = e.getX();
				oldMouseY = e.getY();

			}
		});

	}

	public void setDragEnabled(boolean val) {
		dragEnabled = val;
	}

	public boolean getDragEnabled() {
		return dragEnabled;
	}

    public void handleDrag(MouseEvent e) {
        
		int currTaskX = getX();
		int currTaskY = getY();

		int xDiff = e.getX() - oldMouseX;
		int yDiff = e.getY() - oldMouseY;

		setLocation(currTaskX+xDiff,currTaskY+yDiff);
	}
}

