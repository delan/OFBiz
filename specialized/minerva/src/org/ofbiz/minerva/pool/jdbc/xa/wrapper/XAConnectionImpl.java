/*
 * Licensed under the X license (see http://www.x.org/terms.htm)
 */
package org.ofbiz.minerva.pool.jdbc.xa.wrapper;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.ofbiz.minerva.pool.PoolEvent;
import org.ofbiz.minerva.pool.PoolEventListener;
import org.ofbiz.minerva.pool.PooledObject;
import org.ofbiz.minerva.pool.cache.ObjectCache;
import org.ofbiz.minerva.pool.jdbc.ConnectionInPool;

/**
 * A transaction wrapper around a java.sql.Connection.  This provides access to
 * an XAResource (there is a one-to-one mapping between XAResource and
 * XAConnection) and a java.sql.Connection (in this implementation, there is
 * also a one-to-one mapping between XAConnection and java.sql.Connection).
 * In order to pool java.sql.Connections in a transactional environment, this
 * is the class that should be pooled - though you could pool the connections,
 * there is no need to create and destroy these wrappers so frequently.
 *
 * <P>Note that there con only be one transaction at a time accessing one of
 * these wrappers, and requests to a pool for multiple connections on behalf of
 * one transaction should use the same wrapper.  This is because there is no
 * distinction between connections and transactions in a java.sql.Connection,
 * and work done by one connection on behalf of a transaction would not be
 * visible to another connection working on behalf of the same transaction - you
 * would have effectively created two transactions.</P>
 *
 * <P>This also implies that an XAConnection should not be released to a
 * connection pool until the work has been committed or rolled back.  However,
 * it must sent the close notification as usual in order to be delisted from
 * the transaction.  So the ConnectionEventListener must not release the
 * XAConnection to a pool when it receives the close event.  Instead, it should
 * also register a TransactionListener that will be notified when the
 * Transaction is finished, and release the XAConnection at that time.</P>
 * @see org.ofbiz.minerva.pool.jdbc.xa.wrapper.TransactionListener
 *
 * @author Aaron Mulder (ammulder@alumni.princeton.edu)
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 *
 * REVISIONS:
 * 20010703 bill added code for transaction isolation
 */
public class XAConnectionImpl implements XAConnection, PooledObject {

    private final static String CLOSED = "Connection has been closed!";
    private Connection con;
    private XAResourceImpl resource;
    private Vector listeners, poolListeners;
    private ArrayList clientConnections;
    private TransactionListener transListener;
    private int preparedStatementCacheSize = 0;
    private int clientConnectionCount = 0;
    /** The JDBC user name used to open an underlying connection */
    private String user;
    /** The JDBC password used to open an underlying connection */
    private String password;
    private boolean saveStackTrace;

    /**
     * Creates a new transactional wrapper.
     * @param con The underlying non-transactional Connection.
     * @param resource The transaction resource used to enlist this
     *    connection in a transaction.
     */
    public XAConnectionImpl(Connection con, XAResourceImpl resource, boolean saveStackTrace) {
        this.con = con;
        this.resource = resource;
        listeners = new Vector();
        poolListeners = new Vector();
        clientConnections = new ArrayList();
        this.saveStackTrace = saveStackTrace;
    }

    /**
     * Sets the transaction listener.
     */
    public void setTransactionListener(TransactionListener tl) {
        transListener = tl;
    }

    /**
     * Clears the transaction listener.
     */
    public void clearTransactionListener() {
        transListener = null;
    }

    /**
     * Sets the number of PreparedStatements to be cached for each
     * Connection.  Your DB product may impose a limit on the number
     * of open PreparedStatements.
     */
    public void setPSCacheSize(int maxSize) {
        preparedStatementCacheSize = maxSize;
    }

    /**
     * Gets the number of PreparedStatements to be cached for each
     * Connection.
     */
    public int getPSCacheSize() {
        return preparedStatementCacheSize;
    }


    public void setTransactionIsolation(int iso) throws SQLException {
        con.setTransactionIsolation(iso);
    }

    /**
     * Shuts down this wrapper (and the underlying Connection) permanently.
     */
    public void close() {
        try {
            con.close();
        } catch (SQLException e) {
        }
        ObjectCache cache = (ObjectCache) ConnectionInPool.psCaches.remove(con);
        if (cache != null)
            cache.close();
        con = null;
        resource = null;
        listeners.clear();
        listeners = null;
    }

    /**
     * Indicates that the connection given to the client has been closed.
     * If there is currently a transaction, this object should not be closed or
     * returned to a pool.  If not, it can be closed or returned immediately.
     */
    public void clientConnectionClosed(XAClientConnection clientCon) {
        synchronized(clientConnections) {
            clientConnections.remove(clientCon);
        }
        if (clientConnections.size() > 0)
            return;  // Only take action if the last connection referring to this is closed

        boolean trans = resource.isTransaction(); // could be committed directly on notification?  Seems unlikely, but let's not rule it out.
        Vector local = (Vector) listeners.clone();
        for (int i = local.size() - 1; i >= 0; i--)
            ((ConnectionEventListener) local.elementAt(i)).connectionClosed(new ConnectionEvent(this));
//        if(!trans)
//            transactionFinished();
    }

    /**
     * Indicates that the outstanding transaction has finished and this object
     * can be closed or returned to a pool.  This dispatches a close event to
     * all listeners.
     * @see #addConnectionEventListener
     */
    public void transactionFinished() {
        if (transListener != null)
            transListener.transactionFinished(this);
    }

    /**
     * Indicates that the outstanding transaction has finished with a fatal
     * error, and this object should be closed or permanently removed from a
     * pool.  This dispatches a close event to all listeners.
     * @see #addConnectionEventListener
     */
    public void transactionFailed() {
        if (transListener != null)
            transListener.transactionFailed(this);
    }

    /**
     * Indicates that the connection given to the client has had an error.
     * If there is currently a transaction, this object should not be closed or
     * returned to a pool.  If not, it can be closed or returned immediately.
     */
    public void setConnectionError(SQLException e) {
        Vector local = (Vector) listeners.clone();
        for (int i = local.size() - 1; i >= 0; i--) {
            try {
                ((ConnectionEventListener) local.elementAt(i)).connectionErrorOccurred(new ConnectionEvent(this, e));
            } catch (RuntimeException ex) {
                // there can be thrown an induced exception,
                // but we must report to client the original one, right?
                ex.printStackTrace();
            }
        }
    }

    /**
     * Rolls back the underlying connection.  This is used when there is no
     * current transaction and the connection is returned to the pool - since
     * no transaction will be committed or rolled back but this connection
     * will be reused, we must roll it back. This is only done if autocommit is
     * false.
     */
    public void rollback() throws SQLException {
        if (con.getAutoCommit() == false)
            con.rollback();
    }

    // ---- Implementation of javax.sql.XAConnection ----

    public XAResource getXAResource() {
        return resource;
    }

    public void addConnectionEventListener(ConnectionEventListener listener) {
        listeners.addElement(listener);
    }

    public void removeConnectionEventListener(ConnectionEventListener listener) {
        if (!listeners.remove(listener))
            throw new IllegalArgumentException();
    }

    public Connection getConnection() {
        XAClientConnection xaCon;
        synchronized (clientConnections) {
            xaCon = new XAClientConnection(this, con, saveStackTrace);
            xaCon.setPSCacheSize(preparedStatementCacheSize);
            clientConnections.add(xaCon);
        }
        return xaCon;
    }

    // ---- Implementation of javax.sql.XAConnection ----

    public void addPoolEventListener(PoolEventListener listener) {
        poolListeners.addElement(listener);
    }

    public void removePoolEventListener(PoolEventListener listener) {
        poolListeners.removeElement(listener);
    }

    /**
     * Dispatches an event to the pool event listeners.
     */
    void firePoolEvent(PoolEvent evt) {
        Vector local = (Vector) poolListeners.clone();
        for (int i = local.size() - 1; i >= 0; i--)
            if (evt.getType() == PoolEvent.OBJECT_CLOSED)
                ((PoolEventListener) local.elementAt(i)).objectClosed(evt);
            else if (evt.getType() == PoolEvent.OBJECT_ERROR)
                ((PoolEventListener) local.elementAt(i)).objectError(evt);
            else
                ((PoolEventListener) local.elementAt(i)).objectUsed(evt);
    }

    /** Getter for property password.
     * @return Value of property password.
     */
    public java.lang.String getPassword() {
        return password;
    }

    /** Setter for property password.
     * @param password New value of property password.
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }

    /** Getter for property user.
     * @return Value of property user.
     */
    public java.lang.String getUser() {
        return user;
    }

    /** Setter for property user.
     * @param user New value of property user.
     */
    public void setUser(java.lang.String user) {
        this.user = user;
    }

    public void forceClientConnectionsClose() {
        for (int i = 0; i < clientConnections.size(); i++) {
            XAClientConnection client = (XAClientConnection) clientConnections.get(i);
            try {
                client.forcedClose();
            } catch (SQLException ignored) {
            }
        }
        clientConnections.clear();
    }
}
