<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.lang.StringBuffer" %>
<%@ page import="org.ofbiz.commonapp.workeffort.workeffort.*" %>
<%@ page import="org.ofbiz.core.util.UtilFormatOut" %>
<%-- THis page display application contexts & signatures --%>

<%!
private String makeTable(Map map){
    StringBuffer result = new StringBuffer();
    if (map==null)
       return "Empty";
    Set set = map.keySet() ;
    if (set==null)
       return "Empty";
    Iterator it = set.iterator();
       while (it.hasNext()){
            String key = (String)it.next();
            String value = map.get(key).toString(); 
            result.append("<TR><TD width='26%' align=right><div class='tabletext'>")
                    .append(key)
                    .append("</div></TD><TD  width='74%'>")
                    .append(value)
                    .append("</TD></TR>");
        }
    return result.toString();
}%>



    <% long applicationId = Long.parseLong(request.getParameter("applicationId")); %>
  
    <% Map applicationContext = (Map)request.getAttribute("applicationContext"); 
       String workEffortId = (String) applicationContext.get("workEffortId");
    %>

    <%pageContext.setAttribute("workEffortId",workEffortId,PageContext.REQUEST_SCOPE);
    WorkEffortWorker.getWorkEffort(pageContext, "workeffortId","actualworkeffort","partyAssignsAttrName","canViewAttrName","tryEntityAttrName","currentStatusAttrName");
    WorkEffortApplication.getApplication(pageContext, "actualworkeffort","wfapplication","applicationContextSignature","applicationResultSignature","applicationContext");
    %>

<TABLE width='100%'>
   
    <TR><TD colspan=2 class='boxhead'><div class='boxtop'>Application's Context Signature</div></TD>
        <TD>
        <table border=1><tr><td><%=makeTable((Map)pageContext.getAttribute("applicationContextSignature"))%></td></tr></table>
        </TD></TR> 
    <TR><TD colspan=2 class='boxhead'><div class='boxtop'>Application's Result Signature</div></TD>
        <TD>
        <table border=1><tr><td><%=makeTable((Map)pageContext.getAttribute("applicationResultSignature"))%></td></tr></table>
        </TD></TR> 
    <TR><TD colspan=2 class='boxhead'><div class='boxtop'>Application's Context</div></TD>
        <TD>
        <table border=1><tr><td><%=makeTable((Map)pageContext.getAttribute("applicationContext"))%></td></tr></table>
    </TD></TR> 
<br><br>
    ApplicationID: ----- <%=applicationId%>
    <br><br>
    workEffortId ID: ----- <%=workEffortId%>
    <br><br>
    <table>
    <form name='complete' action='complete_application' method='get'>
        <% for ( Iterator it = ((Map)pageContext.getAttribute("applicationResultSignature")).keySet().iterator(); it.hasNext();){
            String fieldName = (String)it.next();
            try {%>
            <tr><td><%=fieldName%></td><td>&nbsp;</td><td><INPUT type='Text' name='<%=fieldName%>'
                    value='<%=UtilFormatOut.checkNull( (String)(((Map)pageContext.getAttribute("applicationContext")).get(fieldName)))%>'</td></tr>
           <%} catch(Exception ex) {%>
                <%=ex.getMessage()%>
            <%}
            }%>
        <input type='hidden' name='applicationId' value='<%=applicationId%>'>
        <tr><td colspan=2><INPUT type='submit' name='complete' value='complete'></td></tr>
   </form></table>

    <br>
<br><br>
