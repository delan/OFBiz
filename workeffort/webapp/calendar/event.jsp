<%
/**
 *  Title: Calendar Event Editor Page
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

<%@ page import="org.ofbiz.commonapp.workeffort.workeffort.*" %>

<%pageContext.setAttribute("PageName", "Calendar Event Editor Page");%>

<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%WorkEffortWorker.getWorkEffort(pageContext, "workEffortId", "workEffort", "partyAssigns", "canView", "tryEntity");%>

<BR>
<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Calendar Event Editor</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<ofbiz:url>/day</ofbiz:url>' class='lightbuttontext'>[Day&nbsp;View]</A>
            <A href='<ofbiz:url>/week</ofbiz:url>' class='lightbuttontext'>[Week&nbsp;View]</A>
            <A href='<ofbiz:url>/month</ofbiz:url>' class='lightbuttontext'>[Month&nbsp;View]</A>
            <A href='<ofbiz:url>/upcoming</ofbiz:url>' class='lightbuttontext'>[Upcoming&nbsp;Events]</A>
            <A href='<ofbiz:url>/event</ofbiz:url>' class='lightbuttontext'>[New&nbsp;Event]</A>
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
                </ofbiz:if>
                <ofbiz:unless name="workEffort">
                  <input type='hidden' name='UPDATE_MODE' value='CREATE'>
                  <input type='hidden' name='WORK_EFFORT_TYPE_ID' value='EVENT'>
                  <ofbiz:if name="workEffortId">
                    <DIV class='tabletext'>ERROR: Could not find Event with ID "<ofbiz:print attribute="workEffortId"/>"</DIV>
                  </ofbiz:if>
                </ofbiz:unless>

                <input type='hidden' name='CURRENT_STATUS_ID' value='CAL_ACCEPTED'>
                
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Event Name</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='WORK_EFFORT_NAME' value='<ofbiz:inputvalue field="workEffortName" param="WORK_EFFORT_NAME" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'></td>
                </tr>
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
                  <td width='74%'><input type='text' size='30' maxlength='30' name='ESTIMATED_START_DATE' value='<ofbiz:inputvalue field="estimatedStartDate" param="ESTIMATED_START_DATE" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>End Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='ESTIMATED_COMPLETION_DATE' value='<ofbiz:inputvalue field="estimatedCompletionDate" param="ESTIMATED_COMPLETION_DATE" entityAttr="workEffort" tryEntityAttr="tryEntity"/>'></td>
                </tr>

                <tr>
                  <td colspan='3'><input type="submit" name="Update" value="Update"></td>
                </tr>
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
