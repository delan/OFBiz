
<div class="head2">XPDL Repository</div>
<#if packages?has_content>
  <div>&nbsp;</div>
  <div class="tabletext">XPDL packages loaded into the repository.</div>
  <table cellpadding="2" cellspacing="0" border="1">
    <tr>
      <td><div class="tableheadtext">ID</div></td>
      <td><div class="tableheadtext">Opened</div></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <#list packages as package>
      <#if (!package?starts_with("CVS"))>
        <#assign packageId = repMgr.getPackageId(package)>
        <#assign open = pkgMgr.isPackageOpened(packageId)>
        <tr>
          <td align="left"><div class="tabletext">${packageId?default("??")}</div>
          <td align="center"><div class="tabletext"><#if open>Y<#else>N</#if></div>
          <td align="center"><a href="<@ofbizUrl>/repository?delete=${package}</@ofbizUrl>" class="buttontext">Remove</a>
          <td align="center"><a href="<@ofbizUrl>/repository?<#if open>close=${packageId}<#else>open=${package}</#if></@ofbizUrl>" class="buttontext"><#if open>Close<#else>Open</#if></a>
        </tr>
      </#if>
    </#list>
  </table>
<#else>
  <div class="tabletext">Repository is empty.</div>
</#if>

<br>
<div class="head2">Upload XPDL</div>
<div>&nbsp;</div>
<form method="post" enctype="multipart/form-data" action="<@ofbizUrl>/repository?upload=xpdl</@ofbizUrl>" name="xpdlUploadForm">
  <input type="file" class="inputBox" size="50" name="fname">
  <div><hr class="sepbar"></div>
  <input type="submit" class="smallSubmit" value="Upload">
</form>