<%--
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     Oswin Ondarza & Manuel Soto
 *@created    Aug 27 2002
 *@version    1.0
--%>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.workeffort.workeffort.*" %>
<%  WorkEffortWorker.getWorkEffort(pageContext, "workEffortId", "workEffort", "partyAssigns", "canView", "tryEntity", "currentStatusItem");
    String applicationId=WorkEffortApplication.getApplicationId(pageContext,"partyAssigns");
    WorkEffortApplication.getApplication(pageContext, "workEffort",null,null, null,"applicationContext");
    Map context = (Map)pageContext.getAttribute("applicationContext");
    String content = (String)context.get("_VIEW_NAME_");
    if (content== null || content.length()==0) 
        content=((String)pageContext.getAttribute("_SERVER_ROOT_URL_"))
                .concat((String)pageContext.getAttribute("javax.servlet.include.context_path"))
                .concat("/application/default_application.jsp");
%>      
<html>
    <header>
        <META http-equiv='refresh' content='0; url=<%=content%>?applicationId=<%=applicationId%>'>
    </header>
</html>