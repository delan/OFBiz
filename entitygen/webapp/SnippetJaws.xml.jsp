<%@ page import="org.ofbiz.entitygen.*" %>
<%@ page import="java.util.*" %>
<%String ejbName=request.getParameter("ejbName"); String defFileName=request.getParameter("defFileName"); int i;%>
<%Iterator classNamesIterator = null;
  if(ejbName != null && ejbName.length() > 0) { Vector cnVec = new Vector(); cnVec.add(ejbName); classNamesIterator = cnVec.iterator(); }
  else if(defFileName != null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
  while(classNamesIterator != null && classNamesIterator.hasNext()) { EgEntity entity=DefReader.getEgEntity(defFileName,(String)classNamesIterator.next());
%>
    <entity>
      <ejb-name><%=entity.ejbName%></ejb-name>
      <table-name><%=entity.tableName%></table-name>
      <create-table>true</create-table>
      <remove-table>false</remove-table>
      <tuned-updates>false</tuned-updates>
      <read-only>false</read-only>
      <time-out>300</time-out>
      <%for(i=0;i<entity.fields.size();i++){%>
      <cmp-field>
        <field-name><%=((EgField)entity.fields.elementAt(i)).fieldName%></field-name>
        <column-name><%=((EgField)entity.fields.elementAt(i)).columnName%></column-name>
      </cmp-field><%}%>
      <%if(entity.allOrderBy != null && entity.allOrderBy.length() > 0){%>
      <finder>
        <name>findAll</name>
        <query></query>
        <order><%=entity.allOrderBy%></order>
      </finder><%}%><%for(i=0;i<entity.finders.size();i++){%><%EgFinder finderDesc = (EgFinder)entity.finders.elementAt(i);%>
      <finder>
        <name>findBy<%=entity.classNameString(finderDesc.fields,"And","")%></name>
        <query><%=entity.finderQueryString(finderDesc.fields)%></query>
        <order><%=finderDesc.orderBy%></order>
      </finder><%}%>
    </entity><%}%>
