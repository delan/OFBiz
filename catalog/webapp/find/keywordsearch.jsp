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
<%@ page import="org.ofbiz.commonapp.product.product.*"%>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>

<%String searchCategoryId = request.getParameter("SEARCH_CATEGORY_ID");%>
<%String searchOperator = request.getParameter("SEARCH_OPERATOR");%>
<%if (!"AND".equalsIgnoreCase(searchOperator) && !"OR".equalsIgnoreCase(searchOperator)) { searchOperator = "OR"; }%>
<%ProductWorker.getKeywordSearchProducts(pageContext, "", searchCategoryId, true, true, searchOperator);%>
<ofbiz:object name="viewIndex" property="viewIndex" type='java.lang.Integer' />
<ofbiz:object name="viewSize" property="viewSize" type='java.lang.Integer' />
<ofbiz:object name="lowIndex" property="lowIndex" type='java.lang.Integer' />
<ofbiz:object name="highIndex" property="highIndex" type='java.lang.Integer' />
<ofbiz:object name="listSize" property="listSize" type='java.lang.Integer' />
<ofbiz:object name="keywordString" property="keywordString" type='java.lang.String' />

<br>
<div class='head1'>
    Search Results for "<%=UtilFormatOut.checkNull((String)pageContext.getAttribute("keywordString"))%>" 
    in Category with id "<%=UtilFormatOut.checkNull(searchCategoryId)%>"
    where <%="OR".equalsIgnoreCase(searchOperator)?"any keyword":"all keywords"%> matched.
</div>

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
        <td width='20%'>
          <div class='tabletext'><b>[<ofbiz:entityfield attribute="product" field="productId"/>]</b></div>
        </td>
        <td>
            <a href='<ofbiz:url>/EditProduct?productId=<ofbiz:entityfield attribute="product" field="productId"/></ofbiz:url>' class='buttontext'>
              <ofbiz:entityfield attribute="product" field="productName"/>
            </a>
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

<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
