package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import org.ofbiz.designer.pattern.*;

public class RoleDomainModel extends LatticeModel implements ILatticeModel {
	private RoleDomainModel() {
	}
	
	public static ILatticeModel createModelProxy() {
		ILatticeModel newModel = new RoleDomainModel();
		ILatticeModelWrapper proxy = null;
		try {
			proxy = (ILatticeModelWrapper)GuiModelProxy.newProxyInstance(newModel,"latticeeditor.model.ILatticeModelWrapper");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return proxy;
	}
	
	
	public ILatticeNodeModel createNewNode() {
		return (ILatticeNodeModel) RoleModel.createModelProxy();
	}
}
