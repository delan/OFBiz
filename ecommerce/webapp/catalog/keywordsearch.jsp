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
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.product.catalog.*, org.ofbiz.commonapp.product.product.*"%>

<%String contentPathPrefix = CatalogWorker.getContentPathPrefix(pageContext);%>
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
    where <%="OR".equalsIgnoreCase(searchOperator)?"any keyword":"all keywords"%> matched.
</div>

<%String baseSearchStr = "SEARCH_STRING="+keywordString+"&SEARCH_OPERATOR="+searchOperator+"&SEARCH_CATEGORY_ID="+searchCategoryId+"&VIEW_SIZE="+viewSize;%>
<%String nextStr = baseSearchStr+"&VIEW_INDEX="+(viewIndex.intValue()+1);%>
<%String prevStr = baseSearchStr+"&VIEW_INDEX="+(viewIndex.intValue()-1);%>

<ofbiz:unless name="searchProductList">
  <br><div class='head2'>&nbsp;No results found.</div>
</ofbiz:unless>

<ofbiz:if name="searchProductList">
<table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex.intValue() > 0){%>
          <a href="<ofbiz:url>/keywordsearch?<%=prevStr%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <span class="tabletext"><%=lowIndex%> - <%=highIndex%> of <%=listSize%></span>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url>/keywordsearch?<%=nextStr%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
</table>
</ofbiz:if>

<center>
  <table width='100%' cellpadding='0' cellspacing='0'>
    <%int listIndex = lowIndex.intValue();%>
    <ofbiz:iterator name="product" property="searchProductList">
      <tr><td colspan="2"><hr class='sepbar'></td></tr>
      <tr>
        <td>
          <%@ include file="/catalog/productsummary.jsp" %>
        </td>
      </tr>
      <%listIndex++;%>
    </ofbiz:iterator>
  </table>
</center>

<ofbiz:if name="searchProductList">
<table border="0" width="100%" cellpadding="2">
    <tr><td colspan="2"><hr class='sepbar'></td></tr>
    <tr>
      <td align=right>
        <b>
        <%if(viewIndex.intValue() > 0){%>
          <a href="<ofbiz:url>/keywordsearch?<%=prevStr%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if(listSize.intValue() > 0){%>
          <span class="tabletext"><%=lowIndex%> - <%=highIndex%> of <%=listSize%></span>
        <%}%>
        <%if(listSize.intValue() > highIndex.intValue()){%>
          | <a href="<ofbiz:url>/keywordsearch?<%=nextStr%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
</table>
</ofbiz:if>
