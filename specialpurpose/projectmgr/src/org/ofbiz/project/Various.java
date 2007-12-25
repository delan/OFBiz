/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.project;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class Various {

    public static final String module = Various.class.getName();


    public static void setDatesFollowingTasks(GenericValue task) {
    	
    	try {
    		List assocs = task.getRelated("FromWorkEffortAssoc");
    		if (UtilValidate.isNotEmpty(assocs)) {
    			Iterator a = assocs.iterator();
    			while (a.hasNext()) {
    				GenericValue assoc = (GenericValue) a.next();
    				GenericValue nextTask = assoc.getRelatedOne("ToWorkEffort");
    				if (nextTask.getTimestamp("estimatedStartDate").before(task.getTimestamp("estimatedCompletionDate"))) {
    					nextTask.put("estimatedStartDate", task.getTimestamp("estimatedCompletionDate")); 
        				nextTask.put("estimatedCompletionDate", calculateCompletionDate(nextTask, task.getTimestamp("estimatedCompletionDate")));
        				nextTask.store();
    				}
    				setDatesFollowingTasks(nextTask);
    			}
    		}

    	} catch (GenericEntityException e) {
    		Debug.logError("Could not updte task: " + e.getMessage(), module);
    	}
    }
    
    public static Timestamp calculateCompletionDate(GenericValue task, Timestamp startDate) {

		Double plannedHours = 0.00;
    	try {
    		// get planned hours
    		List standards = task.getRelated("WorkEffortSkillStandard");
    		Iterator t = standards.iterator();
    		while (t.hasNext()) {
    			GenericValue standard = (GenericValue) t.next();
    			if (standard.getDouble("estimatedNumPeople") == null) {
    				standard.put("estimatedNumPeople", new Double("1"));
    			}
    			plannedHours += standard.getDouble("estimatedDuration") / standard.getDouble("estimatedNumPeople");
    		}

    	} catch (GenericEntityException e) {
    		Debug.logError("Could not updte task: " + e.getMessage(), module);
    	}
    	return UtilDateTime.addDaysToTimestamp(startDate, plannedHours / 8); 
    }
    
    
}
