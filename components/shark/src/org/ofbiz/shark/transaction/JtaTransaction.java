/*
 * $Id: JtaTransaction.java,v 1.2 2004/07/11 23:26:30 ajzeneski Exp $
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

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;

import org.enhydra.shark.api.ApplicationMappingTransaction;
import org.enhydra.shark.api.ParticipantMappingTransaction;
import org.enhydra.shark.api.SharkTransaction;
import org.enhydra.shark.api.TransactionException;
import org.enhydra.shark.api.UserTransaction;
import org.enhydra.shark.api.RepositoryTransaction;

/**
 * Shark JTA Transaction Implementation
 *
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Revision: 1.2 $
 * @since      3.1
 */
public class JtaTransaction implements SharkTransaction, UserTransaction,
        ApplicationMappingTransaction, ParticipantMappingTransaction, RepositoryTransaction {

    public static final String module = JtaTransaction.class.getName();
    public static final int transactionTimeout = 120;

    protected boolean beganTransaction = false;
    protected boolean active = false;

    public JtaTransaction() {
        /*
        try {
            beganTransaction = TransactionUtil.begin(transactionTimeout);
            active = true;
        } catch (GenericTransactionException e) {
            Debug.logError(e, module);
        }
        */
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
            //throw new TransactionException("No active transaction");
        }
    }

    public void rollback() throws TransactionException {
        if (active) {
            try {
                TransactionUtil.rollback(beganTransaction);
            } catch (GenericTransactionException e) {
                Debug.logError(e, module);
                throw new TransactionException(e);
            }
            active = false;
        } else {
            //throw new TransactionException("No active transaction");
        }
    }

    public void release() throws TransactionException {
        this.commit();
    }
}
