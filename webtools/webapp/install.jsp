
<%@ page import="java.util.*, java.io.*, java.net.*, java.sql.*, org.w3c.dom.*"%>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>
<%@ page import="org.ofbiz.core.entity.model.*, org.ofbiz.core.entity.config.*"%>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
  errorMessages = new LinkedList();
  String groupfile = request.getParameter("groupfile");
  String loadFile = request.getParameter("loadFile");
  String paths = "";
  String groupName = request.getParameter("groupName");
  String helperName = null;

  if(groupName != null && groupName.length() > 0) {
    helperName = delegator.getGroupHelperName(groupName);
    Element rootElement = EntityConfigUtil.getXmlRootElement();
    Element datasourceElement = UtilXml.firstChildElement(rootElement, "datasource", "name", helperName);
    List sqlLoadPathElements = UtilXml.childElementList(datasourceElement, "sql-load-path");
    Iterator slpIter = sqlLoadPathElements.iterator();
    while (slpIter.hasNext()) {
        Element sqlLoadPathElement = (Element) slpIter.next();
        String prependEnv = sqlLoadPathElement.getAttribute("prepend-env");
        paths += (paths.length() == 0 ? "" : ";");
        if (prependEnv != null && prependEnv.length() > 0) {
            paths += System.getProperty(prependEnv) + "/";
        }
        paths += sqlLoadPathElement.getAttribute("path");
    }
    //paths = UtilProperties.getPropertyValue("entityengine", helperName + ".sql.load.paths");
  }

  ArrayList fileList = new ArrayList();
  if(paths != null && paths.length() > 0) {
    StringTokenizer tokenizer = new StringTokenizer(paths, ";");
    while (tokenizer.hasMoreTokens()) {
      String path = tokenizer.nextToken().toLowerCase();
      File loadDir = new File(path);
      if(loadDir.exists() && loadDir.isDirectory()) {
        File[] files = loadDir.listFiles();
        List tempFileList = new LinkedList();
        for(int i=0; i<files.length; i++) {
          if(files[i].getName().toLowerCase().endsWith(".sql") || files[i].getName().toLowerCase().endsWith(".xml")) {
            tempFileList.add(files[i]);
          }
        }
        Collections.sort(tempFileList);
        fileList.addAll(tempFileList);
      }
    }
  }
%>
<br>
Specify the group name for the entity group whose data you want to load:<br>
<form method=post action='<ofbiz:url>/install?groupfile=group</ofbiz:url>'>
  Group Name: <INPUT type=text name='groupName' value='<%=groupName!=null?groupName:"org.ofbiz.commonapp"%>' size='60'>
  <INPUT type=submit value='Load Data'>
</form>
<br>
OR Specify the filename of a ".sql" or ".xml" file to load:<br>
<form method=post action='<ofbiz:url>/install?groupfile=file</ofbiz:url>'>
  Server File Path/Name: <INPUT type=text name='loadFile' value='<%=loadFile!=null?loadFile:""%>' size='60'>
  <br>
  Group Name (required if .sql file): <INPUT type=text name='groupName' value='<%=groupName!=null?groupName:"org.ofbiz.commonapp"%>' size='60'>
  <INPUT type=submit value='Load Data File'>
</form>
<div>OR click <a href='<ofbiz:url>/install?groupfile=gensecurity</ofbiz:url>'>here</a> for entity granularity security settings only (auto generated, not in a file)</div>
<hr>
<%if("group".equals(groupfile)) {%>
  <%if(groupName != null && groupName.length() > 0) {%>
    <%if(request.getParameter("loadfiles") == null) {%>
      <br>
      <DIV class='head1'>Open For Business Installation (Data Load) Page</DIV>
      <DIV class='head2'>Do you want to load the following SQL files and generated entity specific data?</DIV>
      <DIV class='tabletext'>(From the path list: "<%=UtilFormatOut.checkNull(paths)%>")</DIV>
      <UL>
        <%if(fileList.size() > 0) {%>
          <%for(int i=0; i<fileList.size(); i++) {%>
            <%File dataFile = (File)fileList.get(i);%>
            <LI><DIV class='tabletext'><%=dataFile.getAbsolutePath()%></DIV>
          <%}%>
        <%}else{%>
          <LI><DIV class='tabletext'>No SQL Files found.</DIV>
        <%}%>
        <LI><DIV class='tabletext'>Entity granularity security settings (auto generated, not in a file)</DIV>
      </UL>
      <A href='<ofbiz:url>/install?loadfiles=true&groupfile=group&groupName=<%=groupName%></ofbiz:url>' class='buttontext'>[Yes, Load Now]</A>
    <%}else{%>
      <br>
      <DIV class='head1'>Open For Business Installation (Data Load) Page</DIV>
      <DIV class='head2'>Loading the SQL files and generated entity specific data...</DIV>
      <DIV class='tabletext'>(From the path list: "<%=UtilFormatOut.checkNull(paths)%>")</DIV>
      <UL>
        <%int totalRowsChanged = 0;%>
        <%if(fileList.size() > 0) {%>
          <%for(int i=0; i<fileList.size(); i++) {%>
            <%File dataFile = (File)fileList.get(i);%>
            <%int rowsChanged = loadData(dataFile, helperName, delegator);%>
            <%totalRowsChanged += rowsChanged;%>
            <LI><DIV class='tabletext'>Loaded <%=rowsChanged%> rows from <%=dataFile.getAbsolutePath()%> (<%=totalRowsChanged%> total rows so far)</DIV>
          <%}%>
        <%}else{%>
          <LI><DIV class='tabletext'>No SQL Files found.</DIV>
        <%}%>
        <%int genRowsChanged = generateData(delegator);%>
         <%totalRowsChanged += genRowsChanged;%>
        <LI><DIV class='tabletext'>Loaded <%=genRowsChanged%> rows for generated entity granularity security settings (<%=totalRowsChanged%> total rows so far)</DIV>
      </UL>
      <DIV class='head2'>Finished loading all data; <%=totalRowsChanged%> total rows updated.</DIV>

      <DIV class='head2'>Error Messages:</DIV>
      <UL>
        <%Iterator errIter = errorMessages.iterator();%>
        <%while(errIter.hasNext()){%>
          <LI><%=(String)errIter.next()%>
        <%}%>
      </UL>

    <%}%>
  <%}%>
<%}else if("file".equals(groupfile)) {%>
  <%if(loadFile != null && loadFile.length() > 0) {%>
    <%File dataFile = new File(loadFile);%>
    <%int rowsChanged = loadData(dataFile, helperName, delegator);%>
    <DIV class='head2'>Finished loading file data; <%=rowsChanged%> total rows updated.</DIV>

    <DIV class='head2'>Error Messages:</DIV>
    <UL>
      <%Iterator errIter = errorMessages.iterator();%>
      <%while(errIter.hasNext()){%>
        <LI><%=(String)errIter.next()%>
      <%}%>
    </UL>
  <%}%>
<%}else if("gensecurity".equals(groupfile)) {%>
    <%int rowsChanged = generateData(delegator);%>
    <DIV class='head2'>Finished loading file data; <%=rowsChanged%> total rows updated.</DIV>

    <DIV class='head2'>Error Messages:</DIV>
    <UL>
      <%Iterator errIter = errorMessages.iterator();%>
      <%while(errIter.hasNext()){%>
        <LI><%=(String)errIter.next()%>
      <%}%>
    </UL>
<%}%>

<%!
  Collection errorMessages = new LinkedList();

  int loadData(File dataFile, String helperName, GenericDelegator delegator) throws GenericEntityException {
    if(!dataFile.exists()) {
      errorMessages.add("[install.loadData] Could not find file: \"" + dataFile.getAbsolutePath() + "\"");
      return 0;
    }
    int rowsChanged = 0;

    if(dataFile.getName().toLowerCase().endsWith(".xml")) {
      Debug.logInfo("[install.loadData] Loading XML File: \"" + dataFile.getAbsolutePath() + "\"");
      URL url = null;
      try { url = dataFile.toURL(); }
      catch(java.net.MalformedURLException e) {
        String xmlError = "[install.loadData]: Error loading XML file \"" + dataFile.getAbsolutePath() + "\"; Error was: " + e.getMessage();
        errorMessages.add(xmlError); Debug.logWarning(xmlError);
      }

      Collection values = null;
      try {
        values = delegator.readXmlDocument(url);
        delegator.storeAll(values);
        rowsChanged += values.size();
      } catch(Exception e) {
        String xmlError = "[install.loadData]: Error loading XML file \"" + dataFile.getAbsolutePath() + "\"; Error was: " + e.getMessage();
        errorMessages.add(xmlError);
        Debug.logWarning(xmlError);
        Debug.logWarning(e);
      }
    } else if(dataFile.getName().toLowerCase().endsWith(".sql")) {
      Debug.logInfo("[install.loadData] Loading SQL File: \"" + dataFile.getAbsolutePath() + "\"");
      Connection connection = null; 
      Statement stmt = null;
      try {
        connection = ConnectionFactory.getConnection(helperName);
        connection.setAutoCommit(true);
        stmt = connection.createStatement();

        String sql = "";
        BufferedReader in = new BufferedReader(new FileReader(dataFile));
        String line;
        while((line = in.readLine()) != null) {
          line = line.trim();
          if(line.startsWith("--")) continue;
          int scind = line.indexOf(';');
          int linePos = 0;
          if(scind >= 0) {
            while(scind >= 0) {
              sql += " ";
              sql += line.substring(linePos, scind);

              //run the sql...
              //rowsChanged += runSql(sql);
              sql = sql.trim();
              if(sql.startsWith("INSERT") || sql.startsWith("insert"))
              {
                //Debug.logInfo("[install.loadData] Running found insert sql: \"" + sql + "\"");
                try {
                  rowsChanged += stmt.executeUpdate(sql);
                } catch (SQLException sqle) {
                  String sqlError = "[install.loadData]: Error running sql:\"" + sql + "\"; Error was: " + sqle.getMessage();
                  errorMessages.add(sqlError);
                  Debug.logWarning(sqlError);
                }
              }

              linePos = scind + 1;
              scind = line.indexOf(';', linePos);
              sql = "";
            }
          } else {
            sql += " ";
            sql += line;
          }
        }
      } catch (Exception e) { 
        String errorMsg = "[install.loadData]: Load error:" +  e.getMessage();
        errorMessages.add(errorMsg);
        Debug.logWarning(errorMsg);
      } finally {
        try { if (stmt != null) stmt.close(); } catch (SQLException sqle) { }
        try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
      }
    }

    return rowsChanged;
  }

  int generateData(GenericDelegator delegator) throws GenericEntityException {
    int rowsChanged = 0;
    ModelReader reader = delegator.getModelReader();
    Collection entityCol = reader.getEntityNames();
    Iterator classNamesIterator = entityCol.iterator();
    while(classNamesIterator != null && classNamesIterator.hasNext()) { 
      ModelEntity entity = reader.getModelEntity((String)classNamesIterator.next());
      String baseName = entity.getTableName();
      if(entity instanceof ModelViewEntity) {
        baseName = ModelUtil.javaNameToDbName(entity.getEntityName());
      }

      if(baseName != null) {
          try {
            List toStore = new LinkedList();
            toStore.add(delegator.makeValue("SecurityPermission", UtilMisc.toMap("permissionId", baseName + "_ADMIN", "description", "Permission to Administer a " + entity.getEntityName() + " entity.")));
            toStore.add(delegator.makeValue("SecurityGroupPermission", UtilMisc.toMap("groupId", "FULLADMIN", "permissionId", baseName + "_ADMIN")));
            delegator.storeAll(toStore);
            rowsChanged += 2;
          } catch (GenericEntityException e) {
            errorMessages.add("[install.generateData] ERROR: Failed Security Generation for entity \"" + baseName + "\"");
          }

          /*
          toStore.add(delegator.makeValue("SecurityPermission", UtilMisc.toMap("permissionId", baseName + "_VIEW", "description", "Permission to View a " + entity.getEntityName() + " entity.")));
          toStore.add(delegator.makeValue("SecurityPermission", UtilMisc.toMap("permissionId", baseName + "_CREATE", "description", "Permission to Create a " + entity.getEntityName() + " entity.")));
          toStore.add(delegator.makeValue("SecurityPermission", UtilMisc.toMap("permissionId", baseName + "_UPDATE", "description", "Permission to Update a " + entity.getEntityName() + " entity.")));
          toStore.add(delegator.makeValue("SecurityPermission", UtilMisc.toMap("permissionId", baseName + "_DELETE", "description", "Permission to Delete a " + entity.getEntityName() + " entity.")));

          toStore.add(delegator.makeValue("SecurityGroupPermission", UtilMisc.toMap("groupId", "FLEXADMIN", "permissionId", baseName + "_VIEW")));
          toStore.add(delegator.makeValue("SecurityGroupPermission", UtilMisc.toMap("groupId", "FLEXADMIN", "permissionId", baseName + "_CREATE")));
          toStore.add(delegator.makeValue("SecurityGroupPermission", UtilMisc.toMap("groupId", "FLEXADMIN", "permissionId", baseName + "_UPDATE")));
          toStore.add(delegator.makeValue("SecurityGroupPermission", UtilMisc.toMap("groupId", "FLEXADMIN", "permissionId", baseName + "_DELETE")));
          */
      }
    }

    return rowsChanged;
  }
%>
