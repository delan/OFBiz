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
 * @version    1.0
 */
-->
<#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>


<script language='JavaScript'>
    function setNowFromDate(formName) { eval('document.' + formName + '.fromDate.value="${nowStr}"'); }
</script>

<#if party?has_content>
<#-- Main Heading -->
<table width='100%' cellpadding='0' cellspacing='0' border='0'>
  <tr>
    <td align='left'>
      <div class="head1">The Profile of
        <#if lookupPerson?has_content>
          ${lookupPerson.personalTitle?if_exists}
          ${lookupPerson.firstName?if_exists}
          ${lookupPerson.middleName?if_exists}
          ${lookupPerson.lastName?if_exists}
          ${lookupPerson.suffix?if_exists}
        <#elseif lookupGroup?has_content>
          ${lookupGroup.groupName?default("No name (group)")}
        <#else>
          "New User"
       </#if>
      </div>
    </td>
    <td align='right'>
      <div class='tabContainer'>
        <a href="<@ofbizUrl>/viewprofile</@ofbizUrl>" class="tabButtonSelected">Profile</a>
        <a href="<@ofbizUrl>/viewroles</@ofbizUrl>" class="tabButton">Roles</a>
        <a href="<@ofbizUrl>/viewrelationships</@ofbizUrl>" class="tabButton">Relationships</a>
      </div>
      <nobr>
        <#if showOld>
          <a href="<@ofbizUrl>/viewprofile</@ofbizUrl>" class="buttontext">[Hide Old]</a>
        <#else>      
          <a href="<@ofbizUrl>/viewprofile?SHOW_OLD=true</@ofbizUrl>" class="buttontext">[Show Old]</a>
        </#if>
        <a href="/accounting/control/findBillingAccount?partyId=${partyId}${externalKeyParam}" class="buttontext">[Billing Accounts]</a>
        <#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>
          <a href="/ordermgr/control/orderlist?partyId=${partyId}${externalKeyParam}" class="buttontext">[Orders]</a>
        </#if>
        <#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
          <a href="/ordermgr/control/orderentry?mode=SALES_ORDER&partyId=${partyId}${externalKeyParam}" class="buttontext">[New Order]</a>
        </#if>      
      </nobr>
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
              <div class="boxhead">&nbsp;Personal Information</div>
            </td>
            <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session)>
            <td valign="middle" align="right">
              <a href="<@ofbizUrl>/editperson</@ofbizUrl>" class="lightbuttontext">
              [<#if lookupPerson?has_content>Update</#if>]</a>&nbsp;&nbsp;
            </td>
            </#if>            
          </#if>
          <#if lookupGroup?has_content>
            <#assign lookupPartyType = party.getRelatedOneCache("PartyType")>            
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;Party Group Information</div>
            </td>
            <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session)>
            <td valign="middle" align="right">
              <a href="<@ofbizUrl>/editpartygroup</@ofbizUrl>" class="lightbuttontext">
              [<#if lookupGroup?has_content>Update</#if>]</a>&nbsp;&nbsp;
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
      <td align="right" width="10%"><div class="tabletext"><b>Name</b></div></td>
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
    <tr><td align=right nowrap><div class='tabletext'><b>Nickname</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.nickname}</div></td></tr>
    </#if>
    <#if lookupPerson.gender?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>Gender</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.gender}</div></td></tr>
    </#if>
    <#if lookupPerson.birthDate?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>Birth Date</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.birthDate.toString()}</div></td></tr>
    </#if>
    <#if lookupPerson.height?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>Height</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.height}</div></td></tr>
    </#if>
    <#if lookupPerson.weight?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>Weight</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.weight}</div></td></tr>
    </#if>
    <#if lookupPerson.mothersMaidenName?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>Mothers Maiden Name</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.mothersMaidenName}</div></td></tr>
    </#if>
    <#if lookupPerson.maritalStatus?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>Marital Status</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.maritalStatus}</div></td></tr>
    </#if>
    <#if lookupPerson.socialSecurityNumber?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>Social Security Number</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.socialSecurityNumber}</div></td></tr>
    </#if>
    <#if lookupPerson.passportNumber?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>Passport Number</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.passportNumber}</div></td></tr>
    </#if>
    <#if lookupPerson.passportExpireDate?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>Passport Expire</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.passportExpireDate.toString()}</div></td></tr>
    </#if>
    <#if lookupPerson.totalYearsWorkExperience?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>Years Work</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.totalYearsWorkExperience}</div></td></tr>
    </#if>
    <#if lookupPerson.comments?has_content>
    <tr><td align=right nowrap><div class='tabletext'><b>Comments</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${lookupPerson.comments}</div></td></tr>
    </#if>
  </table>
<#elseif lookupGroup?has_content>
    <div class="tabletext">${lookupGroup.groupName} (${(lookupPartyType.description)?if_exists})</div>
<#else>
    <div class="tabletext">Information Not Found</div>
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
            <div class="boxhead">&nbsp;Contact Information</div>
          </td>
          <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
          <td valign="middle" align="right">
              <a href="<@ofbizUrl>/editcontactmech</@ofbizUrl>" class="lightbuttontext">
              [Create New]</a>&nbsp;&nbsp;
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
        <th>Contact&nbsp;Type</th>
        <th width="5">&nbsp;</th>
        <th>Information</th>
        <th colspan='2'>Soliciting&nbsp;OK?</th>
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
                        <b>Purpose Type not found with ID: "${partyContactMechPurpose.contactMechPurposeTypeId}"</b>
                      </#if>
                      <#if partyContactMechPurpose.thruDate?has_content>
                      (Expire: ${partyContactMechPurpose.thruDate.toString()})
                      </#if>
                    </div>
              </#list>
              <#if "POSTAL_ADDRESS" = contactMech.contactMechTypeId>
                  <#assign postalAddress = contactMechMap.postalAddress>
                  <div class="tabletext">                    
                    <#if postalAddress.toName?has_content><b>To:</b> ${postalAddress.toName}<br></#if>
                    <#if postalAddress.attnName?has_content><b>Attn:</b> ${postalAddress.attnName}<br></#if>
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
              <#elseif "TELECOM_NUMBER" = contactMech.contactMechTypeId>
                  <#assign telecomNumber = contactMechMap.telecomNumber>
                  <div class="tabletext">
                    ${telecomNumber.countryCode?if_exists}
                    <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode}-</#if>${telecomNumber.contactNumber}
                    <#if partyContactMech.extension?has_content>ext&nbsp;${partyContactMech.extension}</#if>
                    <#if (telecomNumber?has_content && !telecomNumber.countryCode?has_content) || telecomNumber.countryCode = "011">
                      <a target='_blank' href='http://www.anywho.com/qry/wp_rl?npa=${telecomNumber.areaCode?if_exists}&telephone=${telecomNumber.contactNumber?if_exists}&btnsubmit.x=20&btnsubmit.y=8' class='buttontext'>(lookup:anywho.com)</a>
                      <a target='_blank' href='http://whitepages.com/find_person_results.pl?fid=p&ac=${telecomNumber.areaCode?if_exists}&s=&p=${telecomNumber.contactNumber?if_exists}&pt=b&x=40&y=9' class='buttontext'>(lookup:whitepages.com)</a>
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
                    <a target='_blank' href='${openAddress}' class='buttontext'>(open&nbsp;page&nbsp;in&nbsp;new&nbsp;window)</a>
                  </div>
              <#else>
                  <div class="tabletext">
                    ${contactMech.infoString?if_exists}
                  </div>
              </#if>
              <div class="tabletext">(Updated:&nbsp;${partyContactMech.fromDate.toString()})</div>
              <#if partyContactMech.thruDate?has_content><div class='tabletext'><b>Effective Thru:&nbsp;${partyContactMech.thruDate.toString()}</b></div></#if>
            </td>
            <td align="center" valign="top" nowrap width="1%"><div class="tabletext"><b>(${partyContactMech.allowSolicitation?if_exists})</b></div></td>
            <td width="5">&nbsp;</td>
            <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session)>
            <td align="right" valign="top" nowrap width="1%">
              <div><a href='<@ofbizUrl>/editcontactmech?contactMechId=${contactMech.contactMechId}</@ofbizUrl>' class="buttontext">
              [Update]</a>&nbsp;</div>
            </td>
            </#if>
            <#if security.hasEntityPermission("PARTYMGR", "_DELETE", session)>
            <td align="right" valign="top" width="1%">
              <div><a href='<@ofbizUrl>/deleteContactMech/viewprofile?contactMechId=${contactMech.contactMechId}&partyId=${partyId}</@ofbizUrl>' class="buttontext">
              [Expire]</a>&nbsp;&nbsp;</div>
            </td>
            </#if>
          </tr>
      </#list>
    </table>
  <#else>
    <div class="tabletext">No contact information on file.</div>
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
            <div class="boxhead">&nbsp;Payment Method Information</div>
          </td>
          <td valign="middle" align="right">&nbsp;
              <#if security.hasEntityPermission("PAY_INFO", "_CREATE", session)>
                <a href="<@ofbizUrl>/editcreditcard</@ofbizUrl>" class="lightbuttontext">
                [Create New Credit Card]</a>&nbsp;
                <a href="<@ofbizUrl>/editeftaccount</@ofbizUrl>" class="lightbuttontext">
                [Create New EFT Account]</a>&nbsp;&nbsp;
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
                              <#if "CREDIT_CARD" = paymentMethod.paymentMethodTypeId>
                                  <#assign creditCard = paymentMethodValueMap.creditCard>
                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>
                                        Credit Card: ${creditCard.nameOnCard} - 
                                        <#if security.hasEntityPermission("PAY_INFO", "_VIEW", session)>
                                            ${creditCard.cardType}
                                            ${creditCard.cardNumber}
                                            ${creditCard.expireDate}
                                        <#else>
                                            ${Static["org.ofbiz.commonapp.party.contact.ContactHelper"].formatCreditCard(creditCard)}
                                        </#if>
                                      </b>
                                      (Updated:&nbsp;${paymentMethod.fromDate.toString()})
                                      <#if paymentMethod.thruDate?has_content><b>(Effective Thru:&nbsp;${paymentMethod.thruDate})</b></#if>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <#if security.hasEntityPermission("PAY_INFO", "_UPDATE", session)>
                                        <div><a href='<@ofbizUrl>/editcreditcard?paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                        [Update]</a></div>
                                    </#if>
                                  </td>
                              <#elseif "EFT_ACCOUNT" = paymentMethod.paymentMethodTypeId>
                                  <#assign eftAccount = paymentMethodValueMap.eftAccount>
                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>
                                        EFT Account: ${eftAccount.nameOnAccount} - <#if eftAccount.bankName?has_content>Bank: ${eftAccount.bankName}</#if> <#if eftAccount.accountNumber?has_content>Account #: ${eftAccount.accountNumber}</#if>
                                      </b>
                                      (Updated:&nbsp;${paymentMethod.fromDate.toString()})
                                      <#if paymentMethod.thruDate?has_content><b>(Effective Thru:&nbsp;${paymentMethod.thruDate.toString()}</b></#if>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <#if security.hasEntityPermission("PAY_INFO", "_UPDATE", session)>
                                        <div><a href='<@ofbizUrl>/editeftaccount?paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                        [Update]</a></div>
                                    </#if>
                                  </td>
                              </#if>
                              <td align="right" valign="top" width='1%'>
                                <#if security.hasEntityPermission("PAY_INFO", "_DELETE", session)>
                                    <div><a href='<@ofbizUrl>/deletePaymentMethod/viewprofile?paymentMethodId=${paymentMethod.paymentMethodId}</@ofbizUrl>' class="buttontext">
                                    [Expire]</a></div>
                                </#if>
                              </td>
                            </tr>
                        </#list>
                      </table>

                  </td>
                </tr>
              </table>
              <#else>
                <div class="tabletext">No payment method information on file.</div>
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
            <div class="boxhead">&nbsp;Cybersource AVS Override</div>
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
            <div class="tabletext"><b>AVS String:</b>&nbsp;${(avsOverride.avsDeclineString)?default("Global")}</div>
          </td>
          <#if security.hasEntityPermission("PARTYMGR", "_UPDATE", session)>
          <td align="right" valign="top" width="1%">
            <a href="<@ofbizUrl>/editAvsOverride</@ofbizUrl>" class="buttontext">[Edit]</a>
          </td>          
          <#if avsOverride?exists>
            <td align="right" valign="top" width="1%">
              <a href="<@ofbizUrl>/resetAvsOverride?partyId=${avsOverride.partyId}</@ofbizUrl>" class="buttontext">[Reset]</a>
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
            <div class="boxhead">&nbsp;User Name(s)</div>
          </td>
          <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
          <td valign="middle" align="right">&nbsp;
            <a href="<@ofbizUrl>/createnewlogin</@ofbizUrl>" class="lightbuttontext">[Create New]</a>&nbsp;&nbsp;
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
                <td align="right" valign="top" width="10%" nowrap><div class="tabletext"><b>User Name</b></div></td>
                <td width="5">&nbsp;</td>
                <td align="left" valign="top" width="40%"><div class="tabletext">${userUserLogin.userLoginId}</div></td>
                <td align="left" valign="top" width="30%">
                  <div class="tabletext">
                    <#assign enabled = "ENABLED">
                    <#if (userUserLogin.enabled)?default("Y") = "N">
                      <#assign enabled = "DISABLED - "+userUserLogin.disabledDateTime.toString()>
                    </#if>
                    ${enabled}
                  </div>
                </td>
                <td align="right" valign="top" width="20%">
                  <#if security.hasEntityPermission("PARTYMGR", "_CREATE", session)>
                      <a href="<@ofbizUrl>/editlogin?userlogin_id=${userUserLogin.userLoginId}</@ofbizUrl>" class="buttontext">[Edit]</a>&nbsp;
                  </#if>
                  <#if security.hasEntityPermission("SECURITY", "_VIEW", session)>
                      <a href="<@ofbizUrl>/EditUserLoginSecurityGroups?userLoginId=${userUserLogin.userLoginId}</@ofbizUrl>" class="buttontext">[SecurityGroups]</a>&nbsp;
                  </#if>
                </td>
              </tr>
              </#list>
            </table>
            <#else>
              <div class="tabletext">No UserLogin(s) found for this party.</div>
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
            <div class="boxhead">&nbsp;Last Visit(s)</div>
          </td>   
          <td valign="middle" align="right">&nbsp;
            <a href="<@ofbizUrl>/showvisits?party_id=${partyId}</@ofbizUrl>" class="lightbuttontext">[List All]</a>&nbsp;&nbsp;
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
                <td><div class="tableheadtext">VisitId</div></td>
                <td><div class="tableheadtext">UserLoginId</div></td>
                <td><div class="tableheadtext">New User</div></td>
                <td><div class="tableheadtext">WebApp</div></td>
                <td><div class="tableheadtext">Client IP</div></td>
                <td><div class="tableheadtext">From Date</div></td>
                <td><div class="tableheadtext">Thru Date</div></td>
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
              <div class="tabletext">No Visit(s) found for this party.</div>
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
            <div class="boxhead">&nbsp;Notes</div>
          </td>
          <#if security.hasEntityPermission("PARTYMGR", "_NOTE", session)>
          <td valign="middle" align="right">&nbsp;
            <a href="<@ofbizUrl>/createnewnote</@ofbizUrl>" class="lightbuttontext">[Create New]</a>&nbsp;&nbsp;
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
            <#if notes?exists>
            <table width="100%" border="0" cellpadding="1">
              <#list notes as noteRef>
                <tr>
                  <td align="left" valign="top" width="35%">
                    <div class="tabletext">&nbsp;<b>By: </b>${noteRef.firstName}&nbsp;${noteRef.lastName}</div>
                    <div class="tabletext">&nbsp;<b>At: </b>${noteRef.noteDateTime.toString()}</div>
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
              <div class="tabletext">No notes for this party.</div>
            </#if>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<#else>
    No party found with the partyId of: ${partyId?if_exists}
</#if>
<#else>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
</#if>
