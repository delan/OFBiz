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

<%@ page import="org.ofbiz.commonapp.common.*" %>
<%@ page import="org.ofbiz.commonapp.security.*" %>

<%@ taglib uri="/WEB-INF/webevent.tld" prefix="webevent" %>
<webevent:dispatch loginRequired="true" />

<% pageContext.setAttribute("PageName", "entitymaint"); %> 

<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/onecolumn.jsp" %> 

<h2 style='margin:0;'>Entity Maintenance</h2>
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

  <%if(Security.hasEntityPermission("PERSON", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>Person</TD>
      <TD>
        <%if(Security.hasEntityPermission("PERSON", "_CREATE", session)){%>
          <a href='person/EditPerson.jsp' class='buttontext'>Create</a>
        <%}%>
      </TD>
      <TD><a href='person/FindPerson.jsp' class='buttontext'>Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("PERSON_ATTRIBUTE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PersonAttribute</TD>
      <TD>
        <%if(Security.hasEntityPermission("PERSON_ATTRIBUTE", "_CREATE", session)){%>
          <a href='person/EditPersonAttribute.jsp' class='buttontext'>Create</a>
        <%}%>
      </TD>
      <TD><a href='person/FindPersonAttribute.jsp' class='buttontext'>Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("PERSON_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PersonType</TD>
      <TD>
        <%if(Security.hasEntityPermission("PERSON_TYPE", "_CREATE", session)){%>
          <a href='person/EditPersonType.jsp' class='buttontext'>Create</a>
        <%}%>
      </TD>
      <TD><a href='person/FindPersonType.jsp' class='buttontext'>Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("PERSON_TYPE_ATTRIBUTE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PersonTypeAttribute</TD>
      <TD>
        <%if(Security.hasEntityPermission("PERSON_TYPE_ATTRIBUTE", "_CREATE", session)){%>
          <a href='person/EditPersonTypeAttribute.jsp' class='buttontext'>Create</a>
        <%}%>
      </TD>
      <TD><a href='person/FindPersonTypeAttribute.jsp' class='buttontext'>Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("PERSON_PERSON_TYPE", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PersonPersonType</TD>
      <TD>
        <%if(Security.hasEntityPermission("PERSON_PERSON_TYPE", "_CREATE", session)){%>
          <a href='person/EditPersonPersonType.jsp' class='buttontext'>Create</a>
        <%}%>
      </TD>
      <TD><a href='person/FindPersonPersonType.jsp' class='buttontext'>Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SecurityGroup</TD>
      <TD>
        <%if(Security.hasEntityPermission("SECURITY_GROUP", "_CREATE", session)){%>
          <a href='security/securitygroup/EditSecurityGroup.jsp' class='buttontext'>Create</a>
        <%}%>
      </TD>
      <TD><a href='security/securitygroup/FindSecurityGroup.jsp' class='buttontext'>Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("SECURITY_PERMISSION", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SecurityPermission</TD>
      <TD>
        <%if(Security.hasEntityPermission("SECURITY_PERMISSION", "_CREATE", session)){%>
          <a href='security/securitygroup/EditSecurityPermission.jsp' class='buttontext'>Create</a>
        <%}%>
      </TD>
      <TD><a href='security/securitygroup/FindSecurityPermission.jsp' class='buttontext'>Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>SecurityGroupPermission</TD>
      <TD>
        <%if(Security.hasEntityPermission("SECURITY_GROUP_PERMISSION", "_CREATE", session)){%>
          <a href='security/securitygroup/EditSecurityGroupPermission.jsp' class='buttontext'>Create</a>
        <%}%>
      </TD>
      <TD><a href='security/securitygroup/FindSecurityGroupPermission.jsp' class='buttontext'>Find</a></TD>
    </TR>
  <%}%>
  <%if(Security.hasEntityPermission("PERSON_SECURITY_GROUP", "_VIEW", session)){%>
    <%rowColor=(rowColor==rowColor1?rowColor2:rowColor1);%><tr bgcolor="<%=rowColor%>">
      <TD>PersonSecurityGroup</TD>
      <TD>
        <%if(Security.hasEntityPermission("PERSON_SECURITY_GROUP", "_CREATE", session)){%>
          <a href='security/person/EditPersonSecurityGroup.jsp' class='buttontext'>Create</a>
        <%}%>
      </TD>
      <TD><a href='security/person/FindPersonSecurityGroup.jsp' class='buttontext'>Find</a></TD>
    </TR>
  <%}%>
</TABLE>
<%@ include file="/includes/onecolumnclose.jsp" %>
<%@ include file="/includes/footer.jsp" %>
