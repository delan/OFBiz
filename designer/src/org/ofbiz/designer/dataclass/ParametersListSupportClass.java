package org.ofbiz.designer.dataclass;

import org.ofbiz.designer.pattern.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class ParametersListSupportClass extends AbstractDataSupportClass implements IParametersListSupportClass {
	public IParameter createParameter() {
		IParameter newParameter = new Parameter();
		newParameter.setType(new Type());
		newParameter.getType().setSimpleTypeOrUrl(new SimpleTypeOrUrl());
		((IParametersList) getDtdObject()).addParameter(newParameter);
		return newParameter;
	}
	
	public IParameter getParameter(String name) {
		IParametersList parameterList = (IParametersList)getDtdObject();
		IParameter[] theList = parameterList.getParameters();
		for(int i=0;i<theList.length;i++) {
			if(((String)theList[i].getName()).equals(name))
				return theList[i];
		}
		return null;
	}
}
