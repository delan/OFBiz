package org.ofbiz.core.util;

import java.sql.*;

public class ConnectionFactory
{
  public static Connection getConnection(String serverName) throws SQLException
  {
    try { Class.forName(UtilProperties.getPropertyValue("servers", serverName + ".jdbc.driver")); }
    catch(ClassNotFoundException cnfe) { cnfe.printStackTrace(); return null; }
    return DriverManager.getConnection(UtilProperties.getPropertyValue("servers", serverName + ".jdbc.uri"),
                                       UtilProperties.getPropertyValue("servers", serverName + ".jdbc.username"),
                                       UtilProperties.getPropertyValue("servers", serverName + ".jdbc.password"));
  }
}

