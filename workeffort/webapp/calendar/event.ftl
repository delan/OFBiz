<#--
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
-->

<#macro quoteIfExists str>
<#if str?has_content>'${str}'
<#else>null</#if>
</#macro>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align=left width='40%' class="boxhead">Calendar Event Details</td>
          <td align=right width='60%'>
		  <table><tr><td>
		  <a href='<@ofbizUrl>/day</@ofbizUrl>' class='submenutext'>Day&nbsp;View</a><a href='<@ofbizUrl>/week</@ofbizUrl>' class='submenutext'>Week&nbsp;View</a><a href='<@ofbizUrl>/month</@ofbizUrl>' class='submenutext'>Month&nbsp;View</a><a href='<@ofbizUrl>/upcoming</@ofbizUrl>' class='submenutext'>Upcoming&nbsp;Events</a><a href='<@ofbizUrl>/event</@ofbizUrl>' class='submenutextrightdisabled'>New&nbsp;Event</a>
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
            <#if workEffortId?exists>
              <div class='tabContainer'>
                  <a href="<@ofbizUrl>/event?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButtonSelected">Event</a>
                  <a href="<@ofbizUrl>/eventPartyAssignments?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButton">Parties</a>
                  <a href="<@ofbizUrl>/eventContactMechs?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButton">Contact information</a>
              </div>
            </#if>
            <#if canView = true>
              <#if workEffort?has_content>
                <form action="<@ofbizUrl>/updateevent</@ofbizUrl>" name="eventform" method=POST style='margin: 0;'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='workEffortId' value='${workEffortId}'>              
              <#else>
                <form action="<@ofbizUrl>/createevent</@ofbizUrl>" name="eventform" method=POST style='margin: 0;'>
                <input type='hidden' name='quickAssignPartyId' value='${userLogin.partyId}'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='workEffortTypeId' value='EVENT'>
                  <#if workEffortId?has_content>
                    <div class='tabletext'>ERROR: Could not find Event with ID "${workEffortId}"</div>
                  </#if>
              </#if>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Event Name</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='30' maxlength='30' name="workEffortName" value='${(workEffort.workEffortName)?if_exists}'></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Priority</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <select name='priority' class='selectBox'>
                      <option>${(workEffort.priority)?if_exists}</option>
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
                      <option value='${(currentStatusItem.statusId)?default("CAL_TENTATIVE")}'>${(currentStatusItem.description)?if_exists}</option>
                      <option value=''>--</option>
                      <#list eventStatusItems as statusItem>
                        <option value='${statusItem.statusId}'>${statusItem.description}</option>
                      </#list>
                    </select>
                    <#if workEffort?has_content>
                      <span class='tabletext'>Last Updated ${workEffort.lastStatusUpdate.toString()}</span>
                    </#if>
                  </td>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Location</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='60' maxlength='255' name="locationDesc" value='${(workEffort.locationDesc)?if_exists}'></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Description</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><textarea class='textAreaBox' name='description' cols='50' rows='4'>${(workEffort.description)?if_exists}</textarea>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Start Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='30' maxlength='30' name="estimatedStartDate" value='${(workEffort.estimatedStartDate.toString())?if_exists}'>
                  <a href="javascript:call_cal(document.eventform.estimatedStartDate, <@quoteIfExists str=(workEffort.estimatedStartDate.toString())?if_exists/>);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>End Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='30' maxlength='30' name="estimatedCompletionDate" value='${(workEffort.estimatedCompletionDate.toString())?if_exists}'>
                  <a href="javascript:call_cal(document.eventform.estimatedCompletionDate, <@quoteIfExists str=(workEffort.estimatedCompletionDate.toString())?if_exists/>);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a></td>
                </tr>

                <#if workEffort?has_content>
                  <tr>
                    <td width='26%' align=right><div class='tabletext'>Revision #</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>${workEffort.revisionNumber}</div></td>
                  </tr>
                  <tr>
                    <td width='26%' align=right><div class='tabletext'>Created</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>
                      ${workEffort.createdDate.toString()}
                      by ${workEffort.createdByUserLogin}
                    </div></td>
                  </tr>
                  <tr>
                    <td width='26%' align=right><div class='tabletext'>Last Modified</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>
                      ${workEffort.lastModifiedDate.toString()}
                      by ${workEffort.lastModifiedByUserLogin}
                    </div></td>
                  </tr>
                </#if>

                <tr>
                  <td width='26%' align=right>
                    <#if workEffort?has_content><input type="submit" name="Update" value="Update">
                    <#else><input type="submit" name="Create" value="Create"></#if>
                  </td>
                  <td>&nbsp;</td>
                  <td width='74%'><div class='tabletext'>&nbsp;</div></td>
                </tr>
              </table>
              </form>
            <#else>
              <div class='tabletext'>ERROR: You do not have permission to view this Event. This event must belong to you, or you must be an administrator.</div>
            </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
