
<%
/**
 *  Title: Price Component Attribute Entity
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
<%pageContext.setAttribute("PageName", "ViewPriceComponentAttribute"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRICE_COMPONENT_ATTRIBUTE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRICE_COMPONENT_ATTRIBUTE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRICE_COMPONENT_ATTRIBUTE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRICE_COMPONENT_ATTRIBUTE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String priceComponentId = request.getParameter("PRICE_COMPONENT_ATTRIBUTE_PRICE_COMPONENT_ID");  
  String name = request.getParameter("PRICE_COMPONENT_ATTRIBUTE_NAME");  


  PriceComponentAttribute priceComponentAttribute = PriceComponentAttributeHelper.findByPrimaryKey(priceComponentId, name);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View PriceComponentAttribute</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit PriceComponentAttribute</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: PriceComponentAttribute with (PRICE_COMPONENT_ID, NAME: <%=priceComponentId%>, <%=name%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindPriceComponentAttribute")%>" class="buttontext">[Find PriceComponentAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentAttribute")%>" class="buttontext">[Create New PriceComponentAttribute]</a>
<%}%>
<%if(priceComponentAttribute != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdatePriceComponentAttribute?UPDATE_MODE=DELETE&" + "PRICE_COMPONENT_ATTRIBUTE_PRICE_COMPONENT_ID=" + priceComponentId + "&" + "PRICE_COMPONENT_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Delete this PriceComponentAttribute]</a>
  <%}%>
<%}%>

<%if(priceComponentAttribute == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(priceComponentAttribute == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified PriceComponentAttribute was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRICE_COMPONENT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentAttribute.getPriceComponentId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentAttribute.getName())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>VALUE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentAttribute.getValue())%>
    </td>
  </tr>

<%} //end if priceComponentAttribute == null %>
</table>
  </div>
<%PriceComponentAttribute priceComponentAttributeSave = priceComponentAttribute;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(priceComponentAttribute == null && (priceComponentId != null || name != null)){%>
    PriceComponentAttribute with (PRICE_COMPONENT_ID, NAME: <%=priceComponentId%>, <%=name%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    priceComponentAttribute = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdatePriceComponentAttribute")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(priceComponentAttribute == null){%>
  <%if(hasCreatePermission){%>
    You may create a PriceComponentAttribute by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRICE_COMPONENT_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRICE_COMPONENT_ATTRIBUTE_PRICE_COMPONENT_ID" value="<%=UtilFormatOut.checkNull(priceComponentId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <input class='editInputBox' type="text" size="60" maxlength="60" name="PRICE_COMPONENT_ATTRIBUTE_NAME" value="<%=UtilFormatOut.checkNull(name)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a PriceComponentAttribute (PRICE_COMPONENT_ATTRIBUTE_ADMIN, or PRICE_COMPONENT_ATTRIBUTE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRICE_COMPONENT_ATTRIBUTE_PRICE_COMPONENT_ID" value="<%=priceComponentId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRICE_COMPONENT_ID</td>
      <td>
        <b><%=priceComponentId%></b> (This cannot be changed without re-creating the priceComponentAttribute.)
      </td>
    </tr>
      <input type="hidden" name="PRICE_COMPONENT_ATTRIBUTE_NAME" value="<%=name%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <b><%=name%></b> (This cannot be changed without re-creating the priceComponentAttribute.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a PriceComponentAttribute (PRICE_COMPONENT_ATTRIBUTE_ADMIN, or PRICE_COMPONENT_ATTRIBUTE_UPDATE needed).
  <%}%>
<%} //end if priceComponentAttribute == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>VALUE</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRICE_COMPONENT_ATTRIBUTE_VALUE" value="<%if(priceComponentAttribute!=null){%><%=UtilFormatOut.checkNull(priceComponentAttribute.getValue())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRICE_COMPONENT_ATTRIBUTE_VALUE"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && priceComponentAttribute == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the priceComponentAttribute for cases when removed to retain passed form values --%>
<%priceComponentAttribute = priceComponentAttributeSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=2;
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
<%if(priceComponentAttribute != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRICE_COMPONENT", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> PriceComponent</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> PriceComponentTypeAttr</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for PriceComponent, type: one --%>
<%if(priceComponentAttribute != null){%>
  <%if(Security.hasEntityPermission("PRICE_COMPONENT", "_VIEW", session)){%>
    <%-- PriceComponent priceComponentRelated = PriceComponentHelper.findByPrimaryKey(priceComponentAttribute.getPriceComponentId()); --%>
    <%PriceComponent priceComponentRelated = priceComponentAttribute.getPriceComponent();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>PriceComponent</b> with (PRICE_COMPONENT_ID: <%=priceComponentAttribute.getPriceComponentId()%>)
    </div>
    <%if(priceComponentAttribute.getPriceComponentId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponent?" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponentAttribute.getPriceComponentId())%>" class="buttontext">[View PriceComponent]</a>      
    <%if(priceComponentRelated == null){%>
      <%if(Security.hasEntityPermission("PRICE_COMPONENT", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponent?" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponentAttribute.getPriceComponentId())%>" class="buttontext">[Create PriceComponent]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(priceComponentRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified PriceComponent was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRICE_COMPONENT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPriceComponentId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRICE_COMPONENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPriceComponentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getPartyTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getProductId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getProductFeatureId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getProductCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>AGREEMENT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getAgreementId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>AGREEMENT_ITEM_SEQ_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getAgreementItemSeqId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GEO_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getGeoId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>SALE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getSaleTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ORDER_VALUE_BREAK_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getOrderValueBreakId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>QUANTITY_BREAK_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getQuantityBreakId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UTILIZATION_UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getUtilizationUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UTILIZATION_QUANTITY</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(priceComponentRelated.getUtilizationQuantity())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>FROM_DATE</b></td>
    <td>
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
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>THRU_DATE</b></td>
    <td>
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
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRICE</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(priceComponentRelated.getPrice())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PERCENT</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(priceComponentRelated.getPercent())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>COMMENT</b></td>
    <td>
      <%=UtilFormatOut.checkNull(priceComponentRelated.getComment())%>
    </td>
  </tr>

    <%} //end if priceComponentRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for PriceComponent, type: one --%>
  

<%-- Start Relation for PriceComponentTypeAttr, type: many --%>
<%if(priceComponentAttribute != null){%>
  <%if(Security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PriceComponentTypeAttrHelper.findByName(priceComponentAttribute.getName())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(priceComponentAttribute.getPriceComponentTypeAttrs());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PriceComponentTypeAttr</b> with (NAME: <%=priceComponentAttribute.getName()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentTypeAttr?" + "PRICE_COMPONENT_TYPE_ATTR_NAME=" + priceComponentAttribute.getName())%>" class="buttontext">[Create PriceComponentTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=Name";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + priceComponentAttribute.getName();%>
    <a href="<%=response.encodeURL(controlPath + "/FindPriceComponentAttribute?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PriceComponentTypeAttr]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRICE_COMPONENT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>NAME</nobr></b></div></td>
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
        PriceComponentTypeAttr priceComponentTypeAttrRelated = (PriceComponentTypeAttr)relatedIterator.next();
        if(priceComponentTypeAttrRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentTypeAttrRelated.getPriceComponentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(priceComponentTypeAttrRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentTypeAttr?" + "PRICE_COMPONENT_TYPE_ATTR_PRICE_COMPONENT_TYPE_ID=" + priceComponentTypeAttrRelated.getPriceComponentTypeId() + "&" + "PRICE_COMPONENT_TYPE_ATTR_NAME=" + priceComponentTypeAttrRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePriceComponentTypeAttr?" + "PRICE_COMPONENT_TYPE_ATTR_PRICE_COMPONENT_TYPE_ID=" + priceComponentTypeAttrRelated.getPriceComponentTypeId() + "&" + "PRICE_COMPONENT_TYPE_ATTR_NAME=" + priceComponentTypeAttrRelated.getName() + "&" + "PRICE_COMPONENT_ATTRIBUTE_PRICE_COMPONENT_ID=" + priceComponentId + "&" + "PRICE_COMPONENT_ATTRIBUTE_NAME=" + name + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No PriceComponentTypeAttrs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PriceComponentTypeAttr, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PRICE_COMPONENT_ATTRIBUTE_ADMIN, or PRICE_COMPONENT_ATTRIBUTE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
