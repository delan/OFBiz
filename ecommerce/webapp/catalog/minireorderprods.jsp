
<%org.ofbiz.ecommerce.catalog.CatalogWorker.getQuickReorderProducts(pageContext, "miniReorderProducts", "miniReorderQuantities");%>
<ofbiz:if name="miniReorderProducts" size="0">
  <%Map miniReorderQuantities = (Map)pageContext.getAttribute("miniReorderQuantities");%>
  <TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
          <tr>
            <td valign="middle" align="center">
              <div class="boxhead">Quick&nbsp;Reorder...</div>
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
      <%int miniReorderListIndex = 1;%>
      <!-- random complementary products -->
      <ofbiz:iterator name="miniProduct" property="miniReorderProducts">
        <%if(miniReorderListIndex > 1) {%>
          <tr><td><hr class='sepbar'></td></tr>
        <%}%>
        <tr>
          <td>
            <%Integer quantInt = (Integer)miniReorderQuantities.get(miniProduct.get("productId"));%>
            <%if(quantInt == null) quantInt = new Integer(1);%>
            <%int miniProdQuantity = quantInt.intValue();%>
            <%String miniProdFormName = "theminireorderprod" + UtilFormatOut.formatQuantity(miniReorderListIndex) + "form";%>
            <%@include file="/catalog/miniproductsummary.jsp"%>
          </td>
        </tr>
        <%miniReorderListIndex++;%>
      </ofbiz:iterator>
    </table>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
</ofbiz:if>
