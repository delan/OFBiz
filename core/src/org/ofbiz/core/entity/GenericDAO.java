package org.ofbiz.core.entity;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.model.*;

/**
 * <p><b>Title:</b> Generic Entity Data Access Object
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
 *@created    Wed Aug 08 2001
 *@version    1.0
 */
public class GenericDAO
{
  public static Connection getConnection() throws SQLException { return ConnectionFactory.getConnection(); }
  
  public static void initGenericDAO()
  {
    //quick load the entities...
    ModelReader.getFieldTypeCache();
    ModelReader.getEntityCache();
  }
  
  public static boolean insert(GenericEntity value)
  {
    ModelEntity entity = value.getModelEntity();
    if(entity == null) return false;
/*    
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [GenericDAO.insert]: Cannot insert GenericEntity: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.insert]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "INSERT INTO " + entity.tableName + " (" + entity.colNameString(entity.fields) + ") VALUES (" + entity.fieldsStringList(entity.fields, "?", ", ") + ")";
    try 
    {
      ps = connection.prepareStatement(sql);

      for(int i=0;i<entity.fields.size();i++)
      { 
        ModelField curField=(ModelField)entity.fields.elementAt(i);
        setValue(ps, i+1, curField, value);
      }

      ps.executeUpdate();
      value.modified = false;
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [GenericDAO.insert]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }    
    return true;
  }

  public static boolean update(GenericEntity value)
  {
    ModelEntity entity = value.getModelEntity();
    if(entity == null) return false;
/*
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [GenericDAO.update]: Cannot update GenericEntity: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.update]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }

    String sql = "UPDATE " + entity.tableName + " SET " + entity.colNameString(entity.nopks, "=?, ", "=?") + " WHERE " + entity.colNameString(entity.pks, "=? AND ", "=?") + "";
    try {
      ps = connection.prepareStatement(sql);

      for(int i=0;i<entity.nopks.size();i++)
      {
        ModelField curField=(ModelField)entity.nopks.elementAt(i);
        setValue(ps, i+1, curField, value);
      }
      for(int j=0;j<entity.pks.size();j++)
      {
        ModelField curField=(ModelField)entity.pks.elementAt(j);
        setValue(ps, j+1, curField, value);
      }

      ps.executeUpdate();
      value.modified = false;
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [GenericDAO.update]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }    
    return true;
  }

  public static boolean select(GenericEntity value)
  {
    ModelEntity entity = value.getModelEntity();
    if(entity == null) return false;
/*
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [GenericDAO.select]: Cannot select GenericEntity: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null; 
    PreparedStatement ps = null; 
    ResultSet rs = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.select]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "SELECT " + entity.colNameString(entity.nopks, ", ", "") + " FROM " + entity.tableName + " WHERE " + entity.colNameString(entity.pks, "=? AND ", "=?") + "";
    try {
      ps = connection.prepareStatement(sql);
      
      for(int i=0;i<entity.pks.size();i++)
      {
        ModelField curField=(ModelField)entity.pks.elementAt(i);
        setValue(ps, i+1, curField, value);
      }

      rs = ps.executeQuery();
      
      if(rs.next())
      {
        for(int j=0;j<entity.nopks.size();j++)
        {
          ModelField curField=(ModelField)entity.nopks.elementAt(j);
          getValue(rs, curField, value);
        }

        value.modified = false;
      } else {
        Debug.logWarning("ERROR [GenericDAO.select]: select failed, result set was empty.");
        return false;
      }
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [GenericDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    return true;
  }
    
  public static boolean delete(GenericEntity value)
  {
    ModelEntity entity = value.getModelEntity();
    if(entity == null) return false;
/*
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [GenericDAO.delete]: Cannot delete GenericEntity: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.delete]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "DELETE FROM " + entity.tableName + " WHERE " + entity.colNameString(entity.pks, "=? AND ", "=?") + "";
    try {
      ps = connection.prepareStatement(sql);

      for(int i=0;i<entity.pks.size();i++)
      {
        ModelField curField=(ModelField)entity.pks.elementAt(i);
        setValue(ps, i+1, curField, value);
      }

      ps.executeUpdate();
      value.modified = true;
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [GenericDAO.delete]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }    
    return true;
  }
  
  public static void getValue(ResultSet rs, ModelField curField, GenericEntity value) throws SQLException
  {
    Class valueClass = value.getClass();
    Field field = null;
    try { field = valueClass.getField(curField.name); }
    catch(NoSuchFieldException e) { 
      Debug.logError("GenericDAO.getValue: field " + valueClass.getName() + "." + curField.name + " not found; cannot set result.");
      return;
    }
    catch(SecurityException e) {
      Debug.logError("GenericDAO.getValue: security exception while getting field " + valueClass.getName() + "." + curField.name + "; cannot set result.");
      return;
    }
    
    String fieldType = field.getType().getName();
    ModelFieldType mft = ModelReader.getModelFieldType(curField.type);
    if(!fieldType.equals(mft.javaType) && fieldType.indexOf(mft.javaType) < 0)
    {
      Debug.logWarning("GenericDAO.getValue: type of field " + valueClass.getName() + "." + curField.name + " is " + fieldType + ", was expecting " + mft.javaType + "; this may indicate an error in the configuration or in the class, and may result in an SQL-Java data conversion error. Will use the real field type: " + fieldType + ", not the definition.");
    }

    try 
    {
      if(fieldType.equals("java.lang.String") || fieldType.equals("String"))
        field.set(value, rs.getString(curField.colName));
      else if(fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp"))
        field.set(value, rs.getTimestamp(curField.colName));
      else if(fieldType.equals("java.sql.Time") || fieldType.equals("Time"))
        field.set(value, rs.getTime(curField.colName));
      else if(fieldType.equals("java.sql.Date") || fieldType.equals("Date"))
        field.set(value, rs.getDate(curField.colName));
      else if(fieldType.equals("java.lang.Integer") || fieldType.equals("Integer"))
      {
        if(rs.getObject(curField.colName) == null) field.set(value, null);
        else field.set(value, new Integer(rs.getInt(curField.colName)));
      }
      else if(fieldType.equals("java.lang.Long") || fieldType.equals("Long"))
      {
        if(rs.getObject(curField.colName) == null) field.set(value, null);
        else field.set(value, new Long(rs.getLong(curField.colName)));
      }
      else if(fieldType.equals("java.lang.Float") || fieldType.equals("Float"))
      {
        if(rs.getObject(curField.colName) == null) field.set(value, null);
        else field.set(value, new Float(rs.getFloat(curField.colName)));
      }
      else if(fieldType.equals("java.lang.Double") || fieldType.equals("Double"))
      {
        if(rs.getObject(curField.colName) == null) field.set(value, null);
        else field.set(value, new Double(rs.getDouble(curField.colName)));
      }
    }
    catch(IllegalAccessException e) {
      Debug.logError("GenericDAO.setValue: illegal access exception while setting field " + valueClass.getName() + "." + curField.name + "; cannot set result.");
      return;
    }
  }

  public static void setValue(PreparedStatement ps, int ind, ModelField curField, GenericEntity value) throws SQLException
  {
    Class valueClass = value.getClass();
    Field field = null;
    try { field = valueClass.getField(curField.name); }
    catch(NoSuchFieldException e) { 
      Debug.logError("GenericDAO.setValue: field " + valueClass.getName() + "." + curField.name + " not found; setting indexed parameter to null. This will result in an error if the corresponding column does not exist in the table.");
      ps.setNull(ind, Types.NULL); 
      return;
    }
    catch(SecurityException e) {
      Debug.logError("GenericDAO.setValue: security exception while getting field " + valueClass.getName() + "." + curField.name + "; setting indexed parameter to null.");
      ps.setNull(ind, Types.NULL); 
      return;
    }
    
    String fieldType = field.getType().getName();
    ModelFieldType mft = ModelReader.getModelFieldType(curField.type);
    if(!fieldType.equals(mft.javaType) && fieldType.indexOf(mft.javaType) < 0)
    {
      Debug.logWarning("GenericDAO.setValue: type of field " + valueClass.getName() + "." + curField.name + " is " + fieldType + ", was expecting " + mft.javaType + "; this may indicate an error in the configuration or in the class, and may result in an SQL-Java data conversion error. Will use the real field type: " + fieldType + ", not the definition.");
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
      Debug.logError("GenericDAO.setValue: illegal access exception while getting field " + valueClass.getName() + "." + curField.name + "; setting indexed parameter to null.");
      ps.setNull(ind, Types.NULL); 
      return;
    }
  }
}
