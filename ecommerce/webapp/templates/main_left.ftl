${pages.get("/templates/header.ftl")}

<table width="100%" border="0" cellpadding="0" cellspacing="0">
 <tr>
  ${pages.get("/templates/leftbar.ftl")}
  <td width='100%' valign=top align=left>
    ${pages.get("/templates/errormsg.ftl")}
    ${pages.get("${content.path}")}
  </td>
 </tr>
</table>

${pages.get("/templates/footer.ftl")}
