
<%
/**
 *  Title: Product Type Entity
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
<%pageContext.setAttribute("PageName", "ViewProductType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productTypeId = request.getParameter("PRODUCT_TYPE_PRODUCT_TYPE_ID");  


  ProductType productType = ProductTypeHelper.findByPrimaryKey(productTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ProductType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ProductType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ProductType with (PRODUCT_TYPE_ID: <%=productTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProductType")%>" class="buttontext">[Find ProductType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductType")%>" class="buttontext">[Create New ProductType]</a>
<%}%>
<%if(productType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProductType?UPDATE_MODE=DELETE&" + "PRODUCT_TYPE_PRODUCT_TYPE_ID=" + productTypeId)%>" class="buttontext">[Delete this ProductType]</a>
  <%}%>
<%}%>

<%if(productType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(productType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ProductType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productType.getProductTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productType.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productType.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productType.getDescription())%>
    </td>
  </tr>

<%} //end if productType == null %>
</table>
  </div>
<%ProductType productTypeSave = productType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(productType == null && (productTypeId != null)){%>
    ProductType with (PRODUCT_TYPE_ID: <%=productTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    productType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProductType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(productType == null){%>
  <%if(hasCreatePermission){%>
    You may create a ProductType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_TYPE_PRODUCT_TYPE_ID" value="<%=UtilFormatOut.checkNull(productTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ProductType (PRODUCT_TYPE_ADMIN, or PRODUCT_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_TYPE_PRODUCT_TYPE_ID" value="<%=productTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_TYPE_ID</td>
      <td>
        <b><%=productTypeId%></b> (This cannot be changed without re-creating the productType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ProductType (PRODUCT_TYPE_ADMIN, or PRODUCT_TYPE_UPDATE needed).
  <%}%>
<%} //end if productType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_TYPE_PARENT_TYPE_ID" value="<%if(productType!=null){%><%=UtilFormatOut.checkNull(productType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_TYPE_PARENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>HAS_TABLE</td>
    <td>
      <input class='editInputBox' type="text" size="1" maxlength="1" name="PRODUCT_TYPE_HAS_TABLE" value="<%if(productType!=null){%><%=UtilFormatOut.checkNull(productType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_TYPE_HAS_TABLE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRODUCT_TYPE_DESCRIPTION" value="<%if(productType!=null){%><%=UtilFormatOut.checkNull(productType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && productType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the productType for cases when removed to retain passed form values --%>
<%productType = productTypeSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=4;
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
<%if(productType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Parent ProductType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Child ProductType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> ProductTypeAttr</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CLASS", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> ProductClass</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductType, type: one --%>
<%if(productType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_TYPE", "_VIEW", session)){%>
    <%-- ProductType productTypeRelated = ProductTypeHelper.findByPrimaryKey(productType.getParentTypeId()); --%>
    <%ProductType productTypeRelated = productType.getParentProductType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>ProductType</b> with (PRODUCT_TYPE_ID: <%=productType.getParentTypeId()%>)
    </div>
    <%if(productType.getParentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductType?" + "PRODUCT_TYPE_PRODUCT_TYPE_ID=" + productType.getParentTypeId())%>" class="buttontext">[View ProductType]</a>      
    <%if(productTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductType?" + "PRODUCT_TYPE_PRODUCT_TYPE_ID=" + productType.getParentTypeId())%>" class="buttontext">[Create ProductType]</a>
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
  

<%-- Start Relation for ProductType, type: many --%>
<%if(productType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_TYPE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductTypeHelper.findByParentTypeId(productType.getProductTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productType.getChildProductTypes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Child</b> Related Entities: <b>ProductType</b> with (PARENT_TYPE_ID: <%=productType.getProductTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductType?" + "PRODUCT_TYPE_PARENT_TYPE_ID=" + productType.getProductTypeId())%>" class="buttontext">[Create ProductType]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productType.getProductTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductType]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARENT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>HAS_TABLE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>DESCRIPTION</nobr></b></div></td>
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
        ProductType productTypeRelated = (ProductType)relatedIterator.next();
        if(productTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productTypeRelated.getProductTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productTypeRelated.getParentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productTypeRelated.getHasTable())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productTypeRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductType?" + "PRODUCT_TYPE_PRODUCT_TYPE_ID=" + productTypeRelated.getProductTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductType?" + "PRODUCT_TYPE_PRODUCT_TYPE_ID=" + productTypeRelated.getProductTypeId() + "&" + "PRODUCT_TYPE_PRODUCT_TYPE_ID=" + productTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No ProductTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductType, type: many --%>
  

<%-- Start Relation for ProductTypeAttr, type: many --%>
<%if(productType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductTypeAttrHelper.findByProductTypeId(productType.getProductTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productType.getProductTypeAttrs());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductTypeAttr</b> with (PRODUCT_TYPE_ID: <%=productType.getProductTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_TYPE_ATTR", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_TYPE_ATTR", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_TYPE_ATTR", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductTypeAttr?" + "PRODUCT_TYPE_ATTR_PRODUCT_TYPE_ID=" + productType.getProductTypeId())%>" class="buttontext">[Create ProductTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productType.getProductTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductTypeAttr]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_TYPE_ID</nobr></b></div></td>
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
        ProductTypeAttr productTypeAttrRelated = (ProductTypeAttr)relatedIterator.next();
        if(productTypeAttrRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productTypeAttrRelated.getProductTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productTypeAttrRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductTypeAttr?" + "PRODUCT_TYPE_ATTR_PRODUCT_TYPE_ID=" + productTypeAttrRelated.getProductTypeId() + "&" + "PRODUCT_TYPE_ATTR_NAME=" + productTypeAttrRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductTypeAttr?" + "PRODUCT_TYPE_ATTR_PRODUCT_TYPE_ID=" + productTypeAttrRelated.getProductTypeId() + "&" + "PRODUCT_TYPE_ATTR_NAME=" + productTypeAttrRelated.getName() + "&" + "PRODUCT_TYPE_PRODUCT_TYPE_ID=" + productTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No ProductTypeAttrs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductTypeAttr, type: many --%>
  

<%-- Start Relation for ProductClass, type: many --%>
<%if(productType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CLASS", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductClassHelper.findByProductTypeId(productType.getProductTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productType.getProductClasss());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductClass</b> with (PRODUCT_TYPE_ID: <%=productType.getProductTypeId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewProductClass?" + "PRODUCT_CLASS_PRODUCT_TYPE_ID=" + productType.getProductTypeId())%>" class="buttontext">[Create ProductClass]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productType.getProductTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductClass]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductClass?" + "PRODUCT_CLASS_PRODUCT_ID=" + productClassRelated.getProductId() + "&" + "PRODUCT_CLASS_PRODUCT_TYPE_ID=" + productClassRelated.getProductTypeId() + "&" + "PRODUCT_TYPE_PRODUCT_TYPE_ID=" + productTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  <h3>You do not have permission to view this page (PRODUCT_TYPE_ADMIN, or PRODUCT_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
