<%
/**
 *  Title: Login Page
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

<%@ page import="javax.servlet.*" %>
<%@ page import="javax.servlet.http.*" %>
<%@ page import="org.ofbiz.core.util.*" %> 

<%pageContext.setAttribute("PageName", "newuser");%>
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%String previousParams=(String)session.getAttribute(SiteDefs.PREVIOUS_PARAMS);%>
<%String createFormUrl=controlPath + "/createcustomer"; if(previousParams != null) createFormUrl=createFormUrl + "?" + previousParams;%>

<p class="head1">Request a New Account</p>
<br>
<p>If you already have an account, use your browser's Back button to return to the Login page and log in from there.</p>
<%String fontColor = "Black";%>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign="middle" align="left">
            <p class="head2"><font color="white">&nbsp;Name and Shipping Address</font>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
<form method="post" action="<ofbiz:url>/createcustomer</ofbiz:url>" name="newuserform" style='margin:0;'>
<table width="100%" border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Title</font></div></td>
    <td width="74%">
      <input type="text" name="USER_TITLE" value="<%=UtilFormatOut.checkNull(request.getParameter("USER_TITLE"))%>" size="10" maxlength="30">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>First name</font></div></td>
    <td width="74%">
      <input type="text" name="USER_FIRST_NAME" value="<%=UtilFormatOut.checkNull(request.getParameter("USER_FIRST_NAME"))%>" size="30" maxlength="30">
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Middle initial</font></div></td>
    <td width="74%">
        <input type="text" name="USER_MIDDLE_NAME" value="<%=UtilFormatOut.checkNull(request.getParameter("USER_MIDDLE_NAME"))%>" size="4" maxlength="4">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Last name </font></div></td>
    <td width="74%">
      <input type="text" name="USER_LAST_NAME" value="<%=UtilFormatOut.checkNull(request.getParameter("USER_LAST_NAME"))%>" size="30" maxlength="30">
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Suffix</font></div></td>
    <td width="74%">
      <input type="text" name="USER_SUFFIX" value="<%=UtilFormatOut.checkNull(request.getParameter("USER_SUFFIX"))%>" size="10" maxlength="30">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Address Line 1</font></div></td>
    <td width="74%">
      <input type="text" name="CUSTOMER_ADDRESS1" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_ADDRESS1"))%>" size="30" maxlength="30">
    *</td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Address Line 2</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_ADDRESS2" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_ADDRESS2"))%>" size="30" maxlength="30">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>City</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_CITY" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_CITY"))%>" size="30" maxlength="30">
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>State/Province</font></div></td>
    <td width="74%">
      <select name="CUSTOMER_STATE">
          <option><%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_STATE"))%></option>
          <%@ include file="/includes/states.jsp" %>
      </select>
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Zip/Postal Code</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_POSTAL_CODE" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_POSTAL_CODE"))%>" size="12" maxlength="10">
    * </td>
  </tr>
  <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Country</font></div></td>
      <td width="74%">
          <select name="CUSTOMER_COUNTRY" >
            <option><%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_COUNTRY"))%></option>
            <%@ include file="/includes/countries.jsp" %>
          </select>
      * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Allow Address Solicitation?</font></div></td>
    <td width="74%">
      <select name="CUSTOMER_ADDRESS_ALLOW_SOL">
        <option><%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_ADDRESS_ALLOW_SOL"), "Y")%></option>
        <option></option><option>Y</option><option>N</option>
      </select>
    </td>
  </tr>
</table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign="middle" align="left">
            <p class="head2"><font color="white">&nbsp;Phone Numbers</font>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
<table width="100%">
  <tr>
    <td colspan='2'>All phone numbers: [Country Code] [Area Code] [Contact Number] [Extension]</td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Home phone<BR>(allow solicitation?)</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_HOME_COUNTRY" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_HOME_COUNTRY"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="CUSTOMER_HOME_AREA" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_HOME_AREA"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="CUSTOMER_HOME_CONTACT" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_HOME_CONTACT"))%>" size="15" maxlength="15">
        &nbsp;ext&nbsp;<input type="text" name="CUSTOMER_HOME_EXT" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_HOME_EXT"))%>" size="6" maxlength="10">
        <BR>
        <select name="CUSTOMER_HOME_ALLOW_SOL">
          <option><%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_HOME_ALLOW_SOL"), "Y")%></option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Business phone<BR>(allow solicitation?)</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_WORK_COUNTRY" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_WORK_COUNTRY"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="CUSTOMER_WORK_AREA" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_WORK_AREA"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="CUSTOMER_WORK_CONTACT" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_WORK_CONTACT"))%>" size="15" maxlength="15">
        &nbsp;ext&nbsp;<input type="text" name="CUSTOMER_WORK_EXT" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_WORK_EXT"))%>" size="6" maxlength="10">
        <BR>
        <select name="CUSTOMER_WORK_ALLOW_SOL">
          <option><%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_WORK_ALLOW_SOL"), "Y")%></option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Fax number<BR>(allow solicitation?)</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_FAX_COUNTRY" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_FAX_COUNTRY"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="CUSTOMER_FAX_AREA" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_FAX_AREA"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="CUSTOMER_FAX_CONTACT" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_FAX_CONTACT"))%>" size="15" maxlength="15">
        <BR>
        <select name="CUSTOMER_FAX_ALLOW_SOL">
          <option><%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_FAX_ALLOW_SOL"), "Y")%></option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Mobile phone<BR>(allow solicitation?)</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_MOBILE_COUNTRY" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_MOBILE_COUNTRY"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="CUSTOMER_MOBILE_AREA" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_MOBILE_AREA"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="CUSTOMER_MOBILE_CONTACT" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_MOBILE_CONTACT"))%>" size="15" maxlength="15">
        <BR>
        <select name="CUSTOMER_MOBILE_ALLOW_SOL">
          <option><%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_MOBILE_ALLOW_SOL"), "Y")%></option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
</table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign="middle" align="left">
            <p class="head2"><font color="white">&nbsp;Email Address</font>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
<table width="100%">
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Email address<BR>(allow solicitation?)</font></div></td>
    <td width="74%">
        <input type="text" name="CUSTOMER_EMAIL" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_EMAIL"))%>" size="60" maxlength="255"> *
        <BR>
        <select name="CUSTOMER_EMAIL_ALLOW_SOL">
          <option><%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_EMAIL_ALLOW_SOL"), "Y")%></option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
<%--
  <tr>
    <td width="26%">
        <div class="tabletext"><font color='<%=fontColor%>'>Order Email addresses (comma separated)</font></div>
    </td>
    <td width="74%">
        <input type="text" name="CUSTOMER_ORDER_EMAIL" value="<%=UtilFormatOut.checkNull(request.getParameter("CUSTOMER_ORDER_EMAIL"))%>" size="40" maxlength="80">
    </td>
  </tr>
--%>
</table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>

<TABLE border=0 width='100%' cellpadding='<%=boxBorderWidth%>' cellspacing=0 bgcolor='<%=boxBorderColor%>'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxTopPadding%>' cellspacing='0' bgcolor='<%=boxTopColor%>'>
        <tr>
          <td valign="middle" align="left">
            <p class="head2"><font color="white">&nbsp;Username and Password</font>
          </td>
          <td>
            &nbsp;
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellpadding='<%=boxBottomPadding%>' cellspacing='0' bgcolor='<%=boxBottomColor%>'>
        <tr>
          <td>
  <table width="100%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Username</font></div></td>
      <td width="74%">
          <input type="text" name="USERNAME" value="<%=UtilFormatOut.checkNull(request.getParameter("USERNAME"))%>" size="20" maxlength="50">
      * </td>
    </tr>
    <% if(UtilProperties.propertyValueEqualsIgnoreCase(contextRoot + "/WEB-INF/ecommerce.properties", "create.allow.password", "true")) { pageContext.setAttribute("createAllowPassword", "true"); }%>
    <ofbiz:if name="createAllowPassword">
      <tr>
        <td width="26%">
            <div class="tabletext"><font color='<%=fontColor%>'>Password</font></div>
        </td>
        <td width="74%">
            <input type="password" name="PASSWORD" value="" size="20" maxlength="50">
          * </td>
      </tr>
      <tr>
        <td width="26%">
            <div class="tabletext"><font color='<%=fontColor%>'>Repeat password to confirm</font></div>
        </td>
        <td width="74%">
            <input type="password" name="CONFIRM_PASSWORD" value="" size="20" maxlength="50">
        * </td>
      </tr>
      <tr>
        <td width="26%">
            <div class="tabletext"><font color='<%=fontColor%>'>Password Hint</font></div>
        </td>
        <td width="74%">
            <input type="text" name="PASSWORD_HINT" value="<%=UtilFormatOut.checkNull(request.getParameter("PASSWORD_HINT"))%>" size="40" maxlength="100">
        </td>
      </tr>
    </ofbiz:if>
    <ofbiz:unless name="createAllowPassword">
      <tr>
        <td width="26%">
            <div class="tabletext"><font color='<%=fontColor%>'>Password</font></div>
        </td>
        <td>
           <div class="commentary">You will receive a password by email when your new account is approved.</div>
        </td>
      </tr>
    </ofbiz:unless>
  </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

</form>

<br><div class="commentary">Fields marked with (*) are required.</div>

<a href="/login" class="buttontext">&nbsp;&nbsp;[Back]</a>
<a href="javascript:document.newuserform.submit()" class="buttontext">&nbsp;&nbsp;[Save]</a>
<br>
<br>
<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
