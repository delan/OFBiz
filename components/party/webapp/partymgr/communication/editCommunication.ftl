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
 *@version    $Revision: 1.9 $
 *@since      2.2
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
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
      <#if communicationEvent?has_content>
        <a href="/workeffort/control/task?communicationEventId=${communicationEvent.communicationEventId}${requestAttributes.externalKeyParam}" class="buttontext">[${uiLabelMap.PartyNewTask}]</a>
        <a href="/workeffort/control/event?communicationEventId=${communicationEvent.communicationEventId}${requestAttributes.externalKeyParam}" class="buttontext">[${uiLabelMap.PartyNewEvent}]</a>
        <a href="<@ofbizUrl>/viewCommunicationEvent?partyId=${partyId}&partyIdFrom=${partyId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartyNewCommunication}]</a>
        <a href="<@ofbizUrl>/viewCommunicationEvent?partyId=${partyId}&partyIdTo=${partyId}&parentCommEventId=${communicationEvent.communicationEventId}&origCommEventId=${communicationEvent.origCommEventId?default("")}</@ofbizUrl>" class="buttontext">[Reply]</a>
      </#if>
    </td>
  </tr>
</table>

<br>
<#if communicationEvent?has_content>
  <#assign formAction = "/updateCommunicationEvent">
  <#assign buttonText = uiLabelMap.CommonUpdate>
  <#if communicationEvent.statusId?exists && (communicationEvent.statusId == "COM_COMPLETE" || communicationEvent.statusId == "COM_RESOLVED" || communicationEvent.statusId == "COM_REFERRED")>
    <#assign okayToUpdate = false>
  <#else>
    <#assign okayToUpdate = true>
  </#if>
<#else>
  <#assign formAction = "/createCommunicationEvent">
  <#assign buttonText = uiLabelMap.CommonCreate>
  <#assign okayToUpdate = true>
</#if>  
<table width="100%" border="0" cellpadding="2" cellspacing="0">
  <#if communicationEvent?has_content>
    <#assign eventPurposes = communicationEvent.getRelated("CommunicationEventPurpose")>             
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.PartyEventPurpose}</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <table border='0' cellspacing='1' bgcolor='black'>
          <#if eventPurposes?has_content> 
            <#list eventPurposes as purpose>
              <#assign purposeType = purpose.getRelatedOne("CommunicationEventPrpTyp")>
              <tr>
                <td bgcolor='white'>
                  <div class="tabletext">&nbsp;<b>${purposeType.description} - ${purpose.description?if_exists}</b></div>
                </td>
                <#if okayToUpdate>
                <td bgcolor='white'>
                  <a href="<@ofbizUrl>/removeCommunicationEventPurpose?partyId=${partyId}&communicationEventPrpTypId=${purposeType.communicationEventPrpTypId}&communicationEventId=${communicationEvent.communicationEventId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonDelete}</a>
                </td>
                </#if>
              </tr>
            </#list>          
          </#if>
          <#if okayToUpdate>
          <form method="post" name="addeventpurpose" action="<@ofbizUrl>/createCommunicationEventPurpose</@ofbizUrl>">
            <input type="hidden" name="communicationEventId" value="${communicationEvent.communicationEventId}">
            <input type="hidden" name="partyId" value="${partyId}">
            <tr>
              <td bgcolor="white">
                <select name="communicationEventPrpTypId" class="selectBox">
                  <#list purposeTypes as purpose>
                    <option value="${purpose.communicationEventPrpTypId}">${purpose.description}</option>
                  </#list>
                </select>
                <input type="text" class="inputBox" name="description" size="15">
              </td>
              <td bgcolor="white"><a href="javascript:document.addeventpurpose.submit()" class="buttontext">${uiLabelMap.PartyAddPurpose}</a></td>
            </tr>
          </form>
          </#if>
        </table>          
      </td>
    </tr>
    <tr><td colspan="3">&nbsp;</td></tr>  
    <#assign eventRoles = communicationEvent.getRelated("CommunicationEventRole")>
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.PartyRoles}</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <table border='0' cellspacing='1' bgcolor='black'>
          <#if eventRoles?has_content>
            <#list eventRoles as eventRole>
              <#assign roleType = eventRole.getRelatedOne("RoleType")>
              <tr>
                <td bgcolor='white'>
                  <div class="tabletext">&nbsp;<b>${eventRole.partyId} - ${roleType.description}</b></div>
                </td>
                <#if okayToUpdate>
                <td bgcolor='white'>
                  <a href="<@ofbizUrl>/removeCommunicationEventRole?party_id=${partyId}&partyId=${eventRole.partyId}&roleTypeId=${eventRole.roleTypeId}&communicationEventId=${communicationEvent.communicationEventId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonDelete}</a>
                </td>
                </#if>
              </tr>
            </#list>
          </#if>
          <#if okayToUpdate>
          <tr>
            <form method="post" name="addeventrole" action="<@ofbizUrl>/createCommunicationEventRole</@ofbizUrl>">
              <input type="hidden" name="communicationEventId" value="${communicationEvent.communicationEventId}">
              <input type="hidden" name="party_id" value="${partyId}">
              <td bgcolor='white'>
                <select name="roleTypeId" class="selectBox">
                  <#list roleTypes as roleType>
                     <option value="${roleType.roleTypeId}">${roleType.description}</option>
                  </#list>
                </select>
                <input type="text" class="inputBox" name="partyId" size="10">
              </td>
              <td bgcolor="white"><a href="javascript:document.addeventrole.submit()" class="buttontext">${uiLabelMap.PartyAddRole}</a></td>     
            </form>
          </tr>
          </#if>
        </table>
      </td>
    </tr>
    <tr><td colspan="3">&nbsp;</td></tr>
  </#if>
  <form name="addevent" method="post" action="<@ofbizUrl>${formAction}</@ofbizUrl>" style="margin: 0;">  
    <input type="hidden" name="partyId" value="${partyId}">    
    <#if communicationEvent?has_content>      
      <input type="hidden" name="communicationEventId" value="${communicationEvent.communicationEventId}">
    <#else>
      <input type="hidden" name="partyIdFrom" value="${partyIdFrom}">
      <input type="hidden" name="partyIdTo" value="${partyIdTo}">
    </#if>
    <#if parentEvent?has_content>
      <input type="hidden" name="parentCommEventId" value="${parentEvent.communicationEventId}">
      <#if (parentEvent.origCommEventId?exists && parentEvent.origCommEventId?length > 0)>
        <#assign orgComm = parentEvent.origCommEventId>
      <#else>
        <#assign orgComm = parentEvent.communicationEventId>
      </#if>
      <input type="hidden" name="origCommEventId" value="${orgComm}">
    </#if>
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.PartyEventType}</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <#if okayToUpdate>
        <select class="selectBox" name="communicationEventTypeId">
          <#if communicationEvent?has_content && communicationEvent.communicationEventTypeId?exists>
            <#assign eventType = communicationEvent.getRelatedOne("CommunicationEventType")>
            <option value="${eventType.communicationEventTypeId}">${eventType.description}</option>
            <option value="${eventType.communicationEventTypeId}">----</option>
          <#elseif parentEvent?has_content && parentEvent.communicationEventTypeId?exists>
            <#assign eventType = parentEvent.getRelatedOne("CommunicationEventType")>
            <option value="${eventType.communicationEventTypeId}">${eventType.description}</option>
            <option value="${eventType.communicationEventTypeId}">----</option>
          </#if>
          <#list eventTypes as type>
            <option value="${type.communicationEventTypeId}">${type.description}</option>
          </#list>
        </select>
        <#else>
          <#if communicationEvent?has_content && communicationEvent.communicationEventTypeId?exists>
            <#assign eventType = communicationEvent.getRelatedOne("CommunicationEventType")>
            <div class="tabletext">${eventType.description}</div>
          </#if>
        </#if>
      </td>
    </tr>
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.PartyStatus}</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <#if okayToUpdate>
        <select class="selectBox" name="statusId">
          <#if communicationEvent?has_content && communicationEvent.statusId?exists>
            <#assign statusItem = communicationEvent.getRelatedOne("StatusItem")>
            <option value="${statusItem.statusId}">${statusItem.description}</option>
            <option value="${statusItem.statusId}">----</option>
          </#if>
          <#list statuses as status>
            <option value="${status.statusId}">${status.description}</option>
          </#list>
        </select>
        <#else>
          <#if communicationEvent?has_content && communicationEvent.communicationEventTypeId?exists>
            <#assign statusItem = communicationEvent.getRelatedOne("StatusItem")>
            <div class="tabletext">${statusItem.description}</div>
          </#if>
        </#if>
      </td>
    </tr>
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.PartyContactType}</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <#if okayToUpdate>
        <select class="selectBox" name="contactMechTypeId">
          <#if communicationEvent?has_content && communicationEvent.contactMechTypeId?exists>
            <#assign contactMechType = communicationEvent.getRelatedOne("ContactMechType")>
            <option value="${contactMechType.contactMechTypeId}">${contactMechType.description}</option>
            <option value="${contactMechType.contactMechTypeId}">----</option>
          <#elseif parentEvent?has_content && parentEvent.contactMechTypeId?exists>
            <#assign contactMechType = parentEvent.getRelatedOne("ContactMechType")>
            <option value="${contactMechType.contactMechTypeId}">${contactMechType.description}</option>
            <option value="${contactMechType.contactMechTypeId}">----</option>
          </#if>
          <#list contactMechTypes as contactMechType>
            <option value="${contactMechType.contactMechTypeId}">${contactMechType.description}</option>
          </#list>
        </select>
        <#else>
          <#if communicationEvent?has_content && communicationEvent.communicationEventTypeId?exists>
            <#assign contactMechType = communicationEvent.getRelatedOne("ContactMechType")>
            <div class="tabletext">${contactMechType.description}</div>
          </#if>
        </#if>
      </td>
    </tr>
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.PartyRoleTypeFrom}</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <#if okayToUpdate>
        <select class="selectBox" name="roleTypeIdFrom">
          <#if communicationEvent?has_content && communicationEvent.roleTypeIdFrom?exists>
            <#assign roleType = communicationEvent.getRelatedOne("FromRoleType")>
            <option value="${roleType.roleTypeId}">${roleType.description}</option>
            <option value="${roleType.roleTypeId}">----</option>
          </#if>
          <option></option>
          <#list roleTypes as roleType>        
            <option value="${roleType.roleTypeId}">${roleType.description}</option>
          </#list>
        </select>
        <#else>
          <#if communicationEvent?has_content && communicationEvent.communicationEventTypeId?exists>
            <#assign roleType = (communicationEvent.getRelatedOne("FromRoleType"))?if_exists>
            <div class="tabletext">${(roleType.description)?default(uiLabelMap.CommonNone)}</div>
          </#if>
        </#if>
      </td>
    </tr>
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.PartyRoleTypeTo}</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <#if okayToUpdate>
        <select class="selectBox" name="roleTypeIdTo">
          <#if communicationEvent?has_content && communicationEvent.roleTypeIdTo?exists>
            <#assign roleType = communicationEvent.getRelatedOne("ToRoleType")>
            <option value="${roleType.roleTypeId}">${roleType.description}</option>
            <option value="${roleType.roleTypeId}">----</option>
          </#if>
          <option></option>
          <#list roleTypes as roleType>
            <option value="${roleType.roleTypeId}">${roleType.description}</option>
          </#list>
        </select>
        <#else>
          <#if communicationEvent?has_content && communicationEvent.roleTypeIdTo?exists>
            <#assign roleType = (communicationEvent.getRelatedOne("ToRoleType"))?if_exists>
            <div class="tabletext">${(roleType.description)?default(uiLabelMap.CommonNone)}</div>
          </#if>
        </#if>
      </td>
    </tr>
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.PartyCustomerRequest} #</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <#if okayToUpdate>
        <input type="text" class="inputBox" size="20" name="custRequestId" value="${(communicationEvent.custRequestId)?if_exists}">
        <#else>
        <div class="tabletext">${(communicationEvent.custRequestId)?default("[none]")}</div>
        </#if>
        <#if (!(communicationEvent.custRequestId)?has_content && (communicationEvent.communicationEventId)?has_content)>
          &nbsp;<a href="/workeffort/control/request?partyId=${partyId}&communicationEventId=${communicationEvent.communicationEventId}&subject=${communicationEvent.subject?default("")}${requestAttributes.externalKeyParam}" class="buttontext">Create New Request</a>
        <#elseif ((communicationEvent.custRequestId)?has_content)>
          &nbsp;<a href="/workeffort/control/request?custRequestId=${communicationEvent.custRequestId}${requestAttributes.externalKeyParam}" class="buttontext">View Request</a>
        </#if>
      </td>
    </tr>
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.CommonStartDate}</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <#if okayToUpdate>
        <input type="text" class="inputBox" size="25" name="datetimeStarted" value="${(communicationEvent.datetimeStarted)?if_exists}">
        <a href="javascript:call_cal(document.addevent.datetimeStarted, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
        <#else>
        <span class="tabletext">${(communicationEvent.datetimeStarted)?if_exists}</span>
        </#if>
      </td>
    </tr> 
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.CommonFinishDate}</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <#if okayToUpdate>
        <input type="text" class="inputBox" size="25" name="datetimeEnded" value="${(communicationEvent.datetimeEnded)?if_exists}">
        <a href="javascript:call_cal(document.addevent.datetimeEnded, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a>
        <#else>
        <span class="tabletext">${(communicationEvent.datetimeEnded)?if_exists}</span>
        </#if>
      </td>
    </tr>
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">Subject</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <#assign defaultSubject = (communicationEvent.subject)?default("")>
        <#if (defaultSubject?length == 0)>
          <#assign replyPrefix = uiLabelMap.PartyReplyPrefix + " ">
          <#if parentEvent?has_content>
            <#if !parentEvent.subject?default("")?upper_case?starts_with(replyPrefix)>
              <#assign defaultSubject = replyPrefix>
            </#if>
            <#assign defaultSubject = defaultSubject + parentEvent.subject?default("")>
          </#if>
        </#if>
        <#if okayToUpdate>
        <input type="text" class="inputBox" size="30" name="subject" value="${defaultSubject}">
        <#else>
        <div class="tabletext">${defaultSubject}</div>
        </#if>        
      </td>
    </tr>
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.CommonNote}</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <#if okayToUpdate>
          <#if parentEvent?has_content>
            <#assign eventNote = Static["org.ofbiz.base.util.UtilHttp"].getFullRequestUrl(request).toString()>
          <#else>
            <#assign eventNote = (communicationEvent.note)?if_exists>
          </#if>
          <textarea class="textAreaBox" cols="60" rows="3" name="note">${eventNote}</textarea>
        <#else>
          <div class="tabletext">${(communicationEvent.note)?if_exists}</div>
        </#if>
      </td>
    </tr>
    <tr>
      <td width="20%" align="right"><span class="tableheadtext">${uiLabelMap.CommonContent}</span></td>
      <td width="1">&nbsp;</td>
      <td>
        <#if okayToUpdate>
        <textarea class="textAreaBox" cols="60" rows="10" name="content">${(communicationEvent.content)?if_exists}</textarea>
        <#else>
        <div class="tabletext">${(communicationEvent.content)?if_exists}</div>
        </#if>
      </td>
    </tr>
    <#if okayToUpdate>
    <tr>
      <td colspan="2">&nbsp;</td>
      <td><input type="submit" class="smallSubmit" value="${buttonText}"></td>
    </tr>
    </#if>
  </form>
</table>


<#else>
  <h3>${uiLabelMap.PartyMgrViewPermissionError}</h3>
</#if>
