/*
 * $Id$
 *
 * Copyright (c) 2001-2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.entityext.synchronization;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entityext.synchronization.EntitySyncContext.SyncAbortException;
import org.ofbiz.entityext.synchronization.EntitySyncContext.SyncErrorException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

/**
 * Entity Engine Sync Services
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a> 
 * @version    $Rev$
 * @since      3.0
 */
public class EntitySyncServices {
    
    public static final String module = EntitySyncServices.class.getName();
    
    /**
     * Run an Entity Sync (checks to see if other already running, etc)
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map runEntitySync(DispatchContext dctx, Map context) {
        EntitySyncContext esc = null;
        try {
            esc = new EntitySyncContext(dctx, context);
            if ("Y".equals(esc.entitySync.get("forPullOnly"))) {
                return ServiceUtil.returnError("Cannot do Entity Sync Push because entitySyncId [] is set for Pull Only.");
            }

            esc.runPushStartRunning();

            // increment starting time to run until now
            esc.setSplitStartTime(); // just run this the first time, will be updated between each loop automatically
            while (esc.hasMoreTimeToSync()) {
                
                // TODO make sure the following message is commented out before commit:
                // Debug.logInfo("Doing runEntitySync split, currentRunStartTime=" + esc.currentRunStartTime + ", currentRunEndTime=" + esc.currentRunEndTime, module);
                
                esc.totalSplits++;
                
                // tx times are indexed
                // keep track of how long these sync runs take and store that info on the history table
                // saves info about removed, all entities that don't have no-auto-stamp set, this will be done in the GenericDAO like the stamp sets
                
                // ===== INSERTS =====
                ArrayList valuesToCreate = esc.assembleValuesToCreate();
                // ===== UPDATES =====
                ArrayList valuesToStore = esc.assembleValuesToStore();
                // ===== DELETES =====
                List keysToRemove = esc.assembleKeysToRemove();
                
                esc.runPushSendData(valuesToCreate, valuesToStore, keysToRemove);
                
                esc.saveResultsReportedFromDataStore();
            }

            esc.saveFinalSyncResults();
            
        } catch (SyncAbortException e) {
            return e.returnError(module);
        } catch (SyncErrorException e) {
            e.saveSyncErrorInfo(esc);
            return e.returnError(module);
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    /**
     * Store Entity Sync Data
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map storeEntitySyncData(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        String overrideDelegatorName = (String) context.get("delegatorName");
        if (UtilValidate.isNotEmpty(overrideDelegatorName)) {
            delegator = GenericDelegator.getGenericDelegator(overrideDelegatorName);
            if (delegator == null) {
                return ServiceUtil.returnError("Could not find delegator with specified name " + overrideDelegatorName);
            }
        }
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        
        String entitySyncId = (String) context.get("entitySyncId");
        // incoming lists will already be sorted by lastUpdatedStamp (or lastCreatedStamp)
        List valuesToCreate = (List) context.get("valuesToCreate");
        List valuesToStore = (List) context.get("valuesToStore");
        List keysToRemove = (List) context.get("keysToRemove");
        
        try {
            long toCreateInserted = 0;
            long toCreateUpdated = 0;
            long toCreateNotUpdated = 0;
            long toStoreInserted = 0;
            long toStoreUpdated = 0;
            long toStoreNotUpdated = 0;
            long toRemoveDeleted = 0;
            long toRemoveAlreadyDeleted = 0;
            
            // create all values in the valuesToCreate List; if the value already exists update it, or if exists and was updated more recently than this one dont update it
            Iterator valueToCreateIter = valuesToCreate.iterator();
            while (valueToCreateIter.hasNext()) {
                GenericValue valueToCreate = (GenericValue) valueToCreateIter.next();
                // to Create check if exists (find by pk), if not insert; if exists check lastUpdatedStamp: if null or before the candidate value insert, otherwise don't insert
                // NOTE: use the delegator from this DispatchContext rather than the one named in the GenericValue
                
                // maintain the original timestamps when doing storage of synced data, by default with will update the timestamps to now
                valueToCreate.setIsFromEntitySync(true);
                
                GenericValue existingValue = delegator.findByPrimaryKey(valueToCreate.getPrimaryKey());
                if (existingValue == null) {
                    delegator.create(valueToCreate);
                    toCreateInserted++;
                } else {
                    // if the existing value has a stamp field that is AFTER the stamp on the valueToCreate, don't update it
                    if (existingValue.get(ModelEntity.STAMP_FIELD) != null && existingValue.getTimestamp(ModelEntity.STAMP_FIELD).after(valueToCreate.getTimestamp(ModelEntity.STAMP_FIELD))) {
                        toCreateNotUpdated++;
                    } else {
                        delegator.store(valueToCreate);
                        toCreateUpdated++;
                    }
                }
            }
            
            // iterate through to store list and store each
            Iterator valueToStoreIter = valuesToStore.iterator();
            while (valueToStoreIter.hasNext()) {
                GenericValue valueToStore = (GenericValue) valueToStoreIter.next();
                // to store check if exists (find by pk), if not insert; if exists check lastUpdatedStamp: if null or before the candidate value insert, otherwise don't insert
                
                // maintain the original timestamps when doing storage of synced data, by default with will update the timestamps to now
                valueToStore.setIsFromEntitySync(true);
                
                GenericValue existingValue = delegator.findByPrimaryKey(valueToStore.getPrimaryKey());
                if (existingValue == null) {
                    delegator.create(valueToStore);
                    toStoreInserted++;
                } else {
                    // if the existing value has a stamp field that is AFTER the stamp on the valueToStore, don't update it
                    if (existingValue.get(ModelEntity.STAMP_FIELD) != null && existingValue.getTimestamp(ModelEntity.STAMP_FIELD).after(valueToStore.getTimestamp(ModelEntity.STAMP_FIELD))) {
                        toStoreNotUpdated++;
                    } else {
                        delegator.store(valueToStore);
                        toStoreUpdated++;
                    }
                }
            }
            
            // iterate through to remove list and remove each
            Iterator keyToRemoveIter = keysToRemove.iterator();
            while (keyToRemoveIter.hasNext()) {
                GenericEntity pkToRemove = (GenericEntity) keyToRemoveIter.next();
                
                // check to see if it exists, if so remove and count, if not just count already removed
                // always do a removeByAnd, if it was a removeByAnd great, if it was a removeByPrimaryKey, this will also work and save us a query
                pkToRemove.setIsFromEntitySync(true);
                int numRemByAnd = delegator.removeByAnd(pkToRemove.getEntityName(), pkToRemove);
                if (numRemByAnd == 0) {
                    toRemoveAlreadyDeleted++;
                } else {
                    toRemoveDeleted++;
                }
            }
            
            Map result = ServiceUtil.returnSuccess();
            result.put("toCreateInserted", new Long(toCreateInserted));
            result.put("toCreateUpdated", new Long(toCreateUpdated));
            result.put("toCreateNotUpdated", new Long(toCreateNotUpdated));
            result.put("toStoreInserted", new Long(toStoreInserted));
            result.put("toStoreUpdated", new Long(toStoreUpdated));
            result.put("toStoreNotUpdated", new Long(toStoreNotUpdated));
            result.put("toRemoveDeleted", new Long(toRemoveDeleted));
            result.put("toRemoveAlreadyDeleted", new Long(toRemoveAlreadyDeleted));
            return result;
        } catch (GenericEntityException e) {
            String errorMsg = "Error saving Entity Sync Data for entitySyncId [" + entitySyncId + "]: " + e.toString();
            Debug.logError(e, errorMsg, module);
            return ServiceUtil.returnError(errorMsg);
        }
    }

    /**
     * Run Pull Entity Sync - Pull From Remote
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map runPullEntitySync(DispatchContext dctx, Map context) {
        Debug.logInfo("Running cleanSyncRemoveInfo", module);
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        
        String entitySyncId = (String) context.get("entitySyncId");
        String remotePullAndReportEntitySyncDataName = (String) context.get("remotePullAndReportEntitySyncDataName");
        
        // loop until no data is returned to store
        boolean gotMoreData = true;
        
        Timestamp startDate = null;
        Long toCreateInserted = null;
        Long toCreateUpdated = null;
        Long toCreateNotUpdated = null;
        Long toStoreInserted = null;
        Long toStoreUpdated = null;
        Long toStoreNotUpdated = null;
        Long toRemoveDeleted = null;
        Long toRemoveAlreadyDeleted = null;
        
        while (gotMoreData) {
            gotMoreData = false;
            
            // call pullAndReportEntitySyncData, initially with no results, then with results from last loop
            Map remoteCallContext = new HashMap();
            remoteCallContext.put("entitySyncId", entitySyncId);
            remoteCallContext.put("delegatorName", context.get("remoteDelegatorName"));
            remoteCallContext.put("userLogin", context.get("userLogin"));

            remoteCallContext.put("startDate", startDate);
            remoteCallContext.put("toCreateInserted", toCreateInserted);
            remoteCallContext.put("toCreateUpdated", toCreateUpdated);
            remoteCallContext.put("toCreateNotUpdated", toCreateNotUpdated);
            remoteCallContext.put("toStoreInserted", toStoreInserted);
            remoteCallContext.put("toStoreUpdated", toStoreUpdated);
            remoteCallContext.put("toStoreNotUpdated", toStoreNotUpdated);
            remoteCallContext.put("toRemoveDeleted", toRemoveDeleted);
            remoteCallContext.put("toRemoveAlreadyDeleted", toRemoveAlreadyDeleted);
            
            try {
                Map result = dispatcher.runSync(remotePullAndReportEntitySyncDataName, remoteCallContext);
                if (ServiceUtil.isError(result)) {
                    String errMsg = "Error calling remote pull and report EntitySync service with name: " + remotePullAndReportEntitySyncDataName;
                    return ServiceUtil.returnError(errMsg, null, null, result);
                }
                
                startDate = (Timestamp) result.get("startDate");
                
                try {
                    // store data returned, get results (just call storeEntitySyncData locally, get the numbers back and boom shakalaka)
                    
                    // anything to store locally?
                    if (startDate != null && (!UtilValidate.isEmpty((Collection) result.get("valuesToCreate")) || 
                            !UtilValidate.isEmpty((Collection) result.get("valuesToCreate")) ||
                            !UtilValidate.isEmpty((Collection) result.get("valuesToCreate")))) {
                        
                        // yep, we got more data
                        gotMoreData = true;

                        // at least one of the is not empty, make sure none of them are null now too...
                        List valuesToCreate = (List) result.get("valuesToCreate");
                        if (valuesToCreate == null) valuesToCreate = Collections.EMPTY_LIST;
                        List valuesToStore = (List) result.get("valuesToStore");
                        if (valuesToStore == null) valuesToStore = Collections.EMPTY_LIST;
                        List keysToRemove = (List) result.get("keysToRemove");
                        if (keysToRemove == null) keysToRemove = Collections.EMPTY_LIST;
                        
                        Map callLocalStoreContext = UtilMisc.toMap("entitySyncId", entitySyncId, "delegatorName", context.get("localDelegatorName"),
                                "valuesToCreate", valuesToCreate, "valuesToStore", valuesToStore, 
                                "keysToRemove", keysToRemove);
                        
                        callLocalStoreContext.put("userLogin", context.get("userLogin"));
                        Map storeResult = dispatcher.runSync("storeEntitySyncData", callLocalStoreContext);
                        if (ServiceUtil.isError(storeResult)) {
                            String errMsg = "Error calling service to store data locally";
                            return ServiceUtil.returnError(errMsg, null, null, storeResult);
                        }
                        
                        // get results for next pass
                        toCreateInserted = (Long) storeResult.get("toCreateInserted");
                        toCreateUpdated = (Long) storeResult.get("toCreateUpdated");
                        toCreateNotUpdated = (Long) storeResult.get("toCreateNotUpdated");
                        toStoreInserted = (Long) storeResult.get("toStoreInserted");
                        toStoreUpdated = (Long) storeResult.get("toStoreUpdated");
                        toStoreNotUpdated = (Long) storeResult.get("toStoreNotUpdated");
                        toRemoveDeleted = (Long) storeResult.get("toRemoveDeleted");
                        toRemoveAlreadyDeleted = (Long) storeResult.get("toRemoveAlreadyDeleted");
                    }
                } catch (GenericServiceException e) {
                    String errMsg = "Error calling service to store data locally: " + e.toString();
                    Debug.logError(e, errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            } catch (GenericServiceException e) {
                String errMsg = "Error calling remote pull and report EntitySync service with name: " + remotePullAndReportEntitySyncDataName + "; " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
        }
        
        return ServiceUtil.returnSuccess();
    }

    /**
     * Pull and Report Entity Sync Data - Called Remotely to Push Results from last pull, the Pull next set of results.
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map pullAndReportEntitySyncData(DispatchContext dctx, Map context) {
        EntitySyncContext esc = null;
        try {
            esc = new EntitySyncContext(dctx, context);
            
            if ("Y".equals(esc.entitySync.get("forPushOnly"))) {
                return ServiceUtil.returnError("Cannot do Entity Sync Pull because entitySyncId [] is set for Push Only.");
            }

            // restore info from last pull, or if no results start new run
            esc.runPullStartOrRestoreSavedResults();

            // increment starting time to run until now
            while (esc.hasMoreTimeToSync()) {
                // Part 1: if any results are passed, store the results for the given startDate, update EntitySync, etc
                esc.saveResultsReportedFromDataStore();
                
                // make sure the following message is commented out before commit:
                //Debug.logInfo("Doing runEntitySync split, currentRunStartTime=" + esc.currentRunStartTime + ", currentRunEndTime=" + esc.currentRunEndTime, module);
                
                esc.totalSplits++;
                
                // tx times are indexed
                // keep track of how long these sync runs take and store that info on the history table
                // saves info about removed, all entities that don't have no-auto-stamp set, this will be done in the GenericDAO like the stamp sets
                
                // Part 2: get the next set of data for the given entitySyncId
                // Part 2a: return it back for storage but leave the EntitySyncHistory without results, and don't update the EntitySync last time

                // ===== INSERTS =====
                ArrayList valuesToCreate = esc.assembleValuesToCreate();
                // ===== UPDATES =====
                ArrayList valuesToStore = esc.assembleValuesToStore();
                // ===== DELETES =====
                List keysToRemove = esc.assembleKeysToRemove();
                
                esc.setTotalRowCounts(valuesToCreate, valuesToStore, keysToRemove);
                if (esc.totalRowsToStore > 0) {
                    // stop if we found some data, otherwise look and try again
                    Map result = ServiceUtil.returnSuccess();
                    result.put("startDate", esc.startDate);
                    result.put("valuesToCreate", valuesToCreate);
                    result.put("valuesToStore", valuesToStore);
                    result.put("keysToRemove", keysToRemove);
                    return result;
                }
            }
            
            // if no more results from database to return, save final settings
            if (!esc.hasMoreTimeToSync() ) {
                esc.saveFinalSyncResults();
            }
        } catch (SyncAbortException e) {
            return e.returnError(module);
        } catch (SyncErrorException e) {
            e.saveSyncErrorInfo(esc);
            return e.returnError(module);
        }
        return ServiceUtil.returnSuccess();
    }

    /**
     * Clean EntitySyncRemove Info
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map cleanSyncRemoveInfo(DispatchContext dctx, Map context) {
        Debug.logInfo("Running cleanSyncRemoveInfo", module);
        GenericDelegator delegator = dctx.getDelegator();
        
        try {
            // find the largest keepRemoveInfoHours value on an EntitySyncRemove and kill everything before that, if none found default to 10 days (240 hours)
            double keepRemoveInfoHours = 24;
            
            List entitySyncRemoveList = delegator.findAll("EntitySync");
            Iterator entitySyncRemoveIter = entitySyncRemoveList.iterator();
            while (entitySyncRemoveIter.hasNext()) {
                GenericValue entitySyncRemove = (GenericValue) entitySyncRemoveIter.next();
                Double curKrih = entitySyncRemove.getDouble("keepRemoveInfoHours");
                if (curKrih != null) {
                    double curKrihVal = curKrih.doubleValue();
                    if (curKrihVal > keepRemoveInfoHours) {
                        keepRemoveInfoHours = curKrihVal;
                    }
                }
            }
            
            
            int keepSeconds = (int) Math.floor(keepRemoveInfoHours * 60);
            
            Calendar nowCal = Calendar.getInstance();
            nowCal.setTimeInMillis(System.currentTimeMillis());
            nowCal.add(Calendar.SECOND, -keepSeconds);
            Timestamp keepAfterStamp = new Timestamp(nowCal.getTimeInMillis());
            
            EntityListIterator eli = delegator.findListIteratorByCondition("EntitySyncRemove", new EntityExpr(ModelEntity.STAMP_TX_FIELD, EntityOperator.LESS_THAN, keepAfterStamp), null, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD));
            GenericValue entitySyncRemove = null;
            int numRemoved = 0;
            List valuesToRemove = new LinkedList();
            while ((entitySyncRemove = (GenericValue) eli.next()) != null) {
                valuesToRemove.add(entitySyncRemove.getPrimaryKey());
                numRemoved++;
                // do 1000 at a time to avoid possible problems with removing values while iterating over a cursor
                if (numRemoved > 1000) {
                    eli.close();
                    delegator.removeAll(valuesToRemove);
                    eli = delegator.findListIteratorByCondition("EntitySyncRemove", new EntityExpr(ModelEntity.STAMP_TX_FIELD, EntityOperator.LESS_THAN, keepAfterStamp), null, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD));
                }
            }
            eli.close();
            
            return ServiceUtil.returnSuccess();
        } catch (GenericEntityException e) {
            String errorMsg = "Error cleaning out EntitySyncRemove info: " + e.toString();
            Debug.logError(e, errorMsg, module);
            return ServiceUtil.returnError(errorMsg);
        }
    }
}
