/*
 * $Id: EntityRepositoryMgr.java,v 1.1 2004/07/11 23:26:29 ajzeneski Exp $
 *
 */
package org.ofbiz.shark.repository;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Iterator;

import org.ofbiz.shark.transaction.JtaTransaction;
import org.ofbiz.shark.container.SharkContainer;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;

import org.enhydra.shark.api.internal.repositorypersistence.RepositoryPersistenceManager;
import org.enhydra.shark.api.internal.repositorypersistence.RepositoryException;
import org.enhydra.shark.api.internal.working.CallbackUtilities;
import org.enhydra.shark.api.RootException;
import org.enhydra.shark.api.RepositoryTransaction;
import org.enhydra.shark.api.TransactionException;

/**
 * 
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class EntityRepositoryMgr implements RepositoryPersistenceManager {

    public static final String module = EntityRepositoryMgr.class.getName();
    protected CallbackUtilities callBack = null;

    public void configure(CallbackUtilities callBack) throws RootException {
        this.callBack = callBack;
    }

    public void uploadXPDL(RepositoryTransaction t, String xpdlId, byte[] xpdl) throws RepositoryException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        try {
            GenericValue v = delegator.makeValue("WfRepository", null);
            v.set("xpdlId", xpdlId);
            v.set("xpdlVersion", UtilDateTime.nowDateString());
            v.setBytes("xpdlData", xpdl);
            delegator.create(v);
        } catch (GenericEntityException e) {
            throw new RepositoryException(e);
        }
    }

    public void updateXPDL(RepositoryTransaction t, String xpdlId, String xpdlVersion, byte[] xpdl) throws RepositoryException {
        GenericValue value = this.getXpdlValue(xpdlId, xpdlVersion, false);
        if (value != null) {
            value.setBytes("xpdlData", xpdl);
            try {
               value.store();
            } catch (GenericEntityException e) {
                throw new RepositoryException(e);
            }
        }
    }

    public void deleteXPDL(RepositoryTransaction t, String xpdlId, String xpdlVersion) throws RepositoryException {
        GenericValue value = this.getXpdlValue(xpdlId, xpdlVersion, false);
        if (value != null) {
            try {
                value.remove();
            } catch (GenericEntityException e) {
                throw new RepositoryException(e);
            }
        }
    }

    public void moveToHistory(RepositoryTransaction t, String xpdlId, String xpdlVersion) throws RepositoryException {
        GenericValue value = this.getXpdlValue(xpdlId, xpdlVersion, false);
        value.set("isHistorical", "Y");
        try {
            value.store();
        } catch (GenericEntityException e) {
            throw new RepositoryException(e);
        }
    }

    public void deleteFromHistory(RepositoryTransaction t, String xpdlId, String xpdlVersion) throws RepositoryException {
        GenericValue value = this.getXpdlValue(xpdlId, xpdlVersion, true);
        if (value != null) {
            try {
                value.remove();
            } catch (GenericEntityException e) {
                throw new RepositoryException(e);
            }
        }
    }

    public void clearRepository(RepositoryTransaction t) throws RepositoryException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        try {
            delegator.removeByAnd("WfRepository", null);
        } catch (GenericEntityException e) {
            throw new RepositoryException(e);
        }
    }

    public String getCurrentVersion(RepositoryTransaction t, String xpdlId) throws RepositoryException {
        List lookupList = this.getXpdlValues(xpdlId, null, false);
        GenericValue value = EntityUtil.getFirst(lookupList);
        if (value != null) {
            return value.getString("xpdlVersion");
        }
        return null;
    }

    public String getNextVersion(RepositoryTransaction t, String xpdlId) throws RepositoryException {
        return UtilDateTime.nowDateString();
    }

    public byte[] getXPDL(RepositoryTransaction t, String xpdlId) throws RepositoryException {
        List lookupList = this.getXpdlValues(xpdlId, null, false);
        GenericValue value = EntityUtil.getFirst(lookupList);
        if (value != null) {
            return value.getBytes("xpdlData");
        }
        return null;
    }

    public byte[] getXPDL(RepositoryTransaction t, String xpdlId, String xpdlVersion) throws RepositoryException {
        GenericValue value = this.getXpdlValue(xpdlId, xpdlVersion, false);
        if (value != null) {
            return value.getBytes("xpdlData");
        }
        return null;
    }

    public List getXPDLVersions(RepositoryTransaction t, String xpdlId) throws RepositoryException {
        List lookupList = this.getXpdlValues(xpdlId, null, false);
        List versionList = new ArrayList();
        if (!UtilValidate.isEmpty(lookupList)) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                versionList.add(v.getString("xpdlVersion"));
            }
        }
        return versionList;
    }

    public boolean doesXPDLExist(RepositoryTransaction t, String xpdlId) throws RepositoryException {
        List xpdls = this.getXpdlValues(xpdlId, null, false);
        return !UtilValidate.isEmpty(xpdls);
    }

    public boolean doesXPDLExist(RepositoryTransaction t, String xpdlId, String xpdlVersion) throws RepositoryException {
        GenericValue xpdl = this.getXpdlValue(xpdlId, xpdlVersion, false);
        return (xpdl != null);
    }

    public List getExistingXPDLIds(RepositoryTransaction t) throws RepositoryException {
        List lookupList = this.getXpdlValues(null, null, false);
        List idList = new ArrayList();
        if (!UtilValidate.isEmpty(lookupList)) {
            Iterator i = lookupList.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                String id = v.getString("xpdlId");
                if (!idList.contains(id)) {
                    idList.add(id);
                }
            }
        }
        return idList;
    }

    public void addXPDLReference(RepositoryTransaction t, String referredXPDLId, String referringXPDLId, String referringXPDLVersion) throws RepositoryException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        GenericValue ref = delegator.makeValue("WfRepositoryRef", null);
        ref.set("xpdlId", referringXPDLId);
        ref.set("xpdlVersion", referringXPDLVersion);
        ref.set("refXpdlId", referredXPDLId);
        try {
            delegator.create(ref);
        } catch (GenericEntityException e) {
            throw new RepositoryException(e);
        }
    }

    public List getReferringXPDLIds(RepositoryTransaction t, String referredXPDLId) throws RepositoryException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List referringIds = new ArrayList();
        List refs = null;
        try {
            refs = delegator.findByAnd("WfRepositoryRef", UtilMisc.toMap("refXpdlId", referredXPDLId));
        } catch (GenericEntityException e) {
            throw new RepositoryException(e);
        }
        if (!UtilValidate.isEmpty(refs)) {
            Iterator i = refs.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                referringIds.add(v.getString("xpdlId"));
            }
        }
        return referringIds;
    }

    public List getReferringXPDLVersions(RepositoryTransaction t, String referredXPDLId, String referringXPDLId) throws RepositoryException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List referringVers = new ArrayList();
        List refs = null;
        try {
            refs = delegator.findByAnd("WfRepositoryRef", UtilMisc.toMap("refXpdlId", referredXPDLId, "xpdlId", referringXPDLId));
        } catch (GenericEntityException e) {
            throw new RepositoryException(e);
        }
        if (!UtilValidate.isEmpty(refs)) {
            Iterator i = refs.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                referringVers.add(v.getString("xpdlVersion"));
            }
        }
        return referringVers;
    }

    public List getReferredXPDLIds(RepositoryTransaction t, String referringXPDLId, String referringXPDLVersion) throws RepositoryException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        List referringIds = new ArrayList();
        List refs = null;
        try {
            refs = delegator.findByAnd("WfRepositoryRef", UtilMisc.toMap("xpdlId", referringXPDLId, "xpdlVersion", referringXPDLVersion));
        } catch (GenericEntityException e) {
            throw new RepositoryException(e);
        }
        if (!UtilValidate.isEmpty(refs)) {
            Iterator i = refs.iterator();
            while (i.hasNext()) {
                GenericValue v = (GenericValue) i.next();
                referringIds.add(v.getString("refXpdlId"));
            }
        }
        return referringIds;
    }

    public RepositoryTransaction createTransaction() throws TransactionException {
        return new JtaTransaction();
    }

    private GenericValue getXpdlValue(String xpdlId, String xpdlVersion, boolean includeHistorical) throws RepositoryException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        GenericValue xpdl = null;
        try {
            xpdl = delegator.findByPrimaryKey("WfRepository", UtilMisc.toMap("xpdlId", xpdlId, "xpdlVersion", xpdlVersion));
            if (!includeHistorical && xpdl.getString("isHistorical").equalsIgnoreCase("Y")) {
                xpdl = null;

            }
        } catch (GenericEntityException e) {
            throw new RepositoryException(e);
        }
        return xpdl;
    }

    private List getXpdlValues(String xpdlId, String xpdlVersion, boolean includeHistory) throws RepositoryException {
        GenericDelegator delegator = SharkContainer.getDelegator();
        Map lookupMap = new HashMap();
        if (xpdlId != null) {
            lookupMap.put("xpdlId", xpdlId);
        }
        if (xpdlVersion != null) {
            lookupMap.put("xpdlVersion", xpdlVersion);
        }
        if (includeHistory) {
            lookupMap.put("isHistorical", "Y");
        }

        List lookupList = null;
        try {
            lookupList = delegator.findByAnd("WfRepository", lookupMap, UtilMisc.toList("xpdlVersion"));
        } catch (GenericEntityException e) {
            throw new RepositoryException(e);
        }

        return lookupList;
    }
}
