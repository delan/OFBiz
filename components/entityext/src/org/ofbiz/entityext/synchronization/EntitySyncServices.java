/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
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

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelViewEntity;
import org.ofbiz.entity.serialize.SerializeException;
import org.ofbiz.entity.serialize.XmlSerializer;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.xml.sax.SAXException;

/**
 * Entity Engine Sync Services
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a> 
 * @version    $Rev:$
 * @since      3.0
 */
public class EntitySyncServices {
    
    public static final String module = EntitySyncServices.class.getName();
    
    // set default split to 10 seconds, ie try not to get too much data moving over at once
    public static final long defaultSyncSplitMillis = 10000;
    
    // default to 5 minutes
    public static final long syncEndBufferMillis = 300000;

    /**
     * Run an Entity Sync (checks to see if other already running, etc)
     *@param dctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map runEntitySync(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        // make the last time to sync X minutes before the current time so that if this machines clock is up to that amount of time 
        //ahead of another machine writing to the DB it will still work fine and not lose any data
        Timestamp syncEndStamp = new Timestamp(System.currentTimeMillis() - syncEndBufferMillis);
        
        String entitySyncId = (String) context.get("entitySyncId");
        Debug.logInfo("Running runEntitySync with entitySyncId=" + entitySyncId, module);
        
        // this is the other part of the history PK, leave null until we create the history object
        Timestamp startDate = null;
        
        try {
            GenericValue entitySync = delegator.findByPrimaryKey("EntitySync", UtilMisc.toMap("entitySyncId", entitySyncId));
            if (entitySync == null) {
                return ServiceUtil.returnError("Not running EntitySync [" + entitySyncId + "], no record found with that ID.");
            }
            
            String targetServiceName = entitySync.getString("targetServiceName");
            if (UtilValidate.isEmpty(targetServiceName)) {
                return ServiceUtil.returnError("Not running EntitySync [" + entitySyncId + "], no targetServiceName is specified, where do we send the data?");
            }
            
            String targetDelegatorName = entitySync.getString("targetDelegatorName");
            
            // check to see if this sync is already running, if so return error
            if ("ESR_RUNNING".equals(entitySync.getString("runStatusId"))) {
                return ServiceUtil.returnError("Not running EntitySync [" + entitySyncId + "], an instance is already running.");
            }
            
            // not running, get started NOW
            // set running status on entity sync, run in its own tx
            Map startEntitySyncRes = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", "ESR_RUNNING", "userLogin", userLogin));
            if (ModelService.RESPOND_ERROR.equals(startEntitySyncRes.get(ModelService.RESPONSE_MESSAGE))) {
                return ServiceUtil.returnError("Could not start Entity Sync service, could not mark as running", null, null, startEntitySyncRes);
            }
            
            Timestamp lastSuccessfulSynchTime = entitySync.getTimestamp("lastSuccessfulSynchTime");
            Timestamp currentRunStartTime = lastSuccessfulSynchTime;
            
            Long syncSplitMillis = entitySync.getLong("syncSplitMillis");
            long splitMillis = defaultSyncSplitMillis;
            if (syncSplitMillis != null) {
                splitMillis = syncSplitMillis.longValue();
            }
            
            List entityModelToUseList = makeEntityModelToUseList(delegator, entitySync);
            
            // if currentRunStartTime is null, what to do? I guess iterate through all entities and find earliest tx stamp
            if (currentRunStartTime == null) {
                Iterator entityModelToUseIter = entityModelToUseList.iterator();
                while (entityModelToUseIter.hasNext()) {
                    ModelEntity modelEntity = (ModelEntity) entityModelToUseIter.next();
                    // fields to select will be PK and the STAMP_TX_FIELD, slimmed down so we don't get a ton of data back
                    List fieldsToSelect = new LinkedList(modelEntity.getPkFieldNames());
                    // find all instances of this entity with the STAMP_TX_FIELD != null, sort ascending to get lowest/oldest value first, then grab first and consider as candidate currentRunStartTime
                    fieldsToSelect.add(ModelEntity.STAMP_TX_FIELD);
                    EntityListIterator eli = delegator.findListIteratorByCondition(modelEntity.getEntityName(), new EntityExpr(ModelEntity.STAMP_TX_FIELD, EntityOperator.NOT_EQUAL, null), fieldsToSelect, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD));
                    GenericValue nextValue = (GenericValue) eli.next();
                    eli.close();
                    if (nextValue != null) {
                        Timestamp candidateTime = nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD);
                        if (currentRunStartTime == null || candidateTime.before(currentRunStartTime)) {
                            currentRunStartTime = candidateTime;
                        }
                    }
                }
                if (Debug.infoOn()) Debug.logInfo("No currentRunStartTime was stored on the EntitySync record, so searched for the earliest value and got: " + currentRunStartTime, module);
            }
            
            // create history record, should run in own tx
            Map initialHistoryRes = dispatcher.runSync("createEntitySyncHistory", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", "ESR_RUNNING", "beginningSynchTime", currentRunStartTime, "userLogin", userLogin));
            if (ServiceUtil.isError(initialHistoryRes)) {
                String errorMsg = "Not running EntitySync [" + entitySyncId + "], could not create EntitySyncHistory";
                List errorList = new LinkedList();
                saveSyncErrorInfo(entitySyncId, startDate, "ESR_DATA_ERROR", errorList, dispatcher, userLogin);
                return ServiceUtil.returnError(errorMsg, errorList, null, initialHistoryRes);
            }
            startDate = (Timestamp) initialHistoryRes.get("startDate");

            long toCreateInserted = 0;
            long toCreateUpdated = 0;
            long toCreateNotUpdated = 0;
            long toStoreInserted = 0;
            long toStoreUpdated = 0;
            long toStoreNotUpdated = 0;
            long toRemoveDeleted = 0;
            long toRemoveAlreadyDeleted = 0;
            
            long totalRowsToCreate = 0;
            long totalRowsToStore = 0;
            long totalRowsToRemove = 0;

            long totalStoreCalls = 0;
            long totalSplits = 0;
            long perSplitMinMillis = Long.MAX_VALUE;
            long perSplitMaxMillis = 0;
            long perSplitMinItems = Long.MAX_VALUE;
            long perSplitMaxItems = 0;

            long startingTimeMillis = System.currentTimeMillis();
            
            // increment starting time to run until now
            while (currentRunStartTime.before(syncEndStamp)) {
                long splitStartTime = System.currentTimeMillis();
                
                Timestamp currentRunEndTime = new Timestamp(currentRunStartTime.getTime() + splitMillis);
                if (currentRunEndTime.after(syncEndStamp)) {
                    currentRunEndTime = syncEndStamp;
                }
                
                // make sure the following message is commented out before commit:
                //Debug.logInfo("Doing runEntitySync split, currentRunStartTime=" + currentRunStartTime + ", currentRunEndTime=" + currentRunEndTime, module);
                
                totalSplits++;
                
                // tx times are indexed
                // keep track of how long these sync runs take and store that info on the history table
                // saves info about removed, all entities that don't have no-auto-stamp set, this will be done in the GenericDAO like the stamp sets
                
                // ===== INSERTS =====
                
                // first grab all values inserted in the date range, then get the updates (leaving out all values inserted in the data range)
                ArrayList valuesToCreate = new ArrayList(); // make it an ArrayList to easily merge in sorted lists

                // iterate through entities, get all records with tx stamp in the current time range, put all in a single list
                Iterator entityModelToUseCreateIter = entityModelToUseList.iterator();
                while (entityModelToUseCreateIter.hasNext()) {
                    int insertBefore = 0;
                    ModelEntity modelEntity = (ModelEntity) entityModelToUseCreateIter.next();
                    // get the values created within the current time range
                    EntityCondition findValCondition = new EntityConditionList(UtilMisc.toList(
                            new EntityExpr(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunStartTime), 
                            new EntityExpr(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime)), EntityOperator.AND);
                    EntityListIterator eli = delegator.findListIteratorByCondition(modelEntity.getEntityName(), findValCondition, null, UtilMisc.toList(ModelEntity.CREATE_STAMP_TX_FIELD, ModelEntity.CREATE_STAMP_FIELD));
                    GenericValue nextValue = null;
                    //long valuesPerEntity = 0;
                    while ((nextValue = (GenericValue) eli.next()) != null) {
                        // sort by the tx stamp and then the record stamp 
                        // find first value in valuesToStore list, starting with the current insertBefore value, that has a CREATE_STAMP_TX_FIELD after the nextValue.CREATE_STAMP_TX_FIELD, then do the same with CREATE_STAMP_FIELD
                        while (insertBefore < valuesToCreate.size() && ((GenericValue) valuesToCreate.get(insertBefore)).getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_TX_FIELD))) {
                            insertBefore++;
                        }
                        while (insertBefore < valuesToCreate.size() && ((GenericValue) valuesToCreate.get(insertBefore)).getTimestamp(ModelEntity.CREATE_STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.CREATE_STAMP_FIELD))) {
                            insertBefore++;
                        }
                        valuesToCreate.add(insertBefore, nextValue);
                        //valuesPerEntity++;
                    }
                    eli.close();
                    
                    // definately remove this message and related data gathering
                    //long preCount = delegator.findCountByCondition(modelEntity.getEntityName(), findValCondition, null);
                    //long entityTotalCount = delegator.findCountByCondition(modelEntity.getEntityName(), null, null);
                    //if (entityTotalCount > 0 || preCount > 0 || valuesPerEntity > 0) Debug.logInfo("Got " + valuesPerEntity + "/" + preCount + "/" + entityTotalCount + " values for entity " + modelEntity.getEntityName(), module);
                }
                
                // ===== UPDATES =====
                
                // simulate two ordered lists and merge them on-the-fly for faster combined sorting
                ArrayList valuesToStore = new ArrayList(); // make it an ArrayList to easily merge in sorted lists

                // iterate through entities, get all records with tx stamp in the current time range, put all in a single list
                Iterator entityModelToUseUpdateIter = entityModelToUseList.iterator();
                while (entityModelToUseUpdateIter.hasNext()) {
                    int insertBefore = 0;
                    ModelEntity modelEntity = (ModelEntity) entityModelToUseUpdateIter.next();
                    // get all values that were updated, but NOT created in the current time range; if no info on created stamp, that's okay we'll include it here because it won't have been included in the valuesToCreate list
                    EntityCondition createdBeforeStartCond = new EntityExpr(
                            new EntityExpr(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.EQUALS, null), 
                            EntityOperator.OR, 
                            new EntityExpr(ModelEntity.CREATE_STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunStartTime));
                    EntityCondition findValCondition = new EntityConditionList(UtilMisc.toList(
                            new EntityExpr(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunStartTime), 
                            new EntityExpr(ModelEntity.STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime), 
                            createdBeforeStartCond), 
                            EntityOperator.AND);
                    EntityListIterator eli = delegator.findListIteratorByCondition(modelEntity.getEntityName(), findValCondition, null, UtilMisc.toList(ModelEntity.STAMP_TX_FIELD, ModelEntity.STAMP_FIELD));
                    GenericValue nextValue = null;
                    //long valuesPerEntity = 0;
                    while ((nextValue = (GenericValue) eli.next()) != null) {
                        // sort by the tx stamp and then the record stamp 
                        // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_TX_FIELD after the nextValue.STAMP_TX_FIELD, then do the same with STAMP_FIELD
                        while (insertBefore < valuesToStore.size() && ((GenericValue) valuesToStore.get(insertBefore)).getTimestamp(ModelEntity.STAMP_TX_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_TX_FIELD))) {
                            insertBefore++;
                        }
                        while (insertBefore < valuesToStore.size() && ((GenericValue) valuesToStore.get(insertBefore)).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                            insertBefore++;
                        }
                        valuesToStore.add(insertBefore, nextValue);
                        //valuesPerEntity++;
                    }
                    eli.close();
                    
                    // definately remove this message and related data gathering
                    //long preCount = delegator.findCountByCondition(modelEntity.getEntityName(), findValCondition, null);
                    //long entityTotalCount = delegator.findCountByCondition(modelEntity.getEntityName(), null, null);
                    //if (entityTotalCount > 0 || preCount > 0 || valuesPerEntity > 0) Debug.logInfo("Got " + valuesPerEntity + "/" + preCount + "/" + entityTotalCount + " values for entity " + modelEntity.getEntityName(), module);
                }
                
                // ===== DELETES =====
                
                // get all removed items from the given time range, add to list for those
                List keysToRemove = new LinkedList();
                // find all instances of this entity with the STAMP_TX_FIELD != null, sort ascending to get lowest/oldest value first, then grab first and consider as candidate currentRunStartTime
                EntityCondition findValCondition = new EntityConditionList(UtilMisc.toList(
                        new EntityExpr(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunStartTime), 
                        new EntityExpr(ModelEntity.STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime)), EntityOperator.AND);
                EntityListIterator removeEli = delegator.findListIteratorByCondition("EntitySyncRemove", findValCondition, null, UtilMisc.toList(ModelEntity.STAMP_FIELD));
                GenericValue nextValue = null;
                while ((nextValue = (GenericValue) removeEli.next()) != null) {
                    keysToRemove.add(nextValue);
                }
                removeEli.close();
                
                // grab some totals before calling...
                long totalRowsToCreateCur = valuesToCreate.size();
                long totalRowsToStoreCur = valuesToStore.size();
                long totalRowsToRemoveCur = keysToRemove.size();

                long totalRowsPerSplit = totalRowsToCreateCur + totalRowsToStoreCur + totalRowsToRemoveCur;
                
                if (totalRowsPerSplit < perSplitMinItems) {
                    perSplitMinItems = totalRowsPerSplit;
                }
                if (totalRowsPerSplit > perSplitMaxItems) {
                    perSplitMaxItems = totalRowsPerSplit;
                }

                totalRowsToCreate += totalRowsToCreateCur;
                totalRowsToStore += totalRowsToStoreCur;
                totalRowsToRemove += totalRowsToRemoveCur;
                
                // call service named on EntitySync, IFF there is actually data to send over
                if (totalRowsPerSplit > 0) {
                    Map targetServiceMap = UtilMisc.toMap("entitySyncId", entitySyncId, "valuesToCreate", valuesToCreate, "valuesToStore", valuesToStore, "keysToRemove", keysToRemove, "userLogin", userLogin);
                    if (UtilValidate.isNotEmpty(targetDelegatorName)) {
                        targetServiceMap.put("delegatorName", targetDelegatorName);
                    }
                    Map remoteStoreResult = dispatcher.runSync(targetServiceName, targetServiceMap);
                    if (ServiceUtil.isError(remoteStoreResult)) {
                        String errorMsg = "Error running EntitySync [" + entitySyncId + "], call to store service [" + targetServiceName + "] failed.";
                        List errorList = new LinkedList();
                        saveSyncErrorInfo(entitySyncId, startDate, "ESR_OTHER_ERROR", errorList, dispatcher, userLogin);
                        return ServiceUtil.returnError(errorMsg, errorList, null, remoteStoreResult);
                    }
                    
                    totalStoreCalls++;
                    
                    long toCreateInsertedCur = remoteStoreResult.get("toCreateInserted") == null ? 0 : ((Long) remoteStoreResult.get("toCreateInserted")).longValue();
                    long toCreateUpdatedCur = remoteStoreResult.get("toCreateUpdated") == null ? 0 : ((Long) remoteStoreResult.get("toCreateUpdated")).longValue();
                    long toCreateNotUpdatedCur = remoteStoreResult.get("toCreateNotUpdated") == null ? 0 : ((Long) remoteStoreResult.get("toCreateNotUpdated")).longValue();
                    long toStoreInsertedCur = remoteStoreResult.get("toStoreInserted") == null ? 0 : ((Long) remoteStoreResult.get("toStoreInserted")).longValue();
                    long toStoreUpdatedCur = remoteStoreResult.get("toStoreUpdated") == null ? 0 : ((Long) remoteStoreResult.get("toStoreUpdated")).longValue();
                    long toStoreNotUpdatedCur = remoteStoreResult.get("toStoreNotUpdated") == null ? 0 : ((Long) remoteStoreResult.get("toStoreNotUpdated")).longValue();
                    long toRemoveDeletedCur = remoteStoreResult.get("toRemoveDeleted") == null ? 0 : ((Long) remoteStoreResult.get("toRemoveDeleted")).longValue();
                    long toRemoveAlreadyDeletedCur = remoteStoreResult.get("toRemoveAlreadyDeleted") == null ? 0 : ((Long) remoteStoreResult.get("toRemoveAlreadyDeleted")).longValue();
                    
                    toCreateInserted += toCreateInsertedCur;
                    toCreateUpdated += toCreateUpdatedCur;
                    toCreateNotUpdated += toCreateNotUpdatedCur;
                    toStoreInserted += toStoreInsertedCur;
                    toStoreUpdated += toStoreUpdatedCur;
                    toStoreNotUpdated += toStoreNotUpdatedCur;
                    toRemoveDeleted += toRemoveDeletedCur;
                    toRemoveAlreadyDeleted += toRemoveAlreadyDeletedCur;
                }

                long splitTotalTime = System.currentTimeMillis() - splitStartTime;
                if (splitTotalTime < perSplitMinMillis) {
                    perSplitMinMillis = splitTotalTime;
                }
                if (splitTotalTime > perSplitMaxMillis) {
                    perSplitMaxMillis = splitTotalTime;
                }
                
                long runningTimeMillis = System.currentTimeMillis() - startingTimeMillis;
                
                // store latest result on EntitySync, ie update lastSuccessfulSynchTime, should run in own tx
                Map updateEsRunResult = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "lastSuccessfulSynchTime", currentRunEndTime, "userLogin", userLogin));

                // store result of service call on history with results so far, should run in own tx
                Map updateHistoryMap = UtilMisc.toMap("entitySyncId", entitySyncId, "startDate", startDate, "lastSuccessfulSynchTime", currentRunEndTime);
                updateHistoryMap.put("toCreateInserted", new Long(toCreateInserted));
                updateHistoryMap.put("toCreateUpdated", new Long(toCreateUpdated));
                updateHistoryMap.put("toCreateNotUpdated", new Long(toCreateNotUpdated));
                updateHistoryMap.put("toStoreInserted", new Long(toStoreInserted));
                updateHistoryMap.put("toStoreUpdated", new Long(toStoreUpdated));
                updateHistoryMap.put("toStoreNotUpdated", new Long(toStoreNotUpdated));
                updateHistoryMap.put("toRemoveDeleted", new Long(toRemoveDeleted));
                updateHistoryMap.put("toRemoveAlreadyDeleted", new Long(toRemoveAlreadyDeleted));
                updateHistoryMap.put("runningTimeMillis", new Long(runningTimeMillis));
                updateHistoryMap.put("totalStoreCalls", new Long(totalStoreCalls));
                updateHistoryMap.put("totalSplits", new Long(totalSplits));
                updateHistoryMap.put("totalRowsToCreate", new Long(totalRowsToCreate));
                updateHistoryMap.put("totalRowsToStore", new Long(totalRowsToStore));
                updateHistoryMap.put("totalRowsToRemove", new Long(totalRowsToRemove));
                updateHistoryMap.put("perSplitMinMillis", new Long(perSplitMinMillis));
                updateHistoryMap.put("perSplitMaxMillis", new Long(perSplitMaxMillis));
                updateHistoryMap.put("perSplitMinItems", new Long(perSplitMinItems));
                updateHistoryMap.put("perSplitMaxItems", new Long(perSplitMaxItems));
                updateHistoryMap.put("userLogin", userLogin);
                Map updateEsHistRunResult = dispatcher.runSync("updateEntitySyncHistory", updateHistoryMap);
                
                // now we have updated EntitySync and EntitySyncHistory, check both ops for errors...
                if (ServiceUtil.isError(updateEsRunResult)) {
                    String errorMsg = "Error running EntitySync [" + entitySyncId + "], update of EntitySync record with lastSuccessfulSynchTime failed.";
                    List errorList = new LinkedList();
                    saveSyncErrorInfo(entitySyncId, startDate, "ESR_DATA_ERROR", errorList, dispatcher, userLogin);
                    return ServiceUtil.returnError(errorMsg, errorList, null, updateEsRunResult);
                }
                
                if (ServiceUtil.isError(updateEsHistRunResult)) {
                    String errorMsg = "Error running EntitySync [" + entitySyncId + "], update of EntitySyncHistory (startDate:[" + startDate + "]) record with lastSuccessfulSynchTime and result stats failed.";
                    List errorList = new LinkedList();
                    saveSyncErrorInfo(entitySyncId, startDate, "ESR_DATA_ERROR", errorList, dispatcher, userLogin);
                    return ServiceUtil.returnError(errorMsg, errorList, null, updateEsHistRunResult);
                }
                
                // update start time, loop
                currentRunStartTime = currentRunEndTime;
            }

            // the lastSuccessfulSynchTime on EntitySync will already be set, so just set status as completed 
            Map completeEntitySyncRes = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", "ESR_COMPLETE", "userLogin", userLogin));
            if (ServiceUtil.isError(completeEntitySyncRes)) {
                // what to do here? try again?
                return ServiceUtil.returnError("Could not mark Entity Sync as complete, but all synchronization was successful", null, null, completeEntitySyncRes);
            }
            
            // if nothing moved over, remove the history record, otherwise store status
            long totalRows = totalRowsToCreate + totalRowsToStore + totalRowsToRemove;
            if (totalRows == 0) {
                Map deleteEntitySyncHistRes = dispatcher.runSync("deleteEntitySyncHistory", UtilMisc.toMap("entitySyncId", entitySyncId, "startDate", startDate, "userLogin", userLogin));
                if (ServiceUtil.isError(deleteEntitySyncHistRes)) {
                    return ServiceUtil.returnError("Could not remove Entity Sync History (done becuase nothing was synced in this call), but all synchronization was successful", null, null, deleteEntitySyncHistRes);
                }
            } else {
                // the lastSuccessfulSynchTime on EntitySync will already be set, so just set status as completed 
                Map completeEntitySyncHistRes = dispatcher.runSync("updateEntitySyncHistory", UtilMisc.toMap("entitySyncId", entitySyncId, "startDate", startDate, "runStatusId", "ESR_COMPLETE", "userLogin", userLogin));
                if (ServiceUtil.isError(completeEntitySyncHistRes)) {
                    // what to do here? try again?
                    return ServiceUtil.returnError("Could not mark Entity Sync History as complete, but all synchronization was successful", null, null, completeEntitySyncHistRes);
                }
            }
            
            if (Debug.infoOn()) Debug.logInfo("Finished runEntitySync: totalRows=" + totalRows + ", totalRowsToCreate=" + totalRowsToCreate + ", totalRowsToStore=" + totalRowsToStore + ", totalRowsToRemove=" + totalRowsToRemove, module);
        } catch (GenericEntityException e) {
            String errorMessage = "Error running EntitySync [" + entitySyncId + "], data access error: " + e.toString();
            Debug.logError(e, errorMessage, module);
            List errorList = new LinkedList();
            saveSyncErrorInfo(entitySyncId, startDate, "ESR_DATA_ERROR", errorList, dispatcher, userLogin);
            return ServiceUtil.returnError(errorMessage, errorList, null, null);
        } catch (GenericServiceException e) {
            String errorMessage = "Error running EntitySync [" + entitySyncId + "], service call error: " + e.toString();
            Debug.logError(e, errorMessage, module);
            List errorList = new LinkedList();
            saveSyncErrorInfo(entitySyncId, startDate, "ESR_SERVICE_ERROR", errorList, dispatcher, userLogin);
            return ServiceUtil.returnError(errorMessage, errorList, null, null);
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    protected static void saveSyncErrorInfo(String entitySyncId, Timestamp startDate, String runStatusId, List errorMessages, LocalDispatcher dispatcher, GenericValue userLogin) {
        // set error statuses on the EntitySync and EntitySyncHistory entities
        try {
            Map errorEntitySyncRes = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", runStatusId, "userLogin", userLogin));
            if (ServiceUtil.isError(errorEntitySyncRes)) {
                errorMessages.add("Could not save error run status [" + runStatusId + "] on EntitySync with ID [" + entitySyncId + "]: " + errorEntitySyncRes.get(ModelService.ERROR_MESSAGE));
            }
        } catch (GenericServiceException e) {
            errorMessages.add("Could not save error run status [" + runStatusId + "] on EntitySync with ID [" + entitySyncId + "]: " + e.toString());
        }
        if (startDate != null) {
            try {
                Map errorEntitySyncHistoryRes = dispatcher.runSync("updateEntitySyncHistory", UtilMisc.toMap("entitySyncId", entitySyncId, "startDate", startDate, "runStatusId", runStatusId, "userLogin", userLogin));
                if (ServiceUtil.isError(errorEntitySyncHistoryRes)) {
                    errorMessages.add("Could not save error run status [" + runStatusId + "] on EntitySyncHistory with ID [" + entitySyncId + "]: " + errorEntitySyncHistoryRes.get(ModelService.ERROR_MESSAGE));
                }
            } catch (GenericServiceException e) {
                errorMessages.add("Could not save error run status [" + runStatusId + "] on EntitySyncHistory with ID [" + entitySyncId + ":" + startDate + "]: " + e.toString());
            }
        }
    }
    
    /** prepare a list of all entities we want to synchronize: remove all view-entities and all entities that don't match the patterns attached to this EntitySync */
    protected static List makeEntityModelToUseList(GenericDelegator delegator, GenericValue entitySync) throws GenericEntityException {
        List entityModelToUseList = new LinkedList();
        List entitySyncIncludes = entitySync.getRelated("EntitySyncInclude");

        // get these ones as well, and just add them to the main list, it will have an extra field but that shouldn't hurt anything in the code below
        List entitySyncGroupIncludes = entitySync.getRelated("EntitySyncInclGrpDetailView");
        entitySyncIncludes.addAll(entitySyncGroupIncludes);

        Iterator entityNameIter = delegator.getModelReader().getEntityNamesIterator();
        while (entityNameIter.hasNext()) {
            String entityName = (String) entityNameIter.next();
            ModelEntity modelEntity = delegator.getModelEntity(entityName);
            
            // if view-entity, throw it out
            if (modelEntity instanceof ModelViewEntity) {
                continue;
            }
            
            // if it doesn't have either or both of the two update stamp fields, throw it out
            if (!modelEntity.isField(ModelEntity.STAMP_FIELD) || !modelEntity.isField(ModelEntity.STAMP_TX_FIELD)) {
                continue;
            }
            
            // if there are no includes records, always include; otherwise check each one to make sure at least one matches
            if (entitySyncIncludes.size() == 0) {
                entityModelToUseList.add(modelEntity);
            } else {
                // we have different types of include applications: ESIA_INCLUDE, ESIA_EXCLUDE, ESIA_ALWAYS
                // if we find an always we can break right there because this will always be include regardless of excludes, etc
                // if we find an include or exclude we have to finish going through the rest of them just in case there is something that overrides it (ie an exclude for an include or an always for an exclude)
                boolean matchesInclude = false;
                boolean matchesExclude = false;
                boolean matchesAlways = false;
                Iterator entitySyncIncludeIter = entitySyncIncludes.iterator();
                while (entitySyncIncludeIter.hasNext()) {
                    GenericValue entitySyncInclude = (GenericValue) entitySyncIncludeIter.next();
                    String entityOrPackage = entitySyncInclude.getString("entityOrPackage");
                    boolean matches = false;
                    if (entityName.equals(entityOrPackage)) {
                        matches = true;
                    } else if (modelEntity.getPackageName().startsWith(entityOrPackage)) {
                        matches = true;
                    }
                    
                    if (matches) {
                        if ("ESIA_INCLUDE".equals(entitySyncInclude.getString("applEnumId"))) {
                            matchesInclude = true;
                        } else if ("ESIA_EXCLUDE".equals(entitySyncInclude.getString("applEnumId"))) {
                            matchesExclude = true;
                        } else if ("ESIA_ALWAYS".equals(entitySyncInclude.getString("applEnumId"))) {
                            matchesAlways = true;
                            break;
                        }
                    }
                }
                
                if (matchesAlways || (matchesInclude && !matchesExclude)) {
                    // make sure this log message is not checked in uncommented:
                    //Debug.log("In runEntitySync adding [" + modelEntity.getEntityName() + "] to list of Entities to sync", module);
                    entityModelToUseList.add(modelEntity);
                }
            }
        }
        
        if (Debug.infoOn()) Debug.logInfo("In runEntitySync with ID [" + entitySync.get("entitySyncId") + "] syncing " + entityModelToUseList.size() + " entities", module);
        return entityModelToUseList;
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
                GenericValue entitySyncRemove = (GenericValue) keyToRemoveIter.next();
                // pull the PK from the EntitySyncRemove in the primaryKeyRemoved field, de-XML-serialize it and check to see if it exists, if so remove and count, if not just count already removed
                String primaryKeyRemoved = entitySyncRemove.getString("primaryKeyRemoved");
                GenericEntity pkToRemove = null;
                try {
                    pkToRemove = (GenericEntity) XmlSerializer.deserialize(primaryKeyRemoved, delegator);
                } catch (IOException e) {
                    String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                    Debug.logError(e, errorMsg, module);
                    return ServiceUtil.returnError(errorMsg);
                } catch (SAXException e) {
                    String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                    Debug.logError(e, errorMsg, module);
                    return ServiceUtil.returnError(errorMsg);
                } catch (ParserConfigurationException e) {
                    String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                    Debug.logError(e, errorMsg, module);
                    return ServiceUtil.returnError(errorMsg);
                } catch (SerializeException e) {
                    String errorMsg = "Error deserializing GenericPK to remove in Entity Sync Data for entitySyncId [" + entitySyncId + "] and entitySyncRemoveId [" + entitySyncRemove.getString("entitySyncRemoveId") + "]: " + e.toString();
                    Debug.logError(e, errorMsg, module);
                    return ServiceUtil.returnError(errorMsg);
                }
                
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
