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
 *@author     David E. Jones
 *@created    May 28 2001
 *@version    1.0
--%> 

<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />

<%boolean hasUtilCacheEdit = security.hasPermission("UTIL_CACHE_EDIT", session);%>
<%Runtime rt = Runtime.getRuntime();%>

<h3 style='margin:0;'>Cache Maintenance Page</h3>

<%if(security.hasPermission("UTIL_CACHE_VIEW", session)){%>

<div width="100%">
	<a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>[Reload Cache List]</a>
	<a href='<ofbiz:url>/FindUtilCacheClearAll</ofbiz:url>' class='buttontext'>[Clear All Caches]</a>
	<a href='<ofbiz:url>/FindUtilCacheClearAllExpired</ofbiz:url>' class='buttontext'>[Clear Expired From All]</a>
    <a href='<ofbiz:url>/ForceGarbageCollection</ofbiz:url>' class='buttontext'>[Run GC]</a>
</div>
<div class="tabletext"><u><b>Memory:</b></u> [<b>TOTAL:</b> <%=rt.totalMemory()%>] [<b>FREE:</b> <%=rt.freeMemory()%>] [<b>MAX:</b> <%=rt.maxMemory()%>]</span></div>

<br>
<TABLE border='0' cellpadding='2' cellspacing='2'>
<%
  String rowColor1 = "viewManyTR2";
  String rowColor2 = "viewManyTR1";
  String rowColor = "";
%>
  <TR class='viewOneTR1'>
    <TD>Cache&nbsp;Name</TD>
    <TD>size</TD>
    <TD>hitCount</TD>
    <TD>missCount</TD>
    <TD>maxSize</TD>
    <TD>expireTime</TD>
    <TD>useSoftRef?</TD>
    <TD colspan="3">Administration</TD>
  </TR>

  <%TreeSet names = new TreeSet(UtilCache.utilCacheTable.keySet());%>
  <%Iterator nameIter = names.iterator();%>
  <%if(nameIter!=null && nameIter.hasNext()){%>
    <%while(nameIter.hasNext()){%>
      <%String cacheName = (String)nameIter.next();%>
      <%UtilCache utilCache = (UtilCache)UtilCache.utilCacheTable.get(cacheName);%>
      <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
      <tr class="<%=rowColor%>">
        <TD><%=UtilFormatOut.checkNull(utilCache.getName())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.size())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getHitCount())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getMissCount())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getMaxSize())%></TD>
        <TD><%=UtilFormatOut.formatQuantity(utilCache.getExpireTime())%></TD>
        <TD><%=(new Boolean(utilCache.getUseSoftReference())).toString()%></TD>
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

<div>
	<a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>[Reload Cache List]</a>
	<a href='<ofbiz:url>/FindUtilCacheClearAll</ofbiz:url>' class='buttontext'>[Clear All Caches]</a>
	<a href='<ofbiz:url>/FindUtilCacheClearAllExpired</ofbiz:url>' class='buttontext'>[Clear Expired From All]</a>
</div>

<%}else{%>
  <h3>You do not have permission to view this page (UTIL_CACHE_VIEW needed).</h3>
<%}%>
