package org.ofbiz.designer.newdesigner.DataEditor;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class DataFileNameDialog extends JDialog {
	private DataClassView theParent;
	private JTextField nameField;
	private JTextField superField;
	private JTextField domainField;
	private JButton okButton;
	private int mode;
	private JCheckBox isEx;
	
	public static final int NEW_MODE = 0;
	public static final int SAVEAS_MODE = 1;
	
	
	public DataFileNameDialog(DataClassView theParentIn, int modeIn) {
		super(theParentIn);
		theParent = theParentIn;
		mode = modeIn;
		setModal(true);
		getContentPane().setLayout(null); 
		
		setTitle("Data Class General Info");
		setSize(250,200);
		
		JLabel nameLabel = new JLabel("Name: ");
		nameLabel.setBounds(0,5,75,20);
		getContentPane().add(nameLabel);
		nameField = new JTextField();
		nameField.setBounds(80,5,150,20);
		getContentPane().add(nameField);
		
		JLabel domainLabel = new JLabel("Data Domain: ");
		domainLabel.setBounds(0,30,100,20);
		getContentPane().add(domainLabel);
		domainField = new JTextField();
		domainField.setBounds(80,30,150,20);
		getContentPane().add(domainField);
		
		JLabel superLabel = new JLabel("Parent: ");
		superLabel.setBounds(0,55,75,20);
		getContentPane().add(superLabel);
		superField = new JTextField();
		superField.setBounds(80,55,150,20);
		getContentPane().add(superField);
		
		isEx = new JCheckBox("Is Exception?",false);
		isEx.setBounds(0,80,200,20);
		getContentPane().add(isEx);
		
		okButton = new JButton("OK");
		okButton.setBounds(80,125,75,20);
		getContentPane().add(okButton);
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(mode==NEW_MODE) {
					theParent.handleNew(nameField.getText(),domainField.getText(),superField.getText());
				}
				else {
					theParent.handleSaveAs(nameField.getText(),domainField.getText(),superField.getText());
				}
				hide();
				getParent().remove(DataFileNameDialog.this);
			}
		});
		
		isEx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isEx.isSelected()) {
					superField.setText("BaseException");
					superField.setEnabled(false);
				}
				else {
					superField.setEnabled(true);
				}
			}
		});
		
		show();
	}
	
	
}
