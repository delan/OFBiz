/*
 * $Id: OrderReadHelper.java,v 1.21 2004/02/24 10:09:02 jonesde Exp $
 *
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
import java.util.*;

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
 * @version    $Revision: 1.21 $
 * @since      2.0
 */
public class OrderReadHelper {

    public static final String module = OrderReadHelper.class.getName();

    protected GenericValue orderHeader = null;
    protected List orderItems = null;
    protected List adjustments = null;
    protected List paymentPrefs = null;
    protected List orderStatuses = null;
    protected List orderItemPriceInfos = null;
    protected List orderItemInventoryReses = null;
    protected List orderItemIssuances = null;
    protected Double totalPrice = null;

    protected OrderReadHelper() {}

    public OrderReadHelper(GenericValue orderHeader, List adjustments, List orderItems) {
        this.orderHeader = orderHeader;
        this.adjustments = adjustments;
        this.orderItems = orderItems;
    }

    public OrderReadHelper(GenericValue orderHeader) {
        this.orderHeader = orderHeader;
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
                paymentPrefs = orderHeader.getRelated("OrderPaymentPreference");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        }
        return paymentPrefs;
    }

    public List getOrderPayments() {
        List orderPayments = new ArrayList();
        List prefs = getPaymentPreferences();
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

    public GenericValue getShippingAddress() {
        GenericDelegator delegator = orderHeader.getDelegator();

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

    public GenericValue getBillingAddress() {
        GenericDelegator delegator = orderHeader.getDelegator();
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
        GenericDelegator delegator = orderHeader.getDelegator();

        try {
            GenericEntity distributorRole = EntityUtil.getFirst(orderHeader.getRelatedByAnd("OrderRole", UtilMisc.toMap("roleTypeId", "DISTRIBUTOR")));

            return distributorRole == null ? null : distributorRole.getString("partyId");
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return null;
    }

    public String getAffiliateId() {
        GenericDelegator delegator = orderHeader.getDelegator();

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

    public Map getFeatureIdQtyMap() {
        Map featureMap = new HashMap();
        List validItems = getValidOrderItems();
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

    public double getShippableTotal() {
        double shippableTotal = 0.00;
        List validItems = getValidOrderItems();
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

    public double getShippableQuantity() {
        double shippableQuantity = 0.00;
        List validItems = getValidOrderItems();
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

    public double getShippableWeight() {
        GenericDelegator delegator = orderHeader.getDelegator();
        double shippableWeight = 0.00;
        List validItems = getValidOrderItems();
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
                            shippableWeight += weight.doubleValue();
                        }
                    }
                }
            }
        }
        return shippableWeight;
    }

    public List getShippableSizes() {
        GenericDelegator delegator = orderHeader.getDelegator();
        List shippableSizes = new LinkedList();

        List validItems = getValidOrderItems();
        if (validItems != null) {
            Iterator i = validItems.iterator();
            while (i.hasNext()) {
                GenericValue item = (GenericValue) i.next();
                GenericValue product = null;
                try {
                    product = item.getRelatedOne("Product");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problem getting Product from OrderItem", module);
                    return shippableSizes;
                }
                if (product != null) {
                    if (ProductWorker.shippingApplies(product)) {
                        Double height = product.getDouble("productHeight");
                        Double width = product.getDouble("productWidth");
                        Double depth = product.getDouble("productDepth");
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
                                height = virtual.getDouble("productHeight");
                                width = virtual.getDouble("productWidth");
                                depth = virtual.getDouble("productDepth");
                            }
                        }

                        if (height == null) height = new Double(0);
                        if (width == null) width = new Double(0);
                        if (depth == null) depth = new Double(0);
                        shippableSizes.add(new Double(height.doubleValue() * width.doubleValue() * depth.doubleValue()));
                    }
                }
            }
        }
        return shippableSizes;
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
        return getOrderHeaderAdjustments(getAdjustments());
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
        return (List) orderItems;
    }

    public List getValidOrderItems() {
        List exprs = UtilMisc.toList(
                new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"),
                new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ITEM_REJECTED"));
        return EntityUtil.filterByAnd(getOrderItems(), exprs);
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

    public List getOrderItemInventoryReses(GenericValue orderItem) {
        if (orderItem == null) return null;
        if (this.orderItemInventoryReses == null) {
            GenericDelegator delegator = orderItem.getDelegator();

            try {
                orderItemInventoryReses = delegator.findByAnd("OrderItemInventoryRes", UtilMisc.toMap("orderId", orderItem.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, "Trouble getting OrderItemInventoryRes(s)", module);
            }
        }
        return EntityUtil.filterByAnd(orderItemInventoryReses, UtilMisc.toMap("orderItemSeqId", orderItem.getString("orderItemSeqId")));
    }

    public static List getOrderItemInventoryResFacilityIds(GenericValue orderHeader) {
        GenericDelegator delegator = orderHeader.getDelegator();
        List orderItems = null;
        List orderItemInventoryRes = new ArrayList();
        List result = new ArrayList();

        // filter for approved items only
        try {
            orderItems = delegator.findByAnd("OrderItem", UtilMisc.toMap("orderId", orderHeader.getString("orderId"), "statusId", "ITEM_APPROVED"));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot locate OrderItems from OrderHeader " + orderHeader.getString("orderId"), module);
        }
        if (UtilValidate.isNotEmpty(orderItems)) {
            Iterator oiIter = orderItems.iterator();
            GenericValue orderItem = null;
            List oiInventoryRes = null;

            while (oiIter.hasNext()) {
                orderItem = (GenericValue) oiIter.next();

                try {
                    oiInventoryRes = orderItem.getRelated("OrderItemInventoryRes");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Cannot locate OrderItemInventoryRes from OrderItem " + orderItem.getString("orderId") + " sequenceNum " + orderItem.getString("orderItemSeqId"), module);
                }

                if (UtilValidate.isNotEmpty(oiInventoryRes)) {
                    orderItemInventoryRes.addAll(oiInventoryRes);
                }
                if (oiInventoryRes.size() > 1) {
                    Debug.logWarning("Warning - Should not use quickShip with more than one orderItemInventoryRes for order " + orderHeader.getString("orderId") + " item sequenceNum " + orderItem.get("orderItemsSeqId"), module);
                }
            }
            if (UtilValidate.isNotEmpty(orderItemInventoryRes)) {
                Iterator orderItemInventoryResIter = orderItemInventoryRes.iterator();
                GenericValue anInventoryRes = null;

                while (orderItemInventoryResIter.hasNext()) {
                    anInventoryRes = (GenericValue) orderItemInventoryResIter.next();
                    GenericValue inventoryItem = null;

                    try {
                        inventoryItem = delegator.findByPrimaryKey("InventoryItem", UtilMisc.toMap("inventoryItemId", anInventoryRes.getString("inventoryItemId")));
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Cannot locate InventoryItem for ID " + anInventoryRes.getString("inventoryItemId"), module);
                    }
                    result.add(inventoryItem.getString("facilityId"));
                }
            }
        }
        return result;
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

    public double getOrderReturnedQuantity() {
        GenericDelegator delegator = orderHeader.getDelegator();
        List returnedItems = null;
        try {
            returnedItems = delegator.findByAnd("ReturnItem", UtilMisc.toMap("orderId", orderHeader.getString("orderId")));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting ReturnItem from order", module);
            return -1;
        }

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

        List reses = getOrderItemInventoryReses(orderItem);
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

        List reses = getOrderItemInventoryReses(orderItem);
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
        Double cancelQty = orderItem.getDouble("cancelQuantity");
        Double orderQty = orderItem.getDouble("quantity");

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

    public static List getOrderHeaderAdjustments(List adjustments) {
        List contraints1 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, null));
        List contraints2 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, DataModelConstants.SEQ_ID_NA));
        List contraints3 = UtilMisc.toList(new EntityExpr("orderItemSeqId", EntityOperator.EQUALS, ""));
        List adj = new LinkedList();

        adj.addAll(EntityUtil.filterByAnd(adjustments, contraints1));
        adj.addAll(EntityUtil.filterByAnd(adjustments, contraints2));
        adj.addAll(EntityUtil.filterByAnd(adjustments, contraints3));
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
        newOrderStatuses = EntityUtil.orderBy(newOrderStatuses, UtilMisc.toList("statusDatetime"));
        return newOrderStatuses;
    }

    public static double getOrderAdjustmentsTotal(List orderItems, List adjustments) {
        return calcOrderAdjustments(getOrderHeaderAdjustments(adjustments), getOrderItemsSubTotal(orderItems, adjustments), true, true, true);
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
                includeOther, includeTax, includeShipping, false, false);
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
