/*
 * $Id$
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
 * @version    $Rev:$
 * @since      3.1
 */
public class AndJoinEntry implements AndJoinEntryInterface {

    public static final String module = AndJoinEntry.class.getName();

    protected GenericDelegator delegator = null;
    protected GenericValue andJoin = null;
    protected boolean newValue = false;

    protected AndJoinEntry() {}

    protected AndJoinEntry(GenericDelegator delegator, String andJoinId) throws PersistenceException {
        this.delegator = delegator;
        if (this.delegator != null) {
            try {
                this.andJoin = delegator.findByPrimaryKey("WfAndJoin", UtilMisc.toMap("andJoinId", andJoinId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    protected AndJoinEntry(GenericValue andJoin) {
        this.andJoin = andJoin;
        this.delegator = andJoin.getDelegator();
    }

    public AndJoinEntry(GenericDelegator delegator) {
        this.newValue = true;
        this.delegator = delegator;

        this.andJoin = delegator.makeValue("WfAndJoin", UtilMisc.toMap("andJoinId", delegator.getNextSeqId("WfAndJoin")));
    }

    public static AndJoinEntry getInstance(GenericValue andJoin) throws PersistenceException {
        AndJoinEntry var = new AndJoinEntry(andJoin);
        if (var.isLoaded()) {
            return var;
        }
        return null;
    }

    public static AndJoinEntry getInstance(String andJoinId) throws PersistenceException {
        AndJoinEntry var = new AndJoinEntry(SharkContainer.getDelegator(), andJoinId);
        if (var.isLoaded()) {
            return var;
        }
        return null;
    }

    public boolean isLoaded() {
        if (andJoin == null) {
            return false;
        }
        return true;
    }

    public void setProcessId(String procId) {
        andJoin.set("processId", procId);
    }

    public String getProcessId() {
        return andJoin.getString("processId");
    }

    public void setActivitySetDefinitionId(String asdId) {
        andJoin.set("activitySetDefId", asdId);
    }

    public String getActivitySetDefinitionId() {
        return andJoin.getString("activitySetDefId");
    }

    public void setActivityDefinitionId(String adId) {
        andJoin.set("activityDefId", adId);
    }

    public String getActivityDefinitionId() {
        return andJoin.getString("activityDefId");
    }

    public void setActivityId(String actId) {
        andJoin.set("activityId", actId);
    }

    public String getActivityId() {
        return andJoin.getString("activityId");
    }

    public void store() throws GenericEntityException {
        if (newValue) {
            newValue = false;
            delegator.createOrStore(andJoin);
        } else {
            delegator.store(andJoin);
        }
    }

    public void reload() throws GenericEntityException {
        if (!newValue) {
            andJoin.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        if (!newValue) {
            delegator.removeValue(andJoin);
        }
    }
}
