<#--
 *  Copyright (c) 2003-2005 The Open For Business Project - www.ofbiz.org
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
-->
<div class="tabletext">
<table>
  <tr>
     <td colspan="3"><h1 class="head1">${uiLabelMap.AccountingAgreements}</h1></td>
  </tr>
  
  <tr>
     <td colspan="3">  
<ul>
<li><a href="<@ofbizUrl>FindAgreement</@ofbizUrl>">List Available Agreements</a></li>
</ul>
<br/>
     </td>
  </tr>
  <tr>
     <td colspan="3"><h1 class="head1">${uiLabelMap.AccountingBillingMenu}</h1></td>
  </tr>
  
  <tr>
     <td colspan="3">  
<ul>
<li><a href="<@ofbizUrl>FindBillingAccount</@ofbizUrl>">Show Customer Billing Accounts</a></li>
</ul>
<br/>
     </td>
  </tr>
  
  <tr>
     <td colspan="3"><h1 class="head1">${uiLabelMap.AccountingFixedAssets}</h1></td>
  </tr>
  
  <tr>
     <td colspan="3">  
<ul>
<li><a href="<@ofbizUrl>ListFixedAssets</@ofbizUrl>">Show all Fixed Assets</a></li>
</ul>
<br/>
     </td>
  </tr>
  
  <tr>
     <td colspan="3"><h1 class="head1">${uiLabelMap.AccountingInvoicesMenu}</h1></td>
  </tr>
  
  <tr valign="top">
<td>
<ul>
<li><a href="<@ofbizUrl>findInvoices?lookupFlag=Y</@ofbizUrl>">Show All Invoices</a></li>
</ul>
</td>

<td>
<ul>
<li><a href="<@ofbizUrl>findInvoices?lookupFlag=Y&invoiceTypeId=SALES_INVOICE</@ofbizUrl>">Show Sales Invoices</a></li>
<li><a href="<@ofbizUrl>findInvoices?lookupFlag=Y&invoiceTypeId=PURCHASE_INVOICE</@ofbizUrl>">Show Purchases Invoices</a></li>
</ul>
</td>
<td>
<ul>
<#list invoiceStatus as status>
<li><a href="<@ofbizUrl>findInvoices?lookupFlag=Y&invoiceStatusId=${status.statusId}</@ofbizUrl>">Show ${status.description} Invoices</a></li>
</#list>
</ul>
</td>
</tr>

  <tr>
     <td colspan="3"><h1 class="head1">${uiLabelMap.AccountingPaymentsMenu}</h1></td>
  </tr>

<tr valign="top">
<td>
<ul>
<li><a href="<@ofbizUrl>findPayments?lookupFlag=Y</@ofbizUrl>">Show all Payments</a></li>
</ul>
</td>

<td>
<ul>
<#list paymentMethodTypes as paymentMethodType>
<li><a href="<@ofbizUrl>findPayments?lookupFlag=Y&paymentMethodTypeId=${paymentMethodType.paymentMethodTypeId}</@ofbizUrl>">Show ${paymentMethodType.get("description",locale)} Payments</a></li>
</#list>
</ul>
</td>

<td>
<ul>
<#list paymentStatus as status>
<li><a href="<@ofbizUrl>findPayments?lookupFlag=Y&paymentStatusId=${status.statusId}</@ofbizUrl>">Show ${status.description} Payments</a></li>
</#list>
</ul>
</td>
</tr>
</table>

<p><b>NOTE</b><br/>
A full accounting/GL component for OFBiz is under development.  
<a href="http://www.opensourcestrategies.com/ofbiz/accounting.php">Click here</a> for complete details.</p>
</div>
