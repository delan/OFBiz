
<div class="head2">Process Detail List</div>
<#if processes?has_content>
  <#assign proc1 = processes[0]>
  <div>&nbsp;</div>
  <div class="tabletext"><b>Process :</b> ${proc1.name()} - ${proc1.description()?default("N/A")}</div>

  <table cellpadding="2" cellspacing="0" border="1">
    <tr>
      <td><div class="tableheadtext">ID</div></td>
      <td><div class="tableheadtext">State</div></td>
      <td><div class="tableheadtext">Priority</div></td>
      <td><div class="tableheadtext">Steps</div></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <#list processes as proc>
      <tr>
        <td align="left"><div class="tabletext">${proc.key()}</div></td>
        <td align="left"><div class="tabletext">${proc.state()}</div></td>
        <td align="center"><div class="tabletext">${proc.priority()}</div></td>
        <td align="center"><div class="tabletext">${proc.how_many_step()}</div></td>
        <#if proc.state() != "open.not_running.not_started">
          <td align="center"><a href="<@ofbizUrl>/processHistory?process=${proc.key()}</@ofbizUrl>" class="buttontext">History</a></td>
        <#else>
          <td>&nbsp;</td>
        </#if>
        <#if proc.state() == "open.running">
          <td align="center"><a href="<@ofbizUrl>/processDetailList?manager=${manager?if_exists}&terminate=${proc.key()}</@ofbizUrl>" class="buttontext">Terminate</a></td>
          <td align="center"><a href="<@ofbizUrl>/processDetailList?manager=${manager?if_exists}&abort=${proc.key()}</@ofbizUrl>" class="buttontext">Abort</a></td>
          <td align="center"><a href="<@ofbizUrl>/processDetailList?manager=${manager?if_exists}&suspend=${proc.key()}</@ofbizUrl>" class="buttontext">Suspend</a></td>
          <td align="center"><a href="<@ofbizUrl>/processSteps?process=${proc.key()}</@ofbizUrl>" class="buttontext">Activities</a></td>
        <#else>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <#if proc.state() == "open.not_running.not_started">
            <td align="center"><a href="<@ofbizUrl>/processDetailList?manager=${manager?if_exists}&start=${proc.key()}</@ofbizUrl>" class="buttontext">Start</a></td>
          <#elseif proc.state() == "open.not_running.suspended">
            <td align="center"><a href="<@ofbizUrl>/processDetailList?manager=${manager?if_exists}&resume=${proc.key()}</@ofbizUrl>" class="buttontext">Resume</a></td>
          <#else>
            <td>&nbsp;</td>
          </#if>
        </#if>
      </tr>
    </#list>
  </table>
<#else>
  <div class="tabletext">No running processes.</div>
</#if>