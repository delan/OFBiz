/*
 * $Id: EventAudit.java,v 1.2 2004/07/03 19:54:25 jonesde Exp $
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

import org.enhydra.shark.api.internal.instancepersistence.*;

/**
 * Persistance Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.1
 */
public class EventAudit implements EventAuditPersistenceInterface {

    public static final String module = EventAudit.class.getName();

    protected GenericDelegator delegator = null;
    protected String eventAuditId = null;
    private GenericValue eventAudit = null;
    private boolean newValue = false;

    protected EventAudit() {}

    public EventAudit(GenericDelegator delegator, String eventAuditId) {
        this.delegator = delegator;
        this.eventAuditId = eventAuditId;
        if (this.delegator != null) {
            try {
                this.eventAudit = delegator.findByPrimaryKey("WfEventAudit", UtilMisc.toMap("eventAuditId", eventAuditId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    public EventAudit(GenericDelegator delegator) {
        this.newValue = true;
        this.delegator = delegator;

        this.eventAuditId = delegator.getNextSeqId("WfEventAudit");
        this.eventAudit = delegator.makeValue("WfEventAudit", UtilMisc.toMap("eventAuditId", eventAuditId));
    }

    public EventAudit(GenericValue eventAudit) {
        this.eventAuditId = eventAudit.getString("eventAuditId");
        this.eventAudit = eventAudit;
        this.delegator = eventAudit.getDelegator();
    }

    public void setUTCTime(String ts) {
        eventAudit.set("utcTime", ts);
    }

    public String getUTCTime() {
        return eventAudit.getString("utcTime");
    }

    public void setType(String t) {
        eventAudit.set("auditType", t);
    }

    public String getType() {
        return eventAudit.getString("auditType");
    }

    public void setActivityId(String aId) {
        eventAudit.set("activityId", aId);
    }

    public String getActivityId() {
        return eventAudit.getString("activityId");
    }

    public void setActivityName(String an) {
        eventAudit.set("activityName", an);
    }

    public String getActivityName() {
        return eventAudit.getString("activityName");
    }

    public void setProcessId(String pId) {
        eventAudit.set("processId", pId);
    }

    public String getProcessId() {
        return eventAudit.getString("processId");
    }

    public void setProcessName(String pn) {
        eventAudit.set("processName", pn);
    }

    public String getProcessName() {
        return eventAudit.getString("processName");
    }

    public void setProcessDefinitionName(String pdn) {
        eventAudit.set("processDefName", pdn);
    }

    public String getProcessDefinitionName() {
        return eventAudit.getString("processDefName");
    }

    public void setProcessDefinitionVersion(String pdv) {
        eventAudit.set("processDefVer", pdv);
    }

    public String getProcessDefinitionVersion() {
        return eventAudit.getString("processDefVer");
    }

    public void setActivityDefinitionId(String adId) {
        eventAudit.set("activityDefId", adId);
    }

    public String getActivityDefinitionId() {
        return eventAudit.getString("activityDefId");
    }

    public void setProcessDefinitionId(String pdId) {
        eventAudit.set("processDefId", pdId);
    }

    public String getProcessDefinitionId() {
        return eventAudit.getString("processDefId");
    }

    public void setPackageId(String pkgId) {
        eventAudit.set("packageId", pkgId);
    }

    public String getPackageId() {
        return eventAudit.getString("packageId");
    }

    public void store() throws GenericEntityException {
        if (newValue) {
            newValue = false;
            delegator.createOrStore(eventAudit);
        } else {
            delegator.store(eventAudit);
        }
    }

    public void reload() throws GenericEntityException {
        if (!newValue) {
            eventAudit.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        if (!newValue) {
            delegator.removeValue(eventAudit);
        }
    }
}
