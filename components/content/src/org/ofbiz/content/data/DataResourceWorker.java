/*
 * $Id: DataResourceWorker.java,v 1.32 2004/07/02 15:48:25 byersa Exp $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.content.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.content.email.NotificationServices;
import org.ofbiz.content.webapp.ftl.FreeMarkerWorker;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import freemarker.template.Template;
import freemarker.template.TemplateException;
//import com.clarkware.profiler.Profiler;

/**
 * DataResourceWorker Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version $Revision: 1.32 $
 * @since 3.0
 */
public class DataResourceWorker {

    public static final String module = DataResourceWorker.class.getName();

    /**
     * Traverses the DataCategory parent/child structure and put it in categoryNode. Returns non-null error string if there is an error.
     * @param depth The place on the categoryTypesIds to start collecting.
     * @param getAll Indicates that all descendants are to be gotten. Used as "true" to populate an 
     *     indented select list.
     */
    public static String getDataCategoryMap(GenericDelegator delegator, int depth, Map categoryNode, List categoryTypeIds, boolean getAll) throws GenericEntityException {
        String errorMsg = null;
        String parentCategoryId = (String) categoryNode.get("id");
        String currentDataCategoryId = null;
        int sz = categoryTypeIds.size();
    
        // The categoryTypeIds has the most senior types at the end, so it is necessary to 
        // work backwards. As "depth" is incremented, that is the effect.
        // The convention for the topmost type is "ROOT".
        if (depth >= 0 && (sz - depth) > 0) {
            currentDataCategoryId = (String) categoryTypeIds.get(sz - depth - 1);
        }

        // Find all the categoryTypes that are children of the categoryNode.
        String matchValue = null;
        if (parentCategoryId != null) {
            matchValue = parentCategoryId;
        } else {
            matchValue = null;
        }
        List categoryValues = delegator.findByAndCache("DataCategory", UtilMisc.toMap("parentCategoryId", matchValue));
        categoryNode.put("count", new Integer(categoryValues.size()));
        List subCategoryIds = new ArrayList();
        for (int i = 0; i < categoryValues.size(); i++) {
            GenericValue category = (GenericValue) categoryValues.get(i);
            String id = (String) category.get("dataCategoryId");
            String categoryName = (String) category.get("categoryName");
            Map newNode = new HashMap();
            newNode.put("id", id);
            newNode.put("name", categoryName);
            errorMsg = getDataCategoryMap(delegator, depth + 1, newNode, categoryTypeIds, getAll);
            if (errorMsg != null)
                break;
            subCategoryIds.add(newNode);
        }

        // The first two parentCategoryId test just make sure that the first level of children
        // is gotten. This is a hack to make them available for display, but a more correct
        // approach should be formulated.
        // The "getAll" switch makes sure all descendants make it into the tree, if true.
        // The other test is to only get all the children if the "leaf" node where all the
        // children of the leaf are wanted for expansion.
        if (parentCategoryId == null
            || parentCategoryId.equals("ROOT")
            || (currentDataCategoryId != null && currentDataCategoryId.equals(parentCategoryId))
            || getAll) {
            categoryNode.put("kids", subCategoryIds);
        }
        return errorMsg;
    }

    /**
     * Finds the parents of DataCategory entity and puts them in a list, the start entity at the top.
     */
    public static void getDataCategoryAncestry(GenericDelegator delegator, String dataCategoryId, List categoryTypeIds) throws GenericEntityException {
        categoryTypeIds.add(dataCategoryId);
        GenericValue dataCategoryValue = delegator.findByPrimaryKey("DataCategory", UtilMisc.toMap("dataCategoryId", dataCategoryId));
        if (dataCategoryValue == null)
            return;
        String parentCategoryId = (String) dataCategoryValue.get("parentCategoryId");
        if (parentCategoryId != null) {
            getDataCategoryAncestry(delegator, parentCategoryId, categoryTypeIds);
        }
        return;
    }

    /**
     * Takes a DataCategory structure and builds a list of maps, one value (id) is the dataCategoryId value and the other is an indented string suitable for
     * use in a drop-down pick list.
     */
    public static void buildList(HashMap nd, List lst, int depth) {
        String id = (String) nd.get("id");
        String nm = (String) nd.get("name");
        String spc = "";
        for (int i = 0; i < depth; i++)
            spc += "&nbsp;&nbsp;";
        HashMap map = new HashMap();
        map.put("dataCategoryId", id);
        map.put("categoryName", spc + nm);
        if (id != null && !id.equals("ROOT") && !id.equals("")) {
            lst.add(map);
        }
        List kids = (List) nd.get("kids");
        int sz = kids.size();
        for (int i = 0; i < sz; i++) {
            HashMap kidNode = (HashMap) kids.get(i);
            buildList(kidNode, lst, depth + 1);
        }
    }

    /**
     * Uploads image data from a form and stores it in ImageDataResource. Expects key data in a field identitified by the "idField" value and the binary data
     * to be in a field id'd by uploadField.
     */
    public static String uploadAndStoreImage(HttpServletRequest request, String idField, String uploadField) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

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
        for (int i = 0; i < lst.size(); i++) {
            fi = (FileItem) lst.get(i);
            //String fn = fi.getName();
            String fieldName = fi.getFieldName();
            String fieldStr = fi.getString();
            if (fieldName.equals(idField)) {
                idFieldValue = fieldStr;
            }
            if (fieldName.equals(uploadField)) {
                imageFi = fi;
            }
        }
        if (imageFi == null || idFieldValue == null) {
            request.setAttribute("_ERROR_MESSAGE_", "imageFi(" + imageFi + " or idFieldValue(" + idFieldValue + " is null");
            Debug.logWarning("[DataEvents.uploadImage] imageFi(" + imageFi + " or idFieldValue(" + idFieldValue + " is null", module);
            return "error";
        }


        byte[] imageBytes = imageFi.get();

        try {
            GenericValue dataResource = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", idFieldValue));
            // Use objectInfo field to store the name of the file, since there is no
            // place in ImageDataResource for it.
            if (dataResource != null) {
                dataResource.set("objectInfo", imageFi.getName());
                dataResource.store();
            }

            // See if this needs to be a create or an update procedure
            GenericValue imageDataResource = delegator.findByPrimaryKey("ImageDataResource", UtilMisc.toMap("dataResourceId", idFieldValue));
            if (imageDataResource == null) {
                imageDataResource = delegator.makeValue("ImageDataResource", UtilMisc.toMap("dataResourceId", idFieldValue));
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
     * callDataResourcePermissionCheck Formats data for a call to the checkContentPermission service.
     */
    public static String callDataResourcePermissionCheck(GenericDelegator delegator, LocalDispatcher dispatcher, Map context) {
        Map permResults = callDataResourcePermissionCheckResult(delegator, dispatcher, context);
        String permissionStatus = (String) permResults.get("permissionStatus");
        return permissionStatus;
    }

    /**
     * callDataResourcePermissionCheck Formats data for a call to the checkContentPermission service.
     */
    public static Map callDataResourcePermissionCheckResult(GenericDelegator delegator, LocalDispatcher dispatcher, Map context) {

        Map permResults = new HashMap();
        String skipPermissionCheck = (String) context.get("skipPermissionCheck");
            if (Debug.infoOn()) Debug.logInfo("in callDataResourcePermissionCheckResult, skipPermissionCheck:" + skipPermissionCheck,"");

        if (skipPermissionCheck == null
            || skipPermissionCheck.length() == 0
            || (!skipPermissionCheck.equalsIgnoreCase("true") && !skipPermissionCheck.equalsIgnoreCase("granted"))) {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            Map serviceInMap = new HashMap();
            serviceInMap.put("userLogin", userLogin);
            serviceInMap.put("targetOperationList", context.get("targetOperationList"));
            serviceInMap.put("contentPurposeList", context.get("contentPurposeList"));
            serviceInMap.put("entityOperation", context.get("entityOperation"));

            // It is possible that permission to work with DataResources will be controlled
            // by an external Content entity.
            String ownerContentId = (String) context.get("ownerContentId");
            if (ownerContentId != null && ownerContentId.length() > 0) {
                try {
                    GenericValue content = delegator.findByPrimaryKeyCache("Content", UtilMisc.toMap("contentId", ownerContentId));
                    if (content != null)
                        serviceInMap.put("currentContent", content);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "e.getMessage()", "ContentServices");
                }
            }
            try {
                permResults = dispatcher.runSync("checkContentPermission", serviceInMap);
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem checking permissions", "ContentServices");
            }
        } else {
            permResults.put("permissionStatus", "granted");
        }
        return permResults;
    }

    /**
     * Gets image data from ImageDataResource and returns it as a byte array.
     */
    public static byte[] acquireImage(GenericDelegator delegator, String dataResourceId) throws GenericEntityException {

        byte[] b = null;
        GenericValue dataResource = delegator.findByPrimaryKeyCache("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
        if (dataResource == null)
            return b;

        b = acquireImage(delegator, dataResource);
        return b;
    }

    public static byte[] acquireImage(GenericDelegator delegator, GenericValue dataResource) throws  GenericEntityException {
        byte[] b = null;
        String dataResourceTypeId = dataResource.getString("dataResourceTypeId");
        String dataResourceId = dataResource.getString("dataResourceId");
        GenericValue imageDataResource = delegator.findByPrimaryKey("ImageDataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
        if (imageDataResource != null) {
            b = (byte[]) imageDataResource.get("imageData");
        }
        return b;
    }

    /**
     * Returns the image type.
     */
    public static String getImageType(GenericDelegator delegator, String dataResourceId) throws GenericEntityException {
        GenericValue dataResource = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
        String imageType = getImageType(delegator, dataResource);
        return imageType;
    }

    public static String getImageType(GenericDelegator delegator, GenericValue dataResource) {
        String imageType = null;
        if (dataResource != null) { 
            imageType = (String) dataResource.get("mimeTypeId");
            if (UtilValidate.isEmpty(imageType)) {
                String imageFileNameExt = null;
                String imageFileName = (String)dataResource.get("objectInfo");
                if (UtilValidate.isNotEmpty(imageFileName)) {
                    int pos = imageFileName.lastIndexOf(".");
                    if (pos >= 0) 
                        imageFileNameExt = imageFileName.substring(pos + 1);
                }
                imageType = "image/" + imageFileNameExt;
            }
        }
        return imageType;
    }

    public static String renderDataResourceAsText(GenericDelegator delegator, String dataResourceId, Map templateContext, GenericValue view, Locale locale, String mimeTypeId) throws GeneralException, IOException {
        Writer outWriter = new StringWriter();
        renderDataResourceAsText(delegator, dataResourceId, outWriter, templateContext, view, locale, mimeTypeId);
        return outWriter.toString();
    }
    
    public static String renderDataResourceAsTextCache(GenericDelegator delegator, String dataResourceId, Map templateContext, GenericValue view, Locale locale, String mimeTypeId) throws GeneralException, IOException {
        Writer outWriter = new StringWriter();
        renderDataResourceAsTextCache(delegator, dataResourceId, outWriter, templateContext, view, locale, mimeTypeId);
        return outWriter.toString();
    }
    
    public static void renderDataResourceAsText(GenericDelegator delegator, String dataResourceId, Writer out, Map templateContext, GenericValue view, Locale locale, String mimeTypeId) throws GeneralException, IOException {
        if (templateContext == null) {
            templateContext = new HashMap();
        }

        
//        Map context = (Map) templateContext.get("context");
//        if (context == null) {
//            context = new HashMap();
//        }

        if (UtilValidate.isEmpty(mimeTypeId)) {
            mimeTypeId = "text/html";
        }
        
        // if the target mimeTypeId is not a text type, throw an exception
        if (!mimeTypeId.startsWith("text/")) {
            throw new GeneralException("The desired mime-type is not a text type, cannot render as text: " + mimeTypeId);
        }

        GenericValue dataResource = null;
        if (view != null) {
            String entityName = view.getEntityName();
            dataResource = delegator.makeValue("DataResource", null);
            if ("DataResource".equals(entityName)) {
                dataResource.setAllFields(view, true, null, null);
            } else {
                dataResource.setAllFields(view, true, "dr", null);
            }
            dataResourceId = dataResource.getString("dataResourceId");
            if (UtilValidate.isEmpty(dataResourceId)) {
                throw new GeneralException("The dataResourceId [" + dataResourceId + "] is empty.");
            }
        }

        if (dataResource == null || dataResource.isEmpty()) {
            if (dataResourceId == null) {
                throw new GeneralException("DataResourceId is null");
            }
            dataResource = delegator.findByPrimaryKey("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
        }
        if (dataResource == null || dataResource.isEmpty()) {
            throw new GeneralException("DataResource not found with id=" + dataResourceId);
        }
        
        String drMimeTypeId = dataResource.getString("mimeTypeId");
        if (UtilValidate.isEmpty(drMimeTypeId)) {
            drMimeTypeId = "text/plain";
        }
        
        String dataTemplateTypeId = dataResource.getString("dataTemplateTypeId");
        
        // if this is a template, we need to get the full template text and interpret it, otherwise we should just write a bit at a time to the writer to better support large text
        if (UtilValidate.isEmpty(dataTemplateTypeId) || "NONE".equals(dataTemplateTypeId)) {
            writeDataResourceText(dataResource, mimeTypeId, locale, templateContext, delegator, out);
        } else {
            String subContentId = (String)templateContext.get("subContentId");
            //String subContentId = (String)context.get("subContentId");
            // TODO: the reason why I did this (and I can't remember) may not be valid or it can be done better
            if (UtilValidate.isNotEmpty(subContentId)) {
                //context.put("contentId", subContentId);
                //context.put("subContentId", null);
                templateContext.put("contentId", subContentId);
                templateContext.put("subContentId", null);
            }
            

            // get the full text of the DataResource
            String templateText = getDataResourceText(dataResource, mimeTypeId, locale, templateContext, delegator);
            
            //String subContentId3 = (String)context.get("subContentId");
            
//            context.put("mimeTypeId", null);
            templateContext.put("mimeTypeId", null);
//            templateContext.put("context", context);
            
            if ("FTL".equals(dataTemplateTypeId)) {
                try {
                    FreeMarkerWorker.renderTemplate("DataResource:" + dataResourceId, templateText, templateContext, out);
                } catch (TemplateException e) {
                    throw new GeneralException("Error rendering FTL template", e);
                }
            } else {
                throw new GeneralException("The dataTemplateTypeId [" + dataTemplateTypeId + "] is not yet supported");
            }
        }
    }
    
    
    public static void renderDataResourceAsTextCache(GenericDelegator delegator, String dataResourceId, Writer out, Map templateRoot, GenericValue view, Locale locale, String mimeTypeId) throws GeneralException, IOException {

        if (templateRoot == null) {
            templateRoot = new HashMap();
        }

        //Map context = (Map) templateRoot.get("context");
        //if (context == null) {
            //context = new HashMap();
        //}
        
        String disableCache = UtilProperties.getPropertyValue("content", "disable.ftl.template.cache");
        if (disableCache == null || !disableCache.equalsIgnoreCase("true")) {
            Template cachedTemplate = FreeMarkerWorker.getTemplateCached(dataResourceId);
            if (cachedTemplate != null) {
                try {
                    String subContentId = (String)templateRoot.get("subContentId");
                    if (UtilValidate.isNotEmpty(subContentId)) {
                        templateRoot.put("contentId", subContentId);
                        templateRoot.put("subContentId", null);
                        templateRoot.put("globalNodeTrail", null); // Force getCurrentContent to query for subContent
                    }
                    FreeMarkerWorker.renderTemplateCached(cachedTemplate, templateRoot, out);
                } catch (TemplateException e) {
                    Debug.logError("Error rendering FTL template. " + e.getMessage(), module);
                    throw new GeneralException("Error rendering FTL template", e);
                }
                return;
            }
        }

        if (UtilValidate.isEmpty(mimeTypeId)) {
            mimeTypeId = "text/html";
        }
        
        // if the target mimeTypeId is not a text type, throw an exception
        if (!mimeTypeId.startsWith("text/")) {
            throw new GeneralException("The desired mime-type is not a text type, cannot render as text: " + mimeTypeId);
        }

        GenericValue dataResource = null;
        if (view != null) {
            String entityName = view.getEntityName();
            dataResource = delegator.makeValue("DataResource", null);
            if ("DataResource".equals(entityName)) {
                dataResource.setAllFields(view, true, null, null);
            } else {
                dataResource.setAllFields(view, true, "dr", null);
            }
            String thisDataResourceId = null;
            try {
                thisDataResourceId = (String) view.get("drDataResourceId");
            } catch (Exception e) {
                thisDataResourceId = (String) view.get("dataResourceId");
            }
            if (UtilValidate.isEmpty(thisDataResourceId)) {
                if (UtilValidate.isNotEmpty(dataResourceId)) 
                    view = null; // causes lookup of DataResource
                else
                    throw new GeneralException("The dataResourceId [" + dataResourceId + "] is empty.");
            }
        }

        if (dataResource == null || dataResource.isEmpty()) {
            if (dataResourceId == null) {
                throw new GeneralException("DataResourceId is null");
            }
            dataResource = delegator.findByPrimaryKeyCache("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
        }
        if (dataResource == null || dataResource.isEmpty()) {
            throw new GeneralException("DataResource not found with id=" + dataResourceId);
        }
        
        String drMimeTypeId = dataResource.getString("mimeTypeId");
        if (UtilValidate.isEmpty(drMimeTypeId)) {
            drMimeTypeId = "text/plain";
        }
        
        String dataTemplateTypeId = dataResource.getString("dataTemplateTypeId");
        if (Debug.infoOn()) Debug.logInfo("in renderDataResourceAsText, dataTemplateTypeId :" + dataTemplateTypeId ,"");
        
        // if this is a template, we need to get the full template text and interpret it, otherwise we should just write a bit at a time to the writer to better support large text
        if (UtilValidate.isEmpty(dataTemplateTypeId) || "NONE".equals(dataTemplateTypeId)) {
            writeDataResourceTextCache(dataResource, mimeTypeId, locale, templateRoot, delegator, out);
        } else {
            String subContentId = (String)templateRoot.get("subContentId");
            if (UtilValidate.isNotEmpty(subContentId)) {
                templateRoot.put("contentId", subContentId);
                templateRoot.put("subContentId", null);
            }
            
            //String subContentId2 = (String)templateRoot.get("subContentId");

            // get the full text of the DataResource
            String templateText = getDataResourceTextCache(dataResource, mimeTypeId, locale, templateRoot, delegator);
            // if (Debug.infoOn()) Debug.logInfo("in renderDataResourceAsText, templateText:" + templateText,"");
            
            //String subContentId3 = (String)templateRoot.get("subContentId");
            
            templateRoot.put("mimeTypeId", null);
            
            if ("FTL".equals(dataTemplateTypeId)) {
                try {
                    // This is something of a hack. FTL templates should need "contentId" value and
                    // not subContentId so that it will find subContent.
                    templateRoot.put("subContentId", null);
                    templateRoot.put("globalNodeTrail", null); // Force getCurrentContent to query for subContent
                    //StringWriter sw = new StringWriter();
                    FreeMarkerWorker.renderTemplate("DataResource:" + dataResourceId, templateText, templateRoot, out);
                    //if (Debug.infoOn()) Debug.logInfo("in renderDataResourceAsText, sw:" + sw.toString(),"");
                    //out.write(sw.toString());
                    //out.flush();
                } catch (TemplateException e) {
                    throw new GeneralException("Error rendering FTL template", e);
                }
            } else {
                throw new GeneralException("The dataTemplateTypeId [" + dataTemplateTypeId + "] is not yet supported");
            }
        }
    }
    
    public static String getDataResourceText(GenericValue dataResource, String mimeTypeId, Locale locale, Map context, GenericDelegator delegator) throws IOException, GeneralException {
        Writer outWriter = new StringWriter();
        writeDataResourceText(dataResource, mimeTypeId, locale, context, delegator, outWriter);
        return outWriter.toString();
    }
    
    public static void writeDataResourceText(GenericValue dataResource, String mimeTypeId, Locale locale, Map templateContext, GenericDelegator delegator, Writer outWriter) throws IOException, GeneralException {

        Map context = (Map)templateContext.get("context");
        String webSiteId = (String) templateContext.get("webSiteId");
        if (UtilValidate.isEmpty(webSiteId)) {
            if (context != null)
                webSiteId = (String) context.get("webSiteId");
        }
        String https = (String) templateContext.get("https");
        if (UtilValidate.isEmpty(https)) {
            if (context != null)
                https = (String) context.get("https");
        }
        
        String dataResourceId = dataResource.getString("dataResourceId");
        String dataResourceTypeId = dataResource.getString("dataResourceTypeId");
        if (UtilValidate.isEmpty(dataResourceTypeId)) {
            dataResourceTypeId = "SHORT_TEXT";
        }
        
        if (dataResourceTypeId.equals("SHORT_TEXT")) {
            String text = dataResource.getString("objectInfo");
            outWriter.write(text);
        } else if (dataResourceTypeId.equals("ELECTRONIC_TEXT")) {
            GenericValue electronicText = delegator.findByPrimaryKey("ElectronicText", UtilMisc.toMap("dataResourceId", dataResourceId));
            String text = electronicText.getString("textData");
            outWriter.write(text);
        } else if (dataResourceTypeId.equals("IMAGE_OBJECT")) {
            // TODO: Is this where the image (or any binary) object URL is created? looks like it is just returning 
            //the ID, maybe is okay, but maybe should create the whole image tag so that text and images can be 
            //interchanged without changing the wrapping template, and so the wrapping template doesn't have to know what the root is, etc
            /*
            // decide how to render based on the mime-types
            // TODO: put this in a separate method to be re-used for file objects as well...
            if ("text/html".equals(mimeTypeId)) {
            } else if ("text/plain".equals(mimeTypeId)) {
            } else {
                throw new GeneralException("The renderDataResourceAsText operation does not yet support the desired mime-type: " + mimeTypeId);
            }
            */
            
            String text = (String) dataResource.get("dataResourceId");
            outWriter.write(text);
        } else if (dataResourceTypeId.equals("LINK")) {
            String text = dataResource.getString("objectInfo");
            outWriter.write(text);
        } else if (dataResourceTypeId.equals("URL_RESOURCE")) {
            String text = null;
            URL url = new URL(dataResource.getString("objectInfo"));
            if (url.getHost() != null) { // is absolute
                InputStream in = url.openStream();
                int c;
                StringWriter sw = new StringWriter();
                while ((c = in.read()) != -1) {
                    sw.write(c);
                }
                sw.close();
                text = sw.toString();
            } else {
                String prefix = buildRequestPrefix(delegator, locale, webSiteId, https);
                String sep = "";
                //String s = "";
                if (url.toString().indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                    sep = "/";
                }
                String s2 = prefix + sep + url.toString();
                URL url2 = new URL(s2);
                text = (String) url2.getContent();
            }
            outWriter.write(text);
        } else if (dataResourceTypeId.indexOf("_FILE") >= 0) {
            String rootDir = (String) templateContext.get("rootDir");
            if (UtilValidate.isEmpty(rootDir)) {
                if (context != null)
                    rootDir = (String) context.get("rootDir");
            }
            renderFile(dataResourceTypeId, dataResource.getString("objectInfo"), rootDir, outWriter);
        } else {
            throw new GeneralException("The dataResourceTypeId [" + dataResourceTypeId + "] is not supported in renderDataResourceAsText");
        }
    }

    public static String getDataResourceTextCache(GenericValue dataResource, String mimeTypeId, Locale locale, Map context, GenericDelegator delegator) throws IOException, GeneralException {
        Writer outWriter = new StringWriter();
        writeDataResourceText(dataResource, mimeTypeId, locale, context, delegator, outWriter);
        return outWriter.toString();
    }
    
    public static void writeDataResourceTextCache(GenericValue dataResource, String mimeTypeId, Locale locale, Map context, GenericDelegator delegator, Writer outWriter) throws IOException, GeneralException {
        String webSiteId = (String) context.get("webSiteId");
        String https = (String) context.get("https");
        
        String dataResourceId = dataResource.getString("dataResourceId");
        String dataResourceTypeId = dataResource.getString("dataResourceTypeId");
        if (UtilValidate.isEmpty(dataResourceTypeId)) {
            dataResourceTypeId = "SHORT_TEXT";
        }
        
        if (dataResourceTypeId.equals("SHORT_TEXT")) {
            String text = dataResource.getString("objectInfo");
            if (UtilValidate.isNotEmpty(text)) 
                outWriter.write(text);
        } else if (dataResourceTypeId.equals("ELECTRONIC_TEXT")) {
            GenericValue electronicText = delegator.findByPrimaryKeyCache("ElectronicText", UtilMisc.toMap("dataResourceId", dataResourceId));
            String text = electronicText.getString("textData");
            if (UtilValidate.isNotEmpty(text)) 
                outWriter.write(text);
        } else if (dataResourceTypeId.equals("IMAGE_OBJECT")) {
            // TODO: Is this where the image (or any binary) object URL is created? looks like it is just returning 
            //the ID, maybe is okay, but maybe should create the whole image tag so that text and images can be 
            //interchanged without changing the wrapping template, and so the wrapping template doesn't have to know what the root is, etc
            /*
            // decide how to render based on the mime-types
            // TODO: put this in a separate method to be re-used for file objects as well...
            if ("text/html".equals(mimeTypeId)) {
            } else if ("text/plain".equals(mimeTypeId)) {
            } else {
                throw new GeneralException("The renderDataResourceAsText operation does not yet support the desired mime-type: " + mimeTypeId);
            }
            */
            
            String text = (String) dataResource.get("dataResourceId");
            outWriter.write(text);
        } else if (dataResourceTypeId.equals("LINK")) {
            String text = dataResource.getString("objectInfo");
            outWriter.write(text);
        } else if (dataResourceTypeId.equals("URL_RESOURCE")) { String text = null;
            URL url = new URL(dataResource.getString("objectInfo"));
            if (url.getHost() != null) { // is absolute
                InputStream in = url.openStream();
                int c;
                StringWriter sw = new StringWriter();
                while ((c = in.read()) != -1) {
                    sw.write(c);
                }
                sw.close();
                text = sw.toString();
            } else {
                String prefix = buildRequestPrefix(delegator, locale, webSiteId, https);
                String sep = "";
                //String s = "";
                if (url.toString().indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                    sep = "/";
                }
                String s2 = prefix + sep + url.toString();
                URL url2 = new URL(s2);
                text = (String) url2.getContent();
            }
            outWriter.write(text);
        } else if (dataResourceTypeId.indexOf("_FILE") >= 0) {
            String rootDir = (String) context.get("rootDir");
            renderFile(dataResourceTypeId, dataResource.getString("objectInfo"), rootDir, outWriter);
        } else {
            throw new GeneralException("The dataResourceTypeId [" + dataResourceTypeId + "] is not supported in renderDataResourceAsText");
        }
    }

    public static void renderFile(String dataResourceTypeId, String objectInfo, String rootDir, Writer out) throws GeneralException, IOException {
        // TODO: this method assumes the file is a text file, if it is an image we should respond differently, see the comment above for IMAGE_OBJECT type data resource
        
        if (dataResourceTypeId.equals("LOCAL_FILE")) {
            File file = new File(objectInfo);
            if (!file.isAbsolute()) {
                throw new GeneralException("File (" + objectInfo + ") is not absolute");
            }
            int c;
            FileReader in = new FileReader(file);
            while ((c = in.read()) != -1) {
                out.write(c);
            }
        } else if (dataResourceTypeId.equals("OFBIZ_FILE")) {
            String prefix = System.getProperty("ofbiz.home");
            String sep = "";
            if (objectInfo.indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                sep = "/";
            }
            File file = new File(prefix + sep + objectInfo);
            int c;
            FileReader in = new FileReader(file);
            while ((c = in.read()) != -1)
                out.write(c);
        } else if (dataResourceTypeId.equals("CONTEXT_FILE")) {
            String prefix = rootDir;
            String sep = "";
            if (objectInfo.indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                sep = "/";
            }
            File file = new File(prefix + sep + objectInfo);
            int c;
            FileReader in = null;
            try {
                in = new FileReader(file);
                String enc = in.getEncoding();
                if (Debug.infoOn()) Debug.logInfo("in serveImage, encoding:" + enc, module);

            } catch (FileNotFoundException e) {
                Debug.logError(e, " in renderDataResourceAsHtml(CONTEXT_FILE), in FNFexception:", module);
                throw new GeneralException("Could not find context file to render", e);
            } catch (Exception e) {
                Debug.logError(" in renderDataResourceAsHtml(CONTEXT_FILE), got exception:" + e.getMessage(), module);
            }
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            //out.flush();
        }
        return;
    }

    public static String buildRequestPrefix(GenericDelegator delegator, Locale locale, String webSiteId, String https) {
        String prefix = null;
        Map prefixValues = new HashMap();
        NotificationServices.setBaseUrl(delegator, webSiteId, prefixValues);
        if (https != null && https.equalsIgnoreCase("true")) {
            prefix = (String) prefixValues.get("baseSecureUrl");
        } else {
            prefix = (String) prefixValues.get("baseUrl");
        }
        if (UtilValidate.isEmpty(prefix)) {
            if (https != null && https.equalsIgnoreCase("true")) {
                prefix = UtilProperties.getMessage("content", "baseSecureUrl", locale);
            } else {
                prefix = UtilProperties.getMessage("content", "baseUrl", locale);
            }
        }

        return prefix;
    }

    public static File getContentFile(String dataResourceTypeId, String objectInfo, String rootDir)  throws GeneralException, FileNotFoundException{

        File file = null;
        if (dataResourceTypeId.equals("LOCAL_FILE")) {
            file = new File(objectInfo);
            if (!file.isAbsolute()) {
                throw new GeneralException("File (" + objectInfo + ") is not absolute");
            }
            int c;
        } else if (dataResourceTypeId.equals("OFBIZ_FILE")) {
            String prefix = System.getProperty("ofbiz.home");
            String sep = "";
            if (objectInfo.indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                sep = "/";
            }
            file = new File(prefix + sep + objectInfo);
        } else if (dataResourceTypeId.equals("CONTEXT_FILE")) {
            String prefix = rootDir;
            String sep = "";
            if (objectInfo.indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                sep = "/";
            }
            file = new File(prefix + sep + objectInfo);
        }
        return file;
    }


    public static String getDataResourceMimeType(GenericDelegator delegator, String dataResourceId, GenericValue view) throws GenericEntityException {
        
        String mimeType = null;
        if (view != null)
            mimeType = view.getString("drMimeTypeId");
            //if (Debug.infoOn()) Debug.logInfo("getDataResourceMimeType, mimeType(2):" + mimeType, "");
        if (UtilValidate.isEmpty(mimeType) && UtilValidate.isNotEmpty(dataResourceId)) {
                GenericValue dataResource = delegator.findByPrimaryKeyCache("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
                //if (Debug.infoOn()) Debug.logInfo("getDataResourceMimeType, dataResource(2):" + dataResource, "");
                mimeType = dataResource.getString("mimeTypeId");
                
        }
        return mimeType;
    }
}
