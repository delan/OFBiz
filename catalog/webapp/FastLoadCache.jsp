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

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign=middle align=center>
              <div class="boxhead">Loading Catalog Caches...</div>
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
            <div>Loading Categories...</div>
            <%UtilTimer ctimer = new UtilTimer();%>
            <div><%=ctimer.timerString("Before category find")%></div>
            <%EntityListIterator categories = delegator.findListIteratorByCondition("ProductCategory", null, null, null);%>
            <div><%=ctimer.timerString("Before load all categories into cache")%></div>
            <%
                GenericValue category = null;
                long numCategories = 0;
                while ((category = (GenericValue) categories.next()) != null) {
                    delegator.putInPrimaryKeyCache(category.getPrimaryKey(), category);
                    numCategories++;
                }
                categories.close();
            %>
            <div><%=ctimer.timerString("Finished Categories")%></div>
            <div>Loaded <%=numCategories%> Categories</div>
            <BR>
            <div>Loading Products...</div>
            <%UtilTimer ptimer = new UtilTimer();%>
            <div><%=ptimer.timerString("Before product find")%></div>
            <%EntityListIterator products = delegator.findListIteratorByCondition("Product", null, null, null);%>
            <div><%=ptimer.timerString("Before load all products into cache")%></div>
            <%
                GenericValue product = null;
                long numProducts = 0;
                while ((product = (GenericValue) products.next()) != null) {
                    delegator.putInPrimaryKeyCache(product.getPrimaryKey(), product);
                    numProducts++;
                }
                products.close();
            %>
            <div><%=ptimer.timerString("Finished Products")%></div>
            <div>Loaded <%=numProducts%> products</div>

          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
