<%@ page contentType="text/plain" %><%@ page import="java.util.*" %><%@ page import="org.ofbiz.core.entity.*" %><%@ page import="org.ofbiz.core.entity.model.*" %><jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" /><jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="application" /><%
if(security.hasPermission("ENTITY_MAINT", session)) {
  ModelReader reader = delegator.getModelReader();
  Collection ec = reader.getEntityNames();
  TreeSet entities = new TreeSet(ec);
  Iterator classNamesIterator = entities.iterator();
  while(classNamesIterator != null && classNamesIterator.hasNext()) { ModelEntity entity = reader.getModelEntity((String)classNamesIterator.next());%>
CREATE TABLE <%=entity.tableName%> (<%for(int i=0;i<entity.fields.size();i++){ModelField field=(ModelField)entity.fields.get(i); ModelFieldType type = delegator.getEntityFieldType(entity, field.type);%><%if(field.isPk){%>
  <%=field.colName%> <%=type.sqlType%> NOT NULL,<%}else{%>
  <%=field.colName%> <%=type.sqlType%>,<%}%><%}%>
  PRIMARY KEY (<%=entity.colNameString(entity.pks)%>));
<%}%> 
<%
} 
else {
  %>ERROR: You do not have permission to use this page (ENTITY_MAINT needed)<%
}
%>
