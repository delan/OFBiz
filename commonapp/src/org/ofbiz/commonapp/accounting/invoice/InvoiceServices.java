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
package org.ofbiz.commonapp.accounting.invoice;

import java.util.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

import org.ofbiz.commonapp.accounting.payment.PaymentWorker;
import org.ofbiz.commonapp.order.order.OrderReadHelper;

/**
 * InvoiceServices - Services for creating invoices
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.1
 */
public class InvoiceServices {

    public static String module = InvoiceServices.class.getName();

    /* Service to create an invoice from an order */
    public static Map createInvoiceFromOrder(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");

        List toStore = new LinkedList();
        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get order header", module);
        }

        if (orderHeader == null)
            ServiceUtil.returnError("No OrderHeader, cannot create invoice");

        // figure out the invoice type   
        String invoiceType = null;        

        String orderType = orderHeader.getString("orderTypeId");
        if (orderType.equals("SALES_ORDER")) {
            invoiceType = "SALES_INVOICE";        
        } else if (orderType.equals("PURCHASE_ORDER")) {
            invoiceType = "PURCHASE_INVOICE";            
        }

        OrderReadHelper orh = new OrderReadHelper(orderHeader);

        // create the invoice record        
        String invoiceId = delegator.getNextSeqId("Invoice").toString();               
        GenericValue invoice = delegator.makeValue("Invoice", UtilMisc.toMap("invoiceId", invoiceId));        
        invoice.set("invoiceDate", UtilDateTime.nowTimestamp());
        invoice.set("invoiceTypeId", invoiceType);
        invoice.set("statusId", "INVOICE_IN_PROCESS");
        
        GenericValue billingAccount = null;
        List billingAccountTerms = null;
        try {
            billingAccount = orderHeader.getRelatedOne("BillingAccount");            
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting BillingAccount entity from OrderHeader", module);
            ServiceUtil.returnError("Trouble getting BillingAccount entity from OrderHeader");
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
                ServiceUtil.returnError("Trouble getting BillingAccountTerm entity list");
            }
            
            // set the invoice terms as defined for the billing account
            if (billingAccountTerms != null) {
                Iterator billingAcctTermsIter = billingAccountTerms.iterator();
                while (billingAcctTermsIter.hasNext()) {
                    GenericValue term = (GenericValue) billingAcctTermsIter.next();
                    GenericValue invoiceTerm = delegator.makeValue("InvoiceTerm", 
                        UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", "_NA_"));
                    invoiceTerm.set("termType", term.get("termType"));
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
                ServiceUtil.returnError("Trouble getting BillingAccountRole entity list");
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
            GenericValue billToContactMech = delegator.makeValue("InvoiceContactMech", 
                UtilMisc.toMap("invoiceId", invoiceId, "contactMechId", billingAccount.getString("contactMechId"), 
                    "contactMechPurposeTypeId", "BILLING_LOCATION"));
            toStore.add(billToContactMech);                             
        }
                    
        // store the invoice first
        try {
            delegator.create(invoice);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create invoice record", module);
            ServiceUtil.returnError("Problems storing Invoice record");
        } 
        
        // get a list of the payment method types
        List paymentPreferences = null;
        try {
            paymentPreferences = orderHeader.getRelated("OrderPaymentPreference");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting OrderPaymentPreference entity list", module);
        }
        
        // create a Set of partyIds used for payments
        Set paymentPartyIds = new HashSet();
        if (paymentPreferences != null) {
            Iterator ppi = paymentPreferences.iterator();
            while (ppi.hasNext()) {
                GenericValue pref = (GenericValue) ppi.next();
                paymentPartyIds.add(pref.getString("paymentMethodTypeId"));
            }
        }
        
        // create the roles and contact mechs based on the paymentMethodType's partyId (from payment.properties)
        Iterator partyIdIter = paymentPartyIds.iterator();
        while (partyIdIter.hasNext()) {
            String paymentMethodTypeId = (String) partyIdIter.next();    
        
            // create the bill-from role BILL_FROM_VENDOR as the partyId for the payment method
            String paymentPartyId = PaymentWorker.getPaymentPartyId(delegator, orderHeader.getString("webSiteId"), paymentMethodTypeId);
            if (paymentPartyId != null) {
                GenericValue payToRole = delegator.makeValue("InvoiceRole", UtilMisc.toMap("invoiceId", invoiceId));
                payToRole.set("partyId", paymentPartyId);
                payToRole.set("roleTypeId", "BILL_FROM_VENDOR");
                toStore.add(payToRole);
            }
        
            // create the bill-from (or pay-to) contact mech as the primary PAYMENT_LOCATION of the party from payment.properties
            GenericValue payToAddress = PaymentWorker.getPaymentAddress(delegator, orderHeader.getString("webSiteId"), paymentMethodTypeId);
            if (payToAddress != null) {
                GenericValue payToCm = delegator.makeValue("InvoiceContactMech", 
                    UtilMisc.toMap("invoiceId", invoiceId, "contactMechId", payToAddress.getString("contactMechId"), 
                        "contactMechPurposeTypeId", "PAYMENT_LOCATION")); 
                toStore.add(payToCm);    
            }
        }

        // sequence for items - all OrderItems or InventoryReservations + all Adjustments
        int itemSeqId = 1;

        // create the item records
        List orderItems = orh.getOrderItems();
        if (orderItems != null) {
            Iterator itemIter = orderItems.iterator();
            while (itemIter.hasNext()) {
                GenericValue orderItem = (GenericValue) itemIter.next();
                GenericValue product = null;
                List itemRes = null;
                try {
                    product = orderItem.getRelatedOne("Product");
                    itemRes = orderItem.getRelated("OrderItemInventoryRes");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Trouble getting related entities", module);
                    ServiceUtil.returnError("Trouble getting order item related entities");
                }
                if (itemRes != null && itemRes.size() > 0) {
                    // there are inventory reservations; create one invoice line per so we can see a break down
                    Iterator itemResIter = itemRes.iterator();
                    while (itemResIter.hasNext()) {
                        String invoiceItemType = getInvoiceItemType(delegator, orderItem.getString("orderItemTypeId"), "INV_PROD_ITEM");
                        GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                        GenericValue itemReservation = (GenericValue) itemResIter.next();
                        invoiceItem.set("invoiceItemTypeId", invoiceItemType);
                        invoiceItem.set("inventoryItemId", itemReservation.get("inventoryItemId"));
                        invoiceItem.set("productId", orderItem.get("productId"));
                        invoiceItem.set("productFeatureId", orderItem.get("productFeatureId"));
                        //invoiceItem.set("uomId", "");
                        invoiceItem.set("taxableFlag", product.get("taxable"));
                        invoiceItem.set("quantity", itemReservation.get("quantity"));
                        invoiceItem.set("amount", orderItem.get("unitPrice"));
                        invoiceItem.set("description", orderItem.get("itemDescription"));
                        toStore.add(invoiceItem);
                        
                        // create the OrderItemBilling record
                        GenericValue orderItemBill = delegator.makeValue("OrderItemBilling", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                        orderItemBill.set("orderId", orderItem.get("orderId"));
                        orderItemBill.set("orderItemSeqId", orderItem.get("orderItemSeqId"));
                        orderItemBill.set("quantity", itemReservation.get("quantity"));
                        orderItemBill.set("amount", orderItem.get("unitPrice"));
                        toStore.add(orderItemBill);
                        
                        // increment the counter
                        itemSeqId++;
                    }
                } else {
                    // there were no inventory reservations, leave inventoryItem empty and use the OrderItem quantity
                    String invoiceItemType = getInvoiceItemType(delegator, orderItem.getString("orderItemTypeId"), "INV_PROD_ITEM");
                    GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));                   
                    invoiceItem.set("invoiceItemTypeId", invoiceItemType);
                    invoiceItem.set("productId", orderItem.get("productId"));                
                    invoiceItem.set("productFeatureId", orderItem.get("productFeatureId"));
                    //invoiceItem.set("uomId", "");
                    invoiceItem.set("taxableFlag", product.get("taxable"));
                    invoiceItem.set("quantity", orderItem.get("quantity"));
                    invoiceItem.set("amount", orderItem.get("unitPrice"));
                    invoiceItem.set("description", orderItem.get("itemDescription"));
                    toStore.add(invoiceItem);
                    
                    // create the OrderItemBilling record
                    GenericValue orderItemBill = delegator.makeValue("OrderItemBilling", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                    orderItemBill.set("orderId", orderItem.get("orderId"));
                    orderItemBill.set("orderItemSeqId", orderItem.get("orderItemSeqId"));
                    orderItemBill.set("quantity", orderItem.get("quantity"));
                    orderItemBill.set("amount", orderItem.get("unitPrice"));
                    toStore.add(orderItemBill); 
                    
                    // increment the counter                   
                    itemSeqId++;
                }

                // create the item adjustment as line items
                List itemAdjustments = OrderReadHelper.getOrderItemAdjustmentList(orderItem, orh.getAdjustments());
                Iterator itemAdjIter = itemAdjustments.iterator();
                while (itemAdjIter.hasNext()) {                    
                    GenericValue adj = (GenericValue) itemAdjIter.next();
                    String invoiceItemType = getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), "INVOICE_ITM_ADJ");
                    if (adj.get("amount") != null) {                        
                        Double amount = adj.getDouble("amount");
                        GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                        invoiceItem.set("invoiceItemTypeId", invoiceItemType);
                        invoiceItem.set("productId", orderItem.get("productId"));
                        invoiceItem.set("productFeatureId", orderItem.get("productFeatureId"));
                        //invoiceItem.set("uomId", "");
                        invoiceItem.set("taxableFlag", product.get("taxable"));
                        invoiceItem.set("quantity", new Double(1));
                        invoiceItem.set("amount", amount);
                        invoiceItem.set("description", adj.get("description"));
                        toStore.add(invoiceItem);
                        
                        // increment the counter
                        itemSeqId++;
                    }
                    if (adj.get("percentage") != null || adj.get("amountPerQuantity") != null) {
                        Double amountPerQty = adj.getDouble("amount");
                        Double percent = adj.getDouble("percentage");
                        double totalAmount = 0.00;
                        if (percent != null)
                            totalAmount += percent.doubleValue() * orderItem.getDouble("unitPrice").doubleValue();
                        if (amountPerQty != null)
                            totalAmount += amountPerQty.doubleValue() * orderItem.getDouble("unitPrice").doubleValue();

                        GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                        invoiceItem.set("invoiceItemTypeId", invoiceItemType);
                        invoiceItem.set("productId", orderItem.get("productId"));
                        invoiceItem.set("productFeatureId", orderItem.get("productFeatureId"));
                        //invoiceItem.set("uomId", "");
                        invoiceItem.set("taxableFlag", product.get("taxable"));
                        invoiceItem.set("quantity", orderItem.getDouble("quantity"));
                        invoiceItem.set("amount", new Double(totalAmount));
                        invoiceItem.set("description", adj.get("description"));
                        toStore.add(invoiceItem);
                        
                        // increment the counter
                        itemSeqId++;
                    }
                }
            }
        }

        // create header adjustments as line items
        List headerAdjustments = orh.getOrderHeaderAdjustments();
        Iterator headerAdjIter = headerAdjustments.iterator();
        while (headerAdjIter.hasNext()) {
            GenericValue adj = (GenericValue) headerAdjIter.next();
            if (adj.get("amount") != null) {
                String invoiceItemType = getInvoiceItemType(delegator, adj.getString("orderAdjustmentTypeId"), "INVOICE_ADJ");                
                Double amount = adj.getDouble("amount");
                GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));                
                invoiceItem.set("invoiceItemTypeId", invoiceItemType);
                //invoiceItem.set("productId", orderItem.get("productId"));
                //invoiceItem.set("productFeatureId", orderItem.get("productFeatureId"));
                //invoiceItem.set("uomId", "");
                //invoiceItem.set("taxableFlag", product.get("taxable"));
                invoiceItem.set("quantity", new Double(1));
                invoiceItem.set("amount", amount);
                invoiceItem.set("description", adj.get("description"));
                toStore.add(invoiceItem);
                
                // increment the counter
                itemSeqId++;
            }
        }
        
        // invoice status object
        GenericValue invStatus = delegator.makeValue("InvoiceStatus", 
            UtilMisc.toMap("invoiceId", invoiceId, "statusId", "INVOICE_IN_PROCESS", "statusDate", UtilDateTime.nowTimestamp()));
        toStore.add(invStatus);
                       
        // store value objects
        try {
            delegator.storeAll(toStore);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems storing invoice items", module);
            return ServiceUtil.returnError("Cannot create invoice; problem storing items");
        }

        Map resp = ServiceUtil.returnSuccess();
        resp.put("invoiceId", invoiceId);
        return resp;
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
}
