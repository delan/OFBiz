<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" />
<%GenericValue userLogin = (GenericValue)session.getAttribute(SiteDefs.USER_LOGIN);%>
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<%String pageName = (String)pageContext.getAttribute("PageName"); %>

<%String companyName = "OFBiz: Catalog Manager";%>
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

<html>
<head>

<title><%=companyName%>: <%=pageName%></title>

<link rel="stylesheet" href="/commonapp/includes/maincss.css" type="text/css">
<%-- <%@ include file="/includes/maincss.jsp"%> --%>

<script language="javascript">
function mOvr(src,clrOver){ 
  if (!src.contains(event.fromElement)){ 
          src.style.cursor = 'hand';
          src.bgColor = clrOver; 
  } 
} 
function mOut(src,clrIn){ 
  if (!src.contains(event.toElement)){ 
    src.style.cursor = 'default'; 
    src.bgColor = clrIn; 
  } 
}
function mClk(src){ 
	if(event.srcElement.tagName=='TD')
		src.children.tags('A')[0].click();
}
</script>
</head>

<body>

<TABLE border=0 width='100%' cellpadding='<%=headerBoxBorderWidth%>' cellspacing='0' bgcolor='<%=headerBoxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width="100%" border="0" cellpadding='<%=headerBoxTopPadding%>' cellspacing="0" bgcolor="<%=headerBoxTopColor%>">
        <tr>
          <%if(headerImageUrl != null && headerImageUrl.length() > 0) {%>
            <TD align=left width='1%'>
              <IMG height='50' src='<%=headerImageUrl%>' alt='<%=UtilProperties.getPropertyValue("ecommerce", "company.name")%>'>
            </TD>
          <%}%>
          <TD align=left width='90%'>
            <%if(companyName != null && companyName.length() > 0) {%>
              <div class='headerCompanyName'><%=companyName%></div>
            <%}%>
            <%if(companySubtitle != null && companySubtitle.length() > 0) {%>
              <div class='headerCompanySubtitle'><%=companySubtitle%></div>
            <%}%>
          </TD>
          <TD align=right width='9%'>&nbsp;</td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border=0 cellpadding='<%=headerBoxBottomPadding%>' cellspacing=0 bgcolor='<%=headerBoxBottomColor%>'>
        <tr>
    <%
      String queryString = null;
      Enumeration parameterNames = request.getParameterNames();
      while(parameterNames != null && parameterNames.hasMoreElements())
      {
        String paramName = (String)parameterNames.nextElement();
        if(paramName != null)
        {
          if(queryString == null) queryString = paramName + "=" + request.getParameter(paramName);
          else queryString = queryString + "&" + paramName + "=" + request.getParameter(paramName);
        }
      }
      
      String loginUrl = controlPath + "/checkLogin/" + UtilFormatOut.checkNull((String)request.getAttribute(SiteDefs.CURRENT_VIEW));
      if(queryString != null) loginUrl = loginUrl  + "?" + UtilFormatOut.checkNull(queryString);
    %>
    <%if(userLogin==null){%>
      <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonLeft"><a href="<%=response.encodeURL(loginUrl)%>" class="buttontext">Login</a></td>
    <%}else{%>
      <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonLeft"><a href="<ofbiz:url>/logout/main</ofbiz:url>" class="buttontext">Logout</a></td>
    <%}%>
    <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonLeft"><a href="<ofbiz:url>/main</ofbiz:url>" class="buttontext">Main</a></td>
    <TD bgcolor="<%=headerBoxBottomColor%>" width="90%" align=center class='headerCenter'>Welcome!</TD>
    <%-- <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/sitemap</ofbiz:url>" class="buttontext">Site&nbsp;Map</a></td> --%>
  </TR>
</TABLE>
    </TD>
  </TR>
</TABLE>

<%@ include file='/includes/appbar.jsp' %>
