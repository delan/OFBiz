
<%
/**
 *  Title: Product Feature Entity
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
 *@created    Fri Jul 27 01:37:12 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.feature.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "FindProductFeature"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_FEATURE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_FEATURE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_FEATURE", "_DELETE", session);%>
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

  Collection productFeatureCollection = null;
  Object[] productFeatureArray = (Object[])session.getAttribute("CACHE_SEARCH_RESULTS");
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
  String productFeatureArrayName = (String)session.getAttribute("CACHE_SEARCH_RESULTS_NAME");
  if(productFeatureArray == null || productFeatureArrayName == null || curFindString.compareTo(productFeatureArrayName) != 0 || viewIndex == 0)
  {
    Debug.logInfo("-=-=-=-=- Current Array not found in session, getting new one...");
    Debug.logInfo("-=-=-=-=- curFindString:" + curFindString + " productFeatureArrayName:" + productFeatureArrayName);

    if(searchType.compareTo("all") == 0) productFeatureCollection = ProductFeatureHelper.findAll();

    else if(searchType.compareTo("ProductFeatureTypeId") == 0) productFeatureCollection = ProductFeatureHelper.findByProductFeatureTypeId(searchParam1);

    else if(searchType.compareTo("ProductFeatureCategoryId") == 0) productFeatureCollection = ProductFeatureHelper.findByProductFeatureCategoryId(searchParam1);

    else if(searchType.compareTo("UomId") == 0) productFeatureCollection = ProductFeatureHelper.findByUomId(searchParam1);

    else if(searchType.compareTo("primaryKey") == 0)
    {
      productFeatureCollection = new LinkedList();
      ProductFeature productFeatureTemp = ProductFeatureHelper.findByPrimaryKey(searchParam1);
      if(productFeatureTemp != null) productFeatureCollection.add(productFeatureTemp);
    }
    if(productFeatureCollection != null) productFeatureArray = productFeatureCollection.toArray();

    if(productFeatureArray != null)
    {
      session.setAttribute("CACHE_SEARCH_RESULTS", productFeatureArray);
      session.setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
    }
  }
//--------------
  int lowIndex = viewIndex*viewSize+1;
  int highIndex = (viewIndex+1)*viewSize;
  int arraySize = 0;
  if(productFeatureArray!=null) arraySize = productFeatureArray.length;
  if(arraySize<highIndex) highIndex=arraySize;
  //Debug.logInfo("viewIndex=" + viewIndex + " lowIndex=" + lowIndex + " highIndex=" + highIndex + " arraySize=" + arraySize);
%>
<h3 style=margin:0;>Find ProductFeatures</h3>
Note: you may use the '%' character as a wildcard, to replace any other letters.
<table cellpadding="2" cellspacing="2" border="0">
  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindProductFeature")%>" style=margin:0;>
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
    <td valign="top">ProductFeatureTypeId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindProductFeature")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="ProductFeatureTypeId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">ProductFeatureCategoryId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindProductFeature")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="ProductFeatureCategoryId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">UomId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindProductFeature")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="UomId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">Display All: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindProductFeature")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="all">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>
</table>
<b>ProductFeatures found by:&nbsp; <%=searchType%> : <%=UtilFormatOut.checkNull(searchParam1)%> : <%=UtilFormatOut.checkNull(searchParam2)%> : <%=UtilFormatOut.checkNull(searchParam3)%></b>
<br>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("ViewProductFeature")%>" class="buttontext">[Create New ProductFeature]</a>
<%}%>
<table border="0" width="100%" cellpadding="2">
<% if(arraySize > 0) { %>
    <tr class="<%=rowClassResultIndex%>">
      <td align="left">
        <b>
        <% if(viewIndex > 0) { %>
          <a href="<%=response.encodeURL(controlPath + "/FindProductFeature?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL(controlPath + "/FindProductFeature?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_CATEGORY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>DESCRIPTION</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UOM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>NUMBER_SPECIFIED</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(hasDeletePermission){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
<%
 if(productFeatureArray != null && productFeatureArray.length > 0)
 {
  int loopIndex;
  //for(loopIndex=productFeatureArray.length-1; loopIndex>=0 ; loopIndex--)
  for(loopIndex=lowIndex; loopIndex<=highIndex; loopIndex++)
  {
    ProductFeature productFeature = (ProductFeature)productFeatureArray[loopIndex-1];
    if(productFeature != null)
    {
%>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeature.getProductFeatureId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeature.getProductFeatureTypeId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeature.getProductFeatureCategoryId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeature.getDescription())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeature.getUomId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(productFeature.getNumberSpecified())%>
        &nbsp;</div>
      </td>
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeature.getProductFeatureId())%>" class="buttontext">[View]</a>
      </td>
      <%if(hasDeletePermission){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeature?UPDATE_MODE=DELETE&" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeature.getProductFeatureId() + "&" + curFindString)%>" class="buttontext">[Delete]</a>
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
<td colspan="8">
<h3>No ProductFeatures Found.</h3>
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
          <a href="<%=response.encodeURL(controlPath + "/FindProductFeature?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL(controlPath + "/FindProductFeature?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature")%>" class="buttontext">[Create ProductFeature]</a>
<%}%>
<%}else{%>
  <h3>You do not have permission to view this page (PRODUCT_FEATURE_ADMIN, or PRODUCT_FEATURE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
