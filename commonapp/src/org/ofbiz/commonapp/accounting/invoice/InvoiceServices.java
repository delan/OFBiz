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

import org.ofbiz.commonapp.order.order.OrderReadHelper;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * InventoryServices - Services for creating invoices
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a> 
 * @version    $Revision$
 * @since      2.0
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
        String billingAccountId = orderHeader.getString("billingAccountId");
        GenericValue invoice = delegator.makeValue("Invoice", UtilMisc.toMap("invoiceId", invoiceId));
        invoice.set("invoiceTypeId", invoiceType);
        if (billingAccountId != null)
            invoice.set("billingAccountId", billingAccountId);
        
        // store the invoice first
        try {
            delegator.create(invoice);
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot create invoice record", module);
            ServiceUtil.returnError("Problems storing Invoice record");
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
                        GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                        GenericValue itemReservation = (GenericValue) itemResIter.next();
                        invoiceItem.set("invoiceItemTypeId", "INV_PROD_ITEM");
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
                    GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));                   
                    invoiceItem.set("invoiceItemTypeId", "INV_PROD_ITEM");
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
                    if (adj.get("amount") != null) {
                        Double amount = adj.getDouble("amount");
                        GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));
                        invoiceItem.set("invoiceItemTypeId", "INVOICE_ITM_ADJ");
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
                        invoiceItem.set("invoiceItemTypeId", "INVOICE_ITM_ADJ");
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
                Double amount = adj.getDouble("amount");
                GenericValue invoiceItem = delegator.makeValue("InvoiceItem", UtilMisc.toMap("invoiceId", invoiceId, "invoiceItemSeqId", new Integer(itemSeqId).toString()));                
                invoiceItem.set("invoiceItemTypeId", "INVOICE_ADJ");
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
}
