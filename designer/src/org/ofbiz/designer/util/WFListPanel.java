package org.ofbiz.designer.util;

import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JViewport;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ChangeListener;
import java.util.Vector;
import java.awt.Dimension;
import java.awt.Rectangle;

public class WFListPanel extends WFPanel{
  private JLabel label;
  private WFList list = null;
  private JScrollPane scrollPane = null;
  private Vector listData;
  private Dimension dimension = null;
  private Dimension labelDimension;
  private Dimension listDimension;
  private int listY;
  //private static final double listWeight = 0.65d;

  public WFListPanel(Dimension dimension){
    super(dimension);
    this.dimension = dimension;
  }
	
  public void setLabel(String labelText){
	  label.setText(labelText);
  }
  
  public WFList getList(){
	  return list;
  }

  public void setData(String name, final Object[] data){
    int height = 0;
    listData = new Vector();

    labelDimension = new Dimension(dimension.width, labelHeight);
    label = new JLabel(name);
    label.setBounds(0, height, labelDimension.width, labelDimension.height);
    height += labelDimension.height;

    if (data != null){
      for (int i=0; i<data.length;i++)
          if (!listData.contains(data[i])) listData.addElement(data[i]);
    }

    listDimension = new Dimension(dimension.width, dimension.height-labelHeight);
    list = new WFList(listData);
    scrollPane = new JScrollPane(list);
    //list.setBounds(0, height, listDimension.width, listDimension.height);
    scrollPane.setBounds(0, height, listDimension.width, listDimension.height);
    listY = height;

    add(label);
    //add(list);
    add(scrollPane);
    list.setSelectionMode(0);
  }

  public void updateData(final Object[] data){
	  listData.removeAllElements();
	  for (int i=0; i<data.length; i++)
		  if (!listData.contains(data[i])) listData.addElement(data[i]);
	  list.setListData(data);
  }
 
  public void addChangeListener(ChangeListener listener){
    scrollPane.getViewport().addChangeListener(listener);
  }

  public void removeChangeListener(ChangeListener listener){
    scrollPane.getViewport().removeChangeListener(listener);
  }

  public void setPopup(WFPopup popup){
    list.setPopup(popup);
  }

  public void addListSelectionListener(ListSelectionListener listener){
    list.addListSelectionListener(listener);
  }

  public void removeListSelectionListener(ListSelectionListener listener){
    list.removeListSelectionListener(listener);
  }

  public Vector getVector(){
    return listData;
  }

  public void setSelectedIndex(int index){
    list.setSelectedIndex(index);
  }

  public int[] getSelectedIndices(){
    return list.getSelectedIndices();
  }

  public Object[] getSelectedValues(){
    return list.getSelectedValues();
  }

  public void setSelectedValue(Object value){
    int index = list.getSelectedIndex();

    //remove(scrollPane);
    listData.setElementAt(value, index);
    //WFPopup listPopup = list.getPopup();
    //list = new WFList(listData);
    //list.setPopup(listPopup);
    //scrollPane = new JScrollPane(list);
    //scrollPane.setBounds(0, listY, listDimension.width, listDimension.height);
    //add(scrollPane);
    list.setListData(listData.toArray());
    validate();
    repaint();
  }

  public Object[] getAllValues(){
    Object[] objects = new Object[listData.size()];
    listData.copyInto(objects);
    /*
    for (int i=0; i<objects.length; i++)
      objects[i] = listData.elementAt(i);
    */
    return objects;
  }

  public int getIndexOf(Object obj){
    for (int i=listData.size()-1; i>=0; i--){
      if (listData.elementAt(i) == obj)
        return i;
    }
    return -1;
  }

  public void removeElements(int[] index){
    //remove(list);
    for (int i=index.length-1; i>=0; i--)
      listData.removeElementAt(index[i]);
    //WFPopup listPopup = list.getPopup();
    //list = new WFList(listData);
    //list.setPopup(listPopup);
    //list.setBounds(0, listY, listDimension.width, listDimension.height);
    list.setListData(listData.toArray());

    //add(list);
    validate();
    repaint();
  }

  public void addElements(Object[] objects){
    //remove(list);
    for (int i=0; i<objects.length; i++)
      if (!listData.contains(objects[i])) listData.addElement(objects[i]);
    //WFPopup listPopup = list.getPopup();
    //list = new WFList(listData);
    //list.setPopup(listPopup);
    //list.setBounds(0, listY, listDimension.width, listDimension.height);
    //add(list);
    list.setListData(listData.toArray());
    validate();
    repaint();
  }

/*  public void addElement(Object object){
    remove(list);
    if (!listData.contains(object)) listData.addElement(object);
    WFPopup listPopup = list.getPopup();
    list = new WFList(listData);
    list.setPopup(listPopup);
    list.setBounds(0, listY, listDimension.width, listDimension.height);
    add(list);
    repaint();
  }
*/
  public void addElement(Object object) {
    if (!listData.contains(object)) listData.addElement(object);
    list.setListData(listData.toArray());
    validate();
    repaint();
  }

  public void addElement(int index, Object object) {
    if (!listData.contains(object)) listData.insertElementAt(object, index);
    list.setListData(listData.toArray());
    validate();
    repaint();
  }

  public void removeAllElements(){
    listData.removeAllElements();
	list.setListData(listData.toArray());
	validate();
    repaint();
  }

  public int getScrollPaneY(){
    return scrollPane.getY();
  }

  public Rectangle getCellBounds(int x, int y){
    Rectangle rect = list.getCellBounds(x, y);
    return rect;
  }

  public Rectangle getCellBounds(int x){
    Rectangle rect = list.getCellBounds(x, x);
    return rect;
  }

  public int getViewPositionY(){
    JViewport viewport = scrollPane.getViewport();
    int tempY = (int)viewport.getViewPosition().y;
    return tempY;
  }

  public int getLabelHeight(){
    return labelHeight;
  }
}

