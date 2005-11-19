/* $$ID$ */
package org.ofbiz.order.order;

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
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.product.store.ProductStoreWorker;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
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
    public static final String resource = "org.ofbiz.order.order.PackageMessages";
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

    // worker method which can be used in screen iterations
    public static Double getReturnItemInitialCost(GenericDelegator delegator, String returnId, String returnItemSeqId) {
        if (delegator == null || returnId == null || returnItemSeqId == null) {
            throw new IllegalArgumentException("Method parameters cannot contain nulls");
        }

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

        // check for an orderItem association
        if (returnItem != null) {
            String orderId = returnItem.getString("orderId");
            String orderItemSeqId = returnItem.getString("orderItemSeqId");
            if (orderItemSeqId != null && orderId != null) {
                // locate the item issuance(s) for this order item
                List itemIssue = null;
                try {
                    itemIssue = delegator.findByAnd("ItemIssuance", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
                } catch (GenericEntityException e) {
                    Debug.logError(e, module);
                    throw new GeneralRuntimeException(e.getMessage());
                }
                if (itemIssue != null && itemIssue.size() > 0) {
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
                        itemCost = inventoryItem.getDouble("unitCost");
                    }
                }
            }
        }

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

        // get the returnable price
        double returnablePrice = 0.00;
        if (returnableQuantity > 0) {
            // get all order adjustments
            List orderAdjustments = null;
            try {
                orderAdjustments = delegator.findByAnd("OrderAdjustment", UtilMisc.toMap("orderId", orderItem.get("orderId")));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resource_error,"OrderErrorUnableToGetOrderAdjustmentsFromItem", locale));
            }
            returnablePrice = OrderReadHelper.getOrderItemTotal(orderItem, orderAdjustments);
            returnablePrice = (returnablePrice / orderQty);
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("returnableQuantity", new Double(returnableQuantity));
        result.put("returnablePrice", new Double(returnablePrice));
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
            groupReturnItemsByOrder(returnItems, itemsByOrder, totalByOrder);

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
                if (orderPayPrefs != null && orderPayPrefs.size() > 0) {
                    Iterator payPrefIter = orderPayPrefs.iterator();
                    do {
                        GenericValue pref = (GenericValue) payPrefIter.next();
                        Double maxAmount = pref.getDouble("maxAmount");
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
                    } while (neededAmount > 0 && payPrefIter.hasNext());
                }

                if (neededAmount != 0) {
                    Debug.logError("Was not able to find needed payment preferences for the order RTN: " + returnId + " ORD: " + orderId, module);
                    continue;
                }

                Map prefSplitMap = new HashMap();
                if (prefsToUse == null || prefsToUse.size() == 0) {
                    Debug.logError("We didn't find any possible payment prefs to use for RTN: " + returnId + " ORD: " + orderId, module);
                    continue;
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
                            Map serviceResult = dispatcher.runSync("refundPayment", UtilMisc.toMap("orderPaymentPreference", orderPayPref, "refundAmount", thisRefundAmount, "userLogin", userLogin));
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
                    String responseId = delegator.getNextSeqId("ReturnItemResponse").toString();
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
                        String returnStatusId = delegator.getNextSeqId("ReturnStatus").toString();
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
            groupReturnItemsByOrder(returnItems, itemsByOrder, totalByOrder);

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

    public static void groupReturnItemsByOrder(List returnItems, Map itemsByOrder, Map totalByOrder) {
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
                        Double newTotal = new Double(existingTotal + thisTotal);
                        totalByOrder.put(orderId, newTotal);
                    }
                }
            }
        }
    }

}
