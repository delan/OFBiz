
<#macro renderBlog contentId="" targetPurpose="" stdWrapId="">
<#assign contentIdx = contentId?if_exists />
<#if (!contentIdx?exists || contentIdx?length == 0)>
    <#assign contentIdx = page.contentIdx?if_exists />
    <#if (!contentIdx?exists || contentIdx?length == 0)>
    </#if>
</#if>
<#assign viewIdx = "" />
<#if requestParameters.viewIndex?has_content>
<#assign viewIdx = requestParameters.viewIndex?if_exists />
</#if>
<#assign viewSz = "" />
<#if requestParameters.viewSize?has_content>
<#assign viewSz = requestParameters.viewSize?if_exists />
</#if>

<table width="100%" border="0" >
<@loopSubContentCache subContentId=contentIdx 
    viewIndex=viewIdx
    viewSize=viewSz
    contentAssocTypeId="PUBLISH_LINK"
    pickWhen="purposes.contains(\"ARTICLE\") && content.get(\"statusId\").equals(\"BLOG_PUBLISHED\")"
    returnAfterPickWhen="purposes.contains(\"ARTICLE\")"
    followWhen="contentAssocTypeId != null && contentAssocTypeId.equals(\"never follow\")"
>
  <#assign thisNodeTrailCsv=context.nodeTrailCsv?if_exists/>
  <#assign thisSubContentId=context.subContentId?if_exists/>
  <#assign thisNode=context.globalNodeTrail?last/>
  <#if thisNode?has_content>
  <#assign thisOwnerContentId=thisNode.value.ownerContentId?if_exists/>
  </#if>
  
  <#assign userLoginId=""/>
  <#if context.content?has_content && context.content.createdByUserLogin?has_content>
      <#assign userLoginId=context.content.createdByUserLogin/>
  </#if>
  <#assign authorName=Static["org.ofbiz.content.ContentManagementWorker"].getUserName(request,userLoginId?if_exists)/>

  <tr>
    <td width="40px">&nbsp;</td>
    <td class="blogtext" >
      <div class="tabletext">
        by:<#if authorName?has_content>${authorName?if_exists}
        <#else>
        <#if context.content?has_content>${context.content.createdByUserLogin?if_exists}</#if>
        </#if>
  &nbsp;
        <#if thisNode?exists && thisNode.fromDate?exists>
          <#assign nowTime = thisNode.fromDate?string />
          <#assign shortTime = ""/>
          <#if nowTime?has_content>
              <#assign lastColon=nowTime?last_index_of(":") - 1/>
              <#assign shortTime=nowTime[0..lastColon]/>
          </#if>
          ${shortTime?if_exists}
        </#if>
      </div>
    </td>
    <td class="tabletext" >
        <#if context.content?has_content>${context.content.contentName?if_exists}</#if>
        --
        <#if context.content?has_content>${context.content.description?if_exists}</#if>
    </td>
    <td width="40px" valign="bottom">
<a class="tabButton" href="<@ofbizUrl>/showforumarticle?contentId=${thisSubContentId}&nodeTrailCsv=${thisNodeTrailCsv?if_exists}&forumId=${contentIdx?if_exists}</@ofbizUrl>" >View</a>
    </td>
<@checkPermission mode="equals" entityOperation="_UPDATE" subContentId=context.content.contentId targetOperation="CONTENT_UPDATE" contentPurposeList="ARTICLE">
    <td width="40px" valign="bottom">
<a class="tabButton" style="height:14pt;" href="<@ofbizUrl>/editforumarticle?contentIdTo=${context.content.contentId}&nodeTrailCsv=${contentIdx?if_exists},${context.content.contentId}</@ofbizUrl>" >Edit</a>
    </td>
</@checkPermission>
  </tr>

</@loopSubContentCache>
<@wrapSubContentCache subContentId=contentIdx wrapTemplateId=stdWrapId contentPurposeList="ARTICLE">
</@wrapSubContentCache>
</table>
<table width="100%" border="0" class="summary">
<tr><td align="right">
<@checkPermission mode="equals" entityOperation="_CREATE" subContentId=contentDept statusId="BLOG_PUBLISHED" targetOperation="CONTENT_CREATE" contentPurposeList="ARTICLE" quickCheckContentId=contentIdx>
<a class="tabButton" style="height:14pt;" href="<@ofbizUrl>/createforumarticle?forumId=${contentIdx?if_exists}&nodeTrailCsv=${contentIdx?if_exists}</@ofbizUrl>" >New Article</a>
</@checkPermission>
</td></tr>
</table>
<#--
<@checkPermission mode="not-equals" entityOperation="_CREATE" subContentId=contentIdx statusId="BLOG_PUBLISHED" targetOperation="CONTENT_CREATE" contentPurposeList="ARTICLE">
            ${context.permissionErrorMsg?if_exists}
</@checkPermission>
-->

</#macro>

<#macro renderAncestryPath trail startIndex=0 endIndexOffset=0 buttonTitle="Back to">
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
              <a class="tabButton" href="<@ofbizUrl>/showforum?forumId=${content.contentId?if_exists}&nodeTrailCsv=${csv}</@ofbizUrl>" >Back to</a> &nbsp;${content.contentName?if_exists}
            <#else>
              <a class="tabButton" href="<@ofbizUrl>/showforumarticle?contentId=${content.contentId?if_exists}&nodeTrailCsv=${csv}</@ofbizUrl>" >Back to</a> &nbsp;${content.contentName?if_exists}
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

<#macro nextPrev listSize requestURL queryString lowIndex=0 highIndex=10 viewSize=10 viewIndex=0 >

<#if queryString?has_content>
    <#assign queryString = Static["org.ofbiz.content.ContentManagementWorker"].stripViewParamsFromQueryString(queryString) />
</#if>

<#assign lowIndexShow = lowIndex + 1 />
<#if highIndex < lowIndexShow >
  <#assign lowIndexShow = highIndex/>
</#if>
<table width="100%" border="0" >
<tr><td>
             <#if 0 < listSize?number>
                <#if 0 < viewIndex?number>
                  <a href="${requestURL}?${queryString}&viewSize=${viewSize}&viewIndex=${viewIndex?number-1}" class="submenutext">Previous</a>
                <#else>
                  <span class="submenutextdisabled">Previous</span>
                </#if>
                <#if 0 < listSize>
                  <span class="submenutextinfo">${lowIndexShow} - ${highIndex?if_exists} of ${listSize?if_exists}</span>
                </#if>
                <#if highIndex?if_exists?number < listSize?if_exists?number>
                  <a href="${requestURL}?${queryString?if_exists}&viewSize=${viewSize?if_exists}&viewIndex=${viewIndex?if_exists?number+1}" class="submenutextright">Next</a>
                <#else>
                  <span class="submenutextrightdisabled">Next</span>
                </#if>
              </#if>
</td></tr>
    </table>
</#macro>
