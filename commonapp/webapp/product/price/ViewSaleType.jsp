
<%
/**
 *  Title: Sale Type Entity
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
 *@created    Fri Jul 27 01:37:18 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.price.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewSaleType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("SALE_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("SALE_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SALE_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SALE_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String saleTypeId = request.getParameter("SALE_TYPE_SALE_TYPE_ID");  


  SaleType saleType = SaleTypeHelper.findByPrimaryKey(saleTypeId);
%>

<br>
<SCRIPT language='JavaScript'>  
function ShowViewTab(lname) 
{
    document.all.viewtab.className = (lname == 'view') ? 'ontab' : 'offtab';
    document.all.viewlnk.className = (lname == 'view') ? 'onlnk' : 'offlnk';
    document.all.viewarea.style.visibility = (lname == 'view') ? 'visible' : 'hidden';

    document.all.edittab.className = (lname == 'edit') ? 'ontab' : 'offtab';
    document.all.editlnk.className = (lname == 'edit') ? 'onlnk' : 'offlnk';
    document.all.editarea.style.visibility = (lname == 'edit') ? 'visible' : 'hidden';
}
</SCRIPT>
<table cellpadding='0' cellspacing='0'><tr>  
  <td id=viewtab class=ontab>
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View SaleType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit SaleType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: SaleType with (SALE_TYPE_ID: <%=saleTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindSaleType")%>" class="buttontext">[Find SaleType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewSaleType")%>" class="buttontext">[Create New SaleType]</a>
<%}%>
<%if(saleType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateSaleType?UPDATE_MODE=DELETE&" + "SALE_TYPE_SALE_TYPE_ID=" + saleTypeId)%>" class="buttontext">[Delete this SaleType]</a>
  <%}%>
<%}%>

<%if(saleType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(saleType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified SaleType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SALE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(saleType.getSaleTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(saleType.getDescription())%>
    </td>
  </tr>

<%} //end if saleType == null %>
</table>
  </div>
<%SaleType saleTypeSave = saleType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(saleType == null && (saleTypeId != null)){%>
    SaleType with (SALE_TYPE_ID: <%=saleTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    saleType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateSaleType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(saleType == null){%>
  <%if(hasCreatePermission){%>
    You may create a SaleType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>SALE_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="SALE_TYPE_SALE_TYPE_ID" value="<%=UtilFormatOut.checkNull(saleTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a SaleType (SALE_TYPE_ADMIN, or SALE_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="SALE_TYPE_SALE_TYPE_ID" value="<%=saleTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>SALE_TYPE_ID</td>
      <td>
        <b><%=saleTypeId%></b> (This cannot be changed without re-creating the saleType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a SaleType (SALE_TYPE_ADMIN, or SALE_TYPE_UPDATE needed).
  <%}%>
<%} //end if saleType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="SALE_TYPE_DESCRIPTION" value="<%if(saleType!=null){%><%=UtilFormatOut.checkNull(saleType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("SALE_TYPE_DESCRIPTION"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>
  </div>
<%}%>
</div>
<%if((hasUpdatePermission || hasCreatePermission) && saleType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the saleType for cases when removed to retain passed form values --%>
<%saleType = saleTypeSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=1;
function ShowTab(lname) 
{
  for(inc=1; inc <= numTabs; inc++)
  {
    document.all['tab' + inc].className = (lname == 'tab' + inc) ? 'ontab' : 'offtab';
    document.all['lnk' + inc].className = (lname == 'tab' + inc) ? 'onlnk' : 'offlnk';
    document.all['area' + inc].style.visibility = (lname == 'tab' + inc) ? 'visible' : 'hidden';
  }
}
</SCRIPT>
<%if(saleType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRICE_COMPONENT", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> PriceComponent</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for PriceComponent, type: many --%>
<%if(saleType != null){%>
  <%if(Security.hasEntityPermission("PRICE_COMPONENT", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PriceComponentHelper.findBySaleTypeId(saleType.getSaleTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(saleType.getPriceComponents());%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PriceComponent</b> with (SALE_TYPE_ID: <%=saleType.getSaleTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRICE_COMPONENT", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRICE_COMPONENT", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRICE_COMPONENT", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponent?" + "PRICE_COMPONENT_SALE_TYPE_ID=" + saleType.getSaleTypeId())%>" class="buttontext">[Create PriceComponent]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=SaleTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + saleType.getSaleTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindSaleType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PriceComponent]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
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
        PriceComponent priceComponentRelated = (PriceComponent)relatedIterator.next();
        if(priceComponentRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPriceComponentId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPriceComponentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPartyId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPartyTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getProductFeatureId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getProductCategoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getAgreementId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getAgreementItemSeqId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getUomId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getGeoId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getSaleTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getOrderValueBreakId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getQuantityBreakId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getUtilizationUomId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(priceComponentRelated.getUtilizationQuantity())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(priceComponentRelated != null)
        {
          java.util.Date date = priceComponentRelated.getFromDate();
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
        if(priceComponentRelated != null)
        {
          java.util.Date date = priceComponentRelated.getThruDate();
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
      <%=UtilFormatOut.formatQuantity(priceComponentRelated.getPrice())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(priceComponentRelated.getPercent())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentRelated.getComment())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponent?" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponentRelated.getPriceComponentId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePriceComponent?" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponentRelated.getPriceComponentId() + "&" + "SALE_TYPE_SALE_TYPE_ID=" + saleTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="23">
<h3>No PriceComponents Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PriceComponent, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (SALE_TYPE_ADMIN, or SALE_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
