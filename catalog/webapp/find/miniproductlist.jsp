
<%@page import="org.ofbiz.commonapp.product.category.*"%>
<%-- Get a list of all products in the current category. --%>
<%CategoryWorker.getRelatedProducts(pageContext,"mini_",CategoryWorker.lastTrailItem(pageContext));%>
  <TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
          <tr>
            <td valign="middle" align="center">
              <div class="boxhead">Category&nbsp;Products</div>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
          <tr>
            <td>
    <ofbiz:if name="mini_productList" size="0">
      <table width='100%' CELLSPACING="0" CELLPADDING="4" BORDER="0">
        <ofbiz:iterator name="miniProduct" property="mini_productList">
          <tr>
            <td>
              <a href='<ofbiz:url>/EditProduct?PRODUCT_ID=<ofbiz:entityfield attribute="miniProduct" field="productId"/></ofbiz:url>' class='buttontext'>
                <ofbiz:entityfield attribute="miniProduct" field="name"/>
              </a>
              <div class='tabletext'>
                <b>
                  <ofbiz:entityfield attribute="miniProduct" field="productId"/>,
                  <font color="#006633"><ofbiz:entityfield attribute="miniProduct" field="defaultPrice"/></font>
                </b>
              </div>
            </td>
          </tr>
        </ofbiz:iterator>
      </table>
    </ofbiz:if>
    <ofbiz:unless name="mini_productList" size="0">
      <div class='tabletext'>No products found.</div>
    </ofbiz:unless>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
</ofbiz:if>
