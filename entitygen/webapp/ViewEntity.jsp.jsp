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

[ltp]@ page import="java.util.*" %>
[ltp]@ page import="org.ofbiz.core.util.*" %>
[ltp]@ page import="org.ofbiz.commonapp.security.*" %>
[ltp]@ page import="<%=entity.packageName%>.*" %>
<%@ page import="java.util.*" %><%Hashtable importNames = new Hashtable(); importNames.put("org.ofbiz.commonapp.security","");importNames.put(entity.packageName,"");%><%for(int relIndex=0;relIndex<entity.relations.size();relIndex++){%><%Relation relation = (Relation)entity.relations.elementAt(relIndex);%><%Entity relatedEntity = DefReader.getEntity(defFileName,relation.relatedEjbName);%><%if(!importNames.containsKey(relatedEntity.packageName)){ importNames.put(relatedEntity.packageName,"");%>
[ltp]@ page import="<%=relatedEntity.packageName%>.*" %><%}%><%}%>

[ltp]String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
[ltp]pageContext.setAttribute("PageName", "View<%=entity.ejbName%>"); %>

[ltp]@ include file="/includes/header.jsp" %>
[ltp]@ include file="/includes/onecolumn.jsp" %>

[ltp]boolean hasViewPermission=Security.hasEntityPermission("<%=entity.tableName%>", "_VIEW", session);%>
[ltp]boolean hasCreatePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_CREATE", session);%>
[ltp]boolean hasUpdatePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_UPDATE", session);%>
[ltp]boolean hasDeletePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_DELETE", session);%>
[ltp]if(hasViewPermission){%>

[ltp]
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";
<%for(i=0;i<entity.pks.size();i++){Field curField=(Field)entity.pks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
  String <%=curField.fieldName%> = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>");  <%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
  String <%=curField.fieldName%>Date = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>_DATE");
  String <%=curField.fieldName%>Time = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>_TIME");  <%}else{%>
  String <%=curField.fieldName%>String = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>");  <%}%><%}%>
<%for(i=0;i<entity.pks.size();i++){%><%if(((Field)entity.pks.elementAt(i)).javaType.compareTo("java.lang.String") != 0 && ((Field)entity.pks.elementAt(i)).javaType.compareTo("String") != 0){%>
    <%=((Field)entity.pks.elementAt(i)).javaType%> <%=((Field)entity.pks.elementAt(i)).fieldName%> = null;
    try
    {
      if(<%=((Field)entity.pks.elementAt(i)).fieldName%>String != null)
      {
        <%=((Field)entity.pks.elementAt(i)).fieldName%> = <%=((Field)entity.pks.elementAt(i)).javaType%>.valueOf(<%=((Field)entity.pks.elementAt(i)).fieldName%>String);
      }
    }
    catch(Exception e)
    {
    }<%}%><%}%>

  <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = <%=entity.ejbName%>Helper.findByPrimaryKey(<%=entity.pkNameString()%>);
%>

<br>
<SCRIPT language='JavaScript'>  
function ShowViewTab(lname) 
{
    document.all.viewtab.className = (lname == 'view') ? 'ontab' : 'offtab';
    document.all.viewlnk.className = (lname == 'view') ? 'onlnk' : 'offlnk';
    document.all.viewarea.style.visibility = (lname == 'view') ? 'visible' : 'hidden';

    document.all.edittab.className = (lname == 'edit') ? 'ontab' : 'offtab';
    document.all.editlnk.className = (lname == 'edit') ? 'onlnk' : 'offlnk';
    document.all.editarea.style.visibility = (lname == 'edit') ? 'visible' : 'hidden';
}
</SCRIPT>
<table cellpadding='0' cellspacing='0'><tr>  
  <td id=viewtab class=ontab>
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View <%=entity.ejbName%></a>
  </td>
  [ltp]if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit <%=entity.ejbName%></a>
  </td>
  [ltp]}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: <%=entity.ejbName%> with (<%=entity.colNameString(entity.pks)%>: [ltp]=<%=entity.pkNameString("%" + ">, [ltp]=", "%" + ">")%>).</b>
</div>

<a href="[ltp]=response.encodeURL(controlPath + "/Find<%=entity.ejbName%>")%>" class="buttontext">[Find <%=entity.ejbName%>]</a>
[ltp]if(hasCreatePermission){%>
  <a href="[ltp]=response.encodeURL(controlPath + "/View<%=entity.ejbName%>")%>" class="buttontext">[Create New <%=entity.ejbName%>]</a>
[ltp]}%>
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
  [ltp]if(hasDeletePermission){%>
    <a href="[ltp]=response.encodeURL(controlPath + "/Update<%=entity.ejbName%>?UPDATE_MODE=DELETE&" + <%=entity.httpArgList(entity.pks)%>)%>" class="buttontext">[Delete this <%=entity.ejbName%>]</a>
  [ltp]}%>
[ltp]}%>

[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
[ltp]}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
[ltp]}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null){%>
<tr class="[ltp]=rowClass1%>"><td><h3>Specified <%=entity.ejbName%> was not found.</h3></td></tr>
[ltp]}else{%>
<%for(i=0;i<entity.fields.size();i++){%>
  [ltp]rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="[ltp]=rowClass%>">
    <td><b><%=((Field)entity.fields.elementAt(i)).columnName%></b></td>
    <td><%if(((Field)entity.fields.elementAt(i)).javaType.equals("Timestamp") || ((Field)entity.fields.elementAt(i)).javaType.equals("java.sql.Timestamp")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null)
        {
          java.sql.Timestamp timeStamp = <%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>();
          if(timeStamp  != null)
          {
            dateString = UtilDateTime.toDateString(timeStamp);
            timeString = UtilDateTime.toTimeString(timeStamp);
          }
        }
      %>
      [ltp]=UtilFormatOut.checkNull(dateString)%>&nbsp;[ltp]=UtilFormatOut.checkNull(timeString)%>
      [ltp]}%><%} else if(((Field)entity.fields.elementAt(i)).javaType.equals("Date") || ((Field)entity.fields.elementAt(i)).javaType.equals("java.util.Date")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null)
        {
          java.util.Date date = <%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      [ltp]=UtilFormatOut.checkNull(dateString)%>&nbsp;[ltp]=UtilFormatOut.checkNull(timeString)%>
      [ltp]}%><%}else if(((Field)entity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Long") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
      [ltp]=UtilFormatOut.formatQuantity(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%><%}else{%>
      [ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%><%}%>
    </td>
  </tr>
<%}%>
[ltp]} //end if <%=GenUtil.lowerFirstChar(entity.ejbName)%> == null %>
</table>
  </div>
[ltp]<%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>Save = <%=GenUtil.lowerFirstChar(entity.ejbName)%>;%>
[ltp]if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
[ltp]boolean showFields = true;%>
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null && (<%=entity.pkNameString(" != null || ", " != null")%>)){%>
    <%=entity.ejbName%> with (<%=entity.colNameString(entity.pks)%>: [ltp]=<%=entity.pkNameString("%" + ">, [ltp]=", "%" + ">")%>) not found.<br>
[ltp]}%>
[ltp]
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    <%=GenUtil.lowerFirstChar(entity.ejbName)%> = null;
  }
%>
<form action="[ltp]=response.encodeURL(controlPath + "/Update<%=entity.ejbName%>")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="[ltp]=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null){%>
  [ltp]if(hasCreatePermission){%>
    You may create a <%=entity.ejbName%> by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  <%for(i=0;i<entity.pks.size();i++){%>
    [ltp]rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="[ltp]=rowClass%>">
      <td><%=((Field)entity.pks.elementAt(i)).columnName%></td>
      <td><%if(((Field)entity.pks.elementAt(i)).javaType.equals("Timestamp") || ((Field)entity.pks.elementAt(i)).javaType.equals("java.sql.Timestamp")){%>
        [ltp]{
          String dateString = null;
          String timeString = null;
          if(<%=((Field)entity.pks.elementAt(i)).fieldName%> != null)
          {
            dateString = UtilDateTime.toDateString(<%=((Field)entity.pks.elementAt(i)).fieldName%>);
            timeString = UtilDateTime.toTimeString(<%=((Field)entity.pks.elementAt(i)).fieldName%>);
          }
          else
          {
            dateString = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_DATE");
            timeString = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_TIME");
          }
        %>
        Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_DATE" size="11" value="[ltp]=UtilFormatOut.checkNull(dateString)%>">
        <a href="javascript:show_calendar('updateForm.<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
        Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_TIME" value="[ltp]=UtilFormatOut.checkNull(timeString)%>">
        [ltp]}%><%}else if(((Field)entity.pks.elementAt(i)).javaType.equals("Date") || ((Field)entity.pks.elementAt(i)).javaType.equals("java.util.Date")){%>
        [ltp]{
          String dateString = null;
          String timeString = null;
          if(<%=((Field)entity.pks.elementAt(i)).fieldName%> != null)
          {
            dateString = UtilDateTime.toDateString(<%=((Field)entity.pks.elementAt(i)).fieldName%>);
            timeString = UtilDateTime.toTimeString(<%=((Field)entity.pks.elementAt(i)).fieldName%>);
          }
          else
          {
            dateString = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_DATE");
            timeString = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_TIME");
          }
        %>
        Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_DATE" size="11" value="[ltp]=UtilFormatOut.checkNull(dateString)%>">
        <a href="javascript:show_calendar('updateForm.<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
        Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_TIME" value="[ltp]=UtilFormatOut.checkNull(timeString)%>">
        [ltp]}%><%} else if(((Field)entity.pks.elementAt(i)).javaType.indexOf("Integer") >= 0 || ((Field)entity.pks.elementAt(i)).javaType.indexOf("Long") >= 0 || ((Field)entity.pks.elementAt(i)).javaType.indexOf("Double") >= 0 || ((Field)entity.pks.elementAt(i)).javaType.indexOf("Float") >= 0){%>
        <input class='editInputBox' type="text" size="<%=((Field)entity.pks.elementAt(i)).stringLength()%>" maxlength="<%=((Field)entity.pks.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>" value="[ltp]=UtilFormatOut.formatQuantity(<%=((Field)entity.pks.elementAt(i)).fieldName%>)%>"> <%} else if(((Field)entity.pks.elementAt(i)).stringLength() <= 80){%>
        <input class='editInputBox' type="text" size="<%=((Field)entity.pks.elementAt(i)).stringLength()%>" maxlength="<%=((Field)entity.pks.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>" value="[ltp]=UtilFormatOut.checkNull(<%=((Field)entity.pks.elementAt(i)).fieldName%>)%>"><%} else if(((Field)entity.pks.elementAt(i)).stringLength() <= 255){%>
        <input class='editInputBox' type="text" size="80" maxlength="<%=((Field)entity.pks.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>" value="[ltp]=UtilFormatOut.checkNull(<%=((Field)entity.pks.elementAt(i)).fieldName%>)%>"><%} else {%>
        <textarea cols="60" rows="3" maxlength="<%=((Field)entity.pks.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>">[ltp]=UtilFormatOut.checkNull(<%=((Field)entity.pks.elementAt(i)).fieldName%>)%></textarea><%}%>
      </td>
    </tr><%}%>
  [ltp]}else{%>
    [ltp]showFields=false;%>
    You do not have permission to create a <%=entity.ejbName%> (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_CREATE needed).
  [ltp]}%>
[ltp]}else{%>
  [ltp]if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  <%for(i=0;i<entity.pks.size();i++){%><%if(((Field)entity.pks.elementAt(i)).javaType.indexOf("Timestamp") >= 0 || ((Field)entity.pks.elementAt(i)).javaType.equals("java.util.Date") || ((Field)entity.pks.elementAt(i)).javaType.equals("Date")){%>
      <input type="hidden" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_DATE" value="[ltp]=<%=((Field)entity.pks.elementAt(i)).fieldName%>Date%>">
      <input type="hidden" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>_TIME" value="[ltp]=<%=((Field)entity.pks.elementAt(i)).fieldName%>Time%>"><%}else{%>
      <input type="hidden" name="<%=entity.tableName%>_<%=((Field)entity.pks.elementAt(i)).columnName%>" value="[ltp]=<%=((Field)entity.pks.elementAt(i)).fieldName%>%>"><%}%>
    [ltp]rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="[ltp]=rowClass%>">
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
<%for(i=0;i<entity.fields.size();i++){%><%if(!((Field)entity.fields.elementAt(i)).isPk){%>
  [ltp]rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="[ltp]=rowClass%>">
    <td><%=((Field)entity.fields.elementAt(i)).columnName%></td>
    <td><%if(((Field)entity.fields.elementAt(i)).javaType.equals("Timestamp") || ((Field)entity.fields.elementAt(i)).javaType.equals("java.sql.Timestamp")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null)
        {
          java.sql.Timestamp timeStamp = <%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>();
          if(timeStamp  != null)
          {
            dateString = UtilDateTime.toDateString(timeStamp);
            timeString = UtilDateTime.toTimeString(timeStamp);
          }
        }
        else
        {
          dateString = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_DATE");
          timeString = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_DATE" size="11" value="[ltp]=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_TIME" value="[ltp]=UtilFormatOut.checkNull(timeString)%>">
      [ltp]}%><%}else if(((Field)entity.fields.elementAt(i)).javaType.equals("Date") || ((Field)entity.fields.elementAt(i)).javaType.equals("java.util.Date")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null)
        {
          java.util.Date date = <%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
        else
        {
          dateString = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_DATE");
          timeString = request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_TIME");
        }
      %>
      Date(MM/DD/YYYY):<input class='editInputBox' type="text" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_DATE" size="11" value="[ltp]=UtilFormatOut.checkNull(dateString)%>">
      <a href="javascript:show_calendar('updateForm.<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      Time(HH:MM):<input class='editInputBox' type="text" size="6" maxlength="10" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>_TIME" value="[ltp]=UtilFormatOut.checkNull(timeString)%>">
      [ltp]}%><%} else if(((Field)entity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Long") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
      <input class='editInputBox' type="text" size="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>" value="[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null){%>[ltp]=UtilFormatOut.formatQuantity(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>[ltp]}else{%>[ltp]=UtilFormatOut.checkNull(request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>"))%>[ltp]}%>"><%} else if(((Field)entity.fields.elementAt(i)).stringLength() <= 80){%>
      <input class='editInputBox' type="text" size="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>" value="[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null){%>[ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>[ltp]}else{%>[ltp]=UtilFormatOut.checkNull(request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>"))%>[ltp]}%>"><%} else if(((Field)entity.fields.elementAt(i)).stringLength() <= 255){%>
      <input class='editInputBox' type="text" size="80" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>" value="[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null){%>[ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>[ltp]}else{%>[ltp]=UtilFormatOut.checkNull(request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>"))%>[ltp]}%>"><%} else {%>
      <textarea cols="60" rows="3" maxlength="<%=((Field)entity.fields.elementAt(i)).stringLength()%>" name="<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>">[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null){%>[ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>[ltp]}else{%>[ltp]=UtilFormatOut.checkNull(request.getParameter("<%=entity.tableName%>_<%=((Field)entity.fields.elementAt(i)).columnName%>"))%>[ltp]}%></textarea><%}%>
    </td>
  </tr><%}%><%}%>
  [ltp]rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="[ltp]=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
[ltp]}%>
</table>
</form>
  </div>
[ltp]}%>
</div>
[ltp]if((hasUpdatePermission || hasCreatePermission) && <%=GenUtil.lowerFirstChar(entity.ejbName)%> == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
[ltp]}%>
[ltp]-- Restore the <%=GenUtil.lowerFirstChar(entity.ejbName)%> for cases when removed to retain passed form values --%>
[ltp]<%=GenUtil.lowerFirstChar(entity.ejbName)%> = <%=GenUtil.lowerFirstChar(entity.ejbName)%>Save;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=<%=entity.relations.size()%>;
function ShowTab(lname) 
{
  for(inc=1; inc <= numTabs; inc++)
  {
    document.all['tab' + inc].className = (lname == 'tab' + inc) ? 'ontab' : 'offtab';
    document.all['lnk' + inc].className = (lname == 'tab' + inc) ? 'onlnk' : 'offlnk';
    document.all['area' + inc].style.visibility = (lname == 'tab' + inc) ? 'visible' : 'hidden';
  }
}
</SCRIPT>
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
<table cellpadding='0' cellspacing='0'><tr><%for(int tabIndex=0;tabIndex<entity.relations.size();tabIndex++){%><%Relation relation = (Relation)entity.relations.elementAt(tabIndex);%>
    [ltp]if(Security.hasEntityPermission("<%=relation.relatedTableName%>", "_VIEW", session)){%>
      <td id=tab<%=tabIndex+1%> class=<%=(tabIndex==0?"ontab":"offtab")%>>
        <a href='javascript:ShowTab("tab<%=tabIndex+1%>")' id=lnk<%=tabIndex+1%> class=<%=(tabIndex==0?"onlnk":"offlnk")%>><%=relation.relationTitle%> <%=relation.relatedEjbName%></a>
      </td>
    [ltp]}%><%}%>
</tr></table>
[ltp]}%>
  
<%for(int relIndex=0;relIndex<entity.relations.size();relIndex++){%><%Relation relation = (Relation)entity.relations.elementAt(relIndex);%><%Entity relatedEntity = DefReader.getEntity(defFileName,relation.relatedEjbName);%><%if(relation.relationType.equalsIgnoreCase("one")){%>
[ltp]-- Start Relation for <%=relation.relatedEjbName%>, type: one --%>
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
  [ltp]if(Security.hasEntityPermission("<%=relatedEntity.tableName%>", "_VIEW", session)){%>
    [ltp]-- <%=relatedEntity.ejbName%> <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related = <%=relatedEntity.ejbName%>Helper.findByPrimaryKey(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.keyMapUpperString("(), " + GenUtil.lowerFirstChar(entity.ejbName) + ".get", "()")%>); --%>
    [ltp]<%=relatedEntity.ejbName%> <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related = <%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.relationTitle%><%=relatedEntity.ejbName%>();%>
  <DIV id=area<%=relIndex+1%> style="VISIBILITY: <%=(relIndex==0?"visible":"hidden")%>; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b><%=relation.relationTitle%></b> Related Entity: <b><%=relatedEntity.ejbName%></b> with (<%=relatedEntity.colNameString(relatedEntity.pks)%>: [ltp]=<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.keyMapUpperString("()%" + ">, [ltp]=" + GenUtil.lowerFirstChar(entity.ejbName) + ".get", "()%" + ">")%>)
    </div>
    [ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.keyMapUpperString("() != null && " + GenUtil.lowerFirstChar(entity.ejbName) + ".get", "() != null")%>){%>
      <a href="[ltp]=response.encodeURL(controlPath + "/View<%=relatedEntity.ejbName%>?" + <%=relatedEntity.httpRelationArgList(relatedEntity.pks, relation)%>)%>" class="buttontext">[View <%=relatedEntity.ejbName%>]</a>      
    [ltp]if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related == null){%>
      [ltp]if(Security.hasEntityPermission("<%=relatedEntity.tableName%>", "_CREATE", session)){%>
        <a href="[ltp]=response.encodeURL(controlPath + "/View<%=relatedEntity.ejbName%>?" + <%=relatedEntity.httpRelationArgList(relatedEntity.pks, relation)%>)%>" class="buttontext">[Create <%=relatedEntity.ejbName%>]</a>
      [ltp]}%>
    [ltp]}%>
    [ltp]}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    [ltp]if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related == null){%>
    <tr class="[ltp]=rowClass1%>"><td><h3>Specified <%=relatedEntity.ejbName%> was not found.</h3></td></tr>
    [ltp]}else{%>
<%for(i=0;i<relatedEntity.fields.size();i++){%>
  [ltp]rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="[ltp]=rowClass%>">
    <td><b><%=((Field)relatedEntity.fields.elementAt(i)).columnName%></b></td>
    <td><%if(((Field)relatedEntity.fields.elementAt(i)).javaType.equals("Timestamp") || ((Field)relatedEntity.fields.elementAt(i)).javaType.equals("java.sql.Timestamp")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related != null)
        {
          java.sql.Timestamp timeStamp = <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>();
          if(timeStamp  != null)
          {
            dateString = UtilDateTime.toDateString(timeStamp);
            timeString = UtilDateTime.toTimeString(timeStamp);
          }
        }
      %>
      [ltp]=UtilFormatOut.checkNull(dateString)%>&nbsp;[ltp]=UtilFormatOut.checkNull(timeString)%>
      [ltp]}%><%} else if(((Field)relatedEntity.fields.elementAt(i)).javaType.equals("Date") || ((Field)relatedEntity.fields.elementAt(i)).javaType.equals("java.util.Date")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related != null)
        {
          java.util.Date date = <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      [ltp]=UtilFormatOut.checkNull(dateString)%>&nbsp;[ltp]=UtilFormatOut.checkNull(timeString)%>
      [ltp]}%><%}else if(((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Long") >= 0 || ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
      [ltp]=UtilFormatOut.formatQuantity(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>())%><%}else{%>
      [ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>())%><%}%>
    </td>
  </tr>
<%}%>
    [ltp]} //end if <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related == null %>
    </table>
    </div>
  </div>
  [ltp]}%>
[ltp]}%>
[ltp]-- End Relation for <%=relation.relatedEjbName%>, type: one --%>
  <%}else if(relation.relationType.equalsIgnoreCase("many")){%>
[ltp]-- Start Relation for <%=relation.relatedEjbName%>, type: many --%>
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
  [ltp]if(Security.hasEntityPermission("<%=relatedEntity.tableName%>", "_VIEW", session)){%>    
    [ltp]-- Iterator relatedIterator = UtilMisc.toIterator(<%=relatedEntity.ejbName%>Helper.findBy<%=relation.keyMapRelatedUpperString("And","")%>(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.keyMapUpperString("(), " + GenUtil.lowerFirstChar(entity.ejbName) + ".get", "()")%>)); --%>
    [ltp]Iterator relatedIterator = UtilMisc.toIterator(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.relationTitle%><%=relatedEntity.ejbName%>s());%>
  <DIV id=area<%=relIndex+1%> style="VISIBILITY: <%=(relIndex==0?"visible":"hidden")%>; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b><%=relation.relationTitle%></b> Related Entities: <b><%=relatedEntity.ejbName%></b> with (<%=relation.keyMapRelatedColumnString(", ", "")%>: [ltp]=<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.keyMapUpperString("()%" + ">, [ltp]=" + GenUtil.lowerFirstChar(entity.ejbName) + ".get", "()%" + ">")%>)
    </div>
    [ltp]boolean relatedCreatePerm = Security.hasEntityPermission("<%=relatedEntity.tableName%>", "_CREATE", session);%>
    [ltp]boolean relatedUpdatePerm = Security.hasEntityPermission("<%=relatedEntity.tableName%>", "_UPDATE", session);%>
    [ltp]boolean relatedDeletePerm = Security.hasEntityPermission("<%=relatedEntity.tableName%>", "_DELETE", session);%>
    [ltp]
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      <%String packagePath = relatedEntity.packageName.replace('.','/'); /* remove the first two folders (usually org/ and ofbiz/) */  packagePath = packagePath.substring(packagePath.indexOf("/")+1); packagePath = packagePath.substring(packagePath.indexOf("/")+1);%>
    [ltp]if(relatedCreatePerm){%>
      <a href="[ltp]=response.encodeURL(controlPath + "/View<%=relatedEntity.ejbName%>?" + <%=relatedEntity.httpRelationArgList(relation)%>)%>" class="buttontext">[Create <%=relatedEntity.ejbName%>]</a>
    [ltp]}%>    
    [ltp]String curFindString = "SEARCH_TYPE=<%=relation.keyMapRelatedUpperString("And","")%>";%>
    <%for(int j=0;j<relation.keyMaps.size();j++){%>[ltp]curFindString = curFindString + "&SEARCH_PARAMETER<%=j+1%>=" + <%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((KeyMap)relation.keyMaps.elementAt(j)).fieldName)%><%}%>();%>
    <a href="[ltp]=response.encodeURL(controlPath + "/Find<%=entity.ejbName%>?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find <%=relatedEntity.ejbName%>]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="[ltp]=rowClassResultHeader%>">
  <%for(i=0;i<relatedEntity.fields.size();i++){%>
      <td><div class="tabletext"><b><nobr><%=((Field)relatedEntity.fields.elementAt(i)).columnName%></nobr></b></div></td><%}%>
      <td>&nbsp;</td>
      [ltp]if(relatedDeletePerm){%>
        <td>&nbsp;</td>
      [ltp]}%>
    </tr>
    [ltp]
     int relatedLoopCount = 0;
     if(relatedIterator != null && relatedIterator.hasNext())
     {
      while(relatedIterator != null && relatedIterator.hasNext())
      {
        relatedLoopCount++; //if(relatedLoopCount > 10) break;
        <%=relatedEntity.ejbName%> <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related = (<%=relatedEntity.ejbName%>)relatedIterator.next();
        if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related != null)
        {
    %>
    [ltp]rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="[ltp]=rowClassResult%>">
  <%for(i=0;i<relatedEntity.fields.size();i++){%>
      <td>
        <div class="tabletext"><%if(((Field)relatedEntity.fields.elementAt(i)).javaType.equals("Timestamp") || ((Field)relatedEntity.fields.elementAt(i)).javaType.equals("java.sql.Timestamp")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related != null)
        {
          java.sql.Timestamp timeStamp = <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>();
          if(timeStamp  != null)
          {
            dateString = UtilDateTime.toDateString(timeStamp);
            timeString = UtilDateTime.toTimeString(timeStamp);
          }
        }
      %>
      [ltp]=UtilFormatOut.checkNull(dateString)%>&nbsp;[ltp]=UtilFormatOut.checkNull(timeString)%>
      [ltp]}%><%} else if(((Field)relatedEntity.fields.elementAt(i)).javaType.equals("Date") || ((Field)relatedEntity.fields.elementAt(i)).javaType.equals("java.util.Date")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related != null)
        {
          java.util.Date date = <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      [ltp]=UtilFormatOut.checkNull(dateString)%>&nbsp;[ltp]=UtilFormatOut.checkNull(timeString)%>
      [ltp]}%><%}else if(((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Long") >= 0 || ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
      [ltp]=UtilFormatOut.formatQuantity(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>())%><%}else{%>
      [ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>Related.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>())%><%}%>
        &nbsp;</div>
      </td>
  <%}%>
      <td>
        <a href="[ltp]=response.encodeURL(controlPath + "/View<%=relatedEntity.ejbName%>?" + <%=relatedEntity.httpArgListFromClass(relatedEntity.pks, "Related")%>)%>" class="buttontext">[View]</a>
      </td>
      [ltp]if(relatedDeletePerm){%>
        <td>
          <a href="[ltp]=response.encodeURL(controlPath + "/Update<%=relatedEntity.ejbName%>?" + <%=relatedEntity.httpArgListFromClass(relatedEntity.pks, "Related")%> + "&" + <%=entity.httpArgList(entity.pks)%> + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      [ltp]}%>
    </tr>
    [ltp]}%>
  [ltp]}%>
[ltp]}else{%>
[ltp]rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="[ltp]=rowClassResult%>">
<td colspan="<%=relatedEntity.fields.size() + 2%>">
<h3>No <%=relatedEntity.ejbName%>s Found.</h3>
</td>
</tr>
[ltp]}%>
    </table>
  </div>
Displaying [ltp]=relatedLoopCount%> entities.
  </div>
  [ltp]}%>
[ltp]}%>
[ltp]-- End Relation for <%=relation.relatedEjbName%>, type: many --%>
  <%}%>
<%}%>

<br>
[ltp]}else{%>
  <h3>You do not have permission to view this page (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_VIEW needed).</h3>
[ltp]}%>

[ltp]@ include file="/includes/onecolumnclose.jsp" %>
[ltp]@ include file="/includes/footer.jsp" %>
