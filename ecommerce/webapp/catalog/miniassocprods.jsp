
<%org.ofbiz.ecommerce.catalog.CatalogHelper.getRandomCartProductAssoc(pageContext, "miniAssociatedProducts");%>
<ofbiz:if name="miniAssociatedProducts" size="0">
  <TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
          <tr>
            <td valign="middle" align="center">
              <div class="boxhead">You&nbsp;Might&nbsp;Like...</div>
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
    <table width='100%' CELLSPACING="0" CELLPADDING="4" BORDER="0">
      <%int miniProdListIndex = 1;%>
      <!-- random complementary products -->
      <ofbiz:iterator name="miniProduct" property="miniAssociatedProducts">
        <%if(miniProdListIndex > 1) {%>
          <tr><td><hr class='sepbar'></td></tr>
        <%}%>
        <tr>
          <td>
            <%int miniProdQuantity = 1;%>
            <%String miniProdFormName = "theminiassocprod" + UtilFormatOut.formatQuantity(miniProdListIndex) + "form";%>
            <%@include file="/catalog/miniproductsummary.jsp"%>
          </td>
        </tr>
        <%miniProdListIndex++;%>
      </ofbiz:iterator>
    </table>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
</ofbiz:if>
