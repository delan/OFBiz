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
package org.ofbiz.accounting.payment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.PageContext;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * Worker methods for Payments
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class PaymentWorker {
    
    public static final String module = PaymentWorker.class.getName();
    
    public static void getPartyPaymentMethodValueMaps(PageContext pageContext, String partyId, boolean showOld, String paymentMethodValueMapsAttr) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");
        List paymentMethodValueMaps = getPartyPaymentMethodValueMaps(delegator, partyId, showOld);
        pageContext.setAttribute(paymentMethodValueMapsAttr, paymentMethodValueMaps);
    }

    public static List getPartyPaymentMethodValueMaps(GenericDelegator delegator, String partyId, boolean showOld) {
        List paymentMethodValueMaps = new LinkedList();
        try {
            List paymentMethods = delegator.findByAnd("PaymentMethod", UtilMisc.toMap("partyId", partyId));

            if (!showOld) paymentMethods = EntityUtil.filterByDate(paymentMethods, true);
            if (paymentMethods != null) {
                Iterator pmIter = paymentMethods.iterator();

                while (pmIter.hasNext()) {
                    GenericValue paymentMethod = (GenericValue) pmIter.next();
                    Map valueMap = new HashMap();

                    paymentMethodValueMaps.add(valueMap);
                    valueMap.put("paymentMethod", paymentMethod);
                    if ("CREDIT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {
                        GenericValue creditCard = paymentMethod.getRelatedOne("CreditCard");
                        if (creditCard != null) valueMap.put("creditCard", creditCard);
                    } else if ("GIFT_CARD".equals(paymentMethod.getString("paymentMethodTypeId"))) {
                        GenericValue giftCard = paymentMethod.getRelatedOne("GiftCard");
                        if (giftCard != null) valueMap.put("giftCard", giftCard);
                    } else if ("EFT_ACCOUNT".equals(paymentMethod.getString("paymentMethodTypeId"))) {
                        GenericValue eftAccount = paymentMethod.getRelatedOne("EftAccount");
                        if (eftAccount != null) valueMap.put("eftAccount", eftAccount);
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e, module);
        }
        return paymentMethodValueMaps;
    }

    /** TODO: REMOVE (DEJ 20030301): This is the OLD style and should be removed when the eCommerce and party mgr JSPs are */
    public static void getPaymentMethodAndRelated(PageContext pageContext, String partyId,
            String paymentMethodAttr, String creditCardAttr, String eftAccountAttr, String paymentMethodIdAttr, String curContactMechIdAttr,
            String donePageAttr, String tryEntityAttr) {

        ServletRequest request = pageContext.getRequest();
        Map results = getPaymentMethodAndRelated(request, partyId);
        
        if (results.get("paymentMethod") != null) pageContext.setAttribute(paymentMethodAttr, results.get("paymentMethod"));
        if (results.get("creditCard") != null) pageContext.setAttribute(creditCardAttr, results.get("creditCard"));
        if (results.get("eftAccount") != null) pageContext.setAttribute(eftAccountAttr, results.get("eftAccount"));
        if (results.get("paymentMethodId") != null) pageContext.setAttribute(paymentMethodIdAttr, results.get("paymentMethodId"));
        if (results.get("curContactMechId") != null) pageContext.setAttribute(curContactMechIdAttr, results.get("curContactMechId"));
        if (results.get("donePage") != null) pageContext.setAttribute(donePageAttr, results.get("donePage"));
        if (results.get("tryEntity") != null) pageContext.setAttribute(tryEntityAttr, results.get("tryEntity"));
    }
    
    public static Map getPaymentMethodAndRelated(ServletRequest request, String partyId) {
        GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
        Map results = new HashMap();
        
        boolean tryEntity = true;
        if (request.getAttribute("_ERROR_MESSAGE_") != null) tryEntity = false;

        String donePage = request.getParameter("DONE_PAGE");
        if (donePage == null || donePage.length() <= 0)
            donePage = "viewprofile";
        results.put("donePage", donePage);

        String paymentMethodId = request.getParameter("paymentMethodId");

        // check for a create
        if (request.getAttribute("paymentMethodId") != null) {
            paymentMethodId = (String) request.getAttribute("paymentMethodId");
        }

        // check for an update
        if (request.getAttribute("newPaymentMethodId") != null) {
            paymentMethodId = (String) request.getAttribute("newPaymentMethodId");
        }

        results.put("paymentMethodId", paymentMethodId);

        GenericValue paymentMethod = null;
        GenericValue creditCard = null;
        GenericValue giftCard = null;
        GenericValue eftAccount = null;

        if (UtilValidate.isNotEmpty(paymentMethodId)) {
            try {
                paymentMethod = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId));
                creditCard = delegator.findByPrimaryKey("CreditCard", UtilMisc.toMap("paymentMethodId", paymentMethodId));
                giftCard = delegator.findByPrimaryKey("GiftCard", UtilMisc.toMap("paymentMethodId", paymentMethodId));
                eftAccount = delegator.findByPrimaryKey("EftAccount", UtilMisc.toMap("paymentMethodId", paymentMethodId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e, module);
            }
        }
        if (paymentMethod != null) {
            results.put("paymentMethod", paymentMethod);
        } else {
            tryEntity = false;
        }

        if (creditCard != null) {
            results.put("creditCard", creditCard);
        }
        if (giftCard != null) {
            results.put("giftCard", giftCard);
        }
        if (eftAccount != null) {
            results.put("eftAccount", eftAccount);
        }

        String curContactMechId = null;

        if (creditCard != null) {
            curContactMechId = UtilFormatOut.checkNull(tryEntity ? creditCard.getString("contactMechId") : request.getParameter("contactMechId"));
        } else if (giftCard != null) {
            curContactMechId = UtilFormatOut.checkNull(tryEntity ? giftCard.getString("contactMechId") : request.getParameter("contactMechId"));
        } else if (eftAccount != null) {
            curContactMechId = UtilFormatOut.checkNull(tryEntity ? eftAccount.getString("contactMechId") : request.getParameter("contactMechId"));
        }
        if (curContactMechId != null) {
            results.put("curContactMechId", curContactMechId);
        }

        results.put("tryEntity", new Boolean(tryEntity));
        
        return results;
    }
            
    public static GenericValue getPaymentAddress(GenericDelegator delegator, String partyId) {                
        List paymentAddresses = null;        
        try {
            paymentAddresses = delegator.findByAnd("PartyContactMechPurpose", 
                UtilMisc.toMap("partyId", partyId, "contactMechPurposeTypeId", "PAYMENT_LOCATION"), 
                UtilMisc.toList("-fromDate"));
            paymentAddresses = EntityUtil.filterByDate(paymentAddresses);                
        } catch (GenericEntityException e) {
            Debug.logError(e, "Trouble getting PartyContactMechPurpose entity list", module);            
        }
        
        // get the address for the primary contact mech
        GenericValue purpose = EntityUtil.getFirst(paymentAddresses);
        GenericValue postalAddress = null;
        if (purpose != null) {
            try {
                postalAddress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", purpose.getString("contactMechId")));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Trouble getting PostalAddress record for contactMechId: " + purpose.getString("contactMechId"), module);
            }
        }
                                
        return postalAddress;   
    } 
    
    /**
     * Returns the total from a list of Payment entities
     * 
     * @param payments List of Payment GenericValue items
     * @return total payments as double
     */
    public static double getPaymentsTotal(List payments) {
        if (payments == null) {
            throw new IllegalArgumentException("Payment list cannot be null");            
        }
        
        double paymentsTotal = 0.00;
        Iterator i = payments.iterator();
        while (i.hasNext()) {
            GenericValue payment = (GenericValue) i.next();
            paymentsTotal += payment.getDouble("amount").doubleValue();
        }
        return paymentsTotal;
    }          
}
