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
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("MARKETING", "_VIEW", session)) {%>
<%
    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String trackingCodeId = request.getParameter("trackingCodeId");
    if (UtilValidate.isEmpty(trackingCodeId) && UtilValidate.isNotEmpty((String) request.getAttribute("trackingCodeId"))) {
        trackingCodeId = (String) request.getAttribute("trackingCodeId");
    }
    GenericValue trackingCode = delegator.findByPrimaryKey("TrackingCode", UtilMisc.toMap("trackingCodeId", trackingCodeId));
    GenericValue trackingCodeType = null;
    GenericValue marketingCampaign = null;
    if(trackingCode == null) {
        tryEntity = false;
    } else {
        pageContext.setAttribute("trackingCode", trackingCode);

        trackingCodeType = trackingCode.getRelatedOne("TrackingCodeType");
        if (trackingCodeType != null) pageContext.setAttribute("trackingCodeType", trackingCodeType);

        marketingCampaign = trackingCode.getRelatedOne("MarketingCampaign");
        if (marketingCampaign != null) pageContext.setAttribute("marketingCampaign", marketingCampaign);
    }

    //TrackingCode types
    Collection trackingCodeTypes = delegator.findAll("TrackingCodeType");
    if (trackingCodeTypes != null) pageContext.setAttribute("trackingCodeTypes", trackingCodeTypes);

    //MarketingCampaigns
    Collection marketingCampaigns = delegator.findAll("MarketingCampaign");
    if (marketingCampaigns != null) pageContext.setAttribute("marketingCampaigns", marketingCampaigns);

    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));
%>

<br>
<%if(trackingCodeId != null && trackingCodeId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditTrackingCode?trackingCodeId=<%=trackingCodeId%></ofbiz:url>" class="tabButtonSelected">TrackingCode</a>
  <a href="<ofbiz:url>/FindTrackingCodeOrders?trackingCodeId=<%=trackingCodeId%></ofbiz:url>" class="tabButton">Orders</a>
  <a href="<ofbiz:url>/FindTrackingCodeVisits?trackingCodeId=<%=trackingCodeId%></ofbiz:url>" class="tabButton">Visits</a>
  </div>
<%}%>

<div class="head1">TrackingCode <span class='head2'><%=UtilFormatOut.ifNotEmpty(trackingCode==null?null:trackingCode.getString("description"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(trackingCodeId)%>]</span></div>
<a href="<ofbiz:url>/EditTrackingCode</ofbiz:url>" class="buttontext">[New TrackingCode]</a>

<%if (trackingCode == null) {%>
  <%if (trackingCodeId != null) {%>
    <form action="<ofbiz:url>/createTrackingCode</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <h3>Could not find trackingCode with ID "<%=trackingCodeId%>".</h3>
      <tr>
        <td align=right><div class="tabletext">TrackingCode ID</div></td>
        <td>&nbsp;</td>
        <td>
            <input type="text" name="trackingCodeId" value="<%=trackingCodeId%>" size="20" maxlength="20">
        </td>
      </tr>
  <%} else {%>
    <form action="<ofbiz:url>/createTrackingCode</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
      <tr>
        <td align=right><div class="tabletext">TrackingCode ID</div></td>
        <td>&nbsp;</td>
        <td>
            <input type="text" name="trackingCodeId" size="20" maxlength="20">
        </td>
      </tr>
  <%}%>
<%} else {%>
  <form action="<ofbiz:url>/updateTrackingCode</ofbiz:url>" method=POST style='margin: 0;'>
      <input type=hidden name="trackingCodeId" value="<%=trackingCodeId%>">
  <table border='0' cellpadding='2' cellspacing='0'>
  <tr>
    <td align=right><div class="tabletext">TrackingCode ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=trackingCodeId%></b> (This cannot be changed without re-creating the trackingCode.)
    </td>
  </tr>
<%}%>
      <tr>
        <td width="26%" align=right><div class="tabletext">TrackingCode Type</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <%-- <input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(tryEntity?trackingCode.getString(fieldName):request.getParameter(paramName))%>" size="20" maxlength="20"> --%>
          <select name="trackingCodeTypeId" size=1>
            <ofbiz:if name="trackingCodeType">
              <option selected value='<ofbiz:inputvalue entityAttr="trackingCodeType" field="trackingCodeTypeId"/>'><ofbiz:inputvalue entityAttr="trackingCodeType" field="description"/> <%--<ofbiz:entityfield attribute="trackingCodeType" field="trackingCodeTypeId" prefix="[" suffix="]"/>--%></option>
              <option value='<ofbiz:inputvalue entityAttr="trackingCodeType" field="trackingCodeTypeId"/>'>----</option>
            </ofbiz:if>
            <ofbiz:iterator name="nextTrackingCodeType" property="trackingCodeTypes">
              <option value='<ofbiz:inputvalue entityAttr="nextTrackingCodeType" field="trackingCodeTypeId"/>'><ofbiz:inputvalue entityAttr="nextTrackingCodeType" field="description"/> <%--<ofbiz:entityfield attribute="nextTrackingCodeType" field="trackingCodeTypeId" prefix="[" suffix="]"/>--%></option>
            </ofbiz:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Marketing Campaign</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <select name="marketingCampaignId" size=1>
            <ofbiz:if name="marketingCampaign">
              <option selected value='<ofbiz:inputvalue entityAttr="marketingCampaign" field="marketingCampaignId"/>'><ofbiz:inputvalue entityAttr="marketingCampaign" field="campaignName"/> <%--<ofbiz:entityfield attribute="marketingCampaign" field="marketingCampaignId" prefix="[" suffix="]"/>--%></option>
            </ofbiz:if>
            <!-- always allow the options of no marketingCampaign -->
            <option value=''></option>
            <ofbiz:iterator name="nextMarketingCampaign" property="marketingCampaigns">
              <option value='<ofbiz:inputvalue entityAttr="nextMarketingCampaign" field="marketingCampaignId"/>'><ofbiz:inputvalue entityAttr="nextMarketingCampaign" field="campaignName"/> <%--<ofbiz:entityfield attribute="nextMarketingCampaign" field="marketingCampaignId" prefix="[" suffix="]"/>--%></option>
            </ofbiz:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Description</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="trackingCode" field="description" fullattrs="true"/> size="60" maxlength="250"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Comments</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="trackingCode" field="comments" fullattrs="true"/> size="60" maxlength="250"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Redirect URL</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="trackingCode" field="redirectUrl" fullattrs="true"/> size="60" maxlength="250"> (No redirect if empty)</td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Trackable Lifetime</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="trackingCode" field="trackableLifetime" fullattrs="true"/> size="8" maxlength="18"> (seconds)</td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Billable Lifetime</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="trackingCode" field="billableLifetime" fullattrs="true"/> size="8" maxlength="18"> (seconds)</td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">From Date/Time</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="trackingCode" field="fromDate" fullattrs="true"/> size="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Thru Date/Time</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="trackingCode" field="thruDate" fullattrs="true"/> size="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Group ID</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="trackingCode" field="groupId" fullattrs="true"/> size="20" maxlength="20"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Sub-Group ID</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="trackingCode" field="subgroupId" fullattrs="true"/> size="20" maxlength="20"></td>
      </tr>

  <tr>
    <td colspan='2'>&nbsp;</td>
    <td colspan='1' align=left><input type="submit" name="Update" value="Update"></td>
  </tr>
</table>
</form>

<%} else {%>
  <h3>You do not have permission to view this page. ("MARKETING_VIEW" or "MARKETING_ADMIN" needed)</h3>
<%}%>
