<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>


<%org.ofbiz.ecommerce.catalog.CatalogWorker.getQuickReorderProducts(pageContext, "miniReorderProducts", "miniReorderQuantities");%>
<ofbiz:if name="miniReorderProducts" size="0">
  <%Map miniReorderQuantities = (Map) pageContext.getAttribute("miniReorderQuantities");%>
  <BR>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
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
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
          <tr>
            <td>
                <table width='100%' cellspacing="0" cellpadding="0" border="0">
                  <%int miniReorderListIndex = 1;%>
                  <!-- random complementary products -->
                  <ofbiz:iterator name="miniProduct" property="miniReorderProducts">
                    <%if(miniReorderListIndex > 1) {%>
                      <tr><td><hr class='sepbar'></td></tr>
                    <%}%>
                    <tr>
                      <td>
                        <%Integer quantInt = (Integer) miniReorderQuantities.get(miniProduct.get("productId"));%>
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
