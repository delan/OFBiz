<%
/**
 *  Title: Task List Page
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

<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>

<%@ page import="org.ofbiz.commonapp.workeffort.workeffort.*" %>
<%WorkEffortWorker.getWorkEffortAssignedTasks(pageContext, "tasks");%>
<%WorkEffortWorker.getWorkEffortAssignedActivities(pageContext, "activities");%>

<% pageContext.setAttribute("PageName", "Task List Page"); %> 
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<BR>
<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Current Task List</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<ofbiz:url>/tasklist</ofbiz:url>' class='lightbuttontextdisabled'>[Task&nbsp;List]</A>
            <A href='<ofbiz:url>/task</ofbiz:url>' class='lightbuttontext'>[New&nbsp;Task]</A>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
              <div class='head3'>Assigned Workflow Activities</div>
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <TD><DIV class='tabletext'><b>Start Date/Time</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Activity Status</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>My Status</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Activity Name</b></DIV></TD>
                  <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                </TR>
                <TR><TD colspan='6'><HR class='sepbar'></TD></TR>
                <ofbiz:iterator name="workEffort" property="activities">
                  <TR>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="estimatedStartDate"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="priority"/></DIV></TD>
                    <%GenericValue status;%>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="currentStatusId"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="statusId"/></DIV></TD>
                    <TD><A class='buttontext' href='<ofbiz:url>/activity?WORK_EFFORT_ID=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                        <ofbiz:entityfield attribute="workEffort" field="workEffortName"/></a></DIV></TD>
                    <TD align=right><A class='buttontext' href='<ofbiz:url>/activity?WORK_EFFORT_ID=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                        Edit&nbsp;[<ofbiz:entityfield attribute="workEffort" field="workEffortId"/>]</a></DIV></TD>
                  </TR>
                </ofbiz:iterator>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
              <div class='head3'>Assigned Tasks</div>
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <TD><DIV class='tabletext'><b>Start Date/Time</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Status</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Task Name</b></DIV></TD>
                  <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                </TR>
                <TR><TD colspan='5'><HR class='sepbar'></TD></TR>
                <ofbiz:iterator name="workEffort" property="tasks">
                  <TR>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="estimatedStartDate"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="priority"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="currentStatusId"/></DIV></TD>
                    <TD><A class='buttontext' href='<ofbiz:url>/task?WORK_EFFORT_ID=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                        <ofbiz:entityfield attribute="workEffort" field="workEffortName"/></a></DIV></TD>
                    <TD align=right width='1%'><A class='buttontext' href='<ofbiz:url>/activity?WORK_EFFORT_ID=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                        Edit&nbsp;[<ofbiz:entityfield attribute="workEffort" field="workEffortId"/>]</a></DIV></TD>
                  </TR>
                </ofbiz:iterator>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

