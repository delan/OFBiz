${pages.get("/templates/header.html")}

<table width="100%" border="0" cellpadding="0" cellspacing="0">
 <tr>
  <td width='100%' valign=top align=left>
    ${pages.get("/templates/errormsg.html")}
    ${pages.get("${content.path}")}
  </td>
  ${pages.get("/templates/rightbar.html")}
 </tr>
</table>

${pages.get("/templates/footer.html")}
