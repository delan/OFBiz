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
<%@ page import="org.ofbiz.commonapp.security.*" %>

<%String controlPath=(String)request.getAttribute(SiteDefs.CONTROL_PATH);%>
<% pageContext.setAttribute("PageName", "entitymaint"); %> 

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<h2 style='margin:0;'>Entity Maintenance</h2>
<%if(Security.hasPermission("ENTITY_MAINT", session)){%>
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

  <%if(Security.hasEntityPermission("PARTY", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>Party</TD>
      <TD>
        <%if(Security.hasEntityPermission("PARTY", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewParty")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindParty")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyClassification</TD>
      <TD>
        <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassification")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyClassification")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyClassificationType</TD>
      <TD>
        <%if(Security.hasEntityPermission("PARTY_CLASSIFICATION_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyClassificationType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyClassificationType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("PARTY_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyType</TD>
      <TD>
        <%if(Security.hasEntityPermission("PARTY_TYPE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyType")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyType")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("PARTY_ATTRIBUTE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyAttribute</TD>
      <TD>
        <%if(Security.hasEntityPermission("PARTY_ATTRIBUTE", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyAttribute")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyAttribute")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("PARTY_TYPE_ATTR", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PartyTypeAttr</TD>
      <TD>
        <%if(Security.hasEntityPermission("PARTY_TYPE_ATTR", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewPartyTypeAttr")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindPartyTypeAttr")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("USER_LOGIN", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>UserLogin</TD>
      <TD>
        <%if(Security.hasEntityPermission("USER_LOGIN", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewUserLogin")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindUserLogin")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>LoginAccountHistory</TD>
      <TD>
        <%if(Security.hasEntityPermission("LOGIN_ACCOUNT_HISTORY", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewLoginAccountHistory")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindLoginAccountHistory")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SecurityGroup</TD>
      <TD>
        <%if(Security.hasEntityPermission("SECURITY_GROUP", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewSecurityGroup")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindSecurityGroup")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("SECURITY_PERMISSION", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SecurityPermission</TD>
      <TD>
        <%if(Security.hasEntityPermission("SECURITY_PERMISSION", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewSecurityPermission")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindSecurityPermission")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SecurityGroupPermission</TD>
      <TD>
        <%if(Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_CREATE", session)){%>
          <a href="<%=response.encodeURL(controlPath + "/ViewSecurityGroupPermission")%>" class="buttontext">Create</a>
        <%}%>
      </TD>
      <TD><a href="<%=response.encodeURL(controlPath + "/FindSecurityGroupPermission")%>" class="buttontext">Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>UserLoginSecurityGroup</TD>
      <TD>
        <%if(Security.hasEntityPermission("USER_LOGIN_SECURITY_GROUP", "_CREATE", session)){%>
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
