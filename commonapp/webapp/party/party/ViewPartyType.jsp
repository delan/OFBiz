
<%
/**
 *  Title: Party Type Entity
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
 *@created    Fri Jul 06 16:51:32 MDT 2001
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.webevent.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>
<%@ page import="org.ofbiz.commonapp.party.party.*" %>


<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<%pageContext.setAttribute("PageName", "EditPartyType"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_TYPE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_TYPE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_TYPE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String partyTypeId = request.getParameter("PARTY_TYPE_PARTY_TYPE_ID");  

  

  PartyType partyType = PartyTypeHelper.findByPrimaryKey(partyTypeId);
%>

<br>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: PartyType with (PARTY_TYPE_ID: <%=partyTypeId%>).</b>
</div>

<a href="<%=response.encodeURL("FindPartyType.jsp")%>" class="buttontext">[Find PartyType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyType.jsp")%>" class="buttontext">[Create PartyType]</a>
<%}%>
<%if(partyType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyType.jsp?WEBEVENT=UPDATE_PARTY_TYPE&UPDATE_MODE=DELETE&" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Delete this PartyType]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(partyTypeId != null){%>
    <a href="<%=response.encodeURL("EditPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Edit PartyType]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(partyType == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified PartyType was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_TYPE_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyType.getPartyTypeId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyType.getParentTypeId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyType.getHasTable())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyType.getDescription())%>
    
    </td>
  </tr>

<%} //end if partyType == null %>
</table>

<a href="<%=response.encodeURL("FindPartyType.jsp")%>" class="buttontext">[Find PartyType]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyType.jsp")%>" class="buttontext">[Create PartyType]</a>
<%}%>
<%if(partyType != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyType.jsp?WEBEVENT=UPDATE_PARTY_TYPE&UPDATE_MODE=DELETE&" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Delete this PartyType]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(partyTypeId != null){%>
    <a href="<%=response.encodeURL("EditPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Edit PartyType]</a>
  <%}%>
<%}%>
<br>
<br>
<SCRIPT language='JavaScript'>  
var numTabs=5;
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
<%if(partyType != null){%>
<table cellpadding='0' cellspacing='0'><tr>

  
    <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
    <td id=tab1 class=ontab>
      <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk>Parent PartyType</a>
    </td>
    <%}%>

  
    <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
    <td id=tab2 class=offtab>
      <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk>Children PartyType</a>
    </td>
    <%}%>

  
    <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
    <td id=tab3 class=offtab>
      <a href='javascript:ShowTab("tab3")' id=lnk3 class=offlnk>Sibling PartyType</a>
    </td>
    <%}%>

  
    <%if(Security.hasEntityPermission("PARTY_TYPE_ATTR", "_VIEW", session)){%>
    <td id=tab4 class=offtab>
      <a href='javascript:ShowTab("tab4")' id=lnk4 class=offlnk> PartyTypeAttr</a>
    </td>
    <%}%>

  
    <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION", "_VIEW", session)){%>
    <td id=tab5 class=offtab>
      <a href='javascript:ShowTab("tab5")' id=lnk5 class=offlnk> PartyClassification</a>
    </td>
    <%}%>

</tr></table>
<%}%>
  

  
  
  
<%-- Start Relation for PartyType, type: one --%>
<%if(partyType != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
    <%PartyType partyTypeRelated = PartyTypeHelper.findByPrimaryKey(partyType.getParentTypeId());%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b>Parent</b> Related Entity: <b>PartyType</b> with (PARTY_TYPE_ID: <%=partyType.getParentTypeId()%>)
    </div>
    <%if(partyType.getParentTypeId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/party/party/ViewPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyType.getParentTypeId())%>" class="buttontext">[View PartyType Details]</a>
      
    <%if(partyTypeRelated != null){%>
      <%if(Security.hasEntityPermission("PARTY_TYPE", "_EDIT", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyType.getParentTypeId())%>" class="buttontext">[Edit PartyType]</a>
      <%}%>
    <%}else{%>
      <%if(Security.hasEntityPermission("PARTY_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyType.getParentTypeId())%>" class="buttontext">[Create PartyType]</a>
      <%}%>
    <%}%>
    <%}%>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyTypeRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified PartyType was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_TYPE_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getPartyTypeId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getParentTypeId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>HAS_TABLE</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getHasTable())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>DESCRIPTION</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getDescription())%>
    
    </td>
  </tr>

    <%} //end if partyTypeRelated == null %>
    </table>
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyType, type: one --%>
  

  
  
  
<%-- Start Relation for PartyType, type: many --%>
<%if(partyType != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>    
    <%Iterator relatedIterator = PartyTypeHelper.findByParentTypeIdIterator(partyType.getPartyTypeId());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Children</b> Related Entities: <b>PartyType</b> with (PARENT_TYPE_ID: <%=partyType.getPartyTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyType.jsp?" + "PARTY_TYPE_PARENT_TYPE_ID=" + partyType.getPartyTypeId())%>" class="buttontext">[Create PartyType]</a>
    <%}%>
    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyType.getPartyTypeId();%>
    <a href="<%=response.encodeURL("/commonapp/party/party/FindPartyType.jsp?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyType]</a>

  <div style='width:100%;height:250px;overflow:scroll;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARENT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>HAS_TABLE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>DESCRIPTION</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(relatedUpdatePerm){%>
        <td>&nbsp;</td>
      <%}%>
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
        PartyType partyTypeRelated = (PartyType)relatedIterator.next();
        if(partyTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getPartyTypeId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getParentTypeId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getHasTable())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getDescription())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL("/commonapp/party/party/ViewPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeRelated.getPartyTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedUpdatePerm){%>
        <td>
          <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeRelated.getPartyTypeId())%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(relatedDeletePerm){%>
        <td>
          <%-- <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="<%=response.encodeURL("ViewPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeRelated.getPartyTypeId() + "&" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId + "&WEBEVENT=UPDATE_PARTY_TYPE&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="8">
<h3>No PartyTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyType, type: many --%>
  

  
  
  
<%-- Start Relation for PartyType, type: many --%>
<%if(partyType != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>    
    <%Iterator relatedIterator = PartyTypeHelper.findByParentTypeIdIterator(partyType.getParentTypeId());%>
  <DIV id=area3 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b>Sibling</b> Related Entities: <b>PartyType</b> with (PARENT_TYPE_ID: <%=partyType.getParentTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_TYPE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_TYPE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_TYPE", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyType.jsp?" + "PARTY_TYPE_PARENT_TYPE_ID=" + partyType.getParentTypeId())%>" class="buttontext">[Create PartyType]</a>
    <%}%>
    
    <%String curFindString = "SEARCH_TYPE=ParentTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyType.getParentTypeId();%>
    <a href="<%=response.encodeURL("/commonapp/party/party/FindPartyType.jsp?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyType]</a>

  <div style='width:100%;height:250px;overflow:scroll;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARENT_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>HAS_TABLE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>DESCRIPTION</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(relatedUpdatePerm){%>
        <td>&nbsp;</td>
      <%}%>
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
        PartyType partyTypeRelated = (PartyType)relatedIterator.next();
        if(partyTypeRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getPartyTypeId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getParentTypeId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getHasTable())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getDescription())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL("/commonapp/party/party/ViewPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeRelated.getPartyTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedUpdatePerm){%>
        <td>
          <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeRelated.getPartyTypeId())%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(relatedDeletePerm){%>
        <td>
          <%-- <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="<%=response.encodeURL("ViewPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeRelated.getPartyTypeId() + "&" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId + "&WEBEVENT=UPDATE_PARTY_TYPE&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="8">
<h3>No PartyTypes Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyType, type: many --%>
  

  
  
  
<%-- Start Relation for PartyTypeAttr, type: many --%>
<%if(partyType != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE_ATTR", "_VIEW", session)){%>    
    <%Iterator relatedIterator = PartyTypeAttrHelper.findByPartyTypeIdIterator(partyType.getPartyTypeId());%>
  <DIV id=area4 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PartyTypeAttr</b> with (PARTY_TYPE_ID: <%=partyType.getPartyTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_TYPE_ATTR", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_TYPE_ATTR", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_TYPE_ATTR", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyTypeAttr.jsp?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyType.getPartyTypeId())%>" class="buttontext">[Create PartyTypeAttr]</a>
    <%}%>
    
    <%String curFindString = "SEARCH_TYPE=PartyTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyType.getPartyTypeId();%>
    <a href="<%=response.encodeURL("/commonapp/party/party/FindPartyType.jsp?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyTypeAttr]</a>

  <div style='width:100%;height:250px;overflow:scroll;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>NAME</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(relatedUpdatePerm){%>
        <td>&nbsp;</td>
      <%}%>
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
        PartyTypeAttr partyTypeAttrRelated = (PartyTypeAttr)relatedIterator.next();
        if(partyTypeAttrRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyTypeAttrRelated.getPartyTypeId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyTypeAttrRelated.getName())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL("/commonapp/party/party/ViewPartyTypeAttr.jsp?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyTypeAttrRelated.getPartyTypeId() + "&" + "PARTY_TYPE_ATTR_NAME=" + partyTypeAttrRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedUpdatePerm){%>
        <td>
          <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyTypeAttr.jsp?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyTypeAttrRelated.getPartyTypeId() + "&" + "PARTY_TYPE_ATTR_NAME=" + partyTypeAttrRelated.getName())%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(relatedDeletePerm){%>
        <td>
          <%-- <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="<%=response.encodeURL("ViewPartyTypeAttr.jsp?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyTypeAttrRelated.getPartyTypeId() + "&" + "PARTY_TYPE_ATTR_NAME=" + partyTypeAttrRelated.getName() + "&" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId + "&WEBEVENT=UPDATE_PARTY_TYPE_ATTR&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="8">
<h3>No PartyTypeAttrs Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyTypeAttr, type: many --%>
  

  
  
  
<%-- Start Relation for PartyClassification, type: many --%>
<%if(partyType != null){%>
  <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION", "_VIEW", session)){%>    
    <%Iterator relatedIterator = PartyClassificationHelper.findByPartyTypeIdIterator(partyType.getPartyTypeId());%>
  <DIV id=area5 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PartyClassification</b> with (PARTY_TYPE_ID: <%=partyType.getPartyTypeId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_CLASSIFICATION", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_CLASSIFICATION", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_CLASSIFICATION", "_DELETE", session);%>
    <%
      String rowClassResultHeader = "viewManyHeaderTR";
      String rowClassResult1 = "viewManyTR1";
      String rowClassResult2 = "viewManyTR2"; 
      String rowClassResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyClassification.jsp?" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyType.getPartyTypeId())%>" class="buttontext">[Create PartyClassification]</a>
    <%}%>
    
    <%String curFindString = "SEARCH_TYPE=PartyTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyType.getPartyTypeId();%>
    <a href="<%=response.encodeURL("/commonapp/party/party/FindPartyType.jsp?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyClassification]</a>

  <div style='width:100%;height:250px;overflow:scroll;'>
  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr class="<%=rowClassResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>PARTY_CLASSIFICATION_TYPE_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>FROM_DATE</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>THRU_DATE</nobr></b></div></td>
      <td>&nbsp;</td>
      <%if(relatedUpdatePerm){%>
        <td>&nbsp;</td>
      <%}%>
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
        PartyClassification partyClassificationRelated = (PartyClassification)relatedIterator.next();
        if(partyClassificationRelated != null)
        {
    %>
    <%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyClassificationRelated.getPartyId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyClassificationRelated.getPartyTypeId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyClassificationRelated.getPartyClassificationTypeId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%{
        String dateString = null;
        String timeString = null;
        if(partyClassificationRelated != null)
        {
          java.util.Date date = partyClassificationRelated.getFromDate();
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
        if(partyClassificationRelated != null)
        {
          java.util.Date date = partyClassificationRelated.getThruDate();
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
        <a href="<%=response.encodeURL("/commonapp/party/party/ViewPartyClassification.jsp?" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyClassificationRelated.getPartyId() + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyClassificationRelated.getPartyTypeId())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedUpdatePerm){%>
        <td>
          <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyClassification.jsp?" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyClassificationRelated.getPartyId() + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyClassificationRelated.getPartyTypeId())%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(relatedDeletePerm){%>
        <td>
          <%-- <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="<%=response.encodeURL("ViewPartyClassification.jsp?" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyClassificationRelated.getPartyId() + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyClassificationRelated.getPartyTypeId() + "&" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyTypeId + "&WEBEVENT=UPDATE_PARTY_CLASSIFICATION&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowClassResult=(rowClassResult==rowClassResult1?rowClassResult2:rowClassResult1);%><tr class="<%=rowClassResult%>">
<td colspan="8">
<h3>No PartyClassifications Found.</h3>
</td>
</tr>
<%}%>
    </table>
  </div>
Displaying <%=relatedLoopCount%> entities.
  </div>
  <%}%>
<%}%>
<%-- End Relation for PartyClassification, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_TYPE_ADMIN, or PARTY_TYPE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
