<%
/**
 *  Title: UtilCache Maintenance Page
 *  Description: None
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
 *@author     David E. Jones
 *@created    May 28 2001
 *@version    1.0
 */
%> 

<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="java.util.*" %>

<% pageContext.setAttribute("PageName", "FindUtilCache"); %> 
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<%boolean hasUtilCacheEdit=security.hasPermission("UTIL_CACHE_EDIT", session);%>
<br>
<h2 style='margin:0;'>Cache Maintenance Page</h2>

<%if(security.hasPermission("UTIL_CACHE_VIEW", session)){%>
<TABLE border='0' cellpadding='2' cellspacing='2'>
<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";
%>
  <TR bgcolor='CCCCFF'>
    <TD>Cache&nbsp;Name</TD>
    <TD>size</TD>
    <TD>hitCount</TD>
    <TD>missCount</TD>
    <TD>maxSize</TD>
    <TD>expireTime</TD>
    <TD colspan="3">Administration</TD>
  </TR>

  <%Enumeration enum = UtilCache.utilCacheTable.elements();%>
  <%if(enum!=null && enum.hasMoreElements()){%>
    <%while(enum.hasMoreElements()){%>
      <%UtilCache utilCache = (UtilCache)enum.nextElement();%>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
      <tr bgcolor="<%=rowColor%>">
        <TD><%=UtilFormatOut.checkNull(utilCache.getName())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.size())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getHitCount())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getMissCount())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getMaxSize())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getExpireTime())%></TD>
        <TD>
          <a href="<%=response.encodeURL(controlPath + "/FindUtilCacheElements?UTIL_CACHE_NAME=" + UtilFormatOut.checkNull(utilCache.getName()))%>" class="buttontext">Elements</a>
        </TD>
        <TD>
          <%if(hasUtilCacheEdit){%>
            <a href="<%=response.encodeURL(controlPath + "/EditUtilCache?UTIL_CACHE_NAME=" + UtilFormatOut.checkNull(utilCache.getName()))%>" class="buttontext">Edit</a>
          <%}%>
        </TD>
        <TD>
          <%if(hasUtilCacheEdit){%>
            <a href="<%=response.encodeURL(controlPath + "/FindUtilCacheClear?UTIL_CACHE_NAME=" + UtilFormatOut.checkNull(utilCache.getName()))%>" class="buttontext">Clear</a>
          <%}%>
        </TD>
      </TR>
    <%}%>
  <%}else{%>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
        <TD colspan="5">No UtilCache instances found.</TD>
      </TR>
  <%}%>
</TABLE>
<%}else{%>
  <h3>You do not have permission to view this page (UTIL_CACHE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
