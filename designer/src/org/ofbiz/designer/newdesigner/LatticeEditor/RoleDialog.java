package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.newdesigner.LatticeEditor.model.IRoleModel;

import javax.swing.*;

public class RoleDialog extends LatticeNodeDialog {
	
	private JTextArea privelegesArea;
	
	RoleDialog(RoleDomainView modelIn) {
		super(modelIn);
	}
	
	protected void initDialog() {
		super.initDialog();
		Box box1 = Box.createHorizontalBox();
		box1.add(new JLabel("Priveleges: "));
		getContentPane().add(box1);
	
		box1 = Box.createHorizontalBox();
		privelegesArea = new JTextArea(10,30);
		box1.add(privelegesArea);
		getContentPane().add(box1);
	}
	
	protected void handleOk() {
		super.handleOk();
		try {
			((IRoleModel)theModel).setPriveleges(privelegesArea.getText());
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	protected void transferInData() {
		super.transferInData();
		privelegesArea.setText(((IRoleModel)theModel).getPriveleges());
	}
}
