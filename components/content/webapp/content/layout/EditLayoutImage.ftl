${singleWrapper.renderFormString()}

<#if drDataResourceId?exists>
    <br/>
<#assign id=subContentId?string/>

<@renderSubContent subContentId=id />
<#--
    <img src="<@ofbizUrl>/img?imgId=${drDataResourceId}</@ofbizUrl>" />
-->
</#if>
<p/><div class="head1">Other Layouts that use this subcontent</div>
${listWrapper.renderFormString()}
