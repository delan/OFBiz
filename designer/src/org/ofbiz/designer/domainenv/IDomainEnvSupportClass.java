package org.ofbiz.designer.domainenv;

import org.ofbiz.designer.pattern.*;

public interface IDomainEnvSupportClass extends IDataSupportClass {
    public IDomainInfo createDomainInfo(String id);
    public IDomainRelationship createDomainRelationship(String id);
    public IPolicyRecord createPolicyRecord(String id);
    public IPolicyRecord getReceivePolicy(String fromDomain, String toDomain);
    public IPolicyRecord getSendPolicy(String fromDomain, String toDomain);

}
