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

import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

ppCond = 
    EntityCondition.makeCondition([
                                   EntityCondition.makeCondition([
                                                                  EntityCondition.makeCondition("parentPortalPageId", EntityOperator.EQUALS, parameters.parentPortalPageId),
                                                                  EntityCondition.makeCondition("portalPageId", EntityOperator.EQUALS, parameters.parentPortalPageId),
                                                                  EntityCondition.makeCondition("originalPortalPageId", EntityOperator.EQUALS, parameters.parentPortalPageId)
                                                                  ],EntityOperator.OR),
                                   EntityCondition.makeCondition([
                                                                  EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.EQUALS, parameters.userLogin.userLoginId),
                                                                  EntityCondition.makeCondition("ownerUserLoginId", EntityOperator.EQUALS, "_NA_")
                                                                  ],EntityOperator.OR),
                                   ],EntityOperator.AND);
sortField = parameters.sortField;
if (UtilValidate.isEmpty(sortField)) {
	sortField = "portalPageName";
}
orderBy = [sortField];
portalPages = delegator.findList("PortalPage", ppCond, null, orderBy, null, false);
// remove overridden system pages
portalPages.each { portalPage ->
	if (portalPage.ownerUserLoginId.equals("_NA_")) {
		userPortalPages = delegator.findByAnd("PortalPage", [originalPortalPageId : portalPage.portalPageId, ownerUserLoginId : parameters.userLogin.userLoginId]);
		if (userPortalPages) {
			portalPages.remove(portalPage);
		}
	}
}

context.portalPages = portalPages;

