<#assign unselectedLeftClassName = "tabButton">
<#assign selectedLeftClassName = "tabButtonSelected">
<#assign currentPage = "Edit" + page.entityName?default("DataResourceType") >

<#if currentPage = "EditDataResourceType">
<#assign EditDataResourceType = selectedLeftClassName >
<#else>
<#assign EditDataResourceType = unselectedLeftClassName >
</#if>

<#if currentPage = "EditDataResourceTypeAttr">
<#assign EditDataResourceTypeAttr = selectedLeftClassName >
<#else>
<#assign EditDataResourceTypeAttr = unselectedLeftClassName >
</#if>

<#if currentPage = "EditDataCategory">
<#assign EditDataCategory = selectedLeftClassName >
<#else>
<#assign EditDataCategory = unselectedLeftClassName >
</#if>

<#if currentPage = "EditMetaDataPredicate">
<#assign EditMetaDataPredicate = selectedLeftClassName >
<#else>
<#assign EditMetaDataPredicate = unselectedLeftClassName >
</#if>

<#if currentPage = "EditFileExtension">
<#assign EditFileExtension = selectedLeftClassName >
<#else>
<#assign EditFileExtension = unselectedLeftClassName >
</#if>

<#if currentPage = "EditMimeType">
<#assign EditMimeType = selectedLeftClassName >
<#else>
<#assign EditMimeType = unselectedLeftClassName >
</#if>

<#if currentPage = "EditCharacterSet">
<#assign EditCharacterSet = selectedLeftClassName >
<#else>
<#assign EditCharacterSet = unselectedLeftClassName >
</#if>

<#assign selectedLeftClassMap = { "EditDataResourceType":EditDataResourceType,
	"EditDataResourceTypeAttr":EditDataResourceTypeAttr,
	"EditDataCategory":EditDataCategory,
	"EditFileExtension":EditFileExtension,
	"EditMetaDataPredicate":EditMetaDataPredicate,
	"EditCharacterSet":EditCharacterSet,
	"EditMimeType":EditMimeType }>

  <div class='tabContainer'>
  <a href="<@ofbizUrl>/EditDataResourceType</@ofbizUrl>" class="${selectedLeftClassMap.EditDataResourceType?default(unselectedLeftClassName)}">Type</a>
  <a href="<@ofbizUrl>/EditCharacterSet</@ofbizUrl>" class="${selectedLeftClassMap.EditCharacterSet?default(unselectedLeftClassName)}">CharacterSet</a>
  <a href="<@ofbizUrl>/EditDataCategory</@ofbizUrl>" class="${selectedLeftClassMap.EditDataCategory?default(unselectedLeftClassName)}">Category</a>
  <a href="<@ofbizUrl>/EditDataResourceTypeAttr</@ofbizUrl>" class="${selectedLeftClassMap.EditDataResourceTypeAttr?default(unselectedLeftClassName)}">TypeAttr</a>
  <a href="<@ofbizUrl>/EditFileExtension</@ofbizUrl>" class="${selectedLeftClassMap.EditFileExtension?default(unselectedLeftClassName)}">File Ext</a>
  <a href="<@ofbizUrl>/EditMetaDataPredicate</@ofbizUrl>" class="${selectedLeftClassMap.EditMetaDataPredicate?default(unselectedLeftClassName)}">MetaData Pred</a>
  <a href="<@ofbizUrl>/EditMimeType</@ofbizUrl>" class="${selectedLeftClassMap.EditMimeType?default(unselectedLeftClassName)}">Mime Type</a>
  </div>
