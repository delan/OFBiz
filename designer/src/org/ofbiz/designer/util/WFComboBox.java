//Title:        MLS Workflow
//Version:      
//Copyright:    Copyright (c) 1998
//Author:       Brian Eppinger
//Company:      NRL/NSA
//Description:  MLS workflow designer


package org.ofbiz.designer.util;

import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WFComboBox
  extends JComboBox {

  Color defaultColor = Color.magenta;
  Color selectColor = Color.lightGray;
  Color pressedColor = Color.green;

  Font defaultFont = null;
  Font hoverFont = null;

  int borderThickness = 1;
  int defaultBorderThickness = 1;
  int hoverBorderThickness = 3;

  boolean pressed = false;

  String[] commands = null;

  // following is used to manage the listener list, since the default java
  // implementation of a JComboBox does not allow you to directly modify
  // the ActionEvent being dispatched, but does so for a JButton
  WFButton clientManager = new WFButton("dummy");

  int shiftState = 0;

  public WFComboBox() {
    super();
    defaultFont = getFont();
    int size = defaultFont.getSize();
    hoverFont = new Font(defaultFont.getName(), defaultFont.PLAIN, size-2);
    //setFont(defaultFont);
    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  }

  public void setData(String[][] cbData) {
    commands = new String[cbData.length];
    for( int i = 0; i < cbData.length; i++) {
      addItem(cbData[i][0]);
      commands[i] = cbData[i][1];
    }

  }

  boolean firsttime = true;
  public void paintComponent(Graphics g){
    if (firsttime){
      defaultColor = getParent().getBackground();
      this.setBackground(defaultColor);
      firsttime = false;
    }
    super.paintComponent(g);
		Rectangle bounds = getBounds();
		int width = bounds.width;
		int height = bounds.height;
    Color backup = g.getColor();
		g.setColor(Color.black);
		drawThickRect(g, 0, 0, width-1, height-1, borderThickness);
    if (pressed){
      for (int i=0; i<4; i++){
        g.drawLine(i, 0, i, height-1);
        g.drawLine(0, i, width-1, i);
      }

    }
    g.setColor(backup);
  }

  void drawThickRect(Graphics g, int x, int y, int width, int height, int thickness){
    for (int i=0; i<thickness; i++)
      g.drawRect(x + i, y + i, width - 2*i, height - 2*i);
  }

  protected void fireActionEvent() {
    clientManager.setActionCommand(commands[getSelectedIndex()]);
    clientManager.setShiftState(shiftState);
    ActionEvent newE = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand());
    clientManager.fireActionPerformed(newE);
  }

  protected void processMouseEvent(MouseEvent e) {
    int id = e.getID();
    switch(id) {
      case MouseEvent.MOUSE_PRESSED:
        mousePressed(e);
        break;
      case MouseEvent.MOUSE_RELEASED:
        mouseReleased(e);
        break;
      case MouseEvent.MOUSE_CLICKED:
        mouseClicked(e);
        break;
      case MouseEvent.MOUSE_EXITED:
        mouseExited(e);
        super.processMouseEvent(e);
        break;
      case MouseEvent.MOUSE_ENTERED:
        mouseEntered(e);
        super.processMouseEvent(e);
        break;
    }
  }

	public void mousePressed(MouseEvent e){
    //setFont(defaultFont);
    pressed = true;
    repaint();
	}

	public void mouseClicked(MouseEvent e){
	}

	public void mouseReleased(MouseEvent e){
    shiftState = e.getModifiers() & e.SHIFT_MASK;
    fireActionEvent();
    //setFont(hoverFont);
    pressed = false;
    repaint();
	}

	public void mouseEntered(MouseEvent e){
    setFont(hoverFont);
    borderThickness = hoverBorderThickness;
    repaint();
	}

	public void mouseExited(MouseEvent e){
    setFont(defaultFont);
    borderThickness = defaultBorderThickness;
    repaint();
	}

  public void addActionListener(ActionListener listener){
    clientManager.addActionListener(listener);
  }

  public void removeActionListener(ActionListener listener){
    clientManager.removeActionListener(listener);
  }
}
