<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<%
    GenericValue userLogin = (GenericValue) session.getAttribute(SiteDefs.USER_LOGIN);
    if (userLogin != null)
        request.setAttribute("userLogin", userLogin);
    GenericValue person = (userLogin == null ? null : userLogin.getRelatedOne("Person"));
    if (person != null)
        request.setAttribute("person", person);

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

    layoutSettings.put("bodyTopMargin", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "body.topmargin"));
    layoutSettings.put("bodyLeftMargin", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "body.leftmargin"));
    layoutSettings.put("bodyRightMargin", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "body.rightmargin"));
    layoutSettings.put("bodyMarginHeight", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "body.marginheight"));
    layoutSettings.put("bodyMarginWidth", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "body.marginwidth"));

    layoutSettings.put("headerBoxBorderColor", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.border.color", "black"));
    layoutSettings.put("headerBoxBorderWidth", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.border.width", "1"));
    layoutSettings.put("headerBoxTopColor", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.top.color", "#678475"));
    layoutSettings.put("headerBoxBottomColor", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.bottom.color", "#cccc99"));
    layoutSettings.put("headerBoxBottomColorAlt", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.bottom.color.alt", "#eeeecc"));
    layoutSettings.put("headerBoxTopPadding", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.top.padding", "4"));
    layoutSettings.put("headerBoxBottomPadding", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.bottom.padding", "2"));

    layoutSettings.put("boxBorderColor", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.border.color", "black"));
    layoutSettings.put("boxBorderWidth", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.border.width", "1"));
    layoutSettings.put("boxTopColor", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.top.color", "#678475"));
    layoutSettings.put("boxBottomColor", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.bottom.color", "white"));
    layoutSettings.put("boxTopPadding", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.top.padding", "4"));
    layoutSettings.put("boxBottomPadding", UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.bottom.padding", "4"));
%>
