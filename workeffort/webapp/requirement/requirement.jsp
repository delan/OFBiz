<%
/**
 *  Title: Requirement List Page
 *  Description: None
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
 *@created    July 25, 2002
 *@version    1.0
 */
%>

<%@ page import="java.util.*" %>
<%@ page import="org.ofbiz.core.security.*, org.ofbiz.core.entity.*, org.ofbiz.core.util.*, org.ofbiz.core.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.core.entity.GenericDelegator" scope="request" />

<%
    GenericValue requirement = null;
    String requirementId = request.getParameter("requirementId");
    if (requirementId == null) requirementId = (String) request.getAttribute("requirementId");
    if (requirementId != null) {
        requirement = delegator.findByPrimaryKey("Requirement", UtilMisc.toMap("requirementId", requirementId));
        pageContext.setAttribute("requirementId", requirementId);
    }
    if (requirement != null) pageContext.setAttribute("requirement", requirement);

    Collection requirementTypes = delegator.findAll("RequirementType", UtilMisc.toList("description", "requirementTypeId"));
    if (requirementTypes != null && requirementTypes.size() > 0) pageContext.setAttribute("requirementTypes", requirementTypes);
%>

<BR>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Requirement Detail</div>
          </TD>
          <TD align=right width='60%'>
            <A href='<ofbiz:url>/requirementlist</ofbiz:url>' class='lightbuttontext'>[Requirement&nbsp;List]</A>
            <A href='<ofbiz:url>/task?requirementId=<%=requirementId%></ofbiz:url>' class='lightbuttontext'>[Add&nbsp;Task]</A>
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

              <ofbiz:if name="requirement">
                <form action="<ofbiz:url>/updaterequirement</ofbiz:url>" method=POST style='margin: 0;'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='requirementId' value='<ofbiz:print attribute="requirementId"/>'>
              </ofbiz:if>
              <ofbiz:unless name="requirement">
              <form action="<ofbiz:url>/createrequirement</ofbiz:url>" method=POST style='margin: 0;'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <ofbiz:if name="requirementId">
                    <DIV class='tabletext'>ERROR: Could not find Requirement with ID "<ofbiz:print attribute="requirementId"/>"</DIV>
                  </ofbiz:if>
              </ofbiz:unless>

              <ofbiz:if name="requirement">
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Requirement ID</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><span class="tabletext"><b><ofbiz:print attribute="requirementId"/></b></span></td>
                </tr>
              </ofbiz:if>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Type</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <select name="requirementTypeId">
                      <ofbiz:iterator name="requirementType" property="requirementTypes">
                        <option value="<%=requirementType.getString("requirementTypeId")%>"><%=requirementType.getString("description")%></option>
                      </ofbiz:iterator>
                    </select>
                  </td>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Description</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='255' name='description' value='<ofbiz:inputvalue field="description" param="description" entityAttr="requirement" tryEntityAttr="tryEntity"/>'><span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span></td>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Story</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><TEXTAREA name='story' cols='50' rows='10'><ofbiz:inputvalue field="story" param="story" entityAttr="requirement" tryEntityAttr="tryEntity"/></TEXTAREA>
                </tr>

                 <tr>
                  <td width='26%' align=right><div class='tabletext'>Reason</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='50' maxlength='255' name='reason' value='<ofbiz:inputvalue field="reason" param="reason" entityAttr="requirement" tryEntityAttr="tryEntity"/>'></td>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Required By Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='30' maxlength='30' name='requiredByDate' value='<ofbiz:inputvalue field="requiredByDate" param="requiredByDate" entityAttr="requirement" tryEntityAttr="tryEntity"/>'><span class='tabletext'>(YYYY-MM-DD hh:mm:ss)</span></td>
                </tr>

                <ofbiz:if name="requirement">
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Created Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><span class="tabletext"><ofbiz:inputvalue field="requirementCreationDate" param="requirementCreationDate" entityAttr="requirement" tryEntityAttr="tryEntity"/></span></td>
                </tr>
                </ofbiz:if>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Estimated Budget</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='10' maxlength='30' name='estimatedBudget' value='<ofbiz:inputvalue field="estimatedBudget" param="estimatedBudget" entityAttr="requirement" tryEntityAttr="tryEntity"/>'></td>
                </tr>

               <tr>
                  <td width='26%' align=right><div class='tabletext'>Quantity</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' size='5' maxlength='30' name='quantity' value='<ofbiz:inputvalue field="quantity" param="quantity" entityAttr="requirement" tryEntityAttr="tryEntity"/>'></td>
                </tr>

                <tr>
                  <td width='26%' align=right>
                    <ofbiz:if name="requirement"><input type="submit" name="Update" value="Update"></ofbiz:if>
                    <ofbiz:unless name="requirement"><input type="submit" name="Create" value="Create"></ofbiz:unless>
                  </td>
                  <td>&nbsp;</td>
                  <td width='74%'><div class='tabletext'>&nbsp;</div></td>
                </tr>
              </table>
            </form>

          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
