<SCRIPT language="javascript">
    function submitRows(rowCount) {
        var rowCountElement = document.createElement("input");
        rowCountElement.setAttribute("name", "_rowCount");
        rowCountElement.setAttribute("type", "hidden");
        rowCountElement.setAttribute("value", rowCount);
        document.forms.mostrecent.appendChild(rowCountElement);
        document.forms.mostrecent.submit();
    }
</SCRIPT>

<table width="100%" border="0" >

 <form name="mostrecent" mode="POST" action="<@ofbizUrl>/publishResponse</@ofbizUrl>"/>
  <#assign row=0/>
  <#list entityList as content>
    <@checkPermission entityOperation="_ADMIN" targetOperation="CONTENT_PUBLISH" subContentId=forumId >
        <tr>
          <td class="tabletext"> <b>id:</b>${content.contentId} </td>
          <td class="tabletext"> <b>name:</b>${content.contentName} </td>
      <@injectNodeTrailCsv subContentId=content.contentId redo="true" contentAssocTypeId="PUBLISH_LINK">
          <td>
  <a class="tabButton" href="<@ofbizUrl>/showforumresponse?contentId=${content.contentId}&nodeTrailCsv=${context.nodeTrailCsv?if_exists}</@ofbizUrl>" >View</a> 
          </td>
          <td class="tabletext">
          <b>submitted:</b>
          <input type="radio" name="statusId_o_${row}" value="BLOG_SUBMITTED" checked/>
          </td>
          <td class="tabletext">
          <b>publish:</b>
          <input type="radio" name="statusId_o_${row}" value="BLOG_PUBLISHED"/>
          </td>
        </tr>
          <input type="hidden" name="contentId_o_${row}" value="${content.contentId}"/>
        <tr>
          <td colspan="5" class="tabletext">
          <b>content:</b><br/>
            <@renderSubContentCache subContentId=content.contentId/>
          </td>
        </tr>
        <tr> <td colspan="5"> <hr/> </td> </tr>
        <#assign row = row + 1/>
      </@injectNodeTrailCsv >
    </@checkPermission >
  </#list>
    <#if 0 < entityList?size >
        <tr>
          <td colspan="5">
<div class="standardSubmit" ><a href="javascript:submitRows('${row?default(0)}')">Update</a></div>
          </td>
        </tr>
    </#if>
          <input type="hidden" name="forumId" value="${forumId}"/>
 </form>
</table>
