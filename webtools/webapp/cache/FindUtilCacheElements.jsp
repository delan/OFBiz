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

<%boolean hasUtilCacheEdit=security.hasPermission("UTIL_CACHE_EDIT", session);%>
<%String cacheName=request.getParameter("UTIL_CACHE_NAME");%>

<br>
<h2 style='margin:0;'>Cache Element Maintenance Page</h2>

<%if(security.hasPermission("UTIL_CACHE_VIEW", session)){%>
  <%if(cacheName!=null){%>
   <%UtilCache utilCache = (UtilCache)UtilCache.utilCacheTable.get(cacheName);%>
   <%if(utilCache!=null){%>
    <H3>&nbsp;<%=cacheName%> (<%=(new Date()).toString()%>)</H3>
    <a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>Back to Cache Maintenance</A>
    <TABLE border='0' cellpadding='2' cellspacing='2'>
    <%
      String rowColor1 = "99CCFF";
      String rowColor2 = "CCFFFF";
      String rowColor = "";
    %>
      <TR bgcolor='CCCCFF'>
        <TD>Cache&nbsp;Element&nbsp;Key</TD>
        <%-- <TD>createTime</TD> --%>
        <TD>expireTime</TD>
        <TD></TD>
      </TR>

      <%if(utilCache.getMaxSize() > 0) {%>
          <%Iterator iter = utilCache.keyLRUList.iterator();%>
          <%if(iter!=null && iter.hasNext()){%>
            <%while(iter.hasNext()){%>
              <%Object key = iter.next();%>
              <%UtilCache.CacheLine line = (UtilCache.CacheLine) utilCache.cacheLineTable.get(key);%>
              <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
              <tr bgcolor="<%=rowColor%>">
                <TD><%=key.toString()%></TD>
                <%--
                <TD>
                  <%if(createTime!=null){%>
                    <%=(new Date(createTime.longValue())).toString()%>
                  <%}%>
                  &nbsp;
                </TD>
                --%>
                <TD>
                  <%long expireTime = utilCache.getExpireTime();%>
                  <%if (line != null && line.loadTime > 0){%>
                    <%=(new Date(line.loadTime + expireTime)).toString()%>
                  <%}%>
                  &nbsp;
                </TD>
                <TD>
                  <%if(hasUtilCacheEdit){%>
                    <a href='<ofbiz:url>/FindUtilCacheElementsRemoveElement?UTIL_CACHE_NAME=<%=cacheName%>&UTIL_CACHE_ELEMENT_NUMBER=<%=utilCache.keyLRUList.indexOf(key)%></ofbiz:url>' class='buttontext'>Remove</a>
                  <%}%>
                </TD>
              </TR>
            <%}%>
          <%}else{%>
              <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
                <TD colspan="5">No UtilCache elements found.</TD>
              </TR>
          <%}%>
      <%} else {%>
          <%Iterator iter = utilCache.cacheLineTable.entrySet().iterator();%>
          <%if(iter!=null && iter.hasNext()){%>
            <%int keyNum = 0;%>
            <%while(iter.hasNext()){%>
              <%Map.Entry entry = (Map.Entry)iter.next();%>
              <%Object key = entry.getKey();%>
              <%UtilCache.CacheLine line = (UtilCache.CacheLine) entry.getValue();%>
              <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
              <tr bgcolor="<%=rowColor%>">
                <TD><%=key.toString()%></TD>
                <TD>
                  <%long expireTime = utilCache.getExpireTime();%>
                  <%if(line != null && line.loadTime > 0){%>
                    <%=(new Date(line.loadTime + expireTime)).toString()%>
                  <%}%>
                  &nbsp;
                </TD>
                <TD>
                  <%if(hasUtilCacheEdit){%>
                    <a href='<ofbiz:url>/FindUtilCacheElementsRemoveElement?UTIL_CACHE_NAME=<%=cacheName%>&UTIL_CACHE_ELEMENT_NUMBER=<%=keyNum%></ofbiz:url>' class="buttontext">Remove</a>
                  <%}%>
                </TD>
              </TR>
              <%keyNum++;%>
            <%}%>
          <%}else{%>
              <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
                <TD colspan="5">No UtilCache elements found.</TD>
              </TR>
          <%}%>
      <%}%>
    </TABLE>
   <%}else{%>
    <H3>&nbsp;<%=cacheName%> Not Found</H3>
   <%}%>
  <%}else{%>
    <H3>&nbsp;No Cache Name Specified</H3>
  <%}%>
  <a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>Back to Cache Maintenance</A>
<%}else{%>
  <h3>You do not have permission to view this page (UTIL_CACHE_VIEW needed).</h3>
<%}%>
