<#--
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
 *@author     David E. Jones
 *@created    May 22 2001
 *@version    1.0
-->

<#if party?exists>
<#-- Main Heading -->
<table width='100%' cellpadding='0' cellspacing='0' border='0'>
  <tr>
    <td align=left>
      <div class="head1">The Profile of
        <#if person?exists>
          ${person.personalTitle?if_exists}
          ${person.firstName?if_exists}
          ${person.middleName?if_exists}
          ${person.lastName?if_exists}
          ${person.suffix?if_exists}
        <#else>
          "New User"
        </#if>
      </div>
    </td>
    <td align=right>
      <#if showOld>
        <a href="<@ofbizUrl>/viewprofile</@ofbizUrl>" class="buttontext">[Hide Old]</a>&nbsp;&nbsp;
      <#else>
        <a href="<@ofbizUrl>/viewprofile?SHOW_OLD=true</@ofbizUrl>" class="buttontext">[Show Old]</a>&nbsp;&nbsp;
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
            <div class="boxhead">&nbsp;Personal Information</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/editperson</@ofbizUrl>" class="lightbuttontext">
            [<#if person?exists>Update<#else>Create</#if>]</a>&nbsp;&nbsp;
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
      <td align="right" width="10%"><div class="tabletext"><b>Name</b></div></td>
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
    <#if person.nickname?has_content><tr><td align=right nowrap><div class='tabletext'><b>Nickname</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.nickname}</div></td></tr></#if>
    <#if person.gender?has_content><tr><td align=right nowrap><div class='tabletext'><b>Gender</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.gender}</div></td></tr></#if>
    <#if person.birthDate?exists><tr><td align=right nowrap><div class='tabletext'><b>Birth Date</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.birthDate}</div></td></tr></#if>
    <#if person.height?exists><tr><td align=right nowrap><div class='tabletext'><b>Height</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.height}</div></td></tr></#if>
    <#if person.weight?exists><tr><td align=right nowrap><div class='tabletext'><b>Weight</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.weight}</div></td></tr></#if>
    <#if person.mothersMaidenName?has_content><tr><td align=right nowrap><div class='tabletext'><b>Mothers Maiden Name</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.mothersMaidenName}</div></td></tr></#if>
    <#if person.maritalStatus?has_content><tr><td align=right nowrap><div class='tabletext'><b>Marital Status</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.maritalStatus}</div></td></tr></#if>
    <#if person.socialSecurityNumber?has_content><tr><td align=right nowrap><div class='tabletext'><b>Social Security Number</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.socialSecurityNumber}</div></td></tr></#if>
    <#if person.passportNumber?has_content><tr><td align=right nowrap><div class='tabletext'><b>Passport Number</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.passportNumber}</div></td></tr></#if>
    <#if person.passportExpireDate?exists><tr><td align=right nowrap><div class='tabletext'><b>Passport Expire</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.passportExpireDate}</div></td></tr></#if>
    <#if person.totalYearsWorkExperience?exists><tr><td align=right nowrap><div class='tabletext'><b>Years Work</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.totalYearsWorkExperience}</div></td></tr></#if>
    <#if person.comments?has_content><tr><td align=right nowrap><div class='tabletext'><b>Comments</b></div></td><td>&nbsp;</td><td align=left><div class='tabletext'>${person.comments}</div></td></tr></#if>
  </table>
<#else>
<div class="tabletext">Personal Information Not Found</div>
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
          <td valign="middle" align="right">
              <a href="<@ofbizUrl>/editcontactmech</@ofbizUrl>" class="lightbuttontext">
              [Create New]</a>&nbsp;&nbsp;
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
        <th>Contact&nbsp;Type</th>
        <th width="5">&nbsp;</th>
        <th>Information</th>
        <th colspan='2'>Soliciting&nbsp;OK?</th>
        <th>&nbsp;</th>
      </tr>
      <#list partyContactMechValueMaps as partyContactMechValueMap>
        <#assign contactMech = partyContactMechValueMap.contactMech?if_exists>
        <#assign contactMechType = partyContactMechValueMap.contactMechType?if_exists>
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
                        <b>Purpose Type not found with ID: "${partyContactMechPurpose.contactMechPurposeTypeId}"</b>
                      </#if>
                      <#if partyContactMechPurpose.thruDate?exists>(Expire:${partyContactMechPurpose.thruDate})</#if>
                    </div>
              </#list>
              <#if contactMech.contactMechTypeId?exists = "POSTAL_ADDRESS">
                  <div class="tabletext">
                    <#if postalAddress.toName?has_content><b>To:</b> ${postalAddress.toName}<br></#if>
                    <#if postalAddress.attnName?has_content><b>Attn:</b> ${postalAddress.attnName}<br></#if>
                    ${postalAddress.address1}<br>
                    <#if postalAddress.address2?has_content>${postalAddress.address2}<br></#if>
                    ${postalAddress.city},
                    ${postalAddress.stateProvinceGeoId}
                    ${postalAddress.postalCode}
                    <#if postalAddress.countryGeoId?has_content><br>${postalAddress.countryGeoId}</#if>
                  </div>
                  <#if postalAddress != null && (UtilValidate.isEmpty(postalAddress.getString("countryGeoId")) || postalAddress.getString("countryGeoId").equals("USA"))>
                    <#assign addr1 =postalAddress.address1?if_exists>
                    <#if (addr1.indexOf(' ') > 0)>
                      <#assign addressNum = addr1.substring(0, addr1.indexOf(' '))>
                      <#assign addressOther = addr1.substring(addr1.indexOf(' ')+1)>
                      <a target='_blank' href='http://www.whitepages.com/find_person_results.pl?fid=a&s_n=${addressNum}&s_a=${addressOther}&c=${postalAddress.city?if_exists}&s=${postalAddress.stateProvinceGeoId?if_exists}&x=29&y=18' class='buttontext'>(lookup:whitepages.com)</a>
                    </#if>
                  </#if>
              <#elseif contactMech.contactMechTypeId?exists = "TELECOM_NUMBER">
                  <div class="tabletext">
                    ${telecomNumber.countryCode}
                    <#if telecomNumber.areaCode?has_content>${telecomNumber.areaCode}-</#if>${telecomNumber.contactNumber}
                    <#if partyContactMech.extension?has_content>ext&nbsp;${partyContactMech.extension}</#if>
                    <#if telecomNumber?exists && (!telecomNumber.countryCode?has_content || telecomNumber.countryCode = "011")>
                      <a target='_blank' href='http://www.anywho.com/qry/wp_rl?npa=${telecomNumber.areaCode?if_exists}&telephone=${telecomNumber.contactNumber?if_exists}&btnsubmit.x=20&btnsubmit.y=8' class='buttontext'>(lookup:anywho.com)</a>
                      <a target='_blank' href='http://whitepages.com/find_person_results.pl?fid=p&ac=${telecomNumber.areaCode?if_exists}&s=&p=${telecomNumber.contactNumber?if_exists}&pt=b&x=40&y=9' class='buttontext'>(lookup:whitepages.com)</a>
                    </#if>
                  </div>
              <#elseif contactMech.contactMechTypeId?exists = "EMAIL_ADDRESS">
                  <div class="tabletext">
                    ${contactMech.infoString}
                    <a href='mailto:${contactMech.infoString}' class='buttontext'>(send&nbsp;email)</a>
                  </div>
              <#elseif contactMech.contactMechTypeId?exists = "WEB_ADDRESS">
                  <div class="tabletext">
                    ${contactMech.infoString}
                    <#assign openAddress = contactMech.infoString?if_exists>
                    <#if !openAddress.startsWith("http") && !openAddress.startsWith("HTTP")><#assign openAddress = "http://" + openAddress></#if>
                    <a target='_blank' href='${openAddress}' class='buttontext'>(open&nbsp;page&nbsp;in&nbsp;new&nbsp;window)</a>
                  </div>
              <#else>
                  <div class="tabletext">${contactMech.infoString}</div>
              </#if>
              <div class="tabletext">(Updated:&nbsp;${partyContactMech.fromDate})</div>
              ${partyContactMech.thruDate", "<div class='tabletext'><b>Delete:&nbsp;", "</b></div>}
            </td>
            <td align="center" valign="top" nowrap width="1%"><div class="tabletext"><b>(${partyContactMech.allowSolicitation})</b></div></td>
            <td width="5">&nbsp;</td>
            <td align="right" valign="top" nowrap width="1%">
              <div><a href='<@ofbizUrl>/editcontactmech?contactMechId=${contactMech.contactMechId}</@ofbizUrl>' class="buttontext">
              [Update]</a>&nbsp;</div>
            </td>
            <td align="right" valign="top" width="1%">
              <div><a href='<@ofbizUrl>/deleteContactMech/viewprofile?contactMechId=${contactMech.contactMechId}</@ofbizUrl>' class="buttontext">
              [Expire]</a>&nbsp;&nbsp;</div>
            </td>
          </tr>
      </#list>
    </table>
  <#else>
    <p>No contact information on file.</p><br>
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
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/editcreditcard</@ofbizUrl>" class="lightbuttontext">
            [Create New Credit Card]</a>&nbsp;
            <a href="<@ofbizUrl>/editeftaccount</@ofbizUrl>" class="lightbuttontext">
            [Create New EFT Account]</a>&nbsp;&nbsp;
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
                            <#assign eftAccount = paymentMethodValueMap.eftAccount?if_exists>
                            <tr>
                              <#if paymentMethod.paymentMethodTypeId?if_exists = "CREDIT_CARD">
                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>
                                        Credit Card: ${creditCard.nameOnCard} - ${Static["org.ofbiz.commonapp.party.contact.ContactHelper"].formatCreditCard(creditCard)}
                                      </b>
                                      (Updated:&nbsp;${paymentMethod.fromDate})
                                      <#if paymentMethod.thruDate?exists><b>(Delete:&nbsp;${paymentMethod.thruDate})</b></#if>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <div><a href='<@ofbizUrl>/editcreditcard?paymentMethodId=<%entityField.run("paymentMethod", "paymentMethodId");%></@ofbizUrl>' class="buttontext">
                                    [Update]</a></div>
                                  </td>
                              <#elseif paymentMethod.paymentMethodTypeId?if_exists = "EFT_ACCOUNT">
                                  <td width="90%" valign="top">
                                    <div class="tabletext">
                                      <b>
                                        EFT Account: <%entityField.run("eftAccount", "nameOnAccount");%> - <%entityField.run("eftAccount", "bankName", "Bank: ", "");%> <%entityField.run("eftAccount", "accountNumber", "Account #: ", "");%>
                                      </b>
                                      (Updated:&nbsp;<%entityField.run("paymentMethod", "fromDate");%>)
                                      <%entityField.run("paymentMethod", "thruDate", "<b>(Delete:&nbsp;", ")</b>");%>
                                    </div>
                                  </td>
                                  <td width="5">&nbsp;</td>
                                  <td align="right" valign="top" width='1%' nowrap>
                                    <div><a href='<@ofbizUrl>/editeftaccount?paymentMethodId=<%entityField.run("paymentMethod", "paymentMethodId");%></@ofbizUrl>' class="buttontext">
                                    [Update]</a></div>
                                  </td>
                              </#if>
                              <td align="right" valign="top" width='1%'>
                                <div><a href='<@ofbizUrl>/deletePaymentMethod/viewprofile?paymentMethodId=<%entityField.run("paymentMethod", "paymentMethodId");%></@ofbizUrl>' class="buttontext">
                                [Expire]</a></div>
                              </td>
                            </tr>
                        </#list>
                      </table>
                    <#else>
                      <div class='tabletext'>No payment method information on file.</div>
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
            <div class="boxhead">&nbsp;User Name & Password</div>
          </td>
          <td valign="middle" align="right">
            <a href="<@ofbizUrl>/changepassword</@ofbizUrl>" class="lightbuttontext">[Change Password]</a>&nbsp;&nbsp;
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
                <td align="right" valign="top" width="10%" nowrap><div class="tabletext"><b>User Name</b></div></td>
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
<#else>
    <div class='head3'>No party found for current user with user name: ${userLogin.userLoginId}</div>
</#if>

