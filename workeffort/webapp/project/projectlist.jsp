<%--
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
 *@author     Dustin Caldwell (from code by David Jones)
 *@created    Aug 13, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%@ page import="org.ofbiz.commonapp.workeffort.workeffort.*" %>
<%@ page import="org.ofbiz.commonapp.workeffort.project.*" %>

<%if(request.getParameter("ShowAllProjects") != null) {
  ProjectWorker.getAllAssignedProjects(pageContext, "projects");
} else {
  ProjectWorker.getAssignedProjects(pageContext, "projects");
}%>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left>
            <div class='boxhead'>&nbsp;Projects</div>
          </TD>
          <TD align=right>
            <table><tr>
              <TD>
                <%if(request.getParameter("ShowAllProjects") != null) {%>
                  <A href='<ofbiz:url>/projectlist</ofbiz:url>' class='lightbuttontext'>[Show&nbsp;Active]</A>
                <%} else {%>
                  <A href='<ofbiz:url>/projectlist?ShowAllProjects=true</ofbiz:url>' class='lightbuttontext'>[Show&nbsp;All]</A>
                <%}%>
              </TD>
              <TD align=right>
                <A href='<ofbiz:url>/editproject</ofbiz:url>' class='lightbuttontext'>[New&nbsp;Project]</A>
              </TD>
            </tr></table>
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
<!--              <div class='head3'>Assigned Tasks</div>-->
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <TD><DIV class='tabletext'><b>Name</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Start Date/Time</b></DIV></TD>
<%--                  <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>--%>
                  <TD><DIV class='tabletext'><b>Status</b></DIV></TD>
                  <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                </TR>
                <TR><TD colspan='5'><HR class='sepbar'></TD></TR>
                <ofbiz:iterator name="workEffort" property="projects">
                  <TR>
                    <TD><A class='buttontext' href='<ofbiz:url>/phaselist?projectWorkEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                        <ofbiz:entityfield attribute="workEffort" field="workEffortName"/></a></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="estimatedStartDate"/></DIV></TD>
<%--                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="priority"/></DIV></TD>--%>
                    <%GenericValue currentStatusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId")));%>
                    <%if (currentStatusItem != null) pageContext.setAttribute("currentStatusItem", currentStatusItem);%>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="currentStatusItem" field="description"/></DIV></TD>
                    <TD align=right width='1%'><A class='buttontext' href='<ofbiz:url>/editproject?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                        [Edit]</a></DIV></TD>
                  </TR>
                </ofbiz:iterator>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
