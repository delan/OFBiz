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

<%pageContext.setAttribute("PageName", "ViewGeneric"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%String entityName = request.getParameter("entityName");%>
<%ModelReader reader = delegator.getModelReader();%>
<%ModelEntity entity = reader.getModelEntity(entityName);%>

<%boolean hasViewPermission=security.hasEntityPermission(entity.tableName, "_VIEW", session);%>
<%boolean hasCreatePermission=security.hasEntityPermission(entity.tableName, "_CREATE", session);%>
<%boolean hasUpdatePermission=security.hasEntityPermission(entity.tableName, "_UPDATE", session);%>
<%boolean hasDeletePermission=security.hasEntityPermission(entity.tableName, "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  boolean useValue = true;

  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";
  String curFindString = "entityName=" + entityName;
  GenericPK findByPK = delegator.makePK(entityName, null);
  for(int fnum=0; fnum<entity.pks.size(); fnum++)
  {
    ModelField field = (ModelField)entity.pks.get(fnum);
    ModelFieldType type = delegator.getEntityFieldType(entity, field.type);
    if(type.javaType.equals("Timestamp") || type.javaType.equals("java.sql.Timestamp"))
    {
      String fvalDate = request.getParameter(field.name + "_DATE");
      String fvalTime = request.getParameter(field.name + "_TIME");
      if(fvalDate != null && fvalDate.length() > 0)
      {
        curFindString = curFindString + "&" + field.name + "_DATE=" + fvalDate;
        curFindString = curFindString + "&" + field.name + "_TIME=" + fvalTime;
        findByPK.setString(field.name, fvalDate + " " + fvalTime);
      }
    }
    else
    {
      String fval = request.getParameter(field.name);
      if(fval != null && fval.length() > 0)
      {
        curFindString = curFindString + "&" + field.name + "=" + fval;
        findByPK.setString(field.name, fval);
      }
    }
  }
  curFindString = UtilFormatOut.encodeQuery(curFindString);

  GenericValue value = null;
  //only try to find it if this is a valid primary key...
  if(findByPK.isPrimaryKey()) value = delegator.findByPrimaryKey(findByPK);
  if(value == null) useValue = false;
%>
<br>
<SCRIPT language='JavaScript'>  
var numTabs=<%=entity.relations.size()+2%>;
function ShowTab(lname) 
{
  for(inc=1; inc <= numTabs; inc++)
  {
    document.getElementById('tab' + inc).className = (lname == 'tab' + inc) ? 'ontab' : 'offtab';
    document.getElementById('lnk' + inc).className = (lname == 'tab' + inc) ? 'onlnk' : 'offlnk';
    document.getElementById('area' + inc).className = (lname == 'tab' + inc) ? 'topcontainer' : 'topcontainerhidden';
  }
}
</SCRIPT>
<div style='color: white; background-color: black; padding:3;'>
  <b>View Entity: <%=entityName%> with PK: <%=findByPK.toString()%></b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindGeneric?entityName=" + entityName)%>" class="buttontext">[Find <%=entityName%>]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewGeneric?entityName=" + entityName)%>" class="buttontext">[Create New <%=entityName%>]</a>
<%}%>
<%if(value != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateGeneric?UPDATE_MODE=DELETE&" + curFindString)%>" class="buttontext">[Delete this <%=entityName%>]</a>
  <%}%>
<%}%>
<br>
<br>
<table cellpadding='0' cellspacing='0'><tr>  
  <td id='tab1' class='ontab'>
    <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>View <%=entityName%></a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id='tab2' class='offtab'>
    <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Edit <%=entityName%></a>
  </td>
  <%}%>
</tr>
<%if(value != null){%>
<tr>
  <%for(int tabIndex=0;tabIndex<entity.relations.size();tabIndex++){%>
    <%ModelRelation relation = (ModelRelation)entity.relations.get(tabIndex);%>
    <%ModelEntity relatedEntity = reader.getModelEntity(relation.relEntityName);%>
    <%if(security.hasEntityPermission(relatedEntity.tableName, "_VIEW", session)){%>
      <td id='tab<%=tabIndex+3%>' class='offtab'>
        <a href='javascript:ShowTab("tab<%=tabIndex+3%>")' id='lnk<%=tabIndex+3%>' class='offlnk'>
          <%=relation.title%><%=relation.relEntityName%></a>
      </td>
    <%}%>
    <%if((tabIndex+1)%5 == 0){%></tr><tr><%}%>
  <%}%>
</tr>
<%}%>
</table>
<div class='topouter'>
  <DIV id='area1' class='topcontainer' width="1%">

<table border="0" cellspacing="2" cellpadding="2">
<%if(value == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified <%=entityName%> was not found.</h3></td></tr>
<%}else{%>
    <%for(int fnum=0;fnum<entity.fields.size();fnum++){%>
      <%ModelField field = (ModelField)entity.fields.get(fnum);%>
      <%ModelFieldType type = delegator.getEntityFieldType(entity, field.type);%>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td valign="top"><div class="tabletext"><b><%=field.name%></b></div></td>
      <td valign="top">
        <div class="tabletext">
      <%if(type.javaType.equals("Timestamp") || type.javaType.equals("java.sql.Timestamp")){%>
        <%java.sql.Timestamp dtVal = value.getTimestamp(field.name);%>
        <%=dtVal==null?"":dtVal.toString()%>
      <%} else if(type.javaType.equals("Date") || type.javaType.equals("java.sql.Date")){%>
        <%java.sql.Date dateVal = value.getDate(field.name);%>
        <%=dateVal==null?"":dateVal.toString()%>
      <%} else if(type.javaType.equals("Time") || type.javaType.equals("java.sql.Time")){%>
        <%java.sql.Time timeVal = value.getTime(field.name);%>
        <%=timeVal==null?"":timeVal.toString()%>
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
      </td>
    </tr>
    <%}%>
<%} //end if value == null %>
</table>
  </div>

<%GenericValue valueSave = value;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id='area2' class='topcontainerhidden' width="1%">
<%boolean showFields = true;%>
<%if(value == null && (findByPK.getAllFields().size() > 0)){%>
    <%=entity.entityName%> with primary key <%=findByPK.toString()%> not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the entity data for the fields, use parameters to get the old value
    useValue = false;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateGeneric?entityName=" + entityName)%>" method="POST" name="updateForm" style="margin:0;">
<table cellpadding="2" cellspacing="2" border="0">

<%if(value == null){%>
  <%if(hasCreatePermission){%>
    You may create a <%=entityName%> by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
    <%for(int fnum=0;fnum<entity.pks.size();fnum++){%>
      <%ModelField field = (ModelField)entity.pks.get(fnum);%>
      <%ModelFieldType type = delegator.getEntityFieldType(entity, field.type);%>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td valign="top"><div class="tabletext"><b><%=field.name%></b></div></td>
      <td valign="top">
        <div class="tabletext">
      <%if(type.javaType.equals("Timestamp") || type.javaType.equals("java.sql.Timestamp")){%>
        <%
          String dateString = null;
          String timeString = null;
          if(findByPK.get(field.name) != null){
            java.sql.Timestamp dtVal = findByPK.getTimestamp(field.name);
            if(dtVal != null) {
              String dtStr = dtVal.toString();
              dateString = dtStr.substring(0, dtStr.indexOf(' '));
              timeString = dtStr.substring(dtStr.indexOf(' ') + 1);
            }
          } else {
            dateString = request.getParameter(field.name + "_DATE");
            timeString = request.getParameter(field.name + "_TIME");
          }
        %>
        Date(YYYY-MM-DD):<input class='editInputBox' type="text" name="<%=field.name%>_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
        <a href="javascript:show_calendar('updateForm.<%=field.name%>_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
        Time(HH:mm:SS.sss):<input class='editInputBox' type="text" size="6" maxlength="10" name="<%=field.name%>_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%} else if(type.javaType.equals("Date") || type.javaType.equals("java.sql.Date")){%>
        <%
          String dateString = null;
          if(findByPK.get(field.name) != null){
            java.sql.Date dateVal = value.getDate(field.name);
            dateString = dateVal==null?"":dateVal.toString();
          } else {
            dateString = request.getParameter(field.name);
          }
        %>
        Date(YYYY-MM-DD):<input class='editInputBox' type="text" name="<%=field.name%>" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
        <a href="javascript:show_calendar('updateForm.<%=field.name%>');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      <%} else if(type.javaType.equals("Time") || type.javaType.equals("java.sql.Time")){%>
        <%
          String timeString = null;
          if(findByPK.get(field.name) != null){
            java.sql.Time timeVal = value.getTime(field.name);
            timeString = timeVal==null?"":timeVal.toString();
          } else {
            timeString = request.getParameter(field.name);
          }
        %>
        Time(HH:mm:SS.sss):<input class='editInputBox' type="text" size="6" maxlength="10" name="<%=field.name%>" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}else if(type.javaType.indexOf("Integer") >= 0){%>
        <input class='editInputBox' type="text" size="20" name="<%=field.name%>" value="<%=findByPK.get(field.name)!=null?UtilFormatOut.formatQuantity((Integer)findByPK.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>">
      <%}else if(type.javaType.indexOf("Long") >= 0){%>
        <input class='editInputBox' type="text" size="20" name="<%=field.name%>" value="<%=findByPK.get(field.name)!=null?UtilFormatOut.formatQuantity((Long)findByPK.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>"> 
      <%}else if(type.javaType.indexOf("Double") >= 0){%>
        <input class='editInputBox' type="text" size="20" name="<%=field.name%>" value="<%=findByPK.get(field.name)!=null?UtilFormatOut.formatQuantity((Double)findByPK.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>"> 
      <%}else if(type.javaType.indexOf("Float") >= 0){%>
        <input class='editInputBox' type="text" size="20" name="<%=field.name%>" value="<%=findByPK.get(field.name)!=null?UtilFormatOut.formatQuantity((Float)findByPK.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>">
      <%}else if(type.javaType.indexOf("String") >= 0){%>
        <%if(type.stringLength() <= 80){%>
        <input class='editInputBox' type="text" size="<%=type.stringLength()%>" maxlength="<%=type.stringLength()%>" name="<%=field.name%>" value="<%=findByPK.get(field.name)!=null?UtilFormatOut.checkNull((String)findByPK.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>">
        <%} else if(type.stringLength() <= 255){%>
          <input class='editInputBox' type="text" size="80" maxlength="<%=type.stringLength()%>" name="<%=field.name%>" value="<%=findByPK.get(field.name)!=null?UtilFormatOut.checkNull((String)findByPK.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>">
        <%} else {%>
          <textarea cols="60" rows="3" maxlength="<%=type.stringLength()%>" name="<%=field.name%>"><%=findByPK.get(field.name)!=null?UtilFormatOut.checkNull((String)findByPK.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%></textarea>
        <%}%>
      <%}%>
        &nbsp;</div>
      </td>
    </tr>
    <%}%>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a <%=entityName%> (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">

    <%for(int fnum=0;fnum<entity.pks.size();fnum++){%>
      <%ModelField field = (ModelField)entity.pks.get(fnum);%>
      <%ModelFieldType type = delegator.getEntityFieldType(entity, field.type);%>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td valign="top"><div class="tabletext"><b><%=field.name%></b></div></td>
      <td valign="top">
        <div class="tabletext">
      <%if(type.javaType.equals("Timestamp") || type.javaType.equals("java.sql.Timestamp")){%>
        <%java.sql.Timestamp dtVal = value.getTimestamp(field.name);%>
        <%String dtStr = dtVal==null?"":dtVal.toString();%>
        <input type="hidden" name="<%=field.name%>_DATE" value="<%=dtStr.substring(0, dtStr.indexOf(' '))%>">
        <input type="hidden" name="<%=field.name%>_TIME" value="<%=dtStr.substring(dtStr.indexOf(' ') + 1)%>">
        <%=dtStr%>
      <%} else if(type.javaType.equals("Date") || type.javaType.equals("java.sql.Date")){%>
        <%java.sql.Date dateVal = value.getDate(field.name);%>
        <input type="hidden" name="<%=field.name%>" value="<%=dateVal==null?"":dateVal.toString()%>">
        <%=dateVal==null?"":dateVal.toString()%>
      <%} else if(type.javaType.equals("Time") || type.javaType.equals("java.sql.Time")){%>
        <%java.sql.Time timeVal = value.getTime(field.name);%>
        <input type="hidden" name="<%=field.name%>" value="<%=timeVal==null?"":timeVal.toString()%>">
        <%=timeVal==null?"":timeVal.toString()%>
      <%}else if(type.javaType.indexOf("Integer") >= 0){%>
        <input type="hidden" name="<%=field.name%>" value="<%=UtilFormatOut.formatQuantity((Integer)value.get(field.name))%>">
        <%=UtilFormatOut.formatQuantity((Integer)value.get(field.name))%>
      <%}else if(type.javaType.indexOf("Long") >= 0){%>
        <input type="hidden" name="<%=field.name%>" value="<%=UtilFormatOut.formatQuantity((Long)value.get(field.name))%>">
        <%=UtilFormatOut.formatQuantity((Long)value.get(field.name))%>
      <%}else if(type.javaType.indexOf("Double") >= 0){%>
        <input type="hidden" name="<%=field.name%>" value="<%=UtilFormatOut.formatQuantity((Double)value.get(field.name))%>">
        <%=UtilFormatOut.formatQuantity((Double)value.get(field.name))%>
      <%}else if(type.javaType.indexOf("Float") >= 0){%>
        <input type="hidden" name="<%=field.name%>" value="<%=UtilFormatOut.formatQuantity((Float)value.get(field.name))%>">
        <%=UtilFormatOut.formatQuantity((Float)value.get(field.name))%>
      <%}else if(type.javaType.indexOf("String") >= 0){%>
        <input type="hidden" name="<%=field.name%>" value="<%=UtilFormatOut.checkNull((String)value.get(field.name))%>">
        <%=UtilFormatOut.checkNull((String)value.get(field.name))%>
      <%}%>
        &nbsp;</div>
      </td>
    </tr>
    <%}%>

  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a <%=entityName%> (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_UPDATE needed).
  <%}%>
<%} //end if value == null %>

<%if(showFields){%>

    <%for(int fnum=0;fnum<entity.nopks.size();fnum++){%>
      <%ModelField field = (ModelField)entity.nopks.get(fnum);%>
      <%ModelFieldType type = delegator.getEntityFieldType(entity, field.type);%>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td valign="top"><div class="tabletext"><b><%=field.name%></b></div></td>
      <td valign="top">
        <div class="tabletext">
      <%if(type.javaType.equals("Timestamp") || type.javaType.equals("java.sql.Timestamp")){%>
        <%
          String dateString = null;
          String timeString = null;
          if(value != null && useValue){
            java.sql.Timestamp dtVal = findByPK.getTimestamp(field.name);
            if(dtVal != null) {
              String dtStr = dtVal.toString();
              dateString = dtStr.substring(0, dtStr.indexOf(' '));
              timeString = dtStr.substring(dtStr.indexOf(' ') + 1);
            }
          } else {
            dateString = request.getParameter(field.name + "_DATE");
            timeString = request.getParameter(field.name + "_TIME");
          }
        %>
        Date(YYYY-MM-DD):<input class='editInputBox' type="text" name="<%=field.name%>_DATE" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
        <a href="javascript:show_calendar('updateForm.<%=field.name%>_DATE');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
        Time(HH:mm:SS.sss):<input class='editInputBox' type="text" size="6" maxlength="10" name="<%=field.name%>_TIME" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%} else if(type.javaType.equals("Date") || type.javaType.equals("java.sql.Date")){%>
        <%
          String dateString = null;
          if(value != null && useValue){
            java.sql.Date dateVal = value.getDate(field.name);
            dateString = dateVal==null?"":dateVal.toString();
          } else {
            dateString = request.getParameter(field.name);
          }
        %>
        Date(YYYY-MM-DD):<input class='editInputBox' type="text" name="<%=field.name%>" size="11" value="<%=UtilFormatOut.checkNull(dateString)%>">
        <a href="javascript:show_calendar('updateForm.<%=field.name%>');" onmouseover="window.status='Date Picker';return true;" onmouseout="window.status='';return true;"><img src="/images/show-calendar.gif" border=0 width="24" height="22"></a>
      <%} else if(type.javaType.equals("Time") || type.javaType.equals("java.sql.Time")){%>
        <%
          String timeString = null;
          if(value != null && useValue){
            java.sql.Time timeVal = value.getTime(field.name);
            timeString = timeVal==null?"":timeVal.toString();
          } else {
            timeString = request.getParameter(field.name);
          }
        %>
        Time(HH:mm:SS.sss):<input class='editInputBox' type="text" size="6" maxlength="10" name="<%=field.name%>" value="<%=UtilFormatOut.checkNull(timeString)%>">
      <%}else if(type.javaType.indexOf("Integer") >= 0){%>
        <input class='editInputBox' type="text" size="20" name="<%=field.name%>" value="<%=(value!=null&&useValue)?UtilFormatOut.formatQuantity((Integer)value.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>">
      <%}else if(type.javaType.indexOf("Long") >= 0){%>
        <input class='editInputBox' type="text" size="20" name="<%=field.name%>" value="<%=(value!=null&&useValue)?UtilFormatOut.formatQuantity((Long)value.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>">
      <%}else if(type.javaType.indexOf("Double") >= 0){%>
        <input class='editInputBox' type="text" size="20" name="<%=field.name%>" value="<%=(value!=null&&useValue)?UtilFormatOut.formatQuantity((Double)value.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>">
      <%}else if(type.javaType.indexOf("Float") >= 0){%>
        <input class='editInputBox' type="text" size="20" name="<%=field.name%>" value="<%=(value!=null&&useValue)?UtilFormatOut.formatQuantity((Float)value.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>">
      <%}else if(type.javaType.indexOf("String") >= 0){%>
        <%if(type.stringLength() <= 80){%>
        <input class='editInputBox' type="text" size="<%=type.stringLength()%>" maxlength="<%=type.stringLength()%>" name="<%=field.name%>" value="<%=(value!=null&&useValue)?UtilFormatOut.checkNull((String)value.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>">
        <%} else if(type.stringLength() <= 255){%>
          <input class='editInputBox' type="text" size="80" maxlength="<%=type.stringLength()%>" name="<%=field.name%>" value="<%=(value!=null&&useValue)?UtilFormatOut.checkNull((String)value.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%>">
        <%} else {%>
          <textarea cols="60" rows="3" maxlength="<%=type.stringLength()%>" name="<%=field.name%>"><%=(value!=null&&useValue)?UtilFormatOut.checkNull((String)value.get(field.name)):UtilFormatOut.checkNull(request.getParameter(field.name))%></textarea>
        <%}%>
      <%}%>
        &nbsp;</div>
      </td>
    </tr>
    <%}%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>
  </div>
<%}%>
<%-- ======================================================================== --%>

<%for(int relIndex=0;relIndex<entity.relations.size();relIndex++){%>
  <%ModelRelation relation = (ModelRelation)entity.relations.get(relIndex);%>
    <%ModelEntity relatedEntity = reader.getModelEntity(relation.relEntityName);%>
    <%if(relation.type.equalsIgnoreCase("one")){%>
<%-- Start ModelRelation for <%=relation.relatedEjbName%>, type: one --%>
<%if(value != null){%>
  <%if(security.hasEntityPermission(relatedEntity.tableName, "_VIEW", session)){%>
    <%-- GenericValue valueRelated = delegator.findByPrimaryKey(value.get<%=relation.keyMapUpperString("(), " + GenUtil.lowerFirstChar(entity.entityName) + ".get", "()")%>); --%>
    <%Iterator tempIter = UtilMisc.toIterator(value.getRelated(relation.title + relatedEntity.entityName));%>
    <%GenericValue valueRelated = null;%>
    <%if(tempIter != null && tempIter.hasNext()) valueRelated = (GenericValue)tempIter.next();%>
  <DIV id='area<%=relIndex+3%>' class='topcontainerhidden' width="100%">
    <div class='areaheader'>
     <b><%=relation.title%></b> Related Entity: <b><%=relatedEntity.entityName%></b> with PK: <%=valueRelated!=null?valueRelated.getPrimaryKey().toString():"entity not found!"%>
    </div>
    <%
      String findString = "entityName=" + relatedEntity.entityName;
      for(int knum=0; knum<relation.keyMaps.size(); knum++)
      {
        ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(knum);
        if(value.get(keyMap.fieldName) != null)
        {
          findString = findString + "&" + keyMap.relFieldName + "=" + value.get(keyMap.fieldName);
        }
      }
    %>
      
    <%if(valueRelated == null){%>
      <%if(security.hasEntityPermission(relatedEntity.tableName, "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewGeneric?" + findString)%>" class="buttontext">[Create <%=relatedEntity.entityName%>]</a>
      <%}%>
    <%}else{%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGeneric?" + findString)%>" class="buttontext">[View <%=relatedEntity.entityName%>]</a>
    <%}%>
  <div style='width: 100%; overflow: visible; border-style: none;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(valueRelated == null){%>
      <tr class="<%=rowClass1%>"><td><b>Specified <%=relatedEntity.entityName%> entity was not found.</b></td></tr>
    <%}else{%>
      <%for(int fnum=0;fnum<relatedEntity.fields.size();fnum++){%>
        <%ModelField field = (ModelField)relatedEntity.fields.get(fnum);%>
        <%ModelFieldType type = delegator.getEntityFieldType(entity, field.type);%>
      <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
        <td valign="top"><div class="tabletext"><b><%=field.name%></b></div></td>
        <td valign="top">
          <div class="tabletext">
        <%if(type.javaType.equals("Timestamp") || type.javaType.equals("java.sql.Timestamp")){%>
          <%java.sql.Timestamp dtVal = valueRelated.getTimestamp(field.name);%>
          <%=dtVal==null?"":dtVal.toString()%>
        <%} else if(type.javaType.equals("Date") || type.javaType.equals("java.sql.Date")){%>
          <%java.sql.Date dateVal = valueRelated.getDate(field.name);%>
          <%=dateVal==null?"":dateVal.toString()%>
        <%} else if(type.javaType.equals("Time") || type.javaType.equals("java.sql.Time")){%>
          <%java.sql.Time timeVal = valueRelated.getTime(field.name);%>
          <%=timeVal==null?"":timeVal.toString()%>
        <%}else if(type.javaType.indexOf("Integer") >= 0){%>
          <%=UtilFormatOut.formatQuantity((Integer)valueRelated.get(field.name))%>
        <%}else if(type.javaType.indexOf("Long") >= 0){%>
          <%=UtilFormatOut.formatQuantity((Long)valueRelated.get(field.name))%>
        <%}else if(type.javaType.indexOf("Double") >= 0){%>
          <%=UtilFormatOut.formatQuantity((Double)valueRelated.get(field.name))%>
        <%}else if(type.javaType.indexOf("Float") >= 0){%>
          <%=UtilFormatOut.formatQuantity((Float)valueRelated.get(field.name))%>
        <%}else if(type.javaType.indexOf("String") >= 0){%>
          <%=UtilFormatOut.checkNull((String)valueRelated.get(field.name))%>
        <%}%>
          &nbsp;</div>
        </td>
      </tr>
    <%}%>
    <%} //end if valueRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End ModelRelation for <%=relation.relatedEjbName%>, type: one --%>
  <%}else if(relation.type.equalsIgnoreCase("many")){%>
<%-- Start ModelRelation for <%=relation.relatedEjbName%>, type: many --%>

<%if(value != null){%>
  <%if(security.hasEntityPermission(relatedEntity.tableName, "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(delegator.findBy<%=relation.keyMapRelatedUpperString("And","")%>(value.get<%=relation.keyMapUpperString("(), " + GenUtil.lowerFirstChar(entity.entityName) + ".get", "()")%>)); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(value.getRelated(relation.title + relatedEntity.entityName));%>
  <DIV id=area<%=relIndex+3%> class='topcontainerhidden' width="100%">
    <div class=areaheader>
      <b><%=relation.title%></b> Related Entities: <b><%=relatedEntity.entityName%></b> with 
    </div>
    <%boolean relatedCreatePerm = security.hasEntityPermission(relatedEntity.tableName, "_CREATE", session);%>
    <%boolean relatedUpdatePerm = security.hasEntityPermission(relatedEntity.tableName, "_UPDATE", session);%>
    <%boolean relatedDeletePerm = security.hasEntityPermission(relatedEntity.tableName, "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
<%--
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGeneric?" + <%=relatedEntity.httpRelationArgList(relation)%>)%>" class="buttontext">[Create <%=relatedEntity.entityName%>]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=<%=relation.keyMapRelatedUpperString("And","")%>";%>
    <%for(int j=0;j<relation.keyMaps.size();j++){%><%curFindString = curFindString + "&SEARCH_PARAMETER<%=j+1%>=" + value.get<%=GenUtil.upperFirstChar(((ModelKeyMap)relation.keyMaps.elementAt(j)).fieldName)%><%}%>();%>
    <a href="<%=response.encodeURL(controlPath + "/FindGeneric?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find <%=relatedEntity.entityName%>]</a>
  <div style='width:100%;overflow:visible;border-style:none;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  <%for(i=0;i<relatedEntity.fields.size();i++){%>
      <td><div class="tabletext"><b><nobr><%=((ModelField)relatedEntity.fields.elementAt(i)).columnName%></nobr></b></div></td><%}%>
      <td>&nbsp;</td>
      <%if(relatedDeletePerm){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
    <%
     int relatedLoopCount = 0;
     if(relatedIterator != null && relatedIterator.hasNext())
     {
      while(relatedIterator != null && relatedIterator.hasNext())
      {
        relatedLoopCount++; //if(relatedLoopCount > 10) break;
        <%=relatedEntity.entityName%> valueRelated = (<%=relatedEntity.entityName%>)relatedIterator.next();
        if(valueRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  <%for(i=0;i<relatedEntity.fields.size();i++){%>
      <td>
        <div class="tabletext"><%if(((ModelField)relatedEntity.fields.elementAt(i)).javaType.equals("Timestamp") || ((ModelField)relatedEntity.fields.elementAt(i)).javaType.equals("java.sql.Timestamp")){%>
      <%{
        String dateString = null;
        String timeString = null;
        if(valueRelated != null)
        {
          java.sql.Timestamp timeStamp = valueRelated.get<%=GenUtil.upperFirstChar(((ModelField)relatedEntity.fields.elementAt(i)).fieldName)%>();
          if(timeStamp  != null)
          {
            dateString = UtilDateTime.toDateString(timeStamp);
            timeString = UtilDateTime.toTimeString(timeStamp);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%><%} else if(((ModelField)relatedEntity.fields.elementAt(i)).javaType.equals("Date") || ((ModelField)relatedEntity.fields.elementAt(i)).javaType.equals("java.util.Date")){%>
      <%{
        String dateString = null;
        String timeString = null;
        if(valueRelated != null)
        {
          java.util.Date date = valueRelated.get<%=GenUtil.upperFirstChar(((ModelField)relatedEntity.fields.elementAt(i)).fieldName)%>();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%><%}else if(((ModelField)relatedEntity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || ((ModelField)relatedEntity.fields.elementAt(i)).javaType.indexOf("Long") >= 0 || ((ModelField)relatedEntity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || ((ModelField)relatedEntity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
      <%=UtilFormatOut.formatQuantity(valueRelated.get<%=GenUtil.upperFirstChar(((ModelField)relatedEntity.fields.elementAt(i)).fieldName)%>())%><%}else{%>
      <%=UtilFormatOut.checkNull(valueRelated.get<%=GenUtil.upperFirstChar(((ModelField)relatedEntity.fields.elementAt(i)).fieldName)%>())%><%}%>
        &nbsp;</div>
      </td>
  <%}%>
      <td>
        <a href="<%=response.encodeURL(controlPath + "/View<%=relatedEntity.entityName%>?" + <%=relatedEntity.httpArgListFromClass(relatedEntity.pks, "Related")%>)%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/Update<%=relatedEntity.entityName%>?" + <%=relatedEntity.httpArgListFromClass(relatedEntity.pks, "Related")%> + "&" + <%=entity.httpArgList(entity.pks)%> + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="<%=relatedEntity.fields.size() + 2%>">
<h3>No <%=relatedEntity.entityName%>s Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
--%>
  </div>
  <%}%>
<%}%>
<%-- End ModelRelation for <%=relation.relatedEjbName%>, type: many --%>
  <%}%>
<%}%>
</div>
<%if((hasUpdatePermission || hasCreatePermission) && !useValue){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<br>
<%}else{%>
  <h3>You do not have permission to view this page (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
