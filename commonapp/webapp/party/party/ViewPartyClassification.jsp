
<%
/**
 *  Title: Party Classification Entity
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
 *@created    Wed Jul 04 01:03:16 MDT 2001
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

<%pageContext.setAttribute("PageName", "EditPartyClassification"); %>

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>

<%boolean hasViewPermission=Security.hasEntityPermission("PARTY_CLASSIFICATION", "_VIEW", session);%>
<%boolean hasCreatePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION", "_CREATE", session);%>
<%boolean hasUpdatePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION", "_UPDATE", session);%>
<%boolean hasDeletePermission=Security.hasEntityPermission("PARTY_CLASSIFICATION", "_DELETE", session);%>
<%if(hasViewPermission){%>

<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";

  String partyId = request.getParameter("PARTY_CLASSIFICATION_PARTY_ID");  
  String partyTypeId = request.getParameter("PARTY_CLASSIFICATION_PARTY_TYPE_ID");  

  
  

  PartyClassification partyClassification = PartyClassificationHelper.findByPrimaryKey(partyId, partyTypeId);
%>

<br>
<div style='color:yellow;width:100%;background-color:#330033;padding:3;'>
  <b>View Entity: PartyClassification with (PARTY_ID, PARTY_TYPE_ID: <%=partyId%>, <%=partyTypeId%>).</b>
</div>

<a href="<%=response.encodeURL("FindPartyClassification.jsp")%>" class="buttontext">[Find PartyClassification]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyClassification.jsp")%>" class="buttontext">[Create PartyClassification]</a>
<%}%>
<%if(partyClassification != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyClassification.jsp?WEBEVENT=UPDATE_PARTY_CLASSIFICATION&UPDATE_MODE=DELETE&" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Delete this PartyClassification]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(partyId != null && partyTypeId != null){%>
    <a href="<%=response.encodeURL("EditPartyClassification.jsp?" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Edit PartyClassification]</a>
  <%}%>
<%}%>

<table border="0" cellspacing="2" cellpadding="2">
<%if(partyClassification == null){%>
<tr bgcolor="<%=rowColor1%>"><td><h3>Specified PartyClassification was not found.</h3></td></tr>
<%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARTY_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyClassification.getPartyId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARTY_TYPE_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyClassification.getPartyTypeId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARTY_CLASSIFICATION_TYPE_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyClassification.getPartyClassificationTypeId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>FROM_DATE</b></td>
    <td>
    
      <%{
        String dateString = null;
        String timeString = null;
        if(partyClassification != null)
        {
          java.util.Date date = partyClassification.getFromDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>THRU_DATE</b></td>
    <td>
    
      <%{
        String dateString = null;
        String timeString = null;
        if(partyClassification != null)
        {
          java.util.Date date = partyClassification.getThruDate();
          if(date  != null)
          {
            dateString = UtilDateTime.toDateString(date);
            timeString = UtilDateTime.toTimeString(date);
          }
        }
      %>
      <%=UtilFormatOut.checkNull(dateString)%>&nbsp;<%=UtilFormatOut.checkNull(timeString)%>
      <%}%>
    
    </td>
  </tr>

<%} //end if partyClassification == null %>
</table>

<a href="<%=response.encodeURL("FindPartyClassification.jsp")%>" class="buttontext">[Find PartyClassification]</a>
<%if(hasCreatePermission){%>
  <a href="<%=response.encodeURL("EditPartyClassification.jsp")%>" class="buttontext">[Create PartyClassification]</a>
<%}%>
<%if(partyClassification != null){%>
  <%if(hasDeletePermission){%>
    <a href="<%=response.encodeURL("EditPartyClassification.jsp?WEBEVENT=UPDATE_PARTY_CLASSIFICATION&UPDATE_MODE=DELETE&" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Delete this PartyClassification]</a>
  <%}%>
<%}%>
<%if(hasUpdatePermission){%>
  <%if(partyId != null && partyTypeId != null){%>
    <a href="<%=response.encodeURL("EditPartyClassification.jsp?" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId)%>" class="buttontext">[Edit PartyClassification]</a>
  <%}%>
<%}%>
<br>

  
  
  
<%-- Start Relation for Party, type: one --%>
<%if(partyClassification != null){%>
  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%Party partyRelated = PartyHelper.findByPrimaryKey(partyClassification.getPartyId());%>
    <br>
    <div style='color:yellow;width:100%;background-color:#660066;padding:2;'>
     <b></b> Related Entity: <b>Party</b> with (PARTY_ID: <%=partyClassification.getPartyId()%>)
    </div>
    <%if(partyClassification.getPartyId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/party/party/ViewParty.jsp?" + "PARTY_PARTY_ID=" + partyClassification.getPartyId())%>" class="buttontext">[View Party Details]</a>
      
    <%if(partyRelated != null){%>
      <%if(Security.hasEntityPermission("PARTY", "_EDIT", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditParty.jsp?" + "PARTY_PARTY_ID=" + partyClassification.getPartyId())%>" class="buttontext">[Edit Party]</a>
      <%}%>
    <%}else{%>
      <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditParty.jsp?" + "PARTY_PARTY_ID=" + partyClassification.getPartyId())%>" class="buttontext">[Create Party]</a>
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
  

  
  
  
<%-- Start Relation for PartyType, type: one --%>
<%if(partyClassification != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
    <%PartyType partyTypeRelated = PartyTypeHelper.findByPrimaryKey(partyClassification.getPartyTypeId());%>
    <br>
    <div style='color:yellow;width:100%;background-color:#660066;padding:2;'>
     <b></b> Related Entity: <b>PartyType</b> with (PARTY_TYPE_ID: <%=partyClassification.getPartyTypeId()%>)
    </div>
    <%if(partyClassification.getPartyTypeId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/party/party/ViewPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyClassification.getPartyTypeId())%>" class="buttontext">[View PartyType Details]</a>
      
    <%if(partyTypeRelated != null){%>
      <%if(Security.hasEntityPermission("PARTY_TYPE", "_EDIT", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyClassification.getPartyTypeId())%>" class="buttontext">[Edit PartyType]</a>
      <%}%>
    <%}else{%>
      <%if(Security.hasEntityPermission("PARTY_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyType.jsp?" + "PARTY_TYPE_PARTY_TYPE_ID=" + partyClassification.getPartyTypeId())%>" class="buttontext">[Create PartyType]</a>
      <%}%>
    <%}%>
    <%}%>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyTypeRelated == null){%>
    <tr bgcolor="<%=rowColor1%>"><td><h3>Specified PartyType was not found.</h3></td></tr>
    <%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARTY_TYPE_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getPartyTypeId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getParentTypeId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>HAS_TABLE</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getHasTable())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>DESCRIPTION</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyTypeRelated.getDescription())%>
    
    </td>
  </tr>

    <%} //end if partyTypeRelated == null %>
    </table>
  <%}%>
<%}%>
<%-- End Relation for PartyType, type: one --%>
  

  
  
  
<%-- Start Relation for PartyClassificationType, type: one --%>
<%if(partyClassification != null){%>
  <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_VIEW", session)){%>
    <%PartyClassificationType partyClassificationTypeRelated = PartyClassificationTypeHelper.findByPrimaryKey(partyClassification.getPartyClassificationTypeId());%>
    <br>
    <div style='color:yellow;width:100%;background-color:#660066;padding:2;'>
     <b></b> Related Entity: <b>PartyClassificationType</b> with (PARTY_CLASSIFICATION_TYPE_ID: <%=partyClassification.getPartyClassificationTypeId()%>)
    </div>
    <%if(partyClassification.getPartyClassificationTypeId() != null){%>
      
      <a href="<%=response.encodeURL("/commonapp/party/party/ViewPartyClassificationType.jsp?" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassification.getPartyClassificationTypeId())%>" class="buttontext">[View PartyClassificationType Details]</a>
      
    <%if(partyClassificationTypeRelated != null){%>
      <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_EDIT", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyClassificationType.jsp?" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassification.getPartyClassificationTypeId())%>" class="buttontext">[Edit PartyClassificationType]</a>
      <%}%>
    <%}else{%>
      <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_CREATE", session)){%>
        <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyClassificationType.jsp?" + "PARTY_CLASSIFICATION_TYPE_PARTY_CLASSIFICATION_TYPE_ID=" + partyClassification.getPartyClassificationTypeId())%>" class="buttontext">[Create PartyClassificationType]</a>
      <%}%>
    <%}%>
    <%}%>
    <table border="0" cellspacing="2" cellpadding="2">
    <%if(partyClassificationTypeRelated == null){%>
    <tr bgcolor="<%=rowColor1%>"><td><h3>Specified PartyClassificationType was not found.</h3></td></tr>
    <%}else{%>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARTY_CLASSIFICATION_TYPE_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getPartyClassificationTypeId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>PARENT_TYPE_ID</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getParentTypeId())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>HAS_TABLE</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getHasTable())%>
    
    </td>
  </tr>

  <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%>
  <tr bgcolor="<%=rowColor%>">
    <td><b>DESCRIPTION</b></td>
    <td>
    
      <%=UtilFormatOut.checkNull(partyClassificationTypeRelated.getDescription())%>
    
    </td>
  </tr>

    <%} //end if partyClassificationTypeRelated == null %>
    </table>
  <%}%>
<%}%>
<%-- End Relation for PartyClassificationType, type: one --%>
  

  
  
  
<%-- Start Relation for PartyTypeAttr, type: many --%>
<%if(partyClassification != null){%>
  <%if(Security.hasEntityPermission("PARTY_TYPE_ATTR", "_VIEW", session)){%>    
    <%Iterator relatedIterator = PartyTypeAttrHelper.findByPartyTypeIdIterator(partyClassification.getPartyTypeId());%>
    <br>
    <div style='color:yellow;width:100%;background-color:#660066;padding:2;'>
      <b></b> Related Entities: <b>PartyTypeAttr</b> with (PARTY_TYPE_ID: <%=partyClassification.getPartyTypeId()%>)
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
      <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyTypeAttr.jsp?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyClassification.getPartyTypeId())%>" class="buttontext">[Create PartyTypeAttr]</a>
    <%}%>
    
    <%String curFindString = "SEARCH_TYPE=PartyTypeId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyClassification.getPartyTypeId();%>
    <a href="<%=response.encodeURL("/commonapp/party/party/FindPartyClassification.jsp?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyTypeAttr]</a>

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
          <a href="<%=response.encodeURL("ViewPartyTypeAttr.jsp?" + "PARTY_TYPE_ATTR_PARTY_TYPE_ID=" + partyTypeAttrRelated.getPartyTypeId() + "&" + "PARTY_TYPE_ATTR_NAME=" + partyTypeAttrRelated.getName() + "&" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId + "&WEBEVENT=UPDATE_PARTY_TYPE_ATTR&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
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
  

  
  
  
<%-- Start Relation for PartyAttribute, type: many --%>
<%if(partyClassification != null){%>
  <%if(Security.hasEntityPermission("PARTY_ATTRIBUTE", "_VIEW", session)){%>    
    <%Iterator relatedIterator = PartyAttributeHelper.findByPartyIdIterator(partyClassification.getPartyId());%>
    <br>
    <div style='color:yellow;width:100%;background-color:#660066;padding:2;'>
      <b></b> Related Entities: <b>PartyAttribute</b> with (PARTY_ID: <%=partyClassification.getPartyId()%>)
    </div>
    <%boolean relatedCreatePerm = Security.hasEntityPermission("PARTY_ATTRIBUTE", "_CREATE", session);%>
    <%boolean relatedUpdatePerm = Security.hasEntityPermission("PARTY_ATTRIBUTE", "_UPDATE", session);%>
    <%boolean relatedDeletePerm = Security.hasEntityPermission("PARTY_ATTRIBUTE", "_DELETE", session);%>
    <%
      String rowColorResultHeader = "99CCFF";
      String rowColorResult1 = "99FFCC";
      String rowColorResult2 = "CCFFCC"; 
      String rowColorResult = "";
    %>
      
    <%if(relatedCreatePerm){%>
      <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyAttribute.jsp?" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyClassification.getPartyId())%>" class="buttontext">[Create PartyAttribute]</a>
    <%}%>
    
    <%String curFindString = "SEARCH_TYPE=PartyId";%>
    <%curFindString = curFindString + "&SEARCH_PARAMETER1=" + partyClassification.getPartyId();%>
    <a href="<%=response.encodeURL("/commonapp/party/party/FindPartyClassification.jsp?" + UtilFormatOut.encodeQuery(curFindString))%>" class="buttontext">[Find PartyAttribute]</a>

  <table width="100%" cellpadding="2" cellspacing="2" border="0">
    <tr bgcolor="<%=rowColorResultHeader%>">
  
      <td><div class="tabletext"><b><nobr>PARTY_ID</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>NAME</nobr></b></div></td>
      <td><div class="tabletext"><b><nobr>VALUE</nobr></b></div></td>
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
        PartyAttribute partyAttributeRelated = (PartyAttribute)relatedIterator.next();
        if(partyAttributeRelated != null)
        {
    %>
    <%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyAttributeRelated.getPartyId())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyAttributeRelated.getName())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <div class="tabletext">
    
      <%=UtilFormatOut.checkNull(partyAttributeRelated.getValue())%>
    
        &nbsp;</div>
      </td>
  
      <td>
        <a href="<%=response.encodeURL("/commonapp/party/party/ViewPartyAttribute.jsp?" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyAttributeRelated.getPartyId() + "&" + "PARTY_ATTRIBUTE_NAME=" + partyAttributeRelated.getName())%>" class="buttontext">[View]</a>
      </td>
      <%if(relatedUpdatePerm){%>
        <td>
          <a href="<%=response.encodeURL("/commonapp/party/party/EditPartyAttribute.jsp?" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyAttributeRelated.getPartyId() + "&" + "PARTY_ATTRIBUTE_NAME=" + partyAttributeRelated.getName())%>" class="buttontext">[Edit]</a>
        </td>
      <%}%>
      <%if(relatedDeletePerm){%>
        <td>
          <%-- <a href="<%=response.encodeURL("ViewPersonSecurityGroup.jsp?" + "PERSON_SECURITY_GROUP_USERNAME=" + username + "&" + "PERSON_SECURITY_GROUP_GROUP_ID=" + groupId + "&" + "WEBEVENT=UPDATE_SECURITY_GROUP_PERMISSION&UPDATE_MODE=DELETE&" + "SECURITY_GROUP_PERMISSION_GROUP_ID=" + securityGroupPermission.getGroupId() + "&" + "SECURITY_GROUP_PERMISSION_PERMISSION_ID=" + securityGroupPermission.getPermissionId())%>" class="buttontext">[Delete]</a> --%>
          <a href="<%=response.encodeURL("ViewPartyAttribute.jsp?" + "PARTY_ATTRIBUTE_PARTY_ID=" + partyAttributeRelated.getPartyId() + "&" + "PARTY_ATTRIBUTE_NAME=" + partyAttributeRelated.getName() + "&" + "PARTY_CLASSIFICATION_PARTY_ID=" + partyId + "&" + "PARTY_CLASSIFICATION_PARTY_TYPE_ID=" + partyTypeId + "&WEBEVENT=UPDATE_PARTY_ATTRIBUTE&UPDATE_MODE=DELETE")%>" class="buttontext">[Delete]</a>
        </td>
      <%}%>
    </tr>
    <%}%>
  <%}%>
<%}else{%>
<%rowColorResult=(rowColorResult==rowColorResult1?rowColorResult2:rowColorResult1);%><tr bgcolor="<%=rowColorResult%>">
<td colspan="8">
<h3>No PartyAttributes Found.</h3>
</td>
</tr>
<%}%>
</table>
  <%}%>
<%}%>
<%-- End Relation for PartyAttribute, type: many --%>
  


<br>
<%}else{%>
  <h3>You do not have permission to view this page (PARTY_CLASSIFICATION_ADMIN, or PARTY_CLASSIFICATION_VIEW needed).</h3>
<%}%>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
