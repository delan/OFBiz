<#assign unselectedLeftClassName = "tabButton">
<#assign selectedLeftClassName = "tabButtonSelected">
<#assign ul = userLogin >
<#assign currentPage = "Edit" + page.entityName?default("ContentType") >

<#if currentPage = "EditContentType">
<#assign EditContentType = selectedLeftClassName >
<#else>
<#assign EditContentType = unselectedLeftClassName >
</#if>

<#if currentPage = "EditContentTypeAttr">
<#assign EditContentTypeAttr = selectedLeftClassName >
<#else>
<#assign EditContentTypeAttr = unselectedLeftClassName >
</#if>

<#if currentPage = "EditContentAssocType">
<#assign EditContentAssocType = selectedLeftClassName >
<#else>
<#assign EditContentAssocType = unselectedLeftClassName >
</#if>

<#if currentPage = "EditContentAssocPredicate">
<#assign EditContentAssocPredicate = selectedLeftClassName >
<#else>
<#assign EditContentAssocPredicate = unselectedLeftClassName >
</#if>

<#if currentPage = "EditContentRole">
<#assign EditContentRole = selectedLeftClassName >
<#else>
<#assign EditContentRole = unselectedLeftClassName >
</#if>

<#if currentPage = "EditContentPurposeType">
<#assign EditContentPurposeType = selectedLeftClassName >
<#else>
<#assign EditContentPurposeType = unselectedLeftClassName >
</#if>

<#assign selectedLeftClassMap = { "EditContentType":EditContentType,
	"EditContentTypeAttr":EditContentTypeAttr,
	"EditContentPurposeType":EditContentPurposeType,
	"EditContentAssocType":EditContentAssocType,
	"EditContentAssocPredicate":EditContentAssocPredicate,
	"EditContentRole":EditContentRole }>

  <div class='tabContainer'>
  <a href="<@ofbizUrl>/EditContentType</@ofbizUrl>" class="${selectedLeftClassMap.EditContentType?default(unselectedLeftClassName)}">Type</a>
  <a href="<@ofbizUrl>/EditContentAssocType</@ofbizUrl>" class="${selectedLeftClassMap.EditContentAssocType?default(unselectedLeftClassName)}">AssocType</a>
<!--
  <a href="<@ofbizUrl>/EditContentRole</@ofbizUrl>" class="${selectedLeftClassMap.EditContentRole?default(unselectedLeftClassName)}">Role</a>
-->
  <a href="<@ofbizUrl>/EditContentPurposeType</@ofbizUrl>" class="${selectedLeftClassMap.EditContentPurposeType?default(unselectedLeftClassName)}">PurposeType</a>
  <a href="<@ofbizUrl>/EditContentTypeAttr</@ofbizUrl>" class="${selectedLeftClassMap.EditContentTypeAttr?default(unselectedLeftClassName)}">TypeAttr</a>
  <a href="<@ofbizUrl>/EditContentAssocPredicate</@ofbizUrl>" class="${selectedLeftClassMap.EditContentAssocPredicate?default(unselectedLeftClassName)}">AssocPredicate</a>
  </div>
