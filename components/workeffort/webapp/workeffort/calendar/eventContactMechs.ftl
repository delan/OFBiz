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
 *@version    $Revision: 1.4 $
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>


<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align=left width='40%' >
            <div class='boxhead'>${uiLabelMap.WorkEffortCalendarEventContacts} : ${workEffort.workEffortName?if_exists}</div>
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
                  <tr>
                    <td>
                    <#if workEffortId?exists>
                      <div class='tabContainer'>
                          <a href="<@ofbizUrl>/event?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortEvent}</a>
                          <a href="<@ofbizUrl>/eventPartyAssignments?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButton">Parties</a>
                          <a href="<@ofbizUrl>/eventContactMechs?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButtonSelected">${uiLabelMap.PartyContactInformation}</a>
                      </div>
                    </#if>
<#-- ============================================================= -->
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxinside'>
        <tr>
          <td>
  <#if contactMeches?has_content>
    <table width="100%" border="0" cellpadding="0">
      <tr align=left valign=bottom>
        <th>${uiLabelMap.WorkEffortContactType}</th>
        <th width="5">&nbsp;</th>
        <th>${uiLabelMap.WorkEffortInformation}</th>
        <th>&nbsp;</th>
      </tr>
      <#list contactMeches as contactMechMap>
          <#assign contactMech = contactMechMap.contactMech>
          <#assign workEffortContactMech = contactMechMap.workEffortContactMech>
          <tr><td colspan="4"><hr class='sepbar'></td></tr>
          <tr>
            <td align="right" valign="top" width="10%">
              <div class="tabletext">&nbsp;<b>${contactMechMap.contactMechType.description}</b></div>
            </td>
            <td width="5">&nbsp;</td>
            <td align="left" valign="top" width="80%">
              <#if contactMechMap.postalAddress?exists>
                  <#assign postalAddress = contactMechMap.postalAddress>
                  <div class="tabletext">                    
                    <#if postalAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b> ${postalAddress.toName}<br></#if>
                    <#if postalAddress.attnName?has_content><b>${uiLabelMap.CommonAttn}:</b> ${postalAddress.attnName}<br></#if>
                    ${postalAddress.address1?if_exists}<br>
                    <#if postalAddress.address2?has_content><br></#if>
                    ${postalAddress.city?if_exists},
                    ${postalAddress.stateProvinceGeoId?if_exists}
                    ${postalAddress.postalCode?if_exists}
                    <#if postalAddress.countryGeoId?has_content><br>${postalAddress.countryGeoId}</#if>
                  </div>
                  <#if (postalAddress?has_content && !postalAddress.countryGeoId?has_content) || postalAddress.countryGeoId = "USA">
                    <#assign addr1 = postalAddress.address1?default("")>                 
                    <#if (addr1?index_of(' ') > 0)>
                      <#assign addressOther = "">
                      <#assign addressNum = "">
                      <#list addr1?split(" ") as seq>
                       <#if seq_index = 0>
                        <#assign addressNum = seq>
                       <#else>
                        <#assign addressOther = addressOther + " " + seq>
                       </#if>
                      </#list>
                      <a target='_blank' href='http://www.whitepages.com/find_person_results.pl?fid=a&s_n=${addressNum}&s_a=${addressOther?trim}&c=${postalAddress.city?if_exists}&s=${postalAddress.stateProvinceGeoId?if_exists}&x=29&y=18' class='buttontext'>(lookup:whitepages.com)</a>
                    </#if>
                  </#if>
              <#elseif contactMechMap.telecomNumber?exists>
                  <#assign telecomNumber = contactMechMap.telecomNumber>
                  <div class="tabletext">
                    ${telecomNumber.countryCode?if_exists}
                    <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode}-</#if>${telecomNumber.contactNumber}
                    
                    <#if (telecomNumber?has_content && !telecomNumber.countryCode?has_content) || telecomNumber.countryCode = "011">
                      <a target='_blank' href='http://www.anywho.com/qry/wp_rl?npa=${telecomNumber.areaCode?if_exists}&telephone=${telecomNumber.contactNumber?if_exists}&btnsubmit.x=20&btnsubmit.y=8' class='buttontext'>(${uiLabelMap.WorkEffortLookup}:anywho.com)</a>
                      <a target='_blank' href='http://whitepages.com/find_person_results.pl?fid=p&ac=${telecomNumber.areaCode?if_exists}&s=&p=${telecomNumber.contactNumber?if_exists}&pt=b&x=40&y=9' class='buttontext'>(${uiLabelMap.WorkEffortLookup}:whitepages.com)</a>
                    </#if>
                  </div>
              <#elseif "EMAIL_ADDRESS" = contactMech.contactMechTypeId>
                  <div class="tabletext">
                    ${contactMech.infoString?if_exists}
                    <a href='mailto:${contactMech.infoString?if_exists}' class='buttontext'>(send&nbsp;email)</a>
                  </div>
              <#elseif "WEB_ADDRESS" = contactMech.contactMechTypeId>
                  <div class="tabletext">
                    ${contactMech.infoString?if_exists}
                    <#assign openAddress = contactMech.infoString?default("")>
                    <#if !openAddress?starts_with("http") && !openAddress.starts_with("HTTP")><#assign openAddress = "http://" + openAddress></#if>
                    <a target='_blank' href='${openAddress}' class='buttontext'>(${uiLabelMap.WorkEffortOpenPageInNewWindow})</a>
                  </div>
              <#else>
                  <div class="tabletext">
                    ${contactMech.infoString?if_exists}
                  </div>
              </#if>
            </td>
            
            <#if security.hasEntityPermission("WORKEFFORTMGR", "_DELETE", session)>
            <td align="right" valign="top" width="1%" nowrap>
              <div><a href='<@ofbizUrl>/editContactMech?contactMechId=${contactMech.contactMechId}&workEffortId=${workEffortId}</@ofbizUrl>' class="buttontext">
              ${uiLabelMap.CommonEdit}</a>&nbsp;|&nbsp;<a href='<@ofbizUrl>/deleteContactMech/eventContactMechs?contactMechId=${contactMech.contactMechId}&workEffortId=${workEffortId}</@ofbizUrl>' class="buttontext">
              ${uiLabelMap.CommonRemove}</a>&nbsp;&nbsp;</div>
            </td>
            <#else>
            <td width="5">&nbsp;</td>
            </#if>
          </tr>
                </#list>
            <#else>
    <tr><td colspan="4"><div class="tabletext">${uiLabelMap.WorkEffortNoContactInformationOnFile}.</div></td></tr>
  </#if>

      <#if workEffort?exists>
                <tr>
                  <td colspan="4"><hr class="sepbar"></td>
                </tr>
                <tr>
                  <td colspan="4"></td>
                </tr>
                <tr>
                  <td colspan="4">
                    <form method="post" action="<@ofbizUrl>/addContactMech?DONE_PAGE=/eventContactMechs</@ofbizUrl>">
                      <input type="hidden" name="workEffortId" value="${workEffortId}">
					  <input type="hidden" name="statusId" value="CAL_ACCEPTED">
                      <table width="100%" cellpadding="2" cellspacing="0" border="0">
                        <tr>
                        <td nowrap>${uiLabelMap.CommonAddNew}<select name="preContactMechTypeId" class="selectBox">
              <#list contactMechTypes as contactMechType>
                <option value='${contactMechType.contactMechTypeId}'>${contactMechType.description}</option>
              </#list>
            </select>
            
            &nbsp;from party&nbsp;
            <select name="partyId" class="selectBox">
                          <option selected value="">${uiLabelMap.CommonNone}</option>
                          <#list roles as role>
                           <#assign party = delegator.findByPrimaryKey("Party", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",role.partyId))>
                            <#assign partyGroup = delegator.findByPrimaryKey("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",party.partyId))?if_exists>
                            <#assign person = delegator.findByPrimaryKey("Person", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",party.partyId))?if_exists>                                                                                     
                            <#assign partyName = "">
                            <#if person?has_content>
                              <#assign partyName = person.firstName+"&nbsp;"+person.lastName>
                            <#elseif partyGroup?has_content>
                              <#assign partyName = partyGroup.groupName>
                            </#if>
                                                      
                          <option value="${role.partyId}">${partyName}</option>
                          </#list>
                          </select>
                          </td>
						  <td width="100%"><input type="submit" style="font-size: small;" value="${uiLabelMap.CommonAdd}"></td>
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
<#-- ============================================================= -->
                    </td>
                  </tr>
                
              </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>

