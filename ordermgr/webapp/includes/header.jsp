
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<div class="apptitle">&nbsp;Order Manager Application&nbsp;</div>
<div class="row">
  <div class="col"><a href="<ofbiz:url>/main</ofbiz:url>" class="headerButtonLeft">Main</a></div>
  <%if(security.hasRolePermission("ORDERMGR", "_VIEW", "", "", session)) {%>
  <div class="col"><a href="<ofbiz:url>/tasklist</ofbiz:url>" class="headerButtonLeft">Order&nbsp;List</a></div>
  <%}%>
  <%if(security.hasEntityPermission("ORDERMGR", "_VIEW", session)) {%>
  <div class="col"><a href="<ofbiz:url>/orderlist</ofbiz:url>" class="headerButtonLeft">Find&nbsp;Orders</a></div>
  <%}%>
  <%if(security.hasEntityPermission("ORDERMGR", "_CREATE", session)) {%>
  <div class="col"><a href="<ofbiz:url>/salesentry</ofbiz:url>" class="headerButtonLeft">Sale&nbsp;Entry</a></div>
  <%--<div class="col"><a href="#" class="headerButtonLeft">Purchase&nbsp;Entry</a></div>--%>
  <%}%>
  <div class="col"><a href="<ofbiz:url>/orderreportlist</ofbiz:url>" class="headerButtonLeft">Order&nbsp;Reports</a></div>                 
  <ofbiz:unless name="userLogin">
    <div class="col-right"><a href='<ofbiz:url><%=CommonWorkers.makeLoginUrl(pageContext)%></ofbiz:url>' class='headerButtonRight'>Login</a></div>
  </ofbiz:unless>
  <ofbiz:if name="userLogin">
    <div class="col-right"><a href="<ofbiz:url>/logout</ofbiz:url>" class="headerButtonRight">Logout</a></div>
  </ofbiz:if>  
  <div class="col-right"><a href='<ofbiz:url>/shipsetup</ofbiz:url>' class="headerButtonRight">Setup</a></div>
  <div class="col-fill">&nbsp;</div>
</div>
