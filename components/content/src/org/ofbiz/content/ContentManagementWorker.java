package org.ofbiz.content;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import java.util.Map;
import java.util.Locale;
import java.io.IOException;
import java.sql.Timestamp;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilCache;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.util.EntityUtil;
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
import org.ofbiz.content.content.ContentServicesComplex;
import org.ofbiz.content.data.DataServices;
import org.ofbiz.content.content.ContentWorker;
import org.ofbiz.content.content.ContentPermissionServices;
import org.ofbiz.minilang.MiniLangException;

/**
 * ContentManagementWorker Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.6 $
 * @since      3.0
 *
 * 
 */
public class ContentManagementWorker {

    public static final String module = ContentManagementWorker.class.getName();
    public static Map cachedWebSitePublishPoints = new HashMap();
    public static Map cachedStaticValues = new HashMap();

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

    public static String getFromSomewhere(String name, org.ofbiz.base.util.OrderedMap paramMap, HttpServletRequest request, org.jpublish.JPublishContext context) {

        String ret = null;
        if (paramMap != null)
            ret = (String)paramMap.get(name);

        if (UtilValidate.isEmpty(ret)) {
            ret = (String)request.getAttribute(name);
            if (UtilValidate.isEmpty(ret)) {
                ret = (String)context.get(name);
            }
        }
        return ret;
    }

    public static void getCurrentValue(HttpServletRequest request, GenericDelegator delegator) {


        HttpSession session = request.getSession();
        Map currentEntityMap = (Map)session.getAttribute("currentEntityMap");
        if(currentEntityMap == null){
            currentEntityMap     = new HashMap();
            session.setAttribute("currentEntityMap", currentEntityMap);
        }
        Map paramMap = UtilHttp.getParameterMap(request);
        String entityName = (String)paramMap.get("entityName");
        if (UtilValidate.isEmpty(entityName))
            entityName = (String)request.getAttribute("entityName");
        GenericPK cachedPK = null;
        if (UtilValidate.isNotEmpty(entityName))
            cachedPK = (GenericPK)currentEntityMap.get(entityName);
        getCurrentValueWithCachedPK( request, delegator, cachedPK, entityName);
        GenericPK currentPK = (GenericPK)request.getAttribute("currentPK");
        currentEntityMap.put(entityName, currentPK);
        return;
    }

    public static void getCurrentValueWithCachedPK(HttpServletRequest request, GenericDelegator delegator, GenericPK cachedPK, String entityName) {

        Map paramMap = UtilHttp.getParameterMap(request);
        // Build the primary key that may have been passed in as key values
        GenericValue v = delegator.makeValue(entityName, null);
        GenericPK passedPK = v.getPrimaryKey();
        Collection keyColl = passedPK.getAllKeys();
        Iterator keyIt = keyColl.iterator();
        while (keyIt.hasNext()) {
            String attrName = (String)keyIt.next();
            String attrVal = (String)request.getAttribute(attrName);
            if (UtilValidate.isEmpty(attrVal)) {
                attrVal = (String)paramMap.get(attrName);
            }
            if (UtilValidate.isNotEmpty(attrVal)) {
                passedPK.put(attrName,attrVal);
            }
        }

        // If a full passed primary key exists, it takes precedence over a cached key
        // I cannot determine if the key testing utils of GenericEntity take into account
        // whether or not a field is populated.
        boolean useCached = false;
        boolean usePassed = true;
        if(cachedPK != null ) {
            useCached = true;
            keyColl = cachedPK.getPrimaryKey().getAllKeys();
            keyIt = keyColl.iterator();
            while(keyIt.hasNext()) {
                String sCached = null;
                String sPassed = null;
                Object oPassed = null;
                Object oCached = null;
                String ky = (String)keyIt.next();
                oPassed = passedPK.get(ky);
                if(oPassed != null) {
                    sPassed = oPassed.toString();
                    if(UtilValidate.isEmpty(sPassed)){
                        // If any part of passed key is not available, it can't be used
                        usePassed = false;
                    } else {
                        oCached = cachedPK.get(ky);
                        if(oCached != null) {
                            sCached = oCached.toString();
                            if(UtilValidate.isEmpty(sCached)){
                                useCached = false;
                            } else {
                            }
                        } else {
                            useCached = false;
                        }
                    }
                } else {
                    //useCached = false;
                    usePassed = false;
                }
            }
        }

        GenericPK currentPK = null;
        if (usePassed && useCached) {
            currentPK = passedPK;
        } else if (usePassed && !useCached) {
            currentPK = passedPK;
        } else if (!usePassed && useCached) {
            currentPK = cachedPK;
        }

        if (currentPK != null) {
            request.setAttribute("currentPK", currentPK);
            GenericValue currentValue = null;
            try {
                currentValue = delegator.findByPrimaryKey(currentPK.getPrimaryKey()); 
            } catch(GenericEntityException e) {
            }
            request.setAttribute("currentValue", currentValue);
        }

    }

    public static List getPermittedPublishPoints(GenericDelegator delegator, List allPublishPoints, GenericValue userLogin, Security security, String permittedAction, String permittedOperations, String passedRoles) throws GeneralException {

        List permittedPublishPointList = new ArrayList();
        
        // Check that user has permission to admin sites
        Iterator it = allPublishPoints.iterator();
        while(it.hasNext()) {
            GenericValue webSitePP = (GenericValue)it.next();
            String contentId = (String)webSitePP.get("contentId");
            String templateTitle = (String)webSitePP.get("templateTitle");
            GenericValue content = delegator.makeValue("Content", UtilMisc.toMap("contentId", contentId));
            String statusId = null;
            String entityAction = permittedAction;
            if (entityAction == null)
                entityAction = "_ADMIN";
            List passedPurposes = UtilMisc.toList("ARTICLE");
            List roles = StringUtil.split(passedRoles, "|");
            List targetOperationList = new ArrayList();
            if (UtilValidate.isEmpty(permittedOperations)) {
                 targetOperationList.add("CONTENT" + entityAction);
            } else {
                 targetOperationList = StringUtil.split(permittedOperations, "|");
            }
            Map results = null;
            //if (Debug.infoOn()) Debug.logInfo("in getPermittedPublishPoints, content:" + content, module);
            results = ContentPermissionServices.checkPermission(content, statusId, userLogin, passedPurposes, targetOperationList, roles, delegator, security, entityAction);
            String permissionStatus = (String)results.get("permissionStatus");
            if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
                String [] arr = {contentId,templateTitle};
                permittedPublishPointList.add(arr);
            }
        }
        return permittedPublishPointList;
    }

    public static List getAllPublishPoints(GenericDelegator delegator, String rootPubPt) throws GeneralException {

        GenericValue rootContent = null;
        List relatedPubPts = null;
        try {
            rootContent = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", rootPubPt));
            //relatedPubPts = delegator.findByAndCache("ContentAssoc", UtilMisc.toMap("contentIdTo", rootPubPt));
            relatedPubPts = delegator.findByAndCache("ContentAssoc", UtilMisc.toMap("contentIdTo", rootPubPt, "contentAssocTypeId", "SUBSITE"));

        } catch(GenericEntityException e) {
            throw new GeneralException(e.getMessage());
        }
        List allPublishPoints = new ArrayList();
        GenericValue webSitePublishPoint = null;
        GenericValue rootWebSitePublishPoint = null;
        GenericValue currentWebSitePublishPoint = null;
        GenericValue contentAssoc = null;
        Iterator it = relatedPubPts.iterator();
        while (it.hasNext()) {
           contentAssoc = (GenericValue)it.next();
           String pub = (String)contentAssoc.get("contentId");
           //webSitePublishPoint = delegator.findByPrimaryKeyCache("WebSitePublishPoint", UtilMisc.toMap("contentId", pub));
           webSitePublishPoint = getWebSitePublishPoint(delegator, pub, false);
           allPublishPoints.add(webSitePublishPoint);
        }
        return allPublishPoints;
    }

    public static Map getPublishPointMap(GenericDelegator delegator, String pubPtId ) throws GeneralException {

        List publishPointList = getAllPublishPoints( delegator, pubPtId );
        Map publishPointMap = new HashMap();
        Iterator it = publishPointList.iterator();
        while (it.hasNext()) {
           GenericValue webSitePublishPoint = (GenericValue)it.next();
           String pub = (String)webSitePublishPoint.get("contentId");
           publishPointMap.put(pub, webSitePublishPoint);
        }
        return publishPointMap;
    }


    public static void getAllPublishPointMap(GenericDelegator delegator, String pubPtId, Map publishPointMap ) throws GeneralException {

        List publishPointList = getAllPublishPoints( delegator, pubPtId );
        Iterator it = publishPointList.iterator();
        while (it.hasNext()) {
           GenericValue webSitePublishPoint = (GenericValue)it.next();
           String pub = (String)webSitePublishPoint.get("contentId");
           publishPointMap.put(pub, webSitePublishPoint);
           getAllPublishPointMap(delegator, pub, publishPointMap);
        }
        return;
    }

    public static Map getPublishPointMap(GenericDelegator delegator, List publishPointList ) {

        Map publishPointMap = new HashMap();
        Iterator it = publishPointList.iterator();
        while (it.hasNext()) {
           GenericValue webSitePublishPoint = (GenericValue)it.next();
           String pub = (String)webSitePublishPoint.get("contentId");
           publishPointMap.put(pub, webSitePublishPoint);
        }
        return publishPointMap;
    }

    public static List getStaticValues(GenericDelegator delegator,  String parentPlaceholderId, List permittedPublishPointList) throws GeneralException {

        List assocValueList = null;
        try {
            assocValueList = delegator.findByAndCache("Content", UtilMisc.toMap("contentTypeId", parentPlaceholderId));
        } catch(GenericEntityException e) {
            throw new GeneralException(e.getMessage());
        }

        List staticValueList = new ArrayList();
        Iterator it = assocValueList.iterator();
        int counter = 0;
        while(it.hasNext()) {
            GenericValue content = (GenericValue)it.next();
            String contentId = (String)content.get("contentId");
            String contentName = (String)content.get("contentName");
            String description = (String)content.get("description");
            Map map = new HashMap();
            map.put("contentId", contentId);
            map.put("contentName", contentName);
            map.put("description", description);
            Iterator it2 = permittedPublishPointList.iterator();
            while (it2.hasNext()) {
                String [] publishPointArray = (String [])it2.next();
                String publishPointId = (String)publishPointArray[0];
                //fieldName = "_" + Integer.toString(counter) + "_" + publishPointId;
                String fieldName = publishPointId;
                List contentAssocList = content.getRelatedByAnd("ToContentAssoc", UtilMisc.toMap("contentId", publishPointId));
                List filteredList = EntityUtil.filterByDate(contentAssocList);
                if (filteredList.size() > 0) {
                    map.put(fieldName, "Y");
                    GenericValue assoc = (GenericValue)filteredList.get(0);
                    Timestamp fromDate = (Timestamp)assoc.get("fromDate");
                    map.put(fieldName + "FromDate", fromDate);
                } else {
                    map.put(fieldName, "N");
                }
            }
            staticValueList.add(map);
            counter++;
        }
        return staticValueList;
    }

    public static GenericValue getWebSitePublishPoint(GenericDelegator delegator, String contentId) throws GenericEntityException {
           return getWebSitePublishPoint(delegator, contentId, false);
    }

    public static GenericValue getWebSitePublishPoint(GenericDelegator delegator, String contentId, boolean ignoreCache) throws GenericEntityException {
        GenericValue webSitePublishPoint = null;
        if (!ignoreCache)
            webSitePublishPoint = (GenericValue)cachedWebSitePublishPoints.get(contentId);

        if (webSitePublishPoint == null) {
            webSitePublishPoint = delegator.findByPrimaryKey("WebSitePublishPoint", UtilMisc.toMap("contentId", contentId));
            // If no webSitePublishPoint exists, still try to look for parent by making a dummy value
            if (webSitePublishPoint == null) {
                webSitePublishPoint = delegator.makeValue("WebSitePublishPoint", UtilMisc.toMap("contentId", contentId));
            }
            //if (Debug.infoOn()) Debug.logInfo("in getWebSitePublishPoint, contentId:" + contentId, module);
            webSitePublishPoint = overrideWebSitePublishPoint(delegator, webSitePublishPoint);
            cachedWebSitePublishPoints.put(contentId, webSitePublishPoint);
        }
        return webSitePublishPoint;
    }

    public static GenericValue overrideWebSitePublishPoint(GenericDelegator delegator, GenericValue passedValue) throws GenericEntityException {
        String contentId = passedValue.getString("contentId");
        GenericValue webSitePublishPoint = passedValue;
        String contentIdTo = getParentWebSitePublishPointId(delegator, contentId);
            //if (Debug.infoOn()) Debug.logInfo("in overrideWebSitePublishPoint, contentIdTo:" + contentIdTo, module);
        if (contentIdTo != null) {
            //webSitePublishPoint = getWebSitePublishPoint(delegator, contentIdTo, false);
            webSitePublishPoint = delegator.findByPrimaryKeyCache("WebSitePublishPoint", UtilMisc.toMap("contentId", contentIdTo));
            if (webSitePublishPoint != null) {
                webSitePublishPoint = new GenericValue(webSitePublishPoint);
                webSitePublishPoint = overrideWebSitePublishPoint(delegator, webSitePublishPoint);
                webSitePublishPoint.setNonPKFields(passedValue, false);
                webSitePublishPoint.setPKFields(passedValue, false);
                passedValue.setNonPKFields(webSitePublishPoint);
            }
        }
        return webSitePublishPoint;
    }

    public static GenericValue getParentWebSitePublishPointValue(GenericDelegator delegator, String  contentId) throws GenericEntityException {

        String contentIdTo = getParentWebSitePublishPointId(delegator, contentId);
        GenericValue content = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", contentIdTo));
        return content;
    }

    public static String getParentWebSitePublishPointId(GenericDelegator delegator, String  contentId) throws GenericEntityException {

        
        String contentIdTo = null;
        List contentAssocList = delegator.findByAndCache("ContentAssoc", UtilMisc.toMap("contentId", contentId, "contentAssocTypeId", "SUBSITE"));
        List filteredContentAssocList = EntityUtil.filterByDate(contentAssocList);
        if (filteredContentAssocList.size() > 0) {
            GenericValue contentAssoc = (GenericValue)filteredContentAssocList.get(0); 
            if (contentAssoc != null)
                contentIdTo = contentAssoc.getString("contentIdTo");
        }
        return contentIdTo;
    }

    public static GenericValue getStaticValue(GenericDelegator delegator, String parentPlaceholderId, String webSitePublishPointId, boolean ignoreCache) throws GenericEntityException {
        GenericValue webSitePublishPoint = null;
        GenericValue staticValue = null;
        if (!ignoreCache) {
            Map subStaticValueMap =  (GenericValue)cachedStaticValues.get(parentPlaceholderId);
            if (subStaticValueMap == null) {
                subStaticValueMap = new HashMap();
                cachedStaticValues.put(parentPlaceholderId, subStaticValueMap);
            }
            //Map staticValueMap = (GenericValue)cachedStaticValues.get(web);
        }

/*
        if (webSitePublishPoint == null) {
            webSitePublishPoint = delegator.findByPrimaryKey("WebSitePublishPoint", UtilMisc.toMap("contentId", contentId));
            // If no webSitePublishPoint exists, still try to look for parent by making a dummy value
            if (webSitePublishPoint == null) {
                webSitePublishPoint = delegator.makeValue("WebSitePublishPoint", UtilMisc.toMap("contentId", contentId));
            }
            webSitePublishPoint = overrideStaticValues(delegator, webSitePublishPoint);
            cachedWebSitePublishPoints.put(contentId, webSitePublishPoint);
        }
*/
        return webSitePublishPoint;
    }


    public static List getPublishedLinks(GenericDelegator delegator,  String targContentId, String rootPubId, GenericValue userLogin, Security security, String permittedAction, String permittedOperations , String passedRoles) throws GeneralException {

        // Set up one map with all the top-level publish points (to which only one sub point can be attached to)
        // and another map (publishPointMapAll) that points to one of the top-level points.
        List allPublishPointList = getAllPublishPoints( delegator, rootPubId );
        List publishPointList = getPermittedPublishPoints( delegator, allPublishPointList, userLogin, security , permittedAction, permittedOperations, passedRoles );
        Map publishPointMap = new HashMap();
        Map publishPointMapAll = new HashMap();
        Iterator it = publishPointList.iterator();
        while (it.hasNext()) {
            //GenericValue webSitePublishPoint = (GenericValue)it.next();
            //String contentId = (String)webSitePublishPoint.get("contentId");
            //String description = (String)webSitePublishPoint.get("description");
            String [] arr = (String [])it.next();
            String contentId = (String)arr[0];
            String description = (String)arr[1];
            List subPointList = new ArrayList();
            Object nullObj = null;
            Object [] subArr = {contentId, subPointList, description, nullObj};
            publishPointMap.put(contentId, subArr);
            publishPointMapAll.put(contentId, contentId);
            List subPublishPointList = getAllPublishPoints( delegator, contentId );
            Iterator it2 = subPublishPointList.iterator();
            while (it2.hasNext()) {
                //String [] arr2 = (String [])it2.next();
                //String contentId2 = (String)arr2[0];
                //String description2 = (String)arr2[1];
                GenericValue webSitePublishPoint2 = (GenericValue)it2.next();
                String contentId2 = (String)webSitePublishPoint2.get("contentId");
                String description2 = (String)webSitePublishPoint2.get("templateTitle");
                publishPointMapAll.put(contentId2, contentId);
                Timestamp obj = null;
                Object [] subArr2 = {contentId2, description2, obj};
                subPointList.add(subArr2);
            }
        }
/* */
        List assocValueList = null;
        try {
            List rawAssocValueList = delegator.findByAndCache("ContentAssoc", UtilMisc.toMap("contentId", targContentId, "contentAssocTypeId", "SUB_CONTENT"));
            assocValueList = EntityUtil.filterByDate(rawAssocValueList);
        } catch(GenericEntityException e) {
            throw new GeneralException(e.getMessage());
        }
        Map publishedLinkMap = new HashMap();
        Iterator it4 = assocValueList.iterator();
        while (it4.hasNext()) {
            GenericValue contentAssoc = (GenericValue)it4.next();
            String contentIdTo = contentAssoc.getString("contentIdTo");
            String topContentId = (String)publishPointMapAll.get(contentIdTo);
            Object [] subArr = (Object [])publishPointMap.get(topContentId);
                //if (Debug.infoOn()) Debug.logInfo("in getPublishLinks, subArr:" + Arrays.asList(subArr) , module);
            if (contentIdTo.equals(topContentId)) {
                subArr[3] =  contentAssoc.get("fromDate");
            } else {
                List subPointList = (List)subArr[1];
                Iterator it5 = subPointList.iterator();
                Object [] subArr2 = null;
                while (it5.hasNext()) {
                    subArr2 = (Object [])it5.next();
                    String contentId5 = (String)subArr2[0];
                    if (contentId5.equals(contentIdTo))
                        break;
                }
                subArr2[2] =  contentAssoc.get("fromDate");
            }
        }

        List publishedLinkList = new ArrayList();
        Set keySet = publishPointMap.keySet();
        Iterator it3 = keySet.iterator();
        while (it3.hasNext()) {
            String contentId = (String)it3.next();
            Object [] subPointArr = (Object [])publishPointMap.get(contentId);
            publishedLinkList.add(subPointArr);
        }
        return publishedLinkList;
    }

    public static GenericValue getAuthorContent(GenericDelegator delegator, String contentId) {
 
        GenericValue authorContent = null;
        try {
            List assocTypes = UtilMisc.toList("AUTHOR");
            List contentTypes = null;
            String fromDate = null;
            String thruDate = null;
            Map results =  ContentServicesComplex.getAssocAndContentAndDataResourceCacheMethod(delegator, contentId, null, "To", null, null, assocTypes, contentTypes, new Boolean(true), null);
            List valueList = (List)results.get("entityList");
            if (valueList.size() > 0) {
                GenericValue value = (GenericValue)valueList.get(0);
                authorContent = delegator.makeValue("Content", null);
                authorContent.setPKFields(value);
                authorContent.setNonPKFields(value);
            //if (Debug.infoOn()) Debug.logInfo("in getAuthorContent, authorContent:" + authorContent, module);
            }
        } catch(GenericEntityException e) {
        } catch(MiniLangException e2) {
        }

        return authorContent;
    }

    public static List getPermittedDepartmentPoints(GenericDelegator delegator, List allDepartmentPoints, GenericValue userLogin, Security security, String permittedAction, String permittedOperations, String passedRoles) throws GeneralException {

        List permittedDepartmentPointList = new ArrayList();
        
        // Check that user has permission to admin sites
        Iterator it = allDepartmentPoints.iterator();
        while(it.hasNext()) {
            GenericValue content = (GenericValue)it.next();
            String contentId = (String)content.get("contentId");
            String contentName = (String)content.get("contentName");
            String statusId = null;
            String entityAction = permittedAction;
            if (entityAction == null)
                entityAction = "_ADMIN";
            List passedPurposes = UtilMisc.toList("ARTICLE");
            List roles = StringUtil.split(passedRoles, "|");
            List targetOperationList = new ArrayList();
            if (UtilValidate.isEmpty(permittedOperations)) {
                 targetOperationList.add("CONTENT" + entityAction);
            } else {
                 targetOperationList = StringUtil.split(permittedOperations, "|");
            }
            Map results = null;
            //if (Debug.infoOn()) Debug.logInfo("in getPermittedDepartmentPoints, content:" + content, module);
            results = ContentPermissionServices.checkPermission(content, statusId, userLogin, passedPurposes, targetOperationList, roles, delegator, security, entityAction);
            String permissionStatus = (String)results.get("permissionStatus");
            if (permissionStatus != null && permissionStatus.equalsIgnoreCase("granted")) {
                String [] arr = {contentId,contentName};
                permittedDepartmentPointList.add(arr);
            }
        }
        return permittedDepartmentPointList;
    }

    public static List getAllDepartmentContent(GenericDelegator delegator, String rootPubPt) throws GeneralException {

        GenericValue rootContent = null;
        List relatedPubPts = null;
        try {
            rootContent = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", rootPubPt));
            //relatedPubPts = delegator.findByAndCache("ContentAssoc", UtilMisc.toMap("contentIdTo", rootPubPt));
            relatedPubPts = delegator.findByAndCache("ContentAssoc", UtilMisc.toMap("contentIdTo", rootPubPt, "contentAssocTypeId", "DEPARTMENT"));

        } catch(GenericEntityException e) {
            throw new GeneralException(e.getMessage());
        }
        List allDepartmentPoints = new ArrayList();
        GenericValue departmentContent = null;
        GenericValue contentAssoc = null;
        Iterator it = relatedPubPts.iterator();
        while (it.hasNext()) {
           contentAssoc = (GenericValue)it.next();
           String pub = (String)contentAssoc.get("contentId");
           departmentContent = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", pub));
           allDepartmentPoints.add(departmentContent);
        }
        return allDepartmentPoints;
    }

}
