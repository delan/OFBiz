
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="java.util.*" %>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>
        <tr>
          <%EntityField.run("layoutSettings", "headerImageUrl", "<TD align=left width='1%'><IMG height='50' src='", "'></TD>", pageContext);%>
          <TD>&nbsp;&nbsp;</TD>
          <TD align=left width='70%' <%EntityField.run("layoutSettings", "headerMiddleBackgroundUrl", "background='", "'", pageContext);%>>
              <%EntityField.run("layoutSettings", "companyName", "<div class='headerCompanyName'>", "</div>", pageContext);%>
              <%EntityField.run("layoutSettings", "companySubtitle", "<div class='headerCompanySubtitle'>", "</div>", pageContext);%>
              &nbsp;
          </TD>
          <TD align=right width='30%' nowrap <%EntityField.run("layoutSettings", "headerRightBackgroundUrl", "background='", "'", pageContext);%>>
            <div class="insideHeaderText">&nbsp;<%=UtilDateTime.nowTimestamp().toString()%></div>
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
          <%-- <td class="headerButtonLeft"><a href="<ofbiz:url>/main</ofbiz:url>" class="buttontext">Main</a></td> --%>
          <ofbiz:unless name="person">
            <TD width="90%" align=center class='headerCenter'>Welcome!</TD>
          </ofbiz:unless>
          <ofbiz:if name="person">
            <TD width="90%" align=center class='headerCenter'>Welcome<%EntityField.run("person", "firstName", "&nbsp;", "", pageContext);%><%EntityField.run("person", "lastName", "&nbsp;", "", pageContext);%>!</TD>
          </ofbiz:if>
          <%-- <td class="headerButtonRight"><a href="#" class="buttontext">WebSites</a></td> --%>
          <td class="headerButtonRight"><a href="<ofbiz:url>/FindProdCatalog</ofbiz:url>" class="buttontext">Catalogs</a></td>
          <td class="headerButtonRight"><a href="<ofbiz:url>/main</ofbiz:url>" class="buttontext">Products</a></td>
          <td class="headerButtonRight"><a href="<ofbiz:url>/EditFeatureCategories</ofbiz:url>" class="buttontext">FeatureCats</a></td>
          <td class="headerButtonRight"><a href="<ofbiz:url>/FindFacility</ofbiz:url>" class="buttontext">Facilities</a></td>
          <td class="headerButtonRight"><a href="<ofbiz:url>/FindProductPromo</ofbiz:url>" class="buttontext">Promos</a></td>
          <td class="headerButtonRight"><a href="<ofbiz:url>/FindProductPriceRules</ofbiz:url>" class="buttontext">PriceRules</a></td>
        </TR>
      </TABLE>
    </TD>
  </TR>
</TABLE>
