
<%
/**
 *  Title: Product Association Type Entity
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
 *@created    Fri Jul 27 01:37:10 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.product.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewProductAssocType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productAssocTypeId = request.getParameter("PRODUCT_ASSOC_TYPE_PRODUCT_ASSOC_TYPE_ID");  


  ProductAssocType productAssocType = ProductAssocTypeHelper.findByPrimaryKey(productAssocTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ProductAssocType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ProductAssocType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ProductAssocType with (PRODUCT_ASSOC_TYPE_ID: <%=productAssocTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProductAssocType")%>" class="buttontext">[Find ProductAssocType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductAssocType")%>" class="buttontext">[Create New ProductAssocType]</a>
<%}%>
<%if(productAssocType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProductAssocType?UPDATE_MODE=DELETE&" + "PRODUCT_ASSOC_TYPE_PRODUCT_ASSOC_TYPE_ID=" + productAssocTypeId)%>" class="buttontext">[Delete this ProductAssocType]</a>
  <%}%>
<%}%>

<%if(productAssocType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(productAssocType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ProductAssocType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ASSOC_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productAssocType.getProductAssocTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productAssocType.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productAssocType.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productAssocType.getDescription())%>
    </td>
  </tr>

<%} //end if productAssocType == null %>
</table>
  </div>
<%ProductAssocType productAssocTypeSave = productAssocType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(productAssocType == null && (productAssocTypeId != null)){%>
    ProductAssocType with (PRODUCT_ASSOC_TYPE_ID: <%=productAssocTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    productAssocType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProductAssocType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(productAssocType == null){%>
  <%if(hasCreatePermission){%>
    You may create a ProductAssocType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_ASSOC_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_ASSOC_TYPE_PRODUCT_ASSOC_TYPE_ID" value="<%=UtilFormatOut.checkNull(productAssocTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ProductAssocType (PRODUCT_ASSOC_TYPE_ADMIN, or PRODUCT_ASSOC_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_ASSOC_TYPE_PRODUCT_ASSOC_TYPE_ID" value="<%=productAssocTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_ASSOC_TYPE_ID</td>
      <td>
        <b><%=productAssocTypeId%></b> (This cannot be changed without re-creating the productAssocType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ProductAssocType (PRODUCT_ASSOC_TYPE_ADMIN, or PRODUCT_ASSOC_TYPE_UPDATE needed).
  <%}%>
<%} //end if productAssocType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_ASSOC_TYPE_PARENT_TYPE_ID" value="<%if(productAssocType!=null){%><%=UtilFormatOut.checkNull(productAssocType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_ASSOC_TYPE_PARENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>HAS_TABLE</td>
    <td>
      <input class='editInputBox' type="text" size="1" maxlength="1" name="PRODUCT_ASSOC_TYPE_HAS_TABLE" value="<%if(productAssocType!=null){%><%=UtilFormatOut.checkNull(productAssocType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_ASSOC_TYPE_HAS_TABLE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="PRODUCT_ASSOC_TYPE_DESCRIPTION" value="<%if(productAssocType!=null){%><%=UtilFormatOut.checkNull(productAssocType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_ASSOC_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && productAssocType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the productAssocType for cases when removed to retain passed form values --%>
<%productAssocType = productAssocTypeSave;%>

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
<%if(productAssocType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Parent ProductAssocType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Child ProductAssocType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_ASSOC", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> ProductAssoc</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductAssocType, type: one --%>
<%if(productAssocType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_VIEW", session)){%>
    <%-- ProductAssocType productAssocTypeRelated = ProductAssocTypeHelper.findByPrimaryKey(productAssocType.getParentTypeId()); --%>
    <%ProductAssocType productAssocTypeRelated = productAssocType.getParentProductAssocType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>ProductAssocType</b> with (PRODUCT_ASSOC_TYPE_ID: <%=productAssocType.getParentTypeId()%>)
    </div>
    <%if(productAssocType.getParentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductAssocType?" + "PRODUCT_ASSOC_TYPE_PRODUCT_ASSOC_TYPE_ID=" + productAssocType.getParentTypeId())%>" class="buttontext">[View ProductAssocType]</a>      
    <%if(productAssocTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductAssocType?" + "PRODUCT_ASSOC_TYPE_PRODUCT_ASSOC_TYPE_ID=" + productAssocType.getParentTypeId())%>" class="buttontext">[Create ProductAssocType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productAssocTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductAssocType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ASSOC_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productAssocTypeRelated.getProductAssocTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productAssocTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productAssocTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productAssocTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if productAssocTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductAssocType, type: one --%>
  

<%-- Start Relation for ProductAssocType, type: many --%>
<%if(productAssocType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductAssocTypeHelper.findByParentTypeId(productAssocType.getProductAssocTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productAssocType.getChildProductAssocTypes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Child</b> Related Entities: <b>ProductAssocType</b> with (PARENT_TYPE_ID: <%=productAssocType.getProductAssocTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductAssocType?" + "PRODUCT_ASSOC_TYPE_PARENT_TYPE_ID=" + productAssocType.getProductAssocTypeId())%>" class="buttontext">[Create ProductAssocType]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productAssocType.getProductAssocTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductAssocType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductAssocType]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_ASSOC_TYPE_ID</nobr></b></div></td>
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
        ProductAssocType productAssocTypeRelated = (ProductAssocType)relatedIterator.next();
        if(productAssocTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocTypeRelated.getProductAssocTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocTypeRelated.getParentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocTypeRelated.getHasTable())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocTypeRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductAssocType?" + "PRODUCT_ASSOC_TYPE_PRODUCT_ASSOC_TYPE_ID=" + productAssocTypeRelated.getProductAssocTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductAssocType?" + "PRODUCT_ASSOC_TYPE_PRODUCT_ASSOC_TYPE_ID=" + productAssocTypeRelated.getProductAssocTypeId() + "&" + "PRODUCT_ASSOC_TYPE_PRODUCT_ASSOC_TYPE_ID=" + productAssocTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No ProductAssocTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductAssocType, type: many --%>
  

<%-- Start Relation for ProductAssoc, type: many --%>
<%if(productAssocType != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_ASSOC", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ProductAssocHelper.findByProductAssocTypeId(productAssocType.getProductAssocTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(productAssocType.getProductAssocs());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>ProductAssoc</b> with (PRODUCT_ASSOC_TYPE_ID: <%=productAssocType.getProductAssocTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PRODUCT_ASSOC", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PRODUCT_ASSOC", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PRODUCT_ASSOC", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductAssoc?" + "PRODUCT_ASSOC_PRODUCT_ASSOC_TYPE_ID=" + productAssocType.getProductAssocTypeId())%>" class="buttontext">[Create ProductAssoc]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ProductAssocTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + productAssocType.getProductAssocTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindProductAssocType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find ProductAssoc]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID_TO</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ASSOC_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>REASON</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>QUANTITY</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>INSTRUCTION</nobr></b></div></td>
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
        ProductAssoc productAssocRelated = (ProductAssoc)relatedIterator.next();
        if(productAssocRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getProductIdTo())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getProductAssocTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%{
        String dateString = null;
        String timeString = null;
        if(productAssocRelated != null)
        {
          java.util.Date date = productAssocRelated.getFromDate();
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
        if(productAssocRelated != null)
        {
          java.util.Date date = productAssocRelated.getThruDate();
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
      <%=UtilFormatOut.checkNull(productAssocRelated.getReason())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.formatQuantity(productAssocRelated.getQuantity())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(productAssocRelated.getInstruction())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductAssoc?" + "PRODUCT_ASSOC_PRODUCT_ID=" + productAssocRelated.getProductId() + "&" + "PRODUCT_ASSOC_PRODUCT_ID_TO=" + productAssocRelated.getProductIdTo() + "&" + "PRODUCT_ASSOC_PRODUCT_ASSOC_TYPE_ID=" + productAssocRelated.getProductAssocTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateProductAssoc?" + "PRODUCT_ASSOC_PRODUCT_ID=" + productAssocRelated.getProductId() + "&" + "PRODUCT_ASSOC_PRODUCT_ID_TO=" + productAssocRelated.getProductIdTo() + "&" + "PRODUCT_ASSOC_PRODUCT_ASSOC_TYPE_ID=" + productAssocRelated.getProductAssocTypeId() + "&" + "PRODUCT_ASSOC_TYPE_PRODUCT_ASSOC_TYPE_ID=" + productAssocTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="10">
<h3>No ProductAssocs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductAssoc, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PRODUCT_ASSOC_TYPE_ADMIN, or PRODUCT_ASSOC_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
