
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*, org.ofbiz.content.webapp.control.*" %>
<%@ page import="org.ofbiz.common.*" %>
<%@ page import="java.util.*" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />

<div class="apptitle">&nbsp;Marketing Manager Application&nbsp;</div>
<div class="row">
  <div class="col"><a href="<ofbiz:url>/main</ofbiz:url>" class="headerButtonLeft">Main</a></div>   
  <div class="col"><a href="<ofbiz:url>/EditDataSources</ofbiz:url>" class="headerButtonLeft">DataSource</a></div>
  <div class="col"><a href="<ofbiz:url>/FindMarketingCampaign</ofbiz:url>" class="headerButtonLeft">Campaign</a></div>
  <div class="col"><a href="<ofbiz:url>/FindTrackingCode</ofbiz:url>" class="headerButtonLeft">Tracking</a></div>
  <div class="col"><a href="#<%--<ofbiz:url>/FindContactList</ofbiz:url>--%>" class="headerButtonLeft">ContactList</a></div>
  <ofbiz:unless name="userLogin">
    <div class="col-right"><a href='<ofbiz:url><%=LoginWorker.makeLoginUrl(pageContext)%></ofbiz:url>' class='headerButtonRight'>Login</a></div>
  </ofbiz:unless>
  <ofbiz:if name="userLogin">
    <div class="col-right"><a href="<ofbiz:url>/logout</ofbiz:url>" class="headerButtonRight">Logout</a></div>
  </ofbiz:if>  
  <div class="col-fill">&nbsp;</div>
</div>
