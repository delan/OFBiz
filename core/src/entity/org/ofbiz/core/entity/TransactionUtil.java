/*
 * $Id$
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.entity;

import java.sql.*;
import javax.sql.*;
import java.util.*;
import javax.transaction.*;
import javax.transaction.xa.*;

import org.ofbiz.core.util.*;

/**
 * <p>Transaction Utility to help with some common transaction tasks
 * <p>Provides a wrapper around the transaction objects to allow for changes in underlying implementations in the future.
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @created    Dec 12 2001
 * @version    1.0
 */
public class TransactionUtil implements javax.transaction.Status {
    //Debug module name
    public static final String module = TransactionUtil.class.getName();

    /** Begins a transaction in the current thread IF transactions are available; only
     * tries if the current transaction status is ACTIVE, if not active it returns false.
     * If and on only if it begins a transaction it will return true. In other words, if
     * a transaction is already in place it will return false and do nothing.
     */
    public static boolean begin() throws GenericTransactionException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null) {
            try {
                if (ut.getStatus() == TransactionUtil.STATUS_ACTIVE) {
                    Debug.logInfo("[TransactionUtil.begin] active transaction in place, so no transaction begun", module);
                    return false;
                }
                ut.begin();
                Debug.logVerbose("[TransactionUtil.begin] transaction begun", module);
                return true;
            } catch (NotSupportedException e) {
                throw new GenericTransactionException("Not Supported error, could not begin transaction (probably a nesting problem)", e);
            } catch (SystemException e) {
                throw new GenericTransactionException("System error, could not begin transaction", e);
            }
        } else {
            Debug.logInfo("[TransactionUtil.begin] no user transaction, so no transaction begun", module);
            return false;
        }
    }

    /** Gets the status of the transaction in the current thread IF
     * transactions are available, otherwise returns STATUS_NO_TRANSACTION */
    public static int getStatus() throws GenericTransactionException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null) {
            try {
                return ut.getStatus();
            } catch (SystemException e) {
                throw new GenericTransactionException("System error, could not get status", e);
            }
        } else {
            return STATUS_NO_TRANSACTION;
        }
    }

    /** Commits the transaction in the current thread IF transactions are available
     *  AND if beganTransaction is true
     */
    public static void commit(boolean beganTransaction) throws GenericTransactionException {
        if (beganTransaction)
            TransactionUtil.commit();
    }

    /** Commits the transaction in the current thread IF transactions are available */
    public static void commit() throws GenericTransactionException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null) {
            try {
                int status = ut.getStatus();
                if (status != STATUS_NO_TRANSACTION) {
                    ut.commit();
                    Debug.logVerbose("[TransactionUtil.commit] transaction committed", module);
                } else {
                    Debug.logInfo("[TransactionUtil.commit] Not committing transaction, status is STATUS_NO_TRANSACTION", module);
                }
            } catch (RollbackException e) {
                throw new GenericTransactionException("Roll back error, could not commit transaction, was rolled back instead", e);
            } catch (HeuristicMixedException e) {
                throw new GenericTransactionException("Could not commit transaction", e);
            } catch (HeuristicRollbackException e) {
                throw new GenericTransactionException("Could not commit transaction", e);
            } catch (SystemException e) {
                throw new GenericTransactionException("System error, could not commit transaction", e);
            }
        } else {
            Debug.logInfo("[TransactionUtil.commit] UserTransaction is null, not commiting", module);
        }
    }

    /** Rolls back transaction in the current thread IF transactions are available
     *  AND if beganTransaction is true; if beganTransaction is not true,
     *  setRollbackOnly is called to insure that the transaction will be rolled back
     */
    public static void rollback(boolean beganTransaction) throws GenericTransactionException {
        if (beganTransaction) {
            TransactionUtil.rollback();
        } else {
            TransactionUtil.setRollbackOnly();
        }
    }

    /** Rolls back transaction in the current thread IF transactions are available */
    public static void rollback() throws GenericTransactionException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null) {
            try {
                int status = ut.getStatus();
                if (status != STATUS_NO_TRANSACTION) {
                    ut.rollback();
                    Debug.logInfo("[TransactionUtil.rollback] transaction rolled back", module);
                } else {
                    Debug.logInfo("[TransactionUtil.rollback] transaction not rolled back, status is STATUS_NO_TRANSACTION", module);
                }
            } catch (SystemException e) {
                throw new GenericTransactionException("System error, could not roll back transaction", e);
            }
        } else {
            Debug.logInfo("[TransactionUtil.rollback] No UserTransaction, transaction not rolled back", module);
        }
    }

    /** Makes a roll back the only possible outcome of the transaction in the current thread IF transactions are available */
    public static void setRollbackOnly() throws GenericTransactionException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null) {
            try {
                int status = ut.getStatus();
                if (status != STATUS_NO_TRANSACTION) {
                    ut.setRollbackOnly();
                    Debug.logInfo("[TransactionUtil.setRollbackOnly] transaction roll back only set", module);
                } else {
                    Debug.logInfo("[TransactionUtil.setRollbackOnly] transaction roll back only set, status is STATUS_NO_TRANSACTION", module);
                }
            } catch (SystemException e) {
                throw new GenericTransactionException("System error, could not set roll back only on transaction", e);
            }
        } else {
            Debug.logInfo("[TransactionUtil.setRollbackOnly] No UserTransaction, transaction roll back only not set", module);
        }
    }

    /** Sets the timeout of the transaction in the current thread IF transactions are available */
    public static void setTransactionTimeout(int seconds) throws GenericTransactionException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null) {
            try {
                ut.setTransactionTimeout(seconds);
            } catch (SystemException e) {
                throw new GenericTransactionException("System error, could not set transaction timeout", e);
            }
        }
    }

    /** Enlists the given XAConnection and if a transaction is active in the current thread, returns a plain JDBC Connection */
    public static Connection enlistConnection(XAConnection xacon) throws GenericTransactionException {
        if (xacon == null)
            return null;
        try {
            XAResource resource = xacon.getXAResource();
            TransactionUtil.enlistResource(resource);
            return xacon.getConnection();
        } catch (SQLException e) {
            throw new GenericTransactionException("SQL error, could not enlist connection in transaction even though transactions are available", e);
        }
    }

    public static void enlistResource(XAResource resource) throws GenericTransactionException {
        if (resource == null)
            return;

        try {
            TransactionManager tm = TransactionFactory.getTransactionManager();
            if (tm != null && tm.getStatus() == STATUS_ACTIVE) {
                Transaction tx = tm.getTransaction();
                if (tx != null)
                    tx.enlistResource(resource);
            }
        } catch (RollbackException e) {
            throw new GenericTransactionException("Roll Back error, could not enlist connection in transaction even though transactions are available, current transaction rolled back", e);
        } catch (SystemException e) {
            throw new GenericTransactionException("System error, could not enlist connection in transaction even though transactions are available", e);
        }
    }
}
