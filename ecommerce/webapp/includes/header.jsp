<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" />
<jsp:useBean id="helper" type="org.ofbiz.core.entity.GenericHelper" scope="application" />
<%GenericValue userLogin = (GenericValue)session.getAttribute(SiteDefs.USER_LOGIN);%>
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<%String pageName = (String)pageContext.getAttribute("PageName");%>
<html>
<head>

<title>Open For Business: <%=pageName%></title>

<link rel="stylesheet" href="/commonapp/includes/maincss.css" type="text/css">
<%-- <%@ include file="/includes/maincss.jsp"%> --%>
</head>

<body>
<TABLE width='100%' cellpadding='0' cellspacing='0' border='0' bgcolor='CCCCCC'>
  <TR>
    <TD>
      <H1 style="margin:0;">&nbsp;The Open For Business Project</H1>
    </TD>
  </TR>
</TABLE>
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
<TABLE width='100%' cellpadding='0' cellspacing='0' border='0' bgcolor='CCCCCC'>
  <TR>
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
      
      String loginUrl = controlPath + "/checkLogin/" + UtilFormatOut.checkNull((String)session.getAttribute(SiteDefs.CURRENT_VIEW));
      if(queryString != null) loginUrl = loginUrl  + "?" + UtilFormatOut.checkNull(queryString);
    %>
    <%if(userLogin==null){%>
      <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-right:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(loginUrl)%>" class="buttontext">Login</a></td>
    <%}else{%>
      <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-right:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(controlPath + "/logout/main")%>" class="buttontext">Logout</a></td>
    <%}%>
    <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-right:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(controlPath + "/main")%>" class="buttontext">Main</a></td>
    <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-right:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(controlPath + "/catalog?catalog_id=CATALOG1")%>" class="buttontext">Catalog</a></td>
    <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-right:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(controlPath + "/orderhistory")%>" class="buttontext">Order&nbsp;History</a></td>
    <TD bgcolor="#cccc99" width="90%">&nbsp;</TD>
    <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-left:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(controlPath + "/viewprofile")%>" class="buttontext">Profile</a></td>
    <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-left:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(controlPath + "/view/showcart")%>" class="buttontext">ViewCart</a></td>
    <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-left:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(controlPath + "/shippingAddress")%>" class="buttontext">CheckOut</a></td>
    <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-left:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(controlPath + "/sitemap")%>" class="buttontext">Site&nbsp;Map</a></td>
  </TR>
</TABLE>
