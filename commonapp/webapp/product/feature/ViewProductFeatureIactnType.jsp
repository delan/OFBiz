
<%
/**
 *  Title: Product Feature Interaction Type Entity
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
 *@created    Fri Jul 27 01:37:14 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.feature.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewProductFeatureIactnType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productFeatureIactnTypeId = request.getParameter("PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID");  


  ProductFeatureIactnType productFeatureIactnType = ProductFeatureIactnTypeHelper.findByPrimaryKey(productFeatureIactnTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ProductFeatureIactnType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ProductFeatureIactnType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ProductFeatureIactnType with (PRODUCT_FEATURE_IACTN_TYPE_ID: <%=productFeatureIactnTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProductFeatureIactnType")%>" class="buttontext">[Find ProductFeatureIactnType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactnType")%>" class="buttontext">[Create New ProductFeatureIactnType]</a>
<%}%>
<%if(productFeatureIactnType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureIactnType?UPDATE_MODE=DELETE&" + "PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID=" + productFeatureIactnTypeId)%>" class="buttontext">[Delete this ProductFeatureIactnType]</a>
  <%}%>
<%}%>

<%if(productFeatureIactnType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(productFeatureIactnType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ProductFeatureIactnType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_IACTN_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactnType.getProductFeatureIactnTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactnType.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactnType.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactnType.getDescription())%>
    </td>
  </tr>

<%} //end if productFeatureIactnType == null %>
</table>
  </div>
<%ProductFeatureIactnType productFeatureIactnTypeSave = productFeatureIactnType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(productFeatureIactnType == null && (productFeatureIactnTypeId != null)){%>
    ProductFeatureIactnType with (PRODUCT_FEATURE_IACTN_TYPE_ID: <%=productFeatureIactnTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    productFeatureIactnType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProductFeatureIactnType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(productFeatureIactnType == null){%>
  <%if(hasCreatePermission){%>
    You may create a ProductFeatureIactnType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_IACTN_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID" value="<%=UtilFormatOut.checkNull(productFeatureIactnTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ProductFeatureIactnType (PRODUCT_FEATURE_IACTN_TYPE_ADMIN, or PRODUCT_FEATURE_IACTN_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID" value="<%=productFeatureIactnTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_IACTN_TYPE_ID</td>
      <td>
        <b><%=productFeatureIactnTypeId%></b> (This cannot be changed without re-creating the productFeatureIactnType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ProductFeatureIactnType (PRODUCT_FEATURE_IACTN_TYPE_ADMIN, or PRODUCT_FEATURE_IACTN_TYPE_UPDATE needed).
  <%}%>
<%} //end if productFeatureIactnType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_IACTN_TYPE_PARENT_TYPE_ID" value="<%if(productFeatureIactnType!=null){%><%=UtilFormatOut.checkNull(productFeatureIactnType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_IACTN_TYPE_PARENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>HAS_TABLE</td>
    <td>
      <input class='editInputBox' type="text" size="1" maxlength="1" name="PRODUCT_FEATURE_IACTN_TYPE_HAS_TABLE" value="<%if(productFeatureIactnType!=null){%><%=UtilFormatOut.checkNull(productFeatureIactnType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_IACTN_TYPE_HAS_TABLE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRODUCT_FEATURE_IACTN_TYPE_DESCRIPTION" value="<%if(productFeatureIactnType!=null){%><%=UtilFormatOut.checkNull(productFeatureIactnType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_IACTN_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && productFeatureIactnType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the productFeatureIactnType for cases when removed to retain passed form values --%>
<%productFeatureIactnType = productFeatureIactnTypeSave;%>

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
<%if(productFeatureIactnType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Parent ProductFeatureIactnType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Child ProductFeatureIactnType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> ProductFeatureIactn</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductFeatureIactnType, type: one --%>
<%if(productFeatureIactnType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_VIEW", session)){%>
    <%-- ProductFeatureIactnType productFeatureIactnTypeRelated = ProductFeatureIactnTypeHelper.findByPrimaryKey(productFeatureIactnType.getParentTypeId()); --%>
    <%ProductFeatureIactnType productFeatureIactnTypeRelated = productFeatureIactnType.getParentProductFeatureIactnType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>ProductFeatureIactnType</b> with (PRODUCT_FEATURE_IACTN_TYPE_ID: <%=productFeatureIactnType.getParentTypeId()%>)
    </div>
    <%if(productFeatureIactnType.getParentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactnType?" + "PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID=" + productFeatureIactnType.getParentTypeId())%>" class="buttontext">[View ProductFeatureIactnType]</a>      
    <%if(productFeatureIactnTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactnType?" + "PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID=" + productFeatureIactnType.getParentTypeId())%>" class="buttontext">[Create ProductFeatureIactnType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productFeatureIactnTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductFeatureIactnType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_IACTN_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactnTypeRelated.getProductFeatureIactnTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactnTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactnTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactnTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productFeatureIactnTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeatureIactnType, type: one --%>
  

<%-- Start Relation for ProductFeatureIactnType, type: many --%>
<%if(productFeatureIactnType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductFeatureIactnTypeHelper.findByParentTypeId(productFeatureIactnType.getProductFeatureIactnTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productFeatureIactnType.getChildProductFeatureIactnTypes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Child</b> Related Entities: <b>ProductFeatureIactnType</b> with (PARENT_TYPE_ID: <%=productFeatureIactnType.getProductFeatureIactnTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactnType?" + "PRODUCT_FEATURE_IACTN_TYPE_PARENT_TYPE_ID=" + productFeatureIactnType.getProductFeatureIactnTypeId())%>" class="buttontext">[Create ProductFeatureIactnType]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productFeatureIactnType.getProductFeatureIactnTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductFeatureIactnType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductFeatureIactnType]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_IACTN_TYPE_ID</nobr></b></div></td>
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
        ProductFeatureIactnType productFeatureIactnTypeRelated = (ProductFeatureIactnType)relatedIterator.next();
        if(productFeatureIactnTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnTypeRelated.getProductFeatureIactnTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnTypeRelated.getParentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnTypeRelated.getHasTable())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureIactnTypeRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactnType?" + "PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID=" + productFeatureIactnTypeRelated.getProductFeatureIactnTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureIactnType?" + "PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID=" + productFeatureIactnTypeRelated.getProductFeatureIactnTypeId() + "&" + "PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID=" + productFeatureIactnTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No ProductFeatureIactnTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeatureIactnType, type: many --%>
  

<%-- Start Relation for ProductFeatureIactn, type: many --%>
<%if(productFeatureIactnType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductFeatureIactnHelper.findByProductFeatureIactnTypeId(productFeatureIactnType.getProductFeatureIactnTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productFeatureIactnType.getProductFeatureIactns());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductFeatureIactn</b> with (PRODUCT_FEATURE_IACTN_TYPE_ID: <%=productFeatureIactnType.getProductFeatureIactnTypeId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactn?" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_IACTN_TYPE_ID=" + productFeatureIactnType.getProductFeatureIactnTypeId())%>" class="buttontext">[Create ProductFeatureIactn]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductFeatureIactnTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productFeatureIactnType.getProductFeatureIactnTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductFeatureIactnType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductFeatureIactn]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureIactn?" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID=" + productFeatureIactnRelated.getProductFeatureId() + "&" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID_TO=" + productFeatureIactnRelated.getProductFeatureIdTo() + "&" + "PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID=" + productFeatureIactnTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PRODUCT_FEATURE_IACTN_TYPE_ADMIN, or PRODUCT_FEATURE_IACTN_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
