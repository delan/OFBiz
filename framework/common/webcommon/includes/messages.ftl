<#--
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     David E. Jones (jonesde@ofbiz.org)
 *@version    $Rev: 4140 $
-->

<#if requestAttributes.errorMessageList?has_content><#assign errorMessageList=requestAttributes.errorMessageList></#if>
<#if requestAttributes.eventMessageList?has_content><#assign eventMessageList=requestAttributes.eventMessageList></#if>
<#if requestAttributes.serviceValidationException?exists><#assign serviceValidationException = requestAttributes.serviceValidationException></#if>
<#if requestAttributes.uiLabelMap?has_content><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#-- special error message override -->
<#if serviceValidationException?exists>
  <#assign serviceName = serviceValidationException.getServiceName()?if_exists>
  <#assign missingList = serviceValidationException.getMissingFields()?if_exists>
  <#assign extraList = serviceValidationException.getExtraFields()?if_exists>

  <#--  need if statement for EACH service (see the controller.xml file for service names) -->
  <#if serviceName?has_content && serviceName == "createPartyContactMechPurpose">
    <#-- create the inital message prefix -->
    <#assign message = "${uiLabelMap.CommonRequiredFieldsFoundEmpty}:">

    <#-- loop through all the missing fields -->
    <#list missingList as missing>
      <#--
           check for EACH required field (see the service definition)
           then append a message for the missing field; some fields may be
           and not needed; this example show ALL fields for the service.
           ** The value inside quotes must match 100% case included.
       -->

      <#if missing == "partyId">
        <#assign message = message + "<li>${uiLabelMap.PartyPartyID}</li>">
      </#if>
      <#if missing == "contactMechId">
        <#assign message = message + "<li>${uiLabelMap.PartyContactMechID}</li>">
      </#if>
      <#if missing == "contactMechPurposeTypeId">
        <#assign message = message + "<li>${uiLabelMap.PartyContactPurpose}</li>">
      </#if>
    </#list>

    <#-- this will replace the current error message with the new one -->
    <#assign errorMsgReq = message>
  </#if>
</#if>

<#-- display the error messages -->
<#if errorMessageList?has_content>
<div class="errorMessage">${uiLabelMap.CommonFollowingErrorsOccurred}:</div><br/>
<ul>
  <#list errorMessageList as errorMsg>
    <li class="errorMessage">${errorMsg}</li>
  </#list>
</ul>
<br/>
</#if>
<#if eventMessageList?has_content>
<div class="eventMessage">${uiLabelMap.CommonFollowingOccurred}:</div><br/>
<ul>
  <#list eventMessageList as eventMsg>
    <li class="eventMessage">${eventMsg}</li>
  </#list>
</ul>
<br/>
</#if>
