<%@page contentType="text/html"%>
<!--
 *  Copyright (c) 2001 The Open For Business Project and respected authors.
 
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
 * @author David E. Jones (jonesde@ofbiz.org)
 * @version 1.0
-->

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.base.util.*" %>
<%@ page import="org.ofbiz.entity.*, org.ofbiz.entity.util.*, org.ofbiz.entity.datasource.*" %>
<%@ page import="org.ofbiz.entity.model.*, org.ofbiz.entity.jdbc.*" %>

<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />
<%String controlPath=(String)request.getAttribute("_CONTROL_PATH_");%>

<%
if(security.hasPermission("ENTITY_MAINT", session)) {
  boolean addMissing = "true".equals(request.getParameter("addMissing"));
  String option = request.getParameter("option");
  String groupName = request.getParameter("groupName");
  
  Iterator miter = null;
  if(groupName != null && groupName.length() > 0) {
    String helperName = delegator.getGroupHelperName(groupName);

    List messages = new LinkedList();
    //GenericHelper helper = GenericHelperFactory.getHelper(helperName);
    DatabaseUtil dbUtil = new DatabaseUtil(helperName);
    Map modelEntities = delegator.getModelEntityMapByGroup(groupName);
    Set modelEntityNames = new TreeSet(modelEntities.keySet());

    if ("checkupdatetables".equals(option)) {
        dbUtil.checkDb(modelEntities, messages, addMissing);
    } else if ("checkandrepair".equals(option)) {
        List fieldsToRepair = new ArrayList();
        dbUtil.checkDb(modelEntities, fieldsToRepair, messages, false, addMissing);
        if (fieldsToRepair.size() > 0) {
            dbUtil.repairColumnSizeChanges(modelEntities, fieldsToRepair, messages);
        }
    } else if ("removetables".equals(option)) {
        Iterator modelEntityNameIter = modelEntityNames.iterator();
        while (modelEntityNameIter.hasNext()) {
      	    String modelEntityName = (String) modelEntityNameIter.next();
      	    ModelEntity modelEntity = (ModelEntity) modelEntities.get(modelEntityName);
            dbUtil.deleteTable(modelEntity, messages);
        }
    } else if ("removepks".equals(option)) {
        Iterator modelEntityNameIter = modelEntityNames.iterator();
        while (modelEntityNameIter.hasNext()) {
            String modelEntityName = (String) modelEntityNameIter.next();
            ModelEntity modelEntity = (ModelEntity) modelEntities.get(modelEntityName);
            dbUtil.deletePrimaryKey(modelEntity, messages);
        }
    } else if ("createpks".equals(option)) {
        Iterator modelEntityNameIter = modelEntityNames.iterator();
        while (modelEntityNameIter.hasNext()) {
            String modelEntityName = (String) modelEntityNameIter.next();
            ModelEntity modelEntity = (ModelEntity) modelEntities.get(modelEntityName);
            dbUtil.createPrimaryKey(modelEntity, messages);
        }
    } else if ("createfks".equals(option)) {
        Iterator modelEntityNameIter = modelEntityNames.iterator();
        while (modelEntityNameIter.hasNext()) {
      	    String modelEntityName = (String) modelEntityNameIter.next();
      	    ModelEntity modelEntity = (ModelEntity) modelEntities.get(modelEntityName);
            dbUtil.createForeignKeyIndices(modelEntity, messages);
            dbUtil.createForeignKeys(modelEntity, modelEntities, messages);
        }
    } else if ("removefks".equals(option)) {
        Iterator modelEntityNameIter = modelEntityNames.iterator();
        while (modelEntityNameIter.hasNext()) {
      	    String modelEntityName = (String) modelEntityNameIter.next();
      	    ModelEntity modelEntity = (ModelEntity) modelEntities.get(modelEntityName);
            dbUtil.deleteForeignKeyIndices(modelEntity, messages);
            dbUtil.deleteForeignKeys(modelEntity, modelEntities, messages);
        }
    } else if ("createidx".equals(option)) {
        Iterator modelEntityNameIter = modelEntityNames.iterator();
        while (modelEntityNameIter.hasNext()) {
            String modelEntityName = (String) modelEntityNameIter.next();
      	    ModelEntity modelEntity = (ModelEntity) modelEntities.get(modelEntityName);
            dbUtil.createDeclaredIndices(modelEntity, messages);
        }
    } else if ("removeidx".equals(option)) {
        Iterator modelEntityNameIter = modelEntityNames.iterator();
        while (modelEntityNameIter.hasNext()) {
            String modelEntityName = (String) modelEntityNameIter.next();
      	    ModelEntity modelEntity = (ModelEntity) modelEntities.get(modelEntityName);
            dbUtil.deleteDeclaredIndices(modelEntity, messages);
        }
    }
    miter = messages.iterator();
  }
%>

<H3>Check/Update Database</H3>

<form method=post action="<%=response.encodeURL(controlPath + "/view/checkdb")%>">
  <input type="hidden" name="option" value="checkupdatetables"/>
  Group Name: <input type=text class="inputBox" name="groupName" value="<%=groupName!=null?groupName:"org.ofbiz"%>" size="40"/>
  <input type="submit" value="Check Only"/>
</form>
<form method=post action="<%=response.encodeURL(controlPath + "/view/checkdb")%>">
  <input type="hidden" name="option" value="checkupdatetables"/>
  <input type="hidden" name="addMissing" value="true"/>
  Group Name: <input type=text class="inputBox" name="groupName" value="<%=groupName!=null?groupName:"org.ofbiz"%>" size="40"/>
  <input type="submit" value="Check and Add Missing"/>
</form>
<form method=post action="<%=response.encodeURL(controlPath + "/view/checkdb")%>">
  <input type="hidden" name="option" value="checkandrepair"/>
  <input type="hidden" name="addMissing" value="true"/>
  Group Name: <input type=text class="inputBox" name="groupName" value="<%=groupName!=null?groupName:"org.ofbiz"%>" size="40"/>
  <input type="submit" value="Check, Add Missing and Repair Column Sizes"/>
</form>

<p>NOTE: Use the following at your own risk; make sure you know what you are doing before running these...</p>

<H3>Remove All Tables</H3>
<form method=post action="<%=response.encodeURL(controlPath + "/view/checkdb")%>">
  <input type="hidden" name="option" value="removetables"/>
  Group Name: <input type=text class="inputBox" name="groupName" value="<%=groupName!=null?groupName:"org.ofbiz"%>" size="40"/>
  <input type="submit" value="Remove"/>
</form>

<H3>Create/Remove All Primary Keys</H3>
<form method=post action="<%=response.encodeURL(controlPath + "/view/checkdb")%>">
  <input type="hidden" name="option" value="createpks"/>
  Group Name: <input type=text class="inputBox" name="groupName" value="<%=groupName!=null?groupName:"org.ofbiz"%>" size="40"/>
  <input type="submit" value="Create"/>
</form>
<form method=post action="<%=response.encodeURL(controlPath + "/view/checkdb")%>">
  <input type="hidden" name="option" value="removepks"/>
  Group Name: <input type=text class="inputBox" name="groupName" value="<%=groupName!=null?groupName:"org.ofbiz"%>" size="40"/>
  <input type="submit" value="Remove"/>
</form>

<H3>Create/Remove All Declared Indices</H3>
<form method=post action="<%=response.encodeURL(controlPath + "/view/checkdb")%>">
  <input type="hidden" name="option" value="createidx"/>
  Group Name: <input type=text class="inputBox" name="groupName" value="<%=groupName!=null?groupName:"org.ofbiz"%>" size="40"/>
  <input type="submit" value="Create"/>
</form>
<form method=post action="<%=response.encodeURL(controlPath + "/view/checkdb")%>">
  <input type="hidden" name="option" value="removeidx"/>
  Group Name: <input type=text class="inputBox" name="groupName" value="<%=groupName!=null?groupName:"org.ofbiz"%>" size="40"/>
  <input type="submit" value="Remove"/>
</form>

<H3>Create/Remove All Foreign Keys</H3>
<p>NOTE: Foreign keys may also be created in the Check/Update database operation if the check-fks-on-start and other options on the datasource element are setup to do so.</p>
<form method=post action="<%=response.encodeURL(controlPath + "/view/checkdb")%>">
  <input type="hidden" name="option" value="createfks"/>
  Group Name: <input type=text class="inputBox" name="groupName" value="<%=groupName!=null?groupName:"org.ofbiz"%>" size="40"/>
  <input type="submit" value="Create"/>
</form>
<form method=post action="<%=response.encodeURL(controlPath + "/view/checkdb")%>">
  <input type="hidden" name="option" value="removefks"/>
  Group Name: <input type=text class="inputBox" name="groupName" value="<%=groupName!=null?groupName:"org.ofbiz"%>" size="40"/>
  <input type="submit" value="Remove"/>
</form>

<hr>
<ul>
<%while (miter != null && miter.hasNext()) {%>
  <%String message = (String) miter.next();%>
  <li><%=message%></li>
<%}%>
</ul>
<%} else {%>
<H3>Entity Editor</H3>
ERROR: You do not have permission to use this page (ENTITY_MAINT needed)
<%}%>
