<%
/**
 *  Title: Main Page
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
<%@ page import="org.ofbiz.core.security.*" %>

<% pageContext.setAttribute("PageName", "Main Page"); %> 

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %>
<BR>
<TABLE width='100%' cellpadding='4' cellspacing='1' border='0' bgcolor='black'>
  <TR>
    <TD bgcolor='#678475'>
      <TABLE width='100%' cellpadding='0' cellspacing='0' border='0'>
        <TR>
          <TD align=left width='90%' >
            <div class='boxhead'>&nbsp;Catalog Administration Main Page</div>
          </TD>
          <TD align=right width='10%'>&nbsp;</TD>
        </TR>
      </TABLE>
    </TD>
  </TR>
  <TR>
    <TD bgcolor='white'>
<%if(userLogin == null) {%>
  <DIV class='tabletext'>For something interesting make sure you are logged in, try username:admin, password:ofbiz.</DIV>
<%}%>
<BR>
<%if(security.hasEntityPermission("CATALOG", "_VIEW", session)) {%>
  <A href='<ofbiz:url>/EditCategory</ofbiz:url>' class='buttontext'>Create New Category</A>
  <FORM method=POST action='<ofbiz:url>/EditCategory</ofbiz:url>' style='margin-top: 0;'>
    <INPUT type=text size='20' maxlength='20' name='PRODUCT_CATEGORY_ID' value=''>
    <INPUT type=submit value='Edit Category'>
  </FORM>
  <A href='<ofbiz:url>/EditProduct</ofbiz:url>' class='buttontext'>Create New Product</A>
  <FORM method=POST action='<ofbiz:url>/EditProduct</ofbiz:url>' style='margin: 0;'>
    <INPUT type=text size='20' maxlength='20' name='PRODUCT_ID' value=''>
    <INPUT type=submit value='Edit Product'>
  </FORM>
  <A href='<ofbiz:url>/UpdateAllKeywords</ofbiz:url>' class='buttontext'>Auto-Create Keywords for All Products</A>
<BR><BR>
<%}%>
<DIV class='tabletext'>This application is primarily intended for those repsonsible for the maintenance of product catalog related information.</DIV>
    </TD>
  </TR>
</TABLE>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
