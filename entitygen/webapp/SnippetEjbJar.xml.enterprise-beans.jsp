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
      <home><%=entity.packageName%>.<%=entity.ejbName%>Home</home>
      <remote><%=entity.packageName%>.<%=entity.ejbName%></remote>
      <ejb-class><%=entity.packageName%>.<%=entity.ejbName%>Bean</ejb-class>
      <persistence-type>Container</persistence-type>
      <prim-key-class><%=entity.primKeyClass%></prim-key-class>
      <reentrant>False</reentrant>
<%for(i=0;i<entity.fields.size();i++){%>
      <cmp-field>
        <field-name><%=((Field)entity.fields.elementAt(i)).fieldName%></field-name>
      </cmp-field><%}%>
<%if(entity.pks.size()==1){%>
      <primkey-field><%=((Field)entity.pks.elementAt(0)).fieldName%></primkey-field><%}%>
      <resource-ref>
        <res-ref-name>jdbc/MainDataSource</res-ref-name>
        <res-type>javax.sql.DataSource</res-type>
        <res-auth>Container</res-auth>
      </resource-ref>
    </entity><%}%>
