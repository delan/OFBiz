package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import org.ofbiz.designer.pattern.*;

public class DomainModel extends LatticeNodeModel implements IDomainModel {
	
	private DomainModel() {
	}
	
	public static ILatticeNodeModel createModelProxy() {
		IDomainModel newModel = new DomainModel();
		IDomainModelWrapper proxy = null;
		try {
			proxy = (IDomainModelWrapper)GuiModelProxy.newProxyInstance(newModel,"latticeeditor.model.IDomainModelWrapper");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return proxy;
	}
	
	public Object[][] getRelationships() {
		Object[][] superRel = super.getRelationships();
		int numRels = superRel.length;
		Object[][] returnObj = new Object[numRels+2][3];
		for(int i=0;i<numRels;i++) {
			returnObj[i] = superRel[i];
		}							   
		returnObj[numRels][0]="fromPolicies";
		returnObj[numRels][1]="fromDomain";
		returnObj[numRels][2]=MULTIPLE;
		
		returnObj[numRels+1][0]="toPolicies";
		returnObj[numRels+1][1]="toDomain";
		returnObj[numRels+1][2]=MULTIPLE;
		
		return returnObj;
	}
	
	public int getFromPolicyCount() {
		return getRelationshipCount("fromPolicies");
	}
	
	public int getToPolicyCount() {
		return getRelationshipCount("toPolicies");
	}
	
	public PolicyModelContainer getFromPolicyAt(int index) {
		return (PolicyModelContainer)getRelationshipAt("fromPolicies",index);
	}
	
	public PolicyModelContainer getToPolicyAt(int index) {
		return (PolicyModelContainer)getRelationshipAt("toPolicies",index);
	}
}
