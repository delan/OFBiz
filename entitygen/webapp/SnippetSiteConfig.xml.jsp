<%@ page import="org.ofbiz.entitygen.*" %>
<%@ page import="java.util.*" %>
<%String ejbName=request.getParameter("ejbName"); String defFileName=request.getParameter("defFileName"); int i;%>
<%Iterator classNamesIterator = null;
  if(ejbName != null && ejbName.length() > 0) { Vector cnVec = new Vector(); cnVec.add(ejbName); classNamesIterator = cnVec.iterator(); }
  else if(defFileName != null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
  while(classNamesIterator != null && classNamesIterator.hasNext()) { Entity entity=DefReader.getEntity(defFileName,(String)classNamesIterator.next());String packagePath = entity.packageName.replace('.','/'); /* remove the first three folders (usually org/ofbiz/commonapp) */ packagePath = packagePath.substring(packagePath.indexOf("/")+1);packagePath = packagePath.substring(packagePath.indexOf("/")+1);packagePath = packagePath.substring(packagePath.indexOf("/")+1); %>
    <request-map><uri>Find<%=entity.ejbName%></uri><secure>true</secure><auth>true</auth><success>view:Find<%=entity.ejbName%></success></request-map>
    <request-map><uri>View<%=entity.ejbName%></uri><secure>true</secure><auth>true</auth><success>view:View<%=entity.ejbName%></success></request-map>
    <request-map>
        <uri>Update<%=entity.ejbName%></uri><secure>true</secure><auth>true</auth>
        <event-type>java</event-type><event-path><%=entity.packageName%>.<%=entity.ejbName%>WebEvent</event-path><event-invoke>update<%=entity.ejbName%></event-invoke>
        <success>view:View<%=entity.ejbName%></success><error>view:View<%=entity.ejbName%></error>
    </request-map><%}%>
<%if(ejbName != null && ejbName.length() > 0) { Vector cnVec = new Vector(); cnVec.add(ejbName); classNamesIterator = cnVec.iterator(); }
  else if(defFileName != null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
  while(classNamesIterator != null && classNamesIterator.hasNext()) { Entity entity=DefReader.getEntity(defFileName,(String)classNamesIterator.next());String packagePath = entity.packageName.replace('.','/'); /* remove the first three folders (usually org/ofbiz/commonapp) */ packagePath = packagePath.substring(packagePath.indexOf("/")+1);packagePath = packagePath.substring(packagePath.indexOf("/")+1);packagePath = packagePath.substring(packagePath.indexOf("/")+1); %>
    <view-map><view>Find<%=entity.ejbName%></view><mapped-page>/<%=packagePath%>/Find<%=entity.ejbName%>.jsp</mapped-page></view-map>
    <view-map><view>View<%=entity.ejbName%></view><mapped-page>/<%=packagePath%>/View<%=entity.ejbName%>.jsp</mapped-page></view-map><%}%>
