/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.accounting.util.UtilAccounting;
import java.text.DateFormat;
import java.math.*;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import java.text.NumberFormat;

invoiceId = parameters.get("invoiceId");
invoice = delegator.findByPrimaryKey("Invoice", UtilMisc.toMap("invoiceId", invoiceId));

int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
Locale locale = context.get("locale");

ArrayList paymentsMapList = new ArrayList();  // to pass back to the screeen list of unapplied payments

// retrieve payments for the related parties which have not been (fully) applied yet
List payments = null;
GenericValue payment = null;
exprList = new ArrayList();
expr = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, invoice.getString("partyIdFrom"));
exprList.add(expr); 
expr = EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, invoice.getString("partyId"));
exprList.add(expr); 

// only payments with received and sent and not paid
exprListStatus = new ArrayList();
expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_NOT_PAID");
exprListStatus.add(expr); 
expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_RECEIVED");
exprListStatus.add(expr); 
expr = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PMNT_SENT");
exprListStatus.add(expr);
orCond = EntityCondition.makeCondition(exprListStatus, EntityOperator.OR);
exprList.add(orCond); 

topCond = EntityCondition.makeCondition(exprList, EntityOperator.AND);

payments = delegator.findList("Payment", topCond, null, UtilMisc.toList("effectiveDate"), null, false);
if (payments != null && payments.size() > 0)    {
    List paymentApplications = null;
    GenericValue paymentApplication = null;
    BigDecimal invoiceApplied = InvoiceWorker.getInvoiceAppliedBd(invoice);
    BigDecimal invoiceAmount = InvoiceWorker.getInvoiceTotalBd(invoice);
    BigDecimal invoiceToApply = InvoiceWorker.getInvoiceNotApplied(invoice); 
    Iterator p = payments.iterator();
    while(p.hasNext())    {
        payment = p.next();
        if (PaymentWorker.getPaymentNotAppliedBd(payment).signum() == 1) {
           // put in the map
           Map paymentMap = new HashMap();
           paymentMap.put("paymentId", payment.getString("paymentId"));
           paymentMap.put("effectiveDate", payment.getString("effectiveDate").substring(0,10)); // list as YYYY-MM-DD
           paymentMap.put("amount", payment.getBigDecimal("amount"));
           paymentMap.put("currencyUomId", payment.getString("currencyUomId"));
           paymentMap.put("amountApplied", PaymentWorker.getPaymentAppliedBd(payment));
           BigDecimal paymentToApply = PaymentWorker.getPaymentNotAppliedBd(payment);
           if (paymentToApply.compareTo(invoiceToApply) < 0 ) {
                paymentMap.put("amountToApply",paymentToApply);
           }
           else {
                paymentMap.put("amountToApply",invoiceToApply);
           }
           paymentsMapList.add(paymentMap);
        }
    }
}       
context.put("payments", paymentsMapList);
