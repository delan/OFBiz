package org.ofbiz.designer.util;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.Caret;
import javax.swing.border.BevelBorder;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

public class WFTextFieldPanel extends WFPanel{
  private JLabel label;
  private JTextField textField;
  private Dimension dimension;
  private Dimension labelDimension;
  private Dimension textFieldDimension;
  //private static final double textFieldWeight = 0.4d;

  public WFTextFieldPanel(Dimension dimension){
    super(dimension);
    this.dimension = dimension;
  }
  
  public JTextField getTextField(){
	  return textField;
  }
  
  public void setData(String name, String data){
    int height = 0;

    labelDimension = new Dimension(dimension.width, labelHeight);
    label = new JLabel(name);
    label.setBounds(0, height, labelDimension.width, labelDimension.height);
    height += labelDimension.height;


    textFieldDimension = new Dimension(dimension.width, dimension.height - labelHeight);
    if (data != null) textField = new JTextField(data);
      else textField = new JTextField();
    textField.setBounds(0, height, textFieldDimension.width, textFieldDimension.height);
	BevelBorder border = new BevelBorder(BevelBorder.LOWERED);

    add(label);
    add(textField);
  }
  
  public void setLabel(String labelText){
	  label.setText(labelText);
  }

  public String getText(){
    return textField.getText();
  }

  public void setText(String text){
    textField.setText(text);
  }

  public void addFocusListener(FocusListener listener){
    if (textField != null)
      textField.addFocusListener(listener);
  }

  public void removeFocusListener(FocusListener listener){
    if (textField != null)
      textField.removeFocusListener(listener);
  }

  public void addKeyListener(KeyListener listener){
    if (textField != null)
      textField.addKeyListener(listener);
  }

  public void removeKeyListener(KeyListener listener){
    if (textField != null)
      textField.removeKeyListener(listener);
  }

  public void addActionListener(ActionListener listener){
    if (textField != null)
      textField.addActionListener(listener);
  }

  public void removeActionListener(ActionListener listener){
    if (textField != null)
      textField.removeActionListener(listener);
  }
}

