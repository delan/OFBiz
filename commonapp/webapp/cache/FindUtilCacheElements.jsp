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

<% pageContext.setAttribute("PageName", "FindUtilCacheElements"); %> 
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<%boolean hasUtilCacheEdit=security.hasPermission("UTIL_CACHE_EDIT", session);%>
<%String cacheName=request.getParameter("UTIL_CACHE_NAME");%>

<br>
<h2 style='margin:0;'>Cache Element Maintenance Page</h2>

<%if(security.hasPermission("UTIL_CACHE_VIEW", session)){%>
  <%if(cacheName!=null){%>
   <%UtilCache utilCache = (UtilCache)UtilCache.utilCacheTable.get(cacheName);%>
   <%if(utilCache!=null){%>
    <H3>&nbsp;<%=cacheName%> (<%=(new Date()).toString()%>)</H3>
    <a href="<%=response.encodeURL(controlPath + "/FindUtilCache")%>" class='buttontext'>Back to Cache Maintenance</A>
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
              <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
              <tr bgcolor="<%=rowColor%>">
                <TD><%=key.toString()%></TD>
                <%Long createTime = (Long)utilCache.expireTable.get(key);%>
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
                  <%if(createTime!=null){%>
                    <%=(new Date(createTime.longValue()+expireTime)).toString()%>
                  <%}%>
                  &nbsp;
                </TD>
                <TD>
                  <%if(hasUtilCacheEdit){%>
                    <a href="<%=response.encodeURL(controlPath + "/FindUtilCacheElementsRemoveElement?UTIL_CACHE_NAME=" + cacheName + "&UTIL_CACHE_ELEMENT_NUMBER=" + utilCache.keyLRUList.indexOf(key))%>" class="buttontext">Remove</a>
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
          <%Iterator iter = utilCache.valueTable.entrySet().iterator();%>
          <%if(iter!=null && iter.hasNext()){%>
            <%int keyNum = 0;%>
            <%while(iter.hasNext()){%>
              <%Map.Entry entry = (Map.Entry)iter.next();%>
              <%Object key = entry.getKey();%>
              <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
              <tr bgcolor="<%=rowColor%>">
                <TD><%=key.toString()%></TD>
                <%Long createTime = (Long)utilCache.expireTable.get(key);%>
                <TD>
                  <%long expireTime = utilCache.getExpireTime();%>
                  <%if(createTime!=null){%>
                    <%=(new Date(createTime.longValue()+expireTime)).toString()%>
                  <%}%>
                  &nbsp;
                </TD>
                <TD>
                  <%if(hasUtilCacheEdit){%>
                    <a href='<%=response.encodeURL(controlPath + "/FindUtilCacheElementsRemoveElement?UTIL_CACHE_NAME=" + cacheName + "&UTIL_CACHE_ELEMENT_NUMBER=" + keyNum)%>' class="buttontext">Remove</a>
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
  <a href="<%=response.encodeURL(controlPath + "/FindUtilCache")%>" class='buttontext'>Back to Cache Maintenance</A>
<%}else{%>
  <h3>You do not have permission to view this page (UTIL_CACHE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
