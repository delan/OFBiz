package org.ofbiz.designer.util;

import javax.swing.table.*;

public interface ITableModelWithRemoveRow extends TableModel {
	public void removeRow(int index);
}
