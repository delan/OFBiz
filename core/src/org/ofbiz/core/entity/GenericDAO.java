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
  
  public static boolean insert(GenericEntity entity)
  {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null) return false;
/*    
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("ERROR [GenericDAO.insert]: Cannot insert GenericEntity: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.insert]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "INSERT INTO " + modelEntity.tableName + " (" + modelEntity.colNameString(modelEntity.fields) + ") VALUES (" + modelEntity.fieldsStringList(modelEntity.fields, "?", ", ") + ")";
    try 
    {
      ps = connection.prepareStatement(sql);

      for(int i=0;i<modelEntity.fields.size();i++)
      { 
        ModelField curField=(ModelField)modelEntity.fields.elementAt(i);
        setValue(ps, i+1, curField, entity);
      }

      ps.executeUpdate();
      entity.modified = false;
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

  public static boolean update(GenericEntity entity)
  {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null) return false;
/*
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("ERROR [GenericDAO.update]: Cannot update GenericEntity: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.update]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }

    String sql = "UPDATE " + modelEntity.tableName + " SET " + modelEntity.colNameString(modelEntity.nopks, "=?, ", "=?") + " WHERE " + modelEntity.colNameString(modelEntity.pks, "=? AND ", "=?") + "";
    try {
      ps = connection.prepareStatement(sql);

      int i;
      for(i=0;i<modelEntity.nopks.size();i++)
      {
        ModelField curField=(ModelField)modelEntity.nopks.elementAt(i);
        setValue(ps, i+1, curField, entity);
      }
      for(int j=0;j<modelEntity.pks.size();j++)
      {
        ModelField curField=(ModelField)modelEntity.pks.elementAt(j);
        setValue(ps, i+j+1, curField, entity);
      }

      ps.executeUpdate();
      entity.modified = false;
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

  public static boolean select(GenericEntity entity)
  {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null) return false;
/*
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("ERROR [GenericDAO.select]: Cannot select GenericEntity: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null; 
    PreparedStatement ps = null; 
    ResultSet rs = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.select]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "SELECT " + modelEntity.colNameString(modelEntity.nopks, ", ", "") + " FROM " + modelEntity.tableName + " WHERE " + modelEntity.colNameString(modelEntity.pks, "=? AND ", "=?") + "";
    try {
      ps = connection.prepareStatement(sql);
      
      for(int i=0;i<modelEntity.pks.size();i++)
      {
        ModelField curField=(ModelField)modelEntity.pks.elementAt(i);
        setValue(ps, i+1, curField, entity);
      }

      rs = ps.executeQuery();
      
      if(rs.next())
      {
        for(int j=0;j<modelEntity.nopks.size();j++)
        {
          ModelField curField=(ModelField)modelEntity.nopks.elementAt(j);
          getValue(rs, curField, entity);
        }

        entity.modified = false;
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
    
  public static boolean delete(GenericEntity entity)
  {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null) return false;
/*
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("ERROR [GenericDAO.delete]: Cannot delete GenericEntity: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.delete]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "DELETE FROM " + modelEntity.tableName + " WHERE " + modelEntity.colNameString(modelEntity.pks, "=? AND ", "=?") + "";
    try {
      ps = connection.prepareStatement(sql);

      for(int i=0;i<modelEntity.pks.size();i++)
      {
        ModelField curField=(ModelField)modelEntity.pks.elementAt(i);
        setValue(ps, i+1, curField, entity);
      }

      ps.executeUpdate();
      entity.modified = true;
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
  
  public static void getValue(ResultSet rs, ModelField curField, GenericEntity entity) throws SQLException
  {
    ModelFieldType mft = ModelReader.getModelFieldType(curField.type);
    if(mft == null)
    {
      Debug.logWarning("GenericDAO.getValue: definition fieldType " + curField.type + " not found, cannot getValue for field " + entity.getEntityName() + "." + curField.name + ".");
      return;
    }
    String fieldType = mft.javaType;

    if(fieldType.equals("java.lang.String") || fieldType.equals("String"))
      entity.set(curField.name, rs.getString(curField.colName));
    else if(fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp"))
      entity.set(curField.name, rs.getTimestamp(curField.colName));
    else if(fieldType.equals("java.sql.Time") || fieldType.equals("Time"))
      entity.set(curField.name, rs.getTime(curField.colName));
    else if(fieldType.equals("java.sql.Date") || fieldType.equals("Date"))
      entity.set(curField.name, rs.getDate(curField.colName));
    else if(fieldType.equals("java.lang.Integer") || fieldType.equals("Integer"))
    {
      if(rs.getObject(curField.colName) == null) entity.set(curField.name, null);
      else entity.set(curField.name, new Integer(rs.getInt(curField.colName)));
    }
    else if(fieldType.equals("java.lang.Long") || fieldType.equals("Long"))
    {
      if(rs.getObject(curField.colName) == null) entity.set(curField.name, null);
      else entity.set(curField.name, new Long(rs.getLong(curField.colName)));
    }
    else if(fieldType.equals("java.lang.Float") || fieldType.equals("Float"))
    {
      if(rs.getObject(curField.colName) == null) entity.set(curField.name, null);
      else entity.set(curField.name, new Float(rs.getFloat(curField.colName)));
    }
    else if(fieldType.equals("java.lang.Double") || fieldType.equals("Double"))
    {
      if(rs.getObject(curField.colName) == null) entity.set(curField.name, null);
      else entity.set(curField.name, new Double(rs.getDouble(curField.colName)));
    }
  }

  public static void setValue(PreparedStatement ps, int ind, ModelField curField, GenericEntity entity) throws SQLException
  {
    Object field = entity.get(curField.name);
    Class fieldClass = field.getClass();    
    String fieldType = fieldClass.getName();
    ModelFieldType mft = ModelReader.getModelFieldType(curField.type);
    if(!fieldType.equals(mft.javaType) && fieldType.indexOf(mft.javaType) < 0)
    {
      Debug.logWarning("GenericDAO.setValue: type of field " + entity.getEntityName() + "." + curField.name + " is " + fieldType + ", was expecting " + mft.javaType + "; this may indicate an error in the configuration or in the class, and may result in an SQL-Java data conversion error. Will use the real field type: " + fieldType + ", not the definition.");
    }
    
    if(fieldType.equals("java.lang.String") || fieldType.equals("String"))
      ps.setString(ind, (String)field);
    else if(fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp"))
      ps.setTimestamp(ind, (java.sql.Timestamp)field);
    else if(fieldType.equals("java.sql.Time") || fieldType.equals("Time"))
      ps.setTime(ind, (java.sql.Time)field);
    else if(fieldType.equals("java.sql.Date") || fieldType.equals("Date"))
      ps.setDate(ind, (java.sql.Date)field);
    else if(fieldType.equals("java.lang.Integer") || fieldType.equals("Integer"))
    {
      if(field != null) ps.setInt(ind, ((java.lang.Integer)field).intValue()); 
      else ps.setNull(ind, Types.NULL);
    }
    else if(fieldType.equals("java.lang.Long") || fieldType.equals("Long"))
    {
      if(field != null) ps.setLong(ind, ((java.lang.Long)field).longValue()); 
      else ps.setNull(ind, Types.NULL);
    }
    else if(fieldType.equals("java.lang.Float") || fieldType.equals("Float"))
    {
      if(field != null) ps.setFloat(ind, ((java.lang.Float)field).floatValue()); 
      else ps.setNull(ind, Types.NULL);
    }
    else if(fieldType.equals("java.lang.Double") || fieldType.equals("Double"))
    {
      if(field != null) ps.setDouble(ind, ((java.lang.Double)field).doubleValue()); 
      else ps.setNull(ind, Types.NULL);
    }
    else ps.setNull(ind, Types.NULL);
  }
}
