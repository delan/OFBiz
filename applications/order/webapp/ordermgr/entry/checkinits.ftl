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
 *@author     Jean-Luc.Malet@nereide.biz (migration to uiLabelMap)
 *@version    $Rev: 3227 $
 *@since      2.2
-->

<#if requestParameters.updateParty?exists>
    <#assign updateParty = requestParameters.updateParty>
</#if>

<#if shoppingCart?exists>

<!-- Sales Order Entry -->
<#if !(updateParty?exists) | shoppingCart.getOrderType() = "SALES_ORDER">
<table border=0 align="center" cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td>
      <table width="100%" border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">
              ${uiLabelMap.OrderSalesOrder}<#if updateParty?exists>&nbsp;${uiLabelMap.OrderInProgress}</#if>
            </div>
          </td>
          <td valign="middle" align="right"> 
            <a href="/partymgr/control/findparty?externalLoginKey=${externalLoginKey}" class="submenutext">
              ${uiLabelMap.PartyFindParty}
            </a>
            <a href="javascript:document.salesentryform.submit();" class="submenutextright">
              ${uiLabelMap.CommonContinue}
            </a>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <form method="post" name="salesentryform" action="<@ofbizUrl>/initorderentry</@ofbizUrl>">
      <input type='hidden' name='finalizeMode' value='type'>
      <input type='hidden' name='orderMode' value='SALES_ORDER'>
      <table border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td >&nbsp;</td>
          <td align='right' valign='middle' nowrap><div class='tableheadtext'>${uiLabelMap.ProductProductStore}</div></td>
          <td >&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext' valign='top'>
              <select class="selectBox" name="productStoreId"<#if sessionAttributes.orderMode?exists> disabled</#if>>
                <#assign currentStore = shoppingCart.getProductStoreId()?default("NA")>
                <#list productStores as productStore>
                  <option value="${productStore.productStoreId}"<#if productStore.productStoreId == currentStore> selected</#if>>${productStore.storeName}</option>
                </#list>
              </select>
              <#if !sessionAttributes.orderMode?exists>&nbsp;&nbsp;*<font color='red'>${uiLabelMap.OrderRequiredForSO}</font><#else>${uiLabelMap.OrderCannotBeChanged}</#if>
            </div>
          </td>
        </tr>
        <tr><td colspan="4">&nbsp;</td></tr>
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap><div class='tableheadtext'>Sales Channel</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext' valign='top'>
              <select class="selectBox" name="salesChannelEnumId">
                <#assign currentChannel = shoppingCart.getChannelType()?default("")>               
                <option value="">No Channel</option>
                <#list salesChannels as salesChannel>
                  <option value="${salesChannel.enumId}" <#if (salesChannel.enumId == currentChannel)>selected</#if>>${salesChannel.description}</option>
                </#list>
              </select>
            </div>
          </td>
        </tr>
        <tr><td colspan="4">&nbsp;</td></tr>
        <#if partyId?exists>
          <#assign thisPartyId = partyId>
        <#else>
          <#assign thisPartyId = requestParameters.partyId?if_exists>
        </#if>
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap><div class='tableheadtext'>${uiLabelMap.PartyUserLoginId}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext' valign='top'>
              <input type='text' class='inputBox' name='userLoginId' value='${requestParameters.userLoginId?if_exists}'>
            </div>
          </td>
        </tr>                 
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap><div class='tableheadtext'>${uiLabelMap.PartyPartyId}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext' valign='top'>
              <input type='text' class='inputBox' name='partyId' value='${thisPartyId?if_exists}'>
              ${uiLabelMap.CommonOverridesSelection}
            </div>
          </td>
        </tr>         
      </table>
      </form>
    </td>
  </tr>
</table>
</#if>

<!-- Purchase Order Entry -->
<#if !(updateParty?exists) | shoppingCart.getOrderType() = "PURCHASE_ORDER">
<table border=0 align="center" cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td>
      <table width="100%" border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">
              ${uiLabelMap.OrderPurchaseOrder}<#if updateParty?exists>&nbsp;${uiLabelMap.OrderInProgress}</#if>
            </div>
          </td>
          <td valign="middle" align="right"> 
            <a href="/partymgr/control/findparty?externalLoginKey=${externalLoginKey}" class="submenutext">
              ${uiLabelMap.PartyFindParty}
            </a>
            <a href="javascript:document.poentryform.submit();" class="submenutextright">
              ${uiLabelMap.CommonContinue}
            </a>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <form method="post" name="poentryform" action="<@ofbizUrl>/initorderentry</@ofbizUrl>">
      <input type='hidden' name='finalizeMode' value='type'>
      <input type='hidden' name='orderMode' value='PURCHASE_ORDER'>
      <table border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <#if partyId?exists>
          <#assign thisPartyId = partyId>
        <#else>
          <#assign thisPartyId = requestParameters.partyId?if_exists>
        </#if>
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap><div class='tableheadtext'>${uiLabelMap.PartySupplier}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext' valign='top'>
              <select class="selectBox" name="supplierPartyId"<#if sessionAttributes.orderMode?default("") == "SALES_ORDER"> disabled</#if>>
                <option value="">${uiLabelMap.PartyNoSupplier}</option>
                <#list suppliers as supplier>
                  <option value="${supplier.partyId}"<#if supplier.partyId == thisPartyId> selected</#if>>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(supplier, true)}</option>
                </#list>
              </select>
            </div>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td align='right' valign='middle' nowrap><div class='tableheadtext'>${uiLabelMap.PartyUserLoginId}</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext' valign='top'>
              <input type='text' class='inputBox' name='userLoginId' value='${requestParameters.userLoginId?if_exists}'>
            </div>
          </td>
        </tr>         
      </table>
      </form>
    </td>
  </tr>
</table>
</#if>

</#if>
