
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

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.feature.*" %>

<%@ page import="org.ofbiz.commonapp.product.cost.*" %>
<%@ page import="org.ofbiz.commonapp.product.price.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewProductFeature"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_FEATURE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_FEATURE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_FEATURE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productFeatureId = request.getParameter("PRODUCT_FEATURE_PRODUCT_FEATURE_ID");  


  ProductFeature productFeature = ProductFeatureHelper.findByPrimaryKey(productFeatureId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ProductFeature</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ProductFeature</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ProductFeature with (PRODUCT_FEATURE_ID: <%=productFeatureId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProductFeature")%>" class="buttontext">[Find ProductFeature]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature")%>" class="buttontext">[Create New ProductFeature]</a>
<%}%>
<%if(productFeature != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeature?UPDATE_MODE=DELETE&" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureId)%>" class="buttontext">[Delete this ProductFeature]</a>
  <%}%>
<%}%>

<%if(productFeature == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(productFeature == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ProductFeature was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeature.getProductFeatureId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeature.getProductFeatureTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeature.getProductFeatureCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeature.getDescription())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeature.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NUMBER_SPECIFIED</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(productFeature.getNumberSpecified())%>
    </td>
  </tr>

<%} //end if productFeature == null %>
</table>
  </div>
<%ProductFeature productFeatureSave = productFeature;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(productFeature == null && (productFeatureId != null)){%>
    ProductFeature with (PRODUCT_FEATURE_ID: <%=productFeatureId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    productFeature = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProductFeature")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(productFeature == null){%>
  <%if(hasCreatePermission){%>
    You may create a ProductFeature by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_PRODUCT_FEATURE_ID" value="<%=UtilFormatOut.checkNull(productFeatureId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ProductFeature (PRODUCT_FEATURE_ADMIN, or PRODUCT_FEATURE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_FEATURE_PRODUCT_FEATURE_ID" value="<%=productFeatureId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_ID</td>
      <td>
        <b><%=productFeatureId%></b> (This cannot be changed without re-creating the productFeature.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ProductFeature (PRODUCT_FEATURE_ADMIN, or PRODUCT_FEATURE_UPDATE needed).
  <%}%>
<%} //end if productFeature == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_FEATURE_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_PRODUCT_FEATURE_TYPE_ID" value="<%if(productFeature!=null){%><%=UtilFormatOut.checkNull(productFeature.getProductFeatureTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_PRODUCT_FEATURE_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_FEATURE_CATEGORY_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_PRODUCT_FEATURE_CATEGORY_ID" value="<%if(productFeature!=null){%><%=UtilFormatOut.checkNull(productFeature.getProductFeatureCategoryId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_PRODUCT_FEATURE_CATEGORY_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRODUCT_FEATURE_DESCRIPTION" value="<%if(productFeature!=null){%><%=UtilFormatOut.checkNull(productFeature.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_DESCRIPTION"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>UOM_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_UOM_ID" value="<%if(productFeature!=null){%><%=UtilFormatOut.checkNull(productFeature.getUomId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_UOM_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>NUMBER_SPECIFIED</td>
    <td>
      <input class='editInputBox' type="text" size="25" maxlength="25" name="PRODUCT_FEATURE_NUMBER_SPECIFIED" value="<%if(productFeature!=null){%><%=UtilFormatOut.formatQuantity(productFeature.getNumberSpecified())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_NUMBER_SPECIFIED"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && productFeature == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the productFeature for cases when removed to retain passed form values --%>
<%productFeature = productFeatureSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=8;
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
<%if(productFeature != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> ProductFeatureCategory</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> ProductFeatureType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> ProductFeatureAppl</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk>Main ProductFeatureIactn</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_VIEW", session)){%>
      <td id=tab5 class=offtab>
        <a href='javascript:ShowTab("tab5")' id=lnk5 class=offlnk>Assoc ProductFeatureIactn</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("FEATURE_DATA_OBJECT", "_VIEW", session)){%>
      <td id=tab6 class=offtab>
        <a href='javascript:ShowTab("tab6")' id=lnk6 class=offlnk> FeatureDataObject</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("COST_COMPONENT", "_VIEW", session)){%>
      <td id=tab7 class=offtab>
        <a href='javascript:ShowTab("tab7")' id=lnk7 class=offlnk> CostComponent</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRICE_COMPONENT", "_VIEW", session)){%>
      <td id=tab8 class=offtab>
        <a href='javascript:ShowTab("tab8")' id=lnk8 class=offlnk> PriceComponent</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductFeatureCategory, type: one --%>
<%if(productFeature != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_VIEW", session)){%>
    <%-- ProductFeatureCategory productFeatureCategoryRelated = ProductFeatureCategoryHelper.findByPrimaryKey(productFeature.getProductFeatureCategoryId()); --%>
    <%ProductFeatureCategory productFeatureCategoryRelated = productFeature.getProductFeatureCategory();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ProductFeatureCategory</b> with (PRODUCT_FEATURE_CATEGORY_ID: <%=productFeature.getProductFeatureCategoryId()%>)
    </div>
    <%if(productFeature.getProductFeatureCategoryId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureCategory?" + "PRODUCT_FEATURE_CATEGORY_PRODUCT_FEATURE_CATEGORY_ID=" + productFeature.getProductFeatureCategoryId())%>" class="buttontext">[View ProductFeatureCategory]</a>      
    <%if(productFeatureCategoryRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureCategory?" + "PRODUCT_FEATURE_CATEGORY_PRODUCT_FEATURE_CATEGORY_ID=" + productFeature.getProductFeatureCategoryId())%>" class="buttontext">[Create ProductFeatureCategory]</a>
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
  

<%-- Start Relation for ProductFeatureType, type: one --%>
<%if(productFeature != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_TYPE", "_VIEW", session)){%>
    <%-- ProductFeatureType productFeatureTypeRelated = ProductFeatureTypeHelper.findByPrimaryKey(productFeature.getProductFeatureTypeId()); --%>
    <%ProductFeatureType productFeatureTypeRelated = productFeature.getProductFeatureType();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ProductFeatureType</b> with (PRODUCT_FEATURE_TYPE_ID: <%=productFeature.getProductFeatureTypeId()%>)
    </div>
    <%if(productFeature.getProductFeatureTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureType?" + "PRODUCT_FEATURE_TYPE_PRODUCT_FEATURE_TYPE_ID=" + productFeature.getProductFeatureTypeId())%>" class="buttontext">[View ProductFeatureType]</a>      
    <%if(productFeatureTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureType?" + "PRODUCT_FEATURE_TYPE_PRODUCT_FEATURE_TYPE_ID=" + productFeature.getProductFeatureTypeId())%>" class="buttontext">[Create ProductFeatureType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productFeatureTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductFeatureType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureTypeRelated.getProductFeatureTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productFeatureTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeatureType, type: one --%>
  

<%-- Start Relation for ProductFeatureAppl, type: many --%>
<%if(productFeature != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductFeatureApplHelper.findByProductFeatureId(productFeature.getProductFeatureId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productFeature.getProductFeatureAppls());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductFeatureAppl</b> with (PRODUCT_FEATURE_ID: <%=productFeature.getProductFeatureId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureAppl?" + "PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_ID=" + productFeature.getProductFeatureId())%>" class="buttontext">[Create ProductFeatureAppl]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductFeatureId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productFeature.getProductFeatureId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductFeature?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductFeatureAppl]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_APPL_TYPE_ID</nobr></b></div></td>
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
        ProductFeatureAppl productFeatureApplRelated = (ProductFeatureAppl)relatedIterator.next();
        if(productFeatureApplRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureApplRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureApplRelated.getProductFeatureId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureApplRelated.getProductFeatureApplTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(productFeatureApplRelated != null)
        {
          java.util.Date date = productFeatureApplRelated.getFromDate();
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
        if(productFeatureApplRelated != null)
        {
          java.util.Date date = productFeatureApplRelated.getThruDate();
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
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureAppl?" + "PRODUCT_FEATURE_APPL_PRODUCT_ID=" + productFeatureApplRelated.getProductId() + "&" + "PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_ID=" + productFeatureApplRelated.getProductFeatureId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureAppl?" + "PRODUCT_FEATURE_APPL_PRODUCT_ID=" + productFeatureApplRelated.getProductId() + "&" + "PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_ID=" + productFeatureApplRelated.getProductFeatureId() + "&" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="7">
<h3>No ProductFeatureAppls Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeatureAppl, type: many --%>
  

<%-- Start Relation for ProductFeatureIactn, type: many --%>
<%if(productFeature != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductFeatureIactnHelper.findByProductFeatureId(productFeature.getProductFeatureId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productFeature.getMainProductFeatureIactns());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Main</b> Related Entities: <b>ProductFeatureIactn</b> with (PRODUCT_FEATURE_ID: <%=productFeature.getProductFeatureId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactn?" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID=" + productFeature.getProductFeatureId())%>" class="buttontext">[Create ProductFeatureIactn]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductFeatureId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productFeature.getProductFeatureId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductFeature?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductFeatureIactn]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID_TO</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_IACTN_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
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
        ProductFeatureIactn productFeatureIactnRelated = (ProductFeatureIactn)relatedIterator.next();
        if(productFeatureIactnRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnRelated.getProductFeatureId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnRelated.getProductFeatureIdTo())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnRelated.getProductFeatureIactnTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactn?" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID=" + productFeatureIactnRelated.getProductFeatureId() + "&" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID_TO=" + productFeatureIactnRelated.getProductFeatureIdTo())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureIactn?" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID=" + productFeatureIactnRelated.getProductFeatureId() + "&" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID_TO=" + productFeatureIactnRelated.getProductFeatureIdTo() + "&" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No ProductFeatureIactns Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeatureIactn, type: many --%>
  

<%-- Start Relation for ProductFeatureIactn, type: many --%>
<%if(productFeature != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductFeatureIactnHelper.findByProductFeatureIdTo(productFeature.getProductFeatureId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productFeature.getAssocProductFeatureIactns());%>
  <DIV id=area5 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Assoc</b> Related Entities: <b>ProductFeatureIactn</b> with (PRODUCT_FEATURE_ID_TO: <%=productFeature.getProductFeatureId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactn?" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID_TO=" + productFeature.getProductFeatureId())%>" class="buttontext">[Create ProductFeatureIactn]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductFeatureIdTo";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productFeature.getProductFeatureId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductFeature?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductFeatureIactn]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID_TO</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_IACTN_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
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
        ProductFeatureIactn productFeatureIactnRelated = (ProductFeatureIactn)relatedIterator.next();
        if(productFeatureIactnRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnRelated.getProductFeatureId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnRelated.getProductFeatureIdTo())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnRelated.getProductFeatureIactnTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactn?" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID=" + productFeatureIactnRelated.getProductFeatureId() + "&" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID_TO=" + productFeatureIactnRelated.getProductFeatureIdTo())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureIactn?" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID=" + productFeatureIactnRelated.getProductFeatureId() + "&" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID_TO=" + productFeatureIactnRelated.getProductFeatureIdTo() + "&" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No ProductFeatureIactns Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeatureIactn, type: many --%>
  

<%-- Start Relation for FeatureDataObject, type: many --%>
<%if(productFeature != null){%>
  <%if(Security.hasEntityPermission("FEATURE_DATA_OBJECT", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(FeatureDataObjectHelper.findByProductFeatureId(productFeature.getProductFeatureId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productFeature.getFeatureDataObjects());%>
  <DIV id=area6 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>FeatureDataObject</b> with (PRODUCT_FEATURE_ID: <%=productFeature.getProductFeatureId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("FEATURE_DATA_OBJECT", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("FEATURE_DATA_OBJECT", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("FEATURE_DATA_OBJECT", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewFeatureDataObject?" + "FEATURE_DATA_OBJECT_PRODUCT_FEATURE_ID=" + productFeature.getProductFeatureId())%>" class="buttontext">[Create FeatureDataObject]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductFeatureId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productFeature.getProductFeatureId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductFeature?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find FeatureDataObject]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>DATA_OBJECT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID</nobr></b></div></td>
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
        FeatureDataObject featureDataObjectRelated = (FeatureDataObject)relatedIterator.next();
        if(featureDataObjectRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(featureDataObjectRelated.getDataObjectId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(featureDataObjectRelated.getProductFeatureId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewFeatureDataObject?" + "FEATURE_DATA_OBJECT_DATA_OBJECT_ID=" + featureDataObjectRelated.getDataObjectId() + "&" + "FEATURE_DATA_OBJECT_PRODUCT_FEATURE_ID=" + featureDataObjectRelated.getProductFeatureId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateFeatureDataObject?" + "FEATURE_DATA_OBJECT_DATA_OBJECT_ID=" + featureDataObjectRelated.getDataObjectId() + "&" + "FEATURE_DATA_OBJECT_PRODUCT_FEATURE_ID=" + featureDataObjectRelated.getProductFeatureId() + "&" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No FeatureDataObjects Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for FeatureDataObject, type: many --%>
  

<%-- Start Relation for CostComponent, type: many --%>
<%if(productFeature != null){%>
  <%if(Security.hasEntityPermission("COST_COMPONENT", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(CostComponentHelper.findByProductFeatureId(productFeature.getProductFeatureId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productFeature.getCostComponents());%>
  <DIV id=area7 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>CostComponent</b> with (PRODUCT_FEATURE_ID: <%=productFeature.getProductFeatureId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("COST_COMPONENT", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("COST_COMPONENT", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("COST_COMPONENT", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewCostComponent?" + "COST_COMPONENT_PRODUCT_FEATURE_ID=" + productFeature.getProductFeatureId())%>" class="buttontext">[Create CostComponent]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductFeatureId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productFeature.getProductFeatureId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductFeature?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find CostComponent]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>COST_COMPONENT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>COST_COMPONENT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>GEO_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>COST</nobr></b></div></td>
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
        CostComponent costComponentRelated = (CostComponent)relatedIterator.next();
        if(costComponentRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getCostComponentId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getCostComponentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getProductFeatureId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getPartyId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(costComponentRelated.getGeoId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(costComponentRelated != null)
        {
          java.util.Date date = costComponentRelated.getFromDate();
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
        if(costComponentRelated != null)
        {
          java.util.Date date = costComponentRelated.getThruDate();
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
      <%=UtilFormatOut.formatQuantity(costComponentRelated.getCost())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewCostComponent?" + "COST_COMPONENT_COST_COMPONENT_ID=" + costComponentRelated.getCostComponentId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateCostComponent?" + "COST_COMPONENT_COST_COMPONENT_ID=" + costComponentRelated.getCostComponentId() + "&" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="11">
<h3>No CostComponents Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for CostComponent, type: many --%>
  

<%-- Start Relation for PriceComponent, type: many --%>
<%if(productFeature != null){%>
  <%if(Security.hasEntityPermission("PRICE_COMPONENT", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PriceComponentHelper.findByProductFeatureId(productFeature.getProductFeatureId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productFeature.getPriceComponents());%>
  <DIV id=area8 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PriceComponent</b> with (PRODUCT_FEATURE_ID: <%=productFeature.getProductFeatureId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponent?" + "PRICE_COMPONENT_PRODUCT_FEATURE_ID=" + productFeature.getProductFeatureId())%>" class="buttontext">[Create PriceComponent]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductFeatureId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productFeature.getProductFeatureId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductFeature?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PriceComponent]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdatePriceComponent?" + "PRICE_COMPONENT_PRICE_COMPONENT_ID=" + priceComponentRelated.getPriceComponentId() + "&" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  <h3>You do not have permission to view this page (PRODUCT_FEATURE_ADMIN, or PRODUCT_FEATURE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
