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
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@author     Olivier Heintz (olivier.heintz@nereide.biz)
 *@version    $Rev$
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

${pages.get("/billingaccount/BillingAccountTabBar.ftl")}

<#if billingAccount?has_content>
  <div class="head1">${uiLabelMap.AccountingUpdateBillingAccount}</div>
  <form name="billingform" method="post" action="<@ofbizUrl>/updateBillingAccount</@ofbizUrl>">
    <input type="hidden" name="billingAccountId" value="${billingAccount.billingAccountId}">
<#else>
  <div class="head1">${uiLabelMap.AccountingCreateBillingAccount}</div>
  <form name="billingform" method="post" action="<@ofbizUrl>/createBillingAccount</@ofbizUrl>">
    <#if (requestParameters.partyId)?has_content && (requestParameters.roleTypeId)?has_content>
      <input type="hidden" name="roleTypeId" value="${requestParameters.roleTypeId}">
      <input type="hidden" name="partyId" value="${requestParameters.partyId}">
    </#if>
</#if>

  <br>
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <#if billingAccount?has_content>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.AccountingAccountId}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%" valign="top"><div class="tabletext"><b>${billingAccount.billingAccountId?if_exists}</b> (${uiLabelMap.CommonNotModifRecreat})</td>
    </tr>
    </#if>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.AccountingDescription}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="40" name="description" value="${billingAccountData.description?if_exists}"></td>
    </tr>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.AccountingBillingContactMechId}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="20" name="contactMechId" value="${billingAccountData.contactMechId?if_exists}"></td>
    </tr>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.AccountingAccountLimit}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="10" name="accountLimit" value="${billingAccountData.accountLimit?default(0)?string("##0.00")}">
      *</td>
    </tr>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.AccountingCurrencyUom}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="10" name="accountCurrencyUomId" value="${billingAccountData.accountCurrencyUomId?if_exists}">
      </td>
    </tr>
    <tr>
      <td width="26%" align="right" valign="top">
        <input type="submit" value="${uiLabelMap.CommonSave}" class="smallSubmit">
      <td width="5">&nbsp;</td>
      <td width="74%">&nbsp;</td>
    </tr>
  </table>
</form>
