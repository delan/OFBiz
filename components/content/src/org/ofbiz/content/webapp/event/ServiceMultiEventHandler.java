/*
 * $Id$
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.content.webapp.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceValidationException;

/**
 * ServiceMultiEventHandler - Event handler for running a service multiple times; for bulk forms
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      2.2
 */
public class ServiceMultiEventHandler implements EventHandler {
    
    public static final String module = ServiceMultiEventHandler.class.getName();
    
    public static final String DELIMITER = "_o_";
    public static final String SYNC = "sync";
    public static final String ASYNC = "async";

    /**
     * @see org.ofbiz.content.webapp.event.EventHandler#invoke(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public String invoke(String eventPath, String eventMethod, HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        // make sure we have a valid reference to the Service Engine
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null) {
            throw new EventHandlerException("The local service dispatcher is null");
        }
        DispatchContext dctx = dispatcher.getDispatchContext();
        if (dctx == null) {
            throw new EventHandlerException("Dispatch context cannot be found");
        }        
        
        // get the details for the service(s) to call
        String mode = SYNC;
        String serviceName = null;

        if (eventPath == null || eventPath.length() == 0) {
            mode = SYNC;
        } else {
            mode = eventPath;
        }
        
        // we only support SYNC mode in this handler
        if (mode != SYNC) {
            throw new EventHandlerException("Async mode is not supported");
        }
        
        // nake sure we have a defined service to call
        serviceName = eventMethod;                       
        if (serviceName == null) {
            throw new EventHandlerException("Service name (eventMethod) cannot be null");
        }
        if (Debug.verboseOn()) Debug.logVerbose("[Set mode/service]: " + mode + "/" + serviceName, module);
                
        // some needed info for when running the service
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        
        // get the service model to generate context(s)
        ModelService model = null;

        try {
            model = dctx.getModelService(serviceName);
        } catch (GenericServiceException e) {
            throw new EventHandlerException("Problems getting the service model", e);
        }

        if (model == null) {
            throw new EventHandlerException("Problems getting the service model");
        }

        if (Debug.verboseOn()) Debug.logVerbose("[Processing]: SERVICE Event", module);
        if (Debug.verboseOn()) Debug.logVerbose("[Using delegator]: " + dispatcher.getDelegator().getDelegatorName(), module);
        
        // check if we are using per row submit
        boolean useRowSubmit = request.getParameter("_useRowSubmit") == null ? false : 
                "Y".equalsIgnoreCase(request.getParameter("_useRowSubmit"));
        
        // check if we are to also look in a global scope (no delimiter)        
        boolean checkGlobalScope = request.getParameter("_checkGlobalScope") == null ? false :
                "Y".equalsIgnoreCase(request.getParameter("_checkGlobalScope"));
        
        // get the number of rows
        String rowCountField = request.getParameter("_rowCount");
        if (rowCountField == null) {
            throw new EventHandlerException("Required field _rowCount is missing");
        }
        
        int rowCount = 0; // parsed int value
        try {
            rowCount = Integer.parseInt(rowCountField);    
        } catch (NumberFormatException e) {
            throw new EventHandlerException("Invalid value for _rowCount");
        }
        if (rowCount < 1) {
            throw new EventHandlerException("No rows to process");
        }
        
        // some default message settings        
        String errorPrefixStr = UtilProperties.getMessage("DefaultMessages", "service.error.prefix", locale);
        String errorSuffixStr = UtilProperties.getMessage("DefaultMessages", "service.error.suffix", locale);
        String messagePrefixStr = UtilProperties.getMessage("DefaultMessages", "service.message.prefix", locale);
        String messageSuffixStr = UtilProperties.getMessage("DefaultMessages", "service.message.suffix", locale);        
                
        // prepare the error message list
        List errorMessages = new ArrayList();
        
        // start the transaction
        boolean beganTrans = false;
        try {
            beganTrans = TransactionUtil.begin();
        } catch (GenericTransactionException e) {
            throw new EventHandlerException("Problem starting transaction", e);            
        }
        
        // now loop throw the rows and prepare/invoke the service for each
        for (int i = 0; i < rowCount; i++) {
            String thisSuffix = DELIMITER + i;
            boolean rowSelected = request.getParameter("_rowSubmit" + thisSuffix) == null ? false :
                    "Y".equalsIgnoreCase(request.getParameter("_rowSubmit" + thisSuffix));
            
            // make sure we are to process this row
            if (useRowSubmit && !rowSelected) {            
                continue;
            }
            
            // build the context
            Map serviceContext = new HashMap();
            Iterator modelParmInIter = model.getInModelParamList().iterator();
            while (modelParmInIter.hasNext()) {
                ModelParam modelParam = (ModelParam) modelParmInIter.next();
                String name = (String) modelParam.name;

                // don't include userLogin, that's taken care of below
                if ("userLogin".equals(name)) continue;
                // don't include locale, that is also taken care of below
                if ("locale".equals(name)) continue;

                Object value = null;
                if (modelParam.stringMapPrefix != null && modelParam.stringMapPrefix.length() > 0) {
                    Map paramMap = UtilHttp.makeParamMapWithPrefix(request, modelParam.stringMapPrefix, thisSuffix);
                    value = paramMap;
                } else if (modelParam.stringListSuffix != null && modelParam.stringListSuffix.length() > 0) {
                    List paramList = UtilHttp.makeParamListWithSuffix(request, modelParam.stringListSuffix, null);
                    value = paramList;
                } else {
                    value = request.getParameter(name + thisSuffix);
    
                    // if the parameter wasn't passed and no other value found, don't pass on the null
                    if (value == null) {
                        value = request.getAttribute(name + thisSuffix);
                    } 
                    if (value == null) {
                        value = request.getSession().getAttribute(name + thisSuffix);
                    }
                    
                    // now check global scope
                    if (value == null) {
                        if (checkGlobalScope) {
                            value = request.getParameter(name);
                            if (value == null) {
                                value = request.getAttribute(name);                                
                            }
                            if (value == null) {
                                value = request.getSession().getAttribute(name);
                            }
                        }
                    }
                    
                    if (value == null) {
                        // still null, give up for this one
                        continue;
                    }
                
                    if (value instanceof String && ((String) value).length() == 0) {
                        // interpreting empty fields as null values for each in back end handling...
                        value = null;
                    }
                }
                // set even if null so that values will get nulled in the db later on
                serviceContext.put(name, value);
            }  
            
            // get only the parameters for this service - converted to proper type
            serviceContext = model.makeValid(serviceContext, ModelService.IN_PARAM);  

            // include the UserLogin value object
            if (userLogin != null) {             
                serviceContext.put("userLogin", userLogin);
            }        
        
            // include the Locale object
            if (locale != null) {            
                serviceContext.put("locale", locale);
            }
            
            // invoke the service
            Map result = null;
            try {                
                result = dispatcher.runSync(serviceName, serviceContext);
            } catch (ServiceAuthException e) {
                // not logging since the service engine already did
                errorMessages.add(messagePrefixStr + "Service invocation error on row (" + i +"): " + e.getNonNestedMessage());
            } catch (ServiceValidationException e) {
                // not logging since the service engine already did
                request.setAttribute("serviceValidationException", e);
                errorMessages.add(messagePrefixStr + "Service invocation error on row (" + i +"): " + e.getNonNestedMessage());
            } catch (GenericServiceException e) {
                Debug.logError(e, "Service invocation error", module);
                errorMessages.add(messagePrefixStr + "Service invocation error on row (" + i +"): " + e.getNested() + messageSuffixStr);                             
            } 
            
            // check for an error message
            String errorMessage = ServiceUtil.makeErrorMessage(result, messagePrefixStr, messageSuffixStr, "", "");
            if (UtilValidate.isNotEmpty(errorMessage)) {
                errorMessages.add(errorMessage);           
            }
       
        }
             
        if (errorMessages.size() > 0) {
            // rollback the transaction
            try {
                TransactionUtil.rollback(beganTrans);
            } catch (GenericTransactionException e) {
                Debug.logError(e, "Could not rollback transaction", module);
            }            
            errorMessages.add(0, errorPrefixStr);
            errorMessages.add(errorSuffixStr);
            StringBuffer errorBuf = new StringBuffer();
            Iterator ei = errorMessages.iterator();
            while (ei.hasNext()) {
                String em = (String) ei.next();
                errorBuf.append(em + "\n");
            }
            request.setAttribute("_ERROR_MESSAGE_", errorBuf.toString());            
            return "error";            
        } else {  
            // commit the transaction
            try {
                TransactionUtil.commit(beganTrans);
            } catch (GenericTransactionException e) {
                Debug.logError(e, "Could not commit transaction", module);      
                throw new EventHandlerException("Commit transaction failed");                              
            }                                                                 
            return "success";
        }
    }        
}
