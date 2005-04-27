<table width="100%" border="0" >
  <TR>
    <TD width='100%'>
      <form name="siteRoleForm" mode="POST" action="<@ofbizUrl>/updateSiteRoles</@ofbizUrl>">
      <input type="hidden" name="permRoleSiteId" value="${forumId}"/>
      <input type="hidden" name="forumId" value="${forumId}"/>
      <input type="hidden" name="rootForumId" value="${rootForumId}"/>
      <table width='100%' border='0' cellspacing='0' cellpadding='4' class='boxoutside'>
        <tr>
            <td class="">User</td>
            <#list blogRoleIdList as roleTypeId>
              <td class="">${roleTypeId}</td>
            </#list>
        </tr>

      <#assign rowCount=0/>
        <#list siteList as siteRoleMap>
          <tr>
            <td class="">${siteRoleMap.partyId}</td>
            <#list blogRoleIdList as roleTypeId>
              <#assign cappedSiteRole= Static["org.ofbiz.entity.model.ModelUtil"].dbNameToVarName(roleTypeId) />
              <td align="center">
              <input type="checkbox" name="${cappedSiteRole}_o_${rowCount}" value="Y" <#if siteRoleMap[cappedSiteRole]?if_exists == "Y">checked</#if>/>
              </td>
          <input type="hidden" name="${cappedSiteRole}FromDate_o_${rowCount}" value="${siteRoleMap[cappedSiteRole + "FromDate"]?if_exists}"/>
            </#list>
          </tr>
          <input type="hidden" name="contentId_o_${rowCount}" value="${forumId}"/>
          <input type="hidden" name="partyId_o_${rowCount}" value="${siteRoleMap.partyId}"/>
          <#assign rowCount=rowCount + 1/>
        </#list>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead"><input type="text" name="partyId_o_${rowCount}" value=""/>
<a href="javascript:call_fieldlookup3('<@ofbizUrl>/LookupPartyAndUserLoginAndPerson</@ofbizUrl>')"><img src="<@ofbizContentUrl>/content/images/fieldlookup.gif</@ofbizContentUrl>" width="16" height="16" border="0" alt="Lookup"></a></div>
          </td>
            <#list blogRoleIdList as roleTypeId>
              <#assign cappedSiteRole= Static["org.ofbiz.entity.model.ModelUtil"].dbNameToVarName(roleTypeId) />
              <td align="center">
              <input type="checkbox" name="${cappedSiteRole}_o_${rowCount}" value="Y" />
              </td>
            </#list>
            <input type="hidden" name="contentId_o_${rowCount}" value="${forumId}"/>
            <#assign rowCount=rowCount + 1/>
        </tr>
          <tr>
            <td>
            <input type="submit" name="submitBtn" value="Update"/>
            </td>
          </tr>
      </table>
          <input type="hidden" name="_rowCount" value="${rowCount}"/>
      </form>
    </TD>
  </TR>
</table>

<SCRIPT language="javascript">
function call_fieldlookup3(view_name) {
        window.target = document.siteRoleForm.partyId_o_${rowCount - 1};
	var obj_lookupwindow = window.open(view_name,'FieldLookup', 'width=700,height=550,scrollbars=yes,status=no,top=100,left=100,dependent=yes,alwaysRaised=yes');
	obj_lookupwindow.opener = window;
	obj_lookupwindow.focus();
}
</script>

