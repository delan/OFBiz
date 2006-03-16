<#--
$Id: $

Copyright 2005-2006 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
-->

<#-- *@author     Hans Bakker (h.bakker@antwebsystems.com) -->

<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<#if invoice?has_content>
<div>
<div class="tabContainer">
      <a href="<@ofbizUrl>invoiceOverview?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class="${selectedClassMap.invoiceOverview?default(unselectedClassName)}">${uiLabelMap.AccountingInvoiceOverview}</a>
      <#if invoice.statusId != "INVOICE_CANCELLED" && invoice.statusId != "INVOICE_PAID">
          <#if invoice.statusId == "INVOICE_IN_PROCESS">
              <a href="<@ofbizUrl>editInvoice?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class="${selectedClassMap.editInvoice?default(unselectedClassName)}">${uiLabelMap.AccountingInvoiceHeader}</a>
              <a href="<@ofbizUrl>listInvoiceItems?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class="${selectedClassMap.listInvoiceItems?default(unselectedClassName)}">${uiLabelMap.AccountingInvoiceItems}</a>
              <a href="<@ofbizUrl>invoiceRoles?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class="${selectedClassMap.invoiceRoles?default(unselectedClassName)}">${uiLabelMap.AccountingInvoiceRoles}</a>
              <a href="<@ofbizUrl>invoiceStatus?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class="${selectedClassMap.invoiceStatus?default(unselectedClassName)}">${uiLabelMap.AccountingInvoiceStatus}</a>
          </#if>
      	  <a href="<@ofbizUrl>editInvoiceApplications?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class="${selectedClassMap.editInvoiceApplications?default(unselectedClassName)}">Applications</a>
      </#if>
   	  <a href="<@ofbizUrl>sendPerEmail?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class="${selectedClassMap.sendPerEmail?default(unselectedClassName)}">Send per Email</a>
</div>
<div>
<a href="<@ofbizUrl>editInvoice</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonCreateNew}</a>
<a href="<@ofbizUrl>copyInvoice?invoiceIdToCopyFrom=${invoiceId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonCopy}</a>
<a href="<@ofbizUrl>invoice.pdf?invoiceId=${invoice.invoiceId}</@ofbizUrl>" class="buttontext" target="_blank">${uiLabelMap.AccountingInvoicePDF}</a>
<#if invoice.statusId == "INVOICE_IN_PROCESS" && invoice.invoiceTypeId == "SALES_INVOICE">
    <a href="<@ofbizUrl>setInvoiceStatus?invoiceId=${invoice.invoiceId}&statusId=INVOICE_READY</@ofbizUrl>" class="buttontext">Status to 'Ready'</a>
</#if>
<#if invoice.statusId == "INVOICE_IN_PROCESS" && invoice.invoiceTypeId == "PURCHASE_INVOICE">
    <a href="<@ofbizUrl>setInvoiceStatus?invoiceId=${invoice.invoiceId}&statusId=INVOICE_RECEIVED</@ofbizUrl>" class="buttontext">Status to 'Received'</a>
</#if>
<#if invoice.statusId == "INVOICE_READY" && invoice.invoiceTypeId == "SALES_INVOICE">
    <a href="<@ofbizUrl>setInvoiceStatus?invoiceId=${invoice.invoiceId}&statusId=INVOICE_SENT</@ofbizUrl>"  class="buttontext">Status to 'Send'</a>
    <a href="<@ofbizUrl>setInvoiceStatus?invoiceId=${invoice.invoiceId}&statusId=INVOICE_IN_PROCESS</@ofbizUrl>"  class="buttontext">Status to 'In Process'</a>
</#if>
<#if invoice.statusId == "INVOICE_SENT">
    <a href="<@ofbizUrl>setInvoiceStatus?invoiceId=${invoice.invoiceId}&statusId=INVOICE_PAID</@ofbizUrl>"  class="buttontext">status to 'Paid'</a>
</#if>
<#if invoice.statusId == "INVOICE_RECEIVED">
    <a href="<@ofbizUrl>setInvoiceStatus?invoiceId=${invoice.invoiceId}&statusId=INVOICE_PAID</@ofbizUrl>"  class="buttontext">status to 'Paid'</a>
</#if>
<#-- invoice cannot be cancelled if it is PAID or CANCELLED -->
<#if invoice.statusId != "INVOICE_PAID" && invoice.statusId != "INVOICE_CANCELLED">
    <a href="javascript:confirmActionLink('You want to cancel this invoice number ${invoice.invoiceId}?','setInvoiceStatus?invoiceId=${invoice.invoiceId}&statusId=INVOICE_CANCELLED')" class="buttontext">Status to 'Cancelled'</a>
</#if>
</div>
<br/>
</#if>
