<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
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
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td align=center>
            <form name="keywordsearchform" method="POST" action="<ofbiz:url>/keywordsearch</ofbiz:url>" style='margin: 0;'>
              <input type='hidden' name="VIEW_SIZE" value="10">
              <input type='hidden' name="SEARCH_CATEGORY_ID" value="<%=UtilFormatOut.checkNull(CatalogWorker.getCatalogSearchCategoryId(pageContext, CatalogWorker.getCurrentCatalogId(pageContext)))%>">
              <input type='text' name="SEARCH_STRING" size="14" maxlength="50"><a href="javascript:document.keywordsearchform.submit()" class="buttontext">&nbsp;Find</a>
            </form>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
