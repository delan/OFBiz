package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import java.util.*;

public interface IDomainEnvModel extends ILatticeModel {
	public void addPolicy(PolicyModelContainer newPolicy);
	public PolicyModelContainer addPolicy(IDomainModel fromDomain, IDomainModel toDomain, String type);
	//public void removePolicy(PolicyModelContainer removal);
	public PolicyModelContainer getPolicyAt(int index);
	public int getPolicyCount();
	public Hashtable policiesHashedById();
	public Hashtable policiesHashedByFromToType();
}
