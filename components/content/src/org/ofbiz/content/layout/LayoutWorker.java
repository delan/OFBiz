package org.ofbiz.content.layout;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.service.ServiceUtil;

/**
 * LayoutWorker Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.3 $
 * @since      3.0
 *
 * 
 */
public class LayoutWorker {

    public static final String module = LayoutWorker.class.getName();


    /**
     * Uploads image data from a form and stores it in ImageDataResource. 
     * Expects key data in a field identitified by the "idField" value
     * and the binary data to be in a field id'd by uploadField.
     */
    public static Map uploadImageAndParameters(HttpServletRequest request, String uploadField) {

        //Debug.logVerbose("in uploadAndStoreImage", "");
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");

        HashMap results = new HashMap();
        HashMap formInput = new HashMap();
        results.put("formInput", formInput);
        DiskFileUpload fu = new DiskFileUpload();
        java.util.List lst = null;
        try {
           lst = fu.parseRequest(request);
        } catch (FileUploadException e4) {
            return ServiceUtil.returnError(e4.getMessage());
        }

        if (lst.size() == 0) {
           request.setAttribute("_ERROR_MESSAGE_", "No files uploaded");
            //Debug.logWarning("[DataEvents.uploadImage] No files uploaded", module);
            return ServiceUtil.returnError("No files uploaded.");
        }


        // This code finds the idField and the upload FileItems 
        FileItem fi = null;
        FileItem imageFi = null;
        for (int i=0; i < lst.size(); i++) {
            fi = (FileItem)lst.get(i);
	    String fn = fi.getName();
	    String fieldName = fi.getFieldName();
	    String fieldStr = fi.getString();
            if (fi.isFormField()) {
                formInput.put(fieldName, fieldStr);
            //Debug.logVerbose("in uploadAndStoreImage, fieldName:" + fieldName + " fieldStr:" + fieldStr, "");
            }
            if (fieldName.equals(uploadField)) imageFi = fi;
        }

        if (imageFi == null ) {
           request.setAttribute("_ERROR_MESSAGE_", "imageFi(" + imageFi  + ") is null");
            //Debug.logWarning("[DataEvents.uploadImage] imageFi(" + imageFi + ") is null", module);
            return null;
        }

        byte[] imageBytes = imageFi.get();
        ByteWrapper byteWrap = new ByteWrapper(imageBytes);
        results.put("imageData", byteWrap);
        results.put("imageFileName", imageFi.getName());
      
        //Debug.logVerbose("in uploadAndStoreImage, results:" + results, "");
        return results;

    }


    public static ByteWrapper returnByteWrapper(Map map) {

        ByteWrapper byteWrap = (ByteWrapper)map.get("imageData");
        return byteWrap;
    }

}
