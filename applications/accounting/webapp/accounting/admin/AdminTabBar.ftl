<#--
$Id: $

Copyright 2001-2006 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
-->
<#if (requestAttributes.uiLabelMap)?exists><#assign uiLabelMap = requestAttributes.uiLabelMap></#if>
<#if (parameters.organizationPartyId)?exists><#assign organizationPartyId = parameters.organizationPartyId></#if>
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {page.tabButtonItem?default("void") : "tabButtonSelected"}>
<div class="head1">${title?if_exists} ${labelTitleProperty?if_exists} for organization: ${organizationPartyId}</div>
<div class="tabContainer">
	<a href="<@ofbizUrl>TimePeriods?organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="${selectedClassMap.TimePeriods?default(unselectedClassName)}">Time Periods</a>
	<a href="<@ofbizUrl>PartyAcctgPreference?organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="${selectedClassMap.PartyAcctgPreference?default(unselectedClassName)}">Preferences</a>
	<a href="<@ofbizUrl>listChecksToPrint?organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="${selectedClassMap.ChecksTabButton?default(unselectedClassName)}">Checks</a>
	<a href="<@ofbizUrl>viewFXConversions?organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="${selectedClassMap.ViewFXConversions?default(unselectedClassName)}">Exchange Rates</a>
	<a href="<@ofbizUrl>EditGlJournalEntry?organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="${selectedClassMap.EditGlJournalEntry?default(unselectedClassName)}">Manual Journal Entry</a>
	<a href="<@ofbizUrl>ListUnpostedAcctgTrans?organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="${selectedClassMap.ListUnpostedAcctgTrans?default(unselectedClassName)}">Manual Transaction Posting</a>
	<a href="<@ofbizUrl>GlAccountAssignment?organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="${selectedClassMap.GlAccountAssignment?default(unselectedClassName)}">GL Account defaults</a>
</div>
<#if (page.tabButtonItem)?exists && page.tabButtonItem == "GlAccountAssignment">
	<div>
	<a href="<@ofbizUrl>GlAccountSalInvoice?organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="buttontext">[Sales Invoice]</a>
	<a href="<@ofbizUrl>GlAccountPurInvoice?organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="buttontext">[Purchase Invoice]</a>
	<a href="<@ofbizUrl>GlAccountTypePaymentType?organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="buttontext">[Payment Type/GlAccnt Type]</a>
	<a href="<@ofbizUrl>GlAccountNrPaymentMethod?organizationPartyId=${organizationPartyId}</@ofbizUrl>" class="buttontext">[Payment Method/Gl Accnt. Nr.]</a>
	</div>
	<br/>
</#if>
