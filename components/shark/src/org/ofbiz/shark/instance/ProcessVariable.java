/*
 * $Id: ProcessVariable.java,v 1.2 2004/07/03 19:54:26 jonesde Exp $
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
 * @version    $Revision: 1.2 $
 * @since      3.1
 */
public class ProcessVariable extends AbstractVariable implements ProcessVariablePersistenceInterface {

    public static final String module = ProcessVariable.class.getName();

    protected GenericDelegator delegator = null;
    protected GenericValue processVariable = null;
    protected boolean newValue = false;

    protected ProcessVariable() {}

    protected ProcessVariable(GenericDelegator delegator, String processVariableId) throws PersistenceException {
        this.delegator = delegator;
        if (this.delegator != null) {
            try {
                this.processVariable = delegator.findByPrimaryKey("WfProcessVariable", UtilMisc.toMap("processVariableId", processVariableId));
            } catch (GenericEntityException e) {
                throw new PersistenceException(e);
            }
        } else {
            Debug.logError("Invalid delegator object passed", module);
        }
    }

    protected ProcessVariable(GenericValue processVariable) {
        this.processVariable = processVariable;
        this.delegator = processVariable.getDelegator();
    }

    public ProcessVariable(GenericDelegator delegator) {
        this.newValue = true;
        this.delegator = delegator;

        this.processVariable = delegator.makeValue("WfProcessVariable", UtilMisc.toMap("processVariableId", delegator.getNextSeqId("WfProcessVariable")));
        Debug.log("******* New process variable created", module);
    }

    public static ProcessVariable getInstance(GenericValue processVariable) throws PersistenceException {
        ProcessVariable var = new ProcessVariable(processVariable);
        if (var.isLoaded()) {            
            return var;
        }
        return null;
    }

    public static ProcessVariable getInstance(String processVariableId) throws PersistenceException {
        ProcessVariable var = new ProcessVariable(SharkContainer.getDelegator(), processVariableId);
        if (var.isLoaded()) {
            return var;
        }
        return null;
    }

    public boolean isLoaded() {
        if (processVariable == null) {
            return false;
        }
        return true;
    }

    public void setProcessId(String pId) {
        processVariable.set("processId", pId);
    }

    public String getProcessId() {
        return processVariable.getString("processId");
    }

    public void setDefinitionId(String defId) {
        processVariable.set("definitionId", defId);
    }

    public String getDefinitionId() {
        return processVariable.getString("definitionId");
    }

    public void setValue(Object val) {
        byte[] value = this.getBytes(val);
        processVariable.setBytes("varValue", (value != null ? value : null));
    }

    public Object getValue() {
        byte[] value = processVariable.getBytes("varValue");
        return this.getObject(value);                
    }

    public void store() throws GenericEntityException {
        if (newValue) {
            delegator.createOrStore(processVariable);
            newValue = false;
        } else {
            delegator.store(processVariable);
        }
    }

    public void reload() throws GenericEntityException {
        if (!newValue) {
            processVariable.refresh();
        }
    }

    public void remove() throws GenericEntityException {
        if (!newValue) {
            delegator.removeValue(processVariable);
        }
    }
}
