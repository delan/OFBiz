<%
/**
 *  Title: Calendar Upcoming View Page
 *  Description: None
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones
 *@created    May 22 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.commonapp.workeffort.workeffort.*" %>
<%WorkEffortWorker.getWorkEffortEventsByDays(pageContext, "days", UtilDateTime.nowTimestamp(), 7);%>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Calendar Up-Coming Events View</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<ofbiz:url>/day</ofbiz:url>' class='lightbuttontext'>[Day&nbsp;View]</A>
            <A href='<ofbiz:url>/week</ofbiz:url>' class='lightbuttontext'>[Week&nbsp;View]</A>
            <A href='<ofbiz:url>/month</ofbiz:url>' class='lightbuttontext'>[Month&nbsp;View]</A>
            <A href='<ofbiz:url>/upcoming</ofbiz:url>' class='lightbuttontextdisabled'>[Upcoming&nbsp;Events]</A>
            <A href='<ofbiz:url>/event</ofbiz:url>' class='lightbuttontext'>[New&nbsp;Event]</A>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <ofbiz:if name="days" size="0">
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <TD><DIV class='tabletext'><b>Start Date/Time</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>End Date/Time</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Event Name</b></DIV></TD>
                </TR>
                <ofbiz:iterator name="workEfforts" property="days" type="java.util.List">
                  <TR><TD colspan='3'><HR class='sepbar'></TD></TR>
                  <ofbiz:iterator name="workEffort" property="workEfforts">
                    <TR>
                      <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="estimatedStartDate"/></DIV></TD>
                      <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="estimatedCompletionDate"/></DIV></TD>
                      <TD><A class='buttontext' href='<ofbiz:url>/event?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                          <ofbiz:entityfield attribute="workEffort" field="workEffortName"/></a></DIV></TD>
                    </TR>
                  </ofbiz:iterator>
                  <%-- <ofbiz:iteratorHasNext><TR><TD colspan='3'><HR></TD></TR></ofbiz:iteratorHasNext> --%>
                </ofbiz:iterator>
              </TABLE>
            </ofbiz:if>
            <ofbiz:unless name="days" size="0">
              <div class='tabletext'>No events found.</div>
            </ofbiz:unless>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
