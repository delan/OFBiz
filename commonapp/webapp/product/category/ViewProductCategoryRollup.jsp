
<%
/**
 *  Title: Product Category Rollup Entity
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
<%@ page import="org.ofbiz.commonapp.product.category.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewProductCategoryRollup"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productCategoryId = request.getParameter("PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID");  
  String parentProductCategoryId = request.getParameter("PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID");  


  ProductCategoryRollup productCategoryRollup = ProductCategoryRollupHelper.findByPrimaryKey(productCategoryId, parentProductCategoryId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ProductCategoryRollup</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ProductCategoryRollup</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ProductCategoryRollup with (PRODUCT_CATEGORY_ID, PARENT_PRODUCT_CATEGORY_ID: <%=productCategoryId%>, <%=parentProductCategoryId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProductCategoryRollup")%>" class="buttontext">[Find ProductCategoryRollup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryRollup")%>" class="buttontext">[Create New ProductCategoryRollup]</a>
<%}%>
<%if(productCategoryRollup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProductCategoryRollup?UPDATE_MODE=DELETE&" + "PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID=" + productCategoryId + "&" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + parentProductCategoryId)%>" class="buttontext">[Delete this ProductCategoryRollup]</a>
  <%}%>
<%}%>

<%if(productCategoryRollup == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(productCategoryRollup == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ProductCategoryRollup was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryRollup.getProductCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_PRODUCT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryRollup.getParentProductCategoryId())%>
    </td>
  </tr>

<%} //end if productCategoryRollup == null %>
</table>
  </div>
<%ProductCategoryRollup productCategoryRollupSave = productCategoryRollup;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(productCategoryRollup == null && (productCategoryId != null || parentProductCategoryId != null)){%>
    ProductCategoryRollup with (PRODUCT_CATEGORY_ID, PARENT_PRODUCT_CATEGORY_ID: <%=productCategoryId%>, <%=parentProductCategoryId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    productCategoryRollup = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProductCategoryRollup")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(productCategoryRollup == null){%>
  <%if(hasCreatePermission){%>
    You may create a ProductCategoryRollup by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_CATEGORY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID" value="<%=UtilFormatOut.checkNull(productCategoryId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARENT_PRODUCT_CATEGORY_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID" value="<%=UtilFormatOut.checkNull(parentProductCategoryId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ProductCategoryRollup (PRODUCT_CATEGORY_ROLLUP_ADMIN, or PRODUCT_CATEGORY_ROLLUP_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID" value="<%=productCategoryId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_CATEGORY_ID</td>
      <td>
        <b><%=productCategoryId%></b> (This cannot be changed without re-creating the productCategoryRollup.)
      </td>
    </tr>
      <input type="hidden" name="PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID" value="<%=parentProductCategoryId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PARENT_PRODUCT_CATEGORY_ID</td>
      <td>
        <b><%=parentProductCategoryId%></b> (This cannot be changed without re-creating the productCategoryRollup.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ProductCategoryRollup (PRODUCT_CATEGORY_ROLLUP_ADMIN, or PRODUCT_CATEGORY_ROLLUP_UPDATE needed).
  <%}%>
<%} //end if productCategoryRollup == null %>

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
<%if((hasUpdatePermission || hasCreatePermission) && productCategoryRollup == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the productCategoryRollup for cases when removed to retain passed form values --%>
<%productCategoryRollup = productCategoryRollupSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=5;
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
<%if(productCategoryRollup != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Current ProductCategory</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Parent ProductCategory</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk>Child ProductCategoryRollup</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk>Parent ProductCategoryRollup</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_VIEW", session)){%>
      <td id=tab5 class=offtab>
        <a href='javascript:ShowTab("tab5")' id=lnk5 class=offlnk>Sibling ProductCategoryRollup</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductCategory, type: one --%>
<%if(productCategoryRollup != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_VIEW", session)){%>
    <%-- ProductCategory productCategoryRelated = ProductCategoryHelper.findByPrimaryKey(productCategoryRollup.getProductCategoryId()); --%>
    <%ProductCategory productCategoryRelated = productCategoryRollup.getCurrentProductCategory();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Current</b> Related Entity: <b>ProductCategory</b> with (PRODUCT_CATEGORY_ID: <%=productCategoryRollup.getProductCategoryId()%>)
    </div>
    <%if(productCategoryRollup.getProductCategoryId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategory?" + "PRODUCT_CATEGORY_PRODUCT_CATEGORY_ID=" + productCategoryRollup.getProductCategoryId())%>" class="buttontext">[View ProductCategory]</a>      
    <%if(productCategoryRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategory?" + "PRODUCT_CATEGORY_PRODUCT_CATEGORY_ID=" + productCategoryRollup.getProductCategoryId())%>" class="buttontext">[Create ProductCategory]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productCategoryRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductCategory was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryRelated.getProductCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productCategoryRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategory, type: one --%>
  

<%-- Start Relation for ProductCategory, type: one --%>
<%if(productCategoryRollup != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_VIEW", session)){%>
    <%-- ProductCategory productCategoryRelated = ProductCategoryHelper.findByPrimaryKey(productCategoryRollup.getParentProductCategoryId()); --%>
    <%ProductCategory productCategoryRelated = productCategoryRollup.getParentProductCategory();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>ProductCategory</b> with (PRODUCT_CATEGORY_ID: <%=productCategoryRollup.getParentProductCategoryId()%>)
    </div>
    <%if(productCategoryRollup.getParentProductCategoryId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategory?" + "PRODUCT_CATEGORY_PRODUCT_CATEGORY_ID=" + productCategoryRollup.getParentProductCategoryId())%>" class="buttontext">[View ProductCategory]</a>      
    <%if(productCategoryRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_CATEGORY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategory?" + "PRODUCT_CATEGORY_PRODUCT_CATEGORY_ID=" + productCategoryRollup.getParentProductCategoryId())%>" class="buttontext">[Create ProductCategory]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productCategoryRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductCategory was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryRelated.getProductCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productCategoryRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategory, type: one --%>
  

<%-- Start Relation for ProductCategoryRollup, type: many --%>
<%if(productCategoryRollup != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductCategoryRollupHelper.findByParentProductCategoryId(productCategoryRollup.getProductCategoryId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productCategoryRollup.getChildProductCategoryRollups());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Child</b> Related Entities: <b>ProductCategoryRollup</b> with (PARENT_PRODUCT_CATEGORY_ID: <%=productCategoryRollup.getProductCategoryId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryRollup?" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + productCategoryRollup.getProductCategoryId())%>" class="buttontext">[Create ProductCategoryRollup]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentProductCategoryId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productCategoryRollup.getProductCategoryId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductCategoryRollup?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductCategoryRollup]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_CATEGORY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARENT_PRODUCT_CATEGORY_ID</nobr></b></div></td>
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
        ProductCategoryRollup productCategoryRollupRelated = (ProductCategoryRollup)relatedIterator.next();
        if(productCategoryRollupRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryRollupRelated.getProductCategoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryRollupRelated.getParentProductCategoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryRollup?" + "PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getProductCategoryId() + "&" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getParentProductCategoryId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductCategoryRollup?" + "PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getProductCategoryId() + "&" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getParentProductCategoryId() + "&" + "PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID=" + productCategoryId + "&" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + parentProductCategoryId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No ProductCategoryRollups Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategoryRollup, type: many --%>
  

<%-- Start Relation for ProductCategoryRollup, type: many --%>
<%if(productCategoryRollup != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductCategoryRollupHelper.findByProductCategoryId(productCategoryRollup.getParentProductCategoryId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productCategoryRollup.getParentProductCategoryRollups());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Parent</b> Related Entities: <b>ProductCategoryRollup</b> with (PRODUCT_CATEGORY_ID: <%=productCategoryRollup.getParentProductCategoryId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryRollup?" + "PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID=" + productCategoryRollup.getParentProductCategoryId())%>" class="buttontext">[Create ProductCategoryRollup]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductCategoryId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productCategoryRollup.getParentProductCategoryId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductCategoryRollup?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductCategoryRollup]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_CATEGORY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARENT_PRODUCT_CATEGORY_ID</nobr></b></div></td>
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
        ProductCategoryRollup productCategoryRollupRelated = (ProductCategoryRollup)relatedIterator.next();
        if(productCategoryRollupRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryRollupRelated.getProductCategoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryRollupRelated.getParentProductCategoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryRollup?" + "PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getProductCategoryId() + "&" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getParentProductCategoryId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductCategoryRollup?" + "PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getProductCategoryId() + "&" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getParentProductCategoryId() + "&" + "PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID=" + productCategoryId + "&" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + parentProductCategoryId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No ProductCategoryRollups Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategoryRollup, type: many --%>
  

<%-- Start Relation for ProductCategoryRollup, type: many --%>
<%if(productCategoryRollup != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductCategoryRollupHelper.findByParentProductCategoryId(productCategoryRollup.getParentProductCategoryId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productCategoryRollup.getSiblingProductCategoryRollups());%>
  <DIV id=area5 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Sibling</b> Related Entities: <b>ProductCategoryRollup</b> with (PARENT_PRODUCT_CATEGORY_ID: <%=productCategoryRollup.getParentProductCategoryId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryRollup?" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + productCategoryRollup.getParentProductCategoryId())%>" class="buttontext">[Create ProductCategoryRollup]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentProductCategoryId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productCategoryRollup.getParentProductCategoryId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductCategoryRollup?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductCategoryRollup]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_CATEGORY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARENT_PRODUCT_CATEGORY_ID</nobr></b></div></td>
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
        ProductCategoryRollup productCategoryRollupRelated = (ProductCategoryRollup)relatedIterator.next();
        if(productCategoryRollupRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryRollupRelated.getProductCategoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryRollupRelated.getParentProductCategoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryRollup?" + "PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getProductCategoryId() + "&" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getParentProductCategoryId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductCategoryRollup?" + "PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getProductCategoryId() + "&" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + productCategoryRollupRelated.getParentProductCategoryId() + "&" + "PRODUCT_CATEGORY_ROLLUP_PRODUCT_CATEGORY_ID=" + productCategoryId + "&" + "PRODUCT_CATEGORY_ROLLUP_PARENT_PRODUCT_CATEGORY_ID=" + parentProductCategoryId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No ProductCategoryRollups Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategoryRollup, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PRODUCT_CATEGORY_ROLLUP_ADMIN, or PRODUCT_CATEGORY_ROLLUP_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
