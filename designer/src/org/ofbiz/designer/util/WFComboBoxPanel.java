package org.ofbiz.designer.util;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;
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

import java.util.Vector;

public class WFComboBoxPanel extends WFPanel{
  private JLabel label;
  private MyComboBox comboBox;
  private Dimension dimension = null;
  private Dimension labelDimension;
  private Dimension comboBoxDimension;
  private int comboBoxY;
  //private static final double comboBoxWeight = 0.4d;

  private Vector actionListeners = new Vector();

  public WFComboBoxPanel(Dimension dimension){
    super(dimension);
    this.dimension = dimension;
  }
  
/*  public static void main(String[] args){
	  javax.swing.JFrame frame = new javax.swing.JFrame();
	  WFComboBoxPanel panel = new WFComboBoxPanel(new Dimension(100,50));
	  panel.setEditable(true);
	  Object[] objArr = {"one", "two", "three"};
	  panel.setData("test comboBox", objArr, 0);
	  frame.getContentPane().setLayout(new java.awt.BorderLayout());
	  frame.getContentPane().add(panel, java.awt.BorderLayout.CENTER);
	  frame.setBounds(100, 100, 300, 100);
	  frame.setVisible(true);
  }
*/
  public JComboBox getComboBox(){
	  return comboBox;
  }
  
  public void addFocusListener(FocusListener listener){
    if (comboBox != null)
      comboBox.addFocusListener(listener);
  }

  public void removeFocusListener(FocusListener listener){
    if (comboBox != null)
      comboBox.removeFocusListener(listener);
  }
  
  public void addActionListener(ActionListener listener){
    if (!actionListeners.contains(listener)) actionListeners.addElement(listener);
  }

  public void removeActionListener(ActionListener listener){
    if (actionListeners.contains(listener)) actionListeners.removeElement(listener);
  }

  public void addItemListener(ItemListener listener){
    comboBox.addItemListener(listener);
  }

  public void removeItemListener(ItemListener listener){
    comboBox.removeItemListener(listener);
  }

  public void setData(String name, final Object[] data, int defaultIndex){
    int height = 0;

    labelDimension = new Dimension(dimension.width, labelHeight);
    label = new JLabel(name);
    label.setBounds(0, height, labelDimension.width, labelDimension.height);
    height += labelDimension.height;

	MyComboBoxModel bigData = new MyComboBoxModel(data);
    comboBoxDimension = new Dimension(dimension.width, dimension.height - labelHeight);
    comboBox = new MyComboBox();
    comboBox.setModel(bigData);
    comboBox.setBounds(0, height, comboBoxDimension.width, comboBoxDimension.height);
    try{
      comboBox.setSelectedIndex(defaultIndex);
    } catch (IllegalArgumentException e){
    }
    comboBoxY = height;

    comboBox.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        for (int i=actionListeners.size()-1; i>=0; i--)
          ((ActionListener)actionListeners.elementAt(i)).actionPerformed(new ActionEvent(WFComboBoxPanel.this, ActionEvent.ACTION_PERFORMED, Constants.COMBO_BOX_CHANGED));
      }
    });
    comboBox.addItemListener(new ItemListener(){
      public void itemStateChanged(ItemEvent e){
      }
    });

    add(label);
    add(comboBox);
  }

  public void updateData(Object[] data, int defaultIndex) {
	  MyComboBoxModel bigData = (MyComboBoxModel)comboBox.getModel();
	  bigData.setData(data);
	  comboBox.setSelectedIndex(defaultIndex);
  }
  
/*
  public void updateData(Object[] data, int defaultIndex) {
	MyComboBoxModel bigData = new MyComboBoxModel(data);
    comboBox.setModel(bigData);
	try{
      comboBox.setSelectedIndex(defaultIndex);
    } catch (IllegalArgumentException e){
		System.out.println("** Update data illegal Argument");
    }
  }
*/
  public int getSelectedIndex(){
    return comboBox.getSelectedIndex();
  }

  public String getText(){
    return (String)comboBox.getModel().getSelectedItem();
  }

  public void setEnabled(boolean value){
    comboBox.setEnabled(value);
  }

  public void setEditable(boolean value){
    comboBox.setEditable(value);
  }
}

class MyComboBox extends JComboBox{
/*  public void fireActionEvent(){
    super.fireActionEvent();
  }
*/
  public void setSelectedItem(Object anObject) {
	  super.setSelectedItem(anObject);
	  super.fireActionEvent();
  }
}

class MyComboBoxModel implements ComboBoxModel {
	Object[] data;
	Object selectedItem = null;
  Vector dataListeners = new Vector();
	
	public MyComboBoxModel(final Object[] d) {
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
