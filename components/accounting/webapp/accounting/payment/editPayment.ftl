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
 *@author     Eric.Barbier@nereide.biz (migration to uiLabelMap)
 *@version    $Revision: 1.3 $
 *@since      3.0
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<div class="head1">${uiLabelMap.AccountingEditPayment}</div>
<a href="<@ofbizUrl>/view/manualCcPay</@ofbizUrl>" class="buttontext">[Credit Card Payment]</a>
<a href="<@ofbizUrl>/editPayment</@ofbizUrl>" class="buttontext">[Generic Payment]</a>
<br>

<#if payment?has_content>
  <form name="editpayment" method="post" action="<@ofbizUrl>/updatePayment</@ofbizUrl>">
  <input type="hidden" name="paymentId" value="${payment.paymentId}">
<#else>
  <form name="editpayment" method="post" action="<@ofbizUrl>/createPayment</@ofbizUrl>">
</#if>

  <table border='0' cellpadding='2' cellspacing='0'>
    <#if payment?exists>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingPaymentID}:</div></td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <b>${payment.paymentId}</b>
        </td>
      </tr>
    </#if>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingPaymentType}:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <select name="paymentTypeId" class="selectBox">
          <#if currentType?has_content>
            <option value="${currentType.paymentTypeId}">${currentType.description}</option>
            <option value="${currentType.paymentTypeId}">---</option>
          <#else>
            <option></option>
          </#if>
          <#list paymentTypes as type>
            <option value="${type.paymentTypeId}">${type.description}</option>
          </#list>
        </select>
      </td>
    </tr>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingPaymentMethodId}:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <select name="paymentMethodTypeId" class="selectBox">
          <#if currentMethod?has_content>
            <option value="${currentMethod.paymentMethodTypeId}">${currentMethod.description}</option>
            <option value="${currentMethod.paymentMethodTypeId}">---</option>
          <#else>
            <option></option>
          </#if>
          <#list paymentMethodTypes as method>
            <option value="${method.paymentMethodTypeId}">${method.description}</option>
          </#list>
        </select>
      </td>
    </tr>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingStatus}:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <select name="statusId" class="selectBox">
          <#if currentStatus?has_content>
            <option value="${currentStatus.statusId}">${currentStatus.description}</option>
            <option value="${currentStatus.statusId}">---</option>
          <#else>
            <option></option>
          </#if>
          <#list paymentStatuses as status>
            <option value="${status.statusId}">${status.description}</option>
          </#list>
        </select>
      </td>
    </tr>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingPaymentPreferenceId}:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <input type='text' class='inputBox' name='paymentPreferenceId' value='${(payment.paymentPreferenceId)?if_exists}'>
      </td>
    </tr>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingPaymentMethodId}:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <input type='text' class='inputBox' name='paymentMethodId' value='${(payment.paymentMethodId)?if_exists}'>
      </td>
    </tr>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingFromPartyId}:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <input type='text' class='inputBox' name='partyIdTo' value='${(payment.partyIdTo)?if_exists}'>
      </td>
    </tr>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingToPartyId}:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <input type='text' class='inputBox' name='partyIdFrom' value='${(payment.partyIdFrom)?if_exists}'>
      </td>
    </tr>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingReferenceNumber}:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <input type='text' class='inputBox' name='paymentRefNum' value='${(payment.paymentRefNum)?if_exists}'>
      </td>
    </tr>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingAmount}:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <#assign amount = (payment.amount?string("##0.00"))?if_exists>
        <input type='text' class='inputBox' name='amount' value='${amount?if_exists}'>
      </td>
    </tr>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingEffectiveDate}:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <#if (payment.effectiveDate)?exists>
          <#assign effectiveDate = payment.get("effectiveDate").toString()>
        </#if>
        <input type='text' class='inputBox' size='25' name='effectiveDate' value='${effectiveDate?if_exists}'>
        <a href="javascript:call_cal(document.editpayment.effectiveDate, '');"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
      </td>
    </tr>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%' align='right' nowrap><div class="tabletext">${uiLabelMap.AccountingComments}:</div></td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <input type='text' size='60' class='inputBox' name='comments' value='${(payment.comments)?if_exists}'>
      </td>
    </tr>
    <#if payment?has_content>
      <tr>
        <td width='14%'>&nbsp;</td>
        <td width='6%'>&nbsp;</td>
        <td width='6%'>&nbsp;</td>
        <td width='74%'>
          <input type="submit" class="smallSubmit" value="${uiLabelMap.CommonUpdate}">
        </td>
      </tr>
  <#else>
    <tr>
      <td width='14%'>&nbsp;</td>
      <td width='6%'>&nbsp;</td>
      <td width='6%'>&nbsp;</td>
      <td width='74%'>
        <input type="submit" class="smallSubmit" value="${uiLabelMap.CommonCreateNew}">
      </td>
    </tr>
  </#if>
  </table>
</form>
