
<%
/**
 *  Title: Product Feature Interaction Entity
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
<%pageContext.setAttribute("PageName", "ViewProductFeatureIactn"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String productFeatureId = request.getParameter("PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID");  
  String productFeatureIdTo = request.getParameter("PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID_TO");  


  ProductFeatureIactn productFeatureIactn = ProductFeatureIactnHelper.findByPrimaryKey(productFeatureId, productFeatureIdTo);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ProductFeatureIactn</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ProductFeatureIactn</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ProductFeatureIactn with (PRODUCT_FEATURE_ID, PRODUCT_FEATURE_ID_TO: <%=productFeatureId%>, <%=productFeatureIdTo%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindProductFeatureIactn")%>" class="buttontext">[Find ProductFeatureIactn]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactn")%>" class="buttontext">[Create New ProductFeatureIactn]</a>
<%}%>
<%if(productFeatureIactn != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateProductFeatureIactn?UPDATE_MODE=DELETE&" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID=" + productFeatureId + "&" + "PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID_TO=" + productFeatureIdTo)%>" class="buttontext">[Delete this ProductFeatureIactn]</a>
  <%}%>
<%}%>

<%if(productFeatureIactn == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(productFeatureIactn == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ProductFeatureIactn was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactn.getProductFeatureId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_ID_TO</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactn.getProductFeatureIdTo())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_IACTN_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactn.getProductFeatureIactnTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureIactn.getProductId())%>
    </td>
  </tr>

<%} //end if productFeatureIactn == null %>
</table>
  </div>
<%ProductFeatureIactn productFeatureIactnSave = productFeatureIactn;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(productFeatureIactn == null && (productFeatureId != null || productFeatureIdTo != null)){%>
    ProductFeatureIactn with (PRODUCT_FEATURE_ID, PRODUCT_FEATURE_ID_TO: <%=productFeatureId%>, <%=productFeatureIdTo%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    productFeatureIactn = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateProductFeatureIactn")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(productFeatureIactn == null){%>
  <%if(hasCreatePermission){%>
    You may create a ProductFeatureIactn by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID" value="<%=UtilFormatOut.checkNull(productFeatureId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_ID_TO</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID_TO" value="<%=UtilFormatOut.checkNull(productFeatureIdTo)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ProductFeatureIactn (PRODUCT_FEATURE_IACTN_ADMIN, or PRODUCT_FEATURE_IACTN_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID" value="<%=productFeatureId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_ID</td>
      <td>
        <b><%=productFeatureId%></b> (This cannot be changed without re-creating the productFeatureIactn.)
      </td>
    </tr>
      <input type="hidden" name="PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_ID_TO" value="<%=productFeatureIdTo%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PRODUCT_FEATURE_ID_TO</td>
      <td>
        <b><%=productFeatureIdTo%></b> (This cannot be changed without re-creating the productFeatureIactn.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ProductFeatureIactn (PRODUCT_FEATURE_IACTN_ADMIN, or PRODUCT_FEATURE_IACTN_UPDATE needed).
  <%}%>
<%} //end if productFeatureIactn == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_FEATURE_IACTN_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_IACTN_TYPE_ID" value="<%if(productFeatureIactn!=null){%><%=UtilFormatOut.checkNull(productFeatureIactn.getProductFeatureIactnTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_IACTN_PRODUCT_FEATURE_IACTN_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PRODUCT_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="PRODUCT_FEATURE_IACTN_PRODUCT_ID" value="<%if(productFeatureIactn!=null){%><%=UtilFormatOut.checkNull(productFeatureIactn.getProductId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("PRODUCT_FEATURE_IACTN_PRODUCT_ID"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && productFeatureIactn == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the productFeatureIactn for cases when removed to retain passed form values --%>
<%productFeatureIactn = productFeatureIactnSave;%>

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
<%if(productFeatureIactn != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> ProductFeatureIactnType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Main ProductFeature</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk>Assoc ProductFeature</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for ProductFeatureIactnType, type: one --%>
<%if(productFeatureIactn != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_VIEW", session)){%>
    <%-- ProductFeatureIactnType productFeatureIactnTypeRelated = ProductFeatureIactnTypeHelper.findByPrimaryKey(productFeatureIactn.getProductFeatureIactnTypeId()); --%>
    <%ProductFeatureIactnType productFeatureIactnTypeRelated = productFeatureIactn.getProductFeatureIactnType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>ProductFeatureIactnType</b> with (PRODUCT_FEATURE_IACTN_TYPE_ID: <%=productFeatureIactn.getProductFeatureIactnTypeId()%>)
    </div>
    <%if(productFeatureIactn.getProductFeatureIactnTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactnType?" + "PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID=" + productFeatureIactn.getProductFeatureIactnTypeId())%>" class="buttontext">[View ProductFeatureIactnType]</a>      
    <%if(productFeatureIactnTypeRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactnType?" + "PRODUCT_FEATURE_IACTN_TYPE_PRODUCT_FEATURE_IACTN_TYPE_ID=" + productFeatureIactn.getProductFeatureIactnTypeId())%>" class="buttontext">[Create ProductFeatureIactnType]</a>
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
  

<%-- Start Relation for ProductFeature, type: one --%>
<%if(productFeatureIactn != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
    <%-- ProductFeature productFeatureRelated = ProductFeatureHelper.findByPrimaryKey(productFeatureIactn.getProductFeatureId()); --%>
    <%ProductFeature productFeatureRelated = productFeatureIactn.getMainProductFeature();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Main</b> Related Entity: <b>ProductFeature</b> with (PRODUCT_FEATURE_ID: <%=productFeatureIactn.getProductFeatureId()%>)
    </div>
    <%if(productFeatureIactn.getProductFeatureId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureIactn.getProductFeatureId())%>" class="buttontext">[View ProductFeature]</a>      
    <%if(productFeatureRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureIactn.getProductFeatureId())%>" class="buttontext">[Create ProductFeature]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productFeatureRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductFeature was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getDescription())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NUMBER_SPECIFIED</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(productFeatureRelated.getNumberSpecified())%>
    </td>
  </tr>

    <%} //end if productFeatureRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeature, type: one --%>
  

<%-- Start Relation for ProductFeature, type: one --%>
<%if(productFeatureIactn != null){%>
  <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
    <%-- ProductFeature productFeatureRelated = ProductFeatureHelper.findByPrimaryKey(productFeatureIactn.getProductFeatureIdTo()); --%>
    <%ProductFeature productFeatureRelated = productFeatureIactn.getAssocProductFeature();%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Assoc</b> Related Entity: <b>ProductFeature</b> with (PRODUCT_FEATURE_ID: <%=productFeatureIactn.getProductFeatureIdTo()%>)
    </div>
    <%if(productFeatureIactn.getProductFeatureIdTo() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureIactn.getProductFeatureIdTo())%>" class="buttontext">[View ProductFeature]</a>      
    <%if(productFeatureRelated == null){%>
      <%if(Security.hasEntityPermission("PRODUCT_FEATURE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature?" + "PRODUCT_FEATURE_PRODUCT_FEATURE_ID=" + productFeatureIactn.getProductFeatureIdTo())%>" class="buttontext">[Create ProductFeature]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(productFeatureRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified ProductFeature was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PRODUCT_FEATURE_CATEGORY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getProductFeatureCategoryId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getDescription())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>UOM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(productFeatureRelated.getUomId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NUMBER_SPECIFIED</b></td>
    <td>
      <%=UtilFormatOut.formatQuantity(productFeatureRelated.getNumberSpecified())%>
    </td>
  </tr>

    <%} //end if productFeatureRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for ProductFeature, type: one --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PRODUCT_FEATURE_IACTN_ADMIN, or PRODUCT_FEATURE_IACTN_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
