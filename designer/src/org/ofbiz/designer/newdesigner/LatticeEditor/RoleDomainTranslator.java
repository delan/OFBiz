package org.ofbiz.designer.newdesigner.LatticeEditor;

import org.ofbiz.designer.generic.*;
import org.ofbiz.designer.pattern.*;
import java.util.*;
import org.ofbiz.designer.util.*;
import org.ofbiz.designer.roledomain.*;
import org.ofbiz.designer.newdesigner.LatticeEditor.model.*;

import java.awt.*;

import org.ofbiz.wrappers.xml.*;

							


public class RoleDomainTranslator extends BaseTranslator {

	
	public RoleDomainTranslator(ILatticeModelWrapper modelIn, IRoleDomainWrapper wrapperIn, String direction) {
		super(modelIn, wrapperIn);
		synchronize(direction);
	}
	
	
	public void updateModelImpl(){
		
		ILatticeModel model = (ILatticeModel) getGuiModel();
		IRoleDomainWrapper wrapper = (IRoleDomainWrapper)getDataObject();
		
		model.beginTransaction();
		
		
		model.setId(wrapper.getIdAttribute());
		model.setName(wrapper.getName());
		
		int numDataObjs;
		RoleTranslator roleTrans;
		IRole roleData;
		String id;
		int idNum;
		
		IRoleModelWrapper roleModel = null;
		
		for(int i=model.getLatticeNodeCount()-1;i>=0;i--) {
			roleModel = (IRoleModelWrapper)model.getLatticeNodeAt(i);
			if(wrapper.getIdRef(roleModel.getId())==null)
				roleModel.die();
		}
		
		ILatticeLinkModel relModel;

		
		for(int i=model.getLatticeLinkCount()-1;i>=0;i--) {
			relModel = (ILatticeLinkModel)model.getLatticeLinkAt(i);
			if(wrapper.getIdRef(relModel.getId())==null)
				relModel.die();
		}
		
		Hashtable roleHash = model.latticeNodesHashedById();
		
		for(int i = 0; i < wrapper.getRoleCount(); i++) {
			roleData = wrapper.getRoleAt(i);
			id = roleData.getIdAttribute();
			if(!(roleHash.containsKey(id))) {
				roleModel = (IRoleModelWrapper)RoleModel.createModelProxy();
				model.addLatticeNode(roleModel);
				new RoleTranslator(roleModel,(IRoleWrapper)roleData, RoleTranslator.UPDATE_MODEL);
			}
		}
		
		
		IRoleRelationship relData;
		ILatticeLinkModelWrapper newRel;
		Hashtable relHash = model.latticeLinksHashedById();
		
		for(int i = 0; i < wrapper.getRoleRelationshipCount(); i++) {
			relData = wrapper.getRoleRelationshipAt(i);
			id = relData.getIdAttribute();
			if(!(relHash.containsKey(id))) {
				newRel = (ILatticeLinkModelWrapper)LatticeLinkModel.createModelProxy();
				model.addLatticeLink(newRel);
				new RoleRelationshipTranslator(newRel,(IRoleRelationshipWrapper)relData, RoleRelationshipTranslator.UPDATE_MODEL);
			}
		}
		
		model.commitTransaction();
		
		
	}
	
	public void updateDataImpl(){
		LOG.println("!!!!!!!!!!!!!!!!!!");
		
		ILatticeModel model = (ILatticeModel) getGuiModel();
		IRoleDomainWrapper wrapper = (IRoleDomainWrapper)getDataObject();
		
		wrapper.setIdAttribute(model.getId());
		wrapper.setName(model.getName());
		
		int numDataObjs;
		IRole roleData;
		String id = null;
		
		
		Hashtable roleHash = model.latticeNodesHashedById();
		
		for(int i=wrapper.getRoleCount()-1;i>=0;i--) {
			roleData = wrapper.getRoleAt(i);
			id = roleData.getIdAttribute();
			if(!(roleHash.containsKey(id))) {
				wrapper.removeRoleAt(i);
			}
		}
		
		RelationshipTranslator relTrans;
		IRoleRelationship relData;
		
		Hashtable relHash = model.latticeLinksHashedById();
		
		for(int i=wrapper.getRoleRelationshipCount()-1;i>=0;i--) {
			relData = wrapper.getRoleRelationshipAt(i);
			id = relData.getIdAttribute();
			if(!relHash.containsKey(id)) wrapper.removeRoleRelationshipAt(i);
		}
		
		
		IRoleModelWrapper roleModel;
		
		for(int i = 0; i < model.getLatticeNodeCount();i++) {
			roleModel = (IRoleModelWrapper)model.getLatticeNodeAt(i);
			if(wrapper.getIdRef(roleModel.getId())==null) {
				roleData = wrapper.createRole(roleModel.getId());
				new RoleTranslator(roleModel,(IRoleWrapper)roleData, RoleTranslator.UPDATE_DATA);
			}
		}
		
		ILatticeLinkModelWrapper relModel;
		
		for(int i = 0; i < model.getLatticeLinkCount();i++) {
			relModel = (ILatticeLinkModelWrapper)model.getLatticeLinkAt(i);
			if(wrapper.getIdRef(relModel.getId())==null) {
				relData = wrapper.createRoleRelationship(relModel.getId());
				new RoleRelationshipTranslator(relModel,(IRoleRelationshipWrapper)relData, RoleRelationshipTranslator.UPDATE_DATA);
			}	
		}
		
	}                                  
	
}
