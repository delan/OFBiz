
<%
/**
 *  Title: Security Component - User Login Security Group Entity
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
 *@created    Fri Jul 27 01:37:25 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.security.securitygroup.*" %>

<%@ page import="org.ofbiz.commonapp.security.login.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewUserLoginSecurityGroup"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String userLoginId = request.getParameter("USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID");  
  String groupId = request.getParameter("USER_LOGIN_SECURITY_GROUP_GROUP_ID");  


  UserLoginSecurityGroup userLoginSecurityGroup = UserLoginSecurityGroupHelper.findByPrimaryKey(userLoginId, groupId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View UserLoginSecurityGroup</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit UserLoginSecurityGroup</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: UserLoginSecurityGroup with (USER_LOGIN_ID, GROUP_ID: <%=userLoginId%>, <%=groupId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindUserLoginSecurityGroup")%>" class="buttontext">[Find UserLoginSecurityGroup]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewUserLoginSecurityGroup")%>" class="buttontext">[Create New UserLoginSecurityGroup]</a>
<%}%>
<%if(userLoginSecurityGroup != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateUserLoginSecurityGroup?UPDATE_MODE=DELETE&" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginId + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId)%>" class="buttontext">[Delete this UserLoginSecurityGroup]</a>
  <%}%>
<%}%>

<%if(userLoginSecurityGroup == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(userLoginSecurityGroup == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified UserLoginSecurityGroup was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>USER_LOGIN_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLoginSecurityGroup.getUserLoginId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>GROUP_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLoginSecurityGroup.getGroupId())%>
    </td>
  </tr>

<%} //end if userLoginSecurityGroup == null %>
</table>
  </div>
<%UserLoginSecurityGroup userLoginSecurityGroupSave = userLoginSecurityGroup;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(userLoginSecurityGroup == null && (userLoginId != null || groupId != null)){%>
    UserLoginSecurityGroup with (USER_LOGIN_ID, GROUP_ID: <%=userLoginId%>, <%=groupId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    userLoginSecurityGroup = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateUserLoginSecurityGroup")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(userLoginSecurityGroup == null){%>
  <%if(hasCreatePermission){%>
    You may create a UserLoginSecurityGroup by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>USER_LOGIN_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID" value="<%=UtilFormatOut.checkNull(userLoginId)%>">
      </td>
    </tr>
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GROUP_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="USER_LOGIN_SECURITY_GROUP_GROUP_ID" value="<%=UtilFormatOut.checkNull(groupId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a UserLoginSecurityGroup (USER_LOGIN_SECURITY_GROUP_ADMIN, or USER_LOGIN_SECURITY_GROUP_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID" value="<%=userLoginId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>USER_LOGIN_ID</td>
      <td>
        <b><%=userLoginId%></b> (This cannot be changed without re-creating the userLoginSecurityGroup.)
      </td>
    </tr>
      <input type="hidden" name="USER_LOGIN_SECURITY_GROUP_GROUP_ID" value="<%=groupId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>GROUP_ID</td>
      <td>
        <b><%=groupId%></b> (This cannot be changed without re-creating the userLoginSecurityGroup.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a UserLoginSecurityGroup (USER_LOGIN_SECURITY_GROUP_ADMIN, or USER_LOGIN_SECURITY_GROUP_UPDATE needed).
  <%}%>
<%} //end if userLoginSecurityGroup == null %>

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
<%if((hasUpdatePermission || hasCreatePermission) && userLoginSecurityGroup == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the userLoginSecurityGroup for cases when removed to retain passed form values --%>
<%userLoginSecurityGroup = userLoginSecurityGroupSave;%>

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
<%if(userLoginSecurityGroup != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("USER_LOGIN", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> UserLogin</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("SECURITY_GROUP", "_VIEW", session)){%>
      <td id=tab2 class=offtab>
        <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> SecurityGroup</a>
      </td>
    <%}%>
    <%if(Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_VIEW", session)){%>
      <td id=tab3 class=offtab>
        <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk> SecurityGroupPermission</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for UserLogin, type: one --%>
<%if(userLoginSecurityGroup != null){%>
  <%if(Security.hasEntityPermission("USER_LOGIN", "_VIEW", session)){%>
    <%-- UserLogin userLoginRelated = UserLoginHelper.findByPrimaryKey(userLoginSecurityGroup.getUserLoginId()); --%>
    <%UserLogin userLoginRelated = userLoginSecurityGroup.getUserLogin();%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>UserLogin</b> with (USER_LOGIN_ID: <%=userLoginSecurityGroup.getUserLoginId()%>)
    </div>
    <%if(userLoginSecurityGroup.getUserLoginId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewUserLogin?" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginSecurityGroup.getUserLoginId())%>" class="buttontext">[View UserLogin]</a>      
    <%if(userLoginRelated == null){%>
      <%if(Security.hasEntityPermission("USER_LOGIN", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewUserLogin?" + "USER_LOGIN_USER_LOGIN_ID=" + userLoginSecurityGroup.getUserLoginId())%>" class="buttontext">[Create UserLogin]</a>
      <%}%>
    <%}%>
    <%}%>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(userLoginRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified UserLogin was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>USER_LOGIN_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLoginRelated.getUserLoginId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLoginRelated.getPartyId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTACT_MECHANISM_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLoginRelated.getContactMechanismId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CURRENT_USER_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLoginRelated.getCurrentUserId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CURRENT_PASSWORD</b></td>
    <td>
      <%=UtilFormatOut.checkNull(userLoginRelated.getCurrentPassword())%>
    </td>
  </tr>

    <%} //end if userLoginRelated == null %>
    </table>
    </div>
  </div>
  <%}%>
<%}%>
<%-- End Relation for UserLogin, type: one --%>
  

<%-- Start Relation for SecurityGroup, type: one --%>
<%if(userLoginSecurityGroup != null){%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP", "_VIEW", session)){%>
    <%-- SecurityGroup securityGroupRelated = SecurityGroupHelper.findByPrimaryKey(userLoginSecurityGroup.getGroupId()); --%>
    <%SecurityGroup securityGroupRelated = userLoginSecurityGroup.getSecurityGroup();%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>SecurityGroup</b> with (GROUP_ID: <%=userLoginSecurityGroup.getGroupId()%>)
    </div>
    <%if(userLoginSecurityGroup.getGroupId() != null){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewSecurityGroup?" + "SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroup.getGroupId())%>" class="buttontext">[View SecurityGroup]</a>      
    <%if(securityGroupRelated == null){%>
      <%if(Security.hasEntityPermission("SECURITY_GROUP", "_CREATE", session)){%>
        <a href="<%=response.encodeURL(controlPath + "/ViewSecurityGroup?" + "SECURITY_GROUP_GROUP_ID=" + userLoginSecurityGroup.getGroupId())%>" class="buttontext">[Create SecurityGroup]</a>
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
  

<%-- Start Relation for SecurityGroupPermission, type: many --%>
<%if(userLoginSecurityGroup != null){%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(SecurityGroupPermissionHelper.findByGroupId(userLoginSecurityGroup.getGroupId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(userLoginSecurityGroup.getSecurityGroupPermissions());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>SecurityGroupPermission</b> with (GROUP_ID: <%=userLoginSecurityGroup.getGroupId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewSecurityGroupPermission?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + userLoginSecurityGroup.getGroupId())%>" class="buttontext">[Create SecurityGroupPermission]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=GroupId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + userLoginSecurityGroup.getGroupId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindUserLoginSecurityGroup?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find SecurityGroupPermission]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>GROUP_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PERMISSION_ID</nobr></b></div></td>
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
        SecurityGroupPermission securityGroupPermissionRelated = (SecurityGroupPermission)relatedIterator.next();
        if(securityGroupPermissionRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(securityGroupPermissionRelated.getGroupId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(securityGroupPermissionRelated.getPermissionId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewSecurityGroupPermission?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermissionRelated.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermissionRelated.getPermissionId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateSecurityGroupPermission?" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermissionRelated.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermissionRelated.getPermissionId() + "&" + "USER_LOGIN_SECURITY_GROUP_USER_LOGIN_ID=" + userLoginId + "&" + "USER_LOGIN_SECURITY_GROUP_GROUP_ID=" + groupId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="4">
<h3>No SecurityGroupPermissions Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for SecurityGroupPermission, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (USER_LOGIN_SECURITY_GROUP_ADMIN, or USER_LOGIN_SECURITY_GROUP_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
