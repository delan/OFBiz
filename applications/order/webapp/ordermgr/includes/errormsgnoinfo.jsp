<%@ page import="org.ofbiz.base.util.*" %><%
String errorMsgReq = (String)request.getAttribute("_ERROR_MESSAGE_");
String errorMsgSes = (String)session.getAttribute("_ERROR_MESSAGE_");
if(errorMsgSes != null) session.removeAttribute("_ERROR_MESSAGE_");
if(errorMsgReq != null){ %>
<br><div class='errorMessage'><%=UtilFormatOut.replaceString(errorMsgReq, "\n", "<br>")%></div><br><%} if(errorMsgSes != null) {%>
<br><div class='errorMessage'><%=UtilFormatOut.replaceString(errorMsgSes, "\n", "<br>")%></div><br><%}%> 
