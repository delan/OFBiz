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
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.util.UtilAccounting;
import java.text.DateFormat;
import java.math.*;
 
int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
 
// find existing items
invoiceItemsDb = null;
List invoiceItems = new LinkedList();
BigDecimal invoiceAmount = BigDecimal.ZERO;
BigDecimal total = BigDecimal.ZERO;
BigDecimal quantity = BigDecimal.ZERO;
if (invoiceId && invoice) {    
    invoiceItemsDb = invoice.getRelated("InvoiceItem", ["invoiceItemSeqId"]);
    if (invoiceItemsDb) {
        // create totals
        invoiceItemsDb.each { item ->
            if (!item.quantity) {
               quantity = BigDecimal.ONE;
            } else {
                quantity = item.getBigDecimal("quantity");
            }
            if (item.amount) {
                total = (item.getBigDecimal("amount") * quantity).setScale(decimals, rounding);
            } else {
                total = BigDecimal.ZERO;
            }
            invoiceAmount = invoiceAmount.add(total);
            Map itemmap = new HashMap();
            itemmap.putAll(item);
            itemmap.put("total",total.toString());
            invoiceItems.add(itemmap);
        }
    }
    context.put("invoiceItems",invoiceItems);
    context.put("invoiceAmount",invoiceAmount);
}
