/*
 * $Id: EntitySyncServices.java,v 1.9 2003/12/12 03:58:34 jonesde Exp $
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
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
 * @version    $Revision: 1.9 $
 * @since      3.0
 */
public class EntitySyncServices {
    
    public static final String module = EntitySyncServices.class.getName();
    
    // set default split to 10 seconds, ie try not to get too much data moving over at once
    public static final long defaultSyncSplitMillis = 10000;

    /**
     * Run an Entity Sync (checks to see if other already running, etc)
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map runEntitySync(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        String entitySyncId = (String) context.get("entitySyncId");
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
            
            // check to see if this sync is already running, if so return error
            if ("ESR_RUNNING".equals(entitySync.getString("runStatusId"))) {
                return ServiceUtil.returnError("Not running EntitySync [" + entitySyncId + "], an instance is already running.");
            }
            
            // not running, get started NOW
            // set running status on entity sync, run in its own tx
            Map startEntitySyncRes = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", "ESR_RUNNING"));
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
            }
            
            // create history record, should run in own tx
            Map initialHistoryRes = dispatcher.runSync("createEntitySyncHistory", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", "ESR_RUNNING", "beginningSynchTime", currentRunStartTime));
            if (ServiceUtil.isError(initialHistoryRes)) {
                String errorMsg = "Not running EntitySync [" + entitySyncId + "], could not create EntitySyncHistory";
                List errorList = new LinkedList();
                saveSyncErrorInfo(entitySyncId, startDate, "ESR_DATA_ERROR", errorList, dispatcher);
                return ServiceUtil.returnError(errorMsg, errorList, null, initialHistoryRes);
            }
            startDate = (Timestamp) initialHistoryRes.get("startDate");

            long numInserted = 0;
            long numUpdated = 0;
            long numNotUpdated = 0;
            long numRemoved = 0;
            long numAlreadyRemoved = 0;
            long totalRowsToStore = 0;
            long totalRowsToRemove = 0;
            
            long startingTimeMillis = System.currentTimeMillis();
            
            // increment starting time to run until now
            while (currentRunStartTime.before(nowTimestamp)) {
                Timestamp currentRunEndTime = new Timestamp(currentRunStartTime.getTime() + splitMillis);
                if (currentRunEndTime.after(nowTimestamp)) {
                    currentRunEndTime = nowTimestamp;
                }
                
                // make sure tx times are indexed somehow
                // TODO: keep track of how long these sync runs take and store that info on the history table
                // save info about removed, all entities that don't have no-auto-stamp set, this will be done in the GenericDAO like the stamp sets
                
                // simulate two ordered lists and merge them on-the-fly for faster combined sorting
                ArrayList valuesToStore = new ArrayList(); // make it an ArrayList to easily merge in sorted lists
                
                // iterate through entities, get all records with tx stamp in the current time range, put all in a single list
                Iterator entityModelToUseIter = entityModelToUseList.iterator();
                while (entityModelToUseIter.hasNext()) {
                    int insertBefore = 0;
                    ModelEntity modelEntity = (ModelEntity) entityModelToUseIter.next();
                    // find all instances of this entity with the STAMP_TX_FIELD != null, sort ascending to get lowest/oldest value first, then grab first and consider as candidate currentRunStartTime
                    EntityCondition findValCondition = new EntityConditionList(UtilMisc.toList(
                            new EntityExpr(ModelEntity.STAMP_TX_FIELD, EntityOperator.GREATER_THAN_EQUAL_TO, currentRunStartTime), 
                            new EntityExpr(ModelEntity.STAMP_TX_FIELD, EntityOperator.LESS_THAN, currentRunEndTime)), EntityOperator.AND);
                    EntityListIterator eli = delegator.findListIteratorByCondition(modelEntity.getEntityName(), findValCondition, null, UtilMisc.toList(ModelEntity.STAMP_FIELD));
                    GenericValue nextValue = null;
                    while ((nextValue = (GenericValue) eli.next()) != null) {
                        // find first value in valuesToStore list, starting with the current insertBefore value, that has a STAMP_FIELD after the nextValue.STAMP_FIELD
                        while (insertBefore < valuesToStore.size() && ((GenericValue) valuesToStore.get(insertBefore)).getTimestamp(ModelEntity.STAMP_FIELD).before(nextValue.getTimestamp(ModelEntity.STAMP_FIELD))) {
                            insertBefore++;
                        }
                        valuesToStore.add(insertBefore, nextValue);
                    }
                    eli.close();
                }
                
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
                long totalRowsToStoreCur = valuesToStore.size();
                long totalRowsToRemoveCur = keysToRemove.size();

                totalRowsToStore += totalRowsToStoreCur;
                totalRowsToRemove += totalRowsToRemoveCur;
                
                // call service named on EntitySync
                Map remoteStoreResult = dispatcher.runSync(targetServiceName, UtilMisc.toMap("entitySyncId", entitySyncId, "valuesToStore", valuesToStore, "keysToRemove", keysToRemove));
                if (ServiceUtil.isError(remoteStoreResult)) {
                    String errorMsg = "Error running EntitySync [" + entitySyncId + "], call to store service [" + targetServiceName + "] failed.";
                    List errorList = new LinkedList();
                    saveSyncErrorInfo(entitySyncId, startDate, "ESR_OTHER_ERROR", errorList, dispatcher);
                    return ServiceUtil.returnError(errorMsg, errorList, null, remoteStoreResult);
                }
                
                long numInsertedCur = remoteStoreResult.get("numInserted") == null ? 0 : ((Long) remoteStoreResult.get("numInserted")).longValue();
                long numUpdatedCur = remoteStoreResult.get("numUpdated") == null ? 0 : ((Long) remoteStoreResult.get("numUpdated")).longValue();
                long numNotUpdatedCur = remoteStoreResult.get("numNotUpdated") == null ? 0 : ((Long) remoteStoreResult.get("numNotUpdated")).longValue();
                long numRemovedCur = remoteStoreResult.get("numRemoved") == null ? 0 : ((Long) remoteStoreResult.get("numRemoved")).longValue();
                long numAlreadyRemovedCur = remoteStoreResult.get("numAlreadyRemoved") == null ? 0 : ((Long) remoteStoreResult.get("numAlreadyRemoved")).longValue();
                
                numInserted += numInsertedCur;
                numUpdated += numUpdatedCur;
                numNotUpdated += numNotUpdatedCur;
                numRemoved += numRemovedCur;
                numAlreadyRemoved += numAlreadyRemovedCur;
                
                // store latest result on EntitySync, ie update lastSuccessfulSynchTime, should run in own tx
                Map updateEsRunResult = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "lastSuccessfulSynchTime", currentRunEndTime));

                // store result of service call on history with results so far, should run in own tx
                Map updateHistoryMap = UtilMisc.toMap("entitySyncId", entitySyncId, "startDate", startDate, "lastSuccessfulSynchTime", currentRunEndTime);
                updateHistoryMap.put("numInserted", new Long(numInserted));
                updateHistoryMap.put("numUpdated", new Long(numUpdated));
                updateHistoryMap.put("numNotUpdated", new Long(numNotUpdated));
                updateHistoryMap.put("numRemoved", new Long(numRemoved));
                updateHistoryMap.put("numAlreadyRemoved", new Long(numAlreadyRemoved));
                updateHistoryMap.put("runningTimeMillis", new Long(System.currentTimeMillis() - startingTimeMillis));
                Map updateEsHistRunResult = dispatcher.runSync("updateEntitySyncHistory", updateHistoryMap);
                
                // now we have updated EntitySync and EntitySyncHistory, check both ops for errors...
                if (ServiceUtil.isError(updateEsRunResult)) {
                    String errorMsg = "Error running EntitySync [" + entitySyncId + "], update of EntitySync record with lastSuccessfulSynchTime failed.";
                    List errorList = new LinkedList();
                    saveSyncErrorInfo(entitySyncId, startDate, "ESR_DATA_ERROR", errorList, dispatcher);
                    return ServiceUtil.returnError(errorMsg, errorList, null, updateEsRunResult);
                }
                if (ServiceUtil.isError(updateEsHistRunResult)) {
                    String errorMsg = "Error running EntitySync [" + entitySyncId + "], update of EntitySyncHistory (startDate:[" + startDate + "]) record with lastSuccessfulSynchTime and result stats failed.";
                    List errorList = new LinkedList();
                    saveSyncErrorInfo(entitySyncId, startDate, "ESR_DATA_ERROR", errorList, dispatcher);
                    return ServiceUtil.returnError(errorMsg, errorList, null, updateEsHistRunResult);
                }
                
                // update start time, loop
                currentRunStartTime = currentRunEndTime;
            }

            // the lastSuccessfulSynchTime on EntitySync will already be set, so just set status as completed 
            Map completeEntitySyncRes = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", "ESR_COMPLETE"));
            if (ModelService.RESPOND_ERROR.equals(completeEntitySyncRes.get(ModelService.RESPONSE_MESSAGE))) {
                // what to do here? try again?
                return ServiceUtil.returnError("Could not mark Entity Sync as complete, but all synchronization was successful", null, null, completeEntitySyncRes);
            }
        } catch (GenericEntityException e) {
            String errorMessage = "Error running EntitySync [" + entitySyncId + "], data access error: " + e.toString();
            Debug.logError(e, errorMessage, module);
            List errorList = new LinkedList();
            saveSyncErrorInfo(entitySyncId, startDate, "ESR_DATA_ERROR", errorList, dispatcher);
            return ServiceUtil.returnError(errorMessage, errorList, null, null);
        } catch (GenericServiceException e) {
            String errorMessage = "Error running EntitySync [" + entitySyncId + "], service call error: " + e.toString();
            Debug.logError(e, errorMessage, module);
            List errorList = new LinkedList();
            saveSyncErrorInfo(entitySyncId, startDate, "ESR_SERVICE_ERROR", errorList, dispatcher);
            return ServiceUtil.returnError(errorMessage, errorList, null, null);
        }
        
        return ServiceUtil.returnSuccess();
    }
    
    protected static void saveSyncErrorInfo(String entitySyncId, Timestamp startDate, String runStatusId, List errorMessages, LocalDispatcher dispatcher) {
        // set error statuses on the EntitySync and EntitySyncHistory entities
        try {
            Map errorEntitySyncRes = dispatcher.runSync("updateEntitySyncRunning", UtilMisc.toMap("entitySyncId", entitySyncId, "runStatusId", runStatusId));
            if (ModelService.RESPOND_ERROR.equals(errorEntitySyncRes.get(ModelService.RESPONSE_MESSAGE))) {
                errorMessages.add("Could not save error run status [" + runStatusId + "] on EntitySync with ID [" + entitySyncId + "]: " + errorEntitySyncRes.get(ModelService.ERROR_MESSAGE));
            }
        } catch (GenericServiceException e) {
            errorMessages.add("Could not save error run status [" + runStatusId + "] on EntitySync with ID [" + entitySyncId + "]: " + e.toString());
        }
        if (startDate != null) {
            try {
                Map errorEntitySyncHistoryRes = dispatcher.runSync("updateEntitySyncHistory", UtilMisc.toMap("entitySyncId", entitySyncId, "startDate", startDate, "runStatusId", runStatusId));
                if (ModelService.RESPOND_ERROR.equals(errorEntitySyncHistoryRes.get(ModelService.RESPONSE_MESSAGE))) {
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
            
            // if no includes records, always include; otherwise check each one to make sure at least one matches
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
                    entityModelToUseList.add(modelEntity);
                }
            }
        }
        
        return entityModelToUseList;
    }

    /**
     * Store Entity Sync Data
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map storeEntitySyncData(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        //LocalDispatcher dispatcher = dctx.getDispatcher();
        
        String entitySyncId = (String) context.get("entitySyncId");
        // incoming lists will already be sorted by lastUpdatedStamp
        List valuesToStore = (List) context.get("valuesToStore");
        List keysToRemove = (List) context.get("keysToRemove");
        
        try {
            long numInserted = 0;
            long numUpdated = 0;
            long numNotUpdated = 0;
            long numRemoved = 0;
            long numAlreadyRemoved = 0;
            
            // iterate through to store list and store each
            Iterator valueToStoreIter = valuesToStore.iterator();
            while (valueToStoreIter.hasNext()) {
                GenericValue valueToStore = (GenericValue) valueToStoreIter.next();
                // to store check if exists (find by pk), if not insert; if exists check lastUpdatedStamp: if null or before the candidate value insert, otherwise don't insert
                // NOTE: use the delegator from this DispatchContext rather than the one named in the GenericValue
                
                // maintain the original timestamps when doing storage of synced data, by default with will update the timestamps to now
                valueToStore.setIsFromEntitySync(true);
                
                GenericValue existingValue = delegator.findByPrimaryKey(valueToStore.getPrimaryKey());
                if (existingValue == null) {
                    delegator.create(valueToStore);
                    numInserted++;
                } else {
                    // if the existing value has a stamp field that is AFTER the stamp on the valueToStore, don't update it
                    if (existingValue.get(ModelEntity.STAMP_FIELD) != null && existingValue.getTimestamp(ModelEntity.STAMP_FIELD).after(valueToStore.getTimestamp(ModelEntity.STAMP_FIELD))) {
                        numNotUpdated++;
                    } else {
                        delegator.store(valueToStore);
                        numUpdated++;
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
                int numRemByAnd = delegator.removeByAnd(pkToRemove.getEntityName(), pkToRemove);
                if (numRemByAnd == 0) {
                    numAlreadyRemoved++;
                } else {
                    numRemoved++;
                }
            }
            
            Map result = ServiceUtil.returnSuccess();
            result.put("numInserted", new Long(numInserted));
            result.put("numUpdated", new Long(numUpdated));
            result.put("numNotUpdated", new Long(numNotUpdated));
            result.put("numRemoved", new Long(numRemoved));
            result.put("numAlreadyRemoved", new Long(numAlreadyRemoved));
            return result;
        } catch (GenericEntityException e) {
            String errorMsg = "Error saving Entity Sync Data for entitySyncId [" + entitySyncId + "]: " + e.toString();
            Debug.logError(e, errorMsg, module);
            return ServiceUtil.returnError(errorMsg);
        }
    }
}

