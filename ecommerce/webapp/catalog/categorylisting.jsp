<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" />

<ofbiz:object name="viewIndex" property="viewIndex" type='java.lang.Integer' />
<ofbiz:object name="viewSize" property="viewSize" type='java.lang.Integer' />
<ofbiz:object name="lowIndex" property="lowIndex" type='java.lang.Integer' />
<ofbiz:object name="highIndex" property="highIndex" type='java.lang.Integer' />
<ofbiz:object name="listSize" property="listSize" type='java.lang.Integer' />
<ofbiz:object name="categoryId" property="categoryId" type='java.lang.String' />

<%
    GenericValue category = null;
    try {
        category = delegator.findByPrimaryKeyCache("ProductCategory", UtilMisc.toMap("productCategoryId", categoryId));
    } catch (GenericEntityException e) {
        Debug.logWarning(e.getMessage());
        category = null;
    }
    if (category != null)
        pageContext.setAttribute("listingCategory", category);
%>
<ofbiz:if name="listingCategory">
    <table border="0" width="100%" cellpadding="3">
        <tr>
            <td colspan="2">
                <div class="head1">
                    <%=UtilFormatOut.checkNull(category.getString("description"))%>
                    <ofbiz:if name="productCategoryMembers" size="0">
                        <% // a little routine to see if any have a quantity > 0 assigned
                            boolean hasQuantities = false;
                            Iterator pcmIter = UtilMisc.toIterator((Collection) pageContext.getAttribute("productCategoryMembers"));
                            while (pcmIter != null && pcmIter.hasNext()) {
                                GenericValue productCategoryMember = (GenericValue) pcmIter.next();
                                if (productCategoryMember.get("quantity") != null && productCategoryMember.getDouble("quantity").doubleValue() > 0.0) {
                                    hasQuantities = true;
                                }
                            }
                        %>
                        <%if (hasQuantities) {%>
                            <form method="POST" action="<ofbiz:url>/addCategoryDefaults<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="thecategoryform" style='margin: 0;'>
                              <input type='hidden' name='add_category_id' value='<%EntityField.run("listingCategory", "productCategoryId", pageContext);%>'>
                              <%=UtilFormatOut.ifNotEmpty(request.getParameter("product_id"), "<input type='hidden' name='product_id' value='", "'>")%>
                              <%=UtilFormatOut.ifNotEmpty(request.getParameter("category_id"), "<input type='hidden' name='category_id' value='", "'>")%>
                              <%=UtilFormatOut.ifNotEmpty(request.getParameter("VIEW_INDEX"), "<input type='hidden' name='VIEW_INDEX' value='", "'>")%>
                              <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_STRING"), "<input type='hidden' name='SEARCH_STRING' value='", "'>")%>
                              <%=UtilFormatOut.ifNotEmpty(request.getParameter("SEARCH_CATEGORY_ID"), "<input type='hidden' name='SEARCH_CATEGORY_ID' value='", "'>")%>
                              <a href="javascript:document.thecategoryform.submit()" class="buttontext"><nobr>[Add Products in this Category to the Cart using Default Quantities]</nobr></a>
                            </form>
                        <%}%>
                    </ofbiz:if>
                </div>
            </td>
        </tr>
        <%if (UtilValidate.isNotEmpty(category.getString("categoryImageUrl")) || UtilValidate.isNotEmpty(category.getString("longDescription"))) pageContext.setAttribute("showCategoryDetails", "true");%>
        <ofbiz:if name="showCategoryDetails">
            <tr><td><hr class='sepbar'></td></tr>
            <tr>
                <td align="left" valign="top" width="0">
                    <div class="tabletext">
                        <%EntityField.run("listingCategory", "categoryImageUrl", "<img src='", "' vspace='5' hspace='5' border='1' height='100' align=left>", pageContext);%>
                        <%EntityField.run("listingCategory", "longDescription", pageContext);%>
                    </div>
                </td>
            </tr>
        </ofbiz:if>
    </table>
</ofbiz:if>

<%--
<ofbiz:if name="curCategoryList">
  <hr>
  <b>Categories:</b>
  <hr>
  <br>
</ofbiz:if>

<center>
  <table>
    <ofbiz:iterator name="category" property="curCategoryList">
      <tr>
        <td>
          <a href="<ofbiz:url>/category?category_id=<%= category.getString("productCategoryId") %></ofbiz:url>"><%= category.getString("description") %></a>
        </td>
      </tr>
    </ofbiz:iterator>
  </table>
</center>
<ofbiz:if name="productList">
  <hr>
  <b>Products:</b>
  <hr>
  <br>
</ofbiz:if>
--%>

<ofbiz:if name="productCategoryMembers" size="0">
<table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex.intValue() > 0){%>
          <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <%=lowIndex%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
</table>

<center>
    <table width='100%' border='0' cellpadding='0' cellspacing='0'>
        <%ArrayList productCategoryMembers = (ArrayList) pageContext.getAttribute("productCategoryMembers");%>
        <%for (int listIndex = (lowIndex.intValue() - 1); listIndex < highIndex.intValue(); listIndex++) {%>
            <%GenericValue productCategoryMember = (GenericValue) productCategoryMembers.get(listIndex);%>
            <%if (productCategoryMember != null) pageContext.setAttribute("productCategoryMember", productCategoryMember);%>
            <%GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productCategoryMember.get("productId")));%>
            <%if (product != null) pageContext.setAttribute("product", product);%>
            <tr><td><hr class='sepbar'></td></tr>
            <tr>
                <td>
                    <%@ include file="/catalog/productsummary.jsp" %>
                </td>
            </tr>
        <%}%>
        <tr><td colspan="2"><hr class='sepbar'></td></tr>
    </table>
</center>

<table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex.intValue() > 0){%>
          <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <%=lowIndex%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url><%="/category?category_id=" + categoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
</table>
</ofbiz:if>

<ofbiz:unless name="productCategoryMembers" size="0">
<table border="0" width="100%" cellpadding="2">
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td>
          <DIV class='tabletext'>There are no products in this category.</DIV>
      </td>
    </tr>
</table>
</ofbiz:unless>