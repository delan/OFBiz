<%
/**
 *  Title: Entity Maintenance Page
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

<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.model.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
  ModelReader reader = delegator.getModelReader();
  Collection ec = reader.getEntityNames();
  TreeSet entities = new TreeSet(ec);
  Iterator classNamesIterator = entities.iterator();
%>

<h2 style='margin:0;'>Entity Data Maintenance</h2>
<%if(security.hasPermission("ENTITY_MAINT", session)){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";
%>
<TABLE cellpadding='2' cellspacing='2' border='0'>
  <TR>
    <TD>
        <TABLE cellpadding='2' cellspacing='2' border='0'>
          <TR bgcolor='CCCCFF'>
            <TD>Entity&nbsp;Name</TD>
            <TD>&nbsp;</TD>
            <TD>&nbsp;</TD>
            <TD>&nbsp;</TD>
          </TR>
        
        <%int colSize = entities.size()/3 + 1;%>
        <%int kIdx = 0;%>
        <%while (classNamesIterator != null && classNamesIterator.hasNext()) { ModelEntity entity = reader.getModelEntity((String)classNamesIterator.next());%>
            <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
              <TD><div class='tabletext'><%=entity.getEntityName()%></div></TD>
              <%if (entity instanceof ModelViewEntity) {%>
                    <TD colspan='3' align=center><div class='tabletext'>View Entity</div></TD>
              <%} else {%>
                  <%if (security.hasEntityPermission(entity.getTableName(), "_CREATE", session)){%>
                    <TD><a href='<ofbiz:url>/ViewGeneric?entityName=<%=entity.getEntityName()%></ofbiz:url>' class="buttontext">Create</a></TD>
                  <%} else {%>
                    <TD><div class='tabletext'>Perm</div></TD>
                  <%}%>
                  <%if (security.hasEntityPermission(entity.getTableName(), "_VIEW", session)){%>
                    <TD><a href='<ofbiz:url>/FindGeneric?entityName=<%=entity.getEntityName()%></ofbiz:url>' class="buttontext">Find</a></TD>
                    <TD><a href='<ofbiz:url>/FindGeneric?entityName=<%=entity.getEntityName()%>&find=true&VIEW_SIZE=50&VIEW_INDEX=0</ofbiz:url>' class="buttontext">Find All</a></TD>
                  <%} else {%>
                    <TD><div class='tabletext'>Perm</div></TD>
                    <TD><div class='tabletext'>Perm</div></TD>
                  <%}%>
              <%}%>
            </TR>
        
            <%kIdx++;%>
            <%if(kIdx >= colSize) {%>
              <%colSize += colSize;%>
              </TABLE>
            </TD>
            <TD valign=top>
              <TABLE cellpadding='2' cellspacing='2' border='0'>
              <%rowColor = "";%>
              <TR bgcolor='CCCCFF'>
                <TD>Entity&nbsp;Name</TD>
                <TD>&nbsp;</TD>
                <TD>&nbsp;</TD>
                <TD>&nbsp;</TD>
              </TR>
            <%}%>
        <%}%>
          </TR>
        </TABLE>
    </TD>
  </TR>
</TABLE>
<%}else{%>
  <h3>You do not have permission to view this page (ENTITY_MAINT needed).</h3>
<%}%>
