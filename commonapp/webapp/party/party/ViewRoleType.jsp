
<%
/**
 *  Title: Role Type Entity
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
 *@created    Fri Jul 27 01:37:04 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewRoleType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("ROLE_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("ROLE_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("ROLE_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("ROLE_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String roleTypeId = request.getParameter("ROLE_TYPE_ROLE_TYPE_ID");  


  RoleType roleType = RoleTypeHelper.findByPrimaryKey(roleTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View RoleType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit RoleType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: RoleType with (ROLE_TYPE_ID: <%=roleTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindRoleType")%>" class="buttontext">[Find RoleType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewRoleType")%>" class="buttontext">[Create New RoleType]</a>
<%}%>
<%if(roleType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateRoleType?UPDATE_MODE=DELETE&" + "ROLE_TYPE_ROLE_TYPE_ID=" + roleTypeId)%>" class="buttontext">[Delete this RoleType]</a>
  <%}%>
<%}%>

<%if(roleType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(roleType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified RoleType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ROLE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(roleType.getRoleTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(roleType.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(roleType.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(roleType.getDescription())%>
    </td>
  </tr>

<%} //end if roleType == null %>
</table>
  </div>
<%RoleType roleTypeSave = roleType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(roleType == null && (roleTypeId != null)){%>
    RoleType with (ROLE_TYPE_ID: <%=roleTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    roleType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateRoleType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(roleType == null){%>
  <%if(hasCreatePermission){%>
    You may create a RoleType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>ROLE_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="ROLE_TYPE_ROLE_TYPE_ID" value="<%=UtilFormatOut.checkNull(roleTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a RoleType (ROLE_TYPE_ADMIN, or ROLE_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="ROLE_TYPE_ROLE_TYPE_ID" value="<%=roleTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>ROLE_TYPE_ID</td>
      <td>
        <b><%=roleTypeId%></b> (This cannot be changed without re-creating the roleType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a RoleType (ROLE_TYPE_ADMIN, or ROLE_TYPE_UPDATE needed).
  <%}%>
<%} //end if roleType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>PARENT_TYPE_ID</td>
    <td>
      <input class='editInputBox' type="text" size="20" maxlength="20" name="ROLE_TYPE_PARENT_TYPE_ID" value="<%if(roleType!=null){%><%=UtilFormatOut.checkNull(roleType.getParentTypeId())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("ROLE_TYPE_PARENT_TYPE_ID"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>HAS_TABLE</td>
    <td>
      <input class='editInputBox' type="text" size="1" maxlength="1" name="ROLE_TYPE_HAS_TABLE" value="<%if(roleType!=null){%><%=UtilFormatOut.checkNull(roleType.getHasTable())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("ROLE_TYPE_HAS_TABLE"))%><%}%>">
    </td>
  </tr>
  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="ROLE_TYPE_DESCRIPTION" value="<%if(roleType!=null){%><%=UtilFormatOut.checkNull(roleType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("ROLE_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && roleType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the roleType for cases when removed to retain passed form values --%>
<%roleType = roleTypeSave;%>

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
<%if(roleType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("ROLE_TYPE", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Parent RoleType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("ROLE_TYPE", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Child RoleType</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("ROLE_TYPE_ATTR", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> RoleTypeAttr</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("PARTY_ROLE", "_VIEW", session)){%>
      <td id=tab4 class=offtab>
        <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> PartyRole</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for RoleType, type: one --%>
<%if(roleType != null){%>
  <%if(Security.hasEntityPermission("ROLE_TYPE", "_VIEW", session)){%>
    <%-- RoleType roleTypeRelated = RoleTypeHelper.findByPrimaryKey(roleType.getParentTypeId()); --%>
    <%RoleType roleTypeRelated = roleType.getParentRoleType();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>RoleType</b> with (ROLE_TYPE_ID: <%=roleType.getParentTypeId()%>)
    </div>
    <%if(roleType.getParentTypeId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewRoleType?" + "ROLE_TYPE_ROLE_TYPE_ID=" + roleType.getParentTypeId())%>" class="buttontext">[View RoleType]</a>      
    <%if(roleTypeRelated == null){%>
      <%if(Security.hasEntityPermission("ROLE_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewRoleType?" + "ROLE_TYPE_ROLE_TYPE_ID=" + roleType.getParentTypeId())%>" class="buttontext">[Create RoleType]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(roleTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified RoleType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>ROLE_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(roleTypeRelated.getRoleTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(roleTypeRelated.getParentTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
      <%=UtilFormatOut.checkNull(roleTypeRelated.getHasTable())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(roleTypeRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if roleTypeRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for RoleType, type: one --%>
  

<%-- Start Relation for RoleType, type: many --%>
<%if(roleType != null){%>
  <%if(Security.hasEntityPermission("ROLE_TYPE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(RoleTypeHelper.findByParentTypeId(roleType.getRoleTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(roleType.getChildRoleTypes());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Child</b> Related Entities: <b>RoleType</b> with (PARENT_TYPE_ID: <%=roleType.getRoleTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("ROLE_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("ROLE_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("ROLE_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewRoleType?" + "ROLE_TYPE_PARENT_TYPE_ID=" + roleType.getRoleTypeId())%>" class="buttontext">[Create RoleType]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + roleType.getRoleTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindRoleType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find RoleType]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>ROLE_TYPE_ID</nobr></b></div></td>
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
        RoleType roleTypeRelated = (RoleType)relatedIterator.next();
        if(roleTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(roleTypeRelated.getRoleTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(roleTypeRelated.getParentTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(roleTypeRelated.getHasTable())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(roleTypeRelated.getDescription())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewRoleType?" + "ROLE_TYPE_ROLE_TYPE_ID=" + roleTypeRelated.getRoleTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateRoleType?" + "ROLE_TYPE_ROLE_TYPE_ID=" + roleTypeRelated.getRoleTypeId() + "&" + "ROLE_TYPE_ROLE_TYPE_ID=" + roleTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="6">
<h3>No RoleTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for RoleType, type: many --%>
  

<%-- Start Relation for RoleTypeAttr, type: many --%>
<%if(roleType != null){%>
  <%if(Security.hasEntityPermission("ROLE_TYPE_ATTR", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(RoleTypeAttrHelper.findByRoleTypeId(roleType.getRoleTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(roleType.getRoleTypeAttrs());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>RoleTypeAttr</b> with (ROLE_TYPE_ID: <%=roleType.getRoleTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("ROLE_TYPE_ATTR", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("ROLE_TYPE_ATTR", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("ROLE_TYPE_ATTR", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewRoleTypeAttr?" + "ROLE_TYPE_ATTR_ROLE_TYPE_ID=" + roleType.getRoleTypeId())%>" class="buttontext">[Create RoleTypeAttr]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=RoleTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + roleType.getRoleTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindRoleType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find RoleTypeAttr]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>ROLE_TYPE_ID</nobr></b></div></td>
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
        RoleTypeAttr roleTypeAttrRelated = (RoleTypeAttr)relatedIterator.next();
        if(roleTypeAttrRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(roleTypeAttrRelated.getRoleTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(roleTypeAttrRelated.getName())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewRoleTypeAttr?" + "ROLE_TYPE_ATTR_ROLE_TYPE_ID=" + roleTypeAttrRelated.getRoleTypeId() + "&" + "ROLE_TYPE_ATTR_NAME=" + roleTypeAttrRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateRoleTypeAttr?" + "ROLE_TYPE_ATTR_ROLE_TYPE_ID=" + roleTypeAttrRelated.getRoleTypeId() + "&" + "ROLE_TYPE_ATTR_NAME=" + roleTypeAttrRelated.getName() + "&" + "ROLE_TYPE_ROLE_TYPE_ID=" + roleTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No RoleTypeAttrs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for RoleTypeAttr, type: many --%>
  

<%-- Start Relation for PartyRole, type: many --%>
<%if(roleType != null){%>
  <%if(Security.hasEntityPermission("PARTY_ROLE", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(PartyRoleHelper.findByRoleTypeId(roleType.getRoleTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(roleType.getPartyRoles());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PartyRole</b> with (ROLE_TYPE_ID: <%=roleType.getRoleTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_ROLE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_ROLE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_ROLE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewPartyRole?" + "PARTY_ROLE_ROLE_TYPE_ID=" + roleType.getRoleTypeId())%>" class="buttontext">[Create PartyRole]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=RoleTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + roleType.getRoleTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindRoleType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyRole]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>ROLE_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_ROLE_ID</nobr></b></div></td>
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
        PartyRole partyRoleRelated = (PartyRole)relatedIterator.next();
        if(partyRoleRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyRoleRelated.getPartyId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyRoleRelated.getRoleTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(partyRoleRelated.getPartyRoleId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewPartyRole?" + "PARTY_ROLE_PARTY_ID=" + partyRoleRelated.getPartyId() + "&" + "PARTY_ROLE_ROLE_TYPE_ID=" + partyRoleRelated.getRoleTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdatePartyRole?" + "PARTY_ROLE_PARTY_ID=" + partyRoleRelated.getPartyId() + "&" + "PARTY_ROLE_ROLE_TYPE_ID=" + partyRoleRelated.getRoleTypeId() + "&" + "ROLE_TYPE_ROLE_TYPE_ID=" + roleTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No PartyRoles Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyRole, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (ROLE_TYPE_ADMIN, or ROLE_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
