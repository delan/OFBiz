package org.ofbiz.designer.dataclass;

import org.ofbiz.designer.pattern.*;

//
// IMPORTANT NOTE !!
// call notifyDataChanged() after making *changes* to dtd object
//

public class MethodSupportClass extends AbstractDataSupportClass implements IMethodSupportClass {
	
	public boolean containsException(String name) {
		IMethod theMeth = (IMethod)getDtdObject();
		for(int i=0;i<theMeth.getExceptionCount();i++) {
			if(((String)theMeth.getExceptionAt(i)).equals(name))
				return true;
		}
		return false;
	}
	
}
