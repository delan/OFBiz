package org.ofbiz.designer.newdesigner.LatticeEditor;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class FileNameDialog extends JDialog {
	private LatticeView theParent;
	private JTextField nameField;
	private JButton okButton;
	private int mode;
	
	public static final int NEW_MODE = 0;
	public static final int SAVEAS_MODE = 1;
	
	
	public FileNameDialog(LatticeView theParentIn, int modeIn) {
		super(theParentIn);
		theParent = theParentIn;
		mode = modeIn;
		setModal(true);
		getContentPane().setLayout(new FlowLayout()); 
		
		setTitle("File Name Entry");
		nameField = new JTextField(20);
		okButton = new JButton("OK");
		getContentPane().add(new JLabel("Name: "));
		getContentPane().add(nameField);
		getContentPane().add(okButton);
		pack();
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(mode==NEW_MODE) {
					theParent.handleNew(nameField.getText());
				}
				else {
					theParent.handleSaveAs(nameField.getText());
				}
				hide();
			}
		});
		show();
	}
	
	
}
