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
 *@version    $Revision: 1.4 $
 *@since      2.2
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#assign locale = requestAttributes.locale>
<#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>
<#-- Main Heading -->
<table width="100%" cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td align="left">
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
    </td>
    <td align="right">
	  <div class="tabContainer">
        <a href="<@ofbizUrl>/viewprofile?partyId=${partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyProfile}</a>
        <a href="<@ofbizUrl>/viewvendor?partyId=${partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyVendor}</a>
        <a href="<@ofbizUrl>/viewroles?partyId=${partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyRoles}</a>
        <a href="<@ofbizUrl>/viewrelationships?partyId=${partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyRelationships}</a>
        <a href="<@ofbizUrl>/viewcommunications?partyId=${partyId}</@ofbizUrl>" class="tabButtonSelected">${uiLabelMap.PartyCommunications}</a>
      </div>
    </td>
  </tr>
  <tr>
    <td colspan="2" align="right" nowrap>      
      <a href="<@ofbizUrl>/viewCommunicationEvent?partyId=${partyId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartyNewCommunication}]</a>
    </td>
  </tr>
</table>

<br>
<table width="100%" border="0" cellpadding="0" cellspacing="0"> 
  <tr>
    <td><div class="tableheadtext">${uiLabelMap.PartyCommEvent} #</div></td>
    <td><div class="tableheadtext">${uiLabelMap.PartyType}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.PartyContactType}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.PartyStatus}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.PartyPartyFrom}</div></td>  
    <td><div class="tableheadtext">${uiLabelMap.PartyPartyTo}</div></td>
    <td><div class="tableheadtext">${uiLabelMap.PartyEnteredDate}</div></td>    
  </tr> 
  <tr><td colspan="8"><hr class="sepbar"></td></tr>
  <#if events?has_content>
    <#list events as event>
      <#assign eventType = event.getRelatedOne("CommunicationEventType")?if_exists>
      <#assign contactMechType = event.getRelatedOne("ContactMechType")?if_exists>
      <#if event.statusId?exists>
        <#assign statusItem = event.getRelatedOne("StatusItem")>
      </#if>
      <tr>
        <td><div class="tabletext">${event.communicationEventId?if_exists}</div></td>
        <td><div class="tabletext">${(eventType.get("description",locale))?default(uiLabelMap.CommonNA)}</div></td>
        <td><div class="tabletext">${(contactMechType.get("description",locale))?default(uiLabelMap.CommonNA)}</div></td>
        <td><div class="tabletext">${(statusItem.get("description", locale))?default(uiLabelMap.CommonNA)}</div></td>
        <td><div class="tabletext">${event.partyIdFrom?default(uiLabelMap.CommonNA)}</div></td>
        <td><div class="tabletext">${event.partyIdTo?default(uiLabelMap.CommonNA)}</div></td>
        <td><div class="tabletext">${(event.entryDate?string)?if_exists}</div></td>
        <td align="right"><a href="<@ofbizUrl>/viewCommunicationEvent?partyId=${partyId}&communicationEventId=${event.communicationEventId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonView}]</a>
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
