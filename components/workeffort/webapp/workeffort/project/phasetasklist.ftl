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

<table border=0 cellspacing='0' cellpadding='0'><tr>
  <td width='45%' valign=top>
    <TABLE border=0 cellspacing='0' cellpadding='0' class='boxoutside' width='100%'>
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
            <tr>
              <TD align=left width=>
                <div class='boxhead'>&nbsp;<b>Project:</b>&nbsp;<A class='boxhead' href='<@ofbizUrl>/phaselist?projectWorkEffortId=${projectWorkEffortId}</@ofbizUrl>'>${projectWorkEffort.workEffortName}</a></div>
              </TD>
            </tr>
          </table>
        </TD>
      </TR>
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>Project Status:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top>${(projectWorkEffortStatus.description)?if_exists}</td>
                    </tr>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>Description:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top>${(projectWorkEffort.description)?if_exists}
                    </tr>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>Start Date/Time:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top>${(projectWorkEffort.estimatedStartDate)?if_exists}
                      </td>
                    </tr>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>End Date/Time:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top>${(projectWorkEffort.estimatedCompletionDate)?if_exists}
                      </td>
                    </tr>
          </table>
        </TD>
      </TR>
    </TABLE>
  </td>
  <td width='10%' valign=top>&nbsp;</td>
  <td width='45%' valign=top>
    <TABLE border=0 cellspacing='0' cellpadding='0' class='boxoutside' width='100%'>
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
            <tr>
              <TD align=left>
                <div class='boxhead'>&nbsp;<b>Phase:</b>&nbsp;${(phaseWorkEffort.workEffortName)?if_exists}</div>
              </TD>
            </tr>
          </table>
        </TD>
      </TR>
      <TR>
        <TD width='100%'>
          <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>Phase Status:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top>${(phaseWorkEffortStatus.description)?if_exists}</td>
                    </tr>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>Description:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top>${(phaseWorkEffort.description)?if_exists}
                    </tr>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>Start Date/Time:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top>${(phaseWorkEffort.estimatedStartDate)?if_exists}
                      </td>
                    </tr>
                    <tr>
                      <td align=right valign=top><div class='tabletext'><nobr>End Date/Time:</nobr></div></td>
                      <td>&nbsp;</td>
                      <td valign=top>${(phaseWorkEffort.estimatedCompletionDate)?if_exists}
                      </td>
                    </tr>
          </table>
        </TD>
      </TR>
    </TABLE>
  </td>
</tr></table>
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Phase Tasks</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<@ofbizUrl>/editphasetask?phaseWorkEffortId=${phaseWorkEffortId}</@ofbizUrl>' class='lightbuttontext'>[New&nbsp;Task]</A>
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
                  <TD><DIV class='tabletext'><b>Start Date/Time</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Status</b></DIV></TD>
                  <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                </TR>
                <TR><TD colspan='6'><HR class='sepbar'></TD></TR>
                <#list tasks as workEffort>
                  <TR>
                    <TD><DIV class='tabletext'>${workEffort.workEffortName}</DIV></TD>
                    <TD><DIV class='tabletext'>${workEffort.description}</DIV></TD>
                    <TD><DIV class='tabletext'>${workEffort.estimatedStartDate?if_exists}</DIV></TD>
                    <TD><DIV class='tabletext'>${workEffort.priority?if_exists}</DIV></TD>
                    <#assign currentStatusItem = delegator.findByPrimaryKeyCache("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", workEffort.currentStatusId))>                    
                    <TD><DIV class='tabletext'>${(currentStatusItem.description)?if_exists}</DIV></TD>
                    <TD align=right width='1%'><A class='buttontext' href='<@ofbizUrl>/editphasetask?workEffortId=${workEffort.workEffortId}&phaseWorkEffortId=${phaseWorkEffortId}</@ofbizUrl>'>
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
