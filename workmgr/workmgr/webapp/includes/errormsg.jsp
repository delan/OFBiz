<%@ page import="org.ofbiz.core.util.*" %> 
<%
String eventMsgReq = (String)request.getAttribute("EVENT_MESSAGE");
String errorMsgReqControl = (String)request.getAttribute(SiteDefs.ERROR_MESSAGE);
String errorMsgReq = (String)request.getAttribute("ERROR_MESSAGE");
String errorMsgSes = (String)session.getAttribute("ERROR_MESSAGE");
if(errorMsgSes != null) session.removeAttribute("ERROR_MESSAGE");
if(errorMsgReq != null){ %>
<br>
<p><font color="red"><%=errorMsgReq%></font></p>
<br>
<%} if(errorMsgSes != null){%>
<br>
<p><font color="red"><%=errorMsgSes%></font></p>
<br>
<%} if(errorMsgReqControl != null){%>
<br>
<p><font color="red"><%=errorMsgReqControl%></font></p>
<br>
<%} if(eventMsgReq != null){%>
<br>
<p><font color="blue"><%=eventMsgReq%></font></p>
<br>
<%}%>

