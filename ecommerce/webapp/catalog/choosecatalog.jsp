<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*, org.ofbiz.commonapp.product.catalog.*, org.ofbiz.core.pseudotag.*, org.ofbiz.core.util.*" %>

<%Collection catalogCol = CatalogWorker.getCatalogIdsAvailable(pageContext);%>
<%pageContext.setAttribute("catalogCol", catalogCol);%>
<%if(catalogCol.size() > 0) {%>

  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign=middle align=center>
              <div class="boxhead"><%=CatalogWorker.getCatalogName(pageContext, CatalogWorker.getCurrentCatalogId(pageContext))%></div>
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
              <form name="choosecatalogform" method="POST" action="<ofbiz:url>/main</ofbiz:url>" style='margin: 0;'>
                <SELECT name='CURRENT_CATALOG_ID'>
                  <OPTION value='<%=CatalogWorker.getCurrentCatalogId(pageContext)%>'><%=CatalogWorker.getCatalogName(pageContext, CatalogWorker.getCurrentCatalogId(pageContext))%></OPTION>
                  <OPTION value='<%=CatalogWorker.getCurrentCatalogId(pageContext)%>'></OPTION>
                  <ofbiz:iterator name="catalogId" property="catalogCol" type="java.lang.String">
                    <OPTION value='<%=catalogId%>'><%=CatalogWorker.getCatalogName(pageContext, catalogId)%></OPTION>
                  </ofbiz:iterator>
                </SELECT>
                <div><a href="javascript:document.choosecatalogform.submit()" class="buttontext">Choose&nbsp;Catalog</a></div>
              </form>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
<%}%>
