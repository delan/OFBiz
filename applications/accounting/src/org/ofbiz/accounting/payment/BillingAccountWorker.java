/*
 * $Id$
 *
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
import java.util.TreeMap;

import javolution.util.FastList;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * Worker methods for BillingAccounts
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Rev$
 * @since      2.1
 */
public class BillingAccountWorker {
    
    public static final String module = BillingAccountWorker.class.getName();

    public static List makePartyBillingAccountList(GenericValue userLogin, String currencyUomId, GenericDelegator delegator, LocalDispatcher dispatcher) throws GeneralException {
        List billingAccountList = FastList.newInstance();
        String partyId = userLogin.getString("partyId");

        Map agentResult = dispatcher.runSync("getRelatedParties", UtilMisc.toMap("userLogin", userLogin, "partyIdFrom", partyId, 
                "roleTypeIdFrom", "AGENT", "roleTypeIdTo", "CUSTOMER", "partyRelationshipTypeId", "AGENT", "includeFromToSwitched", "Y"));
        if (ServiceUtil.isError(agentResult)) {
            throw new GeneralException("Error while finding party BillingAccounts when getting Customers that this party is an agent of: " + ServiceUtil.getErrorMessage(agentResult));
        }
        List relatedPartyIdList = (List) agentResult.get("relatedPartyIdList");

        EntityCondition barFindCond = new EntityConditionList(UtilMisc.toList(
                new EntityExpr("partyId", EntityOperator.IN, relatedPartyIdList),
                new EntityExpr("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER")), EntityOperator.AND);
        List billingAccountRoleList = delegator.findByCondition("BillingAccountRole", barFindCond, null, null);
        billingAccountRoleList = EntityUtil.filterByDate(billingAccountRoleList);

        if (billingAccountRoleList != null && billingAccountRoleList.size() > 0) {
            double totalAvailable = 0.0;
            TreeMap sortedAccounts = new TreeMap();
            Iterator billingAcctIter = billingAccountRoleList.iterator();
            while (billingAcctIter.hasNext()) {
                GenericValue billingAccountRole = (GenericValue) billingAcctIter.next();        
                GenericValue billingAccountVO = billingAccountRole.getRelatedOne("BillingAccount");
                if (currencyUomId.equals(billingAccountVO.getString("accountCurrencyUomId"))) {
                    double accountBalance = BillingAccountWorker.getBillingAccountBalance(billingAccountVO);
                
                    Map billingAccount = new HashMap(billingAccountVO);
                    double accountLimit = 0.0;
                    if (billingAccountVO.getDouble("accountLimit") != null) {
                        accountLimit = billingAccountVO.getDouble("accountLimit").doubleValue();
                    }
                
                    billingAccount.put("accountBalance", new Double(accountBalance)); 
                    double accountAvailable = accountLimit - accountBalance;
                    totalAvailable += accountAvailable;    
                    sortedAccounts.put(new Double(accountAvailable), billingAccount);
                }
            }
            
            billingAccountList.addAll(sortedAccounts.values());
        }
        return billingAccountList;
    }
    
    public static double getBillingAccountBalance(GenericValue billingAccount) throws GenericEntityException {
        return getBillingAccountBalance(billingAccount.getDelegator(), billingAccount.getString("billingAccountId"));
    }
        
    public static double getBillingAccountBalance(GenericDelegator delegator, String billingAccountId) throws GenericEntityException {
        double balance = 0.00;
        
        // first get all the pending orders (not cancelled, rejected or completed)
        List orderHeaders = null;
        List exprs1 = new LinkedList();
        exprs1.add(new EntityExpr("billingAccountId", EntityOperator.EQUALS, billingAccountId));
        exprs1.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_REJECTED"));
        exprs1.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_CANCELLED"));
        exprs1.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "ORDER_COMPLETED"));

        orderHeaders = delegator.findByAnd("OrderHeader", exprs1);

        if (orderHeaders != null) {
            Iterator ohi = orderHeaders.iterator();
            while (ohi.hasNext()) {
                GenericValue orderHeader = (GenericValue) ohi.next();
                OrderReadHelper orh = new OrderReadHelper(orderHeader);
                balance += orh.getOrderGrandTotal();            
            }
        }
        
        // next get all the un-paid invoices (this will include all completed orders)
        List invoices = null;
        List exprs2 = new LinkedList();
        exprs2.add(new EntityExpr("billingAccountId", EntityOperator.EQUALS, billingAccountId));       
        exprs2.add(new EntityExpr("statusId", EntityOperator.NOT_EQUAL, "INVOICE_CANCELLED"));

        invoices = delegator.findByAnd("Invoice", exprs2);

        if (invoices != null) {
            Iterator ii = invoices.iterator();
            while (ii.hasNext()) {
                GenericValue invoice = (GenericValue) ii.next();
                balance += InvoiceWorker.getInvoiceTotal(invoice);              
            }
        }
        
        // finally apply any payments to the balance
        List credits = null;
        List exprs3 = new LinkedList();
        exprs3.add(new EntityExpr("billingAccountId", EntityOperator.EQUALS, billingAccountId));       

        credits = delegator.findByAnd("PaymentApplication", exprs3);

        if (credits != null) {
            Iterator ci = credits.iterator();
            while (ci.hasNext()) {
                GenericValue credit = (GenericValue) ci.next();
                Double amount = credit.getDouble("amountApplied");
                if (amount != null) {
                    balance -= amount.doubleValue();
                }                
            }
        }
        
        return balance;
    }   
    
    public static Map calcBillingAccountBalance(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String billingAccountId = (String) context.get("billingAccountId");
        GenericValue billingAccount = null;
        Double accountBalance = null;
        try {
            billingAccount = delegator.findByPrimaryKey("BillingAccount", UtilMisc.toMap("billingAccountId", billingAccountId));
            accountBalance = new Double(getBillingAccountBalance(delegator, billingAccountId));
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError("Error getting billing account or calculating balance for billing account #" + billingAccountId);
        }
        
        if (billingAccount == null) {
            return ServiceUtil.returnError("Unable to locate billing account #" + billingAccountId);
        }
        
        Map result = ServiceUtil.returnSuccess();
        result.put("accountBalance", accountBalance);
        result.put("billingAccount", billingAccount);
        return result;  
    }
}
