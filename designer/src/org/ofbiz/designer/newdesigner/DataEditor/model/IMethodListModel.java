package org.ofbiz.designer.newdesigner.DataEditor.model;

import org.ofbiz.designer.util.*;
import org.ofbiz.designer.pattern.*;
import java.util.*;

public interface IMethodListModel extends ITableModelWithRemoveRow, IGuiModel {
	public Vector addNewMethod(String type, String name);
	public void removeMethod(Vector theMethod);
	public Vector getMethod(String name);
	public Vector getMethods();
	public IExceptionListModel getExceptionListAt(int index);
	public IFieldListModel getParamListAt(int index);
}
