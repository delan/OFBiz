
<%
/**
 *  Title: Product Category Type Entity
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
 *@created    Fri Jul 27 01:37:11 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.category.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewProductCategoryType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productCategoryTypeId = request.getParameter("PRODUCT_CATEGORY_TYPE_PRODUCT_CATEGORY_TYPE_ID");  


  ProductCategoryType productCategoryType = ProductCategoryTypeHelper.findByPrimaryKey(productCategoryTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ProductCategoryType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ProductCategoryType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ProductCategoryType with (PRODUCT_CATEGORY_TYPE_ID: <%=productCategoryTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProductCategoryType")%>" class="buttontext">[Find ProductCategoryType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryType")%>" class="buttontext">[Create New ProductCategoryType]</a>
<%}%>
<%if(productCategoryType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProductCategoryType?UPDATE_MODE=DELETE&" + "PRODUCT_CATEGORY_TYPE_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryTypeId)%>" class="buttontext">[Delete this ProductCategoryType]</a>
  <%}%>
<%}%>

<%if(productCategoryType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(productCategoryType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ProductCategoryType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_CATEGORY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryType.getProductCategoryTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryType.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryType.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryType.getDescription())%>
    </td>
  </tr>

<%} //end if productCategoryType == null %>
</table>
  </div>
<%ProductCategoryType productCategoryTypeSave = productCategoryType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(productCategoryType == null && (productCategoryTypeId != null)){%>
    ProductCategoryType with (PRODUCT_CATEGORY_TYPE_ID: <%=productCategoryTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    productCategoryType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProductCategoryType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(productCategoryType == null){%>
  <%if(hasCreatePermission){%>
    You may create a ProductCategoryType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_CATEGORY_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_CATEGORY_TYPE_PRODUCT_CATEGORY_TYPE_ID" value="<%=UtilFormatOut.checkNull(productCategoryTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ProductCategoryType (PRODUCT_CATEGORY_TYPE_ADMIN, or PRODUCT_CATEGORY_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_CATEGORY_TYPE_PRODUCT_CATEGORY_TYPE_ID" value="<%=productCategoryTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_CATEGORY_TYPE_ID</td>
      <td>
        <b><%=productCategoryTypeId%></b> (This cannot be changed without re-creating the productCategoryType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ProductCategoryType (PRODUCT_CATEGORY_TYPE_ADMIN, or PRODUCT_CATEGORY_TYPE_UPDATE needed).
  <%}%>
<%} //end if productCategoryType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_CATEGORY_TYPE_PARENT_TYPE_ID" value="<%if(productCategoryType!=null){%><%=UtilFormatOut.checkNull(productCategoryType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_CATEGORY_TYPE_PARENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>HAS_TABLE</td>
    <td>
      <input class='editInputBox' type="text" size="1" maxlength="1" name="PRODUCT_CATEGORY_TYPE_HAS_TABLE" value="<%if(productCategoryType!=null){%><%=UtilFormatOut.checkNull(productCategoryType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_CATEGORY_TYPE_HAS_TABLE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRODUCT_CATEGORY_TYPE_DESCRIPTION" value="<%if(productCategoryType!=null){%><%=UtilFormatOut.checkNull(productCategoryType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_CATEGORY_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && productCategoryType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the productCategoryType for cases when removed to retain passed form values --%>
<%productCategoryType = productCategoryTypeSave;%>

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
<%if(productCategoryType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Parent ProductCategoryType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Child ProductCategoryType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> ProductCategoryTypeAttr</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_CLASS", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> ProductCategoryClass</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductCategoryType, type: one --%>
<%if(productCategoryType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_VIEW", session)){%>
    <%-- ProductCategoryType productCategoryTypeRelated = ProductCategoryTypeHelper.findByPrimaryKey(productCategoryType.getParentTypeId()); --%>
    <%ProductCategoryType productCategoryTypeRelated = productCategoryType.getParentProductCategoryType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>ProductCategoryType</b> with (PRODUCT_CATEGORY_TYPE_ID: <%=productCategoryType.getParentTypeId()%>)
    </div>
    <%if(productCategoryType.getParentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryType?" + "PRODUCT_CATEGORY_TYPE_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryType.getParentTypeId())%>" class="buttontext">[View ProductCategoryType]</a>      
    <%if(productCategoryTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryType?" + "PRODUCT_CATEGORY_TYPE_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryType.getParentTypeId())%>" class="buttontext">[Create ProductCategoryType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productCategoryTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductCategoryType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_CATEGORY_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryTypeRelated.getProductCategoryTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productCategoryTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productCategoryTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategoryType, type: one --%>
  

<%-- Start Relation for ProductCategoryType, type: many --%>
<%if(productCategoryType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductCategoryTypeHelper.findByParentTypeId(productCategoryType.getProductCategoryTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productCategoryType.getChildProductCategoryTypes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Child</b> Related Entities: <b>ProductCategoryType</b> with (PARENT_TYPE_ID: <%=productCategoryType.getProductCategoryTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryType?" + "PRODUCT_CATEGORY_TYPE_PARENT_TYPE_ID=" + productCategoryType.getProductCategoryTypeId())%>" class="buttontext">[Create ProductCategoryType]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productCategoryType.getProductCategoryTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductCategoryType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductCategoryType]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_CATEGORY_TYPE_ID</nobr></b></div></td>
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
        ProductCategoryType productCategoryTypeRelated = (ProductCategoryType)relatedIterator.next();
        if(productCategoryTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryTypeRelated.getProductCategoryTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryTypeRelated.getParentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryTypeRelated.getHasTable())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryTypeRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryType?" + "PRODUCT_CATEGORY_TYPE_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryTypeRelated.getProductCategoryTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductCategoryType?" + "PRODUCT_CATEGORY_TYPE_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryTypeRelated.getProductCategoryTypeId() + "&" + "PRODUCT_CATEGORY_TYPE_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No ProductCategoryTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategoryType, type: many --%>
  

<%-- Start Relation for ProductCategoryTypeAttr, type: many --%>
<%if(productCategoryType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductCategoryTypeAttrHelper.findByProductCategoryTypeId(productCategoryType.getProductCategoryTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productCategoryType.getProductCategoryTypeAttrs());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductCategoryTypeAttr</b> with (PRODUCT_CATEGORY_TYPE_ID: <%=productCategoryType.getProductCategoryTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE_ATTR", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE_ATTR", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_TYPE_ATTR", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryTypeAttr?" + "PRODUCT_CATEGORY_TYPE_ATTR_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryType.getProductCategoryTypeId())%>" class="buttontext">[Create ProductCategoryTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductCategoryTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productCategoryType.getProductCategoryTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductCategoryType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductCategoryTypeAttr]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_CATEGORY_TYPE_ID</nobr></b></div></td>
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
        ProductCategoryTypeAttr productCategoryTypeAttrRelated = (ProductCategoryTypeAttr)relatedIterator.next();
        if(productCategoryTypeAttrRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryTypeAttrRelated.getProductCategoryTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryTypeAttrRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryTypeAttr?" + "PRODUCT_CATEGORY_TYPE_ATTR_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryTypeAttrRelated.getProductCategoryTypeId() + "&" + "PRODUCT_CATEGORY_TYPE_ATTR_NAME=" + productCategoryTypeAttrRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductCategoryTypeAttr?" + "PRODUCT_CATEGORY_TYPE_ATTR_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryTypeAttrRelated.getProductCategoryTypeId() + "&" + "PRODUCT_CATEGORY_TYPE_ATTR_NAME=" + productCategoryTypeAttrRelated.getName() + "&" + "PRODUCT_CATEGORY_TYPE_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No ProductCategoryTypeAttrs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategoryTypeAttr, type: many --%>
  

<%-- Start Relation for ProductCategoryClass, type: many --%>
<%if(productCategoryType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_CATEGORY_CLASS", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductCategoryClassHelper.findByProductCategoryTypeId(productCategoryType.getProductCategoryTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productCategoryType.getProductCategoryClasss());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductCategoryClass</b> with (PRODUCT_CATEGORY_TYPE_ID: <%=productCategoryType.getProductCategoryTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_CLASS", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_CLASS", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_CATEGORY_CLASS", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryClass?" + "PRODUCT_CATEGORY_CLASS_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryType.getProductCategoryTypeId())%>" class="buttontext">[Create ProductCategoryClass]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductCategoryTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productCategoryType.getProductCategoryTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductCategoryType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductCategoryClass]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_CATEGORY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_CATEGORY_TYPE_ID</nobr></b></div></td>
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
        ProductCategoryClass productCategoryClassRelated = (ProductCategoryClass)relatedIterator.next();
        if(productCategoryClassRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryClassRelated.getProductCategoryId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productCategoryClassRelated.getProductCategoryTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(productCategoryClassRelated != null)
        {
          java.util.Date date = productCategoryClassRelated.getFromDate();
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
        if(productCategoryClassRelated != null)
        {
          java.util.Date date = productCategoryClassRelated.getThruDate();
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
        <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryClass?" + "PRODUCT_CATEGORY_CLASS_PRODUCT_CATEGORY_ID=" + productCategoryClassRelated.getProductCategoryId() + "&" + "PRODUCT_CATEGORY_CLASS_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryClassRelated.getProductCategoryTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductCategoryClass?" + "PRODUCT_CATEGORY_CLASS_PRODUCT_CATEGORY_ID=" + productCategoryClassRelated.getProductCategoryId() + "&" + "PRODUCT_CATEGORY_CLASS_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryClassRelated.getProductCategoryTypeId() + "&" + "PRODUCT_CATEGORY_TYPE_PRODUCT_CATEGORY_TYPE_ID=" + productCategoryTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No ProductCategoryClasss Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductCategoryClass, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PRODUCT_CATEGORY_TYPE_ADMIN, or PRODUCT_CATEGORY_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
