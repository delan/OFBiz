/*
 * $Id$
 *
 * Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.core.service.eca;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.service.DispatchContext;
import org.ofbiz.core.service.GenericServiceException;
import org.ofbiz.core.service.LocalDispatcher;
import org.ofbiz.core.service.ModelService;
import org.ofbiz.core.util.UtilValidate;
import org.w3c.dom.Element;

/**
 * ServiceEcaAction
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
 * @since      2.0
 */
public class ServiceEcaAction {

    protected String serviceName;
    protected String serviceMode;
    protected String resultMapName;
    protected boolean resultToContext;
    protected boolean ignoreError;
    protected boolean persist;

    protected ServiceEcaAction() {}

    public ServiceEcaAction(Element action) {
        this.serviceName = action.getAttribute("service");
        this.serviceMode = action.getAttribute("mode");
        this.resultMapName = action.getAttribute("result-map-name");
        // default is true, so anything but false is true
        this.resultToContext = !"false".equals(action.getAttribute("result-to-context"));
        this.ignoreError = !"false".equals(action.getAttribute("ignore-error"));
        this.persist = "true".equals(action.getAttribute("persist"));
    }

    public void runAction(String selfService, DispatchContext dctx, Map context, Map result) throws GenericServiceException {
        if (this.serviceName.equals(selfService)) {
            throw new GenericServiceException("Cannot invoke self on ECA.");
        }

        // pull out context parameters needed for this service.
        Map actionContext = dctx.getModelService(serviceName).makeValid(context, ModelService.IN_PARAM);
        Map actionResult = null;
        LocalDispatcher dispatcher = dctx.getDispatcher();

        if (serviceMode.equals("sync")) {
            actionResult = dispatcher.runSync(this.serviceName, actionContext);
        } else if (serviceMode.equals("async")) {
            dispatcher.runAsync(serviceName, actionContext, persist);
        }

        // put the results in to the defined map
        if (resultMapName != null && resultMapName.length() > 0) {
            Map resultMap = (Map) context.get(resultMapName);
            if (resultMap == null) {
                resultMap = new HashMap();
            }
            resultMap.putAll(dctx.getModelService(this.serviceName).makeValid(actionResult, ModelService.OUT_PARAM));
            context.put(resultMapName, resultMap);
        }
        
        // use the result to update the context fields.
        if (resultToContext) {
            context.putAll(dctx.getModelService(this.serviceName).makeValid(actionResult, ModelService.OUT_PARAM));
        }

        // if we aren't ignoring errors check it here...
        if (!ignoreError && result != null) {
            if (ModelService.RESPOND_ERROR.equals(actionResult.get(ModelService.RESPONSE_MESSAGE))) {
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
                String errorMessage = (String) actionResult.get(ModelService.ERROR_MESSAGE);
                List errorMessageList = (List) actionResult.get(ModelService.ERROR_MESSAGE_LIST);
                Map errorMessageMap = (Map) actionResult.get(ModelService.ERROR_MESSAGE_MAP);

                // do something with the errorMessage
                if (UtilValidate.isNotEmpty(errorMessage)) {
                    if (UtilValidate.isEmpty((String) result.get(ModelService.ERROR_MESSAGE))) {
                        result.put(ModelService.ERROR_MESSAGE, errorMessage);
                    } else {
                        if (errorMessageList == null) errorMessageList = new LinkedList();
                        errorMessageList.add(0, errorMessage);
                    }
                }
                // do something with the errorMessageList
                if (errorMessageList != null && errorMessageList.size() > 0) {
                    List origErrorMessageList = (List) result.get(ModelService.ERROR_MESSAGE_LIST);
                    if (origErrorMessageList == null) {
                        result.put(ModelService.ERROR_MESSAGE_LIST, errorMessageList);
                    } else {
                        origErrorMessageList.addAll(errorMessageList);
                    }
                }
                // do something with the errorMessageMap
                if (errorMessageMap != null && errorMessageMap.size() > 0) {
                    Map origErrorMessageMap = (Map) result.get(ModelService.ERROR_MESSAGE_MAP);
                    if (origErrorMessageMap == null) {
                        result.put(ModelService.ERROR_MESSAGE_MAP, errorMessageMap);
                    } else {
                        origErrorMessageMap.putAll(errorMessageMap);
                    }
                }
            }
        }
    }
}
