package org.ofbiz.designer.util;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import java.awt.Dimension;
import java.awt.Color;
import java.util.Vector;

public class WFTextAreaPanel extends WFPanel implements CaretListener{
  private JLabel label;
  private JTextArea textArea;
  private JScrollPane scrollPane = null;
  private Dimension dimension;
  private Dimension labelDimension;
  private Dimension textAreaDimension;
  //private static final double textAreaWeight = 0.6d;
  private Vector caretListeners = new Vector();

  public WFTextAreaPanel(Dimension dimension){
    super(dimension);
    this.dimension = dimension;
  }

  public void setData(String name, String data){
    int height = 0;

    removeAll();
    Dimension labelDimension = new Dimension(dimension.width, labelHeight);
    label = new JLabel(name);
    label.setBounds(0, height, labelDimension.width, labelDimension.height);
    height += labelDimension.height;

    textAreaDimension = new Dimension(dimension.width, dimension.height-labelHeight);
    if (data != null) textArea = new JTextArea(data);
      else textArea = new JTextArea();
    scrollPane = new JScrollPane(textArea);
    scrollPane.setBounds(0, height, textAreaDimension.width, textAreaDimension.height);
    scrollPane.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED, Color.darkGray, new Color(220,220,220)));

    add(label);
    add(scrollPane);
    textArea.setLineWrap(true);
    textArea.addCaretListener(this);
    scrollPane.setEnabled(true);
  }
  
  public JTextArea getTextArea(){
	  return textArea;
  }

  public void setEditable(boolean bool){
    textArea.setEditable(bool);
  }

  public String getText(){
    return textArea.getText();
  }

  public void setText(String text){
    textArea.setText(text);
  }

  public void setLabel(String text){
    label.setText(text);
    repaint();
  }

  public void addCaretListener(CaretListener listener){
    if (!caretListeners.contains(listener)) caretListeners.addElement(listener);
  }

  public void removeCaretListener(CaretListener listener){
    if (caretListeners.contains(listener)) caretListeners.removeElement(listener);
  }

  public void caretUpdate(CaretEvent e){
    // all we care about is the source
    CaretEvent newE = new CaretEvent(this){
      public int getDot(){return -1;}
      public int getMark(){return -1;}
    };
    for (int i=caretListeners.size()-1; i>=0; i--){
      ((CaretListener)caretListeners.elementAt(i)).caretUpdate(newE);
    }
  }
}

