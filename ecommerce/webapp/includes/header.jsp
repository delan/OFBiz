
<html>
<head>

<title><%=companyName%>: <%=pageName%></title>

<%-- <link rel="stylesheet" href="<%=controlPath + "/view/includes/maincss.css"%>" type="text/css"> --%>
<link rel="stylesheet" href="/ecommerce/includes/maincss.css" type="text/css">
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
              <IMG height='50' src='<%=headerImageUrl%>' alt='<%=companyName%>'>
            </TD>
          <%}%>
          <TD align=left>
            <%if(companyName != null && companyName.length() > 0) {%>
              <div class='headerCompanyName'><%=companyName%></div>
            <%}%>
            <%if(companySubtitle != null && companySubtitle.length() > 0) {%>
              <div class='headerCompanySubtitle'><%=companySubtitle%></div>
            <%}%>
          </TD>
          <TD align=right>
<%@ include file="/cart/microcart.jsp"%>
          </td>
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
          <%-- <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonLeft"><a href="<ofbiz:url>/catalog?catalog_id=<%=UtilProperties.getPropertyValue(contextRoot + "/WEB-INF/ecommerce.properties", "catalog.id.default")%></ofbiz:url>" class="buttontext">Catalog</a></td> --%>
          <%if(person==null){%>
            <TD bgcolor="<%=headerBoxBottomColor%>" width="90%" align=center class='headerCenter'>Welcome!</TD>
          <%}else{%>
            <TD bgcolor="<%=headerBoxBottomColor%>" width="90%" align=center class='headerCenter'>Welcome<%=UtilFormatOut.ifNotEmpty(person.getString("firstName"),"&nbsp;","")%><%=UtilFormatOut.ifNotEmpty(person.getString("lastName"),"&nbsp;","")%>!</TD>
          <%}%>
          <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/orderhistory</ofbiz:url>" class="buttontext">Order&nbsp;History</a></td>
          <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/viewprofile</ofbiz:url>" class="buttontext">Profile</a></td>
          <%-- <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/view/showcart</ofbiz:url>" class="buttontext">ViewCart</a></td> --%>
          <%-- <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="buttontext">CheckOut</a></td> --%>
          <%-- <td bgcolor="<%=headerBoxBottomColor%>" onmouseover='mOvr(this,"<%=headerBoxBottomColorAlt%>");' onmouseout='mOut(this,"<%=headerBoxBottomColor%>");' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/sitemap</ofbiz:url>" class="buttontext">Site&nbsp;Map</a></td> --%>
        </TR>
      </TABLE>
    </TD>
  </TR>
</TABLE>
