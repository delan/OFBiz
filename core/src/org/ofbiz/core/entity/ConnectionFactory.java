package org.ofbiz.core.entity;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> ConnectionFactory.java
 * <p><b>Description:</b> ConnectionFactory - central source for JDBC connections
 * <p>Copyright (c) 2001 The Open For Business Project and repected authors.
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
 * @version 1.0
 * Created on July 1, 2001, 5:03 PM
 */
public class ConnectionFactory
{
  static Map dsCache = new Hashtable();
  public static Connection getConnection(String helperName) throws SQLException
  {
    String jndiName = UtilProperties.getPropertyValue("servers", helperName + ".jdbc.jndi.name");
    if(jndiName != null)
    {
      DataSource ds;
      ds = (DataSource)dsCache.get(jndiName);
      if(ds != null) return ds.getConnection();

      try
      {
        InitialContext ic = new InitialContext();
        ds = (DataSource)ic.lookup(jndiName);
        if(ds != null)
        {
          dsCache.put(jndiName, ds);
          return ds.getConnection();
        }
      }
      catch(NamingException ne) { /* Debug.logWarning("Failed to find DataSource named " + jndiName + " in JNDI. Trying normal database."); */ }
    }
    
    // Try to use PoolMan Connection Pool.
    boolean usingPoolMan = true;
    try {
       Class.forName("com.codestudio.sql.PoolMan").newInstance();
       //Debug.logInfo("Found PoolMan Driver...");
    }
    catch ( Exception ex ) { usingPoolMan = false; }
   
    if ( usingPoolMan ) {
        String poolManName = UtilProperties.getPropertyValue("servers", helperName + ".jdbc.poolman");
        //Debug.logInfo("Attempting to connect to '"+poolManName+"'");
        Connection con = DriverManager.getConnection("jdbc:poolman://" + poolManName);
        if ( con != null ) {
            //Debug.logInfo("Connection to PoolMan established.");
            return con;
        }
        usingPoolMan = false;
    }
    
    // Default to plain JDBC.
    String driverClassName = UtilProperties.getPropertyValue("servers", helperName + ".jdbc.driver");
    try { Class.forName(driverClassName); }
    catch(ClassNotFoundException cnfe) { Debug.logWarning("Could not find JDBC driver class named " + driverClassName + ".\n"); Debug.logWarning(cnfe); return null; }
    return DriverManager.getConnection(UtilProperties.getPropertyValue("servers", helperName + ".jdbc.uri"),
                                       UtilProperties.getPropertyValue("servers", helperName + ".jdbc.username"),
                                       UtilProperties.getPropertyValue("servers", helperName + ".jdbc.password"));
  }
}

