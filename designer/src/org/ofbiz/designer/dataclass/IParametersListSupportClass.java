package org.ofbiz.designer.dataclass;

import org.ofbiz.designer.pattern.*;

public interface IParametersListSupportClass extends IDataSupportClass {
	public IParameter createParameter();
	public IParameter getParameter(String name);
}
