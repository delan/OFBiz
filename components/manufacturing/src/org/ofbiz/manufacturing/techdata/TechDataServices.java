/*
 * $Id: TechDataServices.java,v 1.3 2003/11/27 15:38:03 holivier Exp $
 *
 * Copyright (c) 2003 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.manufacturing.techdata;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * TechDataServices - TechData related Services
 *
 * @author     <a href="mailto:olivier.heintz@nereide.biz">Olivier Heintz</a>
 * @version    $Revision: 1.3 $
 * @since      3.0
 */
public class TechDataServices {
    
    public static final String module = TechDataServices.class.getName();

/**
 * 
 * Used to retreive some RoutingTasks (WorkEffort) selected by Name or MachineGroup ordered by Name
 * 
 * @author holivier
 * @param ctx
 * @param context: a map containing workEffortName (routingTaskName) and fixedAssetId (MachineGroup or ANY) 
 * @return result: a map containing lookupResult (list of RoutingTask <=> workEffortId with currentStatusId = "ROU_ACTIVE" and workEffortTypeId = "ROU_TASK"
 */
    public static Map lookupRoutingTask(DispatchContext ctx, Map context) {
        GenericDelegator delegator = ctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
/*		Security security = ctx.getSecurity();  a completer par la suite */
		Map result = new HashMap();
        
		String workEffortName = (String) context.get("workEffortName");    
		String fixedAssetId = (String) context.get("fixedAssetId");    

        List listRoutingTask = null;
		List constraints = new LinkedList();

        if (workEffortName != null && workEffortName.length()>0) 
			constraints.add(new EntityExpr("workEffortName", EntityOperator.GREATER_THAN_EQUAL_TO, workEffortName));
		if (fixedAssetId != null && fixedAssetId.length()>0 && ! "ANY".equals(fixedAssetId)) 
			constraints.add(new EntityExpr("fixedAssetId", EntityOperator.EQUALS, fixedAssetId));

		constraints.add(new EntityExpr("currentStatusId", EntityOperator.EQUALS, "ROU_ACTIVE"));
		constraints.add(new EntityExpr("workEffortTypeId", EntityOperator.EQUALS, "ROU_TASK"));
		
		try {
			listRoutingTask = delegator.findByAnd("WorkEffort", constraints, UtilMisc.toList("workEffortName"));
		} catch (GenericEntityException e) {
			Debug.logWarning(e, module);
			return ServiceUtil.returnError("Error finding desired WorkEffort records: " + e.toString());
		}
		if (listRoutingTask == null) listRoutingTask = new LinkedList();
		if (listRoutingTask.size() == 0) listRoutingTask.add(UtilMisc.toMap("label","no Match","value","NO_MATCH"));
        result.put("lookupResult", listRoutingTask);
        return result;
    }
	/**
	 * 
	 * Used to check if there is not two routing task with the same SeqId valid at the same period
	 * 
	 * @author holivier
	 * @param ctx
	 * @param context: a map containing workEffortIdFrom (routing) and SeqId, fromDate thruDate
	 * @return result: a map containing sequenceNumNotOk which is equal to "Y" if it's not Ok
	 */
		public static Map checkRoutingTaskAssoc(DispatchContext ctx, Map context) {
			GenericDelegator delegator = ctx.getDelegator();
			Map result = new HashMap();
			String sequenceNumNotOk = "N";
        
			String workEffortIdFrom = (String) context.get("workEffortIdFrom");    
			String workEffortIdTo = (String) context.get("workEffortIdTo");    
			String workEffortAssocTypeId = (String) context.get("workEffortAssocTypeId");    
			Long sequenceNum =  (Long) context.get("sequenceNum");
			java.sql.Timestamp  fromDate =  (java.sql.Timestamp) context.get("fromDate");
			java.sql.Timestamp  thruDate =  (java.sql.Timestamp) context.get("thruDate");

			List listRoutingTaskAssoc = null;

			try {
				listRoutingTaskAssoc = delegator.findByAnd("WorkEffortAssoc",UtilMisc.toMap("workEffortIdFrom", workEffortIdFrom,"sequenceNum",sequenceNum), UtilMisc.toList("fromDate"));
			} catch (GenericEntityException e) {
				Debug.logWarning(e, module);
				return ServiceUtil.returnError("Error finding desired WorkEffortAssoc records: " + e.toString());
			}

			if (listRoutingTaskAssoc != null) {
				Iterator  i = listRoutingTaskAssoc.iterator();
				while (i.hasNext()) {
					GenericValue routingTaskAssoc = (GenericValue) i.next();
					if ( ! workEffortIdFrom.equals(routingTaskAssoc.getString("workEffortIdFrom")) ||
						  ! workEffortIdTo.equals(routingTaskAssoc.getString("workEffortIdTo")) ||
						  ! workEffortAssocTypeId.equals(routingTaskAssoc.getString("workEffortAssocTypeId")) ||
						  ! sequenceNum.equals(routingTaskAssoc.getLong("sequenceNum"))
						  ) {
							if (routingTaskAssoc.getTimestamp("thruDate") == null && routingTaskAssoc.getTimestamp("fromDate") == null) sequenceNumNotOk = "Y";
							else if (routingTaskAssoc.getTimestamp("thruDate") == null) {
								if (thruDate == null) sequenceNumNotOk = "Y";
								else if (thruDate.after(routingTaskAssoc.getTimestamp("fromDate"))) sequenceNumNotOk = "Y";
							}
							else  if (routingTaskAssoc.getTimestamp("fromDate") == null) {	
								if (fromDate == null) sequenceNumNotOk = "Y";
								else if (fromDate.before(routingTaskAssoc.getTimestamp("thruDate"))) sequenceNumNotOk = "Y";
							}
							else if ( fromDate == null && thruDate == null) sequenceNumNotOk = "Y";
							else if (thruDate == null) {
								if (fromDate.before(routingTaskAssoc.getTimestamp("thruDate"))) sequenceNumNotOk = "Y";
							}
							else if (fromDate == null) {
								if (thruDate.after(routingTaskAssoc.getTimestamp("fromDate"))) sequenceNumNotOk = "Y";
							}
							else if ( routingTaskAssoc.getTimestamp("fromDate").before(thruDate) && fromDate.before(routingTaskAssoc.getTimestamp("thruDate")) ) sequenceNumNotOk = "Y";
					}
				}
			}
	
			result.put("sequenceNumNotOk", sequenceNumNotOk);
			return result;
		}

}
