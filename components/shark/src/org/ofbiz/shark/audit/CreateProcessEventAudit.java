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
package org.ofbiz.shark.audit;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import org.enhydra.shark.api.internal.eventaudit.CreateProcessEventAuditPersistenceInterface;

/**
 * Persistance Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      3.1
 */
public class CreateProcessEventAudit extends EventAudit implements CreateProcessEventAuditPersistenceInterface {

    public static final String module = AssignmentEventAudit.class.getName();
    protected GenericValue createProcessEventAudit = null;
    private boolean newValue = false;

    protected CreateProcessEventAudit() {}

    public CreateProcessEventAudit(GenericDelegator delegator, String eventAuditId) {
        super(delegator, eventAuditId);
        if (this.delegator != null) {
            try {
                this.createProcessEventAudit = delegator.findByPrimaryKey("WfCreateProcessEventAudit", UtilMisc.toMap("eventAuditId", eventAuditId));
            } catch (GenericEntityException e) {
                Debug.logError(e, module);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    public CreateProcessEventAudit(GenericDelegator delegator) {
        super(delegator);
        this.newValue = true;
        this.createProcessEventAudit = delegator.makeValue("WfCreateProcessEventAudit", UtilMisc.toMap("eventAuditId", this.eventAuditId));
    }

    public CreateProcessEventAudit(GenericValue createProcessEventAudit) {
        super(createProcessEventAudit.getDelegator(), createProcessEventAudit.getString("eventAuditId"));
        this.createProcessEventAudit = createProcessEventAudit;
    }

    public void setPActivityId(String paId) {
        createProcessEventAudit.set("pActivityId", paId);
    }

    public String getPActivityId() {
        return createProcessEventAudit.getString("pActivityId");
    }

    public void setPProcessId(String ppId) {
        createProcessEventAudit.set("pProcessId", ppId);
    }

    public String getPProcessId() {
        return createProcessEventAudit.getString("pProcessId");
    }

    public void setPProcessName(String ppn) {
        createProcessEventAudit.set("pProcessName", ppn);
    }

    public String getPProcessName() {
        return createProcessEventAudit.getString("pProcessName");
    }

    public void setPProcessDefinitionName(String ppdn) {
        createProcessEventAudit.set("pProcessDefName", ppdn);
    }

    public String getPProcessDefinitionName() {
        return createProcessEventAudit.getString("pProcessDefName");
    }

    public void setPProcessDefinitionVersion(String ppdv) {
        createProcessEventAudit.set("pProcessDefVer", ppdv);
    }

    public String getPProcessDefinitionVersion() {
        return createProcessEventAudit.getString("pProcessDefVer");
    }

    public void setPActivityDefinitionId(String padId) {
        createProcessEventAudit.set("pActivityDefId", padId);
    }

    public String getPActivityDefinitionId() {
        return createProcessEventAudit.getString("pActivityDefId");
    }

    public void setPProcessDefinitionId(String ppdId) {
        createProcessEventAudit.set("pProcessDefId", ppdId);
    }

    public String getPProcessDefinitionId() {
        return createProcessEventAudit.getString("pProcessDefId");
    }

    public void setPPackageId(String ppkgId) {
        createProcessEventAudit.set("pPackageId", ppkgId);
    }

    public String getPPackageId() {
        return createProcessEventAudit.getString("pPackageId");
    }

    public void store() throws GenericEntityException {
        super.store();
        if (newValue) {
            newValue = false;
            delegator.createOrStore(createProcessEventAudit);
        } else {
            delegator.store(createProcessEventAudit);
        }
    }

    public void reload() throws GenericEntityException {
        super.reload();
        if (!newValue) {
            createProcessEventAudit.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        super.remove();
        if (!newValue) {
            delegator.removeValue(createProcessEventAudit);
        }
    }
}
