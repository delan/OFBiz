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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@version    $Revision: 1.12 $
 *@since      2.2
-->

<script language="JavaScript">
<!-- //
function lookupOrders(click) {
    orderIdValue = document.lookuporder.order_id.value;
    if (orderIdValue.length > 1) {
        document.lookuporder.action = "<@ofbizUrl>/orderview</@ofbizUrl>";
    } else {
        document.lookuporder.action = "<@ofbizUrl>/findorders</@ofbizUrl>";
    }

    if (click) {
        document.lookuporder.submit();
    }
    return true;
}
// -->
</script>

<#if security.hasEntityPermission("ORDERMGR", "_VIEW", session)>
<form method='post' name="lookuporder" action="<@ofbizUrl>/findorders</@ofbizUrl>" onsubmit="javascript:lookupOrders();">
<input type='hidden' name='lookupFlag' value='Y'>
<input type='hidden' name='hideFields' value='Y'>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td><div class='boxhead'>Find Order(s)</div></td>
          <td align='right'>
            <div class="tabletext">
              <#if requestParameters.hideFields?default("N") == "Y">
                <a href="<@ofbizUrl>/findorders?hideFields=N${paramList}</@ofbizUrl>" class="submenutextright">Show Lookup Fields</a>
              <#else>
                <#if orderHeaderList?exists><a href="<@ofbizUrl>/findorders?hideFields=Y${paramList}</@ofbizUrl>" class="submenutext">Hide Fields</a></#if>
                <a href="javascript:lookupOrders(true);" class="submenutext">Lookup Order(s)</a>
                <a href="/partymgr/control/findparty?externalLoginKey=${requestAttributes.externalLoginKey?if_exists}" class="submenutextright">Lookup Party</a>
              </#if>
            </div>
          </td>
        </tr>
      </table>
      <#if requestParameters.hideFields?default("N") != "Y">
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td align='center' width='100%'>
            <table border='0' cellspacing='0' cellpadding='2'>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Order ID:</div></td>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='order_id'></td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Customer PO#:</div></td>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='correspondingPoId' value='${requestParameters.correspondingPoId?if_exists}'></td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Product ID:</div></td>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='productId' value='${requestParameters.productId?if_exists}'></td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Role Type:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='roleTypeId' class='selectBox'>
                    <#if currentRole?has_content>
                    <option value="${currentRole.roleTypeId}">${currentRole.description}</option>
                    <option value="${currentRole.roleTypeId}">---</option>
                    </#if>
                    <option value="ANY">Any Role Type</option>
                    <#list roleTypes as roleType>
                      <option value="${roleType.roleTypeId}">${roleType.description}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Party ID:</div></td>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='partyId' value='${requestParameters.partyId?if_exists}'></td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>UserLogin ID:</div></td>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='userLoginId' value='${requestParameters.userLoginId?if_exists}'></td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Order Type:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='orderTypeId' class='selectBox'>
                    <#if currentType?has_content>
                    <option value="${currentType.orderTypeId}">${currentType.description}</option>
                    <option value="${currentType.orderTypeId}">---</option>
                    </#if>
                    <option value="ANY">Any Order Type</option>
                    <#list orderTypes as orderType>
                      <option value="${orderType.orderTypeId}">${orderType.description}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Billing Acct:</div>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='billingAccountId' value='${requestParameters.billingAccountId?if_exists}'></td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Created By:</div>
                <td width='5%'>&nbsp;</td>
                <td><input type='text' class='inputBox' name='createdBy' value='${requestParameters.createdBy?if_exists}'></td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Store:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='productStoreId' class='selectBox'>
                    <#if currentProductStore?has_content>
                    <option value="${currentProductStore.productStoreId}">${currentProductStore.storeName}</option>
                    <option value="${currentProductStore.productStoreId}">---</option>
                    </#if>
                    <option value="ANY">Any Store</option>
                    <#list productStores as store>
                      <option value="${store.productStoreId}">${store.storeName}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Web Site:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='webSiteId' class='selectBox'>
                    <#if currentWebSite?has_content>
                    <option value="${currentWebSite.webSiteId}">${currentWebSite.siteName}</option>
                    <option value="${currentWebSite.webSiteId}">---</option>
                    </#if>
                    <option value="ANY">Any Web Site</option>
                    <#list webSites as webSite>
                      <option value="${webSite.webSiteId}">${webSite.siteName}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right'><div class='tableheadtext'>Status:</div></td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <select name='orderStatusId' class='selectBox'>
                    <#if currentStatus?has_content>
                    <option value="${currentStatus.statusId}">${currentStatus.description}</option>
                    <option value="${currentStatus.statusId}">---</option>
                    </#if>
                    <option value="ANY">Any Order Status</option>
                    <#list orderStatuses as orderStatus>
                      <option value="${orderStatus.statusId}">${orderStatus.description}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td width='25%' align='right'>
                  <div class='tableheadtext'>Date Filter:</div>
                </td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <table border='0' cellspacing='0' cellpadding='0'>
                    <tr>
                      <td nowrap>
                        <input type='text' size='25' class='inputBox' name='minDate' value='${requestParameters.minDate?if_exists}'>
                        <a href="javascript:call_cal(document.lookuporder.minDate, '${fromDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                        <span class='tabletext'>From</span>
                      </td>
                    </tr>
                    <tr>
                      <td nowrap>
                        <input type='text' size='25' class='inputBox' name='maxDate' value='${requestParameters.maxDate?if_exists}'>
                        <a href="javascript:call_cal(document.lookuporder.maxDate, '${thruDateStr}');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
                        <span class='tabletext'>Thru</span>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
              <tr><td colspan="3"><hr class="sepbar"></td></tr>
              <tr>
                <td width='25%' align='right'>&nbsp;</td>
                <td width='5%'>&nbsp;</td>
                <td>
                  <div class="tabletext">
                    <input type='checkbox' name='showAll' value='Y' onclick="javascript:lookupOrders(true);">&nbsp;Show All Records
                  </div>
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
      </#if>
    </td>
  </tr>
</table>
<input type="image" src="/images/spacer.gif" onClick="javascript:lookupOrders(true);">
</form>
<#if requestParameters.hideFields?default("N") != "Y">
<script language="JavaScript">
<!--//
document.lookuporder.order_id.focus();
//-->
</script>
</#if>

<#if orderHeaderList?exists>
<br>
<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td width="50%"><div class="boxhead">Order(s) Found</div></td>
          <td width="50%">
            <div class="boxhead" align=right>
              <#if 0 < orderHeaderList?size>
                <#if (viewIndex > 1)>
                  <a href="<@ofbizUrl>/findorders?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutext">Previous</a>
                <#else>
                  <span class="submenutextdisabled">Previous</span>
                </#if>
                <#if (orderHeaderListSize > 0)>
                  <span class="submenutextinfo">${lowIndex} - ${highIndex} of ${orderHeaderListSize}</span>
                </#if>
                <#if (orderHeaderListSize > highIndex)>
                  <a href="<@ofbizUrl>/findorders?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutextright">Next</a>
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
          <td width="5%" align="right"><div class="tableheadtext">Items Ordered</div></td>
          <td width="5%" align="right"><div class="tableheadtext">Items Returned</div></td>
          <td width="10%" align="right"><div class="tableheadtext">Remaining Sub Total</div></td>
          <td width="10%" align="right"><div class="tableheadtext">Order Total</div></td>
          <td width="5%" align="left"><div class="tableheadtext">&nbsp;</div></td>
          <td width="20%" align="left"><div class="tableheadtext">Status</div></td>
          <td width="20%" align="left"><div class="tableheadtext">Order Date</div></td>
          <td width="5%" align="left"><div class="tableheadtext">PartyID</div></td>
          <td width="10%">&nbsp;</td>
        </tr>
        <tr>
          <td colspan='11'><hr class='sepbar'></td>
        </tr>
        <#if orderHeaderList?has_content>
          <#assign rowClass = "viewManyTR2">
          <#list orderHeaderList as orderHeader>
            <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
            <#assign statusItem = orderHeader.getRelatedOneCache("StatusItem")>
            <#assign orderType = orderHeader.getRelatedOneCache("OrderType")>
            <#if orderType.orderTypeId == "PURCHASE_ORDER">
              <#assign displayParty = orh.getSupplierAgent()?if_exists>
            <#else>
              <#assign displayParty = orh.getPlacingParty()?if_exists>
            </#if>
            <#assign partyId = displayParty.partyId?default("_NA_")>
            <tr class='${rowClass}'>
              <td><div class='tabletext'>${orderType.description?default(orderType.orderTypeId?default(""))}</div></td>
              <td><a href="<@ofbizUrl>/orderview?order_id=${orderHeader.orderId}</@ofbizUrl>" class='buttontext'>${orderHeader.orderId}</a></td>
              <td>
                <div class="tabletext">
                  <#if displayParty?has_content>
                    ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(displayParty, true)}
                  <#else>
                    N/A
                  </#if>
                </div>
                <#--
                <div class='tabletext'>

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
                -->
              </td>
              <td align="right"><div class="tabletext">${orh.getTotalOrderItemsQuantity()?string.number}</div></td>
              <td align="right"><div class="tabletext">${orh.getOrderReturnedQuantity()?string.number}</div></td>
              <td align="right"><div class="tabletext"><@ofbizCurrency amount=orderHeader.remainingSubTotal isoCode=orh.getCurrency()/></div></td>
              <td align="right"><div class="tabletext"><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orh.getCurrency()/></div></td>

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
                <a href="<@ofbizUrl>/orderview?order_id=${orderHeader.orderId}</@ofbizUrl>" class='buttontext'>View</a>
              </td>
            </tr>
            <#-- toggle the row color -->
            <#if rowClass == "viewManyTR2">
              <#assign rowClass = "viewManyTR1">
            <#else>
              <#assign rowClass = "viewManyTR2">
            </#if>
          </#list>
        <#else>
          <tr>
            <td colspan='4'><div class='head3'>No order(s) found.</div></td>
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
