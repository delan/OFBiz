<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Calendar Up-Coming Events View</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<@ofbizUrl>/day</@ofbizUrl>' class='lightbuttontext'>[Day&nbsp;View]</A>
            <A href='<@ofbizUrl>/week</@ofbizUrl>' class='lightbuttontext'>[Week&nbsp;View]</A>
            <A href='<@ofbizUrl>/month</@ofbizUrl>' class='lightbuttontext'>[Month&nbsp;View]</A>
            <A href='<@ofbizUrl>/upcoming</@ofbizUrl>' class='lightbuttontextdisabled'>[Upcoming&nbsp;Events]</A>
            <A href='<@ofbizUrl>/event</@ofbizUrl>' class='lightbuttontext'>[New&nbsp;Event]</A>
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
          <#if days?has_content>
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <TD><DIV class='tabletext'><b>Start Date/Time</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>End Date/Time</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Event Name</b></DIV></TD>
                </TR>                
                <#list days as workEfforts>
                  <TR><TD colspan='3'><HR class='sepbar'></TD></TR>
                  <#list workEfforts as workEffort>
                    <TR>
                      <TD><DIV class='tabletext'>${workEffort.estimatedStartDate}</DIV></TD>
                      <TD><DIV class='tabletext'>${workEffort.estimatedCompletionDate}</DIV></TD>
                      <TD><A class='buttontext' href='<@ofbizUrl>/event?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                          ${workEffort.workEffortName}</a></DIV></TD>
                    </TR>
                    </#list>
                  <#if workEfforts_has_next><TR><TD colspan='3'><HR></TD></TR></#if>
                </#list>
              </TABLE>
            <#else>
              <div class='tabletext'>No events found.</div>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>