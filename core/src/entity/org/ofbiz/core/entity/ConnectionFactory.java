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

import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.config.*;

/**
 * ConnectionFactory - central source for JDBC connections
 *
 * @author <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @version 1.0
 * Created on July 1, 2001, 5:03 PM
 */
public class ConnectionFactory {
    //Debug module name
    public static final String module = ConnectionFactory.class.getName();

    static UtilCache dsCache = new UtilCache("entity.JndiDataSources", 0, 0);

    public static Connection getConnection(String helperName) throws SQLException, GenericEntityException {
        //Debug.logVerbose("Getting a connection", module);
        
        Element rootElement = EntityConfigUtil.getXmlRootElement();
        Element datasourceElement = UtilXml.firstChildElement(rootElement, "datasource", "name", helperName);
        Element jndiJdbcElement = UtilXml.firstChildElement(datasourceElement, "jndi-jdbc");
        
        if (jndiJdbcElement != null) {
            String jndiName = jndiJdbcElement.getAttribute("jndi-name");
            String jndiServerName = jndiJdbcElement.getAttribute("jndi-server-name");
            
            //Debug.logVerbose("Trying JNDI name " + jndiName, module);
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
                //try again inside the synch just in case someone when through while we were waiting
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
                    Debug.logInfo("Doing JNDI lookup for name " + jndiName, module);
                    InitialContext ic = JNDIContextFactory.getInitialContext(jndiServerName);
                    if (ic != null)
                        ds = ic.lookup(jndiName);
                    if (ds != null) {
                        dsCache.put(jndiName, ds);
                        Connection con = null;
                        if (ds instanceof XADataSource) {
                            Debug.logInfo("Got XADataSource for name " + jndiName, module);
                            XADataSource xads = (XADataSource) ds;
                            XAConnection xac = xads.getXAConnection();
                            con = TransactionUtil.enlistConnection(xac);
                        } else {
                            Debug.logInfo("Got DataSource for name " + jndiName, module);
                            DataSource nds = (DataSource) ds;
                            con = nds.getConnection();
                        }
                        //if (con != null) Debug.logInfo("[ConnectionFactory.getConnection] Got JNDI connection with catalog: " + con.getCatalog());
                        return con;
                    }
                } catch (NamingException ne) {
                    Debug.logVerbose("[ConnectionFactory.getConnection] Failed to find DataSource named " + jndiName + " in JNDI. Trying normal database.", module);
                }
            }
        }

        Element inlineJdbcElement = UtilXml.firstChildElement(datasourceElement, "inline-jdbc");
        if (inlineJdbcElement != null) {
            //If JNDI sources are not specified, or found, try Tyrex
            try {
                // For Tyrex 0.9.8.5
                Class.forName("tyrex.resource.jdbc.xa.EnabledDataSource").newInstance();
                // For Tyrex 0.9.7.0
                //Class.forName("tyrex.jdbc.xa.EnabledDataSource").newInstance();
                //Debug.logInfo("Found Tyrex Driver...");

                Connection con = TyrexConnectionFactory.getConnection(helperName, inlineJdbcElement);
                if (con != null)
                    return con;
            } catch (Exception ex) {
                Debug.logWarning(ex, "There was an error loading Tyrex, this may not be a serious problem, but you probably want to know anyway.");
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
        } else {
            Debug.logError("Cannot find JDBC definition, no inline-jdbc element found for helperName \"" + helperName + "\"", module);
        }

        Debug.logError("******* ERROR: No database connection found for helperName \"" + helperName + "\"", module);
        return null;
    }
}
