<%--
 *  Copyright (c) 2001 The Open For Business Project and respective authors.
 
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
 * @author David E. Jones (jonesde@ofbiz.org)
 * @version 1.0
--%>

<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<%@ page import="java.net.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.rules.logikus.*" %>
<%@ page import="org.ofbiz.core.rules.parse.*" %>
<%@ page import="org.ofbiz.core.rules.parse.tokens.*" %>
<%@ page import="org.ofbiz.core.rules.engine.*" %>

<%
    List messages = new LinkedList();
    
    String programText = request.getParameter("program");
    if (programText == null) programText = "";
    String queryText = request.getParameter("query");
    if (queryText == null) queryText = "";
    
    boolean allResults = request.getParameter("all_results") != null ? true : false;

    String lastProgramText = (String)session.getAttribute("LAST_RULES_PROGRAM_TEXT");
    String lastQueryText = (String)session.getAttribute("LAST_RULES_QUERY_TEXT");
    Program program = (Program)session.getAttribute("LAST_RULES_PROGRAM");
    Query query = (Query)session.getAttribute("LAST_RULES_QUERY");

    List results = (List)session.getAttribute("LAST_RULES_RESULTS");
    
    boolean programChanged = (lastProgramText == null) || (!lastProgramText.equals(programText));
    if (programText.length() > 0 && programChanged) {
      try {
          program = LogikusFacade.program(programText);
      } catch (Exception e) { messages.add(e.toString()); Debug.logWarning(e); }
      if (program != null) {
          session.setAttribute("LAST_RULES_PROGRAM", program);
          session.setAttribute("LAST_RULES_PROGRAM_TEXT", programText);
      }
    }
    
    // create a fresh query if the program changes or the query text changes
    if (program != null && queryText.length() > 0 && programChanged || (lastQueryText == null) || (!lastQueryText.equals(queryText))) {
      try {
          query = LogikusFacade.query(queryText, program);
      } catch (Exception e) { messages.add(e.toString()); Debug.logWarning(e); }
      if (query != null) {
          session.setAttribute("LAST_RULES_QUERY", query);
          session.setAttribute("LAST_RULES_QUERY_TEXT", queryText);
          results = new LinkedList();
          session.setAttribute("LAST_RULES_RESULTS", results);
      }
    }
%>
<h3>Run Rules (Logikus) Program</h3>
<div>This page is used to run rulesets or rules programs.</div>
<div>For examples look in <code>"ofbiz/core/docs/examples/rules"</code></div>
<BR>
<%if(security.hasPermission("RULES_MAINT", session)) {%>
  <FORM method=POST action='<ofbiz:url>/logikus</ofbiz:url>'>
    <%-- <INPUT name='program_loc' type=text size='60' value='<%=UtilFormatOut.checkNull(programLoc)%>'> Is URL?:<INPUT type=checkbox name='program_is_url' <%=programIsUrl?"checked":""%>><BR> --%>
    Program or Rule Set:<BR>
    <TEXTAREA rows="20" cols="85" name="program"><%=programText%></TEXTAREA><BR>
    Query (for Backward Chaining):<BR>
    <TEXTAREA rows="3" cols="85" name="query"><%=queryText%></TEXTAREA><BR>
    All results?:<INPUT type=checkbox name='all_results' <%=allResults?"checked":""%>>
    <INPUT type=submit value='Get Result(s)'>
  </FORM>

  <HR>

  <%if (messages.size() > 0) {%>
    <H4>The following occurred:</H4>
    <UL>
    <%Iterator errMsgIter = messages.iterator();%>
    <%while(errMsgIter.hasNext()) {%>
      <LI><%=errMsgIter.next()%>
    <%}%>
    </UL>
    <HR>
  <%}%>

  <div><b>Results:</b></div>
  
  <%if (results != null && results.size() > 0) {%>
      <%Iterator riter = results.iterator();%>
      <%while (riter.hasNext()) {%>
          <div><%=riter.next()%></div>
      <%}%>
  <%}%>
  
  <%if (query != null) {%>
    <%Unification vars = query.variables();%>
    <%boolean moreProofs = true;%>
    <%boolean cont = true;%>
    <%while (cont && (moreProofs = query.canFindNextProof())) {%>
      <%if (vars == null || vars.size() == 0) {%>
        <div>yes, result found</div>
        <%cont = false;%>
      <%} else {%>
        <%if (results != null) results.add(vars.toString());%>
        <div><%=vars%></div>
      <%}%>
      <%if (!allResults) cont = false;%>
    <%}%>
    <%if (!moreProofs) {%>
        <%-- If no more proofs, clear out the session --%>
        <%session.removeAttribute("LAST_RULES_PROGRAM_TEXT");%>
        <%session.removeAttribute("LAST_RULES_QUERY_TEXT");%>
        <%session.removeAttribute("LAST_RULES_PROGRAM");%>
        <%session.removeAttribute("LAST_RULES_QUERY");%>
        <%session.removeAttribute("LAST_RULES_RESULTS");%>
        <div>no more results</div>
    <%}%>
  <%}%>

<%}else{%>
  <hr>
  <div>You do not have permission to use this page (RULES_MAINT needed)</div>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %> 
<%@ include file="/includes/footer.jsp" %>

