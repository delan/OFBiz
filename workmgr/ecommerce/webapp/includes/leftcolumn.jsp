<table width="100%" border="0" cellpadding="0" cellspacing="0">
 <tr>
  <td width='150' valign=top align=left>
    <%-- No <br> in front of this one because it is optional, it may not always appear and we don't want to <br>s at the top --%>
    <%@ include file="/catalog/choosecatalog.jsp"%>
    <br><%@ include file="/catalog/keywordsearchbox.jsp"%>
    <br><%@ include file="/catalog/sidedeepcategory.jsp" %>
    <br><%@ include file="/catalog/minireorderprods.jsp" %>
  </td>
  <td width='5'>&nbsp;&nbsp;&nbsp;</td>
  <td width='100%' valign=top align=left>
<%@ include file="/includes/errormsg.jsp"%>

