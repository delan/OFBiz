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
[ltp]@ page import="java.text.*" %>
[ltp]@ page import="java.util.*" %>
[ltp]@ page import="org.ofbiz.commonapp.common.*" %>
[ltp]@ page import="org.ofbiz.commonapp.webevent.*" %>
[ltp]@ page import="org.ofbiz.commonapp.security.*" %>

[ltp]@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

[ltp]pageContext.setAttribute("PageName", "Find<%=entity.ejbName%>"); %>

[ltp]@ include file="/includes/header.jsp" %>
[ltp]@ include file="/includes/onecolumn.jsp" %>

[ltp]boolean hasViewPermission=Security.hasEntityPermission("<%=entity.tableName%>", "_VIEW", session);%>
[ltp]boolean hasCreatePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_CREATE", session);%>
[ltp]boolean hasUpdatePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_UPDATE", session);%>
[ltp]boolean hasDeletePermission=Security.hasEntityPermission("<%=entity.tableName%>", "_DELETE", session);%>
[ltp]if(hasViewPermission){%>
[ltp]
  String rowClassTop1 = "viewOneTR1";
  String rowClassTop2 = "viewOneTR2";
  String rowClassTop = "";
  String rowClassResultIndex = "viewOneTR2";
  String rowClassResultHeader = "viewOneTR1";
  String rowClassResult1 = "viewManyTR1";
  String rowClassResult2 = "viewManyTR2";
  String rowClassResult = "";

  String searchType = request.getParameter("SEARCH_TYPE");
  String searchParam1 = UtilFormatOut.checkNull(request.getParameter("SEARCH_PARAMETER1"));
  String searchParam2 = UtilFormatOut.checkNull(request.getParameter("SEARCH_PARAMETER2"));
  String searchParam3 = UtilFormatOut.checkNull(request.getParameter("SEARCH_PARAMETER3"));
  if(searchType == null || searchType.length() <= 0) searchType = "all";
  String curFindString = "SEARCH_TYPE=" + searchType + "&SEARCH_PARAMETER1=" + searchParam1 + "&SEARCH_PARAMETER2=" + searchParam2 + "&SEARCH_PARAMETER3=" + searchParam3;
  curFindString = UtilFormatOut.encodeQuery(curFindString);

  Collection <%=GenUtil.lowerFirstChar(entity.ejbName)%>Collection = null;
  Object[] <%=GenUtil.lowerFirstChar(entity.ejbName)%>Array = (Object[])session.getAttribute("CACHE_SEARCH_RESULTS");
%>
[ltp]
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
  String <%=GenUtil.lowerFirstChar(entity.ejbName)%>ArrayName = (String)session.getAttribute("CACHE_SEARCH_RESULTS_NAME");
  if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Array == null || <%=GenUtil.lowerFirstChar(entity.ejbName)%>ArrayName == null || curFindString.compareTo(<%=GenUtil.lowerFirstChar(entity.ejbName)%>ArrayName) != 0 || viewIndex == 0)
  {
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("-=-=-=-=- Current Array not found in session, getting new one...");
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("-=-=-=-=- curFindString:" + curFindString + " <%=GenUtil.lowerFirstChar(entity.ejbName)%>ArrayName:" + <%=GenUtil.lowerFirstChar(entity.ejbName)%>ArrayName);

    if(searchType.compareTo("all") == 0) <%=GenUtil.lowerFirstChar(entity.ejbName)%>Collection = <%=entity.ejbName%>Helper.findAll();
<%for(i=0;i<entity.finders.size();i++){%><%Finder finderDesc = (Finder)entity.finders.elementAt(i);%>
    else if(searchType.compareTo("<%=entity.classNameString(finderDesc.fields,"And","")%>") == 0) <%=GenUtil.lowerFirstChar(entity.ejbName)%>Collection = <%=entity.ejbName%>Helper.findBy<%=entity.classNameString(finderDesc.fields,"And","")%>(<%=entity.fieldsStringList(finderDesc.fields,"searchParam",", ",true)%>);
<%}%>
    else if(searchType.compareTo("primaryKey") == 0)
    {
      <%=GenUtil.lowerFirstChar(entity.ejbName)%>Collection = new LinkedList();
      <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>Temp = <%=entity.ejbName%>Helper.findByPrimaryKey(<%=entity.fieldsStringList(entity.pks,"searchParam",", ",true)%>);
      if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Temp != null) <%=GenUtil.lowerFirstChar(entity.ejbName)%>Collection.add(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Temp);
    }
    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Collection != null) <%=GenUtil.lowerFirstChar(entity.ejbName)%>Array = <%=GenUtil.lowerFirstChar(entity.ejbName)%>Collection.toArray();

    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Array != null)
    {
      session.setAttribute("CACHE_SEARCH_RESULTS", <%=GenUtil.lowerFirstChar(entity.ejbName)%>Array);
      session.setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
    }
  }
//--------------
  int lowIndex = viewIndex*viewSize+1;
  int highIndex = (viewIndex+1)*viewSize;
  int arraySize = 0;
  if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Array!=null) arraySize = <%=GenUtil.lowerFirstChar(entity.ejbName)%>Array.length;
  if(arraySize<highIndex) highIndex=arraySize;
  if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("viewIndex=" + viewIndex + " lowIndex=" + lowIndex + " highIndex=" + highIndex + " arraySize=" + arraySize);
%>
<h3 style=margin:0;>Find <%=entity.ejbName%>s</h3>
Note: you may use the '%' character as a wildcard, to replace any other letters.
<table cellpadding="2" cellspacing="2" border="0">
  [ltp]rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="[ltp]=rowClassTop%>">
    <form method="post" action="[ltp]=response.encodeURL("Find<%=entity.ejbName%>.jsp")%>" style=margin:0;>
      <td valign="top">Primary Key:</td>
      <td valign="top">
          <input type="hidden" name="SEARCH_TYPE" value="primaryKey">
        <%for(int j=0;j<entity.pks.size();j++){%>
          <input type="text" name="SEARCH_PARAMETER<%=j+1%>" value="" size="20"><%}%>
          (Must be exact)
      </td>
      <td valign="top">
          <input type="submit" value="Find">
      </td>
    </form>
  </tr>
<%for(i=0;i<entity.finders.size();i++){%><%Finder finderDesc = (Finder)entity.finders.elementAt(i);%>
  [ltp]rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="[ltp]=rowClassTop%>">
    <td valign="top"><%=entity.classNameString(finderDesc.fields," and ","")%>: </td>
    <form method="post" action="[ltp]=response.encodeURL("Find<%=entity.ejbName%>.jsp")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="<%=entity.classNameString(finderDesc.fields,"And","")%>">
      <%for(int j=0;j<finderDesc.fields.size();j++){%>
        <input type="text" name="SEARCH_PARAMETER<%=j+1%>" value="" size="20"><%}%>
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>
<%}%>
  [ltp]rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="[ltp]=rowClassTop%>">
    <td valign="top">Display All: </td>
    <form method="post" action="[ltp]=response.encodeURL("Find<%=entity.ejbName%>.jsp")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="all">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>
</table>
<b><%=entity.ejbName%>s found by:&nbsp; [ltp]=searchType%> : [ltp]=UtilFormatOut.checkNull(searchParam1)%> : [ltp]=UtilFormatOut.checkNull(searchParam2)%> : [ltp]=UtilFormatOut.checkNull(searchParam3)%></b>
<br>
[ltp]if(hasCreatePermission){%>
  <a href="[ltp]=response.encodeURL("View<%=entity.ejbName%>.jsp")%>" class="buttontext">[Create New <%=entity.ejbName%>]</a>
[ltp]}%>
<table border="0" width="100%" cellpadding="2">
[ltp] if(arraySize > 0) { %>
    <tr class="[ltp]=rowClassResultIndex%>">
      <td align="left">
        <b>
        [ltp] if(viewIndex > 0) { %>
          <a href="[ltp]=response.encodeURL("Find<%=entity.ejbName%>.jsp?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        [ltp] } %>
        [ltp] if(arraySize > 0) { %>
          [ltp]=lowIndex%> - [ltp]=highIndex%> of [ltp]=arraySize%>
        [ltp] } %>
        [ltp] if(arraySize>highIndex) { %>
          | <a href="[ltp]=response.encodeURL("Find<%=entity.ejbName%>.jsp?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        [ltp] } %>
        </b>
      </td>
    </tr>
[ltp] } %>
</table>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="[ltp]=rowClassResultHeader%>">
  <%for(i=0;i<entity.fields.size();i++){%>
      <td><div class="tabletext"><b><nobr><%=((Field)entity.fields.elementAt(i)).columnName%></nobr></b></div></td><%}%>
      <td>&nbsp;</td>
      [ltp]if(hasDeletePermission){%>
        <td>&nbsp;</td>
      [ltp]}%>
    </tr>
[ltp]
 if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Array != null && <%=GenUtil.lowerFirstChar(entity.ejbName)%>Array.length > 0)
 {
  int loopIndex;
  //for(loopIndex=<%=GenUtil.lowerFirstChar(entity.ejbName)%>Array.length-1; loopIndex>=0 ; loopIndex--)
  for(loopIndex=lowIndex; loopIndex<=highIndex; loopIndex++)
  {
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = (<%=entity.ejbName%>)<%=GenUtil.lowerFirstChar(entity.ejbName)%>Array[loopIndex-1];
    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null)
    {
%>
    [ltp]rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="[ltp]=rowClassResult%>">
  <%for(i=0;i<entity.fields.size();i++){%>
      <td>
        <div class="tabletext"><%if(((Field)entity.fields.elementAt(i)).javaType.equals("Timestamp") || ((Field)entity.fields.elementAt(i)).javaType.equals("java.sql.Timestamp")){%>
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
        &nbsp;</div>
      </td><%}%>
      <td>
        <a href="[ltp]=response.encodeURL("View<%=entity.ejbName%>.jsp?" + <%=entity.httpArgListFromClass(entity.pks)%>)%>" class="buttontext">[View]</a>
      </td>
      [ltp]if(hasDeletePermission){%>
        <td>
          <a href="[ltp]=response.encodeURL("Find<%=entity.ejbName%>.jsp?WEBEVENT=UPDATE_<%=entity.tableName%>&UPDATE_MODE=DELETE&" + <%=entity.httpArgListFromClass(entity.pks)%> + "&" + curFindString)%>" class="buttontext">[Delete]</a>
        </td>
      [ltp]}%>
    </tr>
  [ltp]}%>
[ltp]
   }
 }
 else
 {
%>
[ltp]rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="[ltp]=rowClassResult%>">
<td colspan="<%=entity.fields.size() + 2%>">
<h3>No <%=entity.ejbName%>s Found.</h3>
</td>
</tr>
[ltp]}%>
</table>

<table border="0" width="100%" cellpadding="2">
[ltp] if(arraySize > 0) { %>
    <tr class="[ltp]=rowClassResultIndex%>">
      <td align="left">
        <b>
        [ltp] if(viewIndex > 0) { %>
          <a href="[ltp]=response.encodeURL("Find<%=entity.ejbName%>.jsp?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        [ltp] } %>
        [ltp] if(arraySize > 0) { %>
          [ltp]=lowIndex%> - [ltp]=highIndex%> of [ltp]=arraySize%>
        [ltp] } %>
        [ltp] if(arraySize>highIndex) { %>
          | <a href="[ltp]=response.encodeURL("Find<%=entity.ejbName%>.jsp?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        [ltp] } %>
        </b>
      </td>
    </tr>
[ltp] } %>
</table>
[ltp]if(hasCreatePermission){%>
  <a href="[ltp]=response.encodeURL("View<%=entity.ejbName%>.jsp")%>" class="buttontext">[Create <%=entity.ejbName%>]</a>
[ltp]}%>
[ltp]}else{%>
  <h3>You do not have permission to view this page (<%=entity.tableName%>_ADMIN, or <%=entity.tableName%>_VIEW needed).</h3>
[ltp]}%>

[ltp]@ include file="/includes/onecolumnclose.jsp" %>
[ltp]@ include file="/includes/footer.jsp" %>
