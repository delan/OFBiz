
<#-- get the default messages -->
<#assign errorMsgReq = requestAttributes.errorMsgReq?if_exists>
<#assign errorMsgSes = requestAttributes.errorMsgSes?if_exists>
<#assign eventMsgReq = requestAttributes.eventMsgReq?if_exists>

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
<#if errorMsgReq?has_content>
<div class='errorMessage'>${errorMsgReq}</div><br>
</#if>
<#if errorMsgSes?has_content>
<div class='errorMessage'>${errorMsgSes}</div><br>
</#if>
<#if eventMsgReq?has_content>
<div class='eventMessage'>${eventMsgReq}</div><br>
</#if>
