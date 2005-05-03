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
package org.ofbiz.shark.transaction;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;

import org.enhydra.shark.api.ApplicationMappingTransaction;
import org.enhydra.shark.api.ParticipantMappingTransaction;
import org.enhydra.shark.api.TransactionException;
import org.enhydra.shark.api.UserTransaction;
import org.enhydra.shark.api.RepositoryTransaction;
import org.enhydra.shark.api.RootException;
import org.enhydra.shark.api.internal.transaction.SharkInternalTransaction;
import org.enhydra.shark.api.internal.working.WfProcessInternal;
import org.enhydra.shark.api.internal.working.WfResourceInternal;

/**
 * Shark JTA Transaction Implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev$
 * @since      3.1
 */
public class JtaTransaction implements SharkInternalTransaction, UserTransaction,
        ApplicationMappingTransaction, ParticipantMappingTransaction, RepositoryTransaction {

    public static final String module = JtaTransaction.class.getName();
    public static final int transactionTimeout = 120;

    protected Map resourceMap = new HashMap();
    protected Map processMap = new HashMap();

    protected boolean beganTransaction = false;
    protected boolean active = false;
    protected boolean enabled = true;

    public JtaTransaction() {
        if (enabled) {
            try {
                this.beganTransaction = TransactionUtil.begin(transactionTimeout);
                active = true;
            } catch (GenericTransactionException e) {
                Debug.logError(e, module);
            }
        }
    }

    public void commit() throws TransactionException {
        if (active) {
            try {
                TransactionUtil.commit(beganTransaction);
            } catch (GenericTransactionException e) {
                Debug.logError(e, module);
                throw new TransactionException(e);
            }
            active = false;
        } else {
            Debug.logError(new Exception(), "No active transaction; unable to commit", module);
            //throw new TransactionException("No active transaction");
        }
    }

    public void rollback() throws TransactionException {
        if (active) {
            try {
                TransactionUtil.rollback(beganTransaction, "Transaction rollback from Shark", null);
            } catch (GenericTransactionException e) {
                Debug.logError(e, module);
                throw new TransactionException(e);
            }
            active = false;
        } else {
            Debug.logError(new Exception(), "No active transaction; unable to rollback", module);
            //throw new TransactionException("No active transaction");
        }
    }

    public void release() throws TransactionException {
        if (active) {
            this.commit();
        }        
    }

    public void addToTransaction(String procId, WfProcessInternal proc) throws RootException {
        this.processMap.put(procId, proc);
    }

    public void addToTransaction(String resId, WfResourceInternal res) throws RootException {
        this.resourceMap.put(resId, res);
    }

    public void removeProcess(String procId) throws RootException {
        this.processMap.remove(procId);
    }

    public void removeResource(String resId) throws RootException {
        this.resourceMap.remove(resId);
    }

    public WfProcessInternal getProcess(String procId) throws RootException {
        return (WfProcessInternal) this.processMap.get(procId);
    }

    public WfResourceInternal getResource(String resId) throws RootException {
        return (WfResourceInternal) this.resourceMap.get(resId);
    }
}
