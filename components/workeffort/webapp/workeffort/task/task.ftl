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
 *@author     Johan Isacsson (conversion of JSP created by David E. Jones)
 *@author     Eric.Barbier@nereide.biz (migration to uiLabelMap)
 *@created    May 14 2003
 *@version    1.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align=left width='40%' >
            <div class='boxhead'>${uiLabelMap.WorkEffortTaskDetail}</div>
          </td>
          <td align=right width='60%'>
            <a href='<@ofbizUrl>/mytasks</@ofbizUrl>' class='submenutext'>${uiLabelMap.WorkEffortTaskList}</a><a href='<@ofbizUrl>/task</@ofbizUrl>' class='submenutextright'>${uiLabelMap.WorkEffortNewTask}</a>
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
            <#if canView = true>
              <#if workEffort?has_content>
                <form name="taskform" action="<@ofbizUrl>/updatetask</@ofbizUrl>" method=POST style='margin: 0;'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='workEffortId' value='${workEffortId}'>
              <#else>              
                <form name="taskform" action="<@ofbizUrl>/createtask</@ofbizUrl>" method=POST style='margin: 0;'>
                <input type='hidden' name='quickAssignPartyId' value='${userLogin.partyId}'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='workEffortTypeId' value='TASK'>
                  <#if workEffortId?has_content>
                    <div class='tabletext'>${uiLabelMap.WorkEffortErrorNotFindTaskId} "${workEffortId}"</div>
                  </#if>
              </#if>
              <#if requestParameters.communicationEventId?exists>
                <input type="hidden" name="communicationEventId" value="${requestParameters.communicationEventId}">
              </#if>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>${uiLabelMap.WorkEffortTaskName}</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='30' maxlength='30' name='workEffortName' value='${(workEffort.workEffortName)?if_exists}'></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>${uiLabelMap.WorkEffortPriority}</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <select name='priority' class='selectBox'>
                      <option>${(workEffort.priority)?if_exists}</option>
                      <option value=''></option>
                      <option>1</option> <option>2</option> <option>3</option>
                      <option>4</option> <option>5</option> <option>6</option>
                      <option>7</option> <option>8</option> <option>9</option>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>${uiLabelMap.WorkEffortTaskStatus}</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <select name='currentStatusId' class='selectBox'>
                      <option value='${(currentStatusItem.statusId)?default("CAL_NEEDS_ACTION")}'>${(currentStatusItem.description)?if_exists}</option>
                      <option value=''></option>
                      <#list taskStatusItems as statusItem>
                        <option value='${statusItem.statusId}'>${statusItem.description}</option>
                      </#list>
                    </select>
                    <#if workEffort?has_content>
                      <span class='tabletext'>${uiLabelMap.CommonLastUpdated} ${(workEffort.lastStatusUpdate.toString())?if_exists}</span>
                    </#if>
                  </td>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>${uiLabelMap.WorkEffortLocation}</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='60' maxlength='255' name='locationDesc' value='${(workEffort.locationDesc)?if_exists}'></td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>${uiLabelMap.CommonDescription}</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><textarea name='description' class='textAreaBox' cols='50' rows='4'>${(workEffort.description)?if_exists}</TEXTAREA>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>${uiLabelMap.CommonStartDateTime}</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <input type='text' class='inputBox' size='30' maxlength='30' name='estimatedStartDate' value='${(workEffort.estimatedStartDate)?if_exists}'>
                    <a href="javascript:call_cal(document.taskform.estimatedStartDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
                  </td>
                </tr>
                <tr>
                  <td width='26%' align=right><div class='tabletext'>${uiLabelMap.CommonEndDateTime}</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <input type='text' class='inputBox' size='30' maxlength='30' name='estimatedCompletionDate' value='${(workEffort.estimatedCompletionDate)?if_exists}'>
                    <a href="javascript:call_cal(document.taskform.estimatedCompletionDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
                  </td>
                </tr>

                <#if workEffort?has_content>
                  <tr>
                    <td width='26%' align=right><div class='tabletext'>${uiLabelMap.WorkEffortRevision} </div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>${workEffort.revisionNumber}</div></td>
                  </tr>
                  <tr>
                    <td width='26%' align=right><div class='tabletext'>${uiLabelMap.WorkEffortCreated}</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>
                      ${workEffort.createdDate.toString()}
                      ${uiLabelMap.CommonBy} ${workEffort.createdByUserLogin}
                    </div></td>
                  </tr>
                  <tr>
                    <td width='26%' align=right><div class='tabletext'>${uiLabelMap.WorkEffortLastModified}</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>
                      ${workEffort.lastModifiedDate.toString()}
                       ${uiLabelMap.CommonBy} ${workEffort.lastModifiedByUserLogin}
                    </div></td>
                  </tr>
                </#if>

                <tr>
                  <td width='26%' align=right>
                    <#if workEffort?exists>
                    <input type="submit" name="Update" value=" ${uiLabelMap.CommonUpdate}">
                    <#else>
                    <input type="submit" name="Create" value=" ${uiLabelMap.CommonCreate}">
                    </#if>
                  </td>
                  <td>&nbsp;</td>
                  <td width='74%'><div class='tabletext'>&nbsp;</div></td>
                </tr>
              </table>
              </form>
            <#else>
              <div class='tabletext'>${uiLabelMap.WorkEffortErrorPermissionViewEvent}.</div>
            </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
