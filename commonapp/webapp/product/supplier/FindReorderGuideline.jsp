
<%
/**
 *  Title: Reorder Guideline Entity
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
 *@created    Fri Jul 27 01:37:23 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.supplier.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "FindReorderGuideline"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("REORDER_GUIDELINE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("REORDER_GUIDELINE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("REORDER_GUIDELINE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("REORDER_GUIDELINE", "_DELETE", session);%>
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

  Collection reorderGuidelineCollection = null;
  Object[] reorderGuidelineArray = (Object[])session.getAttribute("CACHE_SEARCH_RESULTS");
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
  String reorderGuidelineArrayName = (String)session.getAttribute("CACHE_SEARCH_RESULTS_NAME");
  if(reorderGuidelineArray == null || reorderGuidelineArrayName == null || curFindString.compareTo(reorderGuidelineArrayName) != 0 || viewIndex == 0)
  {
    Debug.logInfo("-=-=-=-=- Current Array not found in session, getting new one...");
    Debug.logInfo("-=-=-=-=- curFindString:" + curFindString + " reorderGuidelineArrayName:" + reorderGuidelineArrayName);

    if(searchType.compareTo("all") == 0) reorderGuidelineCollection = ReorderGuidelineHelper.findAll();

    else if(searchType.compareTo("ProductId") == 0) reorderGuidelineCollection = ReorderGuidelineHelper.findByProductId(searchParam1);

    else if(searchType.compareTo("PartyId") == 0) reorderGuidelineCollection = ReorderGuidelineHelper.findByPartyId(searchParam1);

    else if(searchType.compareTo("RoleTypeId") == 0) reorderGuidelineCollection = ReorderGuidelineHelper.findByRoleTypeId(searchParam1);

    else if(searchType.compareTo("PartyIdAndRoleTypeId") == 0) reorderGuidelineCollection = ReorderGuidelineHelper.findByPartyIdAndRoleTypeId(searchParam1, searchParam2);

    else if(searchType.compareTo("FacilityId") == 0) reorderGuidelineCollection = ReorderGuidelineHelper.findByFacilityId(searchParam1);

    else if(searchType.compareTo("GeoId") == 0) reorderGuidelineCollection = ReorderGuidelineHelper.findByGeoId(searchParam1);

    else if(searchType.compareTo("primaryKey") == 0)
    {
      reorderGuidelineCollection = new LinkedList();
      ReorderGuideline reorderGuidelineTemp = ReorderGuidelineHelper.findByPrimaryKey(searchParam1);
      if(reorderGuidelineTemp != null) reorderGuidelineCollection.add(reorderGuidelineTemp);
    }
    if(reorderGuidelineCollection != null) reorderGuidelineArray = reorderGuidelineCollection.toArray();

    if(reorderGuidelineArray != null)
    {
      session.setAttribute("CACHE_SEARCH_RESULTS", reorderGuidelineArray);
      session.setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
    }
  }
//--------------
  int lowIndex = viewIndex*viewSize+1;
  int highIndex = (viewIndex+1)*viewSize;
  int arraySize = 0;
  if(reorderGuidelineArray!=null) arraySize = reorderGuidelineArray.length;
  if(arraySize<highIndex) highIndex=arraySize;
  //Debug.logInfo("viewIndex=" + viewIndex + " lowIndex=" + lowIndex + " highIndex=" + highIndex + " arraySize=" + arraySize);
%>
<h3 style=margin:0;>Find ReorderGuidelines</h3>
Note: you may use the '%' character as a wildcard, to replace any other letters.
<table cellpadding="2" cellspacing="2" border="0">
  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindReorderGuideline")%>" style=margin:0;>
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

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">ProductId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindReorderGuideline")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="ProductId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">PartyId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindReorderGuideline")%>" style=margin:0;>
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
    <td valign="top">RoleTypeId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindReorderGuideline")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="RoleTypeId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">PartyId and RoleTypeId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindReorderGuideline")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="PartyIdAndRoleTypeId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
        <input type="text" name="SEARCH_PARAMETER2" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">FacilityId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindReorderGuideline")%>" style=margin:0;>
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
    <td valign="top">GeoId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindReorderGuideline")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="GeoId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">Display All: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindReorderGuideline")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="all">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>
</table>
<b>ReorderGuidelines found by:&nbsp; <%=searchType%> : <%=UtilFormatOut.checkNull(searchParam1)%> : <%=UtilFormatOut.checkNull(searchParam2)%> : <%=UtilFormatOut.checkNull(searchParam3)%></b>
<br>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("ViewReorderGuideline")%>" class="buttontext">[Create New ReorderGuideline]</a>
<%}%>
<table border="0" width="100%" cellpadding="2">
<% if(arraySize > 0) { %>
    <tr class="<%=rowClassResultIndex%>">
      <td align="left">
        <b>
        <% if(viewIndex > 0) { %>
          <a href="<%=response.encodeURL(controlPath + "/FindReorderGuideline?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL(controlPath + "/FindReorderGuideline?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>REORDER_GUIDELINE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>ROLE_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FACILITY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>GEO_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>REORDER_QUANTITY</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>REORDER_LEVEL</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(hasDeletePermission){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
<%
 if(reorderGuidelineArray != null && reorderGuidelineArray.length > 0)
 {
  int loopIndex;
  //for(loopIndex=reorderGuidelineArray.length-1; loopIndex>=0 ; loopIndex--)
  for(loopIndex=lowIndex; loopIndex<=highIndex; loopIndex++)
  {
    ReorderGuideline reorderGuideline = (ReorderGuideline)reorderGuidelineArray[loopIndex-1];
    if(reorderGuideline != null)
    {
%>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(reorderGuideline.getReorderGuidelineId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(reorderGuideline.getProductId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(reorderGuideline.getPartyId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(reorderGuideline.getRoleTypeId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(reorderGuideline.getFacilityId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(reorderGuideline.getGeoId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(reorderGuideline != null)
        {
          java.util.Date date = reorderGuideline.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(reorderGuideline != null)
        {
          java.util.Date date = reorderGuideline.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(reorderGuideline.getReorderQuantity())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(reorderGuideline.getReorderLevel())%>
        &nbsp;</div>
      </td>
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewReorderGuideline?" + "REORDER_GUIDELINE_REORDER_GUIDELINE_ID=" + reorderGuideline.getReorderGuidelineId())%>" class="buttontext">[View]</a>
      </td>
      <%if(hasDeletePermission){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateReorderGuideline?UPDATE_MODE=DELETE&" + "REORDER_GUIDELINE_REORDER_GUIDELINE_ID=" + reorderGuideline.getReorderGuidelineId() + "&" + curFindString)%>" class="buttontext">[Delete]</a>
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
<td colspan="12">
<h3>No ReorderGuidelines Found.</h3>
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
          <a href="<%=response.encodeURL(controlPath + "/FindReorderGuideline?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL(controlPath + "/FindReorderGuideline?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewReorderGuideline")%>" class="buttontext">[Create ReorderGuideline]</a>
<%}%>
<%}else{%>
  <h3>You do not have permission to view this page (REORDER_GUIDELINE_ADMIN, or REORDER_GUIDELINE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
