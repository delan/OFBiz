
<#-- special error message override -->
<#if requestAttributes.serviceValidationException?exists>
  <#assign serviceException = requestAttributes.serviceValidationException>
  <#assign serviceName = serviceException.getServiceName()?if_exists>
  <#assign missingList = serviceException.getMissingFields()?if_exists>
  <#assign extraList = serviceException.getExtraFields()?if_exists>

  <#--  need if statement for EACH service (see the controller.xml file for service names) -->
  <#if serviceName?has_content && serviceName == "createPartyContactMechPurpose">
    <#-- create the inital message prefix -->
    <#assign message = "The following required fields where found empty:">

    <#-- loop through all the missing fields -->
    <#list missingList as missing>
      <#--
           check for EACH required field (see the service definition)
           then append a message for the missing field; some fields may be
           and not needed; this example show ALL fields for the service.
           ** The value inside quotes must match 100% case included.
       -->

      <#if missing == "partyId">
        <#assign message = message + "<li>Party ID</li>">
      </#if>
      <#if missing == "contactMechId">
        <#assign message = message + "<li>ContactMech ID</li>">
      </#if>
      <#if missing == "contactMechPurposeTypeId">
        <#assign message = message + "<li>Contact Purpose</li>">
      </#if>
    </#list>

    <#-- this will replace the current error message with the new one -->
    <#assign errorMsgReq = message>
  </#if>
</#if>

<#-- display the error messages -->
<#if requestAttributes.errorMessageList?has_content>
<div class="errorMessage">The following errors occurred:</div><br>
<ul>
  <#list requestAttributes.errorMessageList as errorMsg>
    <li class="errorMessage">${errorMsg}</li>
  </#list>
</ul>
</#if>
<#if requestAttributes.eventMessageList?has_content>
<div class="eventMessage">The following occurred:</div><br>
<ul>
  <#list requestAttributes.eventMessageList as eventMsg>
    <li class="eventMessage">${eventMsg}</li>
  </#list>
</ul>
</#if>
