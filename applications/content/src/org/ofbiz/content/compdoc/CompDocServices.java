package org.ofbiz.content.compdoc;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.event.CoreEvents;
import org.ofbiz.service.GenericServiceException;


/**
 * CompDocEvents Class
 *
 * @author     <a href="mailto:byersa@automationgroups.com">Al Byers</a>
 * @version    $Rev: 5462 $
 * @since      3.0
 *
 * 
 */

public class CompDocServices {

    public static final String module = CompDocServices.class.getName();
    
    /** 
     * 
     * @param request
     * @param response
     * @return
     * 
     * Creates the topmost Content entity of a Composite Document tree.
     * Also creates an "empty" Composite Document Instance Content entity.
     * Creates ContentRevision/Item records for each, as well.
     */

    public static Map persistRootCompDoc(DispatchContext dctx, Map context) {
        
        Map result = new HashMap();
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale)context.get("locale");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        String contentId = (String)context.get("contentId");
        String instanceContentId = null;
        
        boolean contentExists = true;
        if (UtilValidate.isEmpty(contentId)) {
            contentExists = false;
        } else {
            try {
                GenericValue val = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
                if (val == null)  contentExists = false;
            } catch(GenericEntityException e) {
                Debug.logError(e, "Error running serviceName persistContentAndAssoc", module);
                String errMsg = UtilProperties.getMessage(CoreEvents.err_resource, "coreEvents.error_modelservice_for_srv_name", locale);
                return ServiceUtil.returnError(errMsg);
           }
        }
        
        ModelService modelService = null;
        try {
            modelService = dispatcher.getDispatchContext().getModelService("persistContentAndAssoc");
        } catch (GenericServiceException e) {
            String errMsg = "Error getting model service for serviceName, 'persistContentAndAssoc'. " + e.getMessage();
            Debug.logError(errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        Map persistMap = modelService.makeValid(context, ModelService.IN_PARAM);
        persistMap.put("userLogin", userLogin);
        try {
            Map persistResult = dispatcher.runSync("persistContentAndAssoc", persistMap);
            contentId = (String)persistResult.get("contentId");
            result.putAll(persistResult);
            //request.setAttribute("contentId", contentId);
            // Update ContentRevision and ContentRevisonItem
            Map contentRevisionMap = new HashMap();
            contentRevisionMap.put("itemContentId", contentId);
            contentRevisionMap.put("contentId", contentId);
            contentRevisionMap.put("userLogin", userLogin);
            persistResult = dispatcher.runSync("persistContentRevisionAndItem", contentRevisionMap);
            result.putAll(persistResult);
            String errorMsg = ServiceUtil.getErrorMessage(result);
            if (UtilValidate.isNotEmpty(errorMsg)) {
                String errMsg = "Error running serviceName, 'persistContentRevisionAndItem'. " + errorMsg;
                Debug.logError(errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
            
        } catch(GenericServiceException e) {
            String errMsg = "Error running serviceName, 'persistContentAndAssoc'. " + e.getMessage();
            Debug.logError(errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
        return result;
    }
    
}
