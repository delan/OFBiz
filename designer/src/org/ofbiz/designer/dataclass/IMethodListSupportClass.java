package org.ofbiz.designer.dataclass;

import org.ofbiz.designer.pattern.*;

public interface IMethodListSupportClass extends IDataSupportClass {
	public IMethod createMethod();
	public IMethod getMethod(String name);
}
