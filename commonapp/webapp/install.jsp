
<%@ page import="java.io.*"%>

<% pageContext.setAttribute("PageName", "Install"); %> 
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<%
  String serverName = helper.getServerName();
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
        <%int rowsChanged = loadData(sqlFile);%>
        <%totalRowsChanged += rowsChanged;%>
        <LI><DIV class='tabletext'>Loaded <%=rowsChanged%> rows from <%=sqlFile.getAbsolutePath()%> (<%=totalRowsChanged%> total rows so far)</DIV>
      <%}%>
    <%}else{%>
      <LI><DIV class='tabletext'>No SQL Files found.</DIV>
    <%}%>
    <LI><DIV class='tabletext'>Entity granularity security settings (auto generated, not in a file)</DIV>
  </UL>
  <DIV class='head2'>Finished loading all data; <%=totalRowsChanged%> total rows updated.</DIV>

<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>

<%!
  int loadData(File sqlFile)
  {
    return 0;
  }
%>
