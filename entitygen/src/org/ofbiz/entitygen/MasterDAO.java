package org.ofbiz.entitygen;

import java.lang.reflect.*;
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
  
  public static EgEntity getEgEntity(Class valueClass)
  {
    String entityName = valueClass.getName();
    entityName = entityName.substring(entityName.lastIndexOf('.')+1, entityName.length());
    Debug.logInfo("MasterDAO.getEgEntity for entityName: " + entityName);
    return DefReader.getEgEntity(fileName, entityName);
  }
  
  public static boolean insert(Object value)
  {
    Class valueClass = value.getClass();
    EgEntity entity = getEgEntity(valueClass);
    if(entity == null) return false;
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
    try 
    {
      ps = connection.prepareStatement(sql);

      for(int i=0;i<entity.fields.size();i++)
      { 
        EgField curField=(EgField)entity.fields.elementAt(i);
        setValue(ps, i+1, curField, value);
      }

      ps.executeUpdate();
      try { valueClass.getField("modified").setBoolean(value, false); } catch(Exception e) { Debug.logWarning("MasterDAO: could not set " + valueClass.getName() + ".modified."); }
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
    Class valueClass = value.getClass();
    EgEntity entity = getEgEntity(valueClass);
    if(entity == null) return false;
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

    String sql = "UPDATE " + entity.tableName + " SET " + entity.colNameString(entity.nopks, "=?, ", "=?") + " WHERE " + entity.colNameString(entity.pks, "=? AND ", "=?") + "";
    try {
      ps = connection.prepareStatement(sql);

      int i;
      for(i=0;i<entity.nopks.size();i++)
      {
        EgField curField=(EgField)entity.nopks.elementAt(i);
        setValue(ps, i+1, curField, value);
      }
      for(int j=0;j<entity.pks.size();j++)
      {
        EgField curField=(EgField)entity.pks.elementAt(j);
        setValue(ps, i+j+1, curField, value);
      }

      ps.executeUpdate();
      try { valueClass.getField("modified").setBoolean(value, false); } catch(Exception e) { Debug.logWarning("MasterDAO: could not set " + valueClass.getName() + ".modified."); }
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
    Class valueClass = value.getClass();
    EgEntity entity = getEgEntity(valueClass);
    if(entity == null) return false;
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
    
    String sql = "SELECT " + entity.colNameString(entity.nopks, ", ", "") + " FROM " + entity.tableName + " WHERE " + entity.colNameString(entity.pks, "=? AND ", "=?") + "";
    try {
      ps = connection.prepareStatement(sql);
      
      for(int i=0;i<entity.pks.size();i++)
      {
        EgField curField=(EgField)entity.pks.elementAt(i);
        setValue(ps, i+1, curField, value);
      }

      rs = ps.executeQuery();
      
      if(rs.next())
      {
        for(int j=0;j<entity.nopks.size();j++)
        {
          EgField curField=(EgField)entity.nopks.elementAt(j);
          getValue(rs, curField, value);
        }

        try { valueClass.getField("modified").setBoolean(value, false); } catch(Exception e) { Debug.logWarning("MasterDAO: could not set " + valueClass.getName() + ".modified."); }
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
    Class valueClass = value.getClass();
    EgEntity entity = getEgEntity(valueClass);
    if(entity == null) return false;
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

      for(int i=0;i<entity.pks.size();i++)
      {
        EgField curField=(EgField)entity.pks.elementAt(i);
        setValue(ps, i+1, curField, value);
      }

      ps.executeUpdate();
      try { valueClass.getField("modified").setBoolean(value, true); } catch(Exception e) { Debug.logWarning("MasterDAO: could not set " + valueClass.getName() + ".modified."); }
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
  
  public static void getValue(ResultSet rs, EgField curField, Object value) throws SQLException
  {
    Class valueClass = value.getClass();
    Field field = null;
    try { field = valueClass.getField(curField.fieldName); }
    catch(NoSuchFieldException e) { 
      Debug.logError("MasterDAO.getValue: field " + valueClass.getName() + "." + curField.fieldName + " not found; cannot set result.");
      return;
    }
    catch(SecurityException e) {
      Debug.logError("MasterDAO.getValue: security exception while getting field " + valueClass.getName() + "." + curField.fieldName + "; cannot set result.");
      return;
    }
    
    String fieldType = field.getType().getName();
    if(!fieldType.equals(curField.javaType) && fieldType.indexOf(curField.javaType) < 0)
    {
      Debug.logWarning("MasterDAO.getValue: type of field " + valueClass.getName() + "." + curField.fieldName + " is " + fieldType + ", was expecting " + curField.javaType + "; this may indicate an error in the configuration or in the class, and may result in an SQL-Java data conversion error. Will use the real field type: " + fieldType + ", not the definition.");
    }

    try 
    {
      if(fieldType.equals("java.lang.String") || fieldType.equals("String"))
        field.set(value, rs.getString(curField.columnName));
      else if(fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp"))
        field.set(value, rs.getTimestamp(curField.columnName));
      else if(fieldType.equals("java.sql.Time") || fieldType.equals("Time"))
        field.set(value, rs.getTime(curField.columnName));
      else if(fieldType.equals("java.sql.Date") || fieldType.equals("Date"))
        field.set(value, rs.getDate(curField.columnName));
      else if(fieldType.equals("java.lang.Integer") || fieldType.equals("Integer"))
      {
        if(rs.getObject(curField.columnName) == null) field.set(value, null);
        else field.set(value, new Integer(rs.getInt(curField.columnName)));
      }
      else if(fieldType.equals("java.lang.Long") || fieldType.equals("Long"))
      {
        if(rs.getObject(curField.columnName) == null) field.set(value, null);
        else field.set(value, new Long(rs.getLong(curField.columnName)));
      }
      else if(fieldType.equals("java.lang.Float") || fieldType.equals("Float"))
      {
        if(rs.getObject(curField.columnName) == null) field.set(value, null);
        else field.set(value, new Float(rs.getFloat(curField.columnName)));
      }
      else if(fieldType.equals("java.lang.Double") || fieldType.equals("Double"))
      {
        if(rs.getObject(curField.columnName) == null) field.set(value, null);
        else field.set(value, new Double(rs.getDouble(curField.columnName)));
      }
    }
    catch(IllegalAccessException e) {
      Debug.logError("MasterDAO.setValue: illegal access exception while setting field " + valueClass.getName() + "." + curField.fieldName + "; cannot set result.");
      return;
    }
  }

  public static void setValue(PreparedStatement ps, int ind, EgField curField, Object value) throws SQLException
  {
    Class valueClass = value.getClass();
    Field field = null;
    try { field = valueClass.getField(curField.fieldName); }
    catch(NoSuchFieldException e) { 
      Debug.logError("MasterDAO.setValue: field " + valueClass.getName() + "." + curField.fieldName + " not found; setting indexed parameter to null. This will result in an error if the corresponding column does not exist in the table.");
      ps.setNull(ind, Types.NULL); 
      return;
    }
    catch(SecurityException e) {
      Debug.logError("MasterDAO.setValue: security exception while getting field " + valueClass.getName() + "." + curField.fieldName + "; setting indexed parameter to null.");
      ps.setNull(ind, Types.NULL); 
      return;
    }
    
    String fieldType = field.getType().getName();
    if(!fieldType.equals(curField.javaType) && fieldType.indexOf(curField.javaType) < 0)
    {
      Debug.logWarning("MasterDAO.setValue: type of field " + valueClass.getName() + "." + curField.fieldName + " is " + fieldType + ", was expecting " + curField.javaType + "; this may indicate an error in the configuration or in the class, and may result in an SQL-Java data conversion error. Will use the real field type: " + fieldType + ", not the definition.");
    }
    
    try
    {
      if(fieldType.equals("java.lang.String") || fieldType.equals("String"))
        ps.setString(ind, (String)field.get(value));
      else if(fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp"))
        ps.setTimestamp(ind, (java.sql.Timestamp)field.get(value));
      else if(fieldType.equals("java.sql.Time") || fieldType.equals("Time"))
        ps.setTime(ind, (java.sql.Time)field.get(value));
      else if(fieldType.equals("java.sql.Date") || fieldType.equals("Date"))
        ps.setDate(ind, (java.sql.Date)field.get(value));
      else if(fieldType.equals("java.lang.Integer") || fieldType.equals("Integer"))
      {
        if(field.get(value) != null) ps.setInt(ind, ((java.lang.Integer)field.get(value)).intValue()); 
        else ps.setNull(ind, Types.NULL);
      }
      else if(fieldType.equals("java.lang.Long") || fieldType.equals("Long"))
      {
        if(field.get(value) != null) ps.setLong(ind, ((java.lang.Long)field.get(value)).longValue()); 
        else ps.setNull(ind, Types.NULL);
      }
      else if(fieldType.equals("java.lang.Float") || fieldType.equals("Float"))
      {
        if(field.get(value) != null) ps.setFloat(ind, ((java.lang.Float)field.get(value)).floatValue()); 
        else ps.setNull(ind, Types.NULL);
      }
      else if(fieldType.equals("java.lang.Double") || fieldType.equals("Double"))
      {
        if(field.get(value) != null) ps.setDouble(ind, ((java.lang.Double)field.get(value)).doubleValue()); 
        else ps.setNull(ind, Types.NULL);
      }
      else ps.setNull(ind, Types.NULL);
    }
    catch(IllegalAccessException e) {
      Debug.logError("MasterDAO.setValue: illegal access exception while getting field " + valueClass.getName() + "." + curField.fieldName + "; setting indexed parameter to null.");
      ps.setNull(ind, Types.NULL); 
      return;
    }
  }
}
