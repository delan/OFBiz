
<%
/**
 *  Title: Party Facility Entity
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
 *@created    Fri Jul 27 01:37:22 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.storage.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "FindPartyFacility"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PARTY_FACILITY", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_FACILITY", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_FACILITY", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_FACILITY", "_DELETE", session);%>
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

  String searchType = request.getParameter("SEARCH_TYPE");
  String searchParam1 = UtilFormatOut.checkNull(request.getParameter("SEARCH_PARAMETER1"));
  String searchParam2 = UtilFormatOut.checkNull(request.getParameter("SEARCH_PARAMETER2"));
  String searchParam3 = UtilFormatOut.checkNull(request.getParameter("SEARCH_PARAMETER3"));
  if(searchType == null || searchType.length() <= 0) searchType = "all";
  String curFindString = "SEARCH_TYPE=" + searchType + "&SEARCH_PARAMETER1=" + searchParam1 + "&SEARCH_PARAMETER2=" + searchParam2 + "&SEARCH_PARAMETER3=" + searchParam3;
  curFindString = UtilFormatOut.encodeQuery(curFindString);

  Collection partyFacilityCollection = null;
  Object[] partyFacilityArray = (Object[])session.getAttribute("CACHE_SEARCH_RESULTS");
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
  String partyFacilityArrayName = (String)session.getAttribute("CACHE_SEARCH_RESULTS_NAME");
  if(partyFacilityArray == null || partyFacilityArrayName == null || curFindString.compareTo(partyFacilityArrayName) != 0 || viewIndex == 0)
  {
    Debug.logInfo("-=-=-=-=- Current Array not found in session, getting new one...");
    Debug.logInfo("-=-=-=-=- curFindString:" + curFindString + " partyFacilityArrayName:" + partyFacilityArrayName);

    if(searchType.compareTo("all") == 0) partyFacilityCollection = PartyFacilityHelper.findAll();

    else if(searchType.compareTo("PartyId") == 0) partyFacilityCollection = PartyFacilityHelper.findByPartyId(searchParam1);

    else if(searchType.compareTo("FacilityId") == 0) partyFacilityCollection = PartyFacilityHelper.findByFacilityId(searchParam1);

    else if(searchType.compareTo("FacilityRoleTypeId") == 0) partyFacilityCollection = PartyFacilityHelper.findByFacilityRoleTypeId(searchParam1);

    else if(searchType.compareTo("FacilityIdAndFacilityRoleTypeId") == 0) partyFacilityCollection = PartyFacilityHelper.findByFacilityIdAndFacilityRoleTypeId(searchParam1, searchParam2);

    else if(searchType.compareTo("primaryKey") == 0)
    {
      partyFacilityCollection = new LinkedList();
      PartyFacility partyFacilityTemp = PartyFacilityHelper.findByPrimaryKey(searchParam1, searchParam2);
      if(partyFacilityTemp != null) partyFacilityCollection.add(partyFacilityTemp);
    }
    if(partyFacilityCollection != null) partyFacilityArray = partyFacilityCollection.toArray();

    if(partyFacilityArray != null)
    {
      session.setAttribute("CACHE_SEARCH_RESULTS", partyFacilityArray);
      session.setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
    }
  }
//--------------
  int lowIndex = viewIndex*viewSize+1;
  int highIndex = (viewIndex+1)*viewSize;
  int arraySize = 0;
  if(partyFacilityArray!=null) arraySize = partyFacilityArray.length;
  if(arraySize<highIndex) highIndex=arraySize;
  //Debug.logInfo("viewIndex=" + viewIndex + " lowIndex=" + lowIndex + " highIndex=" + highIndex + " arraySize=" + arraySize);
%>
<h3 style=margin:0;>Find PartyFacilitys</h3>
Note: you may use the '%' character as a wildcard, to replace any other letters.
<table cellpadding="2" cellspacing="2" border="0">
  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPartyFacility")%>" style=margin:0;>
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

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">PartyId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPartyFacility")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="PartyId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">FacilityId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPartyFacility")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="FacilityId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">FacilityRoleTypeId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPartyFacility")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="FacilityRoleTypeId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">FacilityId and FacilityRoleTypeId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPartyFacility")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="FacilityIdAndFacilityRoleTypeId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
        <input type="text" name="SEARCH_PARAMETER2" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">Display All: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPartyFacility")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="all">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>
</table>
<b>PartyFacilitys found by:&nbsp; <%=searchType%> : <%=UtilFormatOut.checkNull(searchParam1)%> : <%=UtilFormatOut.checkNull(searchParam2)%> : <%=UtilFormatOut.checkNull(searchParam3)%></b>
<br>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("ViewPartyFacility")%>" class="buttontext">[Create New PartyFacility]</a>
<%}%>
<table border="0" width="100%" cellpadding="2">
<% if(arraySize > 0) { %>
    <tr class="<%=rowClassResultIndex%>">
      <td align="left">
        <b>
        <% if(viewIndex > 0) { %>
          <a href="<%=response.encodeURL(controlPath + "/FindPartyFacility?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL(controlPath + "/FindPartyFacility?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FACILITY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FACILITY_ROLE_TYPE_ID</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(hasDeletePermission){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
<%
 if(partyFacilityArray != null && partyFacilityArray.length > 0)
 {
  int loopIndex;
  //for(loopIndex=partyFacilityArray.length-1; loopIndex>=0 ; loopIndex--)
  for(loopIndex=lowIndex; loopIndex<=highIndex; loopIndex++)
  {
    PartyFacility partyFacility = (PartyFacility)partyFacilityArray[loopIndex-1];
    if(partyFacility != null)
    {
%>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyFacility.getPartyId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyFacility.getFacilityId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyFacility.getFacilityRoleTypeId())%>
        &nbsp;</div>
      </td>
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyFacility?" + "PARTY_FACILITY_PARTY_ID=" + partyFacility.getPartyId() + "&" + "PARTY_FACILITY_FACILITY_ID=" + partyFacility.getFacilityId())%>" class="buttontext">[View]</a>
      </td>
      <%if(hasDeletePermission){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePartyFacility?UPDATE_MODE=DELETE&" + "PARTY_FACILITY_PARTY_ID=" + partyFacility.getPartyId() + "&" + "PARTY_FACILITY_FACILITY_ID=" + partyFacility.getFacilityId() + "&" + curFindString)%>" class="buttontext">[Delete]</a>
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
<td colspan="5">
<h3>No PartyFacilitys Found.</h3>
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
          <a href="<%=response.encodeURL(controlPath + "/FindPartyFacility?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL(controlPath + "/FindPartyFacility?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewPartyFacility")%>" class="buttontext">[Create PartyFacility]</a>
<%}%>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_FACILITY_ADMIN, or PARTY_FACILITY_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
