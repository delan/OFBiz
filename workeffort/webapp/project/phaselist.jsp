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

<%ProjectWorker.getAllProjectPhases(pageContext, "phases");%>
<%
  String projectWorkEffortId = request.getParameter("projectWorkEffortId");
  GenericValue projectWorkEffortStatus = null;
  GenericValue projectWorkEffort = delegator.findByPrimaryKey("WorkEffort", 
    UtilMisc.toMap("workEffortId", projectWorkEffortId));
  if(projectWorkEffort != null) {
    projectWorkEffortStatus = projectWorkEffort.getRelatedOne("CurrentStatusItem");
  }
  pageContext.setAttribute("projectWorkEffort", projectWorkEffort);
  pageContext.setAttribute("projectWorkEffortStatus", projectWorkEffortStatus);
%>
<%request.setAttribute("workEffortId", projectWorkEffortId);%>

<%WorkEffortWorker.getWorkEffort(pageContext, "workEffortId", "workEffort", "partyAssigns", "canView", "tryEntity", "currentStatusItem");%>

<BR>

<TABLE border=0 cellspacing='0' cellpadding='0' class='boxoutside' width='45%'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left>
            <div class='boxhead'>&nbsp;<b>Project:</b>&nbsp;<ofbiz:entityfield attribute="projectWorkEffort" field="workEffortName"/></div>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
                <tr>
                  <td align=right valign=top><div class='tabletext'><nobr>Project Status:</nobr></div></td>
                  <td>&nbsp;</td>
                  <td valign=top><ofbiz:entityfield field="description" attribute="projectWorkEffortStatus"/>
                  </td>
                </tr>
                <tr>
                  <td align=right valign=top><div class='tabletext'><nobr>Description:</nobr></div></td>
                  <td>&nbsp;</td>
                  <td valign=top><ofbiz:inputvalue field="description" param="description" entityAttr="projectWorkEffort" tryEntityAttr="tryEntity"/>
                </tr>
                <tr>
                  <td align=right valign=top><div class='tabletext'><nobr>Start Date/Time:</nobr></div></td>
                  <td>&nbsp;</td>
                  <td valign=top><ofbiz:inputvalue field="estimatedStartDate" param="estimatedStartDate" entityAttr="projectWorkEffort" tryEntityAttr="tryEntity"/>
                  </td>
                </tr>
                <tr>
                  <td align=right valign=top><div class='tabletext'><nobr>End Date/Time:</nobr></div></td>
                  <td>&nbsp;</td>
                  <td valign=top><ofbiz:inputvalue field="estimatedCompletionDate" param="estimatedCompletionDate" entityAttr="projectWorkEffort" tryEntityAttr="tryEntity"/>
                  </td>
                </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Project Phases</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<ofbiz:url>/editphase?projectWorkEffortId=<%=projectWorkEffortId%></ofbiz:url>' class='lightbuttontext'>[New&nbsp;Phase]</A>
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
                <ofbiz:iterator name="workEffort" property="phases">
                  <TR>
                    <TD><A class='buttontext' href='<ofbiz:url>/phasetasklist?phaseWorkEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/></ofbiz:url>'>
                        <ofbiz:entityfield attribute="workEffort" field="workEffortName"/></a></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="estimatedStartDate"/></DIV></TD>
<%--                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="priority"/></DIV></TD>--%>
                    <%GenericValue currentStatusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId")));%>
                    <%if (currentStatusItem != null) pageContext.setAttribute("currentStatusItem", currentStatusItem);%>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="currentStatusItem" field="description"/></DIV></TD>
                    <TD align=right width='1%'><A class='buttontext' href='<ofbiz:url>/editphase?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/>&projectWorkEffortId=<%=projectWorkEffortId%></ofbiz:url>'>
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
