<#--
/**
 *  Title: Edit Contact Mechanism Page
 *  Description: None
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *@author     Johan Isacsson
 *@author     Catherine.Heintz@nereide.biz (migration to uiLabelMap)
 *@created    June 02 2003
 *@version    1.2
 */
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>


<table border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <tr>
    <td width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td align=left width='40%' >
            <div class='boxhead'>&nbsp;${uiLabelMap.WorkEffortCalendarEvent}: ${workEffort.workEffortName?if_exists}</div>
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
  <div class='tabContainer'>
  <a href="<@ofbizUrl>/event?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButton">${uiLabelMap.WorkEffortEvent}</a>
  <a href="<@ofbizUrl>/eventPartyAssignments?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyParties}</a>
  <a href="<@ofbizUrl>/eventContactMechs?workEffortId=${workEffortId}</@ofbizUrl>" class="tabButtonSelected">${uiLabelMap.PartyContactInformation}</a>
  </div>					
<#-- ============================================================= -->

<#if (!security.hasEntityPermission("PARTYMGR", "_VIEW", session) && !mechMap.partyContactMech?exists && mechMap.contactMech?exists)>
  <p><h3>${uiLabelMap.PartyErrorContactInformation}</h3></p>
  &nbsp;<a href="<@ofbizUrl>/authview/${donePage}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonBack}]</a>
<#else>
  <#if mechMap.contactMechTypeId?has_content>
    <#if !mechMap.contactMech?has_content>
      <#if contactMeches?has_content><table>
	  <form method="post" action="<@ofbizUrl>/createContactMech</@ofbizUrl>">
	  <input type="hidden" name="DONE_PAGE" value="${donePage}">
	  <input type="hidden" name="workEffortId" value="${workEffortId}">
	  <tr><td colspan="5"><span class="head1">${uiLabelMap.WorkEffortSelectContactMechanism}</span></td></tr>
        <#list contactMeches as contactMechMap>
          <#assign contactMech = contactMechMap.contactMech>
          <tr><td colspan="5"><hr class='sepbar'></td></tr>
          <tr>
		  <td><input type="radio" value="${contactMech.contactMechId}" name="contactMechId"></td>
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
                    <#if contactMechMap.partyContactMech.extension?has_content>ext&nbsp;${contactMechMap.partyContactMech.extension}</#if>
                    <#if (telecomNumber?has_content && !telecomNumber.countryCode?has_content) || telecomNumber.countryCode = "011">
                      <a target='_blank' href='http://www.anywho.com/qry/wp_rl?npa=${telecomNumber.areaCode?if_exists}&telephone=${telecomNumber.contactNumber?if_exists}&btnsubmit.x=20&btnsubmit.y=8' class='buttontext'>(lookup:anywho.com)</a>
                      <a target='_blank' href='http://whitepages.com/find_person_results.pl?fid=p&ac=${telecomNumber.areaCode?if_exists}&s=&p=${telecomNumber.contactNumber?if_exists}&pt=b&x=40&y=9' class='buttontext'>(lookup:whitepages.com)</a>
                    </#if>
                  </div>
              <#elseif "EMAIL_ADDRESS" = contactMech.contactMechTypeId>
                  <div class="tabletext">
                    ${contactMech.infoString?if_exists}
                    <a href='mailto:${contactMech.infoString?if_exists}' class='buttontext'>(${uiLabelMap.PartySendEmail})</a>
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
			
            <td width="5">&nbsp;</td>
          </tr>
                </#list> 
				 <tr><td colspan="5"><input type="submit" value="${uiLabelMap.CommonAdd}"></td></tr>
				</form>     
      </table>
          <br>
      <p class="head1">${uiLabelMap.PartyCreateAdditionalContactInformation}</p>
      <#else>
      <#if partyId?has_content>
<#--		No contact information was found for the party with id ${partyId}<br> -->
         <#assign uiLabelWithVar=uiLabelMap.PartyNoContactInformationParty?interpret><@uiLabelWithVar/><br>
		<br>
	  </#if>
      <p class="head1">${uiLabelMap.PartyCreateNewContact}</p>
      </#if>

      <#if contactMechPurposeType?exists>
        <div>(${uiLabelMap.PartyNoteNewContactInformation}<b>"${contactMechPurposeType.description?if_exists}"</b>)</div>
      </#if>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <form method="post" action='<@ofbizUrl>/${mechMap.requestName}</@ofbizUrl>' name="editcontactmechform">
        <input type=hidden name='DONE_PAGE' value='${donePage}'>
        <input type=hidden name='contactMechTypeId' value='${mechMap.contactMechTypeId}'>
        <input type=hidden name='partyId' value='${partyId}'>
        <input type="hidden" name="workEffortId" value="${workEffortId}">        
        <#if preContactMechTypeId?exists><input type='hidden' name='preContactMechTypeId' value='${preContactMechTypeId}'></#if>
        <#if contactMechPurposeTypeId?exists><input type='hidden' name='contactMechPurposeTypeId' value='${contactMechPurposeTypeId?if_exists}'></#if>
        
        <#if paymentMethodId?exists><input type='hidden' name='paymentMethodId' value='${paymentMethodId}'></#if>
    <#else>
      <p class="head1">${uiLabelMap.PartyEditContactInformation}</p>
    &nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class='buttontext'>[${uiLabelMap.CommonGoBack}]</a>
    &nbsp;<a href="javascript:document.editcontactmechform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
      
        <tr>
          <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyContactPurposes}</div></td>
          <td width="5">&nbsp;</td>
          <td width="74%">
            <table border='0' cellspacing='1' bgcolor='black'>  
            <#if mechMap.partyContactMechPurposes?has_content>          
              <#list mechMap.partyContactMechPurposes as partyContactMechPurpose>
                <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType")>
                <tr>
                  <td bgcolor='white'>
                    <div class="tabletext">&nbsp;
                      <#if contactMechPurposeType?has_content>
                        <b>${contactMechPurposeType.description}</b>
                      <#else>
                        <b>${uiLabelMap.PartyPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"</b>
                      </#if>
                      (${uiLabelMap.CommonSince}:${partyContactMechPurpose.fromDate.toString()})
                      <#if partyContactMechPurpose.thruDate?has_content>(${uiLabelMap.CommonExpires} : ${partyContactMechPurpose.thruDate.toString()}</#if>
                    &nbsp;</div></td>
                  <td bgcolor='white'><div><a href='<@ofbizUrl>/deletePartyContactMechPurpose?partyId=${partyId}&contactMechId=${contactMechId}&contactMechPurposeTypeId=${partyContactMechPurpose.contactMechPurposeTypeId}&fromDate=${partyContactMechPurpose.fromDate.toString()}&DONE_PAGE=${donePage}&useValues=true</@ofbizUrl>' class='buttontext'>&nbsp;${uiLabelMap.CommonDelete}&nbsp;</a></div></td>
                </tr>
              </#list>
				</#if>              
              <#if mechMap.purposeTypes?has_content>
              <tr>
                <form method=POST action='<@ofbizUrl>/createPartyContactMechPurpose?DONE_PAGE=${donePage}&useValues=true</@ofbizUrl>' name='newpurposeform'>
                <input type=hidden name='partyId' value='${partyId?if_exists}'>
                <input type=hidden name='contactMechId' value='${contactMechId?if_exists}'>
                  <td bgcolor='white'>
                    <select name='contactMechPurposeTypeId' class="selectBox">
                      <option></option>
                      <#list mechMap.purposeTypes as contactMechPurposeType>
                        <option value='${contactMechPurposeType.contactMechPurposeTypeId}'>${contactMechPurposeType.description}</option>
                      </#list>
                    </select>
                  </td>
                </form>
                <td bgcolor='white'><div><a href='javascript:document.newpurposeform.submit()' class='buttontext'>&nbsp;${uiLabelMap.PartyAddPurpose}&nbsp;</a></div></td>
              </tr>
              </#if>
            </table>
          </td>
        </tr>
        <form method="post" action='<@ofbizUrl>/${mechMap.requestName}</@ofbizUrl>' name="editcontactmechform">
        <input type=hidden name="DONE_PAGE" value='${donePage}'>
        <input type=hidden name="contactMechId" value='${contactMechId}'>
        <input type=hidden name="contactMechTypeId" value='${mechMap.contactMechTypeId}'>
        <input type="hidden" name="workEffortId" value="${workEffortId}">
    </#if>
  
  <#if "POSTAL_ADDRESS" = mechMap.contactMechTypeId?if_exists>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyToName}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class="inputBox" size="30" maxlength="60" name="toName" value="${(mechMap.postalAddress.toName)?default(request.getParameter('toName')?if_exists)}">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyAttentionName}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class="inputBox" size="30" maxlength="60" name="attnName" value="${(mechMap.postalAddress.attnName)?default(request.getParameter('attnName')?if_exists)}">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyAddressLine1}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class="inputBox" size="30" maxlength="30" name="address1" value="${(mechMap.postalAddress.address1)?default(request.getParameter('address1')?if_exists)}">
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyAddressLine2}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
          <input type="text" class="inputBox" size="30" maxlength="30" name="address2" value="${(mechMap.postalAddress.address2)?default(request.getParameter('address2')?if_exists)}">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyCity}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
          <input type="text" class="inputBox" size="30" maxlength="30" name="city" value="${(mechMap.postalAddress.city)?default(request.getParameter('city')?if_exists)}">
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyStateProvince}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <select name="stateProvinceGeoId" class="selectBox">
          <option>${(mechMap.postalAddress.stateProvinceGeoId)?if_exists}</option>
          <option></option>
          ${pages.get("/includes/states.ftl")}
        </select>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyZipPostalCode}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class="inputBox" size="12" maxlength="10" name="postalCode" value="${(mechMap.postalAddress.postalCode)?default(request.getParameter('postalCode')?if_exists)}">
      *</td> 
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyCountry}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <select name="countryGeoId" class="selectBox">
          <option>${(mechMap.postalAddress.countryGeoId)?if_exists}</option>
          <option></option>
          ${pages.get("/includes/countries.ftl")}
        </select>
      *</td>
    </tr>
  <#elseif "TELECOM_NUMBER" = mechMap.contactMechTypeId?if_exists>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyPhoneNumber}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class="inputBox" size="4" maxlength="10" name="countryCode" value="${(mechMap.telecomNumber.countryCode)?default(request.getParameter('countryCode')?if_exists)}">
        -&nbsp;<input type="text" class="inputBox" size="4" maxlength="10" name="areaCode" value="${(mechMap.telecomNumber.areaCode)?default(request.getParameter('areaCode')?if_exists)}">
        -&nbsp;<input type="text" class="inputBox" size="15" maxlength="15" name="contactNumber" value="${(mechMap.telecomNumber.contactNumber)?default(request.getParameter('contactNumber')?if_exists)}">
        &nbsp;${uiLabelMap.PartyContactExt}&nbsp;<input type="text" class="inputBox" size="6" maxlength="10" name="extension" value="${(mechMap.partyContactMech.extension)?default(request.getParameter('extension')?if_exists)}">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext"></div></td>
      <td width="5">&nbsp;</td>
      <td><div class="tabletext">[${uiLabelMap.PartyCountryCode}] [${uiLabelMap.PartyAreaCode}] [${uiLabelMap.PartyContactNumber}] [${uiLabelMap.PartyContactExt}]</div></td>
    </tr>
  <#elseif "EMAIL_ADDRESS" = mechMap.contactMechTypeId?if_exists>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyEmailAddress}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
          <input type="text" class="inputBox" size="60" maxlength="255" name="emailAddress" value="${(mechMap.contactMech.infoString)?default(request.getParameter('emailAddress')?if_exists)}">
      *</td>
    </tr>
  <#else>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${mechMap.contactMechType.description}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
          <input type="text" class="inputBox" size="60" maxlength="255" name="infoString" value="${(mechMap.contactMech.infoString)?if_exists}">
      *</td>
    </tr>
  </#if>
  <#if partyId?has_content && security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyAddContactInformationParty}</div></td>
      <td width="5">&nbsp;</td>
                  <td width="74%">
          
      <input name="addToParty" type="checkbox" id="addToParty" value="true"></td>
    </tr> 
    <input type="hidden" name="partyId" value="${partyId}">   
  </#if>
  </form>
  </table>

    &nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonGoBack}]</a>
    &nbsp;<a href="javascript:document.editcontactmechform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>
  <#else>
    &nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonGoBack}]</a>
  </#if>
</#if>
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
