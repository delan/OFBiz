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

<%ProjectWorker.getAllPhaseTasks(pageContext, "tasks");%>
<%
  // get workeffort for the current phase
  String phaseWorkEffortId = request.getParameter("phaseWorkEffortId");
  String projectWorkEffortId = null;
  GenericValue phaseWorkEffortStatus = null;
  GenericValue phaseWorkEffort = delegator.findByPrimaryKey("WorkEffort", 
    UtilMisc.toMap("workEffortId", phaseWorkEffortId));
  if(phaseWorkEffort != null) {
    phaseWorkEffortStatus = phaseWorkEffort.getRelatedOne("CurrentStatusItem");

    // get workeffort for current project - assume that the first project related to this phase is it
    Collection projectAssocColl = phaseWorkEffort.getRelated("ToWorkEffortAssoc");
    if(projectAssocColl.size() > 0) {
      GenericValue projectWorkEffortStatus = null;
      GenericValue projectWorkEffort = ((GenericValue)projectAssocColl.iterator().next()).getRelatedOne("FromWorkEffort");
      if(projectWorkEffort != null) {
        projectWorkEffortStatus = projectWorkEffort.getRelatedOne("CurrentStatusItem");
        projectWorkEffortId = projectWorkEffort.getString("workEffortId");
      }
      pageContext.setAttribute("projectWorkEffort", projectWorkEffort);
      pageContext.setAttribute("projectWorkEffortStatus", projectWorkEffortStatus);
    }
  }
  pageContext.setAttribute("phaseWorkEffort", phaseWorkEffort);
  pageContext.setAttribute("phaseWorkEffortStatus", phaseWorkEffortStatus);

%>

<BR>
<table border=0 cellspacing='0' cellpadding='0'><tr>
  <td width='45%' valign=top>
    <TABLE border=0 cellspacing='0' cellpadding='0' class='boxoutside' width='100%'>
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
            <tr>
              <TD align=left width=>
                <div class='boxhead'>&nbsp;<b>Project:</b>&nbsp;<A class='boxhead' href='<ofbiz:url>/phaselist?projectWorkEffortId=<%=projectWorkEffortId%></ofbiz:url>'><ofbiz:entityfield attribute="projectWorkEffort" field="workEffortName"/></a></div>
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
                      <td valign=top><ofbiz:entityfield field="description" attribute="projectWorkEffortStatus"/></td>
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
  </td>
  <td width='10%' valign=top>&nbsp;</td>
  <td width='45%' valign=top>
    <TABLE border=0 cellspacing='0' cellpadding='0' class='boxoutside' width='100%'>
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
            <tr>
              <TD align=left>
                <div class='boxhead'>&nbsp;<b>Phase:</b>&nbsp;<ofbiz:entityfield attribute="phaseWorkEffort" field="workEffortName"/></div>
              </TD>
            </tr>
          </table>
        </TD>
      </TR>
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>Phase Status:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top><ofbiz:entityfield field="description" attribute="phaseWorkEffortStatus"/></td>
                    </tr>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>Description:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top><ofbiz:inputvalue field="description" param="description" entityAttr="phaseWorkEffort" tryEntityAttr="tryEntity"/>
                    </tr>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>Start Date/Time:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top><ofbiz:inputvalue field="estimatedStartDate" param="estimatedStartDate" entityAttr="phaseWorkEffort" tryEntityAttr="tryEntity"/>
                      </td>
                    </tr>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>End Date/Time:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top><ofbiz:inputvalue field="estimatedCompletionDate" param="estimatedCompletionDate" entityAttr="phaseWorkEffort" tryEntityAttr="tryEntity"/>
                      </td>
                    </tr>
          </table>
        </TD>
      </TR>
    </TABLE>
  </td>
</tr></table>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Phase Tasks</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<ofbiz:url>/editphasetask?phaseWorkEffortId=<%=phaseWorkEffortId%></ofbiz:url>' class='lightbuttontext'>[New&nbsp;Task]</A>
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
                  <TD><DIV class='tabletext'><b>Description</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Start Date/Time</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Status</b></DIV></TD>
                  <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                </TR>
                <TR><TD colspan='6'><HR class='sepbar'></TD></TR>
                <ofbiz:iterator name="workEffort" property="tasks">
                  <TR>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="workEffortName"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="description"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="estimatedStartDate"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="workEffort" field="priority"/></DIV></TD>
                    <%GenericValue currentStatusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId")));%>
                    <%if (currentStatusItem != null) pageContext.setAttribute("currentStatusItem", currentStatusItem);%>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="currentStatusItem" field="description"/></DIV></TD>
                    <TD align=right width='1%'><A class='buttontext' href='<ofbiz:url>/editphasetask?workEffortId=<ofbiz:entityfield attribute="workEffort" field="workEffortId"/>&phaseWorkEffortId=<%=phaseWorkEffortId%></ofbiz:url>'>
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
