<#--
 *  Copyright (c) 2002-2005 The Open For Business Project - www.ofbiz.org
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
 *@author     David E. Jones
 *@author     Andy Zeneski
 *@version    $Rev$
 *@since      1.0
-->
<script language="JavaScript">
    function setNowFromDate(formName) { eval('document.' + formName + '.fromDate.value="${nowTimestamp}"'); }
</script>

<#-- Party Relationships -->
<br/>
<table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.PartyRelationships}</div>
          </td>
        </tr>
      </table>
    </td>
    </form>
  </tr>
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <tr>
          <td>
            <#if partyRelationships?has_content>
            <table width="100%" border="1" cellpadding="1" cellspacing="0">
              <tr>
                <td><div class="tabletext"><b>&nbsp;Description</b></div></td>
                <td><div class="tabletext"><b>&nbsp;From Date</b></div></td>
                <#if security.hasEntityPermission("PARTYMGR", "_DELETE", session)>
                <td>&nbsp;</td>
                </#if>
              </tr>
              <#list partyRelationships as partyRelationship>
                  <#assign partyRelationshipType = partyRelationship.getRelatedOneCache("PartyRelationshipType")?if_exists>
                  <#assign roleTypeTo = partyRelationship.getRelatedOneCache("ToRoleType")>
                  <#assign roleTypeFrom = partyRelationship.getRelatedOneCache("FromRoleType")>
                  <tr>
                    <td><div class="tabletext">
                        Party <b>${partyRelationship.partyIdTo}</b>
                        <#if "_NA_" != partyRelationship.roleTypeIdTo>
                            in role <b>${roleTypeTo.description}</b>
                        </#if>
                        is a <b>${partyRelationshipType.partyRelationshipName}</b>
                        of party <b>${partyRelationship.partyIdFrom}</b>
                        <#if "_NA_" != partyRelationship.roleTypeIdFrom>
                            in role <b>${roleTypeFrom.description}</b>
                        </#if>
                    </div></td>
                    <td><div class="tabletext">&nbsp;${partyRelationship.fromDate}</div></td>
                    <#if security.hasEntityPermission("PARTYMGR", "_DELETE", session)>
                    <td align="right">                     
                        <a href="<@ofbizUrl>deletePartyRelationship?partyIdTo=${partyRelationship.partyIdTo}&amp;roleTypeIdTo=${partyRelationship.roleTypeIdTo}&amp;roleTypeIdFrom=${partyRelationship.roleTypeIdFrom}&amp;partyIdFrom=${partyRelationship.partyIdFrom}&amp;fromDate=${partyRelationship.fromDate}&amp;partyId=${partyId?if_exists}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonRemove}]</a>&nbsp;
                    </td>
                    </#if>
                  </tr>
                  <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session)>
                  <tr>
                    <td colspan="3" align="right">
                        <form method="post" name="updatePartyRel${partyRelationship_index}" action="<@ofbizUrl>updatePartyRelationship</@ofbizUrl>">
                            <input type="hidden" name="partyId" value="${partyId}"/>
                            <input type="hidden" name="partyIdFrom" value="${partyRelationship.partyIdFrom}"/>
                            <input type="hidden" name="roleTypeIdFrom" value="${partyRelationship.roleTypeIdFrom}"/>
                            <input type="hidden" name="partyIdTo" value="${partyRelationship.partyIdTo}"/>
                            <input type="hidden" name="roleTypeIdTo" value="${partyRelationship.roleTypeIdTo}"/>
                            <input type="hidden" name="fromDate" value="${partyRelationship.fromDate}"/>
                            <span class="tabletext"><b>Thru Date: </b></span><input type="text" size="24" class="inputBox" name="thruDate" value="${partyRelationship.thruDate?if_exists}"/>
                            <a href="javascript:call_cal(document.updatePartyRel${partyRelationship_index}.thruDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"/></a>
                            <#-- ${partyRelationship.statusId}-->
                            <span class="tabletext"><b>Comments: </b></span><input type="text" size="50" class="inputBox" name="comments" value="${partyRelationship.comments?if_exists}"/>
                            <input type="submit" value="${uiLabelMap.CommonUpdate}" style="font-size: x-small;"/>
                        </form>
                    </td>
                  </tr>
                  </#if>
              </#list>
            </table>
            <#else/>
              <div class="tabletext">No relationships found.</div>
            </#if>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session)>
  <tr>
    <td width="100%"><hr class="sepbar"></td>
  </tr>
  <tr>
    <td width="100%" >
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <form name="addPartyRelationshipTo" method="post" action="<@ofbizUrl>createPartyRelationship</@ofbizUrl>">
        <input type="hidden" name="partyId" value="${partyId}"/>
        <input type="hidden" name="partyIdFrom" value="${partyId}"/>
        <tr>
          <td>
              <div class="tabletext" style="font-weight: bold;">
                The party with ID 
                <input type="text" size="20" name="partyIdTo" class="inputBox">
                <a href="javascript:call_fieldlookup2(document.addPartyRelationshipTo.partyIdTo,'LookupPartyName');"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'/></a>
                in the role of 
                <select name="roleTypeIdTo" class="selectBox">
                  <#list roleTypes as roleType>
                    <option <#if "_NA_" == roleType.roleTypeId>selected="selected"</#if> value="${roleType.roleTypeId}">${roleType.description}<#-- [${roleType.roleTypeId}]--></option>
                  </#list>
                </select>
                is a 
                <select name="partyRelationshipTypeId" class="selectBox">
                  <#list relateTypes as relateType>
                    <option value="${relateType.partyRelationshipTypeId}">${relateType.partyRelationshipName}<#-- [${relateType.partyRelationshipTypeId}]--></option>
                  </#list>
                </select>
                of the current party in the role of
                <select name="roleTypeIdFrom" class="selectBox">
                  <#list roleTypes as roleType>
                    <option <#if "_NA_" == roleType.roleTypeId>selected="selected"</#if> value="${roleType.roleTypeId}">${roleType.description}<#-- [${roleType.roleTypeId}]--></option>
                  </#list>
                </select>
                from <input type="text" size="24" name="fromDate" class="inputBox"/><a href="javascript:call_cal(document.addPartyRelationshipTo.fromDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"/></a>
                thru <input type="text" size="24" name="thruDate" class="inputBox"/><a href="javascript:call_cal(document.addPartyRelationshipTo.thruDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"/></a>
            </div>
          </td>
          <td>
            <a href="javascript:document.addPartyRelationshipTo.submit()" class="buttontext">[${uiLabelMap.CommonAdd}]</a>&nbsp;&nbsp;
          </td>
        </tr>
        <tr>
         <td colspan="2"><span class="tabletext">Comments:&nbsp;&nbsp;</span><input type="text" size="60" name="comments" class="inputBox"/></td>
        </tr>
        </form>
      </table>
    </td>
  </tr>
  <tr>
    <td width="100%"><hr class="sepbar"></td>
  </tr>
  <tr>
    <td width="100%" >
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <form name="addPartyRelationshipFrom" method="post" action="<@ofbizUrl>createPartyRelationship</@ofbizUrl>">
        <input type="hidden" name="partyId" value="${partyId}"/>
        <input type="hidden" name="partyIdTo" value="${partyId}"/>
        <tr>
          <td>
              <div class="tabletext" style="font-weight: bold;">
                  The current party in the role of
                <select name="roleTypeIdTo" class="selectBox">
                  <#list roleTypes as roleType>
                    <option <#if "_NA_" == roleType.roleTypeId>selected="selected"</#if> value="${roleType.roleTypeId}">${roleType.description}<#-- [${roleType.roleTypeId}]--></option>
                  </#list>
                </select>
                is a 
                <select name="partyRelationshipTypeId" class="selectBox">
                  <#list relateTypes as relateType>
                    <option value="${relateType.partyRelationshipTypeId}">${relateType.partyRelationshipName}<#-- [${relateType.partyRelationshipTypeId}]--></option>
                  </#list>
                </select>
                of the party with ID
                <input type="text" size="20" name="partyIdFrom" class="inputBox"/>
                <a href="javascript:call_fieldlookup2(document.addPartyRelationshipFrom.partyIdFrom,'LookupPartyName');"><img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'/></a>
                in the role of 
                <select name="roleTypeIdFrom" class="selectBox">
                  <#list roleTypes as roleType>
                    <option <#if "_NA_" == roleType.roleTypeId>selected="selected"</#if> value="${roleType.roleTypeId}">${roleType.description}<#-- [${roleType.roleTypeId}]--></option>
                  </#list>
                </select>
                from <input type="text" size="24" name="fromDate" class="inputBox"/><a href="javascript:call_cal(document.addPartyRelationshipFrom.fromDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"/></a>
                thru <input type="text" size="24" name="thruDate" class="inputBox"/><a href="javascript:call_cal(document.addPartyRelationshipFrom.thruDate, null);"><img src="/images/cal.gif" width="16" height="16" border="0" alt="Calendar"/></a>
            </div>
          </td>
          <td>
            <a href="javascript:document.addPartyRelationshipFrom.submit()" class="buttontext">[${uiLabelMap.CommonAdd}]</a>&nbsp;&nbsp;
          </td>
        </tr>
        <tr>
          <td colspan="2"><span class="tabletext">Comments:&nbsp;&nbsp;</span><input type="text" size="60" name="comments" class="inputBox"/></td>
        </tr>
        </form>
      </table>
    </td>
  </tr>
  </#if>
</table>
<br/>

