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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href='mailto:chris_maurer@altavista.com'>Chris Maurer</a>
 *@created    Wed Aug 08 2001
 *@version    1.0
 */
public class GenericDAO {
  static Map genericDAOs = new Hashtable();
  String helperName;
  ModelFieldTypeReader modelFieldTypeReader = null;
  
  public static GenericDAO getGenericDAO(String helperName) {
    GenericDAO newGenericDAO = (GenericDAO)genericDAOs.get(helperName);
    if(newGenericDAO == null) //don't want to block here
    {
      synchronized(GenericDAO.class) {
        newGenericDAO = (GenericDAO)genericDAOs.get(helperName);
        if(newGenericDAO == null) {
          newGenericDAO = new GenericDAO(helperName);
          genericDAOs.put(helperName, newGenericDAO);
        }
      }
    }
    return newGenericDAO;
  }
  
  public GenericDAO(String helperName) {
    this.helperName = helperName;
    modelFieldTypeReader = ModelFieldTypeReader.getModelFieldTypeReader(helperName);
  }
  
  public Connection getConnection() throws SQLException { return ConnectionFactory.getConnection(helperName); }
  
  public void insert(GenericEntity entity) throws GenericEntityException {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null) {
      throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
    }
/*
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("[GenericDAO.insert]: Cannot insert GenericEntity: required primary key field(s) missing.");
      return false;
    }
 */
    boolean useTX = true;
    Connection connection = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) {
      throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
    }
    
    
    try { connection.setAutoCommit(false); }
    catch(SQLException sqle) { useTX = false; }
    
    try {
      singleInsert(entity, modelEntity, modelEntity.fields, connection);
      storeAllOther(entity, connection);
      if(useTX) connection.commit();
    }
    catch (SQLException sqle) {
      //Debug.logWarning("[GenericDAO.insert]: SQL Exception while executing insert. Error was:");
      //Debug.logWarning(sqle.getMessage());
      
      try { if(useTX) connection.rollback(); }
      catch(SQLException sqle2) { Debug.logWarning("[GenericDAO.insert]: SQL Exception while rolling back insert. Error was:"); Debug.logWarning(sqle2); }
      throw new GenericDataSourceException("SQL Exception occured on insert or storeAllOther", sqle);
    }
    finally {
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
    }
  }
  
  private void singleInsert(GenericEntity entity, ModelEntity modelEntity, Vector fieldsToSave, Connection connection) throws SQLException, GenericEntityException {
    PreparedStatement ps = null;
    String sql = "INSERT INTO " + modelEntity.tableName + " (" + modelEntity.colNameString(fieldsToSave) + ") VALUES (" + modelEntity.fieldsStringList(fieldsToSave, "?", ", ") + ")";
    ps = connection.prepareStatement(sql);
    
    for(int i=0;i<fieldsToSave.size();i++) {
      ModelField curField=(ModelField)fieldsToSave.elementAt(i);
      setValue(ps, i+1, curField, entity);
    }
    
    ps.executeUpdate();
    entity.modified = false;
    try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
  }
  
  public void updateAll(GenericEntity entity) throws GenericEntityException {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null) {
      throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
    }
    
    customUpdate(entity, modelEntity, modelEntity.nopks);
  }
  
  public void update(GenericEntity entity) throws GenericEntityException {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null) {
      throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
    }
    //we don't want to update ALL fields, just the nonpk fields that are in the passed GenericEntity
    Vector partialFields = new Vector();
    Collection keys = entity.getAllKeys();
    for(int fi=0; fi<modelEntity.nopks.size(); fi++) {
      ModelField curField=(ModelField)modelEntity.nopks.elementAt(fi);
      if(keys.contains(curField.name)) partialFields.add(curField);
    }
    
    customUpdate(entity, modelEntity, partialFields);
  }
  
  private void customUpdate(GenericEntity entity, ModelEntity modelEntity, Vector fieldsToSave) throws GenericEntityException {
/*
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("[GenericDAO.update]: Cannot update GenericEntity: required primary key field(s) missing.");
      return false;
    }
 */
    boolean useTX = true;
    Connection connection = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) {
      throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
    }
    
    try { connection.setAutoCommit(false); }
    catch(SQLException sqle) { useTX = false; }
    
    try {
      singleUpdate(entity, modelEntity, fieldsToSave, connection);
      storeAllOther(entity, connection);
      if(useTX) connection.commit();
    }
    catch (SQLException sqle) {
      //Debug.logWarning("[GenericDAO.update]: SQL Exception while executing update. Error was:");
      //Debug.logWarning(sqle.getMessage());
      
      try { if(useTX) connection.rollback(); }
      catch(SQLException sqle2) { Debug.logWarning("[GenericDAO.insert]: SQL Exception while rolling back insert. Error was:"); Debug.logWarning(sqle2); }
      
      throw new GenericDataSourceException("SQL Exception occured in update or storeAllOther", sqle);
    }
    finally {
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
    }
  }
  
  private void singleUpdate(GenericEntity entity, ModelEntity modelEntity, Vector fieldsToSave, Connection connection) throws SQLException, GenericEntityException {
    String sql = "UPDATE " + modelEntity.tableName + " SET " + modelEntity.colNameString(fieldsToSave, "=?, ", "=?") + " WHERE " + makeWhereStringAnd(modelEntity.pks, entity);
    PreparedStatement ps = null;
    ps = connection.prepareStatement(sql);
    
    int i;
    for(i=0;i<fieldsToSave.size();i++) {
      ModelField curField=(ModelField)fieldsToSave.elementAt(i);
      setValue(ps, i+1, curField, entity);
    }
    for(int j=0;j<modelEntity.pks.size();j++) {
      ModelField curField=(ModelField)modelEntity.pks.elementAt(j);
      //for where clause variables only setValue if not null...
      if(entity.get(curField.name) != null) {
        setValue(ps, i+j+1, curField, entity);
      }
    }
    
    ps.executeUpdate();
    entity.modified = false;
    try { if (ps != null) ps.close(); } catch (SQLException sqle) { Debug.logWarning(sqle); }
  }
  
  private void storeAllOther(GenericEntity entity, Connection connection) throws SQLException, GenericEntityException {
    //also store valueObject.otherToStore entities
    if(entity.otherToStore != null && entity.otherToStore.size() > 0) {
      Iterator entities = entity.otherToStore.iterator();
      while(entities != null && entities.hasNext()) {
        GenericEntity curEntity = (GenericEntity)entities.next();
        singleStore(curEntity, connection);
      }
    }
  }
  
  /** Store the passed entity - insert if does not exist, otherwise update; then call storeAllOther to do a deep store */
  private void singleStore(GenericEntity entity, Connection connection) throws SQLException, GenericEntityException {
    GenericPK tempPK = entity.getPrimaryKey();
    try {
      select(tempPK);
    }
    catch(GenericEntityNotFoundException e) {
      //select failed, does not exist, insert
      singleInsert(entity, entity.getModelEntity(), entity.getModelEntity().fields, connection);
      storeAllOther(entity, connection);
      return;
    }
    //select did not fail, so exists, update
    singleUpdate(entity, entity.getModelEntity(), entity.getModelEntity().nopks, connection);
    storeAllOther(entity, connection);
  }
  
/* ====================================================================== */
/* ====================================================================== */
  
  public void storeAll(Collection entities) throws GenericEntityException {
    if(entities == null || entities.size() <= 0) { return; }
    
    boolean useTX = true;
    Connection connection = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) {
      throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
    }
    
    try { connection.setAutoCommit(false); }
    catch(SQLException sqle) { useTX = false; }
    
    try {
      Iterator entityIter = entities.iterator();
      while(entityIter != null && entityIter.hasNext()) {
        GenericEntity curEntity = (GenericEntity)entityIter.next();
        singleStore(curEntity, connection);
      }
      if(useTX) connection.commit();
    }
    catch(SQLException sqle) {
      //Debug.logWarning("[GenericDAO.insert]: SQL Exception while executing insert. Error was:");
      //Debug.logWarning(sqle.getMessage());
      
      try { if(useTX) connection.rollback(); }
      catch(SQLException sqle2) {
        Debug.logWarning("[GenericDAO.insert]: SQL Exception while rolling back insert. Error was:");
        Debug.logWarning(sqle2);
      }
      throw new GenericDataSourceException("SQL Exception occured in storeAll", sqle);
    }
    finally {
      try { if(connection != null) connection.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
    }
  }
  
/* ====================================================================== */
/* ====================================================================== */
  
  public void select(GenericEntity entity) throws GenericEntityException {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null) {
      throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
    }
/*
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("[GenericDAO.select]: Cannot select GenericEntity: required primary key field(s) missing.");
      return false;
    }
 */
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) {
      throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
    }
    
    String sql = "SELECT ";
    if(modelEntity.nopks.size() > 0) sql += modelEntity.colNameString(modelEntity.nopks, ", ", "");
    else sql += "*";
    sql += " FROM " + modelEntity.tableName + " WHERE " + makeWhereStringAnd(modelEntity.pks, entity);
    //Debug.logInfo(" select: sql=" + sql);
    try {
      ps = connection.prepareStatement(sql);
      
      for(int i=0;i<modelEntity.pks.size();i++) {
        ModelField curField=(ModelField)modelEntity.pks.elementAt(i);
        //for where clause variables only setValue if not null...
        if(entity.get(curField.name) != null) {
          //Debug.logInfo(" setting field " + curField.name + " to " + (i+1) + " entity: " + entity.toString());
          setValue(ps, i+1, curField, entity);
        }
      }
      
      rs = ps.executeQuery();
      
      if(rs.next()) {
        for(int j=0;j<modelEntity.nopks.size();j++) {
          ModelField curField=(ModelField)modelEntity.nopks.elementAt(j);
          getValue(rs, curField, entity);
        }
        
        entity.modified = false;
      } else {
        //Debug.logWarning("[GenericDAO.select]: select failed, result set was empty for entity: " + entity.toString());
        throw new GenericEntityNotFoundException("Result set was empty for entity: " + entity.toString());
      }
    }
    catch(SQLException sqle) {
      //Debug.logWarning("[GenericDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      //Debug.logWarning(sqle.getMessage());
      throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
    }
    finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
    }
  }
  
  public void partialSelect(GenericEntity entity, Set keys) throws GenericEntityException {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null) {
      throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
    }
/*
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("[GenericDAO.select]: Cannot select GenericEntity: required primary key field(s) missing.");
      return false;
    }
 */
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) {
      throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
    }
    
    //we don't want to select ALL fields, just the nonpk fields that are in the passed GenericEntity
    Vector partialFields = new Vector();
    for(int fi=0; fi<modelEntity.nopks.size(); fi++) {
      ModelField curField=(ModelField)modelEntity.nopks.elementAt(fi);
      if(keys.contains(curField.name)) partialFields.add(curField);
    }
    
    String sql = "SELECT ";
    if(partialFields.size() > 0) sql += modelEntity.colNameString(partialFields, ", ", "");
    else sql += "*";
    sql += " FROM " + modelEntity.tableName + " WHERE " + makeWhereStringAnd(modelEntity.pks, entity);
    
    try {
      ps = connection.prepareStatement(sql);
      
      for(int i=0;i<modelEntity.pks.size();i++) {
        ModelField curField=(ModelField)modelEntity.pks.elementAt(i);
        //for where clause variables only setValue if not null...
        if(entity.get(curField.name) != null) {
          setValue(ps, i+1, curField, entity);
        }
      }
      
      rs = ps.executeQuery();
      
      if(rs.next()) {
        for(int j=0;j<partialFields.size();j++) {
          ModelField curField=(ModelField)partialFields.elementAt(j);
          getValue(rs, curField, entity);
        }
        
        entity.modified = false;
      } else {
        //Debug.logWarning("[GenericDAO.select]: select failed, result set was empty.");
        throw new GenericEntityNotFoundException("Result set was empty for entity: " + entity.toString());
      }
    }
    catch (SQLException sqle) {
      //Debug.logWarning("[GenericDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      //Debug.logWarning(sqle.getMessage());
      throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
    }
    finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
    }
  }
  
  public Collection selectByAnd(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
    if(modelEntity == null) return null;
    Collection collection = new LinkedList();
    
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) {
      throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
    }
    
    //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
    Vector whereFields = new Vector();
    Vector selectFields = new Vector();
    if(fields != null && fields.size() > 0) {
      Set keys = fields.keySet();
      for(int fi=0; fi<modelEntity.fields.size(); fi++) {
        ModelField curField=(ModelField)modelEntity.fields.elementAt(fi);
        if(keys.contains(curField.name)) whereFields.add(curField);
        else selectFields.add(curField);
      }
    }
    else { selectFields = modelEntity.fields; }
    
    GenericValue dummyValue;
    if(fields != null && fields.size() > 0) dummyValue = new GenericValue(modelEntity, fields);
    else dummyValue = new GenericValue(modelEntity);
    
    String sql = "SELECT ";
    if(selectFields.size() > 0) sql = sql + modelEntity.colNameString(selectFields, ", ", "");
    else sql = sql + "*";
    sql += " FROM " + modelEntity.tableName;
    if(fields != null && fields.size() > 0) sql = sql + " WHERE " + makeWhereStringAnd(whereFields, dummyValue);
    
    if(orderBy != null && orderBy.size() > 0) {
      Vector orderByStrings = new Vector();
      for(int fi=0; fi<modelEntity.fields.size(); fi++) {
        ModelField curField=(ModelField)modelEntity.fields.get(fi);
        
        for(int oi=0; oi<orderBy.size(); oi++) {
          String keyName = (String)orderBy.get(oi);
          int spaceIdx = keyName.indexOf(' ');
          if(spaceIdx > 0) keyName = keyName.substring(0, spaceIdx);
          if(curField.equals(keyName)) orderByStrings.add(curField.colName + keyName.substring(spaceIdx));
          else orderByStrings.add(curField.colName);
        }
      }
      
      if(orderByStrings.size() > 0) {
        sql = sql + " ORDER BY ";
        
        Iterator iter = orderByStrings.iterator();
        while(iter.hasNext()) {
          String curString = (String)iter.next();
          sql = sql + curString;
          if(iter.hasNext()) sql = sql + ", ";
        }
      }
    }
    
    //Debug.logInfo("[GenericDAO.selectByAnd] sql=" + sql);
    try {
      ps = connection.prepareStatement(sql);
      
      if(fields != null && fields.size() > 0) {
        for(int i=0;i<whereFields.size();i++) {
          ModelField curField=(ModelField)whereFields.elementAt(i);
          //for where clause variables only setValue if not null...
          if(dummyValue.get(curField.name) != null) {
            setValue(ps, i+1, curField, dummyValue);
          }
        }
      }
      //Debug.logInfo("[GenericDAO.selectByAnd] ps=" + ps.toString());
      rs = ps.executeQuery();
      
      while(rs.next()) {
        GenericValue value = new GenericValue(dummyValue);
        
        for(int j=0;j<selectFields.size();j++) {
          ModelField curField=(ModelField)selectFields.elementAt(j);
          getValue(rs, curField, value);
        }
        
        value.modified = false;
        collection.add(value);
      }
    }
    catch (SQLException sqle) {
      //Debug.logWarning("[GenericDAO.selectByAnd]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      //sqle.printStackTrace();
      throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
    }
    finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
    }
    return collection;
  }
  
  public Collection selectByAnd(ModelEntity modelEntity, List fields, List intraFieldOperations, List values, List orderBy) throws GenericEntityException {
    if(modelEntity == null) return null;
    if(intraFieldOperations == null) return null;
    Collection collection = new LinkedList();
    
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) {
      throw new GenericDataSourceException("Unable to establish a connection with the database.", sqle);
    }
    
    //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
    Vector selectFields = modelEntity.fields;
    
    Vector whereFields = new Vector();
    Vector modelEntityFields = modelEntity.fields;
    if(fields != null && fields.size() > 0) {
      for(int fi=0; fi<fields.size(); fi++) {
        if(modelEntity.getField((String)fields.get(fi)) != null )
          whereFields.add((ModelField)modelEntity.getField((String)fields.get(fi)));
      }
    }
    
    String sql = "SELECT ";
    if(selectFields.size() > 0) sql = sql + modelEntity.colNameString(selectFields, ", ", "");
    else sql = sql + "*";
    sql += " FROM " + modelEntity.tableName;
    sql = sql + " WHERE ";
    
    if(whereFields != null && whereFields.size() > 0) {
      int i = 0;
      for(; i < whereFields.size() - 1; i++) {
        sql = sql + ((ModelField)whereFields.elementAt(i)).colName + ((EntityOperator)intraFieldOperations.get(i)).getCode() + " ? AND ";
      }
      sql = sql + ((ModelField)whereFields.elementAt(i)).colName + ((EntityOperator)intraFieldOperations.get(i)).getCode() + " ? ";
    }
    
    if(orderBy != null && orderBy.size() > 0) {
      Vector orderByStrings = new Vector();
      for(int fi=0; fi<modelEntity.fields.size(); fi++) {
        ModelField curField=(ModelField)modelEntity.fields.get(fi);
        
        for(int oi=0; oi<orderBy.size(); oi++) {
          String keyName = (String)orderBy.get(oi);
          int spaceIdx = keyName.indexOf(' ');
          if(spaceIdx > 0) keyName = keyName.substring(0, spaceIdx);
          if(curField.equals(keyName)) orderByStrings.add(curField.colName + keyName.substring(spaceIdx));
          else orderByStrings.add(curField.colName);
        }
      }
      
      if(orderByStrings.size() > 0) {
        sql = sql + " ORDER BY ";
        
        Iterator iter = orderByStrings.iterator();
        while(iter.hasNext()) {
          String curString = (String)iter.next();
          sql = sql + curString;
          if(iter.hasNext()) sql = sql + ", ";
        }
      }
    }
    
    try {
      ps = connection.prepareStatement(sql);
      GenericValue dummyValue = null;
      if(values != null && values.size() > 0 ) {
        for(int i=0;i<whereFields.size();i++) {
          dummyValue = new GenericValue(modelEntity);
          String fieldName = (String)fields.get(i);
          dummyValue.set(fieldName, values.get(i));
          setValue(ps, i+1, ((ModelField)modelEntity.getField(fieldName)), dummyValue);
        }
      }
      else dummyValue = new GenericValue(modelEntity);
      rs = ps.executeQuery();
      
      while(rs.next()) {
        GenericValue value = new GenericValue(dummyValue);
        
        for(int j=0;j<selectFields.size();j++) {
          ModelField curField=(ModelField)selectFields.elementAt(j);
          getValue(rs, curField, value);
        }
        
        value.modified = false;
        collection.add(value);
      }
    }
    catch (SQLException sqle) {
      //Debug.logWarning("[GenericDAO.selectByAnd]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      //sqle.printStackTrace();
      throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
    }
    finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
    }
    return collection;
  }
  
  public Collection selectByLike(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
    if(modelEntity == null) return null;
    Collection collection = new LinkedList();
    
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) {
      throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
    }
    
    //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
    Vector whereFields = new Vector();
    Vector selectFields = new Vector();
    if(fields != null && fields.size() > 0) {
      Set keys = fields.keySet();
      for(int fi=0; fi<modelEntity.fields.size(); fi++) {
        ModelField curField=(ModelField)modelEntity.fields.elementAt(fi);
        if(keys.contains(curField.name)){
          whereFields.add(curField);
          selectFields.add(curField);
        }
        else selectFields.add(curField);
      }
    }
    else { selectFields = modelEntity.fields; }
    
    String sql = "SELECT ";
    if(selectFields.size() > 0) sql = sql + modelEntity.colNameString(selectFields, ", ", "");
    else sql = sql + "*";
    sql += " FROM " + modelEntity.tableName;
    if(fields != null && fields.size() > 0) sql = sql + " WHERE " + modelEntity.colNameString(whereFields, " LIKE ? AND ", " LIKE ?");
    
    if(orderBy != null && orderBy.size() > 0) {
      Vector orderByStrings = new Vector();
      for(int fi=0; fi<modelEntity.fields.size(); fi++) {
        ModelField curField=(ModelField)modelEntity.fields.get(fi);
        
        for(int oi=0; oi<orderBy.size(); oi++) {
          String keyName = (String)orderBy.get(oi);
          int spaceIdx = keyName.indexOf(' ');
          if(spaceIdx > 0) keyName = keyName.substring(0, spaceIdx);
          if(curField.equals(keyName)) orderByStrings.add(curField.colName + keyName.substring(spaceIdx));
          else orderByStrings.add(curField.colName);
        }
      }
      
      if(orderByStrings.size() > 0) {
        sql = sql + " ORDER BY ";
        
        Iterator iter = orderByStrings.iterator();
        while(iter.hasNext()) {
          String curString = (String)iter.next();
          sql = sql + curString;
          if(iter.hasNext()) sql = sql + ", ";
        }
      }
    }
    
    try {
      ps = connection.prepareStatement(sql);
      
      GenericValue dummyValue;
      if(fields != null && fields.size() > 0) {
        dummyValue = new GenericValue(modelEntity, fields);
        for(int i=0;i<whereFields.size();i++) {
          ModelField curField=(ModelField)whereFields.elementAt(i);
          setValue(ps, i+1, curField, dummyValue);
        }
      }
      else dummyValue = new GenericValue(modelEntity);
      rs = ps.executeQuery();
      
      while(rs.next()) {
        GenericValue value = new GenericValue(dummyValue);
        for(int j=0;j<selectFields.size();j++) {
          ModelField curField=(ModelField)selectFields.elementAt(j);
          getValue(rs, curField, value);
        }
        value.modified = false;
        collection.add(value);
      }
    }
    catch (SQLException sqle) {
      //Debug.logWarning("[GenericDAO.selectByLike]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      //sqle.printStackTrace();
      throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
    }
    finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
    }
    return collection;
  }
  
  public Collection selectByClause(ModelEntity modelEntity, List entityClauses, Map fields, List orderBy) throws GenericEntityException {
    if(entityClauses == null) return null;
    
    Collection collection = new LinkedList();
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    
    ModelEntity firstModelEntity = null;
    ModelEntity secondModelEntity = null;
    
    ModelField firstModelField = null;
    ModelField secondModelField = null;
    
    StringBuffer select = new StringBuffer(" SELECT ");
    StringBuffer from = new StringBuffer(" FROM ");
    StringBuffer where = new StringBuffer(" WHERE ");
    StringBuffer order = new StringBuffer();
    
    String test = "";
    
    Vector whereTables = new Vector();
    
    try { connection = getConnection(); }
    catch (SQLException sqle) {
      throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
    }
    
    //each iteration defines one relationship for the query.
    for(int i=0;i<entityClauses.size();i++){
      EntityClause entityClause = (EntityClause)entityClauses.get(i);
      String interFieldOperation = entityClause.getInterFieldOperation().getCode();
      String intraFieldOperation = entityClause.getIntraFieldOperation().getCode();
      
      firstModelEntity = entityClause.getFirstModelEntity();
      if(!whereTables.contains(firstModelEntity.tableName)){
        whereTables.add(firstModelEntity.tableName);
      }
      
      secondModelEntity = entityClause.getSecondModelEntity();
      if(!whereTables.contains(secondModelEntity.tableName)){
        whereTables.add(secondModelEntity.tableName);
      }
      
      firstModelField = firstModelEntity.getField(entityClause.getFirstField());
      secondModelField = secondModelEntity.getField(entityClause.getSecondField());
      
      test = where.toString();
      if(i > 0) where.append(interFieldOperation);
      where.append(firstModelEntity.tableName + "." + firstModelField.colName + " " + intraFieldOperation + " " + secondModelEntity.tableName + "." + secondModelField.colName);
    }
    int ix=0;
    for(;ix<whereTables.size()-1;ix++){
      from.append(whereTables.get(ix) + ", ");
    }
    from.append(whereTables.get(ix));
    
    Vector whereFields = new Vector();
    Vector selectFields = new Vector();
    if(fields != null && fields.size() > 0) {
      Set keys = fields.keySet();
      for(int fi=0; fi<modelEntity.fields.size(); fi++) {
        ModelField curField=(ModelField)modelEntity.fields.elementAt(fi);
        if(keys.contains(curField.name)){
          whereFields.add(curField);
          selectFields.add(curField);
        } else selectFields.add(curField);
      }
    }
    
    String tableNamePrefix = modelEntity.tableName + ".";
    if(fields != null && fields.size() > 0){
      test = where.toString();
      if(test.trim().length() > 0) where.append(" AND ");
      where.append(tableNamePrefix);
      where.append(modelEntity.colNameString(whereFields, "=? AND " + tableNamePrefix, "=?"));
    }
    
    select.append(tableNamePrefix);
    select.append(modelEntity.colNameString(selectFields, ", " + tableNamePrefix, ""));
    
    
    if(orderBy != null && orderBy.size() > 0) {
      Vector orderByStrings = new Vector();
      for(int fi=0; fi<modelEntity.fields.size(); fi++) {
        ModelField curField=(ModelField)modelEntity.fields.get(fi);
        
        for(int oi=0; oi<orderBy.size(); oi++) {
          String keyName = (String)orderBy.get(oi);
          int spaceIdx = keyName.indexOf(' ');
          if(spaceIdx > 0) keyName = keyName.substring(0, spaceIdx);
          if(curField.equals(keyName)) orderByStrings.add(modelEntity.tableName + "." + curField.colName + keyName.substring(spaceIdx));
          else orderByStrings.add(modelEntity.tableName + "." + curField.colName);
        }
      }
      
      if(orderByStrings.size() > 0) {
        order.append(" ORDER BY ");
        
        Iterator iter = orderByStrings.iterator();
        while(iter.hasNext()) {
          String curString = (String)iter.next();
          order.append(curString);
          if(iter.hasNext()) order.append(", ");
        }
      }
    }
    
    
    String sql = "";
    try {
      sql = select.toString() + " " + from.toString() + " " + where.toString() + (order.toString().trim().length() > 0 ? order.toString() : "");
      ps = connection.prepareStatement(sql);
      
      GenericValue dummyValue;
      if(fields != null && fields.size() > 0) {
        dummyValue = new GenericValue(modelEntity, fields);
        for(int i=0;i<whereFields.size();i++) {
          ModelField curField=(ModelField)whereFields.elementAt(i);
          setValue(ps, i+1, curField, dummyValue);
        }
      }
      else dummyValue = new GenericValue(modelEntity);
      rs = ps.executeQuery();
      
      while(rs.next()) {
        GenericValue value = new GenericValue(dummyValue);
        
        for(int j=0;j<selectFields.size();j++) {
          ModelField curField=(ModelField)selectFields.elementAt(j);
          getValue(rs, curField, value);
        }
        
        value.modified = false;
        collection.add(value);
      }
    }
    catch (SQLException sqle) {
      //Debug.logWarning("[GenericDAO.selectByAnd]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      //sqle.printStackTrace();
      throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
    }
    finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
    }
    
    return collection;
  }
  
/* ====================================================================== */
/* ====================================================================== */
  
  public void delete(GenericEntity entity) throws GenericEntityException {
    ModelEntity modelEntity = entity.getModelEntity();
    if(modelEntity == null) {
      throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
    }
/*
    if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
      Debug.logWarning("[GenericDAO.delete]: Cannot delete GenericEntity: required primary key field(s) missing.");
      return false;
    }
 */
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) {
      throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
    }
    
    String sql = "DELETE FROM " + modelEntity.tableName + " WHERE " + makeWhereStringAnd(modelEntity.pks, entity);
    try {
      connection.setAutoCommit(true);
      ps = connection.prepareStatement(sql);
      
      for(int i=0;i<modelEntity.pks.size();i++) {
        ModelField curField=(ModelField)modelEntity.pks.elementAt(i);
        //for where clause variables only setValue if not null...
        if(entity.get(curField.name) != null) {
          setValue(ps, i+1, curField, entity);
        }
      }
      
      ps.executeUpdate();
      entity.modified = true;
    }
    catch (SQLException sqle) {
      //Debug.logWarning("[GenericDAO.delete]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      //Debug.logWarning(sqle.getMessage());
      throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
    }
    finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
    }
  }
  
  public void deleteByAnd(ModelEntity modelEntity, Map fields) throws GenericEntityException {
    if(modelEntity == null || fields == null) return;
    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) {
      throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
    }
    
    //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
    Vector whereFields = new Vector();
    if(fields != null || fields.size() > 0) {
      Set keys = fields.keySet();
      for(int fi=0; fi<modelEntity.fields.size(); fi++) {
        ModelField curField=(ModelField)modelEntity.fields.elementAt(fi);
        if(keys.contains(curField.name)) whereFields.add(curField);
      }
    }
    
    GenericValue dummyValue = new GenericValue(modelEntity, fields);
    String sql = "DELETE FROM " + modelEntity.tableName;
    if(fields != null || fields.size() > 0) sql = sql + " WHERE " + makeWhereStringAnd(whereFields, dummyValue);
    
    try {
      connection.setAutoCommit(true);
      ps = connection.prepareStatement(sql);
      
      if(fields != null || fields.size() > 0) {
        for(int i=0;i<whereFields.size();i++) {
          ModelField curField=(ModelField)whereFields.elementAt(i);
          //for where clause variables only setValue if not null...
          if(dummyValue.get(curField.name) != null) {
            setValue(ps, i+1, curField, dummyValue);
          }
        }
      }
      ps.executeUpdate();
    }
    catch (SQLException sqle) {
      //Debug.logWarning("[GenericDAO.selectByAnd]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      //sqle.printStackTrace();
      throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
    }
    finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { Debug.logWarning(sqle.getMessage()); }
    }
  }
  
/* ====================================================================== */
/* ====================================================================== */
  
  /** Makes a WHERE clause String with "<col name>=?" if not null or "<col name> IS null" if null, all AND separated */
  public String makeWhereStringAnd(Vector modelFields, GenericEntity entity) {
    StringBuffer returnString = new StringBuffer("");
    if(modelFields.size() < 1) { return ""; }
    
    int i = 0;
    for(; i < modelFields.size() - 1; i++) {
      ModelField modelField = (ModelField)modelFields.elementAt(i);
      returnString.append(modelField.colName);
      if(entity.get(modelField.name) != null) returnString.append("=? AND ");
      else returnString.append(" IS NULL AND ");
    }
    ModelField modelField2 = (ModelField)modelFields.elementAt(i);
    returnString.append(modelField2.colName);
    if(entity.get(modelField2.name) != null) returnString.append("=?");
    else returnString.append(" IS NULL");
    return returnString.toString();
  }
  
  public void getValue(ResultSet rs, ModelField curField, GenericEntity entity) throws SQLException, GenericEntityException {
    ModelFieldType mft = modelFieldTypeReader.getModelFieldType(curField.type);
    if(mft == null) {
      throw new GenericModelException("GenericDAO.getValue: definition fieldType " + curField.type + " not found, cannot getValue for field " + entity.getEntityName() + "." + curField.name + ".");
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
    else if(fieldType.equals("java.lang.Integer") || fieldType.equals("Integer")) {
      if(rs.getObject(curField.colName) == null) entity.set(curField.name, null);
      else entity.set(curField.name, new Integer(rs.getInt(curField.colName)));
    }
    else if(fieldType.equals("java.lang.Long") || fieldType.equals("Long")) {
      if(rs.getObject(curField.colName) == null) entity.set(curField.name, null);
      else entity.set(curField.name, new Long(rs.getLong(curField.colName)));
    }
    else if(fieldType.equals("java.lang.Float") || fieldType.equals("Float")) {
      if(rs.getObject(curField.colName) == null) entity.set(curField.name, null);
      else entity.set(curField.name, new Float(rs.getFloat(curField.colName)));
    }
    else if(fieldType.equals("java.lang.Double") || fieldType.equals("Double")) {
      if(rs.getObject(curField.colName) == null) entity.set(curField.name, null);
      else entity.set(curField.name, new Double(rs.getDouble(curField.colName)));
    }
    else {
      throw new GenericNotImplementedException("Java type " + fieldType + " not currently supported. Sorry.");
    }
  }
  
  public void setValue(PreparedStatement ps, int ind, ModelField curField, GenericEntity entity) throws SQLException, GenericEntityException {
    Object field = entity.get(curField.name);
    //there should be no parameter for null fields, so we can just return and do nothing
    
    ModelFieldType mft = modelFieldTypeReader.getModelFieldType(curField.type);
    if(mft == null) {
      throw new GenericModelException("GenericDAO.getValue: definition fieldType " + curField.type + " not found, cannot setValue for field " + entity.getEntityName() + "." + curField.name + ".");
    }
    
    String fieldType = mft.javaType;
    if(field != null) {
      Class fieldClass = field.getClass();
      String fieldClassName = fieldClass.getName();
      if(!fieldClassName.equals(mft.javaType) && fieldClassName.indexOf(mft.javaType) < 0) {
        Debug.logWarning("GenericDAO.setValue: type of field " + entity.getEntityName() + "." + curField.name + " is " + fieldClassName + ", was expecting " + mft.javaType + "; this may indicate an error in the configuration or in the class, and may result in an SQL-Java data conversion error. Will use the real field type: " + fieldClassName + ", not the definition.");
        fieldType = fieldClassName;
      }
    }
    
    if(fieldType.equals("java.lang.String") || fieldType.equals("String")) {
      if(field != null) ps.setString(ind, (String)field);
      else ps.setNull(ind, Types.VARCHAR);
    }
    else if(fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp")) {
      if(field != null) ps.setTimestamp(ind, (java.sql.Timestamp)field);
      else ps.setNull(ind, Types.TIMESTAMP);
    }
    else if(fieldType.equals("java.sql.Time") || fieldType.equals("Time")) {
      if(field != null) ps.setTime(ind, (java.sql.Time)field);
      else ps.setNull(ind, Types.TIME);
    }
    else if(fieldType.equals("java.sql.Date") || fieldType.equals("Date")) {
      if(field != null) ps.setDate(ind, (java.sql.Date)field);
      else ps.setNull(ind, Types.DATE);
    }
    else if(fieldType.equals("java.lang.Integer") || fieldType.equals("Integer")) {
      if(field != null) ps.setInt(ind, ((java.lang.Integer)field).intValue());
      else ps.setNull(ind, Types.NUMERIC);
    }
    else if(fieldType.equals("java.lang.Long") || fieldType.equals("Long")) {
      if(field != null) ps.setLong(ind, ((java.lang.Long)field).longValue());
      else ps.setNull(ind, Types.NUMERIC);
    }
    else if(fieldType.equals("java.lang.Float") || fieldType.equals("Float")) {
      if(field != null) ps.setFloat(ind, ((java.lang.Float)field).floatValue());
      else ps.setNull(ind, Types.NUMERIC);
    }
    else if(fieldType.equals("java.lang.Double") || fieldType.equals("Double")) {
      if(field != null) ps.setDouble(ind, ((java.lang.Double)field).doubleValue());
      else ps.setNull(ind, Types.NUMERIC);
    }
    else {
      throw new GenericNotImplementedException("Java type " + fieldType + " not currently supported. Sorry.");
    }
  }
  
/* ====================================================================== */
/* ====================================================================== */
/*
  public boolean createTable(String entityName)
  {
    return createTable(modelReader.getModelEntity(entityName));
  }
 */
  public boolean createTable(ModelEntity entity) {
    if(entity == null) return false;
    
    Connection connection = null;
    Statement stmt = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) { Debug.logWarning("[GenericDAO.createTable]: Unable to esablish a connection with the database... Error was:"); Debug.logWarning(sqle.getMessage()); }
    
    String sql = "CREATE TABLE " + entity.tableName + " (";
    for(int i=0;i<entity.fields.size();i++) {
      ModelField field=(ModelField)entity.fields.get(i);
      ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.type);
      sql = sql + field.colName + " " + type.sqlType;
      if(field.isPk) sql = sql + " NOT NULL, ";
      else sql = sql + ", ";
    }
    String pkName = "PK_" + entity.tableName;
    if(pkName.length() > 30) pkName = pkName.substring(0,30);
    sql = sql + "CONSTRAINT " + pkName + " PRIMARY KEY (" + entity.colNameString(entity.pks) + "))";
    
    //Debug.logInfo(" create: sql=" + sql);
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate(sql);
    }
    catch (SQLException sqle) {
      Debug.logWarning("[GenericDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      Debug.logWarning(sqle.getMessage());
      return false;
    }
    finally {
      try { if (stmt != null) stmt.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    return true;
  }
/*
  public boolean addColumn(String entityName, String fieldName)
  {
    ModelEntity entity = modelReader.getModelEntity(entityName);
    if(entity == null) return false;
    return addColumn(entity, entity.getField(fieldName));
  }
 */
  public boolean addColumn(ModelEntity entity, ModelField field) {
    if(entity == null || field == null) return false;
    
    Connection connection = null;
    Statement stmt = null;
    try { connection = getConnection(); }
    catch (SQLException sqle) { Debug.logWarning("[GenericDAO.createTable]: Unable to esablish a connection with the database... Error was:"); Debug.logWarning(sqle.getMessage()); }
    
    ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.type);
    String sql = "ALTER TABLE " + entity.tableName + " ADD " + field.colName + " " + type.sqlType;
    
    //Debug.logInfo(" create: sql=" + sql);
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate(sql);
    }
    catch (SQLException sqle) {
      Debug.logWarning("[GenericDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:");
      Debug.logWarning(sqle.getMessage());
      return false;
    }
    finally {
      try { if (stmt != null) stmt.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    return true;
  }
  
/* ====================================================================== */
/* ====================================================================== */
  
  public void checkDb(Map modelEntities, Collection messages, boolean addMissing) {
    UtilTimer timer = new UtilTimer();
    timer.timerString("[GenericDAO.checkDb] Start - Before Get Database Meta Data");
    
    //get ALL tables from this database
    TreeSet tableNames = this.getTableNames(messages);
    timer.timerString("[GenericDAO.checkDb] After Get All Table Names");
    
    //get ALL column info, put into hashmap by table name
    Map colInfo = this.getColumnInfo(tableNames, messages);
    timer.timerString("[GenericDAO.checkDb] After Get All Column Info");
    
    //-make sure all entities have a corresponding table
    //-list all tables that do not have a corresponding entity
    //-display message if number of table columns does not match number of entity fields
    //-list all columns that do not have a corresponding field
    //make sure each corresponding column is of the correct type
    //-list all fields that do not have a corresponding column
    
    timer.timerString("[GenericDAO.checkDb] Before Individual Table/Column Check");
    
    TreeSet namesSet = new TreeSet(modelEntities.keySet());
    Iterator namesIter = namesSet.iterator();
    int curEnt = 0;
    int totalEnt = namesSet.size();
    while(namesIter != null && namesIter.hasNext()) {
      curEnt++;
      String entityName = (String)namesIter.next();
      ModelEntity entity = (ModelEntity)modelEntities.get(entityName);
      
      String entMessage = "(" + timer.timeSinceLast() + "ms) Checking #" + curEnt + "/" + totalEnt + " Entity " + entity.entityName + " with table " + entity.tableName;
      Debug.logInfo("[GenericDAO.checkDb] " + entMessage);
      if(messages != null) messages.add(entMessage);
      
      //-make sure all entities have a corresponding table
      if(tableNames.contains(entity.tableName.toUpperCase())) {
        tableNames.remove(entity.tableName.toUpperCase());
        
        if(colInfo != null) {
          Map fieldColNames = new HashMap();
          for(int fnum=0; fnum<entity.fields.size(); fnum++) {
            ModelField field = (ModelField)entity.fields.get(fnum);
            fieldColNames.put(field.colName.toUpperCase(), field);
          }
          
          List colList = (List)colInfo.get(entity.tableName.toUpperCase());
          int numCols=0;
          for(; numCols<colList.size(); numCols++) {
            ColumnCheckInfo ccInfo  = (ColumnCheckInfo)colList.get(numCols);
            //-list all columns that do not have a corresponding field
            if(fieldColNames.containsKey(ccInfo.columnName)) {
              ModelField field = null;
              field = (ModelField)fieldColNames.remove(ccInfo.columnName);
              ModelFieldType modelFieldType = modelFieldTypeReader.getModelFieldType(field.type);
              
              if(modelFieldType != null) {
                //make sure each corresponding column is of the correct type
                String fullTypeStr = modelFieldType.sqlType;
                String typeName;
                int columnSize = -1;
                int decimalDigits = -1;
                
                int openParen = fullTypeStr.indexOf('(');
                int closeParen = fullTypeStr.indexOf(')');
                int comma = fullTypeStr.indexOf(',');
                
                if(openParen > 0 && closeParen > 0 && closeParen > openParen) {
                  typeName = fullTypeStr.substring(0, openParen);
                  if(comma > 0 && comma > openParen && comma < closeParen) {
                    String csStr = fullTypeStr.substring(openParen+1, comma);
                    try { columnSize = Integer.parseInt(csStr); }
                    catch(NumberFormatException e) { Debug.logError(e); }
                    
                    String ddStr = fullTypeStr.substring(comma+1, closeParen);
                    try { decimalDigits = Integer.parseInt(ddStr); }
                    catch(NumberFormatException e) { Debug.logError(e); }
                  }
                  else {
                    String csStr = fullTypeStr.substring(openParen+1, closeParen);
                    try { columnSize = Integer.parseInt(csStr); }
                    catch(NumberFormatException e) { Debug.logError(e); }
                  }
                }
                else {
                  typeName = fullTypeStr;
                }
                
                if(!ccInfo.typeName.equals(typeName.toUpperCase())) {
                  String message = "WARNING: Column \"" + ccInfo.columnName + "\" of table \"" + entity.tableName + "\" of entity \"" + entity.entityName + "\" is of type \"" + ccInfo.typeName + "\" in the database, but is defined as type \"" + typeName + "\" in the entity definition.";
                  Debug.logError("[GenericDAO.checkDb] " + message);
                  if(messages != null) messages.add(message);
                }
                if(columnSize != -1 && columnSize != ccInfo.columnSize) {
                  String message = "WARNING: Column \"" + ccInfo.columnName + "\" of table \"" + entity.tableName + "\" of entity \"" + entity.entityName + "\" has a column size of \"" + ccInfo.columnSize + "\" in the database, but is defined to have a column size of \"" + columnSize + "\" in the entity definition.";
                  Debug.logError("[GenericDAO.checkDb] " + message);
                  if(messages != null) messages.add(message);
                }
                if(decimalDigits != -1 && decimalDigits != ccInfo.decimalDigits) {
                  String message = "WARNING: Column \"" + ccInfo.columnName + "\" of table \"" + entity.tableName + "\" of entity \"" + entity.entityName + "\" has a decimalDigits of \"" + ccInfo.decimalDigits + "\" in the database, but is defined to have a decimalDigits of \"" + decimalDigits + "\" in the entity definition.";
                  Debug.logError("[GenericDAO.checkDb] " + message);
                  if(messages != null) messages.add(message);
                }
              }
              else {
                String message = "Column \"" + ccInfo.columnName + "\" of table \"" + entity.tableName + "\" of entity \"" + entity.entityName + "\" has a field type name of \"" + field.type + "\" which is not found in the field type definitions";
                Debug.logError("[GenericDAO.checkDb] " + message);
                if(messages != null) messages.add(message);
              }
            }
            else {
              String message = "Column \"" + ccInfo.columnName + "\" of table \"" + entity.tableName + "\" of entity \"" + entity.entityName + "\" exists in the database but has no corresponding field";
              Debug.logError("[GenericDAO.checkDb] " + message);
              if(messages != null) messages.add(message);
            }
          }
          
          //-display message if number of table columns does not match number of entity fields
          if(numCols != entity.fields.size()) {
            String message = "Entity \"" + entity.entityName + "\" has " + entity.fields.size() + " fields but table \"" + entity.tableName + "\" has " + numCols + " columns.";
            Debug.logWarning("[GenericDAO.checkDb] " + message);
            if(messages != null) messages.add(message);
          }
          
          //-list all fields that do not have a corresponding column
          Iterator fcnIter = fieldColNames.keySet().iterator();
          while(fcnIter.hasNext()) {
            String colName = (String)fcnIter.next();
            ModelField field = (ModelField)fieldColNames.get(colName);
            String message = "Field \"" + field.name + "\" of entity \"" + entity.entityName + "\" is missing its corresponding column \"" + field.colName + "\"";
            Debug.logError("[GenericDAO.checkDb] " + message);
            if(messages != null) messages.add(message);
            
            if(addMissing) {
              //add the column
              if(addColumn(entity, field)) message = "Added column \"" + field.colName + "\" to table \"" + entity.tableName + "\"";
              else message = "Could not add column \"" + field.colName + "\" to table \"" + entity.tableName + "\"";
              Debug.logError("[GenericDAO.checkDb] " + message);
              if(messages != null) messages.add(message);
            }
          }
        }
      }
      else {
        String message = "Entity \"" + entity.entityName + "\" with tableName \"" + entity.tableName + "\" has no corresponding table in the database";
        Debug.logError("[GenericDAO.checkDb] " + message);
        if(messages != null) messages.add(message);
        
        if(addMissing) {
          //create the table
          if(createTable(entity)) message = "Created table \"" + entity.tableName + "\"";
          else message = "Could not create table \"" + entity.tableName + "\"";
          Debug.logError("[GenericDAO.checkDb] " + message);
          if(messages != null) messages.add(message);
        }
      }
    }
    
    timer.timerString("[GenericDAO.checkDb] After Individual Table/Column Check");
    
    //-list all tables that do not have a corresponding entity
    Iterator tableNamesIter = tableNames.iterator();
    while(tableNamesIter != null && tableNamesIter.hasNext()) {
      String tableName = (String)tableNamesIter.next();
      String message = "Table named \"" + tableName + "\" exists in the database but has no corresponding entity";
      Debug.logWarning("[GenericDAO.checkDb] " + message);
      if(messages != null) messages.add(message);
    }
    
    timer.timerString("[GenericDAO.checkDb] Finished Checking Entity Database");
  }
  
  /** Creates a list of ModelEntity objects based on meta data from the database */
  public List induceModelFromDb(Collection messages) {
    //get ALL tables from this database
    TreeSet tableNames = this.getTableNames(messages);
    
    //get ALL column info, put into hashmap by table name
    Map colInfo = this.getColumnInfo(tableNames, messages);
    
    //go through each table and make a ModelEntity object, add to list
    //for each entity make corresponding ModelField objects
    //then print out XML for the entities/fields
    List newEntList = new LinkedList();
    
    //iterate over the table names is alphabetical order
    Iterator tableNamesIter = new TreeSet(colInfo.keySet()).iterator();
    while(tableNamesIter.hasNext()) {
      String tableName = (String)tableNamesIter.next();
      Vector colList = (Vector)colInfo.get(tableName);
      
      ModelEntity newEntity = new ModelEntity();
      newEntity.tableName = tableName.toUpperCase();
      newEntity.entityName = ModelUtil.dbNameToClassName(newEntity.tableName);
      newEntList.add(newEntity);
      
      Iterator columns = colList.iterator();
      while(columns.hasNext()) {
        ColumnCheckInfo ccInfo = (ColumnCheckInfo)columns.next();
        ModelField newField = new ModelField();
        newEntity.fields.add(newField);
        newField.colName = ccInfo.columnName.toUpperCase();
        newField.name =  ModelUtil.dbNameToVarName(newField.colName);
        
        //figure out the type according to the typeName, columnSize and decimalDigits
        newField.type = ModelUtil.induceFieldType(ccInfo.typeName, ccInfo.columnSize, ccInfo.decimalDigits, this.modelFieldTypeReader);
        
        //how do we find out if it is a primary key? for now, if not nullable, assume it is a pk
        //this is a bad assumption, but since this output must be edited by hand later anyway, oh well
        if("NO".equals(ccInfo.isNullable)) newField.isPk = true;
        else newField.isPk = false;
      }
      newEntity.updatePkLists();
    }
    
    return newEntList;
  }
  
  public TreeSet getTableNames(Collection messages) {
    Connection connection = null;
    try { connection = getConnection(); }
    catch(SQLException sqle) {
      String message = "Unable to esablish a connection with the database... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.getTableNames] " + message);
      if(messages != null) messages.add(message);
      return null;
    }
    
    DatabaseMetaData dbData = null;
    try { dbData = connection.getMetaData(); }
    catch(SQLException sqle) {
      String message = "Unable to get database meta data... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.getTableNames] " + message);
      if(messages != null) messages.add(message);
      return null;
    }
    
    try {
      Debug.logInfo("[GenericDAO.getTableNames] Database Product Name is " + dbData.getDatabaseProductName());
      Debug.logInfo("[GenericDAO.getTableNames] Database Product Version is " + dbData.getDatabaseProductVersion());
    }
    catch(SQLException sqle) {
      Debug.logInfo("[GenericDAO.getTableNames] Unable to get Database name & version information");
    }
    try {
      Debug.logInfo("[GenericDAO.getTableNames] Database Driver Name is " + dbData.getDriverName());
      Debug.logInfo("[GenericDAO.getTableNames] Database Driver Version is " + dbData.getDriverVersion());
    }
    catch(SQLException sqle) {
      Debug.logInfo("[GenericDAO.getTableNames] Unable to get Driver name & version information");
    }
    
    //get ALL tables from this database
    TreeSet tableNames = new TreeSet();
    ResultSet tableSet = null;
    try { tableSet = dbData.getTables(null, dbData.getUserName(), null, null); }
    catch(SQLException sqle) {
      String message = "Unable to get list of table information... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.getTableNames] " + message);
      if(messages != null) messages.add(message);
      
      try{ connection.close(); }
      catch(SQLException sqle2) {
        String message2 = "Unable to close database connection, continuing anyway... Error was:" + sqle2.toString();
        Debug.logError("[GenericDAO.getTableNames] " + message2);
        if(messages != null) messages.add(message2);
      }
      return null;
    }
    
    try {
      while(tableSet.next()) {
        try {
          String tableName = tableSet.getString("TABLE_NAME");
          tableName = (tableName == null)?null:tableName.toUpperCase();
          String tableType = tableSet.getString("TABLE_TYPE");
          tableType = (tableType == null)?null:tableType.toUpperCase();
          //only allow certain table types
          if(tableType != null && !"TABLE".equals(tableType) && !"VIEW".equals(tableType) && !"ALIAS".equals(tableType) && !"SYNONYM".equals(tableType)) continue;
          
          //String remarks = tableSet.getString("REMARKS");
          tableNames.add(tableName);
          //Debug.logInfo("[GenericDAO.checkDb] Found table named \"" + tableName + "\" of type \"" + tableType + "\" with remarks: " + remarks);
        }
        catch(SQLException sqle) {
          String message = "Error getting table information... Error was:" + sqle.toString();
          Debug.logError("[GenericDAO.getTableNames] " + message);
          if(messages != null) messages.add(message);
          continue;
        }
      }
    }
    catch(SQLException sqle) {
      String message = "Error getting next table information... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.getTableNames] " + message);
      if(messages != null) messages.add(message);
      return tableNames;
    }
    finally {
      try{ tableSet.close(); }
      catch(SQLException sqle) {
        String message = "Unable to close ResultSet for table list, continuing anyway... Error was:" + sqle.toString();
        Debug.logError("[GenericDAO.getTableNames] " + message);
        if(messages != null) messages.add(message);
      }
      
      try{ connection.close(); }
      catch(SQLException sqle) {
        String message = "Unable to close database connection, continuing anyway... Error was:" + sqle.toString();
        Debug.logError("[GenericDAO.getTableNames] " + message);
        if(messages != null) messages.add(message);
      }
    }
    
    return tableNames;
  }
  
  public Map getColumnInfo(Set tableNames, Collection messages) {
    Connection connection = null;
    try { connection = getConnection(); }
    catch(SQLException sqle) {
      String message = "Unable to esablish a connection with the database... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.getColumnInfo] " + message);
      if(messages != null) messages.add(message);
      return null;
    }
    
    DatabaseMetaData dbData = null;
    try { dbData = connection.getMetaData(); }
    catch(SQLException sqle) {
      String message = "Unable to get database meta data... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.getColumnInfo] " + message);
      if(messages != null) messages.add(message);
      
      try{ connection.close(); }
      catch(SQLException sqle2) {
        String message2 = "Unable to close database connection, continuing anyway... Error was:" + sqle2.toString();
        Debug.logError("[GenericDAO.getColumnInfo] " + message2);
        if(messages != null) messages.add(message2);
      }
      return null;
    }
    
    try {
      Debug.logInfo("[GenericDAO.getColumnInfo] Database Product Name is " + dbData.getDatabaseProductName());
      Debug.logInfo("[GenericDAO.getColumnInfo] Database Product Version is " + dbData.getDatabaseProductVersion());
    }
    catch(SQLException sqle) {
      Debug.logInfo("[GenericDAO.getColumnInfo] Unable to get Database name & version information");
    }
    try {
      Debug.logInfo("[GenericDAO.getColumnInfo] Database Driver Name is " + dbData.getDriverName());
      Debug.logInfo("[GenericDAO.getColumnInfo] Database Driver Version is " + dbData.getDriverVersion());
    }
    catch(SQLException sqle) {
      Debug.logInfo("[GenericDAO.getColumnInfo] Unable to get Driver name & version information");
    }
    
    Map colInfo = new HashMap();
    try {
      ResultSet rsCols = dbData.getColumns(null, dbData.getUserName(), null, null);
      while(rsCols.next()) {
        try {
          ColumnCheckInfo ccInfo = new ColumnCheckInfo();
          ccInfo.tableName = rsCols.getString("TABLE_NAME");
          ccInfo.tableName = (ccInfo.tableName == null)?null:ccInfo.tableName.toUpperCase();
          //ignore the column info if the table name is not in the list we are concerned with
          if(!tableNames.contains(ccInfo.tableName)) continue;
          
          ccInfo.columnName = rsCols.getString("COLUMN_NAME");
          ccInfo.columnName = (ccInfo.columnName == null)?null:ccInfo.columnName.toUpperCase();
          
          ccInfo.typeName = rsCols.getString("TYPE_NAME");
          ccInfo.typeName = (ccInfo.typeName == null)?null:ccInfo.typeName.toUpperCase();
          ccInfo.columnSize = rsCols.getInt("COLUMN_SIZE");
          ccInfo.decimalDigits = rsCols.getInt("DECIMAL_DIGITS");
          
          ccInfo.isNullable = rsCols.getString("IS_NULLABLE");
          ccInfo.isNullable = (ccInfo.isNullable == null)?null:ccInfo.isNullable.toUpperCase();
          
          List tableColInfo = (List)colInfo.get(ccInfo.tableName);
          if(tableColInfo == null) {
            tableColInfo = new Vector();
            colInfo.put(ccInfo.tableName, tableColInfo);
          }
          tableColInfo.add(ccInfo);
        }
        catch(SQLException sqle) {
          String message = "Error getting column info for column. Error was:" + sqle.toString();
          Debug.logError("[GenericDAO.getColumnInfo] " + message);
          if(messages != null) messages.add(message);
          continue;
        }
      }
      
      try{ rsCols.close(); }
      catch(SQLException sqle) {
        String message = "Unable to close ResultSet for column list, continuing anyway... Error was:" + sqle.toString();
        Debug.logError("[GenericDAO.getColumnInfo] " + message);
        if(messages != null) messages.add(message);
      }
    }
    catch(SQLException sqle) {
      String message = "Error getting column meta data for Error was:" + sqle.toString() + ". Not checking columns.";
      Debug.logError("[GenericDAO.getColumnInfo] " + message);
      if(messages != null) messages.add(message);
      colInfo = null;
    }
    
    try{ connection.close(); }
    catch(SQLException sqle) {
      String message = "Unable to close database connection, continuing anyway... Error was:" + sqle.toString();
      Debug.logError("[GenericDAO.getColumnInfo] " + message);
      if(messages != null) messages.add(message);
    }
    return colInfo;
  }
  
  class ColumnCheckInfo {
    public String tableName;
    public String columnName;
    public String typeName;
    public int columnSize;
    public int decimalDigits;
    public String isNullable; //YES/NO or "" = ie nobody knows
  }
}
