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
 *@author     Olivier.Heintz@nereide.biz
 *@version    $Rev$
 *@since      3.0
-->

<script language="JavaScript" type="text/javascript">
<!-- //
function copyAndAddRoutingTask() {
    document.addtaskassocform.copyTask.value = "Y";
    document.addtaskassocform.submit();
}
function addRoutingTask() {
    document.addtaskassocform.copyTask.value = "N";
    document.addtaskassocform.submit();
}
// -->
</script>

<#if security.hasEntityPermission("MANUFACTURING", "_CREATE", session)>
<form method="post" action="<@ofbizUrl>AddRoutingTaskAssoc</@ofbizUrl>" name="addtaskassocform">
    <input type="hidden" name="workEffortId" value="${workEffortId}">
    <input type="hidden" name="workEffortIdFrom" value="${workEffortId}">
    <input type="hidden" name="workEffortAssocTypeId" value="ROUTING_COMPONENT">
    <input type="hidden" name="copyTask" value="N">
    <input type="hidden" name="addTask" value="Y">
    <table cellpadding="2" cellspacing="0" border="0" class="boxoutside">
        <tr>
            <td align="right">
                <div class="tableheadtext">${uiLabelMap.ManufacturingRoutingTaskId}</div>
            </td>
            <td>
                <input type="text" name="workEffortIdTo" class="inputBox" size="20">
                <a href="javascript:call_fieldlookup(document.addtaskassocform.workEffortIdTo,'<@ofbizUrl>LookupRoutingTask</@ofbizUrl>', 'vide',540,450);"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'></a>
            </td>
            <td align="right">
                <div class="tableheadtext">${uiLabelMap.CommonFromDate}</div>
            </td>
            <td>
                <input type="text" name="fromDate" class="inputBox" size="25">
                <a href="javascript:call_cal(document.addtaskassocform.fromDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Click here For Calendar"></a>
            </td>
            <td align="center" width="40%">&nbsp;</td>
        </tr>
        <tr>
            <td align="right">
                <div class="tableheadtext">${uiLabelMap.CommonSequenceNum}</div>
            </td>
            <td>
                <input type="text" name="sequenceNum" class="inputBox" size="10">
            </td>
            <td align="right">
                <div class="tableheadtext">${uiLabelMap.CommonThruDate}</div>
            </td>
            <td>
                <input type="text" name="thruDate" class="inputBox" size="25">
                <a href="javascript:call_cal(document.addtaskassocform.thruDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Click here For Calendar"></a>
            </td>
            <td>&nbsp;</td>
        </tr>

        <tr>
            <td >&nbsp;</td>
            <td align="left" colspan="3">
                <a href="javascript:addRoutingTask();" class="buttontext">${uiLabelMap.ManufacturingAddExistingRoutingTask}</a>
                &nbsp;-&nbsp;
                <a href="javascript:copyAndAddRoutingTask();" class="buttontext">${uiLabelMap.ManufacturingCopyAndAddRoutingTask}</a>
            </td>
            <td>&nbsp;</td>
        </tr>
    </table>
</form>
<br/>
</#if>
	