
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>
<%@ page import="java.util.*" %>

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

<TABLE border=0 width='100%' cellpadding='<%EntityField.run("layoutSettings", "headerBoxBorderWidth", pageContext);%>' cellspacing='0' bgcolor='<%EntityField.run("layoutSettings", "headerBoxBorderColor", pageContext);%>'>
  <TR>
    <TD width='100%'>
      <table width="100%" border="0" cellpadding='<%EntityField.run("layoutSettings", "headerBoxTopPadding", pageContext);%>' cellspacing="0" bgcolor="<%EntityField.run("layoutSettings", "headerBoxTopColor", pageContext);%>">
        <tr>
          <%EntityField.run("layoutSettings", "headerImageUrl", "<TD align=left width='1%'><IMG height='50' src='", "'></TD>", pageContext);%>
          <TD align=left width='98%' <%EntityField.run("layoutSettings", "headerMiddleBackgroundUrl", "background='", "'", pageContext);%>>
              <%EntityField.run("layoutSettings", "companyName", "<div class='headerCompanyName'>", "</div>", pageContext);%>
              <%EntityField.run("layoutSettings", "companySubtitle", "<div class='headerCompanySubtitle'>", "</div>", pageContext);%>
              &nbsp;
          </TD>
          <TD align=right width='1%' nowrap <%EntityField.run("layoutSettings", "headerRightBackgroundUrl", "background='", "'", pageContext);%>>
            <%@ include file="/cart/microcart.jsp"%>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border=0 cellpadding='<%EntityField.run("layoutSettings", "headerBoxBottomPadding", pageContext);%>' cellspacing=0 bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>'>
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

            String loginUrl = "/checkLogin/" + UtilFormatOut.checkNull((String)request.getAttribute(SiteDefs.CURRENT_VIEW));
            if(queryString != null) loginUrl = loginUrl  + "?" + UtilFormatOut.checkNull(queryString);
          %>
          <ofbiz:unless name="userLogin">
            <td bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' onmouseover='mOvr(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColorAlt", pageContext);%>');' onmouseout='mOut(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>');' onclick="mClk(this);" class="headerButtonLeft"><a href='<ofbiz:url><%=loginUrl%></ofbiz:url>' class='buttontext'>Login</a></td>
          </ofbiz:unless>
          <ofbiz:if name="userLogin">
            <td bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' onmouseover='mOvr(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColorAlt", pageContext);%>');' onmouseout='mOut(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>');' onclick="mClk(this);" class="headerButtonLeft"><a href="<ofbiz:url>/logout/main</ofbiz:url>" class="buttontext">Logout</a></td>
          </ofbiz:if>
          <td bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' onmouseover='mOvr(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColorAlt", pageContext);%>');' onmouseout='mOut(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>');' onclick="mClk(this);" class="headerButtonLeft"><a href="<ofbiz:url>/main</ofbiz:url>" class="buttontext">Main</a></td>
          <%-- <td bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' onmouseover='mOvr(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColorAlt", pageContext);%>');' onmouseout='mOut(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>');' onclick="mClk(this);" class="headerButtonLeft"><a href="<ofbiz:url>/catalog?catalog_id=<%=UtilProperties.getPropertyValue(application.getResource("/WEB-INF/ecommerce.properties"), "catalog.id.default")%></ofbiz:url>" class="buttontext">Catalog</a></td> --%>
          <ofbiz:unless name="person">
            <TD bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' width="90%" align=center class='headerCenter'>Welcome!</TD>
          </ofbiz:unless>
          <ofbiz:if name="person">
            <TD bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' width="90%" align=center class='headerCenter'>Welcome<%EntityField.run("person", "firstName", "&nbsp;", "", pageContext);%><%EntityField.run("person", "lastName", "&nbsp;", "", pageContext);%>!</TD>
          </ofbiz:if>
          <%if(CatalogWorker.getCatalogQuickaddUse(pageContext)) {%>
            <td bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' onmouseover='mOvr(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColorAlt", pageContext);%>');' onmouseout='mOut(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>');' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/quickadd</ofbiz:url>" class="buttontext">Quick&nbsp;Add</a></td>
          <%}%>
          <td bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' onmouseover='mOvr(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColorAlt", pageContext);%>');' onmouseout='mOut(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>');' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/orderhistory</ofbiz:url>" class="buttontext">Order&nbsp;History</a></td>
          <td bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' onmouseover='mOvr(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColorAlt", pageContext);%>');' onmouseout='mOut(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>');' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/viewprofile</ofbiz:url>" class="buttontext">Profile</a></td>
          <%-- <td bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' onmouseover='mOvr(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColorAlt", pageContext);%>');' onmouseout='mOut(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>');' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/view/showcart</ofbiz:url>" class="buttontext">ViewCart</a></td> --%>
          <%-- <td bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' onmouseover='mOvr(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColorAlt", pageContext);%>');' onmouseout='mOut(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>');' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/checkoutoptions</ofbiz:url>" class="buttontext">CheckOut</a></td> --%>
          <%-- <td bgcolor='<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>' onmouseover='mOvr(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColorAlt", pageContext);%>');' onmouseout='mOut(this,'<%EntityField.run("layoutSettings", "headerBoxBottomColor", pageContext);%>');' onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/sitemap</ofbiz:url>" class="buttontext">Site&nbsp;Map</a></td> --%>
        </TR>
      </TABLE>
    </TD>
  </TR>
</TABLE>
