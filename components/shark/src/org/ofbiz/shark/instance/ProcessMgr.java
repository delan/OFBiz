/*
 * $Id: ProcessMgr.java,v 1.3 2004/06/07 16:48:28 ajzeneski Exp $
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

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.shark.container.SharkContainer;

import org.enhydra.shark.api.internal.instancepersistence.*;

/**
 * Persistance Object
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.3 $
 * @since      3.1
 */
public class ProcessMgr implements ProcessMgrPersistenceInterface {

    public static final String module = ProcessMgr.class.getName();

    protected GenericDelegator delegator = null;
    protected GenericValue processMgr = null;
    protected boolean newValue = false;

    protected ProcessMgr() {}

    protected ProcessMgr(GenericDelegator delegator, String name) throws PersistenceException {
        this.delegator = delegator;
        if (this.delegator != null) {
            try {
                this.processMgr = delegator.findByPrimaryKey("WfProcessMgr", UtilMisc.toMap("mgrName", name));
            } catch (GenericEntityException e) {
                throw new PersistenceException(e);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    protected ProcessMgr(GenericValue processMgr) {
        this.processMgr = processMgr;
        this.delegator = processMgr.getDelegator();
    }

    public ProcessMgr(GenericDelegator delegator) {
        this.newValue = true;
        this.delegator = delegator;
        this.processMgr = delegator.makeValue("WfProcessMgr", UtilMisc.toMap("currentState", new Long(0)));
    }

    public static ProcessMgr getInstance(GenericValue processMgr) throws PersistenceException {
        ProcessMgr mgr = new ProcessMgr(processMgr);
        if (mgr.isLoaded()) {
            return mgr;
        }
        return null;
    }

    public static ProcessMgr getInstance(String name) throws PersistenceException {
        ProcessMgr mgr = new ProcessMgr(SharkContainer.getDelegator(), name);
        if (mgr.isLoaded()) {
            return mgr;
        }
        return null;
    }

    public boolean isLoaded() {
        if (processMgr == null) {
            return false;
        }
        return true;
    }

    public void setName(String name) {
        processMgr.set("mgrName", name);
    }

    public String getName() {
        return processMgr.getString("mgrName");
    }

    public void setPackageId(String pkgId) {
        processMgr.set("packageId", pkgId);
    }

    public String getPackageId() {
        return processMgr.getString("packageId");
    }

    public void setProcessDefinitionId(String pdId) {
        processMgr.set("definitionId", pdId);
    }

    public String getProcessDefinitionId() {
        return processMgr.getString("definitionId");
    }

    public void setState(int state) {
        processMgr.set("currentState", new Long(state));
    }

    public int getState() {
        return processMgr.getLong("currentState").intValue();
    }

    public String getVersion() {
        return processMgr.getString("procVersion");
    }

    public void setVersion(String version) {
        processMgr.set("procVersion", version);
    }

    public String getCreated() {
        return processMgr.getString("created");
    }

    public void setCreated(String created) {
        processMgr.set("created", created);
    }

    public void store() throws GenericEntityException {
        if (newValue) {
            delegator.createOrStore(processMgr);
            newValue = false;
        } else {
            delegator.store(processMgr);
        }
    }

    public void reload() throws GenericEntityException {
        if (!newValue) {
            processMgr.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        if (!newValue) {
            delegator.removeValue(processMgr);
            Debug.log("**** REMOVED : " + this, module);
        }
    }
}
