/*
 * OrderHelper.java
 *
 * Created on August 28, 2001, 11:25 AM
 */

package org.ofbiz.ecommerce.order;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 *
 * @author  epabst
 * @version 
 */
public class OrderHelper {
    private static GenericValue getFirst(Collection values) {
        if ((values != null) && (values.size() > 0)) {
            return (GenericValue) values.iterator().next();
        } else {
            return null;
        }
    }
    
    public static double getOrderShippingTotal(GenericValue orderHeader) {
        GenericHelper helper = orderHeader.helper;
        Iterator shippingChargeIter = helper.findByAnd("OrderAdjustment", UtilMisc.toMap(
              "orderId", orderHeader.getString("orderId"), 
                "orderAdjustmentTypeId", "SHIPPING_AND_HANDLIN"), null).iterator();
                //XXX "orderAdjustmentTypeId", "SHIPPING_AND_HANDLING_CHARGES"), null).iterator();
        double result = 0.0;
        while (shippingChargeIter.hasNext()) {
            GenericValue shippingCharge = (GenericValue) shippingChargeIter.next();
            //FIXME should check percentage and watch for null amount
            result += shippingCharge.getDouble("amount").doubleValue();
        }
        return result;
    }
    
    public static double getOrderTotalPrice(GenericValue orderHeader) {
        Iterator billingIter = orderHeader.getRelated("OrderItemBilling").iterator();
        double result = 0.0;
        while (billingIter.hasNext()) {
            result += ((GenericValue) billingIter.next()).getDouble("amount").doubleValue();
        }
        return result;
    }
    
    public static String getOrderStatusString(GenericValue orderHeader) {
        Collection orderStatusList = orderHeader.getRelated("OrderStatus");
        Set orderStatusIdSet = new HashSet();
        Iterator orderStatusIter = orderStatusList.iterator();
        while (orderStatusIter.hasNext()) {
            orderStatusIdSet.add(((GenericValue) orderStatusIter.next()).getString("statusId"));
        }
        Iterator orderStatusIdIter = orderStatusIdSet.iterator();
        String orderStatusIds;
        if (orderStatusIdIter.hasNext()) {
            orderStatusIds = orderStatusIdIter.next().toString();
            while (orderStatusIdIter.hasNext()) {
                orderStatusIds += "/" + orderStatusIdIter.next().toString();
            }
        } else {
            orderStatusIds = "(unspecified)";
        }
        return orderStatusIds;
    }
    
    public static GenericValue getBillToPerson(GenericValue orderHeader) {
        GenericHelper helper = orderHeader.helper;
        Collection billToRoleList = helper.findByAnd("OrderRole", UtilMisc.toMap(
                "orderId", orderHeader.getString("orderId"), 
                "roleTypeId", "BILL_TO_CUSTOMER"), null);
        if (billToRoleList.size() > 0) {
            GenericValue billToRole = (GenericValue) billToRoleList.iterator().next();
            //XXX this will fail: 
            //  return billToRole.getRelatedOne("Party").getRelatedOne("Person");
            return helper.findByPrimaryKey("Person", UtilMisc.toMap("partyId", billToRole.getString("partyId")));
        } else {
            return null;
        }
    }
    
    public static String getPersonName(GenericValue person) {
        StringBuffer result = new StringBuffer(20);
        if(person!=null){
            result.append(appendSpace(person.getString("firstName")));
            result.append(appendSpace(person.getString("middleName")));
            result.append(appendSpace(person.getString("lastName")));
        }
        return result.toString().trim();
    }
    
    private static String appendSpace(String string) {
        if ((string != null) && (string.length() > 0)) {
            return string + " ";
        } else {
            return "";
        }
    }
}
