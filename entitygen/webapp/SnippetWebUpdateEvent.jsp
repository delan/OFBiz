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
    // a little check to reprocessing the web event in error cases - would cause infinate loop
    if(request.getAttribute("ERROR_MESSAGE") != null) return true;
    if(request.getSession().getAttribute("ERROR_MESSAGE") != null) return true;    
    String errMsg = "";
    
    String updateMode = request.getParameter("UPDATE_MODE");
    if(updateMode == null || updateMode.length() <= 0)
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "update<%=entity.ejbName%>: Update Mode was not specified, but is required.");
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        System.out.println("update<%=entity.ejbName%>: Update Mode was not specified, but is required.");
      }
    }
    
    //check permissions before moving on...
    if(!Security.hasEntityPermission("<%=entity.tableName%>", "_" + updateMode, request.getSession()))
    {
      request.getSession().setAttribute("ERROR_MESSAGE", "You do not have sufficient permissions to "+ updateMode + " <%=entity.ejbName%> (<%=entity.tableName%>_" + updateMode + " or <%=entity.tableName%>_ADMIN needed).");
      return true;
    }

    //get the primary key parameters...
  <%for(i=0;i<entity.pks.size();i++){Field curField=(Field)entity.pks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
    String <%=curField.fieldName%> = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>");  <%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
    String <%=curField.fieldName%>Date = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>_DATE");
    String <%=curField.fieldName%>Time = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>_TIME");  <%}else{%>
    String <%=curField.fieldName%>String = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>");  <%}%><%}%>

  <%for(i=0;i<entity.pks.size();i++){Field curField=(Field)entity.pks.elementAt(i);%><%if(!curField.javaType.equals("java.lang.String") && !curField.javaType.equals("String")){%><%if(curField.javaType.indexOf("Timestamp") >= 0){%>
    java.sql.Timestamp <%=curField.fieldName%> = UtilDateTime.toTimestamp(<%=curField.fieldName%>Date, <%=curField.fieldName%>Time);
    if(!UtilValidate.isDate(<%=curField.fieldName%>Date)) errMsg = errMsg + "<li><%=curField.columnName%> isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(<%=curField.fieldName%>Time)) errMsg = errMsg + "<li><%=curField.columnName%> isTime failed: " + UtilValidate.isTimeMsg;<%}else if(curField.javaType.equals("java.util.Date") || curField.javaType.equals("Date")){%>
    java.util.Date <%=curField.fieldName%> = UtilDateTime.toDate(<%=curField.fieldName%>Date, <%=curField.fieldName%>Time);
    if(!UtilValidate.isDate(<%=curField.fieldName%>Date)) errMsg = errMsg + "<li><%=curField.columnName%> isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(<%=curField.fieldName%>Time)) errMsg = errMsg + "<li><%=curField.columnName%> isTime failed: " + UtilValidate.isTimeMsg;<%}else{%>
    <%=curField.javaType%> <%=curField.fieldName%> = null;
    try
    {
      if(<%=curField.fieldName%>String != null && <%=curField.fieldName%>String.length() > 0)
      {
        <%=curField.fieldName%> = <%=curField.javaType%>.valueOf(<%=curField.fieldName%>String);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li><%=curField.columnName%> conversion failed: \"" + <%=curField.fieldName%>String + "\" is not a valid <%=curField.javaType%>;
    }<%}%><%}%><%}%>

    //if this is a delete, do that before getting all of the non-pk parameters and validating them
    if(updateMode.equals("DELETE"))
    {
      //Remove associated/dependent entries from other tables here
      //Delete actual <%=entity.ejbName%> last, just in case database is set up to do a cascading delete, caches won't get cleared
      <%=entity.ejbName%>Helper.removeByPrimaryKey(<%=entity.pkNameString()%>);
      return true;
    }

    //get the non-primary key parameters
  <%for(i=0;i<entity.fields.size();i++){Field curField=(Field)entity.fields.elementAt(i);%><%if(!curField.isPk){%><%if(curField.javaType.equals("java.lang.String") || curField.javaType.equals("String")){%>
    String <%=curField.fieldName%> = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>");  <%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
    String <%=curField.fieldName%>Date = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>_DATE");
    String <%=curField.fieldName%>Time = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>_TIME");  <%}else{%>
    String <%=curField.fieldName%>String = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>");  <%}%><%}%><%}%>

  <%for(i=0;i<entity.fields.size();i++){Field curField=(Field)entity.fields.elementAt(i);%><%if(!curField.isPk){%><%if(!curField.javaType.equals("java.lang.String") && !curField.javaType.equals("String")){%><%if(curField.javaType.indexOf("Timestamp") >= 0){%>
    java.sql.Timestamp <%=curField.fieldName%> = UtilDateTime.toTimestamp(<%=curField.fieldName%>Date, <%=curField.fieldName%>Time);
    if(!UtilValidate.isDate(<%=curField.fieldName%>Date)) errMsg = errMsg + "<li><%=curField.columnName%> isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(<%=curField.fieldName%>Time)) errMsg = errMsg + "<li><%=curField.columnName%> isTime failed: " + UtilValidate.isTimeMsg;<%}else if(curField.javaType.equals("java.util.Date") || curField.javaType.equals("Date")){%>
    java.util.Date <%=curField.fieldName%> = UtilDateTime.toDate(<%=curField.fieldName%>Date, <%=curField.fieldName%>Time);
    if(!UtilValidate.isDate(<%=curField.fieldName%>Date)) errMsg = errMsg + "<li><%=curField.columnName%> isDate failed: " + UtilValidate.isDateMsg;
    if(!UtilValidate.isTime(<%=curField.fieldName%>Time)) errMsg = errMsg + "<li><%=curField.columnName%> isTime failed: " + UtilValidate.isTimeMsg;<%}else{%>
    <%=curField.javaType%> <%=curField.fieldName%> = null;
    try
    {
      if(<%=curField.fieldName%>String != null && <%=curField.fieldName%>String.length() > 0)
      {
        <%=curField.fieldName%> = <%=curField.javaType%>.valueOf(<%=curField.fieldName%>String);
      }
    }
    catch(Exception e)
    {
      errMsg = errMsg + "<li><%=curField.columnName%> conversion failed: \"" + <%=curField.fieldName%>String + "\" is not a valid <%=curField.javaType%>;
    }<%}%><%}%><%}%><%}%>

    //if the updateMode is CREATE, check to see if an entity with the specified primary key already exists
    if(updateMode.compareTo("CREATE") == 0)
      if(<%=entity.ejbName%>Helper.findByPrimaryKey(<%=entity.pkNameString()%>) != null) errMsg = errMsg + "<li><%=entity.ejbName%> already exists with <%=entity.colNameString(entity.pks)%>:" + <%=entity.pkNameString(" + \", \" + ", "")%> + "; please change.";

    //Validate parameters...
  <%for(i=0;i<entity.fields.size();i++){Field curField=(Field)entity.fields.elementAt(i);%><%for(int j=0;j<curField.validators.size();j++){String curValidate=(String)curField.validators.elementAt(j);%><%if(!curField.javaType.equals("java.lang.String") && !curField.javaType.equals("String")){%>%>
    if(!UtilValidate.<%=curValidate%>(<%=curField.fieldName%>String)) errMsg = errMsg + "<li><%=curField.columnName%> <%=curValidate%> failed: " + UtilValidate.<%=curValidate%>Msg;<%}else{%>
    if(!UtilValidate.<%=curValidate%>(<%=curField.fieldName%>)) errMsg = errMsg + "<li><%=curField.columnName%> <%=curValidate%> failed: " + UtilValidate.<%=curValidate%>Msg;<%}%><%}%><%}%>

    if(errMsg.length() > 0)
    {
      errMsg = "<br><b>The following error(s) occured:</b><ul>" + errMsg + "</ul>";
      request.setAttribute("ERROR_MESSAGE", errMsg);
      //note that it is much easier to do a RequestDispatcher.forward here instead of a respones.sendRedirect because the sendRedirent will not automatically keep the Parameters...
      RequestDispatcher rd;
      String onErrorPage = request.getParameter("ON_ERROR_PAGE");
      if(onErrorPage != null) rd = request.getRequestDispatcher(onErrorPage);
      else rd = request.getRequestDispatcher("/______InsertEditEntityPathNameHERE______/Edit<%=entity.ejbName%>.jsp");
      rd.forward(request, response);
      return false;
    }

    if(updateMode.equals("CREATE"))
    {
      <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = <%=entity.ejbName%>Helper.create(<%=entity.fieldNameString()%>);
      if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Creation of <%=entity.ejbName%> failed. <%=entity.colNameString(entity.pks)%>: " + <%=entity.pkNameString(" + \", \" + ", "")%>);
        return true;
      }
    }
    else if(updateMode.equals("UPDATE"))
    {
      <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = <%=entity.ejbName%>Helper.update(<%=entity.fieldNameString()%>);
      if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null)
      {
        request.getSession().setAttribute("ERROR_MESSAGE", "Update of <%=entity.ejbName%> failed. <%=entity.colNameString(entity.pks)%>: " + <%=entity.pkNameString(" + \", \" + ", "")%>);
        return true;
      }
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