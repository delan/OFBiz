<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
            <div class="boxhead">Search&nbsp;Catalog</div>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <form name="keywordsearchform" method="POST" action="<ofbiz:url>/keywordsearch?VIEW_SIZE=25</ofbiz:url>" style='margin: 0;'>
              <div class='tabletext'>Keywords: <input type="text" name="SEARCH_STRING" size="20" maxlength="50"></div>
              <div class='tabletext'>CategoryId: <input type="text" name="SEARCH_CATEGORY_ID" size="20" maxlength="20"></div>
              <a href="javascript:document.keywordsearchform.submit()" class="buttontext">&nbsp;Find</a>
            </form>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
