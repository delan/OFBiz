
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<%@page import="org.ofbiz.commonapp.product.category.*"%>
<%-- Get a list of all products in the current category. --%>
<%CategoryWorker.getRelatedProducts(pageContext, "mini_", UtilFormatOut.checkNull(request.getParameter("PRODUCT_CATEGORY_ID")),false);%>
  <BR>
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
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
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
          <tr>
            <td>
                <ofbiz:if name="mini_productList" size="0">
                  <table width='100%' CELLSPACING="0" CELLPADDING="4" BORDER="0">
                    <ofbiz:iterator name="miniProduct" property="mini_productList">
                      <tr>
                        <td>
                          <a href='<ofbiz:url>/EditProduct?PRODUCT_ID=<ofbiz:entityfield attribute="miniProduct" field="productId"/></ofbiz:url>' class='buttontext'>
                            <ofbiz:entityfield attribute="miniProduct" field="productName"/>
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
