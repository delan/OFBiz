/*
 * Licensed under the X license (see http://www.x.org/terms.htm)
 */
package org.ofbiz.minerva.pool.jdbc.xa;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

import org.apache.log4j.Logger;
import org.ofbiz.minerva.pool.ObjectPool;
import org.ofbiz.minerva.pool.PoolObjectFactory;
import org.ofbiz.minerva.pool.jdbc.xa.wrapper.TransactionListener;
import org.ofbiz.minerva.pool.jdbc.xa.wrapper.XAConnectionImpl;
import org.ofbiz.minerva.pool.jdbc.xa.wrapper.XADataSourceImpl;
import org.ofbiz.minerva.pool.jdbc.xa.wrapper.XAResourceImpl;

/**
 * Object factory for JDBC 2.0 standard extension XAConnections.  You pool the
 * XAConnections instead of the java.sql.Connections since with vendor
 * conformant drivers, you don't have direct access to the java.sql.Connection,
 * and any work done isn't associated with the java.sql.Connection anyway.
 * <P><B>Note:</B> This implementation requires that the TransactionManager
 * be bound to a JNDI name.</P>
 * <P><B>Note:</B> This implementation has special handling for Minerva JDBC
 * 1/2 XA Wrappers.  Namely, when a request comes in, if it is for a wrapper
 * connection and it has the same current transaction as a previous active
 * connection, the same previous connection will be returned.  Otherwise,
 * you won't be able to share changes across connections like you can with
 * the native JDBC 2 Standard Extension implementations.</P>
 *
 * @author Aaron Mulder (ammulder@alumni.princeton.edu)
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 *
 * REVISIONS:
 * 20010703 bill added code for transaction isolation
 @version $Revision: 1.2 $
 */
public class XAConnectionFactory extends PoolObjectFactory {

    public static final int DEFAULT_ISOLATION = -1;

    private InitialContext ctx;
    private XADataSource source;
    private String userName;
    private String password;
    private int psCacheSize = 10;
    private boolean releaseOnCommit = false;
    private boolean saveStackTrace = false;
    private int transactionIsolation = DEFAULT_ISOLATION;
    private ConnectionEventListener listener, errorListener;
    private TransactionListener transListener;
    private ObjectPool pool;
    private final Map wrapperTx = Collections.synchronizedMap(new HashMap());
    private final Map rms = Collections.synchronizedMap(new HashMap());
    private TransactionManager tm;

    private static Logger log = Logger.getLogger(XAConnectionFactory.class);

    /**
     * Creates a new factory.  You must set the XADataSource and
     * TransactionManager JNDI name before the factory can be used.
     */
    public XAConnectionFactory() throws NamingException {
        ctx = new InitialContext();
        //wrapperTx = new HashMap();
        //rms = new HashMap();
        errorListener = new ConnectionEventListener() {
            public void connectionErrorOccurred(ConnectionEvent evt) {
                if (pool.isInvalidateOnError()) {
                    pool.markObjectAsInvalid(evt.getSource());
                }
            }

            public void connectionClosed(ConnectionEvent evt) {
            }
        };

        listener = new ConnectionEventListener() {

            public void connectionErrorOccurred(ConnectionEvent evt) {
                if (pool.isInvalidateOnError()) {
                    pool.markObjectAsInvalid(evt.getSource());
                }
//                closeConnection(evt, XAResource.TMFAIL);
            }

            public void connectionClosed(ConnectionEvent evt) {
                closeConnection(evt, XAResource.TMSUCCESS);
            }

            private void closeConnection(ConnectionEvent evt, int status) {
                XAConnection con = (XAConnection) evt.getSource();
                try {
                    con.removeConnectionEventListener(listener);
                } catch (IllegalArgumentException e) {
                    return; // Removed twice somehow?
                }
                Transaction trans = null;
                try {
                    if (tm.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        trans = tm.getTransaction();
                        XAResource res = (XAResource) rms.remove(con);
                        if (res != null) {
                            trans.delistResource(res, status);
                        } // end of if ()
                        else {
                            log.warn("no xares in rms for con " + con);
                        } // end of else
                    }
                } catch (Exception e) {
                    log.error("Unable to deregister with TransactionManager", e);
                    throw new RuntimeException("Unable to deregister with TransactionManager: " + e);
                }

                if (!(con instanceof XAConnectionImpl)) {
                    // Real XAConnection -> not associated w/ transaction
                    pool.releaseObject(con);
                } else {
                    XAConnectionImpl xaCon = (XAConnectionImpl) con;
                    if (!((XAResourceImpl) xaCon.getXAResource()).isTransaction()) {
                        // Wrapper - we can only release it if there's no current transaction
                        // Can't just check TM because con may have been committed but left open
                        //   so if there's a current transaction it may not apply to the con.
                        log.warn("XAConnectionImpl: " + xaCon + " has no current tx!");
                        try {
                            xaCon.rollback();
                        } catch (SQLException e) {
                            pool.markObjectAsInvalid(con);
                        }
                        pool.releaseObject(con);
                    } else {
                        // Still track errors, but don't try to close again.
                        con.addConnectionEventListener(errorListener);
                    }
                }
            }
        };
        transListener = new TransactionListener() {
            public void transactionFinished(XAConnectionImpl con) {
                con.clearTransactionListener();
                Object tx = wrapperTx.remove(con);
                //System.out.println("removing con: " + con + "from wrapperTx, tx: " + tx);
                if (tx != null)
                    wrapperTx.remove(tx);
                try {
                    con.removeConnectionEventListener(errorListener);
                } catch (IllegalArgumentException e) {
                    // connection was not closed, but transaction ended
                    if (!releaseOnCommit) {
                        return;
                    } else {
                        rms.remove(con);
                        pool.markObjectAsInvalid(con);
                        con.forceClientConnectionsClose();
                    }
                }

                pool.releaseObject(con);
            }

            public void transactionFailed(XAConnectionImpl con) {
                con.clearTransactionListener();
                Object tx = wrapperTx.remove(con);
                if (tx != null)
                    wrapperTx.remove(tx);
                //System.out.println("removing con: " + con + "from wrapperTx, tx: " + tx);
                pool.markObjectAsInvalid(con);
                try {
                    con.removeConnectionEventListener(errorListener);
                } catch (IllegalArgumentException e) {
                    if (!releaseOnCommit) {
                        return;
                    } else {
                        rms.remove(con);
                        con.forceClientConnectionsClose();
                    }
                }
                pool.releaseObject(con);
            }
        };
    }

    /**
     * Sets the user name used to generate XAConnections.  This is optional,
     * and will only be used if present.
     */
    public void setUser(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the user name used to generate XAConnections.
     */
    public String getUser() {
        return userName;
    }

    /**
     * Sets the password used to generate XAConnections.  This is optional,
     * and will only be used if present.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the password used to generate XAConnections.
     */
    public String getPassword() {
        return password;
    }

    public boolean getReleaseOnCommit() {
        return releaseOnCommit;
    }

    public void setReleaseOnCommit(boolean rel) {
        releaseOnCommit = rel;
    }

    /**
     * Sets the number of PreparedStatements to be cached for each
     * Connection.  Your DB product may impose a limit on the number
     * of open PreparedStatements.  The default value is 10.
     */
    public void setPSCacheSize(int size) {
        psCacheSize = size;
    }

    /**
     * Gets the number of PreparedStatements to be cached for each
     * Connection.  The default value is 10.
     */
    public int getPSCacheSize() {
        return psCacheSize;
    }


    /**
     * Gets the transaction isolation level of connections.  This defaults to
     * whatever the connection's default iso level is.
     */
    public int getTransactionIsolation() {
        return transactionIsolation;
    }

    public void setTransactionIsolation(int iso) {
        this.transactionIsolation = iso;
    }

    public void setTransactionIsolation(String iso) {
        if (iso.equals("TRANSACTION_NONE")) {
            this.transactionIsolation = Connection.TRANSACTION_NONE;
        } else if (iso.equals("TRANSACTION_READ_COMMITTED")) {
            this.transactionIsolation = Connection.TRANSACTION_READ_COMMITTED;
        } else if (iso.equals("TRANSACTION_READ_UNCOMMITTED")) {
            this.transactionIsolation = Connection.TRANSACTION_READ_UNCOMMITTED;
        } else if (iso.equals("TRANSACTION_REPEATABLE_READ")) {
            this.transactionIsolation = Connection.TRANSACTION_REPEATABLE_READ;
        } else if (iso.equals("TRANSACTION_SERIALIZABLE")) {
            this.transactionIsolation = Connection.TRANSACTION_SERIALIZABLE;
        } else {
            throw new IllegalArgumentException("Setting Isolation level to unknown state: " + iso);
        }
    }

    /**
     * Sets the XADataSource used to generate XAConnections.  This may be
     * supplied by the vendor, or it may use the wrappers for non-compliant
     * drivers (see XADataSourceImpl).
     * @see org.ofbiz.minerva.pool.jdbc.xa.wrapper.XADataSourceImpl
     */
    public void setDataSource(XADataSource dataSource) {
        source = dataSource;
    }

    /**
     * Gets the XADataSource used to generate XAConnections.
     */
    public XADataSource getDataSource() {
        return source;
    }

    /**
     * Sets the TransactionManager.
     */
    public void setTransactionManager(TransactionManager tm) {
        this.tm = tm;
    }

    /**
     * Gets the TransactionManager.
     */
    public TransactionManager getTransactionManager() {
        return this.tm;
    }


    /**
     * Have XAClientConnections save a stack trace on creation
     * This is useful for debugging non-closed connections.
     * It must be used with ReleaseOnCommit option
     */
    public boolean getSaveStackTrace() {
        return saveStackTrace;
    }

    public void setSaveStackTrace(boolean save) {
        saveStackTrace = save;
    }

    /**
     * Verifies that the data source and transaction manager are accessible.
     */
    public void poolStarted(ObjectPool pool) {
        if (log.isDebugEnabled())
            log.debug("Starting");

        super.poolStarted(pool);
        this.pool = pool;
        if (source == null)
            throw new IllegalStateException("Must specify XADataSource to " + getClass().getName());
        if (source instanceof XADataSourceImpl) {
            ((XADataSourceImpl) source).setSaveStackTrace(saveStackTrace);
        }

        /*
        if(tmJndiName == null)
            throw new IllegalStateException("Must specify TransactionManager JNDI Name to "+getClass().getName());
        if(ctx == null)
            throw new IllegalStateException("Must specify InitialContext to "+getClass().getName());
        try {
            tm = (TransactionManager)ctx.lookup(tmJndiName);
        } catch(NamingException e) {
            throw new IllegalStateException("Cannot lookup TransactionManager using specified context and name!");
        }
        */
    }

    /**
     * Creates a new XAConnection from the provided XADataSource.
     */
    public Object createObject(Object parameters) throws Exception {

        log.debug("Opening new XAConnection");

        Object obj = null;
        try {
            if (parameters != null) {
                String credentials[] = (String[]) parameters;
                if (credentials.length == 2)
                    obj = source.getXAConnection(credentials[0], credentials[1]);
            } else if (userName != null && userName.length() > 0)
                obj = source.getXAConnection(userName, password);
            else
                obj = source.getXAConnection();
        } catch (SQLException e) {
            log.error("Can't get an XAConnection", e);
            throw e;
        }
        return obj;
    }

    /**
     * Registers the XAConnection's XAResource with the current transaction (if
     * there is one).  Sets listeners that will handle deregistering and
     * returning the XAConnection to the pool via callbacks.
     */
    public Object prepareObject(Object pooledObject) {
        boolean trace = log.isDebugEnabled();
        XAConnection con = (XAConnection) pooledObject;
        con.addConnectionEventListener(listener);
        Transaction trans = null;
        try {
            if (tm.getStatus() != Status.STATUS_NO_TRANSACTION) {
                trans = tm.getTransaction();
                XAResource res = con.getXAResource();
                rms.put(con, res);
                trans.enlistResource(res);
                if (trace)
                    log.debug("Resource '" + res + "' enlisted for '" + con + "'.");
            } else {
                if (trace)
                    log.debug("No transaction right now.");
            }
        } catch (Exception e) {
            //System.out.println("error in prepareObject!!!!!");
            e.printStackTrace();
            log.error("Unable to register with TransactionManager", e);
            con.removeConnectionEventListener(listener);
            throw new RuntimeException("Unable to register with TransactionManager: " + e);
        }

        if (con instanceof XAConnectionImpl) {
            ((XAConnectionImpl) con).setTransactionListener(transListener);
            ((XAConnectionImpl) con).setPSCacheSize(psCacheSize);
            if (transactionIsolation != DEFAULT_ISOLATION) {
                try {
                    ((XAConnectionImpl) con).setTransactionIsolation(transactionIsolation);
                } catch (SQLException sex) {
                    throw new RuntimeException("Unable to setTransactionIsolation: " + sex.getMessage());
                }
            }

            if (trans != null) {
                //System.out.println("inserting con: " + con + "into wrapperTx, tx: " + trans);
                wrapperTx.put(con, trans); // For JDBC 1/2 wrappers, remember which
                wrapperTx.put(trans, con); // connection goes with a given transaction
            }
        }
        return con;
    }

    /**
     * Closes a connection.
     */
    public void deleteObject(Object pooledObject) {
        XAConnection con = (XAConnection) pooledObject;
        try {
            con.close();
        } catch (SQLException e) {
        }
    }

    /**
     * If a new object is requested and it is a JDBC 1/2 wrapper connection
     * in the same Transaction as an existing connection, return that same
     * connection.
     */
    public Object isUniqueRequest() {
        try {
            if (tm.getStatus() != Status.STATUS_NO_TRANSACTION) {
                Transaction trans = tm.getTransaction();
                //System.out.println("isUniqueRequest returning conn: " + wrapperTx.get(trans) + "  attached to tx: " + trans);
                return wrapperTx.get(trans);
            }
        } catch (Exception e) {
        }
        return null;
    }

    /** For XAConnectionImpl check that parameters = String[2]{username, password}
     and that these match the the source connection user and password. Return
     true for non-XAConnectionImpl sources
     */
    public boolean checkValidObject(Object source, Object parameters) {
        boolean validObject = true;
        if (parameters != null && source instanceof XAConnectionImpl) {
            XAConnectionImpl con = (XAConnectionImpl) source;
            String credentials[] = (String[]) parameters;
            if (credentials.length == 2) {
                String user = con.getUser();
                String password = con.getPassword();
                boolean validUser = ((user == null) && (credentials[0] == null)) || ((user != null) && user.equals(credentials[0]));
                boolean validPassword = ((password == null) && (credentials[1] == null)) || ((password != null) && password.equals(credentials[1]));
                validObject = validUser && validPassword;
            }
        }
        return validObject;
    }

}
