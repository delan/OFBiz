<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>

<%
    String partyId = request.getParameter("party_id");
    if (partyId == null) partyId = (String) request.getSession().getAttribute("partyId");
    else request.getSession().setAttribute("partyId", partyId);

    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;
    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));

    GenericValue avsOverride = delegator.findByPrimaryKey("PartyICSAVSOverride", UtilMisc.toMap("partyId", partyId));
    if (avsOverride != null) pageContext.setAttribute("avsOverride", avsOverride);

    String donePage = request.getParameter("DONE_PAGE");
    if(donePage == null || donePage.length() <= 0) donePage="viewprofile";
%>
  <br>
  <p class="head1">Edit AVS Override String</p>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.editavsform.submit()" class="buttontext">[Save]</a>

  <form method="post" action="<ofbiz:url>/updateAvsOverride/<%=donePage%></ofbiz:url>" name="editavsform">
  <input type="hidden" name="partyId" value="<%=partyId%>">
  <table width="90%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%" align=right><div class="tabletext">AVS String</div></td>
      <td width="74%">
        <ofbiz:if name="avsOverride">
          <input type="text" name="avsDeclineString" size="40" value="<%=avsOverride.getString("avsDeclineString")%>">*
        </ofbiz:if>
        <ofbiz:unless name="avsOverride">
          <input type="text" name="avsDeclineString" size="40" value="">*
        </ofbiz:unless>
      </td>
    </tr>
  </table>
  </form>

    &nbsp;<a href="<ofbiz:url>/authview/<%=donePage%></ofbiz:url>" class="buttontext">[Go&nbsp;Back]</a>
    &nbsp;<a href="javascript:document.editavsform.submit()" class="buttontext">[Save]</a>
<%}else{%>
  <h3>You do not have permission to view this page. ("PARTYMGR_VIEW" or "PARTYMGR_ADMIN" needed)</h3>
<%}%>
