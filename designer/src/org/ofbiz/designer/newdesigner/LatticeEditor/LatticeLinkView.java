package org.ofbiz.designer.newdesigner.LatticeEditor;

import java.awt.event.*;
import org.ofbiz.designer.util.*;
import java.awt.*;
import javax.swing.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeLinkModel;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeModel;

public class LatticeLinkView extends ArrowComponent implements IView { 
	protected ILatticeLinkModel theModel;
	
	public LatticeLinkView(ILatticeLinkModel modelIn) {
		setModel(modelIn);
		
		String[] popItems = {"Delete"};
		
		addMouseListener(new PopupMouseListener(popItems,this,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ILatticeModel modelParent = theModel.getParent();
				theModel.die();
			}
		}));
	}
	
	public void setModel(IModel modelIn){
		if (theModel == modelIn)
			return;
		if (modelIn != null && !(modelIn instanceof IModelProxySupportClass))
			throw new RuntimeException("Model is not a Proxy");
		if (theModel != null)
			theModel.setGui(null);
		theModel = (ILatticeLinkModel)modelIn;
		theModel.setGui(this);
		synchronize();
	}
	
	public IModel getModel() {
		return theModel;
	}
	
	public void synchronize() {
		if(theModel==null) return;
		if(theModel.getParent()==null) {
			getParent().remove(this);
			return;
		}
		setLineOutsideBox(theModel.getLowBounds(),theModel.getHighBounds());
		repaint();
	}
}
