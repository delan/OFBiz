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

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import java.text.SimpleDateFormat;

condList = [];
condList.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "ORDER_APPROVED"));
condList.add(EntityCondition.makeCondition("orderTypeId", EntityOperator.EQUALS, "SALES_ORDER"));
condList.add(EntityCondition.makeCondition("pickSheetPrintedDate", EntityOperator.NOT_EQUAL, null));
cond = EntityCondition.makeCondition(condList, EntityOperator.AND);
orderHeaders = delegator.findList("OrderHeader", cond, null, null, null, false);
orders = [];
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'/'K:mm a");
orderHeaders.each { orderHeader ->
    itemIssuanceList = delegator.findByAnd("ItemIssuance", [orderId : orderHeader.orderId], null, false);
    if (itemIssuanceList) {
        orders.add([orderId : orderHeader.orderId, pickSheetPrintedDate : dateFormat.format(orderHeader.pickSheetPrintedDate), isVerified : "Y"]);
    } else {
        orders.add([orderId : orderHeader.orderId, pickSheetPrintedDate : dateFormat.format(orderHeader.pickSheetPrintedDate), isVerified : "N"]);
    }
}
context.orders = orders;
