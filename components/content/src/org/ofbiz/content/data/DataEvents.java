package org.ofbiz.content.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

/**
 * DataEvents Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev$
 * @since      3.0
 *
 * 
 */
public class DataEvents {

    public static final String module = DataEvents.class.getName();
    public static final String err_resource = "ContentErrorUiLabel";

    public static String uploadImage(HttpServletRequest request, HttpServletResponse response) {

        return DataResourceWorker.uploadAndStoreImage(request, "dataResourceId", "imageData");

    }

    /** Streams ImageDataResource data to the output. */
    public static String serveImage(HttpServletRequest request, HttpServletResponse response) {
        Debug.log("Img UserAgent - " + request.getHeader("User-Agent"), module);
        Map parameters = UtilHttp.getParameterMap(request);
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String dataResourceId = (String) parameters.get("imgId");
        if (UtilValidate.isEmpty(dataResourceId)) {
            String errorMsg = "Error getting image record from db: " + " dataResourceId is empty";
            Debug.logError(errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }

        Locale locale = UtilHttp.getLocale(request);
        GenericValue dataResource = null;
        try {

            dataResource = delegator.findByPrimaryKeyCache("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId));
        } catch (GenericEntityException e) {
            String errMsg = UtilProperties.getMessage(DataEvents.err_resource, "dataEvents.error_get_image", locale);
            String errorMsg = "Error getting image record from db: " + e.toString();
            Debug.logError(e, errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errMsg + e.toString());
            return "error";
        }

        byte[] b = null;
        String mimeType = DataResourceWorker.getMimeType(dataResource);
        //if (Debug.infoOn()) Debug.logInfo("in serveImage, imageType:" + imageType, module);
        String dataResourceTypeId = dataResource.getString("dataResourceTypeId");
        //if (Debug.infoOn()) Debug.logInfo("in serveImage, dataResourceTypeId:" + dataResourceTypeId, module);
        if (dataResourceTypeId != null && dataResourceTypeId.equals("IMAGE_OBJECT")) {
            try {
                b = DataResourceWorker.acquireImage(delegator, dataResource);
                if (mimeType == null || b == null || b.length == 0) {
                    Map messageMap = UtilMisc.toMap("b", b, "imageType", mimeType);
                    String errMsg = UtilProperties.getMessage(DataEvents.err_resource, "dataEvents.image_or_type_null", messageMap, locale);
                    request.setAttribute("_ERROR_MESSAGE_", errMsg);
                    return "error";
                } else {
                    try {
                        if (Debug.infoOn()) Debug.logInfo("in serveImage, byteArray.length:" + b.length, module);
                        UtilHttp.streamContentToBrowser(response, b, mimeType);
                        response.flushBuffer();
                    } catch (IOException e) {
                        String errMsg = UtilProperties.getMessage(DataEvents.err_resource, "dataEvents.error_write_image", locale);
                        String errorMsg = "Error writing image to OutputStream: " + e.toString();
                        Debug.logError(e, errorMsg, module);
                        request.setAttribute("_ERROR_MESSAGE_", errMsg + e.toString());
                        return "error";
                    }
                }
            } catch (GenericEntityException e) {
                String errMsg = UtilProperties.getMessage(DataEvents.err_resource, "dataEvents.error_get_image", locale);
                String errorMsg = "Error getting image record from acquireImage: " + e.toString();
                Debug.logError(e, errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg + e.toString());
                return "error";
            }
        } else if (dataResourceTypeId != null && dataResourceTypeId.indexOf("_FILE") >= 0) {
            String fileName = dataResource.getString("objectInfo");
            //if (Debug.infoOn()) Debug.logInfo("in serveImage, fileName:" + fileName, module);
            ServletContext servletContext = request.getSession().getServletContext();
            String rootDir = servletContext.getRealPath("/");
            //if (Debug.infoOn()) Debug.logInfo("in serveImage, rootDir:" + rootDir, module);

            // hack for IE and mime types
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.indexOf("MSIE") > -1) {
                Debug.log("Found MSIE changing mime type from - " + mimeType, module);
                mimeType = "application/octet-stream";
            }

            try {
                File contentFile = DataResourceWorker.getContentFile(dataResourceTypeId, fileName, rootDir);
                FileInputStream fis = new FileInputStream(contentFile);
                int fileSize = (new Long(contentFile.length())).intValue();
                UtilHttp.streamContentToBrowser(response, fis, fileSize, mimeType);
            } catch (FileNotFoundException e4) {
                String errMsg = UtilProperties.getMessage(DataEvents.err_resource, "dataEvents.error_get_image", locale);
                String errorMsg = "Error getting image record from db: " + e4.toString();
                Debug.logError(e4, errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg + e4.toString());
                return "error";

            } catch (IOException e2) {
                String errMsg = UtilProperties.getMessage(DataEvents.err_resource, "dataEvents.error_get_image", locale);
                String errorMsg = "Error getting image record from db: " + e2.toString();
                Debug.logError(e2, errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg + e2.toString());
                return "error";
            } catch (GeneralException e3) {
                String errMsg = UtilProperties.getMessage(DataEvents.err_resource, "dataEvents.error_get_image", locale);
                String errorMsg = "Error getting image record from db: " + e3.toString();
                Debug.logError(e3, errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg + e3.toString());
                return "error";
            }
        }


        return "success";
    }


    /** Dual create and edit event. 
     *  Needed to make permission criteria available to services. 
     */
    public static String persistDataResource(HttpServletRequest request, HttpServletResponse response) {

        Map result = null;
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map paramMap = UtilHttp.getParameterMap(request);
        String dataResourceId = (String)paramMap.get("dataResourceId");
        GenericValue dataResource = delegator.makeValue("DataResource", null);
        dataResource.setPKFields(paramMap);
        dataResource.setNonPKFields(paramMap);
        Map serviceInMap = new HashMap(dataResource); 
        serviceInMap.put("userLogin", userLogin);
        String mode = (String)paramMap.get("mode");
        Locale locale = UtilHttp.getLocale(request);

        if (mode != null && mode.equals("UPDATE")) {
            try {
                result = dispatcher.runSync("updateDataResource", serviceInMap);
            } catch (GenericServiceException e) {
                String errMsg = UtilProperties.getMessage(DataEvents.err_resource, "dataEvents.error_call_update_service", locale);
                String errorMsg = "Error calling the updateDataResource service." + e.toString();
                Debug.logError(e, errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg + e.toString());
                return "error";
            }
        } else {
            mode = "CREATE";
            try {
                result = dispatcher.runSync("createDataResource", serviceInMap);
            } catch (GenericServiceException e) {
                String errMsg = UtilProperties.getMessage(DataEvents.err_resource, "dataEvents.error_call_create_service", locale);                                                                    
                String errorMsg = "Error calling the createDataResource service." + e.toString();
                Debug.logError(e, errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg + e.toString());
                return "error";
            }
            dataResourceId = (String)result.get("dataResourceId");
            dataResource.set("dataResourceId", dataResourceId);
        }
        
   
        // Save the primary key so that it can be used in a "quick pick" list later
        GenericPK pk = dataResource.getPrimaryKey();
        HttpSession session = request.getSession();
        //ContentManagementWorker.mruAdd(session, pk);

        String returnStr = "success";
        if (mode.equals("CREATE") ) {
            // Set up return message to guide selection of follow on view
            request.setAttribute("dataResourceId", result.get("dataResourceId") );
            String dataResourceTypeId = (String)serviceInMap.get("dataResourceTypeId");
            if (dataResourceTypeId != null) {
                 if (dataResourceTypeId.equals("ELECTRONIC_TEXT")
                     || dataResourceTypeId.equals("IMAGE_OBJECT") ) {
                    returnStr = dataResourceTypeId;
                 }
            }
        }

        return returnStr;
    }

}
