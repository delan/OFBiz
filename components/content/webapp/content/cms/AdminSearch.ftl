${menuWrapper.renderMenuString()}
<hr/>
    <form method="POST"  action="/content/control/AdminSearch"  name="searchQuery" style="margin: 0;">
<table border="0" cellpadding="2" cellspacing="0">

<tr>
<td width="20%" align="right">
<span class="tableheadtext">Enter query parameters</span>
</td>
<td>&nbsp;</td>
<td width="80%" align="left">
<input type="text" class="inputBox" name="queryLine" size="60"/>
</td>
</tr>
<tr>
<tr>
<td width="20%" align="right">
<span class="tableheadtext">Select category</span>
</td>
<td>&nbsp;</td>
<td width="80%" align="left">
<select name="lcSiteId">
  <option value=""></option>
  <@listSiteIds contentId="WebStoreCONTENT" indentIndex=0/>
</select>
</td>
</tr>
<tr>
<td width="20%" align="right">
&nbsp;</td>
<td>&nbsp;</td>
<td width="80%" align="left" colspan="4">
<input type="submit" class="standardSubmit" name="submitButton" value="Query"/>
</td>

</tr>
</table>
</form>


<hr/>
    ${listWrapper.renderFormString()}

<#macro listSiteIds contentId indentIndex=0>
  <#assign dummy=Static["org.ofbiz.base.util.Debug"].logInfo("in listSiteIds, contentId:" + contentId,"")/>
  <#assign dummy=Static["org.ofbiz.base.util.Debug"].logInfo("in listSiteIds, indentIndex:" + indentIndex,"")/>
  <#local indent = ""/>
  <#if 0 < indentIndex >
    <#list 0..(indentIndex - 1) as idx>
      <#local indent = indent + "&nbsp;&nbsp;"/>
    </#list>
  </#if>
<@loopSubContentCache subContentId=contentId
    viewIndex=0
    viewSize=9999
    contentAssocTypeId="SUBSITE"
    returnAfterPickWhen="1==1";
>
  <option value="${content.contentId?lower_case}">${indent}${content.description}</option>
  <@listSiteIds contentId=content.contentId indentIndex=indentIndex + 1 />
</@loopSubContentCache >
</#macro>
