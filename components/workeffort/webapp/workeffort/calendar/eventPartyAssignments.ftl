<#--
 *  Description: None
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Johan Isacsson (conversion of jsp created by Andy Zeneski) 
 *@author     Eric.Barbier@nereide.biz (migration to uiLabelMap)
 *@version    $Rev:$
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>


<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align=left width='40%' >
            <div class='boxhead'>${uiLabelMap.WorkEffortCalendarEventRoles} : ${workEffort.workEffortName?if_exists}</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
              <table width='100%' cellpadding='2' cellspacing='0' border='0'>
                <#if roles?has_content>
                  <tr>
                    <td>
                    <#if workEffortId?exists>
                      <div class='tabContainer'>
                          <a href="<@ofbizUrl>/event?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortEvent}</a>
                          <a href="<@ofbizUrl>/eventPartyAssignments?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButtonSelected">${uiLabelMap.PartyParties}</a>
                          <a href="<@ofbizUrl>/eventContactMechs?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyContactInformation}</a>
                      </div>
                    </#if>
                    <table width="100%" cellpadding="2" cellspacing="0" border="0">
                      <tr>
                        <td><div class="tableheadtext">${uiLabelMap.PartyPartyId}</div></td>
                        <td><div class="tableheadtext">${uiLabelMap.PartyName}</div></td>
                        <td><div class="tableheadtext">${uiLabelMap.PartyRoleTypeId}</div></td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr>
                        <td colspan='4'><hr class='sepbar'></td>
                      </tr>
                      <#list roles as role>
                          <tr>
                            <#assign roleType = role.getRelatedOne("RoleType")>
                            <#assign party = (delegator.findByPrimaryKey("Party", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",role.partyId)))?if_exists>
                            <#assign partyGroup = delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",party.partyId))?if_exists>
                            <#assign person = delegator.findByPrimaryKey("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",party.partyId))?if_exists>                                                          
                            <td><div class="tabletext"><a href="/partymgr/control/viewprofile?party_id=${party.partyId}" target="partymgr" class="buttontext">${party.partyId}</a></div></td>
                            <#if person?has_content>
                              <td><div class="tabletext">${person.firstName}&nbsp;${person.lastName}</div></td>
                            </#if>
                            <#if partyGroup?has_content>
                              <td><div class="tabletext">${partyGroup.groupName}</div></td>
                            </#if>
                            <td><div class="tabletext">${roleType.description}</div></td>
                            <td align="right"><div class="tabletext"><a href="<@ofbizUrl>/removeEventPartyAssign?workEffortId=${workEffortId}&partyId=${role.partyId}&roleTypeId=${role.roleTypeId}&fromDate=${role.fromDate.toString()}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonRemove}]</a></td>
                          </tr>
                        </#list>
                      </table>
                    </td>
                  </tr>
                <#else>
                  <tr>
                    <td><div class="tabletext">${uiLabelMap.WorkEffortNoRolesAssociatedRequest}.</div></td>
                  </tr>
                </#if>
                <#if workEffort?exists>
                <tr>
                  <td><hr class="sepbar"></td>
                </tr>
                <tr>
                  <td><div class="head3">${uiLabelMap.CommonAddNew}:</div></td>
                </tr>
                <tr>
                  <td>
                    <form method="post" action="<@ofbizUrl>/createEventPartyAssign</@ofbizUrl>" name="partyform">
                      <input type="hidden" name="workEffortId" value="${workEffortId}">
					  <input type="hidden" name="statusId" value="CAL_ACCEPTED">
                      <table width="100%" cellpadding="2" cellspacing="0" border="0">
                        <tr>
                          <td colspan="2">&nbsp;&nbsp;&nbsp;</td>
                          <td align="right"><div class="tableheadtext">${uiLabelMap.PartyPartyId}</div></td>
                          <td><input type="text" name="partyId" class="inputBox" size="30">
							<a href="javascript:call_fieldlookup(document.partyform.partyId,'<@ofbizUrl>/fieldLookup</@ofbizUrl>', 'lookupParty');"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'></a>                          
                          </td>
                          <td align="right"><div class="tableheadtext">${uiLabelMap.PartyRoleTypeId}</div></td>
                          <td>
                            <select name="roleTypeId" class="selectBox">
                              <option value="CAL_ATTENDEE">${uiLabelMap.WorkEffortAttender}</option>
                              <option value="CAL_ORGANIZER">${uiLabelMap.WorkEffortOrganizer}</option>
                            </select>
                          </td>
                          <td align="center"><input type="submit" style="font-size: small;" value="${uiLabelMap.CommonAdd}"></td>
                          <td colspan="2">&nbsp;&nbsp;&nbsp;</td>
                        </tr>
                      </table>
                    </form>
                  </td>
                </tr>
                </#if>
              </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

