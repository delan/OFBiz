<%@ page import="org.ofbiz.core.util.*" %><%
String eventMsgReq = (String)request.getAttribute(SiteDefs.EVENT_MESSAGE);
String errorMsgReq = (String)request.getAttribute(SiteDefs.ERROR_MESSAGE);
String errorMsgSes = (String)session.getAttribute(SiteDefs.ERROR_MESSAGE);
if(errorMsgSes != null) session.removeAttribute(SiteDefs.ERROR_MESSAGE);
if(errorMsgReq != null){ %>
<br><div class='errorMessage'><%=UtilFormatOut.replaceString(errorMsgReq, "\n", "<br>")%></div><br><%} if(errorMsgSes != null) {%>
<br><div class='errorMessage'><%=UtilFormatOut.replaceString(errorMsgSes, "\n", "<br>")%></div><br><%} if(eventMsgReq != null) {%>
<br><div class='eventMessage'><%=UtilFormatOut.replaceString(eventMsgReq, "\n", "<br>")%></div><br><%}%>
