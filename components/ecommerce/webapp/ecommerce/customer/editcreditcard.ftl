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
 *@version    $Revision: 1.6 $
 *@since      2.1
-->

<#assign uiLabelMap = requestAttributes.uiLabelMap>
<#if canNotView>
  <p><h3>${uiLabelMap.AccountingCardInfoNotBelongToYou}.</h3></p>
&nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonBack}]</a>
<#else>
    <#if !creditCard?exists>
      <p class="head1">${uiLabelMap.AccountingAddNewCreditCard}</p>
      &nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonGoBack}]</a>
      &nbsp;<a href="javascript:document.editcreditcardform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>
      <form method="post" action='<@ofbizUrl>/createCreditCard?DONE_PAGE=${donePage}</@ofbizUrl>' name="editcreditcardform" style='margin: 0;'>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <#else>
      <p class="head1">${uiLabelMap.AccountingEditCreditCard}</p>
      &nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonGoBack}]</a>
      &nbsp;<a href="javascript:document.editcreditcardform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>
      <form method="post" action='<@ofbizUrl>/updateCreditCard?DONE_PAGE=${donePage}</@ofbizUrl>' name="editcreditcardform" style='margin: 0;'>
      <table width="90%" border="0" cellpadding="2" cellspacing="0">
        <input type=hidden name='paymentMethodId' value='${paymentMethodId}'>
    </#if>

    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.AccountingNameOnCard}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class='inputBox' size="30" maxlength="60" name="nameOnCard" value="${creditCardData.nameOnCard?if_exists}">
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.AccountingCompanyNameOnCard}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class='inputBox' size="30" maxlength="60" name="companyNameOnCard" value="${creditCardData.companyNameOnCard?if_exists}">
      </td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.AccountingCardType}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <select name="cardType" class='selectBox'>
          <option>${creditCardData.cardType?if_exists}</option>
          <option></option>
          ${pages.get("/includes/cctypes.ftl")}
        </select>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.AccountingCardNumber}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <#if creditCardData?has_content>
          <#assign cardNumberDisplay = "">
          <#assign cardNumber = creditCardData.cardNumber?if_exists>
          <#if cardNumber?has_content>
            <#assign size = cardNumber?length - 4>
            <#list 0 .. size-1 as foo>
              <#assign cardNumberDisplay = cardNumberDisplay + "*">
            </#list>
            <#assign cardNumberDisplay = cardNumberDisplay + cardNumber[size .. size + 3]>
          </#if>
        </#if>
        <input type="text" class='inputBox' size="20" maxlength="30" name="cardNumber" onfocus="javascript:this.value = '';" value="${cardNumberDisplay?if_exists}">
      *</td>
    </tr>
    <#--<tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.AccountingCardSecurityCode}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <input type="text" class='inputBox' size="5" maxlength="10" name="cardSecurityCode" value="${creditCardData.cardSecurityCode?if_exists}">
      </td>
    </tr>-->
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.AccountingExpirationDate}</div></td>        
      <td width="5">&nbsp;</td>
      <td width="74%">
        <#assign expMonth = "">
        <#assign expYear = "">
        <#if creditCard?exists>
          <#assign expDate = creditCard.expireDate>
          <#if (expDate?exists && expDate.indexOf("/") > 0)>
            <#assign expMonth = expDate.substring(0,expDate.indexOf("/"))>
            <#assign expYear = expDate.substring(expDate.indexOf("/")+1)>
          </#if>
        </#if>
        <select name="expMonth" class='selectBox'>
          <option><#if tryEntity>${expMonth?if_exists}<#else>${requestParameters.expMonth?if_exists}</#if></option>
          ${pages.get("/includes/ccmonths.ftl")}
        </select>
        <select name="expYear" class='selectBox'>
          <option><#if tryEntity>${expYear?if_exists}<#else>${requestParameters.expYear?if_exists}</#if></option>
          ${pages.get("/includes/ccyears.ftl")}
        </select>
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right valign=top><div class="tabletext">${uiLabelMap.PartyBillingAddress}</div></td>
      <td width="5">&nbsp;</td>
      <td width="74%">
        <#-- Removed because is confusing, can add but would have to come back here with all data populated as before...
        <a href="<@ofbizUrl>/editcontactmech</@ofbizUrl>" class="buttontext">
          [Create New Address]</a>&nbsp;&nbsp;
        -->
        <table width="100%" border="0" cellpadding="1">
        <#assign hasCurrent = false>
        <#if curPostalAddress?has_content>
          <#assign hasCurrent = true>
          <tr>
            <td align="right" valign="top" width="1%">
              <input type="radio" name="contactMechId" value="${curContactMechId}" checked>
            </td>
            <td align="left" valign="top" width="80%">
              <div class="tabletext"><b>${uiLabelMap.PartyUseCurrentAddress}:</b></div>
              <#list curPartyContactMechPurposes as curPartyContactMechPurpose> 
                <#assign curContactMechPurposeType = curPartyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType")>
                <div class="tabletext">
                  <b>${curContactMechPurposeType.description?if_exists}</b>
                  <#if curPartyContactMechPurpose.thruDate?exists>
                    (Expire:${curPartyContactMechPurpose.thruDate.toString()})
                  </#if>
                </div>
              </#list>
              <div class="tabletext">
                <#if curPostalAddress.toName?exists><b>${uiLabelMap.CommonTo}:</b> ${curPostalAddress.toName}<br></#if>
                <#if curPostalAddress.attnName?exists><b>${uiLabelMap.PartyAddrAttnName}:</b> ${curPostalAddress.attnName}<br></#if>
                ${curPostalAddress.address1?if_exists}<br>
                <#if curPostalAddress.address2?exists>${curPostalAddress.address2}<br></#if>
                ${curPostalAddress.city}<#if curPostalAddress.stateProvinceGeoId?has_content>,&nbsp;${curPostalAddress.stateProvinceGeoId}</#if>&nbsp;${curPostalAddress.postalCode} 
                <#if curPostalAddress.countryGeoId?exists><br>${curPostalAddress.countryGeoId}</#if>
              </div>
              <div class="tabletext">(${uiLabelMap.CommonUpdated}:&nbsp;${(curPartyContactMech.fromDate.toString())?if_exists})</div>
              <#if curPartyContactMech.thruDate?exists><div class='tabletext'><b>${uiLabelMap.CommonDelete}:&nbsp;${curPartyContactMech.thruDate.toString()}</b></div></#if>
            </td>
          </tr>
        <#else>
           <#-- <tr>
            <td align="left" valign="top" colspan='2'>
              <div class="tabletext">${uiLabelMap.PartyBillingAddressNotSelected}</div>
            </td>
          </tr> -->
        </#if>
          <#-- is confusing
          <tr>
            <td align="left" valign="top" colspan='2'>
              <div class="tabletext"><b>Select a New Billing Address:</b></div>
            </td>
          </tr>
          -->
          <#list postalAddressInfos as postalAddressInfo>
            <#assign contactMech = postalAddressInfo.contactMech>
            <#assign partyContactMechPurposes = postalAddressInfo.partyContactMechPurposes>
            <#assign postalAddress = postalAddressInfo.postalAddress>
            <#assign partyContactMech = postalAddressInfo.partyContactMech>
            <tr>
              <td align="right" valign="top" width="1%">
                <input type=radio name='contactMechId' value='${contactMech.contactMechId}'>
              </td>
              <td align="left" valign="middle" width="80%">
                <#list partyContactMechPurposes as partyContactMechPurpose>
                    <#assign contactMechPurposeType = partyContactMechPurpose.getRelatedOneCache("ContactMechPurposeType")>
                    <div class="tabletext">
                      <b>${contactMechPurposeType.description?if_exists}</b>
                      <#if partyContactMechPurpose.thruDate?exists>(${uiLabelMap.CommonExpire}:${partyContactMechPurpose.thruDate})</#if>
                    </div>
                </#list>
                <div class="tabletext">
                  <#if postalAddress.toName?exists><b>${uiLabelMap.CommonTo}:</b> ${postalAddress.toName}<br></#if>
                  <#if postalAddress.attnName?exists><b>${uiLabelMap.PartyAddrAttnName}:</b> ${postalAddress.attnName}<br></#if>
                  ${postalAddress.address1?if_exists}<br>
                  <#if postalAddress.address2?exists>${postalAddress.address2}<br></#if>
                  ${postalAddress.city}<#if postalAddress.stateProvinceGeoId?has_content>,&nbsp;${postalAddress.stateProvinceGeoId}</#if>&nbsp;${postalAddress.postalCode} 
                  <#if postalAddress.countryGeoId?exists><br>${postalAddress.countryGeoId}</#if>
                </div>
                <div class="tabletext">(${uiLabelMap.CommonUpdated}:&nbsp;${(partyContactMech.fromDate.toString())?if_exists})</div>
                <#if partyContactMech.thruDate?exists><div class='tabletext'><b>${uiLabelMap.CommonDelete}:&nbsp;${partyContactMech.thruDate.toString()}</b></div></#if>
              </td>
            </tr>
          </#list>
          <#if !postalAddressInfos?has_content && !curContactMech?exists>
              <tr><td colspan='2'><div class="tabletext">${uiLabelMap.PartyNoContactInformation}.</div></td></tr>
          </#if>
          <tr>
            <td align="right" valigh="top" width="1%">
              <input type="radio" name="contactMechId" value="_NEW_" <#if !hasCurrent>checked</#if>>
            </td>
            <td align="left" valign="middle" width="80%">
              <span class="tabletext">${uiLabelMap.PartyCreateNewBillingAddress}.</span>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  </form>

  &nbsp;<a href='<@ofbizUrl>/authview/${donePage}</@ofbizUrl>' class="buttontext">[${uiLabelMap.CommonGoBack}]</a>
  &nbsp;<a href="javascript:document.editcreditcardform.submit()" class="buttontext">[${uiLabelMap.CommonSave}]</a>
</#if>

