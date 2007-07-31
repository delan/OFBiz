<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#if orderHeader?has_content>
<div class="screenlet">
    <div class="screenlet-title-bar">
        <h3>&nbsp;${uiLabelMap.OrderAddToOrder}</h3>
    </div>
    <div class="screenlet-body">
        <form method="post" action="<@ofbizUrl>appendItemToOrder?${paramString}</@ofbizUrl>" name="appendItemForm">
            <#-- TODO: Presently, this is the ofbiz way of getting the prodCatalog, which is not generic. Replace with a selecatble list defaulting to this instead -->
            <input type="hidden" name="prodCatalogId" value="${Static["org.ofbiz.product.catalog.CatalogWorker"].getCurrentCatalogId(request)}"/>
            <table class="basic-table" cellspacing="0">
                <tr>
                  <td class="label">${uiLabelMap.ProductProductId} :</td>
                  <td><input type="text" size="25" name="productId" value="${requestParameters.productId?if_exists}"/>
                      <a href="javascript:call_fieldlookup2(document.appendItemForm.productId,'LookupProduct');">
                        <img src="<@ofbizContentUrl>/images/fieldlookup.gif</@ofbizContentUrl>" width="15" height="14" border="0" alt="Click here For Field Lookup"/>
                      </a>
                  </td>
                </tr>
                <tr>
                  <td class="label">${uiLabelMap.OrderPrice} :</td>
                  <td>
                    <input type="text" size="6" name="basePrice" value="${requestParameters.price?if_exists}"/>
                    <input type="checkbox" name="overridePrice" value="Y"/>&nbsp;${uiLabelMap.OrderOverridePrice}
                  </td>
                </tr>
                <tr>
                  <td class="label">${uiLabelMap.OrderQuantity} :</td>
                  <td><input type="text" size="6" name="quantity" value="${requestParameters.quantity?default("1")}"/></td>
                </tr>
                <tr>
                  <td class="label">${uiLabelMap.OrderShipGroup} :</td>
                  <td><input type="text" size="6" name="shipGroupSeqId" value="00001"/></td>
                </tr>
                <tr>
                  <td colspan="2">&nbsp;</td>
                </tr>
                <tr>
                  <td class="label">${uiLabelMap.OrderDesiredDeliveryDate} :</td>
                  <td>
                      <input type="text" size="25" maxlength="30" name="itemDesiredDeliveryDate"/>
                      <a href="javascript:call_cal(document.quickaddform.itemDesiredDeliveryDate,'${toDayDate} 00:00:00.0');"><img src="<@ofbizContentUrl>/images/cal.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="${uiLabelMap.calendar_click_here_for_calendar}"/></a>
                  </td>
                </tr>
                <tr>
                  <td class="label">${uiLabelMap.CommonComment} :</td>
                  <td>
                      <input type="text" size="25" name="itemComment"/>
                  </td>
                </tr>
                <tr>
                  <td class="label">&nbsp;</td>
                  <td><input type="submit" value="${uiLabelMap.OrderAddToOrder}"/></td>
                </tr>
            </table>
        </form>
    </div>
</div>
</#if>
