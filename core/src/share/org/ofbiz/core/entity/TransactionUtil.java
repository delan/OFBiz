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
    public static void begin() throws NotSupportedException, SystemException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null)
            ut.begin();
    }
    
    /** Gets the status of the transaction in the current thread IF 
    * transactions are available, otherwise returns STATUS_NO_TRANSACTION */
    public static int getStatus() throws SystemException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null)
            return ut.getStatus();
        else
            return STATUS_NO_TRANSACTION;
    }
    
    /** Commits the transaction in the current thread IF transactions are available */
    public static void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null)
            ut.commit();
    }
    
    /** Rolls back transaction in the current thread IF transactions are available */
    public static void rollback() throws SystemException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null)
            ut.rollback();
    }
    
    /** Makes a roll back the only possible outcome of the transaction in the current thread IF transactions are available */
    public static void setRollbackOnly() throws SystemException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null)
            ut.setRollbackOnly();
    }
    
    /** Sets the timeout of the transaction in the current thread IF transactions are available */
    public static void setTransactionTimeout(int seconds) throws SystemException {
        UserTransaction ut = TransactionFactory.getUserTransaction();
        if (ut != null)
            ut.setTransactionTimeout(seconds);
    }

    /** Enlists the given Connection if it is an XAConnection and if a transaction is active in the current thread */
    public static void enlistConnection(Connection connection) throws SQLException, RollbackException, SystemException {
        if (connection instanceof XAConnection) {
            XAConnection xAConnection = (XAConnection) connection;
            
            TransactionManager tm = TransactionFactory.getTransactionManager();
            if (tm != null && tm.getStatus() == STATUS_ACTIVE) {
                Transaction tx = tm.getTransaction();
                if (tx != null) {
                    XAResource resource = xAConnection.getXAResource();
                    tx.enlistResource(resource);
                }
            }
        }
    }
}
