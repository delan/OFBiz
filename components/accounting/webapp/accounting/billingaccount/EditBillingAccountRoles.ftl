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
 *@version    $Revision: 1.1 $
 *@since      2.1
-->

<div class='tabContainer'>
  <a href="<@ofbizUrl>/editBillingAccount?billingAccountId=${billingAccount.billingAccountId}</@ofbizUrl>" class="tabButton">Account</a>
  <a href="<@ofbizUrl>/editBillingAccountRoles?billingAccountId=${billingAccount.billingAccountId}</@ofbizUrl>" class="tabButtonSelected">Roles</a>
  <a href="<@ofbizUrl>/editBillingAccountTerms?billingAccountId=${billingAccount.billingAccountId}</@ofbizUrl>" class="tabButton">Terms</a>
  <a href="<@ofbizUrl>/viewBillingAccountInvoices?billingAccountId=${billingAccount.billingAccountId}</@ofbizUrl>" class="tabButton">Invoices</a>
  <a href="<@ofbizUrl>/viewBillingAccountPayments?billingAccountId=${billingAccount.billingAccountId}</@ofbizUrl>" class="tabButton">Payments</a>
</div>

<div class="head1">Billing Account Roles</div>

<br>
<table width="100%" border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td><div class='tableheadtext'>Party ID</div></td>
    <td><div class='tableheadtext'>Role Type ID</div></td>
    <td><div class='tableheadtext'>From Date</div></td>
    <td><div class='tableheadtext'>Thru Date</div></td>
    <td>&nbsp;</td>
  </tr>
  <tr><td colspan='5'><hr class='sepbar'></td></tr>
  <#if !billingAccountRoles?exists || billingAccountRoles?size == 0>
    <tr>
      <td colspan='5'><div class='tabletext'>No billing account roles exist.</div></td>
    </tr>
  <#else>
    <#list billingAccountRoles as role>
    <tr>
      <td><div class='tabletext'>${role.partyId}</div></td>
      <td><div class='tabletext'>${role.roleTypeId}</div></td>
      <td><div class='tabletext'>${role.fromDate}</div></td>
      <td><div class='tabletext'>${role.thruDate?if_exists}</div></td>
      <td align='right'>  
        <#if role.thruDate?exists>
          &nbsp;
        <#else>
        <a href="<@ofbizUrl>/updateBillingAccountRole?billingAccountId=${role.billingAccountId}&partyId=${role.partyId}&roleTypeId=${role.roleTypeId}&fromDate=${role.fromDate}&thruDate=${nowTimestamp}</@ofbizUrl>" class='buttontext'>[Remove]</a> 
        </#if>
      </td>
    </tr>
    </#list>
  </#if>
</table>

<br>
<div class="head1">Create Billing Account Role</div>
<br>
<form name="billingform" method="post" action="<@ofbizUrl>/createBillingAccountRole</@ofbizUrl>">
  <input type='hidden' name='billingAccountId' value='${billingAccount.billingAccountId}'>
  <table width="90%" border="0" cellpadding="2" cellspacing="0"> 
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">Party ID</div></td>
      <td width='5'>&nbsp;</td>
      <td width='74%'><input type="text" class="inputBox" size="25" name="partyId" value="">*</td>
    </tr>  
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">Role Type ID</div></td>
      <td width='5'>&nbsp;</td>
      <td width='74%'>
        <select name="roleTypeId" class="selectBox">
          <#list roleTypes as roleType>
          <option value="${roleType.roleTypeId}">${roleType.description?if_exists}</option>
          </#list>
        </select>
      *</td>
    </tr>    
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">From Date</div></td>
      <td width='5'>&nbsp;</td>
      <td width='74%'>
        <input type="text" name="fromDate" size="25" class="inputBox" value="">
        <a href="javascript:call_cal(document.billingform.fromDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
      </td>
    </tr> 
    <tr>
      <td width='26%' align='right' valign='top'>
        <input type='submit' value='Save' class='smallSubmit'>
      <td width='5'>&nbsp;</td>
      <td width='74%'>&nbsp;</td>
    </tr>    
  </table>
</form>




    