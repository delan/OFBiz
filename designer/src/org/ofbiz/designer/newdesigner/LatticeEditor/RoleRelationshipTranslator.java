package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.util.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.roledomain.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeLinkModelWrapper;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeLinkModel;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeNodeModel;

import java.util.*;

public class RoleRelationshipTranslator extends BaseTranslator {
			
	protected RoleRelationshipTranslator(ILatticeLinkModelWrapper aModel, IRoleRelationshipWrapper aWrapper, String direction) {
		super(aModel, aWrapper);
		synchronize(direction);
	}

	public void updateModelImpl (){
		
		IRoleRelationshipWrapper wrapper = (IRoleRelationshipWrapper)getDataObject();
		ILatticeLinkModel model = (ILatticeLinkModel)getGuiModel();
		
		Hashtable roleHash = model.getParent().latticeNodesHashedById();
		
		model.beginTransaction();
		
		ILatticeNodeModel highRole = (ILatticeNodeModel)roleHash.get(wrapper.getHighRoleAttribute());
		ILatticeNodeModel lowRole = (ILatticeNodeModel)roleHash.get(wrapper.getLowRoleAttribute());
		model.setHigh(highRole);
		model.setHighBounds(highRole.getBounds());
		model.setLow(lowRole);
		model.setLowBounds(lowRole.getBounds());
		model.setId(wrapper.getIdAttribute());
		
		model.commitTransaction();
	}
	
	public void updateDataImpl () {
		IRoleRelationshipWrapper wrapper = (IRoleRelationshipWrapper)getDataObject();
		ILatticeLinkModel model = (ILatticeLinkModel)getGuiModel();
		wrapper.setHighRoleAttribute(model.getHigh().getId());
		wrapper.setLowRoleAttribute(model.getLow().getId());
	}
	
}