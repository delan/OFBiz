package org.ofbiz.designer.util;

import java.awt.FlowLayout;
import java.awt.Container;

// This class is a workaround for the system calling excessive
// validation on the toolbar.  The first method is thus a dummy
// the actual layout is done by the second method
public class MyFlowLayout extends FlowLayout{
  private boolean firstTime = true;

  public MyFlowLayout(){
    super();
  }

  public MyFlowLayout(int x){
    super(x);
  }

  public void layoutContainer(Container target) {
    if (firstTime) {
      super.layoutContainer(target);
      firstTime = false;
    }
  }

  public void doLayoutContainer(Container target) {
    super.layoutContainer(target);
  }
}