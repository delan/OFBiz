<%@ page contentType="text/plain" %><%@ page import="java.util.*" %><%@ page import="org.ofbiz.core.entity.*" %><%@ page import="org.ofbiz.core.entity.model.*" %><jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" /><jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" /><%
if(security.hasPermission("ENTITY_MAINT", session)) {
  ModelReader reader = delegator.getModelReader();
  Collection ec = reader.getEntityNames();
  TreeSet entities = new TreeSet(ec);
  Iterator classNamesIterator = entities.iterator();
  while(classNamesIterator != null && classNamesIterator.hasNext()) { ModelEntity entity = reader.getModelEntity((String)classNamesIterator.next());%>
DROP TABLE IF EXISTS <%=entity.getTableName()%>;<%}%> 
<%
} 
else {
  %>ERROR: You do not have permission to use this page (ENTITY_MAINT needed)<%
}
%>
