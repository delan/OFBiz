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

<div id="findOrdersList" class="screenlet">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.OrderOrderReceivedOn} ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(filterDate)}</li>
      <#assign listSize = state.getSize()>
      <#if (listSize > 10)>
        <li><a href="/ordermgr/control/orderlist?viewIndex=${state.getViewIndex() + 1}&viewSize=${state.getViewSize()}&filterDate=${filterDate?if_exists}">${uiLabelMap.CommonMore}</a></li>
      </#if>
      <#if orderHeaderList?has_content> 
        <li>1-${orderHeaderList.size()} ${uiLabelMap.CommonOf} ${state.getSize()}</li>
      </#if>
    </ul>
    <br class="clear"/>
  </div>
  <div class="screenlet-body">
    <table class="basic-table hover-bar" cellspacing='0'>
      <tr class="header-row">
        <td width="20%">${uiLabelMap.OrderOrder} #</td>
        <td width="25%">${uiLabelMap.OrderOrderBillToParty}</td>
        <td width="15%">${uiLabelMap.CommonAmount}</td>
        <td width="20%">${uiLabelMap.OrderTrackingCode}</td>
        <td width="20%">${uiLabelMap.CommonStatus}</td>
      </tr>
      <#assign alt_row = false>
      <#list orderHeaderList as orderHeader>
        <#assign status = orderHeader.getRelatedOneCache("StatusItem")>
        <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
        <#assign billToParty = orh.getBillToParty()?if_exists>
        <#if billToParty?has_content>
          <#assign billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billToParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
          <#assign billTo = billToPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")/>
        </#if>
        <tr<#if alt_row> class="alternate-row"</#if>>
          <#assign alt_row = !alt_row>
          <td>
            <a href="/ordermgr/control/orderview?orderId=${orderHeader.orderId}" class="buttontext">${orderHeader.orderId}</a>
          </td>
          <td>${billTo?if_exists}</td>
          <td><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom/></td>
          <td>
            <#assign trackingCodes = orderHeader.getRelated("TrackingCodeOrder")>
            <#list trackingCodes as trackingCode>
              <#if trackingCode?has_content>
                <a href="/marketing/control/FindTrackingCodeOrders?trackingCodeId=${trackingCode.trackingCodeId}&externalLoginKey=${requestAttributes.externalLoginKey?if_exists}">${trackingCode.trackingCodeId}</a><br/>
              </#if>
            </#list>
          </td>
          <td>${orderHeader.getRelatedOneCache("StatusItem").get("description",locale)}</td>
        </tr>
      </#list>
      <#if !orderHeaderList?has_content>
        <tr><td colspan="9"><h3>${uiLabelMap.OrderNoOrderFound}</h3></td></tr>
      </#if>
    </table>
  </div>
</div>