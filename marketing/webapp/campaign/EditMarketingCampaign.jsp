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

<%if(security.hasEntityPermission("MARKETING", "_VIEW", session)) {%>
<%
    boolean tryEntity = true;
    if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) tryEntity = false;

    String marketingCampaignId = request.getParameter("marketingCampaignId");
    if (UtilValidate.isEmpty(marketingCampaignId) && UtilValidate.isNotEmpty((String) request.getAttribute("marketingCampaignId"))) {
        marketingCampaignId = (String) request.getAttribute("marketingCampaignId");
    }
    GenericValue marketingCampaign = delegator.findByPrimaryKey("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", marketingCampaignId));
    GenericValue parentCampaign = null;
    if(marketingCampaign == null) {
        tryEntity = false;
    } else {
        pageContext.setAttribute("marketingCampaign", marketingCampaign);

        if (UtilValidate.isNotEmpty(marketingCampaign.getString("parentCampaignId"))) {
            parentCampaign = delegator.findByPrimaryKey("MarketingCampaign", UtilMisc.toMap("marketingCampaignId", marketingCampaign.get("parentCampaignId")));
            if (parentCampaign != null) pageContext.setAttribute("parentCampaign", parentCampaign);
        }
    }

    //MarketingCampaigns
    Collection marketingCampaigns = delegator.findAll("MarketingCampaign");
    if (marketingCampaigns != null) pageContext.setAttribute("marketingCampaigns", marketingCampaigns);

    pageContext.setAttribute("tryEntity", new Boolean(tryEntity));
%>

<br>
<%if(marketingCampaignId != null && marketingCampaignId.length() > 0){%>
  <div class='tabContainer'>
  <a href="<ofbiz:url>/EditMarketingCampaign?marketingCampaignId=<%=marketingCampaignId%></ofbiz:url>" class="tabButtonSelected">Campaign</a>
  <a href="<ofbiz:url>/EditMarketingCampaignRoles?marketingCampaignId=<%=marketingCampaignId%></ofbiz:url>" class="tabButton">Roles</a>
  </div>
<%}%>

<div class="head1">MarketingCampaign <span class='head2'><%=UtilFormatOut.ifNotEmpty(marketingCampaign==null?null:marketingCampaign.getString("campaignName"),"\"","\"")%> [ID:<%=UtilFormatOut.checkNull(marketingCampaignId)%>]</span></div>
<a href="<ofbiz:url>/EditMarketingCampaign</ofbiz:url>" class="buttontext">[New MarketingCampaign]</a>

<%if (marketingCampaign == null) {%>
  <%if (marketingCampaignId != null) {%>
    <form action="<ofbiz:url>/createMarketingCampaign</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
    <h3>Could not find marketingCampaign with ID "<%=marketingCampaignId%>".</h3>
  <%} else {%>
    <form action="<ofbiz:url>/createMarketingCampaign</ofbiz:url>" method=POST style='margin: 0;'>
    <table border='0' cellpadding='2' cellspacing='0'>
  <%}%>
<%} else {%>
  <form action="<ofbiz:url>/updateMarketingCampaign</ofbiz:url>" method=POST style='margin: 0;'>
      <input type=hidden name="marketingCampaignId" value="<%=marketingCampaignId%>">
  <table border='0' cellpadding='2' cellspacing='0'>
  <tr>
    <td align=right><div class="tabletext">MarketingCampaign ID</div></td>
    <td>&nbsp;</td>
    <td>
      <b><%=marketingCampaignId%></b> (This is automatically generated.)
    </td>
  </tr>
<%}%>
      <tr>
        <td width="26%" align=right><div class="tabletext">Campaign Name</div></td>
        <td>&nbsp;</td>
        <td width="74%"><input type="text" <ofbiz:inputvalue entityAttr="marketingCampaign" field="campaignName" fullattrs="true" tryEntityAttr="tryEntity"/> size="30" maxlength="250"></td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Parent Marketing Campaign</div></td>
        <td>&nbsp;</td>
        <td width="74%">
          <select name="parentCampaignId" size=1>
            <ofbiz:if name="parentCampaign">
              <option selected value='<ofbiz:inputvalue entityAttr="marketingCampaign" field="parentCampaignId"/>'><ofbiz:inputvalue entityAttr="parentCampaign" field="campaignName"/><%--<ofbiz:entityfield attribute="parentCampaign" field="marketingCampaignId" prefix="[" suffix="]"/>--%></option>
            </ofbiz:if>
            <!-- always allow the options of no parentCampaign -->
            <option value=''></option>
            <ofbiz:iterator name="nextMarketingCampaign" property="marketingCampaigns">
              <option value='<ofbiz:inputvalue entityAttr="nextMarketingCampaign" field="marketingCampaignId"/>'><ofbiz:inputvalue entityAttr="nextMarketingCampaign" field="campaignName"/><%--<ofbiz:entityfield attribute="nextMarketingCampaign" field="marketingCampaignId" prefix="[" suffix="]"/>--%></option>
            </ofbiz:iterator>
          </select>
        </td>
      </tr>
      <tr>
        <td width="26%" align=right><div class="tabletext">Campaign Summary</div></td>
        <td>&nbsp;</td>
        <td width="74%"><textarea cols="60" rows="7" name="campaignSummary" maxlength="2000" style='font-size: small;'><ofbiz:inputvalue entityAttr='marketingCampaign' field='campaignSummary' tryEntityAttr="tryEntity"/></textarea></td>
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
