
<div class="head2">Process Steps</div>
<#if steps?has_content>
  <div>&nbsp;</div>
  <div class="tabletext"><b>Process :</b> ${process.name()} - ${process.description()?default("N/A")} [${process.key()}]</div>

  <table cellpadding="2" cellspacing="0" border="1">
    <tr>
      <td><div class="tableheadtext">ID</div></td>
      <td><div class="tableheadtext">Name</div></td>
      <td><div class="tableheadtext">Description</div></td>
      <td><div class="tableheadtext">State</div></td>
      <td><div class="tableheadtext">Last State</div></td>
      <td><div class="tableheadtext">Priority</div></td>
      <td><div class="tableheadtext">Assignments</div></td>

    </tr>
    <#list steps as step>
      <#assign time = step.last_state_time().getTime()>
      <tr>
        <td align="left"><div class="tabletext">${step.key()}</div></td>
        <td align="left"><div class="tabletext">${step.name()}</div></td>
        <td align="left"><div class="tabletext">${step.description()}</div></td>
        <td align="left"><div class="tabletext">${step.state()}</div></td>
        <td align="left"><div class="tabletext">${Static["org.ofbiz.base.util.UtilDateTime"].getTimestamp(time)}</div></td>
        <td align="center"><div class="tabletext">${step.priority()}</div></td>
        <td align="center"><div class="tabletext">${step.how_many_assignment()}</div></td>
      </tr>
    </#list>
  </table>
<#else>
  <div class="tabletext">No running activities.</div>
</#if>