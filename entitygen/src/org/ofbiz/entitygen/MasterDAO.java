package org.ofbiz.entitygen;

import java.sql.*;
import java.util.*;
import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> MasterDAO: Master Data Access Object
 * <p><b>Description:</b> Handles persisntence for any defined entity.
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
 *
 *@author     David E. Jones
 *@created    Tue Aug 07 01:10:32 MDT 2001
 *@version    1.0
 */
public class MasterDAO
{
  public static String fileName = "f:\\ofbiz\\work\\ofbiz\\commonapp\\entitydef\\commonapp.xml";
  
  public static Connection getConnection() throws SQLException { return ConnectionFactory.getConnection(); }
  
  public static void initMasterDAO()
  {
    //quick load the entities...
    DefReader.loadAllEntities(fileName);
  }
  
  public static Entity getEntity(Object value)
  {
    String entityName = value.getClass().getName();
    return DefReader.getEntity(fileName, entityName);
  }
  
  public static boolean insert(Object value)
  {
    Entity entity = getEntity(value);
/*    
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [MasterDAO.insert]: Cannot insert Object: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [MasterDAO.insert]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "INSERT INTO " + entity.tableName + " (" + entity.colNameString(entity.fields) + ") VALUES (" + entity.fieldsStringList(entity.fields, "?", ", ") + ")";
    try {
      ps = connection.prepareStatement(sql);
/*
    <%for(i=0;i<entity.fields.size();i++){Field curField=(Field)entity.fields.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
      ps.setString(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
      ps.setTimestamp(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
      ps.setTime(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
      ps.setDate(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
      if(value.<%=curField.fieldName%> != null) ps.setInt(<%=i+1%>, value.<%=curField.fieldName%>.intValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
      if(value.<%=curField.fieldName%> != null) ps.setLong(<%=i+1%>, value.<%=curField.fieldName%>.longValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
      if(value.<%=curField.fieldName%> != null) ps.setFloat(<%=i+1%>, value.<%=curField.fieldName%>.floatValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
      if(value.<%=curField.fieldName%> != null) ps.setDouble(<%=i+1%>, value.<%=curField.fieldName%>.doubleValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}%><%}%>
*/
      ps.executeUpdate();
      //value.modified = false;
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [MasterDAO.insert]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }    
    return true;
  }

  public static boolean update(Object value)
  {
    Entity entity = getEntity(value);
    Vector nopks = new Vector(entity.fields); for(int ind=0;ind<entity.pks.size();ind++){ nopks.removeElement(entity.pks.elementAt(ind)); }
/*
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [MasterDAO.update]: Cannot update Object: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [MasterDAO.update]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }

    String sql = "UPDATE " + entity.tableName + " SET " + entity.colNameString(nopks, "=?, ", "=?") + " WHERE " + entity.colNameString(entity.pks, "=? AND ", "=?") + "";
    try {
      ps = connection.prepareStatement(sql);
/*
    <%for(i=0;i<nopks.size();i++){Field curField=(Field)nopks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
      ps.setString(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
      ps.setTimestamp(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
      ps.setTime(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
      ps.setDate(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
      if(value.<%=curField.fieldName%> != null) ps.setInt(<%=i+1%>, value.<%=curField.fieldName%>.intValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
      if(value.<%=curField.fieldName%> != null) ps.setLong(<%=i+1%>, value.<%=curField.fieldName%>.longValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
      if(value.<%=curField.fieldName%> != null) ps.setFloat(<%=i+1%>, value.<%=curField.fieldName%>.floatValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
      if(value.<%=curField.fieldName%> != null) ps.setDouble(<%=i+1%>, value.<%=curField.fieldName%>.doubleValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}%><%}%>
    <%for(i=0;i<entity.pks.size();i++){Field curField=(Field)entity.pks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
      ps.setString(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
      ps.setTimestamp(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
      ps.setTime(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
      ps.setDate(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
      if(value.<%=curField.fieldName%> != null) ps.setInt(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>.intValue()); else ps.setNull(<%=i+1+nopks.size()%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
      if(value.<%=curField.fieldName%> != null) ps.setLong(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>.longValue()); else ps.setNull(<%=i+1+nopks.size()%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
      if(value.<%=curField.fieldName%> != null) ps.setFloat(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>.floatValue()); else ps.setNull(<%=i+1+nopks.size()%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
      if(value.<%=curField.fieldName%> != null) ps.setDouble(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>.doubleValue()); else ps.setNull(<%=i+1+nopks.size()%>, Types.NULL);<%}%><%}%>
*/
      ps.executeUpdate();
      //value.modified = false;
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [MasterDAO.update]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }    
    return true;
  }

  public static boolean select(Object value)
  {
    Entity entity = getEntity(value);
    Vector nopks = new Vector(entity.fields); for(int ind=0;ind<entity.pks.size();ind++){ nopks.removeElement(entity.pks.elementAt(ind)); }
/*
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [MasterDAO.select]: Cannot select Object: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null; 
    PreparedStatement ps = null; 
    ResultSet rs = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [MasterDAO.select]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "SELECT " + entity.colNameString(nopks, ", ", "") + " FROM " + entity.tableName + " WHERE " + entity.colNameString(entity.pks, "=? AND ", "=?") + "";
    try {
      ps = connection.prepareStatement(sql);
/*
    <%for(i=0;i<entity.pks.size();i++){Field curField=(Field)entity.pks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
      ps.setString(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
      ps.setTimestamp(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
      ps.setTime(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
      ps.setDate(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
      if(value.<%=curField.fieldName%> != null) ps.setInt(<%=i+1%>, value.<%=curField.fieldName%>.intValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
      if(value.<%=curField.fieldName%> != null) ps.setLong(<%=i+1%>, value.<%=curField.fieldName%>.longValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
      if(value.<%=curField.fieldName%> != null) ps.setFloat(<%=i+1%>, value.<%=curField.fieldName%>.floatValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
      if(value.<%=curField.fieldName%> != null) ps.setDouble(<%=i+1%>, value.<%=curField.fieldName%>.doubleValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}%><%}%>
*/
      rs = ps.executeQuery();
      
      if(rs.next())
      {
/*
      <%for(i=0;i<nopks.size();i++){Field curField=(Field)nopks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
        value.<%=curField.fieldName%> = rs.getString("<%=curField.columnName%>");<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
        value.<%=curField.fieldName%> = rs.getTimestamp("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
        value.<%=curField.fieldName%> = rs.getTime("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
        value.<%=curField.fieldName%> = rs.getDate("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Integer(rs.getInt("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Long(rs.getLong("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Float(rs.getFloat("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Double(rs.getDouble("<%=curField.columnName%>"));<%}%><%}%>
*/
        //value.modified = false;
      } else {
        Debug.logWarning("ERROR [MasterDAO.select]: select failed, result set was empty.");
        return false;
      }
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [MasterDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    return true;
  }
    
  public static boolean delete(Object value)
  {
    Entity entity = getEntity(value);
/*
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [MasterDAO.delete]: Cannot delete Object: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [MasterDAO.delete]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "DELETE FROM " + entity.tableName + " WHERE " + entity.colNameString(entity.pks, "=? AND ", "=?") + "";
    try {
      ps = connection.prepareStatement(sql);
/*
    <%for(i=0;i<entity.pks.size();i++){Field curField=(Field)entity.pks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
      ps.setString(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
      ps.setTimestamp(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
      ps.setTime(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
      ps.setDate(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
      if(value.<%=curField.fieldName%> != null) ps.setInt(<%=i+1%>, value.<%=curField.fieldName%>.intValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
      if(value.<%=curField.fieldName%> != null) ps.setLong(<%=i+1%>, value.<%=curField.fieldName%>.longValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
      if(value.<%=curField.fieldName%> != null) ps.setFloat(<%=i+1%>, value.<%=curField.fieldName%>.floatValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
      if(value.<%=curField.fieldName%> != null) ps.setDouble(<%=i+1%>, value.<%=curField.fieldName%>.doubleValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}%><%}%>
*/
      ps.executeUpdate();
      //value.modified = true;
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [MasterDAO.delete]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }    
    return true;
  }  
}
