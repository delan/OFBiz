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
 *@version    $Revision$
 *@since      2.1
-->

<#if billingAccount?has_content>
  <div class='tabContainer'>
    <a href="<@ofbizUrl>/editBillingAccount?billingAccountId=${billingAccount.billingAccountId}</@ofbizUrl>" class="tabButtonSelected">Account</a>
    <a href="<@ofbizUrl>/editBillingAccountRoles?billingAccountId=${billingAccount.billingAccountId}</@ofbizUrl>" class="tabButton">Roles</a>
    <a href="<@ofbizUrl>/editBillingAccountTerms?billingAccountId=${billingAccount.billingAccountId}</@ofbizUrl>" class="tabButton">Terms</a>
    <a href="<@ofbizUrl>/viewBillingAccountInvoices?billingAccountId=${billingAccount.billingAccountId}</@ofbizUrl>" class="tabButton">Invoices</a>
  </div>
  <div class="head1">Update Billing Account</div>
  <form name="billingform" method="post" action="<@ofbizUrl>/updateBillingAccount</@ofbizUrl>">
    <input type="hidden" name="billingAccountId" value="${billingAccount.billingAccountId}">
<#else>
  <div class="head1">Create Billing Account</div>
  <form name="billingform" method="post" action="<@ofbizUrl>/createBillingAccount</@ofbizUrl>">
</#if>

  <br>
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <#if billingAccount?has_content>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">Billing Account ID</div></td>
      <td width='5'>&nbsp;</td>
      <td width='74%' valign='top'><div class="tabletext"><b>${billingAccount.billingAccountId?if_exists}</b> (cannot change without re-creating)</td>
    </tr>  
    </#if>      
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">Description</div></td>
      <td width='5'>&nbsp;</td>
      <td width='74%'><input type="text" class="inputBox" size="40" name="description" value="${billingAccountData.description?if_exists}"></td>
    </tr>    
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">Account Limit</div></td>
      <td width='5'>&nbsp;</td>
      <td width='74%'>
        <input type="text" class="inputBox" size="10" name="accountLimit" value="${billingAccountData.accountLimit?default(0)?string("##0.00")}">
      *</td>
    </tr> 
    <tr>
      <td width='26%' align='right' valign='top'>
        <input type='submit' value='Save' class='smallSubmit'>
      <td width='5'>&nbsp;</td>
      <td width='74%'>&nbsp;</td>
    </tr>    
  </table>
</form>
