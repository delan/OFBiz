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

<div class='tabContainer'>
  <a href="<@ofbizUrl>/request?custRequestId=${custRequestId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortRequest}</a>
  <a href="<@ofbizUrl>/requestroles?custRequestId=${custRequestId}</@ofbizUrl>" class="tabButtonSelected">${uiLabelMap.WorkEffortRequestRoles}</a>
  <a href="<@ofbizUrl>/requestitems?custRequestId=${custRequestId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortRequestItems}</a>
</div>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>${uiLabelMap.WorkEffortRequestRoles}</div>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <#if custRequestRoles?has_content>
                  <TR>
                    <TD>
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
                      <#list custRequestRoles as role>
                          <tr>
                            <#assign roleType = role.getRelatedOne("RoleType")>
                            <#assign party = delegator.findByPrimaryKey("Party", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",role.partyId))>
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
                            <td align="right"><div class="tabletext"><a href="<@ofbizUrl>/removerequestrole?custRequestId=${custRequestId}&partyId=${role.partyId}&roleTypeId=${role.roleTypeId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonRemove}]</a></td>
                          </tr>
                        </#list>
                      </table>
                    </TD>
                  </TR>
                <#else>
                  <TR>
                    <TD><div class="tabletext">${uiLabelMap.WorkEffortNoRolesAssociatedRequest}.</div></TD>
                  </TR>
                </#if>
                <#if custRequest?exists>
                <TR>
                  <TD><HR class="sepbar"></TD>
                </TR>
                <TR>
                  <TD><div class="head3">${uiLabelMap.CommonAddNew}:</div</TD>
                </TR>
                <TR>
                  <TD>
                    <form method="post" action="<@ofbizUrl>/createrequestrole</@ofbizUrl>">
                      <input type="hidden" name="custRequestId" value="${custRequestId}">
                      <table width="100%" cellpadding="2" cellspacing="0" border="0">
                        <tr>
                          <td colspan="2">&nbsp;&nbsp;&nbsp;</td>
                          <td align="right"><div class="tableheadtext">${uiLabelMap.PartyPartyId}</div></td>
                          <td><input type="text" name="partyId" class="inputBox" size="30"></td>
                          <td align="right"><div class="tableheadtext">${uiLabelMap.PartyRoleTypeId}</div></td>
                          <td>
                            <select name="roleTypeId" class="selectBox">
                              <option value="REQ_TAKER">${uiLabelMap.WorkEffortRequestTaker}</option>
                              <option value="REQ_REQUESTER">${uiLabelMap.WorkEffortRequestingParty}</option>
                              <option value="REQ_MANAGER">${uiLabelMap.WorkEffortRequestManager}</option>
                            </select>
                          </td>
                          <td align="center"><input type="submit" style="font-size: small;" value="${uiLabelMap.CommonAdd}"></td>
                          <td colspan="2">&nbsp;&nbsp;&nbsp;</td>
                        </tr>
                      </table>
                    </form>
                  </TD>
                </TR>
                </#if>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

