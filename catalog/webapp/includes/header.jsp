
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="java.util.*" %>
<%Map layoutSettings = (Map) pageContext.findAttribute("layoutSettings");%>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>
        <tr>
          <%if (UtilValidate.isNotEmpty((String) layoutSettings.get("headerImageUrl"))) {%>
            <TD align=left width='1%'><IMG src='<ofbiz:contenturl><%=(String) layoutSettings.get("headerImageUrl")%></ofbiz:contenturl>'></TD>
          <%}%>
          <TD align=center width='98%' <%if (UtilValidate.isNotEmpty((String) layoutSettings.get("headerMiddleBackgroundUrl"))) {%>background='<ofbiz:contenturl><%=(String) layoutSettings.get("headerMiddleBackgroundUrl")%></ofbiz:contenturl>'<%}%>>
              <%EntityField.run("layoutSettings", "companyName", "<span class='headerCompanyName'>", "</span>", "&nbsp;", null, pageContext);%>
              <%EntityField.run("layoutSettings", "companySubtitle", "<br><span class='headerCompanySubtitle'>", "</span>", pageContext);%>
          </TD>
          <TD align=right width='1%' nowrap <%EntityField.run("layoutSettings", "headerRightBackgroundUrl", "background='", "'", pageContext);%>>
              <ofbiz:if name="person">
                <div class="insideHeaderText">Welcome<%EntityField.run("person", "firstName", "&nbsp;", "", pageContext);%><%EntityField.run("person", "lastName", "&nbsp;", "", pageContext);%>!</div>
              </ofbiz:if>
              <ofbiz:unless name="person">
                <ofbiz:if name="partyGroup">
                  <div class="insideHeaderText">Welcome<%EntityField.run("partyGroup", "groupName", "", "", pageContext);%>!</div>
                </ofbiz:if>
                <ofbiz:unless name="partyGroup">
                  <div class="insideHeaderText">Welcome!</div>
                </ofbiz:unless>
              </ofbiz:unless>
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
          <td class="headerButtonLeft"><a href="<ofbiz:url>/main</ofbiz:url>" class="buttontext">Main</a></td>
          <%--<td class="headerButtonLeft"><a href="<ofbiz:url>/FindProdCatalog</ofbiz:url>" class="buttontext">Catalogs</a></td>--%>
          <%--<td class="headerButtonLeft"><a href="<ofbiz:url>/main</ofbiz:url>" class="buttontext">Products</a></td>--%>
          <td class="headerButtonLeft"><a href="<ofbiz:url>/EditFeatureCategories</ofbiz:url>" class="buttontext">FeatureCats</a></td>
          <td class="headerButtonLeft"><a href="<ofbiz:url>/FindFacility</ofbiz:url>" class="buttontext">Facilities</a></td>
          <td class="headerButtonLeft"><a href="<ofbiz:url>/FindProductPromo</ofbiz:url>" class="buttontext">Promos</a></td>
          <td class="headerButtonLeft"><a href="<ofbiz:url>/FindProductPriceRules</ofbiz:url>" class="buttontext">PriceRules</a></td>

          <td width="90%" align=center class='headerCenter'>&nbsp;</td>

          <ofbiz:unless name="userLogin">
            <td class="headerButtonRight"><a href='<ofbiz:url><%=CommonWorkers.makeLoginUrl(pageContext)%></ofbiz:url>' class='buttontext'>Login</a></td>
          </ofbiz:unless>
          <ofbiz:if name="userLogin">
            <td class="headerButtonRight"><a href="<ofbiz:url>/logout/main</ofbiz:url>" class="buttontext">Logout</a></td>
          </ofbiz:if>
        </TR>
      </TABLE>
    </TD>
  </TR>
</TABLE>
