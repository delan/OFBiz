
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxtop'>
        <tr>
          <%EntityField.run("layoutSettings", "headerImageUrl", "<TD align=left width='1%'><IMG height='50' src='", "'></TD><TD>&nbsp;&nbsp;</TD>", pageContext);%>
          <TD align=left width='70%' <%EntityField.run("layoutSettings", "headerMiddleBackgroundUrl", "background='", "'", pageContext);%>>
              <%EntityField.run("layoutSettings", "companyName", "<span class='headerCompanyName'>", "</span>", "&nbsp;", null, pageContext);%>
              <%EntityField.run("layoutSettings", "companySubtitle", "<br><span class='headerCompanySubtitle'>", "</span>", pageContext);%>
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
          <td class="headerButtonLeft"><a href="<ofbiz:url>/main</ofbiz:url>" class="buttontext">Main</a></td>

          <ofbiz:if name="person">
            <TD width="90%" align=center class='headerCenter'>Welcome<%EntityField.run("person", "firstName", "&nbsp;", "", pageContext);%><%EntityField.run("person", "lastName", "&nbsp;", "", pageContext);%>!</TD>
          </ofbiz:if>
          <ofbiz:unless name="person">
            <ofbiz:if name="partyGroup">
              <TD width="90%" align=center class='headerCenter'>Welcome<%EntityField.run("partyGroup", "groupName", "", "", pageContext);%>!</TD>
            </ofbiz:if>
            <ofbiz:unless name="partyGroup">
              <TD width="90%" align=center class='headerCenter'>Welcome!</TD>
            </ofbiz:unless>
          </ofbiz:unless>

          <td class="headerButtonRight"><a href="<ofbiz:url>/orderlist</ofbiz:url>" class="buttontext">Order&nbsp;List</a></td>
          <%if(security.hasEntityPermission("SHIPRATE", "_VIEW", session)) {%>
          <td class="headerButtonRight"><a href="<ofbiz:url>/shipsetup</ofbiz:url>" class="buttontext">Ship&nbsp;Rates</a></td>
          <%}%>
        </TR>
      </TABLE>
    </TD>
  </TR>
</TABLE>
