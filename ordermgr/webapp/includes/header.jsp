<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" />
<jsp:useBean id="helper" type="org.ofbiz.core.entity.GenericHelper" scope="application" />
<%GenericValue userLogin = (GenericValue)session.getAttribute(SiteDefs.USER_LOGIN);%>
<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<%String pageName = (String)pageContext.getAttribute("PageName"); %>
<html>
<head>

<title>Open For Business: <%=pageName%></title>

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
<%GenericValue headerUserLogin = (GenericValue)session.getAttribute(SiteDefs.USER_LOGIN);%>
<%String headerControlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>

<TABLE width='100%' cellpadding='4' cellspacing='1' border='0' bgcolor='black'>
  <TR>
    <TD bgcolor='#678475'>
      <TABLE width='100%' cellpadding='0' cellspacing='0' border='0'>
        <TR>
          <TD align=left width='99%' >
            <div  style="margin: 0; font-size: 18pt; font-weight: bold; color: white;">OFBIZ - Common Application Components</div>
            <div style="FONT-SIZE: 8pt; margin: 0; color: white;">Part&nbsp;of&nbsp;the&nbsp;Open&nbsp;For&nbsp;Business&nbsp;Family&nbsp;of&nbsp;Open&nbsp;Source&nbsp;Software</font></div>
          </TD>
          <TD align=right width='1%'>&nbsp;</TD>
        </TR>
      </TABLE>
    </TD>
  </TR>
  <TR>
    <TD bgcolor='#cccc99'>
      <TABLE width='100%' cellpadding='0' cellspacing='0' border='0'>
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
      
      String loginUrl = headerControlPath + "/checkLogin/" + UtilFormatOut.checkNull((String)request.getAttribute(SiteDefs.CURRENT_VIEW));
      if(queryString != null) loginUrl = loginUrl  + "?" + UtilFormatOut.checkNull(queryString);
    %>
    <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-right:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(headerControlPath + "/main")%>" class="buttontext">Main</a></td>
    <%if(headerUserLogin==null){%>
      <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-right:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(loginUrl)%>" class="buttontext">Login</a></td>
    <%}else{%>
      <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-right:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(headerControlPath + "/logout/main")%>" class="buttontext">Logout</a></td>
    <%}%>
    <TD bgcolor="#cccc99" width="90%">&nbsp;</TD>
    <%-- <td bgcolor="#cccc99" onmouseover='mOvr(this,"#eeeecc");' onmouseout='mOut(this,"#cccc99");' onclick="mClk(this);" style="border-left:solid white 2px;padding-right:10px;padding-left:10px;"><a href="<%=response.encodeURL(headerControlPath + "/sitemap")%>" class="buttontext">Site&nbsp;Map</a></td> --%>
  </TR>
</TABLE>
    </TD>
  </TR>
</TABLE>
