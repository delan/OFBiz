
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<div class="apptitle">&nbsp;Core Application WebTools&nbsp;</div>
<div class="row">
  <div class="col"><a href="<ofbiz:url>/main</ofbiz:url>" class="headerButtonLeft">Main</a></div>     
  <ofbiz:unless name="userLogin">
    <div class="col-right"><a href='<ofbiz:url><%=CommonWorkers.makeLoginUrl(pageContext)%></ofbiz:url>' class='headerButtonRight'>Login</a></div>
  </ofbiz:unless>
  <ofbiz:if name="userLogin">
    <div class="col-right"><a href="<ofbiz:url>/logout</ofbiz:url>" class="headerButtonRight">Logout</a></div>
  </ofbiz:if>  
  <div class="col-fill">&nbsp;</div>
</div>
        
