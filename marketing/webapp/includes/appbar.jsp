<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<ofbiz:if name="userLogin">
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxbottom' style='border-width: 1px 1px 0px 1px;'>
        <tr>
          <%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>
            <td class="headerButtonLeft"><a href="javascript:document.partyform.submit()" class="headerbuttontext">Party</a></td>
          <%}%>
          <%if(security.hasEntityPermission("MARKETING", "_VIEW", session)) {%>
            <td class="headerButtonLeft"><a href="javascript:document.marketingform.submit()" class="headerbuttontext">Marketing</a></td>
          <%}%>
          <%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
            <td class="headerButtonLeft"><a href="javascript:document.catalogform.submit()" class="headerbuttontext">Catalog</a></td>
          <%}%>
          <%if(security.hasEntityPermission("FACILITY", "_VIEW", session)) {%>
            <td class="headerButtonLeft"><a href="javascript:document.facilityform.submit()" class="headerbuttontext">Facility</a></td>
          <%}%>
          <%if(security.hasEntityPermission("ORDERMGR", "_VIEW", session)) {%>
            <td class="headerButtonLeft"><a href="javascript:document.orderform.submit()" class="headerbuttontext">Order</a></td>
          <%}%>
          <%if(security.hasEntityPermission("ACCOUNTING", "_VIEW", session)) {%>
            <td class="headerButtonLeft"><a href="javascript:document.accountingform.submit()" class="headerbuttontext">Accounting</a></td>
          <%}%>
          <%if(security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", session)) {%>
            <td class="headerButtonLeft"><a href="javascript:document.workeffortform.submit()" class="headerbuttontext">WorkEffort</a></td>
          <%}%>
          <%if(security.hasEntityPermission("CONTENTMGR", "_VIEW", session)) {%>
            <td class="headerButtonLeft"><a href="javascript:document.contentform.submit()" class="headerbuttontext">Content</a></td>
          <%}%>
          <td class="headerButtonLeft"><a href="javascript:document.webtoolsform.submit()" class="headerbuttontext">WebTools</a></td>
          <%--
          <ofbiz:if name="person">
            <TD width="90%" align=right class='headerCenter'>Welcome<%EntityField.run("person", "firstName", "&nbsp;", "", pageContext);%><%EntityField.run("person", "lastName", "&nbsp;", "", pageContext);%>!</TD>
          </ofbiz:if>
          <ofbiz:unless name="person">
            <ofbiz:if name="partyGroup">
              <TD width="90%" align=right class='headerCenter'>Welcome<%EntityField.run("partyGroup", "groupName", "", "", pageContext);%>!</TD>
            </ofbiz:if>
            <ofbiz:unless name="partyGroup">
              <TD width="90%" align=right class='headerCenter'>Welcome!</TD>
            </ofbiz:unless>
          </ofbiz:unless>
          --%>
          <TD width="90%" align=right class='headerCenter'>&nbsp;</TD>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

  <form method="POST" target="webtools" action="<%=response.encodeURL("/webtools/control/login/main")%>" name="webtoolsform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" target="workeffort" action="<%=response.encodeURL("/workeffort/control/login/main")%>" name="workeffortform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" target="catalog" action="<%=response.encodeURL("/catalog/control/login/main")%>" name="catalogform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" target="partymgr" action="<%=response.encodeURL("/partymgr/control/login/main")%>" name="partyform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" target="ordermgr" action="<%=response.encodeURL("/ordermgr/control/login/main")%>" name="orderform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" target="accounting" action="<%=response.encodeURL("/accounting/control/login/main")%>" name="accountingform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" target="facility" action="<%=response.encodeURL("/facility/control/login/main")%>" name="facilityform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" target="marketing" action="<%=response.encodeURL("/marketing/control/login/main")%>" name="marketingform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" target="content" action="<%=response.encodeURL("/content/control/login/main")%>" name="contentform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
</ofbiz:if>

<%-- NOTE: this is necessary so that when clicking on an app-bar link in another window the selected one will pop to the front --%>
<script language="JavaScript">
	window.focus();
</script>
