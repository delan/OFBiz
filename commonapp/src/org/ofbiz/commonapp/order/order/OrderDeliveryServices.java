/*
 * $Id$
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.commonapp.order.order;

import java.io.*;
import java.net.*;
import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.ftl.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.util.*;
import org.ofbiz.commonapp.common.*;
import org.ofbiz.commonapp.party.contact.*;
import org.ofbiz.commonapp.product.catalog.*;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.WrappingTemplateModel;

/**
 * Purchase Order Delivery and Schedule Processing Services
 *
 * @author     Tristan Austin (tristana@twibble.org)
 * @version    $Revision$
 */
public class OrderDeliveryServices {
    public static final String module = OrderDeliveryServices.class.getName();
    public static final String resource = "org.ofbiz.commonapp.order.order.PackageMessages";

    /** Service for creating a new purchase order schedule*/
    public static Map createSchedule(DispatchContext ctx, Map context) {
        return storeSchedule(ctx, context, true);
    }

    /** Service for updating an existing purchase order schedule*/
    public static Map updateSchedule(DispatchContext ctx, Map context) {
       return storeSchedule(ctx, context, false);
    }

    /** Service for creating a new purchase order schedule*/
    private static Map storeSchedule(DispatchContext ctx, Map context, boolean doCreation) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Security security = ctx.getSecurity();
        Locale locale = (Locale) context.get("locale");

        GenericValue userLogin = (GenericValue) context.get("userLogin");
        // check security
        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "ORDERMGR", "_CREATE");

        if (result.size() > 0) {
            return result;
        }

        // get the order type
        String orderTypeId = (String) context.get("orderTypeId");
        
        // create the schedule object
        Object orderId = context.get("orderId");
        Object shipmentMethodTypeId= context.get("shipmentMethodTypeId");
        Object trackingNumber= context.get("trackingNumber");
        Object deliveryDate= context.get("deliveryDate");
        Object pickupDate= context.get("pickupDate");
        Object cartons= context.get("cartons");
        Object skidsPallets= context.get("skidsPallets");
        Object unitsPieces= context.get("unitsPieces");
        Object totalCubicSize= context.get("totalCubicSize");
        Object totalCubicUomId= context.get("totalCubicUom");
        Object totalWeight= context.get("totalWeight");
        Object totalWeightUomId= context.get("totalWeightUomId");
        Object statusId= context.get("statusId");
        
        // first try to create the OrderDeliverySchedule; if this does not fail, continue.
        try {
             Map scheduleParams = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", "_NA_");
             scheduleParams.put("shipmentMethodTypeId", shipmentMethodTypeId);
             scheduleParams.put("trackingNumber", trackingNumber);
             scheduleParams.put("deliveryDate", deliveryDate);
             scheduleParams.put("pickupDate", pickupDate);
             scheduleParams.put("cartons", cartons);
             scheduleParams.put("skidsPallets", skidsPallets);
             scheduleParams.put("unitsPieces", unitsPieces);
             scheduleParams.put("totalCubicSize", totalCubicSize);
             scheduleParams.put("totalCubicUomId", totalCubicUomId);
             scheduleParams.put("totalWeight", totalWeight);
             scheduleParams.put("totalWeightUomId", totalWeightUomId);
             scheduleParams.put("statusId", statusId);

             GenericValue schedule = delegator.makeValue("OrderDeliverySchedule", scheduleParams);

             //See whether to create or update
             if (doCreation) {
                 delegator.create(schedule);
             } else {
                 delegator.store(schedule);
             }
            result = ServiceUtil.returnSuccess();
        } catch (GenericEntityException e) {
            e.printStackTrace();
            Debug.logError(e, "Cannot create OrderDeliverySchedule entity; problems with insert", module);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            result.put(ModelService.ERROR_MESSAGE, "Schedule creation failed; please notify customer service.");
            return result;
        }            

        return result;
    }
}
