package org.ofbiz.content.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

/**
 * DataEvents Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.4 $
 * @since      3.0
 *
 * 
 */
public class DataEvents {

    public static final String module = DataEvents.class.getName();

    public static String uploadImage(HttpServletRequest request, HttpServletResponse response) {

        return DataResourceWorker.uploadAndStoreImage(request, "dataResourceId", "imageData");

    }

    /** Streams ImageDataResource data to the output. */
    public static String serveImage(HttpServletRequest request, HttpServletResponse response) {

        byte[] b = DataResourceWorker.acquireImage(request, "imgId");
        if (b == null) return "error";

        String imageType = DataResourceWorker.getImageType(request, "imgId");
        if (imageType == null || imageType.equals("error")) return "error";
        try {
            UtilHttp.streamContentToBrowser(response, b, imageType);
        } catch (IOException e) {
            String errorMsg = "Error writing image to OutputStream: " + e.toString();
            Debug.logError(e, errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
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
        dataResource.setNonPKFields(paramMap);
        Map serviceInMap = new HashMap(dataResource); 
        serviceInMap.put("userLogin", userLogin);
        String mode = null;

        if (dataResourceId != null && dataResourceId.length() > 0) {
            mode = "UPDATE";
            try {
                result = dispatcher.runSync("updateDataResource", serviceInMap);
            } catch (GenericServiceException e) {
                String errMsg = "Error calling the updateDataResource service." + e.toString();
                Debug.logError(e, errMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
        } else {
            mode = "CREATE";
            try {
                result = dispatcher.runSync("createDataResource", serviceInMap);
            } catch (GenericServiceException e) {
                String errMsg = "Error calling the createDataResource service." + e.toString();
                Debug.logError(e, errMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errMsg);
                return "error";
            }
            dataResourceId = (String)result.get("dataResourceId");
            dataResource.set("dataResourceId", dataResourceId);
        }
        
   
        // Save the primary key so that it can be used in a "quick pick" list later
        GenericPK pk = dataResource.getPrimaryKey();
        HttpSession session = request.getSession();
        //ContentManagementWorker.lruAdd(session, pk);

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
