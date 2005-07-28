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
 * @author     Johan Isacsson
 * @author     David E. Jones
 * @author     Andy Zeneski
 * @created    May 26 2003
 *@author     Olivier Heintz (olivier.heintz@nereide.biz)
 * @version    1.0
 */
-->
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>

<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>

<#if security.hasEntityPermission("PARTYMGR", "_VIEW", session)>
<#if party?has_content>
<#-- Main Heading -->
<table width="100%" cellpadding="0" cellspacing="0" border="0">
  <tr>
    <td align="left">
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
       [${partyId}]
      </div>
    </td>
    <td align="right">
      <div class="tabContainer">
        <a href="<@ofbizUrl>/viewprofile?partyId=${party.partyId}</@ofbizUrl>" class="${selectedClassMap.viewprofile?default(unselectedClassName)}">${uiLabelMap.PartyProfile}</a>
        <a href="<@ofbizUrl>/viewroles?partyId=${party.partyId}</@ofbizUrl>" class="${selectedClassMap.viewroles?default(unselectedClassName)}">${uiLabelMap.PartyRoles}</a>
        <a href="<@ofbizUrl>/EditPartyRelationships?partyId=${party.partyId}</@ofbizUrl>" class="${selectedClassMap.EditPartyRelationships?default(unselectedClassName)}">${uiLabelMap.PartyRelationships}</a>
        <a href="<@ofbizUrl>/viewcommunications?partyId=${party.partyId}</@ofbizUrl>" class="${selectedClassMap.listCommunications?default(unselectedClassName)}">${uiLabelMap.PartyCommunications}</a>
        <a href="<@ofbizUrl>/viewvendor?partyId=${party.partyId}</@ofbizUrl>" class="${selectedClassMap.viewvendor?default(unselectedClassName)}">${uiLabelMap.PartyVendor}</a>
        <a href="<@ofbizUrl>/EditPartyTaxInfos?partyId=${party.partyId}</@ofbizUrl>" class="${selectedClassMap.EditPartyTaxInfos?default(unselectedClassName)}">${uiLabelMap.PartyTaxInfos}</a>
        <a href="<@ofbizUrl>/editShoppingList?partyId=${party.partyId}</@ofbizUrl>" class="${selectedClassMap.editShoppingList?default(unselectedClassName)}">${uiLabelMap.PartyShoppingLists}</a>
        <a href="<@ofbizUrl>/ViewSegmentRoles?partyId=${party.partyId}</@ofbizUrl>" class="${selectedClassMap.ViewPartySegmentRoles?default(unselectedClassName)}">${uiLabelMap.PartySegmentRoles}</a>
        <a href="<@ofbizUrl>/EditPartyClassifications?partyId=${party.partyId}</@ofbizUrl>" class="${selectedClassMap.EditPartyClassifications?default(unselectedClassName)}">${uiLabelMap.PartyClassifications}</a>
      </div>
    </td>
  </tr>
  <tr>
    <td colspan="2" align="right" nowrap>
      <a href="/accounting/control/FindBillingAccount?partyId=${partyId}${externalKeyParam}" class="buttontext">[${uiLabelMap.AccountingBillingAccount}]</a>
      <#if security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)>
        <a href="/ordermgr/control/findorders?lookupFlag=Y&hideFields=Y&partyId=${partyId}${externalKeyParam}" class="buttontext">[${uiLabelMap.OrderOrders}]</a>
      </#if>
      <#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
        <a href="/ordermgr/control/checkinits?partyId=${partyId}${externalKeyParam}" class="buttontext">[${uiLabelMap.OrderNewOrder}]</a>
      </#if>
      <#if security.hasEntityPermission("ACCOUNTING", "_VIEW", session)>
        <a href="/accounting/control/findPayment?lookupFlag=Y&hideFields=Y&toPartyId=${partyId}${externalKeyParam}>" class="buttontext">[${uiLabelMap.AccountingPaymentsSent}]</a>
        <a href="/accounting/control/findPayment?lookupFlag=Y&hideFields=Y&fromPartyId=${partyId}${externalKeyParam}>" class="buttontext">[${uiLabelMap.AccountingPaymentsReceived}]</a>
      </#if>
    </td>
  </tr>
</table>

<#else>
  <div class="head2">${uiLabelMap.PartyNoPartyFoundWithPartyId}: ${partyId?if_exists}</div>
</#if>
<#else>
  <div class="head2">${uiLabelMap.PartyMgrViewPermissionError}</div>
</#if>
