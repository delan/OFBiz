<#assign currentPage = "Edit" + page.entityName?default("ContentType") >
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {currentPage : "tabButtonSelected"}>

<div class='tabContainer'>
<a href="<@ofbizUrl>EditDataResourceType</@ofbizUrl>" class="${selectedClassMap.EditDataResourceType?default(unselectedClassName)}">Type</a>
<a href="<@ofbizUrl>EditCharacterSet</@ofbizUrl>" class="${selectedClassMap.EditCharacterSet?default(unselectedClassName)}">CharacterSet</a>
<a href="<@ofbizUrl>EditDataCategory</@ofbizUrl>" class="${selectedClassMap.EditDataCategory?default(unselectedClassName)}">Category</a>
<a href="<@ofbizUrl>EditDataResourceTypeAttr</@ofbizUrl>" class="${selectedClassMap.EditDataResourceTypeAttr?default(unselectedClassName)}">TypeAttr</a>
<a href="<@ofbizUrl>EditFileExtension</@ofbizUrl>" class="${selectedClassMap.EditFileExtension?default(unselectedClassName)}">File Ext</a>
<a href="<@ofbizUrl>EditMetaDataPredicate</@ofbizUrl>" class="${selectedClassMap.EditMetaDataPredicate?default(unselectedClassName)}">MetaData Pred</a>
<a href="<@ofbizUrl>EditMimeType</@ofbizUrl>" class="${selectedClassMap.EditMimeType?default(unselectedClassName)}">Mime Type</a>
</div>
