<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.securityext.login.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />
<%
    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    if (userLogin != null) request.setAttribute("userLogin", userLogin);

    GenericValue person = (GenericValue) session.getAttribute("_PERSON_");
    GenericValue partyGroup = (GenericValue) session.getAttribute("_PARTY_GROUP_");
    if (person == null) {
        person = userLogin == null ? null : userLogin.getRelatedOne("Person");
        if (person != null) session.setAttribute("_PERSON_", person);
    }
    if (person != null) request.setAttribute("person", person);
    else if (partyGroup == null) {
        person = userLogin == null ? null : userLogin.getRelatedOne("PartyGroup");
        if (partyGroup != null) session.setAttribute("_PARTY_GROUP_", partyGroup);
    }
    if (partyGroup != null) request.setAttribute("partyGroup", partyGroup);

    String controlPath = (String) request.getAttribute("_CONTROL_PATH_");
    String contextRoot = (String) request.getAttribute("_CONTEXT_ROOT_");
    String serverRoot = (String) request.getAttribute("_SERVER_ROOT_URL_");

    /* reading of the localization information */
    List availableLocales = Arrays.asList(Locale.getAvailableLocales());
    request.setAttribute("availableLocales",availableLocales);

    Locale locale = UtilHttp.getLocale(request);
    request.setAttribute("locale",locale);
    Map uiLabelMap = UtilProperties.getResourceBundleMap("PartyUiLabels", locale);
    request.setAttribute("uiLabelMap", uiLabelMap);

    Map layoutSettings = new HashMap();
    request.setAttribute("layoutSettings", layoutSettings);
    
    layoutSettings.put("companyName", uiLabelMap.get("PartyCompanyName"));
    layoutSettings.put("companySubtitle", uiLabelMap.get("PartyCompanySubtitle"));

    layoutSettings.put("headerImageUrl", "/images/ofbiz_logo.jpg");
    layoutSettings.put("headerMiddleBackgroundUrl", null);
    layoutSettings.put("headerRightBackgroundUrl", null);

    String externalLoginKey = LoginEvents.getExternalLoginKey(request);
    String externalKeyParam = externalLoginKey == null ? "" : "&externalLoginKey=" + externalLoginKey;
    request.setAttribute("externalKeyParam", externalKeyParam);
    request.setAttribute("externalLoginKey", externalLoginKey);
    request.setAttribute("activeApp", "partymgr");
%>
