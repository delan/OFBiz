package org.ofbiz.content;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.util.Locale;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilCache;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * ContentManagementWorker Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.1 $
 * @since      3.0
 *
 * 
 */
public class ContentManagementWorker {

    public static final String module = ContentManagementWorker.class.getName();


    public static void lruAdd(HttpSession session, GenericPK pk ) {

        Map lookupCaches = (Map)session.getAttribute("lookupCaches");
        if(lookupCaches == null){
            lookupCaches = new HashMap();
            session.setAttribute("lookupCaches", lookupCaches);
        }    
Debug.logInfo("in lruAdd, lookupCaches:" + lookupCaches, "");
        String entityName = pk.getEntityName();
Debug.logInfo("in lruAdd, entityName:" + entityName, "");
        UtilCache lkupCache = (UtilCache)lookupCaches.get(entityName);
Debug.logInfo("in lruAdd, lkupCache:" + lkupCache, "");
        if(lkupCache == null){
            lkupCache	= new UtilCache(entityName,10,0);
            lookupCaches.put(entityName, lkupCache);
        }    
Debug.logInfo("in lruAdd, lkupCache(2):" + lkupCache, "");
Debug.logInfo("in lruAdd, pk:" + pk, "");
        
        String idSig = buildPKSig(pk);
Debug.logInfo("in lruAdd, idSig:" + idSig, "");
        lkupCache.put(idSig,pk);
        return;
    }


    public static String buildPKSig( GenericPK pk ) {

        String sig = "";
        Collection keyColl = pk.getAllKeys();
        Iterator it = keyColl.iterator();
        while (it.hasNext()) {
            if (sig.length() > 0) sig += "_";
            String ky = (String)it.next();
            String val = (String)pk.get(ky);
            if (val == null) val = "";
            sig += val;
        }
        return sig;
    }
}
