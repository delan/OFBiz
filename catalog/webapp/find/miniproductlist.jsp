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
 *@created    May 22 2001
 *@version    1.0
--%>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<%@page import="org.ofbiz.commonapp.product.category.*"%>
<%
    String state = request.getParameter("CategoryProductsState");
    boolean isOpen = true;
    if (state != null) {
        session.setAttribute("CategoryProductsState", state);
        isOpen = "open".equals(state);
    } else {
        state = (String) session.getAttribute("CategoryProductsState");
        if (state != null) {
            isOpen = "open".equals(state);
        }
    }
%>
<%-- Get a list of all products in the current category. --%>
<%if (isOpen) {%>
<ofbiz:service name='getProductCategoryAndLimitedMembers'>
    <ofbiz:param name='productCategoryId' value='<%=UtilFormatOut.checkNull(request.getParameter("productCategoryId"))%>'/>
    <ofbiz:param name='defaultViewSize' value='<%=new Integer(30)%>'/>
    <ofbiz:param name='limitView' value='<%=new Boolean(true)%>'/>
    <ofbiz:param name='useCacheForMembers' value='<%=new Boolean(false)%>'/>
    <%-- Returns: viewIndex, viewSize, lowIndex, highIndex, listSize, productCategory, productCategoryMembers --%>
</ofbiz:service>
<%}%>
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
          <td valign=middle align=right>
            <%if (isOpen) {%>
                <a href='<ofbiz:url>/main?CategoryProductsState=close</ofbiz:url>' class='lightbuttontext'>&nbsp;_&nbsp;</a>
            <%} else {%>
                <a href='<ofbiz:url>/main?CategoryProductsState=open</ofbiz:url>' class='lightbuttontext'>&nbsp;[]&nbsp;</a>
            <%}%>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
<%if (isOpen) {%>
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
<%}%>
</TABLE>
