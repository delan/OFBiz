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
 *@version    $Revision: 1.1 $
 *@since      3.0
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
${pages.get("/jobshopmgt/ProductionRunTabBar.ftl")}

<#if productionRunId?has_content>
	<table border=0 width="100%" cellspacing="0" cellpadding="0">
		<tr valign=top>
			<td>  <#-- ProductionRun Update sub-screen -->
				<table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
				  <tr><td>	
					<table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
						<tr>
				    		<td><div class="boxhead">${uiLabelMap.ManufacturingProductionRunId} : ${productionRunId}</div></td>
				    	</tr>
					</table>
					${updateProductionRunWrapper.renderFormString()}
				  </td></tr>
				</table>
			</td>
		<#if routingTaskId?has_content || actionForm=="addRoutingTask">  <#-- RoutingTask sub-screen  Update or Add  -->
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
		<#if productId?has_content || actionForm=="addProductComponent">  <#-- Product component sub-screen  Update or Add  -->
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
                	<a href="<@ofbizUrl>/EditProductionRun?productionRunId=${productionRunId}&amp;actionForm=addRoutingTask</@ofbizUrl>" class="submenutextright">
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
                	<a href="<@ofbizUrl>/EditProductionRun?productionRunId=${productionRunId}&amp;actionForm=addProductComponent</@ofbizUrl>" class="submenutextright">
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
	