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
 *@author     Johan Isacsson (conversion of jsp created by Dustin Caldwell (from code by David Jones))
 *@created    May 13, 2003
 *@version    1.0
-->


<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left>
            <div class='boxhead'>&nbsp;Projects</div>
          </TD>
          <TD align=right>
            <table><tr>
              <TD>
                <#if showAllProjects = "true">
                  <A href='<@ofbizUrl>/projectlist</@ofbizUrl>' class='lightbuttontext'>[Show&nbsp;Active]</A>
                <#else>
                  <A href='<@ofbizUrl>/projectlist?ShowAllProjects=true</@ofbizUrl>' class='lightbuttontext'>[Show&nbsp;All]</A>
                </#if>
              </TD>
              <TD align=right>
                <A href='<@ofbizUrl>/editproject</@ofbizUrl>' class='lightbuttontext'>[New&nbsp;Project]</A>
              </TD>
            </tr></table>
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
<!--              <div class='head3'>Assigned Tasks</div>-->
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <TD><DIV class='tabletext'><b>Name</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Description</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Start Date</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Status</b></DIV></TD>
                  <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                </TR>
                <TR><TD colspan='5'><HR class='sepbar'></TD></TR>
                <#list projects as workEffort>
                  <TR onclick="javascript:window.location='<@ofbizUrl>/phaselist?projectWorkEffortId=${workEffort.workEffortId}</@ofbizUrl>'">
                    <TD><A class='buttontext' href='<@ofbizUrl>/phaselist?projectWorkEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                        ${workEffort.workEffortName}</a></DIV></TD>
                    <TD><A class='buttontext' href='<@ofbizUrl>/phaselist?projectWorkEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                        ${workEffort.description?if_exists}</a></DIV></TD>
                    <TD><DIV class='tabletext'>${workEffort.estimatedStartDate?datetime?string.short}</DIV></TD>
                    <#assign currentStatusItem = delegator.findByPrimaryKeyCache("StatusItem", Static["org.ofbiz.core.util.UtilMisc"].toMap("statusId", workEffort.currentStatusId))>                    
                    <TD><DIV class='tabletext'>${(currentStatusItem.description)?if_exists}</DIV></TD>
                    <TD align=right width='1%'><A class='buttontext' href='<@ofbizUrl>/editproject?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                        [Edit]</a></DIV></TD>
                  </TR>
                </#list>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
