package org.ofbiz.designer.newdesigner.LatticeEditor.model;

public interface IDomainModel extends ILatticeNodeModel {
	public int getFromPolicyCount();
	public int getToPolicyCount();
	public PolicyModelContainer getFromPolicyAt(int index);
	public PolicyModelContainer getToPolicyAt(int index);
}
