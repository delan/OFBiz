package org.ofbiz.designer.newdesigner.LatticeEditor.model;

import java.util.*;
import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.generic.*;

public class DomainEnvModel extends LatticeModel implements IDomainEnvModel {

    private DomainEnvModel() {
    }

    public static ILatticeModel createModelProxy() {
        IDomainEnvModel newModel = new DomainEnvModel();
        IDomainEnvModelWrapper proxy = null;

        try {
            proxy = (IDomainEnvModelWrapper)GuiModelProxy.newProxyInstance(newModel,"latticeeditor.model.IDomainEnvModelWrapper");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return proxy;
    }


    public Object[][] getRelationships() {
        Object[][] superRel = super.getRelationships();
        int numRels = superRel.length;
        Object[][] returnObj = new Object[numRels+1][3];
        for(int i=0;i<numRels;i++) {
            returnObj[i] = superRel[i];
        }                              
        returnObj[numRels][0]="policies";
        returnObj[numRels][1]="parent";
        returnObj[numRels][2]=MULTIPLE;
        return returnObj;
    }

    public void addPolicy(PolicyModelContainer newPolicy) {
        newPolicy.beginTransaction();
        newPolicy.setParent(this);
        addRelationship("policies",newPolicy);
        newPolicy.commitTransaction();
    }

    public PolicyModelContainer addPolicy(IDomainModel fromDomain, IDomainModel toDomain, String type) {
        PolicyModelContainer newPolicy = new PolicyModelContainer(PlainDocumentModel.createModelProxy());
        newPolicy.beginTransaction();
        newPolicy.setParent(this);
        newPolicy.setId(fromDomain.getId()+toDomain.getId()+type);
        newPolicy.setFromDomain(fromDomain);
        newPolicy.setToDomain(toDomain);
        newPolicy.setType(type);
        //why doesn't this trigger translator?
        addRelationship("policies",newPolicy);
        newPolicy.commitTransaction();
        return newPolicy;
    }

    /*
    public void removePolicy(PolicyModelContainer removal) {
        removeRelationship("policies", removal);
    }
    */

    public PolicyModelContainer getPolicyAt(int index) {
        return(PolicyModelContainer)getRelationshipAt("policies",index);
    }

    public int getPolicyCount() {
        return getRelationshipCount("policies");
    }

    public Hashtable policiesHashedById() {
        Hashtable returnObj = new Hashtable();
        PolicyModelContainer aContainer;
        for(int i=0;i<getRelationshipCount("policies");i++) {
            aContainer = (PolicyModelContainer)getRelationshipAt("policies",i);
            returnObj.put(aContainer.getId(),aContainer);
        }
        return returnObj;
    }

    public Hashtable policiesHashedByFromToType() {
        Hashtable returnObj = new Hashtable();
        PolicyModelContainer aContainer;
        for(int i=0;i<getRelationshipCount("policies");i++) {
            aContainer = (PolicyModelContainer)getRelationshipAt("policies",i);
            returnObj.put(aContainer.getFromDomain().getId()+aContainer.getToDomain().getId()+aContainer.getType(),
                          aContainer);
        }
        return returnObj;
    }

    /*
    public HashSet getModifyMethods() {
        HashSet returnObj = super.getModifyMethods();
        returnObj.add("addPolicy");
        returnObj.add("removePolicy");
        return returnObj;
    }
    */

    public ILatticeNodeModel createNewNode() {
        return(ILatticeNodeModel) DomainModel.createModelProxy();
    }

    protected static HashSet modifyMethods = new HashSet();
    static {
        modifyMethods.addAll(LatticeModel.modifyMethods);
        modifyMethods.add("addPolicy");
    }

    public HashSet getModifyMethods() {
        return modifyMethods;
    }


}
