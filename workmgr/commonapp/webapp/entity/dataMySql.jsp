<%@ page contentType="text/plain" %><%@ page import="java.util.*" %><%@ page import="org.ofbiz.core.entity.*" %><%@ page import="org.ofbiz.core.entity.model.*" %><jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" /><jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" /><%
if(security.hasPermission("ENTITY_MAINT", session)) {
  ModelReader reader = delegator.getModelReader();
  Collection ec = reader.getEntityNames();
  TreeSet entities = new TreeSet(ec);
  Iterator classNamesIterator = entities.iterator();
  while(classNamesIterator != null && classNamesIterator.hasNext()) { ModelEntity entity = reader.getModelEntity((String)classNamesIterator.next());%>
INSERT INTO SECURITY_PERMISSION (PERMISSION_ID,DESCRIPTION) VALUES ('<%=entity.tableName%>_ADMIN','Permission to Administer a <%=entity.entityName%> entity.');
INSERT INTO SECURITY_PERMISSION (PERMISSION_ID,DESCRIPTION) VALUES ('<%=entity.tableName%>_VIEW','Permission to View a <%=entity.entityName%> entity.');
INSERT INTO SECURITY_PERMISSION (PERMISSION_ID,DESCRIPTION) VALUES ('<%=entity.tableName%>_CREATE','Permission to Create a <%=entity.entityName%> entity.');
INSERT INTO SECURITY_PERMISSION (PERMISSION_ID,DESCRIPTION) VALUES ('<%=entity.tableName%>_UPDATE','Permission to Update a <%=entity.entityName%> entity.');
INSERT INTO SECURITY_PERMISSION (PERMISSION_ID,DESCRIPTION) VALUES ('<%=entity.tableName%>_DELETE','Permission to Delete a <%=entity.entityName%> entity.');

INSERT INTO SECURITY_GROUP_PERMISSION (GROUP_ID,PERMISSION_ID) VALUES ('FULLADMIN','<%=entity.tableName%>_ADMIN');
INSERT INTO SECURITY_GROUP_PERMISSION (GROUP_ID,PERMISSION_ID) VALUES ('FLEXADMIN','<%=entity.tableName%>_VIEW');
INSERT INTO SECURITY_GROUP_PERMISSION (GROUP_ID,PERMISSION_ID) VALUES ('FLEXADMIN','<%=entity.tableName%>_CREATE');
INSERT INTO SECURITY_GROUP_PERMISSION (GROUP_ID,PERMISSION_ID) VALUES ('FLEXADMIN','<%=entity.tableName%>_UPDATE');
INSERT INTO SECURITY_GROUP_PERMISSION (GROUP_ID,PERMISSION_ID) VALUES ('FLEXADMIN','<%=entity.tableName%>_DELETE');
<%
  }
} 
else {
  %>ERROR: You do not have permission to use this page (ENTITY_MAINT needed)<%
}
%>
