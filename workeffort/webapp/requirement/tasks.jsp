<%--
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski
 *@created    July 29, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
    String requirementId = request.getParameter("requirementId");
    if (requirementId == null)
        requirementId = (String) request.getSession().getAttribute("requirementId");

    if (requirementId != null) {
        request.getSession().setAttribute("requirementId", requirementId);
        pageContext.setAttribute("requirementId", requirementId);
        Collection tasks = delegator.findByAnd("WorkEffortAndFulfillment", UtilMisc.toMap("requirementId", requirementId));
        if (tasks.size() > 0) pageContext.setAttribute("tasks", tasks);
    }
%>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Task List <span class="tabletext">For Requirement: <a href="<ofbiz:url>/requirement?requirementId=<%=requirementId%></ofbiz:url>" class="lightbuttontext">[<%=requirementId%>]</a></span></div>
          </TD>
          <TD align=right width='60%'>
            <A href='<ofbiz:url>/requirementlist</ofbiz:url>' class='lightbuttontext'>[Requirement&nbsp;List]</A>
            <A href='<ofbiz:url>/requirement</ofbiz:url>' class='lightbuttontext'>[New&nbsp;Requirement]</A>
            <A href='<ofbiz:url>/task?requirementId=<%=requirementId%></ofbiz:url>' class='lightbuttontext'>[Add&nbsp;Task]</A>
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
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <TD><DIV class='tabletext'><b>Start Date/Time</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Status</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Task Name</b></DIV></TD>
                  <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                </TR>
                <TR><TD colspan='5'><HR class='sepbar'></TD></TR>
                <ofbiz:unless name="tasks">
                  <tr><td><div class="tabletext">No tasks currently associated with this requirement.</div></td></tr>
                </ofbiz:unless>
                <ofbiz:iterator name="workEffort" property="tasks">
                  <TR>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="estimatedStartDate"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="priority"/></DIV></TD>
                    <%GenericValue currentStatusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId")));%>
                    <%if (currentStatusItem != null) pageContext.setAttribute("currentStatusItem", currentStatusItem);%>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="currentStatusItem" field="description"/></DIV></TD>
                    <TD><A class='buttontext' href='<ofbiz:url>/task?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                        <ofbiz:entityfield attribute="workEffort" field="workEffortName"/></a></DIV></TD>
                    <TD align=right width='1%'><A class='buttontext' href='<ofbiz:url>/task?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                        Edit&nbsp;[<ofbiz:entityfield attribute="workEffort" field="workEffortId"/>]</a></DIV></TD>
                  </TR>
                </ofbiz:iterator>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR><TD colspan='5'><HR class='sepbar'></TD></TR>
  <TR>
    <TD>
      <table width="50%" cellpadding="2" cellspacing="0" border="0">
        <tr valign="middle"><td>
          <form method="post" action="<ofbiz:url>/assoctask</ofbiz:url>">
            <input type="hidden" name="requirementId" value="<%=requirementId%>">
            <span class="tabletext">Add an existing task:&nbsp;(WorkEffortId)&nbsp;</span>
            <input type="text" name="workEffortId" size="10" style="font-size: small;">
            <input type="submit" style="font-size: small;" value="Add Task">
          </form>
        </td></tr>
      </table>
    </TD>
  </TR>
</TABLE>
