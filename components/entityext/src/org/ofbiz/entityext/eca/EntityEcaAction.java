/*
 * $Id$
 *
 * Copyright (c) 2002-2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.entityext.eca;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.w3c.dom.Element;

/**
 * EntityEcaAction
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      2.1
 */
public class EntityEcaAction {
    public static final String module = EntityEcaAction.class.getName();
    
    protected String serviceName;
    protected String serviceMode;
    protected boolean resultToValue;
    protected boolean abortOnError;
    protected boolean rollbackOnError;
    protected boolean persist;
    protected String valueAttr;

    protected EntityEcaAction() {}

    public EntityEcaAction(Element action) {
        this.serviceName = action.getAttribute("service");
        this.serviceMode = action.getAttribute("mode");
        // default is true, so anything but false is true
        this.resultToValue = !"false".equals(action.getAttribute("result-to-value"));
        // default is false, so anything but true is false
        this.abortOnError = "true".equals(action.getAttribute("abort-on-error"));
        this.rollbackOnError = "true".equals(action.getAttribute("rollback-on-error"));
        this.persist = "true".equals(action.getAttribute("persist"));
        this.valueAttr = action.getAttribute("value-attr");
    }

    public void runAction(DispatchContext dctx, GenericEntity value) throws GenericEntityException {
        Map actionResult = null;
        
        try {
            // pull out context parameters needed for this service.
            Map actionContext = dctx.getModelService(serviceName).makeValid(value, ModelService.IN_PARAM);
            // if value-attr is specified, insert the value object in that attr name
            if (valueAttr != null && valueAttr.length() > 0) {
                actionContext.put(valueAttr, value);
            }
            
            //Debug.logInfo("Running Entity ECA action service " + this.serviceName + " triggered by entity: " + value.getEntityName(), module);
            //Debug.logInfo("Running Entity ECA action service " + this.serviceName + "; value=" + value + "; actionContext=" + actionContext, module);
        
            LocalDispatcher dispatcher = dctx.getDispatcher();
            if ("sync".equals(this.serviceMode)) {
                actionResult = dispatcher.runSync(this.serviceName, actionContext);
            } else if ("async".equals(this.serviceMode)) {
                dispatcher.runAsync(serviceName, actionContext, persist);
            }
        } catch (GenericServiceException e) {
            // check abortOnError and rollbackOnError
            if (rollbackOnError) {
                Debug.logError("Entity ECA action service failed and rollback-on-error is true, so setting rollback only.", module);
                TransactionUtil.setRollbackOnly();
            }

            if (this.abortOnError) {
                throw new EntityEcaException("Error running Entity ECA action service", e);
            } else {
                Debug.logError(e, "Error running Entity ECA action service", module);
            }
        }

        // use the result to update the context fields.
        if (resultToValue) {
            value.setNonPKFields(actionResult);
        }
    }
}
