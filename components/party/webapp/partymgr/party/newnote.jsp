
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.entity.*" %>
<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />

<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>

<%
    String partyId = request.getParameter("party_id");
    if (partyId == null) partyId = request.getParameter("partyId");
    if (partyId == null) partyId = (String) request.getAttribute("partyId");

    boolean tryEntity = true;
    if(request.getAttribute("_ERROR_MESSAGE_") != null) tryEntity = false;
    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));

    String donePage = request.getParameter("DONE_PAGE");
    if(donePage == null || donePage.length() <= 0) donePage="viewprofile?partyId=" + partyId;
%>
  
  <p class="head1">Add Note</p>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.createnoteform.submit()" class="buttontext">[Save]</a>

  <form method="post" action="<ofbiz:url>/createpartynote/<%=donePage%></ofbiz:url>" name="createnoteform">
  <input type="hidden" name="partyId" value="<%=partyId%>">
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align=right><div class="tabletext">Note</div></td>
      <td width="74%">
        <textarea name="note" class="textAreaBox" rows="5" cols="70"></textarea>
      *</td>
    </tr>
  </table>
  </form>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.createnoteform.submit()" class="buttontext">[Save]</a>
<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>
