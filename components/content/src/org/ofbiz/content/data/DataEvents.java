package org.ofbiz.content.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.io.*;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
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
import org.apache.commons.fileupload.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * DataEvents Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.2 $
 * @since      3.0
 *
 * 
 */
public class DataEvents {

    public static final String module = DataEvents.class.getName();

    public static String uploadImage(HttpServletRequest request, HttpServletResponse response) {

        return DataResourceWorker.uploadAndStoreImage(request, "dataResourceId", "imageData");

    }

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
}
