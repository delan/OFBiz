/* $$ID$ */
package org.ofbiz.order.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * OrderReturnServices
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.5
 */
public class OrderReturnServices {

    public static final String module = OrderReturnServices.class.getName();
    public static final String resource = "OrderUiLabels";
    public static final String resource_error = "OrderErrorUiLabels";

    // locate the return item's initial inventory item cost
    public static Map getReturnItemInitialCost(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String returnId = (String) context.get("returnId");
        String returnItemSeqId = (String) context.get("returnItemSeqId");

        Map result = ServiceUtil.returnSuccess();
        result.put("initialItemCost", getReturnItemInitialCost(delegator, returnId, returnItemSeqId));
        return result;
    }

    // obtain order/return total information
    public static Map getOrderAvailableReturnedTotal(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        OrderReadHelper orh = null;
        try {
            orh = new OrderReadHelper(delegator, orderId);
        } catch (IllegalArgumentException e) {
            return ServiceUtil.returnError(e.getMessage());
        }

        // an adjustment value to test
        Double adj = (Double) context.get("adjustment");
        if (adj == null) {
            adj = new Double(0);
        }

        double returnTotal = orh.getOrderReturnedTotal(true);
        double orderTotal = orh.getOrderGrandTotal();
        double available = orderTotal - returnTotal - adj.doubleValue();


        Map result = ServiceUtil.returnSuccess();
        result.put("availableReturnTotal", new Double(available));
        result.put("orderTotal", new Double(orderTotal));
        result.put("returnTotal", new Double(returnTotal));
        return result;
    }

    // worker method which can be used in screen iterations
    public static Double getReturnItemInitialCost(GenericDelegator delegator, String returnId, String returnItemSeqId) {
        if (delegator == null || returnId == null || returnItemSeqId == null) {
            throw new IllegalArgumentException("Method parameters cannot contain nulls");
        }
        Debug.log("Finding the initial item cost for return item : " + returnId + " / " + returnItemSeqId, module);

        // the cost holder
        Double itemCost = new Double(0.00);

        // get the return item information
        GenericValue returnItem = null;
        try {
            returnItem = delegator.findByPrimaryKey("ReturnItem", UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            throw new GeneralRuntimeException(e.getMessage());
        }
        Debug.log("Return item value object - " + returnItem, module);

        // check for an orderItem association
        if (returnItem != null) {
            String orderId = returnItem.getString("orderId");
            String orderItemSeqId = returnItem.getString("orderItemSeqId");
            if (orderItemSeqId != null && orderId != null) {
                Debug.log("Found order item reference", module);
                // locate the item issuance(s) for this order item
                List itemIssue = null;
                try {
                    itemIssue = delegator.findByAnd("ItemIssuance", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    throw new GeneralRuntimeException(e.getMessage());
                }
                if (itemIssue != null && itemIssue.size() > 0) {
                    Debug.log("Found item issuance referece", module);
                    // just use the first one for now; maybe later we can find a better way to determine which was the
                    // actual item being returned; maybe by serial number
                    GenericValue issue = EntityUtil.getFirst(itemIssue);
                    GenericValue inventoryItem = null;
                    try {
                        inventoryItem = issue.getRelatedOne("InventoryItem");
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        throw new GeneralRuntimeException(e.getMessage());
                    }
                    if (inventoryItem != null) {
                        Debug.log("Located inventory item - " + inventoryItem.getString("inventoryItemId"), module);
                        if (inventoryItem.get("unitCost") != null) {
                            itemCost = inventoryItem.getDouble("unitCost");
                        } else {
                            Debug.logInfo("Found item cost; but cost was null. Returning default amount (0.00)", module);
                        }
                    }
                }
            }
        }

        Debug.log("Initial item cost - " + itemCost, module);
        return itemCost;
    }

    // helper method for sending return notifications
    private static Map sendReturnNotificationScreen(DispatchContext dctx, Map context, String emailType) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String returnId = (String) context.get("returnId");
        Locale locale = (Locale) context.get("locale");

        // get the return header
        GenericValue returnHeader = null;
        try {
            returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderErrorUnableToGetReturnHeaderForID", UtilMisc.toMap("returnId",returnId), locale));
        }

        // get the return items
        List returnItems = null;
        try {
            returnItems = returnHeader.getRelated("ReturnItem");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderErrorUnableToGetReturnItemRecordsFromReturnHeader", locale));
        }

        // get the order header -- the first item will determine which product store to use from the order
        String productStoreId = null;
        String emailAddress = null;
        if (returnItems != null && returnItems.size() > 0) {
            GenericValue firstItem = EntityUtil.getFirst(returnItems);
            GenericValue orderHeader = null;
            try {
                orderHeader = firstItem.getRelatedOne("OrderHeader");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderErrorUnableToGetOrderHeaderFromReturnItem", locale));
            }

            if (orderHeader != null && UtilValidate.isNotEmpty(orderHeader.getString("productStoreId"))) {
                OrderReadHelper orh = new OrderReadHelper(orderHeader);
                productStoreId = orh.getProductStoreId();
                emailAddress = orh.getOrderEmailString();
            }
        }

        // get the email setting and send the mail
        if (productStoreId != null && productStoreId.length() > 0) {
            Map sendMap = FastMap.newInstance();

            GenericValue productStoreEmail = null;
            try {
                productStoreEmail = delegator.findByPrimaryKey("ProductStoreEmailSetting", UtilMisc.toMap("productStoreId", productStoreId, "emailType", emailType));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }

            if (productStoreEmail != null && emailAddress != null) {
                String bodyScreenLocation = productStoreEmail.getString("bodyScreenLocation");
                if (UtilValidate.isEmpty(bodyScreenLocation)) {
                    bodyScreenLocation = (String) ProductStoreWorker.getDefaultProductStoreEmailScreenLocation(emailType);
                }
                sendMap.put("bodyScreenUri", bodyScreenLocation);

                ResourceBundleMapWrapper uiLabelMap = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("EcommerceUiLabels", locale);
                uiLabelMap.addBottomResourceBundle("OrderUiLabels");
                uiLabelMap.addBottomResourceBundle("CommonUiLabels");

                Map bodyParameters = UtilMisc.toMap("returnHeader", returnHeader, "returnItems", returnItems, "uiLabelMap", uiLabelMap, "locale", locale);
                sendMap.put("bodyParameters", bodyParameters);

                sendMap.put("subject", productStoreEmail.getString("subject"));
                sendMap.put("contentType", productStoreEmail.get("contentType"));
                sendMap.put("sendFrom", productStoreEmail.get("fromAddress"));
                sendMap.put("sendCc", productStoreEmail.get("ccAddress"));
                sendMap.put("sendBcc", productStoreEmail.get("bccAddress"));
                sendMap.put("sendTo", emailAddress);

                sendMap.put("userLogin", userLogin);

                Map sendResp = null;
                try {
                    sendResp = dispatcher.runSync("sendMailFromScreen", sendMap);
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problem sending mail", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderProblemSendingEmail", locale));
                }

                // check for errors
                if (sendResp != null && !ServiceUtil.isError(sendResp)) {
                    sendResp.put("emailType", emailType);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderProblemSendingEmail", locale), null, null, sendResp);
                }
                return sendResp;
            }
        }

        return ServiceUtil.returnFailure("No valid email setting for store");
    }

    // return request notification
    public static Map sendReturnAcceptNotification(DispatchContext dctx, Map context) {
        return sendReturnNotificationScreen(dctx, context, "PRDS_RTN_ACCEPT");
    }

    // return complete notification
    public static Map sendReturnCompleteNotification(DispatchContext dctx, Map context) {
        return sendReturnNotificationScreen(dctx, context, "PRDS_RTN_COMPLETE");
    }

    // return cancel notification
    public static Map sendReturnCancelNotification(DispatchContext dctx, Map context) {
        return sendReturnNotificationScreen(dctx, context, "PRDS_RTN_CANCEL");
    }

    // get the returnable quantiy for an order item
    public static Map getReturnableQuantity(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        GenericValue orderItem = (GenericValue) context.get("orderItem");
        GenericValue product = null;
        Locale locale = (Locale) context.get("locale");
        if (orderItem.get("productId") != null) {
            try {
                product = orderItem.getRelatedOne("Product");
            } catch (GenericEntityException e) {
                Debug.logError(e, "ERROR: Unable to get Product from OrderItem", module);
            }
        }

        // check returnable status
        boolean returnable = true;

        // first check returnable flag
        if (product != null && product.get("returnable") != null &&
                "N".equalsIgnoreCase(product.getString("returnable"))) {
            // the product is not returnable at all
            returnable = false;
        }

        // next check support discontinuation
        if (product != null && product.get("supportDiscontinuationDate") != null &&
                !UtilDateTime.nowTimestamp().before(product.getTimestamp("supportDiscontinuationDate"))) {
            // support discontinued either now or in the past
            returnable = false;
        }

        String itemStatus = orderItem.getString("statusId");
        double orderQty = orderItem.getDouble("quantity").doubleValue();

        // get the returnable quantity
        double returnableQuantity = 0.00;
        if (returnable && itemStatus.equals("ITEM_COMPLETED")) {
            List returnedItems = null;
            try {
                returnedItems = orderItem.getRelated("ReturnItem");
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorUnableToGetReturnItemInformation", locale));
            }
            if (returnedItems == null || returnedItems.size() == 0) {
                returnableQuantity = orderQty;
            } else {
                double returnedQty = 0.00;
                Iterator ri = returnedItems.iterator();
                while (ri.hasNext()) {
                    GenericValue returnItem = (GenericValue) ri.next();
                    GenericValue returnHeader = null;
                    try {
                        returnHeader = returnItem.getRelatedOne("ReturnHeader");
                    } catch (GenericEntityException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorUnableToGetReturnHeaderFromItem", locale));
                    }
                    String returnStatus = returnHeader.getString("statusId");
                    if (!returnStatus.equals("RETURN_CANCELLED")) {
                        returnedQty += returnItem.getDouble("returnQuantity").doubleValue();
                    }
                }
                if (returnedQty < orderQty) {
                    returnableQuantity = orderQty - returnedQty;
                }
            }
        }

        // get the returnable price now equals to orderItem.unitPrice, since adjustments are booked separately

        Map result = ServiceUtil.returnSuccess();
        result.put("returnableQuantity", new Double(returnableQuantity));
        result.put("returnablePrice", orderItem.get("unitPrice"));
        return result;
    }

    // get a map of returnable items (items not already returned) and quantities
    public static Map getReturnableItems(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        String orderId = (String) context.get("orderId");
        Locale locale = (Locale) context.get("locale");

        GenericValue orderHeader = null;
        try {
            orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorUnableToGetReturnItemInformation", locale));
        }

        Map returnable = new HashMap();
        if (orderHeader != null) {
            List orderItems = null;
            try {
                orderItems = orderHeader.getRelatedByAnd("OrderItem", UtilMisc.toMap("statusId", "ITEM_COMPLETED"));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorUnableToGetReturnHeaderFromItem", locale));
            }
            if (orderItems != null) {
                Iterator i = orderItems.iterator();
                while (i.hasNext()) {
                    GenericValue item = (GenericValue) i.next();
                    Map serviceResult = null;
                    try {
                        serviceResult = dispatcher.runSync("getReturnableQuantity", UtilMisc.toMap("orderItem", item));
                    } catch (GenericServiceException e) {
                        Debug.logError(e, module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorUnableToGetTheItemReturnableQuantity", locale));
                    }
                    if (serviceResult.containsKey(ModelService.ERROR_MESSAGE)) {
                        return ServiceUtil.returnError((String) serviceResult.get(ModelService.ERROR_MESSAGE));
                    } else {
                        Map returnInfo = new HashMap();
                        // first the return info (quantity/price)
                        returnInfo.put("returnableQuantity", serviceResult.get("returnableQuantity"));
                        returnInfo.put("returnablePrice", serviceResult.get("returnablePrice"));

                        // now the product type information
                        String itemTypeKey = "FINISHED_GOOD"; // default item type (same as invoice)
                        GenericValue product = null;
                        if (item.get("productId") != null) {
                            try {
                                product = item.getRelatedOne("Product");
                            } catch (GenericEntityException e) {
                                Debug.logError(e, module);
                                return ServiceUtil.returnError("Unable to obtain order item information!");
                            }
                        }
                        if (product != null) {
                            itemTypeKey = product.getString("productTypeId");
                        } else if (item != null) {
                            itemTypeKey = item.getString("orderItemTypeId");
                        }
                        returnInfo.put("itemTypeKey", itemTypeKey);

                        returnable.put(item, returnInfo);
                    }
                }
            } else {
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorNoOrderItemsFound", locale));
            }
        } else {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorUnableToFindOrderHeader", locale));
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("returnableItems", returnable);
        return result;
    }

    // check return items status and update return header status
    public static Map checkReturnComplete(DispatchContext dctx, Map context) {
        //appears to not be used: LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        String returnId = (String) context.get("returnId");
        Locale locale = (Locale) context.get("locale");

        GenericValue returnHeader = null;
        List returnItems = null;
        try {
            returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));
            if (returnHeader != null) {
                returnItems = returnHeader.getRelated("ReturnItem");
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up return information", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorGettingReturnHeaderItemInformation", locale));
        }

        // if already completed just return
        if (returnHeader != null && returnHeader.get("statusId") != null) {
            String currentStatus = returnHeader.getString("statusId");
            if ("RETURN_COMPLETED".equals(currentStatus) || "RETURN_CANCELLED".equals(currentStatus)) {
                return ServiceUtil.returnSuccess();
            }
        }

        // now; to be used for all timestamps
        Timestamp now = UtilDateTime.nowTimestamp();

        List completedItems = new ArrayList();
        if (returnHeader != null && returnItems != null && returnItems.size() > 0) {
            Iterator itemsIter = returnItems.iterator();
            while (itemsIter.hasNext()) {
                GenericValue item = (GenericValue) itemsIter.next();
                String itemStatus = item != null ? item.getString("statusId") : null;
                if (itemStatus != null) {
                    // both completed and cancelled items qualify for completed status change
                    if ("RETURN_COMPLETED".equals(itemStatus) || "RETURN_CANCELLED".equals(itemStatus)) {
                        completedItems.add(item);
                    }
                }
            }

            // if all items are completed/cancelled these should match
            if (completedItems.size() == returnItems.size()) {
                List toStore = new LinkedList();
                returnHeader.set("statusId", "RETURN_COMPLETED");
                toStore.add(returnHeader);

                // create the status change history and set it to be stored
                String returnStatusId = delegator.getNextSeqId("ReturnStatus").toString();
                GenericValue returnStatus = delegator.makeValue("ReturnStatus", UtilMisc.toMap("returnStatusId", returnStatusId));
                returnStatus.set("statusId", "RETURN_COMPLETED");
                returnStatus.set("returnId", returnId);
                returnStatus.set("statusDatetime", now);
                toStore.add(returnStatus);
                try {
                    delegator.storeAll(toStore);
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorUnableToCreateReturnStatusHistory", locale));
                }
            }

        }

        Map result = ServiceUtil.returnSuccess();
        result.put("statusId", returnHeader.get("statusId"));
        return result;
    }

    // credit (billingAccount) return
    public static Map processCreditReturn(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        String returnId = (String) context.get("returnId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        GenericValue returnHeader = null;
        List returnItems = null;
        try {
            returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));
            if (returnHeader != null) {
                returnItems = returnHeader.getRelatedByAnd("ReturnItem", UtilMisc.toMap("returnTypeId", "RTN_CREDIT"));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up return information", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorGettingReturnHeaderItemInformation", locale));
        }

        if (returnHeader != null && returnItems != null && returnItems.size() > 0) {
            String billingAccountId = returnHeader.getString("billingAccountId");
            String fromPartyId = returnHeader.getString("fromPartyId");
            String toPartyId = returnHeader.getString("toPartyId");
            
            // make sure total refunds on a return don't exceed amount of returned orders
            Map serviceResult = null;
            try {
                serviceResult = dispatcher.runSync("checkPaymentAmountForRefund", UtilMisc.toMap("returnId", returnId));
            } catch (GenericServiceException e) {
                Debug.logError(e, "Problem running the checkPaymentAmountForRefund service", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemsWithCheckPaymentAmountForRefund", locale));                
            }
            if (ServiceUtil.isError(serviceResult)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
            }                        
            if (billingAccountId == null) {
                // create new BillingAccount w/ 0 balance
                try {
                    Map newBa = dispatcher.runSync("createBillingAccount", UtilMisc.toMap("accountLimit", new Double(0.00), "description", "Credit Account", "userLogin", userLogin));
                    if (!newBa.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
                        billingAccountId = (String) newBa.get("billingAccountId");
                        if (billingAccountId != null) {
                            // set the role on the account
                            Map newBaR = dispatcher.runSync("createBillingAccountRole", UtilMisc.toMap("billingAccountId", billingAccountId, "partyId", fromPartyId, "roleTypeId", "BILL_TO_CUSTOMER", "userLogin", userLogin));
                            if (newBaR.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_ERROR)) {
                                Debug.logError("Error with createBillingAccountRole: " + newBaR.get(ModelService.ERROR_MESSAGE), module);
                                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorWithCreateBillingAccountRole", locale) + newBaR.get(ModelService.ERROR_MESSAGE));
                            }
                        }
                    } else {
                        Debug.logError("Error with createBillingAccount: " + newBa.get(ModelService.ERROR_MESSAGE), module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorWithCreateBillingAccount", locale) + newBa.get(ModelService.ERROR_MESSAGE));
                    }
                } catch (GenericServiceException e) {
                    Debug.logError(e, "Problems creating BillingAccount", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemsCreatingBillingAccount", locale));
                }
            }

            // double check; make sure we have a billingAccount
            if (billingAccountId == null) {
                Debug.logError("No available billing account, none was created", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderNoAvailableBillingAccount", locale));
            }

            // now; to be used for all timestamps
            Timestamp now = UtilDateTime.nowTimestamp();

            // start the response creation
            String itemResponseId = delegator.getNextSeqId("ReturnItemResponse").toString();
            GenericValue itemResponse = delegator.makeValue("ReturnItemResponse", UtilMisc.toMap("returnItemResponseId", itemResponseId));

            // need a total for the credit
            List toBeStored = new ArrayList();
            double creditTotal = 0.00;
            Iterator itemsIter = returnItems.iterator();
            while (itemsIter.hasNext()) {
                GenericValue item = (GenericValue) itemsIter.next();
                Double quantity = item.getDouble("returnQuantity");
                Double price = item.getDouble("returnPrice");
                if (quantity == null) quantity = new Double(0);
                if (price == null) price = new Double(0);
                creditTotal += price.doubleValue() * quantity.doubleValue();

                // set the response on the item and flag the item to be stored
                item.set("returnItemResponseId", itemResponseId);
                item.set("statusId", "RETURN_COMPLETED");
                toBeStored.add(item);

                // create the status change history and set it to be stored
                String returnStatusId = delegator.getNextSeqId("ReturnStatus").toString();
                GenericValue returnStatus = delegator.makeValue("ReturnStatus", UtilMisc.toMap("returnStatusId", returnStatusId));
                returnStatus.set("statusId", item.get("statusId"));
                returnStatus.set("returnId", item.get("returnId"));
                returnStatus.set("returnItemSeqId", item.get("returnItemSeqId"));
                returnStatus.set("statusDatetime", now);
                toBeStored.add(returnStatus);
            }

            // create a Double object for the amount
            Double creditAmount = new Double(creditTotal);

            // create a Payment record for this credit; will look just like a normal payment
            // However, since this payment is not a DISBURSEMENT or RECEIPT but really a matter of internal record
            // it is of type "Other (Non-posting)"
            String paymentId = delegator.getNextSeqId("Payment").toString();
            GenericValue payment = delegator.makeValue("Payment", UtilMisc.toMap("paymentId", paymentId));
            payment.set("paymentTypeId", "CUSTOMER_REFUND");
            payment.set("paymentMethodTypeId", "EXT_BILLACT");
            payment.set("partyIdFrom", toPartyId);  // if you receive a return FROM someone, then you'd have to give a return TO that person
            payment.set("partyIdTo", fromPartyId);
            payment.set("effectiveDate", now);
            payment.set("amount", creditAmount);
            payment.set("comments", "Return Credit");
            try {
                delegator.create(payment);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem creating Payment record", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemCreatingPaymentRecord", locale));
            }

            // create the PaymentApplication
            String paId = delegator.getNextSeqId("PaymentApplication").toString();
            GenericValue pa = delegator.makeValue("PaymentApplication", UtilMisc.toMap("paymentApplicationId", paId));
            pa.set("paymentId", paymentId);
            pa.set("billingAccountId", billingAccountId);
            pa.set("amountApplied", creditAmount);
            try {
                delegator.create(pa);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem creating PaymentApplication record", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemCreatingPaymentApplicationRecord", locale));
            }

            // fill in the response fields
            itemResponse.set("paymentId", paymentId);
            itemResponse.set("billingAccountId", billingAccountId);
            itemResponse.set("responseAmount", creditAmount);
            itemResponse.set("responseDate", now);
            try {
                delegator.create(itemResponse);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem creating ReturnItemResponse record", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemCreatingReturnItemResponseRecord", locale));
            }

            // store the item changes (attached responseId)
            try {
                delegator.storeAll(toBeStored);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problem storing ReturnItem updates", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemStoringReturnItemUpdates", locale));
            }
        }

        return ServiceUtil.returnSuccess();
    }

    // refund (cash/charge) return
    public static Map processRefundReturn(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        String returnId = (String) context.get("returnId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        GenericValue returnHeader = null;
        List returnItems = null;
        try {
            returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));
            if (returnHeader != null) {
                returnItems = returnHeader.getRelatedByAnd("ReturnItem", UtilMisc.toMap("returnTypeId", "RTN_REFUND"));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up return information", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorGettingReturnHeaderItemInformation", locale));
        }

        if (returnHeader != null && returnItems != null && returnItems.size() > 0) {
            Map itemsByOrder = new HashMap();
            Map totalByOrder = new HashMap();
            
            // make sure total refunds on a return don't exceed amount of returned orders
            Map serviceResult = null;
            try {
                serviceResult = dispatcher.runSync("checkPaymentAmountForRefund", UtilMisc.toMap("returnId", returnId));
            } catch (GenericServiceException e){
                Debug.logError(e, "Problem running the checkPaymentAmountForRefund service", module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemsWithCheckPaymentAmountForRefund", locale));                
            }
            if (ServiceUtil.isError(serviceResult)) {
                return ServiceUtil.returnError(ServiceUtil.getErrorMessage(serviceResult));
            }                                    
            
            groupReturnItemsByOrder(returnItems, itemsByOrder, totalByOrder, delegator, returnId);

            // process each one by order
            Set itemSet = itemsByOrder.entrySet();
            Iterator itemByOrderIt = itemSet.iterator();
            while (itemByOrderIt.hasNext()) {
                Map.Entry entry = (Map.Entry) itemByOrderIt.next();
                String orderId = (String) entry.getKey();
                List items = (List) entry.getValue();
                Double orderTotal = (Double) totalByOrder.get(orderId);

                // get order header & payment prefs
                GenericValue orderHeader = null;
                List orderPayPrefs = null;
                try {
                    orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                    // sort these desending by maxAmount
                    orderPayPrefs = orderHeader.getRelated("OrderPaymentPreference", null, UtilMisc.toList("-maxAmount"));
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Cannot get Order details for #" + orderId, module);
                    continue;
                }

                // get the payment prefs to use (will use them in order of amount charged)
                List prefsToUse = new ArrayList();
                Map prefsAmount = new HashMap();
                double neededAmount = orderTotal.doubleValue();
                Debug.logInfo("before orderTotal:" + neededAmount, module);
                if (orderPayPrefs != null && orderPayPrefs.size() > 0) {
                    Iterator payPrefIter = orderPayPrefs.iterator();
                    do {
                        GenericValue pref = (GenericValue) payPrefIter.next();
                        Double maxAmount = pref.getDouble("maxAmount");
                        String statusId = pref.getString("statusId");
                        Debug.logInfo("maxAmount:" + maxAmount +", statusId:" + statusId, module);
                        if ("PAYMENT_SETTLED".equals(statusId)) {
                            if (maxAmount == null || maxAmount.doubleValue() == 0.00) {
                                prefsToUse.add(pref);
                                prefsAmount.put(pref, orderTotal);
                                neededAmount = 0.00;
                            } else if (maxAmount.doubleValue() > orderTotal.doubleValue()) {
                                prefsToUse.add(pref);
                                prefsAmount.put(pref, orderTotal);
                                neededAmount = 0.00;
                            } else {
                                prefsToUse.add(pref);
                                if (maxAmount.doubleValue() > neededAmount) {
                                    prefsAmount.put(pref, new Double(maxAmount.doubleValue() - neededAmount));
                                } else {
                                    prefsAmount.put(pref, maxAmount);
                                }
                                neededAmount -= maxAmount.doubleValue();
                            }
                        }
                    } while (neededAmount > 0 && payPrefIter.hasNext());
                }
                Debug.logInfo("after orderTotal:"+ neededAmount, module);
                if (neededAmount != 0) {
                    Debug.logError("Was not able to find needed payment preferences for the order RTN: " + returnId + " ORD: " + orderId, module);
                    return ServiceUtil.returnError("Unable to refund order #" + orderId + "; there are no available payment preferences.");
                }

                Map prefSplitMap = new HashMap();
                if (prefsToUse == null || prefsToUse.size() == 0) {
                    Debug.logError("We didn't find any possible payment prefs to use for RTN: " + returnId + " ORD: " + orderId, module);
                    return ServiceUtil.returnError("Unable to refund order #" + orderId + "; there are no available payment preferences.");                     
                } else if (prefsToUse.size() > 1) {
                    // we need to spit the items up to log which pref it was refunded to
                    // TODO: add the split of items for multiple payment prefs
                } else {
                    // single payment / single refund
                    prefSplitMap.put(prefsToUse.get(0), items);
                }

                // now process all items for each preference
                Set prefItemSet = prefSplitMap.entrySet();
                Iterator prefItemIt = prefItemSet.iterator();
                while (prefItemIt.hasNext()) {
                    Map.Entry prefItemEntry = (Map.Entry) prefItemIt.next();
                    GenericValue orderPayPref = (GenericValue) prefItemEntry.getKey();
                    List itemList = (List) prefItemEntry.getValue();

                    Double thisRefundAmount = (Double) prefsAmount.get(orderPayPref);
                    String paymentId = null;

                    // this can be extended to support additional electronic types
                    List electronicTypes = UtilMisc.toList("CREDIT_CARD", "EFT_ACCOUNT", "GIFT_CARD");
                    //List electronicTypes = new ArrayList();

                    if (electronicTypes.contains(orderPayPref.getString("paymentMethodTypeId"))) {
                        // call the refund service to refund the payment
                        try {
                            serviceResult = dispatcher.runSync("refundPayment", UtilMisc.toMap("orderPaymentPreference", orderPayPref, "refundAmount", thisRefundAmount, "userLogin", userLogin));
                            if (ServiceUtil.isError(serviceResult)) {
                                return ServiceUtil.returnError("Error in refund payment", null, null, serviceResult);
                            }

                            paymentId = (String) serviceResult.get("paymentId");
                        } catch (GenericServiceException e) {
                            Debug.logError(e, "Problem running the refundPayment service", module);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemsWithTheRefundSeeLogs", locale));
                        }
                    } else {
                        // TODO: handle manual refunds (accounts payable)
                    }

                    //Debug.log("Finished handing refund payments", module);

                    // now; for all timestamps
                    Timestamp now = UtilDateTime.nowTimestamp();

                    // create a new response entry
                    String responseId = delegator.getNextSeqId("ReturnItemResponse");
                    GenericValue response = delegator.makeValue("ReturnItemResponse", UtilMisc.toMap("returnItemResponseId", responseId));
                    response.set("orderPaymentPreferenceId", orderPayPref.getString("orderPaymentPreferenceId"));
                    response.set("responseAmount", thisRefundAmount);
                    response.set("responseDate", now);
                    if (paymentId != null) {
                        // a null payment ID means no electronic refund was available; manual refund needed
                        response.set("paymentId", paymentId);
                    }

                    //Debug.log("About to create return response", module);

                    try {
                        delegator.create(response);
                    } catch (GenericEntityException e) {
                        Debug.logError(e, "Problems creating new ReturnItemResponse entity", module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemsCreatingReturnItemResponseEntity", locale));
                    }

                    //Debug.log("Return response created", module);

                    // set the response on each item
                    Iterator itemsIter = itemList.iterator();
                    while (itemsIter.hasNext()) {
                        GenericValue item = (GenericValue) itemsIter.next();
                        item.set("returnItemResponseId", responseId);
                        item.set("statusId", "RETURN_COMPLETED");

                        // create the status history
                        String returnStatusId = delegator.getNextSeqId("ReturnStatus");
                        GenericValue returnStatus = delegator.makeValue("ReturnStatus", UtilMisc.toMap("returnStatusId", returnStatusId));
                        returnStatus.set("statusId", item.get("statusId"));
                        returnStatus.set("returnId", item.get("returnId"));
                        returnStatus.set("returnItemSeqId", item.get("returnItemSeqId"));
                        returnStatus.set("statusDatetime", now);

                        //Debug.log("Updating item status", module);
                        try {
                            item.store();
                            delegator.create(returnStatus);
                        } catch (GenericEntityException e) {
                            Debug.logError("Problem updating the ReturnItem entity", module);
                            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderProblemUpdatingReturnItemReturnItemResponseId", locale));
                        }

                        //Debug.log("Item status and return status history created", module);
                    }
                }
            }
        }

        //Debug.log("Finished refund process");
        return ServiceUtil.returnSuccess();
    }

    // replacement return (create new order adjusted to be at no charge)
    public static Map processReplacementReturn(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        String returnId = (String) context.get("returnId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");

        GenericValue returnHeader = null;
        List returnItems = null;
        try {
            returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));
            if (returnHeader != null) {
                returnItems = returnHeader.getRelatedByAnd("ReturnItem", UtilMisc.toMap("returnTypeId", "RTN_REPLACE"));
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up return information", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorGettingReturnHeaderItemInformation", locale));
        }

        List createdOrderIds = new ArrayList();
        if (returnHeader != null && returnItems != null && returnItems.size() > 0) {
            Map itemsByOrder = new HashMap();
            Map totalByOrder = new HashMap();
            groupReturnItemsByOrder(returnItems, itemsByOrder, totalByOrder, delegator, returnId);

            // process each one by order
            Set itemSet = itemsByOrder.entrySet();
            Iterator itemByOrderIt = itemSet.iterator();
            while (itemByOrderIt.hasNext()) {
                Map.Entry entry = (Map.Entry) itemByOrderIt.next();
                String orderId = (String) entry.getKey();
                List items = (List) entry.getValue();

                // get order header & payment prefs
                GenericValue orderHeader = null;
                try {
                    orderHeader = delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Cannot get Order details for #" + orderId, module);
                    continue;
                }

                OrderReadHelper orh = new OrderReadHelper(orderHeader);

                // create the replacement order
                Map orderMap = UtilMisc.toMap("userLogin", userLogin);
                GenericValue placingParty = orh.getPlacingParty();
                String placingPartyId = null;
                if (placingParty != null) {
                    placingPartyId = placingParty.getString("partyId");
                }

                orderMap.put("orderTypeId", "SALES_ORDER");
                orderMap.put("partyId", placingPartyId);
                orderMap.put("productStoreId", orderHeader.get("productStoreId"));
                orderMap.put("webSiteId", orderHeader.get("webSiteId"));
                orderMap.put("visitId", orderHeader.get("visitId"));
                orderMap.put("currencyUom", orderHeader.get("currencyUom"));
                orderMap.put("grandTotal",  new Double(0.00));

                // make the contact mechs
                List contactMechs = new ArrayList();
                List orderCm = null;
                try {
                    orderCm = orderHeader.getRelated("OrderContactMech");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
                if (orderCm != null) {
                    Iterator orderCmi = orderCm.iterator();
                    while (orderCmi.hasNext()) {
                        GenericValue v = (GenericValue) orderCmi.next();
                        contactMechs.add(GenericValue.create(v));
                    }
                    orderMap.put("orderContactMechs", contactMechs);
                }

                // make the shipment prefs
                List shipmentPrefs = new ArrayList();
                List orderSp = null;
                try {
                    orderSp = orderHeader.getRelated("OrderShipmentPreference");
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                }
                if (orderSp != null) {
                    Iterator orderSpi = orderSp.iterator();
                    while (orderSpi.hasNext()) {
                        GenericValue v = (GenericValue) orderSpi.next();
                        shipmentPrefs.add(GenericValue.create(v));
                    }
                    orderMap.put("orderShipmentPreferences", shipmentPrefs);
                }

                // make the order items
                double itemTotal = 0.00;
                List orderItems = new ArrayList();
                if (items != null) {
                    Iterator ri = items.iterator();
                    int itemCount = 1;
                    while (ri.hasNext()) {
                        GenericValue returnItem = (GenericValue) ri.next();
                        GenericValue orderItem = null;
                        try {
                            orderItem = returnItem.getRelatedOne("OrderItem");
                        } catch (GenericEntityException e) {
                            Debug.logError(e, module);
                            continue;
                        }
                        if (orderItem != null) {
                            Double quantity = returnItem.getDouble("returnQuantity");
                            Double unitPrice = returnItem.getDouble("returnPrice");
                            if (quantity != null && unitPrice != null) {
                                itemTotal = (quantity.doubleValue() * unitPrice.doubleValue());
                                GenericValue newItem = delegator.makeValue("OrderItem", UtilMisc.toMap("orderItemSeqId", new Integer(itemCount).toString()));

                                newItem.set("orderItemTypeId", orderItem.get("orderItemTypeId"));
                                newItem.set("productId", orderItem.get("productId"));
                                newItem.set("productFeatureId", orderItem.get("productFeatureId"));
                                newItem.set("prodCatalogId", orderItem.get("prodCatalogId"));
                                newItem.set("productCategoryId", orderItem.get("productCategoryId"));
                                newItem.set("quantity", quantity);
                                newItem.set("unitPrice", unitPrice);
                                newItem.set("unitListPrice", orderItem.get("unitListPrice"));
                                newItem.set("itemDescription", orderItem.get("itemDescription"));
                                newItem.set("comments", orderItem.get("comments"));
                                newItem.set("correspondingPoId", orderItem.get("correspondingPoId"));
                                newItem.set("statusId", "ITEM_CREATED");
                                orderItems.add(newItem);
                            }
                        }
                    }
                    orderMap.put("orderItems", orderItems);
                } else {
                    Debug.logError("No return items found??", module);
                    continue;
                }

                // create the replacement adjustment
                GenericValue adj = delegator.makeValue("OrderAdjustment", new HashMap());
                adj.set("orderAdjustmentTypeId", "REPLACE_ADJUSTMENT");
                adj.set("amount", new Double(itemTotal * -1));
                adj.set("comments", "Replacement Item Return #" + returnId);
                adj.set("createdDate", UtilDateTime.nowTimestamp());
                adj.set("createdByUserLogin", userLogin.getString("userLoginId"));
                orderMap.put("orderAdjustments", UtilMisc.toList(adj));

                // we'll assume new order is under same terms as original.  note orderTerms is a required parameter of storeOrder
                try {
                    orderMap.put("orderTerms", orderHeader.getRelated("OrderTerm"));
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Cannot create replacement order because order terms for original order is not available", module);
                }

                // create the order
                String createdOrderId = null;
                Map orderResult = null;
                try {
                    orderResult = dispatcher.runSync("storeOrder", orderMap);
                } catch (GenericServiceException e) {
                    Debug.logInfo(e, "Problem creating the order!", module);
                }
                if (orderResult != null) {
                    createdOrderId = (String) orderResult.get("orderId");
                    createdOrderIds.add(createdOrderId);
                }

                // since there is no payments required; order is ready for processing/shipment
                if (createdOrderId != null) {
                    OrderChangeHelper.approveOrder(dispatcher, userLogin, createdOrderId);
                }
            }
        }

        StringBuffer successMessage = new StringBuffer();
        if (createdOrderIds.size() > 0) {
            successMessage.append("The following new orders have been created : ");
            Iterator i = createdOrderIds.iterator();
            while (i.hasNext()) {
                successMessage.append(i.next());
                if (i.hasNext()) {
                    successMessage.append(", ");
                }
            }
        } else {
            successMessage.append("No orders were created.");
        }

        return ServiceUtil.returnSuccess(successMessage.toString());
    }

    /**
     * Takes a List of returnItems and returns a Map of orderId -> items and a Map of orderId -> orderTotal 
     * @param returnItems
     * @param itemsByOrder
     * @param totalByOrder
     * @param delegator
     * @param returnId
     */
    public static void groupReturnItemsByOrder(List returnItems, Map itemsByOrder, Map totalByOrder, GenericDelegator delegator, String returnId) {
        Iterator itemIt = returnItems.iterator();
        while (itemIt.hasNext()) {
            GenericValue item = (GenericValue) itemIt.next();
            String orderId = item.getString("orderId");
            if (orderId != null) {
                if (itemsByOrder != null) {
                    List orderList = (List) itemsByOrder.get(orderId);
                    Double totalForOrder = null;
                    if (totalByOrder != null) {
                        totalForOrder = (Double) totalByOrder.get(orderId);
                    }
                    if (orderList == null) {
                        orderList = new ArrayList();
                    }
                    if (totalForOrder == null) {
                        totalForOrder = new Double(0.00);
                    }

                    // add to the items list
                    orderList.add(item);
                    itemsByOrder.put(orderId, orderList);

                    if (totalByOrder != null) {
                        // add on the total for this line
                        Double quantity = item.getDouble("returnQuantity");
                        Double amount = item.getDouble("returnPrice");
                        if (quantity == null) {
                            quantity = new Double(0);
                        }
                        if (amount == null) {
                            amount = new Double(0.00);
                        }
                        double thisTotal = amount.doubleValue() * quantity.doubleValue();
                        double existingTotal = totalForOrder.doubleValue();
                        Map condition = UtilMisc.toMap("returnId", item.get("returnId"), "returnItemSeqId", item.get("returnItemSeqId"));
                        Double newTotal = new Double(existingTotal + thisTotal + getReturnAdjustmentTotal(delegator, condition) );
                        totalByOrder.put(orderId, newTotal);
                    }
                }
            }
        }

        // We may also have some order-level adjustments, so we need to go through each order again and add those as well
        if ((totalByOrder != null) && (totalByOrder.keySet() != null)) {
            Iterator orderIterator = totalByOrder.keySet().iterator();
            while (orderIterator.hasNext()) {
                String orderId = (String) orderIterator.next();                
                // find returnAdjustment for returnHeader
                Map condition = UtilMisc.toMap("returnId", returnId, "returnItemSeqId", org.ofbiz.common.DataModelConstants.SEQ_ID_NA);
                double existingTotal = ((Double) totalByOrder.get(orderId)).doubleValue() + getReturnAdjustmentTotal(delegator, condition);
                totalByOrder.put(orderId, new Double(existingTotal));
            }
        }
    }

   
    public static Map getReturnAmountByOrder(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String returnId = (String) context.get("returnId");
        Locale locale = (Locale) context.get("locale");
        List returnItems = null;
        Map returnAmountByOrder = new HashMap();
        try {
            returnItems = delegator.findByAnd("ReturnItem", UtilMisc.toMap("returnId", returnId));
            
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up return information", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderErrorGettingReturnHeaderItemInformation", locale));
        }
        if ((returnItems != null) && (returnItems.size() > 0)) {
            Iterator returnItemIterator = returnItems.iterator();
            GenericValue returnItem = null;
            GenericValue returnItemResponse = null;
            GenericValue payment = null;
            String orderId;
            while (returnItemIterator.hasNext()) {
                returnItem = (GenericValue) returnItemIterator.next();
                orderId = returnItem.getString("orderId");
                try {
                    returnItemResponse = returnItem.getRelatedOne("ReturnItemResponse");
                    if (returnItemResponse != null && orderId != null) {
                        // TODO should we filter on payment's status (PMNT_SENT,PMNT_RECEIVED)
                        payment = returnItemResponse.getRelatedOne("Payment");
                        if (payment != null && payment.getDouble("amount") != null) {
                            UtilMisc.addToDoubleInMap(returnAmountByOrder, orderId, payment.getDouble("amount"));
                        }
                    }
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems looking up return item related information", module);
                    return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderErrorGettingReturnHeaderItemInformation", locale));
                }
            }
        }
        return UtilMisc.toMap("orderReturnAmountMap", returnAmountByOrder);
    }

    public static Map checkPaymentAmountForRefund(DispatchContext dctx, Map context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericDelegator delegator = dctx.getDelegator();
        String returnId = (String) context.get("returnId");
        Locale locale = (Locale) context.get("locale");
        Map returnAmountByOrder = null;
        Map serviceResult = null;
        //GenericValue orderHeader = null;
        try {
            serviceResult = dispatcher.runSync("getReturnAmountByOrder", org.ofbiz.base.util.UtilMisc.toMap("returnId", returnId));
        } catch (GenericServiceException e) {
            Debug.logError(e, "Problem running the getReturnAmountByOrder service", module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderProblemsWithGetReturnAmountByOrder", locale));
        }
        if (ServiceUtil.isError(serviceResult)) {
            return ServiceUtil.returnError((String) serviceResult.get(ModelService.ERROR_MESSAGE));
        } else {
            returnAmountByOrder = (Map) serviceResult.get("orderReturnAmountMap");
        }

        if ((returnAmountByOrder != null) && (returnAmountByOrder.keySet() != null)) {
            Iterator orderIterator = returnAmountByOrder.keySet().iterator();
            while (orderIterator.hasNext()) {
                String orderId = (String) orderIterator.next();
                Double returnAmount = (Double) returnAmountByOrder.get(orderId);
                OrderReadHelper helper = new OrderReadHelper(OrderReadHelper.getOrderHeader(delegator, orderId));
                double grandTotal = helper.getOrderGrandTotal();
                if (returnAmount == null) {
                    Debug.logInfo("No returnAmount found for order:" + orderId, module);
                } else {
                    if (grandTotal < returnAmount.doubleValue()) {
                        Debug.logError("Order [" + orderId + "] refund amount exceeds order total", module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource_error, "OrderRefundAmountExceedsOrderTotal", locale));
                    }
                }
            }
        }
        return ServiceUtil.returnSuccess();
    }

    public static Map createReturnAdjustment(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String orderAdjustmentId = (String) context.get("orderAdjustmentId");
        String returnAdjustmentTypeId = (String) context.get("returnAdjustmentTypeId");
        String returnId = (String) context.get("returnId");
        String returnItemSeqId = (String) context.get("returnItemSeqId");         
        String description = (String) context.get("description");
        
        GenericValue returnItemTypeMap = null;
        GenericValue orderAdjustment = null;
        GenericValue returnAdjustmentType = null;
        GenericValue orderItem = null;
        GenericValue returnItem = null;
        GenericValue returnHeader = null;
        
        Double amount;
        
        // if orderAdjustment is not empty, then copy most return adjustment information from orderAdjustment's
        if (orderAdjustmentId != null) {
            try {
                orderAdjustment = delegator.findByPrimaryKey("OrderAdjustment", UtilMisc.toMap("orderAdjustmentId", orderAdjustmentId));

                // get returnHeaderTypeId from ReturnHeader and then use it to figure out return item type mapping
                returnHeader = delegator.findByPrimaryKey("ReturnHeader", UtilMisc.toMap("returnId", returnId));
                String returnHeaderTypeId = ((returnHeader != null) && (returnHeader.getString("returnHeaderTypeId") != null)) ? returnHeader.getString("returnHeaderTypeId") : "CUSTOMER_RETURN";
                returnItemTypeMap = delegator.findByPrimaryKey("ReturnItemTypeMap",
                        UtilMisc.toMap("returnHeaderTypeId", returnHeaderTypeId, "returnItemMapKey", orderAdjustment.get("orderAdjustmentTypeId")));
                returnAdjustmentType = returnItemTypeMap.getRelatedOne("ReturnAdjustmentType");
                if (returnAdjustmentType != null && UtilValidate.isEmpty(description)) {
                    description = returnAdjustmentType.getString("description");
                }
                if (UtilValidate.isNotEmpty(returnItemSeqId)) {
                    returnItem = delegator.findByPrimaryKey("ReturnItem",
                            UtilMisc.toMap("returnId", returnId, "returnItemSeqId", returnItemSeqId));
                    Debug.log("returnId:" + returnId + ",returnItemSeqId:" + returnItemSeqId);
                    orderItem = returnItem.getRelatedOne("OrderItem");
                }
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                throw new GeneralRuntimeException(e.getMessage());
            }
            Map orderAdjustmentValue = orderAdjustment.getAllFields();
            orderAdjustmentValue.remove("oldAmountPerQuantity");
            orderAdjustmentValue.remove("oldPercentage");
            orderAdjustmentValue.remove("orderId");
            orderAdjustmentValue.remove("orderItemSeqId");
            context.putAll(orderAdjustment.getAllFields());

        }

        // if orderAdjustmentTypeId is empty, ie not found from orderAdjustmentId, then try to get returnAdjustmentTypeId from returnItemTypeMap, 
        // if still empty, use default RET_MAN_ADJ
        if (returnAdjustmentTypeId == null) {
            String mappingTypeId = returnItemTypeMap != null ? returnItemTypeMap.get("returnItemTypeId").toString() : null;
            returnAdjustmentTypeId = mappingTypeId != null ? mappingTypeId : "RET_MAN_ADJ";
        }
        // calculate the returnAdjustment amount
        if (returnItem != null) {  // returnAdjustment for returnItem
            if (needRecalculate(returnAdjustmentTypeId)) {
                Debug.logInfo("returnPrice:" + returnItem.getDouble("returnPrice") + ",returnQuantity:" + returnItem.getDouble("returnQuantity") + ",sourcePercentage:" + orderAdjustment.getDouble("sourcePercentage"), module);
                if (orderAdjustment == null) {
                    Debug.logError("orderAdjustment [" + orderAdjustmentId + "] not found", module);
                    return ServiceUtil.returnError("orderAdjustment [" + orderAdjustmentId + "] not found");
                }
                BigDecimal returnTotal = returnItem.getBigDecimal("returnPrice").multiply(returnItem.getBigDecimal("returnQuantity"));
                BigDecimal orderTotal = orderItem.getBigDecimal("quantity").multiply(orderItem.getBigDecimal("unitPrice"));                
                amount = getAdjustmentAmount("RET_SALES_TAX_ADJ".equals(returnAdjustmentType), returnTotal, orderTotal, orderAdjustment.getBigDecimal("amount"));
            } else {
                amount = (Double) context.get("amount");
            }
        } else { // returnAdjustment for returnHeader
            amount = (Double) context.get("amount");
        }

        // store the return adjustment
        String seqId = delegator.getNextSeqId("ReturnAdjustment");
        GenericValue newReturnAdjustment = delegator.makeValue("ReturnAdjustment",
                UtilMisc.toMap("returnAdjustmentId", seqId));

        try {
            newReturnAdjustment.setNonPKFields(context);
            newReturnAdjustment.set("amount", amount);
            newReturnAdjustment.set("returnAdjustmentTypeId", returnAdjustmentTypeId);
            newReturnAdjustment.set("description", description);
            newReturnAdjustment.set("returnItemSeqId", UtilValidate.isEmpty(returnItemSeqId) ? "_NA_" : returnItemSeqId);

            delegator.create(newReturnAdjustment);
            Map result = ServiceUtil.returnSuccess("Create ReturnAdjustment with Id:" + seqId + " successfully.");
            result.put("returnAdjustmentId", seqId);
            return result;
        } catch (GenericEntityException e) {
            Debug.logError(e, "Failed to store returnAdjustment", module);
            return ServiceUtil.returnError("Failed to store returnAdjustment");
        }
    }

    public static Map updateReturnAdjustment(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        double originalReturnPrice = ((Double) context.get("originalReturnPrice")).doubleValue();
        double originalReturnQuantity = ((Double) context.get("originalReturnQuantity")).doubleValue();
                
        GenericValue returnItem = null;
        GenericValue returnAdjustment = null;
        
        Double amount;
        
        String returnAdjustmentTypeId = (String) context.get("returnAdjustmentTypeId");
        String returnItemSeqId = (String) context.get("returnItemSeqId");
        try {
            returnAdjustment = delegator.findByPrimaryKey("ReturnAdjustment", UtilMisc.toMap("returnAdjustmentId", context.get("returnAdjustmentId")));
            if (returnItemSeqId != null) {
                returnItem = delegator.findByPrimaryKey("ReturnItem",
                        UtilMisc.toMap("returnId", context.get("returnId"), "returnItemSeqId", returnItemSeqId));
            }
            // calculate the returnAdjustment amount
            if (returnItem != null) {  // returnAdjustment for returnItem
                if (needRecalculate(returnAdjustmentTypeId)) {
                    BigDecimal returnTotal = returnItem.getBigDecimal("returnPrice").multiply(returnItem.getBigDecimal("returnQuantity"));
                    BigDecimal originalReturnTotal = new BigDecimal(originalReturnPrice).multiply(new BigDecimal(originalReturnQuantity));                    
                    amount = getAdjustmentAmount("RET_SALES_TAX_ADJ".equals(returnAdjustmentTypeId), returnTotal, originalReturnTotal, returnAdjustment.getBigDecimal("amount"));
                } else {
                    amount = (Double) context.get("amount");
                }
            } else { // returnAdjustment for returnHeader
                amount = (Double) context.get("amount");
            }

            returnAdjustment.setNonPKFields(context);
            returnAdjustment.set("amount", amount);
            delegator.store(returnAdjustment);
            Debug.logInfo("Update ReturnAdjustment with Id:" + context.get("returnAdjustmentId") + " to amount " + amount +" successfully.", module);
            Map result = ServiceUtil.returnSuccess("Update ReturnAdjustment with Id:" + context.get("returnAdjustmentId") + " to amount " + amount +" successfully.");
            return result;
        } catch (GenericEntityException e) {
            Debug.logError(e, "Failed to store returnAdjustment", module);
            return ServiceUtil.returnError("Failed to store returnAdjustment");
        }
    }

    //  used as a dispatch service, invoke different service based on the parameters passed in
    public static Map createReturnItemOrAdjustment(DispatchContext dctx, Map context){
        Debug.logInfo("createReturnItemOrAdjustment's context:" + context, module);
        String orderItemSeqId = (String) context.get("orderItemSeqId");
        Debug.logInfo("orderItemSeqId:" + orderItemSeqId +"#", module);
        LocalDispatcher dispatcher = dctx.getDispatcher();
        //if the request is to create returnItem, orderItemSeqId should not be empty
        String serviceName = UtilValidate.isNotEmpty(orderItemSeqId) ? "createReturnItem" : "createReturnAdjustment";
        Debug.logInfo("serviceName:" + serviceName, module);
        try {
            return dispatcher.runSync(serviceName, filterServiceContext(dctx, serviceName, context));
        } catch (org.ofbiz.service.GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
    }
    
    //  used as a dispatch service, invoke different service based on the parameters passed in
    public static Map updateReturnItemOrAdjustment(DispatchContext dctx, Map context){
        Debug.logInfo("updateReturnItemOrAdjustment's context:" + context, module);
        String returnAdjustmentId = (String) context.get("returnAdjustmentId");
        Debug.logInfo("returnAdjustmentId:" + returnAdjustmentId +"#", module);
        LocalDispatcher dispatcher = dctx.getDispatcher();
        //if the request is to create returnItem, orderItemSeqId should not be empty
        String serviceName = UtilValidate.isEmpty(returnAdjustmentId) ? "updateReturnItem" : "updateReturnAdjustment";
        Debug.logInfo("serviceName:" + serviceName, module);
        try {
            return dispatcher.runSync(serviceName, filterServiceContext(dctx, serviceName, context));
        } catch (org.ofbiz.service.GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }        
    }
    
    /**
     * These return adjustment types need to be recalculated when the return item is updated
     * @param returnAdjustmentTypeId
     * @return
     */
    public static boolean needRecalculate(String returnAdjustmentTypeId) {
        return "RET_PROMOTION_ADJ".equals(returnAdjustmentTypeId) ||
                "RET_DISCOUNT_ADJ".equals(returnAdjustmentTypeId) ||
                "RET_SALES_TAX_ADJ".equals(returnAdjustmentTypeId);

    }

    /**
     * Get the total return adjustments for a set of key -> value condition pairs.  Done for code efficiency.
     * @param delegator
     * @param condition
     * @return
     */
    public static double getReturnAdjustmentTotal(GenericDelegator delegator, Map condition) {
        double total = 0.0;
        List adjustments;
        try {
            // TODO: find on a view-entity with a sum is probably more efficient
            adjustments = delegator.findByAnd("ReturnAdjustment", condition);
            if (adjustments != null) {
                Iterator adjustmentIterator = adjustments.iterator();
                while (adjustmentIterator.hasNext()) {
                    GenericValue returnAdjustment = (GenericValue) adjustmentIterator.next();
                    total += returnAdjustment.getDouble("amount").doubleValue();
                }
            }
        } catch (org.ofbiz.entity.GenericEntityException e) {
            Debug.logError(e, module);
        }
        return total;
    }

    /**
     *  Get rid of unnecessary parameters based on the given service name  
     * @param dctx Service DispatchContext
     * @param serviceName  
     * @param context   context before clean up
     * @return filtered context
     * @throws GenericServiceException 
     */
    public static Map filterServiceContext(DispatchContext dctx, String serviceName, Map context) throws GenericServiceException {
        ModelService modelService = dctx.getModelService(serviceName);

        if (modelService == null) {
            throw new GenericServiceException("Problems getting the service model");
        }
        Map serviceContext = FastMap.newInstance();
        List modelParmInList = modelService.getInModelParamList();
        Iterator modelParmInIter = modelParmInList.iterator();
        while (modelParmInIter.hasNext()) {
            ModelParam modelParam = (ModelParam) modelParmInIter.next();
            String paramName =  modelParam.name;
 
            Object value = context.get(paramName);
            if(value != null){
                serviceContext.put(paramName, value);
            }             
        }
        return serviceContext;
    }

    /**
     * Calculate new returnAdjustment amount and set scale and rounding mode based on returnAdjustmentType: RET_SALES_TAX_ADJ use sales.tax._ and others use order._
     * @param isSalesTax  if returnAdjustmentType is SaleTax  
     * @param returnTotal
     * @param originalTotal
     * @param amount
     * @return  new returnAdjustment amount
     */
    public static Double getAdjustmentAmount(boolean isSalesTax, BigDecimal returnTotal, BigDecimal originalTotal, BigDecimal amount) {
        String settingPrefix = isSalesTax ? "salestax" : "order";
        String decimalsPrefix = isSalesTax ? ".calc" : "";
        int decimals = UtilNumber.getBigDecimalScale(settingPrefix + decimalsPrefix + ".decimals");
        int rounding = UtilNumber.getBigDecimalRoundingMode(settingPrefix + ".rounding");
        int finalDecimals = isSalesTax ? UtilNumber.getBigDecimalScale(settingPrefix + ".final.decimals") : decimals;
        returnTotal = returnTotal.setScale(decimals, rounding);
        originalTotal = originalTotal.setScale(decimals, rounding);
        BigDecimal newAmount = returnTotal.divide(originalTotal, decimals, rounding).multiply(amount).setScale(finalDecimals, rounding);
        return new Double(newAmount.doubleValue());
    }
}
