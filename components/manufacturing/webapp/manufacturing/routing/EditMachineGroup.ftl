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

<#if hasPermission>
${pages.get("/routing/RoutingTabBar.ftl")}

	<div class="head1">${uiLabelMap.ManufacturingEditMachineGroup}&nbsp; 
	</div>
	<br>
	<#if machineGroup?has_content>
			${updateMachineGroupWrapper.renderFormString()}
			<br>
			<hr class="sepbar">
	</#if>
	<#if allMachineGroups?has_content>
		${listMachineGroupWrapper.renderFormString()}
		<br>
		<hr class="sepbar">
	</#if>
	${addMachineGroupWrapper.renderFormString()} 
	<br>

<#else>
 	<h3>${uiLabelMap.ManufacturingMachinePermissionError}</h3>
</#if>

	