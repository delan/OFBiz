<%
/**
 *  Title: Afilliate Register Page
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
 *@author     Andy Zeneski (jaz@zsolv.com)
 *@author     David E. Jones (jonesde@yahoo.com)
 *@created    January 24, 2002
 *@version    1.0
 */
%>

<%pageContext.setAttribute("PageName", "newaffiliate");%>
<%@ include file="/includes/envsetup.jsp" %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<%String previousParams=(String)session.getAttribute(SiteDefs.PREVIOUS_PARAMS);%>
<%String createFormUrl=controlPath + "/createcustomer"; if(previousParams != null) createFormUrl=createFormUrl + "?" + previousParams;%>

<p class="head1">Request A New Affiliate Account</p>
<br>
<p>If you already have an account, use your browser's Back button to return to the Login page and log in from there.</p>
<%String fontColor = "Black";%>


<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <p class="head2"><font color="white">&nbsp;Mailing Address</font>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
<form method="post" action="<ofbiz:url>/createAffiliate</ofbiz:url>" name="newuserform" style='margin:0;'>
<table width="100%" border="0" cellpadding="2" cellspacing="0">
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Company name</font></div></td>
    <td width="74%">
      <input type="text" name="COMPANY_NAME" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_NAME"))%>" size="30" maxlength="60">
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Attention to</font></div></td>
    <td width="74%">
      <input type="text" name="COMPANY_ATTN" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_ATTN"))%>" size="30" maxlength="60">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Address Line 1</font></div></td>
    <td width="74%">
      <input type="text" name="COMPANY_ADDRESS1" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_ADDRESS1"))%>" size="30" maxlength="30">
    *</td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Address Line 2</font></div></td>
    <td width="74%">
        <input type="text" name="COMPANY_ADDRESS2" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_ADDRESS2"))%>" size="30" maxlength="30">
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>City</font></div></td>
    <td width="74%">
        <input type="text" name="COMPANY_CITY" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_CITY"))%>" size="30" maxlength="30">
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>State/Province</font></div></td>
    <td width="74%">
      <select name="COMPANY_STATE">
          <option><%=UtilFormatOut.checkNull(request.getParameter("COMPANY_STATE"))%></option>
          <%@ include file="/includes/states.jsp" %>
      </select>
    * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Zip/Postal Code</font></div></td>
    <td width="74%">
        <input type="text" name="COMPANY_POSTAL_CODE" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_POSTAL_CODE"))%>" size="12" maxlength="10">
    * </td>
  </tr>
  <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Country</font></div></td>
      <td width="74%">
          <select name="COMPANY_COUNTRY" >
            <option><%=UtilFormatOut.checkNull(request.getParameter("COMPANY_COUNTRY"))%></option>
            <%@ include file="/includes/countries.jsp" %>
          </select>
      * </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Allow Address Solicitation?</font></div></td>
    <td width="74%">
      <select name="COMPANY_ADDRESS_ALLOW_SOL">
        <option><%=UtilFormatOut.checkNull(request.getParameter("COMPANY_ADDRESS_ALLOW_SOL"), "Y")%></option>
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

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <p class="head2"><font color="white">&nbsp;Primary Contact</font>
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
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
  <table width="100%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>First name</font></div></td>
      <td width="74%">
          <input type="text" name="CONTACT_FIRST_NAME" value="<%=UtilFormatOut.checkNull(request.getParameter("CONTACT_FIRST_NAME"))%>" size="30" maxlength="30">
      * </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Last name</font></div></td>
      <td width="74%">
          <input type="text" name="CONTACT_LAST_NAME" value="<%=UtilFormatOut.checkNull(request.getParameter("CONTACT_LAST_NAME"))%>" size="30" maxlength="30">
      * </td>
    </tr><%--
    <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Company title</font></div></td>
      <td width="74%">
          <input type="text" name="CONTACT_TITLE" value="<%=UtilFormatOut.checkNull(request.getParameter("CONTACT_TITLE"))%>" size="30" maxlength="30">
      * </td>
    </tr>--%>  
  </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
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
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
<table width="100%">
  <tr>
    <td colspan='2'>All phone numbers: [Country Code] [Area Code] [Contact Number] [Extension]</td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Business phone<BR>(allow solicitation?)</font></div></td>
    <td width="74%">
        <input type="text" name="COMPANY_WORK_COUNTRY" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_WORK_COUNTRY"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="COMPANY_WORK_AREA" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_WORK_AREA"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="COMPANY_WORK_CONTACT" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_WORK_CONTACT"))%>" size="15" maxlength="15">
        &nbsp;ext&nbsp;<input type="text" name="COMPANY_WORK_EXT" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_WORK_EXT"))%>" size="6" maxlength="10">
        <BR>
        <select name="COMPANY_WORK_ALLOW_SOL">
          <option><%=UtilFormatOut.checkNull(request.getParameter("COMPANY_WORK_ALLOW_SOL"), "Y")%></option>
          <option></option><option>Y</option><option>N</option>
        </select>
    </td>
  </tr>
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Fax number<BR>(allow solicitation?)</font></div></td>
    <td width="74%">
        <input type="text" name="COMPANY_FAX_COUNTRY" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_FAX_COUNTRY"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="COMPANY_FAX_AREA" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_FAX_AREA"))%>" size="4" maxlength="10">
        -&nbsp;<input type="text" name="COMPANY_FAX_CONTACT" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_FAX_CONTACT"))%>" size="15" maxlength="15">
        <BR>
        <select name="COMPANY_FAX_ALLOW_SOL">
          <option><%=UtilFormatOut.checkNull(request.getParameter("COMPANY_FAX_ALLOW_SOL"), "Y")%></option>
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

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
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
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
<table width="100%">
  <tr>
    <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Email address<BR>(allow solicitation?)</font></div></td>
    <td width="74%">
        <input type="text" name="COMPANY_EMAIL" value="<%=UtilFormatOut.checkNull(request.getParameter("COMPANY_EMAIL"))%>" size="60" maxlength="255"> *
        <BR>
        <select name="COMPANY_EMAIL_ALLOW_SOL">
          <option><%=UtilFormatOut.checkNull(request.getParameter("COMPANY_EMAIL_ALLOW_SOL"), "Y")%></option>
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

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <p class="head2"><font color="white">&nbsp;Web Site Information</font>
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
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
  <table width="100%" border="0" cellpadding="2" cellspacing="0">
    <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Site name</font></div></td>
      <td width="74%">
          <input type="text" name="SITE_NAME" value="<%=UtilFormatOut.checkNull(request.getParameter("SITE_NAME"))%>" size="30" maxlength="30">
      * </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Site URL</font></div></td>
      <td width="74%">
          <input type="text" name="SITE_URL" value="<%=UtilFormatOut.checkNull(request.getParameter("SITE_URL"))%>" size="30" maxlength="60">
      * </td>
    </tr>
    <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Site description</font></div></td>
      <td width="74%">
          <input type="text" name="SITE_DESCRIPTION" value="<%=UtilFormatOut.checkNull(request.getParameter("CONTACT_TITLE"))%>" size="30" maxlength="60">
      * </td>
    </tr>    
    <tr>
      <td width="26%"><div class="tabletext"><font color='<%=fontColor%>'>Affiliate code</font></div></td>
      <td width="74%">
          <input type="text" name="AFFILIATE_ID" value="<%=UtilFormatOut.checkNull(request.getParameter("AFFILIATE_ID"))%>" size="10" maxlength="20">
      * </td>
    </tr>        
  </table>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>

<br>

<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <p class="head2"><font color="white">&nbsp;Affiliate Password</font>
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
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
  <table width="100%" border="0" cellpadding="2" cellspacing="0">
    <% if(UtilProperties.propertyValueEqualsIgnoreCase(application.getResource("/WEB-INF/ecommerce.properties"), "create.allow.password", "true")) { pageContext.setAttribute("createAllowPassword", "true"); }%>
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
