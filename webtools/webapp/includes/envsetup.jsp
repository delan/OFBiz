<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" />
<%GenericValue userLogin = (GenericValue)session.getAttribute(SiteDefs.USER_LOGIN);%>
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<%String pageName = UtilFormatOut.checkNull((String)pageContext.getAttribute("PageName"));%>

<%String companyName = "OFBiz: Core Web Tools";%>
<%String companySubtitle = "Part of the Open For Business Family of Open Source Software";%>
<%String headerImageUrl = null;%>

<%String headerBoxBorderColor = "black";%>
<%String headerBoxBorderWidth = "1";%>
<%String headerBoxTopColor = "#678475";%>
<%String headerBoxBottomColor = "#cccc99";%>
<%String headerBoxBottomColorAlt = "#eeeecc";%>
<%String headerBoxTopPadding = "4";%>
<%String headerBoxBottomPadding = "2";%>

<%String boxBorderColor = "black";%>
<%String boxBorderWidth = "1";%>
<%String boxTopColor = "#678475";%>
<%String boxBottomColor = "white";%>
<%String boxTopPadding = "4";%>
<%String boxBottomPadding = "4";%>

