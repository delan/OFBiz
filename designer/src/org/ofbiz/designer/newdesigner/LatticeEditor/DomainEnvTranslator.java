package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.domainenv.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.*;

import java.awt.*;

import org.ofbiz.wrappers.xml.*;

							


public class DomainEnvTranslator extends BaseTranslator {

	
	public DomainEnvTranslator(IDomainEnvModelWrapper modelIn, IDomainEnvWrapper wrapperIn, String direction) {
		super(modelIn, wrapperIn);
		synchronize(direction);
	}
	
	
	public void updateModelImpl(){
		
		IDomainEnvModel model = (IDomainEnvModel) getGuiModel();
		IDomainEnvWrapper wrapper = (IDomainEnvWrapper)getDataObject();
		
		model.beginTransaction();
		
		
		model.setId(wrapper.getIdAttribute());
		model.setName(wrapper.getName());
		
		int numDataObjs;
		DomainTranslator domainTrans;
		IDomainInfo domainData;
		String id;
		int idNum;
		
		IDomainModelWrapper domainModel = null;
		
		for(int i=model.getLatticeNodeCount()-1;i>=0;i--) {
			domainModel = (IDomainModelWrapper)model.getLatticeNodeAt(i);
			if(wrapper.getIdRef(domainModel.getId())==null)
				domainModel.die();
		}
		
		ILatticeLinkModel relModel;

		
		for(int i=model.getLatticeLinkCount()-1;i>=0;i--) {
			relModel = (ILatticeLinkModel)model.getLatticeLinkAt(i);
			if(wrapper.getIdRef(relModel.getId())==null)
				relModel.die();
		}
		
		PolicyModelContainer policyModel;

		for(int i=model.getPolicyCount()-1;i>=0;i--) {
			policyModel = (PolicyModelContainer)model.getPolicyAt(i);
			if(wrapper.getIdRef(policyModel.getId())==null)
				policyModel.die();
		}
		
		
		Hashtable domainHash = model.latticeNodesHashedById();
		
		for(int i = 0; i < wrapper.getDomainInfoCount(); i++) {
			domainData = wrapper.getDomainInfoAt(i);
			id = domainData.getIdAttribute();
			if(!(domainHash.containsKey(id))) {
				domainModel = (IDomainModelWrapper)DomainModel.createModelProxy();
				model.addLatticeNode(domainModel);
				new DomainTranslator(domainModel,(IDomainInfoWrapper)domainData, DomainTranslator.UPDATE_MODEL);
			}
		}
		
		
		IDomainRelationship relData;
		ILatticeLinkModel newRel;
		Hashtable relHash = model.latticeLinksHashedById();
		
		for(int i = 0; i < wrapper.getDomainRelationshipCount(); i++) {
			relData = wrapper.getDomainRelationshipAt(i);
			id = relData.getIdAttribute();
			if(!(relHash.containsKey(id))) {
				newRel = LatticeLinkModel.createModelProxy();
				model.addLatticeLink(newRel);
				new RelationshipTranslator((ILatticeLinkModelWrapper)newRel,(IDomainRelationshipWrapper)relData, RelationshipTranslator.UPDATE_MODEL);
			}
		}
		
		IPolicyRecord policyData;
		
		Hashtable policyHash = model.policiesHashedById();
		domainHash = model.latticeNodesHashedById();
		
		for(int i = 0; i < wrapper.getPolicyRecordCount(); i++) {
			policyData = wrapper.getPolicyRecordAt(i);
			id = policyData.getIdAttribute();
			if(!(policyHash.containsKey(id))) {
				policyModel = new PolicyModelContainer(PlainDocumentModel.createModelProxy());
				policyModel.setId(policyData.getIdAttribute());
				policyModel.setFromDomain((IDomainModel)domainHash.get(policyData.getFromDomainAttribute()));
				policyModel.setToDomain((IDomainModel)domainHash.get(policyData.getToDomainAttribute()));
				policyModel.setType(policyData.getPolicyTypeAttribute());
				model.addPolicy(policyModel);
				new DocumentTranslator(policyModel,(IPolicyRecordWrapper)policyData,"PCDATA",DocumentTranslator.UPDATE_MODEL);
			}
		}
		
		
		model.commitTransaction();
		
		
	}
	
	public void updateDataImpl(){
		LOG.println("@@@@@@@@");
		
		IDomainEnvModel model = (IDomainEnvModel) getGuiModel();
		IDomainEnvWrapper wrapper = (IDomainEnvWrapper)getDataObject();
		
		wrapper.setIdAttribute(model.getId());
		wrapper.setName(model.getName());
		
		int numDataObjs;
		IDomainInfo domainData;
		String id = null;
		
		
		Hashtable domainHash = model.latticeNodesHashedById();
		
		for(int i=wrapper.getDomainInfoCount()-1;i>=0;i--) {
			domainData = wrapper.getDomainInfoAt(i);
			id = domainData.getIdAttribute();
			if(!(domainHash.containsKey(id))) {
				wrapper.removeDomainInfoAt(i);
			}
		}
		
		RelationshipTranslator relTrans;
		IDomainRelationship relData;
		
		Hashtable relHash = model.latticeLinksHashedById();
		
		for(int i=wrapper.getDomainRelationshipCount()-1;i>=0;i--) {
			relData = wrapper.getDomainRelationshipAt(i);
			id = relData.getIdAttribute();
			if(!relHash.containsKey(id)) wrapper.removeDomainRelationshipAt(i);
		}
		
		DocumentTranslator policyTrans;
		IPolicyRecord policyData;
		
		Hashtable policyHash = model.policiesHashedById();
		
		for(int i=wrapper.getPolicyRecordCount()-1;i>=0;i--) {
			policyData = wrapper.getPolicyRecordAt(i);
			id = policyData.getIdAttribute();
			if(!(policyHash.containsKey(id))) wrapper.removePolicyRecordAt(i);
		}
		
		IDomainModelWrapper domainModel;
		
		for(int i = 0; i < model.getLatticeNodeCount();i++) {
			domainModel = (IDomainModelWrapper)model.getLatticeNodeAt(i);
			if(wrapper.getIdRef(domainModel.getId())==null) {
				domainData = wrapper.createDomainInfo(domainModel.getId());
				new DomainTranslator(domainModel,(IDomainInfoWrapper)domainData, DomainTranslator.UPDATE_DATA);
		
			}
		}
		
		ILatticeLinkModelWrapper relModel;
		
		for(int i = 0; i < model.getLatticeLinkCount();i++) {
			relModel = (ILatticeLinkModelWrapper)model.getLatticeLinkAt(i);
			if(wrapper.getIdRef(relModel.getId())==null) {
				relData = wrapper.createDomainRelationship(relModel.getId());
				new RelationshipTranslator(relModel,(IDomainRelationshipWrapper)relData, RelationshipTranslator.UPDATE_DATA);
			}	
		}
		
		
		PolicyModelContainer policyModel;
		
		for(int i = 0; i < model.getPolicyCount();i++) {
			policyModel = (PolicyModelContainer)model.getPolicyAt(i);
			if(wrapper.getIdRef(policyModel.getId())==null) {
				policyData = (IPolicyRecordWrapper)wrapper.createPolicyRecord(policyModel.getId());
				policyData.setFromDomainAttribute(policyModel.getFromDomain().getId());
				policyData.setToDomainAttribute(policyModel.getToDomain().getId());
				policyData.setPolicyTypeAttribute(policyModel.getType());
				new DocumentTranslator(policyModel,(IPolicyRecordWrapper)policyData,"PCDATA",DocumentTranslator.UPDATE_DATA);
			}	
		}
		
	}                                  
	
}

