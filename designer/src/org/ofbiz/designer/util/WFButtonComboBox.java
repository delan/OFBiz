package org.ofbiz.designer.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.awt.Polygon;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;


public class WFButtonComboBox
  extends WFButton
  implements ActionListener
{

  //Color defaultColor = Color.magenta;
  //Color selectColor = Color.lightGray;
  //Color pressedColor = Color.green;

  Font defaultFont = null;
  Font hoverFont = null;

  //int borderThickness = 1;
  //int defaultBorderThickness = 1;
  //int hoverBorderThickness = 4;
  int downArrowWidth = 12;

  //boolean pressed = false;
  WFPopup popup = null;

  String[][] data = null;
  private String toolTipText = null;

  // following is used to manage the listener list, since the default java
  // implementation of a JComboBox does not allow you to directly modify
  // the ActionEvent being dispatched, but does so for a JButton
  //WFButton clientManager = new WFButton("dummy");

  int shiftState = 0;

  public WFButtonComboBox(String name) {
    super(name + "      ");
    //super(name);
    defaultFont = getFont();
    int size = defaultFont.getSize();
    hoverFont = new Font(defaultFont.getName(), defaultFont.PLAIN, size-2);
    //setFont(defaultFont);
    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
  }

  public void setData(String[][] p_cbData) {
    popup = new WFPopup(p_cbData, "org.ofbiz.designer.util");
    data = p_cbData;
    String command = getText().trim();
    popup.addActionListener(this);
    for (int i=0; i<data.length; i++) {
      if (command.equals(data[i][0])) {
        setActionCommand(data[i][1]);

        repaint();
        break;
      }
    }
    validate();
  }

  public void actionPerformed(ActionEvent e){
    // to deal with resizing the toolbar buttons while WFButtonComboBox popup
    //   is in use need to make sure the font is at the default size and then
    //   reset at the end of this method
    Font tempFont = getFont();
    setFont(defaultFont);

    String command = e.getActionCommand();
    setActionCommand(command);
    fireActionEvent();
    for (int i=0; i<data.length; i++) {
      if (command.equals(data[i][1])) {
        setText(data[i][0]+"      ");
        JFrame frame = null;
        try {
          frame = (JFrame) getRootPane().getParent();
        } catch (Exception ne) {
          WARNING.println("Could not getRootPane of frame for NetworkEditor");
          break;
        }
        LayoutManager lm = getParent().getLayout();
        if (lm instanceof MyFlowLayout)
          ((MyFlowLayout)lm).doLayoutContainer(getParent());
        else
          getParent().getLayout().layoutContainer(getParent());
        frame.getContentPane().repaint();
        break;
      }
    }
    ActionEvent newE = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, Constants.REDRAW_MENU);
    fireActionPerformed(newE);

    setFont(tempFont);
  }

  boolean firsttime = true;

  public void paintComponent(Graphics g){
	  /*
	  if (firsttime){
	  defaultColor = getParent().getBackground();
	  this.setBackground(defaultColor);
	  firsttime = false;
	  }
	  */    
	  //super.paintComponent(g);
	  int fontSize = g.getFont().getSize();
	  Rectangle bounds = getBounds();
	  int width = bounds.width;
	  int height = bounds.height;
	  
	  g.clearRect(0, 0, width, height);
	  g.drawString(this.getText(), 10, fontSize + (this.getHeight() - fontSize)/2);
	  Color backup = g.getColor();
	  g.setColor(Color.black);
	  //drawThickRect(g, 0, 0, width-1-downArrowWidth, height-1, borderThickness);
	  drawThickShadow(g, 0, 0, width, height, borderThickness);
	  //g.clearRect(width - downArrowWidth, 0, width-1, height-1);
	  /*
	  if (pressed){
	  for (int i=0; i<4; i++){
	  g.drawLine(i, 0, i, height-1);
	  g.drawLine(0, i, width-1, i);
	  }
	  }
	  */    

	  Polygon downArrow = new Polygon();
	  downArrow.addPoint(width-downArrowWidth+2,7);
	  downArrow.addPoint(width-1-2,7);
	  downArrow.addPoint(width-1-downArrowWidth/2,height-7);
	  //g.setColor(Color.blue);
	  g.fillPolygon(downArrow);
	  g.setColor(backup);
  }

  public void TBDpaintComponent(Graphics g){
    super.paintComponent(g);
    Color backup = g.getColor();
		Rectangle bounds = getBounds();
		int width = bounds.width;
		int height = bounds.height;
		g.setColor(Color.black);
		drawThickRect(g, 0, 0, width-1, height-1, borderThickness);
		g.setColor(backup);
  }

  void drawThickRect(Graphics g, int x, int y, int width, int height, int thickness){
    for (int i=0; i<thickness; i++)
      g.drawRect(x + i, y + i, width - 2*i, height - 2*i);
  }

  protected void fireActionEvent() {
    ActionEvent newE = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getActionCommand());
    fireActionPerformed(newE);
  }

//  static int count = 0;
/*
  public void mouseClicked(MouseEvent e) {
    Rectangle bounds = getBounds();
		int width = bounds.width;
		int height = bounds.height;

		int mod = e.getModifiers();
  	int x = e.getX();
		int y = e.getY();
    if (mod == 4 ||x >= (width - downArrowWidth)) {
			if (popup != null) {
        popup.show(this, 0, this.getHeight());
      }
			return;
		} else if (popup != null && popup.isVisible()) popup.setVisible(false);
	}
*/
  public void mousePressed(MouseEvent e) {
	  Rectangle bounds = getBounds();
	  int width = bounds.width;
	  int height = bounds.height;

	  int mod = e.getModifiers();
	  int x = e.getX();
	  int y = e.getY();
	  if (mod == 4 ||x >= (width - downArrowWidth)) {
		  if (popup != null) {
			  popup.show(this, 0, this.getHeight());
			  toolTipText = getToolTipText();
			  setToolTipText(null);			  
		  }
		  return;
	  }
	  else {
		  if (popup != null && popup.isVisible())
			  popup.setVisible(false);
		  setToolTipText(toolTipText);
		  super.mousePressed(e);
	  }
  }

/*
  // need this code so that the menu will stay up if
  public void mouseReleased(MouseEvent e) {
    Rectangle bounds = getBounds();
		int width = bounds.width;
		int height = bounds.height;

		int mod = e.getModifiers();
  	int x = e.getX();
		int y = e.getY();
    if ( ((mod == 4 && x >= 0) || x >= (width - downArrowWidth)) && x <= width
          && y >= 0 && y <= height) {
			if (popup != null) {
        popup.show(this, 0, this.getHeight());
      }
			return;
		}
    else {
      if (popup != null && popup.isVisible())
        popup.setVisible(false);
      super.mouseReleased(e);
    }
	}
*/

  private boolean menuOpened = false;

  protected void processMouseEvent(MouseEvent e) {
	  int id = e.getID();
	  int mod = e.getModifiers();
	  int x = e.getX();
	  int y = e.getY();
	  Rectangle bounds = getBounds();
	  int width = bounds.width;
	  int height = bounds.height;

	  if (id == MouseEvent.MOUSE_PRESSED && x >= (width - downArrowWidth)) {
		  menuOpened = true;
		  mousePressed(e);
	  } else if (id == MouseEvent.MOUSE_RELEASED && menuOpened) {
		  menuOpened = false;
		  mouseReleased(e);
	  } else {
		  super.processMouseEvent(e);
		  return;
	  }
	  /*
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
	  break;
	  case MouseEvent.MOUSE_ENTERED:
	  mouseEntered(e);
	  break;
	  }
	  */
  }
}
