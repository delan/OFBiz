/*
 * Created on Jan 28, 2005
 *
 * This service persists a blog article.
 * It can persist text only, image only or a combination of content types.
 * If text or image only, that content is attached directly to the main
 * content as an ElectronicText or ImageDataResource entity.
 * If a combination is desired, the two content pieces are associated
 * through a predefined screen widget template.
 */
package org.ofbiz.community;

/**
 * @author Al Byers (byersa@automationgroups.com)
 *
 */
    
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class CommunityServices {

    public static Map persistBlogAll(DispatchContext dctx, Map context) throws GenericServiceException {

        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        ModelService persistContent = dispatcher.getDispatchContext().getModelService("persistContentAndAssoc");
        Map ctx = persistContent.makeValid(context, "IN");
        String drMimeTypeId_TEXT = (String)context.get("drMimeTypeId_TEXT");
        boolean bTextMimeType = "Y".equals(drMimeTypeId_TEXT);
        String drMimeTypeId_IMAGE = (String)context.get("drMimeTypeId_IMAGE");
        boolean bImageMimeType = "Y".equals(drMimeTypeId_IMAGE);
        String textData = (String)context.get("textData");
        
        // if the content already exists, these will identify it.
        // They are stored in the form
        String textContentId = (String)context.get("textContentId");
        String imageContentId = (String)context.get("imageContentId");
        String textDataResourceId = (String)context.get("textDataResourceId");
        String imageDataResourceId = (String)context.get("imageDataResourceId");
        
        String dataResourceId = (String)context.get("dataResourceId");
        String drDataResourceTypeId = (String)context.get("drDataResourceTypeId");
        String drDataTemplateTypeId = (String)context.get("drDataTemplateTypeId");
        String drMimeTypeId = (String)context.get("drMimeTypeId");
        
        String templateId = (String)context.get("templateId");
        String thisDataResourceId = dataResourceId;
        
        if ("SCREEN_COMBINED".equals(drDataTemplateTypeId) && !bImageMimeType) {
            if (bTextMimeType) {
                ctx.put("dataResourceId", textDataResourceId);                
                ctx.put("drDataResourceId", textDataResourceId);                
                ctx.put("drDataResourceTypeId", "ELECTRONIC_TEXT");
                ctx.put("drDataTemplateTypeId", "NONE");
                ctx.put("drMimeTypeId", "text/html");
            }
        } else if ("SCREEN_COMBINED".equals(drDataTemplateTypeId) && !bTextMimeType) {
            if (bImageMimeType) {
                ctx.put("dataResourceId", imageDataResourceId);                
                ctx.put("drDataResourceId", imageDataResourceId);                
                ctx.put("drDataResourceTypeId", "IMAGE_OBJECT");
                ctx.put("drDataTemplateTypeId", "NONE");
                ctx.put("drMimeTypeId", "image/jpeg");
            }
        } else if (!"SCREEN_COMBINED".equals(drDataTemplateTypeId) ) {
        
            // Store the main record
            // either text or image or both
            if (bTextMimeType && !bImageMimeType) {
                ctx.put("dataResourceId", textDataResourceId);                
                ctx.put("drDataResourceId", textDataResourceId);                
                ctx.put("drDataResourceTypeId", "ELECTRONIC_TEXT");
                ctx.put("drDataTemplateTypeId", "NONE");
                ctx.put("drMimeTypeId", "text/html");
            } else if (!bTextMimeType && bImageMimeType) {
                ctx.put("dataResourceId", imageDataResourceId);                
                ctx.put("drDataResourceId", imageDataResourceId);                
                ctx.put("drDataResourceTypeId", "IMAGE_OBJECT");
                ctx.put("drDataTemplateTypeId", "NONE");
                ctx.put("drMimeTypeId", "image/jpeg");
            } else if (bTextMimeType && bImageMimeType) {
                // if both then set up for and choose a template
                ctx.put("drDataResourceTypeId", null);
                ctx.put("dataResourceTypeId", null);
                ctx.put("textData", null);
                ctx.put("dataResourceId", templateId);                
                ctx.put("drDataResourceId", templateId);                
             
            }
        } else if ("SCREEN_COMBINED".equals(drDataTemplateTypeId) ) {
            if (bTextMimeType && bImageMimeType) {
                // if both then set up for and choose a template
                ctx.put("drDataResourceTypeId", "ELECTRONIC_TEXT");
                ctx.put("drDataTemplateTypeId", "SCREEN_COMBINED");
                ctx.put("drMimeTypeId", "text/html");
                ctx.put("textData", null);
                ctx.put("dataResourceId", templateId);                
                ctx.put("drDataResourceId", templateId);                
             
            }
        }
        // store the main content entity
        result = dispatcher.runSync("persistContentAndAssoc", ctx);
        if (ServiceUtil.isError(result)) {
            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(result));   
        }
        String mainContentId = (String)result.get("contentId");
        
        // If both, then that content needs to be persisted
        if (bTextMimeType && bImageMimeType) {
        
            // first store the text
            ctx.put("caContentId", textContentId);
            ctx.put("contentId", textContentId);
            ctx.put("dataResourceId", textDataResourceId);
            ctx.put("drDataResourceId", textDataResourceId);
            ctx.put("contentIdTo", mainContentId);
            ctx.put("caContentIdTo", mainContentId);
            ctx.put("drDataTemplateTypeId", "NONE");
            ctx.put("caContentAssocTypeId", "SUB_CONTENT");
            ctx.put("drDataResourceTypeId", "ELECTRONIC_TEXT");
            ctx.put("drMimeTypeId", "text/html");
            ctx.put("caMapKey", "MAIN");
            ctx.put("textData", textData);
            Map textResult = dispatcher.runSync("persistContentAndAssoc", ctx);
            if (ServiceUtil.isError(textResult)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(textResult));   
            }
            textContentId = (String)textResult.get("contentId");
            textDataResourceId = (String)textResult.get("drDataResourceId");
            
            
            // persist the image
            String imageData_fileName = (String)context.get("_imageData_fileName");
            // In the update mode, if no upload file is given, do not upload
            if (UtilValidate.isNotEmpty(imageData_fileName)) {
                ctx.put("caContentId", imageContentId);
                ctx.put("contentId", imageContentId);
                ctx.put("dataResourceId", imageDataResourceId);
                ctx.put("drDataResourceId", imageDataResourceId);
                ctx.put("drDataResourceTypeId", "IMAGE_OBJECT");
                ctx.put("drMimeTypeId", "image/jpeg");
                ctx.put("caMapKey", "IMAGE");
                ctx.put("textData", null);
                Map imageResult = dispatcher.runSync("persistContentAndAssoc", ctx);
                if (ServiceUtil.isError(imageResult)) {
                	return ServiceUtil.returnError(ServiceUtil.getErrorMessage(imageResult));   
                }
                imageContentId = (String)imageResult.get("contentId");
                imageDataResourceId = (String)imageResult.get("drDataResourceId");
            }
        }
        
        // persist the summary info. 
        String summaryData = (String)context.get("summaryData");
        String summaryContentIdTo = mainContentId;
        if (UtilValidate.isNotEmpty(summaryData) && UtilValidate.isNotEmpty(summaryContentIdTo) ) {
            Map subContentIn = new HashMap();
            subContentIn.put("contentId", summaryContentIdTo);
            subContentIn.put("mapKey", "SUMMARY");
            Map thisResult = dispatcher.runSync("getSubContent", subContentIn);
            GenericValue view = (GenericValue)thisResult.get("view");
            Map summaryContext = null;
            if (view != null) {
            	summaryContext = persistContent.makeValid(view, "IN");
            	summaryContext.put("textData", summaryData);
            	summaryContext.put("contentTypeId", null);
            	summaryContext.put("contentAssocTypeId", null);
            } else {
            	summaryContext = persistContent.makeValid(result, "IN");
            	summaryContext.put("textData", summaryData);
            	summaryContext.put("caContentIdTo", summaryContentIdTo);
                summaryContext.put("mapKey", "SUMMARY");
                summaryContext.put("caContentAssocTypeId", "SUMMARY");
                summaryContext.put("contentAssocTypeId", "SUMMARY");
                summaryContext.put("contentTypeId", "DOCUMENT");
                summaryContext.put("dataResourceTypeId", "ELECTRONIC_TEXT");
            	summaryContext.put("contentId", null);
            	summaryContext.put("caFromDate", null);
            	summaryContext.put("fromDate", null);
            	summaryContext.put("drDataResourceId", null);
            	summaryContext.put("dataResourceId", null);
            }
          	summaryContext.put("userLogin", userLogin);
            Map summaryResult = dispatcher.runSync("persistContentAndAssoc", summaryContext);
            if (ServiceUtil.isError(summaryResult)) {
                String errMsg =  ServiceUtil.getErrorMessage(summaryResult);
                return ServiceUtil.returnError(errMsg);
            }
        }
        
        return result;
    }
}
