
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<div class="apptitle">&nbsp;Party Manager Application&nbsp;</div>
<div class="row">
  <div class="col"><a href="<ofbiz:url>/main</ofbiz:url>" class="headerButtonLeft">Main</a></div>   
  <div class="col"><a href="<ofbiz:url>/findparty</ofbiz:url>" class="headerButtonLeft">Find&nbsp;Party</a></div>
  <%if(security.hasEntityPermission("PARTYMGR", "_CREATE", session)) {%>
  <div class="col"><a href="<ofbiz:url>/editpartygroup?create_new=Y</ofbiz:url>" class="headerButtonLeft">New&nbsp;Group</a></div>
  <%}%>
  <%if(security.hasEntityPermission("PARTYMGR", "_CREATE", session)) {%>
  <div class="col"><a href="<ofbiz:url>/editperson?create_new=Y</ofbiz:url>" class="headerButtonLeft">New&nbsp;Person</a></div>
  <%}%>
  <%if(security.hasEntityPermission("PARTYMGR", "_CREATE", session)) {%>
  <div class="col"><a href="<ofbiz:url>/newcustomer</ofbiz:url>" class="headerButtonLeft">New&nbsp;Customer</a></div>
  <%}%>
  <div class="col"><a href="<ofbiz:url>/showvisits</ofbiz:url>" class="headerButtonLeft">Visits</a></div>
  <%if(security.hasEntityPermission("SECURITY", "_VIEW", session)) {%>
  <div class="col"><a href="<ofbiz:url>/FindSecurityGroup</ofbiz:url>" class="headerButtonLeft">Security</a></div>
  <%}%>    
  <ofbiz:unless name="userLogin">
    <div class="col-right"><a href='<ofbiz:url><%=CommonWorkers.makeLoginUrl(pageContext)%></ofbiz:url>' class='headerButtonRight'>Login</a></div>
  </ofbiz:unless>
  <ofbiz:if name="userLogin">
    <div class="col-right"><a href="<ofbiz:url>/logout</ofbiz:url>" class="headerButtonRight">Logout</a></div>
  </ofbiz:if>  
  <div class="col-fill">&nbsp;</div>
</div>

