<%@ page import="org.ofbiz.entitygen.*" %>
<%@ page import="java.util.*" %>
<%String ejbName=request.getParameter("ejbName"); String defFileName=request.getParameter("defFileName"); int i;%>
<%Iterator classNamesIterator = null;
  if(ejbName != null && ejbName.length() > 0) { Vector cnVec = new Vector(); cnVec.add(ejbName); classNamesIterator = cnVec.iterator(); }
  else if(defFileName != null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
  while(classNamesIterator != null && classNamesIterator.hasNext()) { Entity entity=DefReader.getEntity(defFileName,(String)classNamesIterator.next());
%>
    <entity>
      <ejb-name><%=entity.ejbName%></ejb-name>
      <jndi-name><%=entity.packageName%>.<%=entity.ejbName%>Home</jndi-name>
      <configuration-name>Standard CMP EntityBean</configuration-name>
      <resource-ref>
        <res-ref-name>jdbc/MainDataSource</res-ref-name>
        <resource-name>jdbc/MainDataSource</resource-name>
      </resource-ref>
    </entity><%}%>
