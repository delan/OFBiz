package org.ofbiz.designer.newdesigner.DataEditor.model;

import javax.swing.table.*;
import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;

public interface IExceptionListModel extends ITableModelWithRemoveRow,IGuiModel {
	public Vector addNewException(String name);
	public Vector getExceptions();
	public void removeException(Vector theException);
	public Vector getException(String name);
}

