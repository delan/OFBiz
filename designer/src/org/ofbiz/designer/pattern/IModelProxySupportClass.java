package org.ofbiz.designer.pattern;

public interface IModelProxySupportClass{
	public void setTranslator(ITranslator translatorIn);
	public ITranslator getTranslator();
	public Object getRawModel() ;
	//public void setGui(IView view);
	//public void synchronizeGui();
}
