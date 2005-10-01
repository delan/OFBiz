<#assign currentPage =  page.getPageName() >
<#assign unselectedClassName = "tabButton">
<#assign selectedClassMap = {currentPage : "tabButtonSelected"}>
<#assign layoutClass="tabButton"/>
<#assign layoutUri="EditLayout"/>
<#if currentPK?exists >
    <#assign valSeq = currentPK.values() />
    <#list valSeq as val>
        <#if !val?exists >
             <#assign layoutUri="AddLayout"/>
             <#break/>
        </#if>
    </#list>
</#if>
<#if currentPage == layoutUri>
    <#assign layoutClass="tabButtonSelected"/>
</#if>


<div class='tabContainer'>
<a href="<@ofbizUrl>ListLayout</@ofbizUrl>" class="${selectedClassMap.ListLayout?default(unselectedClassName)}">List</a>
<a href="<@ofbizUrl>FindLayout</@ofbizUrl>" class="${selectedClassMap.FindLayout?default(unselectedClassName)}">Find</a>
<a href="<@ofbizUrl>EditLayout</@ofbizUrl>" class="${layoutClass}">Layout</a>
<!--
<a href="<@ofbizUrl>EditLayoutContent</@ofbizUrl>" class="${selectedClassMap.EditLayoutContent?default(unselectedClassName)}">SubContent</a>
<a href="<@ofbizUrl>EditLayoutText</@ofbizUrl>" class="${selectedClassMap.EditLayoutText?default(unselectedClassName)}">Text</a>
<a href="<@ofbizUrl>EditLayoutHtml</@ofbizUrl>" class="${selectedClassMap.EditLayoutHtml?default(unselectedClassName)}">Html</a>
<a href="<@ofbizUrl>EditLayoutImage</@ofbizUrl>" class="${selectedClassMap.EditLayoutImage?default(unselectedClassName)}">Image</a>
<a href="<@ofbizUrl>EditLayoutUrl</@ofbizUrl>" class="${selectedClassMap.EditLayoutUrl?default(unselectedClassName)}">Url</a>
<a href="<@ofbizUrl>EditLayoutFile</@ofbizUrl>" class="${selectedClassMap.EditLayoutFile?default(unselectedClassName)}">File</a>
-->
</div>
