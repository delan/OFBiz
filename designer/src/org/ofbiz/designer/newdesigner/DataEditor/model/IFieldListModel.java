package org.ofbiz.designer.newdesigner.DataEditor.model;

import javax.swing.table.*;
import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;

public interface IFieldListModel extends ITableModelWithRemoveRow,IGuiModel {
	public Vector addNewField(String type, String name, String defaultVal);
	public Vector getFields();
	public void removeField(Vector theField);
	public Vector getField(String name);
}
