<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<ofbiz:if name="userLogin">
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='headerboxoutside'>
<%--
  <TR>
    <TD width='100%'>
      <table width="100%" border="0" cellpadding="4" cellspacing="0" bgcolor="#678475">
        <tr>
          <TD align=left width='99%' >
            <div  style="margin: 0; font-size: 12pt; font-weight: bold; color: white;">OFBIZ - Application QuickLinks</div>
          </TD>
          <TD align=right width='1%'>&nbsp;</TD>
        </tr>
      </table>
    </TD>
  </TR>
--%>  
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='headerboxbottom' style='border-width: 1px 1px 0px 1px;'>
        <tr>
          <td class="headerButtonLeft"><a href="javascript:document.webtoolsform.submit()" class="buttontext">WebTools</a></td>
          <TD width="90%" align=center class='headerCenter'>App Links</TD>
          <%if(security.hasEntityPermission("WORKEFFORTMGR", "_VIEW", session)) {%>
            <td class="headerButtonRight"><a href="javascript:document.workeffortform.submit()" class="buttontext">WorkEffort</a></td>
          <%}%>
          <%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
            <td class="headerButtonRight"><a href="javascript:document.catalogform.submit()" class="buttontext">Catalog</a></td>
          <%}%>
          <%if(security.hasEntityPermission("PARTYMGR", "_VIEW", session)) {%>
            <td class="headerButtonRight"><a href="javascript:document.partyform.submit()" class="buttontext">Party</a></td>
          <%}%>
          <%if(security.hasEntityPermission("ORDERMGR", "_VIEW", session)) {%>
            <td class="headerButtonRight"><a href="javascript:document.orderform.submit()" class="buttontext">Order</a></td>
          <%}%>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

  <form method="POST" action="<%=response.encodeURL("/webtools/control/login/main")%>" name="webtoolsform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" action="<%=response.encodeURL("/workeffort/control/login/main")%>" name="workeffortform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" action="<%=response.encodeURL("/catalog/control/login/main")%>" name="catalogform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" action="<%=response.encodeURL("/partymgr/control/login/main")%>" name="partyform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
  <form method="POST" action="<%=response.encodeURL("/ordermgr/control/login/main")%>" name="orderform" style='margin: 0;'>
    <input type="hidden" name="USERNAME" value="<%EntityField.run("userLogin", "userLoginId", pageContext);%>">
    <input type="hidden" name="PASSWORD" value="<%EntityField.run("userLogin", "currentPassword", pageContext);%>">
  </form>
</ofbiz:if>
