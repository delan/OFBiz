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
            <div class='boxhead'>&nbsp;Order Manager Main Page</div>
          </TD>
          <TD align=right width='10%'>&nbsp;</TD>
        </TR>
      </TABLE>
    </TD>
  </TR>
  <TR>
    <TD bgcolor='white'>
<DIV class='tabletext'>For something interesting make sure you are logged in, try username:admin, password:ofbiz.</DIV>
<BR>
<DIV class='tabletext'>This application is primarily intended for those repsonsible for the maintenance of Order Manager related information.</DIV>
    </TD>
  </TR>
</TABLE>

<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
