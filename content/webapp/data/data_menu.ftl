<#assign currentPage =  page.getPageName() >
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {currentPage : "tabButtonSelected"}>

<div class='tabContainer'>
<a href="<@ofbizUrl>/FindDataResource</@ofbizUrl>" class="${selectedClassMap.FindDataResource?default(unselectedClassName)}">Find</a>
<a href="<@ofbizUrl>/EditDataResource</@ofbizUrl>" class="${selectedClassMap.EditDataResource?default(unselectedClassName)}">DataResource</a>
</div>
