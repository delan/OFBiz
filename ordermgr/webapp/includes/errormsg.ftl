<#if requestAttributes.errorMsgReq?exists>
<br><div class='errorMessage'>${requestAttributes.errorMsgReq}</div><br>
</#if>
<#if requestAttributes.errorMsgSes?exists>
<br><div class='errorMessage'>${requestAttributes.errorMsgSes}</div><br>
</#if>
<#if requestAttributes.eventMsgReq?exists>
<br><div class='eventMessage'>${requestAttributes.eventMsgReq}</div><br>
</#if>
