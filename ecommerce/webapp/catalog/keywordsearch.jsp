<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*, org.ofbiz.commonapp.product.product.*"%>

<% pageContext.setAttribute("PageName", "Keyword Search"); %>
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/leftcolumn.jsp" %> 

<%ProductWorker.getKeywordSearchProducts(pageContext, "");%>
<ofbiz:object name="viewIndex" property="viewIndex" type='java.lang.Integer' />
<ofbiz:object name="viewSize" property="viewSize" type='java.lang.Integer' />
<ofbiz:object name="lowIndex" property="lowIndex" type='java.lang.Integer' />
<ofbiz:object name="highIndex" property="highIndex" type='java.lang.Integer' />
<ofbiz:object name="listSize" property="listSize" type='java.lang.Integer' />
<ofbiz:object name="keywordString" property="keywordString" type='java.lang.String' />

<br>
<div class='head1'>Search Results for "<%=UtilFormatOut.checkNull((String)pageContext.getAttribute("keywordString"))%>"</div>

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
  <table width='100%'>
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

<%@ include file="/includes/rightcolumn.jsp" %> 
<%@ include file="/includes/footer.jsp" %>
