<%--
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     David E. Jones
 *@created    Sep 10 2001
 *@version    1.0
--%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.commonapp.product.catalog.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
    if (request.getAttribute("defaultViewSize") == null) {
        request.setAttribute("defaultViewSize", new Integer(10));
    }
    if (request.getAttribute("limitView") == null) {
        request.setAttribute("limitView", new Boolean(true));
    }
%>

<ofbiz:service name='getProductCategoryAndLimitedMembers'>
    <ofbiz:param name='productCategoryId' attribute='productCategoryId'/>
    <ofbiz:param name='viewIndexString' attribute='VIEW_INDEX'/>
    <ofbiz:param name='viewSizeString' attribute='VIEW_SIZE'/>
    <ofbiz:param name='defaultViewSize' attribute='defaultViewSize'/>
    <ofbiz:param name='limitView' attribute='limitView'/>
</ofbiz:service>
<%-- Returns: viewIndex, viewSize, lowIndex, highIndex, listSize, productCategory, productCategoryMembers --%>

<ofbiz:object name='productCategoryId' type='java.lang.String'/>
<ofbiz:object name='viewIndex' type='java.lang.Integer'/>
<ofbiz:object name='viewSize' type='java.lang.Integer'/>
<ofbiz:object name='lowIndex' type='java.lang.Integer'/>
<ofbiz:object name='highIndex' type='java.lang.Integer'/>
<ofbiz:object name='listSize' type='java.lang.Integer'/>
<ofbiz:object name='productCategory' type='org.ofbiz.core.entity.GenericValue'/>
<ofbiz:object name='productCategoryMembers' type='java.util.Collection'/>

<br>
<ofbiz:if name="productCategory">
    <table border="0" width="100%" cellpadding="3">
        <tr>
            <td colspan="2">
                <div class="head1">
                    <%=UtilFormatOut.checkNull(productCategory.getString("description"))%>
                    <ofbiz:if name="productCategoryMembers" size="0">
                        <% // a little routine to see if any have a quantity > 0 assigned
                            boolean hasQuantities = false;
                            Iterator pcmIter = UtilMisc.toIterator((Collection) pageContext.getAttribute("productCategoryMembers"));
                            while (pcmIter != null && pcmIter.hasNext()) {
                                GenericValue productCategoryMember = (GenericValue) pcmIter.next();
                                if (productCategoryMember.get("quantity") != null && productCategoryMember.getDouble("quantity").doubleValue() > 0.0) {
                                    hasQuantities = true;
                                    break;
                                }
                            }
                        %>
                        <%if (hasQuantities) {%>
                            <form method="POST" action="<ofbiz:url>/addCategoryDefaults<%=UtilFormatOut.ifNotEmpty((String)request.getAttribute(SiteDefs.CURRENT_VIEW), "/", "")%></ofbiz:url>" name="thecategoryform" style='margin: 0;'>
                              <input type='hidden' name='add_category_id' value='<%EntityField.run("productCategory", "productCategoryId", pageContext);%>'>
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
        <%if (UtilValidate.isNotEmpty(productCategory.getString("categoryImageUrl")) || UtilValidate.isNotEmpty(productCategory.getString("longDescription"))) pageContext.setAttribute("showCategoryDetails", "true");%>
        <ofbiz:if name="showCategoryDetails">
            <%-- <tr><td><hr class='sepbar'></td></tr> --%>
            <tr>
                <td align="left" valign="top" width="0">
                    <div class="tabletext">
                        <%EntityField.run("productCategory", "categoryImageUrl", "<img src='", "' vspace='5' hspace='5' border='1' height='100' align=left>", pageContext);%>
                        <%EntityField.run("productCategory", "longDescription", pageContext);%>
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
    <ofbiz:iterator name="productCategory" property="curCategoryList">
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
          <a href="<ofbiz:url><%="/category?category_id=" + productCategoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <span class="tabletext"><%=lowIndex%> - <%=highIndex%> of <%=listSize%></span>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url><%="/category?category_id=" + productCategoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
</table>

<center>
    <table width='100%' border='0' cellpadding='0' cellspacing='0'>
        <%int listIndex = lowIndex.intValue();%>
        <ofbiz:iterator name="productCategoryMember" property="productCategoryMembers" offset="<%=lowIndex.intValue()-1%>" limit="<%=viewSize.intValue()%>">
            <%GenericValue product = productCategoryMember.getRelatedOneCache("Product");%>
            <%if (product != null) pageContext.setAttribute("product", product);%>
            <tr><td><hr class='sepbar'></td></tr>
            <tr>
                <td>
                    <%@ include file="/catalog/productsummary.jsp" %>
                </td>
            </tr>
            <%listIndex++;%>
        </ofbiz:iterator>
        <tr><td colspan="2"><hr class='sepbar'></td></tr>
    </table>
</center>

<table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex.intValue() > 0){%>
          <a href="<ofbiz:url><%="/category?category_id=" + productCategoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <span class="tabletext"><%=lowIndex%> - <%=highIndex%> of <%=listSize%></span>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url><%="/category?category_id=" + productCategoryId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()+1)%></ofbiz:url>" class="buttontext">[Next]</a>
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
