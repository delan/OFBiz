
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />

<div class="apptitle">&nbsp;WorkEfforts/Projects/Requests&nbsp;</div>
<div class="row">
  <div class="col"><a href="<ofbiz:url>/main</ofbiz:url>" class="headerButtonLeft">Main</a></div>   
  <div class="col"><a href="<ofbiz:url>/mytasks</ofbiz:url>" class="headerButtonLeft">Task&nbsp;List</a></div>
  <div class="col"><a href="<ofbiz:url>/month</ofbiz:url>" class="headerButtonLeft">Calendar</a></div>
  <div class="col"><a href="<ofbiz:url>/projectlist</ofbiz:url>" class="headerButtonLeft">Projects</a></div>
  <div class="col"><a href="<ofbiz:url>/requestlist</ofbiz:url>" class="headerButtonLeft">Requests</a></div>  
  <ofbiz:unless name="userLogin">
    <div class="col-right"><a href='<ofbiz:url><%=CommonWorkers.makeLoginUrl(pageContext)%></ofbiz:url>' class='headerButtonRight'>Login</a></div>
  </ofbiz:unless>
  <ofbiz:if name="userLogin">
    <div class="col-right"><a href="<ofbiz:url>/logout</ofbiz:url>" class="headerButtonRight">Logout</a></div>
  </ofbiz:if>  
  <div class="col-fill">&nbsp;</div>
</div>
        