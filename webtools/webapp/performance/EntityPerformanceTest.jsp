<%--
 *  Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
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
 *@created    17 April 2003
 *@version    1.0
--%> 

<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<div class="head1">Entity Engine Performance Tests</div>

<div class="tabletext">NOTE: These performance results may vary a great deal for different
databases, JDBC drivers, JTA implementations (transaction managers), connection pools, 
local vs. remote deployment configurations, and hardware (app server hardware, database 
server hardware, network connections).</div>

<br>

<%double startTime, totalTime, callsPerSecond;%>
<%int calls;%>
<table width="100%" border="1" cellspacing="0" cellpadding="2">
  <tr>
    <td><div class="tabletext"><b>Operation</b></div></td>
    <td><div class="tabletext"><b>Entity</b></div></td>
    <td><div class="tabletext"><b>Calls</b></div></td>
    <td><div class="tabletext"><b>Seconds</b></div></td>
    <td><div class="tabletext"><b>Seconds/Call</b></div></td>
    <td><div class="tabletext"><b>Calls/Second</b></div></td>
  </tr>
  <%
    calls = 1000; startTime = (double) System.currentTimeMillis();
    for (int i=0; i < calls; i++) { GenericValue dummy = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", "GZ-1000")); }
    totalTime = (double) System.currentTimeMillis() - startTime;
    callsPerSecond = (double) calls / (totalTime/1000);
  %>
  <tr>
    <td><div class="tabletext">findByPrimaryKey</div></td>
    <td><div class="tabletext">Large:Product</div></td>
    <td><div class="tabletext"><%=calls%></div></td>
    <td><div class="tabletext"><%=totalTime/1000%></div></td>
    <td><div class="tabletext"><%=1/callsPerSecond%></div></td>
    <td><div class="tabletext"><%=callsPerSecond%></div></td>
  </tr>
  <%
    calls = 10000; startTime = (double) System.currentTimeMillis();
    for (int i=0; i < calls; i++) { GenericValue dummy = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", "GZ-1000")); }
    totalTime = (double) System.currentTimeMillis() - startTime;
    callsPerSecond = (double) calls / (totalTime/1000);
  %>
  <tr>
    <td><div class="tabletext">findByPrimaryKeyCache</div></td>
    <td><div class="tabletext">Large:Product</div></td>
    <td><div class="tabletext"><%=calls%></div></td>
    <td><div class="tabletext"><%=totalTime/1000%></div></td>
    <td><div class="tabletext"><%=1/callsPerSecond%></div></td>
    <td><div class="tabletext"><%=callsPerSecond%></div></td>
  </tr>
  <%
    calls = 1000; startTime = (double) System.currentTimeMillis();
    for (int i=0; i < calls; i++) { GenericValue dummy = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", "_NA_")); }
    totalTime = (double) System.currentTimeMillis() - startTime;
    callsPerSecond = (double) calls / (totalTime/1000);
  %>
  <tr>
    <td><div class="tabletext">findByPrimaryKey</div></td>
    <td><div class="tabletext">Small:Party</div></td>
    <td><div class="tabletext"><%=calls%></div></td>
    <td><div class="tabletext"><%=totalTime/1000%></div></td>
    <td><div class="tabletext"><%=1/callsPerSecond%></div></td>
    <td><div class="tabletext"><%=callsPerSecond%></div></td>
  </tr>
  <%
    calls = 100000; startTime = (double) System.currentTimeMillis();
    for (int i=0; i < calls; i++) { Map ptyMap = new HashMap(); ptyMap.put("partyId", "_NA_"); GenericValue dummy = delegator.findByPrimaryKeyCache("Party", ptyMap); }
    totalTime = (double) System.currentTimeMillis() - startTime;
    callsPerSecond = (double) calls / (totalTime/1000);
  %>
  <tr>
    <td><div class="tabletext">findByPrimaryKeyCache - new HashMap</div></td>
    <td><div class="tabletext">Small:Party</div></td>
    <td><div class="tabletext"><%=calls%></div></td>
    <td><div class="tabletext"><%=totalTime/1000%></div></td>
    <td><div class="tabletext"><%=1/callsPerSecond%></div></td>
    <td><div class="tabletext"><%=callsPerSecond%></div></td>
  </tr>
  <%
    calls = 100000; startTime = (double) System.currentTimeMillis();
    for (int i=0; i < calls; i++) { GenericValue dummy = delegator.findByPrimaryKeyCache("Party", UtilMisc.toMap("partyId", "_NA_")); }
    totalTime = (double) System.currentTimeMillis() - startTime;
    callsPerSecond = (double) calls / (totalTime/1000);
  %>
  <tr>
    <td><div class="tabletext">findByPrimaryKeyCache - UtilMisc.toMap</div></td>
    <td><div class="tabletext">Small:Party</div></td>
    <td><div class="tabletext"><%=calls%></div></td>
    <td><div class="tabletext"><%=totalTime/1000%></div></td>
    <td><div class="tabletext"><%=1/callsPerSecond%></div></td>
    <td><div class="tabletext"><%=callsPerSecond%></div></td>
  </tr>
  <%
    calls = 100000; startTime = (double) System.currentTimeMillis();
    Map ptyMap = UtilMisc.toMap("partyId", "_NA_");
    for (int i=0; i < calls; i++) { GenericValue dummy = delegator.findByPrimaryKeyCache("Party", ptyMap); }
    totalTime = (double) System.currentTimeMillis() - startTime;
    callsPerSecond = (double) calls / (totalTime/1000);
  %>
  <tr>
    <td><div class="tabletext">findByPrimaryKeyCache - no new Map</div></td>
    <td><div class="tabletext">Small:Party</div></td>
    <td><div class="tabletext"><%=calls%></div></td>
    <td><div class="tabletext"><%=totalTime/1000%></div></td>
    <td><div class="tabletext"><%=1/callsPerSecond%></div></td>
    <td><div class="tabletext"><%=callsPerSecond%></div></td>
  </tr>
</table>

