<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.product.category.*, org.ofbiz.ecommerce.catalog.*" %>

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
          <td align=center>
            <form name="keywordsearchform" method="POST" action="<ofbiz:url>/keywordsearch</ofbiz:url>" style='margin: 0;'>
              <input type='hidden' name="VIEW_SIZE" value="10">
              <input type='hidden' name="SEARCH_CATEGORY_ID" value="<%=UtilFormatOut.checkNull(CatalogWorker.getCatalogSearchCategoryId(pageContext, CatalogWorker.getCurrentCatalogId(pageContext)))%>">
              <input type='text' name="SEARCH_STRING" size="14" maxlength="50">
              <div class='tabletext'>
                Any<input type=RADIO name='SEARCH_OPERATOR' value='OR' checked>
                All<input type=RADIO name='SEARCH_OPERATOR' value='AND'>
                <a href="javascript:document.keywordsearchform.submit()" class="buttontext">&nbsp;Find</a>
              </div>
            </form>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
