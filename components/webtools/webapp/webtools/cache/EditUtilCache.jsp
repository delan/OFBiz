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

<%@ page import="java.util.*, java.net.*,
                 org.ofbiz.base.util.cache.UtilCache" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.content.webapp.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />

<%String cacheName=request.getParameter("UTIL_CACHE_NAME");%>

<h3>Cache Maintenance Edit Page</h3>

<%if(security.hasPermission("UTIL_CACHE_EDIT", session)){%>
  <%if(cacheName!=null){%>
   <%UtilCache utilCache = (UtilCache)UtilCache.utilCacheTable.get(cacheName);%>
   <%if(utilCache!=null){%>
    <div class="tabletext"><b>Cache Name:</b>&nbsp;<%=cacheName%></div>
    <a href='<ofbiz:url>/EditUtilCacheClear?UTIL_CACHE_NAME=<%=cacheName%></ofbiz:url>' class="buttontext">Clear this Cache</a>
    <br><a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>Back to Cache Maintenance</A>
    <form method="POST" action='<ofbiz:url>/EditUtilCacheUpdate?UTIL_CACHE_NAME=<%=cacheName%></ofbiz:url>'>
    <TABLE border='0' cellpadding='2' cellspacing='2'>
    <%
      String rowColor1 = "viewManyTR2";
      String rowColor2 = "viewManyTR1";
      String rowColor = "";
    %>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <TD>Cache&nbsp;Name</TD>
      <TD colspan="2"><%=UtilFormatOut.checkNull(utilCache.getName())%></TD>
    </TR>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <TD>size</TD>
      <TD colspan="2"><%=UtilFormatOut.formatQuantity(utilCache.size())%></TD>
    </TR>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <TD>hitCount</TD>
      <TD colspan="2"><%=UtilFormatOut.formatQuantity(utilCache.getHitCount())%></TD>
    </TR>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <TD>missCount</TD>
      <TD colspan="2"><%=UtilFormatOut.formatQuantity(utilCache.getMissCount())%></TD>
    </TR>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <TD>maxSize</TD>
      <TD><%=UtilFormatOut.formatQuantity(utilCache.getMaxSize())%></TD>
      <TD><input type="text" class='inputBox' size="15" maxlength="15" name="UTIL_CACHE_MAX_SIZE" value="<%=utilCache.getMaxSize()%>"></TD>
    </TR>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <TD>expireTime(ms)</TD>
      <TD>
        <%=UtilFormatOut.formatQuantity(utilCache.getExpireTime())%>
        <%long exp=utilCache.getExpireTime();%>
        <%long hrs=exp/(60*60*1000);exp=exp%(60*60*1000);%>
        <%long mins=exp/(60*1000);exp=exp%(60*1000);%>
        <%double secs=(double)exp/(1000.0);%>
        (<%=hrs+":"+mins+":"+secs%>)
      </TD>
      <TD><input type="text" class='inputBox' size="15" maxlength="15" name="UTIL_CACHE_EXPIRE_TIME" value="<%=utilCache.getExpireTime()%>"></TD>
    </TR>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
	  <%String softRefValue = (new Boolean(utilCache.getUseSoftReference())).toString();%>
      <TD>useSoftReference?</TD>
      <TD><%=softRefValue%></TD>
      <TD>
		<select name="UTIL_CACHE_USE_SOFT_REFERENCE" class="selectBox">
			<option <%="true".equals(softRefValue)?"selected":""%>>true</option>
			<option <%="false".equals(softRefValue)?"selected":""%>>false</option>
		</select>
      </TD>
    </TR>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <TD colspan="3"><INPUT type="submit" value="Update"></TD>
    </TR>
    </TABLE>
    </form>
   <%}else{%>
    <H3>&nbsp;<%=cacheName%> Not Found</H3>
   <%}%>
   <a href='<ofbiz:url>/EditUtilCacheClear?UTIL_CACHE_NAME=<%=cacheName%></ofbiz:url>' class="buttontext">Clear this Cache</a>
  <%}else{%>
    <H3>&nbsp;No Cache Name Specified</H3>
  <%}%>
  <br><a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>Back to Cache Maintenance</A>

<%}else{%>
  <h3>You do not have permission to view this page (UTIL_CACHE_EDIT needed).</h3>
<%}%>
