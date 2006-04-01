/*
 * $Id: $
 *
 * Copyright 2001-2006 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.ofbiz.accounting.finaccount;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * A package of methods for improving efficiency of financial accounts services
 * @author sichen
 *
 */
public class FinAccountHelper {
	
     public static final String module = FinAccountHelper.class.getName();
     /**
      * A word on precision: since we're just adding and subtracting, the interim figures should have one more decimal place of precision than the final numbers.
      */
     public static int decimals = UtilNumber.getBigDecimalScale("finaccount.decimals");
     public static int rounding = UtilNumber.getBigDecimalScale("finaccount.rounding");
     public static final BigDecimal ZERO = (new BigDecimal("0.0")).setScale(decimals, rounding);
     
	 // pool of available characters for account codes, here numbers plus uppercase characters
	 static char[] char_pool = new char[10+26];
     static {
    	 int j = 0;
         for (int i = "0".charAt(0); i <= "9".charAt(0); i++) {
    	     char_pool[j++] = (char) i;
         } 
         for (int i = "A".charAt(0); i <= "Z".charAt(0); i++) {
             char_pool[j++] = (char) i;
         }
     }
     
     /**
      * Returns a unique randomly generated account code for FinAccount.finAccountCode composed of uppercase letters and numbers
      * @param codeLength length of code in number of characters
      * @param delegator
      * @return
      * @throws GenericEntityException
      */
     public static String getNewFinAccountCode(int codeLength, GenericDelegator delegator) throws GenericEntityException {

         // keep generating new 12-digit account codes until a unique one is found
         Random r = new Random();
         boolean foundUniqueNewCode = false;
         StringBuffer newAccountCode = null;
            
         while (!foundUniqueNewCode) {
            newAccountCode = new StringBuffer(codeLength);
            for (int i = 0; i < codeLength; i++) {
                newAccountCode.append(char_pool[(int) r.nextInt(char_pool.length)]);
            }

     	    List existingAccountsWithCode = delegator.findByAnd("FinAccount", UtilMisc.toMap("finAccountCode", newAccountCode.toString()));
            if (existingAccountsWithCode.size() == 0) {
    	        foundUniqueNewCode = true;
            }
    	 }
    	    
    	 return newAccountCode.toString();
     }
     
     /**
      * Gets the first (and should be only) FinAccount based on finAccountCode, which will be cleaned up to be only uppercase and alphanumeric
      * @param finAccountCode
      * @param delegator
      * @return
      * @throws GenericEntityException
      */
     public static GenericValue getFinAccountFromCode(String finAccountCode, GenericDelegator delegator) throws GenericEntityException {
         // regex magic to turn all letters in code to uppercase and then remove all non-alphanumeric letters
         if (finAccountCode == null) {
             return null;
         }
         Pattern filterRegex = Pattern.compile("[^0-9A-Z]");
         finAccountCode = finAccountCode.toUpperCase().replaceAll(filterRegex.pattern(), "");
         
         // now look for the account
         List accounts = delegator.findByAnd("FinAccount", UtilMisc.toMap("finAccountCode", finAccountCode));
         accounts = EntityUtil.filterByDate(accounts);
         if ((accounts == null) || (accounts.size() == 0)) {
             Debug.logWarning("No fin account found for account code ["  + finAccountCode + "]", module);
             return null;
         } else if (accounts.size() > 1) {
             // This should never happen
             Debug.logError("Multiple fin accounts found for code [" + finAccountCode + "]", module);
             return null;
         } else {
             return (GenericValue) accounts.get(0);
         }
     }
     
 
     /**
      * Sum of all DEPOSIT and ADJUSTMENT transactions minus all WITHDRAWAL transactions whose transactionDate is before asOfDateTime
      * @param finAccountId
      * @param currencyUomId
      * @param asOfDateTime
      * @param delegator
      * @return
      * @throws GenericEntityException
      */
     public static BigDecimal getBalance(String finAccountId, String currencyUomId, Timestamp asOfDateTime, GenericDelegator delegator) throws GenericEntityException {
         BigDecimal incrementTotal = ZERO;  // total amount of transactions which increase balance
         BigDecimal decrementTotal = ZERO;  // decrease balance
         
         // find the sum of all transactions which increase the value
         EntityConditionList incrementConditions = new EntityConditionList(UtilMisc.toList(
                 new EntityExpr("finAccountId", EntityOperator.EQUALS, finAccountId),
                 new EntityExpr("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, asOfDateTime),
                 new EntityExpr("currencyUomId", EntityOperator.EQUALS, currencyUomId),
                 new EntityConditionList(UtilMisc.toList(
                         new EntityExpr("finAccountTransTypeId", EntityOperator.EQUALS, "DEPOSIT"),
                         new EntityExpr("finAccountTransTypeId", EntityOperator.EQUALS, "ADJUSTMENT")),
                     EntityOperator.OR)),
                 EntityOperator.AND);
         List transSums = delegator.findByCondition("FinAccountTransSum", incrementConditions, UtilMisc.toList("amount"), null);
         incrementTotal = UtilAccounting.addFirstEntryAmount(incrementTotal, transSums, "amount", (decimals+1), rounding);
         
         // now find sum of all transactions with decrease the value
         EntityConditionList decrementConditions = new EntityConditionList(UtilMisc.toList(
                 new EntityExpr("finAccountId", EntityOperator.EQUALS, finAccountId),
                 new EntityExpr("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, asOfDateTime),
                 new EntityExpr("currencyUomId", EntityOperator.EQUALS, currencyUomId),
                 new EntityExpr("finAccountTransTypeId", EntityOperator.EQUALS, "WITHDRAWAL")),
             EntityOperator.AND);
         transSums = delegator.findByCondition("FinAccountTransSum", decrementConditions, UtilMisc.toList("amount"), null);
         decrementTotal = UtilAccounting.addFirstEntryAmount(decrementTotal, transSums, "amount", (decimals+1), rounding);
         
         // the net balance is just the incrementTotal minus the decrementTotal
         BigDecimal netBalance = incrementTotal.subtract(decrementTotal).setScale(decimals, rounding);
         
         return netBalance;
     }

     /**
      * Same as above for the current instant
      * @param finAccountId
      * @param currencyUomId
      * @param delegator
      * @return
      * @throws GenericEntityException
      */
     public static BigDecimal getBalance(String finAccountId, String currencyUomId, GenericDelegator delegator) throws GenericEntityException {
         return getBalance(finAccountId, currencyUomId, UtilDateTime.nowTimestamp(), delegator);
     }
     
     /**
      * Returns the net balance (see above) minus the sum of all authorization amounts which are not expired and were authorized by the as of date
      * @param finAccountId
      * @param currencyUomId
      * @param asOfDateTime
      * @param delegator
      * @return
      * @throws GenericEntityException
      */
     public static BigDecimal getAvailableBalance(String finAccountId, String currencyUomId, Timestamp asOfDateTime, GenericDelegator delegator) throws GenericEntityException {
         BigDecimal netBalance = getBalance(finAccountId, currencyUomId, asOfDateTime, delegator);
         
         // find sum of all authorizations which are not expired and which were authorized before as of time
         EntityConditionList authorizationConditions = new EntityConditionList(UtilMisc.toList(
                 new EntityExpr("finAccountId", EntityOperator.EQUALS, finAccountId),
                 new EntityExpr("authorizationDate", EntityOperator.LESS_THAN_EQUAL_TO, asOfDateTime),
                 new EntityExpr("currencyUomId", EntityOperator.EQUALS, currencyUomId),
                 EntityUtil.getFilterByDateExpr(asOfDateTime)),
             EntityOperator.AND);
         
         List authSums = delegator.findByCondition("FinAccountAuthSum", authorizationConditions, UtilMisc.toList("amount"), null);
         
         BigDecimal authorizationsTotal = UtilAccounting.addFirstEntryAmount(ZERO, authSums, "amount", (decimals+1), rounding);
         
         // the total available balance is transactions total minus authorizations total
         BigDecimal netAvailableBalance = netBalance.subtract(authorizationsTotal).setScale(decimals, rounding);
         return netAvailableBalance;
     }

     /**
      * Same as above for the current instant
      * @param finAccountId
      * @param currencyUomId
      * @param delegator
      * @return
      * @throws GenericEntityException
      */
    public static BigDecimal getAvailableBalance(String finAccountId, String currencyUomId, GenericDelegator delegator) throws GenericEntityException {
        return getAvailableBalance(finAccountId, currencyUomId, UtilDateTime.nowTimestamp(), delegator);
    }
}
