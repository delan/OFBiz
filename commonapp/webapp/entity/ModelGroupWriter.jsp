<%@ page contentType="text/plain" %><%@ page import="java.util.*, java.io.*, java.net.*, org.ofbiz.core.util.*, org.ofbiz.core.entity.*, org.ofbiz.core.entity.model.*" %><jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" /><jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" /><%

  if("true".equals(request.getParameter("savetofile"))) {
    if(security.hasPermission("ENTITY_MAINT", session)) {
      //save to the file specified in the ModelReader config
      String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);
      String serverRootUrl=(String)request.getAttribute(SiteDefs.SERVER_ROOT_URL);
      ModelGroupReader modelGroupReader = delegator.getModelGroupReader();

      String filename = modelGroupReader.entityGroupFileName;

      java.net.URL url = new java.net.URL(serverRootUrl + controlPath + "/view/ModelGroupWriter");
      HashMap params = new HashMap();
      HttpClient httpClient = new HttpClient(url, params);
      InputStream in = httpClient.getStream();

      File newFile = new File(filename);
      FileWriter newFileWriter = new FileWriter(newFile);

      BufferedReader post = new BufferedReader(new InputStreamReader(in));
      String line = null;
      while((line = post.readLine()) != null) {
        newFileWriter.write(line);
        newFileWriter.write("\n");
      }
      newFileWriter.close();
      %>
      If you aren't seeing any exceptions, XML was written successfully to:
      <%=filename%>
      from the URL:
      <%=url.toString()%>
      <%
    } 
    else {
      %>ERROR: You do not have permission to use this page (ENTITY_MAINT needed)<%
    }
  }
  else
  {
%><?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<!--
/**
 *  Title: Entity Generator Group Definitions for the General Data Model
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
 *@author David E. Jones (jonesde@ofbiz.org) <%-- *@created    <%=(new Date()).toString()%> --%>
 *@version    1.0
 */
-->

<!DOCTYPE entitygroup [
<!-- ====================== Root Element ======================= -->
<!ELEMENT entitygroup (entity-group*)>

<!-- ================= Children of entitygroup =================== -->
<!ELEMENT entity-group EMPTY>

<!-- ================= Children of entity-group =================== -->
<!ATTLIST entity-group group CDATA #REQUIRED>
<!ATTLIST entity-group entity CDATA #REQUIRED>
]>
<% 
  ModelReader reader = delegator.getModelReader();
  ModelGroupReader groupReader = delegator.getModelGroupReader();

  Map packages = new HashMap();
  TreeSet packageNames = new TreeSet();

  //put the entityNames TreeSets in a HashMap by packageName
  Collection ec = reader.getEntityNames();

  Iterator ecIter = ec.iterator();
  while(ecIter.hasNext()) {
    String eName = (String)ecIter.next();
    ModelEntity ent = reader.getModelEntity(eName);
    TreeSet entities = (TreeSet)packages.get(ent.packageName);
    if(entities == null) {
      entities = new TreeSet();
      packages.put(ent.packageName, entities);
      packageNames.add(ent.packageName);
    }
    entities.add(eName);
  }%>

<entitygroup><%
  Iterator piter = packageNames.iterator();
  while(piter.hasNext()) {
    String pName = (String)piter.next();
    TreeSet entities = (TreeSet)packages.get(pName);
%>

  <!-- ========================================================= -->
  <!-- <%=pName%> -->
  <!-- ========================================================= -->
<%
    Iterator i = entities.iterator();
    while ( i.hasNext() ) {
      String entityName = (String)i.next();
      String groupName = groupReader.getEntityGroupName(entityName);
      if(groupName == null) groupName = "";
%>	
    <entity-group group="<%=groupName%>" entity="<%=entityName%>" /><%
    }
  }%>  
</entitygroup>
<%}%>
