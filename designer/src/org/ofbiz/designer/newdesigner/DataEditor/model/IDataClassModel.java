package org.ofbiz.designer.newdesigner.DataEditor.model;

import org.ofbiz.designer.pattern.*;

public interface IDataClassModel extends IModel, IGuiModel {
	public void setName(String newName);
	public String getName();
	public void setPackage(String packageIn) ;
	public String getPackage() ;
	public void setParent(String parentIn) ;
	public String getParent() ;
	public IFieldListModel getFieldList();
	public IMethodListModel getMethodList();
}
