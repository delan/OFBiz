package org.ofbiz.designer.util;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

public abstract class AbstractButtonPanel extends WFPanel implements ActionListener {
  private Vector listeners = new Vector();

  static protected final int defaultButtonWidth = 125;
  static protected final int defaultButtonHeight = 20;

  public void addActionListener(ActionListener listener){
    if (listeners.contains(listener)) return;
    listeners.addElement(listener);
	}

  public void removeActionListener(ActionListener listener) {
    listeners.removeElement(listener);
  }

	public void actionPerformed(ActionEvent e){
    for( int i=0; i < listeners.size(); i++) {
      ((ActionListener)listeners.elementAt(i)).actionPerformed(e);
    }
	}

  public abstract int numButtons();

  public int defaultWidth(){
    return defaultButtonWidth;
  }

  public int defaultHeight(){
    return numButtons()*defaultButtonHeight + labelHeight;
  }
}


