
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>

<%
    String partyId = request.getParameter("party_id");
    if (partyId == null) partyId = (String) request.getSession().getAttribute("partyId");
    else request.getSession().setAttribute("partyId", partyId);

    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;
    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));

    String donePage = request.getParameter("DONE_PAGE");
    if(donePage == null || donePage.length() <= 0) donePage="viewprofile";
%>
  <p class="head1">Create UserLogin</p>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.changepasswordform.submit()" class="buttontext">[Save]</a>

  <form method="post" action="<ofbiz:url>/createuserlogin/<%=donePage%></ofbiz:url>" name="createloginform">
  <input type="hidden" name="partyId" value="<%=partyId%>">
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align=right><div class="tabletext">UserLogin ID</div></td>
      <td width="74%">
        <input type="text" name="userLoginId" size="20">
      *</td>
    <tr>
      <td width="26%" align=right><div class="tabletext">New Password</div></td>
      <td width="74%">
        <input type="password" name="currentPassword" size="20" maxlength="20">
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">New Password Verify</div></td>
      <td width="74%">
        <input type="password" name="currentPasswordVerify" size="20" maxlength="20">
      *</td>
    </tr>
    <tr>
      <td width="26%" align=right><div class="tabletext">Password Hint</div></td>
      <td width="74%">
        <input type="text" size="40" maxlength="100" <ofbiz:inputvalue field="passwordHint" entityAttr="userLogin" tryEntityAttr="tryEntity" fullattrs="true"/>>
      </td>
    </tr>
  </table>
  </form>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.createloginform.submit()" class="buttontext">[Save]</a>
<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>
