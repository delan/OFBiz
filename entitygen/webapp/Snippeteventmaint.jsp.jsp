<%@ page import="org.ofbiz.entitygen.*" %>
<%@ page import="java.util.*" %>
<%String ejbName=request.getParameter("ejbName"); String defFileName=request.getParameter("defFileName"); int i;%>
<%Iterator classNamesIterator = null;
  if(ejbName != null && ejbName.length() > 0) { Vector cnVec = new Vector(); cnVec.add(ejbName); classNamesIterator = cnVec.iterator(); }
  else if(defFileName != null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
  while(classNamesIterator != null && classNamesIterator.hasNext()) { Entity entity=DefReader.getEntity(defFileName,(String)classNamesIterator.next());
%>
  [ltp]if(Security.hasEntityPermission("<%=entity.tableName%>", "_VIEW", session)){%>
    [ltp]rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="[ltp]=rowColor%>">
      <TD><%=entity.ejbName%></TD>
      <TD>
        [ltp]if(Security.hasEntityPermission("<%=entity.tableName%>", "_CREATE", session)){%>
          <a href="[ltp]=response.encodeURL("dirname/Edit<%=entity.ejbName%>.jsp")%>" class="buttontext">Create</a>
        [ltp]}%>
      </TD>
      <TD><a href="[ltp]=response.encodeURL("dirname/Find<%=entity.ejbName%>.jsp")%>" class="buttontext">Find</a></TD>
    </TR>
  [ltp]}%><%}%>
