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
<@checkPermission mode="equals" entityOperation="_CREATE" targetOperation="HAS_USER_ROLE" >
    <a class="tabButton" href="<@ofbizUrl>/createforumresponse?contentIdTo=${requestParameters.contentId}&amp;nodeTrailCsv=${nodeTrailCsv?if_exists}</@ofbizUrl>" >Respond</a>
</@checkPermission>
<br/>

    </td>
    </tr>
</table>
<hr/>
<#--
<@checkPermission mode="not-equals" subContentId=subContentId targetOperation="CONTENT_CREATE|CONTENT_RESPOND" contentPurposeList="RESPONSE" >
            ${permissionErrorMsg?if_exists}
</@checkPermission>
-->

        <div class="head1">Responses:</div><br/>
<table border="0" width="100%" class="tableheadtext">
<@loopSubContentCache  contentAssocTypeId="RESPONSE" subContentId=subContentId mapKey=""
                pickWhen="contentAssocTypeId != null && \"RESPONSE\".equals(contentAssocTypeId) && mapKey == null"
                followWhen="contentAssocTypeId != null && \"_never_\".equals(contentAssocTypeId)"
>
  <tr>
    <#assign indentStr=indent?default("0")/>
    <#assign indent=indentStr?number/>
    <#if 1 < indent >
  <td class="tabletext">
        <#assign thisContentId = ""/>
        <#if nodeTrailCsv?exists>
            <#assign idList = nodeTrailCsv?split(",")/>
            <#if 0 < idList?size >
                <#assign thisContentId = idList?last>
            </#if>
        </#if>
        <#if content?exists>
        <a class="tabButton" href="<@ofbizUrl>/showforumresponse?contentId=${thisContentId}&amp;nodeTrailCsv=${nodeTrailCsv?if_exists}</@ofbizUrl>" >View</a>
[${thisContentId}]-${content.description?if_exists}
        </#if>

    </#if>
</@loopSubContentCache >
<#--
<@wrapSubContentCache subContentId=subContentId wrapTemplateId="WRAP_NEXT_PREV" >
</@wrapSubContentCache >
-->
</table>
</div>
</div>


