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
 *@author     Johan Isacsson (conversion of jsp created by David E. Jones)
 *@created    May 13 2003
 *@version    1.0
-->


<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align=left width='40%' >
            <div class='boxhead'>&nbsp;My Current Task List</div>
          </td>
          <td align=right width='60%'>
            <A href='<@ofbizUrl>/mytasks</@ofbizUrl>' class='lightbuttontextdisabled'>[Task&nbsp;List]</A>
            <A href='<@ofbizUrl>/task</@ofbizUrl>' class='lightbuttontext'>[New&nbsp;Task]</A>
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
              <div class='head3'>Assigned Tasks</div>
              <table width='100%' cellpadding='2' cellspacing='0' border='0'>
                <tr>
                  <td><div class='tabletext'><b>Start Date/Time</b></div></td>
                  <td><div class='tabletext'><b>Priority</b></div></td>
                  <td><div class='tabletext'><b>Status</b></div></td>
                  <td><div class='tabletext'><b>Task Name</b></div></td>
                  <td align=right><div class='tabletext'><b>Edit</b></div></td>
                </tr>
                <tr><td colspan='5'><HR class='sepbar'></td></tr>
                <#list tasks as workEffort>
                  <tr>
                    <td><div class='tabletext'>${(workEffort.estimatedStartDate.toString())?if_exists}</div></td>
                    <td><div class='tabletext'>${workEffort.priority?if_exists}</div></td>
                    <td><div class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", Static["org.ofbiz.core.util.UtilMisc"].toMap("statusId", workEffort.getString("currentStatusId"))).description)?if_exists}</div></td>
                    <td><A class='buttontext' href='<@ofbizUrl>/task?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                        ${workEffort.workEffortName}</a></div></td>
                    <td align=right width='1%'><A class='buttontext' href='<@ofbizUrl>/task?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                        Edit&nbsp;[${workEffort.workEffortId}]</a></div></td>
                  </tr>
                </#list>
              </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <#if (activities.size() > 0)>
      <tr>
        <td width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
            <tr>
              <td>
                  <div class='head3'>Workflow Activities Assigned to User</div>
                  <table width='100%' cellpadding='2' cellspacing='0' border='0'>
                    <tr>
                      <td><div class='tabletext'><b>Start&nbsp;Date/Time</b></div></td>
                      <td><div class='tabletext'><b>Priority</b></div></td>
                      <td><div class='tabletext'><b>Activity&nbsp;Status</b></div></td>
                      <td><div class='tabletext'><b>My&nbsp;Status</b></div></td>
                      <#-- <td><div class='tabletext'><b>Party&nbsp;ID</b></div></td> -->
                      <td><div class='tabletext'><b>Role&nbsp;ID</b></div></td>
                      <td><div class='tabletext'><b>Activity&nbsp;Name</b></div></td>
                      <td align=right><div class='tabletext'><b>Edit</b></div></td>
                    </tr>
                    <tr><td colspan='8'><HR class='sepbar'></td></tr>
                    <#list activities as workEffort>
                      <tr>
                        <td><div class='tabletext'>${(workEffort.estimatedStartDate.toString())?if_exists}</div></td>
                        <td><div class='tabletext'>${workEffort.priority}</div></td>
                        <td><div class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId"))).description)?if_exists}</div></td>
                        <td><div class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("statusId"))).description)?if_exists}</div></td>
                        <#-- <td><div class='tabletext'>${workEffort.partyId}</div></td> -->
                        <td><div class='tabletext'>${workEffort.roleTypeId}</div></td>
                        <td><A class='buttontext' href='<@ofbizUrl>/activity?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                            ${workEffort.workEffortName}</a></div></td>
                        <td align=right><A class='buttontext' href='<@ofbizUrl>/activity?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                            Edit&nbsp;[${workEffort.workEffortId}]</a></div></td>
                      </tr>
                    </#list>
                  </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
  </#if>
  <#if (roleActivities.size() > 0)>
      <tr>
        <td width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
            <tr>
              <td>
                  <div class='head3'>Workflow Activities Assigned to User Role</div>
                  <table width='100%' cellpadding='2' cellspacing='0' border='0'>
                    <tr>
                      <td><div class='tabletext'><b>Start&nbsp;Date/Time</b></div></td>
                      <td><div class='tabletext'><b>Priority</b></div></td>
                      <td><div class='tabletext'><b>Activity&nbsp;Status</b></div></td>
                      <td><div class='tabletext'><b>My&nbsp;Status</b></div></td>
                      <#-- <td><div class='tabletext'><b>Party&nbsp;ID</b></div></td> -->
                      <td><div class='tabletext'><b>Role&nbsp;ID</b></div></td>
                      <td><div class='tabletext'><b>Activity&nbsp;Name</b></div></td>
                      <td align=right><div class='tabletext'><b>Edit</b></div></td>
                    </tr>
                    <tr><td colspan='8'><HR class='sepbar'></td></tr>
                    <#list roleActivities as workEffort>
                      <tr>
                        <td><div class='tabletext'>${(workEffort.estimatedStartDate.toString())?if_exists}</div></td>
                        <td><div class='tabletext'>${workEffort.priority?if_exists}</div></td>
                        <td><div class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId"))).description)?if_exists}</div></td>
                        <td><div class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("statusId"))).description)?if_exists}</div></td>
                        <#-- <td><div class='tabletext'>${workEffort.partyId}</div></td> -->
                        <td><div class='tabletext'>${workEffort.roleTypeId}</div></td>
                        <td><A class='buttontext' href='<@ofbizUrl>/activity?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                            ${workEffort.workEffortName}</a></div></td>
                        <td align=right><A class='buttontext' href='<@ofbizUrl>/acceptRoleAssignment?workEffortId=${workEffort.workEffortId}&partyId=${workEffort.partyId}&roleTypeId=${workEffort.roleTypeId}&fromDate=${workEffort.fromDate.toString()}</@ofbizUrl>'>
                            Accept&nbsp;Assignment&nbsp;[${workEffort.workEffortId}]</a></div></td>
                      </tr>
                    </#list>
                  </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
  </#if>
  <#if (groupActivities.size() > 0)>
      <tr>
        <td width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
            <tr>
              <td>
                  <div class='head3'>Workflow Activities Assigned to User Group</div>
                  <table width='100%' cellpadding='2' cellspacing='0' border='0'>
                    <tr>
                      <td><div class='tabletext'><b>Start&nbsp;Date/Time</b></div></td>
                      <td><div class='tabletext'><b>Priority</b></div></td>
                      <td><div class='tabletext'><b>Activity&nbsp;Status</b></div></td>
                      <td><div class='tabletext'><b>My&nbsp;Status</b></div></td>
                      <td><div class='tabletext'><b>Group&nbsp;Party&nbsp;ID</b></div></td>
                      <#-- <td><div class='tabletext'><b>Role&nbsp;ID</b></div></td> -->
                      <td><div class='tabletext'><b>Activity&nbsp;Name</b></div></td>
                      <td align=right><div class='tabletext'><b>Edit</b></div></td>
                    </tr>
                    <tr><td colspan='8'><HR class='sepbar'></td></tr>
                    <#list groupActivities as workEffort>
                      <tr>
                        <td><div class='tabletext'>${(workEffort.estimatedStartDate.toString())?if_exists}</div></td>
                        <td><div class='tabletext'>${workEffort.priority}</div></td>
                        <td><div class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId"))).description)?if_exists}</div></td>
                        <td><div class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("statusId"))).description)?if_exists}</div></td>
                        <td><div class='tabletext'>${workEffort.groupPartyId}</div></td>
                        <#-- <td><div class='tabletext'>${workEffort.roleTypeId}</div></td> -->
                        <td><A class='buttontext' href='<@ofbizUrl>/activity?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                            ${workEffort.workEffortName}</a></div></td>
                        <td align=right><A class='buttontext' href='<@ofbizUrl>/acceptassignment?workEffortId=${workEffort.workEffortId}&partyId=${workEffort.partyId}&roleTypeId=${workEffort.roleTypeId}&fromDate=${workEffort.fromDate}</@ofbizUrl>'>
                            Accept&nbsp;Assignment&nbsp;[${workEffort.workEffortId}]</a></div></td>
                      </tr>
                    </#list>
                  </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
  </#if>
</table>
