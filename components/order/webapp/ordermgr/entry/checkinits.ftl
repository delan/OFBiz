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
 *@version    $Revision: 1.5 $
 *@since      2.2
-->

<script language="JavaScript">
<!--
var defaultText = "!";
var defaultValue = "!";
function setStore(disable) {
    var selectBox = document.entryform.productStoreId;
    if (disable) {
        if (defaultText == "!") {
            defaultText = selectBox.options[selectBox.selectedIndex].text;
        }
        if (defaultValue == "!") {
            defaultValue = selectBox.options[selectBox.selectedIndex].value;
        }
        selectBox.options[selectBox.selectedIndex].text = "Not Used For Purchase Orders";
        selectBox.options[selectBox.selectedIndex].value = "";      
    } else {
        if (defaultText != "!") {
            selectBox.options[selectBox.selectedIndex].text = defaultText;
        }
        if (defaultValue != "!") {
            selectBox.options[selectBox.selectedIndex].value = defaultValue;
        }
    }
    selectBox.disabled = disable;
}
//-->
</script>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Order Entry</div>
          </td>
          <td valign="middle" align="right"> 
            <a href="/partymgr/control/findparty?externalLoginKey=${requestAttributes.externalLoginKey}" class="submenutext">Find Party</a><a href="javascript:document.entryform.submit();" class="submenutextright">Continue</a>
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
          <td wdith='6%' align='right' valign='middle' nowrap><div class='tableheadtext'>Order Type:</div></td>
          <td width='6%'>&nbsp;</td>
          <td width='74%' valign='middle'>
            <div class='tabletext' valign='top'>
              <input type='radio' name='orderMode' onChange="javascript:setStore(false)" value='SALES_ORDER'<#if sessionAttributes.orderMode?default("") == "SALES_ORDER"> checked</#if><#if sessionAttributes.orderMode?exists> disabled</#if>>&nbsp;Sales Order&nbsp;<input type='radio' name='orderMode' onChange="javascript:setStore(true)" value='PURCHASE_ORDER'<#if sessionAttributes.orderMode?default("") == "PURCHASE_ORDER"> checked</#if><#if sessionAttributes.orderMode?exists> disabled</#if>>&nbsp;Purchase Order&nbsp;
              <#if !sessionAttributes.orderMode?exists>*<font color='red'>required</font><#else>(cannot be changed without clearing order.)</#if>
            </div>
          </td>
        </tr>
        <tr><td colspan="4">&nbsp;</td></tr>
        <tr>
          <td width='14%'>&nbsp;</td>
          <td wdith='6%' align='right' valign='middle' nowrap><div class='tableheadtext'>Product Store:</div></td>
          <td width='6%'>&nbsp;</td>
          <td width='74%' valign='middle'>
            <div class='tabletext' valign='top'>
              <select class="selectBox" name="productStoreId"<#if sessionAttributes.orderMode?exists> disabled</#if>>
                <#assign currentStore = shoppingCart.getProductStoreId()?default("NA")>
                <#list productStores as productStore>
                  <option value="${productStore.productStoreId}"<#if productStore.productStoreId == currentStore> selected</#if>>${productStore.storeName}</option>
                </#list>
              </select>
              <#if !sessionAttributes.orderMode?exists>*<font color='red'>required</font><#else>(cannot be changed without clearing order.)</#if>
            </div>
          </td>
        <tr><td colspan="4">&nbsp;</td></tr>
        <tr>
          <td width='14%'>&nbsp;</td>
          <td wdith='6%' align='right' valign='middle' nowrap><div class='tableheadtext'>UserLogin ID:</div></td>
          <td width='6%'>&nbsp;</td>
          <td width='74%' valign='middle'>
            <div class='tabletext' valign='top'>
              <input type='text' class='inputBox' name='userLoginId' value='${requestParameters.userLoginId?if_exists}'>
            </div>
          </td>
        </tr>                 
        <tr>
          <td width='14%'>&nbsp;</td>
          <td wdith='6%' align='right' valign='middle' nowrap><div class='tableheadtext'>Party ID:</div></td>
          <td width='6%'>&nbsp;</td>
          <td width='74%' valign='middle'>
            <div class='tabletext' valign='top'>
              <#if partyId?exists>
                <#assign thisPartyId = partyId>
              <#else>
                <#assign thisPartyId = requestParameters.partyId?if_exists>
              </#if>              
              <input type='text' class='inputBox' name='partyId' value='${thisPartyId?if_exists}'>
            </div>
          </td>
        </tr>         
      </table>
      </form>
    </td>
  </tr>
</table>
