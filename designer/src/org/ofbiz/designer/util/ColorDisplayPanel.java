package org.ofbiz.designer.util;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;

public class ColorDisplayPanel extends WFPanel {
  public static final int defaultHeight = 75;

  private final int margin = 20;
  private JPanel colorPane;

  public ColorDisplayPanel(Dimension dimension, Color originalColor){
    int xCood = margin;
    int yCood = 20;
    Dimension colorPanelDimension = new Dimension(dimension.width/10, dimension.width/10);
    colorPane = new JPanel(null);
    colorPane.setBounds(xCood, margin, colorPanelDimension.width, colorPanelDimension.height);
    colorPane.setBackground(originalColor);
    //colorPane.repaint();
    add(colorPane);
  }

  public void setPanelColor(Color color){
    colorPane.setBackground(color);
  }
}

