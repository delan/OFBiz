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


import java.util.*;
import org.w3c.dom.*;

import org.ofbiz.core.util.*;
import org.ofbiz.core.service.*;


/**
 * EventAction
 *
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    Jul 27, 2002
 * @version    1.0
 */
public class EventAction {

    protected String serviceName;
    protected String serviceMode;
    protected boolean updateContext;
    protected boolean ignoreError;

    protected EventAction() {}

    public EventAction(Element action) {
        this.serviceName = action.getAttribute("service");
        this.serviceMode = action.getAttribute("mode");
        // default is true, so anything but false is true
        this.updateContext = !"false".equals(action.getAttribute("update-context"));
        this.ignoreError = !"false".equals(action.getAttribute("ignore-error"));
    }

    public void runAction(String selfService, DispatchContext dctx, Map context, Map result) throws GenericServiceException {
        if (this.serviceName.equals(selfService)) {
            throw new GenericServiceException("Cannot invoke self on ECA.");
        }

        Map newContext = new HashMap(context);

        // pull out context parameters needed for this service.
        Map actionContext = dctx.getModelService(serviceName).makeValid(context, ModelService.IN_PARAM);

        Map actionResult = null;
        LocalDispatcher dispatcher = dctx.getDispatcher();

        if (serviceMode.equals("sync")) {
            actionResult = dispatcher.runSync(this.serviceName, actionContext);
        } else if (serviceMode.equals("async")) {
            dispatcher.runAsync(serviceName, context);
        }

        // use the result to update the context fields.
        if (updateContext) {
            context.putAll(dctx.getModelService(selfService).makeValid(actionResult, ModelService.IN_PARAM));
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
