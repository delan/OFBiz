
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.entity.*" %>
<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />

<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>

<%
    String partyId = request.getParameter("party_id");
    if (partyId == null) partyId = request.getParameter("partyId");
    if (partyId == null) partyId = (String) request.getAttribute("partyId");

    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;
    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));

    String donePage = request.getParameter("DONE_PAGE");
    if(donePage == null || donePage.length() <= 0) donePage="viewprofile?partyId=" + partyId;
%>
  
  <p class="head1">Create New RoleType</p>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.createroleform.submit()" class="buttontext">[Save]</a>

  <form method="post" action="<ofbiz:url>/createroletype/<%=donePage%></ofbiz:url>" name="createroleform">
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align=right><div class="tabletext">Role Type ID</div></td>
      <td width="74%">
        <input type="text" class="inputBox" name="roleTypeId" size="20">
      *</td>
    <tr>
      <td width="26%" align=right><div class="tabletext">Description</div></td>
      <td width="74%">
        <input type="text" class="inputBox" name="description" size="30">
      </td>
    </tr>
  </table>
  </form>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.createroleform.submit()" class="buttontext">[Save]</a>
<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>
