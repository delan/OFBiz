
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<%@ page import="java.util.*, java.net.*, org.apache.log4j.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%if(security.hasPermission("ENTITY_MAINT", session)) {%>
<div class="head1">JOTM/XAPool Database Tests&nbsp;&nbsp;&nbsp<%if (request.getParameter("verbose") == null){%><a href="<ofbiz:url>/view/jotmTest?verbose=Y</ofbiz:url>" class="buttontext"><font color="red">Run Verbose</font></a><%}else{%><a href="<ofbiz:url>/view/jotmTest</ofbiz:url>" class="buttontext"><font color="red">Run Non-Verbose</font></a><%}%></div>

<%
    Level logLevel = Level.INFO;
    if (request.getParameter("verbose") != null) {
    	logLevel = Level.ALL;
    }
    JotmXaPoolTest jxt = new JotmXaPoolTest(delegator, logLevel);
    String results = jxt.runTests();
%>

<%=results%>

<%}else{%>
<H3>Transaction Tests</H3>

ERROR: You do not have permission to use this page (ENTITY_MAINT needed)
<%}%>
