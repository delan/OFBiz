
<#macro renderAncestryPath trail siteId startIndex=0 endIndexOffset=0 buttonTitle="Back to" >
    <#local indent = "">
    <#local csv = "">
    <#local counter = 0>
    <#local len = trail?size>
    <table border="0" class="tabletext" cellspacing="4">
    <#list trail as content>
      <#if counter < (len - endIndexOffset) && startIndex <= counter >
        <#if 0 < counter >
            <#local csv = csv + ","/>
        </#if>
        <#local csv = csv + content.contentId/>
        <#if counter < len && startIndex <= counter >
       <tr>
         <td >
            ${indent}
            <#if content.contentTypeId == "WEB_SITE_PUB_PT" >
              <a class="tabButton" href="<@ofbizUrl>/showcontenttree?contentId=${content.contentId?if_exists}&nodeTrailCsv=${csv}</@ofbizUrl>" >Back to</a> &nbsp;${content.contentName?if_exists}
            <#else>
              <a class="tabButton" href="<@ofbizUrl>/showcontenttree?contentId=${siteId?if_exists}&nodeTrailCsv=${csv}</@ofbizUrl>" >Back to</a> &nbsp;${content.contentName?if_exists}
            </#if>
            <#local indent = indent + "&nbsp;&nbsp;&nbsp;&nbsp;">
            [${content.contentId?if_exists}]</td>
        </#if>
       </tr>
      </#if>
      <#local counter = counter + 1>
    <#if 20 < counter > <#break/></#if>
    </#list>
    </table>
</#macro>

