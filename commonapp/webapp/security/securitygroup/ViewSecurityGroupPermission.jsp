
<%
/**
 *  Title: Security Component - Security Group Permission Entity
 *  Description: Defines a permission available to a security group
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
 *@created    Wed Jul 18 08:43:50 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.security.securitygroup.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewSecurityGroupPermission"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String groupId = request.getParameter("SECURITY_GROUP_PERMISSION_GROUP_ID");  
  String permissionId = request.getParameter("SECURITY_GROUP_PERMISSION_PERMISSION_ID");  


  SecurityGroupPermission securityGroupPermission = SecurityGroupPermissionHelper.findByPrimaryKey(groupId, permissionId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View SecurityGroupPermission</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit SecurityGroupPermission</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: SecurityGroupPermission with (GROUP_ID, PERMISSION_ID: <%=groupId%>, <%=permissionId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindSecurityGroupPermission")%>" class="buttontext">[Find SecurityGroupPermission]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewSecurityGroupPermission")%>" class="buttontext">[Create New SecurityGroupPermission]</a>
<%}%>
<%if(securityGroupPermission != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateSecurityGroupPermission?UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + groupId + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + permissionId)%>" class="buttontext">[Delete this SecurityGroupPermission]</a>
  <%}%>
<%}%>

<%if(securityGroupPermission == null){%>
<div style='width:100%;height:400px;overflow:visible;border-style:inset;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(securityGroupPermission == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified SecurityGroupPermission was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GROUP_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(securityGroupPermission.getGroupId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PERMISSION_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(securityGroupPermission.getPermissionId())%>
    </td>
  </tr>

<%} //end if securityGroupPermission == null %>
</table>
  </div>
<%SecurityGroupPermission securityGroupPermissionSave = securityGroupPermission;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(securityGroupPermission == null && (groupId != null || permissionId != null)){%>
    SecurityGroupPermission with (GROUP_ID, PERMISSION_ID: <%=groupId%>, <%=permissionId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    securityGroupPermission = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateSecurityGroupPermission")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(securityGroupPermission == null){%>
  <%if(hasCreatePermission){%>
    You may create a SecurityGroupPermission by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GROUP_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="SECURITY_GROUP_PERMISSION_GROUP_ID" value="<%=UtilFormatOut.checkNull(groupId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PERMISSION_ID</td>
      <td>
        <input class='editInputBox' type="text" size="60" maxlength="60" name="SECURITY_GROUP_PERMISSION_PERMISSION_ID" value="<%=UtilFormatOut.checkNull(permissionId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a SecurityGroupPermission (SECURITY_GROUP_PERMISSION_ADMIN, or SECURITY_GROUP_PERMISSION_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="SECURITY_GROUP_PERMISSION_GROUP_ID" value="<%=groupId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GROUP_ID</td>
      <td>
        <b><%=groupId%></b> (This cannot be changed without re-creating the securityGroupPermission.)
      </td>
    </tr>
      <input type="hidden" name="SECURITY_GROUP_PERMISSION_PERMISSION_ID" value="<%=permissionId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>PERMISSION_ID</td>
      <td>
        <b><%=permissionId%></b> (This cannot be changed without re-creating the securityGroupPermission.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a SecurityGroupPermission (SECURITY_GROUP_PERMISSION_ADMIN, or SECURITY_GROUP_PERMISSION_UPDATE needed).
  <%}%>
<%} //end if securityGroupPermission == null %>

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
<%if((hasUpdatePermission || hasCreatePermission) && securityGroupPermission == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the securityGroupPermission for cases when removed to retain passed form values --%>
<%securityGroupPermission = securityGroupPermissionSave;%>

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
<%if(securityGroupPermission != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("SECURITY_GROUP", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> SecurityGroup</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("SECURITY_PERMISSION", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> SecurityPermission</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for SecurityGroup, type: one --%>
<%if(securityGroupPermission != null){%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP", "_VIEW", session)){%>
    <%SecurityGroup securityGroupRelated = SecurityGroupHelper.findByPrimaryKey(securityGroupPermission.getGroupId());%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>SecurityGroup</b> with (GROUP_ID: <%=securityGroupPermission.getGroupId()%>)
    </div>
    <%if(securityGroupPermission.getGroupId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewSecurityGroup?" + "SECURITY_GROUP_GROUP_ID=" + securityGroupPermission.getGroupId())%>" class="buttontext">[View SecurityGroup]</a>      
    <%if(securityGroupRelated == null){%>
      <%if(Security.hasEntityPermission("SECURITY_GROUP", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewSecurityGroup?" + "SECURITY_GROUP_GROUP_ID=" + securityGroupPermission.getGroupId())%>" class="buttontext">[Create SecurityGroup]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(securityGroupRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified SecurityGroup was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GROUP_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(securityGroupRelated.getGroupId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(securityGroupRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if securityGroupRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for SecurityGroup, type: one --%>
  

<%-- Start Relation for SecurityPermission, type: one --%>
<%if(securityGroupPermission != null){%>
  <%if(Security.hasEntityPermission("SECURITY_PERMISSION", "_VIEW", session)){%>
    <%SecurityPermission securityPermissionRelated = SecurityPermissionHelper.findByPrimaryKey(securityGroupPermission.getPermissionId());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>SecurityPermission</b> with (PERMISSION_ID: <%=securityGroupPermission.getPermissionId()%>)
    </div>
    <%if(securityGroupPermission.getPermissionId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewSecurityPermission?" + "SECURITY_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[View SecurityPermission]</a>      
    <%if(securityPermissionRelated == null){%>
      <%if(Security.hasEntityPermission("SECURITY_PERMISSION", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewSecurityPermission?" + "SECURITY_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Create SecurityPermission]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(securityPermissionRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified SecurityPermission was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PERMISSION_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(securityPermissionRelated.getPermissionId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(securityPermissionRelated.getDescription())%>
    </td>
  </tr>

    <%} //end if securityPermissionRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for SecurityPermission, type: one --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (SECURITY_GROUP_PERMISSION_ADMIN, or SECURITY_GROUP_PERMISSION_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
