/*
 * $Id$
 *
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.accounting.invoice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.ofbiz.accounting.payment.PaymentWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
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
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:sichen@opensourcestrategies.com">Si Chen</a> 
 * @version    $Rev$
 * @since      2.2
 */
public class InvoiceServices {

    public static String module = InvoiceServices.class.getName();

    /* Service to create an invoice for an order */
    public static Map createInvoiceForOrder(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

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

            // get the product store
            GenericValue productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", orh.getProductStoreId()));

            // get the payToParty
            String payToPartyId = productStore.getString("payToPartyId");
            if (payToPartyId == null) {
                return ServiceUtil.returnError("Unable to create invoice; no payToPartyId set for ProductStore Id : " + orh.getProductStoreId());
            }

            // get some quantity totals
            double totalItemsInOrder = orh.getTotalOrderItemsQuantity();

            // get some price totals
            double shippableAmount = orh.getShippableTotal(null);
            double orderSubTotal = orh.getOrderItemsSubTotal();

            double invoiceShipProRateAmount = 0.00;
            double invoiceSubTotal = 0.00;
            double invoiceQuantity = 0.00;

            GenericValue billingAccount = orderHeader.getRelatedOne("BillingAccount");
            String billingAccountId = billingAccount != null ? billingAccount.getString("billingAccountId") : null;

            // create the invoice record
            Map createInvoiceContext = FastMap.newInstance();
            createInvoiceContext.put("billingAccountId", billingAccountId);
            createInvoiceContext.put("invoiceDate", UtilDateTime.nowTimestamp());
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

            // order terms to invoice terms.  Implemented for purchase orders, although it may be useful
            // for sales orders as well.  Later it might be nice to filter OrderTerms to only copy over financial terms.
            List orderTerms = orderHeader.getRelated("OrderTerm");
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
                    GenericValue invoiceRole = delegator.makeValue("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId));
                    invoiceRole.set("partyId", billToRole.get("partyId"));
                    invoiceRole.set("roleTypeId", "BILL_TO_CUSTOMER");
                    toStore.add(invoiceRole);
                }

                // set the bill-to contact mech as the contact mech of the billing account
                if (UtilValidate.isNotEmpty(billingAccount.getString("contactMechId"))) {
                    GenericValue billToContactMech = delegator.makeValue("InvoiceContactMech", UtilMisc.toMap("invoiceId", invoiceId));
                    billToContactMech.set("contactMechId", billingAccount.getString("contactMechId"));
                    billToContactMech.set("contactMechPurposeTypeId", "BILLING_LOCATION");
                    toStore.add(billToContactMech);
                }
            } else {
                // no billing account use the info off the order header.  Look for BILL_TO_CUSTOMER unless it's a purchase invoice, in which case,
                // look for and create BILL_FROM_VENDOR
                String roleTypeId = "BILL_TO_CUSTOMER";
                if ("PURCHASE_INVOICE".equals(invoiceType)) {
                    roleTypeId = "BILL_FROM_VENDOR";
                }
                GenericValue billToPerson = orh.getPartyFromRole(roleTypeId);
                if (billToPerson != null) {
                    GenericValue invoiceRole = delegator.makeValue("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId));
                    invoiceRole.set("partyId", billToPerson.getString("partyId"));
                    invoiceRole.set("roleTypeId", roleTypeId);
                    toStore.add(invoiceRole);
                }

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

            // payToPartyId of the store is the Vendor on Sales Invoices and Customer on Purchase Invoices
            GenericValue payToRole = delegator.makeValue("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId));
            payToRole.set("partyId", payToPartyId);
            if (invoiceType.equals("PURCHASE_INVOICE")) {
                payToRole.set("roleTypeId", "BILL_TO_CUSTOMER");
            } else {
                payToRole.set("roleTypeId", "BILL_FROM_VENDOR");
            }
            toStore.add(payToRole);

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
                payToAddress = PaymentWorker.getPaymentAddress(delegator, payToPartyId);
            }
            if (payToAddress != null) {
                GenericValue payToCm = delegator.makeValue("InvoiceContactMech", UtilMisc.toMap("invoiceId", invoiceId));
                payToCm.set("contactMechId", payToAddress.getString("contactMechId"));
                payToCm.set("contactMechPurposeTypeId", "PAYMENT_LOCATION");
                toStore.add(payToCm);
            }

            // sequence for items - all OrderItems or InventoryReservations + all Adjustments
            int itemSeqId = 1;

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
                    Double orderedQuantity = orderItem.getDouble("quantity");
                    Double billingQuantity = null;
                    if (itemIssuance != null) {
                        billingQuantity = itemIssuance.getDouble("quantity");
                    } if (shipmentReceipt != null) {
                        billingQuantity = shipmentReceipt.getDouble("quantityAccepted");
                    } else {
                        billingQuantity = orderedQuantity;
                    }
                    if (orderedQuantity == null) orderedQuantity = new Double(0.00);
                    if (billingQuantity == null) billingQuantity = new Double(0.00);

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

                    GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                    invoiceItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, lookupType, invoiceType, "INV_FPROD_ITEM"));
                    invoiceItem.set("description", orderItem.get("itemDescription"));
                    invoiceItem.set("quantity", billingQuantity);
                    invoiceItem.set("amount", orderItem.get("unitPrice"));
                    invoiceItem.set("productId", orderItem.get("productId"));
                    invoiceItem.set("productFeatureId", orderItem.get("productFeatureId"));
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
                    double thisAmount = invoiceItem.getDouble("amount").doubleValue() * invoiceItem.getDouble("quantity").doubleValue();

                    // add to the ship amount only if it applies to this item
                    if (shippingApplies) {
                        invoiceShipProRateAmount += thisAmount;
                    }

                    // increment the invoice subtotal
                    invoiceSubTotal += thisAmount;

                    // increment the invoice quantity
                    invoiceQuantity += billingQuantity.doubleValue();

                    // create the OrderItemBilling record
                    GenericValue orderItemBill = delegator.makeValue("OrderItemBilling", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
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
                    itemSeqId++;

                    // create the item adjustment as line items
                    List itemAdjustments = OrderReadHelper.getOrderItemAdjustmentList(orderItem, orh.getAdjustments());
                    Iterator itemAdjIter = itemAdjustments.iterator();
                    while (itemAdjIter.hasNext()) {
                        GenericValue adj = (GenericValue) itemAdjIter.next();
                        if (adj.get("amount") != null) {
                            // pro-rate the amount
                            double amount = ((adj.getDouble("amount").doubleValue() / orderItem.getDouble("quantity").doubleValue()) * invoiceItem.getDouble("quantity").doubleValue());
                            GenericValue adjInvItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                            adjInvItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), invoiceType, "INVOICE_ITM_ADJ"));
                            adjInvItem.set("productId", orderItem.get("productId"));
                            adjInvItem.set("productFeatureId", orderItem.get("productFeatureId"));
                            //adjInvItem.set("uomId", "");
                            adjInvItem.set("taxableFlag", product.get("taxable"));
                            adjInvItem.set("quantity", new Double(1));
                            adjInvItem.set("amount", new Double(amount));
                            adjInvItem.set("description", adj.get("description"));
                            toStore.add(adjInvItem);

                            // this adjustment amount
                            double thisAdjAmount = adjInvItem.getDouble("amount").doubleValue() * adjInvItem.getDouble("quantity").doubleValue();

                            // adjustments only apply to totals when they are not tax or shipping adjustments
                            if (!"SALES_TAX".equals(adj.getString("orderAdjustmentTypeId")) &&
                                    !"SHIPPING_ADJUSTMENT".equals(adj.getString("orderAdjustmentTypeId"))) {
                                // increment the invoice subtotal
                                invoiceSubTotal += thisAdjAmount;

                                // add to the ship amount only if it applies to this item
                                if (shippingApplies) {
                                    invoiceShipProRateAmount += thisAdjAmount;
                                }
                            }

                            // increment the counter
                            itemSeqId++;
                        }
                        if (adj.get("percentage") != null || adj.get("amountPerQuantity") != null) {
                            Double amountPerQty = adj.getDouble("amountPerQuantity");
                            Double percent = adj.getDouble("percentage");
                            double totalAmount = 0.00;
                            if (percent != null)
                                totalAmount += percent.doubleValue() * (invoiceItem.getDouble("amount").doubleValue() * invoiceItem.getDouble("quantity").doubleValue());
                            if (amountPerQty != null)
                                totalAmount += amountPerQty.doubleValue() * invoiceItem.getDouble("quantity").doubleValue();

                            GenericValue adjInvItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                            adjInvItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), invoiceType, "INVOICE_ITM_ADJ"));
                            adjInvItem.set("productId", orderItem.get("productId"));
                            adjInvItem.set("productFeatureId", orderItem.get("productFeatureId"));
                            //adjInvItem.set("uomId", "");
                            adjInvItem.set("taxableFlag", product.get("taxable"));
                            adjInvItem.set("quantity", orderItem.getDouble("quantity"));
                            adjInvItem.set("amount", new Double(totalAmount));
                            adjInvItem.set("description", adj.get("description"));
                            toStore.add(adjInvItem);

                            // this adjustment amount
                            double thisAdjAmount = adjInvItem.getDouble("amount").doubleValue() * adjInvItem.getDouble("quantity").doubleValue();

                            // adjustments only apply to totals when they are not tax or shipping adjustments
                            if (!"SALES_TAX".equals(adj.getString("orderAdjustmentTypeId")) &&
                                    !"SHIPPING_ADJUSTMENT".equals(adj.getString("orderAdjustmentTypeId"))) {
                                // increment the invoice subtotal
                                invoiceSubTotal += thisAdjAmount;

                                // add to the ship amount only if it applies to this item
                                if (shippingApplies) {
                                    invoiceShipProRateAmount += thisAdjAmount;
                                }
                            }

                            // increment the counter
                            itemSeqId++;
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
                    double adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, itemSeqId, toStore, orderSubTotal, invoiceSubTotal, invoiceQuantity);
                    // invoiceShipProRateAmount += adjAmount;
                    // do adjustments compound or are they based off subtotal? Here we will (unless commented)
                    // invoiceSubTotal += adjAmount;

                    // increment the counter
                    itemSeqId++;
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
                        double adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, itemSeqId, toStore, 1, 1, totalItemsInOrder);
                        // should shipping effect the tax pro-rate?
                        invoiceSubTotal += adjAmount; // here we do

                        // increment the counter
                        itemSeqId++;
                    }
                } else {
                    // pro-rate the shipping amount based on shippable information
                    double adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, itemSeqId, toStore, shippableAmount, invoiceShipProRateAmount, invoiceQuantity);
                    // should shipping effect the tax pro-rate?
                    invoiceSubTotal += adjAmount; // here we do

                    // increment the counter
                    itemSeqId++;
                }
            }

            // last do the tax adjustments
            Iterator taxAdjIter = taxAdjustments.iterator();
            while (taxAdjIter.hasNext()) {
                GenericValue adj = (GenericValue) taxAdjIter.next();
                double adjAmount = calcHeaderAdj(delegator, adj, invoiceType, invoiceId, itemSeqId, toStore, orderSubTotal, invoiceSubTotal, invoiceQuantity);
                // this doesn't really effect anything; but just for our totals
                invoiceSubTotal += adjAmount;
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
                            String applId = delegator.getNextSeqId("PaymentApplication");
                            GenericValue appl = delegator.makeValue("PaymentApplication", UtilMisc.toMap("paymentApplicationId", applId));
                            appl.set("paymentId", payment.get("paymentId"));
                            appl.set("invoiceId", invoiceId);
                            appl.set("billingAccountId", billingAccountId);
                            appl.set("amountApplied", payment.get("amount"));
                            toStore.add(appl);
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
            Debug.logError(e, "Problem getting issued items from Shipment", module);
            return ServiceUtil.returnError("Problem getting issued items from Shipment");
        }
        if (items == null) {
            Debug.logInfo("No items issued for shipment", module);
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
                Double issueQty = new Double(0.0);
                if (issue.getEntityName().equals("ShipmentReceipt")) {
                    issueQty = issue.getDouble("quantityAccepted");
                } else {
                    issueQty = issue.getDouble("quantity");
                }
                Double billAvail = (Double) itemQtyAvail.get(issue.getString("orderItemSeqId"));
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
                    double orderedQty = orderItem.getDouble("quantity").doubleValue();

                    // add up the already billed total
                    if (billed != null && billed.size() > 0) {
                        double billedQuantity = 0.00;
                        Iterator bi = billed.iterator();
                        while (bi.hasNext()) {
                            GenericValue oib = (GenericValue) bi.next();
                            Double qty = oib.getDouble("quantity");
                            if (qty != null) {
                                billedQuantity += qty.doubleValue();
                            }
                        }
                        double leftToBill = orderedQty - billedQuantity;
                        billAvail = new Double(leftToBill);
                    } else {
                        billAvail = new Double(orderedQty);
                    }
                }

                // no available means we cannot bill anymore
                if (billAvail != null && billAvail.doubleValue() > 0) {
                    if (issueQty != null && issueQty.doubleValue() > billAvail.doubleValue()) {
                        // can only bill some of the issuance; others have been billed already
                        issue.set("quantity", billAvail);
                        billAvail = new Double(0);
                    } else {
                        // now have been billed
                        billAvail = new Double(billAvail.doubleValue() - issueQty.doubleValue());
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

    public static Map checkInvoicePaymentApplications(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String invoiceId = (String) context.get("invoiceId");
        List paymentAppl = null;
        try {
            paymentAppl = delegator.findByAnd("PaymentApplication", UtilMisc.toMap("invoiceId", invoiceId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting PaymentApplication(s) for Invoice #" + invoiceId, module);
            return ServiceUtil.returnError("Problem getting PaymentApplication(s) for Invoice #" + invoiceId);
        }

        Map payments = new HashMap();
        if (paymentAppl != null) {
            Iterator pai = paymentAppl.iterator();
            while (pai.hasNext()) {
                GenericValue payAppl = (GenericValue) pai.next();
                payments.put(payAppl.getString("paymentId"), payAppl.getDouble("amountApplied"));
            }
        }

        double totalPayments = 0.00;
        Iterator pi = payments.keySet().iterator();
        while (pi.hasNext()) {
            String paymentId = (String) pi.next();
            Double amount = (Double) payments.get(paymentId);
            if (amount == null) amount = new Double(0.00);
            totalPayments += amount.doubleValue();
        }

        if (totalPayments > 0.00) {
            double invoiceTotal = InvoiceWorker.getInvoiceTotal(delegator, invoiceId);
            //Debug.log("Invoice #" + invoiceId + " total: " + invoiceTotal, module);
            //Debug.log("Total payments : " + totalPayments, module);
            if (totalPayments >= invoiceTotal) {
                // this invoice is paid
                Map svcCtx = UtilMisc.toMap("statusId", "INVOICE_PAID", "invoiceId", invoiceId, "userLogin", userLogin);
                try {
                    Map stRes = dispatcher.runSync("setInvoiceStatus", svcCtx);
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

    private static double calcHeaderAdj(GenericDelegator delegator, GenericValue adj, String invoiceTypeId, String invoiceId, int itemSeqId, List toStore, 
            double divisor, double multiplier, double invoiceQuantity) {
        Debug.log("Divisor : " + divisor + " / Multiplier: " + multiplier, module);
        double adjAmount = 0.00;
        if (adj.get("amount") != null) {
            // pro-rate the amount
            double amount = ((adj.getDouble("amount").doubleValue() / divisor) * multiplier);
            if (amount != 0) {
                GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                invoiceItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), invoiceTypeId, "INVOICE_ADJ"));
                //invoiceItem.set("productId", orderItem.get("productId"));
                //invoiceItem.set("productFeatureId", orderItem.get("productFeatureId"));
                //invoiceItem.set("uomId", "");
                //invoiceItem.set("taxableFlag", product.get("taxable"));
                invoiceItem.set("quantity", new Double(1));
                invoiceItem.set("amount", new Double(amount));
                invoiceItem.set("description", adj.get("description"));
                toStore.add(invoiceItem);
            }
            adjAmount = amount;
        } else if (adj.get("percentage") != null || adj.get("amountPerQuantity") != null) {
            Double amountPerQty = adj.getDouble("amount");
            Double percent = adj.getDouble("percentage");
            double totalAmount = 0.00;
            if (percent != null)
                totalAmount += percent.doubleValue() * multiplier;
            if (amountPerQty != null)
                totalAmount += amountPerQty.doubleValue() * invoiceQuantity;

            if (totalAmount != 0) {
                GenericValue adjInvItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                adjInvItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), invoiceTypeId, "INVOICE_ITM_ADJ"));
                //adjInvItem.set("productId", orderItem.get("productId"));
                //adjInvItem.set("productFeatureId", orderItem.get("productFeatureId"));
                //adjInvItem.set("uomId", "");
                //adjInvItem.set("taxableFlag", product.get("taxable"));
                adjInvItem.set("quantity", new Double(1));
                adjInvItem.set("amount", new Double(totalAmount));
                adjInvItem.set("description", adj.get("description"));
                toStore.add(adjInvItem);
            }
            adjAmount = totalAmount;
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
}
