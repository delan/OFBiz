<%@ page import="org.ofbiz.entitygen.*" %>
<%@ page import="java.util.*" %>
<%String ejbName=request.getParameter("ejbName"); String defFileName=request.getParameter("defFileName"); int i;%>
<%Iterator classNamesIterator = null;
  if(ejbName != null && ejbName.length() > 0) { Vector cnVec = new Vector(); cnVec.add(ejbName); classNamesIterator = cnVec.iterator(); }
  else if(defFileName != null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
  while(classNamesIterator != null && classNamesIterator.hasNext()) { Entity entity=DefReader.getEntity(defFileName,(String)classNamesIterator.next());
%>
INSERT INTO SECURITY_PERMISSION (PERMISSION_ID,DESCRIPTION) VALUES ('<%=entity.tableName%>_ADMIN','Permission to Administer a <%=entity.ejbName%> entity.');
INSERT INTO SECURITY_PERMISSION (PERMISSION_ID,DESCRIPTION) VALUES ('<%=entity.tableName%>_VIEW','Permission to View a <%=entity.ejbName%> entity.');
INSERT INTO SECURITY_PERMISSION (PERMISSION_ID,DESCRIPTION) VALUES ('<%=entity.tableName%>_CREATE','Permission to Create a <%=entity.ejbName%> entity.');
INSERT INTO SECURITY_PERMISSION (PERMISSION_ID,DESCRIPTION) VALUES ('<%=entity.tableName%>_UPDATE','Permission to Update a <%=entity.ejbName%> entity.');
INSERT INTO SECURITY_PERMISSION (PERMISSION_ID,DESCRIPTION) VALUES ('<%=entity.tableName%>_DELETE','Permission to Delete a <%=entity.ejbName%> entity.');

INSERT INTO SECURITY_GROUP_PERMISSION (GROUP_ID,PERMISSION_ID) VALUES ('FULLADMIN','<%=entity.tableName%>_ADMIN');
INSERT INTO SECURITY_GROUP_PERMISSION (GROUP_ID,PERMISSION_ID) VALUES ('FLEXADMIN','<%=entity.tableName%>_VIEW');
INSERT INTO SECURITY_GROUP_PERMISSION (GROUP_ID,PERMISSION_ID) VALUES ('FLEXADMIN','<%=entity.tableName%>_CREATE');
INSERT INTO SECURITY_GROUP_PERMISSION (GROUP_ID,PERMISSION_ID) VALUES ('FLEXADMIN','<%=entity.tableName%>_UPDATE');
INSERT INTO SECURITY_GROUP_PERMISSION (GROUP_ID,PERMISSION_ID) VALUES ('FLEXADMIN','<%=entity.tableName%>_DELETE');
<%}%>

