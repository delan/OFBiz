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
 *@version    $Rev:$
 *@since      3.0
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#assign locale = requestAttributes.locale>
<script language="JavaScript">
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



${pages.get("/routing/RoutingDetailTabBar.ftl")}
<#if security.hasEntityPermission("MANUFACTURING", "_CREATE", session)>
<form method="post" action="<@ofbizUrl>/AddRoutingTaskAssoc</@ofbizUrl>" name="addtaskassocform">
   <input type="hidden" name="workEffortIdFrom" value="${requestParameters.workEffortIdFrom}">
   <input type="hidden" name="workEffortAssocTypeId" value="ROUTING_COMPONENT">
   <input type="hidden" name="copyTask" value="N">
   <input type="hidden" name="addTask" value="Y">
   <table width="100%" cellpadding="2" cellspacing="0" border="0" class="boxoutside">
       <tr>
           <td align="right"><div class="tableheadtext">${uiLabelMap.ManufacturingRoutingTaskId}</div></td>
           <td><input type="text" name="workEffortIdTo" class="inputBox" size="20">
								<a href="javascript:call_fieldlookup(document.addtaskassocform.workEffortIdTo,'<@ofbizUrl>/LookupRoutingTask</@ofbizUrl>', 'vide',540,450);"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'></a>
           </td>
           <td align="right"><div class="tableheadtext">${uiLabelMap.CommonFromDate}</div></td>
           <td><input type="text" name="fromDate" class="inputBox" size="25">
        			<a href="javascript:call_cal(document.addtaskassocform.fromDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Click here For Calendar"></a>
           </td>
           <td align="center" width="40%">&nbsp;</td>
         </tr>
         <tr>
           <td align="right"><div class="tableheadtext">${uiLabelMap.CommonSequenceNum}</div></td>
           <td><input type="text" name="sequenceNum" class="inputBox" size="10"></td>
           <td align="right"><div class="tableheadtext">${uiLabelMap.CommonThruDate}</div></td>
           <td><input type="text" name="thruDate" class="inputBox" size="25">
        			<a href="javascript:call_cal(document.addtaskassocform.thruDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Click here For Calendar"></a>
           </td>
           <td>&nbsp;</td>
         </tr>
         <tr>
            <td >&nbsp;</td>
           <td align="left" colspan="3"> <a href="javascript:addRoutingTask();" class="buttontext">[${uiLabelMap.ManufacturingAddExistingRoutingTask}]</a>
           	&nbsp;-&nbsp;
           	<a href="javascript:copyAndAddRoutingTask();" class="buttontext">[${uiLabelMap.ManufacturingCopyAndAddRoutingTask}]</a>                
			</td>
           <td>&nbsp;</td>
         </tr>
    </table>
</form>
<br>
<table width="100%" cellpadding="2" cellspacing="0" border="0">
        <tr valign=top>
			<#if routingTask?has_content>
		        <td width="50%" >
				      <table width='100%' border='0' cellspacing='0' cellpadding='0' class="boxoutside">
				       <tr   class="boxtop">
				          <td align=center class="boxhead">${uiLabelMap.ManufacturingEditRoutingTaskAssocDateValidity}</td>
				        </tr>
				        <tr>          
				          <td>
								${updateRoutingTaskAssocWrapper.renderFormString()}
				          </td>
				        </tr>
				      </table>
				</td>
          		<td width="50%" >
<#--   RoutingTask  Edition -->
					   <form name="routingTaskform" method="post" action="<@ofbizUrl>/UpdateRoutingTaskForRouting</@ofbizUrl>">
					    	<input type="hidden" name="workEffortId" value="${routingTask.workEffortId}">
					   		<input type="hidden" name="workEffortIdFrom" value="${requestParameters.workEffortIdFrom}">
					  <table width="100%" border="0" cellpadding="2" cellspacing="0" class="boxoutside">
						<tr   class="boxtop">
							<td align=center class=boxhead colspan=3>${uiLabelMap.ManufacturingEditRoutingTaskId}&nbsp;:&nbsp;${routingTask.workEffortId}</td>
						</tr>
					    <tr>
					      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.ManufacturingTaskName}</div></td>
					      <td width="5">&nbsp;</td>
					      <td width="74%"><input type="text" class="inputBox" size="30" name="workEffortName" value="${routingTask.workEffortName?if_exists}"></td>
					    </tr>
					    <tr>
					      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.ManufacturingTaskPurpose}</div></td>
					      <td width="5">&nbsp;</td>
					      <td width="74%">
					         <select class="selectBox" name="workEffortPurposeTypeId">
					          <#list allTaskPurposeTypes as taskPurposeType>
					          <option value="${taskPurposeType.workEffortPurposeTypeId}" <#if routingTask?has_content && routingTask.workEffortPurposeTypeId?default("") == taskPurposeType.workEffortPurposeTypeId>SELECTED</#if>>${(taskPurposeType.get("description", locale))?if_exists}</option>
					          </#list>
					        </select>
					    </tr>
					    <tr>
					      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.CommonDescription}</div></td>
					      <td width="5">&nbsp;</td>
					      <td width="74%"><input type="text" class="inputBox" size="40" name="description" value="${routingTask.description?if_exists}"></td>
					    </tr>
					    <tr>
					      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.ManufacturingMachineGroup}</div></td>
					      <td width="5">&nbsp;</td>
					      <td width="74%">
					         <select class="selectBox" name="fixedAssetId">
							  <option></option>
					          <#list machineGroups as machineGroup>
					          <option value="${machineGroup.fixedAssetId}" <#if routingTask?has_content && routingTask.fixedAssetId?default("") == machineGroup.fixedAssetId>SELECTED</#if>>${(machineGroup.get("fixedAssetName", locale))?if_exists}</option>
					          </#list>
					        </select>
					    </tr>
					    <tr>
					      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.ManufacturingTaskEstimatedSetupMillis}</div></td>
					      <td width="5">&nbsp;</td>
					      <td width="74%"><input type="text" class="inputBox" size="10" name="estimatedSetupMillis" value="${routingTask.estimatedSetupMillis?default(0)}"></td>
					    </tr>
					    <tr>
					      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.ManufacturingTaskEstimatedMilliSeconds}</div></td>
					      <td width="5">&nbsp;</td>
					      <td width="74%"><input type="text" class="inputBox" size="10" name="estimatedMilliSeconds" value="${routingTask.estimatedMilliSeconds?default(0)}"></td>
					    </tr>
					    <tr>
					      <td width="26%" align="right" valign="top">
					      <td width="5">&nbsp;</td>
					      <td width="74%"><input type="submit" value="${uiLabelMap.CommonUpdate}" class="smallSubmit"></td>
					    </tr>
					  </table>
					</form>
<#--   End of  RoutingTask Edition  -->
		       </td>
	      </#if> <#-- routingTask?has_content -->
       </tr>
</table>
</#if> <#-- security.hasEntityPermission("MANUFACTURING", "_CREATE", session) -->
<#if security.hasEntityPermission("MANUFACTURING", "_VIEW", session)>
	<br>
	<hr class="sepbar">
	<#if allRoutingTasks?has_content>
		${listRoutingTaskAssocWrapper.renderFormString()}
	</#if>

<#else>
 	<h3>${uiLabelMap.ManufacturingMachinePermissionError}</h3>
</#if>

	