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
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.webapp.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />

<%@ page import="org.ofbiz.workeffort.workeffort.*" %>
<%@ page import="org.ofbiz.common.status.*" %>
<%WorkEffortWorker.getWorkEffort(pageContext, "workEffortId", "workEffort", "partyAssigns", "canView", "tryEntity", "currentStatusItem");%>
<%StatusWorker.getStatusItems(pageContext, "taskStatusItems", "WORKFLOW_STATUS");%>

<TABLE border="0" width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align="left" width='40%' >
            <div class='boxhead'>&nbsp;Phase Detail</div>
          </TD>
          <TD align="right" width='60%'>
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
                <form name='projectForm' action="<ofbiz:url>/updatephase</ofbiz:url>" method="post" style='margin: 0;'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='workEffortId' value='<ofbiz:print attribute="workEffortId"/>'>
              </ofbiz:if>
              <ofbiz:unless name="workEffort">
                <form name='projectForm' action="<ofbiz:url>/createphase</ofbiz:url>" method="post" style='margin: 0;'>
                <input type='hidden' name='quickAssignPartyId' value='<ofbiz:entityfield field="partyId" attribute="userLogin"/>'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='workEffortTypeId' value='TASK'>
                  <input type='hidden' name='workEffortPurposeTypeId' value='WEPT_PHASE'>
                  <ofbiz:if name="workEffortId">
                    <DIV class='tabletext'>ERROR: Could not find Task with ID "<ofbiz:print attribute="workEffortId"/>"</DIV>
                  </ofbiz:if>
              </ofbiz:unless>

                <input type='hidden' name='projectWorkEffortId' value='<%=request.getParameter("projectWorkEffortId")%>'>

                <tr>
                  <td width='26%' align="right"><div class='tabletext'>Phase Name</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='workEffortName' value='<ofbiz:inputvalue field="workEffortName" param="workEffortName" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'></td>
                </tr>
<%--                <tr>
                  <td width='26%' align="right"><div class='tabletext'>Priority</div></td>
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
                  <td width='26%' align="right"><div class='tabletext'>Phase Status</div></td>
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
                  <td width='26%' align="right"><div class='tabletext'>Location</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='60' maxlength='255' name='locationDesc' value='<ofbiz:inputvalue field="locationDesc" param="locationDesc" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'></td>
                </tr> --%>
                <tr>
                  <td width='26%' align="right"><div class='tabletext'>Description</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><TEXTAREA name='description' cols='50' rows='4'><ofbiz:inputvalue field="description" param="description" entityAttr="workEffort" tryEntityAttr="tryEntity"/></TEXTAREA>
                </tr>

<script language="JavaScript" type="text/javascript">
function insertNowTimestampStart() {
  eval('document.projectForm.estimatedStartDate.value="<%=UtilDateTime.nowTimestamp().toString()%>";');
};
</script>

                <tr>
                  <td width='26%' align="right"><div class='tabletext'>Start Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='estimatedStartDate' value='<ofbiz:inputvalue field="estimatedStartDate" param="estimatedStartDate" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'>
                    <span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span>
                    <a href="#" onclick='javascript:insertNowTimestampStart()' class='buttontext'>[Now]</a>
                  </td>
                </tr>
                <tr>
                  <td width='26%' align="right"><div class='tabletext'>End Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='estimatedCompletionDate' value='<ofbiz:inputvalue field="estimatedCompletionDate" param="estimatedCompletionDate" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'><span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span></td>
                </tr>

                <ofbiz:if name="workEffort">
<%--                  <tr>
                    <td width='26%' align="right"><div class='tabletext'>Revision #</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'><ofbiz:entityfield field="revisionNumber" attribute="workEffort"/></div></td>
                  </tr>--%>
                  <tr>
                    <td width='26%' align="right"><div class='tabletext'>Created</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>
                      <ofbiz:entityfield field="createdDate" attribute="workEffort"/>
                      by <ofbiz:entityfield field="createdByUserLogin" attribute="workEffort"/>
                    </div></td>
                  </tr>
                  <tr>
                    <td width='26%' align="right"><div class='tabletext'>Last Modified</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>
                      <ofbiz:entityfield field="lastModifiedDate" attribute="workEffort"/>
                      by <ofbiz:entityfield field="lastModifiedByUserLogin" attribute="workEffort"/>
                    </div></td>
                  </tr>
                </ofbiz:if>

                <tr>
                  <td width='26%' align="right">
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
<script language="JavaScript" type="text/javascript">
<!--
  document.projectForm.workEffortName.focus();
//-->
</script>
