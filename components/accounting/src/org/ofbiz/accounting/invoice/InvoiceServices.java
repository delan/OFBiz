/*
 * $Id$
 *
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 * @version    $Rev:$
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
            Debug.logVerbose("No items to invoice; not creating; returning success", module);
            return ServiceUtil.returnSuccess();
        }

        List toStore = new LinkedList();
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get order header", module);
        }

        if (orderHeader == null) {
            return ServiceUtil.returnError("No OrderHeader, cannot create invoice");
        }
        
        // get list of previous invoices for the order
        List billedItems = null;
        try {
            billedItems = delegator.findByAnd("OrderItemBilling", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot looking billed items", module);
            return ServiceUtil.returnError("Unable to looked previous billed items");
        }
        if (billedItems != null && billedItems.size() > 0) {
            boolean nonDigitalInvoice = false;
            Iterator bii = billedItems.iterator();
            while (bii.hasNext() && !nonDigitalInvoice) {
                GenericValue orderItemBilling = (GenericValue) bii.next();
                GenericValue invoiceItem = null;
                try {
                    invoiceItem = orderItemBilling.getRelatedOne("InvoiceItem");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Cannot get InvoiceItem from billing record; assuming non-digital.", module);
                    nonDigitalInvoice = true;
                }
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
        GenericValue productStore = null;
        try {
            productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", orh.getProductStoreId()));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Unable to get ProductStore", module);
            return ServiceUtil.returnError("Unable to get Product Store from order");
        }
        
        // get the payToParty
        String payToPartyId = productStore.getString("payToPartyId");
        if (payToPartyId == null) {
            return ServiceUtil.returnError("Unable to create invoice; no payToPartyId set for ProductStore Id : " + orh.getProductStoreId());
        }
        
        // get some quantity totals
        double totalItemsInOrder = orh.getTotalOrderItemsQuantity();
        
        // get some price totals
        double shippableAmount = orh.getShippableTotal();
        double orderSubTotal = orh.getOrderItemsSubTotal();

        double invoiceShipProRateAmount = 0.00;
        double invoiceSubTotal = 0.00;
        double invoiceQuantity = 0.00;

        // create the invoice record
        String invoiceId = delegator.getNextSeqId("Invoice").toString();               
        GenericValue invoice = delegator.makeValue("Invoice", UtilMisc.toMap("invoiceId", invoiceId));        
        invoice.set("invoiceDate", UtilDateTime.nowTimestamp());
        invoice.set("invoiceTypeId", invoiceType);
        invoice.set("statusId", "INVOICE_READY");
        
        GenericValue billingAccount = null;
        List billingAccountTerms = null;
        try {
            billingAccount = orderHeader.getRelatedOne("BillingAccount");            
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting BillingAccount entity from OrderHeader", module);
            return ServiceUtil.returnError("Trouble getting BillingAccount entity from OrderHeader");
        }
        
        // for billing accounts we will use related information
        if (billingAccount != null) {                      
            // set the billing account        
            invoice.set("billingAccountId", billingAccount.getString("billingAccountId"));           
           
            // get the billing account terms
            try {
                billingAccountTerms = billingAccount.getRelated("BillingAccountTerm");                
            } catch (GenericEntityException e) {
                Debug.logError(e, "Trouble getting BillingAccountTerm entity list", module);
                return ServiceUtil.returnError("Trouble getting BillingAccountTerm entity list");
            }
            
            // set the invoice terms as defined for the billing account
            if (billingAccountTerms != null) {
                Iterator billingAcctTermsIter = billingAccountTerms.iterator();
                while (billingAcctTermsIter.hasNext()) {
                    GenericValue term = (GenericValue) billingAcctTermsIter.next();
                    GenericValue invoiceTerm = delegator.makeValue("InvoiceTerm", 
                        UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", "_NA_"));
                    String invoiceTermId = delegator.getNextSeqId("InvoiceTerm").toString();	               
                    invoiceTerm.set("invoiceTermId", invoiceTermId);			
                    invoiceTerm.set("termTypeId", term.get("termTypeId"));	
                    invoiceTerm.set("termValue", term.get("termValue"));
                    invoiceTerm.set("uomId", term.get("uomId"));
                    toStore.add(invoiceTerm);                                        
                }
            }
            
            // set the invoice bill_to_customer from the billing account
            List billToRoles = null;
            try {
                billToRoles = billingAccount.getRelated("BillingAccountRole", UtilMisc.toMap("roleTypeId", "BILL_TO_CUSTOMER"), null);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Trouble getting BillingAccountRole entity list", module);
                return ServiceUtil.returnError("Trouble getting BillingAccountRole entity list");
            }
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
            // no billing account use the info off the order header
            GenericValue billToPerson = orh.getBillToPerson();
            if (billToPerson != null) {            
                GenericValue invoiceRole = delegator.makeValue("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId));
                invoiceRole.set("partyId", billToPerson.getString("partyId"));
                invoiceRole.set("roleTypeId", "BILL_TO_CUSTOMER");
                toStore.add(invoiceRole);
            }
            
            GenericValue billingAddress = orh.getBillingAddress();
            if (billingAddress != null) {            
                GenericValue billToContactMech = delegator.makeValue("InvoiceContactMech", UtilMisc.toMap("invoiceId", invoiceId));
                billToContactMech.set("contactMechId", billingAddress.getString("contactMechId"));
                billToContactMech.set("contactMechPurposeTypeId", "BILLING_LOCATION");
                toStore.add(billToContactMech);
            }              
        }
                    
        // store the invoice first
        try {
            delegator.create(invoice);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create invoice record", module);
            return ServiceUtil.returnError("Problems storing Invoice record");
        }
                
        // get a list of the payment method types
        List paymentPreferences = null;
        try {
            paymentPreferences = orderHeader.getRelated("OrderPaymentPreference");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting OrderPaymentPreference entity list", module);
        }
        
        // create the bill-from role BILL_FROM_VENDOR as the partyId for the store
        GenericValue payToRole = delegator.makeValue("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId));
        payToRole.set("partyId", payToPartyId);
        payToRole.set("roleTypeId", "BILL_FROM_VENDOR");
        toStore.add(payToRole);
        
        // create the bill-from (or pay-to) contact mech as the primary PAYMENT_LOCATION of the party from the store
        GenericValue payToAddress = PaymentWorker.getPaymentAddress(delegator, payToPartyId);
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
                GenericValue currentValue = (GenericValue) itemIter.next();
                if ("ItemIssuance".equals(currentValue.getEntityName())) {
                    itemIssuance = currentValue;
                } else if ("OrderItem".equals(currentValue.getEntityName())) {
                    orderItem = currentValue;
                }
                
                if (orderItem == null && itemIssuance != null) {
                    try {
                        orderItem = itemIssuance.getRelatedOne("OrderItem");
                    } catch (GenericEntityException e) {                   
                        Debug.logError(e, "Trouble getting related OrderItem from ItemIssuance", module);
                        return ServiceUtil.returnError("Trouble getting OrderItem from ItemIssuance");
                    }
                } else if (orderItem == null && itemIssuance == null) {
                    Debug.logError("Cannot create invoice when both orderItem and itemIssuance is null", module);
                    return ServiceUtil.returnError("Illegal values passed to create invoice service");
                }
                GenericValue product = null;
                if (orderItem.get("productId") != null) {                
                    try {
                        product = orderItem.getRelatedOne("Product");                  
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Trouble getting Product from OrderItem", module);
                        return ServiceUtil.returnError("Trouble getting Product from OrderItem");
                    }
                }
                
                // get some quantities
                Double orderedQuantity = orderItem.getDouble("quantity");
                Double billingQuantity = null;
                if (itemIssuance != null) {
                    billingQuantity = itemIssuance.getDouble("quantity");
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

                // check if shipping applies to this item
                boolean shippingApplies = false;
                if (product != null && ProductWorker.shippingApplies(product)) {
                    shippingApplies = true;
                }

                GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                invoiceItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, lookupType, "INV_FPROD_ITEM"));
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
                if (product != null) {
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
                        adjInvItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), "INVOICE_ITM_ADJ"));
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
                        Double amountPerQty = adj.getDouble("amount");
                        Double percent = adj.getDouble("percentage");
                        double totalAmount = 0.00;
                        if (percent != null)
                            totalAmount += percent.doubleValue() * (invoiceItem.getDouble("amount").doubleValue() * invoiceItem.getDouble("quantity").doubleValue());
                        if (amountPerQty != null)
                            totalAmount += amountPerQty.doubleValue() * invoiceItem.getDouble("quantity").doubleValue();

                        GenericValue adjInvItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                        adjInvItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), "INVOICE_ITM_ADJ"));
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
                // other adjustment type
                double adjAmount = calcHeaderAdj(delegator, adj, invoiceId, itemSeqId, toStore, orderSubTotal, invoiceSubTotal, invoiceQuantity);
                // these will effect the shipping pro-rate (unless commented)
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
                    double adjAmount = calcHeaderAdj(delegator, adj, invoiceId, itemSeqId, toStore, 1, 1, totalItemsInOrder);
                    // should shipping effect the tax pro-rate?
                    invoiceSubTotal += adjAmount; // here we do

                    // increment the counter
                    itemSeqId++;
                }
            } else {
                // pro-rate the shipping amount based on shippable information
                double adjAmount = calcHeaderAdj(delegator, adj, invoiceId, itemSeqId, toStore, shippableAmount, invoiceShipProRateAmount, invoiceQuantity);
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
            double adjAmount = calcHeaderAdj(delegator, adj, invoiceId, itemSeqId, toStore, orderSubTotal, invoiceSubTotal, invoiceQuantity);
            // this doesn't really effect anything; but just for our totals
            invoiceSubTotal += adjAmount;
        }

        // invoice status object
        GenericValue invStatus = delegator.makeValue("InvoiceStatus",
            UtilMisc.toMap("invoiceId", invoiceId, "statusId", "INVOICE_IN_PROCESS", "statusDate", UtilDateTime.nowTimestamp()));
        toStore.add(invStatus);

        // check for previous order payments
        List orderPaymentPrefs = null;
        try {
            orderPaymentPrefs = delegator.findByAnd("OrderPaymentPreference", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting order payment preference records", module);
            return ServiceUtil.returnError("Problem getting order payment preference records");
        }
        if (orderPaymentPrefs != null) {
            List currentPayments = new ArrayList();
            Iterator opi = orderPaymentPrefs.iterator();
            while (opi.hasNext()) {
                GenericValue paymentPref = (GenericValue) opi.next();
                try {
                    List payments = paymentPref.getRelated("Payment");
                    currentPayments.addAll(payments);
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problem getting payments from preference", module);
                    return ServiceUtil.returnError("Problem getting payments from preference");
                }
            }
            if (currentPayments.size() > 0) {
                // apply these payments to the invoice; only if they haven't already been applied
                Iterator cpi = currentPayments.iterator();
                while (cpi.hasNext()) {
                    GenericValue payment = (GenericValue) cpi.next();
                    List currentApplications = null;
                    try {
                        currentApplications = payment.getRelated("PaymentApplication");
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Problem getting application(s) for payment", module);
                        return ServiceUtil.returnError("Problem getting application(s) for payment");
                    }
                    if (currentApplications == null || currentApplications.size() == 0) {
                        // no applications; okay to apply
                        String applId = delegator.getNextSeqId("PaymentApplication").toString();
                        GenericValue appl = delegator.makeValue("PaymentApplication", UtilMisc.toMap("paymentApplicationId", applId));
                        appl.set("paymentId", payment.get("paymentId"));
                        appl.set("invoiceId", invoice.get("invoiceId"));
                        appl.set("billingAccountId", invoice.get("billingAccountId"));
                        appl.set("amountApplied", payment.get("amount"));
                        toStore.add(appl);
                    }
                }
            }
        }

        // store value objects
        try {
            //Debug.log("Storing : " + toStore, module);
            delegator.storeAll(toStore);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems storing invoice items", module);
            return ServiceUtil.returnError("Cannot create invoice; problem storing items");
        }

        // check to see if we are all paid up
        Map checkResp = null;
        try {
            checkResp = dispatcher.runSync("checkInvoicePaymentApplications", UtilMisc.toMap("invoiceId", invoice.get("invoiceId"), "userLogin", userLogin));
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem checking payment applications", module);
            return ServiceUtil.returnError("Problem checking payment applications");
        }

        Map resp = ServiceUtil.returnSuccess();
        resp.put("invoiceId", invoiceId);
        return resp;
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

        // get the issued items
        List itemsIssued = null;
        try {
            itemsIssued = delegator.findByAnd("ItemIssuance", UtilMisc.toMap("shipmentId", shipmentId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problem getting issued items from Shipment", module);
            return ServiceUtil.returnError("Problem getting issued items from Shipment");
        }
        if (itemsIssued == null) {
            Debug.logInfo("No items issued for shipment", module);
            return ServiceUtil.returnSuccess();
        }

        // group items by order
        Map shippedOrderItems = new HashMap();
        Iterator itemsIter = itemsIssued.iterator();
        while (itemsIter.hasNext()) {
            GenericValue itemIssuance = (GenericValue) itemsIter.next();
            String itemIssuanceId = itemIssuance.getString("itemIssuanceId");
            String orderId = itemIssuance.getString("orderId");
            String orderItemSeqId = itemIssuance.getString("orderItemSeqId");
            List itemsByOrder = (List) shippedOrderItems.get(orderId);
            if (itemsByOrder == null) {
                itemsByOrder = new ArrayList();
            }

            // check and make sure we haven't already billed for this issuance
            Map billFields = UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId, "itemIssuanceId", itemIssuanceId);
            List itemBillings = null;
            try {
                itemBillings = delegator.findByAnd("OrderItemBilling", billFields);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem looking up OrderItemBilling records for : " + billFields, module);
                return ServiceUtil.returnError("Problem getting OrderItemBilling records");
            }

            // if none found, then okay to bill
            if (itemBillings == null || itemBillings.size() == 0) {
                itemsByOrder.add(itemIssuance);
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
                Double issueQty = issue.getDouble("quantity");
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

    private static String getInvoiceItemType(GenericDelegator delegator, String key, String defaultValue) {
        GenericValue itemMap = null;
        try {
            itemMap = delegator.findByPrimaryKey("InvoiceItemTypeMap", UtilMisc.toMap("invoiceItemMapKey", key));
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

    private static double calcHeaderAdj(GenericDelegator delegator, GenericValue adj, String invoiceId, int itemSeqId, List toStore, double divisor, double multiplier, double invoiceQuantity) {
        //Debug.log("Divisor : " + divisor + " / Multiplier: " + multiplier, module);
        double adjAmount = 0.00;
        if (adj.get("amount") != null) {
            // pro-rate the amount
            double amount = ((adj.getDouble("amount").doubleValue() / divisor) * multiplier);
            if (amount != 0) {
                GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                invoiceItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), "INVOICE_ADJ"));
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
                adjInvItem.set("invoiceItemTypeId", getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), "INVOICE_ITM_ADJ"));
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
}
