<%
    /**
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
     * @author     Andy Zeneski
     * @version    $Revision$
     * @since      2.0
     */
%>

<%	
	String workEffortStatus = null;
	if (workEffortId != null && assignPartyId != null && assignRoleTypeId != null && fromDate != null) { 		
		Map fields = UtilMisc.toMap("workEffortId", workEffortId, "partyId", assignPartyId, "roleTypeId", assignRoleTypeId, "fromDate", fromDate);
	    GenericValue wepa = delegator.findByPrimaryKey("WorkEffortPartyAssignment", fields);
	    Debug.logError(""+wepa);	    
	    if (wepa != null && wepa.get("statusId") != null && wepa.getString("statusId").equals("CAL_ACCEPTED")) {	    	
    		GenericValue workEffort = delegator.findByPrimaryKey("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId));      		
    		workEffortStatus = workEffort.getString("currentStatusId");
    		if (workEffortStatus != null) pageContext.setAttribute("workEffortStatus", workEffortStatus);
    		if (workEffort != null) {
    			if ((delegate != null && delegate.equals("true")) || (workEffortStatus != null && workEffortStatus.equals("WF_RUNNING"))) {    				
    				Map actFields = UtilMisc.toMap("packageId", workEffort.getString("workflowPackageId"), "packageVersion", workEffort.getString("workflowPackageVersion"), "processId", workEffort.getString("workflowProcessId"), "processVersion", workEffort.getString("workflowProcessVersion"), "activityId", workEffort.getString("workflowActivityId"));
    				GenericValue activity = delegator.findByPrimaryKey("WorkflowActivity", actFields);  
    				if (activity != null) {
    					List transitions = activity.getRelated("FromWorkflowTransition", null, UtilMisc.toList("-transitionId"));
    					if (transitions != null) pageContext.setAttribute("wfTransitions", transitions);
    				}
    			}
    		}
    	}
    }
%>

<ofbiz:if name="wfTransitions">
<TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
  <TR>
    <TD width='100%'>
      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
        <tr>
          <td valign="middle" align="left">
            <div class="boxhead">&nbsp;Processing Transitions</div>
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
            <form action="<ofbiz:url>/completeassignment</ofbiz:url>" method="post" name="activityForm">
              <input type="hidden" name="workEffortId" value="<%=workEffortId%>">
              <input type="hidden" name="partyId" value="<%=assignPartyId%>">
              <input type="hidden" name="roleTypeId" value="<%=assignRoleTypeId%>">
              <input type="hidden" name="fromDate" value="<%=fromDate%>">             
              <table>
                <tr>
                  <td>
                    <select name="approvalCode" style="font-size: x-small;">
                      <ofbiz:iterator name="trans" property="wfTransitions">
                        <% Map attrs = StringUtil.strToMap(trans.getString("extendedAttributes")); %>
                        <% if (attrs.containsKey("approvalCode")) { %>
                          <option value="<%=attrs.get("approvalCode")%>"><%=trans.getString("transitionName")%></option>
                        <% } %>
                      </ofbiz:iterator>
                    </select> 
                  </td>
                  <td valign="center">                                        
                    <a href="javascript:document.activityForm.submit()" class="buttontext">[Continue]</a>
                  </td>
                </tr>
              </table>
            </form>                   
          </td>
        </tr>
      </table>
    </TD>
  </TR>
</TABLE>
</ofbiz:if>

<ofbiz:unless name="wfTransitions">
  <ofbiz:if name="workEffortStatus" value="WF_SUSPENDED">
  <TABLE border=0 width='100%' cellspacing='0' cellpadding='0' class='boxoutside'>
    <TR>
      <TD width='100%'>
        <table width='100%' border='0' cellspacing='0' cellpadding='0' class='boxtop'>
          <tr>
            <td valign="middle" align="left">
              <div class="boxhead">&nbsp;Processing Status</div>
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
              <form action="<ofbiz:url>/releasehold</ofbiz:url>" method="post" name="activityForm">
                <input type="hidden" name="workEffortId" value="<%=workEffortId%>">                        
                <table width="98%">
                  <tr>
                    <td>
                      <div class="tabletext">This order is currently in a 'Hold' state. The activity has been suspended.</div>
                      <div class="tabletext">&nbsp;** Note: If this state is a result of an automated activity, releasing may not have an effect until all conditions are met.</div>                     
                    </td>
                    <td align="right" valign="center">                                        
                      <a href="javascript:document.activityForm.submit()" class="buttontext">[Release Hold]</a>
                    </td>
                  </tr>
                </table>
              </form>                   
            </td>
          </tr>
        </table>
      </TD>
    </TR>
  </TABLE>  
  </ofbiz:if>
</ofbiz:unless>
