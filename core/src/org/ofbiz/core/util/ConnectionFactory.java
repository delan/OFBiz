package org.ofbiz.core.util;

import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;

public class ConnectionFactory
{
  static Map dsCache = new Hashtable();
  public static Connection getConnection(String serverName) throws SQLException
  {
    String jndiName = UtilProperties.getPropertyValue("servers", serverName + ".jdbc.jndi.name");
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
    
    String driverClassName = UtilProperties.getPropertyValue("servers", serverName + ".jdbc.driver");
    try { Class.forName(driverClassName); }
    catch(ClassNotFoundException cnfe) { Debug.logWarning("Could not find JDBC driver class named " + driverClassName + ".\n"); Debug.logWarning(cnfe); return null; }
    return DriverManager.getConnection(UtilProperties.getPropertyValue("servers", serverName + ".jdbc.uri"),
                                       UtilProperties.getPropertyValue("servers", serverName + ".jdbc.username"),
                                       UtilProperties.getPropertyValue("servers", serverName + ".jdbc.password"));
  }
}

