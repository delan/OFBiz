package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.newdesigner.LatticeEditor.model.*;
import javax.swing.*;
import java.awt.event.*;

public class LatticeToolButton extends JButton {
	
	public LatticeToolButton(String aName) {
		super(aName);
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					LatticeView theEnclosingFrame = (LatticeView) getTopLevelAncestor();
					theEnclosingFrame.select(LatticeToolButton.this);
				}});
			
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				LatticeView theEnclosingFrame = (LatticeView) getTopLevelAncestor();
					if(theEnclosingFrame.getCurrTool() == LatticeToolButton.this) {	
						theEnclosingFrame.unselectCurrTool();
					}
				}});
		}
	}
