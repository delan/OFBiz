package org.ofbiz.designer.pattern;

public interface IModel{
	public void setGui(IView view);
	public IView getGui();
	public void synchronizeGui();
}
