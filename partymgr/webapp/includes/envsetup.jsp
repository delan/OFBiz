<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<%
    GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);
    if (userLogin != null) request.setAttribute("userLogin", userLogin);

    GenericValue person = (GenericValue) session.getAttribute(SiteDefs.PERSON);
    GenericValue partyGroup = (GenericValue) session.getAttribute(SiteDefs.PARTY_GROUP);
    if (person == null) {
        person = userLogin == null ? null : userLogin.getRelatedOne("Person");
        if (person != null) session.setAttribute(SiteDefs.PERSON, person);
    }
    if (person != null) request.setAttribute("person", person);
    else if (partyGroup == null) {
        person = userLogin == null ? null : userLogin.getRelatedOne("PartyGroup");
        if (partyGroup != null) session.setAttribute(SiteDefs.PARTY_GROUP, partyGroup);
    }
    if (partyGroup != null) request.setAttribute("partyGroup", partyGroup);

    String controlPath = (String) request.getAttribute(SiteDefs.CONTROL_PATH);
    String contextRoot = (String) request.getAttribute(SiteDefs.CONTEXT_ROOT);
    String serverRoot = (String) request.getAttribute(SiteDefs.SERVER_ROOT_URL);

    Map layoutSettings = new HashMap();
    request.setAttribute("layoutSettings", layoutSettings);
    
    layoutSettings.put("companyName", "OFBiz: Party Manager");
    layoutSettings.put("companySubtitle", "Part of the Open For Business Family of Open Source Software");
    layoutSettings.put("headerImageUrl", "/images/ofb_logo.gif");
    layoutSettings.put("headerMiddleBackgroundUrl", null);
    layoutSettings.put("headerRightBackgroundUrl", null);
%>
