<%
/**
 *  Title: Edit Product Page
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
 *@created    Sep 10 2001
 *@version    1.0
 */
%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>

<%
try {
  boolean useValues = true;
  if(request.getAttribute(SiteDefs.ERROR_MESSAGE) != null) useValues = false;

  String productId = request.getParameter("PRODUCT_ID");
  String productIdTo = request.getParameter("PRODUCT_ID_TO");
  String productAssocTypeId = request.getParameter("PRODUCT_ASSOC_TYPE_ID");
  String fromDateStr = request.getParameter("FROM_DATE");
  Timestamp fromDate = null;
  if(fromDateStr != null && fromDateStr.length() > 0) fromDate = Timestamp.valueOf(fromDateStr);
  if(fromDate == null) fromDate = (Timestamp)request.getAttribute("ProductAssocCreateFromDate");

  GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", productId));
  GenericValue productAssoc = delegator.findByPrimaryKey("ProductAssoc", UtilMisc.toMap("productId", productId, "productIdTo", productIdTo, "productAssocTypeId", productAssocTypeId, "fromDate", fromDate));

  if("true".equalsIgnoreCase((String)request.getParameter("useValues"))) useValues = true;
  if(productAssoc == null) useValues = false;
  boolean isCreate = true;

  Collection assocTypes = delegator.findAll("ProductAssocType");
%>

<br>
<div class="head1">Edit Associations for Product 
  <%=UtilFormatOut.ifNotEmpty(product==null?null:product.getString("productName"),"\"","\"")%> 
  with ID "<%=UtilFormatOut.checkNull(productId)%>"</div>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[Create New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[View Product Page]</a>
  <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Product]</a>
  <a href="<ofbiz:url>/EditProductCategories?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Category Members]</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Keywords]</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Associations]</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Attributes]</a>
<%}%>

<form action="<ofbiz:url>/UpdateProductAssoc</ofbiz:url>" method=POST style='margin: 0;'>
<table border='0' cellpadding='2' cellspacing='0'>

<%if(productAssoc == null){%>
  <%if(productId != null || productIdTo != null || productAssocTypeId != null || fromDate != null){%>
    <b>Could not find association with Product Id=<%=UtilFormatOut.checkNull(productId)%>, Product Id To=<%=UtilFormatOut.checkNull(productIdTo)%>, Association Type Id=<%=UtilFormatOut.checkNull(productAssocTypeId)%>, From Date=<%=UtilFormatOut.makeString(fromDate)%>.</b>
    <input type=hidden name="UPDATE_MODE" value="CREATE">
    <tr>
      <td align=right><div class="tabletext">Product ID</div></td>
      <td>&nbsp;</td>
      <td><input type="text" name="PRODUCT_ID" size="20" maxlength="40" value="<%=UtilFormatOut.checkNull(productId)%>"></td>
    </tr>
    <tr>
      <td align=right><div class="tabletext">Product ID To</div></td>
      <td>&nbsp;</td>
      <td><input type="text" name="PRODUCT_ID_TO" size="20" maxlength="40" value="<%=UtilFormatOut.checkNull(productIdTo)%>"></td>
    </tr>
    <tr>
      <td align=right><div class="tabletext">Association Type ID</div></td>
      <td>&nbsp;</td>
      <td>
        <%-- <input type="text" name="PRODUCT_ASSOC_TYPE_ID" size="20" maxlength="40" value="<%=UtilFormatOut.checkNull(productAssocTypeId)%>"> --%>
        <select name="PRODUCT_ASSOC_TYPE_ID" size=1>
          <%if(productAssocTypeId != null && productAssocTypeId.length() > 0) {%>
            <%GenericValue curAssocType = delegator.findByPrimaryKey("ProductAssocType", UtilMisc.toMap("productAssocTypeId", productAssocTypeId));%>
            <%if(curAssocType != null) {%>
              <option selected value='<%=curAssocType.getString("productAssocTypeId")%>'><%=curAssocType.getString("description")%> [<%=curAssocType.getString("productAssocTypeId")%>]</option>
            <%}%>
          <%}%>
          <option value=''>&nbsp;</option>
          <%Iterator assocTypeIter = UtilMisc.toIterator(assocTypes);%>
          <%while(assocTypeIter != null && assocTypeIter.hasNext()) {%>
            <%GenericValue nextAssocType=(GenericValue)assocTypeIter.next();%>
            <option value='<%=nextAssocType.getString("productAssocTypeId")%>'><%=nextAssocType.getString("description")%> [<%=nextAssocType.getString("productAssocTypeId")%>]</option>
          <%}%>
        </select>
      </td>
    </tr>
    <tr>
      <td align=right><div class="tabletext">From Date</div></td>
      <td>&nbsp;</td>
      <td>
        <div class='tabletext'><input type="text" name="FROM_DATE" size="30" maxlength="40" value="<%=UtilFormatOut.makeString(fromDate)%>">(YYYY-MM-DD HH:mm:SS.sss)</div>
        <div class='tabletext'>(Will be set to now if empty)</div>
      </td>
    </tr>
  <%}else{%>
    <input type=hidden name="UPDATE_MODE" value="CREATE">
    <tr>
      <td align=right><div class="tabletext">Product ID</div></td>
      <td>&nbsp;</td>
      <td><input type="text" name="PRODUCT_ID" size="20" maxlength="40" value=""></td>
    </tr>
    <tr>
      <td align=right><div class="tabletext">Product ID To</div></td>
      <td>&nbsp;</td>
      <td><input type="text" name="PRODUCT_ID_TO" size="20" maxlength="40" value=""></td>
    </tr>
    <tr>
      <td align=right><div class="tabletext">Association Type ID</div></td>
      <td>&nbsp;</td>
      <td>
        <%-- <input type="text" name="PRODUCT_ASSOC_TYPE_ID" size="20" maxlength="40" value=""> --%>
        <select name="PRODUCT_ASSOC_TYPE_ID" size=1>
          <option value=''>&nbsp;</option>
          <%Iterator assocTypeIter = UtilMisc.toIterator(assocTypes);%>
          <%while(assocTypeIter != null && assocTypeIter.hasNext()) {%>
            <%GenericValue nextAssocType=(GenericValue)assocTypeIter.next();%>
            <option value='<%=nextAssocType.getString("productAssocTypeId")%>'><%=nextAssocType.getString("description")%> [<%=nextAssocType.getString("productAssocTypeId")%>]</option>
          <%}%>
        </select>
      </td>
    </tr>
    <tr>
      <td align=right><div class="tabletext">From Date</div></td>
      <td>&nbsp;</td>
      <td>
        <div class='tabletext'><input type="text" name="FROM_DATE" size="30" maxlength="40" value="">(YYYY-MM-DD HH:mm:SS.sss)</div>
        <div class='tabletext'>(Will be set to now if empty)</div>
      </td>
    </tr>
  <%}%>
<%}else{%>
  <%isCreate = false;%>
  <input type=hidden name="UPDATE_MODE" value="UPDATE">
  <input type=hidden name="PRODUCT_ID" value="<%=productId%>">
  <input type=hidden name="PRODUCT_ID_TO" value="<%=productIdTo%>">
  <input type=hidden name="PRODUCT_ASSOC_TYPE_ID" value="<%=productAssocTypeId%>">
  <input type=hidden name="FROM_DATE" value="<%=UtilFormatOut.makeString(fromDate)%>">
  <tr>
    <td align=right><div class="tabletext">Product ID</div></td>
    <td>&nbsp;</td>
    <td><b><%=productId%></b> (You must re-create the association to change this.)</td>
  </tr>
  <tr>
    <td align=right><div class="tabletext">Product ID To</div></td>
    <td>&nbsp;</td>
    <td><b><%=productIdTo%></b> (You must re-create the association to change this.)</td>
  </tr>
  <tr>
    <td align=right><div class="tabletext">Association Type ID</div></td>
    <td>&nbsp;</td>
    <td><b><%=productAssocTypeId%></b> (You must re-create the association to change this.)</td>
  </tr>
  <tr>
    <td align=right><div class="tabletext">From Date</div></td>
    <td>&nbsp;</td>
    <td><b><%=fromDate.toString()%></b> (You must re-create the association to change this.)</td>
  </tr>
<%}%>

  <%String fieldName; String paramName;%>
  <tr>
    <%fieldName = "thruDate";%><%paramName = "THRU_DATE";%>    
    <td width="26%" align=right><div class="tabletext">Thru Date</div></td>
    <td>&nbsp;</td>
    <td width="74%">
      <div class='tabletext'><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.makeString(productAssoc.getTimestamp(fieldName)):request.getParameter(paramName))%>" size="30" maxlength="30">(YYYY-MM-DD HH:mm:SS.sss)</div>
    </td>
  </tr>
  <tr>
    <%fieldName = "reason";%><%paramName = "REASON";%>    
    <td width="26%" align=right><div class="tabletext">Reason</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?productAssoc.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255"></td>
  </tr>
  <tr>
    <%fieldName = "instruction";%><%paramName = "INSTRUCTION";%>    
    <td width="26%" align=right><div class="tabletext">Instruction</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?productAssoc.getString(fieldName):request.getParameter(paramName))%>" size="80" maxlength="255"></td>
  </tr>

  <tr>
    <%fieldName = "quantity";%><%paramName = "QUANTITY";%>    
    <td width="26%" align=right><div class="tabletext">Quantity</div></td>
    <td>&nbsp;</td>
    <td width="74%"><input type="text" name="<%=paramName%>" value="<%=UtilFormatOut.checkNull(useValues?UtilFormatOut.formatQuantity(productAssoc.getDouble(fieldName)):request.getParameter(paramName))%>" size="10" maxlength="15"></td>
  </tr>

  <tr>
    <td colspan='3'><input type='submit' value='<%=isCreate?"Create":"Update"%>'></td>
  </tr>
</table>
</form>

<a href="<ofbiz:url>/EditProduct</ofbiz:url>" class="buttontext">[Create New Product]</a>
<%if(productId != null && productId.length() > 0){%>
  <a href="/ecommerce/control/product?product_id=<%=productId%>" class='buttontext' target='_blank'>[View Product Page]</a>
  <a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Product]</a>
  <a href="<ofbiz:url>/EditProductCategories?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Category Members]</a>
  <a href="<ofbiz:url>/EditProductKeyword?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Keywords]</a>
  <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Associations]</a>
  <a href="<ofbiz:url>/EditProductAttributes?PRODUCT_ID=<%=productId%></ofbiz:url>" class="buttontext">[Edit Attributes]</a>
<%}%>
<br>

<%if(productId != null && product != null){%>
<%java.util.Date nowDate = new java.util.Date();%>
<hr>
<p class="head2">Product Associations FROM this Product to...</p>

  <table border="1" cellpadding='2' cellspacing='0'>
    <tr>
      <td><div class="tabletext"><b>To Product ID</b></div></td>
      <td><div class="tabletext"><b>Name</b></div></td>
      <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
      <td><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
      <td><div class="tabletext"><b>Association&nbsp;Type</b></div></td>
      <td><div class="tabletext"><b>&nbsp;</b></div></td>
      <td><div class="tabletext"><b>&nbsp;</b></div></td>
    </tr>
    <%Iterator pcIterator = UtilMisc.toIterator(product.getRelated("MainProductAssoc"));%>
    <%while(pcIterator != null && pcIterator.hasNext()) {%>
      <%GenericValue listProductAssoc = (GenericValue)pcIterator.next();%>
      <%GenericValue listToProduct = listProductAssoc.getRelatedOneCache("AssocProduct");%>
      <tr valign="middle">
        <td><a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=listProductAssoc.getString("productIdTo")%></ofbiz:url>" class="buttontext"><%=listProductAssoc.getString("productIdTo")%></a></td>
        <td><%if(listToProduct!=null){%><a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=listProductAssoc.getString("productIdTo")%></ofbiz:url>" class="buttontext"><%=listToProduct.getString("productName")%></a><%}%>&nbsp;</td>
        <td><div class='tabletext' <%=(listProductAssoc.getTimestamp("fromDate") != null && nowDate.before(listProductAssoc.getTimestamp("fromDate")))?"style='color: red;'":""%>>
          <%=UtilFormatOut.makeString(listProductAssoc.getTimestamp("fromDate"))%>&nbsp;</div></td>
        <td><div class='tabletext' <%=(listProductAssoc.getTimestamp("thruDate") != null && nowDate.after(listProductAssoc.getTimestamp("thruDate")))?"style='color: red;'":""%>>
          <%=UtilFormatOut.makeString(listProductAssoc.getTimestamp("thruDate"))%>&nbsp;</div></td>
        <td><div class='tabletext'><%=listProductAssoc.getString("productAssocTypeId")%></div></td>
        <td>
          <a href="<ofbiz:url>/UpdateProductAssoc?UPDATE_MODE=DELETE&PRODUCT_ID=<%=productId%>&PRODUCT_ID_TO=<%=listProductAssoc.getString("productIdTo")%>&PRODUCT_ASSOC_TYPE_ID=<%=listProductAssoc.getString("productAssocTypeId")%>&FROM_DATE=<%=UtilFormatOut.encodeQueryValue(listProductAssoc.getTimestamp("fromDate").toString())%>&useValues=true</ofbiz:url>" class="buttontext">
          [Delete]</a>
        </td>
        <td>
          <a href="<ofbiz:url>/EditProductAssoc?PRODUCT_ID=<%=productId%>&PRODUCT_ID_TO=<%=listProductAssoc.getString("productIdTo")%>&PRODUCT_ASSOC_TYPE_ID=<%=listProductAssoc.getString("productAssocTypeId")%>&FROM_DATE=<%=UtilFormatOut.encodeQueryValue(listProductAssoc.getTimestamp("fromDate").toString())%>&useValues=true</ofbiz:url>" class="buttontext">
          [Edit]</a>
        </td>
      </tr>
    <%}%>
  </table>

<hr>
<p class="head2">Product Associations TO this Product from...</p>

  <table border="1" cellpadding='2' cellspacing='0'>
    <tr>
      <td><div class="tabletext"><b>Product ID</b></div></td>
      <td><div class="tabletext"><b>Name</b></div></td>
      <td><div class="tabletext"><b>From&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
      <td><div class="tabletext"><b>Thru&nbsp;Date&nbsp;&amp;&nbsp;Time</b></div></td>
      <td><div class="tabletext"><b>Association&nbsp;Type</b></div></td>
      <td><div class="tabletext"><b>&nbsp;</b></div></td>
    </tr>
    <%Iterator tfIterator = UtilMisc.toIterator(product.getRelated("AssocProductAssoc"));%>
    <%while(tfIterator != null && tfIterator.hasNext()) {%>
      <%GenericValue listProductAssoc = (GenericValue)tfIterator.next();%>
      <%GenericValue listToProduct = listProductAssoc.getRelatedOneCache("MainProduct");%>
      <tr valign="middle">
        <td><a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=listProductAssoc.getString("productId")%></ofbiz:url>" class="buttontext"><%=listProductAssoc.getString("productId")%></a></td>
        <td><%if(listToProduct!=null){%><a href="<ofbiz:url>/EditProduct?PRODUCT_ID=<%=listProductAssoc.getString("productId")%></ofbiz:url>" class="buttontext"><%=listToProduct.getString("productName")%></a><%}%>&nbsp;</td>
        <td><div class='tabletext'><%=UtilFormatOut.makeString(listProductAssoc.getTimestamp("fromDate"))%>&nbsp;</div></td>
        <td><div class='tabletext'><%=UtilFormatOut.makeString(listProductAssoc.getTimestamp("thruDate"))%>&nbsp;</div></td>
        <td><div class='tabletext'><%=listProductAssoc.getString("productAssocTypeId")%></div></td>
        <td>
          <a href="<ofbiz:url>/UpdateProductAssoc?UPDATE_MODE=DELETE&PRODUCT_ID=<%=listProductAssoc.getString("productId")%>&PRODUCT_ID_TO=<%=listProductAssoc.getString("productIdTo")%>&PRODUCT_ASSOC_TYPE_ID=<%=listProductAssoc.getString("productAssocTypeId")%>&FROM_DATE=<%=UtilFormatOut.encodeQueryValue(listProductAssoc.getTimestamp("fromDate").toString())%>&useValues=true</ofbiz:url>" class="buttontext">
          [Delete]</a>
        </td>
      </tr>
    <%}%>
  </table>
<br>
<div class='tabletext'>NOTE: <b style='color: red;'>Red</b> date/time entries denote that the current time is before the From Date or after the Thru Date. If the From Date is <b style='color: red;'>red</b>, assocication has not started yet; if Thru Date is <b style='color: red;'>red</b>, association has expired (<u>and should probably be deleted</u>).</div>
<%}%>

<%
}
catch(Exception e) {e.printStackTrace();}
%>
<%}else{%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
