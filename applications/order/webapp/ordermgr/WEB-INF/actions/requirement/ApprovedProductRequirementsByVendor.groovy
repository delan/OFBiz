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

// Gets an entity list iterator of product requirements by vendor.
// This report requires a two-level query:  one subquery to merge the
// requirements with the same productId (grouped by partyId), another
// to count the products required by the partyId.  This is modeled with two
// view entitities chained together, RequirementByPartyCount and
// ProductRequirementCount (see order entitymodel_view.xml).

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

fields = ["partyId", "productId"] as Set;
options = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
orderBy = ["partyId"];
conditions = EntityCondition.makeCondition([
            EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "SUPPLIER"),
            EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "REQ_APPROVED"),
            EntityCondition.makeCondition("requirementTypeId", EntityOperator.EQUALS, "PRODUCT_REQUIREMENT"),
            EntityUtil.getFilterByDateExpr()
            ], EntityOperator.AND);
requirements = delegator.find("RequirementPartyProductCount", conditions, null, fields, orderBy, options);
context.requirements = requirements;
