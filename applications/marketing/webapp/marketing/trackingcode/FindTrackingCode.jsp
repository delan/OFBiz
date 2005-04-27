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
 *@created    October 19, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.base.util.*, org.ofbiz.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.security.Security" scope="request" />

<%if (security.hasEntityPermission("MARKETING", "_VIEW", session)) {%>
<%
    List trackingCodes = delegator.findAll("TrackingCode");
    if (trackingCodes != null) pageContext.setAttribute("trackingCodes", trackingCodes);
%>

<div class="head1">TrackingCodes List</div>

<div><a href='<ofbiz:url>/EditTrackingCode</ofbiz:url>' class="buttontext">[Create New TrackingCode]</a></div>
<br/>
<table border="1" cellpadding='2' cellspacing='0'>
  <tr>
    <td><div class="tabletext"><b>ID</b></div></td>
    <td><div class="tabletext"><b>Type</b></div></td>
    <td><div class="tabletext"><b>Description</b></div></td>
    <td><div class="tabletext"><b>Campaign</b></div></td>
    <td><div class="tabletext">&nbsp;</div></td>
  </tr>
<ofbiz:iterator name="trackingCode" property="trackingCodes">
  <%GenericValue trackingCodeType = trackingCode.getRelatedOne("TrackingCodeType");%>
  <%if (trackingCodeType != null) pageContext.setAttribute("trackingCodeType", trackingCodeType); else pageContext.removeAttribute("trackingCodeType");%>
  <%GenericValue marketingCampaign = trackingCode.getRelatedOne("MarketingCampaign");%>
  <%if (marketingCampaign != null) pageContext.setAttribute("marketingCampaign", marketingCampaign); else pageContext.removeAttribute("marketingCampaign");%>
  <tr valign="middle">
    <td><div class='tabletext'>&nbsp;[<a href='<ofbiz:url>/EditTrackingCode?trackingCodeId=<ofbiz:inputvalue entityAttr="trackingCode" field="trackingCodeId"/></ofbiz:url>' class="buttontext"><ofbiz:inputvalue entityAttr="trackingCode" field="trackingCodeId"/></a>]</div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="trackingCodeType" field="description"/></div></td>
    <td><div class='tabletext'>&nbsp;<ofbiz:inputvalue entityAttr="trackingCode" field="description"/></div></td>
    <td><div class='tabletext'>&nbsp;<a href='<ofbiz:url>/EditMarketingCampaign?marketingCampaignId=<ofbiz:inputvalue entityAttr="marketingCampaign" field="marketingCampaignId"/></ofbiz:url>' class="buttontext"><ofbiz:inputvalue entityAttr="marketingCampaign" field="campaignName"/></a></div></td>
    <td>
      <a href='<ofbiz:url>/EditTrackingCode?trackingCodeId=<ofbiz:inputvalue entityAttr="trackingCode" field="trackingCodeId"/></ofbiz:url>' class="buttontext">
      [Edit]</a>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br/>

<%}else{%>
  <h3>You do not have permission to view this page. ("MARKETING_VIEW" or "MARKETING_ADMIN" needed)</h3>
<%}%>
