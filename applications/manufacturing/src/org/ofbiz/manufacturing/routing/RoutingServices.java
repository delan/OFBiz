/*
 * Copyright (c) 2001-2004, 2003 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.manufacturing.routing;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import org.ofbiz.manufacturing.jobshopmgt.ProductionRun;

/**
 * Routing related services
 *
 * @author     <a href="mailto:tiz@sastau.it">Jacopo Cappellato</a>
 * @version    $Rev$
 * @since      3.0
 */
public class RoutingServices {
    
    public static final String module = RoutingServices.class.getName();
    public static final String resource = "ManufacturingUiLabels";
    
    /**
     * Computes the estimated time needed to perform the task.
     * @param ctx The DispatchContext that this service is operating in.
     * @param context Map containing the input parameters.
     * @return Map with the result of the service, the output parameters.
     */
    public static Map getEstimatedTaskTime(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        // The mandatory IN parameters
        String taskId = (String) context.get("taskId");
        Double quantity = (Double) context.get("quantity");
        // The optional IN parameters
        String productId = (String) context.get("productId");
        String routingId = (String) context.get("routingId");
        
        if (quantity == null) {
            quantity = new Double(1);
        }

        GenericValue task = null;
        try {
            task = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", taskId));
        } catch(GenericEntityException gee) {
            ServiceUtil.returnError("Error finding routing task with id: " + taskId);
        }
        // FIXME: the ProductionRun.getEstimatedTaskTime(...) method will be removed and
        // its logic will be implemented inside this method.
        long estimatedTaskTime = ProductionRun.getEstimatedTaskTime(task, quantity, productId, routingId, dispatcher);
        result.put("estimatedTaskTime", new Long(estimatedTaskTime));

        return result;
    }
}
