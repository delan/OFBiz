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
 * @author     David E. Jones
 * @author     Andy Zeneski
 * @created    June 18, 2002
 * @version    1.0
 */
%>

<%@ page import="java.util.*, java.io.*" %>
<%@ page import="org.ofbiz.core.util.*, org.ofbiz.core.entity.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />
<jsp:useBean id="security" type="org.ofbiz.core.security.Security" scope="request" />

<%if (security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
<%
    Collection productPriceRules = delegator.findAll("ProductPriceRule");
    if (productPriceRules != null && productPriceRules.size() > 0) pageContext.setAttribute("productPriceRules", productPriceRules);

    Collection inputParamEnums = delegator.findByAndCache("Enumeration", UtilMisc.toMap("enumTypeId", "PROD_PRICE_IN_PARAM"), UtilMisc.toList("sequenceId"));
    if (inputParamEnums != null) pageContext.setAttribute("inputParamEnums", inputParamEnums);

    Collection condOperEnums = delegator.findByAndCache("Enumeration", UtilMisc.toMap("enumTypeId", "PROD_PRICE_COND"), UtilMisc.toList("sequenceId"));
    if (condOperEnums != null) pageContext.setAttribute("condOperEnums", condOperEnums);

    Collection productPriceActionTypes = delegator.findAllCache("ProductPriceActionType", UtilMisc.toList("description"));
    if (productPriceActionTypes != null) pageContext.setAttribute("productPriceActionTypes", productPriceActionTypes);
%>
<br>

<div class="head1">Price Rules</div>

<br>
<br>

<table border="1" width="100%" cellpadding='2' cellspacing='0'>
  <tr>
    <td width='10%'><div class="tabletext"><b>Rule ID</b></div></td>
    <td width='80%'><div class="tabletext"><b>Rule Name</b></div></td>
    <td width='10%'><div class="tabletext"><b>&nbsp;</b></div></td>
  </tr>

<ofbiz:iterator name="productPriceRule" property="productPriceRules">
  <%Collection productPriceConds = productPriceRule.getRelated("ProductPriceCond");%>
  <%if (productPriceConds != null) pageContext.setAttribute("productPriceConds", productPriceConds);%>
  <%Collection productPriceActions = productPriceRule.getRelated("ProductPriceAction");%>
  <%if (productPriceActions != null) pageContext.setAttribute("productPriceActions", productPriceActions);%>
  <tr valign="middle">
    <td><div class='tabletext'><b><ofbiz:inputvalue entityAttr="productPriceRule" field="productPriceRuleId"/></b></div></td>
    <td align="left">
        <FORM method=POST action='<ofbiz:url>/updateProductPriceRule</ofbiz:url>'>
            <input type=hidden <ofbiz:inputvalue entityAttr="productPriceRule" field="productPriceRuleId" fullattrs="true"/>>
            <input type=text size='30' <ofbiz:inputvalue entityAttr="productPriceRule" field="ruleName" fullattrs="true"/>>
            <INPUT type=submit value='Update'>
        </FORM>
    </td>
    <td align="center">&nbsp;
      <ofbiz:unless name="productPriceConds" size="0">
          <ofbiz:unless name="productPriceActions" size="0">
              <a href='<ofbiz:url>/deleteProductPriceRule?productPriceRuleId=<ofbiz:entityfield attribute="productPriceRule" field="productPriceRuleId"/></ofbiz:url>' class="buttontext">
              [Delete]</a>
          </ofbiz:unless>
      </ofbiz:unless>
    </td>
  </tr>
  <tr valign="top">
    <td align="right"><div class='tabletext'>Conditions:</div></td>
    <td align="left" colspan='2'>
        <table border="1" width="100%" cellpadding='2' cellspacing='0'>
          <tr>
            <td width='5%'><div class="tabletext"><b>SeqId</b></div></td>
            <td width='85%'><div class="tabletext"><b>Input,&nbsp;Operator&nbsp;&amp;&nbsp;Value</b></div></td>
            <td width='10%'><div class="tabletext"><b>&nbsp;</b></div></td>
          </tr>
          <ofbiz:iterator name="productPriceCond" property="productPriceConds">
              <tr>
                <td><div class='tabletext'><b><ofbiz:inputvalue entityAttr="productPriceCond" field="productPriceCondSeqId"/></b></div></td>
                <td align="left">
                    <FORM method=POST action='<ofbiz:url>/updateProductPriceCond</ofbiz:url>'>
                        <input type=hidden <ofbiz:inputvalue entityAttr="productPriceCond" field="productPriceRuleId" fullattrs="true"/>>
                        <input type=hidden <ofbiz:inputvalue entityAttr="productPriceCond" field="productPriceCondSeqId" fullattrs="true"/>>
                        <select name='inputParamEnumId' size=1>
                            <%if (productPriceCond.get("inputParamEnumId") != null) {%>
                              <option value='<%=productPriceCond.getString("inputParamEnumId")%>'> [<%=productPriceCond.getString("inputParamEnumId")%>]</option>
                              <option value='<%=productPriceCond.getString("inputParamEnumId")%>'>&nbsp;</option>
                            <%} else {%>
                              <option value=''>&nbsp;</option>
                            <%}%>
                            <ofbiz:iterator name="inputParamEnum" property="inputParamEnums">
                              <option value='<%=inputParamEnum.getString("enumId")%>'><%=inputParamEnum.getString("description")%> [<%=inputParamEnum.getString("enumId")%>]</option>
                            </ofbiz:iterator>
                        </select>
                        <select name='operatorEnumId' size=1>
                            <%if (productPriceCond.get("operatorEnumId") != null) {%>
                              <option value='<%=productPriceCond.getString("operatorEnumId")%>'> [<%=productPriceCond.getString("operatorEnumId")%>]</option>
                              <option value='<%=productPriceCond.getString("operatorEnumId")%>'>&nbsp;</option>
                            <%} else {%>
                              <option value=''>&nbsp;</option>
                            <%}%>
                            <ofbiz:iterator name="condOperEnum" property="condOperEnums">
                              <option value='<%=condOperEnum.getString("enumId")%>'><%=condOperEnum.getString("description")%> [<%=condOperEnum.getString("enumId")%>]</option>
                            </ofbiz:iterator>
                        </select>
                        <input type=text size='15' <ofbiz:inputvalue entityAttr="productPriceCond" field="condValue" fullattrs="true"/>>
                        <INPUT type=submit value='Update'>
                    </FORM>
                </td>
                <td align="center">
                  <a href='<ofbiz:url>/deleteProductPriceCond?productPriceRuleId=<ofbiz:entityfield attribute="productPriceCond" field="productPriceRuleId"/>&productPriceCondSeqId=<ofbiz:entityfield attribute="productPriceCond" field="productPriceCondSeqId"/></ofbiz:url>' class="buttontext">
                  [Delete]</a>
                </td>
              </tr>
          </ofbiz:iterator>
          <tr>
            <td align="left" colspan='3'>
                <FORM method=POST action='<ofbiz:url>/createProductPriceCond</ofbiz:url>'>
                    <input type=hidden <ofbiz:inputvalue entityAttr="productPriceRule" field="productPriceRuleId" fullattrs="true"/>>
                    <span class='tabletext'><b>New:</b>&nbsp;</span>
                    <input type=text size='5' name='productPriceCondSeqId'>
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
                    <input type=text size='15' name='condValue'>
                    <INPUT type=submit value='Create'>
                </FORM>
            </td>
          </tr>
        </table>
    </td>
  </tr>
<!--
      <field name="productPriceRuleId" type="id-ne"></field>
      <field name="productPriceActionSeqId" type="id-ne"></field>
      <field name="productPriceActionTypeId" type="id-ne"></field>
      <field name="quantity" type="floating-point"></field>
      <field name="productId" type="id"></field>
      <field name="actionLimit" type="numeric"></field>
-->
  <tr valign="top">
    <td align="right"><div class='tabletext'>Actions:</div></td>
    <td align="left" colspan='2'>
        <table border="1" width="100%" cellpadding='2' cellspacing='0'>
          <tr>
            <td width='5%'><div class="tabletext"><b>SeqId</b></div></td>
            <td width='85%'><div class="tabletext"><b>ActionType,&nbsp;Amount</b></div></td>
            <td width='10%'><div class="tabletext"><b>&nbsp;</b></div></td>
          </tr>
          <ofbiz:iterator name="productPriceAction" property="productPriceActions">
              <tr>
                <td><div class='tabletext'><b><ofbiz:inputvalue entityAttr="productPriceAction" field="productPriceActionSeqId"/></b></div></td>
                <td align="left">
                    <FORM method=POST action='<ofbiz:url>/updateProductPriceAction</ofbiz:url>'>
                        <input type=hidden <ofbiz:inputvalue entityAttr="productPriceAction" field="productPriceRuleId" fullattrs="true"/>>
                        <input type=hidden <ofbiz:inputvalue entityAttr="productPriceAction" field="productPriceActionSeqId" fullattrs="true"/>>
                        <select name='productPriceActionTypeId' size=1>
                            <%if (productPriceAction.get("productPriceActionTypeId") != null) {%>
                              <option value='<%=productPriceAction.getString("productPriceActionTypeId")%>'> [<%=productPriceAction.getString("productPriceActionTypeId")%>]</option>
                              <option value='<%=productPriceAction.getString("productPriceActionTypeId")%>'>&nbsp;</option>
                            <%} else {%>
                              <option value=''>&nbsp;</option>
                            <%}%>
                            <ofbiz:iterator name="productPriceActionType" property="productPriceActionTypes">
                              <option value='<%=productPriceActionType.getString("productPriceActionTypeId")%>'><%=productPriceActionType.getString("description")%> [<%=productPriceActionType.getString("productPriceActionTypeId")%>]</option>
                            </ofbiz:iterator>
                        </select>
                        <input type=text size='8' <ofbiz:inputvalue entityAttr="productPriceAction" field="amount" fullattrs="true"/>>
                        <INPUT type=submit value='Update'>
                    </FORM>
                </td>
                <td align="center">
                  <a href='<ofbiz:url>/deleteProductPriceAction?productPriceRuleId=<ofbiz:entityfield attribute="productPriceAction" field="productPriceRuleId"/>&productPriceActionSeqId=<ofbiz:entityfield attribute="productPriceAction" field="productPriceActionSeqId"/></ofbiz:url>' class="buttontext">
                  [Delete]</a>
                </td>
              </tr>
          </ofbiz:iterator>
          <tr>
            <td align="left" colspan='3'>
                <FORM method=POST action='<ofbiz:url>/createProductPriceAction</ofbiz:url>'>
                    <input type=hidden <ofbiz:inputvalue entityAttr="productPriceRule" field="productPriceRuleId" fullattrs="true"/>>
                    <span class='tabletext'><b>New:</b>&nbsp;</span>
                    <input type=text size='5' name='productPriceActionSeqId'>
                    <select name='productPriceActionTypeId' size=1>
                        <ofbiz:iterator name="productPriceActionType" property="productPriceActionTypes">
                          <option value='<%=productPriceActionType.getString("productPriceActionTypeId")%>'><%=productPriceActionType.getString("description")%> [<%=productPriceActionType.getString("productPriceActionTypeId")%>]</option>
                        </ofbiz:iterator>
                    </select>
                    <input type=text size='8' name='amount'>
                    <INPUT type=submit value='Create'>
                </FORM>
            </td>
          </tr>
        </table>
    </td>
  </tr>
</ofbiz:iterator>
</table>

<br>

<form method="POST" action="<ofbiz:url>/createProductPriceRule</ofbiz:url>" style='margin: 0;'>
  <div class='head2'>Add Price Rule:</div>
  <br>
  ID: <input type=text size='20' name='productPriceRuleId'>
  Name: <input type=text size='30' name='ruleName'>
  <input type="submit" value="Add">
</form>

<br>

<%} else {%>
  <h3>You do not have permission to view this page. ("CATALOG_VIEW" or "CATALOG_ADMIN" needed)</h3>
<%}%>
