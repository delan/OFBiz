<#if requestAttributes.errorMsgReq?exists>
<div class='errorMessage'>${requestAttributes.errorMsgReq}</div><br>
</#if>
<#if requestAttributes.errorMsgSes?exists>
<div class='errorMessage'>${requestAttributes.errorMsgSes}</div><br>
</#if>
<#if requestAttributes.eventMsgReq?exists>
<div class='eventMessage'>${requestAttributes.eventMsgReq}</div><br>
</#if>
