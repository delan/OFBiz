/*
 * $Id: TechDataServices.java,v 1.5 2004/03/28 21:35:46 holivier Exp $
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
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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
 * @version    $Revision: 1.5 $
 * @since      3.0
 */
public class TechDataServices {
    
    public static final String module = TechDataServices.class.getName();

/**
 * 
 * Used to retreive some RoutingTasks (WorkEffort) selected by Name or MachineGroup ordered by Name
 * 
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
	 * @param ctx            The DispatchContext that this service is operating in.
	 * @param context    a map containing workEffortIdFrom (routing) and SeqId, fromDate thruDate
	 * @return result      a map containing sequenceNumNotOk which is equal to "Y" if it's not Ok
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
	/**
	 * Used to check if the routingtaskAssoc is valid for the testDate
	 * 
	 * @param routingTaskAssoc     the routingTaskAssoc to test  
	 * @param testDate                       a date
	 * @return true                               if the routingTAskAssoc is valid
	 */
		public static boolean routingTaskAssocIsValid(GenericValue routingTaskAssoc, Timestamp  testDate) {
			if (routingTaskAssoc.getTimestamp("fromDate") != null)
				if (routingTaskAssoc.getTimestamp("thruDate") != null) 
					if (testDate.after(routingTaskAssoc.getTimestamp("fromDate")) && testDate.before(routingTaskAssoc.getTimestamp("thruDate"))) return true;
					else return false;
				else
					if (testDate.after(routingTaskAssoc.getTimestamp("fromDate"))) return true;
					else return false;
			else
				if (routingTaskAssoc.getTimestamp("thruDate") != null) 
					if (testDate.before(routingTaskAssoc.getTimestamp("thruDate"))) return true;
					else return false;
				else return true;
		}
	/**
	 * Used to check if the productBom is valid for the testDate, currently only tested on date but in futur, maybe there will be the option {valid until stock=0>}
	 * 
	 * @param productBom    the productBom to test  
	 * @param testDate          a date
	 * @return true if the productBom is valid
	 */
		public static boolean productBomIsValid(GenericValue productBom,  Timestamp  testDate) {
			if (productBom.getTimestamp("fromDate") != null)
				if (productBom.getTimestamp("thruDate") != null) 
					if (testDate.after(productBom.getTimestamp("fromDate")) && testDate.before(productBom.getTimestamp("thruDate"))) return true;
					else return false;
				else
					if (testDate.after(productBom.getTimestamp("fromDate"))) return true;
					else return false;
			else
				if (productBom.getTimestamp("thruDate") != null) 
					if (testDate.before(productBom.getTimestamp("thruDate"))) return true;
					else return false;
				else return true;
		}
    /** Used to find the fisrt day in the TechDataCalendarWeek where capacity != 0, beginning at dayStart, dayStart included.
     * This method update the fromDate and retrun the capacity.
     * 
     * @param techDataCalendarWeek        The TechDataCalendarWeek cover  
     * @param dayStart                        
     * @return a map with the  capacity (Double) available and moveDay (int): the number of day it's necessary to move to have capacity available
     */
        public static Map dayStartCapacityAvailable(GenericValue techDataCalendarWeek,  int  dayStart) {
            Map result = new HashMap();
            int moveDay = 0;
            Double capacity = null;
            Time startTime = null;
            while (capacity == null) {
                switch( dayStart){
                    case Calendar.MONDAY:
                        capacity =  techDataCalendarWeek.getDouble("mondayCapacity");
                        startTime =  techDataCalendarWeek.getTime("mondayStartTime");
                    break;
                    case Calendar.TUESDAY:
                        capacity =  techDataCalendarWeek.getDouble("tuesdayCapacity");
                        startTime =  techDataCalendarWeek.getTime("tuesdayStartTime");
                    break;
                    case Calendar.WEDNESDAY:
                        capacity =  techDataCalendarWeek.getDouble("wednesdayCapacity");
                        startTime =  techDataCalendarWeek.getTime("wednesdayStartTime");
                    break;
                    case Calendar.THURSDAY:
                        capacity =  techDataCalendarWeek.getDouble("thursdayCapacity");
                        startTime =  techDataCalendarWeek.getTime("thursdayStartTime");
                    break;
                    case Calendar.FRIDAY:
                        capacity =  techDataCalendarWeek.getDouble("fridayCapacity");
                        startTime =  techDataCalendarWeek.getTime("fridayStartTime");
                    break;
                    case Calendar.SATURDAY:
                        capacity =  techDataCalendarWeek.getDouble("saturdayCapacity");
                        startTime =  techDataCalendarWeek.getTime("saturdayStartTime");
                    break;
                    case Calendar.SUNDAY:
                        capacity =  techDataCalendarWeek.getDouble("sundayCapacity");
                        startTime =  techDataCalendarWeek.getTime("sundayStartTime");
                    break;
                }
                if (capacity == null || capacity.doubleValue() == 0) {
                    moveDay +=1;
                    dayStart = (dayStart==7) ? 1 : dayStart +1;
                }    
                Debug.logInfo("capacity loop: " + capacity+ " moveDay=" +moveDay, module);
            }
            result.put("capacity",capacity);
            result.put("startTime",startTime);
            result.put("moveDay",new Integer(moveDay));
            return result;
        }
	/** Used to move in a TechDataCalenda, produce the Timestamp for the begining of the next day available and its associated capacity.
	 * If the dateFrom (param in) is not  in an available TechDataCalendar period, the return value is the next day available
	 * 
	 * @param techDataCalendar        The TechDataCalendar cover  
	 * @param dateFrom                        the date
	 * @return a map with Timestamp dateTo, Double nextCapacity
	 */
		public static Map startNextDay(GenericValue techDataCalendar,  Timestamp  dateFrom) {
            Map result = new HashMap();
            Timestamp dateTo = null;
            GenericValue techDataCalendarWeek = null;
            // TODO read TechDataCalendarExcWeek to manage execption week (maybe it's needed to refactor the entity definition
            try{
                techDataCalendarWeek = techDataCalendar.getRelatedOneCache("TechDataCalendarWeek");
            } catch (GenericEntityException e) {
                Debug.logError("Pb reading Calendar Week associated with calendar"+e.getMessage(), module);
//                return ServiceUtil.returnError(UtilProperties.getMessage(resource, "PbReadingTechDataCalendarWeekAssociated", locale));
               return ServiceUtil.returnError("Pb reading Calendar Week associated with calendar");
            }
            // TODO read TechDataCalendarExcDay to manage execption day
            Calendar cDateTrav =  Calendar.getInstance();
            cDateTrav.setTime((Date) dateFrom);
            Map position = dayStartCapacityAvailable(techDataCalendarWeek, cDateTrav.get(Calendar.DAY_OF_WEEK));
            Time startTime = (Time) position.get("startTime");
            int moveDay = ((Integer) position.get("moveDay")).intValue();
            dateTo = (moveDay == 0) ? dateFrom : UtilDateTime.getDayStart(dateFrom,moveDay);
// TODO after test (01:00:00).getTime() = 0 and not 3600000 so currently we add this value to be correct but it's needed to find why (maybe GMT pb)
            Timestamp startAvailablePeriod = new Timestamp(UtilDateTime.getDayStart(dateTo).getTime() + startTime.getTime() + 3600000);
            if (dateTo.before(startAvailablePeriod) ) {
                dateTo = startAvailablePeriod;
            } 
             else {
                dateTo = UtilDateTime.getNextDayStart(dateTo);
                cDateTrav.setTime((Date) dateTo);
                position = dayStartCapacityAvailable(techDataCalendarWeek, cDateTrav.get(Calendar.DAY_OF_WEEK));
                startTime = (Time) position.get("startTime");
                moveDay = ((Integer) position.get("moveDay")).intValue();
                if (moveDay != 0) dateTo = UtilDateTime.getDayStart(dateTo,moveDay);
                dateTo.setTime(dateTo.getTime() + startTime.getTime() + 3600000);
            }
            result.put("dateTo",dateTo);
            result.put("nextCapacity",position.get("capacity"));
			return result;
		}
    /** Used to move in a TechDataCalenda, produce the Timestamp for the end of the previous day available and its associated capacity.
     * If the dateFrom (param in) is not  a available TechDataCalendar period, the return value is the previous day available.
     * 
     * @param techDataCalendar        The TechDataCalendar cover  
     * @param dateFrom                        the date
     * @return a map with Timestamp dateTo, Double nextCapacity
     */
        public static Map endPreviousDay(GenericValue techDataCalendar,  Timestamp  dateFrom) {
            Map result = null;
    
            return result;
        }
    /** Used to move forward in a TechDataCalenda, start from the dateFrom and move forward only on available period.
     * If the dateFrom (param in) is not  a available TechDataCalendar period, the startDate is the begining of the next  day available
     * 
     * @param techDataCalendar        The TechDataCalendar cover  
     * @param dateFrom                        the start date
     * @param amount                           the amount of millisecond to move forward
     * @return the dateTo
     */
        public static Timestamp addForward(GenericValue techDataCalendar,  Timestamp  dateFrom, int amount) {
            Timestamp dateTo = null;
    
            return dateTo;
        }
    /** Used to move backward in a TechDataCalenda, start from the dateFrom and move backward only on available period.
     * If the dateFrom (param in) is not  a available TechDataCalendar period, the startDate is the end of the previous  day available
     * 
     * @param techDataCalendar        The TechDataCalendar cover  
     * @param dateFrom                        the start date
     * @param amount                           the amount of millisecond to move backward
     * @return the dateTo
     */
        public static Timestamp addBackward(GenericValue techDataCalendar,  Timestamp  dateFrom, int amount) {
            Timestamp dateTo = null;
    
            return dateTo;
        }

}
