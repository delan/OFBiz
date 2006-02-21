<#--
$Id: $

Copyright 2001-2006 The Apache Software Foundation

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
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<div class="tabContainer">
      <a href="<@ofbizUrl>paymentOverview?paymentId=${payment.paymentId}</@ofbizUrl>" class="${selectedClassMap.paymentOverview?default(unselectedClassName)}">Overview</a>
      <#if payment.statusId == "PMNT_NOT_PAID">
          <a href="<@ofbizUrl>editPayment?paymentId=${payment.paymentId}</@ofbizUrl>" class="${selectedClassMap.editPayment?default(unselectedClassName)}">Header</a>
      </#if>
      <#if payment.statusId == "PMNT_RECEIVED" || payment.statusId == "PMNT_SENT" || payment.statusId == "PMNT_NOT_PAID">
          <a href="<@ofbizUrl>editPaymentApplications?paymentId=${payment.paymentId}</@ofbizUrl>" class="${selectedClassMap.editPaymentApplications?default(unselectedClassName)}">Applications</a>
      </#if>
      <!--a href="<@ofbizUrl>payment.pdf?paymentId=${payment.paymentId}</@ofbizUrl>" class="${unselectedClassName}">${uiLabelMap.AccountingpaymentPDF}</a-->
</div>
<div>
<a href="<@ofbizUrl>editPayment</@ofbizUrl>" class="buttontext">Create new Payment</a>
<#if payment.statusId == "PMNT_NOT_PAID">
<!-- the SENT status is only for disbursements -->
  <#if Static['org.ofbiz.accounting.util.UtilAccounting'].isDisbursement(payment)>
    <a href="<@ofbizUrl>setPaymentStatus?paymentId=${payment.paymentId}&statusId=PMNT_SENT</@ofbizUrl>" class="buttontext">Status to Sent</a>
  <#else>
<!-- other payments (ie, receipts) can only be marked RECEIVED, not SENT -->
    <a href="<@ofbizUrl>setPaymentStatus?paymentId=${payment.paymentId}&statusId=PMNT_RECEIVED</@ofbizUrl>" class="buttontext">Status to Received</a>
  </#if>
</#if>
<#if payment.statusId == "PMNT_SENT" || payment.statusId == "PMNT_RECEIVED">
    <a href="<@ofbizUrl>setPaymentStatus?paymentId=${payment.paymentId}&statusId=PMNT_CONFIRMED</@ofbizUrl>" class="buttontext">Status to Confirm</a>
</#if>
<#if payment.statusId == "PMNT_NOT_PAID">
    <a href="javascript:confirmActionLink('You want to cancel this payment number ${payment.paymentId}?','setPaymentStatus?paymentId=${payment.paymentId}&statusId=PMNT_CANCELLED')" class="buttontext">Status to 'Cancelled'</a>
</#if>
</div>
<br/>
