package org.ofbiz.designer.util;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.border.SoftBevelBorder;

public abstract class WFPanel extends JPanel {
  public static final int leftMargin = 20;
  public static final int topMargin = 20;
  public static final int labelHeight = 20;

  public WFPanel(Dimension dimension){
    setLayout(null);
    setBounds(0, 0, dimension.width, dimension.height);
    //setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED, Color.darkGray, new Color(220,220,220)));
  }

  public WFPanel(){
    setLayout(null);
  }

  void setData(){
  }
  
  void updateData(Object dataSource){
  }
}

