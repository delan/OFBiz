<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.product.catalog.*" %>

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

    URL ecommercePropertiesUrl = application.getResource("/WEB-INF/ecommerce.properties");
    Map layoutSettings = new HashMap();
    request.setAttribute("layoutSettings", layoutSettings);
    
    layoutSettings.put("companyName", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.name"));
    layoutSettings.put("companySubtitle", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.subtitle"));
    layoutSettings.put("headerImageUrl", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.image.url"));
    layoutSettings.put("headerMiddleBackgroundUrl", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.middle.background.url"));
    layoutSettings.put("headerRightBackgroundUrl", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.right.background.url"));
%>
