package org.ofbiz.content.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.ByteWrapper;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;


/**
 * UploadContentAndImage Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Revision: 1.7 $
 * @since      2.2
 *
 * Services for granting operation permissions on Content entities in a data-driven manner.
 */
public class UploadContentAndImage {

    public static final String module = UploadContentAndImage.class.getName();


    public UploadContentAndImage() {}


    public static String uploadContentAndImage(HttpServletRequest request, HttpServletResponse response) {

       
        try {
            LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
            GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
            HttpSession session = request.getSession();
            GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");

            DiskFileUpload fu = new DiskFileUpload();
            //if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]DiskFileUpload " + fu, module);
            java.util.List lst = null;
            try {
                lst = fu.parseRequest(request);
            } catch (FileUploadException e4) {
                request.setAttribute("_ERROR_MESSAGE_", e4.getMessage());
                Debug.logError("[UploadContentAndImage.uploadContentAndImage] " + e4.getMessage(), module);
                return "error";
            }
            //if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]lst " + lst, module);
    
            if (lst.size() == 0) {
                request.setAttribute("_ERROR_MESSAGE_", "No files uploaded");
                Debug.logWarning("[DataEvents.uploadImage] No files uploaded", module);
                return "error";
            }
    
            Map passedParams = new HashMap();
            FileItem fi = null;
            FileItem imageFi = null;
            byte[] imageBytes = {};
            for (int i = 0; i < lst.size(); i++) {
                fi = (FileItem) lst.get(i);
                //String fn = fi.getName();
                String fieldName = fi.getFieldName();
                if (Debug.verboseOn()) Debug.logVerbose("in uploadAndStoreImage, fieldName:" + fieldName, "");
                if (fi.isFormField()) {
                    String fieldStr = fi.getString();
                    passedParams.put(fieldName, fieldStr);
                } else if (fieldName.equals("imageData")) {
                    imageFi = fi;
                    imageBytes = imageFi.get();
                }
            }
            if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]passedParams: " + passedParams, module);

            TransactionUtil.begin();
            List contentPurposeList = ContentWorker.prepContentPurposeList(passedParams);
            passedParams.put("contentPurposeList", contentPurposeList );
            String entityOperation = (String)passedParams.get("entityOperation");
            String passedContentId = (String)passedParams.get("ftlContentId");
            List targetOperationList = ContentWorker.prepTargetOperationList(passedParams, entityOperation);
            passedParams.put("targetOperationList", targetOperationList );
            // Create or update FTL template
            Map ftlContext = new HashMap();
            ftlContext.put("userLogin", userLogin);
            ftlContext.put("contentId", passedParams.get("ftlContentId"));
            ftlContext.put("ownerContentId", passedParams.get("ownerContentId"));
            String contentTypeId = (String)passedParams.get("contentTypeId");
            ftlContext.put("contentTypeId", contentTypeId);
            ftlContext.put("statusId", passedParams.get("statusId"));
            ftlContext.put("contentPurposeList", UtilMisc.toList(passedParams.get("contentPurposeList")));
            ftlContext.put("contentPurposeList", contentPurposeList);
            ftlContext.put("targetOperationList",targetOperationList);
            ftlContext.put("contentName", passedParams.get("contentName"));
            ftlContext.put("dataTemplateTypeId", passedParams.get("dataTemplateTypeId"));
            ftlContext.put("description", passedParams.get("description"));
            ftlContext.put("privilegeEnumId", passedParams.get("privilegeEnumId"));
            if (Debug.verboseOn()) Debug.logVerbose("[UploadContentAndImage]passedParams " + passedParams, module);
            String drid = (String)passedParams.get("dataResourceId");
            //if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]drid:" + drid, module);
            ftlContext.put("dataResourceId", drid);
            if (Debug.verboseOn()) Debug.logVerbose("[UploadContentAndImage]ftlContext(1):" + ftlContext, module);
            ftlContext.put("dataResourceTypeId", null); // inhibits persistence of DataResource, because it already exists
            ftlContext.put("contentIdTo", passedParams.get("contentIdTo"));
            ftlContext.put("contentAssocTypeId", passedParams.get("contentAssocTypeId"));
            if (Debug.verboseOn()) Debug.logVerbose("[UploadContentAndImage]ftlContext " + ftlContext, module);
            if (Debug.verboseOn()) Debug.logVerbose("[UploadContentAndImage]ftlContext(2):" + ftlContext, module);
            if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]persist ftlContext: " + ftlContext, module);
            Map ftlResults = dispatcher.runSync("persistContentAndAssoc", ftlContext);
            if (Debug.verboseOn()) Debug.logVerbose("[UploadContentAndImage]ftlContext(3):" + ftlContext, module);
            boolean isError = ModelService.RESPOND_ERROR.equals(ftlResults.get(ModelService.RESPONSE_MESSAGE));
            if (isError) {
                request.setAttribute("_ERROR_MESSAGE_", ftlResults.get(ModelService.ERROR_MESSAGE));
                    TransactionUtil.rollback();
                return "error";
            }

            String ftlContentId = (String)ftlResults.get("contentId");
            if (UtilValidate.isEmpty(ftlContentId)) 
                ftlContentId = passedContentId;
          
            String ftlDataResourceId = drid;

            if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]ftlContentId:" + ftlContentId, module);
            //if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]ftlDataResourceId:" + ftlDataResourceId, module);
            // Create or update summary text subContent
            if ( passedParams.containsKey("summaryData") ) {
                Map sumContext = new HashMap();
                sumContext.put("userLogin", userLogin);
                sumContext.put("contentId", passedParams.get("sumContentId"));
                sumContext.put("ownerContentId", ftlContentId);
                sumContext.put("contentTypeId", "DOCUMENT");
                sumContext.put("statusId", passedParams.get("statusId"));
                sumContext.put("contentPurposeList", contentPurposeList);
                sumContext.put("targetOperationList",targetOperationList);
                sumContext.put("contentName", passedParams.get("contentName"));
                sumContext.put("description", passedParams.get("description"));
                sumContext.put("privilegeEnumId", passedParams.get("privilegeEnumId"));
                sumContext.put("dataResourceId", passedParams.get("sumDataResourceId"));
                sumContext.put("dataResourceTypeId", "ELECTRONIC_TEXT");
                sumContext.put("contentIdTo", ftlContentId);
                sumContext.put("contentAssocTypeId", "SUB_CONTENT");
                sumContext.put("textData", passedParams.get("summaryData"));
                sumContext.put("mapKey", "SUMMARY");
                sumContext.put("dataTemplateTypeId", "NONE");
                if (Debug.verboseOn()) Debug.logVerbose("[UploadContentAndImage]sumContext " + sumContext, module);
                Map sumResults = dispatcher.runSync("persistContentAndAssoc", sumContext);
                isError = ModelService.RESPOND_ERROR.equals(sumResults.get(ModelService.RESPONSE_MESSAGE));
                if (isError) {
                    request.setAttribute("_ERROR_MESSAGE_", sumResults.get(ModelService.ERROR_MESSAGE));
                    TransactionUtil.rollback();
                    return "error";
                }
            }

            // Create or update electronic text subContent
            if ( passedParams.containsKey("textData") ) {
                Map txtContext = new HashMap();
                txtContext.put("userLogin", userLogin);
                txtContext.put("contentId", passedParams.get("txtContentId"));
                txtContext.put("ownerContentId", ftlContentId);
                txtContext.put("contentTypeId", "DOCUMENT");
                txtContext.put("statusId", passedParams.get("statusId"));
                txtContext.put("contentPurposeList", contentPurposeList);
                txtContext.put("targetOperationList",targetOperationList);
                txtContext.put("contentName", passedParams.get("contentName"));
                txtContext.put("description", passedParams.get("description"));
                txtContext.put("privilegeEnumId", passedParams.get("privilegeEnumId"));
                txtContext.put("dataResourceId", passedParams.get("txtDataResourceId"));
                txtContext.put("dataResourceTypeId", "ELECTRONIC_TEXT");
                txtContext.put("contentIdTo", ftlContentId);
                txtContext.put("contentAssocTypeId", "SUB_CONTENT");
                txtContext.put("textData", passedParams.get("textData"));
                txtContext.put("mapKey", "ARTICLE");
                txtContext.put("dataTemplateTypeId", "NONE");
                if (Debug.verboseOn()) Debug.logVerbose("[UploadContentAndImage]txtContext " + txtContext, module);
                Map txtResults = dispatcher.runSync("persistContentAndAssoc", txtContext);
                isError = ModelService.RESPOND_ERROR.equals(txtResults.get(ModelService.RESPONSE_MESSAGE));
                if (isError) {
                    request.setAttribute("_ERROR_MESSAGE_", txtResults.get(ModelService.ERROR_MESSAGE));
                        TransactionUtil.rollback();
                    return "error";
                }
            }

            // Create or update image subContent
            Map imgContext = new HashMap();
            if (imageBytes.length > 0) {
                imgContext.put("userLogin", userLogin);
                imgContext.put("contentId", passedParams.get("imgContentId"));
                imgContext.put("ownerContentId", ftlContentId);
                imgContext.put("contentTypeId", "DOCUMENT");
                imgContext.put("statusId", passedParams.get("statusId"));
                imgContext.put("contentName", passedParams.get("contentName"));
                imgContext.put("description", passedParams.get("description"));
                imgContext.put("contentPurposeList", contentPurposeList);
                imgContext.put("privilegeEnumId", passedParams.get("privilegeEnumId"));
                imgContext.put("targetOperationList",targetOperationList);
                imgContext.put("dataResourceId", passedParams.get("imgDataResourceId"));
                //String dataResourceTypeId = (String)passedParams.get("dataResourceTypeId");
                //if (UtilValidate.isEmpty(dataResourceTypeId))
                    //dataResourceTypeId = "IMAGE_OBJECT";
                String dataResourceTypeId = "IMAGE_OBJECT";
                imgContext.put("dataResourceTypeId", dataResourceTypeId);
                imgContext.put("contentIdTo", ftlContentId);
                imgContext.put("contentAssocTypeId", "SUB_CONTENT");
                imgContext.put("imageData", new ByteWrapper(imageBytes));
                imgContext.put("mapKey", "IMAGE");
                imgContext.put("dataTemplateTypeId", "NONE");
                String rootDir = request.getSession().getServletContext().getRealPath("/");
                imgContext.put("rootDir", "rootDir");
                if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]imgContext " + imgContext, module);
                Map imgResults = dispatcher.runSync("persistContentAndAssoc", imgContext);
                isError = ModelService.RESPOND_ERROR.equals(imgResults.get(ModelService.RESPONSE_MESSAGE));
                if (isError) {
                    request.setAttribute("_ERROR_MESSAGE_", imgResults.get(ModelService.ERROR_MESSAGE));
                    TransactionUtil.rollback();
                    return "error";
                }
            }
    
            // Check for existing AUTHOR link
            String userLoginId = userLogin.getString("userLoginId");
            List authorAssocList = delegator.findByAnd("ContentAssoc", UtilMisc.toMap("contentId", ftlContentId, "contentIdTo", userLoginId, "contentAssocTypeId", "AUTHOR"));
            List currentAuthorAssocList = EntityUtil.filterByDate(authorAssocList);
            //if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]currentAuthorAssocList " + currentAuthorAssocList, module);
            if (currentAuthorAssocList.size() == 0) {
                // Don't want to bother with permission checking on this association
                GenericValue authorAssoc = delegator.makeValue("ContentAssoc", null);
                authorAssoc.set("contentId", ftlContentId);
                authorAssoc.set("contentIdTo", userLoginId);
                authorAssoc.set("contentAssocTypeId", "AUTHOR");
                authorAssoc.set("fromDate", UtilDateTime.nowTimestamp());
                authorAssoc.set("createdByUserLogin", userLoginId);
                authorAssoc.set("lastModifiedByUserLogin", userLoginId);
                authorAssoc.set("createdDate", UtilDateTime.nowTimestamp());
                authorAssoc.set("lastModifiedDate", UtilDateTime.nowTimestamp());
                authorAssoc.create();
            }

            request.setAttribute("dataResourceId", ftlDataResourceId);
            request.setAttribute("drDataResourceId", ftlDataResourceId);
            request.setAttribute("contentId", ftlContentId);
            String newTrail = passedParams.get("nodeTrailCsv") + "," + ftlContentId;
            request.setAttribute("nodeTrailCsv", newTrail);
            //if (Debug.infoOn()) Debug.logInfo("[UploadContentAndImage]newTrail: " + newTrail, module);
            TransactionUtil.commit();
        } catch( Exception e) {
            Debug.logError(e, "[UploadContentAndImage] " , module);
            request.setAttribute("_ERROR_MESSAGE_", e.getMessage());
            try {
                    TransactionUtil.rollback();
            } catch(GenericTransactionException e2) {
            request.setAttribute("_ERROR_MESSAGE_", e2.getMessage());
            return "error";
            }
            return "error";
        }
        return "success";
    }

} // end of UploadContentAndImage
