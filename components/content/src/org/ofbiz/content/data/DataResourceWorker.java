/*
 * $Id: DataResourceWorker.java,v 1.14 2003/12/23 15:03:23 jonesde Exp $
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

import freemarker.template.TemplateException;

/**
 * DataResourceWorker Class
 * 
 * @author <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version $Revision: 1.14 $
 * @since 3.0
 */
public class DataResourceWorker {

    public static final String module = DataResourceWorker.class.getName();

    /**
     * Traverses the DataCategory parent/child structure and puts it in categoryNode. Returns non-null error string if there is an error.
     */
    public static String getDataCategoryMap(GenericDelegator delegator, int depth, Map categoryNode, List categoryTypeIds, boolean getAll) throws GenericEntityException {
        String errorMsg = null;
        String dataCategoryId = (String) categoryNode.get("id");
        String currentDataCategoryId = null;
        int sz = categoryTypeIds.size();
        if (depth >= 0 && (sz - depth) > 0) {
            currentDataCategoryId = (String) categoryTypeIds.get(sz - depth - 1);
        }

        //EntityExpr expr = null;
        String matchValue = null;
        if (dataCategoryId != null) {
            //expr = new EntityExpr("parentCategoryId", EntityOperator.EQUALS, dataCategoryId);
            matchValue = dataCategoryId;
        } else {
            //expr = new EntityExpr("parentCategoryId", EntityOperator.EQUALS, null);
            matchValue = null;
        }
        //List categoryValues = delegator.findByConditionCache("DataCategory", expr, null, null );
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
        if (dataCategoryId == null
            || dataCategoryId.equals("ROOT")
            || (currentDataCategoryId != null && currentDataCategoryId.equals(dataCategoryId))
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

        if (Debug.infoOn()) Debug.logInfo("in uploadAndStoreImage, idField:" + idField, "");
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
            if (Debug.infoOn()) Debug.logInfo("in uploadAndStoreImage, fieldName:" + fieldName, "");
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

        if (Debug.infoOn()) Debug.logInfo("in uploadAndStoreImage, idFieldValue:" + idFieldValue, "");

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
        String permissionStatus = "granted";
        String skipPermissionCheck = (String) context.get("skipPermissionCheck");

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
                    GenericValue content = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", ownerContentId));
                    if (content != null)
                        serviceInMap.put("currentContent", content);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "e.getMessage()", "ContentServices");
                }
            }
            try {
                Map permResults = dispatcher.runSync("checkContentPermission", serviceInMap);
                permissionStatus = (String) permResults.get("permissionStatus");
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem checking permissions", "ContentServices");
            }
        }
        return permissionStatus;
    }

    /**
     * Gets image data from ImageDataResource and returns it as a byte array.
     */
    public static byte[] acquireImage(GenericDelegator delegator, String dataResourceId) throws GenericEntityException {
        GenericValue imageDataResource = delegator.findByPrimaryKey("ImageDataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
        byte[] b = null;
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
        String imageType = (String) dataResource.get("mimeTypeId");
        return imageType;
    }

    public static void renderDataResourceAsText(GenericDelegator delegator, String dataResourceId, Writer out, Map templateContext, GenericValue view, Locale locale, String mimeTypeId) throws GeneralException, IOException {
        // TODO: is this really where the context comes from? where is it put there?
        Map context = (Map) templateContext.get("context");
        if (context == null) {
            context = new HashMap();
        }

        //if (Debug.infoOn()) Debug.logInfo(" in renderDataResourceAsHtml, mimeTypeId:" + mimeTypeId, module);
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
            //if (Debug.infoOn()) Debug.logInfo("in renderDAtaResource(work), dataResourceMap:" + dataResourceMap, "");
            dataResourceId = dataResource.getString("dataResourceId");
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
            String subContentId = (String)context.get("subContentId");
            if (Debug.infoOn()) Debug.logInfo(" in renderDataResourceAsHtml, subContentId:" + subContentId, module);
            if (UtilValidate.isNotEmpty(subContentId)) {
                context.put("contentId", subContentId);
                context.put("subContentId", null);
            }
            
            //String subContentId2 = (String)context.get("subContentId");

            // get the full text of the DataResource
            String templateText = getDataResourceText(dataResource, mimeTypeId, locale, templateContext, delegator);
            
            //String subContentId3 = (String)context.get("subContentId");
            
            context.put("mimeTypeId", null);
            templateContext.put("context", context);
            
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
    
    public static String getDataResourceText(GenericValue dataResource, String mimeTypeId, Locale locale, Map context, GenericDelegator delegator) throws IOException, GeneralException {
        Writer outWriter = new StringWriter();
        writeDataResourceText(dataResource, mimeTypeId, locale, context, delegator, outWriter);
        return outWriter.toString();
    }
    
    public static void writeDataResourceText(GenericValue dataResource, String mimeTypeId, Locale locale, Map context, GenericDelegator delegator, Writer outWriter) throws IOException, GeneralException {
        String webSiteId = (String) context.get("webSiteId");
        String https = (String) context.get("https");
        
        String dataResourceId = dataResource.getString("dataResourceId");
        String dataResourceTypeId = dataResource.getString("dataResourceTypeId");
        if (UtilValidate.isEmpty(dataResourceTypeId)) {
            dataResourceTypeId = "SHORT_TEXT";
        }
        if (Debug.infoOn()) Debug.logInfo(" in renderDataResourceAsHtml, dataResourceTypeId:" + dataResourceTypeId, module);
        
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
            
            if (Debug.infoOn()) Debug.logInfo(" in renderDataResourceAsHtml(IMAGE), mimeTypeId:" + mimeTypeId, module);
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
                if (Debug.infoOn()) Debug.logInfo(" in renderDataResourceAsHtml(URL-ABS), text:" + text, module);
            } else {
                String prefix = buildRequestPrefix(delegator, locale, webSiteId, https);
                String sep = "";
                //String s = "";
                if (url.toString().indexOf("/") != 0 && prefix.lastIndexOf("/") != (prefix.length() - 1)) {
                    sep = "/";
                }
                String s2 = prefix + sep + url.toString();
                URL url2 = new URL(s2);
                if (Debug.infoOn()) Debug.logInfo(" in renderDataResourceAsHtml(URL-REL), s2:" + s2, module);
                text = (String) url2.getContent();
                if (Debug.infoOn()) Debug.logInfo(" in renderDataResourceAsHtml(URL-REL), text:" + text, module);
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
            if (Debug.infoOn()) Debug.logInfo(" in renderDataResourceAsHtml(LOCAL), file:" + file, module);
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
            if (Debug.infoOn()) Debug.logInfo(" in renderDataResourceAsHtml(OFBIZ_FILE), file:" + file, module);
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
            } catch (FileNotFoundException e) {
                if (Debug.infoOn()) Debug.logInfo(" in renderDataResourceAsHtml(CONTEXT_FILE), in FNFexception:" + e.getMessage(), module);
                throw new GeneralException("Could not find context file to render", e);
            } catch (Exception e) {
                Debug.logError(" in renderDataResourceAsHtml(CONTEXT_FILE), got exception:" + e.getMessage(), module);
            }
            if (Debug.infoOn()) Debug.logInfo(" in renderDataResourceAsHtml(CONTEXT_FILE), after FileReader:", module);
            while ((c = in.read()) != -1) {
                out.write(c);
            }
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
        if (Debug.infoOn()) Debug.logInfo("in buildRequestPrefix, prefix:" + prefix, "");

        return prefix;
    }
}
