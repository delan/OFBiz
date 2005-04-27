<#--
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
 * @author     Johan Isacsson
 * @author     David E. Jones
 * @author     Andy Zeneski
 * @created    May 26 2003
 *@author     Olivier Heintz (olivier.heintz@nereide.biz)
 * @version    1.0
 */
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#-- <#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)> -->

<script language='JavaScript'>
    function setNowFromDate(formName) { eval('document.' + formName + '.fromDate.value="${nowStr}"'); }
</script>

${pages.get("/party/viewcustomers.ftl")}
<BR>

<#if party?has_content>
<#-- Main Heading -->
<table width='100%' cellpadding='0' cellspacing='0' border='0'>
  <tr>
    <td align='left'>
      <div class="head1">${uiLabelMap.PartyTheProfileOf}
        <#if lookupPerson?has_content>
          ${lookupPerson.personalTitle?if_exists}
          ${lookupPerson.firstName?if_exists}
          ${lookupPerson.middleName?if_exists}
          ${lookupPerson.lastName?if_exists}
          ${lookupPerson.suffix?if_exists}
        <#elseif lookupGroup?has_content>
          ${lookupGroup.groupName?default("${uiLabelMap.PartyNoNameGroup}")}
        <#else>
          ${uiLabelMap.PartyNewUser}
       </#if>
      </div>
    </td>
  </tr>
</table>
<br/>

<TABLE border="0" width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <#if lookupPerson?has_content>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;${uiLabelMap.PartyPersonalInformation}</div>
            </td>
            <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session)>
            <td valign="middle" align="right">
              <a href="<@ofbizUrl>/editperson?partyId=${party.partyId}</@ofbizUrl>" class="submenutextright">
              <#if lookupPerson?has_content>${uiLabelMap.CommonUpdate}</#if></a>
            </td>
            </#if>
          </#if>
          <#if lookupGroup?has_content>
            <#assign lookupPartyType = party.getRelatedOneCache("PartyType")>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;${uiLabelMap.PartyPartyGroupInformation}</div>
            </td>
            <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session)>
            <td valign="middle" align="right">
              <a href="<@ofbizUrl>/editpartygroup?partyId=${party.partyId}</@ofbizUrl>" class="submenutextright">
              <#if lookupGroup?has_content>${uiLabelMap.CommonUpdate}</#if></a>
            </td>
            </#if>
          </#if>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
<#if lookupPerson?has_content>
  <table width="100%" border="0" cellpadding="0" cellspacing='0'>
    <tr>
      <td align="right" width="10%"><div class="tabletext"><b>${uiLabelMap.PartyName}</b></div></td>
      <td width="5">&nbsp;</td>
      <td align="left" width="90%">
        <div class="tabletext">
          ${lookupPerson.personalTitle?if_exists}
          ${lookupPerson.firstName?if_exists}
          ${lookupPerson.middleName?if_exists}
          ${lookupPerson.lastName?if_exists}
          ${lookupPerson.suffix?if_exists}
        </div>
      </td>
    </tr>
    <#if lookupPerson.nickname?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartyNickname}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.nickname}</div></td></tr>
    </#if>
    <#if lookupPerson.gender?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartyGender}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.gender}</div></td></tr>
    </#if>
    <#if lookupPerson.birthDate?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartyBirthDate}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.birthDate.toString()}</div></td></tr>
    </#if>
    <#if lookupPerson.height?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartyHeight}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.height}</div></td></tr>
    </#if>
    <#if lookupPerson.weight?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartyWeight}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.weight}</div></td></tr>
    </#if>
    <#if lookupPerson.mothersMaidenName?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartyMothersMaidenName}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.mothersMaidenName}</div></td></tr>
    </#if>
    <#if lookupPerson.maritalStatus?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartyMaritalStatus}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.maritalStatus}</div></td></tr>
    </#if>
    <#if lookupPerson.socialSecurityNumber?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartySocialSecurityNumber}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.socialSecurityNumber}</div></td></tr>
    </#if>
    <#if lookupPerson.passportNumber?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartyPassportNumber}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.passportNumber}</div></td></tr>
    </#if>
    <#if lookupPerson.passportExpireDate?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartyPassportExpire}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.passportExpireDate.toString()}</div></td></tr>
    </#if>
    <#if lookupPerson.totalYearsWorkExperience?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartyYearsWork}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.totalYearsWorkExperience}</div></td></tr>
    </#if>
    <#if lookupPerson.comments?has_content>
    <tr><td align="right" nowrap><div class='tabletext'><b>${uiLabelMap.PartyComments}</b></div></td><td>&nbsp;</td><td align="left"><div class='tabletext'>${lookupPerson.comments}</div></td></tr>
    </#if>
  </table>
<#elseif lookupGroup?has_content>
    <div class="tabletext">${lookupGroup.groupName} (${(lookupPartyType.description)?if_exists})</div>
<#else>
    <div class="tabletext">${uiLabelMap.PartyInformationNotFound}</div>
</#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<#-- ============================================================= -->
<br/>
<TABLE border="0" width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.PartyContactInformation}</div>
          </td>
          <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
          <td valign="middle" align="right">
              <a href="<@ofbizUrl>/editcontactmech?partyId=${party.partyId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonCreateNew}</a>
          </td>
          </#if>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
  <#if contactMeches?has_content>
    <table width="100%" border="0" cellpadding="0">
      <tr align="left" valign=bottom>
        <th><div class="tableheadtext">${uiLabelMap.PartyContactType}</th>
        <th width="5">&nbsp;</th>
        <th><div class="tableheadtext">${uiLabelMap.PartyContactInformation}</th>
        <th colspan='2'><div class="tableheadtext">${uiLabelMap.PartyContactSolicitingOk}</th>
        <th>&nbsp;</th>
      </tr>
      <#list contactMeches as contactMechMap>
          <#assign contactMech = contactMechMap.contactMech>
          <#assign partyContactMech = contactMechMap.partyContactMech>
          <tr><td colspan="7"><hr class='sepbar'></td></tr>
          <tr>
            <td align="right" valign="top" width="10%">
              <div class="tabletext">&nbsp;<b>${contactMechMap.contactMechType.description}</b></div>
            </td>
            <td width="5">&nbsp;</td>
            <td align="left" valign="top" width="80%">
              <#list contactMechMap.partyContactMechPurposes as partyContactMechPurpose>
                  <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType")>
                    <div class="tabletext">
                      <#if contactMechPurposeType?has_content>
                        <b>${contactMechPurposeType.description}</b>
                      <#else>
                        <b>${uiLabelMap.PartyMechPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"</b>
                      </#if>
                      <#if partyContactMechPurpose.thruDate?has_content>
                      (${uiLabelMap.CommonExpire}: ${partyContactMechPurpose.thruDate.toString()})
                      </#if>
                    </div>
              </#list>
              <#if "POSTAL_ADDRESS" = contactMech.contactMechTypeId>
                  <#assign postalAddress = contactMechMap.postalAddress>
                  <div class="tabletext">
                    <#if postalAddress.toName?has_content><b>${uiLabelMap.PartyAddrToName}:</b> ${postalAddress.toName}<br/></#if>
                    <#if postalAddress.attnName?has_content><b>${uiLabelMap.PartyAddrAttnName}:</b> ${postalAddress.attnName}<br/></#if>
                    ${postalAddress.address1?if_exists}<br/>
                    <#if postalAddress.address2?has_content><br/></#if>
                    ${postalAddress.city?if_exists},
                    ${postalAddress.stateProvinceGeoId?if_exists}
                    ${postalAddress.postalCode?if_exists}
                    <#if postalAddress.countryGeoId?has_content><br/>${postalAddress.countryGeoId}</#if>
                  </div>
                  <#if (postalAddress?has_content && !postalAddress.countryGeoId?has_content) || postalAddress.countryGeoId = "USA">
                      <#assign addr1 = postalAddress.address1?if_exists>
                      <#if (addr1.indexOf(" ") > 0)>
                        <#assign addressNum = addr1.substring(0, addr1.indexOf(" "))>
                        <#assign addressOther = addr1.substring(addr1.indexOf(" ")+1)>
                        <a target='_blank' href='http://www.whitepages.com/find_person_results.pl?fid=a&s_n=${addressNum}&s_a=${addressOther}&c=${postalAddress.city?if_exists}&s=${postalAddress.stateProvinceGeoId?if_exists}&x=29&y=18' class='buttontext'>(lookup:whitepages.com)</a>
                      </#if>
                  </#if>
              <#elseif "TELECOM_NUMBER" = contactMech.contactMechTypeId>
                  <#assign telecomNumber = contactMechMap.telecomNumber>
                  <div class="tabletext">
                    ${telecomNumber.countryCode?if_exists}
                    <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode}-</#if>${telecomNumber.contactNumber}
                    <#if partyContactMech.extension?has_content>${uiLabelMap.PartyContactExt}&nbsp;${partyContactMech.extension}</#if>
                    <#if (telecomNumber?has_content && !telecomNumber.countryCode?has_content) || telecomNumber.countryCode = "011">
                      <a target='_blank' href='http://www.anywho.com/qry/wp_rl?npa=${telecomNumber.areaCode?if_exists}&telephone=${telecomNumber.contactNumber?if_exists}&btnsubmit.x=20&btnsubmit.y=8' class='buttontext'>(lookup:anywho.com)</a>
                      <a target='_blank' href='http://whitepages.com/find_person_results.pl?fid=p&ac=${telecomNumber.areaCode?if_exists}&s=&p=${telecomNumber.contactNumber?if_exists}&pt=b&x=40&y=9' class='buttontext'>(lookup:whitepages.com)</a>
                    </#if>
                  </div>
              <#elseif "EMAIL_ADDRESS" = contactMech.contactMechTypeId>
                  <div class="tabletext">
                    ${contactMech.infoString?if_exists}
                    <a href='mailto:${contactMech.infoString?if_exists}' class='buttontext'>(${uiLabelMap.CommonSendEmail})</a>
                  </div>
              <#elseif "WEB_ADDRESS" = contactMech.contactMechTypeId>
                  <div class="tabletext">
                    ${contactMech.infoString?if_exists}
                    <#assign openAddress = contactMech.infoString?default("")>
                    <#if !openAddress?starts_with("http") && !openAddress?starts_with("HTTP")><#assign openAddress = "http://" + openAddress></#if>
                    <a target='_blank' href='${openAddress}' class='buttontext'>(${uiLabelMap.CommonOpenPageNewWindow})</a>
                  </div>
              <#else>
                  <div class="tabletext">
                    ${contactMech.infoString?if_exists}
                  </div>
              </#if>
              <div class="tabletext">(${uiLabelMap.CommonUpdated}:&nbsp;${partyContactMech.fromDate.toString()})</div>
              <#if partyContactMech.thruDate?has_content><div class='tabletext'><b>${uiLabelMap.PartyContactEffectiveThru}:&nbsp;${partyContactMech.thruDate.toString()}</b></div></#if>
            </td>
            <td align="center" valign="top" nowrap width="1%"><div class="tabletext"><b>(${partyContactMech.allowSolicitation?if_exists})</b></div></td>
            <td width="5">&nbsp;</td>
            <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session)>
            <td align="right" valign="top" nowrap width="1%">
              <div><a href='<@ofbizUrl>/editcontactmech?partyId=${party.partyId}&contactMechId=${contactMech.contactMechId}</@ofbizUrl>' class="buttontext">
              [${uiLabelMap.CommonUpdate}]</a>&nbsp;</div>
            </td>
            </#if>
            <#if security.hasEntityPermission("PARTYMGR", "_DELETE", session)>
            <td align="right" valign="top" width="1%">
              <div><a href='<@ofbizUrl>/deleteContactMech/viewprofile?partyId=${party.partyId}&contactMechId=${contactMech.contactMechId}&partyId=${partyId}</@ofbizUrl>' class="buttontext">
              [${uiLabelMap.CommonExpire}]</a>&nbsp;&nbsp;</div>
            </td>
            </#if>
          </tr>
      </#list>
    </table>
  <#else>
    <div class="tabletext">${uiLabelMap.PartyNoContactInformation}</div>
  </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
<#-- =====================Pay Method Information======================================== -->

<#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
<br/>
<TABLE border="0" width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.PartyPaymentMethodInformation}</div>
          </td>
          <td valign="middle" align="right">
             <#if security.hasEntityPermission("PAY_INFO", "_CREATE", session)>
                <a href="<@ofbizUrl>/editcreditcard?partyId=${party.partyId}</@ofbizUrl>" class="submenutext">${uiLabelMap.AccountingCreateNewCreditCard}</a> <#-- <a href="<@ofbizUrl>/editgiftcard?partyId=${party.partyId}</@ofbizUrl>" class="submenutext">${uiLabelMap.AccountingCreateNewGiftCard}</a> --> <a href="<@ofbizUrl>/editeftaccount?partyId=${party.partyId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.AccountingCreateNewEftAccount}</a>
             </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
              <#if paymentMethodValueMaps?has_content>
              <table width="100%" border="0" cellpadding="1">
                <tr>
                  <td align="left">
                      <table width="100%" cellpadding="2" cellspacing="0" border="0">
                        <#list paymentMethodValueMaps as paymentMethodValueMap>
                            <#assign paymentMethod = paymentMethodValueMap.paymentMethod>
                            <tr>
                              <#if "CREDIT_CARD" == paymentMethod.paymentMethodTypeId>
                                  <#assign creditCard = paymentMethodValueMap.creditCard>
                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>
                                        ${uiLabelMap.AccountingCreditCard}: ${creditCard.nameOnCard} -
                                        ${creditCard.cardType}
                                        ${creditCard.cardNumber}
                                        ${creditCard.expireDate}
                                      </b>
                                      (${uiLabelMap.CommonUpdated}:&nbsp;${paymentMethod.fromDate.toString()})
                                      <#if paymentMethod.thruDate?has_content><b>(${uiLabelMap.PartyContactEffectiveThru}:&nbsp;${paymentMethod.thruDate})</b></#if>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                <#if security.hasEntityPermission("PAY_INFO", "_UPDATE", session)>                       
                                  <td align="right" valign="top" width='1%' nowrap>
                                        <div><a href='<@ofbizUrl>/editcreditcard?partyId=${party.partyId}&paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                        [${uiLabelMap.CommonUpdate}]</a></div>
                                  </td>
                                  
                               </#if>
                              <#elseif "GIFT_CARD" == paymentMethod.paymentMethodTypeId>
                                  <#assign giftCard = paymentMethodValueMap.giftCard>
                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>
                                        ${uiLabelMap.AccountingGiftCard}:
                                        <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                                            ${giftCard.cardNumber?default("N/A")} [${giftCard.pinNumber?default("N/A")}]
                                        <#else>
                                            <#if giftCard?has_content && giftCard.cardNumber?has_content>
                                              <#assign giftCardNumber = "">
                                              <#assign pcardNumber = giftCard.cardNumber>
                                              <#if pcardNumber?has_content>
                                                <#assign psize = pcardNumber?length - 4>
                                                <#if 0 < psize>
                                                  <#list 0 .. psize-1 as foo>
                                                    <#assign giftCardNumber = giftCardNumber + "*">
                                                  </#list>
                                                  <#assign giftCardNumber = giftCardNumber + pcardNumber[psize .. psize + 3]>
                                                <#else>
                                                  <#assign giftCardNumber = pcardNumber>
                                                </#if>
                                              </#if>
                                            </#if>
                                            ${giftCardNumber?default("N/A")}
                                        </#if>
                                      </b>
                                      (${uiLabelMap.CommonUpdated}:&nbsp;${paymentMethod.fromDate.toString()})
                                      <#if paymentMethod.thruDate?has_content><b>(${uiLabelMap.PartyContactEffectiveThru}:&nbsp;${paymentMethod.thruDate.toString()}</b></#if>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <#if security.hasEntityPermission("PAY_INFO", "_UPDATE", session)>
                                        <div><a href='<@ofbizUrl>/editeftaccount?partyId=${party.partyId}&paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                        [${uiLabelMap.CommonUpdate}]</a></div>
                                    </#if>
                                  </td>
                              <#elseif "EFT_ACCOUNT" == paymentMethod.paymentMethodTypeId>
                                  <#assign eftAccount = paymentMethodValueMap.eftAccount>
                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>
                                        ${uiLabelMap.PartyEftAccount}: ${eftAccount.nameOnAccount} - <#if eftAccount.bankName?has_content>${uiLabelMap.PartyBank}: ${eftAccount.bankName}</#if> <#if eftAccount.accountNumber?has_content>${uiLabelMap.PartyAccount} #: ${eftAccount.accountNumber}</#if>
                                      </b>
                                      (${uiLabelMap.CommonUpdated}:&nbsp;${paymentMethod.fromDate.toString()})
                                      <#if paymentMethod.thruDate?has_content><b>(${uiLabelMap.PartyContactEffectiveThru}:&nbsp;${paymentMethod.thruDate.toString()}</b></#if>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <#if security.hasEntityPermission("PAY_INFO", "_UPDATE", session)>
                                        <div><a href='<@ofbizUrl>/editeftaccount?partyId=${party.partyId}&paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                        [${uiLabelMap.CommonUpdate}]</a></div>
                                    </#if>
                                  </td>
                              </#if>
                              <td align="right" valign="top" width='1%'>
                                <#if security.hasEntityPermission("PAY_INFO", "_DELETE", session)>
                                    <div><a href='<@ofbizUrl>/deletePaymentMethod/viewprofile?partyId=${party.partyId}&paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                    [${uiLabelMap.CommonExpire}]</a></div>
                                </#if>
                              </td>
                            </tr>
                        </#list>
                      </table>

                  </td>
                </tr>
              </table>
              <#else>
                <div class="tabletext">${uiLabelMap.PartyNoPaymentMethodInformation}</div>
              </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
</#if>
<#if security.hasEntityPermission("SALESREP_ORDER", "_VIEW", session)>
<BR>
${pages.get("/party/viewcustorderhistory.ftl")}
</#if>
<#else>
    <div class="tabletext">${uiLabelMap.PartyNoPartyFoundWithPartyId}: ${partyId?if_exists}</div>
</#if>
