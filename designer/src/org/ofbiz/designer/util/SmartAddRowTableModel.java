package org.ofbiz.designer.util;

import javax.swing.table.*;
import java.util.*;

public class SmartAddRowTableModel extends DefaultTableModel {
	
	public SmartAddRowTableModel(Object[] columnNames) {
		super(columnNames,1);
	}
	
	public void setValueAt(Object val, int row, int column) {
		super.setValueAt(val,row,column);
		
		if((row==(getRowCount()-1))&&(!val.equals(""))) {
			addNewBlankRow();
		}
	}
	
	protected void addNewBlankRow() {
		Vector newRow = new Vector();
		for(int i=0;i<getColumnCount();i++) {
			newRow.add("");
		}
		super.addRow(newRow);
	}
	
	public void addRow(Vector newRow) {
		insertRow(getRowCount()-1,newRow);
	}
	
}
