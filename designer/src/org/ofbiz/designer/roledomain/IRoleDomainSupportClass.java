package org.ofbiz.designer.roledomain;

import org.ofbiz.designer.pattern.*;

public interface IRoleDomainSupportClass extends IDataSupportClass {
	public IRole createRole(String id);
	public IRoleRelationship createRoleRelationship(String id);
}
