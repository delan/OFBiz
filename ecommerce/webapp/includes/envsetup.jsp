<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" />
<%GenericValue userLogin = (GenericValue)session.getAttribute(SiteDefs.USER_LOGIN);%>
<%GenericValue person = userLogin==null?null:userLogin.getRelatedOne("Person");%>
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%String contextRoot=(String)request.getAttribute(SiteDefs.CONTEXT_ROOT);%>
<%String serverRoot = (String)request.getAttribute(SiteDefs.SERVER_ROOT_URL);%>

<%String pageName = UtilFormatOut.checkNull((String)pageContext.getAttribute("PageName"));%>

<%URL ecommercePropertiesUrl = application.getResource("/WEB-INF/ecommerce.properties");%>

<%String companyName = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.name");%>
<%String companySubtitle = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "company.subtitle");%>
<%String headerImageUrl = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.image.url");%>

<%String headerBoxBorderColor = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.border.color", "black");%>
<%String headerBoxBorderWidth = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.border.width", "1");%>
<%String headerBoxTopColor = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.top.color", "#678475");%>
<%String headerBoxBottomColor = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.bottom.color", "#cccc99");%>
<%String headerBoxBottomColorAlt = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.bottom.color.alt", "#eeeecc");%>
<%String headerBoxTopPadding = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.top.padding", "4");%>
<%String headerBoxBottomPadding = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "header.box.bottom.padding", "2");%>

<%String boxBorderColor = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.border.color", "black");%>
<%String boxBorderWidth = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.border.width", "1");%>
<%String boxTopColor = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.top.color", "#678475");%>
<%String boxBottomColor = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.bottom.color", "white");%>
<%String boxTopPadding = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.top.padding", "4");%>
<%String boxBottomPadding = UtilProperties.getPropertyValue(ecommercePropertiesUrl, "box.bottom.padding", "4");%>
