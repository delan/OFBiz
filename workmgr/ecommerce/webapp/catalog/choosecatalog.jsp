<%@ page import="org.ofbiz.ecommerce.catalog.*" %>
<%Collection catalogCol = CatalogWorker.getCatalogIdsAvailable(pageContext); pageContext.setAttribute("catalogCol", catalogCol);%>
<%if(catalogCol.size() > 0) {%>
  <BR>
  <TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
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
        <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
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