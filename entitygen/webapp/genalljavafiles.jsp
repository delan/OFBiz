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
    filePath = filePath + GenUtil.packageToPath(entity.packageName);

%> <b>Creating files for <%=entity.ejbName%></b><ul> <%

    java.net.URL url;
    String fileName;
    String codeString;

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/Entity.java.jsp");
    fileName = entity.ejbName + ".java";
    codeString = GenUtil.getCodeFromUrl(url,params);
    if(codeString != null && codeString.length() > 0)
    {
      %> <li>Successfully Retrieved URL: <%=url.toString() + "?" + params%> <%
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

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/EntityHome.java.jsp");
    fileName = entity.ejbName + "Home.java";
    codeString = GenUtil.getCodeFromUrl(url,params);
    if(codeString != null && codeString.length() > 0)
    {
      %> <li>Successfully Retrieved URL: <%=url.toString() + "?" + params%> <%
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

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/EntityBean.java.jsp");
    fileName = entity.ejbName + "Bean.java";
    codeString = GenUtil.getCodeFromUrl(url,params);
    if(codeString != null && codeString.length() > 0)
    {
      %> <li>Successfully Retrieved URL: <%=url.toString() + "?" + params%> <%
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

    //we only want to generate the PK java file if the entity has multiple primary keys...
    if(entity.pks.size() > 1)
    {
      url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/EntityPK.java.jsp");
      fileName = entity.ejbName + "PK.java";
      codeString = GenUtil.getCodeFromUrl(url,params);
      if(codeString != null && codeString.length() > 0)
      {
        %> <li>Successfully Retrieved URL: <%=url.toString() + "?" + params%> <%
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
    }

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/EntityValue.java.jsp");
    fileName = entity.ejbName + "Value.java";
    codeString = GenUtil.getCodeFromUrl(url,params);
    if(codeString != null && codeString.length() > 0)
    {
      %> <li>Successfully Retrieved URL: <%=url.toString() + "?" + params%> <%
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

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/EntityHelper.java.jsp");
    fileName = entity.ejbName + "Helper.java";
    codeString = GenUtil.getCodeFromUrl(url,params);
    if(codeString != null && codeString.length() > 0)
    {
      %> <li>Successfully Retrieved URL: <%=url.toString() + "?" + params%> <%
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

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/EntityWebEvent.java.jsp");
    fileName = entity.ejbName + "WebEvent.java";
    codeString = GenUtil.getCodeFromUrl(url,params);
    if(codeString != null && codeString.length() > 0)
    {
      %> <li>Successfully Retrieved URL: <%=url.toString() + "?" + params%> <%
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
