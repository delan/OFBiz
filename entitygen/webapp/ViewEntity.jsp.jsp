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
[ltp]@ page import="org.ofbiz.commonapp.common.*" %>
[ltp]@ page import="org.ofbiz.commonapp.webevent.*" %>
[ltp]@ page import="org.ofbiz.commonapp.security.*" %>
[ltp]@ page import="<%=entity.packageName%>.*" %>
<%@ page import="java.util.*" %><%Hashtable importNames = new Hashtable(); importNames.put("org.ofbiz.commonapp.security","");importNames.put(entity.packageName,"");%><%for(int relIndex=0;relIndex<entity.relations.size();relIndex++){%><%Relation relation = (Relation)entity.relations.elementAt(relIndex);%><%Entity relatedEntity = DefReader.getEntity(defFileName,relation.relatedEjbName);%><%if(!importNames.containsKey(relatedEntity.packageName)){ importNames.put(relatedEntity.packageName,"");%>
[ltp]@ page import="<%=relatedEntity.packageName%>.*" %><%}%><%}%>

[ltp]@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

[ltp]pageContext.setAttribute("PageName", "Edit<%=entity.ejbName%>"); %>

[ltp]@ include file="/includes/header.jsp" %>
[ltp]@ include file="/includes/onecolumn.jsp" %>

[ltp]boolean hasViewPermission=Security.hasEntityPermission("<%=entity.tableName%>", "_VIEW", session);%>
[ltp]boolean hasCreatePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_CREATE", session);%>
[ltp]boolean hasUpdatePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_UPDATE", session);%>
[ltp]boolean hasDeletePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_DELETE", session);%>
[ltp]if(hasViewPermission){%>

[ltp]
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";
<%for(i=0;i<entity.pks.size();i++){Field curField=(Field)entity.pks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
  String <%=curField.fieldName%> = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>");  <%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
  String <%=curField.fieldName%>Date = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>_DATE");
  String <%=curField.fieldName%>Time = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>_TIME");  <%}else{%>
  String <%=curField.fieldName%>String = request.getParameter("<%=entity.tableName%>_<%=curField.columnName%>");  <%}%><%}%>
<%for(i=0;i<entity.pks.size();i++){%>
  <%if(((Field)entity.pks.elementAt(i)).javaType.compareTo("java.lang.String") != 0 && ((Field)entity.pks.elementAt(i)).javaType.compareTo("String") != 0){%>
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

<b><u>View Entity: <%=entity.ejbName%> with (<%=entity.colNameString(entity.pks)%>: [ltp]=<%=entity.pkNameString("%" + ">, [ltp]=", "%" + ">")%>).</u></b>
<br>
<a href="[ltp]=response.encodeURL("Find<%=entity.ejbName%>.jsp")%>" class="buttontext">[Find <%=entity.ejbName%>]</a>
[ltp]if(hasCreatePermission){%>
  <a href="[ltp]=response.encodeURL("Edit<%=entity.ejbName%>.jsp")%>" class="buttontext">[Create <%=entity.ejbName%>]</a>
[ltp]}%>
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
  [ltp]if(hasDeletePermission){%>
    <a href="[ltp]=response.encodeURL("Edit<%=entity.ejbName%>.jsp?WEBEVENT=UPDATE_<%=entity.tableName%>&UPDATE_MODE=DELETE&" + <%=entity.httpArgList(entity.pks)%>)%>" class="buttontext">[Delete this <%=entity.ejbName%>]</a>
  [ltp]}%>
[ltp]}%>
[ltp]if(hasUpdatePermission){%>
  [ltp]if(<%=entity.pkNameString(" != null && ", " != null")%>){%>
    <a href="[ltp]=response.encodeURL("Edit<%=entity.ejbName%>.jsp?" + <%=entity.httpArgList(entity.pks)%>)%>" class="buttontext">[Edit <%=entity.ejbName%>]</a>
  [ltp]}%>
[ltp]}%>

<table border="0" cellspacing="2" cellpadding="2">
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null){%>
<tr bgcolor="[ltp]=rowColor1%>"><td><h3>Specified <%=entity.ejbName%> was not found.</h3></td></tr>
[ltp]}else{%>
<%for(i=0;i<entity.fields.size();i++){%>
  [ltp]rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="[ltp]=rowColor%>">
    <td><b><%=((Field)entity.fields.elementAt(i)).columnName%></b></td>
    <td>
    <%if(((Field)entity.fields.elementAt(i)).javaType.equals("Timestamp") || 
         ((Field)entity.fields.elementAt(i)).javaType.equals("java.sql.Timestamp")){%>
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
      [ltp]}%>
    <%} else if(((Field)entity.fields.elementAt(i)).javaType.equals("Date") || 
                ((Field)entity.fields.elementAt(i)).javaType.equals("java.util.Date")){%>
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
      [ltp]}%>
    <%}else if(((Field)entity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || 
               ((Field)entity.fields.elementAt(i)).javaType.indexOf("Long") >= 0 || 
               ((Field)entity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || 
               ((Field)entity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
      [ltp]=UtilFormatOut.formatQuantity(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>
    <%}else{%>
      [ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>
    <%}%>
    </td>
  </tr>
<%}%>
[ltp]} //end if <%=GenUtil.lowerFirstChar(entity.ejbName)%> == null %>
</table>

<a href="[ltp]=response.encodeURL("Find<%=entity.ejbName%>.jsp")%>" class="buttontext">[Find <%=entity.ejbName%>]</a>
[ltp]if(hasCreatePermission){%>
  <a href="[ltp]=response.encodeURL("Edit<%=entity.ejbName%>.jsp")%>" class="buttontext">[Create <%=entity.ejbName%>]</a>
[ltp]}%>
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
  [ltp]if(hasDeletePermission){%>
    <a href="[ltp]=response.encodeURL("Edit<%=entity.ejbName%>.jsp?WEBEVENT=UPDATE_<%=entity.tableName%>&UPDATE_MODE=DELETE&" + <%=entity.httpArgList(entity.pks)%>)%>" class="buttontext">[Delete this <%=entity.ejbName%>]</a>
  [ltp]}%>
[ltp]}%>
[ltp]if(hasUpdatePermission){%>
  [ltp]if(<%=entity.pkNameString(" != null && ", " != null")%>){%>
    <a href="[ltp]=response.encodeURL("Edit<%=entity.ejbName%>.jsp?" + <%=entity.httpArgList(entity.pks)%>)%>" class="buttontext">[Edit <%=entity.ejbName%>]</a>
  [ltp]}%>
[ltp]}%>
<%for(int relIndex=0;relIndex<entity.relations.size();relIndex++){%>
  <%Relation relation = (Relation)entity.relations.elementAt(relIndex);%>
  <%Entity relatedEntity = DefReader.getEntity(defFileName,relation.relatedEjbName);%>
  <%if(relation.relationType.equalsIgnoreCase("one")){%>
[ltp]-- Start Relation for <%=relation.relatedEjbName%>, type: <%=relation.relationType%> --%>
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
  [ltp]if(Security.hasEntityPermission("<%=relatedEntity.tableName%>", "_VIEW", session)){%>
    [ltp]<%=relatedEntity.ejbName%> <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%> = <%=relatedEntity.ejbName%>Helper.findByPrimaryKey(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.keyMapUpperString("(), " + GenUtil.lowerFirstChar(entity.ejbName) + ".get", "()")%>);%>
    <hr>
    <b>Related Entity: <%=relatedEntity.ejbName%> with (<%=relatedEntity.colNameString(relatedEntity.pks)%>: [ltp]=<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.keyMapUpperString("()%" + ">, [ltp]=" + GenUtil.lowerFirstChar(entity.ejbName) + ".get", "()%" + ">")%>)</b>
    <br>
    [ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.keyMapUpperString("() != null && " + GenUtil.lowerFirstChar(entity.ejbName) + ".get", "() != null")%>){%>
      <%
        String packagePath = relatedEntity.packageName.replace('.','/');
        //remove the first two folders (usually org/ and ofbiz/)
        packagePath = packagePath.substring(packagePath.indexOf("/")+1);
        packagePath = packagePath.substring(packagePath.indexOf("/")+1);
      %>
      <a href="[ltp]=response.encodeURL("/<%=packagePath%>/View<%=relatedEntity.ejbName%>.jsp?" + <%=relatedEntity.httpRelationArgList(relatedEntity.pks, relation)%>)%>" class="buttontext">[View <%=relatedEntity.ejbName%> Details]</a>
    [ltp]}%>
    <table border="0" cellspacing="2" cellpadding="2">
    [ltp]if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%> == null){%>
    <tr bgcolor="[ltp]=rowColor1%>"><td><h3>Specified <%=relatedEntity.ejbName%> was not found.</h3></td></tr>
    [ltp]}else{%>
<%for(i=0;i<relatedEntity.fields.size();i++){%>
  [ltp]rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="[ltp]=rowColor%>">
    <td><b><%=((Field)relatedEntity.fields.elementAt(i)).columnName%></b></td>
    <td>
    <%if(((Field)relatedEntity.fields.elementAt(i)).javaType.equals("Timestamp") || 
         ((Field)relatedEntity.fields.elementAt(i)).javaType.equals("java.sql.Timestamp")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%> != null)
        {
          java.sql.Timestamp timeStamp = <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>();
          if(timeStamp  != null)
          {
            dateString = UtilDateTime.toDateString(timeStamp);
            timeString = UtilDateTime.toTimeString(timeStamp);
          }
        }
      %>
      [ltp]=UtilFormatOut.checkNull(dateString)%>&nbsp;[ltp]=UtilFormatOut.checkNull(timeString)%>
      [ltp]}%>
    <%} else if(((Field)relatedEntity.fields.elementAt(i)).javaType.equals("Date") || 
                ((Field)relatedEntity.fields.elementAt(i)).javaType.equals("java.util.Date")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%> != null)
        {
          java.util.Date date = <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      [ltp]=UtilFormatOut.checkNull(dateString)%>&nbsp;[ltp]=UtilFormatOut.checkNull(timeString)%>
      [ltp]}%>
    <%}else if(((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || 
               ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Long") >= 0 || 
               ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || 
               ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
      [ltp]=UtilFormatOut.formatQuantity(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>())%>
    <%}else{%>
      [ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>())%>
    <%}%>
    </td>
  </tr>
<%}%>
    [ltp]} //end if <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%> == null %>
    </table>
  [ltp]}%>
[ltp]}%>
[ltp]-- End Relation for <%=relation.relatedEjbName%>, type: <%=relation.relationType%> --%>
  <%}else if(relation.relationType.equalsIgnoreCase("many")){%>
[ltp]-- Start Relation for <%=relation.relatedEjbName%>, type: <%=relation.relationType%> --%>
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
  [ltp]if(Security.hasEntityPermission("<%=relatedEntity.tableName%>", "_VIEW", session)){%>    
    [ltp]Iterator relatedIterator = <%=relatedEntity.ejbName%>Helper.findBy<%=relation.keyMapUpperString("And","")%>Iterator(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.keyMapUpperString("(), " + GenUtil.lowerFirstChar(entity.ejbName) + ".get", "()")%>);%>
    <hr>
    <b>Related Entities: <%=relatedEntity.ejbName%> with (<%=relation.keyMapRelatedColumnString(", ", "")%>: [ltp]=<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=relation.keyMapUpperString("()%" + ">, [ltp]=" + GenUtil.lowerFirstChar(entity.ejbName) + ".get", "()%" + ">")%>)</b>
    <br>
    [ltp]boolean relatedCreatePerm = Security.hasEntityPermission("<%=relatedEntity.tableName%>", "_CREATE", session);%>
    [ltp]boolean relatedUpdatePerm = Security.hasEntityPermission("<%=relatedEntity.tableName%>", "_UPDATE", session);%>
    [ltp]boolean relatedDeletePerm = Security.hasEntityPermission("<%=relatedEntity.tableName%>", "_DELETE", session);%>
    [ltp]
      String rowColorResultHeader = "99CCFF";
      String rowColorResult1 = "99FFCC";
      String rowColorResult2 = "CCFFCC"; 
      String rowColorResult = "";
    %>
    [ltp]if(relatedCreatePerm){%>
      <%
        String packagePath = relatedEntity.packageName.replace('.','/');
        //remove the first two folders (usually org/ and ofbiz/)
        packagePath = packagePath.substring(packagePath.indexOf("/")+1);
        packagePath = packagePath.substring(packagePath.indexOf("/")+1);
      %>
      <a href="[ltp]=response.encodeURL("/<%=packagePath%>/Edit<%=relatedEntity.ejbName%>.jsp?" + <%=relatedEntity.httpRelationArgList(relation)%>)%>" class="buttontext">[Create <%=relatedEntity.ejbName%>]</a>
    [ltp]}%>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr bgcolor="[ltp]=rowColorResultHeader%>">
  <%for(i=0;i<relatedEntity.fields.size();i++){%>
      <td><div class="tabletext"><b><nobr><%=((Field)relatedEntity.fields.elementAt(i)).columnName%></nobr></b></div></td><%}%>
      <td>&nbsp;</td>
      [ltp]if(relatedUpdatePerm){%>
        <td>&nbsp;</td>
      [ltp]}%>
      [ltp]if(relatedDeletePerm){%>
        <td>&nbsp;</td>
      [ltp]}%>
    </tr>
    [ltp]
     if(relatedIterator != null && relatedIterator.hasNext())
     {
      while(relatedIterator != null && relatedIterator.hasNext())
      {
        <%=relatedEntity.ejbName%> <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%> = (<%=relatedEntity.ejbName%>)relatedIterator.next();
        if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%> != null)
        {
    %>
    [ltp]rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="[ltp]=rowColorResult%>">
  <%for(i=0;i<relatedEntity.fields.size();i++){%>
      <td>
        <div class="tabletext">
    <%if(((Field)relatedEntity.fields.elementAt(i)).javaType.equals("Timestamp") || 
         ((Field)relatedEntity.fields.elementAt(i)).javaType.equals("java.sql.Timestamp")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%> != null)
        {
          java.sql.Timestamp timeStamp = <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>();
          if(timeStamp  != null)
          {
            dateString = UtilDateTime.toDateString(timeStamp);
            timeString = UtilDateTime.toTimeString(timeStamp);
          }
        }
      %>
      [ltp]=UtilFormatOut.checkNull(dateString)%>&nbsp;[ltp]=UtilFormatOut.checkNull(timeString)%>
      [ltp]}%>
    <%} else if(((Field)relatedEntity.fields.elementAt(i)).javaType.equals("Date") || 
                ((Field)relatedEntity.fields.elementAt(i)).javaType.equals("java.util.Date")){%>
      [ltp]{
        String dateString = null;
        String timeString = null;
        if(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%> != null)
        {
          java.util.Date date = <%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      [ltp]=UtilFormatOut.checkNull(dateString)%>&nbsp;[ltp]=UtilFormatOut.checkNull(timeString)%>
      [ltp]}%>
    <%}else if(((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || 
               ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Long") >= 0 || 
               ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || 
               ((Field)relatedEntity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
      [ltp]=UtilFormatOut.formatQuantity(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>())%>
    <%}else{%>
      [ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(relatedEntity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)relatedEntity.fields.elementAt(i)).fieldName)%>())%>
    <%}%>
        &nbsp;</div>
      </td>
  <%}%>
      <td>
        <a href="[ltp]=response.encodeURL("/<%=packagePath%>/View<%=relatedEntity.ejbName%>.jsp?" + <%=relatedEntity.httpArgListFromClass(relatedEntity.pks)%>)%>" class="buttontext">[View]</a>
      </td>
      [ltp]if(relatedUpdatePerm){%>
        <td>
          <a href="[ltp]=response.encodeURL("/<%=packagePath%>/Edit<%=relatedEntity.ejbName%>.jsp?" + <%=relatedEntity.httpArgListFromClass(relatedEntity.pks)%>)%>" class="buttontext">[Edit]</a>
        </td>
      [ltp]}%>
      [ltp]if(relatedDeletePerm){%>
        <td>
          [ltp]-- <a href="[ltp]=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="[ltp]=response.encodeURL("View<%=relatedEntity.ejbName%>.jsp?" + <%=entity.httpArgList(entity.pks)%> + "&WEBEVENT=UPDATE_<%=relatedEntity.tableName%>&UPDATE_MODE=DELETE&" + <%=relatedEntity.httpArgListFromClass(relatedEntity.pks)%>)%>" class="buttontext">[Delete]</a>
        </td>
      [ltp]}%>
    </tr>
    [ltp]}%>
  [ltp]}%>
[ltp]}else{%>
[ltp]rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="[ltp]=rowColorResult%>">
<td colspan="8">
<h3>No <%=relatedEntity.ejbName%>s Found.</h3>
</td>
</tr>
[ltp]}%>
</table>
    [ltp]if(relatedCreatePerm){%>
      <a href="[ltp]=response.encodeURL("/<%=packagePath%>/Edit<%=relatedEntity.ejbName%>.jsp?" + <%=relatedEntity.httpRelationArgList(relation)%>)%>" class="buttontext">[Create <%=relatedEntity.ejbName%>]</a>
    [ltp]}%>
  [ltp]}%>
[ltp]}%>
[ltp]-- End Relation for <%=relation.relatedEjbName%>, type: <%=relation.relationType%> --%>
  <%}%>
<%}%>

<br>
[ltp]}else{%>
  <h3>You do not have permission to view this page (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_VIEW needed).</h3>
[ltp]}%>

[ltp]@ include file="/includes/onecolumnclose.jsp" %>
[ltp]@ include file="/includes/footer.jsp" %>
