<%@ page import="org.ofbiz.entitygen.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%
  String ejbName=request.getParameter("ejbName");
  String outPathName=request.getParameter("outPathName"); 
  String defFileName=request.getParameter("defFileName");
  int i;
%>
<%
  Iterator classNamesIterator = null;
  if(ejbName != null && ejbName.length() > 0) { Vector cnVec = new Vector(); cnVec.add(ejbName); classNamesIterator = cnVec.iterator(); }
  else if(defFileName != null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
  while(classNamesIterator != null && classNamesIterator.hasNext())
  //if(classNamesIterator != null && classNamesIterator.hasNext())
  { 
    EgEntity entity = DefReader.getEgEntity(defFileName,(String)classNamesIterator.next());
    HashMap params = new HashMap();
    params.put("defFileName", defFileName);
    params.put("ejbName", entity.ejbName);

    String sep = File.separator;
    String filePath = outPathName;
    if(!filePath.endsWith(sep)) filePath = filePath + sep;
    String packagePath = GenUtil.packageToPath(entity.packageName);
    //remove the first two folders (usually org/ and ofbiz/)
    packagePath = packagePath.substring(packagePath.indexOf(sep)+1);
    packagePath = packagePath.substring(packagePath.indexOf(sep)+1);
    //remove the next folder too for JSPs, that folder will be the name of the webapp mount point
    //--actually don't, need away to separate different apps, even in the same def file...
    //packagePath = packagePath.substring(packagePath.indexOf(sep)+1);
    filePath = filePath + packagePath;

%> <b>Creating files for <%=entity.ejbName%></b><ul> <%

    java.net.URL url;
    String fileName;
    String codeString;

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/FindEntity.jsp.jsp");
    fileName = "Find" + entity.ejbName + ".jsp";
    codeString = GenUtil.getCodeFromUrl(url,params);
    if(codeString != null && codeString.length() > 0)
    {
      %> <li>Successfully Retrieved URL: <%=url.toString() + "?" + params%> <%
      //before writing file, replace all "[ltp]" instances with a "<%"
      codeString = GenUtil.replaceString(codeString, "[ltp]", "<%");
      if(GenUtil.writeFile(filePath,fileName,codeString))
      {
        Debug.logInfo("Successfully Created file: " + filePath + sep + fileName);
        %> <li>Successfully Created file: <%=filePath + sep + fileName%> <%
      }
      else
      {
        Debug.logWarning("Failed to Create file: " + filePath + sep + fileName);
        %> <li>Failed to Create file: <%=filePath + sep + fileName%> <%
      }
    }
    else
    {
      %> <li>Retrieved empty or null String from URL: <%=url.toString() + "?" + params%> <%
    }

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/ViewEntity.jsp.jsp");
    fileName = "View" + entity.ejbName + ".jsp";
    codeString = GenUtil.getCodeFromUrl(url,params);
    if(codeString != null && codeString.length() > 0)
    {
      %> <li>Successfully Retrieved URL: <%=url.toString() + "?" + params%> <%
      //before writing file, replace all "[ltp]" instances with a "<%"
      codeString = GenUtil.replaceString(codeString, "[ltp]", "<%");
      if(GenUtil.writeFile(filePath,fileName,codeString))
      {
        Debug.logInfo("Successfully Created file: " + filePath + sep + fileName);
        %> <li>Successfully Created file: <%=filePath + sep + fileName%> <%
      }
      else
      {
        Debug.logWarning("Failed to Create file: " + filePath + sep + fileName);
        %> <li>Failed to Create file: <%=filePath + sep + fileName%> <%
      }
    }
    else
    {
      %> <li>Retrieved empty or null String from URL: <%=url.toString() + "?" + params%> <%
    }

%> </ul> <%
  }
%>
