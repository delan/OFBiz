/*
 * $Id$
 *
 *  Copyright (c) 2002-2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.order.order;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.DataModelConstants;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.security.Security;

import org.apache.commons.collections.set.ListOrderedSet;

/**
 * Utility class for easily extracting important information from orders
 *
 * <p>NOTE: in the current scheme order adjustments are never included in tax or shipping,
 * but order item adjustments ARE included in tax and shipping calcs unless they are
 * tax or shipping adjustments or the includeInTax or includeInShipping are set to N.</p>
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     Eric Pabst
 * @author     <a href="mailto:ray.barlow@whatsthe-point.com">Ray Barlow</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class OrderReadHelper {

    public static final String module = OrderReadHelper.class.getName();

    protected GenericValue orderHeader = null;
    protected List orderItemAndShipGrp = null;
    protected List orderItems = null;
    protected List adjustments = null;
    protected List paymentPrefs = null;
    protected List orderStatuses = null;
    protected List orderItemPriceInfos = null;
    protected List orderItemShipGrpInvResList = null;
    protected List orderItemIssuances = null;
    protected List orderReturnItems = null;
    protected Double totalPrice = null;

    protected OrderReadHelper() {}

    public OrderReadHelper(GenericValue orderHeader, List adjustments, List orderItems) {
        this.orderHeader = orderHeader;
        this.adjustments = adjustments;
        this.orderItems = orderItems;
        if (this.orderHeader != null && !this.orderHeader.getEntityName().equals("OrderHeader")) {
            try {
                this.orderHeader = orderHeader.getDelegator().findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId",
                        orderHeader.getString("orderId")));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                this.orderHeader = null;
            }
        } else if (this.orderHeader == null && orderItems != null) {
            GenericValue firstItem = EntityUtil.getFirst(orderItems);
            try {
                this.orderHeader = firstItem.getRelatedOne("OrderHeader");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                this.orderHeader = null;
            }
        }
        if (this.orderHeader == null) {
            throw new IllegalArgumentException("Order header is not valid");
        }
    }

    public OrderReadHelper(GenericValue orderHeader) {
        this(orderHeader, null, null);
    }

    public OrderReadHelper(List adjustments, List orderItems) {
        this.adjustments = adjustments;
        this.orderItems = orderItems;
    }
    
    public OrderReadHelper(GenericDelegator delegator, String orderId) {
        try {
            this.orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            throw new IllegalArgumentException("Invalid orderId");
        }
    }

    // ==========================================
    // ========== Order Header Methods ==========
    // ==========================================

    public String getOrderId() {
        return orderHeader.getString("orderId");
    }

    public String getWebSiteId() {
        return orderHeader.getString("webSiteId");
    }

    public String getProductStoreId() {
        return orderHeader.getString("productStoreId");
    }

    public String getOrderTypeId() {
        return orderHeader.getString("orderTypeId");
    }

    public String getCurrency() {
        return orderHeader.getString("currencyUom");
    }

    public List getAdjustments() {
        if (adjustments == null) {
            try {
                adjustments = orderHeader.getRelated("OrderAdjustment");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
            if (adjustments == null)
                adjustments = new ArrayList();
        }
        return (List) adjustments;
    }

    public List getPaymentPreferences() {
        if (paymentPrefs == null) {
            try {
                paymentPrefs = orderHeader.getRelated("OrderPaymentPreference", UtilMisc.toList("orderPaymentPreferenceId"));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }
        return paymentPrefs;
    }
    
    public List getOrderPayments() {
        return getOrderPayments(null);
    }

    public List getOrderPayments(GenericValue orderPaymentPreference) {
        List orderPayments = new ArrayList();
        List prefs = null;

        if (orderPaymentPreference == null) {
            prefs = getPaymentPreferences();
        } else {
            prefs = UtilMisc.toList(orderPaymentPreference);
        }
        if (prefs != null) {
            Iterator i = prefs.iterator();
            while (i.hasNext()) {
                GenericValue payPref = (GenericValue) i.next();
                try {
                    orderPayments.addAll(payPref.getRelated("Payment"));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return null;
                }
            }
        }
        return orderPayments;
    }

    public List getOrderStatuses() {
        if (orderStatuses == null) {
            try {
                orderStatuses = orderHeader.getRelated("OrderStatus");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }
        return (List) orderStatuses;
    }

    /** @deprecated */
    public String getShippingMethod() {
        try {
            GenericValue shipmentPreference = null;
            Iterator tempIter = UtilMisc.toIterator(orderHeader.getRelated("OrderShipmentPreference"));

            if (tempIter != null && tempIter.hasNext()) {
                shipmentPreference = (GenericValue) tempIter.next();
            }
            if (shipmentPreference != null) {
                GenericValue carrierShipmentMethod = shipmentPreference.getRelatedOne("CarrierShipmentMethod");

                if (carrierShipmentMethod != null) {
                    GenericValue shipmentMethodType = carrierShipmentMethod.getRelatedOne("ShipmentMethodType");

                    if (shipmentMethodType != null) {
                        return UtilFormatOut.checkNull(shipmentPreference.getString("carrierPartyId")) + " " + UtilFormatOut.checkNull(shipmentMethodType.getString("description"));
                    }
                }
                return UtilFormatOut.checkNull(shipmentPreference.getString("carrierPartyId"));
            }
            return "";
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return "";
    }

    public String getShippingMethod(String shipGroupSeqId) {
        try {
            GenericValue shipGroup = orderHeader.getDelegator().findByPrimaryKey("OrderItemShipGroup",
                    UtilMisc.toMap("orderId", orderHeader.getString("orderId"), "shipGroupSeqId", shipGroupSeqId));

            if (shipGroup != null) {
                GenericValue carrierShipmentMethod = shipGroup.getRelatedOne("CarrierShipmentMethod");

                if (carrierShipmentMethod != null) {
                    GenericValue shipmentMethodType = carrierShipmentMethod.getRelatedOne("ShipmentMethodType");

                    if (shipmentMethodType != null) {
                        return UtilFormatOut.checkNull(shipGroup.getString("carrierPartyId")) + " " +
                                UtilFormatOut.checkNull(shipmentMethodType.getString("description"));
                    }
                }
                return UtilFormatOut.checkNull(shipGroup.getString("carrierPartyId"));
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return "";
    }

    /** @deprecated */
    public String getShippingMethodCode() {
        try {
            GenericValue shipmentPreference = null;
            Iterator tempIter = UtilMisc.toIterator(orderHeader.getRelated("OrderShipmentPreference"));

            if (tempIter != null && tempIter.hasNext()) {
                shipmentPreference = (GenericValue) tempIter.next();
            }
            if (shipmentPreference != null) {
                GenericValue carrierShipmentMethod = shipmentPreference.getRelatedOne("CarrierShipmentMethod");

                if (carrierShipmentMethod != null) {
                    GenericValue shipmentMethodType = carrierShipmentMethod.getRelatedOne("ShipmentMethodType");

                    if (shipmentMethodType != null) {
                        return UtilFormatOut.checkNull(shipmentMethodType.getString("shipmentMethodTypeId")) + "@" + UtilFormatOut.checkNull(shipmentPreference.getString("carrierPartyId"));
                    }
                }
                return UtilFormatOut.checkNull(shipmentPreference.getString("carrierPartyId"));
            }
            return "";
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return "";
    }

    public String getShippingMethodCode(String shipGroupSeqId) {
        try {
            GenericValue shipGroup = orderHeader.getDelegator().findByPrimaryKey("OrderItemShipGroup",
                    UtilMisc.toMap("orderId", orderHeader.getString("orderId"), "shipGroupSeqId", shipGroupSeqId));

            if (shipGroup != null) {
                GenericValue carrierShipmentMethod = shipGroup.getRelatedOne("CarrierShipmentMethod");

                if (carrierShipmentMethod != null) {
                    GenericValue shipmentMethodType = carrierShipmentMethod.getRelatedOne("ShipmentMethodType");

                    if (shipmentMethodType != null) {
                        return UtilFormatOut.checkNull(shipmentMethodType.getString("shipmentMethodTypeId")) + "@" + UtilFormatOut.checkNull(shipGroup.getString("carrierPartyId"));
                    }
                }
                return UtilFormatOut.checkNull(shipGroup.getString("carrierPartyId"));
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return "";
    }

    public boolean hasShippingAddress() {
        if (UtilValidate.isNotEmpty(this.getShippingLocations())) {
            return true;
        }
        return false;
    }

    public GenericValue getOrderItemShipGroup(String shipGroupSeqId) {
        try {
            return orderHeader.getDelegator().findByPrimaryKey("OrderItemShipGroup",
                    UtilMisc.toMap("orderId", orderHeader.getString("orderId"), "shipGroupSeqId", shipGroupSeqId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return null;
    }

    public List getOrderItemShipGroups() {
        try {
            return orderHeader.getRelated("OrderItemShipGroup", UtilMisc.toList("shipGroupSeqId"));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return null;
    }

    public List getShippingLocations() {
        List shippingLocations = new LinkedList();
        List shippingCms = this.getOrderContactMechs("SHIPPING_LOCATION");
        if (shippingCms != null) {
            Iterator i = shippingCms.iterator();
            while (i.hasNext()) {
                GenericValue ocm = (GenericValue) i.next();
                if (ocm != null) {
                    try {
                        GenericValue addr = ocm.getDelegator().findByPrimaryKey("PostalAddress",
                                UtilMisc.toMap("contactMechId", ocm.getString("contactMechId")));
                        if (addr != null) {
                            shippingLocations.add(addr);
                        }
                    } catch (GenericEntityException e) {
                        Debug.logWarning(e, module);
                    }
                }
            }
        }
        return shippingLocations;
    }

    public GenericValue getShippingAddress(String shipGroupSeqId) {
        try {
            GenericValue shipGroup = orderHeader.getDelegator().findByPrimaryKey("OrderItemShipGroup",
                    UtilMisc.toMap("orderId", orderHeader.getString("orderId"), "shipGroupSeqId", shipGroupSeqId));

            if (shipGroup != null) {
                return shipGroup.getRelatedOne("PostalAddress");

            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return null;
    }

    /** @deprecated */
    public GenericValue getShippingAddress() {
        try {
            GenericValue orderContactMech = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderContactMech", UtilMisc.toMap(
                            "contactMechPurposeTypeId", "SHIPPING_LOCATION")));

            if (orderContactMech != null) {
                GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");

                if (contactMech != null) {
                    return contactMech.getRelatedOne("PostalAddress");
                }
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return null;
    }

    public List getBillingLocations() {
        List billingLocations = new LinkedList();
        List billingCms = this.getOrderContactMechs("BILLING_LOCATION");
        if (billingCms != null) {
            Iterator i = billingCms.iterator();
            while (i.hasNext()) {
                GenericValue ocm = (GenericValue) i.next();
                if (ocm != null) {
                    try {
                        GenericValue addr = ocm.getDelegator().findByPrimaryKey("PostalAddress",
                                UtilMisc.toMap("contactMechId", ocm.getString("contactMechId")));
                        if (addr != null) {
                            billingLocations.add(addr);
                        }
                    } catch (GenericEntityException e) {
                        Debug.logWarning(e, module);
                    }
                }
            }
        }
        return billingLocations;
    }

    /** @deprecated */
    public GenericValue getBillingAddress() {
        GenericValue billingAddress = null;
        try {
            GenericValue orderContactMech = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderContactMech", UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION")));

            if (orderContactMech != null) {
                GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");

                if (contactMech != null) {
                    billingAddress = contactMech.getRelatedOne("PostalAddress");
                }
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }

        if (billingAddress == null) {
            // get the address from the billing account
            GenericValue billingAccount = getBillingAccount();
            if (billingAccount != null) {
                try {
                    billingAddress = billingAccount.getRelatedOne("PostalAddress");
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, module);
                }
            } else {
                // get the address from the first payment method
                GenericValue paymentPreference = EntityUtil.getFirst(getPaymentPreferences());
                if (paymentPreference != null) {
                    try {
                        GenericValue paymentMethod = paymentPreference.getRelatedOne("PaymentMethod");
                        if (paymentMethod != null) {
                            GenericValue creditCard = paymentMethod.getRelatedOne("CreditCard");
                            if (creditCard != null) {
                                billingAddress = creditCard.getRelatedOne("PostalAddress");
                            } else {
                                GenericValue eftAccount = paymentMethod.getRelatedOne("EftAccount");
                                if (eftAccount != null) {
                                    billingAddress = eftAccount.getRelatedOne("PostalAddress");
                                }
                            }
                        }
                    } catch (GenericEntityException e) {
                        Debug.logWarning(e, module);
                    }
                }
            }
        }
        return billingAddress;
    }

    public List getOrderContactMechs(String purposeTypeId) {
        try {
            return orderHeader.getRelatedByAnd("OrderContactMech",
                    UtilMisc.toMap("contactMechPurposeTypeId", purposeTypeId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return null;
    }

    public String getCurrentStatusString() {
        GenericValue statusItem = null;
        try {
            statusItem = orderHeader.getRelatedOneCache("StatusItem");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        if (statusItem != null) {
            return statusItem.getString("description");
        } else {
            return orderHeader.getString("statusId");
        }
    }

    public String getStatusString() {
        List orderStatusList = this.getOrderHeaderStatuses();

        if (orderStatusList == null) return "";

        Iterator orderStatusIter = orderStatusList.iterator();
        StringBuffer orderStatusIds = new StringBuffer(50);
        boolean statusSet = false;

        while (orderStatusIter.hasNext()) {
            try {
                GenericValue orderStatus = (GenericValue) orderStatusIter.next();
                GenericValue statusItem = orderStatus.getRelatedOneCache("StatusItem");

                if ( false == statusSet ) {
                    statusSet = true;
                } else {
                    orderStatusIds.append( "/" );
                }

                if (statusItem != null) {
                    orderStatusIds.append(statusItem.getString("description"));
                } else {
                    orderStatusIds.append(orderStatus.getString("statusId"));
                }
            } catch (GenericEntityException gee) {
                Debug.logWarning(gee, module);
            }
        }

        if (false == statusSet) {
            orderStatusIds.append("(unspecified)");
        }
        return orderStatusIds.toString();
    }

    public GenericValue getBillingAccount() {
        GenericValue billingAccount = null;
        try {
            billingAccount = orderHeader.getRelatedOne("BillingAccount");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return billingAccount;
    }

    public GenericValue getBillToPerson() {
        GenericDelegator delegator = orderHeader.getDelegator();

        try {
            GenericEntity billToRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "BILL_TO_CUSTOMER")));

            if (billToRole != null) {
                return delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", billToRole.getString("partyId")));
            } else {
                return null;
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return null;
    }

    public GenericValue getPlacingParty() {
        return this.getPartyFromRole("PLACING_CUSTOMER");
    }

    public GenericValue getSupplierAgent() {
        return this.getPartyFromRole("SUPPLIER_AGENT");
    }

    public GenericValue getPartyFromRole(String roleTypeId) {
        GenericDelegator delegator = orderHeader.getDelegator();
        GenericValue partyObject = null;
        try {
            GenericValue orderRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", roleTypeId)));

            if (orderRole != null) {
                partyObject = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", orderRole.getString("partyId")));

                if (partyObject == null) {
                    partyObject = delegator.findByPrimaryKey("PartyGroup", UtilMisc.toMap("partyId", orderRole.getString("partyId")));
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        return partyObject;
    }

    public String getDistributorId() {
        try {
            GenericEntity distributorRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "DISTRIBUTOR")));

            return distributorRole == null ? null : distributorRole.getString("partyId");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return null;
    }

    public String getAffiliateId() {
        try {
            GenericEntity distributorRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "AFFILIATE")));

            return distributorRole == null ? null : distributorRole.getString("partyId");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return null;
    }

    public double getShippingTotal() {
        return OrderReadHelper.calcOrderAdjustments(getOrderHeaderAdjustments(), getOrderItemsSubTotal(), false, false, true);
    }

    public double getTaxTotal() {
        return OrderReadHelper.calcOrderAdjustments(getOrderHeaderAdjustments(), getOrderItemsSubTotal(), false, true, false);
    }

    public Set getItemFeatureSet(GenericValue item) {
        Set featureSet = new ListOrderedSet();
        List featureAppls = null;
        if (item.get("productId") != null) {
            try {
                featureAppls = item.getDelegator().findByAndCache("ProductFeatureAppl", UtilMisc.toMap("productId", item.getString("productId")));
                List filterExprs = UtilMisc.toList(new EntityExpr("productFeatureApplTypeId", EntityOperator.EQUALS, "STANDARD_FEATURE"));
                filterExprs.add(new EntityExpr("productFeatureApplTypeId", EntityOperator.EQUALS, "REQUIRED_FEATURE"));
                featureAppls = EntityUtil.filterByOr(featureAppls, filterExprs);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Unable to get ProductFeatureAppl for item : " + item, module);
            }
            if (featureAppls != null) {
                Iterator fai = featureAppls.iterator();
                while (fai.hasNext()) {
                    GenericValue appl = (GenericValue) fai.next();
                    featureSet.add(appl.getString("productFeatureId"));
                }
            }
        }

        // get the ADDITIONAL_FEATURE adjustments
        List additionalFeatures = null;
        try {
            additionalFeatures = item.getRelatedByAnd("OrderAdjustment", UtilMisc.toMap("orderAdjustmentTypeId", "ADDITIONAL_FEATURE"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get OrderAdjustment from item : " + item, module);
        }
        if (additionalFeatures != null) {
            Iterator afi = additionalFeatures.iterator();
            while (afi.hasNext()) {
                GenericValue adj = (GenericValue) afi.next();
                String featureId = adj.getString("productFeatureId");
                if (featureId != null) {
                    featureSet.add(featureId);
                }
            }
        }

        return featureSet;
    }

    public Map getFeatureIdQtyMap(String shipGroupSeqId) {
        Map featureMap = new HashMap();
        List validItems = getValidOrderItems(shipGroupSeqId);
        if (validItems != null) {
            Iterator i = validItems.iterator();
            while (i.hasNext()) {
                GenericValue item = (GenericValue) i.next();
                List featureAppls = null;
                if (item.get("productId") != null) {
                    try {
                        featureAppls = item.getDelegator().findByAndCache("ProductFeatureAppl", UtilMisc.toMap("productId", item.getString("productId")));
                        List filterExprs = UtilMisc.toList(new EntityExpr("productFeatureApplTypeId", EntityOperator.EQUALS, "STANDARD_FEATURE"));
                        filterExprs.add(new EntityExpr("productFeatureApplTypeId", EntityOperator.EQUALS, "REQUIRED_FEATURE"));
                        featureAppls = EntityUtil.filterByOr(featureAppls, filterExprs);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Unable to get ProductFeatureAppl for item : " + item, module);
                    }
                    if (featureAppls != null) {
                        Iterator fai = featureAppls.iterator();
                        while (fai.hasNext()) {
                            GenericValue appl = (GenericValue) fai.next();
                            Double lastQuantity = (Double) featureMap.get(appl.getString("productFeatureId"));
                            if (lastQuantity == null) {
                                lastQuantity = new Double(0);
                            }
                            Double newQuantity = new Double(lastQuantity.doubleValue() + getOrderItemQuantity(item).doubleValue());
                            featureMap.put(appl.getString("productFeatureId"), newQuantity);
                        }
                    }
                }

                // get the ADDITIONAL_FEATURE adjustments
                List additionalFeatures = null;
                try {
                    additionalFeatures = item.getRelatedByAnd("OrderAdjustment", UtilMisc.toMap("orderAdjustmentTypeId", "ADDITIONAL_FEATURE"));
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Unable to get OrderAdjustment from item : " + item, module);
                }
                if (additionalFeatures != null) {
                    Iterator afi = additionalFeatures.iterator();
                    while (afi.hasNext()) {
                        GenericValue adj = (GenericValue) afi.next();
                        String featureId = adj.getString("productFeatureId");
                        if (featureId != null) {
                            Double lastQuantity = (Double) featureMap.get(featureId);
                            if (lastQuantity == null) {
                                lastQuantity = new Double(0);
                            }
                            Double newQuantity = new Double(lastQuantity.doubleValue() + getOrderItemQuantity(item).doubleValue());
                            featureMap.put(featureId, newQuantity);
                        }
                    }
                }
            }
        }

        return featureMap;
    }

    public double getShippableTotal(String shipGroupSeqId) {
        double shippableTotal = 0.00;
        List validItems = getValidOrderItems(shipGroupSeqId);
        if (validItems != null) {
            Iterator i = validItems.iterator();
            while (i.hasNext()) {
                GenericValue item = (GenericValue) i.next();
                GenericValue product = null;
                try {
                    product = item.getRelatedOne("Product");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problem getting Product from OrderItem; returning 0", module);
                    return 0.00;
                }
                if (product != null) {
                    if (ProductWorker.shippingApplies(product)) {
                        shippableTotal += OrderReadHelper.getOrderItemSubTotal(item, getAdjustments(), false, true);
                    }
                }
            }
        }
        return shippableTotal;
    }

    public double getShippableQuantity(String shipGroupSeqId) {
        double shippableQuantity = 0.00;
        List validItems = getValidOrderItems(shipGroupSeqId);
        if (validItems != null) {
            Iterator i = validItems.iterator();
            while (i.hasNext()) {
                GenericValue item = (GenericValue) i.next();
                GenericValue product = null;
                try {
                    product = item.getRelatedOne("Product");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problem getting Product from OrderItem; returning 0", module);
                    return 0.00;
                }
                if (product != null) {
                    if (ProductWorker.shippingApplies(product)) {
                        shippableQuantity += getOrderItemQuantity(item).doubleValue();
                    }
                }
            }
        }
        return shippableQuantity;
    }

    public double getShippableWeight(String shipGroupSeqId) {
        double shippableWeight = 0.00;
        List validItems = getValidOrderItems(shipGroupSeqId);
        if (validItems != null) {
            Iterator i = validItems.iterator();
            while (i.hasNext()) {
                GenericValue item = (GenericValue) i.next();
                shippableWeight += this.getItemWeight(item);
            }
        }

        return shippableWeight;
    }

    public double getItemWeight(GenericValue item) {
        GenericDelegator delegator = orderHeader.getDelegator();
        double itemWeight = 0.00;

        GenericValue product = null;
        try {
            product = item.getRelatedOne("Product");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Product from OrderItem; returning 0", module);
            return 0.00;
        }
        if (product != null) {
            if (ProductWorker.shippingApplies(product)) {
                Double weight = product.getDouble("weight");
                String isVariant = product.getString("isVariant");
                if (weight == null && isVariant != null && "Y".equals(isVariant)) {
                    // get the virtual product and check its weight
                    GenericValue virtual = null;
                    try {
                        List virtuals = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productIdTo", product.getString("productId"), "productAssocTypeId", "PRODUCT_VARIENT"), UtilMisc.toList("-fromDate"));
                        if (virtuals != null) {
                            virtuals = EntityUtil.filterByDate(virtuals);
                        }
                        virtual = EntityUtil.getFirst(virtuals);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Problem getting virtual product");
                    }
                    if (virtual != null) {
                        weight = virtual.getDouble("weight");
                    }
                }

                if (weight != null) {
                    itemWeight = weight.doubleValue();
                }
            }
        }

        return itemWeight;
    }

    public List getShippableSizes() {
        List shippableSizes = new LinkedList();

        List validItems = getValidOrderItems();
        if (validItems != null) {
            Iterator i = validItems.iterator();
            while (i.hasNext()) {
                GenericValue item = (GenericValue) i.next();
                shippableSizes.add(new Double(this.getItemSize(item)));
            }
        }
        return shippableSizes;
    }

    public double getItemSize(GenericValue item) {
        GenericDelegator delegator = orderHeader.getDelegator();
        double size = 0;

        GenericValue product = null;
        try {
            product = item.getRelatedOne("Product");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting Product from OrderItem", module);
            return 0;
        }
        if (product != null) {
            if (ProductWorker.shippingApplies(product)) {
                Double height = product.getDouble("shippingHeight");
                Double width = product.getDouble("shippingWidth");
                Double depth = product.getDouble("shippingDepth");
                String isVariant = product.getString("isVariant");
                if (height == null && width == null && depth == null && isVariant != null && "Y".equals(isVariant)) {
                    // get the virtual product and check its values
                    GenericValue virtual = null;
                    try {
                        List virtuals = delegator.findByAnd("ProductAssoc", UtilMisc.toMap("productIdTo", product.getString("productId"), "productAssocTypeId", "PRODUCT_VARIENT"), UtilMisc.toList("-fromDate"));
                        if (virtuals != null) {
                            virtuals = EntityUtil.filterByDate(virtuals);
                        }
                        virtual = EntityUtil.getFirst(virtuals);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Problem getting virtual product");
                    }
                    if (virtual != null) {
                        height = virtual.getDouble("shippingHeight");
                        width = virtual.getDouble("shippingWidth");
                        depth = virtual.getDouble("shippingDepth");
                    }
                }

                if (height == null) height = new Double(0);
                if (width == null) width = new Double(0);
                if (depth == null) depth = new Double(0);

                // determine girth (longest field is length)
                double[] sizeInfo = { height.doubleValue(), width.doubleValue(), depth.doubleValue() };
                Arrays.sort(sizeInfo);
                
                size = (sizeInfo[0] * 2) + (sizeInfo[1] * 2) + sizeInfo[2];
            }
        }

        return size;
    }

   public List getShippableItemInfo(String shipGroupSeqId) {
        List shippableInfo = new LinkedList();

        List validItems = getValidOrderItems(shipGroupSeqId);
        if (validItems != null) {
            Iterator i = validItems.iterator();
            while (i.hasNext()) {
                GenericValue item = (GenericValue) i.next();
                shippableInfo.add(this.getItemInfoMap(item));
            }
        }

        return shippableInfo;
    }

    public Map getItemInfoMap(GenericValue item) {
        Map itemInfo = new HashMap();
        itemInfo.put("productId", item.getString("productId"));
        itemInfo.put("quantity", getOrderItemQuantity(item));
        itemInfo.put("weight", new Double(this.getItemWeight(item)));
        itemInfo.put("size",  new Double(this.getItemSize(item)));
        itemInfo.put("featureSet", this.getItemFeatureSet(item));
        return itemInfo;
    }

    public String getOrderEmailString() {
        GenericDelegator delegator = orderHeader.getDelegator();
        // get the email addresses from the order contact mech(s)
        List orderContactMechs = null;
        try {
            Map ocFields = UtilMisc.toMap("orderId", orderHeader.get("orderId"), "contactMechPurposeTypeId", "ORDER_EMAIL");
            orderContactMechs = delegator.findByAnd("OrderContactMech", ocFields);
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting order contact mechs", module);
        }

        StringBuffer emails = new StringBuffer();
        if (orderContactMechs != null) {
            Iterator oci = orderContactMechs.iterator();
            while (oci.hasNext()) {
                try {
                    GenericValue orderContactMech = (GenericValue) oci.next();
                    GenericValue contactMech = orderContactMech.getRelatedOne("ContactMech");
                    emails.append(emails.length() > 0 ? "," : "").append(contactMech.getString("infoString"));
                } catch (GenericEntityException e) {
                    Debug.logWarning(e, "Problems getting contact mech from order contact mech", module);
                }
            }
        }
        return emails.toString();
    }

    public double getOrderGrandTotal() {
        if (totalPrice == null) {
            totalPrice = new Double(getOrderGrandTotal(getValidOrderItems(), getAdjustments()));
        }// else already set
        return totalPrice.doubleValue();
    }

    public List getOrderHeaderAdjustments() {
        return getOrderHeaderAdjustments(getAdjustments(), null);
    }

    public List getOrderHeaderAdjustments(String shipGroupSeqId) {
        return getOrderHeaderAdjustments(getAdjustments(), shipGroupSeqId);
    }

    public List getOrderHeaderAdjustmentsToShow() {
        return filterOrderAdjustments(getOrderHeaderAdjustments(), true, false, false, false, false);
    }

    public List getOrderHeaderStatuses() {
        return getOrderHeaderStatuses(getOrderStatuses());
    }

    public double getOrderAdjustmentsTotal() {
        return getOrderAdjustmentsTotal(getValidOrderItems(), getAdjustments());
    }

    public double getOrderAdjustmentTotal(GenericValue adjustment) {
        return calcOrderAdjustment(adjustment, getOrderItemsSubTotal());
    }

    public int hasSurvey() {
        GenericDelegator delegator = orderHeader.getDelegator();
        List surveys = null;
        try {
            surveys = delegator.findByAnd("SurveyResponse", UtilMisc.toMap("orderId", orderHeader.getString("orderId")));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        int size = 0;
        if (surveys != null) {
            size = surveys.size();
        }

        return size;
    }

    // ========================================
    // ========== Order Item Methods ==========
    // ========================================

    public List getOrderItems() {
        if (orderItems == null) {
            try {
                orderItems = orderHeader.getRelated("OrderItem");
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
        return orderItems;
    }

    public List getOrderItemAndShipGroupAssoc() {
        if (orderItemAndShipGrp == null) {
            try {
                orderItemAndShipGrp = orderHeader.getDelegator().findByAnd("OrderItemAndShipGroupAssoc",
                        UtilMisc.toMap("orderId", orderHeader.getString("orderId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
        return orderItemAndShipGrp;
    }

    public List getOrderItemAndShipGroupAssoc(String shipGroupSeqId) {
        List exprs = UtilMisc.toList(new EntityExpr("shipGroupSeqId", EntityOperator.EQUALS, shipGroupSeqId));
        return EntityUtil.filterByAnd(getOrderItemAndShipGroupAssoc(), exprs);
    }

    public List getValidOrderItems() {
        List exprs = UtilMisc.toList(
                new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
                new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));
        return EntityUtil.filterByAnd(getOrderItems(), exprs);
    }

    public List getValidOrderItems(String shipGroupSeqId) {
        if (shipGroupSeqId == null) return getValidOrderItems();
        List exprs = UtilMisc.toList(
                new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
                new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"),
                new EntityExpr("shipGroupSeqId", EntityOperator.EQUALS, shipGroupSeqId));
        return EntityUtil.filterByAnd(getOrderItemAndShipGroupAssoc(), exprs);
    }

    public GenericValue getOrderItem(String orderItemSeqId) {
        List exprs = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, orderItemSeqId));
        return EntityUtil.getFirst(EntityUtil.filterByAnd(getOrderItems(), exprs));
    }

    public List getValidDigitalItems() {
        List digitalItems = new ArrayList();
        // only approved or complete items apply
        List exprs = UtilMisc.toList(
                new EntityExpr("statusId", EntityOperator.EQUALS, "ITEM_APPROVED"),
                new EntityExpr("statusId", EntityOperator.EQUALS, "ITEM_COMPLETED"));
        List items = EntityUtil.filterByOr(getOrderItems(), exprs);
        Iterator i = items.iterator();
        while (i.hasNext()) {
            GenericValue item = (GenericValue) i.next();
            if (item.get("productId") != null) {
                GenericValue product = null;
                try {
                    product = item.getRelatedOne("Product");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Unable to get Product from OrderItem", module);
                }
                if (product != null) {
                    String productType = product.getString("productTypeId");
                    if ("DIGITAL_GOOD".equals(productType) || "FINDIG_GOOD".equals(productType)) {
                        // make sure we have an OrderItemBilling record
                        List orderItemBillings = null;
                        try {
                            orderItemBillings = item.getRelated("OrderItemBilling");
                        } catch (GenericEntityException e) {
                            Debug.logError(e, "Unable to get OrderItemBilling from OrderItem");
                        }

                        if (orderItemBillings != null && orderItemBillings.size() > 0) {
                            // get the ProductContent records
                            List productContents = null;
                            try {
                                productContents = product.getRelated("ProductContent");
                            } catch (GenericEntityException e) {
                                Debug.logError("Unable to get ProductContent from Product", module);
                            }
                            List cExprs = UtilMisc.toList(
                                    new EntityExpr("productContentTypeId", EntityOperator.EQUALS, "DIGITAL_DOWNLOAD"),
                                    new EntityExpr("productContentTypeId", EntityOperator.EQUALS, "FULFILLMENT_EMAIL"),
                                    new EntityExpr("productContentTypeId", EntityOperator.EQUALS, "FULFILLMENT_EXTERNAL"));
                            // add more as needed
                            productContents = EntityUtil.filterByDate(productContents);
                            productContents = EntityUtil.filterByOr(productContents, cExprs);

                            if (productContents != null && productContents.size() > 0) {
                                // make sure we are still within the allowed timeframe and use limits
                                Iterator pci = productContents.iterator();
                                while (pci.hasNext()) {
                                    GenericValue productContent = (GenericValue) pci.next();
                                    Timestamp fromDate = productContent.getTimestamp("purchaseFromDate");
                                    Timestamp thruDate = productContent.getTimestamp("purchaseThruDate");
                                    if (fromDate == null || item.getTimestamp("orderDate").after(fromDate)) {
                                        if (thruDate == null || item.getTimestamp("orderDate").before(thruDate)) {
                                            // TODO: Implement use count and days
                                            digitalItems.add(item);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return digitalItems;
    }

    public List getOrderItemAdjustments(GenericValue orderItem) {
        return getOrderItemAdjustmentList(orderItem, getAdjustments());
    }

    public String getCurrentItemStatus(GenericValue orderItem) {
        GenericValue statusItem = null;
        try {
            statusItem = orderItem.getRelatedOne("StatusItem");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting StatusItem : " + orderItem, module);
        }
        if (statusItem == null || statusItem.get("description") == null) {
            return "Not Available";
        } else {
            return statusItem.getString("description");
        }
    }

    public List getOrderItemPriceInfos(GenericValue orderItem) {
        if (orderItem == null) return null;
        if (this.orderItemPriceInfos == null) {
            GenericDelegator delegator = orderHeader.getDelegator();

            try {
                orderItemPriceInfos = delegator.findByAnd("OrderItemPriceInfo", UtilMisc.toMap("orderId", orderHeader.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
        String orderItemSeqId = (String) orderItem.get("orderItemSeqId");

        return EntityUtil.filterByAnd(this.orderItemPriceInfos, UtilMisc.toMap("orderItemSeqId", orderItemSeqId));
    }

    public List getOrderItemShipGrpInvResList(GenericValue orderItem) {
        if (orderItem == null) return null;
        if (this.orderItemShipGrpInvResList == null) {
            GenericDelegator delegator = orderItem.getDelegator();
            try {
                orderItemShipGrpInvResList = delegator.findByAnd("OrderItemShipGrpInvRes", UtilMisc.toMap("orderId", orderItem.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Trouble getting OrderItemShipGrpInvRes List", module);
            }
        }
        return EntityUtil.filterByAnd(orderItemShipGrpInvResList, UtilMisc.toMap("orderItemSeqId", orderItem.getString("orderItemSeqId")));
    }

    public List getOrderItemIssuances(GenericValue orderItem) {
        if (orderItem == null) return null;
        if (this.orderItemIssuances == null) {
            GenericDelegator delegator = orderItem.getDelegator();

            try {
                orderItemIssuances = delegator.findByAnd("ItemIssuance", UtilMisc.toMap("orderId", orderItem.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Trouble getting ItemIssuance(s)", module);
            }
        }
        return EntityUtil.filterByAnd(orderItemIssuances, UtilMisc.toMap("orderItemSeqId", orderItem.getString("orderItemSeqId")));
    }
    
    public List getOrderReturnItems() {
        GenericDelegator delegator = orderHeader.getDelegator();
        if (this.orderReturnItems == null) {
            try {
                this.orderReturnItems = delegator.findByAnd("ReturnItem", UtilMisc.toMap("orderId", orderHeader.getString("orderId")));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem getting ReturnItem from order", module);
                return null;
            }
        }
        return this.orderReturnItems;
    }

    public double getOrderReturnedQuantity() {
        List returnedItems = getOrderReturnItems();
        double returnedQuantity = 0.00;
        if (returnedItems != null) {
            Iterator i = returnedItems.iterator();
            while (i.hasNext()) {
                GenericValue returnedItem = (GenericValue) i.next();
                if (returnedItem.get("returnQuantity") != null) {
                    returnedQuantity += returnedItem.getDouble("returnQuantity").doubleValue();
                }
            }
        }
        return returnedQuantity;
    }
    
    public double getOrderReturnedTotal() {
        List returnedItemsBase = getOrderReturnItems();
        List returnedItems = new ArrayList(returnedItemsBase.size());
        
        // get only the RETURN_RECEIVED and RETURN_COMPLETED statusIds
        returnedItems.addAll(EntityUtil.filterByAnd(returnedItemsBase, UtilMisc.toMap("statusId", "RETURN_RECEIVED")));
        returnedItems.addAll(EntityUtil.filterByAnd(returnedItemsBase, UtilMisc.toMap("statusId", "RETURN_COMPLETED")));
        
        double returnedAmount = 0.00;
        Iterator i = returnedItems.iterator();
        while (i.hasNext()) {
            GenericValue returnedItem = (GenericValue) i.next();
            if (returnedItem.get("returnPrice") != null) {
                returnedAmount += returnedItem.getDouble("returnPrice").doubleValue();
            }
        }
        return returnedAmount;
    }
    
    public double getOrderNonReturnedTaxAndShipping() {
        // first make a Map of orderItemSeqId key, returnQuantity value
        List returnedItemsBase = getOrderReturnItems();
        List returnedItems = new ArrayList(returnedItemsBase.size());
        
        // get only the RETURN_RECEIVED and RETURN_COMPLETED statusIds
        returnedItems.addAll(EntityUtil.filterByAnd(returnedItemsBase, UtilMisc.toMap("statusId", "RETURN_RECEIVED")));
        returnedItems.addAll(EntityUtil.filterByAnd(returnedItemsBase, UtilMisc.toMap("statusId", "RETURN_COMPLETED")));
        
        Map itemReturnedQuantities = new HashMap();
        Iterator i = returnedItems.iterator();
        while (i.hasNext()) {
            GenericValue returnedItem = (GenericValue) i.next();
            String orderItemSeqId = returnedItem.getString("orderItemSeqId");
            Double returnedQuantity = returnedItem.getDouble("returnQuantity");
            if (orderItemSeqId != null && returnedQuantity != null) {
                Double existingQuantity = (Double) itemReturnedQuantities.get(orderItemSeqId);
                if (existingQuantity == null) {
                    itemReturnedQuantities.put(orderItemSeqId, returnedQuantity);
                } else {
                    itemReturnedQuantities.put(orderItemSeqId, new Double(returnedQuantity.doubleValue() + existingQuantity.doubleValue()));
                }
            }
        }
        
        // then go through all order items and for the quantity not returned calculate it's portion of the item, and of the entire order
        double totalSubTotalNotReturned = 0;
        double totalTaxNotReturned = 0;
        double totalShippingNotReturned = 0;
        
        Iterator orderItems = this.getValidOrderItems().iterator();
        while (orderItems.hasNext()) {
            GenericValue orderItem = (GenericValue) orderItems.next();
            
            Double itemQuantityDbl = orderItem.getDouble("quantity");
            if (itemQuantityDbl == null) {
                continue;
            }
            double itemQuantity = itemQuantityDbl.doubleValue();
            double itemSubTotal = this.getOrderItemSubTotal(orderItem);
            double itemTaxes = this.getOrderItemTax(orderItem);
            double itemShipping = this.getOrderItemShipping(orderItem);

            Double quantityReturnedDouble = (Double) itemReturnedQuantities.get(orderItem.get("orderItemSeqId"));
            double quantityReturned = 0;
            if (quantityReturnedDouble != null) {
                quantityReturned = quantityReturnedDouble.doubleValue();
            }

            double quantityNotReturned = itemQuantity - quantityReturned;

            double factorNotReturned = quantityNotReturned / itemQuantity;
            double subTotalNotReturned = itemSubTotal * factorNotReturned;

            // calculate tax and shipping adjustments for each item, add to accumulators
            double itemTaxNotReturned = itemTaxes * factorNotReturned;
            double itemShippingNotReturned = itemShipping * factorNotReturned;

            totalSubTotalNotReturned += subTotalNotReturned;
            totalTaxNotReturned += itemTaxNotReturned;
            totalShippingNotReturned += itemShippingNotReturned;
        }

        // calculate tax and shipping adjustments for entire order, add to result
        double orderItemsSubTotal = this.getOrderItemsSubTotal();
        double orderFactorNotReturned = 0.0;
        if (orderItemsSubTotal != 0.0) {
            orderFactorNotReturned = totalSubTotalNotReturned / orderItemsSubTotal;
        }
        double orderTaxNotReturned = this.getTaxTotal() * orderFactorNotReturned;
        double orderShippingNotReturned = this.getShippingTotal() * orderFactorNotReturned;
        
        return totalTaxNotReturned + totalShippingNotReturned + orderTaxNotReturned + orderShippingNotReturned;
    }

    public double getItemShippedQuantity(GenericValue orderItem) {
        double quantityShipped = 0.00;
        List issuance = getOrderItemIssuances(orderItem);
        if (issuance != null) {
            Iterator i = issuance.iterator();
            while (i.hasNext()) {
                GenericValue issue = (GenericValue) i.next();
                Double issueQty = issue.getDouble("quantity");
                if (issueQty != null) {
                    quantityShipped += issueQty.doubleValue();
                }
            }
        }
        return quantityShipped;
    }

    public double getItemReservedQuantity(GenericValue orderItem) {
        double reserved = 0.00;

        List reses = getOrderItemShipGrpInvResList(orderItem);
        if (reses != null) {
            Iterator i = reses.iterator();
            while (i.hasNext()) {
                GenericValue res = (GenericValue) i.next();
                Double quantity = res.getDouble("quantity");
                if (quantity != null) {
                    reserved += quantity.doubleValue();
                }
            }
        }
        return reserved;
    }

    public double getItemBackorderedQuantity(GenericValue orderItem) {
        double backOrdered = 0.00;

        Timestamp shipDate = orderItem.getTimestamp("estimatedShipDate");
        Timestamp autoCancel = orderItem.getTimestamp("autoCancelDate");

        List reses = getOrderItemShipGrpInvResList(orderItem);
        if (reses != null) {
            Iterator i = reses.iterator();
            while (i.hasNext()) {
                GenericValue res = (GenericValue) i.next();
                Timestamp promised = res.getTimestamp("currentPromisedDate");
                if (promised == null) {
                    promised = res.getTimestamp("promisedDatetime");
                }
                if (autoCancel != null || (shipDate != null && shipDate.after(promised))) {
                    Double resQty = res.getDouble("quantity");
                    if (resQty != null) {
                        backOrdered += resQty.doubleValue();
                    }
                }
            }
        }
        return backOrdered;
    }

    public double getItemPendingShipmentQuantity(GenericValue orderItem) {
        double reservedQty = getItemReservedQuantity(orderItem);
        double backordered = getItemBackorderedQuantity(orderItem);
        return (reservedQty - backordered);
    }

    public double getItemCanceledQuantity(GenericValue orderItem) {
        Double cancelQty = orderItem.getDouble("cancelQuantity");
        if (cancelQty == null) cancelQty = new Double(0.0);
        return cancelQty.doubleValue();
    }

    public double getTotalOrderItemsQuantity() {
        List orderItems = getValidOrderItems();
        double totalItems = 0;

        for (int i = 0; i < orderItems.size(); i++) {
            GenericValue oi = (GenericValue) orderItems.get(i);

            totalItems += getOrderItemQuantity(oi).doubleValue();
        }
        return totalItems;
    }

    public double getTotalOrderItemsOrderedQuantity() {
        List orderItems = getValidOrderItems();
        double totalItems = 0;

        for (int i = 0; i < orderItems.size(); i++) {
            GenericValue oi = (GenericValue) orderItems.get(i);

            totalItems += oi.getDouble("quantity").doubleValue();
        }
        return totalItems;
    }

    public double getOrderItemsSubTotal() {
        return getOrderItemsSubTotal(getValidOrderItems(), getAdjustments());
    }

    public double getOrderItemSubTotal(GenericValue orderItem) {
        return getOrderItemSubTotal(orderItem, getAdjustments());
    }

    public double getOrderItemsTotal() {
        return getOrderItemsTotal(getValidOrderItems(), getAdjustments());
    }

    public double getOrderItemTotal(GenericValue orderItem) {
        return getOrderItemTotal(orderItem, getAdjustments());
    }

    public double getOrderItemTax(GenericValue orderItem) {
        return getOrderItemAdjustmentsTotal(orderItem, false, true, false);
    }

    public double getOrderItemShipping(GenericValue orderItem) {
        return getOrderItemAdjustmentsTotal(orderItem, false, false, true);
    }

    public double getOrderItemAdjustmentsTotal(GenericValue orderItem, boolean includeOther, boolean includeTax, boolean includeShipping) {
        return getOrderItemAdjustmentsTotal(orderItem, getAdjustments(), includeOther, includeTax, includeShipping);
    }

    public double getOrderItemAdjustmentsTotal(GenericValue orderItem) {
        return getOrderItemAdjustmentsTotal(orderItem, true, false, false);
    }

    public double getOrderItemAdjustmentTotal(GenericValue orderItem, GenericValue adjustment) {
        return calcItemAdjustment(adjustment, orderItem);
    }

    public String getAdjustmentType(GenericValue adjustment) {
        GenericValue adjustmentType = null;
        try {
            adjustmentType = adjustment.getRelatedOne("OrderAdjustmentType");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems with order adjustment", module);
        }
        if (adjustmentType == null || adjustmentType.get("description") == null) {
            return "";
        } else {
            return adjustmentType.getString("description");
        }
    }

    public List getOrderItemStatuses(GenericValue orderItem) {
        return getOrderItemStatuses(orderItem, getOrderStatuses());
    }

    public String getCurrentItemStatusString(GenericValue orderItem) {
        GenericValue statusItem = null;
        try {
            statusItem = orderItem.getRelatedOneCache("StatusItem");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }
        if (statusItem != null) {
            return statusItem.getString("description");
        } else {
            return orderHeader.getString("statusId");
        }
    }

    /**
     * Checks to see if this user has read permission on this order
     * @param userLogin The UserLogin value object to check
     * @return boolean True if we have read permission
     */
    public boolean hasPermission(Security security, GenericValue userLogin) {
        return OrderReadHelper.hasPermission(security, userLogin, orderHeader);
    }

    /**
     * Getter for property orderHeader.
     * @return Value of property orderHeader.
     */
    public GenericValue getOrderHeader() {
        return orderHeader;
    }

    // ======================================================
    // =================== Static Methods ===================
    // ======================================================

    public static GenericValue getOrderHeader(GenericDelegator delegator, String orderId) {
        GenericValue orderHeader = null;
        if (orderId != null && delegator != null) {
            try {
                orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get order header", module);
            }
        }
        return orderHeader;
    }

    public static Double getOrderItemQuantity(GenericValue orderItem) {
        String cancelQtyField = "cancelQuantity";
        String quantityField = "quantity";

        if ("OrderItemAndShipGroupAssoc".equals(orderItem.getEntityName())) {
            cancelQtyField = "shipGroupCancelQuantity";
            quantityField = "shipGroupQuantity";
        }

        Double cancelQty = orderItem.getDouble(cancelQtyField);
        Double orderQty = orderItem.getDouble(quantityField);

        if (cancelQty == null) cancelQty = new Double(0.0);
        if (orderQty == null) orderQty = new Double(0.0);
        
        return new Double(orderQty.doubleValue() - cancelQty.doubleValue());
    }

    public static GenericValue getProductStoreFromOrder(GenericDelegator delegator, String orderId) {
        return getProductStoreFromOrder(getOrderHeader(delegator, orderId));
    }

    public static GenericValue getProductStoreFromOrder(GenericValue orderHeader) {
        GenericDelegator delegator = orderHeader.getDelegator();
        GenericValue productStore = null;
        if (orderHeader != null && orderHeader.get("productStoreId") != null) {
            try {
                productStore = delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", orderHeader.getString("productStoreId")));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot locate ProductStore from OrderHeader", module);
            }
        } else {
            Debug.logError("Null header or productStoreId", module);
        }
        return productStore;
    }
    public static double getOrderGrandTotal(List orderItems, List adjustments) {
        double total = getOrderItemsTotal(orderItems, adjustments);
        double adj = getOrderAdjustmentsTotal(orderItems, adjustments);

        return total + adj;
    }

    public static List getOrderHeaderAdjustments(List adjustments, String shipGroupSeqId) {
        List contraints1 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, null));
        List contraints2 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, DataModelConstants.SEQ_ID_NA));
        List contraints3 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, ""));
        List contraints4 = new LinkedList();
        if (shipGroupSeqId != null) {
            contraints4.add(new EntityExpr("shipGroupSeqId", EntityOperator.EQUALS, shipGroupSeqId));
        }
        List toFilter = null;
        List adj = new LinkedList();

        if (shipGroupSeqId != null) {
            toFilter = EntityUtil.filterByAnd(adjustments, contraints4);
        } else {
            toFilter = adjustments;
        }

        adj.addAll(EntityUtil.filterByAnd(toFilter, contraints1));
        adj.addAll(EntityUtil.filterByAnd(toFilter, contraints2));
        adj.addAll(EntityUtil.filterByAnd(toFilter, contraints3));
        return adj;
    }

    public static List getOrderHeaderStatuses(List orderStatuses) {
        List contraints1 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, null));
        List contraints2 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, DataModelConstants.SEQ_ID_NA));
        List contraints3 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, ""));
        List newOrderStatuses = new LinkedList();

        newOrderStatuses.addAll(EntityUtil.filterByAnd(orderStatuses, contraints1));
        newOrderStatuses.addAll(EntityUtil.filterByAnd(orderStatuses, contraints2));
        newOrderStatuses.addAll(EntityUtil.filterByAnd(orderStatuses, contraints3));
        newOrderStatuses = EntityUtil.orderBy(newOrderStatuses, UtilMisc.toList("-statusDatetime"));
        return newOrderStatuses;
    }

    public static double getOrderAdjustmentsTotal(List orderItems, List adjustments) {
        return calcOrderAdjustments(getOrderHeaderAdjustments(adjustments, null), getOrderItemsSubTotal(orderItems, adjustments), true, true, true);
    }

    public static List getOrderSurveyResponses(GenericValue orderHeader) {
        GenericDelegator delegator = orderHeader.getDelegator();
        String orderId = orderHeader.getString("orderId");
         List responses = null;
        try {
            responses = delegator.findByAnd("SurveyResponse", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", "_NA_"));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        if (responses == null) {
            responses = new LinkedList();
        }
        return responses;
    }

    public static List getOrderItemSurveyResponse(GenericValue orderItem) {
        GenericDelegator delegator = orderItem.getDelegator();
        String orderItemSeqId = orderItem.getString("orderItemSeqId");
        String orderId = orderItem.getString("orderId");
        List responses = null;
        try {
            responses = delegator.findByAnd("SurveyResponse", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
        }

        if (responses == null) {
            responses = new LinkedList();
        }
        return responses;
    }

    // ================= Order Adjustments =================

    public static double calcOrderAdjustments(List orderHeaderAdjustments, double subTotal, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double adjTotal = 0.0;

        if (orderHeaderAdjustments != null && orderHeaderAdjustments.size() > 0) {
            List filteredAdjs = filterOrderAdjustments(orderHeaderAdjustments, includeOther, includeTax, includeShipping, false, false);
            Iterator adjIt = filteredAdjs.iterator();

            while (adjIt.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) adjIt.next();

                adjTotal += OrderReadHelper.calcOrderAdjustment(orderAdjustment, subTotal);
            }
        }
        return adjTotal;
    }

    public static double calcOrderAdjustment(GenericValue orderAdjustment, double orderSubTotal) {
        double adjustment = 0.0;

        if (orderAdjustment.get("amount") != null) {
            adjustment += orderAdjustment.getDouble("amount").doubleValue();
        }
        if (orderAdjustment.get("percentage") != null) {
            adjustment += (orderAdjustment.getDouble("percentage").doubleValue() * orderSubTotal);
        }
        return adjustment;
    }

    // ================= Order Item Adjustments =================

    public static double getOrderItemsSubTotal(List orderItems, List adjustments) {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(orderItems);

        while (itemIter != null && itemIter.hasNext()) {
            GenericValue orderItem = (GenericValue) itemIter.next();
            double itemTotal = getOrderItemSubTotal(orderItem, adjustments);
            //Debug.log("Item : " + orderItem.getString("orderId") + " / " + orderItem.getString("orderItemSeqId") + " = " + itemTotal, module);
            result += itemTotal;
        }
        return result;
    }

    /** The passed adjustments can be all adjustments for the order, ie for all line items */
    public static double getOrderItemSubTotal(GenericValue orderItem, List adjustments) {
        return getOrderItemSubTotal(orderItem, adjustments, false, false);
    }

    /** The passed adjustments can be all adjustments for the order, ie for all line items */
    public static double getOrderItemSubTotal(GenericValue orderItem, List adjustments, boolean forTax, boolean forShipping) {
        Double unitPrice = orderItem.getDouble("unitPrice");
        Double quantity = getOrderItemQuantity(orderItem);
        double result = 0.0;

        if (unitPrice == null || quantity == null) {
            Debug.logWarning("[getOrderItemTotal] unitPrice or quantity are null, using 0 for the item base price", module);
        } else {
            if (Debug.verboseOn()) Debug.logVerbose("Unit Price : " + unitPrice.doubleValue() + " / " + "Quantity : " + quantity.doubleValue(), module);
            result = unitPrice.doubleValue() * quantity.doubleValue();
        }

        // subtotal also includes non tax and shipping adjustments; tax and shipping will be calculated using this adjusted value
        result += getOrderItemAdjustmentsTotal(orderItem, adjustments, true, false, false, forTax, forShipping);

        return result;
    }

    public static double getOrderItemsTotal(List orderItems, List adjustments) {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(orderItems);

        while (itemIter != null && itemIter.hasNext()) {
            result += getOrderItemTotal((GenericValue) itemIter.next(), adjustments);
        }
        return result;
    }

    public static double getOrderItemTotal(GenericValue orderItem, List adjustments) {
        // add tax and shipping to subtotal
        return (getOrderItemSubTotal(orderItem, adjustments) + getOrderItemAdjustmentsTotal(orderItem, adjustments, false, true, true));
    }

    public static double getAllOrderItemsAdjustmentsTotal(List orderItems, List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        double result = 0.0;
        Iterator itemIter = UtilMisc.toIterator(orderItems);

        while (itemIter != null && itemIter.hasNext()) {
            result += getOrderItemAdjustmentsTotal((GenericValue) itemIter.next(), adjustments, includeOther, includeTax, includeShipping);
        }
        return result;
    }

    /** The passed adjustments can be all adjustments for the order, ie for all line items */
    public static double getOrderItemAdjustmentsTotal(GenericValue orderItem, List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping) {
        return getOrderItemAdjustmentsTotal(orderItem, adjustments, includeOther, includeTax, includeShipping, false, false);
    }

    /** The passed adjustments can be all adjustments for the order, ie for all line items */
    public static double getOrderItemAdjustmentsTotal(GenericValue orderItem, List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping, boolean forTax, boolean forShipping) {
        return calcItemAdjustments(getOrderItemQuantity(orderItem), orderItem.getDouble("unitPrice"),
                getOrderItemAdjustmentList(orderItem, adjustments),
                includeOther, includeTax, includeShipping, forTax, forShipping);
    }

    public static List getOrderItemAdjustmentList(GenericValue orderItem, List adjustments) {
        return EntityUtil.filterByAnd(adjustments, UtilMisc.toMap("orderItemSeqId", orderItem.get("orderItemSeqId")));
    }

    public static List getOrderItemStatuses(GenericValue orderItem, List orderStatuses) {
        return EntityUtil.filterByAnd(orderStatuses, UtilMisc.toMap("orderItemSeqId", orderItem.get("orderItemSeqId")));
    }

    // Order Item Adjs Utility Methods

    public static double calcItemAdjustments(Double quantity, Double unitPrice, List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping, boolean forTax, boolean forShipping) {
        double adjTotal = 0.0;

        if (adjustments != null && adjustments.size() > 0) {
            List filteredAdjs = filterOrderAdjustments(adjustments, includeOther, includeTax, includeShipping, forTax, forShipping);
            Iterator adjIt = filteredAdjs.iterator();

            while (adjIt.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) adjIt.next();

                adjTotal += OrderReadHelper.calcItemAdjustment(orderAdjustment, quantity, unitPrice);
            }
        }
        return adjTotal;
    }

    public static double calcItemAdjustment(GenericValue itemAdjustment, GenericValue item) {
        return calcItemAdjustment(itemAdjustment, getOrderItemQuantity(item), item.getDouble("unitPrice"));
    }

    public static double calcItemAdjustment(GenericValue itemAdjustment, Double quantity, Double unitPrice) {
        double adjustment = 0.0;

        if (itemAdjustment.get("amount") != null) {
            adjustment += itemAdjustment.getDouble("amount").doubleValue();
        }
        if (itemAdjustment.get("amountPerQuantity") != null && quantity != null) {
            adjustment += itemAdjustment.getDouble("amountPerQuantity").doubleValue() * quantity.doubleValue();
        }
        if (itemAdjustment.get("percentage") != null && unitPrice != null) {
            adjustment += (itemAdjustment.getDouble("percentage").doubleValue() * unitPrice.doubleValue());
        }
        if (Debug.verboseOn())
            Debug.logVerbose("calcItemAdjustment: " + itemAdjustment + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", adjustment=" + adjustment, module);
        return adjustment;
    }

    public static List filterOrderAdjustments(List adjustments, boolean includeOther, boolean includeTax, boolean includeShipping, boolean forTax, boolean forShipping) {
        List newOrderAdjustmentsList = new LinkedList();

        if (adjustments != null && adjustments.size() > 0) {
            Iterator adjIt = adjustments.iterator();

            while (adjIt.hasNext()) {
                GenericValue orderAdjustment = (GenericValue) adjIt.next();

                boolean includeAdjustment = false;

                if ("SALES_TAX".equals(orderAdjustment.getString("orderAdjustmentTypeId"))) {
                    if (includeTax) includeAdjustment = true;
                } else if ("SHIPPING_CHARGES".equals(orderAdjustment.getString("orderAdjustmentTypeId"))) {
                    if (includeShipping) includeAdjustment = true;
                } else {
                    if (includeOther) includeAdjustment = true;
                }

                // default to yes, include for shipping; so only exclude if includeInShipping is N, or false; if Y or null or anything else it will be included
                if (forTax && "N".equals(orderAdjustment.getString("includeInTax"))) {
                    includeAdjustment = false;
                }

                // default to yes, include for shipping; so only exclude if includeInShipping is N, or false; if Y or null or anything else it will be included
                if (forShipping && "N".equals(orderAdjustment.getString("includeInShipping"))) {
                    includeAdjustment = false;
                }

                if (includeAdjustment) {
                    newOrderAdjustmentsList.add(orderAdjustment);
                }
            }
        }
        return newOrderAdjustmentsList;
    }

    /**
     * Checks to see if this user has read permission on the specified order
     * @param userLogin The UserLogin value object to check
     * @param orderHeader The OrderHeader for the specified order
     * @return boolean True if we have read permission
     */
    public static boolean hasPermission(Security security, GenericValue userLogin, GenericValue orderHeader) {
        if (userLogin == null || orderHeader == null)
            return false;

        if (security.hasEntityPermission("ORDERMGR", "_VIEW", userLogin)) {
            return true;
        } else if (security.hasEntityPermission("ORDERMGR", "_ROLEVIEW", userLogin)) {
            List orderRoles = null;
            try {
                orderRoles = orderHeader.getRelatedByAnd("OrderRole",
                        UtilMisc.toMap("partyId", userLogin.getString("partyId")));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot get OrderRole from OrderHeader", module);
            }

            if (orderRoles.size() > 0) {
                // we are in at least one role
                return true;
            }
        }

        return false;
    }

    public static OrderReadHelper getHelper(GenericValue orderHeader) {
        return new OrderReadHelper(orderHeader);
    }
}
