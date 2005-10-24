<%--
 *  Copyright (c) 2001-2005 The Open For Business Project - www.ofbiz.org
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
 
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.security.*" %>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />

<table border="0" width="100%" cellspacing="0" cellpadding="0" class="boxoutside">
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxtop">
        <tr>
          <td align="left" width="90%" >
            <div class="boxhead">&nbsp;Web Tools Main Page</div>
          </td>
          <td align="right" width="10%"><div class="lightbuttontextdisabled"><%=delegator.getDelegatorName()%></div></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td width="100%">
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="boxbottom">
        <tr>
          <td>
            <ofbiz:unless name="userLogin">
              <div class="tabletext">For something interesting make sure you are logged in, try username:admin, password:ofbiz.</div>
              <br/>
            </ofbiz:unless>
            <div class="tabletext">The purpose of this Web Tools administration package is to contain all of the 
            administration tools that directly relate to the various Core Tool Components. The Core Tool Component layer is
            defined in the architecture documents as the container of all entity definitions shared by the vertical applications that
            are built on top of these entity definitions and the tools surrounding them such as the entity, workflow, and rule engines,
            content and knowledge management, data analysis, and so forth.</div>
            <br/>
            <div class="tabletext">This application is primarily intended for developers and system administrators.</div>
            <ul>
              <%if (security.hasPermission("UTIL_CACHE_VIEW", session) || security.hasPermission("UTIL_DEBUG_VIEW", session)) {%>
                <li><div class="tabletext">Cache &amp; Debug Tools</div>
                <ul>
                  <%if(security.hasPermission("UTIL_CACHE_VIEW", session)){%>
                    <li><a href="<ofbiz:url>/FindUtilCache</ofbiz:url>" class="linktext">Cache Maintenance</a>
                  <%}%>
                  <%if(security.hasPermission("UTIL_DEBUG_VIEW", session)){%>
                    <li><a href="<ofbiz:url>/debuglevels</ofbiz:url>" class="linktext">Adjust Debugging Levels</a>
                  <%}%>
                </ul>
              <%}%>
              <%if(security.hasPermission("ENTITY_MAINT", session)){%>
                <li><div class="tabletext">Entity Engine Tools</div>
                <ul>
                  <li><a href="<ofbiz:url>/entitymaint</ofbiz:url>" class="linktext">Entity Data Maintenance</a>
                  <li><a href="<ofbiz:url>/view/entityref</ofbiz:url>" class="linktext" target="_blank">Entity Reference</a>&nbsp;<a href="<ofbiz:url>/view/entityref?forstatic=true</ofbiz:url>" class="linktext" target="_blank">[Static Version]</a>
                  <li><a href="<ofbiz:url>/EntitySQLProcessor</ofbiz:url>" class="linktext">Entity SQL Processor</a>
                  <li><a href="<ofbiz:url>/EntitySyncStatus</ofbiz:url>" class="linktext">Entity Sync Status</a>
                  <li><a href="<ofbiz:url>/view/ModelInduceFromDb</ofbiz:url>" target="_blank" class="linktext">Induce Model XML from Database</a><br/>
                  <li><a href="<ofbiz:url>/view/checkdb</ofbiz:url>" class="linktext">Check/Update Database</a>

                  <!-- want to leave these out because they are only working so-so, and cause people more problems that they solve, IMHO
                  <ul>
                    <li><a href="<ofbiz:url>/view/EditEntity</ofbiz:url>" class="linktext" target="_blank">Edit Entity Definitions</a>
                    <li><a href="<ofbiz:url>/ModelWriter</ofbiz:url>" class="linktext" target="_blank">Generate Entity Model XML (all in one)</a>
                    <li><a href="<ofbiz:url>/ModelWriter?savetofile=true</ofbiz:url>" target="_blank" class="linktext">Save Entity Model XML to Files</a><br/>
                  -->
                  <!-- not working right now anyway
                    <li><a href="<ofbiz:url>/ModelGroupWriter</ofbiz:url>" target="_blank" class="linktext">Generate Entity Group XML</a><br/>
                    <li><a href="<ofbiz:url>/ModelGroupWriter?savetofile=true</ofbiz:url>" target="_blank" class="linktext">Save Entity Group XML to File</a><br/>
                  </ul>
                  -->
                  <!--
                  <li><a href="<ofbiz:url>/view/tablesMySql</ofbiz:url>" class="linktext">MySQL Table Creation SQL</a>
                  <li><a href="<ofbiz:url>/view/dataMySql</ofbiz:url>" class="linktext">MySQL Auto Data SQL</a>
                  -->
                </ul>
                <li><div class="tabletext">Entity XML Tools</div>
                <ul>
                  <li><a href="<ofbiz:url>/xmldsdump</ofbiz:url>" class="linktext">XML Data Export</a>
                  <li><a href="<ofbiz:url>/EntityExportAll</ofbiz:url>" class="linktext">XML Data Export All</a>
                  <li><a href="<ofbiz:url>/EntityImport</ofbiz:url>" class="linktext">XML Data Import</a>
                  <li><a href="<ofbiz:url>/EntityImportDir</ofbiz:url>" class="linktext">XML Data Import Dir</a>
                </ul>
              <%}%>
              <%if(security.hasPermission("SERVICE_MAINT", session)) {%>
                <li><div class="tabletext">Service Engine Tools</div>
                <ul>
                  <li><a href="<ofbiz:url>/availableServices</ofbiz:url>" class="linktext">Service Reference</a>
                  <li><a href="<ofbiz:url>/scheduleJob</ofbiz:url>" class="linktext">Schedule Job</a>
                  <li><a href="<ofbiz:url>/jobList</ofbiz:url>" class="linktext">Job List</a>
                  <li><a href="<ofbiz:url>/threadList</ofbiz:url>" class="linktext">Thread List</a>
                  <li><a href="<ofbiz:url>/serviceList</ofbiz:url>" class="linktext">Service Log</a>
                </ul>
              <%}%>
              <%if(security.hasPermission("WORKFLOW_MAINT", session)){%>
                <li><div class="tabletext">Workflow Engine Tools</div>
                <ul>
                  <li><a href="<ofbiz:url>/workflowMonitor</ofbiz:url>" class="linktext">Workflow Monitor</a>
                  <li><a href="<ofbiz:url>/readxpdl</ofbiz:url>" class="linktext">Read XPDL File</a>
                </ul>
              <%}%>
              <%if(security.hasPermission("DATAFILE_MAINT", session)){%>
                <li><div class="tabletext">Data File Tools</div>
                <ul>
                  <li><a href="<ofbiz:url>/viewdatafile</ofbiz:url>" class="linktext">Work With Data Files</a>
                </ul>
              <%}%>
              <% if (session.getAttribute("userLogin") != null) { %>
                <li><div class="tabletext">Misc. Setup Tools</div>
                <ul>
                  <%if(security.hasPermission("PERIOD_MAINT", session)){%>
                    <li><a href="<ofbiz:url>/EditCustomTimePeriod</ofbiz:url>" class="linktext">Edit Custom Time Periods</a>
                  <%}%>
                  <%if(security.hasPermission("ENUM_STATUS_MAINT", session)){%>
                  <!--
                    <li><a href="<ofbiz:url>/EditEnumerationTypes</ofbiz:url>" class="linktext">Edit Enumerations</a>
                    <li><a href="<ofbiz:url>/EditStatusTypes</ofbiz:url>" class="linktext">Edit Status Options</a>
                  -->
                  <%}%>
                </ul>
                <li><div class="tabletext">Performance Tests</div>
                <ul>
                  <li><a href="<ofbiz:url>/EntityPerformanceTest</ofbiz:url>" class="linktext">Entity Engine</a>
                </ul>
              <%}%>
              <%if(security.hasPermission("SERVER_STATS_VIEW", session)){%>
                <li><div class="tabletext">Server Hit Statistics Tools</div>
                <ul>
                  <li><a href="<ofbiz:url>/StatsSinceStart</ofbiz:url>" class="linktext">Stats Since Server Start</a>
                </ul>
              <%}%>
            </ul>

            <div class="tabletext">NOTE: If you have not already run the installation data loading script, from the ofbiz home directory run "ant run-install" or "java -jar ofbiz.jar install"</div>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
