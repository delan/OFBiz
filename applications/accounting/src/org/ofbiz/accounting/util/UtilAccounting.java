/*
 * $Id:$
 *
 *  Copyright (c) 2003-2006 The Open For Business Project - www.ofbiz.org
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
 *
 *  @author Si Chen (sichen@opensourcestrategies.com)
 *  @author Leon Torres (leon@opensourcestrategies.com)
 */

package org.ofbiz.accounting.util;

import java.util.Map;

import org.ofbiz.accounting.AccountingException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

public class UtilAccounting {

    public static String module = UtilAccounting.class.getName();

    /**
     * Get the GL Account for a product or the default account type based on input. This replaces the simple-method service
     * getProductOrgGlAccount. First it will look in ProductGlAccount using the primary keys productId and
     * productGlAccountTypeId. If none is found, it will look up GlAccountTypeDefault to find the default account for
     * organizationPartyId with type glAccountTypeId.
     *
     * @param   productId                  When searching for ProductGlAccounts, specify the productId
     * @param   productGlAccountTypeId     When searching for ProductGlAccounts, specify the productGlAccountTypeId
     * @param   glAccountTypeId            The default glAccountTypeId to look for if no ProductGlAccount is found
     * @param   organizationPartyId        The organization party of the default account
     * @return  The account ID (glAccountId) found
     * @throws  AccountingException        When the no accounts found or an entity exception occurs
     */
    public static String getProductOrgGlAccountId(String productId, String productGlAccountTypeId, 
            String glAccountTypeId, String organizationPartyId, GenericDelegator delegator) 
        throws AccountingException {

        GenericValue account = null;
        try {
            // first try to find the account in ProductGlAccount
            account = delegator.findByPrimaryKeyCache("ProductGlAccount", 
                    UtilMisc.toMap("productId", productId, "productGlAccountTypeId", productGlAccountTypeId));
        } catch (GenericEntityException e) {
            throw new AccountingException("Failed to find a ProductGLAccount for productId [" + productId + "] and productGlAccountTypeId [" + productGlAccountTypeId + "].", e);
        }

        // otherwise try the default accounts
        if (account == null) {
            try {
                account = delegator.findByPrimaryKeyCache("GlAccountTypeDefault", UtilMisc.toMap("glAccountTypeId", glAccountTypeId, "organizationPartyId", organizationPartyId));
            } catch (GenericEntityException e) {
                throw new AccountingException("Failed to find a GlAccountTypeDefault for glAccountTypeId [" + glAccountTypeId + "] and organizationPartyId [" + organizationPartyId+ "].", e);
            }
        }

        // if no results yet, serious problem
        if (account == null) {
            throw new AccountingException("Failed to find any accounts in ProductGlAccount for  productId [" + productId + "] and productGlAccountTypeId [" + productGlAccountTypeId + "] or any accounts in GlAccountTypeDefault for glAccountTypeId [" + glAccountTypeId + "] and organizationPartyId [" + organizationPartyId+ "]. Please check your data to make sure that at least a GlAccountTypeDefault is defined for this account type and organization.");
        }

        // otherwise return the glAccountId
        return account.getString("glAccountId");
    }

    /** 
     * As above, but explicitly looking for default account for given type and organization 
     * 
     * @param   glAccountTypeId         The type of account
     * @param   organizationPartyId     The organization of the account
     * @return  The default account ID (glAccountId) for this type
     * @throws  AccountingException     When the default is not configured
     */
    public static String getDefaultAccountId(String glAccountTypeId, String organizationPartyId, GenericDelegator delegator) throws AccountingException {
        return getProductOrgGlAccountId(null, null, glAccountTypeId, organizationPartyId, delegator);
    }

    /**
     * Little method to figure out the net or ending balance of a GlAccountHistory or GlAccountAndHistory value, based on what kind
     * of account (DEBIT or CREDIT) it is
     * @param account - GlAccountHistory or GlAccountAndHistory value
     * @return balance - a Double 
     */
    public static Double getNetBalance(GenericValue account, String debugModule) {
        try {
            String parentClassId = account.getRelatedOne("GlAccount").getRelatedOne("GlAccountClass").getRelatedOne("ParentGlAccountClass").getString("glAccountClassId");
            double balance = 0.0;
            if (parentClassId.equals("DEBIT")) {
                balance = account.getDouble("postedDebits").doubleValue() - account.getDouble("postedCredits").doubleValue();
            } else if (parentClassId.equals("CREDIT")) {
                balance = account.getDouble("postedCredits").doubleValue() - account.getDouble("postedDebits").doubleValue();
            }
            return new Double(balance);    
        } catch (GenericEntityException ex) {
            Debug.logError(ex.getMessage(), debugModule);
            return null;
        }
    }


    /**
     * Recurses up payment type tree via parentTypeId to see if input payment type ID is in tree.
     */
    private static boolean isPaymentTypeRecurse(GenericValue paymentType, String inputTypeId) throws GenericEntityException {

        // first check the parentTypeId against inputTypeId
        String parentTypeId = paymentType.getString("parentTypeId");
        if (parentTypeId == null) {
            return false;
        }
        if (parentTypeId.equals(inputTypeId)) {
            return true;
        }

        // otherwise, we have to go to the grandparent (recurse)
        return isPaymentTypeRecurse(paymentType.getRelatedOne("ParentPaymentType"), inputTypeId);
    }


    /**
     * Checks if a payment is of a specified PaymentType.paymentTypeId. It's better to use the
     * more specific calls like isVendorPrepay().
     */
    public static boolean isPaymentType(GenericValue payment, String inputTypeId) throws GenericEntityException {
        if (payment == null) { 
            throw new GenericEntityException("Cannot check payment type: input payment is null");
        }

        GenericValue paymentType = payment.getRelatedOneCache("PaymentType");
        if (paymentType == null) {
            throw new GenericEntityException("Cannot find PaymentType for paymentId " + payment.getString("paymentId"));
        }

        String paymentTypeId = paymentType.getString("paymentTypeId");
        if (inputTypeId.equals(paymentTypeId)) {
            return true;
        }

        // recurse up tree
        return isPaymentTypeRecurse(paymentType, inputTypeId);
    }


    public static boolean isTaxPayment(GenericValue payment) throws GenericEntityException {
        return isPaymentType(payment, "TAX_PAYMENT");
    }

    public static boolean isDisbursement(GenericValue payment) throws GenericEntityException {
        return isPaymentType(payment, "DISBURSEMENT");
    }

    public static boolean isReceipt(GenericValue payment) throws GenericEntityException {
        return isPaymentType(payment, "RECEIPT");
    }


    /**
     * Recurses up class tree via parentClassId to see if input account class ID is in tree.
     */
    private static boolean isAccountClassRecurse(GenericValue accountClass, String inputClassId) throws GenericEntityException {

        // first check parentClassId against inputClassId
        String parentClassId = accountClass.getString("parentClassId");
        if (parentClassId == null) {
            return false;
        }
        if (parentClassId.equals(inputClassId)) {
            return true;
        }

        // otherwise, we have to go to the grandparent (recurse)
        return isAccountClassRecurse(accountClass.getRelatedOne("ParentGlAccountClass"), inputClassId);
    }

    /**
     * Checks if an account is of a specified GlAccountClass.glAccountClassId. It's better to use the
     * more specific calls like isDebitAccount().
     */
    public static boolean isAccountClass(GenericValue account, String inputClassId) throws GenericEntityException {
        if (account == null) {
            throw new GenericEntityException("Cannot check account class: input account is null");
        }

        GenericValue accountClass = account.getRelatedOneCache("GlAccountClass");
        if (accountClass == null) {
            throw new GenericEntityException("Cannot find GlAccountClass for glAccountId " + account.getString("glAccountId"));
        }

        String accountClassId = accountClass.getString("glAccountClassId");
        if (inputClassId.equals(accountClassId)) {
            return true;
        }

        // recurse up the tree
        return isAccountClassRecurse(accountClass, inputClassId);

    }


    public static boolean isDebitAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "DEBIT");
    }

    public static boolean isCreditAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "CREDIT");
    }

    public static boolean isAssetAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "ASSET");
    }

    public static boolean isLiabilityAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "LIABILITY");
    }

    public static boolean isEquityAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "EQUITY");
    }

    public static boolean isIncomeAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "INCOME");
    }

    public static boolean isRevenueAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "REVENUE");
    }

    public static boolean isExpenseAccount(GenericValue account) throws GenericEntityException {
        return isAccountClass(account, "EXPENSE");
    }
}
