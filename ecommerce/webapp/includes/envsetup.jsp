<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" />
<%GenericValue userLogin = (GenericValue)session.getAttribute(SiteDefs.USER_LOGIN);%>
<%GenericValue person = userLogin==null?null:userLogin.getRelatedOne("Person");%>
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%String contextRoot=(String)request.getAttribute(SiteDefs.CONTEXT_ROOT);%>
<%String serverRoot = (String)request.getAttribute(SiteDefs.SERVER_ROOT_URL);%>

<%String pageName = UtilFormatOut.checkNull((String)pageContext.getAttribute("PageName"));%>

<%String companyName = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "company.name");%>
<%String companySubtitle = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "company.subtitle");%>
<%String headerImageUrl = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "header.image.url");%>

<%String headerBoxBorderColor = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "header.box.border.color", "black");%>
<%String headerBoxBorderWidth = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "header.box.border.width", "1");%>
<%String headerBoxTopColor = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "header.box.top.color", "#678475");%>
<%String headerBoxBottomColor = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "header.box.bottom.color", "#cccc99");%>
<%String headerBoxBottomColorAlt = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "header.box.bottom.color.alt", "#eeeecc");%>
<%String headerBoxTopPadding = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "header.box.top.padding", "4");%>
<%String headerBoxBottomPadding = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "header.box.bottom.padding", "2");%>

<%String boxBorderColor = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "box.border.color", "black");%>
<%String boxBorderWidth = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "box.border.width", "1");%>
<%String boxTopColor = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "box.top.color", "#678475");%>
<%String boxBottomColor = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "box.bottom.color", "white");%>
<%String boxTopPadding = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "box.top.padding", "4");%>
<%String boxBottomPadding = UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "box.bottom.padding", "4");%>
