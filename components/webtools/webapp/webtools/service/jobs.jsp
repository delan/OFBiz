<%--
 *  Description: None
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski 
 *@version    $Revision: 1.4 $
 *@since      2.0
--%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />

<%if(security.hasPermission("WEBTOOLS_VIEW", session)) {%>

<%
	List jobs = delegator.findAll("JobSandbox");
	if (jobs != null) pageContext.setAttribute("jobs", jobs);	
%>
<div class="head2">Scheduled Jobs</div>

<br>
<table cellpadding="2" cellspacing="0" border="1" width="100%">
  <tr>
    <td><div class="tableheadtext">Job</div></td>
    <td><div class="tableheadtext">Pool</div></td>
    <td><div class="tableheadtext">Run Time</div></td>
	<td><div class="tableheadtext">Start Time</div></td>
    <td><div class="tableheadtext">Service</div></td>
    <td><div class="tableheadtext">Finish Time</div></td>
    <td>&nbsp;</td>
  </tr>
  <ofbiz:iterator name="job" property="jobs">
  <tr>
    <td><a href="#" class="buttontext"><%=UtilFormatOut.checkNull(job.getString("jobName"),"&nbsp;")%></a></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(job.getString("poolId"), "&nbsp;")%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(job.getString("runTime"),"&nbsp;")%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(job.getString("startDateTime"),"&nbsp;")%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(job.getString("serviceName"),"&nbsp;")%></div></td>
    <td><div class="tabletext"><%=UtilFormatOut.checkNull(job.getString("finishDateTime"),"&nbsp;")%></div></td>
    <td align='center'><a href="#" class="buttontext">[View Data]</a></td>
  </tr>
  </ofbiz:iterator>  
</table>

<%}else{%>
  <hr>
  <div>You do not have permission to use this page.</div>
<%}%>	