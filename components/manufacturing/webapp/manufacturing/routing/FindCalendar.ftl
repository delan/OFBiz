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
 *@author     Olivier Heintz (olivier.heintz@nereide.biz)
 *@version    $Rev:$
 *@since      3.0
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<div class='tabContainer'>
        <a href="<@ofbizUrl>/FindCalendar</@ofbizUrl>" class="tabButtonSelected">${uiLabelMap.ManufacturingCalendar}</a>
        <a href="<@ofbizUrl>/ListCalendarWeek</@ofbizUrl>" class="tabButton">${uiLabelMap.ManufacturingCalendarWeek}</a>
</div>

<div><a href="<@ofbizUrl>/EditCalendar</@ofbizUrl>" class="buttontext">[${uiLabelMap.ManufacturingNewCalendar}]</a></div>

<br>
<table width="100%" border="0" cellpadding="0" cellspacing="0"> 
  <tr>
    <td><div class="tableheadtext">${uiLabelMap.ManufacturingCalendarId}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.CommonDescription}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.ManufacturingCalendarWeekId}</div></td>
    <td>&nbsp;</td>
  </tr>  
  <tr><td colspan="4"><hr class="sepbar"></td></tr>    
  <#if techDataCalendars?has_content>
    <#list techDataCalendars as techDataCalendar>
      <tr>
        <td><div class="tabletext">${techDataCalendar.calendarId}</div></td>
        <td><div class="tabletext">${techDataCalendar.description?if_exists}</div></td>
        <td><div class="tabletext">${techDataCalendar.calendarWeekId?if_exists}</div></td>
        <td align="right">
          <a href="<@ofbizUrl>/EditCalendar?calendarId=${techDataCalendar.calendarId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonEdit}]</a>
          <a href="<@ofbizUrl>/RemoveCalendar?calendarId=${techDataCalendar.calendarId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonDelete}]</a>
        </td>        
      </tr>
    </#list>
  <#else>
    <tr>
      <td colspan='4'><div class="tabletext">${uiLabelMap.ManufacturingNoCalendarFound}</div></td>
    </tr>    
  </#if>
</table>
