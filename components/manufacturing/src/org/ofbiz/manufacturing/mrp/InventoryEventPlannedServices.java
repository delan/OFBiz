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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
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
     * Transform parameters used to created a requirement to parameters needed to created an InventoryEventPlanned.
     * This service is call by ECA to create an InventoryEventPlanned when a proposed Order is created.
     *
     * @param ctx
     * @param context: a map containing the parameters used to create an requirement (see the servcie definition)
     * @return result: a map containing the parameters needed for the InventoryEventPlannedCreate services
     */
    public static Map prepParamFromRequirement(DispatchContext ctx, Map context) {
        // No permission checking because this services is call from other services/
        Map result = new HashMap();
        
        result.put("productId", context.get("productId"));
        result.put("eventDate", context.get("requiredByDate"));
        if (context.get("requirementTypeId").equals("MRP_PRO_PROD_ORDER"))
            result.put("inventoryEventPlanTypeId","PROP_MANUF_O_RECP");
        else if (context.get("requirementTypeId").equals("MRP_PRO_PURCH_ORDER"))
            result.put("inventoryEventPlanTypeId","PROP_PUR_O_RECP");
        else {
            Debug.logError("Error : createInventoryEventPlanned from Requirement with requirementTypeId = "+context.get("requirementTypeId"), module);
            return ServiceUtil.returnError("Error createInventoryEventPlanned from Requirement, invalid parameters");
        }
        result.put("eventQuantity", context.get("quantity"));
        return result;
    }
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

    /*
    public static Map createInventoryEventPlanned(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        // No permission checking because this services is call from other services/
        Map parameters = UtilMisc.toMap("productId", context.get("productId"),
                                        "eventDate", context.get("eventDate"),
                                        "inventoryEventPlanTypeId",context.get("inventoryEventPlanTypeId"));
        GenericValue inventoryEventPlanned = null;
        // test if a record exist with same key
        try {
            inventoryEventPlanned = delegator.findByPrimaryKey("InventoryEventPlanned", parameters);
        } catch (GenericEntityException e) {
            Debug.logError(e,"Error : delegator.findByPrimaryKey(\"InventoryEventPlanned\", parameters =)"+parameters, module);
            return ServiceUtil.returnError("Problem, on database access, for more detail look at the log");
        }
        if(inventoryEventPlanned==null){  // record creation
            try {
                inventoryEventPlanned = delegator.makeValue("InventoryEventPlanned",parameters);
                inventoryEventPlanned.put("eventQuantity", context.get("eventQuantity"));
                inventoryEventPlanned.create();
            } catch (GenericEntityException e) {
                Debug.logError(e,"Error : InventoryEventPlanned.create() parameters = "+inventoryEventPlanned, module);
                return ServiceUtil.returnError("Problem, we can not create a record in InventoryEventPlanned, for more detail look at the log");
            }
        }
        else{  // adding the eventQuantity to the exiting record) and update the record
            double quantity = ((Double) context.get("eventQuantity")).doubleValue() + inventoryEventPlanned.getDouble("eventQuantity").doubleValue();
            inventoryEventPlanned.put("eventQuantity", new Double(quantity));
            try {
                inventoryEventPlanned.store();
            } catch (GenericEntityException e) {
                Debug.logError(e,"Error : InventoryEventPlanned.store() parameters = "+inventoryEventPlanned, module);
                return ServiceUtil.returnError("Problem, cannot update InventoryEventPlanned, for more detail look at the log");
            }
        }
        return ServiceUtil.returnSuccess();
    }
     */
}
