
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.product.catalog.*, org.ofbiz.commonapp.common.*" %>
<%@ page import="java.util.*" %>
<%Map layoutSettings = (Map) pageContext.findAttribute("layoutSettings");%>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>
        <tr>
		  <%if (session.getAttribute("overrideLogo") != null) {%>
            <TD align=left width='1%'><IMG src='<%=session.getAttribute("overrideLogo")%>'></TD> 
          <%} else if (CatalogWorker.getProdCatalog(pageContext) != null && CatalogWorker.getProdCatalog(pageContext).get("headerLogo") != null) {%>       
          	<TD align=left width='1%'><IMG src='<%=CatalogWorker.getProdCatalog(pageContext).getString("headerLogo")%>'></TD> 
          <%} else if (UtilValidate.isNotEmpty((String) layoutSettings.get("headerImageUrl"))) {%>
            <TD align=left width='1%'><IMG src='<ofbiz:contenturl><%=(String) layoutSettings.get("headerImageUrl")%></ofbiz:contenturl>'></TD>
          <%}%>
          <TD align=center width='98%' <%if (UtilValidate.isNotEmpty((String) layoutSettings.get("headerMiddleBackgroundUrl"))) {%>background='<ofbiz:contenturl><%=(String) layoutSettings.get("headerMiddleBackgroundUrl")%></ofbiz:contenturl>'<%}%>>
              <%EntityField.run("layoutSettings", "companyName", "<span class='headerCompanyName'>", "</span>", "&nbsp;", null, pageContext);%>
              <%EntityField.run("layoutSettings", "companySubtitle", "<br><span class='headerCompanySubtitle'>", "</span>", pageContext);%>
          </TD>
          <TD align=right width='1%' nowrap <%if (UtilValidate.isNotEmpty((String) layoutSettings.get("headerRightBackgroundUrl"))) {%>background='<ofbiz:contenturl><%=(String) layoutSettings.get("headerRightBackgroundUrl")%></ofbiz:contenturl>'<%}%>>
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
          <ofbiz:unless name="userLogin">
            <td class="headerButtonLeft"><a href='<ofbiz:url><%=CommonWorkers.makeLoginUrl(pageContext, "checkLogin")%></ofbiz:url>' class='headerbuttontext'>Login</a></td>
          </ofbiz:unless>
          <ofbiz:if name="userLogin">
            <td class="headerButtonLeft"><a href="<ofbiz:url>/logout</ofbiz:url>" class="headerbuttontext">Logout</a></td>
          </ofbiz:if>
          <td class="headerButtonLeft"><a href="<ofbiz:url>/main</ofbiz:url>" class="headerbuttontext">Main</a></td>

          <ofbiz:if name="autoName">
            <TD width="90%" align="center" class="headerCenter">
                Welcome&nbsp;<ofbiz:print attribute="autoName"/>!
                (Not&nbsp;You?&nbsp;<a href="<ofbiz:url>/autoLogout</ofbiz:url>" class="buttontext">click&nbsp;here</a>)
            </TD>
          </ofbiz:if>
          <ofbiz:unless name="autoName">
              <TD width="90%" align=center class='headerCenter'>Welcome!</TD>
          </ofbiz:unless>

          <%if(CatalogWorker.getCatalogQuickaddUse(pageContext)) {%>
            <td class="headerButtonRight"><a href="<ofbiz:url>/quickadd</ofbiz:url>" class="headerbuttontext">Quick&nbsp;Add</a></td>
          <%}%>
          <td class="headerButtonRight"><a href="<ofbiz:url>/orderhistory</ofbiz:url>" class="headerbuttontext">Order&nbsp;History</a></td>
          <td class="headerButtonRight"><a href="<ofbiz:url>/viewprofile</ofbiz:url>" class="headerbuttontext">Profile</a></td>
        </TR>
      </TABLE>
    </TD>
  </TR>
</TABLE>
