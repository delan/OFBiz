<#include "bloglib.ftl" />
<div class="boxoutside" >
<div style="margin:10px;" >
<#if ancestorList?has_content && (0 < ancestorList?size) >
    <#assign lastContent=ancestorList?last />
    <#assign firstContent=ancestorList[0] />
</#if>
<@renderAncestryPath trail=ancestorList?default([]) endIndexOffset=1 siteId=firstContent.contentId/>

<#if lastContent?has_content>
    <div class="head1">[${lastContent.contentId}] ${lastContent.description}</div>
</#if>
<#-- Do this so that we don't have to find the content twice (again in renderSubContent) -->
<#assign subContentId=requestParameters.contentId?if_exists/>
<#assign nodeTrailCsv=requestParameters.nodeTrailCsv?if_exists/>
<#assign dummy=Static["org.ofbiz.base.util.Debug"].logInfo("in viewcontent, nodeTrailCsv:" + nodeTrailCsv, "")/>
<#--
<#assign globalNodeTrail=[]/>
<#assign firstContentId=""/>
<#if nodeTrailCsv?has_content>
  <#assign globalNodeTrail=Static["org.ofbiz.base.util.StringUtil"].split(nodeTrailCsv, ",") />
  <#if 0 < globalNodeTrail?size>
    <#assign firstContentId=globalNodeTrail[0]?string/>
  </#if>
</#if>
<#assign globalNodeTrail=requestParameters.globalNodeTrail?default([])/>
-->
<#if globalNodeTrail?has_content && (0 < globalNodeTrail?size) >
    <#assign lastNode = globalNodeTrail?last/>
    <#if lastNode?has_content>
      <#assign subContent=lastNode.value/>
    </#if>
<#else>
    <#assign subContent = delegator.findByPrimaryKeyCache("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", subContentId))/>
</#if>
<#assign dummy=Static["org.ofbiz.base.util.Debug"].logInfo("in viewcontent, subContent:" + subContent, "")/>
<br/>
<div class="head1">Content for [${subContentId}] ${subContent.contentName?if_exists} - ${subContent.description?if_exists}:</div><br/>
<table border="0" width="100%" class="blogtext">
    <tr>
    <td width="40">&nbsp;</td>
    <td>
    <@renderSubContentCache subContentId=subContentId />
    </td>
    <td width="40" valign="bottom">
<#--
<@wrapSubContentCache subContentId=subContentId wrapTemplateId="WRAP_VIEW" >
</@wrapSubContentCache >
<@checkPermission mode="equals" entityOperation="_CREATE" targetOperation="HAS_USER_ROLE" >
    <a class="tabButton" href="<@ofbizUrl>/createforumresponse?contentIdTo=${requestParameters.contentId}&amp;nodeTrailCsv=${nodeTrailCsv?if_exists}</@ofbizUrl>" >Respond</a>
</@checkPermission>
-->
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

</div>
</div>


