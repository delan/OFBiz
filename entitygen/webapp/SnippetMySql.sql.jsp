<%@ page import="org.ofbiz.entitygen.*" %>
<%@ page import="java.util.*" %>
<%String ejbName=request.getParameter("ejbName"); String defFileName=request.getParameter("defFileName"); int i;%>
<%Iterator classNamesIterator = null;
  if(ejbName != null && ejbName.length() > 0) { Vector cnVec = new Vector(); cnVec.add(ejbName); classNamesIterator = cnVec.iterator(); }
  else if(defFileName != null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
  while(classNamesIterator != null && classNamesIterator.hasNext()) { EgEntity entity=DefReader.getEgEntity(defFileName,(String)classNamesIterator.next());
%>
DROP TABLE <%=entity.tableName%>;
CREATE TABLE <%=entity.tableName%> (<%for(i=0;i<entity.fields.size();i++){%>
  <%if(((EgField)entity.fields.elementAt(i)).isPk){%>    <%=((EgField)entity.fields.elementAt(i)).columnName%> <%=((EgField)entity.fields.elementAt(i)).sqlType%> NOT NULL,<%}else{%>    <%=((EgField)entity.fields.elementAt(i)).columnName%> <%=((EgField)entity.fields.elementAt(i)).sqlType%>,<%}%><%}%>
  PRIMARY KEY (<%=entity.colNameString(entity.pks)%>));
<%}%>
