package org.ofbiz.designer.util;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.table.*;

public class JTableWithDelete extends JTable {
	
	public JTableWithDelete() {
		
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if((e.getKeyCode()==127)&&
				   (getSelectedRow()>=0)&&
				   (getSelectedRow()<getModel().getRowCount()-1)) {
					((ITableModelWithRemoveRow)getModel()).removeRow(getSelectedRow());
					getCellEditor(getRowCount()-1,0).cancelCellEditing();
				}
			}
		});
		
	}
	

}
