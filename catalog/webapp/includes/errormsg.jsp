<%@ page import="org.ofbiz.core.util.*" %><%
String eventMsgReq = (String)request.getAttribute(SiteDefs.EVENT_MESSAGE);
String errorMsgReq = (String)request.getAttribute(SiteDefs.ERROR_MESSAGE);
String errorMsgSes = (String)session.getAttribute(SiteDefs.ERROR_MESSAGE);
if(errorMsgSes != null) session.removeAttribute(SiteDefs.ERROR_MESSAGE);
if(errorMsgReq != null){ %>
<br><div class='errorMessage'><%=errorMsgReq%></div><br><%} if(errorMsgSes != null) {%>
<br><div class='errorMessage'><%=errorMsgSes%></div><br><%} if(eventMsgReq != null) {%>
<br><div class='eventMessage'><%=eventMsgReq%></div><br><%}%>
