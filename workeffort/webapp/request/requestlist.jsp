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
 *@created    May 22 2001
 *@version    1.0
--%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%@ page import="org.ofbiz.commonapp.workeffort.workeffort.*" %>

<ofbiz:service name="getCustRequestsByRole"></ofbiz:service>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;My Request List</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<ofbiz:url>/requestlist</ofbiz:url>' class='lightbuttontextdisabled'>[Request&nbsp;List]</A>
            <A href='<ofbiz:url>/request</ofbiz:url>' class='lightbuttontext'>[New&nbsp;Request]</A>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <ofbiz:if name="custRequestAndRoles" size="0">
      <TR>
        <TD width='100%'>
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <TD><DIV class='tabletext'><b>Priority</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Request</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Response Required By</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Status</b></DIV></TD>
                  <%-- <TD><DIV class='tabletext'><b>Party&nbsp;ID</b></DIV></TD> --%>
                  <TD><DIV class='tabletext'><b>Role&nbsp;ID</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Request&nbsp;Name</b></DIV></TD>
                  <TD align=right><DIV class='tabletext'><b>Edit</b></DIV></TD>
                </TR>
                <TR><TD colspan='8'><HR class='sepbar'></TD></TR>
                <ofbiz:iterator name="custRequestAndRole" property="custRequestAndRoles">
                  <TR>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="custRequestAndRole" field="priority"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="custRequestAndRole" field="custRequestDate"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="custRequestAndRole" field="responseRequiredDate"/></DIV></TD>
                    <%GenericValue statusItem = delegator.findByPrimaryKeyCache("StatusItem", UtilMisc.toMap("statusId", custRequestAndRole.getString("statusId")));%>
                    <%if (statusItem != null) pageContext.setAttribute("statusItem", statusItem);%>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="statusItem" field="description"/></DIV></TD>
                    <%-- <TD><DIV class='tabletext'><ofbiz:entityfield attribute="custRequestAndRole" field="partyId"/></DIV></TD> --%>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="custRequestAndRole" field="roleTypeId"/></DIV></TD>
                    <TD><A class='buttontext' href='<ofbiz:url>/request?custRequestId=<ofbiz:entityfield attribute="custRequestAndRole" field="custRequestId"/></ofbiz:url>'>
                        <ofbiz:entityfield attribute="custRequestAndRole" field="custRequestName"/></a></DIV></TD>
                    <TD align=right><A class='buttontext' href='<ofbiz:url>/request?custRequestId=<ofbiz:entityfield attribute="custRequestAndRole" field="custRequestId"/></ofbiz:url>'>
                        Edit&nbsp;[<ofbiz:entityfield attribute="custRequestAndRole" field="custRequestId"/>]</a></DIV></TD>
                  </TR>
                </ofbiz:iterator>
              </TABLE>
        </TD>
      </TR>
  </ofbiz:if>
</TABLE>
