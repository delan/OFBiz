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
package org.ofbiz.core.extentity;

import java.net.*;
import java.util.*;
import java.sql.Timestamp;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.service.*;
import org.ofbiz.core.util.*;

/**
 * Entity Engine Cache Services
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a> 
 * @version    $Revision$
 * @since      2.0
 */
public class EntityCacheServices implements DistributedCacheClear {

    protected GenericDelegator delegator = null;
    protected LocalDispatcher dispatcher = null;
    protected String userLoginId = null;

    public EntityCacheServices() {}

    public void setDelegator(GenericDelegator delegator, String userLoginId) {
        this.delegator = delegator;
        this.dispatcher = new LocalDispatcher("entity-cache-dispatcher", delegator, null);
        this.userLoginId = userLoginId;
    }
    
    public GenericValue getAuthUserLogin() {
        GenericValue userLogin = null;
        try {
            userLogin = delegator.findByPrimaryKeyCache("UserLogin", UtilMisc.toMap("userLoginId", userLoginId));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error finding the userLogin for distributed cache clear");
        }
        return userLogin;
    }

    public void distributedClearCacheLine(GenericValue value) {
        // Debug.logInfo("running distributedClearCacheLine for value: " + value);
        if (this.dispatcher == null) {
            Debug.logWarning("No dispatcher is available, somehow the setDelegator (which also creates a dispatcher) was not called, not running distributed cache clear");
            return;
        }

        GenericValue userLogin = getAuthUserLogin();
        if (userLogin == null) {
            Debug.logWarning("The userLogin for distributed cache clear was not found with userLoginId [" + userLoginId + "], not clearing remote caches.");
            return;
        }
        
        try {
            this.dispatcher.runAsync("distributedClearCacheLineByValue", UtilMisc.toMap("value", value, "userLogin", userLogin), false);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error running the distributedClearCacheLineByValue service");
        }
    }

    public void distributedClearCacheLineFlexible(GenericEntity dummyPK) {
        // Debug.logInfo("running distributedClearCacheLineFlexible for dummyPK: " + dummyPK);
        if (this.dispatcher == null) {
            Debug.logWarning("No dispatcher is available, somehow the setDelegator (which also creates a dispatcher) was not called, not running distributed cache clear");
            return;
        }

        GenericValue userLogin = getAuthUserLogin();
        if (userLogin == null) {
            Debug.logWarning("The userLogin for distributed cache clear was not found with userLoginId [" + userLoginId + "], not clearing remote caches.");
            return;
        }
                
        try {
            this.dispatcher.runAsync("distributedClearCacheLineByDummyPK", UtilMisc.toMap("dummyPK", dummyPK, "userLogin", userLogin), false);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error running the distributedClearCacheLineByDummyPK service");
        }
    }

    public void distributedClearCacheLine(GenericPK primaryKey) {
        // Debug.logInfo("running distributedClearCacheLine for primaryKey: " + primaryKey);
        if (this.dispatcher == null) {
            Debug.logWarning("No dispatcher is available, somehow the setDelegator (which also creates a dispatcher) was not called, not running distributed cache clear");
            return;
        }

        GenericValue userLogin = getAuthUserLogin();
        if (userLogin == null) {
            Debug.logWarning("The userLogin for distributed cache clear was not found with userLoginId [" + userLoginId + "], not clearing remote caches.");
            return;
        }
        
        try {
            this.dispatcher.runAsync("distributedClearCacheLineByPrimaryKey", UtilMisc.toMap("primaryKey", primaryKey, "userLogin", userLogin), false);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error running the distributedClearCacheLineByPrimaryKey service");
        }
    }

    public void clearAllCaches() {
        if (this.dispatcher == null) {
            Debug.logWarning("No dispatcher is available, somehow the setDelegator (which also creates a dispatcher) was not called, not running distributed clear all caches");
            return;
        }

        GenericValue userLogin = getAuthUserLogin();
        if (userLogin == null) {
            Debug.logWarning("The userLogin for distributed cache clear was not found with userLoginId [" + userLoginId + "], not clearing remote caches.");
            return;
        }
        
        try {
            this.dispatcher.runAsync("distributedClearAllEntityCaches", UtilMisc.toMap("userLogin", userLogin), false);
        } catch (GenericServiceException e) {
            Debug.logError(e, "Error running the distributedClearAllCaches service");
        }
    }
    
    /**
     * Clear All Entity Caches Service
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map clearAllEntityCaches(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Boolean distributeBool = (Boolean) context.get("distribute");
        boolean distribute = false;
        if (distributeBool != null) distribute = distributeBool.booleanValue();
        
        delegator.clearAllCaches(distribute);
        
        return ServiceUtil.returnSuccess();
    }
    
    /**
     * Clear Cache Line Service: one of the following context parameters is required: value, dummyPK or primaryKey
     *@param ctx The DispatchContext that this service is operating in
     *@param context Map containing the input parameters
     *@return Map with the result of the service, the output parameters
     */
    public static Map clearCacheLine(DispatchContext dctx, Map context) {
        GenericDelegator delegator = dctx.getDelegator();
        Boolean distributeBool = (Boolean) context.get("distribute");
        boolean distribute = false;
        if (distributeBool != null) distribute = distributeBool.booleanValue();

        if (context.containsKey("value")) {
            GenericValue value = (GenericValue) context.get("value");
            if (Debug.infoOn()) Debug.logInfo("Got a clear cache line by value service call; entityName: " + value.getEntityName());
            if (Debug.verboseOn()) Debug.logVerbose("Got a clear cache line by value service call; value: " + value);
            delegator.clearCacheLine(value, distribute);
        } else if (context.containsKey("dummyPK")) {
            GenericEntity dummyPK = (GenericEntity) context.get("dummyPK");
            if (Debug.infoOn()) Debug.logInfo("Got a clear cache line by dummyPK service call; entityName: " + dummyPK.getEntityName());
            if (Debug.verboseOn()) Debug.logVerbose("Got a clear cache line by dummyPK service call; dummyPK: " + dummyPK);
            delegator.clearCacheLineFlexible(dummyPK, distribute);
        } else if (context.containsKey("primaryKey")) {
            GenericPK primaryKey = (GenericPK) context.get("primaryKey");
            if (Debug.infoOn()) Debug.logInfo("Got a clear cache line by primaryKey service call; entityName: " + primaryKey.getEntityName());
            if (Debug.verboseOn()) Debug.logVerbose("Got a clear cache line by primaryKey service call; primaryKey: " + primaryKey);
            delegator.clearCacheLine(primaryKey, distribute);
        }
        return ServiceUtil.returnSuccess();
    }
}
