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


<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;My Current Task List</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<@ofbizUrl>/mytasks</@ofbizUrl>' class='lightbuttontextdisabled'>[Task&nbsp;List]</A>
            <A href='<@ofbizUrl>/task</@ofbizUrl>' class='lightbuttontext'>[New&nbsp;Task]</A>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <#if (activities.size() > 0)>
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
            <tr>
              <td>
                  <div class='head3'>Workflow Activities Assigned to User</div>
                  <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                    <TR>
                      <TD><DIV class='tabletext'><b>Start&nbsp;Date/Time</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Status</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>My&nbsp;Status</b></DIV></TD>
                      <%-- <TD><DIV class='tabletext'><b>Party&nbsp;ID</b></DIV></TD> --%>
                      <TD><DIV class='tabletext'><b>Role&nbsp;ID</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Name</b></DIV></TD>
                      <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                    </TR>
                    <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
                    <#list activities as workEffort>
                      <TR>
                        <TD><DIV class='tabletext'>${workEffort.estimatedStartDate}</DIV></TD>
                        <TD><DIV class='tabletext'>${workEffort.priority}</DIV></TD>
                        <TD><DIV class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId"))).description)?if_exists}</DIV></TD>
                        <TD><DIV class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("statusId"))).description)?if_exists}</DIV></TD>
                        <%-- <TD><DIV class='tabletext'>${workEffort.partyId}</DIV></TD> --%>
                        <TD><DIV class='tabletext'>${workEffort.roleTypeId}</DIV></TD>
                        <TD><A class='buttontext' href='<@ofbizUrl>/activity?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                            ${workEffort.workEffortName}</a></DIV></TD>
                        <TD align=right><A class='buttontext' href='<@ofbizUrl>/activity?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                            Edit&nbsp;[${workEffort.workEffortId}]</a></DIV></TD>
                      </TR>
                    </#list>
                  </TABLE>
              </td>
            </tr>
          </table>
        </TD>
      </TR>
  </#if>
  <#if (roleActivities.size() > 0)>
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
            <tr>
              <td>
                  <div class='head3'>Workflow Activities Assigned to User Role</div>
                  <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                    <TR>
                      <TD><DIV class='tabletext'><b>Start&nbsp;Date/Time</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Status</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>My&nbsp;Status</b></DIV></TD>
                      <%-- <TD><DIV class='tabletext'><b>Party&nbsp;ID</b></DIV></TD> --%>
                      <TD><DIV class='tabletext'><b>Role&nbsp;ID</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Name</b></DIV></TD>
                      <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                    </TR>
                    <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
                    <#list roleActivities as workEffort>
                      <TR>
                        <TD><DIV class='tabletext'>${workEffort.estimatedStartDate}</DIV></TD>
                        <TD><DIV class='tabletext'>${workEffort.priority}</DIV></TD>
                        <TD><DIV class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId"))).description)?if_exists}</DIV></TD>
                        <TD><DIV class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("statusId"))).description)?if_exists}</DIV></TD>
                        <%-- <TD><DIV class='tabletext'>${workEffort.partyId}</DIV></TD> --%>
                        <TD><DIV class='tabletext'>${workEffort.roleTypeId}</DIV></TD>
                        <TD><A class='buttontext' href='<@ofbizUrl>/activity?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                            ${workEffort.workEffortName}</a></DIV></TD>
                        <TD align=right><A class='buttontext' href='<@ofbizUrl>/acceptRoleAssignment?workEffortId=${workEffort.workEffortId}&partyId=${workEffort.partyId}&roleTypeId=${workEffort.roleTypeId}&fromDate=<ofbiz:inputvalue field="fromDate" entityAttr="workEffort"/></@ofbizUrl>'>
                            Accept&nbsp;Assignment&nbsp;[${workEffort.workEffortId}]</a></DIV></TD>
                      </TR>
                    </#list>
                  </TABLE>
              </td>
            </tr>
          </table>
        </TD>
      </TR>
  </#if>
  <#if (groupActivities.size() > 0)>
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
            <tr>
              <td>
                  <div class='head3'>Workflow Activities Assigned to User Group</div>
                  <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                    <TR>
                      <TD><DIV class='tabletext'><b>Start&nbsp;Date/Time</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Status</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>My&nbsp;Status</b></DIV></TD>
                      <TD><DIV class='tabletext'><b>Group&nbsp;Party&nbsp;ID</b></DIV></TD>
                      <%-- <TD><DIV class='tabletext'><b>Role&nbsp;ID</b></DIV></TD> --%>
                      <TD><DIV class='tabletext'><b>Activity&nbsp;Name</b></DIV></TD>
                      <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                    </TR>
                    <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
                    <#list groupActivities as workEffort>
                      <TR>
                        <TD><DIV class='tabletext'>${workEffort.estimatedStartDate}</DIV></TD>
                        <TD><DIV class='tabletext'>${workEffort.priority}</DIV></TD>
                        <TD><DIV class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("currentStatusId"))).description)?if_exists}</DIV></TD>
                        <TD><DIV class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", workEffort.getString("statusId"))).description)?if_exists}</DIV></TD>
                        <TD><DIV class='tabletext'>${workEffort.groupPartyId}</DIV></TD>
                        <%-- <TD><DIV class='tabletext'>${workEffort.roleTypeId}</DIV></TD> --%>
                        <TD><A class='buttontext' href='<@ofbizUrl>/activity?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                            ${workEffort.workEffortName}</a></DIV></TD>
                        <TD align=right><A class='buttontext' href='<@ofbizUrl>/acceptassignment?workEffortId=${workEffort.workEffortId}&partyId=<ofbiz:entityfield field="partyId" attribute="workEffort"/>&roleTypeId=<ofbiz:entityfield field="roleTypeId" attribute="workEffort"/>&fromDate=<ofbiz:inputvalue field="fromDate" entityAttr="workEffort"/></@ofbizUrl>'>
                            Accept&nbsp;Assignment&nbsp;[${workEffort.workEffortId}]</a></DIV></TD>
                      </TR>
                    </#list>
                  </TABLE>
              </td>
            </tr>
          </table>
        </TD>
      </TR>
  </#if>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
              <div class='head3'>Assigned Tasks</div>
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <TD><DIV class='tabletext'><b>Start Date/Time</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Status</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Task Name</b></DIV></TD>
                  <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                </TR>
                <TR><TD colspan='5'><HR class='sepbar'></TD></TR>
                <#list tasks as workEffort>
                  <TR>
                    <TD><DIV class='tabletext'>${workEffort.estimatedStartDate}</DIV></TD>
                    <TD><DIV class='tabletext'>${workEffort.priority?if_exists}</DIV></TD>
                    <TD><DIV class='tabletext'>${(delegator.findByPrimaryKeyCache("StatusItem", Static["org.ofbiz.core.util.UtilMisc"].toMap("statusId", workEffort.getString("currentStatusId"))).description)?if_exists}</DIV></TD>
                    <TD><A class='buttontext' href='<@ofbizUrl>/task?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                        ${workEffort.workEffortName}</a></DIV></TD>
                    <TD align=right width='1%'><A class='buttontext' href='<@ofbizUrl>/task?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                        Edit&nbsp;[${workEffort.workEffortId}]</a></DIV></TD>
                  </TR>
                </#list>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
