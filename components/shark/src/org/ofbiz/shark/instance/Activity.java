/*
 * $Id: Activity.java,v 1.3 2004/07/11 23:26:25 ajzeneski Exp $
 *
 * Copyright (c) 2004 The Open For Business Project - www.ofbiz.org
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
package org.ofbiz.shark.instance;

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.shark.container.SharkContainer;

import org.enhydra.shark.api.internal.instancepersistence.*;

/**
 * Persistance Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.3 $
 * @since      3.1
 */
public class Activity implements ActivityPersistenceInterface {

    public static final String module = Activity.class.getName();

    protected GenericDelegator delegator = null;
    protected GenericValue activity = null;
    protected boolean newValue = false;

    protected Activity() {}

    protected Activity(GenericDelegator delegator, String activityId) throws PersistenceException {
        this.delegator = delegator;
        if (this.delegator != null) {
            try {
                this.activity = delegator.findByPrimaryKey("WfActivity", UtilMisc.toMap("activityId", activityId));
            } catch (GenericEntityException e) {
                throw new PersistenceException(e);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    protected Activity(GenericValue activity) {
        this.activity = activity;
        this.delegator = activity.getDelegator();
    }

    public Activity(GenericDelegator delegator) {
        this.newValue = true;
        this.delegator = delegator;
        this.activity = delegator.makeValue("WfActivity", null);
    }

    public static Activity getInstance(GenericValue activity) throws PersistenceException {
        Activity act = new Activity(activity);
        if (act.isLoaded()) {
            return act;
        }
        return null;
    }

    public static Activity getInstance(String activityId) throws PersistenceException {
        Activity act = new Activity(SharkContainer.getDelegator(), activityId);
        if (act.isLoaded()) {
            return act;
        }
        return null;
    }

    public boolean isLoaded() {
        if (activity == null) {
            return false;
        }
        return true;
    }

    public void setId(String s) {
        activity.set("activityId", s);
    }

    public String getId() {
        return activity.getString("activityId");
    }

    public void setActivitySetDefinitionId(String asdId) {
        activity.set("setDefinitionId", asdId);
    }

    public String getActivitySetDefinitionId() {
        return activity.getString("setDefinitionId");
    }

    public void setActivityDefinitionId(String s) {
        activity.set("definitionId", s);
    }

    public String getActivityDefinitionId() {
       return activity.getString("definitionId");
    }

    public void setProcessId(String s) {
        activity.set("processId", s);
    }

    public String getProcessId() {
        return activity.getString("processId");
    }

    public void setSubflowProcessId(String s) {
        activity.set("subFlowId", s);
    }

    public String getSubflowProcessId() {
        return activity.getString("subFlowId");
    }

    public void setResourceUsername(String s) {
        activity.set("resourceUser", s);
    }

    public String getResourceUsername() {
        return activity.getString("resourceUser");
    }

    public void setState(String s) {
        activity.set("currentState", s);
    }

    public String getState() {
        return activity.getString("currentState");
    }

    public void setBlockActivityId(String s) {
        activity.set("blockId", s);
    }

    public String getBlockActivityId() {
        return activity.getString("blockId");
    }

    public String getName() {
        return activity.getString("activityName");
    }

    public void setName(String s) {
        activity.set("activityName", s);
    }

    public String getDescription() {
        return activity.getString("description");
    }

    public void setDescription(String s) {
        activity.set("description", s);
    }

    public int getPriority() {
        return activity.getLong("priority").intValue();
    }

    public void setPriority(int i) {
        activity.set("priority", new Long(i));
    }

    public long getLastStateTime() {
        return activity.get("lastStateTime") != null ? activity.getTimestamp("lastStateTime").getTime() : 0;
    }

    public void setLastStateTime(long timestamp) {
        activity.set("lastStateTime", UtilDateTime.getTimestamp(timestamp));
    }

    public long getAcceptedTime() {
        return activity.get("acceptedTime") != null ? activity.getTimestamp("acceptedTime").getTime() : 0;
    }

    public void setAcceptedTime(long timestamp) {
        activity.set("acceptedTime", UtilDateTime.getTimestamp(timestamp));
    }

    public long getActivatedTime() {
        return activity.get("activatedTime") != null ? activity.getTimestamp("activatedTime").getTime() : 0;
    }

    public void setActivatedTime(long timestamp) {
        activity.set("activatedTime", UtilDateTime.getTimestamp(timestamp));
    }

    public void store() throws GenericEntityException {
        if (newValue) {
            delegator.createOrStore(activity);
            newValue = false;
        } else {
            delegator.store(activity);
        }
    }

    public void reload() throws GenericEntityException {
        if (!newValue) {
            activity.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        if (!newValue) {
            delegator.removeValue(activity);
            Debug.log("**** REMOVED : " + this, module);
        }
    }
}