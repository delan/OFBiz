
<%
/**
 *  Title: Container Type Entity
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
 *@created    Fri Jul 27 01:37:21 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.product.storage.*" %>


<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<%pageContext.setAttribute("PageName", "ViewContainerType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("CONTAINER_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("CONTAINER_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("CONTAINER_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("CONTAINER_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String containerTypeId = request.getParameter("CONTAINER_TYPE_CONTAINER_TYPE_ID");  


  ContainerType containerType = ContainerTypeHelper.findByPrimaryKey(containerTypeId);
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
    <a href='javascript:ShowViewTab("view")' id=viewlnk class=onlnk>View ContainerType</a>
  </td>
  <%if(hasUpdatePermission || hasCreatePermission){%>
  <td id=edittab class=offtab>
    <a href='javascript:ShowViewTab("edit")' id=editlnk class=offlnk>Edit ContainerType</a>
  </td>
  <%}%>
</table>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: ContainerType with (CONTAINER_TYPE_ID: <%=containerTypeId%>).</b>
</div>

<a href="<%=response.encodeURL(controlPath + "/FindContainerType")%>" class="buttontext">[Find ContainerType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL(controlPath + "/ViewContainerType")%>" class="buttontext">[Create New ContainerType]</a>
<%}%>
<%if(containerType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL(controlPath + "/UpdateContainerType?UPDATE_MODE=DELETE&" + "CONTAINER_TYPE_CONTAINER_TYPE_ID=" + containerTypeId)%>" class="buttontext">[Delete this ContainerType]</a>
  <%}%>
<%}%>

<%if(containerType == null){%>
<div style='width:100%;height:400px;overflow:visible;'>
<%}else{%>
<div style='width:100%;height:200px;overflow:auto;border-style:inset;'>
<%}%>
  <DIV id=viewarea style="VISIBILITY: visible; POSITION: absolute" width="100%">
<table border="0" cellspacing="2" cellpadding="2">
<%if(containerType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified ContainerType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>CONTAINER_TYPE_ID</b></td>
    <td>
      <%=UtilFormatOut.checkNull(containerType.getContainerTypeId())%>
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
      <%=UtilFormatOut.checkNull(containerType.getDescription())%>
    </td>
  </tr>

<%} //end if containerType == null %>
</table>
  </div>
<%ContainerType containerTypeSave = containerType;%>
<%if(hasUpdatePermission || hasCreatePermission){%>
  <DIV id=editarea style="VISIBILITY: hidden; POSITION: absolute" width="100%">
<%boolean showFields = true;%>
<%if(containerType == null && (containerTypeId != null)){%>
    ContainerType with (CONTAINER_TYPE_ID: <%=containerTypeId%>) not found.<br>
<%}%>
<%
  String lastUpdateMode = request.getParameter("UPDATE_MODE");
  if((session.getAttribute("ERROR_MESSAGE") != null || request.getAttribute("ERROR_MESSAGE") != null) && 
      lastUpdateMode != null && !lastUpdateMode.equals("DELETE"))
  {
    //if we are updating and there is an error, don't use the EJB data for the fields, use parameters to get the old value
    containerType = null;
  }
%>
<form action="<%=response.encodeURL(controlPath + "/UpdateContainerType")%>" method="POST" name="updateForm" style="margin:0;">
  <input type="hidden" name="ON_ERROR_PAGE" value="<%=request.getServletPath()%>">
<table cellpadding="2" cellspacing="2" border="0">

<%if(containerType == null){%>
  <%if(hasCreatePermission){%>
    You may create a ContainerType by entering the values you want, and clicking Update.
    <input type="hidden" name="UPDATE_MODE" value="CREATE">
  
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>CONTAINER_TYPE_ID</td>
      <td>
        <input class='editInputBox' type="text" size="20" maxlength="20" name="CONTAINER_TYPE_CONTAINER_TYPE_ID" value="<%=UtilFormatOut.checkNull(containerTypeId)%>">
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to create a ContainerType (CONTAINER_TYPE_ADMIN, or CONTAINER_TYPE_CREATE needed).
  <%}%>
<%}else{%>
  <%if(hasUpdatePermission){%>
    <input type="hidden" name="UPDATE_MODE" value="UPDATE">
  
      <input type="hidden" name="CONTAINER_TYPE_CONTAINER_TYPE_ID" value="<%=containerTypeId%>">
    <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
      <td>CONTAINER_TYPE_ID</td>
      <td>
        <b><%=containerTypeId%></b> (This cannot be changed without re-creating the containerType.)
      </td>
    </tr>
  <%}else{%>
    <%showFields=false;%>
    You do not have permission to update a ContainerType (CONTAINER_TYPE_ADMIN, or CONTAINER_TYPE_UPDATE needed).
  <%}%>
<%} //end if containerType == null %>

<%if(showFields){%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%><tr class="<%=rowClass%>">
    <td>DESCRIPTION</td>
    <td>
      <input class='editInputBox' type="text" size="80" maxlength="255" name="CONTAINER_TYPE_DESCRIPTION" value="<%if(containerType!=null){%><%=UtilFormatOut.checkNull(containerType.getDescription())%><%}else{%><%=UtilFormatOut.checkNull(request.getParameter("CONTAINER_TYPE_DESCRIPTION"))%><%}%>">
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
<%if((hasUpdatePermission || hasCreatePermission) && containerType == null){%>
  <SCRIPT language='JavaScript'>  
    ShowViewTab("edit");
  </SCRIPT>
<%}%>
<%-- Restore the containerType for cases when removed to retain passed form values --%>
<%containerType = containerTypeSave;%>

<br>
<SCRIPT language='JavaScript'>  
var numTabs=1;
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
<%if(containerType != null){%>
<table cellpadding='0' cellspacing='0'><tr>
    <%if(Security.hasEntityPermission("CONTAINER", "_VIEW", session)){%>
      <td id=tab1 class=ontab>
        <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> Container</a>
      </td>
    <%}%>
</tr></table>
<%}%>
  

<%-- Start Relation for Container, type: many --%>
<%if(containerType != null){%>
  <%if(Security.hasEntityPermission("CONTAINER", "_VIEW", session)){%>    
    <%-- Iterator relatedIterator = UtilMisc.toIterator(ContainerHelper.findByContainerTypeId(containerType.getContainerTypeId())); --%>
    <%Iterator relatedIterator = UtilMisc.toIterator(containerType.getContainers());%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>Container</b> with (CONTAINER_TYPE_ID: <%=containerType.getContainerTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("CONTAINER", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("CONTAINER", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("CONTAINER", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL(controlPath + "/ViewContainer?" + "CONTAINER_CONTAINER_TYPE_ID=" + containerType.getContainerTypeId())%>" class="buttontext">[Create Container]</a>
    <%}%>    
    <%String curFindString = "SEARCH_TYPE=ContainerTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + containerType.getContainerTypeId();%>
    <a href="<%=response.encodeURL(controlPath + "/FindContainerType?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find Container]</a>
  <div style='width:100%;height:250px;overflow:auto;border-style:inset;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>CONTAINER_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>CONTAINER_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FACILITY_ID</nobr></b></div></td>
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
        Container containerRelated = (Container)relatedIterator.next();
        if(containerRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(containerRelated.getContainerId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(containerRelated.getContainerTypeId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
      <%=UtilFormatOut.checkNull(containerRelated.getFacilityId())%>
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL(controlPath + "/ViewContainer?" + "CONTAINER_CONTAINER_ID=" + containerRelated.getContainerId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedDeletePerm){%>
        <td>
          <a href="<%=response.encodeURL(controlPath + "/UpdateContainer?" + "CONTAINER_CONTAINER_ID=" + containerRelated.getContainerId() + "&" + "CONTAINER_TYPE_CONTAINER_TYPE_ID=" + containerTypeId + "&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="5">
<h3>No Containers Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for Container, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (CONTAINER_TYPE_ADMIN, or CONTAINER_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
