
<%
/**
 *  Title: Product Type Attribute Entity
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
 *@created    Fri Jul 27 01:37:09 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.product.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewProductTypeAttr"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_TYPE_ATTR", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_TYPE_ATTR", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_TYPE_ATTR", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_TYPE_ATTR", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productTypeId = request.getParameter("PRODUCT_TYPE_ATTR_PRODUCT_TYPE_ID");  
  String name = request.getParameter("PRODUCT_TYPE_ATTR_NAME");  


  ProductTypeAttr productTypeAttr = ProductTypeAttrHelper.findByPrimaryKey(productTypeId, name);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ProductTypeAttr</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ProductTypeAttr</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ProductTypeAttr with (PRODUCT_TYPE_ID, NAME: <%=productTypeId%>, <%=name%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProductTypeAttr")%>" class="buttontext">[Find ProductTypeAttr]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductTypeAttr")%>" class="buttontext">[Create New ProductTypeAttr]</a>
<%}%>
<%if(productTypeAttr != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProductTypeAttr?UPDATE_MODE=DELETE&" + "PRODUCT_TYPE_ATTR_PRODUCT_TYPE_ID=" + productTypeId + "&" + "PRODUCT_TYPE_ATTR_NAME=" + name)%>" class="buttontext">[Delete this ProductTypeAttr]</a>
  <%}%>
<%}%>

<%if(productTypeAttr == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(productTypeAttr == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ProductTypeAttr was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productTypeAttr.getProductTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NAME</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productTypeAttr.getName())%>
    </td>
  </tr>

<%} //end if productTypeAttr == null %>
</table>
  </div>
<%ProductTypeAttr productTypeAttrSave = productTypeAttr;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(productTypeAttr == null && (productTypeId != null || name != null)){%>
    ProductTypeAttr with (PRODUCT_TYPE_ID, NAME: <%=productTypeId%>, <%=name%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    productTypeAttr = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProductTypeAttr")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(productTypeAttr == null){%>
  <%if(hasCreatePermission){%>
    You may create a ProductTypeAttr by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_TYPE_ATTR_PRODUCT_TYPE_ID" value="<%=UtilFormatOut.checkNull(productTypeId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <input class='editInputBox' type="text" size="60" maxlength="60" name="PRODUCT_TYPE_ATTR_NAME" value="<%=UtilFormatOut.checkNull(name)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ProductTypeAttr (PRODUCT_TYPE_ATTR_ADMIN, or PRODUCT_TYPE_ATTR_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_TYPE_ATTR_PRODUCT_TYPE_ID" value="<%=productTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_TYPE_ID</td>
      <td>
        <b><%=productTypeId%></b> (This cannot be changed without re-creating the productTypeAttr.)
      </td>
    </tr>
      <input type="hidden" name="PRODUCT_TYPE_ATTR_NAME" value="<%=name%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>NAME</td>
      <td>
        <b><%=name%></b> (This cannot be changed without re-creating the productTypeAttr.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ProductTypeAttr (PRODUCT_TYPE_ATTR_ADMIN, or PRODUCT_TYPE_ATTR_UPDATE needed).
  <%}%>
<%} //end if productTypeAttr == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td colspan="2"><input type="submit" name="Update" value="Update"></td>
  </tr>
<%}%>
</table>
</form>
  </div>
<%}%>
</div>
<%if((hasUpdatePermission || hasCreatePermission) && productTypeAttr == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the productTypeAttr for cases when removed to retain passed form values --%>
<%productTypeAttr = productTypeAttrSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=3;
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
<%if(productTypeAttr != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> ProductType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> ProductAttribute</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CLASS", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> ProductClass</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductType, type: one --%>
<%if(productTypeAttr != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_TYPE", "_VIEW", session)){%>
    <%-- ProductType productTypeRelated = ProductTypeHelper.findByPrimaryKey(productTypeAttr.getProductTypeId()); --%>
    <%ProductType productTypeRelated = productTypeAttr.getProductType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ProductType</b> with (PRODUCT_TYPE_ID: <%=productTypeAttr.getProductTypeId()%>)
    </div>
    <%if(productTypeAttr.getProductTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductType?" + "PRODUCT_TYPE_PRODUCT_TYPE_ID=" + productTypeAttr.getProductTypeId())%>" class="buttontext">[View ProductType]</a>      
    <%if(productTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductType?" + "PRODUCT_TYPE_PRODUCT_TYPE_ID=" + productTypeAttr.getProductTypeId())%>" class="buttontext">[Create ProductType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productTypeRelated.getProductTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductType, type: one --%>
  

<%-- Start Relation for ProductAttribute, type: many --%>
<%if(productTypeAttr != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductAttributeHelper.findByName(productTypeAttr.getName())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productTypeAttr.getProductAttributes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductAttribute</b> with (NAME: <%=productTypeAttr.getName()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductAttribute?" + "PRODUCT_ATTRIBUTE_NAME=" + productTypeAttr.getName())%>" class="buttontext">[Create ProductAttribute]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=Name";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productTypeAttr.getName();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductTypeAttr?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductAttribute]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>NAME</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>VALUE</nobr></b></div></td>
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
        ProductAttribute productAttributeRelated = (ProductAttribute)relatedIterator.next();
        if(productAttributeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAttributeRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAttributeRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAttributeRelated.getValue())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductAttribute?" + "PRODUCT_ATTRIBUTE_PRODUCT_ID=" + productAttributeRelated.getProductId() + "&" + "PRODUCT_ATTRIBUTE_NAME=" + productAttributeRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductAttribute?" + "PRODUCT_ATTRIBUTE_PRODUCT_ID=" + productAttributeRelated.getProductId() + "&" + "PRODUCT_ATTRIBUTE_NAME=" + productAttributeRelated.getName() + "&" + "PRODUCT_TYPE_ATTR_PRODUCT_TYPE_ID=" + productTypeId + "&" + "PRODUCT_TYPE_ATTR_NAME=" + name + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No ProductAttributes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductAttribute, type: many --%>
  

<%-- Start Relation for ProductClass, type: many --%>
<%if(productTypeAttr != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CLASS", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductClassHelper.findByProductTypeId(productTypeAttr.getProductTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productTypeAttr.getProductClasss());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductClass</b> with (PRODUCT_TYPE_ID: <%=productTypeAttr.getProductTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_CLASS", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_CLASS", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_CLASS", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductClass?" + "PRODUCT_CLASS_PRODUCT_TYPE_ID=" + productTypeAttr.getProductTypeId())%>" class="buttontext">[Create ProductClass]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productTypeAttr.getProductTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductTypeAttr?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductClass]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
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
        ProductClass productClassRelated = (ProductClass)relatedIterator.next();
        if(productClassRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productClassRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productClassRelated.getProductTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(productClassRelated != null)
        {
          java.util.Date date = productClassRelated.getFromDate();
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
        if(productClassRelated != null)
        {
          java.util.Date date = productClassRelated.getThruDate();
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
        <a href="<%=response.encodeURL(controlPath + "/ViewProductClass?" + "PRODUCT_CLASS_PRODUCT_ID=" + productClassRelated.getProductId() + "&" + "PRODUCT_CLASS_PRODUCT_TYPE_ID=" + productClassRelated.getProductTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductClass?" + "PRODUCT_CLASS_PRODUCT_ID=" + productClassRelated.getProductId() + "&" + "PRODUCT_CLASS_PRODUCT_TYPE_ID=" + productClassRelated.getProductTypeId() + "&" + "PRODUCT_TYPE_ATTR_PRODUCT_TYPE_ID=" + productTypeId + "&" + "PRODUCT_TYPE_ATTR_NAME=" + name + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No ProductClasss Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductClass, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PRODUCT_TYPE_ATTR_ADMIN, or PRODUCT_TYPE_ATTR_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
