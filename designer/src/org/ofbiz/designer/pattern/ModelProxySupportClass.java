package org.ofbiz.designer.pattern;

import java.lang.reflect.*;

public class ModelProxySupportClass implements IModelProxySupportClass{
	private IGuiModel guiModelObj;
	private ITranslator translatorObj;
	//private IView view;
	
	public ModelProxySupportClass(IGuiModel guiModelIn) {
		guiModelObj = guiModelIn;
	}
	
	public void setTranslator(ITranslator translatorIn) {
		if (translatorObj == translatorIn)
			return;
		if (translatorObj != null)
			translatorObj.close();
		translatorObj = translatorIn;
	}
	
	public ITranslator getTranslator() {
		return translatorObj;
	}
	
	public Object getRawModel() {
		return guiModelObj;
	}

	/*
	public void setGui(IView view){
		//GuiModelProxy.getGuiModelProxy(guiModelObj);
		guiModelObj.setGui(view);
		view.setModel(GuiModelProxy.getGuiModelProxy(guiModelObj));
	}
	*/
	
	public static boolean isSupportMethod(Method method){
		for (int i=0; i<supportMethods.length; i++)
			if (methodsRequal(method, supportMethods[i]))
				return true;
		return false;
	}
	
	private static Method[] supportMethods = ModelProxySupportClass.class.getDeclaredMethods();
	
	static boolean methodsRequal(Method method1, Method method2){
		if (!method1.getName().equals(method2.getName())) return false;
		else if (!method1.getReturnType().getName().equals(method2.getReturnType().getName())) return false;
		for (int i=0; i<method1.getParameterTypes().length; i++)
			if (!method1.getParameterTypes()[i].getName().equals(method2.getParameterTypes()[i].getName())) return false;
		return true;
	}
}
