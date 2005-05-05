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
 *
-->

<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<#if custRequest?exists>
<div class='tabContainer'>
  <a href="<@ofbizUrl>/request?custRequestId=${custRequest.custRequestId}</@ofbizUrl>" class="${selectedClassMap.request?default(unselectedClassName)}">${uiLabelMap.WorkEffortRequest}</a>
  <a href="<@ofbizUrl>/requestroles?custRequestId=${custRequest.custRequestId}</@ofbizUrl>" class="${selectedClassMap.requestroles?default(unselectedClassName)}">${uiLabelMap.WorkEffortRequestRoles}</a>
  <a href="<@ofbizUrl>/requestitems?custRequestId=${custRequest.custRequestId}</@ofbizUrl>" class="${selectedClassMap.requestitems?default(unselectedClassName)}">${uiLabelMap.WorkEffortRequestItems}</a>
  <#if custRequestItem?exists>
    <a href="<@ofbizUrl>/requestitem?custRequestId=${custRequest.custRequestId}&custRequestItemSeqId=${custRequestItem.custRequestItemSeqId}</@ofbizUrl>" class="${selectedClassMap.requestitem?default(unselectedClassName)}">${uiLabelMap.WorkEffortItem}</a>
    <a href="<@ofbizUrl>/requestitemnotes?custRequestId=${custRequest.custRequestId}&custRequestItemSeqId=${custRequestItem.custRequestItemSeqId}</@ofbizUrl>" class="${selectedClassMap.requestitemnotes?default(unselectedClassName)}">${uiLabelMap.WorkEffortNotes}</a>
    <#if custRequest.custRequestTypeId = "RF_QUOTE">
    <a href="<@ofbizUrl>/RequestItemQuotes?custRequestId=${custRequest.custRequestId}&custRequestItemSeqId=${custRequestItem.custRequestItemSeqId}</@ofbizUrl>" class="${selectedClassMap.requestitemquotes?default(unselectedClassName)}">${uiLabelMap.WorkEffortQuotes}</a>
    </#if>
    <a href="<@ofbizUrl>/requestitemrequirements?custRequestId=${custRequest.custRequestId}&custRequestItemSeqId=${custRequestItem.custRequestItemSeqId}</@ofbizUrl>" class="${selectedClassMap.requestitemrequirements?default(unselectedClassName)}">${uiLabelMap.WorkEffortRequirements}</a>
    <a href="<@ofbizUrl>/EditRequestItemWorkEffort?custRequestId=${custRequest.custRequestId}&custRequestItemSeqId=${custRequestItem.custRequestItemSeqId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortTasks}</a>
  </#if>
</div>
</#if>
