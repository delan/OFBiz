<%@ page import="org.ofbiz.entitygen.*" %>
<%@ page import="java.util.*" %>
<%String ejbName=request.getParameter("ejbName"); String defFileName=request.getParameter("defFileName"); int i;%>
<%Iterator classNamesIterator = null;
  if(ejbName != null && ejbName.length() > 0) { Vector cnVec = new Vector(); cnVec.add(ejbName); classNamesIterator = cnVec.iterator(); }
  else if(defFileName != null) classNamesIterator = DefReader.getEjbNamesIterator(defFileName);
  while(classNamesIterator != null && classNamesIterator.hasNext()) { Entity entity=DefReader.getEntity(defFileName,(String)classNamesIterator.next());
%>
  /**
   *  An HTTP WebEvent handler that updates a <%=entity.ejbName%> entity
   *
   * @param request The HTTP request object for the current JSP or Servlet request.
   * @param response The HTTP response object for the current JSP or Servlet request.
   * @return Return a boolean which specifies whether or not the calling Servlet or JSP should generate its own content. This allows an event to override the default content.
   * @exception javax.servlet.ServletException Standard J2EE Servlet Exception
   * @exception java.rmi.RemoteException Standard RMI Remote Exception
   * @exception java.io.IOException Standard IO Exception
   */
  public static boolean update<%=entity.ejbName%>(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, java.rmi.RemoteException, java.io.IOException
  {
    String updateMode = request.getParameter("UPDATE_MODE");

<%for(i=0;i<entity.fields.size();i++){%><%if(((Field)entity.fields.elementAt(i)).javaType.compareTo("java.lang.String") == 0 || ((Field)entity.fields.elementAt(i)).javaType.compareTo("String") == 0){%>
    String <%=((Field)entity.fields.elementAt(i)).fieldName%> = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>");  <%}else if(((Field)entity.fields.elementAt(i)).javaType.indexOf("Timestamp") >= 0){%>
    String <%=((Field)entity.fields.elementAt(i)).fieldName%>Date = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_DATE");
    String <%=((Field)entity.fields.elementAt(i)).fieldName%>Time = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_TIME");  <%}else{%>
    String <%=((Field)entity.fields.elementAt(i)).fieldName%>String = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>");  <%}%><%}%>
<%for(i=0;i<entity.fields.size();i++){%><%if(((Field)entity.fields.elementAt(i)).javaType.compareTo("java.lang.String") != 0 && ((Field)entity.fields.elementAt(i)).javaType.compareTo("String") != 0){%><%if(((Field)entity.fields.elementAt(i)).javaType.indexOf("Timestamp") >= 0){%>
    Timestamp <%=((Field)entity.fields.elementAt(i)).fieldName%> = UtilTimestamp.toTimestamp(<%=((Field)entity.fields.elementAt(i)).fieldName%>Date, <%=((Field)entity.fields.elementAt(i)).fieldName%>Time);<%}else{%>
    <%=((Field)entity.fields.elementAt(i)).javaType%> <%=((Field)entity.fields.elementAt(i)).fieldName%> = null;
    try
    {
      if(<%=((Field)entity.fields.elementAt(i)).fieldName%>String != null)
      {
        <%=((Field)entity.fields.elementAt(i)).fieldName%> = <%=((Field)entity.fields.elementAt(i)).javaType%>.valueOf(<%=((Field)entity.fields.elementAt(i)).fieldName%>String);
      }
    }
    catch(Exception e)
    {
    }<%}%><%}%><%}%>

    if(updateMode.compareTo("CREATE") == 0)
    {
      if(!Security.hasEntityPermission("<%=entity.tableName%>", "_CREATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to create <%=entity.ejbName%> (<%=entity.tableName%>_CREATE or <%=entity.tableName%>_ADMIN needed).");
        return true;
      }

      <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = <%=entity.ejbName%>Helper.create(<%=entity.fieldNameString()%>);
      if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of <%=entity.ejbName%> failed. <%=entity.colNameString(entity.pks)%>: " + <%=entity.pkNameString(" + \", \" + ", "")%>);
        return true;
      }
    }
    else if(updateMode.compareTo("UPDATE") == 0)
    {
      if(!Security.hasEntityPermission("<%=entity.tableName%>", "_UPDATE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to update <%=entity.ejbName%> (<%=entity.tableName%>_UPDATE or <%=entity.tableName%>_ADMIN needed).");
        return true;
      }

      <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = <%=entity.ejbName%>Helper.update(<%=entity.fieldNameString()%>);
      if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of <%=entity.ejbName%> failed. <%=entity.colNameString(entity.pks)%>: " + <%=entity.pkNameString(" + \", \" + ", "")%>);
        return true;
      }
    }
    else if(updateMode.compareTo("DELETE") == 0)
    {
      if(!Security.hasEntityPermission("<%=entity.tableName%>", "_DELETE", request.getSession()))
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to delete <%=entity.ejbName%> (<%=entity.tableName%>_DELETE or <%=entity.tableName%>_ADMIN needed).");
        return true;
      }

      //Remove associated/dependent entries from other tables here
      //Delete actual <%=GenUtil.lowerFirstChar(entity.ejbName)%> last, just in case database is set up to do a cascading delete, caches won't get cleared
      <%=entity.ejbName%>Helper.removeByPrimaryKey(<%=entity.pkNameString()%>);
    }
    else
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "update<%=entity.ejbName%>: Update Mode specified (" + updateMode + ") was not valid.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("update<%=entity.ejbName%>: Update Mode specified (" + updateMode + ") was not valid.");
      }
    }

    return true;
  }
<%}%>