package org.ofbiz.content;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Locale;
import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilCache;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntity;
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
import org.ofbiz.content.content.ContentServices;
import org.ofbiz.content.data.DataServices;
import org.ofbiz.content.content.ContentWorker;

/**
 * ContentManagementWorker Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.4 $
 * @since      3.0
 *
 * 
 */
public class ContentManagementWorker {

    public static final String module = ContentManagementWorker.class.getName();

    public static void mruAdd(HttpServletRequest request, GenericEntity pk, String suffix ) {
        HttpSession session = request.getSession();
        mruAdd(session, pk, suffix );
    }

    public static void mruAdd(HttpServletRequest request, GenericEntity pk ) {
        HttpSession session = request.getSession();
        mruAdd(session, pk, null );
    }

    public static void mruAdd(HttpSession session, GenericEntity pk ) {
        mruAdd(session, pk, null );
    }

    public static void mruAdd(HttpSession session, GenericEntity pk, String suffix ) {

        if (pk == null) return;

        Map lookupCaches = (Map)session.getAttribute("lookupCaches");
        if(lookupCaches == null){
            lookupCaches = new HashMap();
            session.setAttribute("lookupCaches", lookupCaches);
        }    
            //Debug.logVerbose("in mruAdd, lookupCaches:" + lookupCaches, "");
        String entityName = pk.getEntityName();
            //Debug.logVerbose("in mruAdd, entityName:" + entityName, "");
            //Debug.logVerbose("in mruAdd, suffix:" + suffix, "");
        if (entityName.indexOf("DataResource") >= 0) {
            GenericDelegator delegator = pk.getDelegator();
      
            // Force all view variations to DataResource
            GenericValue p = delegator.makeValue("DataResourceContentView", null);
            //Debug.logVerbose("in mruAdd, p:" + p, "");
            String s = null;
            try {
                s = (String)pk.get("dataResourceId");
                if (UtilValidate.isEmpty(s))  {
                    s = (String)pk.get("drDataResourceId");
                }
                p.set("dataResourceId", s);
                s = (String)pk.get("contentId");
                if (UtilValidate.isEmpty(s))  {
                    s = (String)pk.get("coContentId");
                }
                p.set("coContentId", s);
            } catch(IllegalArgumentException e) { 
                // ignore 
            }
            //Debug.logVerbose("in mruAdd, s:" + s, "");
            if (UtilValidate.isNotEmpty(s))  {
                mruAddByEntityName( "DataResourceContentView", null, p, lookupCaches);
                if (suffix != null && suffix.length() > 0) {
                    mruAddByEntityName( "DataResourceContentView", suffix, p, lookupCaches);
                }
            }

        } else {
            mruAddByEntityName( entityName, null, pk, lookupCaches);
            if (suffix != null && suffix.length() > 0) {
                mruAddByEntityName( entityName, suffix, pk, lookupCaches);
            }
        }
        return;
    }

   /**
    * Makes an entry in the "most recently used" cache. It picks the cache
    * by the entity name and builds a signature from the primary key values.
    *
    * @param entityName 
    * @param suffix 
    * @param pk either a GenericValue or GenericPK - populated
    */
    public static void mruAddByEntityName(String entityName, String suffix, 
                                          GenericEntity pk, Map lookupCaches) {

            //Debug.logVerbose("in mruAddByEntityName, pk:" + pk, "");
        String cacheEntityName = entityName;
        if (UtilValidate.isNotEmpty(suffix)) {
            cacheEntityName = entityName + suffix;
        }
            //Debug.logVerbose("in mruAddByEntityName, cacheEntityName:" + cacheEntityName, "");
        UtilCache lkupCache = (UtilCache)lookupCaches.get(cacheEntityName);
            //Debug.logVerbose("in mruAddByEntityName, lkupCache:" + lkupCache, "");
        if(lkupCache == null){
            lkupCache	= new UtilCache(cacheEntityName,10,0);
            lookupCaches.put(cacheEntityName, lkupCache);
        }    
        
        String idSig = buildPKSig(pk, null);
            //Debug.logVerbose("in mruAddByEntityName, idSig:" + idSig, "");
        GenericPK p = pk.getPrimaryKey();
            //Debug.logVerbose("in mruAddByEntityName, p:" + p, "");
        lkupCache.put(idSig,p);
        return;
    }


   /**
    * Builds a string signature from a GenericValue or GenericPK.
    *
    * @param pk either a populated GenericValue or GenericPK.
    * @param suffix a string that can be used to distinguish the signature (probably not used).
    */
    public static String buildPKSig( GenericEntity pk, String suffix ) {

        String sig = "";
        Collection keyColl = pk.getPrimaryKey().getAllKeys();
        List keyList = new ArrayList(keyColl);
        Collections.sort(keyList);
        Iterator it = keyList.iterator();
        while (it.hasNext()) {
            String ky = (String)it.next();
            String val = (String)pk.get(ky);
            //Debug.logVerbose("in buildPKSig, ky:" + ky + " val:" + val, "");
            if (val != null && val.length() > 0) {
                if (sig.length() > 0) sig += "_";
                sig += val;
            }
        }
        if (suffix != null && suffix.length() > 0) {
            if (sig.length() > 0) sig += "_";
            sig += suffix;
        }
        return sig;
    }


    public static void setCurrentEntityMap(HttpServletRequest request, GenericEntity ent) {
     
        String entityName = ent.getEntityName();
        setCurrentEntityMap(request, entityName, ent);
    }

    public static void setCurrentEntityMap(HttpServletRequest request,
                                 String entityName, GenericEntity ent) {
        HttpSession session = request.getSession();
        Map currentEntityMap = (Map)session.getAttribute("currentEntityMap");
        if(currentEntityMap == null){
            currentEntityMap     = new HashMap();
            session.setAttribute("currentEntityMap", currentEntityMap);
        }

        currentEntityMap.put(entityName, ent);
        //Debug.logVerbose("in setCurrentEntityMap, ent:" + ent,"");
    }
}
