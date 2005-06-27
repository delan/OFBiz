<div class="tabletext">
<table>
  <tr>
     <td colspan="3"><h1 class="head1">${uiLabelMap.AccountingAgreements}</h1></td>
  </tr>
  
  <tr>
     <td colspan="3">  
<ul>
<li><a href="<@ofbizUrl>/FindAgreement</@ofbizUrl>">List Available Agreements</a></li>
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
<li><a href="<@ofbizUrl>/FindBillingAccount</@ofbizUrl>">Show Customer Billing Accounts</a></li>
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
<li><a href="<@ofbizUrl>/ListFixedAssets</@ofbizUrl>">Show all Fixed Assets</a></li>
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
<li><a href="<@ofbizUrl>/findInvoices?lookupFlag=Y</@ofbizUrl>">Show All Invoices</a></li>
</ul>
</td>

<td>
<ul>
<li><a href="<@ofbizUrl>/findInvoices?lookupFlag=Y&invoiceTypeId=SALES_INVOICE</@ofbizUrl>">Show Sales Invoices</a></li>
<li><a href="<@ofbizUrl>/findInvoices?lookupFlag=Y&invoiceTypeId=PURCHASE_INVOICE</@ofbizUrl>">Show Purchases Invoices</a></li>
</ul>
</td>
<td>
<ul>
<#list invoiceStatus as status>
<li><a href="<@ofbizUrl>/findInvoices?lookupFlag=Y&invoiceStatusId=${status.statusId}</@ofbizUrl>">Show ${status.description} Invoices</a></li>
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
<li><a href="<@ofbizUrl>/findPayment?lookupFlag=Y</@ofbizUrl>">Show all Payments</a></li>
</ul>
</td>

<td>
<ul>
<#list paymentMethodTypes as paymentMethodType>
<li><a href="<@ofbizUrl>/findPayment?lookupFlag=Y&paymentMethodTypeId=${paymentMethodType.paymentMethodTypeId}</@ofbizUrl>">Show ${paymentMethodType.description} Payments</a></li>
</#list>
</ul>
</td>

<td>
<ul>
<#list paymentStatus as status>
<li><a href="<@ofbizUrl>/findPayment?lookupFlag=Y&paymentStatusId=${status.statusId}</@ofbizUrl>">Show ${status.description} Payments</a></li>
</#list>
</ul>
</td>
</tr>
</table>

<p><b>NOTE</b><br/>
A full accounting/GL component for OFBiz is under development.  
<a href="http://www.opensourcestrategies.com/ofbiz/accounting.php">Click here</a> for complete details.</p>
</div>