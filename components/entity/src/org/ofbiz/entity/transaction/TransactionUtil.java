/*
 * $Id: TransactionUtil.java,v 1.6 2004/05/25 06:30:57 ajzeneski Exp $
 *
 *  Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.ofbiz.entity.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import javax.sql.XAConnection;
import javax.transaction.*;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.XAException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;

/**
 * <p>Transaction Utility to help with some common transaction tasks
 * <p>Provides a wrapper around the transaction objects to allow for changes in underlying implementations in the future.
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision: 1.6 $
 * @since      2.0
 */
public class TransactionUtil implements Status {
    // Debug module name
    public static final String module = TransactionUtil.class.getName();
    public static Map debugResMap = new HashMap();
    public static boolean debugResources = true;

    /** Begins a transaction in the current thread IF transactions are available; only
     * tries if the current transaction status is ACTIVE, if not active it returns false.
     * If and on only if it begins a transaction it will return true. In other words, if
     * a transaction is already in place it will return false and do nothing.
     */
    public static boolean begin() throws GenericTransactionException {
        return begin(0);
    }
    
    /** Begins a transaction in the current thread IF transactions are available; only
     * tries if the current transaction status is ACTIVE, if not active it returns false.
     * If and on only if it begins a transaction it will return true. In other words, if
     * a transaction is already in place it will return false and do nothing.
     */   
    public static boolean begin(int timeout) throws GenericTransactionException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null) {
            try {
                int currentStatus = ut.getStatus();
                Debug.logVerbose("[TransactionUtil.begin] current status : " + getTransactionStateString(currentStatus), module);
                if (currentStatus == Status.STATUS_ACTIVE) {
                    Debug.logVerbose("[TransactionUtil.begin] active transaction in place, so no transaction begun", module);
                    return false;
                } else if (currentStatus == Status.STATUS_MARKED_ROLLBACK) {
                    Debug.logVerbose("[TransactionUtil.begin] active transaction marked for rollback in place, so no transaction begun", module);
                    throw new GenericTransactionException("The current transaction is marked for rollback, should stop immediately.");
                    //return false;
                }
                
                // set the timeout for THIS transaction
                if (timeout > 0) {                    
                    ut.setTransactionTimeout(timeout);
                    Debug.logVerbose("[TransactionUtil.begin] set transaction timeout to : " + timeout + " seconds", module);    
                }
                
                // begin the transaction
                ut.begin();
                Debug.logVerbose("[TransactionUtil.begin] transaction begun", module);
                
                // reset the transaction stamps, just in case...
                clearTransactionStamps();
                // initialize the start stamp
                getTransactionStartStamp();

                // initialize the debug resource
                if (debugResources) {
                    DebugXaResource dxa = new DebugXaResource();
                    try {
                        dxa.enlist();                        
                    } catch (XAException e) {
                        Debug.logError(e, module);
                    }
                }

                return true;
            } catch (NotSupportedException e) {
                //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
                throw new GenericTransactionException("Not Supported error, could not begin transaction (probably a nesting problem)", e);
            } catch (SystemException e) {
                //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
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
        if (beganTransaction) {
            TransactionUtil.commit();
        }
    }

    /** Commits the transaction in the current thread IF transactions are available */
    public static void commit() throws GenericTransactionException {
        UserTransaction ut = TransactionFactory.getUserTransaction();

        if (ut != null) {
            try {
                int status = ut.getStatus();
                Debug.logVerbose("[TransactionUtil.commit] current status : " + getTransactionStateString(status), module);
                
                if (status != STATUS_NO_TRANSACTION) {
                    ut.commit();
                    Debug.logVerbose("[TransactionUtil.commit] transaction committed", module);
                    
                    // clear out the stamps to keep it clean
                    clearTransactionStamps();
                } else {
                    Debug.logInfo("[TransactionUtil.commit] Not committing transaction, status is STATUS_NO_TRANSACTION", module);
                }
            } catch (RollbackException e) {
                if (Debug.infoOn()) Thread.dumpStack();
                //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
                throw new GenericTransactionException("Roll back error, could not commit transaction, was rolled back instead", e);
            } catch (HeuristicMixedException e) {
                //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
                throw new GenericTransactionException("Could not commit transaction, HeuristicMixed exception", e);
            } catch (HeuristicRollbackException e) {
                //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
                throw new GenericTransactionException("Could not commit transaction, HeuristicRollback exception", e);
            } catch (SystemException e) {
                //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
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
                Debug.logVerbose("[TransactionUtil.rollback] current status : " + getTransactionStateString(status), module);

                if (status != STATUS_NO_TRANSACTION) {
                    //if (Debug.infoOn()) Thread.dumpStack();
                    if (Debug.infoOn()) {
                        Exception newE = new Exception("Stack Trace");
                        Debug.logError(newE, "[TransactionUtil.rollback]", module);
                    }
                    ut.rollback();
                    Debug.logInfo("[TransactionUtil.rollback] transaction rolled back", module);
                    
                    // clear out the stamps to keep it clean
                    clearTransactionStamps();
                } else {
                    Debug.logInfo("[TransactionUtil.rollback] transaction not rolled back, status is STATUS_NO_TRANSACTION", module);
                }
            } catch (SystemException e) {
                //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
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
                Debug.logVerbose("[TransactionUtil.setRollbackOnly] current code : " + getTransactionStateString(status), module);

                if (status != STATUS_NO_TRANSACTION) {
                    if (Debug.infoOn()) Thread.dumpStack();
                    ut.setRollbackOnly();
                    Debug.logInfo("[TransactionUtil.setRollbackOnly] transaction roll back only set", module);
                } else {
                    Debug.logInfo("[TransactionUtil.setRollbackOnly] transaction roll back only not set, status is STATUS_NO_TRANSACTION", module);
                }
            } catch (SystemException e) {
                //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
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
        if (xacon == null) {
            return null;
        }
        try {
            XAResource resource = xacon.getXAResource();
            TransactionUtil.enlistResource(resource);
            return xacon.getConnection();
        } catch (SQLException e) {
            throw new GenericTransactionException("SQL error, could not enlist connection in transaction even though transactions are available", e);
        }
    }

    public static void enlistResource(XAResource resource) throws GenericTransactionException {
        if (resource == null) {
            return;
        }

        try {
            TransactionManager tm = TransactionFactory.getTransactionManager();
            if (tm != null && tm.getStatus() == STATUS_ACTIVE) {
                Transaction tx = tm.getTransaction();
                if (tx != null) {
                     tx.enlistResource(resource);
                }
            }
        } catch (RollbackException e) {
            //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
            throw new GenericTransactionException("Roll Back error, could not enlist resource in transaction even though transactions are available, current transaction rolled back", e);
        } catch (SystemException e) {
            //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
            throw new GenericTransactionException("System error, could not enlist resource in transaction even though transactions are available", e);
        }
    }

    public static String getTransactionStateString(int state) {
        switch (state) {
            case Status.STATUS_ACTIVE:
                return "Transaction Active (" + state + ")";
            case Status.STATUS_COMMITTED:
                return "Transaction Committed (" + state + ")";
            case Status.STATUS_COMMITTING:
                return "Transaction Committing (" + state + ")";
            case Status.STATUS_MARKED_ROLLBACK:
                return "Transaction Marked Rollback (" + state + ")";
            case Status.STATUS_NO_TRANSACTION:
                return "No Transaction (" + state + ")";
            case Status.STATUS_PREPARED:
                return "Transaction Prepared (" + state + ")";
            case Status.STATUS_PREPARING:
                return "Transaction Preparing (" + state + ")";
            case Status.STATUS_ROLLEDBACK:
                return "Transaction Rolledback (" + state + ")";
            case Status.STATUS_ROLLING_BACK:
                return "Transaction Rolling Back (" + state + ")";
            case Status.STATUS_UNKNOWN:
                return "Transaction Status Unknown (" + state + ")";
            default:
                return "Not a valid state code (" + state + ")";
        }
    }

    public static void logRunningTx() {
        if (debugResources) {
            if (debugResMap != null && debugResMap.size() > 0) {
                Iterator i = debugResMap.keySet().iterator();
                while (i.hasNext()) {
                    Object o = i.next();
                    DebugXaResource dxa = (DebugXaResource) debugResMap.get(o);
                    dxa.log();
                }
            }
        }
    }

    public static void registerSynchronization(Synchronization sync) throws GenericTransactionException {
        if (sync == null) {
            return;
        }

        try {
            TransactionManager tm = TransactionFactory.getTransactionManager();
            if (tm != null && tm.getStatus() == STATUS_ACTIVE) {
                Transaction tx = tm.getTransaction();
                if (tx != null) {
                    tx.registerSynchronization(sync);
                }
            }
        } catch (RollbackException e) {
            //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
            throw new GenericTransactionException("Roll Back error, could not register synchronization in transaction even though transactions are available, current transaction rolled back", e);
        } catch (SystemException e) {
            //This is Java 1.4 only, but useful for certain debuggins: Throwable t = e.getCause() == null ? e : e.getCause();
            throw new GenericTransactionException("System error, could not register synchronization in transaction even though transactions are available", e);
        }
    }
    
    private static ThreadLocal transactionStartStamp = new ThreadLocal();
    private static ThreadLocal transactionLastNowStamp = new ThreadLocal();
    
    public static Timestamp getTransactionStartStamp() {
        Timestamp curStamp = (Timestamp) transactionStartStamp.get();
        if (curStamp == null) {
            curStamp = UtilDateTime.nowTimestamp();
            transactionStartStamp.set(curStamp);
            
            // we know this is the first time set for this transaction, so make sure the StampClearSync is registered
            try {
                registerSynchronization(new StampClearSync());
            } catch (GenericTransactionException e) {
                Debug.logError(e, "Error registering StampClearSync synchronization, stamps will still be reset if begin/commit/rollback are call through TransactionUtil, but not if otherwise", module);
            }
        }
        return curStamp;
    }

    public static Timestamp getTransactionUniqueNowStamp() {
        Timestamp lastNowStamp = (Timestamp) transactionLastNowStamp.get();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        
        // check for an overlap with the lastNowStamp, or if the lastNowStamp is in the future because of incrementing to make each stamp unique
        if (lastNowStamp != null && (lastNowStamp.equals(nowTimestamp) || lastNowStamp.after(nowTimestamp))) {
            nowTimestamp = new Timestamp(lastNowStamp.getTime() + 1);
        }
        
        transactionLastNowStamp.set(nowTimestamp);
        return nowTimestamp;
    }

    protected static void clearTransactionStamps() {
        transactionStartStamp.set(null);
        transactionLastNowStamp.set(null);
    }
    
    public static class StampClearSync implements Synchronization {
        public void afterCompletion(int status) {
            TransactionUtil.clearTransactionStamps();
        }
        
        public void beforeCompletion() {            
        }
    }
}
