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

<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%boolean hasUtilCacheEdit = security.hasPermission("UTIL_CACHE_EDIT", session);%>
<br>
<h2 style='margin:0;'>Cache Maintenance Page</h2>

<%if(security.hasPermission("UTIL_CACHE_VIEW", session)){%>
<p><a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>Reload Cache List</A></p>
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

  <%TreeSet names = new TreeSet(UtilCache.utilCacheTable.keySet());%>
  <%Iterator nameIter = names.iterator();%>
  <%if(nameIter!=null && nameIter.hasNext()){%>
    <%while(nameIter.hasNext()){%>
      <%String cacheName = (String)nameIter.next();%>
      <%UtilCache utilCache = (UtilCache)UtilCache.utilCacheTable.get(cacheName);%>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
      <tr bgcolor="<%=rowColor%>">
        <TD><%=UtilFormatOut.checkNull(utilCache.getName())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.size())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getHitCount())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getMissCount())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getMaxSize())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getExpireTime())%></TD>
        <TD align=center valign=middle>
          <a href='<ofbiz:url>/FindUtilCacheElements?UTIL_CACHE_NAME=<%=UtilFormatOut.checkNull(utilCache.getName())%></ofbiz:url>' class="buttontext">Elements</a>
        </TD>
        <TD align=center valign=middle>
          <%if(hasUtilCacheEdit){%>
            <a href='<ofbiz:url>/EditUtilCache?UTIL_CACHE_NAME=<%=UtilFormatOut.checkNull(utilCache.getName())%></ofbiz:url>' class="buttontext">Edit</a>
          <%}%>
        </TD>
        <TD align=center valign=middle>
          <%if(hasUtilCacheEdit){%>
            <a href='<ofbiz:url>/FindUtilCacheClear?UTIL_CACHE_NAME=<%=UtilFormatOut.checkNull(utilCache.getName())%></ofbiz:url>' class="buttontext">Clear</a>
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
<p><a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>Reload Cache List</A></p>
<%}else{%>
  <h3>You do not have permission to view this page (UTIL_CACHE_VIEW needed).</h3>
<%}%>
