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
 *@created    December 12 2002
 *@version    1.0
--%> 

<%@ page import="java.util.*, java.net.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
	String findOrganizationPartyId = request.getParameter("findOrganizationPartyId");
	if ("".equals(findOrganizationPartyId)) findOrganizationPartyId = null;
	String currentCustomTimePeriodId = request.getParameter("currentCustomTimePeriodId");
	if ("".equals(currentCustomTimePeriodId)) currentCustomTimePeriodId = null;

	GenericValue currentCustomTimePeriod = currentCustomTimePeriodId == null ? null : delegator.findByPrimaryKey("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", currentCustomTimePeriodId));
	if (currentCustomTimePeriod != null) pageContext.setAttribute("currentCustomTimePeriod", currentCustomTimePeriod);
    GenericValue currentPeriodType = currentCustomTimePeriod == null ? null : currentCustomTimePeriod.getRelatedOneCache("PeriodType");
	if (currentPeriodType != null) pageContext.setAttribute("currentPeriodType", currentPeriodType);

	List customTimePeriods = delegator.findByAnd("CustomTimePeriod", 
			UtilMisc.toMap("organizationPartyId", findOrganizationPartyId, "parentPeriodId", currentCustomTimePeriodId), 
			UtilMisc.toList("periodTypeId", "periodNum", "fromDate"));
	if (customTimePeriods != null) pageContext.setAttribute("customTimePeriods", customTimePeriods);

	List allCustomTimePeriods = delegator.findAll("CustomTimePeriod", UtilMisc.toList("organizationPartyId", "parentPeriodId", "periodTypeId", "periodNum", "fromDate"));
	if (allCustomTimePeriods != null) pageContext.setAttribute("allCustomTimePeriods", allCustomTimePeriods);
	
	List periodTypes = delegator.findAllCache("PeriodType", UtilMisc.toList("description"));
	if (periodTypes != null) pageContext.setAttribute("periodTypes", periodTypes);
	
	String newPeriodTypeId = "FISCAL_YEAR";
	if (currentCustomTimePeriod != null && "FISCAL_YEAR".equals(currentCustomTimePeriod.getString("periodTypeId"))) newPeriodTypeId = "FISCAL_QUARTER";
	if (currentCustomTimePeriod != null && "FISCAL_QUARTER".equals(currentCustomTimePeriod.getString("periodTypeId"))) newPeriodTypeId = "FISCAL_MONTH";
	if (currentCustomTimePeriod != null && "FISCAL_MONTH".equals(currentCustomTimePeriod.getString("periodTypeId"))) newPeriodTypeId = "FISCAL_WEEK";
	if (currentCustomTimePeriod != null && "FISCAL_BIWEEK".equals(currentCustomTimePeriod.getString("periodTypeId"))) newPeriodTypeId = "FISCAL_WEEK";
	if (currentCustomTimePeriod != null && "FISCAL_WEEK".equals(currentCustomTimePeriod.getString("periodTypeId"))) newPeriodTypeId = "";
%>

<br>
<h2 style='margin:0;'>Custom Time Period Maintenance Page</h2>

<%if(security.hasPermission("PERIOD_MAINT", session)){%>

<!--
        <attribute name="customTimePeriodId" type="String" mode="IN" optional="false"/>
        <attribute name="parentPeriodId" type="String" mode="IN" optional="true"/>
        <attribute name="organizationPartyId" type="String" mode="IN" optional="true"/>
        <attribute name="periodTypeId" type="String" mode="IN" optional="false"/>
        <attribute name="periodNum" type="Long" mode="IN" optional="true"/>
        <attribute name="fromDate" type="Date" mode="IN" optional="true"/>
        <attribute name="thruDate" type="Date" mode="IN" optional="true"/>
-->
<br>
<FORM method=POST action='<ofbiz:url>/EditCustomTimePeriod</ofbiz:url>' name='setOrganizationPartyIdForm'>
    <input type="hidden" name="currentCustomTimePeriodId" value="<%=UtilFormatOut.checkNull(currentCustomTimePeriodId)%>">
	<div class='tabletext'>
		Show Only Periods with Organization Party ID:
		<input type="text" size="20" name="findOrganizationPartyId" value="<%=UtilFormatOut.checkNull(findOrganizationPartyId)%>">
		<INPUT type=submit value='Update' style='font-size: x-small;'>
	</div>
</FORM>
<br>
<div class='tabletext'>Current Custom Time Period <ofbiz:if name="currentCustomTimePeriod"><a href='<ofbiz:url>/EditCustomTimePeriod<%=findOrganizationPartyId == null ? "" : "?findOrganizationPartyId=" + findOrganizationPartyId%></ofbiz:url>' class="buttontext">[Clear Current]</a></ofbiz:if></div>
<TABLE border='1' cellpadding='2' cellspacing='0'>
  <TR>
    <TD><div class='tabletext'>ID</div></TD>
    <TD><div class='tabletext'>Parent&nbsp;ID</div></TD>
    <TD><div class='tabletext'>Org&nbsp;Party&nbsp;ID</div></TD>
    <TD><div class='tabletext'>Period&nbsp;Type</div></TD>
    <TD><div class='tabletext'>#</div></TD>
    <TD><div class='tabletext'>Name</div></TD>
    <TD><div class='tabletext'>From&nbsp;Date</div></TD>
    <TD><div class='tabletext'>Thru&nbsp;Date</div></TD>
    <TD><div class='tabletext'>&nbsp;</div></TD>
    <TD><div class='tabletext'>&nbsp;</div></TD>
  </TR>
  <ofbiz:if name="currentCustomTimePeriod">
    <FORM method=POST action='<ofbiz:url>/updateCustomTimePeriod</ofbiz:url>' name='updateCustomTimePeriodForm'>
      <input type="hidden" name="findOrganizationPartyId" value="<%=UtilFormatOut.checkNull(findOrganizationPartyId)%>">
      <input type="hidden" name="currentCustomTimePeriodId" value="<%=UtilFormatOut.checkNull(currentCustomTimePeriodId)%>">
    <td><div class='tabletext'><ofbiz:inputvalue entityAttr="currentCustomTimePeriod" field="customTimePeriodId"/></div></td>
    <td>
        <select name="parentPeriodId" style='font-size: x-small;'>
            <option value=''>&nbsp;</option>
            <ofbiz:iterator name="allCustomTimePeriod" property="allCustomTimePeriods">
			    <%GenericValue allPeriodType = allCustomTimePeriod.getRelatedOneCache("PeriodType");%>
                <%boolean isDefault = currentCustomTimePeriod.getString("parentPeriodId") == null ? false : currentCustomTimePeriod.getString("parentPeriodId").equals(allCustomTimePeriod.getString("customTimePeriodId"));%>
                <option value='<ofbiz:entityfield attribute="allCustomTimePeriod" field="customTimePeriodId"/>' <%if (isDefault) {%>selected<%}%>>Pty:<ofbiz:entityfield attribute="allCustomTimePeriod" field="organizationPartyId"/> <%=allPeriodType == null ? "" : allPeriodType.getString("description") + ":"%> <ofbiz:inputvalue entityAttr="allCustomTimePeriod" field="periodNum"/> [<ofbiz:entityfield attribute="allCustomTimePeriod" field="customTimePeriodId"/>]</option>
            </ofbiz:iterator>
        </select>
        <%if (currentCustomTimePeriod.getString("parentPeriodId") != null) {%>
            <a href='<ofbiz:url>/EditCustomTimePeriod?currentCustomTimePeriodId=<ofbiz:inputvalue entityAttr="currentCustomTimePeriod" field="parentPeriodId"/><%=findOrganizationPartyId == null ? "" : "&findOrganizationPartyId=" + findOrganizationPartyId%></ofbiz:url>' class="buttontext">
	        [Set As Current]</a>
	    <%}%>
    </td>
    <td><input type=text size='12' <ofbiz:inputvalue entityAttr="currentCustomTimePeriod" field="organizationPartyId" fullattrs="true"/> style='font-size: x-small;'></td>
    <td>
        <select name="periodTypeId" style='font-size: x-small;'>
            <ofbiz:iterator name="periodType" property="periodTypes">
                <%boolean isDefault = currentCustomTimePeriod.getString("periodTypeId") == null ? false : currentCustomTimePeriod.getString("periodTypeId").equals(periodType.getString("periodTypeId"));%>
                <option value='<ofbiz:entityfield attribute="periodType" field="periodTypeId"/>' <%if (isDefault) {%>selected<%}%>><ofbiz:entityfield attribute="periodType" field="description"/><%-- [<ofbiz:entityfield attribute="periodType" field="periodTypeId"/>]--%></option>
            </ofbiz:iterator>
        </select>
    </td>
    <td><input type=text size='4' <ofbiz:inputvalue entityAttr="currentCustomTimePeriod" field="periodNum" fullattrs="true"/> style='font-size: x-small;'></td>
    <td><input type=text size='10' <ofbiz:inputvalue entityAttr="currentCustomTimePeriod" field="periodName" fullattrs="true"/> style='font-size: x-small;'></td>
    <td>
        <%boolean hasntStarted = false;%>
        <%if (currentCustomTimePeriod.getDate("fromDate") != null && UtilDateTime.nowDate().before(currentCustomTimePeriod.getDate("fromDate"))) { hasntStarted = true; }%>
        <input type=text size='13' <ofbiz:inputvalue entityAttr="currentCustomTimePeriod" field="fromDate" fullattrs="true"/> style='font-size: x-small;<%if (hasntStarted) {%> color: red;<%}%>'>
    </td>
    <td align="center">
        <%boolean hasExpired = false;%>
        <%if (currentCustomTimePeriod.getDate("thruDate") != null && UtilDateTime.nowDate().after(currentCustomTimePeriod.getDate("thruDate"))) { hasExpired = true; }%>
        <input type=text size='13' <ofbiz:inputvalue entityAttr="currentCustomTimePeriod" field="thruDate" fullattrs="true"/> style='font-size: x-small;<%if (hasExpired) {%> color: red;<%}%>'>
    </td>
    <td align="center"><INPUT type=submit value='Update' style='font-size: x-small;'></td>
    <td align="center">
      <a href='<ofbiz:url>/deleteCustomTimePeriod?customTimePeriodId=<ofbiz:entityfield attribute="currentCustomTimePeriod" field="customTimePeriodId"/></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
    </FORM>
  </ofbiz:if>
  <ofbiz:unless name="currentCustomTimePeriod">
  	<TR><TD colspan="8"><div class='tabletext'>No Current Custom Time Period Selected; "Children" below have no Parent Period.</div></TD></TR>
  </ofbiz:unless>
</table>

<br>
<div class='tabletext'>Child Periods</div>
<TABLE border='1' cellpadding='2' cellspacing='0'>
  <TR>
    <TD><div class='tabletext'>ID</div></TD>
    <TD><div class='tabletext'>Parent&nbsp;ID</div></TD>
    <TD><div class='tabletext'>Org&nbsp;Party&nbsp;ID</div></TD>
    <TD><div class='tabletext'>Period&nbsp;Type</div></TD>
    <TD><div class='tabletext'>#</div></TD>
    <TD><div class='tabletext'>Name</div></TD>
    <TD><div class='tabletext'>From&nbsp;Date</div></TD>
    <TD><div class='tabletext'>Thru&nbsp;Date</div></TD>
    <TD><div class='tabletext'>&nbsp;</div></TD>
    <TD><div class='tabletext'>&nbsp;</div></TD>
    <TD><div class='tabletext'>&nbsp;</div></TD>
  </TR>

<%int line = 0;%>
<ofbiz:iterator name="customTimePeriod" property="customTimePeriods">
  <%line++;%>
  <%-- GenericValue periodType = customTimePeriod.getRelatedOneCache("PeriodType"); --%>
  <tr valign="middle">
    <FORM method=POST action='<ofbiz:url>/updateCustomTimePeriod</ofbiz:url>' name='lineForm<%=line%>'>
      <input type="hidden" name="findOrganizationPartyId" value="<%=UtilFormatOut.checkNull(findOrganizationPartyId)%>">
      <input type="hidden" name="currentCustomTimePeriodId" value="<%=UtilFormatOut.checkNull(currentCustomTimePeriodId)%>">
      <input type=hidden <ofbiz:inputvalue entityAttr="customTimePeriod" field="customTimePeriodId" fullattrs="true"/>>
    <td><div class='tabletext'><ofbiz:inputvalue entityAttr="customTimePeriod" field="customTimePeriodId"/></div></td>
    <td>
        <select name="parentPeriodId" style='font-size: x-small;'>
            <option value=''>&nbsp;</option>
            <ofbiz:iterator name="allCustomTimePeriod" property="allCustomTimePeriods">
			    <%GenericValue allPeriodType = allCustomTimePeriod.getRelatedOneCache("PeriodType");%>
                <%boolean isDefault = customTimePeriod.getString("parentPeriodId") == null ? false : customTimePeriod.getString("parentPeriodId").equals(allCustomTimePeriod.getString("customTimePeriodId"));%>
                <option value='<ofbiz:entityfield attribute="allCustomTimePeriod" field="customTimePeriodId"/>' <%if (isDefault) {%>selected<%}%>>Pty:<ofbiz:entityfield attribute="allCustomTimePeriod" field="organizationPartyId"/> <%=allPeriodType == null ? "" : allPeriodType.getString("description") + ":"%> <ofbiz:inputvalue entityAttr="allCustomTimePeriod" field="periodNum"/> [<ofbiz:entityfield attribute="allCustomTimePeriod" field="customTimePeriodId"/>]</option>
            </ofbiz:iterator>
        </select>
    </td>
    <td><input type=text size='12' <ofbiz:inputvalue entityAttr="customTimePeriod" field="organizationPartyId" fullattrs="true"/> style='font-size: x-small;'></td>
    <td>
        <select name="periodTypeId" style='font-size: x-small;'>
            <ofbiz:iterator name="periodType" property="periodTypes">
                <%boolean isDefault = customTimePeriod.getString("periodTypeId") == null ? false : customTimePeriod.getString("periodTypeId").equals(periodType.getString("periodTypeId"));%>
                <option value='<ofbiz:entityfield attribute="periodType" field="periodTypeId"/>' <%if (isDefault) {%>selected<%}%>><ofbiz:entityfield attribute="periodType" field="description"/><%-- [<ofbiz:entityfield attribute="periodType" field="periodTypeId"/>]--%></option>
            </ofbiz:iterator>
        </select>
    </td>
    <td><input type=text size='4' <ofbiz:inputvalue entityAttr="customTimePeriod" field="periodNum" fullattrs="true"/> style='font-size: x-small;'></td>
    <td><input type=text size='10' <ofbiz:inputvalue entityAttr="customTimePeriod" field="periodName" fullattrs="true"/> style='font-size: x-small;'></td>
    <td>
        <%boolean hasntStarted = false;%>
        <%if (customTimePeriod.getDate("fromDate") != null && UtilDateTime.nowDate().before(customTimePeriod.getDate("fromDate"))) { hasntStarted = true; }%>
        <input type=text size='13' <ofbiz:inputvalue entityAttr="customTimePeriod" field="fromDate" fullattrs="true"/> style='font-size: x-small;<%if (hasntStarted) {%> color: red;<%}%>'>
    </td>
    <td align="center">
        <%boolean hasExpired = false;%>
        <%if (customTimePeriod.getDate("thruDate") != null && UtilDateTime.nowDate().after(customTimePeriod.getDate("thruDate"))) { hasExpired = true; }%>
        <input type=text size='13' <ofbiz:inputvalue entityAttr="customTimePeriod" field="thruDate" fullattrs="true"/> style='font-size: x-small;<%if (hasExpired) {%> color: red;<%}%>'>
    </td>
    <td align="center"><INPUT type=submit value='Update' style='font-size: x-small;'></td>
    <td align="center">
      <a href='<ofbiz:url>/deleteCustomTimePeriod?customTimePeriodId=<ofbiz:inputvalue entityAttr="customTimePeriod" field="customTimePeriodId"/><%=currentCustomTimePeriodId == null ? "" : "&currentCustomTimePeriodId=" + currentCustomTimePeriodId%><%=findOrganizationPartyId == null ? "" : "&findOrganizationPartyId=" + findOrganizationPartyId%></ofbiz:url>' class="buttontext">
      [Delete]</a>
    </td>
    <td align="center">
      <a href='<ofbiz:url>/EditCustomTimePeriod?currentCustomTimePeriodId=<ofbiz:inputvalue entityAttr="customTimePeriod" field="customTimePeriodId"/><%=findOrganizationPartyId == null ? "" : "&findOrganizationPartyId=" + findOrganizationPartyId%></ofbiz:url>' class="buttontext">
      [Set As Current]</a>
    </td>
    </FORM>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/createCustomTimePeriod</ofbiz:url>" style='margin: 0;' name='createCustomTimePeriodForm'>
    <input type="hidden" name="findOrganizationPartyId" value="<%=UtilFormatOut.checkNull(findOrganizationPartyId)%>">
    <input type="hidden" name="currentCustomTimePeriodId" value="<%=UtilFormatOut.checkNull(currentCustomTimePeriodId)%>">
    <input type="hidden" name="useValues" value="true">

    <div class='head2'>Add Custom Time Period:</div>
    <div class='tabletext'>
        Parent Period:
        <select name="parentPeriodId" style='font-size: x-small;'>
            <option value=''>&nbsp;</option>
            <ofbiz:iterator name="allCustomTimePeriod" property="allCustomTimePeriods">
			    <%GenericValue allPeriodType = allCustomTimePeriod.getRelatedOneCache("PeriodType");%>
                <%boolean isDefault = currentCustomTimePeriod == null ? false : currentCustomTimePeriod.getString("customTimePeriodId").equals(allCustomTimePeriod.getString("customTimePeriodId"));%>
                <option value='<ofbiz:entityfield attribute="allCustomTimePeriod" field="customTimePeriodId"/>' <%if (isDefault) {%>selected<%}%>>Pty:<ofbiz:entityfield attribute="allCustomTimePeriod" field="organizationPartyId"/> Par:<ofbiz:entityfield attribute="allCustomTimePeriod" field="parentPeriodId"/> <%=allPeriodType == null ? "" : allPeriodType.getString("description")%> <ofbiz:entityfield attribute="allCustomTimePeriod" field="periodNum"/> [<ofbiz:entityfield attribute="allCustomTimePeriod" field="customTimePeriodId"/>]</option>
            </ofbiz:iterator>
        </select>
    </div>
    <div class='tabletext'>
        Organization&nbsp;Party&nbsp;ID:&nbsp;<input type=text size='20' name='organizationPartyId' style='font-size: x-small;'>
        Period Type:
        <select name="periodTypeId" style='font-size: x-small;'>
            <ofbiz:iterator name="periodType" property="periodTypes">
                <%boolean isDefault = newPeriodTypeId == "" ? false : newPeriodTypeId.equals(periodType.getString("periodTypeId"));%>
                <option value='<ofbiz:entityfield attribute="periodType" field="periodTypeId"/>' <%if (isDefault) {%>selected<%}%>><ofbiz:entityfield attribute="periodType" field="description"/><%-- [<ofbiz:entityfield attribute="periodType" field="periodTypeId"/>]--%></option>
            </ofbiz:iterator>
        </select>
        Period&nbsp;Number:&nbsp;<input type=text size='4' name='periodNum' style='font-size: x-small;'>
        Period&nbsp;Name:&nbsp;<input type=text size='10' name='periodName' style='font-size: x-small;'>
    </div>
    <div class='tabletext'>
        From&nbsp;Date:&nbsp;<input type=text size='14' name='fromDate' style='font-size: x-small;'>
        Thru&nbsp;Date:&nbsp;<input type=text size='14' name='thruDate' style='font-size: x-small;'>
        <input type="submit" value="Add" style='font-size: x-small;'>
    </div>

</form>


</TABLE>

<%} else {%>
  <h3>You do not have permission to view this page (PERIOD_MAINT needed).</h3>
<%}%>
