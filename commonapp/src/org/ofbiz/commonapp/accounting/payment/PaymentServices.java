/*
 * $Id$
 * 
 *  Copyright (c) 2001 The Open For Business Project and repected authors.
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

package org.ofbiz.commonapp.accounting.payment;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.security.*;
import org.ofbiz.core.service.*;

/**
 * Services for Payment maintenance
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * Created on January 18, 2001
 */
public class PaymentServices {

    /**
     * Deletes a PaymentMethod entity according to the parameters passed in the context
     * <b>security check</b>: userLogin partyId must equal paymentMethod partyId, or must have PAY_INFO_DELETE permission
     * @param ctx The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map deletePaymentMethod(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Timestamp now = UtilDateTime.nowTimestamp();
        
        //never delete a PaymentMethod, just put a to date on the link to the party
        String paymentMethodId = (String) context.get("paymentMethodId");
        GenericValue paymentMethod = null;
        try {
            paymentMethod = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId));
        } catch(GenericEntityException e) {
            Debug.logWarning(e.toString());
            return ServiceUtil.returnError("ERROR: Could not find Payment Method to delete (read failure: " + e.getMessage() + ")");
        }

        if(paymentMethod == null) {
            return ServiceUtil.returnError("ERROR: Could not find Payment Method to delete (read failure)");
        }

        //<b>security check</b>: userLogin partyId must equal paymentMethod partyId, or must have PAY_INFO_DELETE permission
        if (paymentMethod.get("partyId") == null || !paymentMethod.getString("partyId").equals(userLogin.getString("partyId"))) {
            if (!security.hasEntityPermission("PAY_INFO", "_DELETE", userLogin)) {
                return ServiceUtil.returnError("You do not have permission to delete Payment Method for this partyId");
            }
        }
        
        paymentMethod.set("thruDate", now);
        try {
            paymentMethod.store(); 
        } catch(GenericEntityException e) {
            Debug.logWarning(e.toString());
            return ServiceUtil.returnError("ERROR: Could not delete Payment Method (write failure): " + e.getMessage());
        }
        
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Creates CreditCard and PaymentMethod entities according to the parameters passed in the context
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PAY_INFO_CREATE permission
     * @param ctx The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map createCreditCard(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PAY_INFO", "_CREATE");
        if (result.size() > 0)
            return result;
        
        //do some more complicated/critical validation...
        List messages = new LinkedList();
        if (!UtilValidate.isCardMatch((String) context.get("cardType"), (String) context.get("cardNumber")))
            messages.add((String) context.get("cardNumber") + UtilValidate.isCreditCardPrefixMsg + 
                    (String) context.get("cardType") + UtilValidate.isCreditCardSuffixMsg + 
                    " (It appears to be a " + UtilValidate.getCardType((String) context.get("cardNumber")) + " credit card number)");
        if (!UtilValidate.isDateAfterToday((String) context.get("expireDate"))) 
            messages.add("The expiration date " + (String) context.get("expireDate") + " is before today.");
        if (messages.size() > 0) {
            return ServiceUtil.returnError(messages);
        }

        Collection toBeStored = new LinkedList();
        GenericValue newPm = delegator.makeValue("PaymentMethod", null);
        toBeStored.add(newPm);
        GenericValue newCc = delegator.makeValue("CreditCard", null);
        toBeStored.add(newCc);

        Long newPmId = delegator.getNextSeqId("PaymentMethod");
        if(newPmId == null) {
            return ServiceUtil.returnError("ERROR: Could not create credit card (id generation failure)");
        }
        newPm.set("partyId", partyId);
        newPm.set("fromDate", (context.get("fromDate") != null ? context.get("fromDate") : now));
        newPm.set("thruDate", context.get("thruDate"));
        newCc.set("nameOnCard", context.get("nameOnCard"));
        newCc.set("companyNameOnCard", context.get("companyNameOnCard"));
        newCc.set("cardType", context.get("cardType"));
        newCc.set("cardNumber", context.get("cardNumber"));
        newCc.set("cardSecurityCode", context.get("cardSecurityCode"));
        newCc.set("expireDate", context.get("expireDate"));

        newPm.set("paymentMethodId", newPmId.toString());
        newPm.set("paymentMethodTypeId", "CREDIT_CARD");
        newCc.set("paymentMethodId", newPmId.toString());

        GenericValue newPartyContactMechPurpose = null;
        String contactMechId = (String) context.get("contactMechId");
        if (contactMechId != null && contactMechId.length() > 0 && !contactMechId.equals("_NEW_")) {
            //set the contactMechId on the credit card
            newCc.set("contactMechId", context.get("contactMechId"));
            //add a PartyContactMechPurpose of BILLING_LOCATION if necessary
            String contactMechPurposeTypeId = "BILLING_LOCATION";

            GenericValue tempVal = null;
            try {
                Collection allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId), null), true);
                tempVal = EntityUtil.getFirst(allPCMPs);
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                tempVal = null;
            }

            if (tempVal == null) {
                //no value found, create a new one
                newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", now));
            }
        }

        if (newPartyContactMechPurpose != null)
            toBeStored.add(newPartyContactMechPurpose);

        try {
            delegator.storeAll(toBeStored);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("ERROR: Could not create credit card (write failure): " + e.getMessage());
        }

        result.put("paymentMethodId", newCc.getString("paymentMethodId"));
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    /**
     * Updates CreditCard and PaymentMethod entities according to the parameters passed in the context
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PAY_INFO_UPDATE permission
     * @param ctx The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map updateCreditCard(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PAY_INFO", "_UPDATE");
        if (result.size() > 0)
            return result;

        //do some more complicated/critical validation...
        List messages = new LinkedList();
        if (!UtilValidate.isCardMatch((String) context.get("cardType"), (String) context.get("cardNumber")))
            messages.add((String) context.get("cardNumber") + UtilValidate.isCreditCardPrefixMsg + 
                    (String) context.get("cardType") + UtilValidate.isCreditCardSuffixMsg + 
                    " (It appears to be a " + UtilValidate.getCardType((String) context.get("cardNumber")) + " credit card number)");
        if (!UtilValidate.isDateAfterToday((String) context.get("expireDate"))) 
            messages.add("The expiration date " + (String) context.get("expireDate") + " is before today.");
        if (messages.size() > 0) {
            return ServiceUtil.returnError(messages);
        }

        Collection toBeStored = new LinkedList();
        boolean isModified = false;

        GenericValue paymentMethod = null;
        GenericValue newPm = null;
        GenericValue creditCard = null;
        GenericValue newCc = null;
        String paymentMethodId = (String) context.get("paymentMethodId");
        try {
            creditCard = delegator.findByPrimaryKey("CreditCard", UtilMisc.toMap("paymentMethodId", paymentMethodId));
            paymentMethod = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("ERROR: Could not get credit card to update (read error): " + e.getMessage());
        }

        if (creditCard == null || paymentMethod == null) {
            return ServiceUtil.returnError("ERROR: Could not find credit card to update with payment method id " + paymentMethodId);
        }
        
        newPm = new GenericValue(paymentMethod);
        toBeStored.add(newPm);
        newCc = new GenericValue(creditCard);
        toBeStored.add(newCc);

        Long newPmId = delegator.getNextSeqId("PaymentMethod");
        if(newPmId == null) {
            return ServiceUtil.returnError("ERROR: Could not update credit card info (id generation failure)");
        }
        
        newPm.set("partyId", partyId);
        newPm.set("fromDate", context.get("fromDate"), false);
        newPm.set("thruDate", context.get("thruDate"));
        newCc.set("nameOnCard", context.get("nameOnCard"));
        newCc.set("companyNameOnCard", context.get("companyNameOnCard"));
        newCc.set("cardType", context.get("cardType"));
        newCc.set("cardNumber", context.get("cardNumber"));
        newCc.set("cardSecurityCode", context.get("cardSecurityCode"));
        newCc.set("expireDate", context.get("expireDate"));
        newCc.set("contactMechId", context.get("contactMechId"));

        if (!newCc.equals(creditCard) || !newPm.equals(paymentMethod)) {
            newPm.set("paymentMethodId", newPmId.toString());
            newCc.set("paymentMethodId", newPmId.toString());

            newPm.set("fromDate", (context.get("fromDate") != null ? context.get("fromDate") : now));
            isModified = true;
        }

        GenericValue newPartyContactMechPurpose = null;
        String contactMechId = (String) context.get("contactMechId");
        if (contactMechId != null && contactMechId.length() > 0) {
            //add a PartyContactMechPurpose of BILLING_LOCATION if necessary
            String contactMechPurposeTypeId = "BILLING_LOCATION";

            GenericValue tempVal = null;
            try {
                Collection allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId), null), true);
                tempVal = EntityUtil.getFirst(allPCMPs);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                tempVal = null;
            }

            if(tempVal == null) {
                //no value found, create a new one
                newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", now));
            }
        }

        if(isModified) {
            //Debug.logInfo("yes, is modified");
            if (newPartyContactMechPurpose != null) toBeStored.add(newPartyContactMechPurpose);

            //set thru date on old paymentMethod
            paymentMethod.set("thruDate", now);
            toBeStored.add(paymentMethod);

            try {
                delegator.storeAll(toBeStored);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                return ServiceUtil.returnError("ERROR: Could not update credit card (write failure): " + e.getMessage());
            }
        } else {
            result.put("newPaymentMethodId", paymentMethodId);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put(ModelService.SUCCESS_MESSAGE, "No changes made, not updating credit card");
            
            return result;
        }

        result.put("newPaymentMethodId", newCc.getString("paymentMethodId"));
    
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Creates EftAccount and PaymentMethod entities according to the parameters passed in the context
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PAY_INFO_CREATE permission
     * @param ctx The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map createEftAccount(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PAY_INFO", "_CREATE");
        if (result.size() > 0)
            return result;
        
        Collection toBeStored = new LinkedList();
        GenericValue newPm = delegator.makeValue("PaymentMethod", null);
        toBeStored.add(newPm);
        GenericValue newEa = delegator.makeValue("EftAccount", null);
        toBeStored.add(newEa);

        Long newPmId = delegator.getNextSeqId("PaymentMethod");
        if(newPmId == null) {
            return ServiceUtil.returnError("ERROR: Could not create credit card (id generation failure)");
        }
        newPm.set("partyId", partyId);
        newPm.set("fromDate", (context.get("fromDate") != null ? context.get("fromDate") : now));
        newPm.set("thruDate", context.get("thruDate"));
        newEa.set("bankName", context.get("bankName"));
        newEa.set("routingNumber", context.get("routingNumber"));
        newEa.set("accountType", context.get("accountType"));
        newEa.set("accountNumber", context.get("accountNumber"));
        newEa.set("nameOnAccount", context.get("nameOnAccount"));
        newEa.set("companyNameOnAccount", context.get("companyNameOnAccount"));
        newEa.set("contactMechId", context.get("contactMechId"));

        newPm.set("paymentMethodId", newPmId.toString());
        newPm.set("paymentMethodTypeId", "EFT_ACCOUNT");
        newEa.set("paymentMethodId", newPmId.toString());

        GenericValue newPartyContactMechPurpose = null;
        String contactMechId = (String) context.get("contactMechId");
        if (contactMechId != null && contactMechId.length() > 0) {
            //add a PartyContactMechPurpose of BILLING_LOCATION if necessary
            String contactMechPurposeTypeId = "BILLING_LOCATION";

            GenericValue tempVal = null;
            try {
                Collection allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId), null), true);
                tempVal = EntityUtil.getFirst(allPCMPs);
            } catch (GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                tempVal = null;
            }

            if (tempVal == null) {
                //no value found, create a new one
                newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", now));
            }
        }

        if (newPartyContactMechPurpose != null)
            toBeStored.add(newPartyContactMechPurpose);

        try {
            delegator.storeAll(toBeStored);
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("ERROR: Could not create credit card (write failure): " + e.getMessage());
        }

        result.put("paymentMethodId", newEa.getString("paymentMethodId"));
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
    
    /**
     * Updates EftAccount and PaymentMethod entities according to the parameters passed in the context
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PAY_INFO_UPDATE permission
     * @param ctx The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map updateEftAccount(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PAY_INFO", "_UPDATE");
        if (result.size() > 0)
            return result;

        Collection toBeStored = new LinkedList();
        boolean isModified = false;

        GenericValue paymentMethod = null;
        GenericValue newPm = null;
        GenericValue eftAccount = null;
        GenericValue newEa = null;
        String paymentMethodId = (String) context.get("paymentMethodId");
        try {
            eftAccount = delegator.findByPrimaryKey("EftAccount", UtilMisc.toMap("paymentMethodId", paymentMethodId));
            paymentMethod = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId));
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("ERROR: Could not get EFT Account to update (read error): " + e.getMessage());
        }

        if (eftAccount == null || paymentMethod == null) {
            return ServiceUtil.returnError("ERROR: Could not find EFT Account to update with id " + paymentMethodId);
        }
        
        newPm = new GenericValue(paymentMethod);
        toBeStored.add(newPm);
        newEa = new GenericValue(eftAccount);
        toBeStored.add(newEa);

        Long newPmId = delegator.getNextSeqId("PaymentMethod");
        if(newPmId == null) {
            return ServiceUtil.returnError("ERROR: Could not update EFT Account info (id generation failure)");
        }
        
        newPm.set("partyId", partyId);
        newPm.set("fromDate", context.get("fromDate"), false);
        newPm.set("thruDate", context.get("thruDate"));
        newEa.set("bankName", context.get("bankName"));
        newEa.set("routingNumber", context.get("routingNumber"));
        newEa.set("accountType", context.get("accountType"));
        newEa.set("accountNumber", context.get("accountNumber"));
        newEa.set("nameOnAccount", context.get("nameOnAccount"));
        newEa.set("companyNameOnAccount", context.get("companyNameOnAccount"));
        newEa.set("contactMechId", context.get("contactMechId"));

        if (!newEa.equals(eftAccount) || !newPm.equals(paymentMethod)) {
            newPm.set("paymentMethodId", newPmId.toString());
            newEa.set("paymentMethodId", newPmId.toString());

            newPm.set("fromDate", (context.get("fromDate") != null ? context.get("fromDate") : now));
            isModified = true;
        }

        GenericValue newPartyContactMechPurpose = null;
        String contactMechId = (String) context.get("contactMechId");
        if (contactMechId != null && contactMechId.length() > 0) {
            //add a PartyContactMechPurpose of BILLING_LOCATION if necessary
            String contactMechPurposeTypeId = "BILLING_LOCATION";

            GenericValue tempVal = null;
            try {
                Collection allPCMPs = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId), null), true);
                tempVal = EntityUtil.getFirst(allPCMPs);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                tempVal = null;
            }

            if(tempVal == null) {
                //no value found, create a new one
                newPartyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "fromDate", now));
            }
        }

        if(isModified) {
            //Debug.logInfo("yes, is modified");
            if (newPartyContactMechPurpose != null) toBeStored.add(newPartyContactMechPurpose);

            //set thru date on old paymentMethod
            paymentMethod.set("thruDate", now);
            toBeStored.add(paymentMethod);

            try {
                delegator.storeAll(toBeStored);
            } catch(GenericEntityException e) {
                Debug.logWarning(e.getMessage());
                return ServiceUtil.returnError("ERROR: Could not update EFT Account (write failure): " + e.getMessage());
            }
        } else {
            result.put("newPaymentMethodId", paymentMethodId);
            result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
            result.put(ModelService.SUCCESS_MESSAGE, "No changes made, not updating EFT Account");
            
            return result;
        }

        result.put("newPaymentMethodId", newEa.getString("paymentMethodId"));
    
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }

    /**
     * Creates a Payment entity according to the parameters passed in the context
     * <b>security check</b>: userLogin partyId must equal partyId, or must have PAY_INFO_UPDATE permission
     * @param ctx The DispatchContext that this service is operating in
     * @param context Map containing the input parameters
     * @return Map with the result of the service, the output parameters
     */
    public static Map createPayment(DispatchContext ctx, Map context) {
        Map result = new HashMap();
        GenericDelegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Timestamp now = UtilDateTime.nowTimestamp();

        String partyId = ServiceUtil.getPartyIdCheckSecurity(userLogin, security, context, result, "PAY_INFO", "_CREATE");
        if (result.size() > 0) {
            if (partyId != context.get("partyIdFrom") && partyId != context.get("partyIdTo")) {
                return ServiceUtil.returnError("ERROR: To Create a Payment you must either be the to or from party or have the PAY_INFO_CREATE or PAY_INFO_ADMIN permissions.");
            }
        }
        
        Long newPmId = delegator.getNextSeqId("Payment");
        if(newPmId == null) {
            return ServiceUtil.returnError("ERROR: Could not Create Payment (id generation failure)");
        }
        
        GenericValue payment = delegator.makeValue("Payment", null);
        payment.set("paymentId", newPmId.toString());
        payment.set("paymentTypeId", context.get("paymentTypeId"));
        payment.set("paymentMethodTypeId", context.get("paymentMethodTypeId"));
        payment.set("paymentMethodId", context.get("paymentMethodId"));
        payment.set("partyIdFrom", context.get("partyIdFrom"));
        payment.set("partyIdTo", context.get("partyIdTo"));
        payment.set("effectiveDate", context.get("effectiveDate") != null ? context.get("effectiveDate") : now);
        payment.set("paymentRefNum", context.get("paymentRefNum"));
        payment.set("amount", context.get("amount"));
        payment.set("comments", context.get("comments"));

        try {
            payment.create();
        } catch(GenericEntityException e) {
            Debug.logWarning(e.getMessage());
            return ServiceUtil.returnError("ERROR: Could not Create Payment (write failure): " + e.getMessage());
        }

        result.put("paymentId", payment.getString("paymentId"));
        result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        return result;
    }
}
