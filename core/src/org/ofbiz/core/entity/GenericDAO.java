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
  static Map genericDAOs = new Hashtable();
  String serverName;
  ModelReader modelReader;
  
  public static GenericDAO getGenericDAO(String serverName)
  {
    GenericDAO newGenericDAO = (GenericDAO)genericDAOs.get(serverName);
    if(newGenericDAO == null) //don't want to block here
    {
      synchronized(GenericDAO.class) 
      { 
        if(newGenericDAO == null)
        {
          newGenericDAO = new GenericDAO(serverName);
          genericDAOs.put(serverName, newGenericDAO);
        }
      }
    }
    return newGenericDAO;
  }
  
  public GenericDAO(String serverName)
  { 
    this.serverName = serverName;
    modelReader = ModelReader.getModelReader(serverName);
  }
    
  public Connection getConnection() throws SQLException { return ConnectionFactory.getConnection(serverName); }
  
  public boolean insert(GenericEntity entity)
  {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null)
    {
      Debug.logError("[GenericDAO.insert] Could not find ModelEntity record for entityName: " + entity.getEntityName());
      return false;
    }
/*    
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("ERROR [GenericDAO.insert]: Cannot insert GenericEntity: required primary key field(s) missing.");
      return false;
    }
*/    
    boolean useTX = true;
    Connection connection = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.insert]: Unable to esablish a connection with the database... Error was:"); Debug.logWarning(sqle); }
    
    
    try { connection.setAutoCommit(false); } 
    catch(SQLException sqle) { useTX = false; }
    
    try 
    {
      singleInsert(entity, modelEntity, modelEntity.fields, connection);
      storeAllRelated(entity, connection);
      if(useTX) connection.commit();
    } 
    catch (SQLException sqle) 
    {
      Debug.logWarning("ERROR [GenericDAO.insert]: SQL Exception while executing insert. Error was:");
      Debug.logWarning(sqle.getMessage());
      
      try { if(useTX) connection.rollback(); }
      catch(SQLException sqle2) { Debug.logWarning("ERROR [GenericDAO.insert]: SQL Exception while rolling back insert. Error was:"); Debug.logWarning(sqle2); }
      
      return false;
    } finally {
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }    
    return true;
  }
  
  private void singleInsert(GenericEntity entity, ModelEntity modelEntity, Vector fieldsToSave, Connection connection) throws SQLException
  {
    PreparedStatement ps = null;
    String sql = "INSERT INTO " + modelEntity.tableName + " (" + modelEntity.colNameString(fieldsToSave) + ") VALUES (" + modelEntity.fieldsStringList(fieldsToSave, "?", ", ") + ")";
    ps = connection.prepareStatement(sql);

    for(int i=0;i<fieldsToSave.size();i++)
    { 
      ModelField curField=(ModelField)fieldsToSave.elementAt(i);
      setValue(ps, i+1, curField, entity);
    }

    ps.executeUpdate();
    entity.modified = false;
    try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
  }

  public boolean update(GenericEntity entity)
  {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null)
    {
      Debug.logError("[GenericDAO.update] Could not find ModelEntity record for entityName: " + entity.getEntityName());
      return false;
    }
    
    return customUpdate(entity, modelEntity, modelEntity.nopks);
  }

  public boolean partialUpdate(GenericEntity entity)
  {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null)
    {
      Debug.logError("[GenericDAO.partialUpdate] Could not find ModelEntity record for entityName: " + entity.getEntityName());
      return false;
    }
    //we don't want to update ALL fields, just the nonpk fields that are in the passed GenericEntity
    Vector partialFields = new Vector();
    Collection keys = entity.getAllKeys();
    for(int fi=0; fi<modelEntity.nopks.size(); fi++)
    {
      ModelField curField=(ModelField)modelEntity.nopks.elementAt(fi);
      if(keys.contains(curField.name)) partialFields.add(curField);
    }
    
    return customUpdate(entity, modelEntity, partialFields);
  }
  
  private boolean customUpdate(GenericEntity entity, ModelEntity modelEntity, Vector fieldsToSave)
  {
/*
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("ERROR [GenericDAO.update]: Cannot update GenericEntity: required primary key field(s) missing.");
      return false;
    }
*/    
    boolean useTX = true;
    Connection connection = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.update]: Unable to esablish a connection with the database... Error was:"); Debug.logWarning(sqle); }

    try { connection.setAutoCommit(false); } 
    catch(SQLException sqle) { useTX = false; }
        
    try 
    {
      singleUpdate(entity, modelEntity, fieldsToSave, connection);
      storeAllRelated(entity, connection);
      if(useTX) connection.commit();
    } 
    catch (SQLException sqle) 
    {
      Debug.logWarning("ERROR [GenericDAO.update]: SQL Exception while executing update. Error was:");
      Debug.logWarning(sqle.getMessage());

      try { if(useTX) connection.rollback(); }
      catch(SQLException sqle2) { Debug.logWarning("ERROR [GenericDAO.insert]: SQL Exception while rolling back insert. Error was:"); Debug.logWarning(sqle2); }

      return false;
    } finally {
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    
    return true;
  }

  private void singleUpdate(GenericEntity entity, ModelEntity modelEntity, Vector fieldsToSave, Connection connection) throws SQLException
  {
    String sql = "UPDATE " + modelEntity.tableName + " SET " + modelEntity.colNameString(fieldsToSave, "=?, ", "=?") + " WHERE " + modelEntity.colNameString(modelEntity.pks, "=? AND ", "=?") + "";
    PreparedStatement ps = null;
    ps = connection.prepareStatement(sql);

    int i;
    for(i=0;i<fieldsToSave.size();i++)
    {
      ModelField curField=(ModelField)fieldsToSave.elementAt(i);
      setValue(ps, i+1, curField, entity);
    }
    for(int j=0;j<modelEntity.pks.size();j++)
    {
      ModelField curField=(ModelField)modelEntity.pks.elementAt(j);
      setValue(ps, i+j+1, curField, entity);
    }

    ps.executeUpdate();
    entity.modified = false;
    try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
  }
  
  private void storeAllRelated(GenericEntity entity, Connection connection) throws SQLException
  {
    //also store valueObject.relatedToStore related entities
    if(entity.relatedToStore != null && entity.relatedToStore.size() > 0)
    {
      Iterator entries = entity.relatedToStore.entrySet().iterator();
      Map.Entry anEntry = null;
      while(entries != null && entries.hasNext())
      {
        anEntry = (Map.Entry)entries.next();
        String relationName = (String)anEntry.getKey();
        Collection entities = (Collection)anEntry.getValue();
        storeRelated(relationName, entity, entities, connection);
      }
    }
  }
  
  private void storeRelated(String relationName, GenericEntity value, Collection entities, Connection connection) throws SQLException
  {
    ModelEntity entity = value.getModelEntity();
    ModelRelation relation = entity.getRelation(relationName);
    ModelEntity relatedEntity = modelReader.getModelEntity(relation.relEntityName);

    //if entity exists, update, else insert
    Iterator entIter = UtilMisc.toIterator(entities);
    while(entIter != null && entIter.hasNext())
    {
      GenericEntity curEntity = (GenericEntity)entIter.next();
      if(select(curEntity)) singleUpdate(curEntity, relatedEntity, relatedEntity.nopks, connection);
      else singleInsert(curEntity, relatedEntity, relatedEntity.fields, connection);
    }    
  }  

/* ====================================================================== */
/* ====================================================================== */
  
  public boolean select(GenericEntity entity)
  {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null)
    {
      Debug.logError("[GenericDAO.select] Could not find ModelEntity record for entityName: " + entity.getEntityName());
      return false;
    }
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
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.select]: Unable to esablish a connection with the database... Error was:"); Debug.logWarning(sqle.getMessage()); }
    
    String sql = "SELECT ";
    if(modelEntity.nopks.size() > 0) sql = sql + modelEntity.colNameString(modelEntity.nopks, ", ", "");
    else sql = sql + "*";
    sql = sql + " FROM " + modelEntity.tableName + " WHERE " + modelEntity.colNameString(modelEntity.pks, "=? AND ", "=?") + "";
    //Debug.logInfo(" select: sql=" + sql);
    try {
      ps = connection.prepareStatement(sql);
      
      for(int i=0;i<modelEntity.pks.size();i++)
      {
        ModelField curField=(ModelField)modelEntity.pks.elementAt(i);
        //Debug.logInfo(" setting field " + curField.name + " to " + (i+1) + " entity: " + entity.toString()); 
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
        Debug.logWarning("ERROR [GenericDAO.select]: select failed, result set was empty for entity: " + entity.toString());
        return false;
      }
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [GenericDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      Debug.logWarning(sqle.getMessage());
      return false;
    } finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    return true;
  }

  public boolean partialSelect(GenericEntity entity, Set keys)
  {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null)
    {
      Debug.logError("[GenericDAO.delete] Could not find ModelEntity record for entityName: " + entity.getEntityName());
      return false;
    }
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
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.select]: Unable to esablish a connection with the database... Error was:"); Debug.logWarning(sqle.getMessage()); }
    
    //we don't want to select ALL fields, just the nonpk fields that are in the passed GenericEntity
    Vector partialFields = new Vector();
    for(int fi=0; fi<modelEntity.nopks.size(); fi++)
    {
      ModelField curField=(ModelField)modelEntity.nopks.elementAt(fi);
      if(keys.contains(curField.name)) partialFields.add(curField);
    }
    
    String sql = "SELECT " + modelEntity.colNameString(partialFields, ", ", "") + " FROM " + modelEntity.tableName + " WHERE " + modelEntity.colNameString(modelEntity.pks, "=? AND ", "=?");
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
        for(int j=0;j<partialFields.size();j++)
        {
          ModelField curField=(ModelField)partialFields.elementAt(j);
          getValue(rs, curField, entity);
        }

        entity.modified = false;
      } else {
        Debug.logWarning("ERROR [GenericDAO.select]: select failed, result set was empty.");
        return false;
      }
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [GenericDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      Debug.logWarning(sqle.getMessage());
      return false;
    } finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    return true;
  }

  public Collection selectByAnd(String entityName, Map fields, List orderBy)
  {
    if(entityName == null || fields == null) return null;
    Collection collection = new LinkedList();
    ModelEntity modelEntity = modelReader.getModelEntity(entityName);
    if(modelEntity == null) 
    {
      Debug.logError("[GenericDAO.selectByAnd] Could not find ModelEntity record for entityName: " + entityName);
      return null;
    }
    
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.selectByAnd]: Unable to esablish a connection with the database... Error was:" + sqle.toString() ); }
    
    //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
    Vector whereFields = new Vector();
    Vector selectFields = new Vector();
    if(fields != null || fields.size() > 0)
    {
      Set keys = fields.keySet();
      for(int fi=0; fi<modelEntity.fields.size(); fi++)
      {
        ModelField curField=(ModelField)modelEntity.fields.elementAt(fi);
        if(keys.contains(curField.name)) whereFields.add(curField);
        else selectFields.add(curField);
      }
    }
    else { selectFields = modelEntity.fields; }
    
    String sql = "SELECT " + modelEntity.colNameString(selectFields, ", ", "") + " FROM " + modelEntity.tableName;
    if(fields != null || fields.size() > 0) sql = sql + " WHERE " + modelEntity.colNameString(whereFields, "=? AND ", "=?");

    if(orderBy != null && orderBy.size() > 0)
    {
      Vector orderByStrings = new Vector();
      for(int fi=0; fi<modelEntity.fields.size(); fi++)
      {
        ModelField curField=(ModelField)modelEntity.fields.get(fi);

        for(int oi=0; oi<orderBy.size(); oi++)
        {          
          String keyName = (String)orderBy.get(oi);
          int spaceIdx = keyName.indexOf(' ');
          if(spaceIdx > 0) keyName = keyName.substring(0, spaceIdx);
          if(curField.equals(keyName)) orderByStrings.add(curField.colName + keyName.substring(spaceIdx));
          else orderByStrings.add(curField.colName);
        }
      }
      
      if(orderByStrings.size() > 0)
      {
        sql = sql + " ORDER BY ";

        Iterator iter = orderByStrings.iterator();
        while(iter.hasNext())
        {
          String curString = (String)iter.next();
          sql = sql + curString;
          if(iter.hasNext()) sql = sql + ", ";
        }
      }
    }
    
    try {
      ps = connection.prepareStatement(sql);
      
      GenericValue dummyValue;
      if(fields != null || fields.size() > 0)
      {
        dummyValue = new GenericValue(modelReader.getModelEntity(entityName), fields);
        for(int i=0;i<whereFields.size();i++)
        {
          ModelField curField=(ModelField)whereFields.elementAt(i);
          setValue(ps, i+1, curField, dummyValue);
        }
      }
      else dummyValue = new GenericValue(modelReader.getModelEntity(entityName));
      rs = ps.executeQuery();
      
      while(rs.next())
      {        
        GenericValue value = new GenericValue(dummyValue);
      
        for(int j=0;j<selectFields.size();j++)
        {
          ModelField curField=(ModelField)selectFields.elementAt(j);
          getValue(rs, curField, value);
        }

        value.modified = false;
        collection.add(value);
      }
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [GenericDAO.selectByAnd]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      sqle.printStackTrace();
      return null;
    } finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    return collection;
  }
  
  public Collection selectRelated(String relationName, GenericEntity value)
  {
    ModelEntity modelEntity = value.getModelEntity();
    ModelRelation relation = modelEntity.getRelation(relationName);
    ModelEntity relatedEntity = modelReader.getModelEntity(relation.relEntityName);

    Map fields = new HashMap();
    for(int i=0; i<relation.keyMaps.size(); i++)
    {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(i);
      fields.put(keyMap.relFieldName, value.get(keyMap.fieldName));
    }

    return selectByAnd(relatedEntity.entityName, fields, null);
  }

/* ====================================================================== */
/* ====================================================================== */

  public boolean delete(GenericEntity entity)
  {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null)
    {
      Debug.logError("[GenericDAO.delete] Could not find ModelEntity record for entityName: " + entity.getEntityName());
      return false;
    }
/*
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("ERROR [GenericDAO.delete]: Cannot delete GenericEntity: required primary key field(s) missing.");
      return false;
    }
*/    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.delete]: Unable to esablish a connection with the database... Error was:"); Debug.logWarning(sqle.getMessage()); }
    
    String sql = "DELETE FROM " + modelEntity.tableName + " WHERE " + modelEntity.colNameString(modelEntity.pks, "=? AND ", "=?");
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
      Debug.logWarning("ERROR [GenericDAO.delete]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      Debug.logWarning(sqle.getMessage());
      return false;
    } finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }    
    return true;
  }

  public boolean deleteByAnd(String entityName, Map fields)
  {
    if(entityName == null || fields == null) return false;
    ModelEntity modelEntity = modelReader.getModelEntity(entityName);
    if(modelEntity == null) 
    {
      Debug.logError("[GenericDAO.selectByAnd] Could not find ModelEntity record for entityName: " + entityName);
      return false;
    }
    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [GenericDAO.selectByAnd]: Unable to esablish a connection with the database... Error was:" + sqle.toString() ); }
    
    //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
    Vector whereFields = new Vector();
    if(fields != null || fields.size() > 0)
    {
      Set keys = fields.keySet();
      for(int fi=0; fi<modelEntity.fields.size(); fi++)
      {
        ModelField curField=(ModelField)modelEntity.fields.elementAt(fi);
        if(keys.contains(curField.name)) whereFields.add(curField);
      }
    }
    
    String sql = "DELETE FROM " + modelEntity.tableName;
    if(fields != null || fields.size() > 0) sql = sql + " WHERE " + modelEntity.colNameString(whereFields, "=? AND ", "=?");
    
    try {
      ps = connection.prepareStatement(sql);
      
      if(fields != null || fields.size() > 0)
      {
        GenericValue dummyValue = new GenericValue(modelReader.getModelEntity(entityName), fields);
        for(int i=0;i<whereFields.size();i++)
        {
          ModelField curField=(ModelField)whereFields.elementAt(i);
          setValue(ps, i+1, curField, dummyValue);
        }
      }
      ps.executeUpdate();      
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [GenericDAO.selectByAnd]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      sqle.printStackTrace();
      return false;
    } finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    return true;
  }
  
  public boolean deleteRelated(String relationName, GenericEntity value)
  {
    ModelEntity modelEntity = value.getModelEntity();
    ModelRelation relation = modelEntity.getRelation(relationName);
    ModelEntity relatedEntity = modelReader.getModelEntity(relation.relEntityName);

    Map fields = new HashMap();
    for(int i=0; i<relation.keyMaps.size(); i++)
    {
      ModelKeyMap keyMap = (ModelKeyMap)relation.keyMaps.get(i);
      fields.put(keyMap.relFieldName, value.get(keyMap.fieldName));
    }

    return deleteByAnd(relatedEntity.entityName, fields);
  }  
    
/* ====================================================================== */
/* ====================================================================== */

  public void getValue(ResultSet rs, ModelField curField, GenericEntity entity) throws SQLException
  {
    ModelFieldType mft = modelReader.getModelFieldType(curField.type);
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

  public void setValue(PreparedStatement ps, int ind, ModelField curField, GenericEntity entity) throws SQLException
  {
    Object field = entity.get(curField.name);
    if(field == null)
    {
      ps.setNull(ind, Types.NULL);
      return;
    }
    
    Class fieldClass = field.getClass();    
    String fieldType = fieldClass.getName();
    ModelFieldType mft = modelReader.getModelFieldType(curField.type);
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
