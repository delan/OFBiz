/*
 * $Id$
 *
 * Copyright (c) 2001, 2002 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.ofbiz.entity.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.transaction.DBCPConnectionFactory;
import org.ofbiz.entity.transaction.TransactionFactory;
import org.ofbiz.entity.transaction.MinervaConnectionFactory;
import org.w3c.dom.Element;

/**
 * ConnectionFactory - central source for JDBC connections
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@ofbiz.org">Andy Zeneski</a>
 * @version    $Rev:$
 * @since      2.0
 */
public class ConnectionFactory {
    // Debug module name
    public static final String module = ConnectionFactory.class.getName();

    public static Connection getConnection(String helperName) throws SQLException, GenericEntityException {
        // Debug.logVerbose("Getting a connection", module);

        Connection con = TransactionFactory.getConnection(helperName);
        if (con == null) {
            Debug.logError("******* ERROR: No database connection found for helperName \"" + helperName + "\"", module);
        }
        return con;
    }
    
    public static Connection tryGenericConnectionSources(String helperName, Element inlineJdbcElement) throws SQLException, GenericEntityException {
        // first try Minerva
        try {
            Connection con = MinervaConnectionFactory.getConnection(helperName, inlineJdbcElement);
            if (con != null) return con;
        } catch (Exception ex) {
            Debug.logError(ex, "There was an error getting a Minerva datasource.", module);
        }

        // next try DBCP
        try {
            Connection con = DBCPConnectionFactory.getConnection(helperName, inlineJdbcElement);
            if (con != null) return con;
        } catch (Exception ex) {
            Debug.logError(ex, "There was an error getting a DBCP datasource.", module);
        }
        
        // Default to plain JDBC.
        String driverClassName = inlineJdbcElement.getAttribute("jdbc-driver");

        if (driverClassName != null && driverClassName.length() > 0) {
            try {
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                Class clazz = loader.loadClass(driverClassName);
                clazz.newInstance();
            } catch (ClassNotFoundException e) {
                Debug.logWarning(e, "Could not find JDBC driver class named " + driverClassName, module);
                return null;
            } catch (java.lang.IllegalAccessException e) {
                Debug.logWarning(e, "Not allowed to access JDBC driver class named " + driverClassName, module);
                return null;
            } catch (java.lang.InstantiationException e) {
                Debug.logWarning(e, "Could not create new instance of JDBC driver class named " + driverClassName, module);
                return null;
            }
            return DriverManager.getConnection(inlineJdbcElement.getAttribute("jdbc-uri"),
                    inlineJdbcElement.getAttribute("jdbc-username"), inlineJdbcElement.getAttribute("jdbc-password"));
        }

        return null;
    }
}
