<#include "bloglib.ftl" />
<#assign siteId = requestParameters.contentId?if_exists />
<@renderAncestryPath trail=ancestorList?default([]) endIndexOffset=1 siteId=siteId searchOn="true"/>
 
<#if ancestorList?has_content && (0 < ancestorList?size) >
    <#assign lastContent=ancestorList?last />
    <div class="head1">[${lastContent.contentId}] ${lastContent.description}
              <a class="tabButton" href="<@ofbizUrl>/searchContent?siteId=${lastContent.contentId?if_exists}</@ofbizUrl>" >Search</a> 
    </div>
</#if>
<table width="100%" border="0" >
<#assign viewIdx = "" />
<#if requestParameters.viewIndex?has_content>
<#assign viewIdx = requestParameters.viewIndex?if_exists />
</#if>
<#assign viewSz = "" />
<#if requestParameters.viewSize?has_content>
<#assign viewSz = requestParameters.viewSize?if_exists />
</#if>
<#assign nodeTrailCsv=requestParameters.nodeTrailCsv?if_exists/>
<#assign dummy=Static["org.ofbiz.base.util.Debug"].logInfo("in showcontenttree, nodeTrailCsv(0):" + nodeTrailCsv, "")/>
<#assign nodeTrail=[]/>
<#assign firstContentId=""/>
<#if nodeTrailCsv?has_content>
  <#assign nodeTrail=Static["org.ofbiz.base.util.StringUtil"].split(nodeTrailCsv, ",") />
  <#if 0 < nodeTrail?size>
    <#assign firstContentId=nodeTrail[0]?string/>
  </#if>
</#if>

<#--
<#assign dummy=Static["org.ofbiz.base.util.Debug"].logInfo("in showcontenttree, siteId:" + siteId, "")/>
<#assign dummy=Static["org.ofbiz.base.util.Debug"].logInfo("in showcontenttree, nodeTrail:" + nodeTrail, "")/>
-->
         <@renderCategoryBrowse contentId=siteId indentIndex=1 nodeTrail=nodeTrail />

</table>




<#macro renderCategoryBrowse contentId="" indentIndex=0 nodeTrail=[] viewSz=9999 viewIdx=0>

<#local contentIdx = contentId?if_exists />
<#if (!contentIdx?exists || contentIdx?length == 0)>
    <#local contentIdx = page.contentIdx?if_exists />
    <#if (!contentIdx?exists || contentIdx?length == 0)>
    </#if>
</#if>

<#local thisContentId=nodeTrail[indentIndex]?if_exists/>

<#local indent = "">
<#local thisNodeTrailCsv = "" />
<#local listUpper = (indentIndex - 1) />
<#if nodeTrail?size < listUpper >
    <#local listUpper = (nodeTrail?size - 1)>
</#if>
<#list 0..listUpper as idx>
    <#local indent = indent + "&nbsp;&nbsp;&nbsp;&nbsp;">
    <#if thisNodeTrailCsv?has_content>
        <#local thisNodeTrailCsv = thisNodeTrailCsv + ","/>
    </#if>
    <#local thisNodeTrailCsv = thisNodeTrailCsv + nodeTrail[idx]>
</#list>
<#--
<#assign dummy=Static["org.ofbiz.base.util.Debug"].logInfo("in showcontenttree, contentIdx:" + contentIdx, "")/>
-->
<!-- Look for content first -->
<@loopSubContentCache subContentId=contentIdx 
    viewIndex=viewIdx
    viewSize=viewSz
    contentAssocTypeId="PUBLISH_LINK"
    returnAfterPickWhen="1==1";
>
<#assign dummy=Static["org.ofbiz.base.util.Debug"].logInfo("in showcontenttree, nodeTrailCsv(1):" + nodeTrailCsv, "")/>
       <#local thisCsv=thisNodeTrailCsv + "," + subContentId />
       <tr>
         <td >
            ${indent}
            <a class="tabButton" href="<@ofbizUrl>/viewcontent?contentId=${subContentId?if_exists}&nodeTrailCsv=${thisCsv}</@ofbizUrl>" >View</a>  ${content.description?if_exists}
         </td >
       </tr>
</@loopSubContentCache >


<!-- Look for sub-topics -->
<@loopSubContentCache subContentId=contentIdx 
    viewIndex=viewIdx
    viewSize=viewSz
    contentAssocTypeId="SUBSITE"
    returnAfterPickWhen="1==1";
>

       <tr>
         <td >
            ${indent}
            <#local plusMinus="+"/>
            <#if thisContentId == subContentId>
                <#local plusMinus="-"/>
            </#if>
            <#local thisCsv=thisNodeTrailCsv />
            <#local thisCsv=thisNodeTrailCsv + "," + subContentId />
            <a class="tabButton" href="<@ofbizUrl>/showcontenttree?contentId=${siteId?if_exists}&nodeTrailCsv=${thisCsv}</@ofbizUrl>" >${plusMinus}</a> &nbsp;${content.description?if_exists}
              <a class="tabButton" href="<@ofbizUrl>/searchContent?siteId=${subContentId?if_exists}&nodeTrailCsv=${thisCsv}</@ofbizUrl>" >Search</a> 
         </td >
       </tr>
       <#if thisContentId == subContentId>
         <#assign catTrail = nodeTrail + [subContentId]/>
         <@renderCategoryBrowse contentId=subContentId indentIndex=indentIndex + 1  nodeTrail=catTrail viewSz=viewSz viewIdx=viewIdx />
       </#if>
</@loopSubContentCache >
</#macro>

