
<%
/**
 *  Title: Price Component Entity
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
 *@created    Fri Jul 27 01:37:16 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.text.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.price.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "FindPriceComponent"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRICE_COMPONENT", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRICE_COMPONENT", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRICE_COMPONENT", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRICE_COMPONENT", "_DELETE", session);%>
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

  Collection priceComponentCollection = null;
  Object[] priceComponentArray = (Object[])session.getAttribute("CACHE_SEARCH_RESULTS");
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
  String priceComponentArrayName = (String)session.getAttribute("CACHE_SEARCH_RESULTS_NAME");
  if(priceComponentArray == null || priceComponentArrayName == null || curFindString.compareTo(priceComponentArrayName) != 0 || viewIndex == 0)
  {
    Debug.logInfo("-=-=-=-=- Current Array not found in session, getting new one...");
    Debug.logInfo("-=-=-=-=- curFindString:" + curFindString + " priceComponentArrayName:" + priceComponentArrayName);

    if(searchType.compareTo("all") == 0) priceComponentCollection = PriceComponentHelper.findAll();

    else if(searchType.compareTo("PriceComponentTypeId") == 0) priceComponentCollection = PriceComponentHelper.findByPriceComponentTypeId(searchParam1);

    else if(searchType.compareTo("PartyId") == 0) priceComponentCollection = PriceComponentHelper.findByPartyId(searchParam1);

    else if(searchType.compareTo("PartyTypeId") == 0) priceComponentCollection = PriceComponentHelper.findByPartyTypeId(searchParam1);

    else if(searchType.compareTo("PartyIdAndPartyTypeId") == 0) priceComponentCollection = PriceComponentHelper.findByPartyIdAndPartyTypeId(searchParam1, searchParam2);

    else if(searchType.compareTo("ProductId") == 0) priceComponentCollection = PriceComponentHelper.findByProductId(searchParam1);

    else if(searchType.compareTo("ProductFeatureId") == 0) priceComponentCollection = PriceComponentHelper.findByProductFeatureId(searchParam1);

    else if(searchType.compareTo("ProductIdAndProductFeatureId") == 0) priceComponentCollection = PriceComponentHelper.findByProductIdAndProductFeatureId(searchParam1, searchParam2);

    else if(searchType.compareTo("ProductCategoryId") == 0) priceComponentCollection = PriceComponentHelper.findByProductCategoryId(searchParam1);

    else if(searchType.compareTo("ProductIdAndProductCategoryId") == 0) priceComponentCollection = PriceComponentHelper.findByProductIdAndProductCategoryId(searchParam1, searchParam2);

    else if(searchType.compareTo("AgreementId") == 0) priceComponentCollection = PriceComponentHelper.findByAgreementId(searchParam1);

    else if(searchType.compareTo("AgreementIdAndAgreementItemSeqId") == 0) priceComponentCollection = PriceComponentHelper.findByAgreementIdAndAgreementItemSeqId(searchParam1, searchParam2);

    else if(searchType.compareTo("UomId") == 0) priceComponentCollection = PriceComponentHelper.findByUomId(searchParam1);

    else if(searchType.compareTo("GeoId") == 0) priceComponentCollection = PriceComponentHelper.findByGeoId(searchParam1);

    else if(searchType.compareTo("SaleTypeId") == 0) priceComponentCollection = PriceComponentHelper.findBySaleTypeId(searchParam1);

    else if(searchType.compareTo("OrderValueBreakId") == 0) priceComponentCollection = PriceComponentHelper.findByOrderValueBreakId(searchParam1);

    else if(searchType.compareTo("QuantityBreakId") == 0) priceComponentCollection = PriceComponentHelper.findByQuantityBreakId(searchParam1);

    else if(searchType.compareTo("primaryKey") == 0)
    {
      priceComponentCollection = new LinkedList();
      PriceComponent priceComponentTemp = PriceComponentHelper.findByPrimaryKey(searchParam1);
      if(priceComponentTemp != null) priceComponentCollection.add(priceComponentTemp);
    }
    if(priceComponentCollection != null) priceComponentArray = priceComponentCollection.toArray();

    if(priceComponentArray != null)
    {
      session.setAttribute("CACHE_SEARCH_RESULTS", priceComponentArray);
      session.setAttribute("CACHE_SEARCH_RESULTS_NAME", curFindString);
    }
  }
//--------------
  int lowIndex = viewIndex*viewSize+1;
  int highIndex = (viewIndex+1)*viewSize;
  int arraySize = 0;
  if(priceComponentArray!=null) arraySize = priceComponentArray.length;
  if(arraySize<highIndex) highIndex=arraySize;
  //Debug.logInfo("viewIndex=" + viewIndex + " lowIndex=" + lowIndex + " highIndex=" + highIndex + " arraySize=" + arraySize);
%>
<h3 style=margin:0;>Find PriceComponents</h3>
Note: you may use the '%' character as a wildcard, to replace any other letters.
<table cellpadding="2" cellspacing="2" border="0">
  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
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
    <td valign="top">PriceComponentTypeId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="PriceComponentTypeId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">PartyId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
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
    <td valign="top">PartyTypeId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="PartyTypeId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">PartyId and PartyTypeId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="PartyIdAndPartyTypeId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
        <input type="text" name="SEARCH_PARAMETER2" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">ProductId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
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
    <td valign="top">ProductFeatureId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="ProductFeatureId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">ProductId and ProductFeatureId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="ProductIdAndProductFeatureId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
        <input type="text" name="SEARCH_PARAMETER2" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">ProductCategoryId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="ProductCategoryId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">ProductId and ProductCategoryId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="ProductIdAndProductCategoryId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
        <input type="text" name="SEARCH_PARAMETER2" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">AgreementId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="AgreementId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">AgreementId and AgreementItemSeqId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="AgreementIdAndAgreementItemSeqId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
        <input type="text" name="SEARCH_PARAMETER2" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">UomId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
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
    <td valign="top">GeoId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
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
    <td valign="top">SaleTypeId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="SaleTypeId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">OrderValueBreakId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="OrderValueBreakId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">QuantityBreakId: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="QuantityBreakId">
      
        <input type="text" name="SEARCH_PARAMETER1" value="" size="20">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>

  <%rowClassTop=(rowClassTop==rowClassTop1?rowClassTop2:rowClassTop1);%><tr class="<%=rowClassTop%>">
    <td valign="top">Display All: </td>
    <form method="post" action="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" style=margin:0;>
      <td valign="top">
        <input type="hidden" name="SEARCH_TYPE" value="all">
      </td>
      <td valign="top">
        <input type="submit" value="Find">
      </td>
    </form>
  </tr>
</table>
<b>PriceComponents found by:&nbsp; <%=searchType%> : <%=UtilFormatOut.checkNull(searchParam1)%> : <%=UtilFormatOut.checkNull(searchParam2)%> : <%=UtilFormatOut.checkNull(searchParam3)%></b>
<br>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("ViewPriceComponent")%>" class="buttontext">[Create New PriceComponent]</a>
<%}%>
<table border="0" width="100%" cellpadding="2">
<% if(arraySize > 0) { %>
    <tr class="<%=rowClassResultIndex%>">
      <td align="left">
        <b>
        <% if(viewIndex > 0) { %>
          <a href="<%=response.encodeURL(controlPath + "/FindPriceComponent?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL(controlPath + "/FindPriceComponent?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRICE_COMPONENT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRICE_COMPONENT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_CATEGORY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>AGREEMENT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>AGREEMENT_ITEM_SEQ_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UOM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>GEO_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>SALE_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>ORDER_VALUE_BREAK_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>QUANTITY_BREAK_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UTILIZATION_UOM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UTILIZATION_QUANTITY</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRICE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PERCENT</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>COMMENT</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(hasDeletePermission){%>
        <td>&nbsp;</td>
      <%}%>
    </tr>
<%
 if(priceComponentArray != null && priceComponentArray.length > 0)
 {
  int loopIndex;
  //for(loopIndex=priceComponentArray.length-1; loopIndex>=0 ; loopIndex--)
  for(loopIndex=lowIndex; loopIndex<=highIndex; loopIndex++)
  {
    PriceComponent priceComponent = (PriceComponent)priceComponentArray[loopIndex-1];
    if(priceComponent != null)
    {
%>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getPriceComponentId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getPriceComponentTypeId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getPartyId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getPartyTypeId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getProductId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getProductFeatureId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getProductCategoryId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getAgreementId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getAgreementItemSeqId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getUomId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getGeoId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getSaleTypeId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getOrderValueBreakId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getQuantityBreakId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getUtilizationUomId())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(priceComponent.getUtilizationQuantity())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(priceComponent != null)
        {
          java.util.Date date = priceComponent.getFromDate();
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
        if(priceComponent != null)
        {
          java.util.Date date = priceComponent.getThruDate();
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
      <%=UtilFormatOut.formatQuantity(priceComponent.getPrice())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(priceComponent.getPercent())%>
        &nbsp;</div>
      </td>
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponent.getComment())%>
        &nbsp;</div>
      </td>
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponent?" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponent.getPriceComponentId())%>" class="buttontext">[View]</a>
      </td>
      <%if(hasDeletePermission){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePriceComponent?UPDATE_MODE=DELETE&" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponent.getPriceComponentId() + "&" + curFindString)%>" class="buttontext">[Delete]</a>
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
<td colspan="23">
<h3>No PriceComponents Found.</h3>
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
          <a href="<%=response.encodeURL(controlPath + "/FindPriceComponent?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1))%>" class="buttontext">[Previous]</a> |
        <% } %>
        <% if(arraySize > 0) { %>
          <%=lowIndex%> - <%=highIndex%> of <%=arraySize%>
        <% } %>
        <% if(arraySize>highIndex) { %>
          | <a href="<%=response.encodeURL(controlPath + "/FindPriceComponent?" + curFindString + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1))%>" class="buttontext">[Next]</a>
        <% } %>
        </b>
      </td>
    </tr>
<% } %>
</table>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponent")%>" class="buttontext">[Create PriceComponent]</a>
<%}%>
<%}else{%>
  <h3>You do not have permission to view this page (PRICE_COMPONENT_ADMIN, or PRICE_COMPONENT_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
