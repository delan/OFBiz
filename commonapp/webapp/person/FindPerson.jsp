
<%
/**
 *  Title: Person Component - Person Entity
 *  Description: None
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
 *@created    Tue May 22 23:54:50 MDT 2001
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

<%pageContext.setAttribute("PageName", "FindPerson"); %>

<%@ include file="/includes/header.jsp" %> 
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PERSON", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PERSON", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PERSON", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PERSON", "_DELETE", session);%>
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

  Collection personCollection = null;
  Object[] personArray = (Object[])session.getAttribute("CACHE_SEARCH_RESULTS");
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
  String personArrayName = (String)session.getAttribute("CACHE_SEARCH_RESULTS_NAME");
  if(personArray == null || personArrayName == null || curFindString.compareTo(personArrayName) != 0 || viewIndex == 0)
  {
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("-=-=-=-=- Current Array not found in session, getting new one...");
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("-=-=-=-=- curFindString:" + curFindString + " personArrayName:" + personArrayName);

    if(searchType.compareTo("all") == 0) personCollection = PersonHelper.findAll();

    else if(searchType.compareTo("FirstName") == 0) personCollection = PersonHelper.findByFirstName(searchParam1);

    else if(searchType.compareTo("LastName") == 0) personCollection = PersonHelper.findByLastName(searchParam1);

    else if(searchType.compareTo("FirstNameAndLastName") == 0) personCollection = PersonHelper.findByFirstNameAndLastName(searchParam1,searchParam2);

    else if(searchType.compareTo("HomePhone") == 0) personCollection = PersonHelper.findByHomePhone(searchParam1);

    else if(searchType.compareTo("Email") == 0) personCollection = PersonHelper.findByEmail(searchParam1);

    else if(searchType.compareTo("primaryKey") == 0)
    {
      personCollection = new LinkedList();
      Person personTemp = PersonHelper.findByPrimaryKey(searchParam1);
      if(personTemp != null) personCollection.add(personTemp);
    }
    if(personCollection != null) personArray = personCollection.toArray();

    if(personArray != null)
    {
      session.setAttribute("CACHE_SEARCH_RESULTS", personArray);
      session.setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
    }
  }
//--------------
  int lowIndex = viewIndex*viewSize+1;
  int highIndex = (viewIndex+1)*viewSize;
  int arraySize = 0;
  if(personArray!=null) arraySize = personArray.length;
  if(arraySize<highIndex) highIndex=arraySize;
  if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true")) System.out.println("viewIndex=" + viewIndex + " lowIndex=" + lowIndex + " highIndex=" + highIndex + " arraySize=" + arraySize);
%>
<h3 style=margin:0;>Find Persons</h3>
Note: you may use the '%' character as a wildcard, to replace any other letters.
<table cellpadding="2" cellspacing="2" border="0">
  <%rowColorTop=(rowColorTop==rowColorTop1?rowColorTop2:rowColorTop1);%><tr bgcolor="<%=rowColorTop%>">
    <form method="post" action="FindPerson.jsp" style=margin:0;>
      <td valign="top">Primary Key:</td>
      <td valign="top">
          <input type="hidden" name="SEARCH_TYPE" value="primaryKey">

          <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
          (Must be exact)
      </td>
      <td valign="top">
          <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  
  <%rowColorTop=(rowColorTop==rowColorTop1?rowColorTop2:rowColorTop1);%><tr bgcolor="<%=rowColorTop%>">
    <td valign="top">FirstName: </td>
    <form method="post" action="FindPerson.jsp" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="FirstName">

        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  
  <%rowColorTop=(rowColorTop==rowColorTop1?rowColorTop2:rowColorTop1);%><tr bgcolor="<%=rowColorTop%>">
    <td valign="top">LastName: </td>
    <form method="post" action="FindPerson.jsp" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="LastName">

        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  
  <%rowColorTop=(rowColorTop==rowColorTop1?rowColorTop2:rowColorTop1);%><tr bgcolor="<%=rowColorTop%>">
    <td valign="top">FirstName and LastName: </td>
    <form method="post" action="FindPerson.jsp" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="FirstNameAndLastName">

        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
        <input type="text" name="SEARCH_PARAMETER2" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  
  <%rowColorTop=(rowColorTop==rowColorTop1?rowColorTop2:rowColorTop1);%><tr bgcolor="<%=rowColorTop%>">
    <td valign="top">HomePhone: </td>
    <form method="post" action="FindPerson.jsp" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="HomePhone">

        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  
  <%rowColorTop=(rowColorTop==rowColorTop1?rowColorTop2:rowColorTop1);%><tr bgcolor="<%=rowColorTop%>">
    <td valign="top">Email: </td>
    <form method="post" action="FindPerson.jsp" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="Email">

        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowColorTop=(rowColorTop==rowColorTop1?rowColorTop2:rowColorTop1);%><tr bgcolor="<%=rowColorTop%>">
    <td valign="top">Display All: </td>
    <form method="post" action="FindPerson.jsp" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="all">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>
</table>
<b>Persons found by:&nbsp; <%=searchType%> : <%=UtilFormatOut.checkNull(searchParam1)%> : <%=UtilFormatOut.checkNull(searchParam2)%> : <%=UtilFormatOut.checkNull(searchParam3)%></b>
<br>
<%if(hasCreatePermission){%>
  <a href="EditPerson.jsp" class="buttontext">[Create Person]</a>
<%}%>
<table border="0" width="100%" cellpadding="2">
<% if(arraySize > 0) { %>
    <tr bgcolor="<%=rowColorResultIndex%>">
      <td align="left">
        <b>
        <% if(viewIndex > 0) { %>
          <a href="FindPerson.jsp?<%=curFindString%>&VIEW_SIZE=<%=viewSize%>&VIEW_INDEX=<%=viewIndex-1%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="FindPerson.jsp?<%=curFindString%>&VIEW_SIZE=<%=viewSize%>&VIEW_INDEX=<%=viewIndex+1%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr bgcolor="<%=rowColorResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>Username</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>First Name</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>Last Name</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>Home Phone</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>Email</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>State</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(hasUpdatePermission){%>
        <td>&nbsp;</td>
      <%}%>
      <%if(hasDeletePermission){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
<%
 if(personArray != null && personArray.length > 0)
 {
  int loopIndex;
  //for(loopIndex=personArray.length-1; loopIndex>=0 ; loopIndex--)
  for(loopIndex=lowIndex; loopIndex<=highIndex; loopIndex++)
  {
    Person person = (Person)personArray[loopIndex-1];
    if(person != null)
    {
%>
    <%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(person.getUsername())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(person.getFirstName())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(person.getLastName())%>
    
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(person.getHomePhone())%>
    
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(person.getEmail())%>
    
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(person.getHomeState())%>
    
        &nbsp;</div>
      </td>
      <td>
        <a href="ViewPerson.jsp?PERSON_USERNAME=<%=person.getUsername()%>" class="buttontext">[View]</a>
      </td>
      <%if(hasUpdatePermission){%>
        <td>
          <a href="EditPerson.jsp?PERSON_USERNAME=<%=person.getUsername()%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(hasDeletePermission){%>
        <td>
          <a href="FindPerson.jsp?WEBEVENT=UPDATE_PERSON&UPDATE_MODE=DELETE&PERSON_USERNAME=<%=person.getUsername()%>&<%=curFindString%>" class="buttontext">[Delete]</a>
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
<h3>No Persons Found.</h3>
</td>
</tr>
<%}%>
</table>

<table border="0" width="100%" cellpadding="2">
<%if(arraySize > 0){%>
  <tr bgcolor="<%=rowColorResultIndex%>">
    <td align="left">
      <b>
      <% if(viewIndex > 0) { %>
      <a href="FindPerson.jsp?<%=curFindString%>&VIEW_SIZE=<%=viewSize%>&VIEW_INDEX=<%=viewIndex-1%>" class="buttontext">[Previous]</a> |
      <% } %>
      <% if(arraySize > 0) { %>
      <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
      <% } %>
      <% if(arraySize>highIndex) { %>
      | <a href="FindPerson.jsp?<%=curFindString%>&VIEW_SIZE=<%=viewSize%>&VIEW_INDEX=<%=viewIndex+1%>" class="buttontext">[Next]</a>
      <% } %>
      </b>
    </td>
  </tr>
<%}%>
</table>
<%if(hasCreatePermission){%>
  <a href="EditPerson.jsp" class="buttontext">[Create Person]</a>
<%}%>
<%}else{%>
  <h3>You do not have permission to view this page (PERSON_ADMIN, or PERSON_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
