package org.ofbiz.designer.util;

import java.awt.Graphics;
import javax.swing.JSlider;
import java.awt.event.MouseEvent;

class WFSlider extends JSlider{
  public WFSlider(int a, int b, int c, int d){
    super(a, b, c, d);
  }

  public void processMouseEvent(MouseEvent e){
    super.processMouseEvent(e);
  }

  public void mousePressed(MouseEvent e){
  }

  public void paintComponent(Graphics g){
    super.paintComponent(g);
    //g.fillRect(0, 0, getWidth(), getHeight());
//LOG.println("parent is " + getParent());
  }

}

