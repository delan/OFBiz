<#-- *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org * *  Permission is hereby granted, free of charge, to any person obtaining a  *  copy of this software and associated documentation files (the "Software"),  *  to deal in the Software without restriction, including without limitation  *  the rights to use, copy, modify, merge, publish, distribute, sublicense,  *  and/or sell copies of the Software, and to permit persons to whom the  *  Software is furnished to do so, subject to the following conditions: * *  The above copyright notice and this permission notice shall be included  *  in all copies or substantial portions of the Software. * *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS  *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF  *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY  *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT  *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR  *  THE USE OR OTHER DEALINGS IN THE SOFTWARE. * *@author     Olivier Heintz (olivier.heintz@nereide.biz) *@version    $Rev$ *@since      3.0--><#if (requestAttributes.uiLabelMap)?exists>    <#assign uiLabelMap = requestAttributes.uiLabelMap></#if><script language="JavaScript"><!-- //function lookupRoutings() {    document.lookuprouting.submit();}// --></script><#--<#if routingId?has_content>	${pages.get("/routing/RoutingDetailTabBar.ftl")}<#else>		${pages.get("/routing/RoutingTabBar.ftl")}</#if>--><#--   Add Routing  --><div><a href="<@ofbizUrl>/FindRouting?addRecord=Y&hideFields=N</@ofbizUrl>" class="buttontext">[${uiLabelMap.ManufacturingNewRouting}]</a></div><#if requestParameters.addRecord?default("N") == "Y">   <#assign formSuite ="Y">   <#assign buttonEnr = uiLabelMap.CommonAdd>		   <form name="routingform" method="post" action="<@ofbizUrl>/CreateRouting?hideFields=N${paramList}</@ofbizUrl>">    	<input type="hidden" name="workEffortTypeId" value="ROUTING">    	<input type="hidden" name="currentStatusId" value="ROU_ACTIVE"><#elseif routing?has_content && ! (requestParameters.fromEdit?default("N") == "Y")>   <#assign formSuite ="Y">		    <#assign buttonEnr = uiLabelMap.CommonUpdate>		   <form name="routingform" method="post" action="<@ofbizUrl>/UpdateRouting?hideFields=N${paramList}</@ofbizUrl>">    	<input type="hidden" name="workEffortId" value="${routing.workEffortId}"></#if><#if formSuite?default("N") == "Y">     	<input type="hidden" name="VIEW_SIZE" value="${viewSize}">     	<input type="hidden" name="VIEW_INDEX" value="${viewIndex}">     	<input type="hidden" name="fromEdit" value="Y">  <br>  <table width="90%" border="0" cellpadding="2" cellspacing="0">    <tr>      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.ManufacturingRoutingName}</div></td>      <td width="5">&nbsp;</td>      <td width="74%"><input type="text" class="inputBox" size="30" name="workEffortName" value="${routing.workEffortName?if_exists}"></td>    </tr>    <tr>      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.CommonDescription}</div></td>      <td width="5">&nbsp;</td>      <td width="74%"><input type="text" class="inputBox" size="40" name="description" value="${routing.description?if_exists}"></td>    </tr>    <tr>      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.ManufacturingQuantityMinimum}</div></td>      <td width="5">&nbsp;</td>      <td width="74%"><input type="text" class="inputBox" size="10" name="quantityToProduce" value="${routing.quantityToProduce?default(0)}"></td>    </tr>    <tr>      <td width="26%" align="right" valign="top">      <td width="5">&nbsp;</td>      <td width="74%"><input type="submit" value="${buttonEnr}" class="smallSubmit"></td>    </tr>  </table></form></#if> <#--  formSuite?default("N") == "Y" --><#--   End of Add Routing  --><form method="post" name="lookuprouting" action="<@ofbizUrl>/FindRouting</@ofbizUrl>"><input type="hidden" name="lookupFlag" value="Y"><input type="hidden" name="hideFields" value="Y"><table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">  <tr>    <td width="100%">      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">        <tr>          <td><div class="boxhead">${uiLabelMap.ManufacturingFindRouting}</div></td>          <td align="right">            <div class="tabletext">              <#if requestParameters.hideFields?default("N") == "Y">                <a href="<@ofbizUrl>/FindRouting?hideFields=N${paramList}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonShowLookupFields}</a>              <#else>                <#if routingList?exists>                    <a href="<@ofbizUrl>/FindRouting?hideFields=Y${paramList}</@ofbizUrl>" class="submenutext">${uiLabelMap.CommonHideFields}</a>                </#if>                <a href="javascript:lookupRoutings();" class="submenutextright">${uiLabelMap.CommonLookup}</a>                              </#if>            </div>          </td>        </tr>      </table>      <#if requestParameters.hideFields?default("N") != "Y">      <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">        <tr>          <td align="center" width="100%">            <table border="0" cellspacing="0" cellpadding="2">              <tr>                <td width="25%" align="right"><div class="tableheadtext">${uiLabelMap.ManufacturingRoutingId}:</div></td>                <td width="5%">&nbsp;</td>                <td>                    <input type="text" size="20" class="inputBox" name="workEffortId" value="${requestParameters.workEffortId?if_exists}">                </td>              </tr>              <tr>                <td width="25%" align="right"><div class="tableheadtext">${uiLabelMap.ManufacturingRoutingName}:</div></td>                <td width="5%">&nbsp;</td>                <td>                    <input type="text" size="30" class="inputBox" name="workEffortName" value="${requestParameters.workEffortName?if_exists}">                </td>              </tr>                  		  <tr>      		    <td width="26%" align="center" valign="top">      		    <td width="5">&nbsp;</td>      		    <td width="74%"><input type="submit" value="${uiLabelMap.CommonFind}" class="smallSubmit"></td>    		</tr>            </table>          </td>        </tr>      </table>      </#if>    </td>  </tr></table></form> <br><#if routingList?exists><table border=0 width="100%" cellspacing="0" cellpadding="0" class="boxoutside">  <tr>    <td width="100%">      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">        <tr>          <td width="50%"><div class="boxhead">${uiLabelMap.CommonElementsFound}</div></td>          <td width="50%">            <div class="boxhead" align=right>              <#if 0 < routingList?size>                             <#if 0 < viewIndex>                  <a href="<@ofbizUrl>/FindRouting?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutext">${uiLabelMap.CommonPrevious}</a>                <#else>                  <span class="submenutextdisabled">${uiLabelMap.CommonPrevious}</span>                </#if>                <#if 0 < listSize>                  <span class="submenutextinfo">${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${listSize}</span>                </#if>                <#if highIndex < listSize>                  <a href="<@ofbizUrl>/FindRouting?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}&hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonNext}</a>                <#else>                  <span class="submenutextrightdisabled">${uiLabelMap.CommonNext}</span>                </#if>              </#if>              &nbsp;            </div>          </td>        </tr>      </table>      <table width="100%" border="0" cellspacing="0" cellpadding="2" class="boxbottom">        <tr>          <td width="10%" align="left"><div class="tableheadtext">${uiLabelMap.ManufacturingRoutingId}</div></td>          <td width="15%" align="left"><div class="tableheadtext">${uiLabelMap.ManufacturingRoutingName}</div></td>          <td width="20%" align="left"><div class="tableheadtext">${uiLabelMap.CommonDescription}</div></td>          <td width="10%" align="left"><div class="tableheadtext">${uiLabelMap.ManufacturingQuantityMinimum}</div></td>          <td width="35%" align="right"><div class="tableheadtext">&nbsp;</div></td>          <td width="10%" align="right"><div class="tableheadtext">&nbsp;</div></td>        </tr>        <tr>          <td colspan="6"><hr class="sepbar"></td>        </tr>        <#if routingList?has_content>          <#assign rowClass = "viewManyTR2">          <#list routingList[lowIndex..highIndex-1] as routing>                        <tr class="${rowClass}">              <td>                  <a href="<@ofbizUrl>/FindRouting?workEffortId=${routing.workEffortId}&hideFields=Y${paramList}</@ofbizUrl>" class="buttontext">${routing.workEffortId}</a>              </td>              <td>                  <a href="<@ofbizUrl>/FindRouting?workEffortId=${routing.workEffortId}&hideFields=Y${paramList}</@ofbizUrl>" class="buttontext">${routing.workEffortName}</a>              </td>              <td>${routing.description?default("&nbsp;")}</td>              <td>${routing.quantityToProduce?default("&nbsp;")}</td>              <td align="right"><a href="<@ofbizUrl>/EditRoutingProductLink?workEffortId=${routing.workEffortId}</@ofbizUrl>" class="buttontext">${uiLabelMap.ManufacturingEditRoutingProductLink}</a></td>              <td align="right"><a href="<@ofbizUrl>/EditRoutingTaskAssoc?workEffortIdFrom=${routing.workEffortId}</@ofbizUrl>" class="buttontext">${uiLabelMap.ManufacturingEditRoutingTaskAssoc}</a></td>            </tr>            <#-- toggle the row color -->            <#if rowClass == "viewManyTR2">              <#assign rowClass = "viewManyTR1">            <#else>              <#assign rowClass = "viewManyTR2">            </#if>          </#list>                  <#else>          <tr>            <td colspan="5"><div class="head3">${uiLabelMap.CommonNoElementFound}.</div></td>          </tr>                </#if>        <#if lookupErrorMessage?exists>          <tr>            <td colspan="4"><div class="head3">${lookupErrorMessage}</div></td>          </tr>        </#if>      </table>    </td>  </tr></table></#if> 