
<%
/**
 *  Title: Product Feature Applicability Type Entity
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
<%pageContext.setAttribute("PageName", "ViewProductFeatureApplType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productFeatureApplTypeId = request.getParameter("PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID");  


  ProductFeatureApplType productFeatureApplType = ProductFeatureApplTypeHelper.findByPrimaryKey(productFeatureApplTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ProductFeatureApplType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ProductFeatureApplType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ProductFeatureApplType with (PRODUCT_FEATURE_APPL_TYPE_ID: <%=productFeatureApplTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProductFeatureApplType")%>" class="buttontext">[Find ProductFeatureApplType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureApplType")%>" class="buttontext">[Create New ProductFeatureApplType]</a>
<%}%>
<%if(productFeatureApplType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureApplType?UPDATE_MODE=DELETE&" + "PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID=" + productFeatureApplTypeId)%>" class="buttontext">[Delete this ProductFeatureApplType]</a>
  <%}%>
<%}%>

<%if(productFeatureApplType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(productFeatureApplType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ProductFeatureApplType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_APPL_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplType.getProductFeatureApplTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplType.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplType.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplType.getDescription())%>
    </td>
  </tr>

<%} //end if productFeatureApplType == null %>
</table>
  </div>
<%ProductFeatureApplType productFeatureApplTypeSave = productFeatureApplType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(productFeatureApplType == null && (productFeatureApplTypeId != null)){%>
    ProductFeatureApplType with (PRODUCT_FEATURE_APPL_TYPE_ID: <%=productFeatureApplTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    productFeatureApplType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProductFeatureApplType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(productFeatureApplType == null){%>
  <%if(hasCreatePermission){%>
    You may create a ProductFeatureApplType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_APPL_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID" value="<%=UtilFormatOut.checkNull(productFeatureApplTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ProductFeatureApplType (PRODUCT_FEATURE_APPL_TYPE_ADMIN, or PRODUCT_FEATURE_APPL_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID" value="<%=productFeatureApplTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_APPL_TYPE_ID</td>
      <td>
        <b><%=productFeatureApplTypeId%></b> (This cannot be changed without re-creating the productFeatureApplType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ProductFeatureApplType (PRODUCT_FEATURE_APPL_TYPE_ADMIN, or PRODUCT_FEATURE_APPL_TYPE_UPDATE needed).
  <%}%>
<%} //end if productFeatureApplType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_APPL_TYPE_PARENT_TYPE_ID" value="<%if(productFeatureApplType!=null){%><%=UtilFormatOut.checkNull(productFeatureApplType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_APPL_TYPE_PARENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>HAS_TABLE</td>
    <td>
      <input class='editInputBox' type="text" size="1" maxlength="1" name="PRODUCT_FEATURE_APPL_TYPE_HAS_TABLE" value="<%if(productFeatureApplType!=null){%><%=UtilFormatOut.checkNull(productFeatureApplType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_APPL_TYPE_HAS_TABLE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRODUCT_FEATURE_APPL_TYPE_DESCRIPTION" value="<%if(productFeatureApplType!=null){%><%=UtilFormatOut.checkNull(productFeatureApplType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_APPL_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && productFeatureApplType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the productFeatureApplType for cases when removed to retain passed form values --%>
<%productFeatureApplType = productFeatureApplTypeSave;%>

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
<%if(productFeatureApplType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Parent ProductFeatureApplType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Child ProductFeatureApplType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> ProductFeatureAppl</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductFeatureApplType, type: one --%>
<%if(productFeatureApplType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_VIEW", session)){%>
    <%-- ProductFeatureApplType productFeatureApplTypeRelated = ProductFeatureApplTypeHelper.findByPrimaryKey(productFeatureApplType.getParentTypeId()); --%>
    <%ProductFeatureApplType productFeatureApplTypeRelated = productFeatureApplType.getParentProductFeatureApplType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>ProductFeatureApplType</b> with (PRODUCT_FEATURE_APPL_TYPE_ID: <%=productFeatureApplType.getParentTypeId()%>)
    </div>
    <%if(productFeatureApplType.getParentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureApplType?" + "PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID=" + productFeatureApplType.getParentTypeId())%>" class="buttontext">[View ProductFeatureApplType]</a>      
    <%if(productFeatureApplTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureApplType?" + "PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID=" + productFeatureApplType.getParentTypeId())%>" class="buttontext">[Create ProductFeatureApplType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productFeatureApplTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductFeatureApplType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_APPL_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getProductFeatureApplTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productFeatureApplTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeatureApplType, type: one --%>
  

<%-- Start Relation for ProductFeatureApplType, type: many --%>
<%if(productFeatureApplType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductFeatureApplTypeHelper.findByParentTypeId(productFeatureApplType.getProductFeatureApplTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productFeatureApplType.getChildProductFeatureApplTypes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Child</b> Related Entities: <b>ProductFeatureApplType</b> with (PARENT_TYPE_ID: <%=productFeatureApplType.getProductFeatureApplTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureApplType?" + "PRODUCT_FEATURE_APPL_TYPE_PARENT_TYPE_ID=" + productFeatureApplType.getProductFeatureApplTypeId())%>" class="buttontext">[Create ProductFeatureApplType]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productFeatureApplType.getProductFeatureApplTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductFeatureApplType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductFeatureApplType]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_FEATURE_APPL_TYPE_ID</nobr></b></div></td>
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
        ProductFeatureApplType productFeatureApplTypeRelated = (ProductFeatureApplType)relatedIterator.next();
        if(productFeatureApplTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getProductFeatureApplTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getParentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getHasTable())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productFeatureApplTypeRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureApplType?" + "PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID=" + productFeatureApplTypeRelated.getProductFeatureApplTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureApplType?" + "PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID=" + productFeatureApplTypeRelated.getProductFeatureApplTypeId() + "&" + "PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID=" + productFeatureApplTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No ProductFeatureApplTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeatureApplType, type: many --%>
  

<%-- Start Relation for ProductFeatureAppl, type: many --%>
<%if(productFeatureApplType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductFeatureApplHelper.findByProductFeatureApplTypeId(productFeatureApplType.getProductFeatureApplTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productFeatureApplType.getProductFeatureAppls());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductFeatureAppl</b> with (PRODUCT_FEATURE_APPL_TYPE_ID: <%=productFeatureApplType.getProductFeatureApplTypeId()%>)
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
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureAppl?" + "PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_APPL_TYPE_ID=" + productFeatureApplType.getProductFeatureApplTypeId())%>" class="buttontext">[Create ProductFeatureAppl]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductFeatureApplTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productFeatureApplType.getProductFeatureApplTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductFeatureApplType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductFeatureAppl]</a>
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
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureAppl?" + "PRODUCT_FEATURE_APPL_PRODUCT_ID=" + productFeatureApplRelated.getProductId() + "&" + "PRODUCT_FEATURE_APPL_PRODUCT_FEATURE_ID=" + productFeatureApplRelated.getProductFeatureId() + "&" + "PRODUCT_FEATURE_APPL_TYPE_PRODUCT_FEATURE_APPL_TYPE_ID=" + productFeatureApplTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PRODUCT_FEATURE_APPL_TYPE_ADMIN, or PRODUCT_FEATURE_APPL_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
