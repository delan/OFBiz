package org.ofbiz.designer.util;

import javax.swing.JLabel;
import javax.swing.JCheckBox;
//import javax.swing.CheckBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.Dimension;

import javax.swing.*;

import java.util.Vector;

public class WFCheckBoxPanel extends WFPanel{
  private JLabel label;
  //private MyCheckBox checkBox;
  private JCheckBox checkBox;
  private Dimension dimension = null;
  private Dimension labelDimension;
  private Dimension checkBoxDimension;
  private int checkBoxY;
  //private static final double comboBoxWeight = 0.4d;

  private Vector actionListeners = new Vector();

  public WFCheckBoxPanel(Dimension dimension){
    super(dimension);
    this.dimension = dimension;
  }
  
  public static void main(String[] args){
	  javax.swing.JFrame frame = new javax.swing.JFrame();
	  WFCheckBoxPanel panel = new WFCheckBoxPanel(new Dimension(120,50));
	  //panel.setEnabled(true);
	  Object[] objArr = {"one", "two", "three"};
	  panel.setData("test checkBox", true);
	  frame.getContentPane().setLayout(new java.awt.BorderLayout());
	  frame.getContentPane().add(panel, java.awt.BorderLayout.CENTER);
	  frame.setBounds(100, 100, 300, 100);
	  frame.setVisible(true);
  }
  public JCheckBox getCheckBox(){
	  return checkBox;
  }
  
  public void addFocusListener(FocusListener listener){
    if (checkBox != null)
      checkBox.addFocusListener(listener);
  }

  public void removeFocusListener(FocusListener listener){
    if (checkBox != null)
      checkBox.removeFocusListener(listener);
  }
  
  public void addActionListener(ActionListener listener){
    if (!actionListeners.contains(listener)) actionListeners.addElement(listener);
  }

  public void removeActionListener(ActionListener listener){
    if (actionListeners.contains(listener)) actionListeners.removeElement(listener);
  }

  public void addItemListener(ItemListener listener){
    checkBox.addItemListener(listener);
  }

  public void removeItemListener(ItemListener listener){
    checkBox.removeItemListener(listener);
  }

  public void setData(String name, boolean value){
    int height = 0;

    labelDimension = new Dimension(dimension.width, labelHeight);
    label = new JLabel(name);
    label.setBounds(0, height, labelDimension.width, labelDimension.height);
    height += labelDimension.height;

	//MyCheckBoxModel bigData = new MyCheckBoxModel(data);
    checkBoxDimension = new Dimension(dimension.width, dimension.height - labelHeight);
    checkBox = new JCheckBox(name, value);
    //checkBox.setModel(bigData);
    checkBox.setBounds(0, height, checkBoxDimension.width, checkBoxDimension.height);
    checkBoxY = height;

    checkBox.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        for (int i=actionListeners.size()-1; i>=0; i--)
          ((ActionListener)actionListeners.elementAt(i)).actionPerformed(new ActionEvent(WFCheckBoxPanel.this, ActionEvent.ACTION_PERFORMED, Constants.COMBO_BOX_CHANGED));
      }
    });
    checkBox.addItemListener(new ItemListener(){
      public void itemStateChanged(ItemEvent e){
      }
    });

    add(label);
    add(checkBox);
  }

  /*
  public void updateData(Object[] data, int defaultIndex) {
	  MyCheckBoxModel bigData = (MyCheckBoxModel)checkBox.getModel();
	  bigData.setData(data);
	  checkBox.setSelectedIndex(defaultIndex);
  }
  */
  
/*
  public void updateData(Object[] data, int defaultIndex) {
	MyCheckBoxModel bigData = new MyCheckBoxModel(data);
    checkBox.setModel(bigData);
	try{
      checkBox.setSelectedIndex(defaultIndex);
    } catch (IllegalArgumentException e){
		System.out.println("** Update data illegal Argument");
    }
  }
*/

  public void setEnabled(boolean value){
    checkBox.setEnabled(value);
  }
}

/*
class MyCheckBoxModel implements CheckBoxModel {
	Object[] data;
	Object selectedItem = null;
  Vector dataListeners = new Vector();
	
	public MyCheckBoxModel(final Object[] d) {
		data = d;
	}
	
	public int getSize() { 
		return data.length;
	}
	
	public void setData(Object[] dataIn){
		data = dataIn;
		fireItemChanged();
	}
	
	public Object getElementAt(int index) {
		if (index >= data.length)
			return null;
		return data[index];
	}
	
	public Object getSelectedItem(){
		return selectedItem;
	}
	public void setSelectedItem(Object obj){
		selectedItem = obj;
	}
	public void removeListDataListener(ListDataListener listener){
		if (dataListeners.contains(listener))
			dataListeners.removeElement(listener);
	}

	public void addListDataListener(ListDataListener listener){
		if (!(dataListeners.contains(listener)))
			dataListeners.addElement(listener);
	}

	public void fireItemChanged() {
		int listenerSize = dataListeners.size();
		for( int i=0; i < listenerSize; i++ ) {
			ListDataListener ldl = (ListDataListener)dataListeners.elementAt(i);
			ldl.contentsChanged(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, data.length-1));
		}
	}
}
*/
