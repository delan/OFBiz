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
package org.ofbiz.commonapp.accounting.payment;

import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 * Worker methods for Payments
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision$
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
                    } else if ("EFT_ACCOUNT".equals(paymentMethod.getString("paymentMethodTypeId"))) {
                        GenericValue eftAccount = paymentMethod.getRelatedOne("EftAccount");

                        if (eftAccount != null) valueMap.put("eftAccount", eftAccount);
                    }
                }
            }
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
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
        if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

        String donePage = request.getParameter("DONE_PAGE");
        if (donePage == null || donePage.length() <= 0)
            donePage = "viewprofile";
        results.put("donePage", donePage);

        String paymentMethodId = request.getParameter("paymentMethodId");

        if (request.getAttribute("paymentMethodId") != null) paymentMethodId = (String) request.getAttribute("paymentMethodId");
        results.put("paymentMethodId", paymentMethodId);

        GenericValue paymentMethod = null;
        GenericValue creditCard = null;
        GenericValue eftAccount = null;

        if (UtilValidate.isNotEmpty(paymentMethodId)) {
            try {
                paymentMethod = delegator.findByPrimaryKey("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId));
                creditCard = delegator.findByPrimaryKey("CreditCard", UtilMisc.toMap("paymentMethodId", paymentMethodId));
                eftAccount = delegator.findByPrimaryKey("EftAccount", UtilMisc.toMap("paymentMethodId", paymentMethodId));
            } catch (GenericEntityException e) {
                Debug.logWarning(e);
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
        if (eftAccount != null) {
            results.put("eftAccount", eftAccount);
        }

        String curContactMechId = null;

        if (creditCard != null) {
            curContactMechId = UtilFormatOut.checkNull(tryEntity ? creditCard.getString("contactMechId") : request.getParameter("contactMechId"));
        } else if (eftAccount != null) {
            curContactMechId = UtilFormatOut.checkNull(tryEntity ? eftAccount.getString("contactMechId") : request.getParameter("contactMechId"));
        }
        if (curContactMechId != null) {
            results.put("curContactMechId", curContactMechId);
        }

        results.put("tryEntity", new Boolean(tryEntity));
        
        return results;
    }
    
    public static GenericValue getPaymentSetting(GenericDelegator delegator, String webSiteId, String paymentMethodTypeId) {
        GenericValue webSitePayment = null;
        try {
            webSitePayment = delegator.findByPrimaryKeyCache("WebSitePaymentSetting", UtilMisc.toMap("webSiteId", webSiteId, "paymentMethodTypeId", paymentMethodTypeId));    
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up payment method type by website.", module);
        }
        
        if (webSitePayment == null) {
            try {
                webSitePayment = delegator.findByPrimaryKeyCache("WebSitePaymentSetting", UtilMisc.toMap("webSiteId", webSiteId, "paymentMethodTypeId", "_NA_"));
            } catch (GenericEntityException e) {
                Debug.logError(e, "Problems looking up payment method type by website.", module);
            }
        }
                
        return webSitePayment;                          
    }  
    
    public static String getPaymentPartyId(GenericDelegator delegator, String webSiteId, String paymentMethodTypeId) {
        GenericValue paymentSettings = PaymentWorker.getPaymentSetting(delegator, webSiteId, paymentMethodTypeId);
        String paymentConfig = paymentSettings != null && paymentSettings.get("paymentConfiguration") != null ? paymentSettings.getString("paymentConfiguration") : null;
        if (paymentConfig == null) paymentConfig = "payment.properties";    
        return UtilProperties.getPropertyValue(paymentConfig, "payment.general.payTo", "Company");
    }
    
    public static GenericValue getPaymentAddress(GenericDelegator delegator, String webSiteId, String paymentMethodTypeId) {
        GenericValue paymentSettings = PaymentWorker.getPaymentSetting(delegator, webSiteId, paymentMethodTypeId);
        String paymentConfig = paymentSettings != null && paymentSettings.get("paymentConfiguration") != null ? paymentSettings.getString("paymentConfiguration") : null;
        if (paymentConfig == null) paymentConfig = "payment.properties";
        String payToPartyId = UtilProperties.getPropertyValue(paymentConfig, "payment.general.payTo", "Company");      
        
        List paymentAddresses = null;        
        try {
            paymentAddresses = delegator.findByAnd("PartyContactMechPurpose", 
                UtilMisc.toMap("partyId", payToPartyId, "contactMechPurposeTypeId", "PAYMENT_LOCATION"), 
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
}
