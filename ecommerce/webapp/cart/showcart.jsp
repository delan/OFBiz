<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<% pageContext.setAttribute("PageName", "showcart"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<br><br>
<center>
  <table>
    <%@ include file="displaycart.jsp" %>
  </table>
</center>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
