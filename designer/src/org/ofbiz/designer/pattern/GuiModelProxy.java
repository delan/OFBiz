
/**
 *	GuiModelProxy.java
 * 
 *	GuiModelProxy is similar in principle to the DataProxy, except it is more general.
 *	It does not restrict itself to dxml classes.  Essentially, anyone intending to
 *	use the GuiModelProxy framework, needs to specify the target object, a list of methods
 *	that it is interested in being notified for, and a translator object capable of 
 *	handling these notifications.
 *	
 */

package org.ofbiz.designer.pattern;

import java.lang.reflect.*;
import java.util.*;
import java.net.*;
import org.ofbiz.designer.util.*;

public class GuiModelProxy implements InvocationHandler {
	static Hashtable proxies = new Hashtable();
	private ModelProxySupportClass supportClass;
	private HashSet modifyMethods;
	//private Method[] supportMethods;
	
	/*
	public static Object newProxyInstance(IGuiModel guiModelobj, 
										String modelInterfaceClass, 
										ITranslator translatorObj) throws Exception{
		
		HashSet modifyMethods = guiModelobj.getModifyMethods();
		if (proxies.containsKey(guiModelobj)) 
			return proxies.get(guiModelobj);
		Object newproxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), 
												 new Class[] {Class.forName(modelInterfaceClass)}, 
												 new GuiModelProxy(guiModelobj, modifyMethods, translatorObj));
		proxies.put(guiModelobj, newproxy);
		return newproxy;
	}
	*/
	public static Object newProxyInstance(IGuiModel guiModelobj, 
										String modelInterfaceClass) {
		try{
			HashSet modifyMethods = guiModelobj.getModifyMethods();
			if (proxies.containsKey(guiModelobj)) 
				return proxies.get(guiModelobj);
			Object newproxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), 
													 new Class[] {Class.forName(modelInterfaceClass)}, 
													 new GuiModelProxy(guiModelobj, modifyMethods));
			proxies.put(guiModelobj, newproxy);
			return newproxy;
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	public static Object createGuiModel(String className, String interfaceName) {
		Object returnObj = null;
		try {
			Object rawModel = Class.forName(className).newInstance();
			returnObj =  GuiModelProxy.newProxyInstance((IGuiModel) rawModel,interfaceName);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return returnObj;
	}
	*/
	
	public static IGuiModel getGuiModelProxy(IGuiModel modelIn) {
		return (IGuiModel)proxies.get(modelIn);
	}
	
	public static void removeGuiModelProxy(IGuiModel modelIn) {
		proxies.remove(modelIn);
	}
	
	/*
	public GuiModelProxy(Object guiModelobj, HashSet modifyMethods, ITranslator translatorObj) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		this.modifyMethods =  modifyMethods;
		supportClass = new GuiProxySupportClass(guiModelobj,translatorObj);
		supportMethods = supportClass.getClass().getDeclaredMethods();
	}
	*/
	
	public GuiModelProxy(IGuiModel guiModelobj, HashSet modifyMethods) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		this.modifyMethods =  modifyMethods;
		supportClass = new ModelProxySupportClass(guiModelobj);
	}
	
	//Object originalInvoker = null;
	public Object invoke(Object obj, Method method, Object[] params){
		try{
			if (ModelProxySupportClass.isSupportMethod(method))
				return method.invoke(supportClass, params);
			// for the equals method, we do not wrap the parameter
			if (method.getName().equals("equals") && method.getReturnType().getName().equals("boolean") && method.getParameterTypes().length == 1){
				if (params[0] instanceof IModelProxySupportClass)
					params[0] = ((IModelProxySupportClass)params[0]).getRawModel();
			} else if (params != null)
				for (int i=0; i<params.length; i++)
					if (params[i] != null && proxies.containsKey(params[i]))
						params[i] = proxies.get(params[i]);

			
			//if (originalInvoker == null) originalInvoker = this;
			Object returnObj = method.invoke(supportClass.getRawModel(), params);
			//if (originalInvoker == this){
				//originalInvoker = null;
				if(modifyMethods.contains(method.getName())) 
					if (supportClass.getTranslator() == null)
						;
					//LOG.println("Warning : Translator is null !");
					else
						supportClass.getTranslator().updateData(); // notify data changes
			//}

			if (returnObj != null && proxies.containsKey(returnObj))
				return proxies.get(returnObj);
			else return returnObj; 
		} catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
}


