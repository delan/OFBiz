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


import java.util.*;
import javax.servlet.*;
import javax.servlet.jsp.*;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;


/**
 * Worker methods for Payments
 *
 *@author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@version 1.0
 *@created January 22, 2002
 */
public class PaymentWorker {
    public static void getPartyPaymentMethodValueMaps(PageContext pageContext, String partyId, boolean showOld, String paymentMethodValueMapsAttr) {
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");

        try {
            List paymentMethodValueMaps = new LinkedList();
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
            pageContext.setAttribute(paymentMethodValueMapsAttr, paymentMethodValueMaps);
        } catch (GenericEntityException e) {
            Debug.logWarning(e);
        }
    }

    public static void getPaymentMethodAndRelated(PageContext pageContext, String partyId,
        String paymentMethodAttr, String creditCardAttr, String eftAccountAttr, String paymentMethodIdAttr, String curContactMechIdAttr,
        String donePageAttr, String tryEntityAttr) {

        ServletRequest request = pageContext.getRequest();
        GenericDelegator delegator = (GenericDelegator) pageContext.getRequest().getAttribute("delegator");

        boolean tryEntity = true;

        if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null)
            tryEntity = false;

        String donePage = request.getParameter("DONE_PAGE");

        if (donePage == null || donePage.length() <= 0)
            donePage = "viewprofile";
        pageContext.setAttribute(donePageAttr, donePage);

        String paymentMethodId = request.getParameter("paymentMethodId");

        if (request.getAttribute("paymentMethodId") != null)
            paymentMethodId = (String) request.getAttribute("paymentMethodId");
        if (paymentMethodId != null)
            pageContext.setAttribute(paymentMethodIdAttr, paymentMethodId);

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
            pageContext.setAttribute(paymentMethodAttr, paymentMethod);
        } else {
            tryEntity = false;
        }

        if (creditCard != null) {
            pageContext.setAttribute(creditCardAttr, creditCard);
        }
        if (eftAccount != null) {
            pageContext.setAttribute(eftAccountAttr, eftAccount);
        }

        String curContactMechId = null;

        if (creditCard != null) {
            curContactMechId = UtilFormatOut.checkNull(tryEntity ? creditCard.getString("contactMechId") : request.getParameter("contactMechId"));
        } else if (eftAccount != null) {
            curContactMechId = UtilFormatOut.checkNull(tryEntity ? eftAccount.getString("contactMechId") : request.getParameter("contactMechId"));
        }
        if (curContactMechId != null) {
            pageContext.setAttribute(curContactMechIdAttr, curContactMechId);
        }

        pageContext.setAttribute(tryEntityAttr, new Boolean(tryEntity));
    }
}
