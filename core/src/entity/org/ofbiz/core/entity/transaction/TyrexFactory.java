/*
 * $Id$
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
package org.ofbiz.core.entity.transaction;

import java.net.*;
import java.sql.*;
import javax.sql.*;
import javax.transaction.*;
import org.w3c.dom.Element;

import org.ofbiz.core.entity.*;
import org.ofbiz.core.entity.config.*;
import org.ofbiz.core.util.*;

import tyrex.tm.*;
import tyrex.resource.*;

/**
 * TyrexTransactionFactory - central source for Tyrex JTA objects
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version    $Revision$
 * @since      2.0
 */
public class TyrexFactory implements TransactionFactoryInterface {

    protected static TransactionDomain td = null;
    protected static String DOMAIN_NAME = "default";

    static {

        td = TransactionDomain.getDomain(DOMAIN_NAME);

        if (td == null) {
            // probably because there was no tyrexdomain.xml file, try another method:

            /* For Tyrex version 0.9.8.5 */
            try {
                String resourceName = "tyrexdomain.xml";
                URL url = UtilURL.fromResource(resourceName);

                if (url != null) {
                    td = TransactionDomain.createDomain(url.toString());
                } else {
                    Debug.logError("ERROR: Could not create Tyrex Transaction Domain (resource not found):" + resourceName);
                }
            } catch (tyrex.tm.DomainConfigurationException e) {
                Debug.logError("Could not create Tyrex Transaction Domain (configuration):");
                Debug.logError(e);
            }

            if (td != null) {
                Debug.logImportant("Got TyrexDomain from classpath (NO tyrex.config file found)");
            }
        } else {
            Debug.logImportant("Got TyrexDomain from tyrex.config location");
        }

        if (td != null) {
            try {
                td.recover();
            } catch (tyrex.tm.RecoveryException e) {
                Debug.logError("Could not complete recovery phase of Tyrex TransactionDomain creation");
                Debug.logError(e);
            }
        } else {
            Debug.logError("Could not get Tyrex TransactionDomain for domain " + DOMAIN_NAME);
        }

        /* For Tyrex version 0.9.7.0 * /
         tyrex.resource.ResourceLimits rls = new tyrex.resource.ResourceLimits();
         td = new TransactionDomain("ofbiztx", rls);
         */
    }

    public static Resources getResources() {
        if (td != null) {
            return td.getResources();
        } else {
            Debug.logWarning("No Tyrex TransactionDomain, not returning resources");
            return null;
        }
    }

    public static DataSource getDataSource(String dsName) {
        Resources resources = getResources();

        if (resources != null) {
            try {
                return (DataSource) resources.getResource(dsName);
            } catch (tyrex.resource.ResourceException e) {
                Debug.logError(e, "Could not get tyrex dataSource resource with name " + dsName);
                return null;
            }
        } else {
            return null;
        }
    }

    public TransactionManager getTransactionManager() {
        if (td != null) {
            return td.getTransactionManager();
        } else {
            Debug.logWarning("No Tyrex TransactionDomain, not returning TransactionManager");
            return null;
        }
    }

    public UserTransaction getUserTransaction() {
        if (td != null) {
            return td.getUserTransaction();
        } else {
            Debug.logWarning("No Tyrex TransactionDomain, not returning UserTransaction");
            return null;
        }
    }
    
    public String getTxMgrName() {
        return "tyrex";
    }
    
    public Connection getConnection(String helperName) throws SQLException, GenericEntityException {
        EntityConfigUtil.DatasourceInfo datasourceInfo = EntityConfigUtil.getDatasourceInfo(helperName);

        if (datasourceInfo.inlineJdbcElement != null) {
            // Use JOTM (xapool.jar) connection pooling
            try {
                Connection con = TyrexConnectionFactory.getConnection(helperName, datasourceInfo.inlineJdbcElement);
                if (con != null) return con;
            } catch (Exception ex) {
                Debug.logError(ex, "Tyrex is the configured transaction manager but there was an error getting a database Connection through Tyrex for the " + helperName + " datasource. Please check your configuration, class path, etc.");
            }
        
            Connection otherCon = ConnectionFactory.tryGenericConnectionSources(helperName, datasourceInfo.inlineJdbcElement);
            return otherCon;
        } else if (datasourceInfo.tyrexDataSourceElement != null) {
            Element tyrexDataSourceElement = datasourceInfo.tyrexDataSourceElement;
            String dataSourceName = tyrexDataSourceElement.getAttribute("dataSource-name");

            if (UtilValidate.isEmpty(dataSourceName)) {
                Debug.logError("dataSource-name not set for tyrex-dataSource element in the " + helperName + " data-source definition");
            } else {
                DataSource tyrexDataSource = TyrexFactory.getDataSource(dataSourceName);

                if (tyrexDataSource == null) {
                    Debug.logError("Got a null data source for dataSource-name " + dataSourceName + " for tyrex-dataSource element in the " + helperName + " data-source definition; trying other sources");
                } else {
                    Connection con = tyrexDataSource.getConnection();

                    if (con != null) {
                        /* NOTE: This code causes problems because settting the transaction isolation level after a transaction has started is a no-no
                         * The question is: how should we do this?
                         String isolationLevel = tyrexDataSourceElement.getAttribute("isolation-level");
                         if (isolationLevel != null && isolationLevel.length() > 0) {
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
            }
            Connection otherCon = ConnectionFactory.tryGenericConnectionSources(helperName, datasourceInfo.inlineJdbcElement);
            return otherCon;
        } else {
            Debug.logError("Tyrex is the configured transaction manager but no inline-jdbc or tyrex-dataSource element was specified in the " + helperName + " datasource. Please check your configuration");
            return null;
        }
    }
    
    public void shutdown() {
        TyrexConnectionFactory.closeAll();
        if (td != null) {
            td.terminate();
            td = null;
        }                
    }
}
