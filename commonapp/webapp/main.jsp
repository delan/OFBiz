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

<%@ page import="java.sql.*, java.util.*" %>
<%@ page import="org.ofbiz.core.entity.*" %>
<%@ page import="org.ofbiz.core.util.*" %>
<%@ page import="org.ofbiz.core.security.*" %>
<%@ taglib uri="ofbizTags" prefix="ofbiz" %>

<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<ofbiz:service name="testScv">
  <ofbiz:param name="message" value="hello all!"/>
  <ofbiz:param name="resp" mode="OUT" alias="respMgs"/>
</ofbiz:service>

<%
    String helperName = delegator.getEntityHelperName("ProductPrice");
    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet result = null;

    Debug.logError("\n\n----------------------------------\n\n");
    try {
        con = ConnectionFactory.getConnection(helperName);
        stmt = con.prepareStatement("SELECT * FROM product_price WHERE price >= ?");
        stmt.setDouble(1, 50.00);
        result = stmt.executeQuery();
        Debug.logInfo(""+result);
    } catch (SQLException e) {
        Debug.logError(e.getMessage());
    } catch (Throwable t) {
        Debug.logError(t);
    }

    Debug.logError("\n\n----------------------------------\n\n");
    try {
        List testList = delegator.findByAnd("ProductPrice", UtilMisc.toList(new EntityExpr("price", EntityOperator.GREATER_THAN_EQUAL_TO, new Double(50.00)))); 
        Debug.logInfo(""+testList);
    } catch (GenericEntityException e) {
        Debug.logError(e.getMessage());
    } catch (Throwable t) {
        Debug.logError(t);
    }
%>

<% if (Debug.verboseOn()) Debug.logVerbose("Response Message: " + pageContext.findAttribute("respMgs")); %>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='90%' >
            <div class='boxhead'>&nbsp;Commonapp Main Page</div>
          </TD>
          <TD align=right width='10%'>&nbsp;</TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
            <ofbiz:unless name="userLogin">
              <DIV class='tabletext'>For something interesting make sure you are logged in, try username:admin, password:ofbiz.</DIV>
              <BR>
            </ofbiz:unless>
            <DIV class='tabletext'>This web application is empty. It is now only used for remove services access (ie SOAP accress to services).</DIV>
            <DIV class='tabletext'>All of the stuff that was in the commonapp webapp is now in WebTools, check it out there.</DIV>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
