package org.ofbiz.designer.util;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.util.Vector;
import javax.swing.JRadioButton;
import javax.swing.JLabel;

public abstract class WFRadioButton
  extends WFPanel
  implements ActionListener
{
  JRadioButton radioButton = new JRadioButton();
  JLabel label = new JLabel("default");
  Vector listenerList = new Vector();
  String actionCommand;

  public WFRadioButton(){
    //setLayout(new FlowLayout());
    setVisible(true);
  }

  public void setText(String labelText){
    label = new JLabel(labelText);
    add(radioButton);
    add(label);
    radioButton.setSelected(true);
    radioButton.addActionListener(this);
  }

  public void addActionListener(ActionListener listener){
    if (!listenerList.contains(listener)) listenerList.addElement(listener);
  }

  public void removeActionListener(ActionListener listener){
    if (listenerList.contains(listener)) listenerList.removeElement(listener);
  }

  public void actionPerformed(ActionEvent e){
    if (radioButton.isSelected()) setActionCommand(getActionOnSelect());
    else setActionCommand(getActionOnUnSelect());
    fireActionEvent();
  }

  void setActionCommand(String command){
    this.actionCommand = command;
  }

  void fireActionEvent(){
    ActionEvent actionEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, actionCommand);
    for (int i=0; i<listenerList.size(); i++)
      ((ActionListener)listenerList.elementAt(i)).actionPerformed(actionEvent);
  }

  public abstract String getActionOnSelect();
  public abstract String getActionOnUnSelect();
}

