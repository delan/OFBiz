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
package org.ofbiz.core.entity.transaction;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import org.w3c.dom.Element;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.ObjectPool;

/**
 * DBCP ConnectionFactory - central source for JDBC connections from DBCP
 *
 * This is currently non transactional as DBCP doesn't seem to support transactional datasources yet (DBCP 1.0).
 *
 * @author <a href="mailto:mike@atlassian.com">Mike Cannon-Brookes</a>
 * @version 1.0
 * Created on Dec 18, 2001, 5:03 PM
 */
public class DBCPConnectionFactory {

    protected static Map dsCache = new HashMap();

    public static Connection getConnection(String helperName, Element dbcpJdbcElement) throws SQLException, GenericEntityException {
        // the PooledDataSource implementation
        DataSource dataSource = (DataSource)dsCache.get(helperName);

        if (dataSource != null) {
            return dataSource.getConnection();
        }

        try
        {
            synchronized (DBCPConnectionFactory.class) {
                //try again inside the synch just in case someone when through while we were waiting
                dataSource = (DataSource)dsCache.get(helperName);
                if (dataSource != null) {
                    return dataSource.getConnection();
                }

                // First, we'll need a ObjectPool that serves as the actual pool of connections.
                ObjectPool connectionPool = new GenericObjectPool(null);

                // Next, we'll create a ConnectionFactory that the pool will use to create Connections.
                String connectURI = dbcpJdbcElement.getAttribute("jdbc-uri");

                // String driver = dbcpJdbcElement.getAttribute("jdbc-driver");
                String username = dbcpJdbcElement.getAttribute("jdbc-username");
                String password = dbcpJdbcElement.getAttribute("jdbc-password");

                String driverClassName = dbcpJdbcElement.getAttribute("jdbc-driver");
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                loader.loadClass(driverClassName);
                org.apache.commons.dbcp.ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI,username,password);

                // Now we'll create the PoolableConnectionFactory, which wraps
                // the "real" Connections created by the ConnectionFactory with
                // the classes that implement the pooling functionality.
                //PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);

                // Finally, we create the PoolingDriver itself,
                // passing in the object pool we created.
                dataSource = new PoolingDataSource(connectionPool);

                dataSource.setLogWriter(Debug.getPrintWriter());

                dsCache.put(helperName, dataSource);

                return dataSource.getConnection();
            }
        } catch (Exception e) {
            String errorMsg = "Error getting datasource via DBCP.";
            Debug.logError(e, errorMsg, module);
        }

        return null;
    }
}
