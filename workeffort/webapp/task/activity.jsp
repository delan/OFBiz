<%
/**
 *  Title: Task Page
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

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>


<%@ page import="org.ofbiz.commonapp.workeffort.workeffort.*" %>
<%@ page import="org.ofbiz.commonapp.common.status.*" %>
<%WorkEffortWorker.getWorkEffort(pageContext, "workEffortId", "workEffort", "partyAssigns", "canView", "tryEntity", "currentStatusItem");%>
<%
    //if this was an update on a party assignment, set tryEntity to true
    if (request.getParameter("partyId") != null || request.getParameter("roleTypeId") != null) {
        pageContext.setAttribute("tryEntity", new Boolean(true));
    }
%>


<%pageContext.setAttribute("PageName", "Activity Editor Page");%>

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
            <div class='boxhead'>&nbsp;Activity Detail</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<ofbiz:url>/tasklist</ofbiz:url>' class='lightbuttontext'>[Task&nbsp;List]</A>
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
            <ofbiz:if name="canView" type="Boolean">
              <form action="<ofbiz:url>/updateactivity</ofbiz:url>" method=POST style='margin: 0;'>
              <table border='0' cellpadding='2' cellspacing='0'>
                <ofbiz:if name="workEffort">
                  <input type='hidden' name='UPDATE_MODE' value='UPDATE'>
                  <input type='hidden' name='workEffortId' value='<ofbiz:print attribute="workEffortId"/>'>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Activity Name</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='workEffortName' value='<ofbiz:inputvalue field="workEffortName" param="workEffortName" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Priority</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <SELECT name='priority'>
                      <OPTION><ofbiz:inputvalue field="priority" param="priority" entityAttr="workEffort" tryEntityAttr="tryEntity"/></OPTION>
                      <OPTION value=''>--</OPTION>
                      <OPTION>1</OPTION> <OPTION>2</OPTION> <OPTION>3</OPTION>
                      <OPTION>4</OPTION> <OPTION>5</OPTION> <OPTION>6</OPTION>
                      <OPTION>7</OPTION> <OPTION>8</OPTION> <OPTION>9</OPTION>
                    </SELECT>
                  </td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Activity Status</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <input type='hidden' name='currentStatusId' value='<ofbiz:entityfield field="currentStatusId" attribute="workEffort"/>'>
                    <span class='tabletext'><ofbiz:entityfield field="description" attribute="currentStatusItem"/></span>
                    <span class='tabletext'> - Last Updated: <ofbiz:entityfield field="lastStatusUpdate" attribute="workEffort"/></span>
                  </td>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Location</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='60' maxlength='255' name='locationDesc' value='<ofbiz:inputvalue field="locationDesc" param="locationDesc" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Description</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><TEXTAREA name='description' cols='50' rows='4'><ofbiz:inputvalue field="description" param="description" entityAttr="workEffort" tryEntityAttr="tryEntity"/></TEXTAREA>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Start Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='estimatedStartDate' value='<ofbiz:inputvalue field="estimatedStartDate" param="estimatedStartDate" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'><span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>End Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='estimatedCompletionDate' value='<ofbiz:inputvalue field="estimatedCompletionDate" param="estimatedCompletionDate" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'><span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span></td>
                </tr>

                  <tr>
                    <td width='26%' align=right><div class='tabletext'>Revision #</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'><ofbiz:entityfield field="revisionNumber" attribute="workEffort"/></div></td>
                  </tr>
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

                  <tr>
                    <td width='26%' align=right>
                      <input type="submit" name="Update" value="Update">
                    </td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>&nbsp;</div></td>
                  </tr>
                </ofbiz:if>
                <ofbiz:unless name="workEffort">
                  <tr>
                    <td colspan='3'>
                      <DIV class='tabletext'>ERROR: Could not find Activity with ID "<ofbiz:print attribute="workEffortId"/>"</DIV>
                    </td>
                  </tr>
                </ofbiz:unless>
              </table>
              </form>
            </ofbiz:if>
            <ofbiz:unless name="canView" type="Boolean">
              <DIV class='tabletext'>ERROR: You do not have permission to view this Event. This event must belong to you, or you must be an administrator.</DIV>
            </ofbiz:unless>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<%-- ===================================================================== --%>
<ofbiz:if name="partyAssigns" size="0">
    <BR>
    <TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
            <tr>
              <TD align=left width='40%' >
                <div class='boxhead'>&nbsp;Party Assignments Detail</div>
              </TD>
              <TD align=right width='60%'>
                <%-- <A href='<ofbiz:url>/tasklist</ofbiz:url>' class='lightbuttontext'>[Task&nbsp;List]</A> --%>
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
                <ofbiz:iterator name="workEffortPartyAssignment" property="partyAssigns">
                  <%
                    //if there was an error message and the current PK equals the parameter PK, set assignTryEntity to false
                    pageContext.setAttribute("assignTryEntity", new Boolean(true));
                    if (request.getAttribute(SiteDefs.ERROR_MESSAGE) != null &&
                        workEffortPartyAssignment.getString("workEffortId").equals(request.getParameter("workEffortId")) && 
                        workEffortPartyAssignment.getString("partyId").equals(request.getParameter("partyId")) && 
                        workEffortPartyAssignment.getString("roleTypeId").equals(request.getParameter("roleTypeId")) && 
                        workEffortPartyAssignment.getTimestamp("fromDate").toString().equals(request.getParameter("fromDate"))) {
                        pageContext.setAttribute("assignTryEntity", new Boolean(false));
                    }
                  %>
                  <%String statusId = workEffortPartyAssignment.getString("statusId");%>
                  <%StatusWorker.getStatusValidChangeToDetails(pageContext, "taskStatusDetails", statusId);%>
                  <form action="<ofbiz:url>/updateactivityassign</ofbiz:url>" method=POST style='margin: 0;'>
                  <table border='0' cellpadding='2' cellspacing='0'>
                    <input type='hidden' name='UPDATE_MODE' value='UPDATE'>
                    <input type='hidden' name='workEffortId' value='<ofbiz:print attribute="workEffortId"/>'>
                    <input type='hidden' name='partyId' value='<ofbiz:entityfield field="partyId" attribute="workEffortPartyAssignment"/>'>
                    <input type='hidden' name='roleTypeId' value='<ofbiz:entityfield field="roleTypeId" attribute="workEffortPartyAssignment"/>'>
                    <input type='hidden' name='fromDate' value='<ofbiz:inputvalue field="fromDate" param="fromDate" entityAttr="workEffortPartyAssignment" tryEntityAttr="assignTryEntity"/>'>
    
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>Party ID</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'><span class='tabletext'><ofbiz:entityfield field="partyId" attribute="workEffortPartyAssignment"/></span></td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>Role Type ID</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'><span class='tabletext'><ofbiz:entityfield field="roleTypeId" attribute="workEffortPartyAssignment"/></span></td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>From Date</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'><span class='tabletext'><ofbiz:entityfield field="fromDate" attribute="workEffortPartyAssignment"/></span></td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>Thru Date</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'><input type='text' size='30' maxlength='30' name='thruDate' value='<ofbiz:inputvalue field="thruDate" param="thruDate" entityAttr="workEffortPartyAssignment" tryEntityAttr="assignTryEntity"/>'><span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span></td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>Party Assignment Status</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'>
                        <SELECT name='statusId'>
                          <%GenericValue wepaStatusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", statusId));%>
                          <%if (wepaStatusItem != null) pageContext.setAttribute("wepaStatusItem", wepaStatusItem);%>
                          <OPTION value='<ofbiz:entityfield field="statusId" attribute="wepaStatusItem" default="CAL_SENT"/>'><ofbiz:entityfield field="description" attribute="wepaStatusItem"/></OPTION>
                          <OPTION value=''>--</OPTION>
                          <ofbiz:iterator name="statusValidChangeToDetail" property="taskStatusDetails">
                            <OPTION value='<ofbiz:entityfield field="statusIdTo" attribute="statusValidChangeToDetail"/>'><ofbiz:entityfield field="description" attribute="statusValidChangeToDetail"/> (<ofbiz:entityfield field="transitionName" attribute="statusValidChangeToDetail"/>)</OPTION>
                          </ofbiz:iterator>
                        </SELECT>
                        <ofbiz:if name="workEffort">
                          <span class='tabletext'> - Last Updated: <ofbiz:entityfield field="statusDateTime" attribute="workEffortPartyAssignment"/></span>
                        </ofbiz:if>
                      </td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>Comments</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'><input type='text' size='60' maxlength='255' name='comments' value='<ofbiz:inputvalue field="comments" param="comments" entityAttr="workEffortPartyAssignment" tryEntityAttr="assignTryEntity"/>'></td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>Must RSVP?</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'>
                        <SELECT name='mustRsvp'>
                          <OPTION><ofbiz:inputvalue field="mustRsvp" param="mustRsvp" entityAttr="workEffortPartyAssignment" tryEntityAttr="assignTryEntity"/></OPTION>
                          <OPTION value=''>--</OPTION>
                          <OPTION>Y</OPTION> <OPTION>N</OPTION>
                        </SELECT>
                      </td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>Expectation</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'><span class='tabletext'><ofbiz:entityfield field="expectationEnumId" attribute="workEffortPartyAssignment"/></span></td>
                    </tr>
                    
                    <tr>
                      <td width='26%' align=right>
                        <input type="submit" name="Update" value="Update">
                      </td>
                      <td>&nbsp;</td>
                      <td width='74%'><div class='tabletext'>&nbsp;</div></td>
                    </tr>
                  </table>
                  </form>
                  <ofbiz:iteratorHasNext><HR></ofbiz:iteratorHasNext>
                </ofbiz:iterator>
              </td>
            </tr>
          </table>
        </TD>
      </TR>
    </TABLE>
</ofbiz:if>
    
<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

