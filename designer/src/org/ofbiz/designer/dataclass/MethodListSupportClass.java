package org.ofbiz.designer.dataclass;

import org.ofbiz.designer.pattern.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class MethodListSupportClass extends AbstractDataSupportClass implements IMethodListSupportClass {
	
	public IMethod createMethod() {
		IMethod newMethod = new Method();
		newMethod.setType(new Type());
		newMethod.getType().setSimpleTypeOrUrl(new SimpleTypeOrUrl());
		newMethod.setParametersList(new ParametersList());
		((IMethodList) getDtdObject()).addMethod(newMethod);
		return newMethod;
	}
	
	public IMethod getMethod(String name) {
		IMethodList methodList = (IMethodList)getDtdObject();
		IMethod[] theList = methodList.getMethods();
		for(int i=0;i<theList.length;i++) {
			if(((String)theList[i].getName()).equals(name))
				return theList[i];
		}
		return null;
	}

}
