package org.ofbiz.core.entity;

import java.sql.*;
import javax.sql.*;
import java.util.*;
import javax.transaction.*;
import javax.transaction.xa.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Transaction Utility to help with some common transaction tasks
 * <p><b>Description:</b> Provides a wrapper around the transaction objects to allow for changes in underlying implementations in the future.
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
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @created    Dec 12 2001
 * @version    1.0
 */
public class TransactionUtil implements javax.transaction.Status {
    /** Begins a transaction in the current thread IF transactions are available */
    public static void begin() throws GenericTransactionException  {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null) {
            try {
                ut.begin();
            } catch (NotSupportedException e) {
                throw new GenericTransactionException("Not Supported error, could not begin transaction (probably a nesting problem)", e);
            } catch (SystemException e) {
                throw new GenericTransactionException("System error, could not begin transaction", e);
            }
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
        }  else {
            return STATUS_NO_TRANSACTION;
        }
    }
    
    /** Commits the transaction in the current thread IF transactions are available */
    public static void commit() throws GenericTransactionException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null) {
            try {
                ut.commit();
            } catch (RollbackException e) {
                throw new GenericTransactionException("Roll back error, could not commit transaction, was rolled back instead", e);
            } catch (HeuristicMixedException e) {
                throw new GenericTransactionException("Could not commit transaction", e);
            } catch (HeuristicRollbackException e) {
                throw new GenericTransactionException("Could not commit transaction", e);
            } catch (SystemException e) {
                throw new GenericTransactionException("System error, could not commit transaction", e);
            }
        }
    }
    
    /** Rolls back transaction in the current thread IF transactions are available */
    public static void rollback() throws GenericTransactionException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null) {
            try {
                ut.rollback();
            } catch (SystemException e) {
                throw new GenericTransactionException("System error, could not roll back transaction", e);
            }
        }
    }
    
    /** Makes a roll back the only possible outcome of the transaction in the current thread IF transactions are available */
    public static void setRollbackOnly() throws GenericTransactionException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null) {
            try {
                ut.setRollbackOnly();
            } catch (SystemException e) {
                throw new GenericTransactionException("System error, could not set roll back only on transaction", e);
            }
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
            TransactionManager tm = TransactionFactory.getTransactionManager();
            if (tm != null && tm.getStatus() == STATUS_ACTIVE) {
                Transaction tx = tm.getTransaction();
                if (tx != null) {
                    XAResource resource = xacon.getXAResource();
                    tx.enlistResource(resource);
                }
            }
            return xacon.getConnection();
        } catch (SQLException e) {
            throw new GenericTransactionException("SQL error, could not enlist connection in transaction even though transactions are available", e);
        } catch (RollbackException e) {
            throw new GenericTransactionException("Roll Back error, could not enlist connection in transaction even though transactions are available, current transaction rolled back", e);
        } catch (SystemException e) {
            throw new GenericTransactionException("System error, could not enlist connection in transaction even though transactions are available", e);
        }
    }
}
