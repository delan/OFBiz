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
 *@version    $Revision$
 *@since      2.2
-->

<#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>
<#-- Main Heading -->
<table width="100%" cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td align="left">
      <div class="head1">Communications with
        <#if lookupPerson?exists>
          ${lookupPerson.personalTitle?if_exists}
          ${lookupPerson.firstName?if_exists}
          ${lookupPerson.middleName?if_exists}
          ${lookupPerson.lastName?if_exists}
          ${lookupPerson.suffix?if_exists}
        <#else>
          <#if lookupGroup?exists>
            ${lookupGroup.groupName?default("No name (group)")}
          <#else>
          "New User"
          </#if>
        </#if>
      </div>
    </td>
    <td align="right">
	  <div class="tabContainer">
        <a href="<@ofbizUrl>/viewprofile?partyId=${partyId}</@ofbizUrl>" class="tabButton">Profile</a>
        <a href="<@ofbizUrl>/viewvendor?partyId=${partyId}</@ofbizUrl>" class="tabButton">Vendor</a>
        <a href="<@ofbizUrl>/viewroles?partyId=${partyId}</@ofbizUrl>" class="tabButton">Roles</a>
        <a href="<@ofbizUrl>/viewrelationships?partyId=${partyId}</@ofbizUrl>" class="tabButton">Relationships</a>
        <a href="<@ofbizUrl>/viewcommunications?partyId=${partyId}</@ofbizUrl>" class="tabButtonSelected">Communications</a>
      </div>
    </td>
  </tr>
  <tr>
    <td colspan="2" align="right" nowrap>      
      <a href="<@ofbizUrl>/viewCommunicationEvent?partyId=${partyId}</@ofbizUrl>" class="buttontext">[New Communication]</a>
    </td>
  </tr>
</table>

<br>
<table width="100%" border="0" cellpadding="0" cellspacing="0"> 
  <tr>
    <td><div class="tableheadtext">Event #</div></td>
    <td><div class="tableheadtext">Type</div></td>
    <td><div class="tableheadtext">Contact Type</div></td>
    <td><div class="tableheadtext">Status</div></td>
    <td><div class="tableheadtext">Party From</div></td>  
    <td><div class="tableheadtext">Party To</div></td>
    <td><div class="tableheadtext">Entered Date</div></td>    
  </tr> 
  <tr><td colspan="8"><hr class="sepbar"></td></tr>
  <#if events?has_content>
    <#list events as event>
      <#assign eventType = event.getRelatedOne("CommunicationEventType")>
      <#assign contactMechType = event.getRelatedOne("ContactMechType")>
      <#if event.statusId?exists>
        <#assign statusItem = event.getRelatedOne("StatusItem")>
      </#if>
      <tr>
        <td><div class="tabletext">${event.communicationEventId?if_exists}</div></td>
        <td><div class="tabletext">${eventType.description?default("N/A")}</div></td>
        <td><div class="tabletext">${contactMechType.description?default("N/A")}</div></td>
        <td><div class="tabletext">${(statusItem.description)?default("N/A")}</div></td>
        <td><div class="tabletext">${event.partyIdFrom?default("N/A")}</div></td>
        <td><div class="tabletext">${event.partyIdTo?default("N/A")}</div></td>
        <td><div class="tabletext">${(event.entryDate?string)?if_exists}</div></td>
        <td align="right"><a href="<@ofbizUrl>/viewCommunicationEvent?partyId=${partyId}&communicationEventId=${event.communicationEventId}</@ofbizUrl>" class="buttontext">[View]</a>
      </tr>
    </#list>
  <#else>
    <tr>
      <td colspan="8"><div class="tabletext">No Communication Events</div></td>
    </tr>
  </#if>
</table>
<#else>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
</#if>
