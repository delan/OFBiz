package org.ofbiz.designer.generic;

import org.ofbiz.designer.pattern.IGuiModel;
import javax.swing.ListModel;

public interface IListModel extends ListModel, IGuiModel { 
	public Object remove(int i);
	public void insertElementAt(Object obj, int index) ;
	
	public void addElement(Object obj);  
	public boolean contains(Object elem);  
	public Object elementAt(int index) ;
	public int getSize();	
}
