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
    <div class="tabContainer">
        <a href="<@ofbizUrl>/ListCalendarWeek</@ofbizUrl>" class="tabButton">${uiLabelMap.CommonBackToList}</a>
        <a href="<@ofbizUrl>/EditCalendarWeek</@ofbizUrl>" class="tabButton">${uiLabelMap.ManufacturingNewCalendarWeek}</a>
	</div>

		<#if calendarWeek?has_content>
  			<div class="head1">${uiLabelMap.ManufacturingUpdateCalendarWeek} </div>
			<br>
  			<form name="calendarWeekform" method="post" action="<@ofbizUrl>/UpdateCalendarWeek</@ofbizUrl>">
			<table width="90%" border="0" cellpadding="2" cellspacing="0">
			  <tr>
		      	<td width="26%" align="right" valign="top" ><div class="tabletext">${uiLabelMap.ManufacturingCalendarWeekId}</div></td>
      			<td width="5">&nbsp;</td>
    			<input type="hidden" name="calendarWeekId" value="${calendarWeek.calendarWeekId}">
      			<td width="74%" valign="top" colspan="5"><div class="tabletext"><b>${calendarWeek.calendarWeekId?if_exists}</b> (${uiLabelMap.CommonNotModifRecreat})</td>
		<#else>
  			<div class="head1">${uiLabelMap.ManufacturingCreateCalendarWeek} </div>
			<br>
  			<form name="calendarWeekform" method="post" action="<@ofbizUrl>/CreateCalendarWeek</@ofbizUrl>">
			<table width="90%" border="0" cellpadding="2" cellspacing="0">
			  <tr>
		      	<td width="26%" align="right" valign="top"><div class="tabletext">${uiLabelMap.ManufacturingCalendarWeekId}</div></td>
      			<td width="5">&nbsp;</td>
      			<td width="74%" colspan="5"><input type="text" class="inputBox" size="12" name="calendarWeekId" value="${calendarWeekData.calendarWeekId?if_exists}"></td>
		</#if>
			</tr>
			<tr>
      			<td width="26%" align="right" valign="top"><div class="tabletext">${uiLabelMap.CommonDescription}</div></td>
      			<td width="5">&nbsp;</td>
      			<td width="74%" colspan="5"><input type="text" class="inputBox" size="30" name="description" value="${calendarWeekData.description?if_exists}"></td>
    		</tr>
			<tr>
      			<td width="26%" align="right" valign="top"><div class="tabletext"><b>${uiLabelMap.CommonMonday}</b>&nbsp;${uiLabelMap.ManufacturingStartTime}</div></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%"><input type="text" class="inputBox" size="12" name="mondayStartTime" value="${calendarWeekData.mondayStartTime?if_exists}"></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%" align="right" valign="top"><div class="tabletext">${uiLabelMap.ManufacturingCalendarCapacity}</div></td>
      			<td width="1%">&nbsp;</td>
      			<td width="40%"><input type="text" class="inputBox" size="8" name="mondayCapacity" value="${calendarWeekData.mondayCapacity?if_exists}"></td>
    		</tr>
 			<tr>
      			<td width="26%" align="right" valign="top"><div class="tabletext"><b>${uiLabelMap.CommonTuesday}</b>&nbsp;${uiLabelMap.ManufacturingStartTime}</div></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%"><input type="text" class="inputBox" size="12" name="tuesdayStartTime" value="${calendarWeekData.tuesdayStartTime?if_exists}"></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%" align="right" valign="top"><div class="tabletext">${uiLabelMap.ManufacturingCalendarCapacity}</div></td>
      			<td width="1%">&nbsp;</td>
      			<td width="40%"><input type="text" class="inputBox" size="8" name="tuesdayCapacity" value="${calendarWeekData.tuesdayCapacity?if_exists}"></td>
    		</tr>
 			<tr>
      			<td width="26%" align="right" valign="top"><div class="tabletext"><b>${uiLabelMap.CommonWednesday}</b>&nbsp;${uiLabelMap.ManufacturingStartTime}</div></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%"><input type="text" class="inputBox" size="12" name="wednesdayStartTime" value="${calendarWeekData.wednesdayStartTime?if_exists}"></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%" align="right" valign="top"><div class="tabletext">${uiLabelMap.ManufacturingCalendarCapacity}</div></td>
      			<td width="1%">&nbsp;</td>
      			<td width="40%"><input type="text" class="inputBox" size="8" name="wednesdayCapacity" value="${calendarWeekData.wednesdayCapacity?if_exists}"></td>
    		</tr>
 			<tr>
      			<td width="26%" align="right" valign="top"><div class="tabletext"><b>${uiLabelMap.CommonThursday}</b>&nbsp;${uiLabelMap.ManufacturingStartTime}</div></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%"><input type="text" class="inputBox" size="12" name="thursdayStartTime" value="${calendarWeekData.thursdayStartTime?if_exists}"></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%" align="right" valign="top"><div class="tabletext">${uiLabelMap.ManufacturingCalendarCapacity}</div></td>
      			<td width="1%">&nbsp;</td>
      			<td width="40%"><input type="text" class="inputBox" size="8" name="thursdayCapacity" value="${calendarWeekData.thursdayCapacity?if_exists}"></td>
    		</tr>
 			<tr>
      			<td width="26%" align="right" valign="top"><div class="tabletext"><b>${uiLabelMap.CommonFriday}</b>&nbsp;${uiLabelMap.ManufacturingStartTime}</div></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%"><input type="text" class="inputBox" size="12" name="fridayStartTime" value="${calendarWeekData.fridayStartTime?if_exists}"></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%" align="right" valign="top"><div class="tabletext">${uiLabelMap.ManufacturingCalendarCapacity}</div></td>
      			<td width="1%">&nbsp;</td>
      			<td width="40%"><input type="text" class="inputBox" size="8" name="fridayCapacity" value="${calendarWeekData.fridayCapacity?if_exists}"></td>
    		</tr>
 			<tr>
      			<td width="26%" align="right" valign="top"><div class="tabletext"><b>${uiLabelMap.CommonSaturday}</b>&nbsp;${uiLabelMap.ManufacturingStartTime}</div></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%"><input type="text" class="inputBox" size="12" name="saturdayStartTime" value="${calendarWeekData.saturdayStartTime?if_exists}"></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%" align="right" valign="top"><div class="tabletext">${uiLabelMap.ManufacturingCalendarCapacity}</div></td>
      			<td width="1%">&nbsp;</td>
      			<td width="40%"><input type="text" class="inputBox" size="8" name="saturdayCapacity" value="${calendarWeekData.saturdayCapacity?if_exists}"></td>
    		</tr>
 			<tr>
      			<td width="26%" align="right" valign="top"><div class="tabletext"><b>${uiLabelMap.CommonSunday}</b>&nbsp;${uiLabelMap.ManufacturingStartTime}</div></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%"><input type="text" class="inputBox" size="12" name="sundayStartTime" value="${calendarWeekData.sundayStartTime?if_exists}"></td>
      			<td width="5">&nbsp;</td>
      			<td width="1%" align="right" valign="top"><div class="tabletext">${uiLabelMap.ManufacturingCalendarCapacity}</div></td>
      			<td width="1%">&nbsp;</td>
      			<td width="40%"><input type="text" class="inputBox" size="8" name="sundayCapacity" value="${calendarWeekData.sundayCapacity?if_exists}"></td>
    		</tr>
    		<tr>
      			<td width="26%" align="right" valign="top">
      			<td width="5">&nbsp;</td>
      			<td width="74%" colspan="5"><input type="submit" value="${uiLabelMap.CommonUpdate}" class="smallSubmit"></td>
    </tr>
			
		</table>
	<br>

<#else>
 	<h3>${uiLabelMap.ManufacturingCalendarPermissionError}</h3>
</#if>

	