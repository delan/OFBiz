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
<#assign locale = requestAttributes.locale>

${pages.get("/mrp/MrpTabBar.ftl")}

  <div class="head1">${uiLabelMap.ManufacturingRunningMrp}</div>
  <form name="runningMrpform" method="post" action="<@ofbizUrl>/RunningMrpGo</@ofbizUrl>">

  <br>
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width='26%' align='right' valign='top'><div class="tabletext">${uiLabelMap.ManufacturingTimePeriod}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
           <select class="selectBox" name="timePeriod">
           <option value="${uiLabelMap.CommonWeek}" SELECTED>${uiLabelMap.CommonWeek}</option>
           <option value="${uiLabelMap.CommonDay}">${uiLabelMap.CommonDay}</option>
           <option value="${uiLabelMap.CommonHour}">${uiLabelMap.CommonHour}</option>
        </select>
    </tr>
    <tr>
      <td width="26%" align="right" valign="top">
      <td width="5">&nbsp;</td>
      <td width="74%"><input type="submit" value="${uiLabelMap.CommonSubmit}" class="smallSubmit"></td>
    </tr>
  </table>
</form>
	