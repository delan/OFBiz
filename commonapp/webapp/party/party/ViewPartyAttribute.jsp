
<%
/**
 *  Title: Party Attribute Entity
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

<%pageContext.setAttribute("PageName", "EditPartyAttribute"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PARTY_ATTRIBUTE", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_ATTRIBUTE", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_ATTRIBUTE", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_ATTRIBUTE", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowClass1 = "viewOneTR1";
  String rowClass2 = "viewOneTR2";
  String rowClass = "";

  String partyId = request.getParameter("PARTY_ATTRIBUTE_PARTY_ID");  
  String name = request.getParameter("PARTY_ATTRIBUTE_NAME");  

  
  

  PartyAttribute partyAttribute = PartyAttributeHelper.findByPrimaryKey(partyId, name);
%>

<br>
<div style='color: white; width: 100%; background-color: black; padding:3;'>
  <b>View Entity: PartyAttribute with (PARTY_ID, NAME: <%=partyId%>, <%=name%>).</b>
</div>

<a href="<%=response.encodeURL("FindPartyAttribute.jsp")%>" class="buttontext">[Find PartyAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyAttribute.jsp")%>" class="buttontext">[Create PartyAttribute]</a>
<%}%>
<%if(partyAttribute != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyAttribute.jsp?WEBEVENT=UPDATE_PARTY_ATTRIBUTE&UPDATE_MODE=DELETE&" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyId + "&" + "PARTY_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Delete this PartyAttribute]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(partyId != null && name != null){%>
    <a href="<%=response.encodeURL("EditPartyAttribute.jsp?" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyId + "&" + "PARTY_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Edit PartyAttribute]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(partyAttribute == null){%>
<tr class="<%=rowClass1%>"><td><h3>Specified PartyAttribute was not found.</h3></td></tr>
<%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyAttribute.getPartyId())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>NAME</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyAttribute.getName())%>
    
    </td>
  </tr>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>VALUE</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyAttribute.getValue())%>
    
    </td>
  </tr>

<%} //end if partyAttribute == null %>
</table>

<a href="<%=response.encodeURL("FindPartyAttribute.jsp")%>" class="buttontext">[Find PartyAttribute]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyAttribute.jsp")%>" class="buttontext">[Create PartyAttribute]</a>
<%}%>
<%if(partyAttribute != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyAttribute.jsp?WEBEVENT=UPDATE_PARTY_ATTRIBUTE&UPDATE_MODE=DELETE&" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyId + "&" + "PARTY_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Delete this PartyAttribute]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(partyId != null && name != null){%>
    <a href="<%=response.encodeURL("EditPartyAttribute.jsp?" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyId + "&" + "PARTY_ATTRIBUTE_NAME=" + name)%>" class="buttontext">[Edit PartyAttribute]</a>
  <%}%>
<%}%>
<br>
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
<%if(partyAttribute != null){%>
<table cellpadding='0' cellspacing='0'><tr>

  
    <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <td id=tab1 class=ontab>
      <a href='javascript:ShowTab("tab1")' id=lnk1 class=onlnk> Party</a>
    </td>
    <%}%>

  
    <%if(Security.hasEntityPermission("PARTY_TYPE_ATTR", "_VIEW", session)){%>
    <td id=tab2 class=offtab>
      <a href='javascript:ShowTab("tab2")' id=lnk2 class=offlnk> PartyTypeAttr</a>
    </td>
    <%}%>

</tr></table>
<%}%>
  

  
  
  
<%-- Start Relation for Party, type: one --%>
<%if(partyAttribute != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%Party partyRelated = PartyHelper.findByPrimaryKey(partyAttribute.getPartyId());%>
  <DIV id=area1 style="VISIBILITY: visible; POSITION: absolute" width="100%">
    <div class=areaheader>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=partyAttribute.getPartyId()%>)
    </div>
    <%if(partyAttribute.getPartyId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/party/party/ViewParty.jsp?" + "PARTY_PARTY_ID=" + partyAttribute.getPartyId())%>" class="buttontext">[View Party Details]</a>
      
    <%if(partyRelated != null){%>
      <%if(Security.hasEntityPermission("PARTY", "_EDIT", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditParty.jsp?" + "PARTY_PARTY_ID=" + partyAttribute.getPartyId())%>" class="buttontext">[Edit Party]</a>
      <%}%>
    <%}else{%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditParty.jsp?" + "PARTY_PARTY_ID=" + partyAttribute.getPartyId())%>" class="buttontext">[Create Party]</a>
      <%}%>
    <%}%>
    <%}%>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyRelated == null){%>
    <tr class="<%=rowClass1%>"><td><h3>Specified Party was not found.</h3></td></tr>
    <%}else{%>

  <%rowClass=(rowClass==rowClass1?rowClass2:rowClass1);%>
  <tr class="<%=rowClass%>">
    <td><b>PARTY_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyRelated.getPartyId())%>
    
    </td>
  </tr>

    <%} //end if partyRelated == null %>
    </table>
  </div>
  <%}%>
<%}%>
<%-- End Relation for Party, type: one --%>
  

  
  
  
<%-- Start Relation for PartyTypeAttr, type: many --%>
<%if(partyAttribute != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE_ATTR", "_VIEW", session)){%>    
    <%Iterator relatedIterator = PartyTypeAttrHelper.findByNameIterator(partyAttribute.getName());%>
  <DIV id=area2 style="VISIBILITY: hidden; POSITION: absolute" width="100%">
    <div class=areaheader>
      <b></b> Related Entities: <b>PartyTypeAttr</b> with (NAME: <%=partyAttribute.getName()%>)
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
      <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyTypeAttr.jsp?" + "PARTY_TYPE_ATTR_NAME=" + partyAttribute.getName())%>" class="buttontext">[Create PartyTypeAttr]</a>
    <%}%>
    
    <%String curFindString = "SEARCH_TYPE=Name";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyAttribute.getName();%>
    <a href="<%=response.encodeURL("/commonapp/party/party/FindPartyAttribute.jsp?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyTypeAttr]</a>

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
          <a href="<%=response.encodeURL("ViewPartyTypeAttr.jsp?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyTypeAttrRelated.getPartyTypeId() + "&" + "PARTY_TYPE_ATTR_NAME=" + partyTypeAttrRelated.getName() + "&" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyId + "&" + "PARTY_ATTRIBUTE_NAME=" + name + "&WEBEVENT=UPDATE_PARTY_TYPE_ATTR&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_ATTRIBUTE_ADMIN, or PARTY_ATTRIBUTE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
