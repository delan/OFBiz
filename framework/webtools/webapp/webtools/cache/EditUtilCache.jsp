<%--
Copyright 2001-2006 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the License.
--%>

<%@ page import="java.util.*, java.net.*,
                 org.ofbiz.base.util.cache.UtilCache" %>
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.webapp.pseudotag.*" %>

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
    <br/><a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>Back to Cache Maintenance</A>
    <form method="POST" action='<ofbiz:url>/EditUtilCacheUpdate?UTIL_CACHE_NAME=<%=cacheName%></ofbiz:url>'>
    <table border="0" cellpadding="2" cellspacing="2">
    <%
      String rowColor1 = "viewManyTR2";
      String rowColor2 = "viewManyTR1";
      String rowColor = "";
    %>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <td>Cache&nbsp;Name</td>
      <TD colspan="2"><%=UtilFormatOut.checkNull(utilCache.getName())%></td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <td>size</td>
      <TD colspan="2"><%=UtilFormatOut.formatQuantity(utilCache.size())%></td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <td>hitCount</td>
      <TD colspan="2"><%=UtilFormatOut.formatQuantity(utilCache.getHitCount())%></td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <td>Miss Count Total</td>
      <TD colspan="2"><%=UtilFormatOut.formatQuantity(utilCache.getMissCountTotal())%></td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <td>Miss Count Not Found</td>
      <TD colspan="2"><%=UtilFormatOut.formatQuantity(utilCache.getMissCountNotFound())%></td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <td>Miss Count Expired</td>
      <TD colspan="2"><%=UtilFormatOut.formatQuantity(utilCache.getMissCountExpired())%></td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <td>Miss Count SoftRef Cleared</td>
      <TD colspan="2"><%=UtilFormatOut.formatQuantity(utilCache.getMissCountSoftRef())%></td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <td>Remove Hit Count</td>
      <TD colspan="2"><%=UtilFormatOut.formatQuantity(utilCache.getRemoveHitCount())%></td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <td>Remove Miss Count</td>
      <TD colspan="2"><%=UtilFormatOut.formatQuantity(utilCache.getRemoveMissCount())%></td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <td>maxSize</td>
      <td><%=UtilFormatOut.formatQuantity(utilCache.getMaxSize())%></td>
      <td><input type="text" class='inputBox' size="15" maxlength="15" name="UTIL_CACHE_MAX_SIZE" value="<%=utilCache.getMaxSize()%>"></td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <td>expireTime(ms)</td>
      <td>
        <%=UtilFormatOut.formatQuantity(utilCache.getExpireTime())%>
        <%long exp=utilCache.getExpireTime();%>
        <%long hrs=exp/(60*60*1000);exp=exp%(60*60*1000);%>
        <%long mins=exp/(60*1000);exp=exp%(60*1000);%>
        <%double secs=(double)exp/(1000.0);%>
        (<%=hrs+":"+mins+":"+secs%>)
      </td>
      <td><input type="text" class='inputBox' size="15" maxlength="15" name="UTIL_CACHE_EXPIRE_TIME" value="<%=utilCache.getExpireTime()%>"></td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
	  <%String softRefValue = (new Boolean(utilCache.getUseSoftReference())).toString();%>
      <td>Use Soft Reference?</td>
      <td><%=softRefValue%></td>
      <td>
		<select name="UTIL_CACHE_USE_SOFT_REFERENCE" class="selectBox">
			<option <%="true".equals(softRefValue)?"selected":""%>>true</option>
			<option <%="false".equals(softRefValue)?"selected":""%>>false</option>
		</select>
      </td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
	  <%String fileStoreValue = (new Boolean(utilCache.getUseFileSystemStore())).toString();%>
      <td>Use File System Store?</td>
      <td><%=fileStoreValue%></td>
      <td>&nbsp;</td>
    </tr>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr class="<%=rowColor%>">
      <TD colspan="3"><INPUT type="submit" value="Update"></td>
    </tr>
    </TABLE>
    </form>
   <%}else{%>
    <H3>&nbsp;<%=cacheName%> Not Found</H3>
   <%}%>
   <a href='<ofbiz:url>/EditUtilCacheClear?UTIL_CACHE_NAME=<%=cacheName%></ofbiz:url>' class="buttontext">Clear this Cache</a>
  <%}else{%>
    <H3>&nbsp;No Cache Name Specified</H3>
  <%}%>
  <br/><a href='<ofbiz:url>/FindUtilCache</ofbiz:url>' class='buttontext'>Back to Cache Maintenance</A>

<%}else{%>
  <h3>You do not have permission to view this page (UTIL_CACHE_EDIT needed).</h3>
<%}%>
