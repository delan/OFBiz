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
 
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.security.*" %>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='90%' >
            <div class='boxhead'>&nbsp;Web Tools Main Page</div>
          </TD>
          <TD align=right width='10%'><div class='lightbuttontextdisabled'><%=delegator.getDelegatorName()%></div></TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <ofbiz:unless name="userLogin">
              <DIV class='tabletext'>For something interesting make sure you are logged in, try username:admin, password:ofbiz.</DIV>
              <BR>
            </ofbiz:unless>
            <DIV class='tabletext'>The purpose of this Web Tools administration package is to contain all of the 
            administration tools that directly relate to the various Core Tool Components. The Core Tool Component layer is
            defined in the architecture documents as the container of all entity definitions shared by the vertical applications that
            are built on top of these entity definitions and the tools surrounding them such as the entity, workflow, and rule engines,
            content and knowledge management, data analysis, and so forth.</DIV>
            <BR>
            <DIV class='tabletext'>This application is primarily intended for developers and system administrators.</DIV>
            <ul>
              <%if(security.hasPermission("UTIL_CACHE_VIEW", session)){%>
                <li><div class='tabletext'>Cache Utilities</div>
                <ul>
                  <li><a href="<ofbiz:url>/FindUtilCache</ofbiz:url>" class='buttontext'>Cache Maintenance</A>
                </ul>
              <%}%>
              <%if(security.hasPermission("UTIL_DEBUG_VIEW", session)){%>
                <li><div class='tabletext'>Debug Utilities</div>
                <ul>
                  <li><a href="<ofbiz:url>/debuglevels</ofbiz:url>" class='buttontext'>Adjust Debugging Levels</A>
                </ul>
              <%}%>
              <%if(security.hasPermission("ENTITY_MAINT", session)){%>
                <li><div class='tabletext'>Entity Engine Utilities</div>
                <ul>
                  <li><a href="<ofbiz:url>/entitymaint</ofbiz:url>" class='buttontext'>Entity Data Maintenance</A>
                  <li><a href="<ofbiz:url>/view/entityref</ofbiz:url>" class='buttontext' target='_blank'>Entity Reference &amp; Editing Tools</A>
                  <ul>
                    <li><a href="<ofbiz:url>/view/checkdb</ofbiz:url>" class='buttontext'>Check/Update Database</A>
                    <li><a href="<ofbiz:url>/view/ModelWriter</ofbiz:url>" class='buttontext' target='_blank'>Generate Entity Model XML (all in one)</A>
                    <li><a href="<ofbiz:url>/view/ModelWriter?savetofile=true</ofbiz:url>" target='_blank' class='buttontext'>Save Entity Model XML to Files</A><BR>
                    <li><a href="<ofbiz:url>/view/ModelGroupWriter</ofbiz:url>" target='_blank' class='buttontext'>Generate Entity Group XML</A><BR>
                    <li><a href="<ofbiz:url>/view/ModelGroupWriter?savetofile=true</ofbiz:url>" target='_blank' class='buttontext'>Save Entity Group XML to File</A><BR>
                    <li><a href="<ofbiz:url>/view/EditEntity</ofbiz:url>" class='buttontext' target='_blank'>Edit Entity Definitions</A>
                    <li><a href="<ofbiz:url>/view/ModelInduceFromDb</ofbiz:url>" target='_blank' class='buttontext'>Induce Model XML from Database</A><BR>
                  </ul>
                  <li><a href="<ofbiz:url>/view/xmldsdump</ofbiz:url>" class='buttontext'>XML Data Export</A>
                  <li><a href="<ofbiz:url>/view/xmldsimport</ofbiz:url>" class='buttontext'>XML Data Import</A>
            <%--
                  <li><a href="<ofbiz:url>/view/tablesMySql</ofbiz:url>" class='buttontext'>MySQL Table Creation SQL</A>
                  <li><a href="<ofbiz:url>/view/dataMySql</ofbiz:url>" class='buttontext'>MySQL Auto Data SQL</A>
            --%>
                </ul>
              <%}%>
              <%if(security.hasPermission("DATAFILE_MAINT", session)){%>
                <li><div class='tabletext'>Data File Utilities</div>
                <ul>
                  <li><a href="<ofbiz:url>/viewdatafile</ofbiz:url>" class='buttontext'>View Data File</A>
                </ul>
              <%}%>
              <%if(security.hasPermission("RULES_MAINT", session)){%>
                <li><div class='tabletext'>Rules Utilities</div>
                <ul>
                  <li><a href="<ofbiz:url>/logikus</ofbiz:url>" class='buttontext'>Logikus - Run Rulesets</A>
                </ul>
              <%}%>
              <%if(security.hasPermission("WORKFLOW_MAINT", session)){%>
                <li><div class='tabletext'>Workflow Utilities</div>
                <ul>
                  <li><a href="<ofbiz:url>/workflowMonitor</ofbiz:url>" class="buttontext">Workflow Monitor</a>
                  <li><a href="<ofbiz:url>/readxpdl</ofbiz:url>" class='buttontext'>Read XPDL File</A>
                </ul>
              <%}%>
              <% if (session.getAttribute("userLogin") != null) { %>
                <li><div class='tabletext'>Misc. Setup</div>
                <ul>
                  <%if(security.hasPermission("PERIOD_MAINT", session)){%>
                    <li><a href="<ofbiz:url>/EditCustomTimePeriod</ofbiz:url>" class='buttontext'>Edit Custom Time Periods</a>
                  <%}%>
                  <%if(security.hasPermission("ENUM_STATUS_MAINT", session)){%>
                    <li><a href="<ofbiz:url>/EditEnumerationTypes</ofbiz:url>" class='buttontext'>Edit Enumerations</a>
                    <li><a href="<ofbiz:url>/EditStatusTypes</ofbiz:url>" class='buttontext'>Edit Status Options</a>
                  <%}%>
                </ul>
              <%}%>
              <%if(security.hasPermission("SERVER_STATS_VIEW", session)){%>
                <li><div class='tabletext'>Server Hit Statistics</div>
                <ul>
                  <li><a href="<ofbiz:url>/StatsSinceStart</ofbiz:url>" class='buttontext'>Stats Since Server Start</a>
                </ul>
              <%}%>
            </ul>

            <DIV class='tabletext'>NOTE: If you have not already run the installation data loading script, <a href="<ofbiz:url>/install</ofbiz:url>" class='buttontext'>click here</a> to run it.</DIV>
            <DIV class='tabletext'>DOUBLE NOTE: If you are deploying this version of commonapp on a public server, remove the install.jsp page and these two paragraphs about it.</DIV>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
