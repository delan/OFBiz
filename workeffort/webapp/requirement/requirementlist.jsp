<%--
 *  Copyright (c) 2002 The Open For Business Project - www.ofbiz.org
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
 *@author     Andy Zeneski
 *@created    July 29, 2002
 *@version    1.0
--%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
    Collection requirements = delegator.findAll("Requirement", UtilMisc.toList("requirementTypeId", "requiredByDate"));
    if (requirements != null && requirements.size() > 0) pageContext.setAttribute("requirements", requirements);
%>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Requirement List</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<ofbiz:url>/requirementlist</ofbiz:url>' class='lightbuttontextdisabled'>[Requirement&nbsp;List]</A>
            <A href='<ofbiz:url>/requirement</ofbiz:url>' class='lightbuttontext'>[New&nbsp;Requirement]</A>
          </TD>
        </tr>
      </table>
    </TD>
  </TR>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxbottom'>
        <tr>
          <td>
              <TABLE width='100%' cellpadding='2' cellspacing='0' border='0'>
                <TR>
                  <TD><DIV class='tabletext'><b>Requirement Type</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Description</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Required By</b></DIV></TD>
                  <TD><DIV class='tabletext'><b>Estmated Budget</b></DIV></TD>
                  <TD align=right><DIV class='tabletext'><b>View Tasks / Edit</b></DIV></TD>
                </TR>
                <TR><TD colspan='5'><HR class='sepbar'></TD></TR>
                <ofbiz:unless name="requirements">
                  <div class="tabletext">No open requirements found.</div>
                </ofbiz:unless>
                <ofbiz:iterator name="requirement" property="requirements">
                  <% GenericValue requirementType = requirement.getRelatedOne("RequirementType"); %>
                  <TR>
                    <TD><DIV class='tabletext'><%=requirementType.getString("description")%></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="requirement" field="description"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="requirement" field="requiredByDate"/></DIV></TD>
                    <TD><DIV class='tabletext'><ofbiz:entityfield attribute="requirement" field="estimatedBudget"/></DIV></TD>

                    <TD align=right NOWRAP><A class="buttontext" href="<ofbiz:url>/workefforts?requirementId=<%=requirement.getString("requirementId")%></ofbiz:url>">Tasks</a>&nbsp;/&nbsp;<A class='buttontext' href='<ofbiz:url>/requirement?requirementId=<ofbiz:entityfield attribute="requirement" field="requirementId"/></ofbiz:url>'>
                        Edit&nbsp;[<ofbiz:entityfield attribute="requirement" field="requirementId"/>]</a></DIV></TD>
                  </TR>
                </ofbiz:iterator>
              </TABLE>
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
