package org.ofbiz.designer.roledomain;

import org.ofbiz.designer.pattern.*;


public class RoleDomainSupportClass extends AbstractDataSupportClass implements IRoleDomainSupportClass {

	public IRole createRole(String id) {
		IRole newRole = new Role();
		newRole.setIdAttribute(id);
		newRole.setColor(new Color());
		newRole.setPosition(new Position());
		newRole.setDescription("");
		newRole.setPrivileges("");
		((RoleDomain) getDtdObject()).addRole(newRole);
		setIdRef(id,newRole);
		return newRole;
	}
	
	public IRoleRelationship createRoleRelationship(String id) {
		IRoleRelationship newRel = new RoleRelationship();
		newRel.setIdAttribute(id);									 
		((RoleDomain) getDtdObject()).addRoleRelationship(newRel);
		setIdRef(id,newRel);
		return newRel;
	}
	
}
