<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align=left width='40%' class="boxhead">Calendar Up-Coming Events View</td>
          <td align=right width='60%'>		  
            <a href='<@ofbizUrl>/day</@ofbizUrl>' class='submenutext'>Day&nbsp;View</a><a href='<@ofbizUrl>/week</@ofbizUrl>' class='submenutext'>Week&nbsp;View</a><a href='<@ofbizUrl>/month</@ofbizUrl>' class='submenutext'>Month&nbsp;View</a><a href='<@ofbizUrl>/upcoming</@ofbizUrl>' class='submenutextdisabled'>Upcoming&nbsp;Events</a><a href='<@ofbizUrl>/event</@ofbizUrl>' class='submenutextright'>New&nbsp;Event</a>
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
          <#if days?has_content>
              <table width='100%' cellpadding='2' cellspacing='0' border='0'>
                <tr>
                  <td><div class='tabletext'><b>Start Date/Time</b></div></td>
                  <td><div class='tabletext'><b>End Date/Time</b></div></td>
                  <td><div class='tabletext'><b>Event Name</b></div></td>
                </tr>                
                <#list days as workEfforts>
                  <tr><td colspan='3'><hr class='sepbar'></td></tr>
                  <#list workEfforts as workEffort>
                    <tr>
                      <td><div class='tabletext'>${workEffort.estimatedStartDate}</div></td>
                      <td><div class='tabletext'>${workEffort.estimatedCompletionDate}</div></td>
                      <td><a class='buttontext' href='<@ofbizUrl>/event?workEffortId=${workEffort.workEffortId}</@ofbizUrl>'>
                          ${workEffort.workEffortName}</a></DIV></td>
                    </tr>
                    </#list>
                  <#if workEfforts_has_next><tr><td colspan='3'><hr></td></tr></#if>
                </#list>
              </table>
            <#else>
              <div class='tabletext'>No events found.</div>
            </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>