/*
 * $Id: DataResourceWorker.java,v 1.3 2003/11/05 00:05:53 byersa Exp $
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
package org.ofbiz.content.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.BshUtil;
import org.ofbiz.base.util.FlexibleStringExpander;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import java.sql.Timestamp;


import bsh.EvalError;
import org.apache.commons.fileupload.*;
import javax.servlet.http.HttpServletRequest;


/**
 * DataResourceWorker Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.3 $
 * @since      3.0
 *
 * 
 */
public class DataResourceWorker {

    public static final String module = DataResourceWorker.class.getName();

    /**
     * Traverses the DataCategory parent/child structure and puts it in categoryNode.
     * Returns non-null error string if there is an error.
     */
    public static String getDataCategoryMap(GenericDelegator delegator, int depth,
                  Map categoryNode, List categoryTypeIds, boolean getAll) 
        throws GenericEntityException {

        String errorMsg = null;
        String dataCategoryId = (String)categoryNode.get("id");
        String currentDataCategoryId = null;
        int sz = categoryTypeIds.size();
        if (depth >= 0 && (sz - depth) > 0) {
            currentDataCategoryId = (String)categoryTypeIds.get(sz - depth - 1);
        }

        EntityExpr expr = null;
        String matchValue = null;
        if (dataCategoryId != null  ) {
            //expr = new EntityExpr("parentCategoryId", EntityOperator.EQUALS, dataCategoryId);
            matchValue = dataCategoryId;
        } else {
            //expr = new EntityExpr("parentCategoryId", EntityOperator.EQUALS, null);
            matchValue = null;
        }
        //List categoryValues = delegator.findByConditionCache("DataCategory", expr, null, null );
        List categoryValues = delegator.findByAndCache("DataCategory", 
                                                  UtilMisc.toMap("parentCategoryId", matchValue));
        categoryNode.put("count", new Integer(categoryValues.size()));

        List subCategoryIds = new ArrayList();
        for (int i=0; i < categoryValues.size(); i++) {
                GenericValue category = (GenericValue)categoryValues.get(i);
                String id = (String)category.get("dataCategoryId");
                String categoryName = (String)category.get("categoryName");
                Map newNode = new HashMap();
                newNode.put("id", id);
                newNode.put("name", categoryName);
                errorMsg = getDataCategoryMap(delegator, depth + 1, newNode, categoryTypeIds, getAll);
                if (errorMsg != null) break;
                subCategoryIds.add(newNode);
        }
        if (dataCategoryId == null 
            || dataCategoryId.equals("ROOT") 
            || (currentDataCategoryId != null &&currentDataCategoryId.equals(dataCategoryId) ) 
            || getAll ) {
            categoryNode.put("kids", subCategoryIds);
        }
        return errorMsg;
    }

    /**
     * Finds the parents of DataCategory entity and puts them in a list,
     * the start entity at the top.
     */
    public static void getDataCategoryAncestry(GenericDelegator delegator, String dataCategoryId,
                                               List categoryTypeIds) 
        throws GenericEntityException {

        categoryTypeIds.add(dataCategoryId);
        GenericValue dataCategoryValue = delegator.findByPrimaryKey("DataCategory",
                      UtilMisc.toMap("dataCategoryId", dataCategoryId));
        if (dataCategoryValue == null) return;
        String parentCategoryId = (String)dataCategoryValue.get("parentCategoryId");
        if (parentCategoryId != null) {
            getDataCategoryAncestry(delegator, parentCategoryId, categoryTypeIds);
        }
        return;
    }


    /**
     * Takes a DataCategory structure and builds a list of maps,
     * one value (id) is the dataCategoryId value and the other
     * is an indented string suitable for use in a drop-down pick list.
     */
    public static void buildList(HashMap nd, List lst, int depth) {
        String id = (String)nd.get("id");
        String nm = (String)nd.get("name");
        String spc = "";
        for (int i=0; i < depth; i++) spc += "&nbsp;&nbsp;";
        HashMap map = new HashMap();
        map.put("dataCategoryId", id);
        map.put("categoryName", spc + nm);
        if (id != null && !id.equals("ROOT") && !id.equals("")) {
            lst.add(map);
        }
        List kids = (List)nd.get("kids");
        int sz = kids.size();
        for (int i=0; i <sz; i++) {
            HashMap kidNode = (HashMap)kids.get(i);
            buildList(kidNode, lst, depth + 1);
        }
    }

    /**
     * Uploads image data from a form and stores it in ImageDataResource. 
     * Expects key data in a field identitified by the "idField" value
     * and the binary data to be in a field id'd by uploadField.
     */
    public static String uploadAndStoreImage(HttpServletRequest request, 
                         String idField, String uploadField) {

        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

Debug.logInfo("in uploadAndStoreImage, idField:" + idField, "");
        String idFieldValue = null;
        DiskFileUpload fu = new DiskFileUpload();
        java.util.List lst = null;
        try {
           lst = fu.parseRequest(request);
        } catch (FileUploadException e4) {
            request.setAttribute("_ERROR_MESSAGE_", e4.getMessage());
            return "error";
        }

        if (lst.size() == 0) {
           request.setAttribute("_ERROR_MESSAGE_", "No files uploaded");
            Debug.logWarning("[DataEvents.uploadImage] No files uploaded", module);
            return "error";
        }


        // This code finds the idField and the upload FileItems 
        FileItem fi = null;
        FileItem imageFi = null;
        for (int i=0; i < lst.size(); i++) {
            fi = (FileItem)lst.get(i);
	    String fn = fi.getName();
	    String fieldName = fi.getFieldName();
	    String fieldStr = fi.getString();
Debug.logInfo("in uploadAndStoreImage, fieldName:" + fieldName, "");
            if (fieldName.equals(idField)) idFieldValue = fieldStr;
            if (fieldName.equals(uploadField)) imageFi = fi;
        }
        if (imageFi == null || idFieldValue == null) {
           request.setAttribute("_ERROR_MESSAGE_", "imageFi(" + imageFi 
                                      + " or idFieldValue(" + idFieldValue + " is null");
            Debug.logWarning("[DataEvents.uploadImage] imageFi(" + imageFi 
                                      + " or idFieldValue(" + idFieldValue + " is null", module);
            return "error";
        }

Debug.logInfo("in uploadAndStoreImage, idFieldValue:" + idFieldValue, "");

           
        byte[] imageBytes = imageFi.get();

        try {
            GenericValue dataResource = delegator.findByPrimaryKey("DataResource",
                          UtilMisc.toMap("dataResourceId", idFieldValue));
            // Use objectInfo field to store the name of the file, since there is no
            // place in ImageDataResource for it.
            if (dataResource != null) {
                dataResource.set("objectInfo", imageFi.getName());
                dataResource.store();
            }

            // See if this needs to be a create or an update procedure
            GenericValue imageDataResource = delegator.findByPrimaryKey("ImageDataResource",
                          UtilMisc.toMap("dataResourceId", idFieldValue));
            if (imageDataResource == null) {
                imageDataResource = delegator.makeValue("ImageDataResource",
                          UtilMisc.toMap("dataResourceId", idFieldValue));
                imageDataResource.set("imageData", imageBytes);
                imageDataResource.create();
            } else {
                imageDataResource.set("imageData", imageBytes);
                imageDataResource.store();
            }
        } catch (GenericEntityException e3) {
            request.setAttribute("_ERROR_MESSAGE_", e3.getMessage());
            return "error";
        }

        request.setAttribute("dataResourceId", idFieldValue);
        request.setAttribute(idField, idFieldValue);
        return "success";
    }

    /**
     * callDataResourcePermissionCheck
     * Formats data for a call to the checkContentPermission service.
     */
    public static String callDataResourcePermissionCheck(GenericDelegator delegator,
                          LocalDispatcher dispatcher, Map context) {

        String permissionStatus = "granted";
        String skipPermissionCheck = (String)context.get("skipPermissionCheck");

        if (skipPermissionCheck == null 
            || skipPermissionCheck.length() == 0
            || ( !skipPermissionCheck.equalsIgnoreCase("true")  
                && !skipPermissionCheck.equalsIgnoreCase("granted") ) ) {
            GenericValue userLogin = (GenericValue) context.get("userLogin"); 
            Map serviceInMap = new HashMap();
            serviceInMap.put("userLogin", userLogin); 
            serviceInMap.put("targetOperationList", context.get("targetOperationList")); 
            serviceInMap.put("contentPurposeList", context.get("contentPurposeList")); 
            serviceInMap.put("entityOperation", context.get("entityOperation")); 

            // It is possible that permission to work with DataResources will be controlled
            // by an external Content entity.
            String ownerContentId = (String)context.get("ownerContentId");
            if (ownerContentId != null && ownerContentId.length() > 0 ) {
                try {
                    GenericValue content = delegator.findByPrimaryKey("Content", 
                              UtilMisc.toMap("contentId", ownerContentId));
                    if (content != null) serviceInMap.put("currentContent", content);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "e.getMessage()", "ContentServices");
                }
            }
            try {
                Map permResults = dispatcher.runSync("checkContentPermission", serviceInMap);
                permissionStatus = (String)permResults.get("permissionStatus");
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem checking permissions", "ContentServices");
            }
        }
        return permissionStatus;
    }

    /**
     * Gets image data from ImageDataResource and returns it as a byte array.
     */
    public static byte[] acquireImage(HttpServletRequest request, String imgFieldName) {

        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String dataResourceId = request.getParameter(imgFieldName);
        GenericValue dataResource = null;
        GenericValue imageDataResource = null;
        try {
            imageDataResource = delegator.findByPrimaryKey("ImageDataResource",
                             UtilMisc.toMap("dataResourceId", dataResourceId)); 
        } catch (GenericEntityException e) {
            String errorMsg = "Error getting image record from db: " + e.toString();
            Debug.logError(e, errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return null;
        }

        byte[] b = (byte[])imageDataResource.get("imageData");
        if (b == null || b.length == 0) {
            request.setAttribute("_ERROR_MESSAGE_", "There was no image data available.");
            return null;
        }

        return b;

    }

    /**
     * Returns the image type.
     */
    public static String getImageType(HttpServletRequest request, String imgFieldName) {

        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String dataResourceId = request.getParameter(imgFieldName);
        GenericValue dataResource = null;
        GenericValue imageDataResource = null;
        try {
            dataResource = delegator.findByPrimaryKey("DataResource",
                             UtilMisc.toMap("dataResourceId", dataResourceId)); 
        } catch (GenericEntityException e) {
            String errorMsg = "Error getting image record from db: " + e.toString();
            Debug.logError(e, errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }


        String imageType = (String)dataResource.get("mimeTypeId");
        return imageType;
    }

}
