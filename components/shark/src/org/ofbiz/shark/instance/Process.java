/*
 * $Id: Process.java,v 1.2 2004/05/11 16:59:55 ajzeneski Exp $
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
 * @version    $Revision: 1.2 $
 * @since      3.1
 */
public class Process implements ProcessPersistenceInterface {

    public static final String module = Process.class.getName();

    protected GenericDelegator delegator = null;
    protected GenericValue process = null;
    protected boolean newValue = false;

    protected Process() {}

    protected Process(GenericDelegator delegator, String processId) throws PersistenceException {
        this.delegator = delegator;
        if (this.delegator != null) {
            try {
                this.process = delegator.findByPrimaryKey("WfProcess", UtilMisc.toMap("processId", processId));
            } catch (GenericEntityException e) {
                throw new PersistenceException(e);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    protected Process(GenericValue process) {
        this.process = process;
        this.delegator = process.getDelegator();
    }

    public Process(GenericDelegator delegator) {
        this.newValue = true;
        this.delegator = delegator;
        this.process = delegator.makeValue("WfProcess", null);
    }

    public static Process getInstance(GenericValue process) throws PersistenceException {
        Process proc = new Process(process);
        if (proc.isLoaded()) {
            return proc;
        }
        return null;
    }

    public static Process getInstance(String processId) throws PersistenceException {
        Process proc = new Process(SharkContainer.getDelegator(), processId);
        if (proc.isLoaded()) {
            Debug.log("Returning loaded Process", module);
            return proc;
        }
        Debug.log("Returning null Process ID : " + processId, module);
        if (processId == null) Debug.log(new Exception(), module);
        return null;
    }

    public boolean isLoaded() {
        if (process == null) {
            return false;
        }
        return true;
    }

    public void setId(String s) {
        process.set("processId", s);
    }

    public String getId() {
        return process.getString("processId");
    }

    public void setProcessMgrName(String s) {
        process.set("mgrName", s);
    }

    public String getProcessMgrName() {
        return process.getString("mgrName");
    }

    public void setActivityRequesterId(String s) {
        process.set("activityReqId", s);
    }

    public String getActivityRequesterId() {
        return process.getString("activityReqId");
    }

    public void setActivityRequestersProcessId(String s) {
        process.set("activityReqProcessId", s);
    }

    public String getActivityRequestersProcessId() {
        return process.getString("activityReqProcessId");
    }

    public void setResourceRequesterId(String s) {
        process.set("resourceReqId", s);
    }

    public String getResourceRequesterId() {
        return process.getString("resourceReqId");
    }

    public void setState(String s) {
        process.set("currentState", s);
    }

    public String getState() {
        return process.getString("currentState");
    }

    public String getName() {
        return process.getString("processName");
    }

    public void setName(String s) {
        process.set("processName", s);
    }

    public String getDescription() {
        return process.getString("description");
    }

    public void setDescription(String s) {
        process.set("description", s);
    }

    public int getPriority() {
        return process.getLong("priority").intValue();
    }

    public void setPriority(int i) {
        process.set("priority", new Long(i));
    }

    public long getLastStateTime() {
        return process.get("lastStateTime") != null ? process.getTimestamp("lastStateTime").getTime() : 0;
    }

    public void setLastStateTime(long timestamp) {
        process.set("lastStateTime", UtilDateTime.getTimestamp(timestamp));
    }

    public long getStartedTime() {
        return process.get("startedTime") != null ? process.getTimestamp("startedTime").getTime() : 0;
    }

    public void setStartedTime(long timestamp) {
        process.set("startedTime", UtilDateTime.getTimestamp(timestamp));
    }

    public void store() throws GenericEntityException {
        if (newValue) {
            delegator.createOrStore(process);
            newValue = false;
        } else {
            delegator.store(process);
        }
    }

    public void reload() throws GenericEntityException {
        if (!newValue) {
            process.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        if (!newValue) {
            delegator.removeValue(process);
            Debug.log("**** REMOVED : " + this, module);
        }

        // remove all requesters
        delegator.removeByAnd("WfRequester", UtilMisc.toMap("processId", this.getId()));
    }
}
