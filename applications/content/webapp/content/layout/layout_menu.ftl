<#--
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
<a href="<@ofbizUrl>ListLayout</@ofbizUrl>" class="${selectedClassMap.ListLayout?default(unselectedClassName)}">${uiLabelMap.CommonList}</a>
<a href="<@ofbizUrl>FindLayout</@ofbizUrl>" class="${selectedClassMap.FindLayout?default(unselectedClassName)}">${uiLabelMap.CommonFind}</a>
<a href="<@ofbizUrl>EditLayout</@ofbizUrl>" class="${layoutClass}">${uiLabelMap.ContentLayout}</a>
<!--
<a href="<@ofbizUrl>EditLayoutContent</@ofbizUrl>" class="${selectedClassMap.EditLayoutContent?default(unselectedClassName)}">${uiLabelMap.ContentSubContent}</a>
<a href="<@ofbizUrl>EditLayoutText</@ofbizUrl>" class="${selectedClassMap.EditLayoutText?default(unselectedClassName)}">${uiLabelMap.ContentText}</a>
<a href="<@ofbizUrl>EditLayoutHtml</@ofbizUrl>" class="${selectedClassMap.EditLayoutHtml?default(unselectedClassName)}">${uiLabelMap.ContentHtml}</a>
<a href="<@ofbizUrl>EditLayoutImage</@ofbizUrl>" class="${selectedClassMap.EditLayoutImage?default(unselectedClassName)}">${uiLabelMap.ContentImage}</a>
<a href="<@ofbizUrl>EditLayoutUrl</@ofbizUrl>" class="${selectedClassMap.EditLayoutUrl?default(unselectedClassName)}">${uiLabelMap.ContentUrl}</a>
<a href="<@ofbizUrl>EditLayoutFile</@ofbizUrl>" class="${selectedClassMap.EditLayoutFile?default(unselectedClassName)}">${uiLabelMap.ContentFile}</a>
-->
</div>
