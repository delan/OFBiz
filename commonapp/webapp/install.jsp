
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="org.ofbiz.core.entity.model.*"%>

<% pageContext.setAttribute("PageName", "Install"); %> 
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<%
  String serverName = delegator.getServerName();
  String paths = UtilProperties.getPropertyValue("servers", serverName + ".sql.load.paths");
  
  ArrayList fileList = new ArrayList();
  if(paths != null && paths.length() > 0) {
    StringTokenizer tokenizer = new StringTokenizer(paths, ";");
    while (tokenizer.hasMoreTokens()) {
      String path = tokenizer.nextToken().toLowerCase();
      File loadDir = new File(path);
      if(loadDir.exists() && loadDir.isDirectory()) {
        File[] files = loadDir.listFiles();
        for(int i=0; i<files.length; i++) {
          if(files[i].getName().endsWith(".sql") || files[i].getName().endsWith(".SQL")) {
            fileList.add(files[i]);
          }
        }
      }
    }
  }

  if(request.getParameter("loadfiles") == null) {%>
  <br>
  <DIV class='head1'>Open For Business Installation (Data Load) Page</DIV>
  <DIV class='head2'>Do you want to load the following SQL files and generated entity specific data?</DIV>
  <DIV class='tabletext'>(From the path list: "<%=UtilFormatOut.checkNull(paths)%>")</DIV>
  <UL>
    <%if(fileList.size() > 0) {%>
      <%for(int i=0; i<fileList.size(); i++) {%>
        <%File sqlFile = (File)fileList.get(i);%>
        <LI><DIV class='tabletext'><%=sqlFile.getAbsolutePath()%></DIV>
      <%}%>
    <%}else{%>
      <LI><DIV class='tabletext'>No SQL Files found.</DIV>
    <%}%>
    <LI><DIV class='tabletext'>Entity granularity security settings (auto generated, not in a file)</DIV>
  </UL>
  <A href='<%=response.encodeURL(controlPath + "/install?loadfiles=true")%>' class='buttontext'>[Yes, Load Now]</A>
<%}else{%>
  <br>
  <DIV class='head1'>Open For Business Installation (Data Load) Page</DIV>
  <DIV class='head2'>Loading the SQL files and generated entity specific data...</DIV>
  <DIV class='tabletext'>(From the path list: "<%=UtilFormatOut.checkNull(paths)%>")</DIV>
  <UL>
    <%int totalRowsChanged = 0;%>
    <%if(fileList.size() > 0) {%>
      <%for(int i=0; i<fileList.size(); i++) {%>
        <%File sqlFile = (File)fileList.get(i);%>
        <%int rowsChanged = loadData(sqlFile, serverName);%>
        <%totalRowsChanged += rowsChanged;%>
        <LI><DIV class='tabletext'>Loaded <%=rowsChanged%> rows from <%=sqlFile.getAbsolutePath()%> (<%=totalRowsChanged%> total rows so far)</DIV>
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

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

<%!
  Collection errorMessages = new LinkedList();

  int loadData(File sqlFile, String serverName)
  {
    if(!sqlFile.exists()) return 0;
    Debug.logInfo("[install.loadData] Loading SQL File: \"" + sqlFile.getAbsolutePath() + "\"");

    Connection connection = null; 
    Statement stmt = null;
    int rowsChanged = 0;
    try {
      connection = ConnectionFactory.getConnection(serverName);
      connection.setAutoCommit(true);
      stmt = connection.createStatement();

      String sql = "";
      BufferedReader in = new BufferedReader(new FileReader(sqlFile));
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
              Debug.logInfo("[install.loadData] Running found insert sql: \"" + sql + "\"");
              try {
                rowsChanged += stmt.executeUpdate(sql);
              }
              catch (SQLException sqle) {
                String sqlError = "[install.loadData]: Error running sql:\"" + sql + "\"; Error was: " + sqle.getMessage();
                errorMessages.add(sqlError);
                Debug.logWarning(sqlError);
              }
            }

            linePos = scind + 1;
            scind = line.indexOf(';', linePos);
            sql = "";
          }
        }
        else {
          sql += " ";
          sql += line;
        }
      }
    } 
    catch (Exception e) { 
      String errorMsg = "[install.loadData]: Load error:" +  e.getMessage();
      errorMessages.add(errorMsg);
      Debug.logWarning(errorMsg);
    } 
    finally 
    {
      try { if (stmt != null) stmt.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }

    return rowsChanged;
  }

  int generateData(GenericDelegator delegator)
  {
    int rowsChanged = 0;
    ModelReader reader = delegator.getModelReader();
    Collection entityCol = reader.getEntityNames();
    Iterator classNamesIterator = entityCol.iterator();
    while(classNamesIterator != null && classNamesIterator.hasNext()) { 
      ModelEntity entity = reader.getModelEntity((String)classNamesIterator.next());
      if(delegator.create("SecurityPermission", UtilMisc.toMap("permissionId", entity.tableName + "_ADMIN", "description", "Permission to Administer a " + entity.entityName + " entity.")) != null) rowsChanged++;
      else { String errorMsg = "[install.generateData]: Generated Data Load error for entity \"" + entity.tableName + "\" creating ADMIN SecurityPermission"; errorMessages.add(errorMsg); }
      if(delegator.create("SecurityPermission", UtilMisc.toMap("permissionId", entity.tableName + "_VIEW", "description", "Permission to View a " + entity.entityName + " entity.")) != null) rowsChanged++;
      else { String errorMsg = "[install.generateData]: Generated Data Load error for entity \"" + entity.tableName + "\" creating VIEW SecurityPermission"; errorMessages.add(errorMsg); }
      if(delegator.create("SecurityPermission", UtilMisc.toMap("permissionId", entity.tableName + "_CREATE", "description", "Permission to Create a " + entity.entityName + " entity.")) != null) rowsChanged++;
      else { String errorMsg = "[install.generateData]: Generated Data Load error for entity \"" + entity.tableName + "\" creating CREATE SecurityPermission"; errorMessages.add(errorMsg); }
      if(delegator.create("SecurityPermission", UtilMisc.toMap("permissionId", entity.tableName + "_UPDATE", "description", "Permission to Update a " + entity.entityName + " entity.")) != null) rowsChanged++;
      else { String errorMsg = "[install.generateData]: Generated Data Load error for entity \"" + entity.tableName + "\" creating UPDATE SecurityPermission"; errorMessages.add(errorMsg); }
      if(delegator.create("SecurityPermission", UtilMisc.toMap("permissionId", entity.tableName + "_DELETE", "description", "Permission to Delete a " + entity.entityName + " entity.")) != null) rowsChanged++;
      else { String errorMsg = "[install.generateData]: Generated Data Load error for entity \"" + entity.tableName + "\" creating DELETE SecurityPermission"; errorMessages.add(errorMsg); }

      if(delegator.create("SecurityGroupPermission", UtilMisc.toMap("groupId", "FULLADMIN", "permissionId", entity.tableName + "_ADMIN")) != null) rowsChanged++;
      else { String errorMsg = "[install.generateData]: Generated Data Load error for entity \"" + entity.tableName + "\" creating FULLADMIN SecurityGroupPermission"; errorMessages.add(errorMsg); }
      //if(delegator.create("SecurityGroupPermission", UtilMisc.toMap("groupId", "FLEXADMIN", "permissionId", entity.tableName + "_VIEW")) != null) rowsChanged++;
      //if(delegator.create("SecurityGroupPermission", UtilMisc.toMap("groupId", "FLEXADMIN", "permissionId", entity.tableName + "_CREATE")) != null) rowsChanged++;
      //if(delegator.create("SecurityGroupPermission", UtilMisc.toMap("groupId", "FLEXADMIN", "permissionId", entity.tableName + "_UPDATE")) != null) rowsChanged++;
      //if(delegator.create("SecurityGroupPermission", UtilMisc.toMap("groupId", "FLEXADMIN", "permissionId", entity.tableName + "_DELETE")) != null) rowsChanged++;
    }

    return rowsChanged;
  }
%>
