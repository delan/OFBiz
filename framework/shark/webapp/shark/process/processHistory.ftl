
<div class="head2">Process History</div>
<#if historyList?has_content>
  <div>&nbsp;</div>
  <div class="tabletext"><b>Process :</b> ${process.name()} - ${process.description()?default("N/A")} [${process.key()}]</div>
  <table cellpadding="2" cellspacing="0" border="1">
    <tr>
      <td><div class="tableheadtext">Time</div></td>
      <td><div class="tableheadtext">Event</div></td>
    </tr>
    <#list historyList as history>
      <#assign time = history.time_stamp().getTime()>
      <tr>
        <td align="left"><div class="tabletext">${Static["org.ofbiz.base.util.UtilDateTime"].getTimestamp(time)}</div></td>
        <td align="left">
          <div class="tabletext">
            <#assign eventType = history.event_type()>
            ${eventType}
            <#if eventType == "processStateChanged">
              [${history.old_state()} -> ${history.new_state()}]
            </#if>
          </div>
        </td>
      </tr>
    </#list>
  </table>
<#else>
  <div class="tabletext">No history available.</div>
</#if>