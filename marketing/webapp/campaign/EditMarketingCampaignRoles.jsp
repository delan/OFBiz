<%--
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@created    October 24, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("MARKETING", "_VIEW", session)) {%>
<%
    String marketingCampaignId = request.getParameter("marketingCampaignId");
    GenericValue marketingCampaign = delegator.findByPrimaryKey("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", marketingCampaignId));
    Collection marketingCampaignRoles = delegator.findByAnd("MarketingCampaignRole", 
            UtilMisc.toMap("marketingCampaignId", marketingCampaignId), UtilMisc.toList("orderId"));
    if (marketingCampaignRoles != null) pageContext.setAttribute("marketingCampaignRoles", marketingCampaignRoles);

    //RoleTypes
    Collection roleTypes = delegator.findAll("RoleType");
    if (roleTypes != null) pageContext.setAttribute("roleTypes", roleTypes);

    int viewIndex = 0;
    int viewSize = 20;
    int highIndex = 0;
    int lowIndex = 0;
    int listSize = 0;

    try {
        viewIndex = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_INDEX")).intValue();
    } catch (Exception e) {
        viewIndex = 0;
    }
    try {
        viewSize = Integer.valueOf((String) pageContext.getRequest().getParameter("VIEW_SIZE")).intValue();
    } catch (Exception e) {
        viewSize = 20;
    }
    if (marketingCampaignRoles != null) {
        listSize = marketingCampaignRoles.size();
    }
    lowIndex = viewIndex * viewSize;
    highIndex = (viewIndex + 1) * viewSize;
    if (listSize < highIndex) {
        highIndex = listSize;
    }
%>

<br>
<%if(marketingCampaignId != null && marketingCampaignId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditMarketingCampaign?marketingCampaignId=<%=marketingCampaignId%></ofbiz:url>" class="tabButton">Campaign</a>
  <a href="<ofbiz:url>/EditMarketingCampaignRoles?marketingCampaignId=<%=marketingCampaignId%></ofbiz:url>" class="tabButtonSelected">Roles</a>
  </div>
<%}%>

<div class="head1">Roles <span class='head2'>for <%=UtilFormatOut.ifNotEmpty(marketingCampaign==null?null:marketingCampaign.getString("campaignName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(marketingCampaignId)%>]</span></div>

<a href="<ofbiz:url>/EditMarketingCampaign</ofbiz:url>" class="buttontext">[New MarketingCampaign]</a>

<ofbiz:if name="marketingCampaignRoles" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/EditMarketingCampaignRoles?marketingCampaignId=" + marketingCampaignId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/EditMarketingCampaignRoles?marketingCampaignId=" + marketingCampaignId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>
<%if (marketingCampaignId != null){%>
<table border="1" cellpadding='2' cellspacing='0' width='100%'>
  <tr>
    <td><div class="tabletext"><b>Role&nbsp;Type</b></div></td>
    <td><div class="tabletext"><b>Party&nbsp;ID</b></div></td>
    <td><div class="tabletext"><b>Party&nbsp;Name</b></div></td>
    <td><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:iterator name="marketingCampaignRole" property="marketingCampaignRoles" offset="<%=lowIndex%>" limit="<%=viewSize%>">
  <%GenericValue roleType = marketingCampaignRole.getRelatedOne("RoleType");%>
  <%if (roleType != null) pageContext.setAttribute("roleType", roleType);%>
  <tr valign="middle">
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="roleType" field="description"/></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="marketingCampaignRole" field="partyId"/></div></td>
    <td><div class='tabletext'>&nbsp;<%-- get party name from person or partyGroup... --%></div></td>
    <td><a href='<ofbiz:url>/removeRoleFromMarketingCampaign?marketingCampaignId=<%=marketingCampaignId%>&partyId=<ofbiz:entityfield attribute="marketingCampaignRole" field="partyId"/>&roleTypeId=<ofbiz:entityfield attribute="marketingCampaignRole" field="roleTypeId"/></ofbiz:url>' class='buttontext'>Remove</a></td>
  </tr>
</ofbiz:iterator>
</table>
<ofbiz:if name="marketingCampaignRoles" size="0">
  <table border="0" width="100%" cellpadding="2">
    <tr>
      <td align=right>
        <b>
        <%if (viewIndex > 0) {%>
          <a href="<ofbiz:url><%="/EditMarketingCampaignRoles?marketingCampaignId=" + marketingCampaignId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex-1)%></ofbiz:url>" class="buttontext">[Previous]</a> |
        <%}%>
        <%if (listSize > 0) {%>
          <%=lowIndex+1%> - <%=highIndex%> of <%=listSize%>
        <%}%>
        <%if (listSize > highIndex) {%>
          | <a href="<ofbiz:url><%="/EditMarketingCampaignRoles?marketingCampaignId=" + marketingCampaignId + "&VIEW_SIZE=" + viewSize + "&VIEW_INDEX=" + (viewIndex+1)%></ofbiz:url>" class="buttontext">[Next]</a>
        <%}%>
        </b>
      </td>
    </tr>
  </table>
</ofbiz:if>
<form method="POST" action="<ofbiz:url>/addRoleToMarketingCampaign</ofbiz:url>" style='margin: 0;' name='createMarketingCampaignRoleForm'>
    <input type="hidden" name="marketingCampaignId" value="<%=marketingCampaignId%>">

    <div class='head2'>Add Party in a Role:</div>
    <div class='tabletext'>
        Role Type:
        <select name="roleTypeId" style='font-size: x-small;'>
            <ofbiz:iterator name="roleType" property="roleTypes">
                <option value='<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>'><ofbiz:entityfield attribute="roleType" field="description"/><%--[<ofbiz:entityfield attribute="roleType" field="roleTypeId"/>]--%></option>
            </ofbiz:iterator>
        </select>
        Party&nbsp;ID:&nbsp;<input type=text size='20' name='partyId' style='font-size: x-small;'>&nbsp;<input type="submit" value="Add" style='font-size: x-small;'>
    </div>

</form>
<%}%>
<br>

<%}else{%>
  <h3>You do not have permission to view this page. ("MARKETING_VIEW" or "MARKETING_ADMIN" needed)</h3>
<%}%>
