<%
/**
 *  Title: Keyword Search Page
 *  Description: None
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
 */
%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.commonapp.product.product.*"%>

<%String searchCategoryId = request.getParameter("SEARCH_CATEGORY_ID");%>
<%ProductWorker.getKeywordSearchProducts(pageContext, "", searchCategoryId);%>
<ofbiz:object name="viewIndex" property="viewIndex" type='java.lang.Integer' />
<ofbiz:object name="viewSize" property="viewSize" type='java.lang.Integer' />
<ofbiz:object name="lowIndex" property="lowIndex" type='java.lang.Integer' />
<ofbiz:object name="highIndex" property="highIndex" type='java.lang.Integer' />
<ofbiz:object name="listSize" property="listSize" type='java.lang.Integer' />
<ofbiz:object name="keywordString" property="keywordString" type='java.lang.String' />

<br>
<div class='head1'>Search Results for "<%=UtilFormatOut.checkNull((String)pageContext.getAttribute("keywordString"))%>" in Category with id "<%=UtilFormatOut.checkNull(searchCategoryId)%>"</div>

<ofbiz:unless name="searchProductList">
  <br><div class='head2'>&nbsp;No results found.</div>
</ofbiz:unless>

<ofbiz:if name="searchProductList">
<table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex.intValue() > 0){%>
          <a href="<ofbiz:url><%="/keywordsearch?SEARCH_STRING=" + keywordString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <%=lowIndex%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url><%="/keywordsearch?SEARCH_STRING=" + keywordString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
</table>
</ofbiz:if>

<center>
  <table width='100%' cellpadding='2'>
    <%int listIndex = lowIndex.intValue();%>
    <ofbiz:iterator name="product" property="searchProductList">
      <%-- <tr><td colspan="2"><hr class='sepbar'></td></tr> --%>
      <tr>
        <td>
          <div class='tabletext'>
            <a href='<ofbiz:url>/EditProduct?PRODUCT_ID=<ofbiz:entityfield attribute="product" field="productId"/></ofbiz:url>' class='buttontext'>
              <ofbiz:entityfield attribute="product" field="productName"/>
            </a>
            <b>
              [<ofbiz:entityfield attribute="product" field="productId"/>]
              <font color="#006633"><ofbiz:entityfield attribute="product" field="defaultPrice"/></font>
            </b>
          </div>
        </td>
      </tr>
      <%listIndex++;%>
    </ofbiz:iterator>
    <%-- <tr><td colspan="2"><hr class='sepbar'></td></tr> --%>
  </table>
</center>

<ofbiz:if name="searchProductList">
<table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex.intValue() > 0){%>
          <a href="<ofbiz:url><%="/keywordsearch?SEARCH_STRING=" + keywordString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <%=lowIndex%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url><%="/keywordsearch?SEARCH_STRING=" + keywordString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex.intValue()+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
</table>
</ofbiz:if>
