mapKey:${mapKey?if_exists}
<!--
<table border="2" bordercolor="blue">
<tr><th>SUCCESS</th></tr>
</table>
-->

<#assign subDataResourceId=drDataResourceId?if_exists/>
<#if !subDataResourceId?exists || (0 == subDataResourceId?length)>
    <#assign subDataResourceId=request.getAttribute("drDataResourceId")?if_exists/>
</#if>
<#assign subDataResourceTypeId=subDataResourceTypeId?if_exists/>
<#if !subDataResourceTypeId?exists || (0 == subDataResourceTypeId?length)>
    <#assign subDataResourceTypeId=request.getAttribute("subDataResourceTypeId")?if_exists/>
<br/>
</#if>
<!--
<div id="divTwo" style="border-color:red; border-width:thin; border-style:solid;">
<div id="divOne" style="border-color:red; border-width:thin; border-style:solid;">
-->
<div id="divTwo" class="wrapOuter">
<div id="divOne" class="wrapInner">
<@renderWrappedText />
</div>
<#assign contentIdTo=contentId?if_exists/>
<#assign mimeTypeId=mimeTypeId?if_exists/>
<#assign mapKey=mapKey?if_exists/>
<#assign subContentId=subContentId?if_exists/>
<#if !subContentId?exists || (0 == subContentId?length)>
    <#assign subContentId=request.getAttribute("subContentId")?if_exists/>
<br/>
</#if>
<a class="tabButton" href="javascript:lookupSubContent('<@ofbizUrl>/LookupSubContent</@ofbizUrl>', '${contentId?if_exists}','${mapKey?if_exists}',  '${subDataResourceTypeId?if_exists}', '${mimeTypeId?if_exists}') " > 
&nbsp;&nbsp;&nbsp;&nbsp;Lookup&nbsp;&nbsp;&nbsp;&nbsp;
</a>
&nbsp;
<#assign ofbizRequest=""/>
<#assign httpParams="contentIdTo=" + contentIdTo?if_exists + "&mapKey=" + mapKey?if_exists />
<#if subDataResourceTypeId == "ELECTRONIC_TEXT">
    <#if mimeTypeId == "text/html">
        <#assign ofbizRequest="EditLayoutHtml" />
    <#else>
        <#assign ofbizRequest="EditLayoutText" />
    </#if>
<#else>
    <#if subDataResourceTypeId == "IMAGE_OBJECT">
        <#assign ofbizRequest="EditLayoutImage" />
    <#else>
        <#if subDataResourceTypeId == "URL_RESOURCE">
            <#assign ofbizRequest="EditLayoutUrl" />
        </#if>
    </#if>
</#if>
<a class="tabButton" href="<@ofbizUrl>/${ofbizRequest}?${httpParams}&mode=add</@ofbizUrl>" >New</a>
<#if subContentId?exists && (0 < subContentId?length)>
&nbsp;
    <a class="tabButton" href="<@ofbizUrl>/${ofbizRequest}?${httpParams}&contentId=${subContentId}&drDataResourceId=${subDataResourceId?if_exists}</@ofbizUrl>" >Edit</a>
</#if>
</div>
