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

[ltp]boolean hasViewPermission=Security.hasEntityPermission("<%=entity.tableName%>", "_VIEW", session);%>
[ltp]boolean hasCreatePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_CREATE", session);%>
[ltp]boolean hasUpdatePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_UPDATE", session);%>
[ltp]boolean hasDeletePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_DELETE", session);%>
[ltp]if(hasViewPermission){%>

[ltp]
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
[ltp]if(hasDeletePermission){%>
  [ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
    <a href="Edit<%=entity.ejbName%>.jsp?WEBEVENT=UPDATE_<%=entity.tableName%>&UPDATE_MODE=DELETE&<%=entity.httpArgList(entity.pks)%>" class="buttontext">[Delete this <%=entity.ejbName%>]</a>
  [ltp]}%>
[ltp]}%>
[ltp]if(hasUpdatePermission){%>
  [ltp]if(<%=entity.pkNameString(" != null && ", " != null")%>){%>
    <a href="Edit<%=GenUtil.lowerFirstChar(entity.ejbName)%>.jsp?<%=entity.httpArgList(entity.pks)%>" class="buttontext">[Edit <%=entity.ejbName%>]</a>
  [ltp]}%>
[ltp]}%>

<table border="0" cellspacing="2" cellpadding="2">
[ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null){%>
<tr bgcolor="[ltp]=rowColor1%>"><td><h3>Specified <%=entity.ejbName%> was not found.</h3></td></tr>
[ltp]}else{%>
  <input type="hidden" name="WEBEVENT" value="UPDATE_<%=entity.tableName%>">
  <input type="hidden" name="UPDATE_MODE" value="UPDATE">
<%for(i=0;i<entity.fields.size();i++){%>
  [ltp]rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="[ltp]=rowColor%>">
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
      %>
      Date(MM/DD/YYYY):[ltp]=UtilFormatOut.checkNull(dateString)%>
      Time(HH:MM):[ltp]=UtilFormatOut.checkNull(timeString)%>
      [ltp]}%>
    <%} else if(((Field)entity.fields.elementAt(i)).javaType.indexOf("Integer") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Double") >= 0 || ((Field)entity.fields.elementAt(i)).javaType.indexOf("Float") >= 0){%>
      [ltp]=UtilFormatOut.formatQuantity(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>
    <%} else {%>
      [ltp]=UtilFormatOut.checkNull(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>())%>
    <%}%>
    </td>
  </tr>
<%}%>
[ltp]} //end if <%=GenUtil.lowerFirstChar(entity.ejbName)%> == null %>
</table>

<a href="Find<%=entity.ejbName%>.jsp" class="buttontext">[Find <%=entity.ejbName%>]</a>
[ltp]if(hasCreatePermission){%>
  <a href="Edit<%=entity.ejbName%>.jsp" class="buttontext">[Create <%=entity.ejbName%>]</a>
[ltp]}%>
[ltp]if(hasDeletePermission){%>
  [ltp]if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null){%>
    <a href="Edit<%=entity.ejbName%>.jsp?WEBEVENT=UPDATE_<%=entity.tableName%>&UPDATE_MODE=DELETE&<%=entity.httpArgList(entity.pks)%>" class="buttontext">[Delete this <%=entity.ejbName%>]</a>
  [ltp]}%>
[ltp]}%>
[ltp]if(hasUpdatePermission){%>
  [ltp]if(<%=entity.pkNameString(" != null && ", " != null")%>){%>
    <a href="Edit<%=GenUtil.lowerFirstChar(entity.ejbName)%>.jsp?<%=entity.httpArgList(entity.pks)%>" class="buttontext">[Edit <%=entity.ejbName%>]</a>
  [ltp]}%>
[ltp]}%>
<br>
[ltp]}else{%>
  <h3>You do not have permission to view this page (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_VIEW needed).</h3>
[ltp]}%>

[ltp]@ include file="/includes/onecolumnclose.jsp" %>
[ltp]@ include file="/includes/footer.jsp" %>
