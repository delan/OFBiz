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

<#assign uiLabelMap = requestAttributes.uiLabelMap>


<script language="JavaScript">
<!--
var defaultStoreText = "!";
var defaultStoreValue = "!";
var defaultSuppText = "!";
var defaultSuppValue = "!";

function setOrderType(po) {
    var storeBox = document.entryform.productStoreId;
    var suppBox = document.entryform.supplierPartyId;
    if (po) {
        if (defaultStoreText == "!") {
            defaultStoreText = storeBox.options[storeBox.selectedIndex].text;
        }
        if (defaultStoreValue == "!") {
            defaultStoreValue = storeBox.options[storeBox.selectedIndex].value;
        }
        storeBox.options[storeBox.selectedIndex].text = ${uiLabelMap.OrderNotUsedForPurchase};
        storeBox.options[storeBox.selectedIndex].value = "";

        if (defaultSuppText != "!") {
            suppBox.options[suppBox.selectedIndex].text = defaultSuppText;
        }
        if (defaultSuppValue != "!") {
            suppBox.options[suppBox.selectedIndex].value = defaultSuppValue;
        }
    } else {
        if (defaultStoreText != "!") {
            storeBox.options[storeBox.selectedIndex].text = defaultStoreText;
        }
        if (defaultStoreValue != "!") {
            storeBox.options[storeBox.selectedIndex].value = defaultStoreValue;
        }

        if (defaultSuppText == "!") {
            defaultSuppText = suppBox.options[suppBox.selectedIndex].text;
        }
        if (defaultSuppValue == "!") {
            defaultSuppValue = suppBox.options[suppBox.selectedIndex].value;
        }
        suppBox.options[suppBox.selectedIndex].text = ${uiLabelMap.OrderNotUsedForSales};
        suppBox.options[suppBox.selectedIndex].value = "";
    }
    storeBox.disabled = po;
    suppBox.disabled = !po;
}

//-->
</script>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">${uiLabelMap.OrderOrderEntry}</div>
          </td>
          <td valign="middle" align="right"> 
            <a href="/partymgr/control/findparty?externalLoginKey=${requestAttributes.externalLoginKey}" class="submenutext">${uiLabelMap.PartyFindParty}</a><a href="javascript:document.entryform.submit();" class="submenutextright">${uiLabelMap.CommonContinue}</a>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <form method="post" name="entryform" action="<@ofbizUrl>/orderentry</@ofbizUrl>">
      <input type='hidden' name='finalizeMode' value='type'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td width='14%'>&nbsp;</td>
          <td wdith='6%' align='right' valign='middle' nowrap><div class='tableheadtext'>${uiLabelMap.OrderOrderType}</div></td>
          <td width='6%'>&nbsp;</td>
          <td width='74%' valign='middle'>
            <div class='tabletext' valign='top'>
              <input type='radio' name='orderMode' onChange="javascript:setOrderType(false)" value='SALES_ORDER'<#if sessionAttributes.orderMode?default("") == "SALES_ORDER"> checked</#if><#if sessionAttributes.orderMode?exists> disabled</#if>>${uiLabelMap.OrderSalesOrder}
              <input type='radio' name='orderMode' onChange="javascript:setOrderType(true)" value='PURCHASE_ORDER'<#if sessionAttributes.orderMode?default("") == "PURCHASE_ORDER"> checked</#if><#if sessionAttributes.orderMode?exists> disabled</#if>>${uiLabelMap.OrderPurchaseOrder}&nbsp;&nbsp;
              <#if !sessionAttributes.orderMode?exists>*<font color='red'>${uiLabelMap.CommonRequired}</font><#else>${uiLabelMap.OrderCannotBeChanged}</#if>
            </div>
          </td>
        </tr>
        <tr><td colspan="4">&nbsp;</td></tr>
        <tr>
          <td width='14%'>&nbsp;</td>
          <td wdith='6%' align='right' valign='middle' nowrap><div class='tableheadtext'>${uiLabelMap.ProductProductStore}</div></td>
          <td width='6%'>&nbsp;</td>
          <td width='74%' valign='middle'>
            <div class='tabletext' valign='top'>
              <select class="selectBox" name="productStoreId"<#if sessionAttributes.orderMode?exists>${uiLabelMap.CommonDisabled}</#if>>
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
        <tr><td colspan="4">&nbsp;</td></tr>
        <#if partyId?exists>
          <#assign thisPartyId = partyId>
        <#else>
          <#assign thisPartyId = requestParameters.partyId?if_exists>
        </#if>
        <tr>
          <td width='14%'>&nbsp;</td>
          <td wdith='6%' align='right' valign='middle' nowrap><div class='tableheadtext'>${uiLabelMap.PartySupplier}</div></td>
          <td width='6%'>&nbsp;</td>
          <td width='74%' valign='middle'>
            <div class='tabletext' valign='top'>
              <select class="selectBox" name="supplierPartyId"<#if sessionAttributes.orderMode?default("") == "SALES_ORDER">${uiLabelMap.CommonDisabled}</#if>>
                <option value="">${uiLabelMap.PartyNoSupplier}</option>
                <#list suppliers as supplier>
                  <option value="${supplier.partyId}"<#if supplier.partyId == thisPartyId>${uiLabelMap.CommonSelected}</#if>>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(supplier, true)}</option>
                </#list>
              </select>
            </div>
          </td>
        </tr>
        <tr>
          <td width='14%'>&nbsp;</td>
          <td wdith='6%' align='right' valign='middle' nowrap><div class='tableheadtext'>${uiLabelMap.PartyUserLoginId}</div></td>
          <td width='6%'>&nbsp;</td>
          <td width='74%' valign='middle'>
            <div class='tabletext' valign='top'>
              <input type='text' class='inputBox' name='userLoginId' value='${requestParameters.userLoginId?if_exists}'>
            </div>
          </td>
        </tr>                 
        <tr>
          <td width='14%'>&nbsp;</td>
          <td wdith='6%' align='right' valign='middle' nowrap><div class='tableheadtext'>${uiLabelMap.PartyPartyId}</div></td>
          <td width='6%'>&nbsp;</td>
          <td width='74%' valign='middle'>
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
