<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*, org.ofbiz.commonapp.product.catalog.*,
                 org.ofbiz.commonapp.product.promo.ProductPromoWorker" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*, org.ofbiz.core.entity.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%-- get these for the promoText --%>
<%pageContext.setAttribute("productPromos", ProductPromoWorker.getCatalogProductPromos(delegator, request));%>

<%-- Make sure that at least one promo has non-empty promoText --%>
<%boolean showPromoText = false;%>
<ofbiz:iterator name="productPromo" property="productPromos">
    <%if (UtilValidate.isNotEmpty(productPromo.getString("promoText"))) { showPromoText = true; }%>
</ofbiz:iterator>
<%pageContext.setAttribute("showPromoText", new Boolean(showPromoText));%>

<ofbiz:if name="showPromoText" type="Boolean">
  <BR>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;Special&nbsp;Offers</div>
            </td>
            <td valign="middle" align="right">&nbsp;</td>
          </tr>
        </table>
      </TD>
    </TR>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
          <tr>
            <td>
                <table width='100%' cellspacing="0" cellpadding="1" border="0">
                  <%int listIndex = 1;%>
                  <!-- show promotions text -->
                  <ofbiz:iterator name="productPromo" property="productPromos">
                    <%if (UtilValidate.isNotEmpty(productPromo.getString("promoText"))) {%>
                        <%if (listIndex > 1) {%>
                          <tr><td><hr class='sepbar'></td></tr>
                        <%}%>
                        <tr>
                          <td>
                            <div class='tabletext'><%=productPromo.getString("promoText")%></div>
                          </td>
                        </tr>
                        <%listIndex++;%>
                    <%}%>
                  </ofbiz:iterator>
                </table>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
</ofbiz:if>
