package org.ofbiz.designer.util;

import java.awt.event.*;
import java.awt.*;

public class ContainedDraggableJComponent extends DraggableJComponent {
  protected Container container;

  public ContainedDraggableJComponent(Container containerIn) {	  container = containerIn;
  }

  public void handleDrag(MouseEvent e) {
    super.handleDrag(e);
    if(container!=null) {
      int newX = getX();
      int newY = getY();

      if(newX<0)
        newX=0;
      if(newY<0)
        newY=0;
      if((newX+getWidth())>(container.getWidth()))
        newX=(container.getWidth()-getWidth());
      if((newY+getHeight())>(container.getHeight()))
        newY=(container.getHeight()-getHeight());

       setLocation(newX,newY);
    }

  }
  /*
  public void setContainer(Container containerIn) {
    container = containerIn;
  }  */


}