<#import "bloglib.ftl" as blog/>
<div class="boxoutside" >
<div class="head1">&nbsp;&nbsp;&nbsp;&nbsp;From Site:</div><br/>
<div style="margin:10px;" >
<@blog.renderAncestryPath trail=ancestorList?default([]) endIndexOffset=0 />
<#if trailList?exists && 1 < trailList?size >
<div class="head1">&nbsp;&nbsp;From Parent Article:</div><br/>
</#if>

<#assign thisContentId=subContentId?if_exists>
<#if !thisContentId?has_content>
    <#assign thisContentId=contentId?if_exists>
</#if>
<table border="0" width="100%" class="blogtext">
    <tr>
    <td width="40">&nbsp;</td>
    <td>
    <@renderSubContentCache subContentId=thisContentId />
    </td>
    <td width="40" valign="bottom">
<@checkPermission subContentId=subContentId targetOperation="HAS_USER_ROLE" contentPurposeList="RESPONSE" >
<a class="tabButton" href="<@ofbizUrl>/createforumresponse?contentIdTo=${subContentId}&amp;nodeTrailCsv=${context.nodeTrailCsv?if_exists}</@ofbizUrl>" >Respond</a>
</@checkPermission>
<br/>

    </td>
    </tr>
</table>
<hr/>
<#--
<@checkPermission mode="not-equals" subContentId=subContentId targetOperation="CONTENT_CREATE|CONTENT_RESPOND" contentPurposeList="RESPONSE" >
            ${context.permissionErrorMsg?if_exists}
</@checkPermission>
-->

<table border="0" width="100%" class="tableheadtext">
<!-- Note that the "...When" arguments in the loopSubContentCache must be compatible with those in
     any embedded transformSubContent, because it will assume that the first node has already
     had its conditions checked.
     It is not convenient to have the traverseSubContent check or recheck the first node
     because the associated ContentAssoc entity is not known.
-->
        <div class="head1">Responses:</div><br/>
<@loopSubContentCache  contentAssocTypeId="RESPONSE" subContentId=subContentId mapKey=""
                pickWhen="contentAssocTypeId != null && contentAssocTypeId.equals(\"RESPONSE\") && mapKey == null"
                followWhen="contentAssocTypeId != null && contentAssocTypeId.equals(\"RESPONSE\")"
>
    <@traverseSubContentCache  contentAssocTypeId="RESPONSE" 
                            pickWhen="contentAssocTypeId != null && contentAssocTypeId.equals(\"RESPONSE\")"
                            followWhen="contentAssocTypeId != null && contentAssocTypeId.equals(\"RESPONSE\")"
                            wrapTemplateId=""
                        >
    <#assign indentStr=context.indent?default("0")/>
    <#assign indent=indentStr?number/>
    <#if 1 < indent >
        <#assign fillRange=1..indent/>
        <#assign indentFill=""/>
        <#list fillRange as i>
            <#assign indentFill = indentFill + "&nbsp;&nbsp;&nbsp;&nbsp;" />
        </#list>
        <#assign thisContentId = ""/>
        <#if context.nodeTrailCsv?exists>
            <#assign idList = context.nodeTrailCsv?split(",")/>
            <#if 0 < idList?size >
                <#assign thisContentId = idList?last>
            </#if>
        </#if>
        <#if context.content?exists>
  <tr>
  <td class="tabletext">
        ${indentFill}
        <a class="tabButton" href="<@ofbizUrl>/ViewBlog?contentId=${thisContentId}&amp;nodeTrailCsv=${context.nodeTrailCsv?if_exists}</@ofbizUrl>" >View</a>
                     ${context.content.contentId?if_exists}-${context.content.description?if_exists}<br/>
  </td>
  </tr>
        </#if>
    </#if>
     </@traverseSubContentCache >
</@loopSubContentCache >
<#--
<@wrapSubContentCache subContentId=subContentId wrapTemplateId="WRAP_NEXT_PREV" >
</@wrapSubContentCache >
-->
</table>
</div>
</div>


<#-- not used, will be deleted -->
<#macro getCurrentContent >
    <#assign globalNodeTrail=context.globalNodeTrail/>
    <#if globalNodeTrail?exists>
        <#assign currentNode=globalNodeTrail?last/>
        <#if currentNode?exists>
            <#assign currentValue=currentNode.value/>
            <#if currentValue?exists>
                <@wrapSubContentCache subContentId=currentValue.contentId wrapTemplateId="WRAP_ARTICLE" >
                    <@traverseSubContentCache  contentAssocTypeId="SUB_CONTENT" 
                            pickWhen="mapKey != null && mapKey.equals(\"ARTICLE\")"
                            returnAfterPickWhen="mapKey != null && mapKey.equals(\"ARTICLE\")"
                            followWhen="contentAssocTypeId != null && contentAssocTypeId.equals(\"_never_\")"
                            wrapTemplateId=""
                        >
                <#assign description=currentValue.description?default("No description")/>
description[${currentValue.contentId?if_exists}]:${description}
<a class="tabButton" href="<@ofbizUrl>/ViewBlog?contentId=${thisContentId}&amp;nodeTrailCsv=${context.nodeTrailCsv?if_exists}</@ofbizUrl>" >View</a>
                   </@traverseSubContentCache >
                </@wrapSubContentCache>
            </#if>
        </#if>
    </#if>
</#macro>

<#macro renderSiteAncestryPath trail startIndex=0 >
    <#assign indent = "">
    <#assign csv = "">
    <#assign counter = 0>
    <#assign len = trail?size>
    <table border="0" class="tabletext" cellspacing="4">
    <#list trail as webSitePublishPoint>
        <#if counter < len && startIndex <= counter >
       <tr>
         <td >
            ${indent}
            <a class="tabButton" href="<@ofbizUrl>/main?pubPt=${webSitePublishPoint.contentId?if_exists}</@ofbizUrl>" >Back to</a> &nbsp;${webSitePublishPoint.templateTitle?if_exists}
                <#assign indent = indent + "&nbsp;&nbsp;&nbsp;&nbsp;">
         [${webSitePublishPoint.contentId?if_exists}]</td>
        </#if>
       </tr>
        <#assign counter = counter + 1>
    <#if 20 < counter > <#break/></#if>
    </#list>
    </table>
</#macro>
