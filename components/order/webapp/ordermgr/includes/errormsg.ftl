<#if requestAttributes.errorMsgReq?has_content>
<div class='errorMessage'>${requestAttributes.errorMsgReq}</div><br>
</#if>
<#if requestAttributes.errorMsgSes?has_content>
<div class='errorMessage'>${requestAttributes.errorMsgSes}</div><br>
</#if>
<#if requestAttributes.eventMsgReq?has_content>
<div class='eventMessage'>${requestAttributes.eventMsgReq}</div><br>
</#if>
