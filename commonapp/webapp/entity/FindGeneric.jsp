<%
/**
 *  Title: Generic Find User Interface for Generic Entities
 *  Description: none
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a 
 *  copy of this software and associated documentation files (the "Software"), 
 *  to deal in the Software without restriction, including without limitation 
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *  and/or sell copies of the Software, and to permit persons to whom the 
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included 
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT 
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *@author     <a href='mailto:jonesde@ofbiz.org'>David E. Jones (jonesde@ofbiz.org)</a>
 *@created    Aug 18 2001
 *@version    1.0
 */
%>

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.entity.model.*" %>

<%pageContext.setAttribute("PageName", "FindGeneric"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%String entityName=request.getParameter("entityName");%>
<%ModelReader reader = delegator.getModelReader();%>
<%ModelEntity entity = reader.getModelEntity(entityName);%>

<%boolean hasViewPermission=security.hasEntityPermission(entity.tableName, "_VIEW", session);%>
<%boolean hasCreatePermission=security.hasEntityPermission(entity.tableName, "_CREATE", session);%>
<%boolean hasUpdatePermission=security.hasEntityPermission(entity.tableName, "_UPDATE", session);%>
<%boolean hasDeletePermission=security.hasEntityPermission(entity.tableName, "_DELETE", session);%>
<%if(hasViewPermission){%>
<%
  String rowClassTop1 = "viewOneTR1";
  String rowClassTop2 = "viewOneTR2";
  String rowClassTop = "";
  String rowClassResultIndex = "viewOneTR2";
  String rowClassResultHeader = "viewOneTR1";
  String rowClassResult1 = "viewManyTR1";
  String rowClassResult2 = "viewManyTR2";
  String rowClassResult = "";

  String find = request.getParameter("find");
  if(find == null) find="false";
  String curFindString = "entityName=" + entityName + "&find=" + find;
  GenericEntity findByEntity = new GenericEntity(entity);
  for(int fnum=0; fnum<entity.fields.size(); fnum++)
  {
    ModelField field = (ModelField)entity.fields.get(fnum);
    String fval = request.getParameter(field.name);
    if(fval != null)
    {
      if(fval.length() > 0)
      {
        curFindString = curFindString + "&" + field.name + "=" + fval;
        findByEntity.setString(field.name, fval);
      }
    }
  }
  curFindString = UtilFormatOut.encodeQuery(curFindString);

  Collection resultCol = null;
  Object[] resultArray = (Object[])session.getAttribute("CACHE_SEARCH_RESULTS");
%>
<%
//--------------
  String viewIndexString = (String)request.getParameter("VIEW_INDEX");
  if (viewIndexString == null || viewIndexString.length() == 0) { viewIndexString = "0"; }
  int viewIndex = 0;
  try { viewIndex = Integer.valueOf(viewIndexString).intValue(); }
  catch (NumberFormatException nfe) { viewIndex = 0; }

  String viewSizeString = (String)request.getParameter("VIEW_SIZE");
  if (viewSizeString == null || viewSizeString.length() == 0) { viewSizeString = "10"; }
  int viewSize = 10;
  try { viewSize = Integer.valueOf(viewSizeString).intValue(); }
  catch (NumberFormatException nfe) { viewSize = 10; }

//--------------
  String resultArrayName = (String)session.getAttribute("CACHE_SEARCH_RESULTS_NAME");
  if(resultArray == null || resultArrayName == null || curFindString.compareTo(resultArrayName) != 0 || viewIndex == 0)
  {
    Debug.logInfo("-=-=-=-=- Current Array not found in session, getting new one...");
    Debug.logInfo("-=-=-=-=- curFindString:" + curFindString + " resultArrayName:" + resultArrayName);

    if("true".equals(find))
    {
      resultCol = delegator.findByAnd(findByEntity.entityName, findByEntity.getAllFields(), null);
      if(resultCol != null) resultArray = resultCol.toArray();
    }
    else
    {
      resultCol = new LinkedList();
      resultArray = resultCol.toArray();
    }

    if(resultArray != null)
    {
      session.setAttribute("CACHE_SEARCH_RESULTS", resultArray);
      session.setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
    }
  }
//--------------
  int lowIndex = viewIndex*viewSize+1;
  int highIndex = (viewIndex+1)*viewSize;
  int arraySize = 0;
  if(resultArray!=null) arraySize = resultArray.length;
  if(arraySize<highIndex) highIndex=arraySize;
  //Debug.logInfo("viewIndex=" + viewIndex + " lowIndex=" + lowIndex + " highIndex=" + highIndex + " arraySize=" + arraySize);
%>
<h3 style='margin:0;'>Find <%=entity.entityName%>s</h3>
Note: you may use the '%' character as a wildcard for String fields.
<br>To find ALL <%=entity.entityName%>s, leave all entries blank.
<form method="post" action="<%=response.encodeURL(controlPath + "/FindGeneric?entityName=" + entityName)%>" style='margin:0;'>
<INPUT type=hidden name='find' value='true'>
<table cellpadding="2" cellspacing="2" border="0">
  <%for(int fnum=0; fnum<entity.fields.size(); fnum++){%>
    <%ModelField field = (ModelField)entity.fields.get(fnum);%>
    <%ModelFieldType type = field.modelFieldType;%>
    <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
      <td valign="top"><%=field.name%>(<%=type.javaType%>,<%=type.sqlType%>):</td>
      <td valign="top">
        <input type="text" name="<%=field.name%>" value="" size="40">
      </td>
    </tr>
  <%}%>
  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top"><input type="submit" value="Find"></td>
  </tr>
</table>
</form>
<b><%=entity.entityName%>s found by: <%=findByEntity.toString()%></b><br>
<b><%=entity.entityName%>s curFindString: <%=curFindString%></b><br>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewGeneric?entityName=" + entityName)%>" class="buttontext">[Create New <%=entity.entityName%>]</a>
<%}%>
<table border="0" width="100%" cellpadding="2">
<% if(arraySize > 0) { %>
    <tr class="<%=rowClassResultIndex%>">
      <td align="left">
        <b>
        <% if(viewIndex > 0) { %>
          <a href="<%=response.encodeURL(controlPath + "/FindGeneric?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL(controlPath + "/FindGeneric?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<%}%>
</table>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
    <%for(int fnum=0;fnum<entity.fields.size();fnum++){%>
      <%ModelField field = (ModelField)entity.fields.get(fnum);%>
      <td nowrap><div class="tabletext"><b><%=field.name%></b></div></td>
    <%}%>
      <td>&nbsp;</td>
      <%if(hasDeletePermission){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
<%
 if(resultArray != null && resultArray.length > 0)
 {
  int loopIndex;
  //for(loopIndex=resultArray.length-1; loopIndex>=0 ; loopIndex--)
  for(loopIndex=lowIndex; loopIndex<=highIndex; loopIndex++)
  {
    GenericValue value = (GenericValue)resultArray[loopIndex-1];
    if(value != null)
    {
%>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
    <%for(int fnum=0;fnum<entity.fields.size();fnum++){%>
      <%ModelField field = (ModelField)entity.fields.get(fnum);%>
      <%ModelFieldType type = field.modelFieldType;%>
      <td>
        <div class="tabletext">
      <%if(type.javaType.equals("Timestamp") || type.javaType.equals("java.sql.Timestamp")){%>
        <%=UtilDateTime.toDateTimeString((java.sql.Timestamp)value.get(field.name))%>
      <%} else if(type.javaType.equals("Date") || type.javaType.equals("java.sql.Date")){%>
        <%=UtilDateTime.toDateString((java.sql.Date)value.get(field.name))%>
      <%} else if(type.javaType.equals("Time") || type.javaType.equals("java.sql.Time")){%>
        <%=UtilDateTime.toTimeString((java.sql.Time)value.get(field.name))%>
      <%}else if(type.javaType.indexOf("Integer") >= 0){%>
        <%=UtilFormatOut.formatQuantity((Integer)value.get(field.name))%>
      <%}else if(type.javaType.indexOf("Long") >= 0){%>
        <%=UtilFormatOut.formatQuantity((Long)value.get(field.name))%>
      <%}else if(type.javaType.indexOf("Double") >= 0){%>
        <%=UtilFormatOut.formatQuantity((Double)value.get(field.name))%>
      <%}else if(type.javaType.indexOf("Float") >= 0){%>
        <%=UtilFormatOut.formatQuantity((Float)value.get(field.name))%>
      <%}else if(type.javaType.indexOf("String") >= 0){%>
        <%=UtilFormatOut.checkNull((String)value.get(field.name))%>
      <%}%>
        &nbsp;</div>
      </td><%}%>
      <td>
        <%
          String findString = "entityName=" + entityName;
          for(int pknum=0; pknum<entity.pks.size(); pknum++)
          {
            ModelField pkField = (ModelField)entity.pks.get(pknum);
            findString = findString + "&" + pkField.name + "=" + value.get(pkField.name);
          }
        %>
        <a href="<%=response.encodeURL(controlPath + "/ViewGeneric?" + findString)%>" class="buttontext">[View]</a>
      </td>
      <%if(hasDeletePermission){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateGeneric?" + findString + "&UPDATE_MODE=DELETE&" + curFindString)%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
  <%}%>
<%
   }
 }
 else
 {
%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="<%=entity.fields.size() + 2%>">
<h3>No <%=entity.entityName%>s Found.</h3>
</td>
</tr>
<%}%>
</table>

<table border="0" width="100%" cellpadding="2">
<% if(arraySize > 0) { %>
    <tr class="<%=rowClassResultIndex%>">
      <td align="left">
        <b>
        <% if(viewIndex > 0) { %>
          <a href="<%=response.encodeURL(controlPath + "/FindGeneric?entityName=" + entityName + "&" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL(controlPath + "/FindGeneric?entityName=" + entityName + "&" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewGeneric?entityName=" + entityName)%>" class="buttontext">[Create <%=entity.entityName%>]</a>
<%}%>
<%}else{%>
  <h3>You do not have permission to view this page (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
