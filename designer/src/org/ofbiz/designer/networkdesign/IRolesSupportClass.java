package org.ofbiz.designer.networkdesign;

import org.ofbiz.designer.pattern.*;

public interface IRolesSupportClass extends IDataSupportClass {
	void createNewRole(String selectedRole);
	void removeRole(String selectedRole);
}
