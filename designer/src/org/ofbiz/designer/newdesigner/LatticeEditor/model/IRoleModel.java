package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import javax.swing.text.*;

public interface IRoleModel extends ILatticeNodeModel {
	public void setPriveleges(String privelegesIn);
	public String getPriveleges();
}
