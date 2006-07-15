<#--

Copyright 2001-2006 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
-->

  <#if partyId?exists>
    <#assign title = uiLabelMap.PartyParty>
  <#else>
    <#assign title = uiLabelMap.PartyActive>
  </#if>
  <div class="head1">${title}&nbsp;${uiLabelMap.PartyVisitListing}</div>
  <#if !partyId?exists && showAll?lower_case == "true">
    <a href="<@ofbizUrl>showvisits?showAll=false</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartyShowActive}]</a>
  <#elseif !partyId?exists>
    <a href="<@ofbizUrl>showvisits?showAll=true</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartyShowAll}]</a>
  </#if>
  <br/>
  <#if visitList?has_content>
    <table border="0" width="100%" cellpadding="2">
      <tr>
        <td align="right">
          <b>
            <#if 0 < viewIndex>
              <a href="<@ofbizUrl>showvisits?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}<#if sort?has_content>&sort=${sort}</#if><#if partyId?has_content>&partyId=${partyId}</#if>&showAll=${showAll}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
            </#if>
            <#if 0 < listSize>
              <span class="tabletext">${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${visitSize}</span>
            </#if>
            <#if highIndex < listSize>
              | <a href="<@ofbizUrl>showvisits?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}<#if sort?has_content>&sort=${sort}</#if><#if partyId?has_content>&partyId=${partyId}</#if>&showAll=${showAll}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>              
            </#if>
          </b>
        </td>
      </tr>
    </table>
  <#else>
    <br/>
  </#if>
  
  <table width="100%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td><a href="<@ofbizUrl>showvisits?sort=visitId&showAll=${showAll}<#if partyId?has_content>&partyId=${partyId}</#if></@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyVisitId}</a></td>      
      <td><a href="<@ofbizUrl>showvisits?sort=visitorId&showAll=${showAll}<#if visitorId?has_content>&visitorId=${visitorId}</#if></@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyVisitorId}</a></td>
      <td><a href="<@ofbizUrl>showvisits?sort=partyId&showAll=${showAll}<#if partyId?has_content>&partyId=${partyId}</#if></@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyPartyId}</a></td>
      <td><a href="<@ofbizUrl>showvisits?sort=userLoginId&showAll=${showAll}<#if partyId?has_content>&partyId=${partyId}</#if></@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyUserLoginId}</a></td>
      <td><a href="<@ofbizUrl>showvisits?sort=-userCreated&showAll=${showAll}<#if partyId?has_content>&partyId=${partyId}</#if></@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyNewUser}</a></td>
      <td><a href="<@ofbizUrl>showvisits?sort=webappName&showAll=${showAll}<#if partyId?has_content>&partyId=${partyId}</#if></@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyWebApp}</a></td>
      <td><a href="<@ofbizUrl>showvisits?sort=clientIpAddress&showAll=${showAll}<#if partyId?has_content>&partyId=${partyId}</#if></@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyClientIP}</a></td>
      <td><a href="<@ofbizUrl>showvisits?sort=fromDate&showAll=${showAll}<#if partyId?has_content>&partyId=${partyId}</#if></@ofbizUrl>" class="tableheadbutton">${uiLabelMap.CommonFromDate}</a></td>
      <td><a href="<@ofbizUrl>showvisits?sort=thruDate&showAll=${showAll}<#if partyId?has_content>&partyId=${partyId}</#if></@ofbizUrl>" class="tableheadbutton">${uiLabelMap.CommonThruDate}</a></td>
    </tr>
    <tr><td colspan="9"><hr class="sepbar"></td></tr>
    <#-- set initial row color -->
    <#assign rowClass = "viewManyTR2">
    <#list visitList as visitObj>
      <tr class="${rowClass}">
        <td><a href="<@ofbizUrl>visitdetail?visitId=${visitObj.visitId}</@ofbizUrl>" class="buttontext">${visitObj.visitId}</a></td>       
        <td><div class="tabletext">${visitObj.visitorId?if_exists}</div></td>
        <td><a href="<@ofbizUrl>viewprofile?partyId=${visitObj.partyId?if_exists}</@ofbizUrl>" class="buttontext">${visitObj.partyId?if_exists}</a></td>
        <td><div class="tabletext">${visitObj.userLoginId?if_exists}</div></td>
        <td><div class="tabletext">${visitObj.userCreated?if_exists}</div></td>
        <td><div class="tabletext">${visitObj.webappName?if_exists}</div></td>
        <td><div class="tabletext">${visitObj.clientIpAddress?if_exists}</div></td>
        <td><div class="tabletext">${(visitObj.fromDate?string)?if_exists}</div></td>
        <td><div class="tabletext">${(visitObj.thruDate?string)?if_exists}</div></td>
      </tr>
      <#-- toggle the row color -->
      <#if rowClass == "viewManyTR2">
        <#assign rowClass = "viewManyTR1">
      <#else>
        <#assign rowClass = "viewManyTR2">
      </#if>
    </#list>
  </table>

  <#if visitList?has_content>
    <table border="0" width="100%" cellpadding="2">
      <tr>
        <td align="right">
          <b>
            <#if 0 < viewIndex>
              <a href="<@ofbizUrl>showvisits?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex-1}<#if sort?has_content>&sort=${sort}</#if><#if partyId?has_content>&partyId=${partyId}</#if>&showAll=${showAll}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonPrevious}]</a> |
            </#if>
            <#if 0 < listSize>
              <span class="tabletext">${lowIndex+1} - ${highIndex} ${uiLabelMap.CommonOf} ${visitSize}</span>
            </#if>
            <#if highIndex < listSize>
              | <a href="<@ofbizUrl>showvisits?VIEW_SIZE=${viewSize}&VIEW_INDEX=${viewIndex+1}<#if sort?has_content>&sort=${sort}</#if><#if partyId?has_content>&partyId=${partyId}</#if>&showAll=${showAll}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonNext}]</a>              
            </#if>
          </b>
        </td>
      </tr>
    </table> 
  </#if>  
