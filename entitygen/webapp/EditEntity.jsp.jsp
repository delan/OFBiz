<%@ include file="EntitySetup.jsp" %>
[ltp]
/**
 *  Title: <%=entity.title%>
 *  Description: <%=entity.description%>
 *  <%=entity.copyright%>
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
 *@author     <%=entity.author%>
 *@created    <%=(new java.util.Date()).toString()%>
 *@version    <%=entity.version%>
 */
%>

[ltp]@ page import="<%=entity.packageName%>.*" %>
[ltp]@ page import="java.util.*" %>
[ltp]@ page import="org.ofbiz.commonapp.common.*" %>
[ltp]@ page import="org.ofbiz.commonapp.webevent.*" %>
[ltp]@ page import="org.ofbiz.commonapp.security.*" %>

[ltp]@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

[ltp]pageContext.setAttribute("PageName", "Edit<%=entity.ejbName%>"); %>

[ltp]@ include file="/includes/header.jsp" %>
[ltp]@ include file="/includes/onecolumn.jsp" %>

[ltp]boolean hasCreatePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_CREATE", session);%>
[ltp]boolean hasUpdatePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_UPDATE", session);%>
[ltp]boolean hasDeletePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_DELETE", session);%>
[ltp]if(hasCreatePermission || hasUpdatePermission){%>

[ltp]
  boolean showFields = true;
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";
<%for(i=0;i<entity.pks.size();i++){%>
  <%if(((Field)entity.pks.elementAt(i)).javaType.compareTo("java.lang.String") == 0 || ((Field)entity.pks.elementAt(i)).javaType.compareTo("String") == 0){%>  String <%=((Field)entity.pks.elementAt(i)).fieldName%> = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>");<%}else{%>  String <%=((Field)entity.pks.elementAt(i)).fieldName%>String = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>");<%}%><%}%>
<%for(i=0;i<entity.pks.size();i++){%>
  <%if(((Field)entity.pks.elementAt(i)).javaType.compareTo("java.lang.String") != 0 && ((Field)entity.pks.elementAt(i)).javaType.compareTo("String") != 0){%>
    <%=((Field)entity.pks.elementAt(i)).javaType%> <%=((Field)entity.pks.elementAt(i)).fieldName%> = null;
    try
    {
      if(<%=((Field)entity.pks.elementAt(i)).fieldName%>String != null)
      {
        <%=((Field)entity.pks.elementAt(i)).fieldName%> = new <%=((Field)entity.pks.elementAt(i)).javaType%>(<%=((Field)entity.pks.elementAt(i)).fieldName%>String);
      }
    }
    catch(Exception e)
    {
    }<%}%><%}%>

  <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = <%=entity.ejbName%>Helper.findByPrimaryKey(<%=entity.pkNameString()%>);
%>

<a href="Find<%=entity.ejbName%>.jsp" class="buttontext">[Find <%=entity.ejbName%>]</a>
[ltp]if(hasCreatePermission){%>
  <a href="Edit<%=entity.ejbName%>.jsp" class="buttontext">[Create <%=entity.ejbName%>]</a>
[ltp]}%>
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
  [ltp]if(hasDeletePermission){%>
    <a href="Edit<%=entity.ejbName%>.jsp?WEBEVENT=UPDATE_<%=entity.tableName%>&UPDATE_MODE=DELETE&<%=entity.httpArgList(entity.pks)%>" class="buttontext">[Delete this <%=entity.ejbName%>]</a>
  [ltp]}%>
[ltp]}%>
[ltp]if(<%=entity.pkNameString(" != null && ", " != null")%>){%>
  <a href="View<%=entity.ejbName%>.jsp?<%=entity.httpArgList(entity.pks)%>" class="buttontext">[View <%=entity.ejbName%> Details]</a>
[ltp]}%>
<br>

<form action="Edit<%=entity.ejbName%>.jsp" method="POST" name="updateForm">
<table cellpadding="2" cellspacing="2" border="0">

[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null){%>
  [ltp]if(<%=entity.pkNameString(" != null || ", " != null")%>){%>
    <%=entity.ejbName%> with (<%=entity.colNameString(entity.pks)%>: [ltp]=<%=entity.pkNameString("%" + ">, [ltp]=", "%" + ">")%>) not found. 
    [ltp]if(hasCreatePermission){%>
      You may create a <%=entity.ejbName%> by entering the values you want, and clicking Update.
      <input type="hidden" name="WEBEVENT" value="UPDATE_<%=entity.tableName%>">
      <input type="hidden" name="UPDATE_MODE" value="CREATE">
  <%for(i=0;i<entity.pks.size();i++){%>
      [ltp]rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="[ltp]=rowColor%>">
        <td><%=((Field)entity.pks.elementAt(i)).columnName%></td>
        <td>
        <%if(((Field)entity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
          <input type="text" size="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>" value="[ltp]=UtilFormatOut.formatQuantity(<%=((Field)entity.pks.elementAt(i)).fieldName%>)%>">
        <%} else if(((Field)entity.fields.elementAt(i)).stringLength() <= 80){%>
          <input type="text" size="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>" value="[ltp]=UtilFormatOut.checkNull(<%=((Field)entity.pks.elementAt(i)).fieldName%>)%>">
        <%} else if(((Field)entity.fields.elementAt(i)).stringLength() <= 255){%>
          <input type="text" size="80" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>" value="[ltp]=UtilFormatOut.checkNull(<%=((Field)entity.pks.elementAt(i)).fieldName%>)%>">
        <%} else {%>
          <textarea cols="70" rows="3" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>">[ltp]=UtilFormatOut.checkNull(<%=((Field)entity.pks.elementAt(i)).fieldName%>)%></textarea>
        <%}%>
        </td>
      </tr><%}%>
    [ltp]}else{%>
      [ltp]showFields=false;%>
      You do not have permission to create a <%=entity.ejbName%> (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_CREATE needed).
    [ltp]}%>
  [ltp]}else{%>
    [ltp]if(hasCreatePermission){%>
      <input type="hidden" name="WEBEVENT" value="UPDATE_<%=entity.tableName%>">
      <input type="hidden" name="UPDATE_MODE" value="CREATE">
  <%for(i=0;i<entity.pks.size();i++){%>
      [ltp]rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="[ltp]=rowColor%>">
        <td><%=((Field)entity.pks.elementAt(i)).columnName%></td>
        <td>
        <%if(((Field)entity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
          <input type="text" size="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>" value="">
        <%} else if(((Field)entity.fields.elementAt(i)).stringLength() <= 80){%>
          <input type="text" size="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>" value="">
        <%} else if(((Field)entity.fields.elementAt(i)).stringLength() <= 255){%>
          <input type="text" size="80" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>" value="">
        <%} else {%>
          <textarea cols="70" rows="3" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>"></textarea>
        <%}%>
        </td>
      </tr><%}%>
    [ltp]}else{%>
      [ltp]showFields=false;%>
      You do not have permission to create a <%=entity.ejbName%> (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_CREATE needed).
    [ltp]}%>
  [ltp]} //end if sku == null%>
[ltp]}else{%>
  [ltp]if(hasUpdatePermission){%>
    <input type="hidden" name="WEBEVENT" value="UPDATE_<%=entity.tableName%>">
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  <%for(i=0;i<entity.pks.size();i++){%>
    <input type="hidden" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>" value="[ltp]=<%=((Field)entity.pks.elementAt(i)).fieldName%>%>">
    [ltp]rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="[ltp]=rowColor%>">
      <td><%=((Field)entity.pks.elementAt(i)).columnName%></td>
      <td>
        <b>[ltp]=<%=((Field)entity.pks.elementAt(i)).fieldName%>%></b> (This cannot be changed without re-creating the <%=GenUtil.lowerFirstChar(entity.ejbName)%>.)
      </td>
    </tr><%}%>
  [ltp]}else{%>
    [ltp]showFields=false;%>
    You do not have permission to update a <%=entity.ejbName%> (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_UPDATE needed).
  [ltp]}%>
[ltp]} //end if <%=GenUtil.lowerFirstChar(entity.ejbName)%> == null %>

[ltp]if(showFields){%>
[ltp]
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if(session.getAttribute("ERROR_MESSAGE") != null && lastUpdateMode != null && lastUpdateMode.compareTo("UPDATE") == 0)
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    <%=GenUtil.lowerFirstChar(entity.ejbName)%> = null;
  }
%>  
<%for(i=0;i<entity.fields.size();i++){%>
  <%if(!((Field)entity.fields.elementAt(i)).isPk){%>
  [ltp]rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="[ltp]=rowColor%>">
    <td><%=((Field)entity.fields.elementAt(i)).columnName%></td>
    <td>
    <%if(((Field)entity.fields.elementAt(i)).javaType.indexOf("Timestamp") >= 0){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null)
        {
          java.sql.Timestamp timeStamp = <%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>();
          if(timeStamp  != null)
          {
            dateString = UtilTimestamp.toDateString(timeStamp);
            timeString = UtilTimestamp.toTimeString(timeStamp);
          }
        }
        else
        {
          dateString = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_DATE");
          timeString = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input type="text" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_DATE" size="11" value="[ltp]=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input type="text" size="6" maxlength="10" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_TIME" value="[ltp]=UtilFormatOut.checkNull(timeString)%>">
      [ltp]}%>
    <%} else if(((Field)entity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
      <input type="text" size="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>" value="[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null){%>[ltp]=UtilFormatOut.formatQuantity(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>[ltp]}else{%>[ltp]=UtilFormatOut.checkNull(request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>"))%>[ltp]}%>">
    <%} else if(((Field)entity.fields.elementAt(i)).stringLength() <= 80){%>
      <input type="text" size="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>" value="[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null){%>[ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>[ltp]}else{%>[ltp]=UtilFormatOut.checkNull(request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>"))%>[ltp]}%>">
    <%} else if(((Field)entity.fields.elementAt(i)).stringLength() <= 255){%>
      <input type="text" size="80" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>" value="[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null){%>[ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>[ltp]}else{%>[ltp]=UtilFormatOut.checkNull(request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>"))%>[ltp]}%>">
    <%} else {%>
      <textarea cols="60" rows="3" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>">[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null){%>[ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>[ltp]}else{%>[ltp]=UtilFormatOut.checkNull(request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>"))%>[ltp]}%></textarea>
    <%}%>
    </td>
  </tr>
  <%}%>
<%}%>
  [ltp]rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="[ltp]=rowColor%>">
    <td><input type="submit" name="Update" value="Update"></td>
  </tr>
[ltp]}%>
</table>
</form>

<a href="Find<%=entity.ejbName%>.jsp" class="buttontext">[Find <%=entity.ejbName%>]</a>
[ltp]if(hasCreatePermission){%>
  <a href="Edit<%=entity.ejbName%>.jsp" class="buttontext">[Create <%=entity.ejbName%>]</a>
[ltp]}%>
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
  [ltp]if(hasDeletePermission){%>
    <a href="Edit<%=entity.ejbName%>.jsp?WEBEVENT=UPDATE_<%=entity.tableName%>&UPDATE_MODE=DELETE&<%=entity.httpArgList(entity.pks)%>" class="buttontext">[Delete this <%=entity.ejbName%>]</a>
  [ltp]}%>
[ltp]}%>
[ltp]if(<%=entity.pkNameString(" != null && ", " != null")%>){%>
  <a href="View<%=entity.ejbName%>.jsp?<%=entity.httpArgList(entity.pks)%>" class="buttontext">[View <%=entity.ejbName%> Details]</a>
[ltp]}%>
<br>
[ltp]}else{%>
  <h3>You do not have permission to view this page (<%=entity.tableName%>_ADMIN, <%=entity.tableName%>_CREATE, or <%=entity.tableName%>_UPDATE needed).</h3>
[ltp]}%>

[ltp]@ include file="/includes/onecolumnclose.jsp" %>
[ltp]@ include file="/includes/footer.jsp" %>
