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
<%WorkEffortWorker.getWorkEffortAssignedActivitiesByRole(pageContext, "roleActivities");%>
<%WorkEffortWorker.getWorkEffortAssignedActivitiesByGroup(pageContext, "groupActivities");%>

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
  <ofbiz:if name="activities" size="0">
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
            <tr>
              <td>
                  <div class='head3'>Workflow Activities Assigned to User</div>
                  <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                    <TR>
                      <TD><DIV class='tabletext'><b>Start&nbsp;Date/Time</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Status</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>My&nbsp;Status</b></DIV></TD>
                      <%-- <TD><DIV class='tabletext'><b>Party&nbsp;ID</b></DIV></TD> --%>
                      <TD><DIV class='tabletext'><b>Role&nbsp;ID</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Name</b></DIV></TD>
                      <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                    </TR>
                    <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
                    <ofbiz:iterator name="workEffort" property="activities">
                      <TR>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="estimatedStartDate"/></DIV></TD>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="priority"/></DIV></TD>
                        <%GenericValue currentStatusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId")));%>
                        <%if (currentStatusItem != null) pageContext.setAttribute("currentStatusItem", currentStatusItem);%>
                        <%GenericValue statusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("statusId")));%>
                        <%if (statusItem != null) pageContext.setAttribute("statusItem", statusItem);%>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="currentStatusItem" field="description"/></DIV></TD>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="statusItem" field="description"/></DIV></TD>
                        <%-- <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="partyId"/></DIV></TD> --%>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="roleTypeId"/></DIV></TD>
                        <TD><A class='buttontext' href='<ofbiz:url>/activity?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                            <ofbiz:entityfield attribute="workEffort" field="workEffortName"/></a></DIV></TD>
                        <TD align=right><A class='buttontext' href='<ofbiz:url>/activity?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                            Edit&nbsp;[<ofbiz:entityfield attribute="workEffort" field="workEffortId"/>]</a></DIV></TD>
                      </TR>
                    </ofbiz:iterator>
                  </TABLE>
              </td>
            </tr>
          </table>
        </TD>
      </TR>
  </ofbiz:if>
  <ofbiz:if name="roleActivities" size="0">
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
            <tr>
              <td>
                  <div class='head3'>Workflow Activities Assigned to User Role</div>
                  <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                    <TR>
                      <TD><DIV class='tabletext'><b>Start&nbsp;Date/Time</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Status</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>My&nbsp;Status</b></DIV></TD>
                      <%-- <TD><DIV class='tabletext'><b>Party&nbsp;ID</b></DIV></TD> --%>
                      <TD><DIV class='tabletext'><b>Role&nbsp;ID</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Name</b></DIV></TD>
                      <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                    </TR>
                    <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
                    <ofbiz:iterator name="workEffort" property="roleActivities">
                      <TR>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="estimatedStartDate"/></DIV></TD>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="priority"/></DIV></TD>
                        <%GenericValue currentStatusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId")));%>
                        <%if (currentStatusItem != null) pageContext.setAttribute("currentStatusItem", currentStatusItem);%>
                        <%GenericValue statusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("statusId")));%>
                        <%if (statusItem != null) pageContext.setAttribute("statusItem", statusItem);%>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="currentStatusItem" field="description"/></DIV></TD>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="statusItem" field="description"/></DIV></TD>
                        <%-- <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="partyId"/></DIV></TD> --%>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="roleTypeId"/></DIV></TD>
                        <TD><A class='buttontext' href='<ofbiz:url>/activity?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                            <ofbiz:entityfield attribute="workEffort" field="workEffortName"/></a></DIV></TD>
                        <TD align=right><A class='buttontext' href="<ofbiz:url>/acceptassignment?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/>&partyId=<ofbiz:entityfield field="partyId" attribute="workEffort"/>&roleTypeId=<ofbiz:entityfield field="roleTypeId" attribute="workEffort"/>&fromDate=<ofbiz:inputvalue field="fromDate" param="fromDate" entityAttr="workEffort" tryEntityAttr="assignTryEntity"/></ofbiz:url>">
                            Accept&nbsp;Assignment&nbsp;[<ofbiz:entityfield attribute="workEffort" field="workEffortId"/>]</a></DIV></TD>
                      </TR>
                    </ofbiz:iterator>
                  </TABLE>
              </td>
            </tr>
          </table>
        </TD>
      </TR>
  </ofbiz:if>
  <ofbiz:if name="groupActivities" size="0">
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
            <tr>
              <td>
                  <div class='head3'>Workflow Activities Assigned to User Group</div>
                  <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                    <TR>
                      <TD><DIV class='tabletext'><b>Start&nbsp;Date/Time</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Status</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>My&nbsp;Status</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Group&nbsp;Party&nbsp;ID</b></DIV></TD>
                      <%-- <TD><DIV class='tabletext'><b>Role&nbsp;ID</b></DIV></TD> --%>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Name</b></DIV></TD>
                      <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                    </TR>
                    <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
                    <ofbiz:iterator name="workEffort" property="groupActivities">
                      <TR>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="estimatedStartDate"/></DIV></TD>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="priority"/></DIV></TD>
                        <%GenericValue currentStatusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId")));%>
                        <%if (currentStatusItem != null) pageContext.setAttribute("currentStatusItem", currentStatusItem);%>
                        <%GenericValue statusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("statusId")));%>
                        <%if (statusItem != null) pageContext.setAttribute("statusItem", statusItem);%>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="currentStatusItem" field="description"/></DIV></TD>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="statusItem" field="description"/></DIV></TD>
                        <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="groupPartyId"/></DIV></TD>
                        <%-- <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="roleTypeId"/></DIV></TD> --%>
                        <TD><A class='buttontext' href='<ofbiz:url>/activity?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                            <ofbiz:entityfield attribute="workEffort" field="workEffortName"/></a></DIV></TD>
                        <TD align=right><A class='buttontext' href='<ofbiz:url>/acceptassignment?
                                workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/>&
                                partyId=<ofbiz:entityfield field="partyId" attribute="workEffort"/>&
                                roleTypeId=<ofbiz:entityfield field="roleTypeId" attribute="workEffort"/>&
                                fromDate=<ofbiz:inputvalue field="fromDate" param="fromDate" entityAttr="workEffort" tryEntityAttr="assignTryEntity"/></ofbiz:url>'>
                            Accept&nbsp;Assignment&nbsp;[<ofbiz:entityfield attribute="workEffort" field="workEffortId"/>]</a></DIV></TD>
                      </TR>
                    </ofbiz:iterator>
                  </TABLE>
              </td>
            </tr>
          </table>
        </TD>
      </TR>
  </ofbiz:if>
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
                    <%GenericValue currentStatusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId")));%>
                    <%if (currentStatusItem != null) pageContext.setAttribute("currentStatusItem", currentStatusItem);%>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="currentStatusItem" field="description"/></DIV></TD>
                    <TD><A class='buttontext' href='<ofbiz:url>/task?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                        <ofbiz:entityfield attribute="workEffort" field="workEffortName"/></a></DIV></TD>
                    <TD align=right width='1%'><A class='buttontext' href='<ofbiz:url>/activity?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
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

