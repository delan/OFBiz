/*
 * $Id: ContentServicesComplex.java,v 1.5 2003/12/15 11:55:58 byersa Exp $
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.content.content;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

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
import org.ofbiz.content.data.DataServices;
import org.ofbiz.content.content.ContentWorker;


/**
 * ContentServicesComplex Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.5 $
 * @since      2.2
 *
 * 
 */
public class ContentServicesComplex {

    public static final String module = ContentServicesComplex.class.getName();


   /*
    * A service that returns a list of ContentAssocDataResourceViewFrom/To views that are
    * associated with the passed in contentId. Other conditions are also applied, including:
    * a list of contentAssocTypeIds or contentTypeIds that the result set views must match.
    * A direction (From or To - case insensitive).
    * From and thru dates or date strings.
    * A mapKey value.
    */
    public static Map getAssocAndContentAndDataResource(DispatchContext dctx, Map context) {

        GenericDelegator delegator = dctx.getDelegator();
        List assocTypes = (List)context.get("assocTypes");
        List contentTypes = (List)context.get("contentTypes");
        Timestamp fromDate = (Timestamp)context.get("fromDate");
        Timestamp thruDate = (Timestamp)context.get("thruDate");
        String fromDateStr = (String)context.get("fromDateStr");
        String thruDateStr = (String)context.get("thruDateStr");
        String contentId = (String)context.get("contentId");
        String direction = (String)context.get("direction");
        String mapKey = (String)context.get("mapKey");
        //Debug.logInfo("in getAACADR, contentId:" +  contentId, null);
        //Debug.logInfo("in getAACADR, mapKey:" +  mapKey, null);
        //Debug.logInfo("in getAACADR, direction:" +  direction, null);
        //Debug.logInfo("in getAACADR, fromDateStr:" +  fromDateStr, null);
        Map results = getAssocAndContentAndDataResourceMethod(delegator,
                          contentId, mapKey, direction, fromDate, thruDate,
                          fromDateStr, thruDateStr, assocTypes, contentTypes);
        return results;
    }

    public static Map getAssocAndContentAndDataResourceMethod(GenericDelegator delegator,
                          String contentId, String mapKey, String direction, 
                          Timestamp fromDate, Timestamp thruDate, String fromDateStr, String thruDateStr, 
                          List assocTypes, List contentTypes) {

        List exprList = new ArrayList();
        EntityExpr joinExpr = null;
        EntityExpr expr = null;
        String viewName = null;
        if (mapKey != null ) {
            EntityExpr mapKeyExpr = new EntityExpr("caMapKey", EntityOperator.EQUALS, mapKey);
            //Debug.logInfo("in getAACADR, mapKeyExpr:" +  mapKeyExpr, null);
            exprList.add(mapKeyExpr);
        }
        if (direction != null && direction.equalsIgnoreCase("From") ) {
            joinExpr = new EntityExpr("caContentIdTo", EntityOperator.EQUALS, contentId);
            viewName = "ContentAssocDataResourceViewFrom";
        } else {
            joinExpr = new EntityExpr("caContentId", EntityOperator.EQUALS, contentId);
            viewName = "ContentAssocDataResourceViewTo";
        }
            //Debug.logInfo("in getAACADR, joinExpr:" +  joinExpr, null);
        exprList.add(joinExpr);
        if (assocTypes != null && assocTypes.size() > 0) {
            List exprListOr = new ArrayList();
            Iterator it = assocTypes.iterator();
            while (it.hasNext()) {
                String assocType = (String)it.next();
                expr = new EntityExpr("caContentAssocTypeId", 
                                  EntityOperator.EQUALS, assocType);
            //Debug.logInfo("in getAACADR, assoc expr	:" +  expr, null);
                exprListOr.add(expr);
            }
            EntityConditionList assocExprList = new EntityConditionList(exprListOr, EntityOperator.OR);

            exprList.add(assocExprList);
        }
        if (contentTypes != null && contentTypes.size() > 0) {
            List exprListOr = new ArrayList();
            Iterator it = contentTypes.iterator();
            while (it.hasNext()) {
                String contentType = (String)it.next();
                expr = new EntityExpr("contentTypeId", 
                                  EntityOperator.EQUALS, contentType);
                exprListOr.add(expr);
            }
            EntityConditionList contentExprList = new EntityConditionList(exprListOr, EntityOperator.OR);
            exprList.add(contentExprList);
        }

        if (fromDate == null && fromDateStr != null ) {
            fromDate = UtilDateTime.toTimestamp( fromDateStr );
	}
        if (thruDate == null && thruDateStr != null ) {
            thruDate = UtilDateTime.toTimestamp( thruDateStr );
	}

        if (fromDate != null) {
            EntityExpr fromExpr = new EntityExpr("caFromDate", EntityOperator.LESS_THAN, fromDate);
            //Debug.logInfo("in getAACADR, fromExpr:" +  fromExpr, null);
            exprList.add(fromExpr);
        }
        if (thruDate != null) {
            List thruList = new ArrayList();
            //thruDate = UtilDateTime.getDayStart(thruDate, daysLater);

            EntityExpr thruExpr = new EntityExpr("caThruDate", EntityOperator.LESS_THAN, thruDate);
            thruList.add(thruExpr);
            EntityExpr thruExpr2 = new EntityExpr("caThruDate", EntityOperator.EQUALS, null);
            thruList.add(thruExpr2);
            EntityConditionList thruExprList = new EntityConditionList(thruList, EntityOperator.OR);
            exprList.add(thruExprList);
        } else if (fromDate != null) {
            List thruList = new ArrayList();

            EntityExpr thruExpr = new EntityExpr("caThruDate", EntityOperator.GREATER_THAN, fromDate);
                    //Debug.logInfo("in getAACADR, thruExpr:" +  thruExpr, null);
            thruList.add(thruExpr);
            EntityExpr thruExpr2 = new EntityExpr("caThruDate", EntityOperator.EQUALS, null);
                    //Debug.logInfo("in getAACADR, thruExpr2:" +  thruExpr2, null);
            thruList.add(thruExpr2);
            EntityConditionList thruExprList = new EntityConditionList(thruList, EntityOperator.OR);
            exprList.add(thruExprList);
        }
        EntityConditionList assocExprList = new EntityConditionList(exprList, EntityOperator.AND);
        List relatedAssocs = null;
            //Debug.logInfo("in getAACADR, viewName:" +  viewName, null);
        try {
            //relatedAssocs = delegator.findByCondition(viewName, joinExpr, 
            relatedAssocs = delegator.findByCondition(viewName, assocExprList, 
                                  new ArrayList(),UtilMisc.toList("caFromDate"));
        } catch(GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
                //Debug.logInfo("relatedAssocs size:" + relatedAssocs.size(), null);
        for (int i=0; i < relatedAssocs.size(); i++) {
            GenericValue a = (GenericValue)relatedAssocs.get(i);
                Debug.logInfo(" contentId:" + a.get("contentId")
                         + " To:" + a.get("caContentIdTo")
                         + " fromDate:" + a.get("caFromDate")
                         + " thruDate:" + a.get("caThruDate")
                         + " AssocTypeId:" + a.get("caContentAssocTypeId")
                         ,null);

        }
        HashMap results = new HashMap();
        results.put("entityList", relatedAssocs);
        return results;
    }

}
