/*
 * $Id: ActivityVariable.java,v 1.1 2004/04/22 15:41:00 ajzeneski Exp $
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

import java.io.IOException;

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
public class ActivityVariable extends AbstractVariable implements ActivityVariablePersistenceInterface {

    public static final String module = ActivityVariable.class.getName();

    protected GenericDelegator delegator = null;
    protected GenericValue activityVariable = null;
    protected boolean newValue = false;

    protected ActivityVariable() {}

    protected ActivityVariable(GenericDelegator delegator, String activityVariableId) throws PersistenceException {
        this.delegator = delegator;
        if (this.delegator != null) {
            try {
                this.activityVariable = delegator.findByPrimaryKey("WfActivityVariable", UtilMisc.toMap("activityVariableId", activityVariableId));
            } catch (GenericEntityException e) {
                throw new PersistenceException(e);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    protected ActivityVariable(GenericValue activityVariable) {
        this.activityVariable = activityVariable;
        this.delegator = activityVariable.getDelegator();
    }

    public ActivityVariable(GenericDelegator delegator) {
        this.newValue = true;
        this.delegator = delegator;

        Long variableId = delegator.getNextSeqId("WfActivityVariable");
        this.activityVariable = delegator.makeValue("WfActivityVariable", UtilMisc.toMap("activityVariableId", variableId.toString()));
        Debug.log("******* New activity variable created", module);
    }

    public static ActivityVariable getInstance(GenericValue activityVariable) throws PersistenceException {
        ActivityVariable var = new ActivityVariable(activityVariable);
        if (var.isLoaded()) {
            return var;
        }
        return null;
    }

    public static ActivityVariable getInstance(String activityVariableId) throws PersistenceException {
        ActivityVariable var = new ActivityVariable(SharkContainer.getDelegator(), activityVariableId);
        if (var.isLoaded()) {
            return var;
        }
        return null;
    }

    public boolean isLoaded() {
        if (activityVariable == null) {
            return false;
        }
        return true;
    }

    public void setActivityId(String aId) {
        activityVariable.set("activityId", aId);
    }

    public String getActivityId() {
        return activityVariable.getString("activityId");
    }

    public void setDefinitionId(String defId) {
        activityVariable.set("definitionId", defId);
    }

    public String getDefinitionId() {
        return activityVariable.getString("definitionId");
    }

    public void setValue(Object val) {
        byte[] value = this.getBytes(val);
        activityVariable.setBytes("varValue", (value != null ? value : null));
    }

    public Object getValue() {
        byte[] value = activityVariable.getBytes("varValue");
        return this.getObject(value);        
    }

    public void setResultVariable(boolean modified) {
        activityVariable.set("isModified", (modified ? "Y" : "N"));
    }

    public boolean isResultVariable() {
        return (activityVariable.get("isModified") != null ?
                ("Y".equals(activityVariable.getString("isModified")) ? true : false) : false);
    }

    public void store() throws GenericEntityException {
        if (newValue) {
            delegator.createOrStore(activityVariable);
            newValue = false;
        } else {
            delegator.store(activityVariable);
        }
    }

    public void reload() throws GenericEntityException {
        if (!newValue) {
            activityVariable.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        if (!newValue) {
            delegator.removeValue(activityVariable);
        }
    }
}
