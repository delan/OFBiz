/*
 * $Id: CheckOutHelper.java,v 1.3 2003/08/27 13:53:25 ajzeneski Exp $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.order.shoppingcart;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderChangeHelper;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.party.contact.ContactHelper;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * A facade over the ShoppingCart to simplify the relatively complex
 * processing required to create an order in the system.
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:cnelson@einnovation.com">Chris Nelson</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:tristana@twibble.org">Tristan Austin</a>
 * @version    $Revision: 1.3 $
 * @since      2.0
 */
public class CheckOutHelper {

    public static final String module = CheckOutHelper.class.getName();
    
    private ShoppingCart cart;
    private GenericDelegator delegator;
    private LocalDispatcher dispatcher;
    
    public CheckOutHelper (LocalDispatcher dispatcher, GenericDelegator delegator, ShoppingCart cart) {
        this.delegator = delegator;
        this.dispatcher = dispatcher;
        this.cart = cart;
    }    
         
    public Map setCheckOutOptions(String shippingMethod, String shippingContactMechId, String checkOutPaymentId,            
            String correspondingPoId, String shippingInstructions, String orderAdditionalEmails,
            String maySplit, String giftMessage, String isGift) {
        List errorMessages = new ArrayList();
        Map result;

        if (this.cart != null && this.cart.size() > 0) {
            String billingAccountId = null;
            if (checkOutPaymentId != null && checkOutPaymentId.startsWith("EXT_BILLACT")) {
                billingAccountId = checkOutPaymentId.substring(checkOutPaymentId.indexOf('|')+1);
                checkOutPaymentId = "EXT_BILLACT"; 
            } 
            
            if (UtilValidate.isNotEmpty(shippingMethod)) {
                int delimiterPos = shippingMethod.indexOf('@');
                String shipmentMethodTypeId = null;
                String carrierPartyId = null;

                if (delimiterPos > 0) {
                    shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
                    carrierPartyId = shippingMethod.substring(delimiterPos + 1);
                }

                this.cart.setShipmentMethodTypeId(shipmentMethodTypeId);
                this.cart.setCarrierPartyId(carrierPartyId);
            } else {
                errorMessages.add("Please Select a Shipping Method");
            }
            this.cart.setShippingInstructions(shippingInstructions);
            if (UtilValidate.isNotEmpty(maySplit)) {
                cart.setMaySplit(Boolean.valueOf(maySplit));
            } else {
                errorMessages.add("Please Select a Splitting Preference");
            }
            this.cart.setGiftMessage(giftMessage);
            if (UtilValidate.isNotEmpty(isGift)) {
                cart.setIsGift(Boolean.valueOf(isGift));
            } else {
                errorMessages.add("Please Specify Whether or Not This Order is a Gift");
            }
            
            this.cart.setOrderAdditionalEmails(orderAdditionalEmails);

            // set the shipping address
            if (UtilValidate.isNotEmpty(shippingContactMechId)) {
                this.cart.setShippingContactMechId(shippingContactMechId);
            } else {
                errorMessages.add("Please Select a Shipping Destination");
            }

            // set the payment method option
            if (UtilValidate.isNotEmpty(checkOutPaymentId)) {
                // clear out the old payments
                this.cart.clearPaymentMethodTypeIds();
                this.cart.clearPaymentMethodIds();
                // all payment method ids will be numeric, type ids will start with letter
                if (Character.isLetter(checkOutPaymentId.charAt(0))) {
                    this.cart.addPaymentMethodTypeId(checkOutPaymentId);
                } else {
                    this.cart.addPaymentMethodId(checkOutPaymentId);
                }            
            } else if (UtilValidate.isEmpty(checkOutPaymentId)) {
                errorMessages.add("Please Select a Method of Billing");
            }
                
            // set the billingAccountId - if null then set it to null (resetting)
            this.cart.setBillingAccountId(billingAccountId);
            
            // set the PO number              
            if (UtilValidate.isNotEmpty(correspondingPoId)) {
                this.cart.setPoNumber(correspondingPoId);
            } else {
                this.cart.setPoNumber("(none)");
            }            
                                              
        } else {
            errorMessages.add("There are no items in the cart.");
        }

        if (errorMessages.size() == 1) {
            result = ServiceUtil.returnError(errorMessages.get(0).toString());
        }else if (errorMessages.size() > 0) {
            result = ServiceUtil.returnError(errorMessages);
        } else {
            result = ServiceUtil.returnSuccess();
        }
        
        return result;
    }

    // Create order event - uses createOrder service for processing
    public Map createOrder(GenericValue userLogin, String distributorId, String affiliateId,
            List trackingCodeOrders, boolean areOrderItemsExploded, String visitId, String webSiteId) {
        if (this.cart == null) {
            return null; 
        }
        String orderId = this.cart.getOrderId();
        Map result;
        
        // format the grandTotal
        String currencyFormat = UtilProperties.getPropertyValue("general.properties", "currency.decimal.format", "##0.00");
        DecimalFormat formatter = new DecimalFormat(currencyFormat);
        double cartTotal = this.cart.getGrandTotal();
		String grandTotalString = formatter.format(cartTotal);               
        Double grandTotal = null;
		try {
			grandTotal = new Double(formatter.parse(grandTotalString).doubleValue());
		} catch (ParseException e) {
            Debug.logError(e, "Problem getting parsed currency amount from DecimalFormat", module);
            result = ServiceUtil.returnError("ERROR: Could not create order (problem parsing order totals");
            return result;			
		}

        // store the order - build the context
        Map context = this.cart.makeCartMap(this.dispatcher, areOrderItemsExploded);

        //get the TrackingCodeOrder List
        context.put("trackingCodeOrders", trackingCodeOrders);
        
        if (distributorId != null) context.put("distributorId", distributorId);
        if (affiliateId != null) context.put("affiliateId", affiliateId);
        
        // need the partyId; don't use userLogin in case of an order via order mgr
        String partyId = this.cart.getPartyId();
        String productStoreId = cart.getProductStoreId();           
               
        context.put("grandTotal", grandTotal);
        context.put("userLogin", userLogin);
        context.put("partyId", partyId);
        context.put("productStoreId", productStoreId);
        context.put("visitId", visitId);
        context.put("webSiteId", webSiteId);

        // store the order - invoke the service
        Map storeResult = null;

        try {
            storeResult = dispatcher.runSync("storeOrder", context);
            orderId = (String) storeResult.get("orderId");
            if (orderId != null && orderId.length() > 0) {
                this.cart.setOrderId(orderId);
                if (this.cart.getFirstAttemptOrderId() == null) {
                    this.cart.setFirstAttemptOrderId(orderId);
                }
            }
        } catch (GenericServiceException e) {
            result = ServiceUtil.returnError("ERROR: Could not create order (problem invoking the service: " + e.getMessage() + ")");
            Debug.logError(e, module);
            return result;
        }

        // check for error message(s)
        if (ModelService.RESPOND_ERROR.equals(storeResult.get(ModelService.RESPONSE_MESSAGE)) ||
            storeResult.containsKey(ModelService.ERROR_MESSAGE) ||
            storeResult.containsKey(ModelService.ERROR_MESSAGE)) {

            result = ServiceUtil.returnError("Did not complete the order, the following occurred:");
            return result;
        }

        // set the orderId for use by chained events
        result = ServiceUtil.returnSuccess();
        result.put("order_id", orderId);
        result.put("orderId", orderId);
        result.put("orderAdditionalEmails", this.cart.getOrderAdditionalEmails());
        
        // save the emails to the order                              
        List toBeStored = new LinkedList();
               
        GenericValue party = null;
        try {
            party = this.delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e, "Problems getting Party record", module);
            party = null;
        }
        
        // create order contact mechs for the email address(s)
        if (party != null) {
            Iterator emailIter = UtilMisc.toIterator(ContactHelper.getContactMechByType(party, "EMAIL_ADDRESS", false));
            while (emailIter != null && emailIter.hasNext()) {
                GenericValue email = (GenericValue) emailIter.next();
                GenericValue orderContactMech = this.delegator.makeValue("OrderContactMech",
                        UtilMisc.toMap("orderId", orderId, "contactMechId", email.getString("contactMechId"), "contactMechPurposeTypeId", "ORDER_EMAIL"));
                toBeStored.add(orderContactMech);                                   
            }
        }
                                             
        // create dummy contact mechs and order contact mechs for the additional emails    
        String additionalEmails = this.cart.getOrderAdditionalEmails();  
        List emailList = StringUtil.split(additionalEmails, ",");
        if (emailList == null) emailList = new ArrayList();                                       
        Iterator eli = emailList.iterator();       
        while (eli.hasNext()) {
            String email = (String) eli.next();
            String contactMechId = this.delegator.getNextSeqId("ContactMech").toString();
            GenericValue contactMech = this.delegator.makeValue("ContactMech", 
                    UtilMisc.toMap("contactMechId", contactMechId, "contactMechTypeId", "EMAIL_ADDRESS", "infoString", email)); 
                                                    
            GenericValue orderContactMech = this.delegator.makeValue("OrderContactMech", 
                    UtilMisc.toMap("orderId", orderId, "contactMechId", contactMechId, "contactMechPurposeTypeId", "ORDER_EMAIL"));
            toBeStored.add(contactMech);
            toBeStored.add(orderContactMech);                                                         
        }
        
        if (toBeStored.size() > 0) {
            try {
                if (Debug.verboseOn()) Debug.logVerbose("To Be Stored: " + toBeStored, module);
                this.delegator.storeAll(toBeStored);
                /* Why is this here?
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {}
                */
            } catch (GenericEntityException e) {
                // not a fatal error; so just print a message
                Debug.logWarning(e, "Problems storing order email contact information", module);
            }
        }
        
        return result;
    }

    public void calcAndAddTax() throws GeneralException {
        if (!"SALES_ORDER".equals(cart.getOrderType())) {
            return;
        }
        List items = this.cart.makeOrderItems();
        List adjs = this.cart.makeAllAdjustments();
        GenericValue shipAddress = this.cart.getShippingAddress();
        String productStoreId = cart.getProductStoreId();

        if (shipAddress == null) {
            throw new GeneralException("Shipping address is not set in the shopping cart.");
        }

        // remove old tax adjustments
        this.cart.removeAdjustmentByType("SALES_TAX");

        // get the tax adjustments
        List taxReturn = getTaxAdjustments(dispatcher, "calcTax", productStoreId, items, adjs, shipAddress);

        if (Debug.verboseOn()) Debug.logVerbose("ReturnList: " + taxReturn, module);

        List orderAdj = (List) taxReturn.get(0);
        List itemAdj = (List) taxReturn.get(1);

        // pass the order adjustments back
        if (orderAdj != null && orderAdj.size() > 0) {
            Iterator oai = orderAdj.iterator();

            while (oai.hasNext())
            this.cart.addAdjustment((GenericValue) oai.next());
        }

        // return the order item adjustments
        if (itemAdj != null && itemAdj.size() > 0) {
            List cartItems = this.cart.items();

            for (int i = 0; i < cartItems.size(); i++) {
                ShoppingCartItem item = (ShoppingCartItem) cartItems.get(i);
                List itemAdjustments = (List) itemAdj.get(i);
                Iterator ida = itemAdjustments.iterator();

                while (ida.hasNext())
                    item.addAdjustment((GenericValue) ida.next());
            }
        }
    }

    // Calc the tax adjustments.
    private List getTaxAdjustments(LocalDispatcher dispatcher, String taxService, String productStoreId,
    	List orderItems, List allAdjustments, GenericValue shipAddress) throws GeneralException {
        List products = new ArrayList(orderItems.size());
        List amounts = new ArrayList(orderItems.size());
        List shipAmts = new ArrayList(orderItems.size());

        double orderSubTotal = OrderReadHelper.getOrderItemsSubTotal(orderItems, allAdjustments);
        List orderHeaderAdjustments = OrderReadHelper.getOrderHeaderAdjustments(allAdjustments);
        Double cartShipping = new Double(OrderReadHelper.calcOrderAdjustments(orderHeaderAdjustments, orderSubTotal, false, false, true));

        // build up the list of tax calc service parameters
        for (int i = 0; i < orderItems.size(); i++) {
            GenericValue orderItem = (GenericValue) orderItems.get(i);

            try {
                products.add(i, orderItem.getRelatedOne("Product"));  // get the product entity
                amounts.add(i, new Double(OrderReadHelper.getOrderItemSubTotal(orderItem, allAdjustments, true, false))); // get the item amount
                shipAmts.add(i, new Double(OrderReadHelper.getOrderItemAdjustmentsTotal(orderItem, allAdjustments, false, false, true))); // get the shipping amount
            } catch (GenericEntityException e) {
                Debug.logError(e, "Cannot read order item entity (" + e.getMessage() + ")", module);
                throw new GeneralException("Cannot read the order item entity", e);
            }
        }
        Map serviceContext = UtilMisc.toMap("productStoreId", productStoreId, "itemProductList", products, "itemAmountList", amounts,
                "itemShippingList", shipAmts, "orderShippingAmount", cartShipping, "shippingAddress", shipAddress);

        Map serviceResult = null;

        try {
            serviceResult = dispatcher.runSync(taxService, serviceContext);
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            throw new GeneralException("Problem occurred in tax service (" + e.getMessage() + ")", e);
        }

        // the adjustments (returned in order) from taxware.
        List orderAdj = (List) serviceResult.get("orderAdjustments");
        List itemAdj = (List) serviceResult.get("itemAdjustments");

        return UtilMisc.toList(orderAdj, itemAdj);
    }

    public Map processPayment(GenericValue productStore, GenericValue userLogin) throws GeneralException {                
        // Get some payment related strings
        String DECLINE_MESSAGE = productStore.getString("authDeclinedMessage");
        String ERROR_MESSAGE = productStore.getString("authErrorMessage");
        String RETRY_ON_ERROR = productStore.getString("retryFailedAuths");

        // Get the orderId from the cart.
        String orderId = this.cart.getOrderId();
        
        // Get the paymentMethodTypeId - this will need to change when ecom supports multiple payments
        List paymentMethodTypeId = this.cart.getPaymentMethodTypeIds(); 

        // Check the payment preferences; if we have ANY w/ status PAYMENT_NOT_AUTH invoke payment service.
        boolean requireAuth = false;
        List paymentPreferences = null;
        try {
            Map paymentFields = UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_NOT_AUTH");
            paymentPreferences = this.delegator.findByAnd("OrderPaymentPreference", paymentFields);
        } catch (GenericEntityException e) {
            throw new GeneralException("Problems getting payment preferences", e);
        }
        if (paymentPreferences != null && paymentPreferences.size() > 0)
            requireAuth = true;
        
        // Invoke payment processing.
        if (requireAuth) {
            Map paymentResult = null;
            try {
                // invoke the payment gateway service.
                paymentResult = dispatcher.runSync("authOrderPayments", UtilMisc.toMap("orderId", orderId));
            } catch (GenericServiceException e) {
                Debug.logWarning(e, module);
            }
            if (Debug.verboseOn()) Debug.logVerbose("Finsished w/ Payment Service", module);
            if (paymentResult != null && paymentResult.containsKey("processResult")) {
                String authResp = (String) paymentResult.get("processResult");

                if (authResp.equals("FAILED")) {
                    // order was NOT approved
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth was NOT a success!", module);                                      
                                       
                    boolean ok = OrderChangeHelper.rejectOrder(dispatcher, userLogin, orderId);
                    if (!ok) {
                        throw new GeneralException("Problem with order change; see above error");
                    }                                                
                        
                    // null out the orderId for next pass.
                    cart.setOrderId(null);
                    return ServiceUtil.returnError(DECLINE_MESSAGE);                                                               
                } else if (authResp.equals("APPROVED")) {
                    // order WAS approved
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth was a success!", module);
                                        
                    // set the order and item status to approved  
                    boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
                    if (!ok) {
                        throw new GeneralException("Problem with order change; see above error");
                    }                                                                                         
                        
                    return ServiceUtil.returnSuccess();                           
                } else if (authResp.equals("ERROR")) {
                    // service failed
                    if (Debug.verboseOn()) Debug.logVerbose("Payment auth failed due to processor trouble.", module);               
                    if ("Y".equalsIgnoreCase(RETRY_ON_ERROR)) {                    
                        return ServiceUtil.returnSuccess(ERROR_MESSAGE);
                    } else {
                        boolean ok = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId);
                        if (!ok) {
                            throw new GeneralException("Problem with order change; see above error");
                        }
                        // null out orderId for next pass
                        this.cart.setOrderId(null);
                        return ServiceUtil.returnError(ERROR_MESSAGE);
                    }                    
                } else {
                    // should never happen
                    return ServiceUtil.returnError("Please contact customer service; payment return code unknown.");
                }
            } else {
                // result returned null == service failed
                if (Debug.verboseOn()) Debug.logVerbose("Payment auth failed due to processor trouble.", module);               
                if ("Y".equalsIgnoreCase(RETRY_ON_ERROR)) {                    
                    return ServiceUtil.returnSuccess(ERROR_MESSAGE);
                } else {
                    boolean ok = OrderChangeHelper.cancelOrder(dispatcher, userLogin, orderId);
                    if (!ok) {
                        throw new GeneralException("Problem with order change; see above error");
                    }
                    // null out orderId for next pass
                    this.cart.setOrderId(null);
                    return ServiceUtil.returnError(ERROR_MESSAGE);
                }                                                                                               
            }
        } else if (this.cart.getBillingAccountId() != null || paymentMethodTypeId.contains("EXT_COD")) {
            // approve all billing account or COD transactions (would not be able to use account if limit is reached)
            // note this is okay for now since only one payment method can be used; but this will need to be adjusted later
            boolean ok = OrderChangeHelper.approveOrder(dispatcher, userLogin, orderId);
            if (!ok) {
                throw new GeneralException("Problem with order change; see above error");
            }   
            
            return ServiceUtil.returnSuccess();                      
        } else {
            // Handle NO payment gateway as a success.
            return ServiceUtil.returnSuccess();            
        }
    }
    
    public Map checkOrderBlacklist(GenericValue userLogin) {
        Map result;
        GenericValue shippingAddressObj = this.cart.getShippingAddress();
        String shippingAddress = UtilFormatOut.checkNull(shippingAddressObj.getString("address1").toUpperCase());
        List exprs = UtilMisc.toList(new EntityExpr(
                new EntityExpr("blacklistString", true, EntityOperator.EQUALS, shippingAddress, true), EntityOperator.AND,                
                new EntityExpr("orderBlacklistTypeId", EntityOperator.EQUALS, "BLACKLIST_ADDRESS")));
        
        List paymentMethods = this.cart.getPaymentMethods();
        Iterator i = paymentMethods.iterator();
        while (i.hasNext()) {
            GenericValue paymentMethod = (GenericValue) i.next();
            if (paymentMethod.getString("paymentMethodTypeId").equals("CREDIT_CARD")) {
                GenericValue creditCard = null;
                GenericValue billingAddress = null;
                try {
                    creditCard = paymentMethod.getRelatedOne("CreditCard");
                    if (creditCard != null)
                        billingAddress = creditCard.getRelatedOne("PostalAddress");
                } catch (GenericEntityException e) {
                    Debug.logError(e, "Problems getting credit card from payment method", module);
                    result = ServiceUtil.returnError("Problems reading the database, please try again.");
                    return result;                    
                }
                if (creditCard != null) {
                    String creditCardNumber = UtilFormatOut.checkNull(creditCard.getString("cardNumber"));
                    exprs.add(new EntityExpr(
                            new EntityExpr("blacklistString", EntityOperator.EQUALS, creditCardNumber), EntityOperator.AND,                            
                            new EntityExpr("orderBlacklistTypeId", EntityOperator.EQUALS, "BLACKLIST_CREDITCARD")));
                }
                if (billingAddress != null) {
                    String address = UtilFormatOut.checkNull(billingAddress.getString("address1").toUpperCase());
                    exprs.add(new EntityExpr(
                            new EntityExpr("blacklistString", true, EntityOperator.EQUALS, address, true), EntityOperator.AND,                            
                            new EntityExpr("orderBlacklistTypeId", EntityOperator.EQUALS, "BLACKLIST_ADDRESS")));
                }  
            }
        }
        
        List blacklistFound = null;
        if (exprs.size() > 0) {            
            try {
                blacklistFound = this.delegator.findByOr("OrderBlacklist", exprs);
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems with OrderBlacklist lookup.", module);
                result = ServiceUtil.returnError("Problems reading the database, please try again.");
                return result;
            }
        }
        
        if (blacklistFound != null && blacklistFound.size() > 0) {
            result = ServiceUtil.returnError("failed"); 
            return result;
        } else {
            result = ServiceUtil.returnSuccess("success");
            return result;
        }
    }
    
    public Map failedBlacklistCheck(GenericValue userLogin, GenericValue productStore) {
        Map result;
    
    	String REJECT_MESSAGE = productStore.getString("authFraudMessage");

        // Get the orderId from the cart.
        String orderId = this.cart.getOrderId();
        
        // set the order/item status - reverse inv
        OrderChangeHelper.rejectOrder(dispatcher, userLogin, orderId);
        
        // nuke the userlogin
        userLogin.set("enabled", "N");
        try {
            userLogin.store();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems de-activating userLogin.", module);
            result = ServiceUtil.returnError("Database error.");
            return result;
        }  
        result = ServiceUtil.returnSuccess();
        result.put(ModelService.ERROR_MESSAGE, REJECT_MESSAGE);      
        
        // wipe the cart and session
        this.cart.clear();
        return result;
    }   
        
    public Map checkExternalPayment(String orderId) {
        Map result;
        // warning there can only be ONE payment preference for this to work
        // you cannot accept multiple payment type when using an external gateway
        GenericValue orderHeader = null;
        try {
            orderHeader = this.delegator.findByPrimaryKey("OrderHeader", UtilMisc.toMap("orderId", orderId));  
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems getting order header", module);
            result = ServiceUtil.returnError("Problems getting order header. Cannot check external gateways!");
            return result;
        }
        if (orderHeader != null) {
            List paymentPrefs = null;
            try {
                paymentPrefs = orderHeader.getRelated("OrderPaymentPreference");
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems getting order payments", module);
                result = ServiceUtil.returnError("Problems getting payment preference. Cannot check external gateways!");
                return result;
            }
            if (paymentPrefs != null && paymentPrefs.size() > 0) {
                if (paymentPrefs.size() > 1) {
                    Debug.logError("Too many payment preferences, you cannot have more then one when using external gateways", module);
                }
                GenericValue paymentPreference = EntityUtil.getFirst(paymentPrefs);
                String paymentMethodTypeId = paymentPreference.getString("paymentMethodTypeId");
                if (paymentMethodTypeId.startsWith("EXT_")) {
                    String type = paymentMethodTypeId.substring(4);
                    result = ServiceUtil.returnSuccess();
                    result.put("type", type.toLowerCase());
                    return result;
                }                
            } 
            result = ServiceUtil.returnSuccess();
            result.put("type", "none");
            return result;
        } else {
            result = ServiceUtil.returnError("Error, cannot located order for processing.");
            result.put("type", "error");
            return result;
        }        
    }
    
    /**
     * Sets the shipping contact mechanism on the cart
     * 
     * @param shippingContactMechId The identifier of the contact
     * @return A Map conforming to the OFBiz Service conventions containing
     * any error messages
     */
    public Map finalizeOrderEntryShip(String shippingContactMechId) {
        Map result;
        
        //Verify the field is valid
        if (UtilValidate.isNotEmpty(shippingContactMechId)) {
            this.cart.setShippingContactMechId(shippingContactMechId);
            result = ServiceUtil.returnSuccess();
        } else {
            result = ServiceUtil.returnError("Please enter a shipping address");
        }
        
        return result;   
    }
    
    /**
     * Sets the options associated with the order
     * 
     * @param shippingMethod The shipping method indicating the carrier and
     * shipment type to use
     * @param shippingInstructions Any additional handling instructions
     * @param maySplit "true" or anything else for <code>false</code> 
     * @param giftMessage A message to have included for the recipient
     * @param isGift "true" or anything else for <code>false</code>
     * @return A Map conforming to the OFBiz Service conventions containing
     * any error messages
     */
    public Map finalizeOrderEntryOptions(String shippingMethod,
            String shippingInstructions, String maySplit, String giftMessage, String isGift) {
        Map result;
        
        //Verify the shipping method is valid
        if (UtilValidate.isNotEmpty(shippingMethod)) {
            int delimiterPos = shippingMethod.indexOf('@');
            String shipmentMethodTypeId = null;
            String carrierPartyId = null;

            if (delimiterPos > 0) {
                shipmentMethodTypeId = shippingMethod.substring(0, delimiterPos);
                carrierPartyId = shippingMethod.substring(delimiterPos + 1);
            }

            this.cart.setShipmentMethodTypeId(shipmentMethodTypeId);
            this.cart.setCarrierPartyId(carrierPartyId);
        } else {
            result = ServiceUtil.returnError("Please Select a Shipping Method");
        }  
        
        //Set the remaining order options
        this.cart.setShippingInstructions(shippingInstructions);  
        this.cart.setGiftMessage(giftMessage);  
        this.cart.setMaySplit(Boolean.valueOf(maySplit)); 
        this.cart.setIsGift(Boolean.valueOf(isGift));
        
        result = ServiceUtil.returnSuccess();
        return result;
    }
    
    /**
     * Indicates whether the payment should be made offline or
     * whether further settings will be given for the electronic
     * payment method.
     * 
     * @param paymentMthodType "offline" to indicate that is to be paid 
     * offline, <code>null</code> or anything else for online.
     * @return A Map conforming to the OFBiz Service conventions containing
     * any error messages
     */
    public Map finalizeOrderEntryMethodType(String paymentMthodType) {
        Map result;
        
        this.cart.clearPaymentMethodTypeIds();
        this.cart.clearPaymentMethodIds();
        if (paymentMthodType != null && paymentMthodType.equals("offline")) {                                
            this.cart.addPaymentMethodTypeId("EXT_OFFLINE");
        }
        
        result = ServiceUtil.returnSuccess();
        return result;            
    }
    
    /**
     * Sets the payment ID to use during the checkout process
     * 
     * @param checkOutPaymentId The type of payment to use, should
     * be "OFFLINE_PAYMENT" to indicate offlinem otherwise the payment ID
     * will be associated with the cart
     * @return A Map conforming to the OFBiz Service conventions containing
     * any error messages. Includes the field "OFFLINE_PAYMENT"
     * containing a <code>Boolean</code> indicating whether it's an offline
     * payment or not. 
     */
    public Map finalizeOrderEntryPayment(String checkOutPaymentId) {
        Map result = ServiceUtil.returnSuccess();
        
        if (UtilValidate.isNotEmpty(checkOutPaymentId)) {
            if (!checkOutPaymentId.equals("OFFLINE_PAYMENT")) {                
                // clear out the old payments
                this.cart.clearPaymentMethodTypeIds();
                this.cart.clearPaymentMethodIds();
                // all payment method ids will be numeric, type ids will start with letter
                if (Character.isLetter(checkOutPaymentId.charAt(0))) {
                    this.cart.addPaymentMethodTypeId(checkOutPaymentId);
                } else {
                    this.cart.addPaymentMethodId(checkOutPaymentId);
                }
            } else {
                this.cart.clearPaymentMethodIds();
                this.cart.clearPaymentMethodTypeIds();
                result.put("OFFLINE_PAYMENT", new Boolean(true));
            }
        }
        
        return result;
    }
    
    /**
     * Defines the payment options for an order.
     *   
     * @param params Contains the amount associated with
     * each <code>paymentMethodTypeId</code>.
     * @return A Map conforming to the OFBiz Service conventions containing
     * any error messages. Includes the field "OFFLINE_PAYMENTS"
     * containing a <code>Boolean</code> indicating whether it's an offline
     * payment or not. 
     */
    public Map finalizeOrderEntryOfflinePayments(Map params) {
        Map result = ServiceUtil.returnSuccess();
        List errorMessages = new ArrayList();
        
        // get a list of payment types
        List paymentTypes = null;
        try {
            paymentTypes = delegator.findAll("PaymentMethodType");
        } catch (GenericEntityException e) {
            Debug.logError(e, "Cannot get payment method types from datasource", module);
        }   
        if (paymentTypes != null) {
            Map paymentPrefs = new HashMap();
            double paymentTally = 0.00;
            Iterator pi = paymentTypes.iterator();
            while (pi.hasNext()) {
                GenericValue paymentMethodType = (GenericValue) pi.next();   
                String paymentType = null;
                if (paymentMethodType != null && paymentMethodType.get("paymentMethodTypeId") != null) {
                    paymentType = paymentMethodType.getString("paymentMethodTypeId");
                }
                    
                // get the amount by type
                double paymentAmount = 0.00;
                if (paymentType != null && !paymentType.equals("OFFLINE")) {                                      
                    String amount = (String)params.get(paymentMethodType.getString("paymentMethodTypeId"));
                    if (amount != null && amount.length() > 0) {
                        try {                                                                                
                            paymentAmount = NumberFormat.getNumberInstance().parse(amount).doubleValue();                                                           
                        } catch (java.text.ParseException pe) {
                            result = ServiceUtil.returnError("Problems parsing amount.");
                            return result;
                        }
                    }
                }
                    
                // only worry about types w/ an amount
                if (paymentAmount > 0.00) {
                   paymentPrefs.put(paymentType, new Double(paymentAmount));
                   paymentTally += paymentAmount;                        
                }                    
            }
                
            double cartTotal = cart.getGrandTotal();
            if (cartTotal != paymentTally) {
                result = ServiceUtil.returnError("Totals do not match order total.");
                return result;
            } else {
                Set keySet = paymentPrefs.keySet();
                Iterator i = keySet.iterator();
                while (i.hasNext()) {
                    String type = (String) i.next();
                    Double amt = (Double) paymentPrefs.get(type);
                    cart.addPaymentMethodTypeId(type, amt);
                }
                result.put("OFFLINE_PAYMENTS", new Boolean(true));
            }
        }
        
        return result;
    }

    /**
     * Performs all the finalization settings and combines all the results.
     * This is a convenience method, primarily to match the original 
     * code structure of the adapter class <code>CheckOutEvents</code>.
     * <p>
     * I would prefer to remove this altogether and move the method
     * {@link #addErrors(List, Map, Map) addErrors} to the utility
     * class {@link ServiceUtil ServiceUtil}  
     * 
     * @see CheckOutHelper#finalizeOrderEntryMethodType(String)
     * @see CheckOutHelper#finalizeOrderEntryOfflinePayments(Map)
     * @see CheckOutHelper#finalizeOrderEntryOptions(String, String, String, String, String)
     * @see CheckOutHelper#finalizeOrderEntryPayment(String)
     * @see CheckOutHelper#finalizeOrderEntryShip(String)
     */
    public Map finalizeOrderEntry(String finalizeMode, String shippingContactMechId, String shippingMethod,
            String shippingInstructions, String maySplit, String giftMessage, String isGift, String methodType, 
            String checkOutPaymentId, Map params) {
        Map result = ServiceUtil.returnSuccess();
        Map errorMaps = new HashMap();
        Map callResult;
        List errorMessages = new ArrayList();
                 
        // set the shipping method
        if (finalizeMode != null && finalizeMode.equals("ship")) {
            callResult = this.finalizeOrderEntryShip(shippingContactMechId);
            this.addErrors(errorMessages, errorMaps, callResult);                
        }
            
        // set the options
        if (finalizeMode != null && finalizeMode.equals("options")) {
            callResult = this.finalizeOrderEntryOptions(shippingMethod, shippingInstructions, maySplit, giftMessage, isGift);
            this.addErrors(errorMessages, errorMaps, callResult);                  
        }
        
        // payment option; if offline we skip the payment screen
        if (finalizeMode != null && finalizeMode.equals("payoption")) {   
            callResult = this.finalizeOrderEntryMethodType(methodType);
            this.addErrors(errorMessages, errorMaps, callResult);                   
        }
        
        // set the payment
        if (finalizeMode != null && finalizeMode.equals("payment")) {   
            callResult = this.finalizeOrderEntryPayment(checkOutPaymentId);
            this.addErrors(errorMessages, errorMaps, callResult);                
        } 

        // create offline payments
        if (finalizeMode != null && finalizeMode.equals("offline_payments")) {   
            callResult = this.finalizeOrderEntryOfflinePayments(params);
            this.addErrors(errorMessages, errorMaps, callResult);
        }
        
        //See whether we need to return an error or not
        if (errorMessages.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_LIST, errorMessages);       
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        }
        if (errorMaps.size() > 0) {
            result.put(ModelService.ERROR_MESSAGE_MAP, errorMaps);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
        }
        
        return result;
    }
    
    /**
     * Takes the result of an invocation and extracts any error messages
     * and adds them to the targetList. This will handle both List and String
     * error messags.
     * 
     * @param targetList    The List to add the error messages to
     * @param targetMap The Map to add any Map error messages to
     * @param callResult The result from an invocation 
     */
    private void addErrors(List targetList, Map targetMap, Map callResult) {
        List newList;
        Map.Entry entry;
        Iterator mapIter;
        Map errorMsgMap;
        StringBuffer outMsg;
        
        //See if there is a single message
        if (callResult.containsKey(ModelService.ERROR_MESSAGE)) {
            targetList.add(callResult.get(ModelService.ERROR_MESSAGE));
        }

        //See if there is a message list
        if (callResult.containsKey(ModelService.ERROR_MESSAGE_LIST)) {
            newList = (List)callResult.get(ModelService.ERROR_MESSAGE_LIST);
            targetList.addAll(newList);
        }
        
        //See if there are an error message map
        if (callResult.containsKey(ModelService.ERROR_MESSAGE_MAP)) {
            errorMsgMap = (Map)callResult.get(ModelService.ERROR_MESSAGE_MAP);
            targetMap.putAll(errorMsgMap);
        }
    }
}
