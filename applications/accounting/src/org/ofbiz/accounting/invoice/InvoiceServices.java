/*
 * $Id$
 *
 * Copyright 2003-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.accounting.invoice;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.ofbiz.accounting.payment.BillingAccountWorker;
import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.product.product.ProductWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * InvoiceServices - Services for creating invoices
 *
 * Note that throughout this file we use BigDecimal to do arithmetic. It is 
 * critical to understand the way BigDecimal works if you wish to modify the 
 * computations in this file. The most important things to keep in mind:
 *
 * Critically important: BigDecimal arigthmetic methods like add(), 
 * multiply(), divide() do not modify the BigDecimal itself. Instead, they 
 * return a new BigDecimal. For example, to keep a running total of an 
 * amount, make sure you do this:
 *
 *      amount = amount.add(subAmount);
 *
 * and not this,
 *
 *      amount.add(subAmount);
 *
 * Use .setScale(scale, roundingMode) after every computation to scale and 
 * round off the decimals. Check the code to see how the scale and 
 * roundingMode are obtained and how the function is used.
 *
 * use .compareTo() to compare big decimals
 *
 *      ex.  (amountOne.compareTo(amountTwo) == 1) 
 *           checks if amountOne is greater than amountTwo
 *
 * Use .signum() to test if value is negative, zero, or positive
 *
 *      ex.  (amountOne.signum() == 1) 
 *           checks if the amount is a positive non-zero number
 *
 * Never use the .equals() function becaues it considers 2.0 not equal to 2.00 (the scale is different)
 * Instead, use .compareTo() or .signum(), which handles scale correctly.
 *
 * For reference, check the official Sun Javadoc on java.math.BigDecimal.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:sichen@opensourcestrategies.com">Si Chen</a> 
 * @author     <a href="mailto:leon@opensourcestrategies.com">Leon Torres</a> 
 * @version    $Rev$
 * @since      2.2
 */
public class InvoiceServices {

    public static String module = InvoiceServices.class.getName();

    // set some BigDecimal properties
    private static BigDecimal ZERO = new BigDecimal("0");
    private static int decimals = -1;
    private static int rounding = -1;
    static {
        decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
        rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");

        // set zero to the proper scale
        if (decimals != -1) ZERO.setScale(decimals);
    }

    /* Service to create an invoice for an order */
    public static Map createInvoiceForOrder(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        if (decimals == -1 || rounding == -1) {
            return ServiceUtil.returnError("Arithmetic properties for Invoice services not configured properly. Cannot proceed.");
        }

        String orderId = (String) context.get("orderId");
        List billItems = (List) context.get("billItems");
        boolean previousInvoiceFound = false;

        if (billItems == null || billItems.size() == 0) {
            Debug.logVerbose("No order items to invoice; not creating invoice; returning success", module);
            return ServiceUtil.returnSuccess("No order items to invoice, not creating invoice.");
        }

        try {
            List toStore = new LinkedList();
            GenericValue orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
            if (orderHeader == null) {
                return ServiceUtil.returnError("No OrderHeader, cannot create invoice");
            }

            // get list of previous invoices for the order
            List billedItems = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("orderId", orderId));
            if (billedItems != null && billedItems.size() > 0) {
                boolean nonDigitalInvoice = false;
                Iterator bii = billedItems.iterator();
                while (bii.hasNext() && !nonDigitalInvoice) {
                    GenericValue orderItemBilling = (GenericValue) bii.next();
                    GenericValue invoiceItem = orderItemBilling.getRelatedOne("InvoiceItem");
                    if (invoiceItem != null) {
                        String invoiceItemType = invoiceItem.getString("invoiceItemTypeId");
                        if (invoiceItemType != null) {
                            if ("INV_FPROD_ITEM".equals(invoiceItemType) || "INV_PROD_FEATR_ITEM".equals(invoiceItemType)) {
                                nonDigitalInvoice = true;
                            }
                        }
                    }
                }
                if (nonDigitalInvoice) {
                    previousInvoiceFound = true;
                }
            }

            // figure out the invoice type
            String invoiceType = null;

            String orderType = orderHeader.getString("orderTypeId");
            if (orderType.equals("SALES_ORDER")) {
                invoiceType = "SALES_INVOICE";
            } else if (orderType.equals("PURCHASE_ORDER")) {
                invoiceType = "PURCHASE_INVOICE";
            }

            OrderReadHelper orh = new OrderReadHelper(orderHeader);

            // get the billing parties
            String billToCustomerPartyId = orh.getBillToParty().getString("partyId");
            String billFromVendorPartyId = orh.getBillFromParty().getString("partyId");

            // get some quantity totals
            BigDecimal totalItemsInOrder = orh.getTotalOrderItemsQuantityBd();

            // get some price totals
            BigDecimal shippableAmount = orh.getShippableTotalBd(null);
            BigDecimal orderSubTotal = orh.getOrderItemsSubTotalBd();

            BigDecimal invoiceShipProRateAmount = ZERO;
            BigDecimal invoiceSubTotal = ZERO;
            BigDecimal invoiceQuantity = ZERO;

            GenericValue billingAccount = orderHeader.getRelatedOne("BillingAccount");
            String billingAccountId = billingAccount != null ? billingAccount.getString("billingAccountId") : null;

            // TODO: ideally this should be the same time as when a shipment is sent and be passed in as a parameter 
            Timestamp invoiceDate = UtilDateTime.nowTimestamp();
            // TODO: perhaps consider billing account net days term as well?
            Timestamp dueDate = UtilDateTime.getDayEnd(invoiceDate, orh.getOrderTermNetDays().intValue());
            
            // create the invoice record
            Map createInvoiceContext = FastMap.newInstance();
            createInvoiceContext.put("partyId", billToCustomerPartyId);
            createInvoiceContext.put("partyIdFrom", billFromVendorPartyId);
            createInvoiceContext.put("billingAccountId", billingAccountId);
            createInvoiceContext.put("invoiceDate", invoiceDate);
            createInvoiceContext.put("dueDate", dueDate);
            createInvoiceContext.put("invoiceTypeId", invoiceType);
            // start with INVOICE_IN_PROCESS, in the INVOICE_READY we can't change the invoice (or shouldn't be able to...)
            createInvoiceContext.put("statusId", "INVOICE_IN_PROCESS");
            createInvoiceContext.put("currencyUomId", orderHeader.getString("currencyUom"));
            createInvoiceContext.put("userLogin", userLogin);

            // store the invoice first
            Map createInvoiceResult = dispatcher.runSync("createInvoice", createInvoiceContext);
            if (ServiceUtil.isError(createInvoiceResult)) {
                return ServiceUtil.returnError("Error creating invoice from order", null, null, createInvoiceResult);
            }
            
            // call service, not direct entity op: delegator.create(invoice);
            String invoiceId = (String) createInvoiceResult.get("invoiceId");

            // order roles to invoice roles
            List orderRoles = orderHeader.getRelated("OrderRole");
            if (orderRoles != null) {
                Iterator orderRolesIt = orderRoles.iterator();
                Map createInvoiceRoleContext = FastMap.newInstance();
                createInvoiceRoleContext.put("invoiceId", invoiceId);
                createInvoiceRoleContext.put("userLogin", userLogin);
                while (orderRolesIt.hasNext()) {
                    GenericValue orderRole = (GenericValue)orderRolesIt.next();
                    createInvoiceRoleContext.put("partyId", orderRole.getString("partyId"));
                    createInvoiceRoleContext.put("roleTypeId", orderRole.getString("roleTypeId"));
                    Map createInvoiceRoleResult = dispatcher.runSync("createInvoiceRole", createInvoiceRoleContext);
                    if (ServiceUtil.isError(createInvoiceRoleResult)) {
                        return ServiceUtil.returnError("Error creating invoice role from order", null, null, createInvoiceRoleResult);
                    }
                }
            }

            // order terms to invoice terms.  Implemented for purchase orders, although it may be useful
            // for sales orders as well.  Later it might be nice to filter OrderTerms to only copy over financial terms.
            List orderTerms = orh.getOrderTerms();
            toStore.addAll(createInvoiceTerms(delegator, invoiceId, orderTerms));

            // billing accounts
            List billingAccountTerms = null;
            // for billing accounts we will use related information
            if (billingAccount != null) {
                // get the billing account terms
                billingAccountTerms = billingAccount.getRelated("BillingAccountTerm");

                // set the invoice terms as defined for the billing account
                toStore.addAll(createInvoiceTerms(delegator, invoiceId, billingAccountTerms));

                // set the invoice bill_to_customer from the billing account
                List billToRoles = billingAccount.getRelated("BillingAccountRole", UtilMisc.toMap("roleTypeId", "BILL_TO_CUSTOMER"), null);
                Iterator billToIter = billToRoles.iterator();
                while (billToIter.hasNext()) {
                    GenericValue billToRole = (GenericValue) billToIter.next();
                    if (!(billToRole.getString("partyId").equals(billToCustomerPartyId))) {
                        GenericValue invoiceRole = delegator.makeValue("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId));
                        invoiceRole.set("partyId", billToRole.get("partyId"));
                        invoiceRole.set("roleTypeId", "BILL_TO_CUSTOMER");
                        toStore.add(invoiceRole);
                    }
                }

                // set the bill-to contact mech as the contact mech of the billing account
                if (UtilValidate.isNotEmpty(billingAccount.getString("contactMechId"))) {
                    GenericValue billToContactMech = delegator.makeValue("InvoiceContactMech", UtilMisc.toMap("invoiceId", invoiceId));
                    billToContactMech.set("contactMechId", billingAccount.getString("contactMechId"));
                    billToContactMech.set("contactMechPurposeTypeId", "BILLING_LOCATION");
                    toStore.add(billToContactMech);
                }
            } else {
                List billingLocations = orh.getBillingLocations();
                if (billingLocations != null) {
                    Iterator bli = billingLocations.iterator();
                    while (bli.hasNext()) {
                        GenericValue ocm = (GenericValue) bli.next();
                        GenericValue billToContactMech = delegator.makeValue("InvoiceContactMech", UtilMisc.toMap("invoiceId", invoiceId));
                        billToContactMech.set("contactMechId", ocm.getString("contactMechId"));
                        billToContactMech.set("contactMechPurposeTypeId", "BILLING_LOCATION");
                        toStore.add(billToContactMech);
                    }
                }
            }

            // get a list of the payment method types
            //DEJ20050705 doesn't appear to be used: List paymentPreferences = orderHeader.getRelated("OrderPaymentPreference");

            // get the product store
            GenericValue productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", orh.getProductStoreId()));

            // create the bill-from (or pay-to) contact mech as the primary PAYMENT_LOCATION of the party from the store
            GenericValue payToAddress = null;
            if (invoiceType.equals("PURCHASE_INVOICE")) {
                // for purchase orders, the pay to address is the BILLING_LOCATION of the vendor
                GenericValue billFromVendor = orh.getPartyFromRole("BILL_FROM_VENDOR");
                if (billFromVendor != null) {
                    List billingContactMechs = billFromVendor.getRelatedOne("Party").getRelatedByAnd("PartyContactMechPurpose",
                            UtilMisc.toMap("contactMechPurposeTypeId", "BILLING_LOCATION"));
                    if ((billingContactMechs != null) && (billingContactMechs.size() > 0)) {
                        payToAddress = (GenericValue) billingContactMechs.get(0);
                    }
                }
            } else {
                // for sales orders, it is the payment address on file for the store
                payToAddress = PaymentWorker.getPaymentAddress(delegator, productStore.getString("payToPartyId"));
            }
            if (payToAddress != null) {
                GenericValue payToCm = delegator.makeValue("InvoiceContactMech", UtilMisc.toMap("invoiceId", invoiceId));
                payToCm.set("contactMechId", payToAddress.getString("contactMechId"));
                payToCm.set("contactMechPurposeTypeId", "PAYMENT_LOCATION");
                toStore.add(payToCm);
            }

            // sequence for items - all OrderItems or InventoryReservations + all Adjustments
            int invoiceItemSeqNum = 1;
            String invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, 2);

            // create the item records
            if (billItems != null) {
                Iterator itemIter = billItems.iterator();
                while (itemIter.hasNext()) {
                    GenericValue itemIssuance = null;
                    GenericValue orderItem = null;
                    GenericValue shipmentReceipt = null;
                    GenericValue currentValue = (GenericValue) itemIter.next();
                    if ("ItemIssuance".equals(currentValue.getEntityName())) {
                        itemIssuance = currentValue;
                    } else if ("OrderItem".equals(currentValue.getEntityName())) {
                        orderItem = currentValue;
                    } else if ("ShipmentReceipt".equals(currentValue.getEntityName())) {
                        shipmentReceipt = currentValue;
                    } else {
                        Debug.logError("Unexpected entity " + currentValue + " of type " + currentValue.getEntityName(), module);
                    }

                    if (orderItem == null && itemIssuance != null) {
                        orderItem = itemIssuance.getRelatedOne("OrderItem");
                    } else if ((orderItem == null) && (shipmentReceipt != null)) {
                        orderItem = shipmentReceipt.getRelatedOne("OrderItem");
                    } else if ((orderItem == null) && (itemIssuance == null) && (shipmentReceipt == null)) {
                        Debug.logError("Cannot create invoice when orderItem, itemIssuance, and shipmentReceipt are all null", module);
                        return ServiceUtil.returnError("Illegal values passed to create invoice service");
                    }
                    GenericValue product = null;
                    if (orderItem.get("productId") != null) {
                        product = orderItem.getRelatedOne("Product");
                    }

                    // get some quantities
                    BigDecimal orderedQuantity = orderItem.getBigDecimal("quantity");
                    BigDecimal billingQuantity = null;
                    if (itemIssuance != null) {
                        billingQuantity = itemIssuance.getBigDecimal("quantity");
                    } else if (shipmentReceipt != null) {
                        billingQuantity = shipmentReceipt.getBigDecimal("quantityAccepted");
                    } else {
                        billingQuantity = orderedQuantity;
                    }
                    if (orderedQuantity == null) orderedQuantity = ZERO;
                    if (billingQuantity == null) billingQuantity = ZERO;

                    String lookupType = "FINISHED_GOOD"; // the default product type
                    if (product != null) {
                        lookupType = product.getString("productTypeId");
                    } else if (orderItem != null) {
                        lookupType = orderItem.getString("orderItemTypeId");
                    }

                    // check if shipping applies to this item.  Shipping is calculated for sales invoices, not purchase invoices.
                    boolean shippingApplies = false;
                    if ((product != null) && (ProductWorker.shippingApplies(product)) && (invoiceType.equals("SALES_INVOICE"))) {
                        shippingApplies = true;
                    }

                    GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId));
                    invoiceItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, lookupType, invoiceType, "INV_FPROD_ITEM"));
                    invoiceItem.set("description", orderItem.get("itemDescription"));
                    invoiceItem.set("quantity", new Double(billingQuantity.doubleValue()));
                    invoiceItem.set("amount", orderItem.get("unitPrice"));
                    invoiceItem.set("productId", orderItem.get("productId"));
                    invoiceItem.set("productFeatureId", orderItem.get("productFeatureId"));
                    invoiceItem.set("overrideGlAccountId", orderItem.get("overrideGlAccountId"));
                    //invoiceItem.set("uomId", "");

                    String itemIssuanceId = null;
                    if (itemIssuance != null && itemIssuance.get("inventoryItemId") != null) {
                        itemIssuanceId = itemIssuance.getString("itemIssuanceId");
                        invoiceItem.set("inventoryItemId", itemIssuance.get("inventoryItemId"));
                    }
                    // similarly, tax only for purchase invoices
                    if ((product != null) && (invoiceType.equals("SALES_INVOICE"))) {
                        invoiceItem.set("taxableFlag", product.get("taxable"));
                    }
                    toStore.add(invoiceItem);

                    // this item total
                    BigDecimal thisAmount = invoiceItem.getBigDecimal("amount").multiply(invoiceItem.getBigDecimal("quantity")).setScale(decimals, rounding);

                    // add to the ship amount only if it applies to this item
                    if (shippingApplies) {
                        invoiceShipProRateAmount = invoiceShipProRateAmount.add(thisAmount).setScale(decimals, rounding);
                    }

                    // increment the invoice subtotal
                    invoiceSubTotal = invoiceSubTotal.add(thisAmount).setScale(decimals, rounding);

                    // increment the invoice quantity
                    invoiceQuantity = invoiceQuantity.add(billingQuantity).setScale(decimals, rounding);

                    // create the OrderItemBilling record
                    GenericValue orderItemBill = delegator.makeValue("OrderItemBilling", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId));
                    orderItemBill.set("orderId", orderItem.get("orderId"));
                    orderItemBill.set("orderItemSeqId", orderItem.get("orderItemSeqId"));
                    orderItemBill.set("itemIssuanceId", itemIssuanceId);
                    if ((shipmentReceipt != null) && (shipmentReceipt.getString("receiptId") != null)) {
                        orderItemBill.set("shipmentReceiptId", shipmentReceipt.getString("receiptId"));
                    }
                    orderItemBill.set("quantity", invoiceItem.get("quantity"));
                    orderItemBill.set("amount", invoiceItem.get("amount"));
                    toStore.add(orderItemBill);

                    // increment the counter
                    invoiceItemSeqNum++;
                    invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, 2);

                    // create the item adjustment as line items
                    List itemAdjustments = OrderReadHelper.getOrderItemAdjustmentList(orderItem, orh.getAdjustments());
                    Iterator itemAdjIter = itemAdjustments.iterator();
                    while (itemAdjIter.hasNext()) {
                        GenericValue adj = (GenericValue) itemAdjIter.next();
                        if (adj.get("amount") != null) {
                            // pro-rate the amount

                            // set decimals = 100 means we don't round this intermediate value, which is very important
                            BigDecimal amount = adj.getBigDecimal("amount").divide(orderItem.getBigDecimal("quantity"), 100, rounding);
                            amount = amount.multiply(invoiceItem.getBigDecimal("quantity"));
                            amount = amount.setScale(decimals, rounding);
                            GenericValue adjInvItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId));
                            adjInvItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), invoiceType, "INVOICE_ITM_ADJ"));
                            adjInvItem.set("productId", orderItem.get("productId"));
                            adjInvItem.set("productFeatureId", orderItem.get("productFeatureId"));
                            //adjInvItem.set("uomId", "");
                            
                            // invoice items for sales tax are not taxable themselves
                            // TODO: This is not an ideal solution. Instead, we need to use OrderAdjustment.includeInTax when it is implemented
                            if (!(adj.getString("orderAdjustmentTypeId").equals("SALES_TAX"))) {
                                adjInvItem.set("taxableFlag", product.get("taxable"));    
                            }
                            adjInvItem.set("quantity", new Double(1));
                            adjInvItem.set("amount", new Double(amount.doubleValue()));
                            adjInvItem.set("description", adj.get("description"));
                            adjInvItem.set("taxAuthPartyId", adj.get("taxAuthPartyId"));
                            adjInvItem.set("overrideGlAccountId", adj.get("overrideGlAccountId"));
                            adjInvItem.set("taxAuthGeoId", adj.get("taxAuthGeoId"));
                            toStore.add(adjInvItem);
                            // this adjustment amount
                            BigDecimal thisAdjAmount = adjInvItem.getBigDecimal("amount").multiply(adjInvItem.getBigDecimal("quantity")).setScale(decimals, rounding);

                            // adjustments only apply to totals when they are not tax or shipping adjustments
                            if (!"SALES_TAX".equals(adj.getString("orderAdjustmentTypeId")) &&
                                    !"SHIPPING_ADJUSTMENT".equals(adj.getString("orderAdjustmentTypeId"))) {
                                // increment the invoice subtotal
                                invoiceSubTotal = invoiceSubTotal.add(thisAdjAmount).setScale(decimals, rounding);

                                // add to the ship amount only if it applies to this item
                                if (shippingApplies) {
                                    invoiceShipProRateAmount = invoiceShipProRateAmount.add(thisAdjAmount).setScale(decimals, rounding);
                                }
                            }

                            // increment the counter
                            invoiceItemSeqNum++;
                            invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, 2);
                        }
                    }
                }
            }

            // get the shipping adjustment mode (Y = Pro-Rate; N = First-Invoice)
            String prorateShipping = productStore.getString("prorateShipping");
            if (prorateShipping == null) {
                prorateShipping = "Y";
            }

            // create header adjustments as line items -- always to tax/shipping last
            List shipAdjustments = new ArrayList();
            List taxAdjustments = new ArrayList();

            List headerAdjustments = orh.getOrderHeaderAdjustments();
            Iterator headerAdjIter = headerAdjustments.iterator();
            while (headerAdjIter.hasNext()) {
                GenericValue adj = (GenericValue) headerAdjIter.next();
                if ("SHIPPING_CHARGES".equals(adj.getString("orderAdjustmentTypeId"))) {
                    shipAdjustments.add(adj);
                } else if ("SALES_TAX".equals(adj.getString("orderAdjustmentTypeId"))) {
                    taxAdjustments.add(adj);
                } else {
                    // these will effect the shipping pro-rate (unless commented)
                    // other adjustment type
                    BigDecimal adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, toStore, 
                            orderSubTotal, invoiceSubTotal, invoiceQuantity, decimals, rounding);
                    // invoiceShipProRateAmount += adjAmount;
                    // do adjustments compound or are they based off subtotal? Here we will (unless commented)
                    // invoiceSubTotal += adjAmount;

                    // increment the counter
                    invoiceItemSeqNum++;
                    invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, 2);
                }
            }

            // next do the shipping adjustments
            Iterator shipAdjIter = shipAdjustments.iterator();
            while (shipAdjIter.hasNext()) {
                GenericValue adj = (GenericValue) shipAdjIter.next();
                if ("N".equalsIgnoreCase(prorateShipping)) {
                    if (previousInvoiceFound) {
                        Debug.logInfo("Previous invoice found for this order [" + orderId + "]; shipping already billed", module);
                        continue;
                    } else {
                        // this is the first invoice; bill it all now
                        BigDecimal adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, toStore, 
                                new BigDecimal("1"), new BigDecimal("1"), totalItemsInOrder, decimals, rounding);
                        // should shipping effect the tax pro-rate?
                        invoiceSubTotal = invoiceSubTotal.add(adjAmount).setScale(decimals, rounding); // here we do

                        // increment the counter
                        invoiceItemSeqNum++;
                        invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, 2);
                    }
                } else {
                    // pro-rate the shipping amount based on shippable information
                    BigDecimal adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, toStore, 
                            shippableAmount, invoiceShipProRateAmount, invoiceQuantity, decimals, rounding);
                    // should shipping effect the tax pro-rate?
                    invoiceSubTotal = invoiceSubTotal.add(adjAmount).setScale(decimals, rounding); // here we do

                    // increment the counter
                    invoiceItemSeqNum++;
                    invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, 2);
                }
            }

            // last do the tax adjustments
            Iterator taxAdjIter = taxAdjustments.iterator();
            while (taxAdjIter.hasNext()) {
                GenericValue adj = (GenericValue) taxAdjIter.next();
                BigDecimal adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, invoiceItemSeqId, toStore, 
                        orderSubTotal, invoiceSubTotal, invoiceQuantity, decimals, rounding);
                // this doesn't really effect anything; but just for our totals
                invoiceSubTotal = invoiceSubTotal.add(adjAmount).setScale(decimals, rounding);
            }

            // check for previous order payments
            List orderPaymentPrefs = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId));
            if (orderPaymentPrefs != null) {
                List currentPayments = new ArrayList();
                Iterator opi = orderPaymentPrefs.iterator();
                while (opi.hasNext()) {
                    GenericValue paymentPref = (GenericValue) opi.next();
                    List payments = paymentPref.getRelated("Payment");
                    currentPayments.addAll(payments);
                }
                if (currentPayments.size() > 0) {
                    // apply these payments to the invoice; only if they haven't already been applied
                    Iterator cpi = currentPayments.iterator();
                    while (cpi.hasNext()) {
                        GenericValue payment = (GenericValue) cpi.next();
                        List currentApplications = null;
                        currentApplications = payment.getRelated("PaymentApplication");
                        if (currentApplications == null || currentApplications.size() == 0) {
                            // no applications; okay to apply
                            Map appl = new HashMap();
                            appl.put("paymentId", payment.get("paymentId"));
                            appl.put("invoiceId", invoiceId);
                            appl.put("billingAccountId", billingAccountId);
                            appl.put("amountApplied", payment.get("amount"));
                            appl.put("userLogin", userLogin);
                            Map createPayApplResult = dispatcher.runSync("createPaymentApplication", appl); 
                            if (ServiceUtil.isError(createPayApplResult)) {
                                return ServiceUtil.returnError("Error creating invoice from order", null, null, createPayApplResult);
                            }
                        }
                    }
                }
            }

            // store value objects
            //Debug.log("Storing : " + toStore, module);
            // TODO BIG TIME: need to get rid of the storeAll/toStore stuff and call all services for these things rather than direct entity ops
            delegator.storeAll(toStore);

            // should all be in place now, so set status to INVOICE_READY (unless it's a purchase invoice, which we sets to INVOICE_IN_PROCESS) 
            String nextStatusId = "INVOICE_READY";
            if (invoiceType.equals("PURCHASE_INVOICE")) {
                nextStatusId = "INVOICE_IN_PROCESS";
            }
            Map setInvoiceStatusResult = dispatcher.runSync("setInvoiceStatus", UtilMisc.toMap("invoiceId", invoiceId, "statusId", nextStatusId, "userLogin", userLogin));
            if (ServiceUtil.isError(setInvoiceStatusResult)) {
                return ServiceUtil.returnError("Error creating invoice from order", null, null, setInvoiceStatusResult);
            }

            // check to see if we are all paid up
            Map checkResp = dispatcher.runSync("checkInvoicePaymentApplications", UtilMisc.toMap("invoiceId", invoiceId, "userLogin", userLogin));
            if (ServiceUtil.isError(checkResp)) {
                return ServiceUtil.returnError("Error creating invoice from order while checking payment applications", null, null, checkResp);
            }

            Map resp = ServiceUtil.returnSuccess();
            resp.put("invoiceId", invoiceId);
            return resp;
        } catch (GenericEntityException e) {
            String errMsg = "Entity/data problem creating invoice from order items: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        } catch (GenericServiceException e) {
            String errMsg = "Service/other problem creating invoice from order items: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }
    }

    public static Map createInvoicesFromShipment(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String shipmentId = (String) context.get("shipmentId");

        List invoicesCreated = new ArrayList();

        GenericValue shipment = null;
        try {
            shipment = delegator.findByPrimaryKey("Shipment", UtilMisc.toMap("shipmentId", shipmentId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting Shipment entity", module);
            return ServiceUtil.returnError("Trouble getting Shipment entity");
        }

        // check the status of the shipment

        // get the items of the shipment.  They can come from ItemIssuance if the shipment were from a sales order or ShipmentReceipt
        // if it were a purchase order
        List items = null;
        try {
            if ((shipment.getString("shipmentTypeId") != null) && (shipment.getString("shipmentTypeId").equals("PURCHASE_SHIPMENT"))) {
                items = delegator.findByAnd("ShipmentReceipt", UtilMisc.toMap("shipmentId", shipmentId));
            } else {
                items = delegator.findByAnd("ItemIssuance", UtilMisc.toMap("shipmentId", shipmentId));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting issued items from Shipment [" + shipmentId + "]", module);
            return ServiceUtil.returnError("Problem getting issued items from Shipment [" + shipmentId + "]");
        }
        if (items == null) {
            Debug.logInfo("No items issued for shipment [" + shipmentId + "]", module);
            return ServiceUtil.returnSuccess();
        }

        // group items by order
        Map shippedOrderItems = new HashMap();
        Iterator itemsIter = items.iterator();
        while (itemsIter.hasNext()) {
            GenericValue item = (GenericValue) itemsIter.next();
            String orderId = item.getString("orderId");
            String orderItemSeqId = item.getString("orderItemSeqId");
            List itemsByOrder = (List) shippedOrderItems.get(orderId);
            if (itemsByOrder == null) {
                itemsByOrder = new ArrayList();
            }

            // check and make sure we haven't already billed for this issuance or shipment receipt
            Map billFields = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId);
            if (item.getEntityName().equals("ItemIssuance")) {
                billFields.put("itemIssuanceId", item.get("itemIssuanceId"));
            } else if (item.getEntityName().equals("ShipmentReceipt")) {
                billFields.put("shipmentReceiptId", item.getString("receiptId"));
            }
            List itemBillings = null;
            try {
                itemBillings = delegator.findByAnd("OrderItemBilling", billFields);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem looking up OrderItemBilling records for : " + billFields, module);
                return ServiceUtil.returnError("Problem getting OrderItemBilling records");
            }

            // if none found, then okay to bill
            if (itemBillings == null || itemBillings.size() == 0) {
                itemsByOrder.add(item);
            }

            // update the map with modified list
            shippedOrderItems.put(orderId, itemsByOrder);
        }

        // make sure we aren't billing items already invoiced i.e. items billed as digital (FINDIG)
        Set orders = shippedOrderItems.keySet();
        Iterator ordersIter = orders.iterator();
        while (ordersIter.hasNext()) {
            String orderId = (String) ordersIter.next();

            // we'll only use this list to figure out which ones to send
            List billItems = (List) shippedOrderItems.get(orderId);

            // a new list to be used to pass to the create invoice service
            List toBillItems = new ArrayList();

            // map of available quantities so we only have to calc once
            Map itemQtyAvail = new HashMap();

            // now we will check each issuance and make sure it hasn't already been billed
            Iterator billIt = billItems.iterator();
            while (billIt.hasNext()) {
                GenericValue issue = (GenericValue) billIt.next();
                BigDecimal issueQty = ZERO;
                if (issue.getEntityName().equals("ShipmentReceipt")) {
                    issueQty = issue.getBigDecimal("quantityAccepted");
                } else {
                    issueQty = issue.getBigDecimal("quantity");
                }

                BigDecimal billAvail = (BigDecimal) itemQtyAvail.get(issue.getString("orderItemSeqId"));
                if (billAvail == null) {
                    Map lookup = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", issue.get("orderItemSeqId"));
                    GenericValue orderItem = null;
                    List billed = null;
                    try {
                        orderItem = issue.getRelatedOne("OrderItem");
                        billed = delegator.findByAnd("OrderItemBilling", lookup);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Problem looking up OrderItem/OrderItemBilling records for : " + lookup, module);
                        return ServiceUtil.returnError("Problem getting OrderItem/OrderItemBilling records");
                    }

                    // total ordered
                    BigDecimal orderedQty = orderItem.getBigDecimal("quantity");

                    // add up the already billed total
                    if (billed != null && billed.size() > 0) {
                        BigDecimal billedQuantity = ZERO;
                        Iterator bi = billed.iterator();
                        while (bi.hasNext()) {
                            GenericValue oib = (GenericValue) bi.next();
                            BigDecimal qty = oib.getBigDecimal("quantity");
                            if (qty != null) {
                                billedQuantity = billedQuantity.add(qty).setScale(decimals, rounding);
                            }
                        }
                        BigDecimal leftToBill = orderedQty.subtract(billedQuantity).setScale(decimals, rounding);
                        billAvail = leftToBill;
                    } else {
                        billAvail = orderedQty;
                    }
                }

                // no available means we cannot bill anymore
                if (billAvail != null && billAvail.signum() == 1) { // this checks if billAvail is a positive non-zero number
                    if (issueQty != null && issueQty.doubleValue() > billAvail.doubleValue()) {
                        // can only bill some of the issuance; others have been billed already
                        issue.set("quantity", new Double(billAvail.doubleValue()));
                        billAvail = ZERO;
                    } else {
                        // now have been billed
                        billAvail = billAvail.subtract(issueQty).setScale(decimals, rounding);
                    }

                    // okay to bill these items; but none else
                    toBillItems.add(issue);
                }

                // update the available to bill quantity for the next pass
                itemQtyAvail.put(issue.getString("orderItemSeqId"), billAvail);
            }

            // call the createInvoiceForOrder service for each order
            Map serviceContext = UtilMisc.toMap("orderId", orderId, "billItems", toBillItems, "userLogin", context.get("userLogin"));
            try {
                Map result = dispatcher.runSync("createInvoiceForOrder", serviceContext);
                invoicesCreated.add(result.get("invoiceId"));
            } catch (GenericServiceException e) {
                Debug.logError(e, "Trouble calling createInvoiceForOrder service; invoice not created for shipment", module);
                return ServiceUtil.returnError("Trouble calling createInvoiceForOrder service; invoice not created for shipment");
            }
        }

        Map response = ServiceUtil.returnSuccess();
        response.put("invoicesCreated", invoicesCreated);
        return response;
    }

    private static String getInvoiceItemType(GenericDelegator delegator, String key, String invoiceTypeId, String defaultValue) {
        GenericValue itemMap = null;
        try {
            itemMap = delegator.findByPrimaryKey("InvoiceItemTypeMap", UtilMisc.toMap("invoiceItemMapKey", key, "invoiceTypeId", invoiceTypeId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting InvoiceItemTypeMap entity", module);
            return defaultValue;
        }
        if (itemMap != null) {
            return itemMap.getString("invoiceItemTypeId");
        } else {
            return defaultValue;
        }
    }

    public static Map createInvoicesFromReturnShipment(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();

        String shipmentId = (String) context.get("shipmentId");
        String errorMsg = "Error creating invoice for shipment [" + shipmentId + "]. ";
        List invoicesCreated = new ArrayList();
        try {

            // get the shipment and validate that it is a sales return
            GenericValue shipment = delegator.findByPrimaryKey("Shipment", UtilMisc.toMap("shipmentId", shipmentId));
            if (shipment == null) {
                return ServiceUtil.returnError(errorMsg + "Shipment not found.");
            }
            if (!shipment.getString("shipmentTypeId").equals("SALES_RETURN")) {
                return ServiceUtil.returnError(errorMsg + "Shipment is not of type SALES_RETURN.");
            }

            // get the list of ShipmentReceipt for this shipment
            List shipmentReceipts = shipment.getRelated("ShipmentReceipt");

            // group the shipments by returnId (because we want a seperate itemized invoice for each return)
            Map receiptsGroupedByReturn = new HashMap();
            for (Iterator iter = shipmentReceipts.iterator(); iter.hasNext(); ) {
                GenericValue receipt = (GenericValue) iter.next();
                String returnId = receipt.getString("returnId");

                // see if there are ReturnItemBillings for this item
                List billings = delegator.findByAnd("ReturnItemBilling", UtilMisc.toMap("shipmentReceiptId", receipt.getString("receiptId"), "returnId", returnId, 
                            "returnItemSeqId", receipt.get("returnItemSeqId")));

                // if there are billings, we have already billed the item, so skip it
                if (billings.size() > 0) continue;

                // get the List of receipts keyed to this returnId or create a new one
                List receipts = (List) receiptsGroupedByReturn.get(returnId);
                if (receipts == null) {
                    receipts = new ArrayList();
                }

                // add our item to the group and put it back in the map
                receipts.add(receipt);
                receiptsGroupedByReturn.put(returnId, receipts);
            }

            // loop through the returnId keys in the map and invoke the createInvoiceFromReturn service for each
            for (Iterator iter = receiptsGroupedByReturn.keySet().iterator(); iter.hasNext(); ) {
                String returnId = (String) iter.next();
                List receipts = (List) receiptsGroupedByReturn.get(returnId);
                if (Debug.verboseOn()) {
                    Debug.logVerbose("Creating invoice for return [" + returnId + "] with receipts: " + receipts.toString(), module);
                }
                Map input = UtilMisc.toMap("returnId", returnId, "shipmentReceiptsToBill", receipts, "userLogin", context.get("userLogin"));
                Map serviceResults = dispatcher.runSync("createInvoiceFromReturn", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
                }

                // put the resulting invoiceId in the return list
                invoicesCreated.add(serviceResults.get("invoiceId"));
            }
        } catch (GenericServiceException e) {
            Debug.logError(e, errorMsg + e.getMessage(), module);
            return ServiceUtil.returnError(errorMsg + e.getMessage());
        } catch (GenericEntityException e) {
            Debug.logError(e, errorMsg + e.getMessage(), module);
            return ServiceUtil.returnError(errorMsg + e.getMessage());
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("invoicesCreated", invoicesCreated);
        return result;
    }

    public static Map createInvoiceFromReturn(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String returnId= (String) context.get("returnId");
        List receipts = (List) context.get("shipmentReceiptsToBill");
        String errorMsg = "Error creating invoice for return [" + returnId + "]. ";
        List invoicesCreated = new ArrayList();
        try {
            // get the return header
            GenericValue returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));

            // set the invoice data
            Map input = UtilMisc.toMap("invoiceTypeId", "CUST_RTN_INVOICE", "statusId", "INVOICE_IN_PROCESS");
            input.put("partyId", returnHeader.get("toPartyId"));
            input.put("partyIdFrom", returnHeader.get("fromPartyId"));
            input.put("currencyUomId", returnHeader.get("currencyUomId"));
            input.put("invoiceDate", UtilDateTime.nowTimestamp());
            input.put("billingAccountId", returnHeader.get("billingAccountId"));
            input.put("userLogin", userLogin);

            // call the service to create the invoice
            Map serviceResults = dispatcher.runSync("createInvoice", input);
            if (ServiceUtil.isError(serviceResults)) {
                return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
            }
            String invoiceId = (String) serviceResults.get("invoiceId");

            // keep track of the invoice total vs the promised return total (how much the customer promised to return)
            BigDecimal invoiceTotal = ZERO;
            BigDecimal promisedTotal = ZERO;

            // loop through shipment receipts to create invoice items and return item billings for each item and adjustment
            int invoiceItemSeqNum = 1;
            String invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, 2);
            for (Iterator iter = receipts.iterator(); iter.hasNext(); ) {
                GenericValue receipt = (GenericValue) iter.next();

                // we need the related return item and product
                GenericValue returnItem = receipt.getRelatedOneCache("ReturnItem");
                GenericValue product = returnItem.getRelatedOneCache("Product");

                // extract the return price as a big decimal for convenience
                BigDecimal returnPrice = returnItem.getBigDecimal("returnPrice");

                // determine invoice item type from the return item type
                String invoiceItemTypeId = getInvoiceItemType(delegator, returnItem.getString("returnItemTypeId"), "CUST_RTN_INVOICE", null);
                if (invoiceItemTypeId == null) {
                    return ServiceUtil.returnError(errorMsg + "No known invoice item type for the return item type [" 
                            +  returnItem.getString("returnItemTypeId") + "]");
                }

                // create the invoice item for this shipment receipt
                input = UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemTypeId", invoiceItemTypeId, "quantity", receipt.get("quantityAccepted"));
                input.put("invoiceItemSeqId", "" + invoiceItemSeqId); // turn the int into a string with ("" + int) hack
                input.put("amount", returnItem.get("returnPrice")); // this service requires Double
                input.put("productId", returnItem.get("productId"));
                input.put("taxableFlag", product.get("taxable"));
                input.put("description", returnItem.get("description"));
                // TODO: what about the productFeatureId?
                input.put("userLogin", userLogin);
                serviceResults = dispatcher.runSync("createInvoiceItem", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
                }

                // copy the return item information into ReturnItemBilling
                input = UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItem.get("returnItemSeqId"), 
                        "invoiceId", invoiceId);
                input.put("invoiceItemSeqId", "" + invoiceItemSeqId); // turn the int into a string with ("" + int) hack
                input.put("shipmentReceiptId", receipt.get("receiptId"));
                input.put("quantity", receipt.get("quantityAccepted"));
                input.put("amount", returnItem.get("returnPrice")); // this service requires Double
                input.put("userLogin", userLogin);
                serviceResults = dispatcher.runSync("createReturnItemBilling", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
                }
                if (Debug.verboseOn()) {
                    Debug.logVerbose("Creating Invoice Item with amount " + returnPrice + " and quantity " + receipt.getBigDecimal("quantityAccepted") 
                            + " for shipment receipt [" + receipt.getString("receiptId") + "]", module);
                }

                // increment the seqId counter after creating the invoice item and return item billing
                invoiceItemSeqNum += 1;  
                invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, 2);

                // keep a running total (note: a returnItem may have many receipts. hence, the promised total quantity is the receipt quantityAccepted + quantityRejected)
                BigDecimal actualAmount = returnPrice.multiply(receipt.getBigDecimal("quantityAccepted")).setScale(decimals, rounding);
                BigDecimal promisedAmount = returnPrice.multiply(receipt.getBigDecimal("quantityAccepted").add(receipt.getBigDecimal("quantityRejected"))).setScale(decimals, rounding);
                invoiceTotal = invoiceTotal.add(actualAmount).setScale(decimals, rounding);
                promisedTotal = promisedTotal.add(promisedAmount).setScale(decimals, rounding);

                // for each adjustment related to this ReturnItem, create a separate invoice item
                List adjustments = returnItem.getRelatedCache("ReturnAdjustment");
                for (Iterator adjIter = adjustments.iterator(); adjIter.hasNext(); ) {
                    GenericValue adjustment = (GenericValue) adjIter.next();

                    // determine invoice item type from the return item type
                    invoiceItemTypeId = getInvoiceItemType(delegator, adjustment.getString("returnAdjustmentTypeId"), "CUST_RTN_INVOICE", null);
                    if (invoiceItemTypeId == null) {
                        return ServiceUtil.returnError(errorMsg + "No known invoice item type for the return adjustment type [" 
                                +  adjustment.getString("returnAdjustmentTypeId") + "]");
                    }

                    // prorate the adjustment amount by the returned amount; do not round ratio
                    BigDecimal ratio = receipt.getBigDecimal("quantityAccepted").divide(returnItem.getBigDecimal("returnQuantity"), 100, rounding);
                    BigDecimal amount = adjustment.getBigDecimal("amount");
                    amount = amount.multiply(ratio).setScale(decimals, rounding);
                    if (Debug.verboseOn()) {
                        Debug.logVerbose("Creating Invoice Item with amount " + adjustment.getBigDecimal("amount") + " prorated to " + amount 
                                + " for return adjustment [" + adjustment.getString("returnAdjustmentId") + "]", module);
                    }

                    // prepare invoice item data for this adjustment
                    input = UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemTypeId", invoiceItemTypeId, "quantity", new Double(1.0));
                    input.put("amount", new Double(amount.doubleValue()));
                    input.put("invoiceItemSeqId", "" + invoiceItemSeqId); // turn the int into a string with ("" + int) hack
                    input.put("productId", returnItem.get("productId"));
                    input.put("description", adjustment.get("description"));
                    input.put("overrideGlAccountId", adjustment.get("overrideGlAccountId"));
                    input.put("taxAuthPartyId", adjustment.get("taxAuthPartyId"));
                    input.put("taxAuthGeoId", adjustment.get("taxAuthGeoId"));
                    input.put("userLogin", userLogin);

                    // only set taxable flag when the adjustment is not a tax 
                    // TODO: Note that we use the value of Product.taxable here. This is not an ideal solution. Instead, use returnAdjustment.includeInTax
                    if (adjustment.get("returnAdjustmentTypeId").equals("RET_SALES_TAX_ADJ")) {
                        input.put("taxableFlag", "N");
                    }

                    // create the invoice item
                    serviceResults = dispatcher.runSync("createInvoiceItem", input);
                    if (ServiceUtil.isError(serviceResults)) {
                        return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
                    }

                    // increment the seqId counter
                    invoiceItemSeqNum += 1;  
                    invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, 2);

                    // keep a running total (promised adjustment in this case is the same as the invoice adjustment)
                    invoiceTotal = invoiceTotal.add(amount).setScale(decimals, rounding);
                    promisedTotal = promisedTotal.add(amount).setScale(decimals, rounding);
                }
            }

            // ratio of the invoice total to the promised total so far or zero if the amounts were zero
            BigDecimal actualToPromisedRatio = ZERO;
            if (invoiceTotal.signum() != 0) {
                actualToPromisedRatio = invoiceTotal.divide(promisedTotal, 100, rounding);  // do not round ratio
            }

            // loop through return-wide adjustments and create invoice items for each
            List adjustments = returnHeader.getRelatedByAndCache("ReturnAdjustment", UtilMisc.toMap("returnItemSeqId", "_NA_"));
            for (Iterator iter = adjustments.iterator(); iter.hasNext(); ) {
                GenericValue adjustment = (GenericValue) iter.next();

                // determine invoice item type from the return item type
                String invoiceItemTypeId = getInvoiceItemType(delegator, adjustment.getString("returnAdjustmentTypeId"), "CUST_RTN_INVOICE", null);
                if (invoiceItemTypeId == null) {
                    return ServiceUtil.returnError(errorMsg + "No known invoice item type for the return adjustment type [" 
                            +  adjustment.getString("returnAdjustmentTypeId") + "]");
                }

                // prorate the adjustment amount by the actual to promised ratio
                BigDecimal amount = adjustment.getBigDecimal("amount").multiply(actualToPromisedRatio).setScale(decimals, rounding);
                if (Debug.verboseOn()) {
                    Debug.logVerbose("Creating Invoice Item with amount " + adjustment.getBigDecimal("amount") + " prorated to " + amount 
                            + " for return adjustment [" + adjustment.getString("returnAdjustmentId") + "]", module);
                }

                // prepare the invoice item for the return-wide adjustment
                input = UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemTypeId", invoiceItemTypeId, "quantity", new Double(1.0));
                input.put("amount", new Double(amount.doubleValue()));
                input.put("invoiceItemSeqId", "" + invoiceItemSeqId); // turn the int into a string with ("" + int) hack
                input.put("description", adjustment.get("description"));
                input.put("overrideGlAccountId", adjustment.get("overrideGlAccountId"));
                input.put("taxAuthPartyId", adjustment.get("taxAuthPartyId"));
                input.put("taxAuthGeoId", adjustment.get("taxAuthGeoId"));
                input.put("userLogin", userLogin);

                // XXX TODO Note: we need to implement ReturnAdjustment.includeInTax for this to work properly
                input.put("taxableFlag", adjustment.get("includeInTax"));

                // create the invoice item
                serviceResults = dispatcher.runSync("createInvoiceItem", input);
                if (ServiceUtil.isError(serviceResults)) {
                    return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
                }

                // increment the seqId counter
                invoiceItemSeqNum += 1;  
                invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum, 2);
            }
        
            // Set the invoice to READY
            serviceResults = dispatcher.runSync("setInvoiceStatus", UtilMisc.toMap("invoiceId", invoiceId, "statusId", "INVOICE_READY", "userLogin", userLogin));
            if (ServiceUtil.isError(serviceResults)) {
                return ServiceUtil.returnError(errorMsg, null, null, serviceResults);
            }

            // return the invoiceId
            Map results = ServiceUtil.returnSuccess();
            results.put("invoiceId", invoiceId);
            return results;
        } catch (GenericServiceException e) {
            Debug.logError(e, errorMsg + e.getMessage(), module);
            return ServiceUtil.returnError(errorMsg + e.getMessage());
        } catch (GenericEntityException e) {
            Debug.logError(e, errorMsg + e.getMessage(), module);
            return ServiceUtil.returnError(errorMsg + e.getMessage());
        }
    }

    public static Map checkInvoicePaymentApplications(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        if (decimals == -1 || rounding == -1) {
            return ServiceUtil.returnError("Arithmetic properties for Invoice services not configured properly. Cannot proceed.");
        }

        String invoiceId = (String) context.get("invoiceId");
        List paymentAppl = null;
        try {
            paymentAppl = delegator.findByAnd("PaymentApplication", UtilMisc.toMap("invoiceId", invoiceId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting PaymentApplication(s) for Invoice #" + invoiceId, module);
            return ServiceUtil.returnError("Problem getting PaymentApplication(s) for Invoice #" + invoiceId);
        }

        Map payments = new HashMap();
        Timestamp paidDate = null;
        if (paymentAppl != null) {
            Iterator pai = paymentAppl.iterator();
            while (pai.hasNext()) {
                GenericValue payAppl = (GenericValue) pai.next();
                payments.put(payAppl.getString("paymentId"), payAppl.getBigDecimal("amountApplied"));
                
                // paidDate will be the last date (chronologically) of all the Payments applied to this invoice
                try {
                    GenericValue Payment = payAppl.getRelatedOne("Payment");
                    Timestamp paymentDate = Payment.getTimestamp("effectiveDate");
                    if (paymentDate != null) {
                        if ((paidDate == null) || (paidDate.before(paymentDate))) {
                            paidDate = paymentDate;
                        }
                    }    
                } catch (GenericEntityException ex) {
                    Debug.logError(ex, "Cannot get payment for application [" + payAppl + "]", module);
                    return ServiceUtil.returnError("Cannot get payment for application [" + payAppl + "] due to " + ex.getMessage());
                }
                
            }
        }

        BigDecimal totalPayments = ZERO;
        Iterator pi = payments.keySet().iterator();
        while (pi.hasNext()) {
            String paymentId = (String) pi.next();
            BigDecimal amount = (BigDecimal) payments.get(paymentId);
            if (amount == null) amount = ZERO;
            totalPayments = totalPayments.add(amount).setScale(decimals, rounding);
        }

        if (totalPayments.signum() == 1) {
            BigDecimal invoiceTotal = InvoiceWorker.getInvoiceTotalBd(delegator, invoiceId);
            if (Debug.verboseOn()) {
                Debug.logVerbose("Invoice #" + invoiceId + " total: " + invoiceTotal, module);
                Debug.logVerbose("Total payments : " + totalPayments, module);
            }
            if (totalPayments.compareTo(invoiceTotal) >= 0) { // this checks that totalPayments is greater than or equal to invoiceTotal
                // this invoice is paid
                Map svcCtx = UtilMisc.toMap("statusId", "INVOICE_PAID", "invoiceId", invoiceId, 
                        "paidDate", paidDate, "userLogin", userLogin);
                try {
                    dispatcher.runSync("setInvoiceStatus", svcCtx);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem changing invoice status : " + svcCtx, module);
                    return ServiceUtil.returnError("Problem changing invoice status");
                }
            }
        } else {
            Debug.log("No payments found for Invoice #" + invoiceId, module);
        }

        return ServiceUtil.returnSuccess();
    }

    private static BigDecimal calcHeaderAdj(GenericDelegator delegator, GenericValue adj, String invoiceTypeId, String invoiceId, String invoiceItemSeqId, List toStore, 
            BigDecimal divisor, BigDecimal multiplier, BigDecimal invoiceQuantity, int decimals, int rounding) {
        BigDecimal adjAmount = ZERO;
        if (adj.get("amount") != null) {
            // pro-rate the amount
            BigDecimal baseAdjAmount = adj.getBigDecimal("amount");
            BigDecimal amount = ZERO;
            // make sure the divisor is not 0 to avoid NaN problems; just leave the amount as 0 and skip it in essense
            if (divisor.signum() != 0) {
                // multiply first then divide to avoid rounding errors
                amount = baseAdjAmount.multiply(multiplier).divide(divisor, decimals, rounding);
            }
            if (amount.signum() != 0) {
                GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId));
                invoiceItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), invoiceTypeId, "INVOICE_ADJ"));
                //invoiceItem.set("productId", orderItem.get("productId"));
                //invoiceItem.set("productFeatureId", orderItem.get("productFeatureId"));
                //invoiceItem.set("uomId", "");
                //invoiceItem.set("taxableFlag", product.get("taxable"));
                invoiceItem.set("quantity", new Double(1));
                invoiceItem.set("amount", new Double(amount.doubleValue()));
                invoiceItem.set("description", adj.get("description"));
                toStore.add(invoiceItem);
            }
            amount.setScale(decimals, rounding);
            adjAmount = amount;
        }

        if (Debug.verboseOn()) {
            Debug.logVerbose("adjAmount: " + adjAmount + ", divisor: " + divisor + ", multiplier: " + multiplier + 
                ", invoiceTypeId: " + invoiceTypeId + ", invoiceId: " + invoiceId + ", itemSeqId: " + invoiceItemSeqId + ", adj: " + adj, module);
        }
        return adjAmount;
    }

    /* Creates InvoiceTerm entries for a list of terms, which can be BillingAccountTerms, OrderTerms, etc. */
    private static List createInvoiceTerms(GenericDelegator delegator, String invoiceId, List terms) {
        List invoiceTerms = new LinkedList();
        if ((terms != null) && (terms.size() > 0)) {
            for (Iterator termsIter = terms.iterator(); termsIter.hasNext(); ) {
                GenericValue term = (GenericValue) termsIter.next();
                GenericValue invoiceTerm = delegator.makeValue("InvoiceTerm",
                    UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", "_NA_"));
                String invoiceTermId = delegator.getNextSeqId("InvoiceTerm").toString();
                invoiceTerm.set("invoiceTermId", invoiceTermId);
                invoiceTerm.set("termTypeId", term.get("termTypeId"));
                invoiceTerm.set("termValue", term.get("termValue"));
                invoiceTerm.set("termDays", term.get("termDays"));
                invoiceTerm.set("uomId", term.get("uomId"));
                invoiceTerms.add(invoiceTerm);
            }
        }
        return invoiceTerms;
    }

    /**
     * Service to add payment application records to indicate which invoices
     * have been paid/received. For invoice processing, this service works on
     * the invoice level when 'invoiceProcessing' parameter is set to "Y" else
     * it works on the invoice item level.
     * 
     * @author <a href="mailto:h.bakker@antwebsystems.com">Hans Bakker</a>
     */
    public static Map updatePaymentApplication(DispatchContext dctx, Map context) {
        Double amountApplied = (Double) context.get("amountApplied");
        if (amountApplied != null) {
            BigDecimal amountAppliedBd = new BigDecimal(amountApplied.toString());
            context.put("amountApplied", amountAppliedBd);
        } else {
            BigDecimal amountAppliedBd = ZERO;
            context.put("amountApplied", amountAppliedBd);
        }

        return updatePaymentApplicationBd(dctx, context);
    }

    public static Map updatePaymentApplicationBd(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();

        if (decimals == -1 || rounding == -1) {
            return ServiceUtil.returnError("Arithmetic properties for Invoice services not configured properly. Cannot proceed.");
        }

        String defaultInvoiceProcessing = UtilProperties.getPropertyValue("AccountingConfig","invoiceProcessing").toString();
        
        boolean debug = true; // show processing messages in the log..or
                                // not....

        // a 'y' in invoiceProssesing wil reverse the default processing
        String changeProcessing = (String) context.get("invoiceProcessing");
        String invoiceId = (String) context.get("invoiceId");
        String invoiceItemSeqId = (String) context.get("invoiceItemSeqId");
        String paymentId = (String) context.get("paymentId");
        String toPaymentId = (String) context.get("toPaymentId");
        String paymentApplicationId = (String) context.get("paymentApplicationId");
        BigDecimal amountApplied = (BigDecimal) context.get("amountApplied");
        String billingAccountId = (String) context.get("billingAccountId");
        String taxAuthGeoId = (String) context.get("taxAuthGeoId");

        List errorMessageList = new LinkedList();
        if (debug) Debug.logInfo("Input parameters..." + 
        		" defaultInvoiceProcessing: " + defaultInvoiceProcessing + 
        		" changeDefaultInvoiceProcessing: " + changeProcessing + 
        		" paymentApplicationId: " + paymentApplicationId + 
        		" PaymentId: " + paymentId + 
        		" InvoiceId: " + invoiceId + 
        		" InvoiceItemSeqId: " + invoiceItemSeqId + 
        		" BillingAccountId: " + billingAccountId + 
        		" toPaymentId: " + toPaymentId + 
        		" amountApplied: " + amountApplied.toString() + 
        		" TaxAuthGeoId: " + taxAuthGeoId, module);

        if (changeProcessing == null) changeProcessing = new String("N");	// not provided, so no change
        
        boolean invoiceProcessing = true;
        if (defaultInvoiceProcessing.equals("YY")) invoiceProcessing = true;

        else if (defaultInvoiceProcessing.equals("NN")) invoiceProcessing = false;

        else if (defaultInvoiceProcessing.equals("Y")) {
        	if (changeProcessing.equals("Y")) invoiceProcessing = false;
        	else invoiceProcessing = true;
        }

        else if (defaultInvoiceProcessing.equals("N")) {
        	if (changeProcessing.equals("Y")) invoiceProcessing = true;
        	else invoiceProcessing = false;
        }

        // on a new paymentApplication check if only billing or invoice or tax
        // id is provided not 2,3...
        if (paymentApplicationId == null) {
            int count = 0;
            if (invoiceId != null) count++;
            if (toPaymentId != null) count++;
            if (billingAccountId != null) count++;
            if (taxAuthGeoId != null) count++;
            if (count != 1) {
                errorMessageList.add("- Specify either Invoice or toPayment or billing account or taxGeoId....\n");
            }
        }

        // avoid null pointer exceptions.
        if (amountApplied == null) amountApplied = new BigDecimal("0"); 
        // makes no sense to have an item numer without an invoice number
        if (invoiceId == null) invoiceItemSeqId = null; 

        // retrieve all information and perform checking on the retrieved info.....

        // Payment.....
        BigDecimal paymentApplyAvailable = new BigDecimal("0"); 
        // amount available on the payment reduced by the already applied amounts
        BigDecimal amountAppliedMax = new BigDecimal("0"); 
        // the maximum that can be applied taking payment,invoice,invoiceitem,billing account in concideration
        // if maxApplied is missing, this value can be used
        GenericValue payment = null;
        if (paymentId == null || paymentId.equals("")) {
            errorMessageList.add("- PaymentId blank or not supplied.... .....\n");
        } else {
            try {
                payment = delegator.findByPrimaryKey("Payment", UtilMisc.toMap("paymentId", paymentId));
            } catch (GenericEntityException e) {
                ServiceUtil.returnError(e.getMessage());
            }
            if (payment == null) {
                errorMessageList.add("- Payment record not found, ID: " + paymentId + "\n");
            }
            paymentApplyAvailable = payment.getBigDecimal("amount").subtract(PaymentWorker.getPaymentAppliedBd(payment)).setScale(decimals,rounding);

            if (payment.getString("statusId").equals("PMNT_CANCELLED")) {
                errorMessageList.add("- Payment(" + paymentId + ") is cancelled and cannot be applied\n");
            }

            // if the amount to apply is 0 give it amount the payment still need
            // to apply
            if (amountApplied.signum() == 0) {
                amountAppliedMax = paymentApplyAvailable;
            }

            if (paymentApplicationId == null) { 
                // only check for new application records, update on existing records is checked in the paymentApplication section
                if (paymentApplyAvailable.signum() == 0) {
                    errorMessageList.add("- Payment(" + paymentId + ") is already fully applied\n");
                } else {
                    // check here for too much application if a new record is
                    // added (paymentApplicationId == null)
                    if (amountApplied.compareTo(paymentApplyAvailable) > 0) {
                        errorMessageList.add("- Payment(" + paymentId + ") has  " + paymentApplyAvailable + " to apply but " + amountApplied + " is requested\n");
                    }
                }
            }

            if (debug) Debug.logInfo("Payment info retrieved and checked...", module);
        }

        // the "TO" Payment.....
        BigDecimal toPaymentApplyAvailable = new BigDecimal("0"); 
        GenericValue toPayment = null;
        if (toPaymentId != null && !toPaymentId.equals("")) {
            try {
                toPayment = delegator.findByPrimaryKey("Payment", UtilMisc.toMap("paymentId", toPaymentId));
            } catch (GenericEntityException e) {
                ServiceUtil.returnError(e.getMessage());
            }
            if (toPayment == null) {
                errorMessageList.add("- toPayment record not found, ID: " + toPaymentId + "\n");
            }
            toPaymentApplyAvailable = toPayment.getBigDecimal("amount").subtract(PaymentWorker.getPaymentAppliedBd(toPayment)).setScale(decimals,rounding);

            if (toPayment.getString("statusId").equals("PMNT_CANCELLED")) {
                errorMessageList.add("- toPayment(" + toPaymentId + ") is cancelled and cannot be applied\n");
            }

            // if the amount to apply is less then required by the payment reduce it
            if (amountAppliedMax.compareTo(toPaymentApplyAvailable) > 0) {
                amountAppliedMax = toPaymentApplyAvailable;
            }

            if (paymentApplicationId == null) { 
                // only check for new application records, update on existing records is checked in the paymentApplication section
                if (toPaymentApplyAvailable.signum() == 0) {
                    errorMessageList.add("- toPayment(" + toPaymentId + ") is already fully applied\n");
                } else {
                    // check here for too much application if a new record is
                    // added (paymentApplicationId == null)
                    if (amountApplied.compareTo(toPaymentApplyAvailable) > 0) {
                        errorMessageList.add("- toPayment(" + toPaymentId + ") has  " + toPaymentApplyAvailable + " to apply but " + amountApplied + " is requested\n");
                    }
                }
            }
            
            // check if at least one send is the same as one receiver on the other payment
            if (!payment.getString("partyIdFrom").equals(toPayment.getString("partyIdTo")) && 
            		!payment.getString("partyIdFrom").equals(toPayment.getString("partyIdTo")))	{
            	errorMessageList.add("- At least the 'from' party should be the same as the 'to' party of the other payment\n");
            }

            if (debug) Debug.logInfo("toPayment info retrieved and checked...", module);
        }

        // billing account
        GenericValue billingAccount = null;
        BigDecimal billingAccountApplyAvailable = new BigDecimal("0");
        if (billingAccountId != null && !billingAccountId.equals("")) {
            try {
                billingAccount = delegator.findByPrimaryKey("BillingAccount", UtilMisc.toMap("billingAccountId", billingAccountId));
            } catch (GenericEntityException e) {
                ServiceUtil.returnError(e.getMessage());
            }
            if (billingAccount == null) {
                errorMessageList.add("- Billing Account record not found, ID: " + billingAccountId + "\n");
            }
            try {
                billingAccountApplyAvailable = billingAccount.getBigDecimal("accountLimit").add(
                        new BigDecimal(BillingAccountWorker.getBillingAccountBalance(billingAccount))).setScale(decimals,rounding);
            } catch (GenericEntityException e) {
                ServiceUtil.returnError(e.getMessage());
                errorMessageList.add("- Billing Account(" + billingAccountId + ") balance could not be retrieved, see log for more info...(\n");
            }

            if (paymentApplicationId == null) { 
                // only check when a new record is created, existing record check is done in the paymentAllication section
                if (billingAccountApplyAvailable.signum() <= 0) {
                    errorMessageList.add("- Billing Account(" + billingAccountId + " doesn't have a positive balance: " + billingAccountApplyAvailable + "\n");
                } else {
                    // check here for too much application if a new record is
                    // added (paymentApplicationId == null)
                    if (amountApplied.compareTo(billingAccountApplyAvailable) == 1) {
                        errorMessageList.add("- Billing Account(" + billingAccountId + ") has " + paymentApplyAvailable + " to apply but " + amountApplied + " is requested\n");
                    }
                }
            }

            // check the currency
            if (billingAccount.get("accountCurrencyUomId") != null && payment.get("currencyUomId") != null && 
                    !billingAccount.getString("accountCurrencyUomId").equals(payment.getString("currencyUomId"))) {
                errorMessageList.add("- Currencies are not the same, Billing Account(" + billingAccountId + ") has currency " + billingAccount.getString("accountCurrencyUomId") + " and Payment(" + paymentId + ") has a currency of: " + payment.getString("currencyUomId") + "\n");
            }

            if (debug) Debug.logInfo("Billing Account info retrieved and checked...", module);
        }

        // get the invoice (item) information
        BigDecimal invoiceApplyAvailable = new BigDecimal("0");
        // amount available on the invoice reduced by the already applied amounts
        BigDecimal invoiceItemApplyAvailable = new BigDecimal("0");
        // amount available on the invoiceItem reduced by the already applied amounts
        GenericValue invoice = null;
    	GenericValue invoiceItem = null;
        if (invoiceId != null) {
        	try {
        		invoice = delegator.findByPrimaryKey("Invoice", UtilMisc.toMap("invoiceId", invoiceId));
        	} catch (GenericEntityException e) {
        		ServiceUtil.returnError(e.getMessage());
        	}
        	
        	if (invoice == null) {
        		errorMessageList.add("- Invoice record not found, ID: " + invoiceId + "\n");
        	}
        	else { // check the invoice and when supplied the invoice item...
        		
        		if (invoice.getString("statusId").equals("INVOICE_CANCELLED")) {
        			errorMessageList.add("- Invoice is cancelled, cannot be applied to, ID: " + invoiceId + "\n");
        		}
        		
        		// check if the invoice already covered by payments
        		BigDecimal invoiceTotal = InvoiceWorker.getInvoiceTotalBd(invoice);
        		invoiceApplyAvailable = invoiceTotal.subtract(InvoiceWorker.getInvoiceAppliedBd(invoice)).setScale(decimals, rounding);
        		
        		// adjust the amountAppliedMax value if required....
        		if (invoiceApplyAvailable.compareTo(amountAppliedMax) < 0) {
        			amountAppliedMax = invoiceApplyAvailable;
        		}
        		
        		if (invoiceTotal.signum() == 0) {
        			errorMessageList.add("- Invoice (" + invoiceId + ") has a total value of zero....cannot apply anything...\n");
        		} else if (paymentApplicationId == null) { 
        			// only check for new records here...updates are checked in the paymentApplication section
        			if (invoiceApplyAvailable.signum() == 0) {
        				errorMessageList.add("- Invoice (" + invoiceId + ") is already completely covered by payments... \n");
        			}
        			// check here for too much application if a new record(s) are
        			// added (paymentApplicationId == null)
        			else if (amountApplied.compareTo(invoiceApplyAvailable) > 0) {
        				errorMessageList.add("- Invoice(" + invoiceId + ") has " + invoiceApplyAvailable + " to apply but " + amountApplied + " is requested\n");
        			}
        		}
        		
        		// check if at least one sender is the same as one receiver on the invoice
        		if (!payment.getString("partyIdFrom").equals(invoice.getString("partyId")) && 
        				!payment.getString("partyIdTo").equals(invoice.getString("partyIdFrom")))	{
        			errorMessageList.add("- At least the 'from' party should be the same as the 'to' party of the payment/invoice\n");
        		}
        		
        		if (debug) Debug.logInfo("Invoice info retrieved and checked ...", module);
        	}
        	
        	// if provided check the invoice item.
        	if (invoiceItemSeqId != null) { 
        		// when itemSeqNr not provided delay checking on invoiceItemSeqId
        		try {
        			invoiceItem = delegator.findByPrimaryKey("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", invoiceItemSeqId));
        		} catch (GenericEntityException e) {
        			ServiceUtil.returnError(e.getMessage());
        		}
        		
        		if (invoiceItem == null) {
        			errorMessageList.add("- InvoiceItem record not found, ID: " + invoiceId + " " + invoiceItemSeqId + "\n");
        		} else {
        			if (invoiceItem.get("uomId") != null && payment.get("currencyUomId") != null && !invoiceItem.getString("uomId").equals(payment.getString("currencyUomId"))) {
        				errorMessageList.add("- Payment currency(" + payment.getString("currencyUomId") + ") and invoice Item currency(" + invoiceItem.getString("uomId") + ") not the same\n");
        			} else if (invoice.get("currencyUomId") != null && payment.get("currencyUomId") != null && !invoice.getString("currencyUomId").equals(payment.getString("currencyUomId"))) {
        				errorMessageList.add("- Payment currency(" + payment.getString("currencyUomId") + ") and invoice currency(" + invoice.getString("currencyUomId") + ") not the same\n");
        			}
        			
        			// get the invoice item applied value
        			BigDecimal quantity = null;
        			if (invoiceItem.get("quantity") == null) {
        				quantity = new BigDecimal("1");
        			} else {
        				quantity = invoiceItem.getBigDecimal("quantity").setScale(decimals,rounding);
        			}
        			invoiceItemApplyAvailable = invoiceItem.getBigDecimal("amount").multiply(quantity).subtract(InvoiceWorker.getInvoiceItemAppliedBd(invoiceItem));
        			// check here for too much application if a new record is added
        			// (paymentApplicationId == null)
        			if (paymentApplicationId == null && amountApplied.compareTo(invoiceItemApplyAvailable) > 0) { 
        				// new record
        				errorMessageList.add("- Invoice(" + invoiceId + ") item(" + invoiceItemSeqId + ") has  " + invoiceItemApplyAvailable + " to apply but " + amountApplied + " is requested\n");
        			}
        			
        		}
        		if (debug) Debug.logInfo("InvoiceItem info retrieved and checked against the Invoice (currency and amounts) ...", module);
        	}
        }

        // get the application record if the applicationId is supplied if not
        // create empty record.
        BigDecimal newInvoiceApplyAvailable = invoiceApplyAvailable; 
        // amount available on the invoice taking into account if the invoiceItemnumber has changed
        BigDecimal newInvoiceItemApplyAvailable = invoiceItemApplyAvailable; 
        // amount available on the invoiceItem taking into account if the itemnumber has changed
        BigDecimal newToPaymentApplyAvailable = toPaymentApplyAvailable; 
        // amount available on the Billing Account taking into account if the billing account number has changed
        BigDecimal newBillingAccountApplyAvailable = billingAccountApplyAvailable; 
        // amount available on the Billing Account taking into account if the billing account number has changed
        BigDecimal newPaymentApplyAvailable = paymentApplyAvailable;
        GenericValue paymentApplication = null;
        if (paymentApplicationId == null) {
            paymentApplication = delegator.makeValue("PaymentApplication", null); 
            // prepare for creation
        } else { // retrieve existing paymentApplication
            try {
                paymentApplication = delegator.findByPrimaryKey("PaymentApplication", UtilMisc.toMap("paymentApplicationId", paymentApplicationId));
            } catch (GenericEntityException e) {
                ServiceUtil.returnError(e.getMessage());
            }

            if (paymentApplication == null) {
                errorMessageList.add("- PaymentApplication record not found, ID: " + paymentApplicationId + "\n");
                paymentApplicationId = null;
            } else {

                // if both invoiceId and BillingId is entered there was
                // obviously a change
                // only take the newly entered item, same for tax authority and toPayment
                if (paymentApplication.get("invoiceId") == null && invoiceId != null) {
                    billingAccountId = null;
                    taxAuthGeoId = null;
                    toPaymentId = null;
                } else if (paymentApplication.get("toPaymentId") == null && toPaymentId != null) {
                    invoiceId = null;
                    invoiceItemSeqId = null;
                    taxAuthGeoId = null;
                    billingAccountId = null;
                } else if (paymentApplication.get("billingAccountId") == null && billingAccountId != null) {
                    invoiceId = null;
                    invoiceItemSeqId = null;
                    toPaymentId = null;
                    taxAuthGeoId = null;
                } else if (paymentApplication.get("taxAuthGeoId") == null && taxAuthGeoId != null) {
                    invoiceId = null;
                    invoiceItemSeqId = null;
                    toPaymentId = null;
                    billingAccountId = null;
                }

                // check if the payment for too much application if an existing
                // application record is changed
                newPaymentApplyAvailable = paymentApplyAvailable.add(paymentApplication.getBigDecimal("amountApplied")).subtract(amountApplied).setScale(decimals, rounding);
                if (newPaymentApplyAvailable.compareTo(ZERO) < 0) {
                    errorMessageList.add("- Payment(" + paymentId + ") has an amount(" + paymentApplyAvailable.add(paymentApplication.getBigDecimal("amountApplied")) + ") to apply and the  " + amountApplied + " specified is too much\n");
                }

                if (invoiceId != null) { 
                    // only when we are processing an invoice on existing paymentApplication check invoice item for to much application if the invoice
                    // number did not change
                    if (invoiceId.equals(paymentApplication .getString("invoiceId"))) {
                        // check if both the itemNumbers are null then this is a
                        // record for the whole invoice
                        if (invoiceItemSeqId == null && paymentApplication.get("invoiceItemSeqId") == null) {
                            newInvoiceApplyAvailable = invoiceApplyAvailable.add(paymentApplication.getBigDecimal("amountApplied")).subtract(amountApplied).setScale(decimals, rounding);
                            if (invoiceApplyAvailable.compareTo(ZERO) < 0) {
                                errorMessageList.add("- Update would apply " + newInvoiceApplyAvailable.negate() + " to much on this Invoice(" + invoiceId + ")\n");
                            }
                        } else if (invoiceItemSeqId == null && paymentApplication.get("invoiceItemSeqId") != null) {
                            // check if the item number changed from a real Item number to a null value
                            newInvoiceApplyAvailable = invoiceApplyAvailable.add(paymentApplication.getBigDecimal("amountApplied")).subtract(amountApplied).setScale(decimals, rounding);
                            if (invoiceApplyAvailable.compareTo(ZERO) < 0) {
                                errorMessageList.add("- Update would apply " + newInvoiceApplyAvailable.negate() + " to much on this Invoice(" + invoiceId + ")\n");
                            }
                        } else if (invoiceItemSeqId != null && paymentApplication.get("invoiceItemSeqId") == null) {
                            // check if the item number changed from a null value to
                            // a real Item number
                            newInvoiceItemApplyAvailable = invoiceItemApplyAvailable.subtract(amountApplied).setScale(decimals, rounding);
                            if (newInvoiceItemApplyAvailable.compareTo(ZERO) < 0) {
                                errorMessageList.add("- Update would apply " + newInvoiceItemApplyAvailable.negate() + " to much on this Invoice item(" + invoiceId + "/" + invoiceItemSeqId + "\n");
                            }
                        } else if (invoiceItemSeqId.equals(paymentApplication.getString("invoiceItemSeqId"))) {
                            // check if the real item numbers the same
                            // item number the same numeric value
                            newInvoiceItemApplyAvailable = invoiceItemApplyAvailable.add(paymentApplication.getBigDecimal("amountApplied")).subtract(amountApplied).setScale(decimals, rounding);
                            if (newInvoiceItemApplyAvailable.compareTo(ZERO) < 0) {
                                errorMessageList.add("- Update would apply " + newInvoiceItemApplyAvailable.negate() + " to much on this Invoice item(" + invoiceId + "/" + invoiceItemSeqId + "\n");
                            }
                        } else {
                            // item number changed only check new item
                            newInvoiceItemApplyAvailable = invoiceItemApplyAvailable.add(amountApplied).setScale(decimals, rounding);
                            if (newInvoiceItemApplyAvailable.compareTo(ZERO) < 0) {
                                errorMessageList.add("- Update would apply " + newInvoiceItemApplyAvailable.negate() + " to much on this Invoice item\n");
                            }
                        }

                        // if the amountApplied = 0 give it the higest possible
                        // value
                        if (amountApplied.signum() == 0) {
                            if (newInvoiceItemApplyAvailable.compareTo(newPaymentApplyAvailable) < 0) {
                                amountApplied = newInvoiceItemApplyAvailable; 
                                // from the item number
                            } else {
                                amountApplied = newPaymentApplyAvailable; 
                                // from the payment
                            }
                        }

                        // check the invoice
                        newInvoiceApplyAvailable = invoiceApplyAvailable.add(paymentApplication.getBigDecimal("amountApplied").subtract(amountApplied)).setScale(decimals, rounding);
                        if (newInvoiceApplyAvailable.compareTo(ZERO) < 0) {
                            errorMessageList.add("- Invoice (" + invoiceId + ") has only " + invoiceApplyAvailable.add(paymentApplication.getBigDecimal("amountApplied")) + " left which it not applied, so " + amountApplied + " is too much\n");
                        }
                    }
                }

                // check the toPayment account when only the amountApplied has
                // changed,
                if (toPaymentId != null && toPaymentId.equals(paymentApplication.getString("toPaymentId"))) {
                    newToPaymentApplyAvailable = toPaymentApplyAvailable.subtract(paymentApplication.getBigDecimal("amountApplied")).add(amountApplied).setScale(decimals, rounding);
                    if (newToPaymentApplyAvailable.compareTo(ZERO) < 0) {
                        errorMessageList.add("- toPaymentId(" + toPaymentId + ") has only " + newToPaymentApplyAvailable + " available so " + amountApplied + " is too much\n");
                    }
                } else if (toPaymentId != null) {
                    // billing account entered number has changed so we have to
                    // check the new billing account number.
                    newToPaymentApplyAvailable = toPaymentApplyAvailable.add(amountApplied).setScale(decimals, rounding);
                    if (newToPaymentApplyAvailable.compareTo(ZERO) < 0) {
                        errorMessageList.add("- toPaymentId(" + toPaymentId + ") has only "+ newToPaymentApplyAvailable + " available so " + amountApplied + " is too much\n");
                    }

                }
                // check the billing account when only the amountApplied has
                // changed,
                // change in account number is already checked in the billing
                // account section
                if (billingAccountId != null && billingAccountId.equals(paymentApplication.getString("billingAccountId"))) {
                    newBillingAccountApplyAvailable = billingAccountApplyAvailable.subtract(paymentApplication.getBigDecimal("amountApplied")).add(amountApplied).setScale(decimals, rounding);
                    if (newBillingAccountApplyAvailable.compareTo(ZERO) < 0) {
                        errorMessageList.add("- Billing Account(" + billingAccountId + ") has only " + newBillingAccountApplyAvailable + " available so " + amountApplied + " is too much\n");
                    }
                } else if (billingAccountId != null) {
                    // billing account entered number has changed so we have to
                    // check the new billing account number.
                    newBillingAccountApplyAvailable = billingAccountApplyAvailable.add(amountApplied).setScale(decimals, rounding);
                    if (newBillingAccountApplyAvailable.compareTo(ZERO) < 0) {
                        errorMessageList.add("- Billing Account(" + billingAccountId + ") has only "+ newBillingAccountApplyAvailable + " available so " + amountApplied + " is too much\n");
                    }

                }
            }
            if (debug) Debug.logInfo("paymentApplication record info retrieved and checked...", module);
        }

        // show the maximumus what can be added in the payment application file.
        if (debug) {
            String extra = new String("");
            if (invoiceItemSeqId != null) {
                extra = new String(" Invoice item(" + invoiceItemSeqId + ") amount not yet applied: " + newInvoiceItemApplyAvailable);
            }
            Debug.logInfo("checking finished, start processing withe the following data... ", module);
            if (invoiceId != null) {
            Debug.logInfo(" Invoice(" + invoiceId + ") amount not yet applied: " + newInvoiceApplyAvailable + extra + " Payment(" + paymentId + ") amount not yet applied: " + newPaymentApplyAvailable +  " Requested amount to apply:" + amountApplied, module);
            }
            if (toPaymentId != null) {
                Debug.logInfo(" toPayment(" + toPaymentId + ") amount not yet applied: " + newToPaymentApplyAvailable + " Payment(" + paymentId + ") amount not yet applied: " + newPaymentApplyAvailable + " Requested amount to apply:" + amountApplied, module);
            }
            if (billingAccountId != null) {
                Debug.logInfo(" billingAccount(" + billingAccountId + ") amount not yet applied: " + newBillingAccountApplyAvailable + " Payment(" + paymentId + ") amount not yet applied: " + newPaymentApplyAvailable + " Requested amount to apply:" + amountApplied, module);
            }
            if (taxAuthGeoId != null) {
                Debug.logInfo(" taxAuthGeoId(" + taxAuthGeoId + ")  Payment(" + paymentId + ") amount not yet applied: " + newPaymentApplyAvailable + " Requested amount to apply:" + amountApplied, module);
            }
        }

        // report error messages if any
        if (errorMessageList.size() > 0) {
            return ServiceUtil.returnError(errorMessageList);
        }

        // if the application is specified it is easy, update the existing record only
        if (paymentApplicationId != null) { 
            // record is already retrieved previously
            if (debug) Debug.logInfo("Process an existing paymentApplication record: " + paymentApplicationId, module);
            // update the current record
            paymentApplication.set("invoiceId", invoiceId);
            paymentApplication.set("invoiceItemSeqId", invoiceItemSeqId);
            paymentApplication.set("paymentId", paymentId);
            paymentApplication.set("toPaymentId", toPaymentId);
            paymentApplication.set("amountApplied", new Double(amountApplied.doubleValue()));
            paymentApplication.set("billingAccountId", billingAccountId);
            paymentApplication.set("taxAuthGeoId", taxAuthGeoId);
            return storePaymentApplication(delegator, paymentApplication);
        }

        // if no invoice sequence number is provided it assumed the requested paymentAmount will be
        // spread over the invoice starting with the lowest sequence number if
        // itemprocessing is on otherwise creat one record
        if (invoiceId != null && paymentId != null && (invoiceItemSeqId == null)) {
            if (invoiceProcessing) { 
                // create only a single record with a null seqId
                if (debug) Debug.logInfo("Try to allocate the payment to the invoice as a whole", module);
                paymentApplication.set("paymentId", paymentId);
                paymentApplication.set("toPaymentId",toPaymentId);
                paymentApplication.set("invoiceId", invoiceId);
                paymentApplication.set("invoiceItemSeqId", null);
                paymentApplication.set("toPaymentId", null);
                if (amountApplied.compareTo(ZERO) > 0) {
                    paymentApplication.set("amountApplied", new Double(amountApplied.doubleValue()));
                } else {
                    paymentApplication.set("amountApplied", new Double(amountAppliedMax.doubleValue()));
                }
                paymentApplication.set("billingAccountId", null);
                paymentApplication.set("taxAuthGeoId", null);
                if (debug) Debug.logInfo("NEW paymentapplication ID:" + paymentApplicationId + " created", module);
                return storePaymentApplication(delegator, paymentApplication);
            } else { // spread the amount over every single item number
                if (debug) Debug.logInfo("Try to allocate the payment to the itemnumbers of the invoice", module);
                // get the invoice items
                List invoiceItems = null;
                try {
                    invoiceItems = delegator.findByAnd("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId));
                } catch (GenericEntityException e) {
                    ServiceUtil.returnError(e.getMessage());
                }
                if (invoiceItems == null || invoiceItems.size() == 0) {
                    errorMessageList.add("- No invoice items found for invoice " + invoiceId + " to match payment against...\n");
                    return ServiceUtil.returnError(errorMessageList);
                } else { // we found some invoice items, start processing....
                    Iterator i = invoiceItems.iterator();
                    // check if the user want to apply a smaller amount than the maximum possible on the payment
                    if (amountApplied.signum() != 0 && amountApplied.compareTo(paymentApplyAvailable) < 0)	{
                    	paymentApplyAvailable = amountApplied;
                    }
                    while (i.hasNext() && paymentApplyAvailable.compareTo(ZERO) > 0) {
                        // get the invoiceItem
                        invoiceItem = (GenericValue) i.next();
                        if (debug) Debug.logInfo("Start processing item: " + invoiceItem.getString("invoiceItemSeqId"), module);
                        BigDecimal itemQuantity = new BigDecimal("1");
                        if (invoiceItem.get("quantity") != null && invoiceItem.getBigDecimal("quantity").signum() != 0) {
                            itemQuantity = new BigDecimal(invoiceItem.getString("quantity")).setScale(decimals,rounding);
                        }
                        BigDecimal itemAmount = invoiceItem.getBigDecimal("amount").setScale(decimals,rounding);
                        BigDecimal itemTotal = itemAmount.multiply(itemQuantity).setScale(decimals,rounding);

                        // get the application(s) already allocated to this
                        // item, if available
                        List paymentApplications = null;
                        try {
                            paymentApplications = invoiceItem.getRelated("PaymentApplication");
                        } catch (GenericEntityException e) {
                            ServiceUtil.returnError(e.getMessage());
                        }
                        BigDecimal tobeApplied = new BigDecimal("0"); 
                        // item total amount - already applied (if any)
                        BigDecimal alreadyApplied = new BigDecimal("0");
                        if (paymentApplications != null && paymentApplications.size() > 0) { 
                            // application(s) found, add them all together
                            Iterator p = paymentApplications.iterator();
                            while (p.hasNext()) {
                                paymentApplication = (GenericValue) p.next();
                                alreadyApplied = alreadyApplied.add(paymentApplication.getBigDecimal("amountApplied").setScale(decimals,rounding));
                            }
                            tobeApplied = itemTotal.subtract(alreadyApplied).setScale(decimals,rounding);
                        } else { 
                            // no application connected yet
                            tobeApplied = itemTotal;
                        }
                        if (debug) Debug.logInfo("tobeApplied:(" + tobeApplied + ") = " + "itemTotal(" + itemTotal + ") - alreadyApplied(" + alreadyApplied + ") but not more then (nonapplied) paymentAmount(" + paymentApplyAvailable + ")", module);

                        if (tobeApplied.signum() == 0) { 
                            // invoiceItem already fully applied so look at the next one....
                            continue;
                        }

                        if (paymentApplyAvailable.compareTo(tobeApplied) > 0) {
                            paymentApplyAvailable = paymentApplyAvailable.subtract(tobeApplied);
                        } else {
                            tobeApplied = paymentApplyAvailable;
                            paymentApplyAvailable = new BigDecimal("0");
                        }

                        // create application payment record but check currency
                        // first if supplied
                        if (invoiceItem.get("uomId") != null && payment.get("currencyUomId") != null && !invoiceItem.getString("uomId").equals(payment.getString("currencyUomId"))) {
                            errorMessageList.add("- Payment currency (" + payment.getString("currencyUomId") + ") and invoice Item(" + invoiceItem.getString("invoiceItemSeqId") + ") currency(" + invoiceItem.getString("uomId") + ") not the same\n");
                        } else if (invoice.get("currencyUomId") != null && payment.get("currencyUomId") != null && !invoice.getString("currencyUomId").equals( payment.getString("currencyUomId"))) {
                            errorMessageList.add("- Payment currency (" + payment.getString("currencyUomId") + ") and invoice currency(" + invoice.getString("currencyUomId") + ") not the same\n");
                        } else {
                            paymentApplication.set("paymentApplicationId", null);
                            // make sure we get a new record
                            paymentApplication.set("invoiceId", invoiceId);
                            paymentApplication.set("invoiceItemSeqId", invoiceItem.getString("invoiceItemSeqId"));
                            paymentApplication.set("paymentId", paymentId);
                            paymentApplication.set("toPaymentId", toPaymentId);
                            paymentApplication.set("amountApplied", new Double( tobeApplied.doubleValue()));
                            paymentApplication.set("billingAccountId", billingAccountId);
                            paymentApplication.set("taxAuthGeoId", taxAuthGeoId);
                            storePaymentApplication(delegator, paymentApplication);
                        }

                        // check if either the invoice or the payment is fully
                        // applied, when yes change the status to paid
                        // which triggers the ledger routines....
                        /*
                         * if
                         * (InvoiceWorker.getInvoiceTotalBd(invoice).equals(InvoiceWorker.getInvoiceAppliedBd(invoice))) {
                         * try { dispatcher.runSync("setInvoiceStatus",
                         * UtilMisc.toMap("invoiceId",invoiceId,"statusId","INVOICE_PAID")); }
                         * catch (GenericServiceException e1) {
                         * Debug.logError(e1, "Error updating invoice status",
                         * module); } }
                         * 
                         * if
                         * (payment.getBigDecimal("amount").equals(PaymentWorker.getPaymentAppliedBd(payment))) {
                         * GenericValue appliedPayment = (GenericValue)
                         * delegator.makeValue("Payment",
                         * UtilMisc.toMap("paymentId",paymentId,"statusId","INVOICE_PAID"));
                         * try { appliedPayment.store(); } catch
                         * (GenericEntityException e) {
                         * ServiceUtil.returnError(e.getMessage()); } }
                         */
                    }

                    if (errorMessageList.size() > 0) {
                        return ServiceUtil.returnError(errorMessageList);
                    } else {
                        return ServiceUtil.returnSuccess();
                    }
                }
            }
        }
        
        // if no paymentApplicationId supplied create a new record with the data
        // supplied...
        if (paymentApplicationId == null && amountApplied != null) {
            paymentApplication.set("paymentApplicationId", paymentApplicationId);
            paymentApplication.set("invoiceId", invoiceId);
            paymentApplication.set("invoiceItemSeqId", invoiceItemSeqId);
            paymentApplication.set("paymentId", paymentId);
            paymentApplication.set("toPaymentId", toPaymentId);
            paymentApplication.set("amountApplied", new Double(amountApplied.doubleValue()));
            paymentApplication.set("billingAccountId", billingAccountId);
            paymentApplication.set("taxAuthGeoId", taxAuthGeoId);
            return storePaymentApplication(delegator, paymentApplication);
        }

        // should never come here...
        errorMessageList.add("??unsuitable parameters passed...?? This message.... should never be shown\n");
        errorMessageList.add("--Input parameters...InvoiceId:" + invoiceId + " invoiceItemSeqId:" + invoiceItemSeqId + " PaymentId:" + paymentId + " toPaymentId:" + toPaymentId + "\n  paymentApplicationId:" + paymentApplicationId + " amountApplied:" + amountApplied);
        return ServiceUtil.returnError(errorMessageList);
    }

    /**
     * Update/add to the paymentApplication table and making sure no dup[licate
     * record exist
     * 
     * @param delegator
     * @param paymentApplication
     * @return map results
     */
    private static Map storePaymentApplication(GenericDelegator delegator, GenericValue paymentApplication) {
    	Map results = ServiceUtil.returnSuccess();
    	boolean debug = true;
    	if (debug) Debug.logInfo("===============Start updating the paymentApplication table ", module);

        if (decimals == -1 || rounding == -1) {
            return ServiceUtil.returnError("Arithmetic properties for Invoice services not configured properly. Cannot proceed.");
        }
    	
    	// check if a record already exists with this data
    	List checkAppls = null;
    	try {
    		checkAppls = delegator.findByAnd("PaymentApplication", UtilMisc.toMap(
    				"invoiceId", paymentApplication.get("invoiceId"),
    				"invoiceItemSeqId", paymentApplication.get("invoiceItemSeqId"),
    				"billingAccountId", paymentApplication.get("billingAccountId"), 
    				"paymentId", paymentApplication.get("paymentId"), 
    				"toPaymentId", paymentApplication.get("toPaymentId"), 
    				"taxAuthGeoId", paymentApplication.get("taxAuthGeoId")));
    	} catch (GenericEntityException e) {
    		ServiceUtil.returnError(e.getMessage());
    	}
    	if (checkAppls != null && checkAppls.size() > 0) {
    		if (debug) Debug.logInfo("===============" + checkAppls.size() + " records already exist", module);
    		// 1 record exists just update and if diffrent ID delete other record and add together.
    		GenericValue checkAppl = (GenericValue) checkAppls.get(0);
    		// if new record  add to the already existing one.
    		if ( paymentApplication.get("paymentApplicationId") == null)	{
    			// add 2 amounts together
    			checkAppl.set("amountApplied", new Double(paymentApplication.getBigDecimal("amountApplied").
    					add(checkAppl.getBigDecimal("amountApplied")).setScale(decimals,rounding).doubleValue()));
    			if (debug) 	Debug.logInfo("==============Update paymentApplication record: " + checkAppl.getString("paymentApplicationId") + " with appliedAmount:" + checkAppl.getBigDecimal("amountApplied"), module);
    			try {
    				checkAppl.store();
    			} catch (GenericEntityException e) {
    				ServiceUtil.returnError(e.getMessage());
    			}
    		} else if (paymentApplication.getString("paymentApplicationId").equals(checkAppl.getString("paymentApplicationId"))) {
    			// update existing record inplace
    			checkAppl.set("amountApplied", new Double(paymentApplication.getBigDecimal("amountApplied").doubleValue()));
    			if (debug) 	Debug.logInfo("==============Update paymentApplication record: " + checkAppl.getString("paymentApplicationId") + " with appliedAmount:" + checkAppl.getBigDecimal("amountApplied"), module);
    			try {
    				checkAppl.store();
    			} catch (GenericEntityException e) {
    				ServiceUtil.returnError(e.getMessage());
    			}
    		} else	{ // two existing records, an updated one added to the existing one
    			// add 2 amounts together
    			checkAppl.set("amountApplied", new Double(paymentApplication.getBigDecimal("amountApplied").
    					add(checkAppl.getBigDecimal("amountApplied")).setScale(decimals,rounding).doubleValue()));
    			// delete paymentApplication record and update the checkAppls one.
    			if (debug) Debug.logInfo("============Delete paymentApplication record: " + paymentApplication.getString("paymentApplicationId") + " with appliedAmount:" + paymentApplication.getBigDecimal("amountApplied"), module);
    			try {
    				paymentApplication.remove();
    			} catch (GenericEntityException e) {
    				ServiceUtil.returnError(e.getMessage());
    			}
    			// update amount existing record
    			if (debug) 	Debug.logInfo("==============Update paymentApplication record: " + checkAppl.getString("paymentApplicationId") + " with appliedAmount:" + checkAppl.getBigDecimal("amountApplied"), module);
    			try {
    				checkAppl.store();
    			} catch (GenericEntityException e) {
    				ServiceUtil.returnError(e.getMessage());
    			}
    		}
    	} else {
    		if (debug) Debug.logInfo("No records found with paymentId,invoiceid..etc probaly changed one of them...", module);
    		// create record if ID null;
    		if (paymentApplication.get("paymentApplicationId") == null) {
    			paymentApplication.set("paymentApplicationId", delegator.getNextSeqId("PaymentApplication"));
    				if (debug) Debug.logInfo("=============Create new paymentAppication record: " + paymentApplication.getString("paymentApplicationId") + " with appliedAmount:" + paymentApplication.getBigDecimal("amountApplied"), module);
                try {
                    paymentApplication.create();
                } catch (GenericEntityException e) {
                    ServiceUtil.returnError(e.getMessage());
                }
            } else {
                // update existing record (could not be found because a non existing combination of paymentId/invoiceId/invoiceSeqId/ etc... was provided
                if (debug) Debug.logInfo("===========Update existing paymentApplication record: " + paymentApplication.getString("paymentApplicationId") + " with appliedAmount:" + paymentApplication.getBigDecimal("amountApplied"), module);
                try {
                    paymentApplication.store();
                } catch (GenericEntityException e) {
                    ServiceUtil.returnError(e.getMessage());
                }
            }
        }
        return results;
    }

    public static Map checkPaymentInvoices(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String paymentId = (String) context.get("paymentId");
        try {
            GenericValue payment = delegator.findByPrimaryKey("Payment", UtilMisc.toMap("paymentId", paymentId));
            if (payment == null) throw new GenericServiceException("Payment with ID [" + paymentId  + "] not found!");

            List paymentApplications = payment.getRelated("PaymentApplication");
            if (paymentApplications == null) return ServiceUtil.returnSuccess();

            Iterator iter = paymentApplications.iterator();
            while (iter.hasNext()) {
                GenericValue paymentApplication = (GenericValue) iter.next();
                String invoiceId = paymentApplication.getString("invoiceId");
                Map serviceResult = dispatcher.runSync("checkInvoicePaymentApplications", UtilMisc.toMap("invoiceId", invoiceId, "userLogin", userLogin));
                if (ServiceUtil.isError(serviceResult)) return serviceResult;
            }
            return ServiceUtil.returnSuccess();
        } catch (GenericServiceException se) {
            Debug.logError(se, se.getMessage(), module);
            return ServiceUtil.returnError(se.getMessage());
        } catch (GenericEntityException ee) {
            Debug.logError(ee, ee.getMessage(), module);
            return ServiceUtil.returnError(ee.getMessage());
        }
    }

}
