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
<#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>

<script language='JavaScript'>
    function setNowFromDate(formName) { eval('document.' + formName + '.fromDate.value="${nowStr}"'); }
</script>

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
    <td align='right'>
      <div class='tabContainer'>
        <a href="<@ofbizUrl>/viewprofile?partyId=${party.partyId}</@ofbizUrl>" class="tabButtonSelected">${uiLabelMap.PartyProfile}</a>
        <a href="<@ofbizUrl>/viewvendor?partyId=${party.partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyVendor}</a>
        <a href="<@ofbizUrl>/viewroles?partyId=${party.partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyRoles}</a>
        <a href="<@ofbizUrl>/viewrelationships?partyId=${party.partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyRelationships}</a>
        <a href="<@ofbizUrl>/viewcommunications?partyId=${partyId}</@ofbizUrl>" class="tabButton">${uiLabelMap.PartyCommunications}</a>
      </div>
    </td>
  </tr>
  <tr>
    <td colspan="2" align="right" nowrap>
      <#if showOld>
        <a href="<@ofbizUrl>/viewprofile?partyId=${party.partyId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartyHideOld}]</a>
      <#else>
        <a href="<@ofbizUrl>/viewprofile?partyId=${party.partyId}&SHOW_OLD=true</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartyShowOld}]</a>
      </#if>
      <a href="/accounting/control/FindBillingAccount?partyId=${partyId}${externalKeyParam}" class="buttontext">[${uiLabelMap.AccountingBillingAccount}]</a>
      <#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>
        <a href="/ordermgr/control/findorders?lookupFlag=Y&hideFields=Y&partyId=${partyId}${externalKeyParam}" class="buttontext">[${uiLabelMap.OrderOrders}]</a>
      </#if>
      <#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
        <a href="/ordermgr/control/orderentry?mode=SALES_ORDER&partyId=${partyId}${externalKeyParam}" class="buttontext">[${uiLabelMap.OrderNewOrder}]</a>
      </#if>
    </td>
  </tr>
</table>
<br>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
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
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyNickname}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.nickname}</div></td></tr>
    </#if>
    <#if lookupPerson.gender?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyGender}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.gender}</div></td></tr>
    </#if>
    <#if lookupPerson.birthDate?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyBirthDate}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.birthDate.toString()}</div></td></tr>
    </#if>
    <#if lookupPerson.height?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyHeight}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.height}</div></td></tr>
    </#if>
    <#if lookupPerson.weight?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyWeight}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.weight}</div></td></tr>
    </#if>
    <#if lookupPerson.mothersMaidenName?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyMothersMaidenName}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.mothersMaidenName}</div></td></tr>
    </#if>
    <#if lookupPerson.maritalStatus?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyMaritalStatus}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.maritalStatus}</div></td></tr>
    </#if>
    <#if lookupPerson.socialSecurityNumber?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartySocialSecurityNumber}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.socialSecurityNumber}</div></td></tr>
    </#if>
    <#if lookupPerson.passportNumber?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyPassportNumber}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.passportNumber}</div></td></tr>
    </#if>
    <#if lookupPerson.passportExpireDate?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyPassportExpire}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.passportExpireDate.toString()}</div></td></tr>
    </#if>
    <#if lookupPerson.totalYearsWorkExperience?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyYearsWork}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.totalYearsWorkExperience}</div></td></tr>
    </#if>
    <#if lookupPerson.comments?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyComments}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.comments}</div></td></tr>
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
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
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
      <tr align=left valign=bottom>
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
                    <#if postalAddress.toName?has_content><b>${uiLabelMap.PartyAddrToName}:</b> ${postalAddress.toName}<br></#if>
                    <#if postalAddress.attnName?has_content><b>${uiLabelMap.PartyAddrAttnName}:</b> ${postalAddress.attnName}<br></#if>
                    ${postalAddress.address1?if_exists}<br>
                    <#if postalAddress.address2?has_content><br></#if>
                    ${postalAddress.city?if_exists},
                    ${postalAddress.stateProvinceGeoId?if_exists}
                    ${postalAddress.postalCode?if_exists}
                    <#if postalAddress.countryGeoId?has_content><br>${postalAddress.countryGeoId}</#if>
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
                    <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode?default("000")}-</#if>${telecomNumber.contactNumber?default("000-0000")}
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
<#-- ============================================================= -->
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.PartyPaymentMethodInformation}</div>
          </td>
          <td valign="middle" align="right">
              <#if security.hasEntityPermission("PAY_INFO", "_CREATE", session)>
                <a href="<@ofbizUrl>/editcreditcard?partyId=${party.partyId}</@ofbizUrl>" class="submenutext">${uiLabelMap.AccountingCreateNewCreditCard}</a><a href="<@ofbizUrl>/editgiftcard?partyId=${party.partyId}</@ofbizUrl>" class="submenutext">${uiLabelMap.AccountingCreateNewGiftCard}</a><a href="<@ofbizUrl>/editeftaccount?partyId=${party.partyId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.AccountingCreateNewEftAccount}</a>
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
                                        ${uiLabelMap.AccountingCreditCard}: ${creditCard.firstNameOnCard} ${creditCard.lastNameOnCard} -
                                        <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                                            ${creditCard.cardType}
                                            ${creditCard.cardNumber}
                                            ${creditCard.expireDate}
                                        <#else>
                                            ${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}
                                        </#if>
                                      </b>
                                      (${uiLabelMap.CommonUpdated}:&nbsp;${paymentMethod.fromDate.toString()})
                                      <#if paymentMethod.thruDate?has_content><b>(${uiLabelMap.PartyContactEffectiveThru}:&nbsp;${paymentMethod.thruDate})</b></#if>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <div>
                                    <#if security.hasEntityPermission("MANUAL", "_PAYMENT", session)>
                                      <a href="/accounting/control/manualETx?paymentMethodId=${paymentMethod.paymentMethodId}${externalKeyParam}" class="buttontext">[Manual Tx]</a>
                                    </#if>
                                    <#if security.hasEntityPermission("PAY_INFO", "_UPDATE", session)>
                                        <a href='<@ofbizUrl>/editcreditcard?partyId=${party.partyId}&paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                        [${uiLabelMap.CommonUpdate}]</a>
                                    </#if>
                                    </div>
                                  </td>
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
                                        <div><a href='<@ofbizUrl>/editgiftcard?partyId=${party.partyId}&paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
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

<#-- AVS Strings -->
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.PartyCybersourceAvsOver}</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='2' class='boxbottom'>
        <tr>
          <td>
            <div class="tabletext"><b>${uiLabelMap.PartyAvsString}:</b>&nbsp;${(avsOverride.avsDeclineString)?default("Global")}</div>
          </td>
          <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session)>
          <td align="right" valign="top" width="1%">
            <a href="<@ofbizUrl>/editAvsOverride?partyId=${party.partyId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonEdit}]</a>
          </td>
          <#if avsOverride?exists>
            <td align="right" valign="top" width="1%">
              <a href="<@ofbizUrl>/resetAvsOverride?partyId=${party.partyId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonReset}]</a>
            </td>
          </#if>
          </#if>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<#-- UserLogins -->
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.PartyUserName}</div>
          </td>
          <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/createnewlogin?partyId=${party.partyId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonCreateNew}</a>
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
            <#if userLogins?exists>
            <table width="100%" border="0" cellpadding="1">
              <#list userLogins as userUserLogin>
              <tr>
                <td align="right" valign="top" width="10%" nowrap><div class="tabletext"><b>${uiLabelMap.PartyUserLogin}</b></div></td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="40%"><div class="tabletext">${userUserLogin.userLoginId}</div></td>
                <td align="left" valign="top" width="30%">
                  <div class="tabletext">
                    <#assign enabled = uiLabelMap.PartyEnabled>
                    <#if (userUserLogin.enabled)?default("Y") == "N">
                      <#if userUserLogin.disabledDateTime?exists>
                        <#assign disabledTime = userUserLogin.disabledDateTime.toString()>
                      <#else>
                        <#assign disabledTime = "??">
                      </#if>
                      <#assign enabled = uiLabelMap.PartyDisabled + " - " + disabledTime>
                    </#if>
                    ${enabled}
                  </div>
                </td>
                <td align="right" valign="top" width="20%">
                  <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
                      <a href="<@ofbizUrl>/editlogin?partyId=${party.partyId}&userlogin_id=${userUserLogin.userLoginId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.CommonEdit}]</a>&nbsp;
                  </#if>
                  <#if security.hasEntityPermission("SECURITY", "_VIEW", session)>
                      <a href="<@ofbizUrl>/EditUserLoginSecurityGroups?partyId=${party.partyId}&userLoginId=${userUserLogin.userLoginId}</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartySecurityGroups}]</a>&nbsp;
                  </#if>
                </td>
              </tr>
              </#list>
            </table>
            <#else>
              <div class="tabletext">${uiLabelMap.PartyNoUserLogin}</div>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<#-- Visits -->
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.PartyLastVisit}</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/showvisits?party_id=${partyId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonListAll}</a>
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
            <#if visits?exists>
            <table width="100%" border="0" cellpadding="2" cellspacing="0">
              <tr>
                <td><div class="tableheadtext">${uiLabelMap.PartyVisitId}</div></td>
                <td><div class="tableheadtext">${uiLabelMap.PartyUserLogin}</div></td>
                <td><div class="tableheadtext">${uiLabelMap.PartyNewUser}</div></td>
                <td><div class="tableheadtext">${uiLabelMap.PartyWebApp}</div></td>
                <td><div class="tableheadtext">${uiLabelMap.PartyClientIP}</div></td>
                <td><div class="tableheadtext">${uiLabelMap.CommonFromDate}</div></td>
                <td><div class="tableheadtext">${uiLabelMap.CommonThruDate}</div></td>
              </tr>
              <tr>
                <td colspan="7"><hr class="sepbar"></td>
              </tr>
              <#list visits as visitObj>
              <#if (visitObj_index > 4)><#break></#if>
              <tr>
                <td><a href="<@ofbizUrl>/visitdetail?visitId=${visitObj.visitId?if_exists}</@ofbizUrl>" class="buttontext">${visitObj.visitId?if_exists}</a></td>
                <td><div class="tabletext">${visitObj.userLoginId?if_exists}</div></td>
                <td><div class="tabletext">${visitObj.userCreated?if_exists}</div></td>
                <td><div class="tabletext">${visitObj.webappName?if_exists}</div></td>
                <td><div class="tabletext">${visitObj.clientIpAddress?if_exists}</div></td>
                <td><div class="tabletext">${(visitObj.fromDate.toString())?if_exists}</div></td>
                <td><div class="tabletext">${(visitObj.thruDate.toString())?if_exists}</div></td>
              </tr>
              </#list>
            </table>
            <#else>
              <div class="tabletext">${uiLabelMap.PartyNoVisitFound}</div>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<#-- Party Notes -->
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.CommonNotes}</div>
          </td>
          <#if security.hasEntityPermission("PARTYMGR", "_NOTE", session)>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/createnewnote?partyId=${partyId}</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonCreateNew}</a>
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
            <#if notes?has_content>
            <table width="100%" border="0" cellpadding="1">
              <#list notes as noteRef>
                <tr>
                  <td align="left" valign="top" width="35%">
                    <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonBy}: </b>${noteRef.firstName}&nbsp;${noteRef.lastName}</div>
                    <div class="tabletext">&nbsp;<b>${uiLabelMap.CommonAt}: </b>${noteRef.noteDateTime.toString()}</div>
                  </td>
                  <td align="left" valign="top" width="65%">
                    <div class="tabletext">${noteRef.noteInfo}</div>
                  </td>
                </tr>
                <#if noteRef_has_next>
                  <tr><td colspan="2"><hr class="sepbar"></td></tr>
                </#if>
              </#list>
            </table>
            <#else>
              <div class="tabletext">${uiLabelMap.PartyNoNotesForParty}</div>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<#else>
    ${uiLabelMap.PartyNoPartyFoundWithPartyId}: ${partyId?if_exists}
</#if>
<#else>
  <h3>${uiLabelMap.PartyMgrViewPermissionError}</h3>
</#if>
