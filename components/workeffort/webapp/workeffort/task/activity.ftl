<#--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@author     Eric.Barbier@nereide.biz (migration to uiLabelMap)
 *@version    $Revision: 1.3 $
 *@since      2.2
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>

<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align=left width='40%' >
            <div class='boxhead'>${uiLabelMap.WorkEffortActivityDetail}</div>
          </td>
          <td align='right' width='60%'>
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
            <#if canView>
              <form name="taskform" action="<@ofbizUrl>/updateactivity</@ofbizUrl>" method="post" style='margin: 0;'>
              <table border='0' cellpadding='2' cellspacing='0'>
                <#if workEffort?has_content>
                  <input type='hidden' name='workEffortId' value='${workEffort.workEffortId}'>
                  <tr>
                    <td width='26%' align='right'><div class='tabletext'>${uiLabelMap.WorkEffortActivityName}</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><input type='text' class='inputBox' size='30' maxlength='30' name="workEffortName" value="${workEffort.workEffortName}"></td>
                  </tr>
                  <tr>
                    <td width='26%' align='right'><div class='tabletext'>${uiLabelMap.WorkEffortPriority}</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'>
                      <select name='priority' class='selectBox'>
                        <option>${workEffort.priority?if_exists}</option>
                        <option value=''>--</option>
                        <option>1</option> <option>2</option> <option>3</option>
                        <option>4</option> <option>5</option> <option>6</option>
                        <option>7</option> <option>8</option> <option>9</option>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td width='26%' align='right'><div class='tabletext'>${uiLabelMap.WorkEffortActivityStatus}</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'>
                      <input type='hidden' name='currentStatusId' value='${workEffort.currentStatusId}'>
                      <span class='tabletext'>${currentStatusItem.description}</span>
                      <span class='tabletext'> - ${uiLabelMap.CommonLastUpdated}: ${workEffort.lastStatusUpdate?if_exists?string}</span>
                    </td>
                  </tr>

                  <tr>
                    <td width='26%' align='right'><div class='tabletext'>${uiLabelMap.WorkEffortLocation}</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><input type='text' class='inputBox' size='60' maxlength='255' name="locationDesc" value="${workEffort.locationDesc?if_exists}"></td>
                  </tr>
                  <tr>
                    <td width='26%' align='right'><div class='tabletext'>${uiLabelMap.CommonDescription}</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'>
                      <textarea class="textAreaBox" name='description' cols='50' rows='4'>${workEffort.description?if_exists}</textarea>
                    </td>
                  </tr>

                  <tr>
                    <td width='26%' align='right'><div class='tabletext'>${uiLabelMap.CommonStartDateTime}</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'>
                      <input type='text' class='inputBox' size='30' maxlength='30' name="estimatedStartDate" value="${workEffort.estimatedStartDate}" >
                      <a href="javascript:call_cal(document.taskform.estimatedStartDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
                      <span class='tabletext'>(${uiLabelMap.CommonFormatDateTime})</span>
                    </td>
                  </tr>
                  <tr>
                    <td width='26%' align='right'><div class='tabletext'>${uiLabelMap.CommonEndDateTime}</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'>
                      <input type='text' class='inputBox' size='30' maxlength='30' name="estimatedCompletionDate" value="${workEffort.estimatedCompletionDate?if_exists?string}">
                      <a href="javascript:call_cal(document.taskform.estimatedCompletionDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
                      <span class='tabletext'>(${uiLabelMap.CommonFormatDateTime})</span>
                    </td>
                  </tr>

                  <tr>
                    <td width='26%' align='right'><div class='tabletext'>${uiLabelMap.WorkEffortRevision} #</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>${workEffort.revisionNumber?default("0")}</div></td>
                  </tr>
                  <tr>
                    <td width='26%' align='right'><div class='tabletext'>${uiLabelMap.WorkEffortCreated}</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'>
                      <div class='tabletext'>${workEffort.createdDate?string} ${uiLabelMap.CommonBy} ${workEffort.createdByUserLogin?default("unknown")}</div>
                    </td>
                  </tr>
                  <tr>
                    <td width='26%' align='right'><div class='tabletext'>${uiLabelMap.WorkEffortLastModified}</div></td>
                    <td>&nbsp;</td>
                    <td width='74%'>
                      <div class='tabletext'>${workEffort.lastModifiedDate?string} ${uiLabelMap.CommonBy} ${workEffort.lastModifiedByUserLogin?default("unknown")}</div>
                    </td>
                  </tr>

                  <tr>
                    <td width='26%' align='right'>
                      <input type="submit" name="Update" value="${uiLabelMap.CommonUpdate}">
                    </td>
                    <td>&nbsp;</td>
                    <td width='74%'><div class='tabletext'>&nbsp;</div></td>
                  </tr>
                <#else>
                  <tr>
                    <td colspan='3'>
                      <div class='tabletext'>${uiLabelMap.WorkEffortErrorNotFindActivityId} "${requestParameters.workEffortId?if_exists}"</div>
                    </td>
                  </tr>
                </#if>                
              </table>
              </form>
            <#else>            
              <div class='tabletext'>${uiLabelMap.WorkEffortErrorPermissionViewActivity}.</div>
            </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

<#if partyAssigns?has_content>
  <br>
  <table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <tr>
      <td width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td align=left width='40%' >
              <div class='boxhead'>${uiLabelMap.PartyPartyAssignmentsDetail}</div>
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
              <#list partyAssigns as workEffortPartyAssignment>               
                <#assign statusItem = workEffortPartyAssignment.getRelatedOne("StatusItem")>
                <#assign statusChanges = statusItem.getRelated("MainStatusValidChange")>
                <#assign roleType = workEffortPartyAssignment.getRelatedOne("RoleType")>
                <#assign person = workEffortPartyAssignment.getRelatedOne("Person")>
                <#assign assignedName = workEffortPartyAssignment.partyId>
                <#if person?has_content>
                  <#assign assignedName = person.firstName + " " + person.lastName>
                </#if>
                
                <#-- TODO Add in check for workflow application -->  
                <#if isApplication?default(false)>
                  <form name='openapp' action="<@ofbizUrl>/openapplication</@ofbizUrl>" method=GET style='margin: 0;'>
                    <input type='hidden' name="workEffortId" value="${workEffortPartyAssignment.workEffortId}">
                    <input type='hidden' name="partyId" value="${workEffortPartyAssignment.partyId}">
                    <input type='hidden' name="roleTypeId" value="${workEffortPartyAssignment.roleTypeId}">
                    <input type='hidden' name="fromDate" value="${workEffortPartyAssignment.fromDate}">
                  </form>
                </#if>
               
                <form name="assignform" action="<@ofbizUrl>/updateactivityassign</@ofbizUrl>" method="post" style='margin: 0;'>
                  <table border='0' cellpadding='2' cellspacing='0'>
                    <input type='hidden' name="workEffortId" value="${workEffortPartyAssignment.workEffortId}">
                    <input type='hidden' name="partyId" value="${workEffortPartyAssignment.partyId}">
                    <input type='hidden' name="roleTypeId" value="${workEffortPartyAssignment.roleTypeId}">
                    <input type='hidden' name="fromDate" value="${workEffortPartyAssignment.fromDate}">
    
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>${uiLabelMap.PartyPartyId}</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'><span class='tabletext'>${assignedName?default(workEffortPartyAssignment.partyId)} [${workEffortPartyAssignment.partyId?if_exists}]</span></td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>${uiLabelMap.PartyRoleTypeId}</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'><span class='tabletext'>${roleType.description} [${workEffortPartyAssignment.roleTypeId?if_exists}]</span></td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>${uiLabelMap.CommonFromDate}</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'><span class='tabletext'>${workEffortPartyAssignment.fromDate?if_exists?string}</span></td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>${uiLabelMap.CommonThruDate}</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'>
                        <input type='text' class='inputBox' size='30' maxlength='30' name="thruDate" value="${workEffortPartyAssignment.thruDate?if_exists?string}">
                        <a href="javascript:call_cal(document.assignform.thruDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
                        <span class='tabletext'>(${uiLabelMap.CommonFormatDateTime})</span>
                      </td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>${uiLabelMap.PartyPartyAssignmentStatus}</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'>                                                
                        <select name='statusId' class='selectBox'>                                                   
                          <option value='${statusItem.statusId}'>${statusItem.description}</option>
                          <option value='${statusItem.statusId}'>--</option>
                          <#list statusChanges as statusChange>     
                            <#assign changeItem = statusChange.getRelatedOne("ToStatusItem")>                    
                            <option value='${statusChange.statusIdTo}'>${changeItem.description} (${statusChange.transitionName})</option>
                          </#list>
                        </select>
                        <#if workEffortPartyAssignment?has_content>
                          <span class='tabletext'> - ${uiLabelMap.CommonLastUpdated} : ${workEffortPartyAssignment.statusDateTime?if_exists?string}</span>
                        </#if>
                      </td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>${uiLabelMap.WorkEffortComments}</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'><input type='text' class="inputBox" size='60' maxlength='255' name="comments" value="${workEffortPartyAssignment.comments?if_exists}"></td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>${uiLabelMap.WorkEffortMustRsvp}?</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'>
                        <select name='mustRsvp' class='selectBox'>
                          <#if workEffortPartyAssignment.mustRsvp?exists>
                          <option>${workEffortPartyAssignment.mustRsvp}</option>
                          <option value='${workEffortPartyAssignment.mustRsvp}'>--</option>
                          </#if>
                          <option value="Y">${uiLabelMap.CommonY}</option> <option value="N">${uiLabelMap.CommonN}</option>
                        </select>
                      </td>
                    </tr>
                    <tr>
                      <td width='26%' align=right><div class='tabletext'>${uiLabelMap.WorkEffortExpectation}</div></td>
                      <td>&nbsp;</td>
                      <td width='74%'><span class='tabletext'>${workEffortPartyAssignment.expectationEnumId?default("N/A")}</span></td>
                    </tr>
                    
                    <tr>
                      <td width='26%' align=right>
                        <input type="submit" name="Update" value="${uiLabelMap.CommonUpdate}">
                        <#if isApplication?default(false)>
                        &nbsp;
                        <input type="button" onclick="javascript:document.openapp.submit()" value="${uiLabelMap.WorkEffortOpenApplication}">
                        </#if>
                      </td>
                      <td>&nbsp;</td>
                      <td width='74%'><div class='tabletext'>&nbsp;</div></td>
                    </tr>
                  </table>
                </form>
                <#if workEffortPartyAssignment_has_next>
                  <hr class='sepbar'>
                </#if>
              </#list>
            </td>
          </tr>
        </table>
      </td>
    </td>
</table>
</#if>

