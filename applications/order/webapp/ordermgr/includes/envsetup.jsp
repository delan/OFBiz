<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.webapp.pseudotag.*, org.ofbiz.webapp.control.*" %>
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

    Map layoutSettings = new HashMap();
    request.setAttribute("layoutSettings", layoutSettings);
    
    layoutSettings.put("companyName", "OFBiz: Order Manager");
    layoutSettings.put("companySubtitle", "Part of the Open For Business Family of Open Source Software");
    layoutSettings.put("headerImageUrl", "/images/ofbiz_logo.jpg");
    layoutSettings.put("headerMiddleBackgroundUrl", null);
    layoutSettings.put("headerRightBackgroundUrl", null);

String externalLoginKey = LoginWorker.getExternalLoginKey(request);
String externalKeyParam = externalLoginKey == null ? "" : "&externalLoginKey=" + externalLoginKey;
request.setAttribute("externalKeyParam", externalKeyParam);
request.setAttribute("externalLoginKey", externalLoginKey);
request.setAttribute("activeApp", "ordermgr");

    List eventMessageList = (List) request.getAttribute("eventMessageList");
    if (eventMessageList == null) eventMessageList = new LinkedList();
    List errorMessageList = (List) request.getAttribute("errorMessageList");
    if (errorMessageList == null) errorMessageList = new LinkedList();

    if (request.getAttribute("_EVENT_MESSAGE_") != null) {
        eventMessageList.add(UtilFormatOut.replaceString((String) request.getAttribute("_EVENT_MESSAGE_"), "\n", "<br/>"));
        request.removeAttribute("_EVENT_MESSAGE_");
    }
    if (request.getAttribute("_EVENT_MESSAGE_LIST_") != null) {
        eventMessageList.addAll((List) request.getAttribute("_EVENT_MESSAGE_LIST_"));
        request.removeAttribute("_EVENT_MESSAGE_LIST_");
    }
    if (request.getAttribute("_ERROR_MESSAGE_") != null) {
        errorMessageList.add(UtilFormatOut.replaceString((String) request.getAttribute("_ERROR_MESSAGE_"), "\n", "<br/>"));
        request.removeAttribute("_ERROR_MESSAGE_");
    }
    if (session.getAttribute("_ERROR_MESSAGE_") != null) {
        errorMessageList.add(UtilFormatOut.replaceString((String) session.getAttribute("_ERROR_MESSAGE_"), "\n", "<br/>"));
        session.removeAttribute("_ERROR_MESSAGE_");
    }
    if (request.getAttribute("_ERROR_MESSAGE_LIST_") != null) {
        errorMessageList.addAll((List) request.getAttribute("_ERROR_MESSAGE_LIST_"));
        request.removeAttribute("_ERROR_MESSAGE_LIST_");
    }
    request.setAttribute("eventMessageList", eventMessageList);
    request.setAttribute("errorMessageList", errorMessageList);
%>
