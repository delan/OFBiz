
<%
/**
 *  Title: Good Identification Type Entity
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
<%pageContext.setAttribute("PageName", "ViewGoodIdentificationType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String goodIdentificationTypeId = request.getParameter("GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID");  


  GoodIdentificationType goodIdentificationType = GoodIdentificationTypeHelper.findByPrimaryKey(goodIdentificationTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View GoodIdentificationType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit GoodIdentificationType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: GoodIdentificationType with (GOOD_IDENTIFICATION_TYPE_ID: <%=goodIdentificationTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindGoodIdentificationType")%>" class="buttontext">[Find GoodIdentificationType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentificationType")%>" class="buttontext">[Create New GoodIdentificationType]</a>
<%}%>
<%if(goodIdentificationType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateGoodIdentificationType?UPDATE_MODE=DELETE&" + "GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationTypeId)%>" class="buttontext">[Delete this GoodIdentificationType]</a>
  <%}%>
<%}%>

<%if(goodIdentificationType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(goodIdentificationType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified GoodIdentificationType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GOOD_IDENTIFICATION_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationType.getGoodIdentificationTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationType.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationType.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationType.getDescription())%>
    </td>
  </tr>

<%} //end if goodIdentificationType == null %>
</table>
  </div>
<%GoodIdentificationType goodIdentificationTypeSave = goodIdentificationType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(goodIdentificationType == null && (goodIdentificationTypeId != null)){%>
    GoodIdentificationType with (GOOD_IDENTIFICATION_TYPE_ID: <%=goodIdentificationTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    goodIdentificationType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateGoodIdentificationType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(goodIdentificationType == null){%>
  <%if(hasCreatePermission){%>
    You may create a GoodIdentificationType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GOOD_IDENTIFICATION_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID" value="<%=UtilFormatOut.checkNull(goodIdentificationTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a GoodIdentificationType (GOOD_IDENTIFICATION_TYPE_ADMIN, or GOOD_IDENTIFICATION_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID" value="<%=goodIdentificationTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GOOD_IDENTIFICATION_TYPE_ID</td>
      <td>
        <b><%=goodIdentificationTypeId%></b> (This cannot be changed without re-creating the goodIdentificationType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a GoodIdentificationType (GOOD_IDENTIFICATION_TYPE_ADMIN, or GOOD_IDENTIFICATION_TYPE_UPDATE needed).
  <%}%>
<%} //end if goodIdentificationType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="GOOD_IDENTIFICATION_TYPE_PARENT_TYPE_ID" value="<%if(goodIdentificationType!=null){%><%=UtilFormatOut.checkNull(goodIdentificationType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("GOOD_IDENTIFICATION_TYPE_PARENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>HAS_TABLE</td>
    <td>
      <input class='editInputBox' type="text" size="1" maxlength="1" name="GOOD_IDENTIFICATION_TYPE_HAS_TABLE" value="<%if(goodIdentificationType!=null){%><%=UtilFormatOut.checkNull(goodIdentificationType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("GOOD_IDENTIFICATION_TYPE_HAS_TABLE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="GOOD_IDENTIFICATION_TYPE_DESCRIPTION" value="<%if(goodIdentificationType!=null){%><%=UtilFormatOut.checkNull(goodIdentificationType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("GOOD_IDENTIFICATION_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && goodIdentificationType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the goodIdentificationType for cases when removed to retain passed form values --%>
<%goodIdentificationType = goodIdentificationTypeSave;%>

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
<%if(goodIdentificationType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Parent GoodIdentificationType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Child GoodIdentificationType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> GoodIdentification</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for GoodIdentificationType, type: one --%>
<%if(goodIdentificationType != null){%>
  <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_VIEW", session)){%>
    <%-- GoodIdentificationType goodIdentificationTypeRelated = GoodIdentificationTypeHelper.findByPrimaryKey(goodIdentificationType.getParentTypeId()); --%>
    <%GoodIdentificationType goodIdentificationTypeRelated = goodIdentificationType.getParentGoodIdentificationType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>GoodIdentificationType</b> with (GOOD_IDENTIFICATION_TYPE_ID: <%=goodIdentificationType.getParentTypeId()%>)
    </div>
    <%if(goodIdentificationType.getParentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentificationType?" + "GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationType.getParentTypeId())%>" class="buttontext">[View GoodIdentificationType]</a>      
    <%if(goodIdentificationTypeRelated == null){%>
      <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentificationType?" + "GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationType.getParentTypeId())%>" class="buttontext">[Create GoodIdentificationType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(goodIdentificationTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified GoodIdentificationType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GOOD_IDENTIFICATION_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getGoodIdentificationTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if goodIdentificationTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for GoodIdentificationType, type: one --%>
  

<%-- Start Relation for GoodIdentificationType, type: many --%>
<%if(goodIdentificationType != null){%>
  <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(GoodIdentificationTypeHelper.findByParentTypeId(goodIdentificationType.getGoodIdentificationTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(goodIdentificationType.getChildGoodIdentificationTypes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Child</b> Related Entities: <b>GoodIdentificationType</b> with (PARENT_TYPE_ID: <%=goodIdentificationType.getGoodIdentificationTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentificationType?" + "GOOD_IDENTIFICATION_TYPE_PARENT_TYPE_ID=" + goodIdentificationType.getGoodIdentificationTypeId())%>" class="buttontext">[Create GoodIdentificationType]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + goodIdentificationType.getGoodIdentificationTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindGoodIdentificationType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find GoodIdentificationType]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>GOOD_IDENTIFICATION_TYPE_ID</nobr></b></div></td>
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
        GoodIdentificationType goodIdentificationTypeRelated = (GoodIdentificationType)relatedIterator.next();
        if(goodIdentificationTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getGoodIdentificationTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getParentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getHasTable())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(goodIdentificationTypeRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentificationType?" + "GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationTypeRelated.getGoodIdentificationTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateGoodIdentificationType?" + "GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationTypeRelated.getGoodIdentificationTypeId() + "&" + "GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No GoodIdentificationTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for GoodIdentificationType, type: many --%>
  

<%-- Start Relation for GoodIdentification, type: many --%>
<%if(goodIdentificationType != null){%>
  <%if(Security.hasEntityPermission("GOOD_IDENTIFICATION", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(GoodIdentificationHelper.findByGoodIdentificationTypeId(goodIdentificationType.getGoodIdentificationTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(goodIdentificationType.getGoodIdentifications());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>GoodIdentification</b> with (GOOD_IDENTIFICATION_TYPE_ID: <%=goodIdentificationType.getGoodIdentificationTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("GOOD_IDENTIFICATION", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("GOOD_IDENTIFICATION", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("GOOD_IDENTIFICATION", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentification?" + "GOOD_IDENTIFICATION_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationType.getGoodIdentificationTypeId())%>" class="buttontext">[Create GoodIdentification]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=GoodIdentificationTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + goodIdentificationType.getGoodIdentificationTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindGoodIdentificationType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find GoodIdentification]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>GOOD_IDENTIFICATION_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PRODUCT_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>ID_VALUE</nobr></b></div></td>
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
        GoodIdentification goodIdentificationRelated = (GoodIdentification)relatedIterator.next();
        if(goodIdentificationRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(goodIdentificationRelated.getGoodIdentificationTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(goodIdentificationRelated.getProductId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(goodIdentificationRelated.getIdValue())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentification?" + "GOOD_IDENTIFICATION_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationRelated.getGoodIdentificationTypeId() + "&" + "GOOD_IDENTIFICATION_PRODUCT_ID=" + goodIdentificationRelated.getProductId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateGoodIdentification?" + "GOOD_IDENTIFICATION_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationRelated.getGoodIdentificationTypeId() + "&" + "GOOD_IDENTIFICATION_PRODUCT_ID=" + goodIdentificationRelated.getProductId() + "&" + "GOOD_IDENTIFICATION_TYPE_GOOD_IDENTIFICATION_TYPE_ID=" + goodIdentificationTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No GoodIdentifications Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for GoodIdentification, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (GOOD_IDENTIFICATION_TYPE_ADMIN, or GOOD_IDENTIFICATION_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
