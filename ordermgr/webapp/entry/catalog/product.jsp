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
<%@ page import="java.util.*, org.ofbiz.core.util.*, org.ofbiz.core.entity.*"%>
<%@ page import="org.ofbiz.core.pseudotag.*, org.ofbiz.commonapp.product.product.*"%>
<%@ page import="org.ofbiz.commonapp.product.catalog.*"%>

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<%String productId = request.getParameter("product_id");%>
<ofbiz:service name='getProduct'>
    <ofbiz:param name='productId' attribute='product_id'/>
</ofbiz:service>

<ofbiz:unless name="product">
  <center><div class='head2'>Product not found for Product ID "<%=UtilFormatOut.checkNull(productId)%>"!</div></center>
</ofbiz:unless>
<ofbiz:if name="product">
    <ofbiz:object name="product" property="product"/>
    <%request.setAttribute("product", product);%>

    <%-- now figure out which template to use for this product... --%>
    <%
        String detailTemplate = product.getString("detailTemplate");
        if (UtilValidate.isEmpty(detailTemplate)) {
            detailTemplate = "/entry/catalog/productdetail.jsp";
        }
        String templatePathPrefix = CatalogWorker.getTemplatePathPrefix(pageContext);
        if (Debug.infoOn()) Debug.logInfo("Catalog template: prefix=" + templatePathPrefix + ", template=" + detailTemplate);
        detailTemplate = templatePathPrefix + detailTemplate;

        RequestDispatcher rd = null;
        try {
            rd = application.getRequestDispatcher(detailTemplate);
        } catch (Exception e) {
            Debug.logError(e, "Error getting request dispatcher");
        }
        if (rd != null) {
            try {
                rd.include(request, response);
            } catch (java.io.FileNotFoundException e) {
                Debug.logError(e, "Error dispatching request");
%>
    <br>
    <center><div class='head2'>ERROR: The template for this product was not found at <%=detailTemplate%>.</div></center>
    <center><div class='head2'>Please try back later.</div></center>
<%
            }
        } else {
            Debug.logError("ERROR: The template for this product was not found at " + detailTemplate);
    %>
    <br>
    <center><div class='head2'>ERROR: The template for this product was not found at <%=detailTemplate%>.</div></center>
    <center><div class='head2'>Please try back later.</div></center>
    <%}%>
</ofbiz:if>
