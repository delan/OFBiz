
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.ecommerce.catalog.*, org.ofbiz.commonapp.common.*" %>
<%@ page import="java.util.*" %>
<%Map layoutSettings = (Map) pageContext.findAttribute("layoutSettings");%>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>
        <tr>
          <%if (UtilValidate.isNotEmpty((String) layoutSettings.get("headerImageUrl"))) {%>
            <TD align=left width='1%'><IMG height='50' src='<ofbiz:contenturl><%=(String) layoutSettings.get("headerImageUrl")%></ofbiz:contenturl>'></TD>
          <%}%>
          <TD>&nbsp;&nbsp;</TD>
          <TD align=left width='98%' <%if (UtilValidate.isNotEmpty((String) layoutSettings.get("headerMiddleBackgroundUrl"))) {%>background='<ofbiz:contenturl><%=(String) layoutSettings.get("headerMiddleBackgroundUrl")%></ofbiz:contenturl>'<%}%>>
              <%EntityField.run("layoutSettings", "companyName", "<div class='headerCompanyName'>", "</div>", pageContext);%>
              <%EntityField.run("layoutSettings", "companySubtitle", "<div class='headerCompanySubtitle'>", "</div>", pageContext);%>
              &nbsp;
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
            <td class="headerButtonLeft"><a href='<ofbiz:url><%=CommonWorkers.makeLoginUrl(pageContext)%></ofbiz:url>' class='buttontext'>Login</a></td>
          </ofbiz:unless>
          <ofbiz:if name="userLogin">
            <td class="headerButtonLeft"><a href="<ofbiz:url>/logout/main</ofbiz:url>" class="buttontext">Logout</a></td>
          </ofbiz:if>
          <td class="headerButtonLeft"><a href="<ofbiz:url>/main</ofbiz:url>" class="buttontext">Main</a></td>
          <ofbiz:unless name="person">
            <TD width="90%" align=center class='headerCenter'>Welcome!</TD>
          </ofbiz:unless>
          <ofbiz:if name="person">
            <TD width="90%" align=center class='headerCenter'>Welcome<%EntityField.run("person", "firstName", "&nbsp;", "", pageContext);%><%EntityField.run("person", "lastName", "&nbsp;", "", pageContext);%>!</TD>
          </ofbiz:if>
          <%if(CatalogWorker.getCatalogQuickaddUse(pageContext)) {%>
            <td class="headerButtonRight"><a href="<ofbiz:url>/quickadd</ofbiz:url>" class="buttontext">Quick&nbsp;Add</a></td>
          <%}%>
          <td class="headerButtonRight"><a href="<ofbiz:url>/orderhistory</ofbiz:url>" class="buttontext">Order&nbsp;History</a></td>
          <td class="headerButtonRight"><a href="<ofbiz:url>/viewprofile</ofbiz:url>" class="buttontext">Profile</a></td>
        </TR>
      </TABLE>
    </TD>
  </TR>
</TABLE>
