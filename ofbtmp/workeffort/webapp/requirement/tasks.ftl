<#--
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Johan Isacsson (conversion of jsp created by Andy Zeneski)
 *@created    May 13, 2003
 *@version    1.0
-->


<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Task List <span class="tabletext">For Requirement: <a href="<@ofbizUrl>/requirement?requirementId=${requirementId}</@ofbizUrl>" class="lightbuttontext">[${requirementId}]</a></span></div>
          </TD>
          <TD align=right width='60%'>
            <A href='<@ofbizUrl>/requirementlist</@ofbizUrl>' class='lightbuttontext'>[Requirement&nbsp;List]</A>
            <A href='<@ofbizUrl>/requirement</@ofbizUrl>' class='lightbuttontext'>[New&nbsp;Requirement]</A>
            <A href='<@ofbizUrl>/task?requirementId=${requirementId}</@ofbizUrl>' class='lightbuttontext'>[Add&nbsp;Task]</A>
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
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <TD><DIV class='tabletext'><b>Start Date/Time</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Status</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Task Name</b></DIV></TD>
                  <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                </TR>
                <TR><TD colspan='5'><HR class='sepbar'></TD></TR>
                <#if tasks?has_content>
                <#list tasks as workEffort>
                  <TR>
                    <TD><DIV class='tabletext'>${workEffort.estimatedStartDate}</DIV></TD>
                    <TD><DIV class='tabletext'>${workEffort.priority?if_exists}</DIV></TD>
                    <#assign currentStatusItem = delegator.findByPrimaryKeyCache("StatusItem", Static["org.ofbiz.core.util.UtilMisc"].toMap("statusId", workEffort.currentStatusId))>                    
                    <TD><DIV class='tabletext'>${(currentStatusItem.description)?if_exists}</DIV></TD>
                    <TD><A class='buttontext' href='<@ofbizUrl>/task?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                        ${workEffort.workEffortName}</a></DIV></TD>
                    <TD align=right width='1%'><A class='buttontext' href='<@ofbizUrl>/task?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                        Edit&nbsp;[${workEffort.workEffortId}]</a></DIV></TD>
                  </TR>
                </#list>
                <#else>
                 <tr><td><div class="tabletext">No tasks currently associated with this requirement.</div></td></tr>
                 </#if>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR><TD colspan='5'><HR class='sepbar'></TD></TR>
  <TR>
    <TD>
      <table width="50%" cellpadding="2" cellspacing="0" border="0">
        <tr valign="middle"><td>
          <form method="post" action="<@ofbizUrl>/assoctask</@ofbizUrl>">
            <input type="hidden" name="requirementId" value="${requirementId}">
            <span class="tabletext">Add an existing task:&nbsp;(WorkEffortId)&nbsp;</span>
            <input type="text" name="workEffortId" class="inputBox" size="10" style="font-size: small;">
            <input type="submit" style="font-size: small;" value="Add Task">
          </form>
        </td></tr>
      </table>
    </TD>
  </TR>
</TABLE>
