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
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />

<%boolean hasUtilCacheEdit=security.hasPermission("UTIL_CACHE_EDIT", session);%>
<%String cacheName=request.getParameter("UTIL_CACHE_NAME");%>

<h3>Cache Element Maintenance Page</h3>

<%if(security.hasPermission("UTIL_CACHE_VIEW", session)){%>
  <%if(cacheName!=null){%>
   <%UtilCache utilCache = (UtilCache)UtilCache.utilCacheTable.get(cacheName);%>
   <%if(utilCache!=null){%>
    <div class="tabletext"><b>Cache Name:</b>&nbsp;<%=cacheName%> (<%=(new Date()).toString()%>)</div>
    <a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>Back to Cache Maintenance</A>
    <TABLE border='0' cellpadding='2' cellspacing='2'>
    <%
      String rowColor1 = "viewManyTR2";
      String rowColor2 = "viewManyTR1";
      String rowColor = "";
    %>
      <TR class='viewOneTR1'>
        <TD>Cache&nbsp;Element&nbsp;Key</TD>
        <%-- <TD>createTime</TD> --%>
        <TD>expireTime</TD>
        <TD>bytes</TD>
        <TD></TD>
      </TR>

      <%if(utilCache.getMaxSize() > 0) {%>
          <%Iterator iter = utilCache.keyLRUList.iterator();%>
          <%if(iter!=null && iter.hasNext()){%>
            <%while(iter.hasNext()){%>
              <%Object key = iter.next();%>
              <%UtilCache.CacheLine line = (UtilCache.CacheLine) utilCache.cacheLineTable.get(key);%>
              <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
              <tr class="<%=rowColor%>">
                <TD><%=key%></TD>
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
                  <%=line.getSizeInBytes()%>
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
              <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
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
              <tr class="<%=rowColor%>">
                <TD><%=key%></TD>
                <TD>
                  <%long expireTime = utilCache.getExpireTime();%>
                  <%if(line != null && line.loadTime > 0){%>
                    <%=(new Date(line.loadTime + expireTime)).toString()%>
                  <%}%>
                  &nbsp;
                </TD>
                <TD>
                  <%=line.getSizeInBytes()%>
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
    <H2>&nbsp;<%=cacheName%> Not Found</H2>
   <%}%>
  <%}else{%>
    <H2>&nbsp;No Cache Name Specified</H2>
  <%}%>
  <a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>Back to Cache Maintenance</A>
<%}else{%>
  <h3>You do not have permission to view this page (UTIL_CACHE_VIEW needed).</h3>
<%}%>
