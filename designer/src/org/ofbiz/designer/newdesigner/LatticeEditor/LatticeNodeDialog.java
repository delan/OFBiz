package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeNodeModel;

import javax.swing.*;
import java.awt.event.*;

public class LatticeNodeDialog extends JDialog {
	
	protected ILatticeNodeModel theModel;
	protected JColorChooser colorChoose;
	protected JTextField nameField, descField;
	protected JButton okButton,cancelButton;
	
	public LatticeNodeDialog(LatticeView env) {
		super((JFrame)env);
		initDialog();
		initButtons();
		pack();
	}
	
	protected void initDialog() {
		setModal(true);
		setBounds(0,0,300,300);
		getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
		colorChoose = new JColorChooser();
		
		Box box1 = Box.createHorizontalBox();
		nameField = new JTextField();
		box1.add(new JLabel("Domain Name: "));
		box1.add(nameField);
		getContentPane().add(box1);
		
		box1 = Box.createHorizontalBox();
		descField = new JTextField();
		box1.add(new JLabel("Description: "));
		box1.add(descField);
		getContentPane().add(box1);
		
		box1 = Box.createHorizontalBox();
		box1.add(colorChoose);
		getContentPane().add(box1);	
	}
	
	private void initButtons() {
		Box box1 = Box.createHorizontalBox();
		okButton = new JButton("OK");
		box1.add(okButton);
		cancelButton = new JButton("Cancel");
		box1.add(cancelButton);
		getContentPane().add(box1);

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleOk();
				hide();
			}
		});
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
				
	}
	
	public void activate(ILatticeNodeModel modelIn) {
		theModel = modelIn;
		transferInData();
		setLocationRelativeTo((LatticeNodeView)theModel.getGui());
		show();
	}
	
	protected void transferInData() {
		colorChoose.setColor(theModel.getColor());
		nameField.setText(theModel.getName());
		descField.setText(theModel.getDescription());
	}
	
	protected void handleOk() {
		theModel.setColor(colorChoose.getColor());
		theModel.setName(nameField.getText());
		theModel.setDescription(descField.getText());
	}
}
