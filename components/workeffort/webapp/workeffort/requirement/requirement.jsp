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
<%@ page import="org.ofbiz.security.*, org.ofbiz.entity.*, org.ofbiz.base.util.*, org.ofbiz.webapp.pseudotag.*" %>

<%@ taglib uri="ofbizTags" prefix="ofbiz" %>
<jsp:useBean id="delegator" type="org.ofbiz.entity.GenericDelegator" scope="request" />

<%
    GenericValue requirement = null;
    String donePage = request.getParameter("donePage");
    String requirementId = request.getParameter("requirementId");
    String custRequestId = request.getParameter("custRequestId");
    String custRequestItemSeqId = request.getParameter("custRequestItemSeqId");
    if (requirementId == null) requirementId = (String) request.getAttribute("requirementId");
    if (requirementId != null) {
        requirement = delegator.findByPrimaryKey("Requirement", UtilMisc.toMap("requirementId", requirementId));
        pageContext.setAttribute("requirementId", requirementId);
        pageContext.setAttribute("tryEntity", new Boolean(true));
    } else {
    	pageContext.setAttribute("tryEntity", new Boolean(false));
    }
    if (requirement != null) pageContext.setAttribute("requirement", requirement);
    if (donePage == null) donePage = "requirement";
    
    if (requirement != null && custRequestId == null && custRequestItemSeqId == null) {
    	List reqRequests = delegator.findByAnd("RequirementCustRequest", UtilMisc.toMap("requirementId", requirementId));
    	if (reqRequests != null) pageContext.setAttribute("requirementRequests", reqRequests);
    }

    Collection requirementTypes = delegator.findAll("RequirementType", UtilMisc.toList("description", "requirementTypeId"));
    if (requirementTypes != null && requirementTypes.size() > 0) pageContext.setAttribute("requirementTypes", requirementTypes);
%>

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
            <ofbiz:if name="requirement">
              <A href='<ofbiz:url>/workefforts?requirementId=<%=requirementId%></ofbiz:url>' class='lightbuttontext'>[Requirement&nbsp;Tasks]</A>
              <A href='<ofbiz:url>/task?requirementId=<%=requirementId%></ofbiz:url>' class='lightbuttontext'>[Add&nbsp;Task]</A>              
            </ofbiz:if>
            <ofbiz:unless name="requirement">
              <span class="lightbuttontextdisabled">[Requirement&nbsp;Tasks]</span>
              <span class="lightbuttontextdisabled">[Add&nbsp;Task]</span>              
            </ofbiz:unless>
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
                <form name="requirementForm" action="<ofbiz:url>/updaterequirement/<%=donePage%></ofbiz:url>" method=POST style='margin: 0;'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <input type='hidden' name='requirementId' value='<ofbiz:print attribute="requirementId"/>'>
              </ofbiz:if>
              <ofbiz:unless name="requirement">
              <form name="requirementForm" action="<ofbiz:url>/createrequirement/<%=donePage%></ofbiz:url>" method=POST style='margin: 0;'>
                <table border='0' cellpadding='2' cellspacing='0'>
                  <ofbiz:if name="requirementId">
                    <DIV class='tabletext'>ERROR: Could not find Requirement with ID "<ofbiz:print attribute="requirementId"/>"</DIV>
                  </ofbiz:if>
              </ofbiz:unless>
              
              <% if (custRequestId != null && custRequestItemSeqId != null) { %>
                <input type="hidden" name="custRequestId" value="<%=custRequestId%>">
                <input type="hidden" name="custRequestItemSeqId" value="<%=custRequestItemSeqId%>">
              <% } %>              

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
                    <select name="requirementTypeId" class="selectBox">
                      <ofbiz:iterator name="requirementType" property="requirementTypes">
                        <option value="<%=requirementType.getString("requirementTypeId")%>"><%=requirementType.getString("description")%></option>
                      </ofbiz:iterator>
                    </select>
                  </td>
                </tr>
                                  
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Description</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='40' maxlength='255' name='description' value='<ofbiz:inputvalue field="description" entityAttr="requirement"/>'></td>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Facility ID</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='20' maxlength='20' name='facilityId' value='<ofbiz:inputvalue field="facilityId" entityAttr="requirement"/>'></td>
                </tr>
                
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Product ID</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='20' maxlength='20' name='productId' value='<ofbiz:inputvalue field="productId" entityAttr="requirement" tryEntityAttr="tryEntity"/>'></td>
                </tr>
                
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Use Case</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><textarea name='story' class='textAreaBox' cols='50' rows='10'><ofbiz:inputvalue field="useCase" entityAttr="requirement"/></TEXTAREA>
                </tr>

                 <tr>
                  <td width='26%' align=right><div class='tabletext'>Reason</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='50' maxlength='255' name='reason' value='<ofbiz:inputvalue field="reason" entityAttr="requirement"/>'></td>
                </tr>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Required By Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'>
                    <input type='text' class='inputBox' size='30' maxlength='30' name='requiredByDate' value='<ofbiz:inputvalue field="requiredByDate" entityAttr="requirement"/>'>
                    <a href="javascript:call_cal(document.requirementForm.requiredByDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Click here For Calendar'></a>
                  </td>
                </tr>

                <ofbiz:if name="requirement">
                <tr>
                  <td width='26%' align=right><div class='tabletext'>Created Date/Time</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><span class="tabletext"><ofbiz:inputvalue field="createdDate" entityAttr="requirement"/></span></td>
                </tr>
                </ofbiz:if>

                <tr>
                  <td width='26%' align=right><div class='tabletext'>Estimated Budget</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='10' maxlength='30' name='estimatedBudget' value='<ofbiz:inputvalue field="estimatedBudget" entityAttr="requirement"/>'></td>
                </tr>

               <tr>
                  <td width='26%' align=right><div class='tabletext'>Quantity</div></td>
                  <td>&nbsp;</td>
                  <td width='74%'><input type='text' class='inputBox' size='5' maxlength='30' name='quantity' value='<ofbiz:inputvalue field="quantity" entityAttr="requirement" tryEntityAttr="tryEntity"/>'></td>
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

<ofbiz:if name="requirementRequests">
<br>
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <TD align=left width='40%' >
            <div class='boxhead'>&nbsp;Requirement Requests</div>
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
                  <TD>
                    <table width="100%" cellpadding="2" cellspacing="0" border="0">
                      <tr>
                        <td><div class="tableheadtext">RequestID</div></td>
                        <td><div class="tableheadtext">Request Type</div></td>
                        <td><div class="tableheadtext">Priority</div></td>
                        <td><div class="tableheadtext">Status</div></td>
                        <td><div class="tableheadtext">Request Name</div></td>
                        <td><div class="tableheadtext">Description</div></td>
                        <td><div class="tableheadtext">Requested Date</div></td>                                               
                        <td>&nbsp;</td>
                      </tr>
                      <tr>
                        <td colspan="8"><hr class="sepbar"></td>
                      </tr>
                      <ofbiz:iterator name="reqReq" property="requirementRequests">
                        <% GenericValue custRequest = reqReq.getRelatedOne("CustRequest"); %>
                          <tr>
                            <td><div class="tabletext"><%=custRequest.getString("custRequestId")%></div></td>
                            <td><div class="tabletext"><%=UtilFormatOut.checkNull(custRequest.getString("custRequestTypeId"))%></div></td>
                            <td><div class="tabletext"><%=UtilFormatOut.checkNull(custRequest.getString("priority"))%></div></td>
                            <td><div class="tabletext"><%=UtilFormatOut.checkNull(custRequest.getString("statusId"))%></div></td>
                            <td><div class="tabletext"><%=UtilFormatOut.checkNull(custRequest.getString("custRequestName"))%></div></td>                            
                            <td><div class="tabletext"><%=UtilFormatOut.checkNull(custRequest.getString("description"))%></div></td>
                            <td><div class="tabletext"><%=UtilFormatOut.checkNull(custRequest.getString("custRequestDate"))%></div></td>
                            <td align="right"><div class="tabletext"><a href="<ofbiz:url>/request?custRequestId=<%=custRequest.getString("custRequestId")%>&custRequestItemSeqId=<%=reqReq.getString("custRequestItemSeqId")%></ofbiz:url>" class="buttontext">[View]</a></td>
                          </tr>
                      </ofbiz:iterator>
                    </table>
                  </TD>
                </TR>                             
              </TABLE>                 
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
</ofbiz:if>          