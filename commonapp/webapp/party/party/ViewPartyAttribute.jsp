
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
 *@created    Wed Jul 04 01:03:18 MDT 2001
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
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String partyId = request.getParameter("PARTY_ATTRIBUTE_PARTY_ID");  
  String name = request.getParameter("PARTY_ATTRIBUTE_NAME");  

  
  

  PartyAttribute partyAttribute = PartyAttributeHelper.findByPrimaryKey(partyId, name);
%>

<br>
<div style='color:yellow;width:100%;background-color:#330033;padding:3;'>
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
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified PartyAttribute was not found.</h3></td></tr>
<%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARTY_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyAttribute.getPartyId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>NAME</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyAttribute.getName())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
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

  
  
  
<%-- Start Relation for Party, type: one --%>
<%if(partyAttribute != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%Party partyRelated = PartyHelper.findByPrimaryKey(partyAttribute.getPartyId());%>
    <br>
    <div style='color:yellow;width:100%;background-color:#660066;padding:2;'>
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
    <tr bgcolor="<%=rowColor1%>"><td><h3>Specified Party was not found.</h3></td></tr>
    <%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARTY_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyRelated.getPartyId())%>
    
    </td>
  </tr>

    <%} //end if partyRelated == null %>
    </table>
  <%}%>
<%}%>
<%-- End Relation for Party, type: one --%>
  

  
  
  
<%-- Start Relation for PartyTypeAttr, type: many --%>
<%if(partyAttribute != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE_ATTR", "_VIEW", session)){%>    
    <%Iterator relatedIterator = PartyTypeAttrHelper.findByNameIterator(partyAttribute.getName());%>
    <br>
    <div style='color:yellow;width:100%;background-color:#660066;padding:2;'>
      <b></b> Related Entities: <b>PartyTypeAttr</b> with (NAME: <%=partyAttribute.getName()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_TYPE_ATTR", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_TYPE_ATTR", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_TYPE_ATTR", "_DELETE", session);%>
    <%
      String rowColorResultHeader = "99CCFF";
      String rowColorResult1 = "99FFCC";
      String rowColorResult2 = "CCFFCC"; 
      String rowColorResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyTypeAttr.jsp?" + "PARTY_TYPE_ATTR_NAME=" + partyAttribute.getName())%>" class="buttontext">[Create PartyTypeAttr]</a>
    <%}%>
    
    <%String curFindString = "SEARCH_TYPE=Name";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyAttribute.getName();%>
    <a href="<%=response.encodeURL("/commonapp/party/party/FindPartyAttribute.jsp?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyTypeAttr]</a>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr bgcolor="<%=rowColorResultHeader%>">
  
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
     if(relatedIterator != null && relatedIterator.hasNext())
     {
      int relatedLoopCount = 0;
      while(relatedIterator != null && relatedIterator.hasNext())
      {
        relatedLoopCount++; if(relatedLoopCount > 10) break;
        PartyTypeAttr partyTypeAttrRelated = (PartyTypeAttr)relatedIterator.next();
        if(partyTypeAttrRelated != null)
        {
    %>
    <%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
  
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
<%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
<td colspan="8">
<h3>No PartyTypeAttrs Found.</h3>
</td>
</tr>
<%}%>
</table>
  <%}%>
<%}%>
<%-- End Relation for PartyTypeAttr, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_ATTRIBUTE_ADMIN, or PARTY_ATTRIBUTE_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
