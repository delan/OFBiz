
<%
/**
 *  Title: Person Component - Person Person Type Entity
 *  Description: Maps a Person to a Person Type; necessary so a person can be of multiple types.
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
 *@author     David E. Jones
 *@created    Mon May 28 22:02:22 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="org.ofbiz.commonapp.person.*" %>
<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>

<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "FindPersonPersonType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PERSON_PERSON_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PERSON_PERSON_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PERSON_PERSON_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PERSON_PERSON_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>
<%
  String rowColorTop1 = "99CCFF";
  String rowColorTop2 = "CCFFFF";
  String rowColorTop = "";
  String rowColorResultIndex = "CCFFFF";
  String rowColorResultHeader = "99CCFF";
  String rowColorResult1 = "99FFCC";
  String rowColorResult2 = "CCFFCC";
  String rowColorResult = "";

  String searchType = request.getParameter("SEARCH_TYPE");
  String searchParam1 = UtilFormatOut.checkNull(request.getParameter("SEARCH_PARAMETER1"));
  String searchParam2 = UtilFormatOut.checkNull(request.getParameter("SEARCH_PARAMETER2"));
  String searchParam3 = UtilFormatOut.checkNull(request.getParameter("SEARCH_PARAMETER3"));
  if(searchType == null || searchType.length() <= 0) searchType = "all";
  String curFindString = "SEARCH_TYPE=" + searchType + "&SEARCH_PARAMETER1=" + searchParam1 + "&SEARCH_PARAMETER2=" + searchParam2 + "&SEARCH_PARAMETER3=" + searchParam3;
  curFindString = UtilFormatOut.encodeQuery(curFindString);

  Collection personPersonTypeCollection = null;
  Object[] personPersonTypeArray = (Object[])session.getAttribute("CACHE_SEARCH_RESULTS");
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
  String personPersonTypeArrayName = (String)session.getAttribute("CACHE_SEARCH_RESULTS_NAME");
  if(personPersonTypeArray == null || personPersonTypeArrayName == null || curFindString.compareTo(personPersonTypeArrayName) != 0 || viewIndex == 0)
  {
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("-=-=-=-=- Current Array not found in session, getting new one...");
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("-=-=-=-=- curFindString:" + curFindString + " personPersonTypeArrayName:" + personPersonTypeArrayName);

    if(searchType.compareTo("all") == 0) personPersonTypeCollection = PersonPersonTypeHelper.findAll();

    else if(searchType.compareTo("Username") == 0) personPersonTypeCollection = PersonPersonTypeHelper.findByUsername(searchParam1);

    else if(searchType.compareTo("TypeId") == 0) personPersonTypeCollection = PersonPersonTypeHelper.findByTypeId(searchParam1);

    else if(searchType.compareTo("primaryKey") == 0)
    {
      personPersonTypeCollection = new LinkedList();
      PersonPersonType personPersonTypeTemp = PersonPersonTypeHelper.findByPrimaryKey(searchParam1, searchParam2);
      if(personPersonTypeTemp != null) personPersonTypeCollection.add(personPersonTypeTemp);
    }
    if(personPersonTypeCollection != null) personPersonTypeArray = personPersonTypeCollection.toArray();

    if(personPersonTypeArray != null)
    {
      session.setAttribute("CACHE_SEARCH_RESULTS", personPersonTypeArray);
      session.setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
    }
  }
//--------------
  int lowIndex = viewIndex*viewSize+1;
  int highIndex = (viewIndex+1)*viewSize;
  int arraySize = 0;
  if(personPersonTypeArray!=null) arraySize = personPersonTypeArray.length;
  if(arraySize<highIndex) highIndex=arraySize;
  if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("viewIndex=" + viewIndex + " lowIndex=" + lowIndex + " highIndex=" + highIndex + " arraySize=" + arraySize);
%>
<h3 style=margin:0;>Find PersonPersonTypes</h3>
Note: you may use the '%' character as a wildcard, to replace any other letters.
<table cellpadding="2" cellspacing="2" border="0">
  <%rowColorTop=(rowColorTop==rowColorTop1?rowColorTop2:rowColorTop1);%><tr bgcolor="<%=rowColorTop%>">
    <form method="post" action="<%=response.encodeURL("FindPersonPersonType.jsp")%>" style=margin:0;>
      <td valign="top">Primary Key:</td>
      <td valign="top">
          <input type="hidden" name="SEARCH_TYPE" value="primaryKey">

          <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
          <input type="text" name="SEARCH_PARAMETER2" value="" size="20">
          (Must be exact)
      </td>
      <td valign="top">
          <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  
  <%rowColorTop=(rowColorTop==rowColorTop1?rowColorTop2:rowColorTop1);%><tr bgcolor="<%=rowColorTop%>">
    <td valign="top">Username: </td>
    <form method="post" action="<%=response.encodeURL("FindPersonPersonType.jsp")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="Username">

        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  
  <%rowColorTop=(rowColorTop==rowColorTop1?rowColorTop2:rowColorTop1);%><tr bgcolor="<%=rowColorTop%>">
    <td valign="top">TypeId: </td>
    <form method="post" action="<%=response.encodeURL("FindPersonPersonType.jsp")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="TypeId">

        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowColorTop=(rowColorTop==rowColorTop1?rowColorTop2:rowColorTop1);%><tr bgcolor="<%=rowColorTop%>">
    <td valign="top">Display All: </td>
    <form method="post" action="<%=response.encodeURL("FindPersonPersonType.jsp")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="all">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>
</table>
<b>PersonPersonTypes found by:&nbsp; <%=searchType%> : <%=UtilFormatOut.checkNull(searchParam1)%> : <%=UtilFormatOut.checkNull(searchParam2)%> : <%=UtilFormatOut.checkNull(searchParam3)%></b>
<br>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPersonPersonType.jsp")%>" class="buttontext">[Create PersonPersonType]</a>
<%}%>
<table border="0" width="100%" cellpadding="2">
<% if(arraySize > 0) { %>
    <tr bgcolor="<%=rowColorResultIndex%>">
      <td align="left">
        <b>
        <% if(viewIndex > 0) { %>
          <a href="<%=response.encodeURL("FindPersonPersonType.jsp?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL("FindPersonPersonType.jsp?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr bgcolor="<%=rowColorResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>USERNAME</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>TYPE_ID</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(hasUpdatePermission){%>
        <td>&nbsp;</td>
      <%}%>
      <%if(hasDeletePermission){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
<%
 if(personPersonTypeArray != null && personPersonTypeArray.length > 0)
 {
  int loopIndex;
  //for(loopIndex=personPersonTypeArray.length-1; loopIndex>=0 ; loopIndex--)
  for(loopIndex=lowIndex; loopIndex<=highIndex; loopIndex++)
  {
    PersonPersonType personPersonType = (PersonPersonType)personPersonTypeArray[loopIndex-1];
    if(personPersonType != null)
    {
%>
    <%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(personPersonType.getUsername())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(personPersonType.getTypeId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL("ViewPersonPersonType.jsp?" + "PERSON_PERSON_TYPE_USERNAME=" + personPersonType.getUsername() + "&" + "PERSON_PERSON_TYPE_TYPE_ID=" + personPersonType.getTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(hasUpdatePermission){%>
        <td>
          <a href="<%=response.encodeURL("EditPersonPersonType.jsp?" + "PERSON_PERSON_TYPE_USERNAME=" + personPersonType.getUsername() + "&" + "PERSON_PERSON_TYPE_TYPE_ID=" + personPersonType.getTypeId())%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(hasDeletePermission){%>
        <td>
          <a href="<%=response.encodeURL("FindPersonPersonType.jsp?WEBEVENT=UPDATE_PERSON_PERSON_TYPE&UPDATE_MODE=DELETE&" + "PERSON_PERSON_TYPE_USERNAME=" + personPersonType.getUsername() + "&" + "PERSON_PERSON_TYPE_TYPE_ID=" + personPersonType.getTypeId() + "&" + curFindString)%>" class="buttontext">[Delete]</a>
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
<%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
<td colspan="8">
<h3>No PersonPersonTypes Found.</h3>
</td>
</tr>
<%}%>
</table>

<table border="0" width="100%" cellpadding="2">
<% if(arraySize > 0) { %>
    <tr bgcolor="<%=rowColorResultIndex%>">
      <td align="left">
        <b>
        <% if(viewIndex > 0) { %>
          <a href="<%=response.encodeURL("FindPersonPersonType.jsp?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL("FindPersonPersonType.jsp?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPersonPersonType.jsp")%>" class="buttontext">[Create PersonPersonType]</a>
<%}%>
<%}else{%>
  <h3>You do not have permission to view this page (PERSON_PERSON_TYPE_ADMIN, or PERSON_PERSON_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
