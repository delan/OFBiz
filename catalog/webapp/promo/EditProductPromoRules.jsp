<%
/**
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
 *@created    May 20 2002
 *@version    1.0
 */
%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    String productPromoId = request.getParameter("productPromoId");
    GenericValue productPromo = delegator.findByPrimaryKey("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId));
    Collection productPromoRules = null;
    if (productPromo != null) {
        productPromoRules = productPromo.getRelated("ProductPromoRule", null, UtilMisc.toList("ruleName"));
        if (productPromoRules != null) pageContext.setAttribute("productPromoRules", productPromoRules);
    }

    Collection inputParamEnums = delegator.findByAndCache("Enumeration", UtilMisc.toMap("enumTypeId", "PROD_PROMO_IN_PARAM"), UtilMisc.toList("sequenceId"));
    if (inputParamEnums != null) pageContext.setAttribute("inputParamEnums", inputParamEnums);

    Collection condOperEnums = delegator.findByAndCache("Enumeration", UtilMisc.toMap("enumTypeId", "PROD_PROMO_COND"), UtilMisc.toList("sequenceId"));
    if (condOperEnums != null) pageContext.setAttribute("condOperEnums", condOperEnums);
%>
<br>

<a href="<ofbiz:url>/EditProductPromo</ofbiz:url>" class="buttontext">[New ProductPromo]</a>
<%if(productPromoId != null && productPromoId.length() > 0){%>
  <a href="<ofbiz:url>/EditProductPromo?productPromoId=<%=productPromoId%></ofbiz:url>" class="buttontext">[Promo]</a>
  <a href="<ofbiz:url>/EditProductPromoRules?productPromoId=<%=productPromoId%></ofbiz:url>" class="buttontextdisabled">[Rules]</a>
  <a href="<ofbiz:url>/EditProductPromoCatalogs?productPromoId=<%=productPromoId%></ofbiz:url>" class="buttontext">[Catalogs]</a>
<%}%>

<div class="head1">Rules for Promotion
  <%=UtilFormatOut.ifNotEmpty(productPromo==null?null:productPromo.getString("promoName"),"\"","\"")%> 
  with ID "<%=UtilFormatOut.checkNull(productPromoId)%>"</div>

<br>
<br>
<%if (productPromoId != null && productPromo != null) {%>

<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td width='10%'><div class="tabletext"><b>Rule ID</b></div></td>
    <td width='80%'><div class="tabletext"><b>Rule Name</b></div></td>
    <td width='10%'><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>
<ofbiz:iterator name="productPromoRule" property="productPromoRules">
  <%Collection productPromoConds = productPromoRule.getRelated("ProductPromoCond");%>
  <%if (productPromoConds != null) pageContext.setAttribute("productPromoConds", productPromoConds);%>
  <%Collection productPromoActions = productPromoRule.getRelated("ProductPromoAction");%>
  <%if (productPromoActions != null) pageContext.setAttribute("productPromoActions", productPromoActions);%>
  <tr valign="middle">
    <td><div class='tabletext'><b><ofbiz:inputvalue entityAttr="productPromoRule" field="productPromoRuleId"/></b></div></td>
    <td align="left">
        <FORM method=POST action='<ofbiz:url>/updateProductPromoRule</ofbiz:url>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="productPromoRule" field="productPromoId" fullattrs="true"/>>
            <input type=hidden <ofbiz:inputvalue entityAttr="productPromoRule" field="productPromoRuleId" fullattrs="true"/>>
            <input type=text size='30' <ofbiz:inputvalue entityAttr="productPromoRule" field="ruleName" fullattrs="true"/>>
            <INPUT type=submit value='Update'>
        </FORM>
    </td>
    <td align="center">&nbsp;
      <ofbiz:unless name="productPromoConds" size="0">
          <ofbiz:unless name="productPromoActions" size="0">
              <a href='<ofbiz:url>/deleteProductPromoRule?productPromoId=<ofbiz:entityfield attribute="productPromoRule" field="productPromoId"/>&productPromoRuleId=<ofbiz:entityfield attribute="productPromoRule" field="productPromoRuleId"/></ofbiz:url>' class="buttontext">
              [Delete]</a>
          </ofbiz:unless>
      </ofbiz:unless>
    </td>
  </tr>
  <tr valign="top">
    <td align="right"><div class='tabletext'>Cs:</div></td>
    <td align="left" colspan='2'>
        <table border="1" width="100%" cellpadding='2' cellspacing='0'>
          <tr>
            <td width='5%'><div class="tabletext"><b>SeqId</b></div></td>
            <td width='85%'><div class="tabletext"><b>Input,&nbsp;Operator&nbsp;&amp;&nbsp;Value</b></div></td>
            <td width='10%'><div class="tabletext"><b>&nbsp;</b></div></td>
          </tr>
          <ofbiz:iterator name="productPromoCond" property="productPromoConds">
           <tr>
            <td><div class='tabletext'><b><ofbiz:inputvalue entityAttr="productPromoCond" field="productPromoCondSeqId"/></b></div></td>
            <td align="left">
                <FORM method=POST action='<ofbiz:url>/updateProductPromoCond</ofbiz:url>'>
                    <input type=hidden <ofbiz:inputvalue entityAttr="productPromoCond" field="productPromoId" fullattrs="true"/>>
                    <input type=hidden <ofbiz:inputvalue entityAttr="productPromoCond" field="productPromoRuleId" fullattrs="true"/>>
                    <input type=hidden <ofbiz:inputvalue entityAttr="productPromoCond" field="productPromoCondSeqId" fullattrs="true"/>>
                    <select name='inputParamEnumId' size=1>
                        <%if (productPromoCond.get("inputParamEnumId") != null) {%>
                          <option value='<%=productPromoCond.getString("inputParamEnumId")%>'> [<%=productPromoCond.getString("inputParamEnumId")%>]</option>
                          <option value='<%=productPromoCond.getString("inputParamEnumId")%>'>&nbsp;</option>
                        <%} else {%>
                          <option value=''>&nbsp;</option>
                        <%}%>
                        <ofbiz:iterator name="inputParamEnum" property="inputParamEnums">
                          <option value='<%=inputParamEnum.getString("enumId")%>'><%=inputParamEnum.getString("description")%> [<%=inputParamEnum.getString("enumId")%>]</option>
                        </ofbiz:iterator>
                    </select>
                    <select name='operatorEnumId' size=1>
                        <%if (productPromoCond.get("operatorEnumId") != null) {%>
                          <option value='<%=productPromoCond.getString("operatorEnumId")%>'> [<%=productPromoCond.getString("operatorEnumId")%>]</option>
                          <option value='<%=productPromoCond.getString("operatorEnumId")%>'>&nbsp;</option>
                        <%} else {%>
                          <option value=''>&nbsp;</option>
                        <%}%>
                        <ofbiz:iterator name="condOperEnum" property="condOperEnums">
                          <option value='<%=condOperEnum.getString("enumId")%>'><%=condOperEnum.getString("description")%> [<%=condOperEnum.getString("enumId")%>]</option>
                        </ofbiz:iterator>
                    </select>
                    <input type=text size='30' <ofbiz:inputvalue entityAttr="productPromoCond" field="condValue" fullattrs="true"/>>
                    <INPUT type=submit value='Update'>
                </FORM>
            </td>
            <td align="center">
              <a href='<ofbiz:url>/deleteProductPromoCond?productPromoId=<ofbiz:entityfield attribute="productPromoCond" field="productPromoId"/>&productPromoRuleId=<ofbiz:entityfield attribute="productPromoCond" field="productPromoRuleId"/>&productPromoCondSeqId=<ofbiz:entityfield attribute="productPromoCond" field="productPromoCondSeqId"/></ofbiz:url>' class="buttontext">
              [Delete]</a>
            </td>
           </tr>
          </ofbiz:iterator>
           <tr>
            <td align="left" colspan='3'>
                <FORM method=POST action='<ofbiz:url>/createProductPromoCond</ofbiz:url>'>
                    <input type=hidden <ofbiz:inputvalue entityAttr="productPromoRule" field="productPromoId" fullattrs="true"/>>
                    <input type=hidden <ofbiz:inputvalue entityAttr="productPromoRule" field="productPromoRuleId" fullattrs="true"/>>
                    <span class='tabletext'><b>New:</b>&nbsp;</span>
                    <input type=text size='5' name='productPromoCondSeqId'>
                    <select name='inputParamEnumId' size=1>
                        <ofbiz:iterator name="inputParamEnum" property="inputParamEnums">
                          <option value='<%=inputParamEnum.getString("enumId")%>'><%=inputParamEnum.getString("description")%> [<%=inputParamEnum.getString("enumId")%>]</option>
                        </ofbiz:iterator>
                    </select>
                    <select name='operatorEnumId' size=1>
                        <ofbiz:iterator name="condOperEnum" property="condOperEnums">
                          <option value='<%=condOperEnum.getString("enumId")%>'><%=condOperEnum.getString("description")%> [<%=condOperEnum.getString("enumId")%>]</option>
                        </ofbiz:iterator>
                    </select>
                    <input type=text size='30' name='condValue'>
                    <INPUT type=submit value='Update'>
                </FORM>
            </td>
           </tr>
        </table>
    </td>
  </tr>
<!--
      <field name="productPromoId" type="id-ne"></field>
      <field name="productPromoRuleId" type="id-ne"></field>
      <field name="productPromoActionSeqId" type="id-ne"></field>
      <field name="productPromoActionTypeId" type="id-ne"></field>
      <field name="quantity" type="floating-point"></field>
      <field name="productId" type="id"></field>
      <field name="actionLimit" type="numeric"></field>
-->
  <tr valign="top">
    <td align="right"><div class='tabletext'>As:</div></td>
    <td align="left" colspan='2'>
        <table border="1" width="100%" cellpadding='2' cellspacing='0'>
          <tr>
            <td width='5%'><div class="tabletext"><b>SeqId</b></div></td>
            <td width='85%'><div class="tabletext"><b>Type,&nbsp;Quantity,&nbsp;ProductId&nbsp;&amp;&nbsp;Limit</b></div></td>
            <td width='10%'><div class="tabletext"><b>&nbsp;</b></div></td>
          </tr>
          <ofbiz:iterator name="productPromoAction" property="productPromoActions">
          </ofbiz:iterator>
           <tr>
            <td align="left" colspan='3'>
                <FORM method=POST action='<ofbiz:url>/createProductPromoAction</ofbiz:url>'>
                    <input type=hidden <ofbiz:inputvalue entityAttr="productPromoRule" field="productPromoId" fullattrs="true"/>>
                    <input type=hidden <ofbiz:inputvalue entityAttr="productPromoRule" field="productPromoRuleId" fullattrs="true"/>>
                    <span class='tabletext'><b>New:</b>&nbsp;</span>
                    <input type=text size='5' name='productPromoActionSeqId'>
                    <select name='inputParamEnumId' size=1>
                        <ofbiz:iterator name="inputParamEnum" property="inputParamEnums">
                          <option value='<%=inputParamEnum.getString("enumId")%>'><%=inputParamEnum.getString("description")%> [<%=inputParamEnum.getString("enumId")%>]</option>
                        </ofbiz:iterator>
                    </select>
                    <select name='operatorEnumId' size=1>
                        <ofbiz:iterator name="condOperEnum" property="condOperEnums">
                          <option value='<%=condOperEnum.getString("enumId")%>'><%=condOperEnum.getString("description")%> [<%=condOperEnum.getString("enumId")%>]</option>
                        </ofbiz:iterator>
                    </select>
                    <input type=text size='30' name='condValue'>
                    <INPUT type=submit value='Update'>
                </FORM>
            </td>
           </tr>
        </table>
    </td>
  </tr>
</ofbiz:iterator>
</table>
<br>
<form method="POST" action="<ofbiz:url>/createProductPromoRule</ofbiz:url>" style='margin: 0;'>
  <input type="hidden" name="productPromoId" value="<%=productPromoId%>">

  <div class='head2'>Add Promo Rule:</div>
  <br>
  ID: <input type=text size='20' name='productPromoRuleId'>
  Name: <input type=text size='30' name='ruleName'>
  <input type="submit" value="Add">
</form>
<%}%>
<br>

<%} else {%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
