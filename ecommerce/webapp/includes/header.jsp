
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*" %>
<%@ page import="java.util.*" %>

<script language="javascript">
function mClk(src){ 
	if(event.srcElement.tagName=='TD')
		src.children.tags('A')[0].click();
}
</script>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>
        <tr>
          <%EntityField.run("layoutSettings", "headerImageUrl", "<TD align=left width='1%'><IMG height='50' src='", "'></TD>", pageContext);%>
          <TD>&nbsp;&nbsp;</TD>
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
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxbottom'>
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
            <td onclick="mClk(this);" class="headerButtonLeft"><a href='<ofbiz:url><%=loginUrl%></ofbiz:url>' class='buttontext'>Login</a></td>
          </ofbiz:unless>
          <ofbiz:if name="userLogin">
            <td onclick="mClk(this);" class="headerButtonLeft"><a href="<ofbiz:url>/logout/main</ofbiz:url>" class="buttontext">Logout</a></td>
          </ofbiz:if>
          <td onclick="mClk(this);" class="headerButtonLeft"><a href="<ofbiz:url>/main</ofbiz:url>" class="buttontext">Main</a></td>
          <ofbiz:unless name="person">
            <TD width="90%" align=center class='headerCenter'>Welcome!</TD>
          </ofbiz:unless>
          <ofbiz:if name="person">
            <TD width="90%" align=center class='headerCenter'>Welcome<%EntityField.run("person", "firstName", "&nbsp;", "", pageContext);%><%EntityField.run("person", "lastName", "&nbsp;", "", pageContext);%>!</TD>
          </ofbiz:if>
          <%if(CatalogWorker.getCatalogQuickaddUse(pageContext)) {%>
            <td onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/quickadd</ofbiz:url>" class="buttontext">Quick&nbsp;Add</a></td>
          <%}%>
          <td onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/orderhistory</ofbiz:url>" class="buttontext">Order&nbsp;History</a></td>
          <td onclick="mClk(this);" class="headerButtonRight"><a href="<ofbiz:url>/viewprofile</ofbiz:url>" class="buttontext">Profile</a></td>
        </TR>
      </TABLE>
    </TD>
  </TR>
</TABLE>
