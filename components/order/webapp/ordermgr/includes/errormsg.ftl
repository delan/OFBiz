<#-- display the error messages -->
<#if requestAttributes.errorMsgReq?has_content>
<div class="errorMessage">${requestAttributes.errorMsgReq}</div><br>
</#if>
<#if requestAttributes.errorMsgListReq?has_content>
<ul>
  <#list requestAttributes.errorMsgListReq as errorMsg>
    <li class="errorMessage">${errorMsg}</li>
  </#list>
</ul>
</#if>
<#if sessionAttributes.errorMsgSes?has_content>
<div class="errorMessage">${sessionAttributes.errorMsgSes}</div><br>
</#if>
<#if requestAttributes.eventMsgReq?has_content>
<div class="eventMessage">${requestAttributes.eventMsgReq}</div><br>
</#if>
<#if requestAttributes.eventMsgListReq?has_content>
<ul>
  <#list requestAttributes.eventMsgListReq as eventMsg>
    <li class="eventMessage">${eventMsg}</li>
  </#list>
</ul>
</#if>
