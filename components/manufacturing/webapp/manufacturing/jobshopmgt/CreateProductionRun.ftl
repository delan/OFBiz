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
 *@version    $Revision: 1.1 $
 *@since      3.0
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
${pages.get("/jobshopmgt/ProductionRunTabBar.ftl")}

  <div class="head1">${uiLabelMap.ManufacturingCreateProductionRun}</div>
  <form name="productionRunform" method="post" action="<@ofbizUrl>/CreateProductionRunGo</@ofbizUrl>">

  <br>
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.ProductProductId}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="16" name="productId" value="${productionRunData.productId?if_exists}">
					<a href="javascript:call_fieldlookup(document.productionRunform.productId,'<@ofbizUrl>/LookupProduct</@ofbizUrl>', 'none',640,460);"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'></a>
	  </td>			
    </tr>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.ManufacturingQuantity}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="6" name="pRQuantity" value="${productionRunData.pRQuantity?if_exists}"></td>
    </tr>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.ManufacturingStartDate}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="19" name="startDate" value="${productionRunData.startDate?if_exists}">
         			<a href="javascript:call_cal(document.productionRunform.startDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Click here For Calendar"></a>
       </td>			
    </tr>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.ManufacturingRoutingId}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="16" name="routingId" value="${productionRunData.routingId?if_exists}">
					<a href="javascript:call_fieldlookup(document.productionRunform.routingId,'<@ofbizUrl>/LookupRouting</@ofbizUrl>', 'none',560,460);"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'></a>
	   </td>				
    </tr>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.ManufacturingProductionRunName}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="30" name="workEffortName" value="${productionRunData.workEffortName?if_exists}"></td>
    </tr>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tableheadtext">${uiLabelMap.CommonDescription}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="50" name="description" value="${productionRunData.description?if_exists}"></td>
    </tr>
    <tr>
      <td width="26%" align="right" valign="top">
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="submit" value="${uiLabelMap.CommonSubmit}" class="smallSubmit"></td>
    </tr>
  </table>
</form>


	