package org.ofbiz.core.util;

import java.sql.*;

public class ConnectionFactory
{
  public static Connection getConnection() throws SQLException
  {
    try { Class.forName("org.gjt.mm.mysql.Driver"); }
    catch(ClassNotFoundException cnfe) { cnfe.printStackTrace(); return null; }
    return DriverManager.getConnection("jdbc:mysql://localhost/ofbiz","mysqldb","mysqldb");
  }
}

