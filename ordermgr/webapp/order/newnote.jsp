
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("ORDERMGR", "_VIEW", session)) {%>

<%
    String orderId = request.getParameter("order_id");
    if (orderId == null) orderId = (String) request.getSession().getAttribute("orderId");
    else request.getSession().setAttribute("orderId", orderId);
    
    String workEffortId = request.getParameter("workEffortId");
    String partyId = request.getParameter("partyId");
    String roleTypeId = request.getParameter("roleTypeId");
    String fromDate = request.getParameter("fromDate");

    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;
    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));

    String donePage = request.getParameter("DONE_PAGE");
    if(donePage == null || donePage.length() <= 0) 
    	donePage="orderview?order_id=" + orderId;
    	if (workEffortId != null)
    		donePage = donePage + "&workEffortId=" + workEffortId;
        if (partyId != null)
        	donePage = donePage + "&partyId=" + partyId;
        if (roleTypeId != null)
        	donePage = donePage + "&roleTypeId=" + roleTypeId;
        if (fromDate != null)
        	donePage = donePage + "&fromDate=" + fromDate;
%>
  <br>
  <p class="head1">Add Note</p>

  &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
  &nbsp;<a href="javascript:document.createnoteform.submit()" class="buttontext">[Save]</a>

  <form method="post" action="<ofbiz:url>/createordernote/<%=donePage%></ofbiz:url>" name="createnoteform">
  <input type="hidden" name="orderId" value="<%=orderId%>">
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align=right><div class="tabletext">Note</div></td>
      <td width="74%">
        <textarea name="note" rows="5" cols="70"></textarea>
      </td>
      <td>*</td>
    </tr>
  </table>
  </form>

  &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
  &nbsp;<a href="javascript:document.createnoteform.submit()" class="buttontext">[Save]</a>
<%}else{%>
  <h3>You do not have permission to view this page. ("ORDERMGR_VIEW" or "ORDERMGR_ADMIN" needed)</h3>
<%}%>
