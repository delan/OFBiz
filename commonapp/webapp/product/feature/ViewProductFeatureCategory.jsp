
<%
/**
 *  Title: Product Feature Category Entity
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
 *@created    Fri Jul 27 01:37:13 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.feature.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewProductFeatureCategory"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productFeatureCategoryId = request.getParameter("PRODUCT_FEATURE_CATEGORY_PRODUCT_FEATURE_CATEGORY_ID");  


  ProductFeatureCategory productFeatureCategory = ProductFeatureCategoryHelper.findByPrimaryKey(productFeatureCategoryId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ProductFeatureCategory</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ProductFeatureCategory</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ProductFeatureCategory with (PRODUCT_FEATURE_CATEGORY_ID: <%=productFeatureCategoryId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProductFeatureCategory")%>" class="buttontext">[Find ProductFeatureCategory]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureCategory")%>" class="buttontext">[Create New ProductFeatureCategory]</a>
<%}%>
<%if(productFeatureCategory != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureCategory?UPDATE_MODE=DELETE&" + "PRODUCT_FEATURE_CATEGORY_PRODUCT_FEATURE_CATEGORY_ID=" + productFeatureCategoryId)%>" class="buttontext">[Delete this ProductFeatureCategory]</a>
  <%}%>
<%}%>

<%if(productFeatureCategory == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(productFeatureCategory == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ProductFeatureCategory was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureCategory.getProductFeatureCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureCategory.getParentCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureCategory.getDescription())%>
    </td>
  </tr>

<%} //end if productFeatureCategory == null %>
</table>
  </div>
<%ProductFeatureCategory productFeatureCategorySave = productFeatureCategory;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(productFeatureCategory == null && (productFeatureCategoryId != null)){%>
    ProductFeatureCategory with (PRODUCT_FEATURE_CATEGORY_ID: <%=productFeatureCategoryId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    productFeatureCategory = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProductFeatureCategory")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(productFeatureCategory == null){%>
  <%if(hasCreatePermission){%>
    You may create a ProductFeatureCategory by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_CATEGORY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_CATEGORY_PRODUCT_FEATURE_CATEGORY_ID" value="<%=UtilFormatOut.checkNull(productFeatureCategoryId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ProductFeatureCategory (PRODUCT_FEATURE_CATEGORY_ADMIN, or PRODUCT_FEATURE_CATEGORY_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_FEATURE_CATEGORY_PRODUCT_FEATURE_CATEGORY_ID" value="<%=productFeatureCategoryId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_CATEGORY_ID</td>
      <td>
        <b><%=productFeatureCategoryId%></b> (This cannot be changed without re-creating the productFeatureCategory.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ProductFeatureCategory (PRODUCT_FEATURE_CATEGORY_ADMIN, or PRODUCT_FEATURE_CATEGORY_UPDATE needed).
  <%}%>
<%} //end if productFeatureCategory == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_CATEGORY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_CATEGORY_PARENT_CATEGORY_ID" value="<%if(productFeatureCategory!=null){%><%=UtilFormatOut.checkNull(productFeatureCategory.getParentCategoryId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_CATEGORY_PARENT_CATEGORY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRODUCT_FEATURE_CATEGORY_DESCRIPTION" value="<%if(productFeatureCategory!=null){%><%=UtilFormatOut.checkNull(productFeatureCategory.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_CATEGORY_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && productFeatureCategory == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the productFeatureCategory for cases when removed to retain passed form values --%>
<%productFeatureCategory = productFeatureCategorySave;%>

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
<%if(productFeatureCategory != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> ProductFeatureCategory</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> ProductFeature</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductFeatureCategory, type: one --%>
<%if(productFeatureCategory != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_VIEW", session)){%>
    <%-- ProductFeatureCategory productFeatureCategoryRelated = ProductFeatureCategoryHelper.findByPrimaryKey(productFeatureCategory.getParentCategoryId()); --%>
    <%ProductFeatureCategory productFeatureCategoryRelated = productFeatureCategory.getProductFeatureCategory();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ProductFeatureCategory</b> with (PRODUCT_FEATURE_CATEGORY_ID: <%=productFeatureCategory.getParentCategoryId()%>)
    </div>
    <%if(productFeatureCategory.getParentCategoryId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureCategory?" + "PRODUCT_FEATURE_CATEGORY_PRODUCT_FEATURE_CATEGORY_ID=" + productFeatureCategory.getParentCategoryId())%>" class="buttontext">[View ProductFeatureCategory]</a>      
    <%if(productFeatureCategoryRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureCategory?" + "PRODUCT_FEATURE_CATEGORY_PRODUCT_FEATURE_CATEGORY_ID=" + productFeatureCategory.getParentCategoryId())%>" class="buttontext">[Create ProductFeatureCategory]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productFeatureCategoryRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductFeatureCategory was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureCategoryRelated.getProductFeatureCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureCategoryRelated.getParentCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureCategoryRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productFeatureCategoryRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeatureCategory, type: one --%>
  

<%-- Start Relation for ProductFeature, type: many --%>
<%if(productFeatureCategory != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductFeatureHelper.findByProductFeatureCategoryId(productFeatureCategory.getProductFeatureCategoryId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productFeatureCategory.getProductFeatures());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductFeature</b> with (PRODUCT_FEATURE_CATEGORY_ID: <%=productFeatureCategory.getProductFeatureCategoryId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_FEATURE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_FEATURE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_FEATURE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_CATEGORY_ID=" + productFeatureCategory.getProductFeatureCategoryId())%>" class="buttontext">[Create ProductFeature]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductFeatureCategoryId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productFeatureCategory.getProductFeatureCategoryId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductFeatureCategory?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductFeature]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_CATEGORY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>DESCRIPTION</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>UOM_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>NUMBER_SPECIFIED</nobr></b></div></td>
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
        ProductFeature productFeatureRelated = (ProductFeature)relatedIterator.next();
        if(productFeatureRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureCategoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureRelated.getUomId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(productFeatureRelated.getNumberSpecified())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureRelated.getProductFeatureId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureRelated.getProductFeatureId() + "&" + "PRODUCT_FEATURE_CATEGORY_PRODUCT_FEATURE_CATEGORY_ID=" + productFeatureCategoryId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="8">
<h3>No ProductFeatures Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeature, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PRODUCT_FEATURE_CATEGORY_ADMIN, or PRODUCT_FEATURE_CATEGORY_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
