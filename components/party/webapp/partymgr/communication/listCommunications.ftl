<#--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski (jaz@ofbiz.org)
 *@author     Olivier Heintz (olivier.heintz@nereide.biz)
 *@version    $Revision: 1.6 $
 *@since      2.2
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#assign locale = requestAttributes.locale>
<#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>
<#-- Main Heading -->
<table width="100%" cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td align="left">
      <#if lookupPerson?has_content || lookupGroup?has_content>
        <div class="head1">${uiLabelMap.PartyCommunicationsWith}
          <#if lookupPerson?exists>
            ${lookupPerson.personalTitle?if_exists}
            ${lookupPerson.firstName?if_exists}
            ${lookupPerson.middleName?if_exists}
            ${lookupPerson.lastName?if_exists}
            ${lookupPerson.suffix?if_exists}
          <#else>
            <#if lookupGroup?exists>
              ${lookupGroup.groupName?default(uiLabelMap.PartyNoNameGroup)}
            <#else>
              "${uiLabelMap.PartyNewUser}"
            </#if>
          </#if>
        </div>
      <#else>
        <div class="head1">Pending Communications</div>
      </#if>
    </td>
    <#if lookupPerson?has_content || lookupGroup?has_content>
      <td align="right">
	    <div class="tabContainer">
          <a href="<@ofbizUrl>/viewprofile?partyId=${partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyProfile}</a>
          <a href="<@ofbizUrl>/viewvendor?partyId=${partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyVendor}</a>
          <a href="<@ofbizUrl>/viewroles?partyId=${partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyRoles}</a>
          <a href="<@ofbizUrl>/viewrelationships?partyId=${partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyRelationships}</a>
          <a href="<@ofbizUrl>/viewcommunications?partyId=${partyId}</@ofbizUrl>" class="tabButtonSelected">${uiLabelMap.PartyCommunications}</a>
        </div>
      </td>
    </#if>
  </tr>
  <#if partyId?exists>
    <tr>
      <td colspan="2" align="right" nowrap>
        <a href="<@ofbizUrl>/viewCommunicationEvent?partyIdFrom=${partyId}&partyId=${partyId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartyNewCommunication}]</a>
      </td>
    </tr>
  </#if>
</table>

<br>
<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <#assign target = requestAttributes.targetRequestUri>
  <#if partyId?exists>
    <#assign target = target + "?partyId=" + partyId + "&previousSort=" + previousSort>
  <#else>
    <#assign target = target + "?previousSort=" + previousSort>
  </#if>

  <tr>
    <td><a href="<@ofbizUrl>${target}&sort=communicationEventId</@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyCommEvent} #</a></td>
    <td><a href="<@ofbizUrl>${target}&sort=communicationEventTypeId</@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyType}</a></td>
    <td><a href="<@ofbizUrl>${target}&sort=contactMechTypeId</@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyContactType}</a></td>
    <td><a href="<@ofbizUrl>${target}&sort=statusId</@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyStatus}</a></td>
    <td><a href="<@ofbizUrl>${target}&sort=subject</@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartySubject}</a></td>
    <td><a href="<@ofbizUrl>${target}&sort=partyIdFrom</@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyPartyFrom}</a></td>
    <td><a href="<@ofbizUrl>${target}&sort=partyIdTo</@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyPartyTo}</a></td>
    <td><a href="<@ofbizUrl>${target}&sort=entryDate</@ofbizUrl>" class="tableheadbutton">${uiLabelMap.PartyEnteredDate}</a></td>
  </tr> 
  <tr><td colspan="9"><hr class="sepbar"></td></tr>
  <#if events?has_content>
    <#list events as event>
      <#assign eventType = event.getRelatedOne("CommunicationEventType")?if_exists>
      <#assign contactMechType = event.getRelatedOne("ContactMechType")?if_exists>
      <#if event.statusId?exists>
        <#assign statusItem = event.getRelatedOne("StatusItem")>
      </#if>
      <#if !partyId?exists>
        <#assign partyId = event.partyIdFrom>
      </#if>
      <tr>
        <td><div class="tabletext">${event.communicationEventId?if_exists}</div></td>
        <td><div class="tabletext">${(eventType.get("description",locale))?default(uiLabelMap.CommonNA)}</div></td>
        <td><div class="tabletext">${(contactMechType.get("description",locale))?default(uiLabelMap.CommonNA)}</div></td>
        <td><div class="tabletext">${(statusItem.get("description", locale))?default(uiLabelMap.CommonNA)}</div></td>
        <td><div class="tabletext">${event.subject?if_exists}</div></td>
        <#if event.partyIdFrom?has_content>
          <td><a href="<@ofbizUrl>/viewprofile?partyId=${event.partyIdFrom}</@ofbizUrl>" class="buttontext">${event.partyIdFrom}</a></td>
        <#else>
          <td><div class="tabletext">${uiLabelMap.CommonNA}</div></td>
        </#if>
        <#if event.partyIdTo?has_content>
          <td><a href="<@ofbizUrl>/viewprofile?partyId=${event.partyIdTo}</@ofbizUrl>" class="buttontext">${event.partyIdTo}</a></td>
        <#else>
          <td><div class="tabletext">${uiLabelMap.CommonNA}</div></td>
        </#if>
        <td><div class="tabletext">${(event.entryDate?string)?if_exists}</div></td>
        <td align="right"><a href="<@ofbizUrl>/viewCommunicationEvent?partyId=${event.partyIdFrom}&communicationEventId=${event.communicationEventId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonView}]</a>
      </tr>
    </#list>
  <#else>
    <tr>
      <td colspan="8"><div class="tabletext">${uiLabelMap.PartyNoCommunicationFound}</div></td>
    </tr>
  </#if>
</table>
<#else>
  <h3>${uiLabelMap.PartyMgrViewPermissionError}</h3>
</#if>
