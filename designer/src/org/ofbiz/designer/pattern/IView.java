package org.ofbiz.designer.pattern;

public interface IView{
	public void setModel(IModel model);
	public IModel getModel();
	public void synchronize();
}
