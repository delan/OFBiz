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
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.LocalDispatcher;

/**
 * DataEvents Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.6 $
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

        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        String dataResourceId = request.getParameter("imgId");
        byte[] b = null;
        String imageType = null;
        try {
            b = DataResourceWorker.acquireImage(delegator, dataResourceId);
            imageType = DataResourceWorker.getImageType(delegator, dataResourceId);
        } catch (GenericEntityException e) {
            String errorMsg = "Error getting image record from db: " + e.toString();
            Debug.logError(e, errorMsg, module);
            request.setAttribute("_ERROR_MESSAGE_", errorMsg);
            return "error";
        }

        if (imageType == null || b == null) {
                String errorMsg = "image(" + b + ") or type(" + imageType + ") is null.";
                Debug.logInfo(errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errorMsg);
                return "error";
        } else {
            try {
                UtilHttp.streamContentToBrowser(response, b, imageType);
            } catch (IOException e) {
                String errorMsg = "Error writing image to OutputStream: " + e.toString();
                Debug.logError(e, errorMsg, module);
                request.setAttribute("_ERROR_MESSAGE_", errorMsg);
                return "error";
            }
        }

        return "success";
    }


}
