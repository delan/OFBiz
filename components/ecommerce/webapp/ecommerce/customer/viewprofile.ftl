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
 *@author     David E. Jones (jonesde@ofbiz.org) 
 *@version    $Revision: 1.9 $
 *@since      2.1
-->
<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if party?exists>
<#-- Main Heading -->
<table width='100%' cellpadding='0' cellspacing='0' border='0'>
  <tr>
    <td align=left>
      <div class="head1">${uiLabelMap.PartyTheProfileOf}
        <#if person?exists>
          ${person.personalTitle?if_exists}
          ${person.firstName?if_exists}
          ${person.middleName?if_exists}
          ${person.lastName?if_exists}
          ${person.suffix?if_exists}
        <#else>
          "${uiLabelMap.PartyNewUser}"
        </#if>
      </div>
    </td>
    <td align=right>
      <#if showOld>
        <a href="<@ofbizUrl>/viewprofile</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartyHideOld}]</a>&nbsp;&nbsp;
      <#else>
        <a href="<@ofbizUrl>/viewprofile?SHOW_OLD=true</@ofbizUrl>" class="buttontext">[${uiLabelMap.PartyShowOld}]</a>&nbsp;&nbsp;
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
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.PartyPersonalInformation}</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/editperson</@ofbizUrl>" class="submenutextright">
            <#if person?exists>${uiLabelMap.CommonUpdate}<#else>${uiLabelMap.CommonCreate}</#if></a>
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
<#if person?exists>
  <table width="100%" border="0" cellpadding="0" cellspacing='0'>
    <tr>
      <td align="right" width="10%"><div class="tabletext"><b>${uiLabelMap.PartyName}</b></div></td>
      <td width="5">&nbsp;</td>
      <td align="left" width="90%">
        <div class="tabletext">
          ${person.personalTitle?if_exists}
          ${person.firstName?if_exists}
          ${person.middleName?if_exists}
          ${person.lastName?if_exists}
          ${person.suffix?if_exists}
        </div>
      </td>
    </tr>
    <#if person.nickname?has_content><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyNickName}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.nickname}</div></td></tr></#if>
    <#if person.gender?has_content><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyGender}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.gender}</div></td></tr></#if>
    <#if person.birthDate?exists><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyBirthDate}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.birthDate.toString()}</div></td></tr></#if>
    <#if person.height?exists><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyHeight}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.height}</div></td></tr></#if>
    <#if person.weight?exists><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyWeight}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.weight}</div></td></tr></#if>
    <#if person.mothersMaidenName?has_content><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyMaidenName}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.mothersMaidenName}</div></td></tr></#if>
    <#if person.maritalStatus?has_content><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyMaritalStatus}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.maritalStatus}</div></td></tr></#if>
    <#if person.socialSecurityNumber?has_content><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartySocialSecurityNumber}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.socialSecurityNumber}</div></td></tr></#if>
    <#if person.passportNumber?has_content><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyPassportNumber}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.passportNumber}</div></td></tr></#if>
    <#if person.passportExpireDate?exists><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyPassportExpireDate}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.passportExpireDate.toString()}</div></td></tr></#if>
    <#if person.totalYearsWorkExperience?exists><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.PartyYearsWork}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.totalYearsWorkExperience}</div></td></tr></#if>
    <#if person.comments?has_content><tr><td align=right nowrap><div class='tabletext'><b>${uiLabelMap.CommonComments}</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.comments}</div></td></tr></#if>
  </table>
<#else>
<div class="tabletext">${uiLabelMap.PartyPersonalInformationNotFound}</div>
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
          <td valign="middle" align="right">
              <a href="<@ofbizUrl>/editcontactmech</@ofbizUrl>" class="submenutextright">${uiLabelMap.CommonCreateNew}</a>
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
  <#if partyContactMechValueMaps?has_content>
    <table width="100%" border="0" cellpadding="0">
      <tr align=left valign=bottom>
        <th>${uiLabelMap.PartyContactType}</th>
        <th width="5">&nbsp;</th>
        <th>${uiLabelMap.CommonInformation}</th>
        <th colspan='2'>${uiLabelMap.PartySolicitingOk}?</th>
        <th>&nbsp;</th>
      </tr>
      <#list partyContactMechValueMaps as partyContactMechValueMap>
        <#assign contactMech = partyContactMechValueMap.contactMech?if_exists>
        <#assign contactMechType = partyContactMechValueMap.contactMechType?if_exists>
        <#assign partyContactMech = partyContactMechValueMap.partyContactMech?if_exists>
          <tr><td colspan="7"><hr class='sepbar'></td></tr>
          <tr>
            <td align="right" valign="top" width="10%">
              <div class="tabletext">&nbsp;<b>${contactMechType.description}</b></div>
            </td>
            <td width="5">&nbsp;</td>
            <td align="left" valign="top" width="80%">
              <#list partyContactMechValueMap.partyContactMechPurposes?if_exists as partyContactMechPurpose> 
                  <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType")>
                    <div class="tabletext">
                      <#if contactMechPurposeType?exists>
                        <b>${contactMechPurposeType.description}</b>
                      <#else>
                        <b>${uiLabelMap.PartyPurposeTypeNotFound}: "${partyContactMechPurpose.contactMechPurposeTypeId}"</b>
                      </#if>
                      <#if partyContactMechPurpose.thruDate?exists>(${uiLabelMap.CommonExpire}:${partyContactMechPurpose.thruDate.toString()})</#if>
                    </div>
              </#list>
              <#if contactMech.contactMechTypeId?if_exists = "POSTAL_ADDRESS">
                  <#assign postalAddress = partyContactMechValueMap.postalAddress?if_exists>
                  <div class="tabletext">
                  <#if postalAddress?exists>
                    <#if postalAddress.toName?has_content><b>${uiLabelMap.CommonTo}:</b> ${postalAddress.toName}<br></#if>
                    <#if postalAddress.attnName?has_content><b>${uiLabelMap.PartyAddrAttnName}:</b> ${postalAddress.attnName}<br></#if>
                    ${postalAddress.address1}<br>
                    <#if postalAddress.address2?has_content>${postalAddress.address2}<br></#if>
                    ${postalAddress.city}<#if postalAddress.stateProvinceGeoId?has_content>,&nbsp;${postalAddress.stateProvinceGeoId}</#if>&nbsp;${postalAddress.postalCode?if_exists}
                    <#if postalAddress.countryGeoId?has_content><br>${postalAddress.countryGeoId}</#if>
                    <#if (!postalAddress.countryGeoId?has_content || postalAddress.countryGeoId?if_exists = "USA")>
                      <#assign addr1 = postalAddress.address1?if_exists>
                      <#if (addr1.indexOf(" ") > 0)>
                        <#assign addressNum = addr1.substring(0, addr1.indexOf(" "))>
                        <#assign addressOther = addr1.substring(addr1.indexOf(" ")+1)>
                        <a target='_blank' href='http://www.whitepages.com/find_person_results.pl?fid=a&s_n=${addressNum}&s_a=${addressOther}&c=${postalAddress.city?if_exists}&s=${postalAddress.stateProvinceGeoId?if_exists}&x=29&y=18' class='buttontext'>(lookup:whitepages.com)</a>
                      </#if>
                    </#if>
                  <#else>
                    ${uiLabelMap.PartyPostalInformationNotFound}.
                  </#if>
                  </div>
              <#elseif contactMech.contactMechTypeId?if_exists = "TELECOM_NUMBER">
                  <#assign telecomNumber = partyContactMechValueMap.telecomNumber?if_exists>
                  <div class="tabletext">
                  <#if telecomNumber?exists>
                    ${telecomNumber.countryCode?if_exists}
                    <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode}-</#if>${telecomNumber.contactNumber?if_exists}
                    <#if partyContactMech.extension?has_content>ext&nbsp;${partyContactMech.extension}</#if>
                    <#if (!telecomNumber.countryCode?has_content || telecomNumber.countryCode = "011")>
                      <a target='_blank' href='http://www.anywho.com/qry/wp_rl?npa=${telecomNumber.areaCode?if_exists}&telephone=${telecomNumber.contactNumber?if_exists}&btnsubmit.x=20&btnsubmit.y=8' class='buttontext'>(lookup:anywho.com)</a>
                      <a target='_blank' href='http://whitepages.com/find_person_results.pl?fid=p&ac=${telecomNumber.areaCode?if_exists}&s=&p=${telecomNumber.contactNumber?if_exists}&pt=b&x=40&y=9' class='buttontext'>(lookup:whitepages.com)</a>
                    </#if>
                  <#else>
                    ${uiLabelMap.PartyPhoneNumberInfoNotFound}.
                  </#if>
                  </div>
              <#elseif contactMech.contactMechTypeId?if_exists = "EMAIL_ADDRESS">
                  <div class="tabletext">
                    ${contactMech.infoString}
                    <a href='mailto:${contactMech.infoString}' class='buttontext'>(${uiLabelMap.PartySendEmail})</a>
                  </div>
              <#elseif contactMech.contactMechTypeId?if_exists = "WEB_ADDRESS">
                  <div class="tabletext">
                    ${contactMech.infoString}
                    <#assign openAddress = contactMech.infoString?if_exists>
                    <#if !openAddress.startsWith("http") && !openAddress.startsWith("HTTP")><#assign openAddress = "http://" + openAddress></#if>
                    <a target='_blank' href='${openAddress}' class='buttontext'>(${uiLabelMap.CommonOpenNewWindow})</a>
                  </div>
              <#else>
                  <div class="tabletext">${contactMech.infoString?if_exists}</div>
              </#if>
              <div class="tabletext">(${uiLabelMap.CommonUpdated}:&nbsp;${partyContactMech.fromDate.toString()})</div>
              <#if partyContactMech.thruDate?exists><div class='tabletext'><b>${uiLabelMap.CommonDelete}:&nbsp;${partyContactMech.thruDate.toString()}</b></div></#if>
            </td>
            <td align="center" valign="top" nowrap width="1%"><div class="tabletext"><b>(${partyContactMech.allowSolicitation?if_exists})</b></div></td>
            <td width="5">&nbsp;</td>
            <td align="right" valign="top" nowrap width="1%">
              <div><a href='<@ofbizUrl>/editcontactmech?contactMechId=${contactMech.contactMechId}</@ofbizUrl>' class="buttontext">
              [${uiLabelMap.CommonUpdate}]</a>&nbsp;</div>
            </td>
            <td align="right" valign="top" width="1%">
              <div><a href='<@ofbizUrl>/deleteContactMech/viewprofile?contactMechId=${contactMech.contactMechId}</@ofbizUrl>' class="buttontext">
              [${uiLabelMap.CommonExpire}]</a>&nbsp;&nbsp;</div>
            </td>
          </tr>
      </#list>
    </table>
  <#else>
    <p>${uiLabelMap.PartyNoContactInformation}.</p><br>
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
            <div class="boxhead">&nbsp;${uiLabelMap.AccountingPaymentMethodInformation}</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/editcreditcard</@ofbizUrl>" class="submenutext">${uiLabelMap.PartyCreateNewCreditCard}</a><a href="<@ofbizUrl>/editgiftcard</@ofbizUrl>" class="submenutext">${uiLabelMap.PartyCreateNewGiftCard}</a><a href="<@ofbizUrl>/editeftaccount</@ofbizUrl>" class="submenutextright">${uiLabelMap.PartyCreateNewEftAccount}</a>
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
              <table width="100%" border="0" cellpadding="1">
                <tr>
                  <td align="left">
                    <#if paymentMethodValueMaps?has_content>
                      <table width="100%" cellpadding="2" cellspacing="0" border="0">
                        <#list paymentMethodValueMaps as paymentMethodValueMap>
                            <#assign paymentMethod = paymentMethodValueMap.paymentMethod?if_exists>
                            <#assign creditCard = paymentMethodValueMap.creditCard?if_exists>
                            <#assign giftCard = paymentMethodValueMap.giftCard?if_exists>
                            <#assign eftAccount = paymentMethodValueMap.eftAccount?if_exists>
                            <tr>
                              <#if paymentMethod.paymentMethodTypeId?if_exists == "CREDIT_CARD">
                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>
                                        ${uiLabelMap.AccountingCreditCard}: ${creditCard.nameOnCard} - ${Static["org.ofbiz.party.contact.ContactHelper"].formatCreditCard(creditCard)}
                                      </b>
                                      (${uiLabelMap.CommonUpdated}:&nbsp;${paymentMethod.fromDate.toString()})
                                      <#if paymentMethod.thruDate?exists><b>(${uiLabelMap.CommonDelete}:&nbsp;${paymentMethod.thruDate.toString()})</b></#if>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <div><a href='<@ofbizUrl>/editcreditcard?paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                    [${uiLabelMap.CommonUpdate}]</a></div>
                                  </td>
                              <#elseif paymentMethod.paymentMethodTypeId?if_exists == "GIFT_CARD">
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

                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>${uiLabelMap.AccountingGiftCard}: ${giftCardNumber}</b>
                                      (${uiLabelMap.CommonUpdated}:&nbsp;${paymentMethod.fromDate.toString()})
                                      <#if paymentMethod.thruDate?exists><b>(${uiLabelMap.CommonDelete}:&nbsp;${paymentMethod.thruDate.toString()})</b></#if>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <div><a href='<@ofbizUrl>/editgiftcard?paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                    [${uiLabelMap.CommonUpdate}]</a></div>
                                  </td>
                              <#elseif paymentMethod.paymentMethodTypeId?if_exists == "EFT_ACCOUNT">
                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>${uiLabelMap.AccountingEftAccount}: ${eftAccount.nameOnAccount?if_exists} - <#if eftAccount.bankName?has_content>Bank: ${eftAccount.bankName}</#if> <#if eftAccount.accountNumber?has_content>${uiLabelMap.AccountingAccount} #: ${eftAccount.accountNumber}</#if></b>
                                      (${uiLabelMap.CommonUpdated}:&nbsp;${paymentMethod.fromDate.toString()})
                                      <#if paymentMethod.thruDate?exists><b>(${uiLabelMap.CommonDelete}:&nbsp;${paymentMethod.thruDate.toString()})</b></#if>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <div><a href='<@ofbizUrl>/editeftaccount?paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                    [${uiLabelMap.CommonUpdate}]</a></div>
                                  </td>
                              </#if>
                              <td align="right" valign="top" width='1%'>
                                <div><a href='<@ofbizUrl>/deletePaymentMethod/viewprofile?paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                [${uiLabelMap.CommonExpire}]</a></div>
                              </td>
                            </tr>
                        </#list>
                      </table>
                    <#else>
                      <div class='tabletext'>${uiLabelMap.AccountingNoPaymentMethodInformation}.</div>
                    </#if>
                  </td>
                </tr>
              </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;${uiLabelMap.CommonUsername} & ${uiLabelMap.CommonPassword}</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/changepassword</@ofbizUrl>" class="submenutextright">${uiLabelMap.PartyChangePassword}</a>
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
            <table width="100%" border="0" cellpadding="1">
              <tr>
                <td align="right" valign="top" width="10%" nowrap><div class="tabletext"><b>${uiLabelMap.CommonUsername}</b></div></td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="90%"><div class="tabletext">${userLogin.userLoginId}</div></td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<#if surveys?has_content>
  <br>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Surveys</div>
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
            <table width="100%" border="0" cellpadding="1">
              <#list surveys as surveyAppl>
                <#assign survey = surveyAppl.getRelatedOne("Survey")>
                <tr>
                  <td>&nbsp;</td>
                  <td align="left" valign="top" width="10%" nowrap><div class="tabletext"><b>${survey.surveyName?if_exists}</b>&nbsp;-&nbsp;${survey.description?if_exists}</div></td>
                  <td width="5">&nbsp;</td>
                  <td align="left" valign="top" width="80%">
                    <#assign responses = Static["org.ofbiz.product.store.ProductStoreWorker"].checkSurveyResponse(request, survey.surveyId)?default(0)>
                    <div class="tabletext"><#if (responses < 1)><font color="red"><b>Not Completed</b><#else>Completed - Thank-you!</if></div>
                  </td>
                  <#if (responses == 0 || survey.allowMultiple?default("N") == "Y")>
                    <#assign surveyLabel = "[Take Survey]">
                    <#if (responses > 0 && survey.allowUpdate?default("N") == "Y")>
                      <#assign surveyLabel = "[Update Survey]">
                    </#if>
                    <td align="right" width="10%" nowrap><a href="<@ofbizUrl>/takesurvey?productStoreSurveyId=${surveyAppl.productStoreSurveyId}</@ofbizUrl>" class="buttontext">${surveyLabel}</a></td>
                  <#else>
                    &nbsp;
                  </#if>
                </tr>
              </#list>
            </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
</#if>


<#else>
    <div class='head3'>${uiLabelMap.PartyNoPartyForCurrentUserName}: ${userLogin.userLoginId}</div>
</#if>

