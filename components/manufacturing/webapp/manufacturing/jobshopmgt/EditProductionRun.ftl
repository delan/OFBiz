<#--
 *  Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
<#assign uiLabelMap = requestAttributes.uiLabelMap>
${pages.get("/jobshopmgt/ProductionRunTabBar.ftl")}

<#if productionRunId?has_content>
<#-- Mandatory work efforts -->
<#if mandatoryWorkEfforts?has_content>
    <div class="tabletext">
    ${uiLabelMap.ManufacturingMandatoryProductionRuns}:
    <#list mandatoryWorkEfforts as mandatoryWorkEffortAssoc>
        <#assign mandatoryWorkEffort = mandatoryWorkEffortAssoc.getRelatedOne("FromWorkEffort")>
        <#if "PRUN_COMPLETED" == mandatoryWorkEffort.getString("currentStatusId") || "PRUN_CLOSED" == mandatoryWorkEffort.getString("currentStatusId")>
            [${mandatoryWorkEffort.workEffortId}]&nbsp;
        <#else>
            <#if "PRUN_CREATED" == mandatoryWorkEffort.getString("currentStatusId")>
                <a href="<@ofbizUrl>/EditProductionRun?productionRunId=${mandatoryWorkEffort.workEffortId}</@ofbizUrl>" class="buttontext">[${mandatoryWorkEffort.workEffortId}]</a>
            <#else>
                <a href="<@ofbizUrl>/ProductionRunDeclaration?productionRunId=${mandatoryWorkEffort.workEffortId}</@ofbizUrl>" class="buttontext">[${mandatoryWorkEffort.workEffortId}]</a>
            </#if>
        </#if>
    </#list>
    </div>
</#if>
<#-- Dependent work efforts -->
<#if dependentWorkEfforts?has_content>
    <div class="tabletext">
    ${uiLabelMap.ManufacturingDependentProductionRuns}: 
    <#list dependentWorkEfforts as dependentWorkEffortAssoc>
        <#assign dependentWorkEffort = dependentWorkEffortAssoc.getRelatedOne("ToWorkEffort")>
        <#if "PRUN_COMPLETED" == dependentWorkEffort.currentStatusId || "PRUN_CLOSED" == dependentWorkEffort.currentStatusId>
            [${dependentWorkEffort.workEffortId}]&nbsp;
        <#else>
            <#if "PRUN_CREATED" == dependentWorkEffort.getString("currentStatusId")>
                <a href="<@ofbizUrl>/EditProductionRun?productionRunId=${dependentWorkEffort.workEffortId}</@ofbizUrl>" class="buttontext">[${dependentWorkEffort.workEffortId}]</a>
            <#else>
                <a href="<@ofbizUrl>/ProductionRunDeclaration?productionRunId=${dependentWorkEffort.workEffortId}</@ofbizUrl>" class="buttontext">[${dependentWorkEffort.workEffortId}]</a>
            </#if>
        </#if>
    </#list>
    </div>
</#if>


<table border=0 width="100%" cellspacing="0" cellpadding="0">
    <tr valign=top>
        <td>
            <#-- ProductionRun Update sub-screen -->
            <table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
                <tr>
                    <td>
                        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
                            <tr>
                                <td>
                                    <div class="boxhead">${uiLabelMap.ManufacturingProductionRunId}: ${productionRunId}</div>
                                </td>
                            </tr>
                        </table>
                        ${updateProductionRunWrapper.renderFormString()}
                    </td>
                </tr>
                <#if orderItems?has_content>
                <tr>
                    <td align="left">
                        <table border="0" cellpadding="2" cellspacing="0">
                            <tr>
                                <td width="20%" align="right">
                                    <span class="tableheadtext">${uiLabelMap.ManufacturingOrderItems}</span>
                                </td>
                                <td>&nbsp;</td>
                                <td width="80%" align="left">
                                    <span class="tabletext">
                                        <#list orderItems as orderItem>
                                            <a href="/ordermgr/control/orderview?order_id=${orderItem.getString("orderId")}" class="buttontext" target="_blank">
                                                ${orderItem.getString("orderId")}/${orderItem.getString("orderItemSeqId")}
                                            </a>&nbsp;
                                        </#list>
                                    </span>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                </#if>
                <tr>
                    <td align="center">
                        <a href="<@ofbizUrl>/changeProductionRunStatusToPrinted?productionRunId=${productionRunId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.ManufacturingProductionRunPrintDocuments}]</a>
                    </td>
                </tr>
            </table>
        </td>
        <#-- RoutingTask sub-screen  Update or Add  -->
        <#if routingTaskId?has_content || actionForm=="AddRoutingTask">
			<td> &nbsp; </td>
			<td>
				<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
				  <tr><td>	
					<table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
						<tr>
						<#if routingTaskId?has_content> <#-- RoutingTask Update  -->
				    		<td><div class="boxhead">${uiLabelMap.CommonEdit}&nbsp;${uiLabelMap.ManufacturingRoutingTaskId} : ${routingTaskId}</div></td>
				    	<#else>											 <#-- RoutingTask Add         -->
				    		<td><div class="boxhead">${uiLabelMap.ManufacturingAddRoutingTask}</div></td>
				    	</#if>
				    	</tr>
					</table>
					${editPrRoutingTaskWrapper.renderFormString()}
				  </td></tr>
				</table>
			</td>
		</#if>	
                <#-- Product component sub-screen  Update or Add  -->
                <#if productId?has_content || actionForm=="AddProductComponent">

			<td> &nbsp; </td>
			<td>
				<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
				  <tr><td>	
					<table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
						<tr>
						<#if productId?has_content> <#-- Product component Update  -->
				    		<td><div class="boxhead">${uiLabelMap.CommonEdit}&nbsp;${uiLabelMap.ManufacturingProductionRunProductComponent} : ${productId}</div></td>
				    	<#else>									 <#-- Product component Add         -->
				    		<td><div class="boxhead">${uiLabelMap.ManufacturingAddProductionRunProductComponent}</div></td>
				    	</#if>
				    	</tr>
					</table>
					${editPrProductComponentWrapper.renderFormString()}
				  </td></tr>
				</table>
			</td>
		</#if>	
		</tr>		   		
	</table>   		
	<br>
	
			  <#-- List Of ProductionRun RoutingTasks  sub-screen -->
	<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
	  <tr><td>	
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
			<tr>
	    		<td><div class="boxhead">${uiLabelMap.ManufacturingListOfProductionRunRoutingTasks}</div></td>
        		<td align="right"><div class="tabletext">
                	<a href="<@ofbizUrl>/EditProductionRun?productionRunId=${productionRunId}&amp;actionForm=AddRoutingTask</@ofbizUrl>" class="submenutextright">
                					${uiLabelMap.ManufacturingAddRoutingTask}</a>
                </td>	
	    	</tr>
		</table>
		${ListProductionRunRoutingTasksWrapper.renderFormString()}
	  </td></tr>
	</table>   		

			  <#-- List Of ProductionRun Components  sub-screen -->
	<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
	  <tr><td>	
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
			<tr>
	    		<td><div class="boxhead">${uiLabelMap.ManufacturingListOfProductionRunComponents}</div></td>
        		<td align="right"><div class="tabletext">
                	<a href="<@ofbizUrl>/EditProductionRun?productionRunId=${productionRunId}&amp;actionForm=AddProductComponent</@ofbizUrl>" class="submenutextright">
                					${uiLabelMap.ManufacturingAddProductionRunProductComponent}</a>
                </td>	
	    	</tr>
		</table>
		${ListProductionRunComponentsWrapper.renderFormString()}
	  </td></tr>
	</table>   		
<#else>
  <div class="head1">${uiLabelMap.ManufacturingNoProductionRunSelected}</div>
</#if>
	