<%
/**
 *  Title: Entity Maintenance Page
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
 *@created    May 22 2001
 *@version    1.0
 */
%> 

<%@ page import="org.ofbiz.core.util.*" %> 

<% pageContext.setAttribute("PageName", "entitymaint"); %> 
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>


<h2 style='margin:0;'>Entity Maintenance</h2>
<%if(security.hasPermission("ENTITY_MAINT", session)){%>
<table border='0' cellpadding='2' cellspacing='2'>
<%
  String rowColor1 = "99CCFF";
  String rowColor2 = "CCFFFF";
  String rowColor = "";
%>
  <TR bgcolor='CCCCFF'>
    <TD>Entity&nbsp;Name</TD>
    <TD>Create</TD>
    <TD>Find</TD>
  </TR>

  <%if(security.hasEntityPermission("GEO", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>Geo</TD>
      <TD>
        <%if(security.hasEntityPermission("GEO", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewGeo")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindGeo")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("GEO_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>GeoType</TD>
      <TD>
        <%if(security.hasEntityPermission("GEO_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewGeoType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindGeoType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("GEO_ASSOC", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>GeoAssoc</TD>
      <TD>
        <%if(security.hasEntityPermission("GEO_ASSOC", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewGeoAssoc")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindGeoAssoc")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("GEO_ASSOC_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>GeoAssocType</TD>
      <TD>
        <%if(security.hasEntityPermission("GEO_ASSOC_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewGeoAssocType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindGeoAssocType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PERIOD_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PeriodType</TD>
      <TD>
        <%if(security.hasEntityPermission("PERIOD_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPeriodType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPeriodType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("STANDARD_TIME_PERIOD", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>StandardTimePeriod</TD>
      <TD>
        <%if(security.hasEntityPermission("STANDARD_TIME_PERIOD", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewStandardTimePeriod")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindStandardTimePeriod")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("STATUS", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>Status</TD>
      <TD>
        <%if(security.hasEntityPermission("STATUS", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewStatus")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindStatus")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("STATUS_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>StatusType</TD>
      <TD>
        <%if(security.hasEntityPermission("STATUS_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewStatusType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindStatusType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("UOM", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>Uom</TD>
      <TD>
        <%if(security.hasEntityPermission("UOM", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewUom")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindUom")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("UOM_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>UomType</TD>
      <TD>
        <%if(security.hasEntityPermission("UOM_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewUomType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindUomType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("UOM_CONVERSION", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>UomConversion</TD>
      <TD>
        <%if(security.hasEntityPermission("UOM_CONVERSION", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewUomConversion")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindUomConversion")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>Party</TD>
      <TD>
        <%if(security.hasEntityPermission("PARTY", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewParty")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindParty")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PARTY_CLASSIFICATION", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyClassification</TD>
      <TD>
        <%if(security.hasEntityPermission("PARTY_CLASSIFICATION", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassification")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyClassification")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyClassificationType</TD>
      <TD>
        <%if(security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassificationType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyClassificationType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyType</TD>
      <TD>
        <%if(security.hasEntityPermission("PARTY_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PARTY_ATTRIBUTE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyAttribute</TD>
      <TD>
        <%if(security.hasEntityPermission("PARTY_ATTRIBUTE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyAttribute")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyAttribute")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PARTY_TYPE_ATTR", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyTypeAttr</TD>
      <TD>
        <%if(security.hasEntityPermission("PARTY_TYPE_ATTR", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyTypeAttr")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyTypeAttr")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PARTY_ROLE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyRole</TD>
      <TD>
        <%if(security.hasEntityPermission("PARTY_ROLE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyRole")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyRole")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("ROLE_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>RoleType</TD>
      <TD>
        <%if(security.hasEntityPermission("ROLE_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewRoleType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindRoleType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("ROLE_TYPE_ATTR", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>RoleTypeAttr</TD>
      <TD>
        <%if(security.hasEntityPermission("ROLE_TYPE_ATTR", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewRoleTypeAttr")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindRoleTypeAttr")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>Product</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProduct")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProduct")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_CLASS", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductClass</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_CLASS", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductClass")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductClass")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductType</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductAttribute</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_ATTRIBUTE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductAttribute")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductAttribute")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_TYPE_ATTR", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductTypeAttr</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_TYPE_ATTR", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductTypeAttr")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductTypeAttr")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("GOOD_IDENTIFICATION", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>GoodIdentification</TD>
      <TD>
        <%if(security.hasEntityPermission("GOOD_IDENTIFICATION", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentification")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindGoodIdentification")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>GoodIdentificationType</TD>
      <TD>
        <%if(security.hasEntityPermission("GOOD_IDENTIFICATION_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewGoodIdentificationType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindGoodIdentificationType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_ASSOC", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductAssoc</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_ASSOC", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductAssoc")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductAssoc")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductAssocType</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_ASSOC_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductAssocType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductAssocType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_DATA_OBJECT", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductDataObject</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_DATA_OBJECT", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductDataObject")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductDataObject")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_CATEGORY", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductCategory</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_CATEGORY", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductCategory")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductCategory")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_CATEGORY_CLASS", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductCategoryClass</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_CATEGORY_CLASS", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryClass")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductCategoryClass")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductCategoryType</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_CATEGORY_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductCategoryType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_CATEGORY_ATTRIBUTE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductCategoryAttribute</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_CATEGORY_ATTRIBUTE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryAttribute")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductCategoryAttribute")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_CATEGORY_TYPE_ATTR", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductCategoryTypeAttr</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_CATEGORY_TYPE_ATTR", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryTypeAttr")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductCategoryTypeAttr")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_CATEGORY_MEMBER", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductCategoryMember</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_CATEGORY_MEMBER", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryMember")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductCategoryMember")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductCategoryRollup</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_CATEGORY_ROLLUP", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductCategoryRollup")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductCategoryRollup")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_FEATURE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductFeature</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_FEATURE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductFeature")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductFeature")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_FEATURE_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductFeatureType</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_FEATURE_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductFeatureType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductFeatureCategory</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_FEATURE_CATEGORY", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureCategory")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductFeatureCategory")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductFeatureAppl</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_FEATURE_APPL", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureAppl")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductFeatureAppl")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductFeatureApplType</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_FEATURE_APPL_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureApplType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductFeatureApplType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductFeatureIactn</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_FEATURE_IACTN", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactn")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductFeatureIactn")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ProductFeatureIactnType</TD>
      <TD>
        <%if(security.hasEntityPermission("PRODUCT_FEATURE_IACTN_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewProductFeatureIactnType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindProductFeatureIactnType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("FEATURE_DATA_OBJECT", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>FeatureDataObject</TD>
      <TD>
        <%if(security.hasEntityPermission("FEATURE_DATA_OBJECT", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewFeatureDataObject")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindFeatureDataObject")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("COST_COMPONENT", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>CostComponent</TD>
      <TD>
        <%if(security.hasEntityPermission("COST_COMPONENT", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewCostComponent")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindCostComponent")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("COST_COMPONENT_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>CostComponentType</TD>
      <TD>
        <%if(security.hasEntityPermission("COST_COMPONENT_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindCostComponentType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>CostComponentAttribute</TD>
      <TD>
        <%if(security.hasEntityPermission("COST_COMPONENT_ATTRIBUTE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentAttribute")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindCostComponentAttribute")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>CostComponentTypeAttr</TD>
      <TD>
        <%if(security.hasEntityPermission("COST_COMPONENT_TYPE_ATTR", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewCostComponentTypeAttr")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindCostComponentTypeAttr")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRICE_COMPONENT", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PriceComponent</TD>
      <TD>
        <%if(security.hasEntityPermission("PRICE_COMPONENT", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponent")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPriceComponent")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRICE_COMPONENT_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PriceComponentType</TD>
      <TD>
        <%if(security.hasEntityPermission("PRICE_COMPONENT_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPriceComponentType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRICE_COMPONENT_ATTRIBUTE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PriceComponentAttribute</TD>
      <TD>
        <%if(security.hasEntityPermission("PRICE_COMPONENT_ATTRIBUTE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentAttribute")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPriceComponentAttribute")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PriceComponentTypeAttr</TD>
      <TD>
        <%if(security.hasEntityPermission("PRICE_COMPONENT_TYPE_ATTR", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPriceComponentTypeAttr")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPriceComponentTypeAttr")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("QUANTITY_BREAK", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>QuantityBreak</TD>
      <TD>
        <%if(security.hasEntityPermission("QUANTITY_BREAK", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewQuantityBreak")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindQuantityBreak")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("ORDER_VALUE_BREAK", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>OrderValueBreak</TD>
      <TD>
        <%if(security.hasEntityPermission("ORDER_VALUE_BREAK", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewOrderValueBreak")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindOrderValueBreak")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("SALE_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SaleType</TD>
      <TD>
        <%if(security.hasEntityPermission("SALE_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewSaleType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindSaleType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("INVENTORY_ITEM", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>InventoryItem</TD>
      <TD>
        <%if(security.hasEntityPermission("INVENTORY_ITEM", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItem")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindInventoryItem")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>InventoryItemType</TD>
      <TD>
        <%if(security.hasEntityPermission("INVENTORY_ITEM_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindInventoryItemType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("INVENTORY_ITEM_ATTRIBUTE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>InventoryItemAttribute</TD>
      <TD>
        <%if(security.hasEntityPermission("INVENTORY_ITEM_ATTRIBUTE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemAttribute")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindInventoryItemAttribute")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("INVENTORY_ITEM_TYPE_ATTR", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>InventoryItemTypeAttr</TD>
      <TD>
        <%if(security.hasEntityPermission("INVENTORY_ITEM_TYPE_ATTR", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemTypeAttr")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindInventoryItemTypeAttr")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PHYSICAL_INVENTORY", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PhysicalInventory</TD>
      <TD>
        <%if(security.hasEntityPermission("PHYSICAL_INVENTORY", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPhysicalInventory")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPhysicalInventory")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>InventoryItemVariance</TD>
      <TD>
        <%if(security.hasEntityPermission("INVENTORY_ITEM_VARIANCE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewInventoryItemVariance")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindInventoryItemVariance")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("VARIANCE_REASON", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>VarianceReason</TD>
      <TD>
        <%if(security.hasEntityPermission("VARIANCE_REASON", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewVarianceReason")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindVarianceReason")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("ITEM_VARIANCE_ACCTG_TRANS", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ItemVarianceAcctgTrans</TD>
      <TD>
        <%if(security.hasEntityPermission("ITEM_VARIANCE_ACCTG_TRANS", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewItemVarianceAcctgTrans")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindItemVarianceAcctgTrans")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("LOT", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>Lot</TD>
      <TD>
        <%if(security.hasEntityPermission("LOT", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewLot")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindLot")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("CONTAINER", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>Container</TD>
      <TD>
        <%if(security.hasEntityPermission("CONTAINER", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewContainer")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindContainer")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("CONTAINER_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ContainerType</TD>
      <TD>
        <%if(security.hasEntityPermission("CONTAINER_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewContainerType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindContainerType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("FACILITY", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>Facility</TD>
      <TD>
        <%if(security.hasEntityPermission("FACILITY", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewFacility")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindFacility")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("FACILITY_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>FacilityType</TD>
      <TD>
        <%if(security.hasEntityPermission("FACILITY_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewFacilityType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindFacilityType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("FACILITY_ATTRIBUTE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>FacilityAttribute</TD>
      <TD>
        <%if(security.hasEntityPermission("FACILITY_ATTRIBUTE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewFacilityAttribute")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindFacilityAttribute")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("FACILITY_TYPE_ATTR", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>FacilityTypeAttr</TD>
      <TD>
        <%if(security.hasEntityPermission("FACILITY_TYPE_ATTR", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewFacilityTypeAttr")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindFacilityTypeAttr")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("FACILITY_CONTACT_MECHANISM", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>FacilityContactMechanism</TD>
      <TD>
        <%if(security.hasEntityPermission("FACILITY_CONTACT_MECHANISM", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewFacilityContactMechanism")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindFacilityContactMechanism")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("PARTY_FACILITY", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyFacility</TD>
      <TD>
        <%if(security.hasEntityPermission("PARTY_FACILITY", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyFacility")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyFacility")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("FACILITY_ROLE_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>FacilityRoleType</TD>
      <TD>
        <%if(security.hasEntityPermission("FACILITY_ROLE_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewFacilityRoleType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindFacilityRoleType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("REORDER_GUIDELINE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>ReorderGuideline</TD>
      <TD>
        <%if(security.hasEntityPermission("REORDER_GUIDELINE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewReorderGuideline")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindReorderGuideline")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("SUPPLIER_PRODUCT", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SupplierProduct</TD>
      <TD>
        <%if(security.hasEntityPermission("SUPPLIER_PRODUCT", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewSupplierProduct")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindSupplierProduct")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("SUPPLIER_RATING_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SupplierRatingType</TD>
      <TD>
        <%if(security.hasEntityPermission("SUPPLIER_RATING_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewSupplierRatingType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindSupplierRatingType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("SUPPLIER_PREF_ORDER", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SupplierPrefOrder</TD>
      <TD>
        <%if(security.hasEntityPermission("SUPPLIER_PREF_ORDER", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewSupplierPrefOrder")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindSupplierPrefOrder")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("MARKET_INTEREST", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>MarketInterest</TD>
      <TD>
        <%if(security.hasEntityPermission("MARKET_INTEREST", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewMarketInterest")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindMarketInterest")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("USER_LOGIN", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>UserLogin</TD>
      <TD>
        <%if(security.hasEntityPermission("USER_LOGIN", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewUserLogin")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindUserLogin")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>LoginAccountHistory</TD>
      <TD>
        <%if(security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewLoginAccountHistory")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindLoginAccountHistory")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("SECURITY_GROUP", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SecurityGroup</TD>
      <TD>
        <%if(security.hasEntityPermission("SECURITY_GROUP", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewSecurityGroup")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindSecurityGroup")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("SECURITY_PERMISSION", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SecurityPermission</TD>
      <TD>
        <%if(security.hasEntityPermission("SECURITY_PERMISSION", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewSecurityPermission")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindSecurityPermission")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SecurityGroupPermission</TD>
      <TD>
        <%if(security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewSecurityGroupPermission")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindSecurityGroupPermission")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>UserLoginSecurityGroup</TD>
      <TD>
        <%if(security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewUserLoginSecurityGroup")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindUserLoginSecurityGroup")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
</TABLE>
<%}else{%>
  <h3>You do not have permission to view this page (ENTITY_MAINT needed).</h3>
<%}%>
<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
