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

${pages.get("/routing/CalendarTabBar.ftl")}

<#if techDataCalendar?has_content>
  <div class="head1">${uiLabelMap.ManufacturingUpdateCalendar} </div>
  <form name="calendarform" method="post" action="<@ofbizUrl>/UpdateCalendar</@ofbizUrl>">
    <input type="hidden" name="calendarId" value="${techDataCalendar.calendarId}">
<#else>
  <div class="head1">${uiLabelMap.ManufacturingCreateCalendar}</div>
  <form name="calendarform" method="post" action="<@ofbizUrl>/CreateCalendar</@ofbizUrl>">
</#if>

  <br>
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <#if techDataCalendar?has_content>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.ManufacturingCalendarId}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%" valign="top"><div class="tabletext"><b>${techDataCalendar.calendarId?if_exists}</b> (${uiLabelMap.CommonNotModifRecreat})</td>
    </tr>
    <#else>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.ManufacturingCalendarId}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="12" name="calendarId" value="${calendarData.calendarId?if_exists}"></td>
    </tr>
    </#if>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.CommonDescription}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="text" class="inputBox" size="40" name="description" value="${calendarData.description?if_exists}"></td>
    </tr>
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.ManufacturingCalendarWeekId}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
         <select class="selectBox" name="calendarWeekId">
          <#list calendarWeeks as calendarWeek>
          <option value="${calendarWeek.calendarWeekId}" <#if calendarData?has_content && calendarData.calendarWeekId?default("") == calendarWeek.calendarWeekId>SELECTED</#if>>${(calendarWeek.get("description", locale))?if_exists}</option>
          </#list>
        </select>
    </tr>
    <tr>
      <td width="26%" align="right" valign="top">
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="submit" value="${uiLabelMap.CommonUpdate}" class="smallSubmit"></td>
    </tr>
  </table>
</form>
<table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      	<td width='100%' align='right' valign='top'>
			<a href="<@ofbizUrl>/EditCalendar</@ofbizUrl>" class="buttontext">[${uiLabelMap.ManufacturingNewCalendar}]</a>
		</td>
	</tr>
 </table>
	