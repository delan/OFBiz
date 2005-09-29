<fo:block white-space-collapse="false">
<#-- company information goes here -->
<#-- <@ofbizContentUrl> does not work here by default: a browser understands what /images/... means but a FOP does not.  So, you have to use an explicit
URL for your content -->
<#if logoImageUrl?has_content><fo:external-graphic src="${logoImageUrl}" overflow="hidden" width="200pt"/></#if>
${companyName?if_exists}
<#if postalAddress?exists>
${postalAddress.address1?if_exists}
${postalAddress.address2?if_exists}
${postalAddress.city?if_exists}, ${postalAddress.stateProvinceGeoId?if_exists} ${postalAddress.postalCode?if_exists}
${postalAddress.countryGeoId?if_exists}
</#if>
</fo:block>
 