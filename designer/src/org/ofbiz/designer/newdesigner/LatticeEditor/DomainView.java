package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.newdesigner.LatticeEditor.model.IDomainModel;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.IDomainModelWrapper;

import java.awt.*;

public class DomainView extends LatticeNodeView {
	
	public DomainView(IDomainModel modelIn, Container containerIn) {
		super(modelIn, containerIn);
	}
	
	DomainEnvView topMostFrame = (DomainEnvView)theModel.getParent().getGui();
	
	public void checkForRestOfTools() {
		if(topMostFrame.getCurrTool() == topMostFrame.getPolicyTool()) {
			if(topMostFrame.getPossibleSource() == null) {
				topMostFrame.setPossibleSource(this);
			}
			else {
				DomainView src = (DomainView)topMostFrame.getPossibleSource();
				DomainView fromView;
				if(this != src) {
					fromView = (DomainView)((IDomainModelWrapper)src.getModel()).getGui();
					topMostFrame.doPolicyEdit(fromView,this);
				}
				topMostFrame.unselectCurrTool();
			}
		}
	}
}
