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
 *@author     David E. Jones
 *@created    May 22 2001
 *@version    1.0
--%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="org.ofbiz.commonapp.workeffort.workeffort.*" %>
<%@ page import="org.ofbiz.commonapp.common.status.*" %>
<%WorkEffortWorker.getWorkEffort(pageContext, "workEffortId", "workEffort", "partyAssigns", "canView", "tryEntity", "currentStatusItem");%>
<%StatusWorker.getStatusItems(pageContext, "eventStatusItems", "EVENT_STATUS");%>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align=left width='40%' class="boxhead">Calendar Event Details</td>
          <td align=right width='60%'>
		  <table><tr><td>
		  <a href='<ofbiz:url>/day</ofbiz:url>' class='submenutext'>Day&nbsp;View</a><a href='<ofbiz:url>/week</ofbiz:url>' class='submenutext'>Week&nbsp;View</a><a href='<ofbiz:url>/month</ofbiz:url>' class='submenutext'>Month&nbsp;View</a><a href='<ofbiz:url>/upcoming</ofbiz:url>' class='submenutext'>Upcoming&nbsp;Events</a><a href='<ofbiz:url>/event</ofbiz:url>' class='submenutextrightdisabled'>New&nbsp;Event</a>
		  </td></tr></table>
		  </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <ofbiz:if name="canView">
              <ofbiz:if name="workEffort">
                <form action="<ofbiz:url>/updateevent</ofbiz:url>" method=POST style='margin: 0;'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='workEffortId' value='<ofbiz:print attribute="workEffortId"/>'>
              </ofbiz:if>
              <ofbiz:unless name="workEffort">
                <form action="<ofbiz:url>/createevent</ofbiz:url>" method=POST style='margin: 0;'>
                <input type='hidden' name='quickAssignPartyId' value='<ofbiz:entityfield field="partyId" attribute="userLogin"/>'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='workEffortTypeId' value='EVENT'>
                  <ofbiz:if name="workEffortId">
                    <div class='tabletext'>ERROR: Could not find Event with ID "<ofbiz:print attribute="workEffortId"/>"</div>
                  </ofbiz:if>
              </ofbiz:unless>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Event Name</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' <ofbiz:inputvalue field="workEffortName" entityAttr="workEffort" tryEntityAttr="tryEntity" fullattrs="true"/>></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Priority</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <select name='priority' class='selectBox'>
                      <option><ofbiz:inputvalue field="priority" entityAttr="workEffort" tryEntityAttr="tryEntity"/></option>
                      <option value=''>--</option>
                      <option>1</option> <option>2</option> <option>3</option>
                      <option>4</option> <option>5</option> <option>6</option>
                      <option>7</option> <option>8</option> <option>9</option>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Event Status</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <select name='currentStatusId' class='selectBox'>
                      <option value='<ofbiz:entityfield field="statusId" attribute="currentStatusItem" default="CAL_TENTATIVE"/>'><ofbiz:entityfield field="description" attribute="currentStatusItem"/></option>
                      <option value=''>--</option>
                      <ofbiz:iterator name="statusItem" property="eventStatusItems">
                        <option value='<ofbiz:entityfield field="statusId" attribute="statusItem"/>'><ofbiz:entityfield field="description" attribute="statusItem"/></option>
                      </ofbiz:iterator>
                    </select>
                    <ofbiz:if name="workEffort">
                      <span class='tabletext'>Last Updated <ofbiz:entityfield field="lastStatusUpdate" attribute="workEffort"/></span>
                    </ofbiz:if>
                  </td>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Location</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='60' maxlength='255' <ofbiz:inputvalue field="locationDesc" entityAttr="workEffort" tryEntityAttr="tryEntity" fullattrs="true"/>></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Description</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><textarea class='textAreaBox' name='description' cols='50' rows='4'><ofbiz:inputvalue field="description" entityAttr="workEffort" tryEntityAttr="tryEntity"/></textarea>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Start Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='30' maxlength='30' <ofbiz:inputvalue field="estimatedStartDate" entityAttr="workEffort" tryEntityAttr="tryEntity" fullattrs="true"/>><span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>End Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='30' maxlength='30' <ofbiz:inputvalue field="estimatedCompletionDate" entityAttr="workEffort" tryEntityAttr="tryEntity" fullattrs="true"/>><span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span></td>
                </tr>

                <ofbiz:if name="workEffort">
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
                      by <ofbiz:entityfield field="createdByUserLogin" attribute="workEffort"/>
                    </div></td>
                  </tr>
                  <tr>
                    <td width='26%' align=right><div class='tabletext'>Last Modified</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>
                      <ofbiz:entityfield field="lastModifiedDate" attribute="workEffort"/>
                      by <ofbiz:entityfield field="lastModifiedByUserLogin" attribute="workEffort"/>
                    </div></td>
                  </tr>
                </ofbiz:if>

                <tr>
                  <td width='26%' align=right>
                    <ofbiz:if name="workEffort"><input type="submit" name="Update" value="Update"></ofbiz:if>
                    <ofbiz:unless name="workEffort"><input type="submit" name="Create" value="Create"></ofbiz:unless>
                  </td>
                  <td>&nbsp;</td>
                  <td width='74%'><div class='tabletext'>&nbsp;</div></td>
                </tr>
              </table>
              </form>
            </ofbiz:if>
            <ofbiz:unless name="canView">
              <div class='tabletext'>ERROR: You do not have permission to view this Event. This event must belong to you, or you must be an administrator.</div>
            </ofbiz:unless>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
