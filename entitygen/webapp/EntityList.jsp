<%@ page import="org.ofbiz.entitygen.*" %>
<%@ page import="java.util.*" %>
<%String ejbName=request.getParameter("ejbName"); String defFileName=request.getParameter("defFileName"); int i;%>
<%String packName=null, lastPackName=null;%>
<%String firstPack=null, lastFirstPack=null;%> 
<%Iterator classNamesIterator = null;
  if(ejbName != null && ejbName.length() > 0) { Vector cnVec = new Vector(); cnVec.add(ejbName); classNamesIterator = cnVec.iterator(); }
  else if(defFileName != null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
  while(classNamesIterator != null && classNamesIterator.hasNext()) { Entity entity=DefReader.getEntity(defFileName,(String)classNamesIterator.next());
%><%if(packName == null){%><%packName = entity.packageName; packName = packName.substring(packName.indexOf(".")+1); packName = packName.substring(packName.indexOf(".")+1);  packName = packName.substring(packName.indexOf(".")+1);%><%firstPack=packName.substring(0,(packName.indexOf(".")>=0?packName.indexOf("."):packName.length()));%>
<ul>
  <li><a href="#<%=GenUtil.upperFirstChar(firstPack)%>"><b><%=GenUtil.upperFirstChar(firstPack)%></b></a>
  <ul>
    <li><b><i><a href="#<%=packName%>"><%=packName%></a></i></b>
  <%}else{%><%lastPackName = packName;%><%packName = entity.packageName; packName = packName.substring(packName.indexOf(".")+1); packName = packName.substring(packName.indexOf(".")+1);  packName = packName.substring(packName.indexOf(".")+1);%><%lastFirstPack = firstPack;%><%firstPack=packName.substring(0,(packName.indexOf(".")>=0?packName.indexOf("."):packName.length()));%><%if(!firstPack.equals(lastFirstPack)){%>
  </ul>
  <li><a href="#<%=GenUtil.upperFirstChar(firstPack)%>"><b><%=GenUtil.upperFirstChar(firstPack)%></b></a>
  <ul>
    <li><b><i><a href="#<%=packName%>"><%=packName%></a></i></b>
  <%}else if(!packName.equals(lastPackName)){%>
    <li><b><i><a href="#<%=packName%>"><%=packName%></a></i></b>
  <%}%><%}%><%}%>
</ul>

<%
  if(ejbName != null && ejbName.length() > 0) { Vector cnVec = new Vector(); cnVec.add(ejbName); classNamesIterator = cnVec.iterator(); }
  else if(defFileName != null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
  while(classNamesIterator != null && classNamesIterator.hasNext()) { Entity entity=DefReader.getEntity(defFileName,(String)classNamesIterator.next());
%><%if(packName == null){%><%packName = entity.packageName; packName = packName.substring(packName.indexOf(".")+1); packName = packName.substring(packName.indexOf(".")+1);  packName = packName.substring(packName.indexOf(".")+1);%><%firstPack=packName.substring(0,(packName.indexOf(".")>=0?packName.indexOf("."):packName.length()));%>
<hr>
<H3><a name="<%=GenUtil.upperFirstChar(firstPack)%>"><%=GenUtil.upperFirstChar(firstPack)%></a></H3>
<hr>
<ul>
  <li><b><i><a name="<%=packName%>"><%=packName%></a></i></b>
  <ul><%}else{%><%lastPackName = packName;%><%packName = entity.packageName; packName = packName.substring(packName.indexOf(".")+1); packName = packName.substring(packName.indexOf(".")+1);  packName = packName.substring(packName.indexOf(".")+1);%><%lastFirstPack = firstPack;%><%firstPack=packName.substring(0,(packName.indexOf(".")>=0?packName.indexOf("."):packName.length()));%><%if(!firstPack.equals(lastFirstPack)){%>
  </ul>
</ul>
<hr>
<H3><a name="<%=GenUtil.upperFirstChar(firstPack)%>"><%=GenUtil.upperFirstChar(firstPack)%></a></H3>
<hr>
<ul>
  <li><b><i><a name="<%=packName%>"><%=packName%></a></i></b>
  <ul><%}else if(!packName.equals(lastPackName)){%>
  </ul>
  <li><b><i><a name="<%=packName%>"><%=packName%></a></i></b>
  <ul><%}%>
    <li><%=entity.ejbName%><%}%><%}%>
  </ul>
</ul>
