<%
/**
 *  Title: Main Page
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
 *@created    May 22 2001
 *@version    1.0
 */
%>

<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>

<% pageContext.setAttribute("PageName", "Main Page"); %> 

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<BR>
<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <TD align=left width='90%' >
            <div class='boxhead'>&nbsp;Commonapp Main Page</div>
          </TD>
          <TD align=right width='10%'>&nbsp;</TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
<%if(userLogin == null) {%>
<DIV class='tabletext'>For something interesting make sure you are logged in, try username:admin, password:ofbiz.</DIV>
<BR>
<%}%>
<DIV class='tabletext'>The purpose of this Common Application Components administration package is to contain all of the 
administration tools that directly relate to the Common Application Components. The Common Application Component layer is
defined in the architecture documents as the container of all entity definitions shared by the vertical applications that
are built on top of these entity definitions and the tools surrounding them such as the entity, workflow, and rule engines,
content and knowledge management, data analysis, and so forth.</DIV>
<BR>
<DIV class='tabletext'>This application is primarily intended for developers and system administrators. It contains tools for viewing and changing entity 
definitions, checking them with the current database, and generating text based on those definitions. </DIV>
<ul>
  <%if(security.hasPermission("ENTITY_MAINT", session)){%>
    <li>Entity Maintenance Utilities
    <ul>
      <li><a href="<%=response.encodeURL(controlPath + "/entitymaint")%>" class='buttontext'>Entity Data Maintenance</A>
      <li><a href="<%=response.encodeURL(controlPath + "/view/entityref")%>" class='buttontext' target='_blank'>Entity Reference</A>
<%--
      <li><a href="<%=response.encodeURL(controlPath + "/view/tablesMySql")%>" class='buttontext'>MySQL Table Creation SQL</A>
      <li><a href="<%=response.encodeURL(controlPath + "/view/dataMySql")%>" class='buttontext'>MySQL Auto Data SQL</A>
--%>
      <li><a href="<%=response.encodeURL(controlPath + "/view/checkdb")%>" class='buttontext'>Check/Update Database</A>
      <li><a href="<%=response.encodeURL(controlPath + "/view/ModelWriter")%>" class='buttontext' target='_blank'>Generate Entity Model XML</A>
      <li><a href="<%=response.encodeURL(controlPath + "/view/EditEntity")%>" class='buttontext' target='_blank'>Edit Entity Definitions</A> (also see the Entity Reference for this)
    </ul>
  <%}%>
  <%if(security.hasPermission("UTIL_CACHE_VIEW", session)){%>
    <li><a href="<%=response.encodeURL(controlPath + "/FindUtilCache")%>" class='buttontext'>Cache Maintenance</A>
  <%}%>
</ul>

<DIV class='tabletext'>NOTE: If you have not already run the installation data loading script, <a href="<%=response.encodeURL(controlPath + "/install")%>" class='buttontext'>click here</a> to run it.</DIV>
<DIV class='tabletext'>DOUBLE NOTE: If you are deploying this version of commonapp on a public server, remove the install.jsp page and these two paragraphs about it.</DIV>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
