
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<%@page import="org.ofbiz.commonapp.product.category.*"%>
<%-- Get a list of all products in the current category. --%>
<ofbiz:service name='getProductCategoryAndLimitedMembers'>
    <ofbiz:param name='productCategoryId' value='<%=UtilFormatOut.checkNull(request.getParameter("productCategoryId"))%>'/>
    <ofbiz:param name='defaultViewSize' value='<%=new Integer(30)%>'/>
    <ofbiz:param name='limitView' value='<%=new Boolean(true)%>'/>
    <ofbiz:param name='useCacheForMembers' value='<%=new Boolean(false)%>'/>
    <%-- Returns: viewIndex, viewSize, lowIndex, highIndex, listSize, productCategory, productCategoryMembers --%>
</ofbiz:service>
<ofbiz:object name='viewIndex' type='java.lang.Integer'/>
<ofbiz:object name='viewSize' type='java.lang.Integer'/>
<ofbiz:object name='lowIndex' type='java.lang.Integer'/>
<ofbiz:object name='highIndex' type='java.lang.Integer'/>
<ofbiz:object name='listSize' type='java.lang.Integer'/>
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
              <ofbiz:if name="productCategory">
                <ofbiz:if name="productCategoryMembers" size="0">
                  <table width='100%' cellspacing="0" cellpadding="1" border="0">
                    <ofbiz:iterator name="productCategoryMember" property="productCategoryMembers" offset="<%=0%>" limit="<%=viewSize.intValue()%>">
                        <%GenericValue product = productCategoryMember.getRelatedOneCache("Product");%>
                        <%if (product != null) {%>
                            <%pageContext.setAttribute("product", product);%>
                      <tr>
                        <td>
                          <a href='<ofbiz:url>/EditProduct?productId=<ofbiz:entityfield attribute="product" field="productId"/></ofbiz:url>' class='buttontext'>
                            <ofbiz:entityfield attribute="product" field="productName"/>
                          </a>
                          <div class='tabletext'>
                            <b><ofbiz:entityfield attribute="product" field="productId"/></b>
                          </div>
                        </td>
                      </tr>
                        <%}%>
                    </ofbiz:iterator>
                    <%if (listSize.intValue() > viewSize.intValue()) {%>
                      <tr>
                        <td>
                          <div class='tabletext'>NOTE: Only showing the first <%=viewSize.intValue()%> of <%=listSize.intValue()%> products. To view the rest, use the Products tab for this category.</div>
                        </td>
                      </tr>
                    <%}%>
                  </table>
                </ofbiz:if>
                <ofbiz:unless name="productCategoryMembers" size="0">
                  <div class='tabletext'>No products in category.</div>
                </ofbiz:unless>
              </ofbiz:if>
              <ofbiz:unless name="productCategory">
                  <div class='tabletext'>No category specified.</div>
              </ofbiz:unless>
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>
