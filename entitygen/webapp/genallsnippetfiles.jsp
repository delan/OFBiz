<%@ page import="org.ofbiz.entitygen.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%
  String ejbName=request.getParameter("ejbName");
  if(ejbName == null) ejbName = "";
  String outPathName=request.getParameter("outPathName"); 
  String defFileName=request.getParameter("defFileName");
  int i;
%>
<ul>
<%

    HashMap params = new HashMap();
    params.put("defFileName", defFileName);
    if(ejbName != null && ejbName.length() > 0) params.put("ejbName", ejbName);

    String sep = File.separator;
    String filePath = outPathName;

    java.net.URL url;
    String fileName;
    String codeString;

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/SnippetSiteConfig.xml.jsp");
    fileName = ejbName + "siteconfig.xml";
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

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/SnippetMySql.sql.jsp");
    fileName = ejbName + "mysql.sql";
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

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/SnippetDataMySql.sql.jsp");
    fileName = ejbName + "data-mysql.sql";
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

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/SnippetEjbJar.xml.enterprise-beans.jsp");
    fileName = ejbName + "ejb-jar.enterprise-beans.xml";
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

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/SnippetEjbJar.xml.assembly-descriptor.jsp");
    fileName = ejbName + "ejb-jar.assembly-descriptor.xml";
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

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/SnippetJboss.xml.jsp");
    fileName = ejbName + "jboss.xml";
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

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/SnippetJaws.xml.jsp");
    fileName = ejbName + "jaws.xml";
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

    url = new java.net.URL("http",request.getServerName(),request.getServerPort(),request.getContextPath() + "/Snippeteventmaint.jsp.jsp");
    fileName = ejbName + "entitymaint.jsp";
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

%> </ul>
