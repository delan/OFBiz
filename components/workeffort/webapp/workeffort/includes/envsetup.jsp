<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*, org.ofbiz.base.util.collections.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />
<%
    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
    if (userLogin != null) request.setAttribute("userLogin", userLogin);

    GenericValue person = (GenericValue) session.getAttribute("_PERSON_");
    if (person == null) {
        person = userLogin == null ? null : userLogin.getRelatedOne("Person");
        if (person != null) session.setAttribute("_PERSON_", person);
    }
    if (person != null) request.setAttribute("person", person);

    String controlPath = (String) request.getAttribute("_CONTROL_PATH_");
    String contextRoot = (String) request.getAttribute("_CONTEXT_ROOT_");
    String serverRoot = (String) request.getAttribute("_SERVER_ROOT_URL_");

	/* reading of the localization information */
	Collection availableLocales = UtilMisc.availableLocales();
	request.setAttribute("availableLocales",availableLocales);
	
	Locale locale = UtilHttp.getLocale(request);
	request.setAttribute("locale",locale);
    ResourceBundleMapWrapper uiLabelMap = (ResourceBundleMapWrapper) UtilProperties.getResourceBundleMap("WorkEffortUiLabels", locale);
    uiLabelMap.addBottomResourceBundle("CommonUiLabels");
	request.setAttribute("uiLabelMap", uiLabelMap);

    Map layoutSettings = new HashMap();
    request.setAttribute("layoutSettings", layoutSettings);
    
	layoutSettings.put("companyName", uiLabelMap.get("WorkEffortCompanyName"));
	layoutSettings.put("companySubtitle", uiLabelMap.get("WorkEffortCompanySubtitle"));
    layoutSettings.put("headerImageUrl", "/images/ofbiz_logo.jpg");
    layoutSettings.put("headerMiddleBackgroundUrl", null);
    layoutSettings.put("headerRightBackgroundUrl", null);
%>
