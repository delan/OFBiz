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


import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import org.w3c.dom.Element;

import org.ofbiz.core.entity.config.*;
import org.ofbiz.core.entity.transaction.*;
import org.ofbiz.core.config.*;
import org.ofbiz.core.util.*;


/**
 * ConnectionFactory - central source for JDBC connections
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * Created on July 1, 2001, 5:03 PM
 */
public class ConnectionFactory {
    // Debug module name
    public static final String module = ConnectionFactory.class.getName();

    // protected static UtilCache dsCache = new UtilCache("entity.JndiDataSources", 0, 0);
    protected static Map dsCache = new HashMap();

    public static Connection getConnection(String helperName) throws SQLException, GenericEntityException {
        // Debug.logVerbose("Getting a connection", module);

        EntityConfigUtil.DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperName);

        switch (datasourceInfo.datasourceType) {
        case EntityConfigUtil.DatasourceInfo.TYPE_JNDI_JDBC:
            Element jndiJdbcElement = datasourceInfo.datasourceTypeElement;
            String jndiName = jndiJdbcElement.getAttribute("jndi-name");
            String jndiServerName = jndiJdbcElement.getAttribute("jndi-server-name");

            // if (Debug.verboseOn()) Debug.logVerbose("Trying JNDI name " + jndiName, module);
            Object ds;

            ds = dsCache.get(jndiName);
            if (ds != null) {
                if (ds instanceof XADataSource) {
                    XADataSource xads = (XADataSource) ds;

                    return TransactionUtil.enlistConnection(xads.getXAConnection());
                } else {
                    DataSource nds = (DataSource) ds;

                    return nds.getConnection();
                }
            }

            synchronized (ConnectionFactory.class) {
                // try again inside the synch just in case someone when through while we were waiting
                ds = dsCache.get(jndiName);
                if (ds != null) {
                    if (ds instanceof XADataSource) {
                        XADataSource xads = (XADataSource) ds;

                        return TransactionUtil.enlistConnection(xads.getXAConnection());
                    } else {
                        DataSource nds = (DataSource) ds;

                        return nds.getConnection();
                    }
                }

                try {
                    if (Debug.infoOn()) Debug.logInfo("Doing JNDI lookup for name " + jndiName, module);
                    InitialContext ic = JNDIContextFactory.getInitialContext(jndiServerName);

                    if (ic != null) {
                        ds = ic.lookup(jndiName);
                    } else {
                        Debug.logWarning("Initial Context returned was NULL for server name " + jndiServerName, module);
                    }

                    if (ds != null) {
                        if (Debug.verboseOn()) Debug.logVerbose("Got a Datasource object.", module);
                        dsCache.put(jndiName, ds);
                        Connection con = null;

                        if (ds instanceof XADataSource) {
                            if (Debug.infoOn()) Debug.logInfo("Got XADataSource for name " + jndiName, module);
                            XADataSource xads = (XADataSource) ds;
                            XAConnection xac = xads.getXAConnection();

                            con = TransactionUtil.enlistConnection(xac);
                        } else {
                            if (Debug.infoOn()) Debug.logInfo("Got DataSource for name " + jndiName, module);
                            DataSource nds = (DataSource) ds;

                            con = nds.getConnection();
                        }

                        /* NOTE: This code causes problems because settting the transaction isolation level after a transaction has started is a no-no
                         * The question is: how should we do this?
                         String isolationLevel = jndiJdbcElement.getAttribute("isolation-level");
                         if (con != null && isolationLevel != null && isolationLevel.length() > 0) {
                         if ("Serializable".equals(isolationLevel)) {
                         con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                         } else if ("RepeatableRead".equals(isolationLevel)) {
                         con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                         } else if ("ReadUncommitted".equals(isolationLevel)) {
                         con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                         } else if ("ReadCommitted".equals(isolationLevel)) {
                         con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                         } else if ("None".equals(isolationLevel)) {
                         con.setTransactionIsolation(Connection.TRANSACTION_NONE);
                         }
                         }
                         */

                        // if (con != null) if (Debug.infoOn()) Debug.logInfo("[ConnectionFactory.getConnection] Got JNDI connection with catalog: " + con.getCatalog());
                        return con;
                    } else {
                        Debug.logError("Datasource returned was NULL.", module);
                    }
                } catch (NamingException ne) {
                    Debug.logWarning(ne, "[ConnectionFactory.getConnection] Failed to find DataSource named " + jndiName + " in JNDI server with name " + jndiServerName + ". Trying normal database.", module);
                } catch (GenericConfigException gce) {
                    throw new GenericEntityException("Problems with the JNDI configuration.", gce.getNested());
                }
            }

            break;

        case EntityConfigUtil.DatasourceInfo.TYPE_TYREX_DATA_SOURCE:
            Element tyrexDataSourceElement = datasourceInfo.datasourceTypeElement;
            String dataSourceName = tyrexDataSourceElement.getAttribute("dataSource-name");

            if (UtilValidate.isEmpty(dataSourceName)) {
                Debug.logError("dataSource-name not set for tyrex-dataSource element in the " + helperName + " data-source definition");
            } else {
                DataSource tyrexDataSource = TyrexFactory.getDataSource(dataSourceName);

                if (tyrexDataSource == null) {
                    Debug.logError("Got a null data source for dataSource-name " + dataSourceName + " for tyrex-dataSource element in the " + helperName + " data-source definition");
                } else {
                    Connection con = tyrexDataSource.getConnection();

                    /* NOTE: This code causes problems because settting the transaction isolation level after a transaction has started is a no-no
                     * The question is: how should we do this?
                     String isolationLevel = tyrexDataSourceElement.getAttribute("isolation-level");
                     if (con != null && isolationLevel != null && isolationLevel.length() > 0) {
                     if ("Serializable".equals(isolationLevel)) {
                     con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                     } else if ("RepeatableRead".equals(isolationLevel)) {
                     con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                     } else if ("ReadUncommitted".equals(isolationLevel)) {
                     con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                     } else if ("ReadCommitted".equals(isolationLevel)) {
                     con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                     } else if ("None".equals(isolationLevel)) {
                     con.setTransactionIsolation(Connection.TRANSACTION_NONE);
                     }
                     }
                     */
                    return con;
                }
            }

            break;

        case EntityConfigUtil.DatasourceInfo.TYPE_INLINE_JDBC:
            Element inlineJdbcElement = datasourceInfo.datasourceTypeElement;

            // If JNDI sources are not specified, or found, try Tyrex
            try {
                Connection con = TyrexConnectionFactory.getConnection(helperName, inlineJdbcElement);

                if (con != null)
                    return con;
            } catch (Exception ex) {
                Debug.logWarning(ex, "There was an error loading Tyrex, this may not be a serious problem, but you probably want to know anyway. Will continue with probably very slow manual JDBC load.");
            }

            // Default to plain JDBC.
            String driverClassName = inlineJdbcElement.getAttribute("jdbc-driver");

            if (driverClassName != null && driverClassName.length() > 0) {
                try {
                    Class.forName(driverClassName);
                } catch (ClassNotFoundException cnfe) {
                    Debug.logWarning("Could not find JDBC driver class named " + driverClassName + ".\n");
                    Debug.logWarning(cnfe);
                    return null;
                }
                return DriverManager.getConnection(inlineJdbcElement.getAttribute("jdbc-uri"),
                        inlineJdbcElement.getAttribute("jdbc-username"), inlineJdbcElement.getAttribute("jdbc-password"));
            }

            break;

        case EntityConfigUtil.DatasourceInfo.TYPE_OTHER:
            Debug.logError("Cannot find JDBC definition, no know element found for helperName \"" + helperName + "\"", module);
            break;
        }
        Debug.logError("******* ERROR: No database connection found for helperName \"" + helperName + "\"", module);
        return null;
    }
}
