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
<jsp:useBean id="userLogin" type="org.ofbiz.core.entity.GenericValue" scope="request" />

<%@ page import="org.ofbiz.commonapp.workeffort.workeffort.*" %>
<%@ page import="org.ofbiz.commonapp.common.status.*" %>
<%WorkEffortWorker.getWorkEffort(pageContext, "workEffortId", "workEffort", "partyAssigns", "canView", "tryEntity", "currentStatusItem");%>
<%StatusWorker.getStatusItems(pageContext, "taskStatusItems", "WORKFLOW_STATUS");%>
<%String workEffortId = request.getParameter("workEffortId");%>
<%//GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);%>
<%java.sql.Timestamp now = new java.sql.Timestamp((new java.util.Date()).getTime());%>
<%Collection assignments = delegator.findByAnd("WorkEffortPartyAssignment",
        UtilMisc.toList(new EntityExpr("partyId", EntityOperator.EQUALS, userLogin.get("partyId")),
        new EntityExpr("workEffortId", EntityOperator.EQUALS, workEffortId),
        new EntityExpr("roleTypeId", EntityOperator.EQUALS, "CAL_OWNER")));%>
<%boolean isOwner = assignments.size() > 0;%>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Project Detail</div>
          </TD>
          <TD align=right width='60%'>
<%--            <A href='<ofbiz:url>/mytasks</ofbiz:url>' class='lightbuttontext'>[Task&nbsp;List]</A>
            <A href='<ofbiz:url>/task</ofbiz:url>' class='lightbuttontext'>[New&nbsp;Task]</A>--%>
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
            <ofbiz:if name="canView" type="Boolean">
              <ofbiz:if name="workEffort">
                <form name='projectForm' action="<ofbiz:url>/updateproject</ofbiz:url>" method=POST style='margin: 0;'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='workEffortId' value='<ofbiz:print attribute="workEffortId"/>'>
              </ofbiz:if>
              <ofbiz:unless name="workEffort">
                <form name='projectForm' action="<ofbiz:url>/createproject</ofbiz:url>" method=POST style='margin: 0;'>
                <input type='hidden' name='quickAssignPartyId' value='<ofbiz:entityfield field="partyId" attribute="userLogin"/>'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='workEffortTypeId' value='TASK'>
                  <input type='hidden' name='workEffortPurposeTypeId' value='WEPT_PROJECT'>
                  <ofbiz:if name="workEffortId">
                    <DIV class='tabletext'>ERROR: Could not find Task with ID "<ofbiz:print attribute="workEffortId"/>"</DIV>
                  </ofbiz:if>
              </ofbiz:unless>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Project Name</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='workEffortName' value='<ofbiz:inputvalue field="workEffortName" param="workEffortName" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'></td>
                </tr>
<%--                <tr>
                  <td width='26%' align=right><div class='tabletext'>Priority</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <SELECT name='priority'>
                      <OPTION><ofbiz:inputvalue field="priority" param="priority" entityAttr="workEffort" tryEntityAttr="tryEntity"/></OPTION>
                      <OPTION value=''></OPTION>
                      <OPTION>1</OPTION> <OPTION>2</OPTION> <OPTION>3</OPTION>
                      <OPTION>4</OPTION> <OPTION>5</OPTION> <OPTION>6</OPTION>
                      <OPTION>7</OPTION> <OPTION>8</OPTION> <OPTION>9</OPTION>
                    </SELECT>
                  </td>
                </tr>--%>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Project Status</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <SELECT name='currentStatusId'>
                      <OPTION value='<ofbiz:entityfield field="statusId" attribute="currentStatusItem" default="CAL_NEEDS_ACTION"/>'><ofbiz:entityfield field="description" attribute="currentStatusItem"/></OPTION>
                      <OPTION value=''></OPTION>
                      <ofbiz:iterator name="statusItem" property="taskStatusItems">
                        <OPTION value='<ofbiz:entityfield field="statusId" attribute="statusItem"/>'><ofbiz:entityfield field="description" attribute="statusItem"/></OPTION>
                      </ofbiz:iterator>
                    </SELECT>
                    <ofbiz:if name="workEffort">
                      <span class='tabletext'>Last Updated <ofbiz:entityfield field="lastStatusUpdate" attribute="workEffort"/></span>
                    </ofbiz:if>
                  </td>
                </tr>

<%--                <tr>
                  <td width='26%' align=right><div class='tabletext'>Location</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='60' maxlength='255' name='locationDesc' value='<ofbiz:inputvalue field="locationDesc" param="locationDesc" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'></td>
                </tr> --%>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Description</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><TEXTAREA name='description' cols='50' rows='4'><ofbiz:inputvalue field="description" param="description" entityAttr="workEffort" tryEntityAttr="tryEntity"/></TEXTAREA>
                </tr>

<SCRIPT language='JavaScript'>
function insertNowTimestampStart() {
  eval('document.projectForm.estimatedStartDate.value="<%=UtilDateTime.nowTimestamp().toString()%>";');
};
</SCRIPT>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Start Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='estimatedStartDate' value='<ofbiz:inputvalue field="estimatedStartDate" param="estimatedStartDate" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'>
                    <span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span>
                    <a href="#" onclick='javascript:insertNowTimestampStart()' class='buttontext'>[Now]</a>
                  </td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>End Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='estimatedCompletionDate' value='<ofbiz:inputvalue field="estimatedCompletionDate" param="estimatedCompletionDate" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'><span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span></td>
                </tr>

                <ofbiz:if name="workEffort">
<%--                  <tr>
                    <td width='26%' align=right><div class='tabletext'>Revision #</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'><ofbiz:entityfield field="revisionNumber" attribute="workEffort"/></div></td>
                  </tr>--%>
                  <tr>
                    <td width='26%' align=right><div class='tabletext'>Created</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>
                      <ofbiz:entityfield field="createdDate" attribute="workEffort"/>
                      by <ofbiz:entityfield field="createdByPartyId" attribute="workEffort"/>
                    </div></td>
                  </tr>
                  <tr>
                    <td width='26%' align=right><div class='tabletext'>Last Modified</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>
                      <ofbiz:entityfield field="lastModifiedDate" attribute="workEffort"/>
                      by <ofbiz:entityfield field="lastModifiedByPartyId" attribute="workEffort"/>
                    </div></td>
                  </tr>
                </ofbiz:if>

                <tr>
                  <td width='26%' align=right>
<%--                    <input type="submit" name="Save" value="Save">--%>
                    <ofbiz:if name="workEffort"><input type="submit" name="Update" value="Update"></ofbiz:if>
                    <ofbiz:unless name="workEffort"><input type="submit" name="Create" value="Create"></ofbiz:unless>
                  </td>
                  <td>&nbsp;</td>
                  <td width='74%'><div class='tabletext'>&nbsp;</div></td>
                </tr>
              </table>
              </form>
            </ofbiz:if>
            <ofbiz:unless name="canView" type="Boolean">
              <DIV class='tabletext'>ERROR: You do not have permission to view this Event. This project must belong to you, or you must be an administrator.</DIV>
            </ofbiz:unless>
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
          <TD align=left>
            <div class='boxhead'>&nbsp;Project Assignments</div>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <% 
          Iterator assigns = delegator.findByAnd("WorkEffortAndPartyAssign", 
            UtilMisc.toMap("workEffortId", workEffortId)).iterator();
          while(assigns.hasNext()) {%>
            <% GenericValue WEPA = (GenericValue)assigns.next();%>
            <% GenericValue person = WEPA.getRelatedOne("Person");%>
            <tr><td><%=person.getString("firstName") + " " + person.getString("lastName")%></td></tr>
          <%}%>
        <% if(isOwner) {%>
        <tr><td><hr></td></tr>
        <tr>
          <td align=left>Assign User to Project&nbsp;&nbsp;
            <% Iterator people = delegator.findAll("Person").iterator();%>
            <form name='assignform' action='<ofbiz:url>/addprojectassignment</ofbiz:url>' method=POST>
            <input type=hidden name='roleTypeId' value='CAL_DELEGATE'>
            <input type=hidden name='workEffortId' value='<%=workEffortId%>'>
            <select name='quickAssignPartyId' onchange='javascript:window.assignform.submit();'>
              <option value=''>(Choose User to Assign)
            <%while(people.hasNext()) {%>
              <% GenericValue person = (GenericValue)people.next();%>
              <option value='<%=person.getString("partyId")%>'><%=person.getString("firstName") + " " + person.getString("lastName")%>
            <%}%>
            </select>
            </form>
          </td>
        </tr>
        <%}%>
      </table>
    </TD>
  </TR>
</TABLE>
<script language="JavaScript">
<!--
  document.projectForm.workEffortName.focus();
//-->
</script>
