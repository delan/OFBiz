<#assign currentPage =  page.getPageName() >
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {currentPage : "tabButtonSelected"}>

<div class='tabContainer'>
<a href="<@ofbizUrl>/FindDataResource</@ofbizUrl>" class="${selectedClassMap.FindDataResource?default(unselectedClassName)}">Find</a>
<a href="<@ofbizUrl>/EditDataResource</@ofbizUrl>" class="${selectedClassMap.EditDataResource?default(unselectedClassName)}">DataResource</a>
<a href="<@ofbizUrl>/EditElectronicText</@ofbizUrl>" class="${selectedClassMap.EditElectronicText?default(unselectedClassName)}">ElectronicText</a>
<a href="<@ofbizUrl>/EditDataResourceAttribute</@ofbizUrl>" class="${selectedClassMap.EditDataResourceAttribute?default(unselectedClassName)}">Attribute</a>
<a href="<@ofbizUrl>/EditDataResourceRole</@ofbizUrl>" class="${selectedClassMap.EditDataResourceRole?default(unselectedClassName)}">Role</a>
</div>
