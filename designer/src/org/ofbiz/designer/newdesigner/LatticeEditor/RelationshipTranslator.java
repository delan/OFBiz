package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.util.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.domainenv.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeLinkModelWrapper;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeLinkModel;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.ILatticeNodeModel;

import java.util.*;

public class RelationshipTranslator extends BaseTranslator {
			
	protected RelationshipTranslator(ILatticeLinkModelWrapper aModel, IDomainRelationshipWrapper aWrapper, String direction) {
		super(aModel, aWrapper);
		synchronize(direction);
	}

	public void updateModelImpl (){
		
		IDomainRelationshipWrapper wrapper = (IDomainRelationshipWrapper)getDataObject();
		ILatticeLinkModel model = (ILatticeLinkModel)getGuiModel();
		
		Hashtable domainHash = model.getParent().latticeNodesHashedById();
		
		model.beginTransaction();
		
		ILatticeNodeModel highDomain = (ILatticeNodeModel)domainHash.get(wrapper.getHighDomainAttribute());
		ILatticeNodeModel lowDomain = (ILatticeNodeModel)domainHash.get(wrapper.getLowDomainAttribute());
		model.setHigh(highDomain);
		model.setHighBounds(highDomain.getBounds());
		model.setLow(lowDomain);
		model.setLowBounds(lowDomain.getBounds());
		model.setId(wrapper.getIdAttribute());
		
		model.commitTransaction();
	}
	
	public void updateDataImpl () {
		IDomainRelationshipWrapper wrapper = (IDomainRelationshipWrapper)getDataObject();
		ILatticeLinkModel model = (ILatticeLinkModel)getGuiModel();
		wrapper.setHighDomainAttribute(model.getHigh().getId());
		wrapper.setLowDomainAttribute(model.getLow().getId());
	}
	
}









