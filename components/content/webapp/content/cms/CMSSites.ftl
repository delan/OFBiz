
<SCRIPT language="javascript">
function call_fieldlookup4(rootForumId, parentForumId ) {
	var obj_lookupwindow = window.open("addSubSite?rootForumId=" + rootForumId + "&parentForumId=" + parentForumId, 'FieldLookup', 'width=500,height=250,scrollbars=yes,status=no,top='+my+',left='+mx+',dependent=yes,alwaysRaised=yes');
	obj_lookupwindow.opener = window;
	obj_lookupwindow.focus();
}
</SCRIPT>


<#include "publishlib.ftl" />
<#assign rootForumId=rootForumId?if_exists/>
<#if !rootForumId?has_content>
    <#assign rootForumId=requestParameters.rootForumId?if_exists/>
</#if>
<#if !rootForumId?has_content>
    <#assign rootForumId=page.getProperty("defaultSiteId")?if_exists/>
</#if>
${menuWrapper.renderMenuString()}
<@checkPermission entityOperation="_ADMIN" targetOperation="CONTENT_ADMIN" >
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <form name="userform" mode="POST" action="<@ofbizUrl>/CMSSites</@ofbizUrl>" >
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='appTitle'>
        <tr>
          <td colspan="1" valign="middle" align="right">
            <div class="boxhead">&nbsp; Root Site ID&nbsp;&nbsp; </div>
          </td>
          <td valign="middle" align="left">
            <div class="boxhead">
             <input type="text" name="rootForumId" size="20" value="${rootForumId?if_exists}">
            </div>
          </td>
          <td valign="middle" align="right">
            <a href="javascript:document.userform.submit()" class="submenutextright">Refresh</a>
          </td>
        </tr>
      <table>
      </form>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <form mode="POST" name="publishsite" action="<@ofbizUrl>/linkContentToPubPt</@ofbizUrl>">
              <table width="100%" border="0" cellpadding="1">
                    <#assign rowCount = 0 />
                    <@showSites forumId=rootForumId />
              </table>
            </form>
          </td>
        </tr>
<#--
        <tr>
          <td colspan="1">
<div class="standardSubmit" ><a href="javascript:submitRows('${rowCount?default(0)}')">Update</a></div>
          </td>
        </tr>
-->

      </table>
    </TD>
  </TR>
<#if requestParameters.moderatedSiteId?has_content>
  <TR>
    <TD width='100%'>
      <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
         <tr><td><hr/></td></tr>
         <tr><td align="center"><div class="head1">Unapproved entries for forum Id:${requestParameters.moderatedSiteId}</div></td></tr>
         <tr><td><hr/></td></tr>
         <@moderateSite rootForumId=rootForumId forumId=requestParameters.moderatedSiteId />
      </TABLE>
    </TD>
  </TR>
</#if>
<#if requestParameters.permRoleSiteId?has_content>
  <TR>
    <TD width='100%'>
      <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
         <tr><td><hr/></td></tr>
         <tr><td align="center"><div class="head1">Associated roles for forum Id:${requestParameters.permRoleSiteId}</div></td></tr>
         <tr><td><hr/></td></tr>
         <@grantSiteRoles rootForumId=rootForumId forumId=requestParameters.permRoleSiteId/>
      </TABLE>
    </TD>
  </TR>
</#if>
</TABLE>
</@checkPermission>
