package org.ofbiz.designer.domainenv;

import org.ofbiz.designer.pattern.*;
import org.ofbiz.designer.generic.*;

public class DomainEnvSupportClass extends AbstractDataSupportClass implements IDomainEnvSupportClass {
    public IDomainInfo createDomainInfo(String id) {
        IDomainInfo newDomain = new DomainInfo();
        newDomain.setIdAttribute(id);
        newDomain.setColor(new Color());
        newDomain.setPosition(new Position());
        newDomain.setDescription("");
        domainEnv().addDomainInfo(newDomain);
        setIdRef(id,newDomain);
        return newDomain;
    }

    public IDomainRelationship createDomainRelationship(String id) {
        IDomainRelationship newRel = new DomainRelationship();
        newRel.setIdAttribute(id);
        domainEnv().addDomainRelationship(newRel);
        setIdRef(id,newRel);
        return newRel;
    }

    public IPolicyRecord createPolicyRecord(String id) {
        IPolicyRecord newPolicy = new PolicyRecord();
        newPolicy.setIdAttribute(id);
        newPolicy.setPolicyTypeAttribute("");
        domainEnv().addPolicyRecord(newPolicy);
        setIdRef(id,newPolicy);
        return newPolicy;
    }

    private IPolicyRecord getPolicy(String fromDomain, String toDomain, String mode){
        int count = domainEnv().getPolicyRecordCount();
        for(int i=0;i<count;i++) {
            IPolicyRecord pr = domainEnv().getPolicyRecordAt(i);
            if(pr.getFromDomainAttribute().equals(fromDomain) && pr.getToDomainAttribute().equals(toDomain) && pr.getPolicyTypeAttribute().equals(mode)) 
                return pr;
        }

        IPolicyRecord pr = createPolicyRecord(getXml().generateUniqueName("Policy"));
        pr.setPolicyTypeAttribute(mode);
        pr.setFromDomainAttribute(fromDomain);
        pr.setToDomainAttribute(toDomain);
        return pr;
    }

    public IPolicyRecord getReceivePolicy(String fromDomain, String toDomain){
        return getPolicy(fromDomain, toDomain, "Receive");
    }

    public IPolicyRecord getSendPolicy(String fromDomain, String toDomain){
        return getPolicy(fromDomain, toDomain, "Send");
    }
    
    public IDomainEnv domainEnv(){
        return (IDomainEnv)getDtdObject();
    }
}
