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
 *@created    October 19, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("MARKETING", "_VIEW", session)) {%>
<%
    List dataSources = delegator.findAll("DataSource", UtilMisc.toList("dataSourceTypeId", "description"));
    if (dataSources != null) pageContext.setAttribute("dataSources", dataSources);
    List dataSourceTypes = delegator.findAll("DataSourceType", UtilMisc.toList("description"));
    if (dataSourceTypes != null) pageContext.setAttribute("dataSourceTypes", dataSourceTypes);
%>
<br>

<div class="head1">Data Sources</div>

<br>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>ID</b></div></td>
    <td><div class="tabletext"><b>Type</b></div></td>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="dataSource" property="dataSources">
  <tr valign="middle">
    <FORM method=POST action='<ofbiz:url>/updateDataSource</ofbiz:url>'>
        <input type=hidden <ofbiz:inputvalue entityAttr="dataSource" field="dataSourceId" fullattrs="true"/>>
    <td><div class='tabletext'><ofbiz:entityfield attribute="dataSource" field="dataSourceId"/></div></td>
    <td>
      <select name='dataSourceTypeId' size='1' style='size: x-small;'>
        <%if (dataSource.get("dataSourceTypeId") != null) {%>
          <%GenericValue curDataSourceType = delegator.findByPrimaryKey("DataSourceType", UtilMisc.toMap("dataSourceTypeId", dataSource.get("dataSourceTypeId")));%>
          <%if (curDataSourceType != null) {%>
            <option value='<%=curDataSourceType.getString("dataSourceTypeId")%>'><%=curDataSourceType.getString("description")%><%-- [<%=curDataSourceType.getString("dataSourceTypeId")%>]--%></option>
          <%}%>
          <option value='<%=curDataSourceType.getString("dataSourceTypeId")%>'>-----</option>
        <%}%>
        <ofbiz:iterator name="dataSourceType" property="dataSourceTypes">
          <option value='<%=dataSourceType.getString("dataSourceTypeId")%>'><%=dataSourceType.getString("description")%><%-- [<%=dataSourceType.getString("dataSourceId")%>]--%></option>
        </ofbiz:iterator>
      </select>
    </td>
    <td><input type=text size='60' style='size: x-small;' <ofbiz:inputvalue entityAttr="dataSource" field="description" fullattrs="true"/>></td>
    <td><INPUT type=submit value='Update' style='size: x-small;'></td>
    <td><a href='<ofbiz:url>/deleteDataSource?dataSourceId=<ofbiz:entityfield attribute="dataSource" field="dataSourceId"/></ofbiz:url>' class='buttontext'>Delete</a></td>
    </FORM>
  </tr>
</ofbiz:iterator>
</table>
<br>

<form method="POST" action="<ofbiz:url>/createDataSource</ofbiz:url>" style='margin: 0;'>
  <div class='head2'>Create a Data Source:</div>
  <br>
  <table>
    <tr>
      <td><div class='tabletext'>ID:</div></td>
      <td><input type=text size='20' name='dataSourceId' value='' style='size: x-small;'></td>
    </tr>
    <tr>
      <td><div class='tabletext'>Type:</div></td>
      <td>
          <select name='dataSourceTypeId' size='1' style='size: x-small;'>
            <ofbiz:iterator name="dataSourceType" property="dataSourceTypes">
              <option value='<%=dataSourceType.getString("dataSourceTypeId")%>'><%=dataSourceType.getString("description")%><%-- [<%=dropDownDataSource.getString("dataSourceId")%>]--%></option>
            </ofbiz:iterator>
          </select>
      </td>
    </tr>
    <tr>
      <td><div class='tabletext'>Description:</div></td>
      <td><input type=text size='60' name='description' value='' style='size: x-small;'></td>
    </tr>
    <tr>
      <td colspan='2'><input type="submit" value="Create"></td>
    </tr>
  </table>
</form>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("MARKETING_VIEW" or "MARKETING_ADMIN" needed)</h3>
<%}%>
