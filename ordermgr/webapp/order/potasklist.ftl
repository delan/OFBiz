<#--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@author     Tristan Austin (tristana@twibble.org)
 *@version    $Revision$
-->

<#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>

<#if orderHeaderList?exists>
<br>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="50%"><div class="boxhead">Orders Found</div></td>
          <td width="50%">
            <div class="boxhead" align=right>
              <#if 0 < orderHeaderList?size>             
                <#if 0 < viewIndex>
                  <a href="<@ofbizUrl>/potasklist?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutext">Previous</a>
                <#else>
                  <span class="submenutextdisabled">Previous</span>
                </#if>
                <#if 0 < listSize>
                  <span class="submenutextinfo">${lowIndex+1} - ${highIndex} of ${listSize}</span>
                </#if>
                <#if highIndex < listSize>
                  <a href="<@ofbizUrl>/potasklist?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutextright">Next</a>
                <#else>
                  <span class="submenutextrightdisabled">Next</span>
                </#if>
              </#if>
              &nbsp;
            </div>
          </td>
        </tr>
      </table>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td width="5%" align="left"><div class="tableheadtext">Type</div></td>
          <td width="5%" align="left"><div class="tableheadtext">OrderID</div></td>
          <td width="20%" align="left"><div class="tableheadtext">Name</div></td>
          <td width="5%" align="right"><div class="tableheadtext">Total Items</div></td>
          <td width="10%" align="right"><div class="tableheadtext">Order Total</div></td>
          <td width="5%" align="left"><div class="tableheadtext">&nbsp;</div></td>
          <td width="20%" align="left"><div class="tableheadtext">Status</div></td>
          <td width="20%" align="left"><div class="tableheadtext">Order Date</div></td>
          <td width="5%" align="left"><div class="tableheadtext">PartyID</div></td>
          <td width="10%">&nbsp;</td>
        </tr>
        <tr>
          <td colspan='10'><hr class='sepbar'></td>
        </tr>
        <#if orderHeaderList?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list orderHeaderList[lowIndex..highIndex-1] as orderHeader>
            <#assign orh = Static["org.ofbiz.commonapp.order.order.OrderReadHelper"].getHelper(orderHeader)>
            <#assign statusItem = orderHeader.getRelatedOneCache("StatusItem")>
            <#assign orderType = orderHeader.getRelatedOneCache("OrderType")>
            <#assign placingParty = orh.getPlacingParty()?if_exists>
            <tr class='${rowClass}'>
              <td><div class='tabletext'>${orderType.description?default(orderType.orderTypeId?default(""))}</div></td>
              <td><a href="<@ofbizUrl>/orderview?order_id=${orderHeader.orderId}</@ofbizUrl>" class='buttontext'>${orderHeader.orderId}</a></td>
              <td>
                <div class='tabletext'>
                <#assign partyId = "_NA_">
                <#if placingParty?has_content>
                  <#assign partyId = placingParty.partyId>
                  <#if placingParty.getEntityName() == "Person">
                    <#if placingParty.lastName?exists>
                      ${placingParty.lastName}<#if placingParty.firstName?exists>, ${placingParty.firstName}</#if>
                    <#else>
                      N/A
                    </#if>
                  <#else>
                    <#if placingParty.groupName?exists>
                      ${placingParty.groupName}
                    <#else>
                      N/A
                    </#if>
                  </#if>
                <#else>
                  N/A
                </#if>
                </div>
              </td>
              <td align="right"><div class="tabletext">${orh.getTotalOrderItemsQuantity()?string.number}</div></td>
              <td align="right"><div class="tabletext">${orh.getOrderGrandTotal()?string.currency}</div></td>
              <td>&nbsp;</td>
              <td><div class="tabletext">${statusItem.description?default(statusItem.statusId?default("N/A"))}</div></td>
              <td><div class="tabletext"><nobr>${orderHeader.getString("orderDate")}</nobr></div></td>              
              <td>
                <#if partyId != "_NA_">
                  <a href="/partymgr/control/viewprofile?party_id=${partyId}${requestAttributes.externalKeyParam}" class="buttontext">${partyId}</a>
                <#else>
                  <span class='tabletext'>N/A</span>
                </#if>
              </td>
              <td align='right'>
                <a href="<@ofbizUrl>/schedulepo?order_id=${orderHeader.orderId}</@ofbizUrl>" class='buttontext'>Schedule&nbsp;Delivery</a>
              </td>
          </#list>          
          <#-- toggle the row color -->
          <#if rowClass == "viewManyTR2">
            <#assign rowClass = "viewManyTR1">
          <#else>
            <#assign rowClass = "viewManyTR2">
          </#if>        
        <#else>
          <tr>
            <td colspan='4'><div class='head3'>No orders found.</div></td>
          </tr>        
        </#if>
        <#if lookupErrorMessage?exists>
          <tr>
            <td colspan='4'><div class="head3">${lookupErrorMessage}</div></td>
          </tr>
        </#if>
      </table>
    </td>
  </tr>
</table>
        
</#if> 
<#else>
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>
</#if>
