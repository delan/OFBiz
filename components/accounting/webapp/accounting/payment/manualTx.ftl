<#--
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
 *@version    $Rev:$
 *@since      3.0
-->

<div class="head1">Manual Electronic Transaction</div>
<br>

<#if security.hasEntityPermission("MANUAL", "_PAYMENT", session)>
  ${setRequestAttribute("validTx", "false")}
  <form name="manualTxForm" method="get" action="<@ofbizUrl>/manualETx</@ofbizUrl>">
    <#if requestParameters.paymentMethodId?exists>
      <input type="hidden" name="paymentMethodId" value="${requestParameters.paymentMethodId}">
    </#if>

    <table border='0' cellpadding='2' cellspacing='0'>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Payment Method Type</div></td>
        <td width="5">&nbsp;</td>
        <td width='74%'>
          <#if paymentMethodType?has_content>
            <div class="tabletext">${paymentMethodType.description}</div>
            <input type="hidden" name="paymentMethodTypeId" value="${paymentMethodType.paymentMethodTypeId}">
          <#else>
            <select name="paymentMethodTypeId" class="selectBox">
              <option value="CREDIT_CARD">Credit Card</option>
            </select>
          </#if>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Product Store</div></td>
        <td width="5">&nbsp;</td>
        <td width='74%'>
          <#if currentStore?has_content>
            <div class="tabletext">${currentStore.storeName}</div>
            <input type="hidden" name="productStoreId" value="${currentStore.productStoreId}">
          <#else>
            <select name="productStoreId" class="selectBox">
              <#list productStores as productStore>
                <option value="${productStore.productStoreId}">${productStore.storeName}</option>
              </#list>
            </select>
          </#if>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right valign=middle><div class="tableheadtext">Transaction Type</div></td>
        <td width="5">&nbsp;</td>
        <td width='74%'>
          <#if currentTx?has_content>
            <div class="tabletext">${currentTx.description}</div>
            <input type="hidden" name="transactionType" value="${currentTx.enumId}">
          <#else>
            <select name="transactionType" class="selectBox" onchange="javascript:document.manualTxForm.submit();">
              <#list paymentSettings as setting>
                <option value="${setting.enumId}">${setting.description}</option>
              </#list>
            </select>
          </#if>
        </td>
      </tr>

      <#-- payment method information -->
      <#if paymentMethodType?has_content && paymentMethodTypeId == "CREDIT_CARD">
        ${pages.get("/payment/manualCCTx.ftl")}
      <#elseif paymentMethodType?has_content && paymentMethodTypeId == "GIFT_CARD">
        ${pages.get("/payment/manualGCTx.ftl")}
      </#if>

     <#if requestAttributes.validTx?default("false") == "true">
        <tr><td colspan="3"><hr class="sepbar"></td></tr>

        <#-- amount field -->
        <#if txType != "PRDS_PAY_RELEASE">
          <tr>
            <td width="26%" align=right valign=middle><div class="tableheadtext">Amount</div></td>
            <td width="5">&nbsp;</td>
            <td width="74%">
              <input type="text" class="inputBox" size="20" maxlength="30" name="amount">
            *</td>
          </tr>
        </#if>

        <#-- submit button -->
        <tr>
          <td width="26%" align=right valign=middle>&nbsp;</td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <input type="submit" value="Submit">
          </td>
        </tr>
      <#elseif txType?has_content>
        <tr>
          <td colspan="3" align="center">
            <br>
            <div class="head2">This transaction type is not yet supported</div>
            <br>
          </td>
        </tr>
      </#if>
    </table>
  </form>
<#else>
  <h3>You do not have permission for this function.</h3>
</#if>

