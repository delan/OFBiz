<%
String errorMsgReq = (String)request.getAttribute("ERROR_MESSAGE");
String errorMsgSes = (String)session.getAttribute("ERROR_MESSAGE");
if(errorMsgSes != null) session.removeAttribute("ERROR_MESSAGE");
if(errorMsgReq != null)
{
%>
<p><font color="red"><%=errorMsgReq%></font></p>
<%} if(errorMsgSes != null){%>
<p><font color="red"><%=errorMsgSes%></font></p>
<%}%>

