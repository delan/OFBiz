<%@ page contentType="text/plain" %><%@ page import="java.util.*" %><%@ page import="org.ofbiz.core.entity.*" %><%@ page import="org.ofbiz.core.entity.model.*" %><jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="application" /><%
  ModelReader reader = delegator.getModelReader();
  Collection ec = reader.getEntityNames();
  TreeSet entities = new TreeSet(ec);
  Iterator classNamesIterator = entities.iterator();
  while(classNamesIterator != null && classNamesIterator.hasNext()) { ModelEntity entity = reader.getModelEntity((String)classNamesIterator.next());%>
DROP TABLE IF EXISTS <%=entity.tableName%>;
CREATE TABLE <%=entity.tableName%> (<%for(int i=0;i<entity.fields.size();i++){ModelField field=(ModelField)entity.fields.get(i); ModelFieldType type = reader.getModelFieldType(field.type);%><%if(field.isPk){%>
  <%=field.colName%> <%=type.sqlType%> NOT NULL,<%}else{%>
  <%=field.colName%> <%=type.sqlType%>,<%}%><%}%>
  PRIMARY KEY (<%=entity.colNameString(entity.pks)%>));
<%}%> 
