/*
 * $Id: EntitySyncServices.java,v 1.3 2003/12/06 00:55:20 jonesde Exp $
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

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * Entity Engine Sync Services
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a> 
 * @version    $Revision: 1.3 $
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
        
        try {
            GenericValue entitySync = delegator.findByPrimaryKey("EntitySync", UtilMisc.toMap("entitySyncId", entitySyncId));
            if (entitySync == null) {
                return ServiceUtil.returnError("Not running EntitySync [" + entitySyncId + "], no record found with that ID.");
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
            
            // TODO: create history record, should run in own tx
            
            
            
            String targetServiceName = entitySync.getString("targetServiceName");
            if (UtilValidate.isEmpty(targetServiceName)) {
                return ServiceUtil.returnError("Not running EntitySync [" + entitySyncId + "], no targetServiceName is specified, where do we send the data?");
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
            
            // increment starting time to run until now
            while (currentRunStartTime.before(nowTimestamp)) {
                Timestamp currentRunEndTime = new Timestamp(currentRunStartTime.getTime() + splitMillis);
                if (currentRunEndTime.after(nowTimestamp)) {
                    currentRunEndTime = nowTimestamp;
                }
                
                // TODO: make sure tx times are indexed somehow
                
                // TODO: iterate through entities, get all records with tx stamp in the current time range, put all in a single list
                
                // TODO: get all removed items from the given time range, add to list for those
                
                // TODO: call service named on EntitySync
                
                // TODO: store result of service call on history with results so far, should run in own tx
                // TODO: store latest result on EntitySync, ie update lastSuccessfulSynchTime, should run in own tx
                
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
            // TODO: save failure status on history record, should run in own tx
            // TODO: save failure status on EntitySync record, should run in own tx
            // TODO: return error describing what happened
        } catch (GenericServiceException e) {
            // TODO: save failure status on history record, should run in own tx
            // TODO: save failure status on EntitySync record, should run in own tx
            // TODO: return error describing what happened
        }
        
        return ServiceUtil.returnSuccess();
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
        LocalDispatcher dispatcher = dctx.getDispatcher();
        
        // TODO: sort incoming lists by lastUpdatedStamp
        
        // TODO: iterate through to store list and store each
        // TODO: to store check if exists (find by pk), if not insert; if exists check lastUpdatedStamp: if null or before the candidate value insert, otherwise don't insert
        
        // TODO: iterate through to remove list and remove each
        
        return ServiceUtil.returnSuccess();
    }
}

