<#import "bloglib.ftl" as blog/>
<div class="boxoutside" >
<div style="margin:10px;" >
<@blog.renderAncestryPath trail=ancestorList?default([])/>

<table border="0" width="100%" class="blogtext">
    <tr>
    <td width="40">&nbsp;</td>
    <td>
    <@renderSubContentCache subContentId=requestParameters.contentId />
    </td>
    <td width="40" valign="bottom">
<#--
<@wrapSubContentCache subContentId=subContentId wrapTemplateId="WRAP_VIEW" >
</@wrapSubContentCache >
-->
<@checkPermission subContentId=subContentId targetOperation="CONTENT_CREATE|CONTENT_RESPOND" contentPurposeList="RESPONSE" >
<a class="tabButton" href="<@ofbizUrl>/createforumresponse?contentIdTo=${requestParameters.contentId}&amp;nodeTrailCsv=${context.nodeTrailCsv?if_exists}</@ofbizUrl>" >Respond</a>
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

        <div class="head1">Responses:</div><br/>
<table border="0" width="100%" class="tableheadtext">
<@loopSubContentCache  contentAssocTypeId="RESPONSE" subContentId=subContentId mapKey=""
                pickWhen="contentAssocTypeId != null && contentAssocTypeId.equals(\"RESPONSE\") && mapKey == null"
                followWhen="contentAssocTypeId != null && contentAssocTypeId.equals(\"_never_\")"
>
  <tr>
    <#assign indentStr=context.indent?default("0")/>
    <#assign indent=indentStr?number/>
    <#if 1 < indent >
  <td class="tabletext">
        <#assign thisContentId = ""/>
        <#if context.nodeTrailCsv?exists>
            <#assign idList = context.nodeTrailCsv?split(",")/>
            <#if 0 < idList?size >
                <#assign thisContentId = idList?last>
            </#if>
        </#if>
        <#if context.content?exists>
        <a class="tabButton" href="<@ofbizUrl>/showforumresponse?contentId=${thisContentId}&amp;nodeTrailCsv=${context.nodeTrailCsv?if_exists}</@ofbizUrl>" >View</a>
[${thisContentId}]-${context.content.description?if_exists}
        </#if>

    </#if>
</@loopSubContentCache >
<@wrapSubContentCache subContentId=subContentId wrapTemplateId="WRAP_NEXT_PREV" >
</@wrapSubContentCache >
</table>
</div>
</div>


