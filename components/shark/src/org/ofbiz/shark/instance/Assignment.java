/*
 * $Id: Assignment.java,v 1.1 2004/04/22 15:41:00 ajzeneski Exp $
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
import org.ofbiz.shark.container.SharkContainer;

import org.enhydra.shark.api.internal.instancepersistence.*;

/**
 * Persistance Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.1 $
 * @since      3.1
 */
public class Assignment implements AssignmentPersistenceInterface {

    public static final String module = Assignment.class.getName();

    protected GenericDelegator delegator = null;
    protected GenericValue assignment = null;
    protected boolean newValue = false;

    protected Assignment() {}

    protected Assignment(GenericDelegator delegator, String activityId, String userName) throws PersistenceException {
        this.delegator = delegator;
        if (this.delegator != null) {
            try {
                this.assignment = delegator.findByPrimaryKey("WfAssignment", UtilMisc.toMap("activityId", activityId, "userName", userName));
            } catch (GenericEntityException e) {
                throw new PersistenceException(e);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    protected Assignment(GenericValue assignment) {
        this.assignment = assignment;
        this.delegator = assignment.getDelegator();
    }

    public Assignment(GenericDelegator delegator) {
        this.newValue = true;
        this.delegator = delegator;
        this.assignment = delegator.makeValue("WfAssignment", null);
        Debug.log("******* New assignment created", module);
    }

    public static Assignment getInstance(GenericValue assignment) throws PersistenceException {
        Assignment assign = new Assignment(assignment);
        if (assign.isLoaded()) {
            return assign;
        }
        return null;
    }

    public static Assignment getInstance(String activityId, String userName) throws PersistenceException {
        Assignment assign = new Assignment(SharkContainer.getDelegator(), activityId, userName);
        if (assign.isLoaded()) {
            return assign;
        }
        return null;
    }

    public boolean isLoaded() {
        if (assignment == null) {
            return false;
        }
        return true;
    }

    public void setActivityId(String actId) {
        assignment.set("activityId", actId);
        Debug.log("Set activityId on assignment : " + actId, module);
        // set the processId - kludge
        /* should not be needed anymore
        try {
            Activity activity = Activity.getInstance(actId);
            if (activity != null) {
                this.setProcessId(activity.getProcessId());
            }
        } catch (PersistenceException e) {
            Debug.logError(e, module);
        }
        */
    }

    public String getActivityId() {
        return assignment.getString("activityId");
    }

    public void setResourceUsername(String username) {
        assignment.set("userName", username);
        Debug.log("Set userName on assignment : " + username, module);
    }

    public String getResourceUsername() {
        return assignment.getString("userName");
    }

    public void setProcessId(String procId) {
        assignment.set("processId", procId);
        Debug.log("Set processId on assignment : " + procId, module);
    }

    public String getProcessId() {
        return assignment.getString("processId");
    }

    public void store() throws GenericEntityException {
        if (newValue) {
            delegator.createOrStore(assignment);
            newValue = false;
        } else {
            delegator.store(assignment);
        }
        Debug.log("Stored assignment : " + assignment, module);
    }

    public void reload() throws GenericEntityException {
        if (!newValue) {
            assignment.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        if (!newValue) {
            delegator.removeValue(assignment);
            Debug.log("**** REMOVED : " + this, module);
        }
    }
}
