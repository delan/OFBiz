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
<%WorkEffortWorker.getWorkEffort(pageContext, "workEffortId", "workEffort", "partyAssigns", "canView", "tryEntity", "currentStatusItem");%>
<%WorkEffortWorker.getActivityStatusItems(pageContext, "activityStatusItems");%>

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
              <form action="<ofbiz:url>/updateevent</ofbiz:url>" method=POST style='margin: 0;'>
              <table border='0' cellpadding='2' cellspacing='0'>
                <ofbiz:if name="workEffort">
                  <input type='hidden' name='UPDATE_MODE' value='UPDATE'>
                  <input type='hidden' name='WORK_EFFORT_ID' value='<ofbiz:print attribute="workEffortId"/>'>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Activity Name</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='WORK_EFFORT_NAME' value='<ofbiz:inputvalue field="workEffortName" param="WORK_EFFORT_NAME" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Priority</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <SELECT name='PRIORITY'>
                      <OPTION><ofbiz:inputvalue field="priority" param="PRIORITY" entityAttr="workEffort" tryEntityAttr="tryEntity"/></OPTION>
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
                    <SELECT name='CURRENT_STATUS_ID'>
                      <OPTION value='<ofbiz:entityfield field="statusId" attribute="currentStatusItem"/>'><ofbiz:entityfield field="description" attribute="currentStatusItem"/></OPTION>
                      <OPTION value=''>--</OPTION>
                      <ofbiz:iterator name="statusItem" property="activityStatusItems">
                        <OPTION value='<ofbiz:entityfield field="statusId" attribute="statusItem"/>'><ofbiz:entityfield field="description" attribute="statusItem"/></OPTION>
                      </ofbiz:iterator>
                    </SELECT>
                    <ofbiz:if name="workEffort">
                      <span class='tabletext'>Last Updated <ofbiz:entityfield field="lastStatusUpdate" attribute="workEffort"/></span>
                    </ofbiz:if>
                  </td>
                </tr>
                <input type='hidden' name='CURRENT_STATUS_ID' value='CAL_ACCEPTED'>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Location</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='60' maxlength='255' name='LOCATION_DESC' value='<ofbiz:inputvalue field="locationDesc" param="LOCATION_DESC" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Description</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><TEXTAREA name='DESCRIPTION' cols='50' rows='4'><ofbiz:inputvalue field="description" param="DESCRIPTION" entityAttr="workEffort" tryEntityAttr="tryEntity"/></TEXTAREA>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Start Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='ESTIMATED_START_DATE' value='<ofbiz:inputvalue field="estimatedStartDate" param="ESTIMATED_START_DATE" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'><span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>End Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='ESTIMATED_COMPLETION_DATE' value='<ofbiz:inputvalue field="estimatedCompletionDate" param="ESTIMATED_COMPLETION_DATE" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'><span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span></td>
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

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

