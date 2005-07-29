/*
 * $Id$
 *
 * Copyright (c) 2003, 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.manufacturing.mrp;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * InventoryEventPlannedServices - InventoryEventPlanned related Services
 *
 * @author     <a href="mailto:olivier.heintz@nereide.biz">Olivier Heintz</a>
 * @version    $Rev$
 * @since      3.0
 */
public class InventoryEventPlannedServices {
    
    public static final String module = InventoryEventPlannedServices.class.getName();
   
    /**
     *
     *  Create an InventoryEventPlanned.
     *  Make an update if a record exist with same key,  (adding the eventQuantity to the exiting record)
     *
     * @param ctx
     * @param context: a map containing the parameters used to create an InventoryEventPlanned (see the servcie definition)
     * @return result: a map with service status
     */
    public static Map createInventoryEventPlanned(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        // No permission checking because this services is call from other services/
        Map parameters = UtilMisc.toMap("productId", context.get("productId"),
                                        "eventDate", context.get("eventDate"),
                                        "inventoryEventPlanTypeId",context.get("inventoryEventPlanTypeId"));
        Double quantity = (Double)context.get("eventQuantity");
        GenericValue inventoryEventPlanned = null;
        try {
            createOrUpdateInventoryEventPlanned(parameters, quantity, delegator);
        } catch (GenericEntityException e) {
            Debug.logError(e,"Error : delegator.findByPrimaryKey(\"InventoryEventPlanned\", parameters =)"+parameters, module);
            return ServiceUtil.returnError("Problem, on database access, for more detail look at the log");
        }
        return ServiceUtil.returnSuccess();
    }

    public static void createOrUpdateInventoryEventPlanned(Map inventoryEventPlannedKeyMap, Double newQuantity, GenericDelegator delegator) throws GenericEntityException {
        GenericValue inventoryEventPlanned = null;
        inventoryEventPlanned = delegator.findByPrimaryKey("InventoryEventPlanned", inventoryEventPlannedKeyMap);
        if (inventoryEventPlanned == null) {
            inventoryEventPlanned = delegator.makeValue("InventoryEventPlanned", inventoryEventPlannedKeyMap);
            inventoryEventPlanned.put("eventQuantity", newQuantity);
            inventoryEventPlanned.create();
        } else {
            double qties = newQuantity.doubleValue() + ((Double)inventoryEventPlanned.get("eventQuantity")).doubleValue();
            inventoryEventPlanned.put("eventQuantity", new Double(qties));
            inventoryEventPlanned.store();
        }
    }

}
