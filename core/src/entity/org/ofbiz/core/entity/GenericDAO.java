/*
 * $Id$
 *
 * Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.entity;


import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import org.w3c.dom.Element;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.config.*;
import org.ofbiz.core.entity.model.*;
import org.ofbiz.core.entity.jdbc.*;


/**
 * Generic Entity Data Access Object - Handles persisntence for any defined entity.
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:chris_maurer@altavista.com">Chris Maurer</a>
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     <a href="mailto:jdonnerstag@eds.de">Juergen Donnerstag</a>
 *@created    Wed Aug 08 2001
 *@version    1.0
 */
public class GenericDAO {
    
    public static final String module = GenericDAO.class.getName();
    
    protected static Map genericDAOs = new Hashtable();
    protected String helperName;
    protected ModelFieldTypeReader modelFieldTypeReader = null;
    
    public static GenericDAO getGenericDAO(String helperName) {
        GenericDAO newGenericDAO = (GenericDAO) genericDAOs.get(helperName);
        
        if (newGenericDAO == null)//don't want to block here
        {
            synchronized (GenericDAO.class) {
                newGenericDAO = (GenericDAO) genericDAOs.get(helperName);
                if (newGenericDAO == null) {
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
    
    public int insert(GenericEntity entity) throws GenericEntityException {
        ModelEntity modelEntity = entity.getModelEntity();
        
        if (modelEntity == null) {
            throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
        }
        
        SQLProcessor sql = new SQLProcessor(helperName);
        
        try {
            return singleInsert(entity, modelEntity, modelEntity.getFieldsCopy(), sql.getConnection());
        } catch (GenericDataSourceException e) {
            sql.rollback();
            throw new GenericDataSourceException("Exception while inserting the following entity: " + entity.toString(), e);
        } finally {
            sql.close();
        }
    }
    
    private int singleInsert(GenericEntity entity, ModelEntity modelEntity, List fieldsToSave, Connection connection) throws GenericEntityException {
        if (modelEntity instanceof ModelViewEntity) {
            throw new GenericNotImplementedException("Operation insert not supported yet for view entities");
        }
        
        // if we have a STAMP_FIELD then set it with NOW.
        if (modelEntity.isField(ModelEntity.STAMP_FIELD)) {
            entity.set(ModelEntity.STAMP_FIELD, UtilDateTime.nowTimestamp());
        }
        
        String sql = "INSERT INTO " + modelEntity.getTableName() + " (" + modelEntity.colNameString(fieldsToSave) + ") VALUES (" +
        modelEntity.fieldsStringList(fieldsToSave, "?", ", ") + ")";
        
        Debug.logVerbose("[GenericDAO.singleInsert] sql=" + sql + "\nEntity=" + entity, module);
        
        SQLProcessor sqlP = new SQLProcessor(helperName, connection);
        
        try {
            sqlP.prepareStatement(sql);
            SqlJdbcUtil.setValues(sqlP, fieldsToSave, entity, modelFieldTypeReader);
            int retVal = sqlP.executeUpdate();
            entity.modified = false;
            return retVal;
        } catch (GenericEntityException e) {
            throw new GenericEntityException("while inserting: " + entity.toString(), e);
        } finally {
            sqlP.close();
        }
    }
    
    public int updateAll(GenericEntity entity) throws GenericEntityException {
        ModelEntity modelEntity = entity.getModelEntity();
        
        if (modelEntity == null) {
            throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
        }
        
        return customUpdate(entity, modelEntity, modelEntity.getNopksCopy());
    }
    
    public int update(GenericEntity entity) throws GenericEntityException {
        ModelEntity modelEntity = entity.getModelEntity();
        
        if (modelEntity == null) {
            throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
        }

        //we don't want to update ALL fields, just the nonpk fields that are in the passed GenericEntity
        List partialFields = new ArrayList();
        Collection keys = entity.getAllKeys();
        
        for (int fi = 0; fi < modelEntity.getNopksSize(); fi++) {
            ModelField curField = modelEntity.getNopk(fi);
            
            if (keys.contains(curField.getName()))
                partialFields.add(curField);
        }
        
        return customUpdate(entity, modelEntity, partialFields);
    }
    
    private int customUpdate(GenericEntity entity, ModelEntity modelEntity, List fieldsToSave) throws GenericEntityException {
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            return singleUpdate(entity, modelEntity, fieldsToSave, sqlP.getConnection());
        } catch (GenericDataSourceException e) {
            sqlP.rollback();
            throw new GenericDataSourceException("Exception while updating the following entity: " + entity.toString(), e);
        } finally {
            sqlP.close();
        }
    }
    
    private int singleUpdate(GenericEntity entity, ModelEntity modelEntity, List fieldsToSave, Connection connection) throws GenericEntityException {
        if (modelEntity instanceof ModelViewEntity) {
            throw new GenericNotImplementedException("Operation update not supported yet for view entities");
        }
        
        //no non-primaryKey fields, update doesn't make sense, so don't do it
        if (fieldsToSave.size() <= 0) {
            Debug.logVerbose("Trying to do an update on an entity with no non-PK fields, returning having done nothing; entity=" + entity);
            //returning one because it was effectively updated, ie the same thing, so don't trigger any errors elsewhere
            return 1;
        }
        
        if (modelEntity.lock()) {
            GenericEntity entityCopy = new GenericEntity(entity);
            
            select(entityCopy, connection);
            Object stampField = entity.get(ModelEntity.STAMP_FIELD);
            if ((stampField != null) && (!stampField.equals(entityCopy.get(ModelEntity.STAMP_FIELD)))) {
                String lockedTime = entityCopy.getTimestamp(ModelEntity.STAMP_FIELD).toString();
                throw new EntityLockedException("You tried to update an old version of this data. Version locked: (" + lockedTime + ")");
            }
        }
        
        // if we have a STAMP_FIELD then update it with NOW.
        if (modelEntity.isField(ModelEntity.STAMP_FIELD)) {
            entity.set(ModelEntity.STAMP_FIELD, UtilDateTime.nowTimestamp());
        }
        
        String sql = "UPDATE " + modelEntity.getTableName() + " SET " + modelEntity.colNameString(fieldsToSave, "=?, ", "=?") + " WHERE " +
        SqlJdbcUtil.makeWhereStringAnd(modelEntity.getPksCopy(), entity);
        
        Debug.logVerbose("[GenericDAO.singleUpdate] sql=" + sql + "\nEntity=" + entity, module);
        
        SQLProcessor sqlP = new SQLProcessor(helperName, connection);
        
        int retVal = 0;
        try {
            sqlP.prepareStatement(sql);
            SqlJdbcUtil.setValues(sqlP, fieldsToSave, entity, modelFieldTypeReader);
            SqlJdbcUtil.setPkValues(sqlP, modelEntity, entity, modelFieldTypeReader);
            retVal = sqlP.executeUpdate();
            entity.modified = false;
        } catch (GenericEntityException e) {
            throw new GenericEntityException("while updating: " + entity.toString(), e);
        } finally {
            sqlP.close();
        }
        
        if (retVal == 0) {
            throw new GenericEntityNotFoundException("Tried to update an entity that does not exist.");
        }
        return retVal;
    }
    
    /** Store the passed entity - insert if does not exist, otherwise update */
    private int singleStore(GenericEntity entity, Connection connection) throws GenericEntityException {
        GenericPK tempPK = entity.getPrimaryKey();
        ModelEntity modelEntity = entity.getModelEntity();
        
        try {
            //must use same connection for select or it won't be in the same transaction...
            select(tempPK, connection);
        } catch (GenericEntityNotFoundException e) {
            //Debug.logInfo(e);
            //select failed, does not exist, insert
            return singleInsert(entity, modelEntity, modelEntity.getFieldsCopy(), connection);
        }
        //select did not fail, so exists, update

        //we don't want to update ALL fields, just the nonpk fields that are in the passed GenericEntity
        List partialFields = new ArrayList();
        Collection keys = entity.getAllKeys();
        
        for (int fi = 0; fi < modelEntity.getNopksSize(); fi++) {
            ModelField curField = modelEntity.getNopk(fi);
            
            if (keys.contains(curField.getName()))
                partialFields.add(curField);
        }

        return singleUpdate(entity, modelEntity, partialFields, connection);
    }
    
    /* ====================================================================== */
    
    /* ====================================================================== */
    
    public int storeAll(Collection entities) throws GenericEntityException {
        if (entities == null || entities.size() <= 0) {
            return 0;
        }
        
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        int totalStored = 0;
        try {
            Iterator entityIter = entities.iterator();
            
            while (entityIter != null && entityIter.hasNext()) {
                GenericEntity curEntity = (GenericEntity) entityIter.next();
                totalStored += singleStore(curEntity, sqlP.getConnection());
            }
        } catch (GenericDataSourceException e) {
            sqlP.rollback();
            throw new GenericDataSourceException("Exception occured in storeAll", e);
        } finally {
            sqlP.close();
        }
        return totalStored;
    }
    
    /* ====================================================================== */
    
    /* ====================================================================== */
    
    public void select(GenericEntity entity) throws GenericEntityException {
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            select(entity, sqlP.getConnection());
        } finally {
            sqlP.close();
        }
    }
    
    public void select(GenericEntity entity, Connection connection) throws GenericEntityException {
        ModelEntity modelEntity = entity.getModelEntity();
        
        if (modelEntity == null) {
            throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
        }
        
        if (modelEntity.getPksSize() <= 0)
            throw new GenericEntityException("Entity has no primary keys, cannot select by primary key");
        
        String sql = "SELECT ";
        
        if (modelEntity.getNopksSize() > 0)
            sql += modelEntity.colNameString(modelEntity.getNopksCopy(), ", ", "");
        else
            sql += "*";
        
        sql += SqlJdbcUtil.makeFromClause(modelEntity);
        sql += SqlJdbcUtil.makeWhereClauseAnd(modelEntity, modelEntity.getPksCopy(), entity);
        
        Debug.logVerbose("[GenericDAO.select] sql=" + sql, module);
        
        SQLProcessor sqlP = new SQLProcessor(helperName, connection);
        
        try {
            sqlP.prepareStatement(sql);
            SqlJdbcUtil.setPkValues(sqlP, modelEntity, entity, modelFieldTypeReader);
            sqlP.executeQuery();
            
            if (sqlP.next()) {
                for (int j = 0; j < modelEntity.getNopksSize(); j++) {
                    ModelField curField = modelEntity.getNopk(j);
                    
                    SqlJdbcUtil.getValue(sqlP.getResultSet(), j + 1, curField, entity, modelFieldTypeReader);
                }
                
                entity.modified = false;
            } else {
                //Debug.logWarning("[GenericDAO.select]: select failed, result set was empty for entity: " + entity.toString());
                throw new GenericEntityNotFoundException("Result set was empty for entity: " + entity.toString());
            }
        } finally {
            sqlP.close();
        }
    }
    
    public void partialSelect(GenericEntity entity, Set keys) throws GenericEntityException {
        ModelEntity modelEntity = entity.getModelEntity();
        
        if (modelEntity == null) {
            throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
        }
        
        if (modelEntity instanceof ModelViewEntity) {
            throw new org.ofbiz.core.entity.GenericNotImplementedException("Operation partialSelect not supported yet for view entities");
        }
        
        /*
         if(entity == null || entity.<%=modelEntity.pkNameString(" == null || entity."," == null")%>) {
         Debug.logWarning("[GenericDAO.select]: Cannot select GenericEntity: required primary key field(s) missing.");
         return false;
         }
         */
        //we don't want to select ALL fields, just the nonpk fields that are in the passed GenericEntity
        List partialFields = new ArrayList();
        
        for (int fi = 0; fi < modelEntity.getNopksSize(); fi++) {
            ModelField curField = modelEntity.getNopk(fi);
            
            if (keys.contains(curField.getName()))
                partialFields.add(curField);
        }
        
        String sql = "SELECT ";
        
        if (partialFields.size() > 0) {
            sql += modelEntity.colNameString(partialFields, ", ", "");
        } else {
            sql += "*";
        }
        sql += SqlJdbcUtil.makeFromClause(modelEntity);
        sql += SqlJdbcUtil.makeWhereClauseAnd(modelEntity, modelEntity.getPksCopy(), entity);
        
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            sqlP.prepareStatement(sql);
            SqlJdbcUtil.setPkValues(sqlP, modelEntity, entity, modelFieldTypeReader);
            sqlP.executeQuery();
            
            if (sqlP.next()) {
                for (int j = 0; j < partialFields.size(); j++) {
                    ModelField curField = (ModelField) partialFields.get(j);
                    
                    SqlJdbcUtil.getValue(sqlP.getResultSet(), j + 1, curField, entity, modelFieldTypeReader);
                }
                
                entity.modified = false;
            } else {
                //Debug.logWarning("[GenericDAO.select]: select failed, result set was empty.");
                throw new GenericEntityNotFoundException("Result set was empty for entity: " + entity.toString());
            }
        } finally {
            sqlP.close();
        }
    }
    
    public Collection selectByAnd(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        if (modelEntity == null)
            return null;
        
        Collection collection = new LinkedList();
        //make two ArrayLists of fields, one for fields to select and the other for where clause fields (to find by)
        List whereFields = new ArrayList();
        List selectFields = new ArrayList();
        
        if (fields != null && fields.size() > 0) {
            Set keys = fields.keySet();
            
            for (int fi = 0; fi < modelEntity.getFieldsSize(); fi++) {
                ModelField curField = modelEntity.getField(fi);
                
                if (keys.contains(curField.getName()))
                    whereFields.add(curField);
                else
                    selectFields.add(curField);
            }
        } else {
            selectFields = modelEntity.getFieldsCopy();
        }
        
        GenericValue dummyValue;
        
        if (fields != null && fields.size() > 0) {
            dummyValue = new GenericValue(modelEntity, fields);
        } else {
            dummyValue = new GenericValue(modelEntity);
        }
        
        String sql = "SELECT ";
        
        if (selectFields.size() > 0) {
            sql += modelEntity.colNameString(selectFields, ", ", "");
        } else {
            sql += "*";
        }
        
        sql += SqlJdbcUtil.makeFromClause(modelEntity);
        sql += SqlJdbcUtil.makeWhereClauseAnd(modelEntity, whereFields, dummyValue);
        sql += SqlJdbcUtil.makeOrderByClause(modelEntity, orderBy);
        
        Debug.logVerbose("[GenericDAO.selectByAnd] sql=" + sql, module);
        
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            sqlP.prepareStatement(sql);
            if (fields != null && fields.size() > 0) {
                SqlJdbcUtil.setValuesWhereClause(sqlP, whereFields, dummyValue, modelFieldTypeReader);
            }
            Debug.logVerbose("[GenericDAO.selectByAnd] ps=" + sqlP.getPreparedStatement().toString(), module);
            sqlP.executeQuery();
            
            while (sqlP.next()) {
                GenericValue value = new GenericValue(dummyValue);
                
                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.get(j);
                    
                    SqlJdbcUtil.getValue(sqlP.getResultSet(), j + 1, curField, value, modelFieldTypeReader);
                }
                
                value.modified = false;
                collection.add(value);
            }
        } finally {
            sqlP.close();
        }
        
        return collection;
    }
    
    public Collection selectByOr(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        if (modelEntity == null)
            return null;
        
        Collection collection = new LinkedList();
        //make two ArrayLists of fields, one for fields to select and the other for where clause fields (to find by)
        List whereFields = new ArrayList();
        List selectFields = new ArrayList();
        
        if (fields != null && fields.size() > 0) {
            Set keys = fields.keySet();
            
            for (int fi = 0; fi < modelEntity.getFieldsSize(); fi++) {
                ModelField curField = modelEntity.getField(fi);
                
                if (keys.contains(curField.getName()))
                    whereFields.add(curField);
                else
                    selectFields.add(curField);
            }
        } else {
            selectFields = modelEntity.getFieldsCopy();
        }
        
        GenericValue dummyValue;
        
        if (fields != null && fields.size() > 0)
            dummyValue = new GenericValue(modelEntity, fields);
        else
            dummyValue = new GenericValue(modelEntity);
        
        String sql = "SELECT ";
        
        if (selectFields.size() > 0)
            sql += modelEntity.colNameString(selectFields, ", ", "");
        else
            sql += "*";
        
        sql += SqlJdbcUtil.makeFromClause(modelEntity);
        sql += SqlJdbcUtil.makeWhereClauseOr(modelEntity, whereFields, dummyValue);
        sql += SqlJdbcUtil.makeOrderByClause(modelEntity, orderBy);
        
        Debug.logVerbose("[GenericDAO.selectByOr] sql=" + sql, module);
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            sqlP.prepareStatement(sql);
            
            if (fields != null && fields.size() > 0) {
                SqlJdbcUtil.setValuesWhereClause(sqlP, whereFields, dummyValue, modelFieldTypeReader);
            }
            Debug.logVerbose("[GenericDAO.selectByOr] ps=" + sqlP.getPreparedStatement().toString(), module);
            sqlP.executeQuery();
            
            while (sqlP.next()) {
                GenericValue value = new GenericValue(dummyValue);
                
                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.get(j);
                    
                    SqlJdbcUtil.getValue(sqlP.getResultSet(), j + 1, curField, value, modelFieldTypeReader);
                }
                
                value.modified = false;
                collection.add(value);
            }
        } finally {
            sqlP.close();
        }
        
        return collection;
    }
    
    public Collection selectByAnd(ModelEntity modelEntity, List expressions, List orderBy) throws GenericEntityException {
        if (modelEntity == null)
            return null;
        
        if (expressions == null)
            return null;
        Collection collection = new LinkedList();
        //make two ArrayLists of fields, one for fields to select and the other for where clause fields (to find by)
        List selectFields = modelEntity.getFieldsCopy();
        
        StringBuffer sqlBuffer = new StringBuffer("SELECT ");
        
        if (selectFields.size() > 0) {
            sqlBuffer.append(modelEntity.colNameString(selectFields, ", ", ""));
        } else {
            sqlBuffer.append("*");
        }
        sqlBuffer.append(" ");
        sqlBuffer.append(SqlJdbcUtil.makeFromClause(modelEntity));
        
        StringBuffer whereString = new StringBuffer("");
        
        if (expressions != null && expressions.size() > 0) {
            for (int i = 0; i < expressions.size(); i++) {
                EntityExpr expr = (EntityExpr) expressions.get(i);
                ModelField field = (ModelField) modelEntity.getField((String) expr.getLhs());
                
                if (field != null) {
                    if (expr.getRhs() == null) {
                        whereString.append(field.getColName());
                        if (expr.getOperator() == EntityOperator.NOT_EQUAL) {
                            whereString.append(" IS NOT NULL ");
                        } else {
                            whereString.append(" IS NULL ");
                        }
                    } else {
                        if (expr.isLUpper())
                            whereString.append("UPPER(" + field.getColName() + ")");
                        else
                            whereString.append(field.getColName());
                        whereString.append(expr.getOperator());
                        whereString.append(" ? ");
                    }
                    if (i < expressions.size() - 1) {
                        whereString.append(" AND ");
                    }
                } else {
                    throw new IllegalArgumentException("ModelField with field name " + (String) expr.getLhs() + " not found");
                }
            }
        }
        
        String viewClause = SqlJdbcUtil.makeViewWhereClause(modelEntity);
        
        if (viewClause.length() > 0) {
            whereString.append(" AND ");
            whereString.append(viewClause);
        }
        
        if (whereString.length() > 0) {
            sqlBuffer.append(" WHERE ");
            sqlBuffer.append(whereString.toString());
        }
        
        sqlBuffer.append(SqlJdbcUtil.makeOrderByClause(modelEntity, orderBy));
        String sql = sqlBuffer.toString();
        
        Debug.logVerbose("[GenericDAO.selectByAnd] sql=" + sql, module);
        
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            sqlP.prepareStatement(sql);
            GenericValue dummyValue = new GenericValue(modelEntity);
            
            if (expressions != null && expressions.size() > 0) {
                for (int i = 0; i < expressions.size(); i++) {
                    EntityExpr expr = (EntityExpr) expressions.get(i);
                    
                    if (expr.getRhs() != null) {
                        ModelField field = (ModelField) modelEntity.getField((String) expr.getLhs());
                        
                        //set the field in the dummyValue so that the setValue method can get it out
                        dummyValue.set(field.getName(), expr.getRhs());
                        SqlJdbcUtil.setValue(sqlP, field, dummyValue, modelFieldTypeReader);
                    }
                }
            }
            Debug.logVerbose("[GenericDAO.selectByAnd] ps=" + sqlP.getPreparedStatement().toString(), module);
            sqlP.executeQuery();
            
            while (sqlP.next()) {
                GenericValue value = new GenericValue(modelEntity);
                
                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.get(j);
                    SqlJdbcUtil.getValue(sqlP.getResultSet(), j + 1, curField, value, modelFieldTypeReader);
                }
                
                value.modified = false;
                collection.add(value);
            }
        } finally {
            sqlP.close();
        }
        
        return collection;
    }
    
    public Collection selectByOr(ModelEntity modelEntity, List expressions, List orderBy) throws GenericEntityException {
        if (modelEntity == null)
            return null;
        
        if (expressions == null)
            return null;
        Collection collection = new LinkedList();
        //make two ArrayLists of fields, one for fields to select and the other for where clause fields (to find by)
        List selectFields = modelEntity.getFieldsCopy();
        
        StringBuffer sqlBuffer = new StringBuffer("SELECT ");
        
        if (selectFields.size() > 0)
            sqlBuffer.append(modelEntity.colNameString(selectFields, ", ", ""));
        else
            sqlBuffer.append("*");
        sqlBuffer.append(" ");
        sqlBuffer.append(SqlJdbcUtil.makeFromClause(modelEntity));
        
        StringBuffer whereString = new StringBuffer("");
        
        if (expressions != null && expressions.size() > 0) {
            whereString.append("(");
            for (int i = 0; i < expressions.size(); i++) {
                EntityExpr expr = (EntityExpr) expressions.get(i);
                ModelField field = (ModelField) modelEntity.getField((String) expr.getLhs());
                
                if (field != null) {
                    if (expr.getRhs() == null) {
                        whereString.append(field.getColName());
                        if (expr.getOperator() == EntityOperator.NOT_EQUAL) {
                            whereString.append(" IS NOT NULL ");
                        } else {
                            whereString.append(" IS NULL ");
                        }
                    } else {
                        whereString.append(field.getColName());
                        whereString.append(expr.getOperator());
                        whereString.append(" ? ");
                    }
                    if (i < expressions.size() - 1) {
                        whereString.append(" OR ");
                    }
                } else {
                    throw new IllegalArgumentException("ModelField with field name " + (String) expr.getLhs() + " not found");
                }
            }
            whereString.append(")");
        }
        
        String viewClause = SqlJdbcUtil.makeViewWhereClause(modelEntity);
        
        if (viewClause.length() > 0) {
            whereString.append(" AND ");
            whereString.append(viewClause);
        }
        
        if (whereString.length() > 0) {
            sqlBuffer.append(" WHERE ");
            sqlBuffer.append(whereString);
        }
        
        sqlBuffer.append(SqlJdbcUtil.makeOrderByClause(modelEntity, orderBy));
        String sql = sqlBuffer.toString();
        
        Debug.logVerbose("[GenericDAO.selectByOr] sql=" + sql, module);
        
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            sqlP.prepareStatement(sql);
            GenericValue dummyValue = new GenericValue(modelEntity);
            
            if (expressions != null && expressions.size() > 0) {
                for (int i = 0; i < expressions.size(); i++) {
                    EntityExpr expr = (EntityExpr) expressions.get(i);
                    
                    if (expr.getRhs() != null) {
                        ModelField field = (ModelField) modelEntity.getField((String) expr.getLhs());
                        
                        //set the field in the dummyValue so that the setValue method can get it out
                        dummyValue.set(field.getName(), expr.getRhs());
                        SqlJdbcUtil.setValue(sqlP, field, dummyValue, modelFieldTypeReader);
                    }
                }
            }
            Debug.logVerbose("[GenericDAO.selectByOr] ps=" + sqlP.getPreparedStatement().toString(), module);
            sqlP.executeQuery();
            
            while (sqlP.next()) {
                GenericValue value = new GenericValue(modelEntity);
                
                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.get(j);
                    
                    SqlJdbcUtil.getValue(sqlP.getResultSet(), j + 1, curField, value, modelFieldTypeReader);
                }
                
                value.modified = false;
                collection.add(value);
            }
        } finally {
            sqlP.close();
        }
        
        return collection;
    }
    
    public Collection selectByLike(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        if (modelEntity == null)
            return null;
        if (modelEntity instanceof ModelViewEntity) {
            throw new org.ofbiz.core.entity.GenericNotImplementedException("Operation selectByLike not supported yet for view entities");
        }
        
        Collection collection = new LinkedList();
        
        //make two ArrayLists of fields, one for fields to select and the other for where clause fields (to find by)
        List whereFields = new ArrayList();
        List selectFields = new ArrayList();
        
        if (fields != null && fields.size() > 0) {
            Set keys = fields.keySet();
            
            for (int fi = 0; fi < modelEntity.getFieldsSize(); fi++) {
                ModelField curField = modelEntity.getField(fi);
                
                if (keys.contains(curField.getName())) {
                    whereFields.add(curField);
                    selectFields.add(curField);
                } else
                    selectFields.add(curField);
            }
        } else {
            selectFields = modelEntity.getFieldsCopy();
        }
        
        String sql = "SELECT ";
        
        if (selectFields.size() > 0)
            sql += modelEntity.colNameString(selectFields, ", ", "");
        else
            sql += "*";
        sql += " FROM " + modelEntity.getTableName();
        if (fields != null && fields.size() > 0)
            sql += " WHERE " + modelEntity.colNameString(whereFields, " LIKE ? AND ", " LIKE ?");
        
        sql += SqlJdbcUtil.makeOrderByClause(modelEntity, orderBy);
        Debug.logVerbose("[GenericDAO.selectByLike] sql=" + sql, module);
        
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            sqlP.prepareStatement(sql);
            
            GenericValue dummyValue;
            
            if (fields != null && fields.size() > 0) {
                dummyValue = new GenericValue(modelEntity, fields);
                SqlJdbcUtil.setValuesWhereClause(sqlP, whereFields, dummyValue, modelFieldTypeReader);
            } else {
                dummyValue = new GenericValue(modelEntity);
            }
            
            Debug.logVerbose("[GenericDAO.selectByLike] ps=" + sqlP.getPreparedStatement().toString(), module);
            sqlP.executeQuery();
            
            while (sqlP.next()) {
                GenericValue value = new GenericValue(dummyValue);
                
                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.get(j);
                    
                    SqlJdbcUtil.getValue(sqlP.getResultSet(), j + 1, curField, value, modelFieldTypeReader);
                }
                value.modified = false;
                collection.add(value);
            }
        } finally {
            sqlP.close();
        }
        
        return collection;
    }
    
    public Collection selectByClause(ModelEntity modelEntity, List entityClauses, Map fields, List orderBy) throws GenericEntityException {
        if (modelEntity == null)
            return null;
        if (modelEntity instanceof ModelViewEntity) {
            throw new org.ofbiz.core.entity.GenericNotImplementedException("Operation insert not supported yet for view entities");
        }
        
        if (entityClauses == null)
            return null;
        
        Collection collection = new LinkedList();
        ModelEntity firstModelEntity = null;
        ModelEntity secondModelEntity = null;
        
        ModelField firstModelField = null;
        ModelField secondModelField = null;
        
        StringBuffer select = new StringBuffer(" SELECT ");
        StringBuffer from = new StringBuffer(" FROM ");
        StringBuffer where = new StringBuffer(" WHERE ");
        StringBuffer order = new StringBuffer();
        
        String test = "";
        
        List whereTables = new ArrayList();
        
        //each iteration defines one relationship for the query.
        for (int i = 0; i < entityClauses.size(); i++) {
            EntityClause entityClause = (EntityClause) entityClauses.get(i);
            String interFieldOperation = entityClause.getInterFieldOperation().toString();
            String intraFieldOperation = entityClause.getIntraFieldOperation().toString();
            
            firstModelEntity = entityClause.getFirstModelEntity();
            if (!whereTables.contains(firstModelEntity.getTableName())) {
                whereTables.add(firstModelEntity.getTableName());
            }
            
            secondModelEntity = entityClause.getSecondModelEntity();
            if (!whereTables.contains(secondModelEntity.getTableName())) {
                whereTables.add(secondModelEntity.getTableName());
            }
            
            firstModelField = firstModelEntity.getField(entityClause.getFirstField());
            secondModelField = secondModelEntity.getField(entityClause.getSecondField());
            
            test = where.toString();
            if (i > 0)
                where.append(interFieldOperation);
            where.append(firstModelEntity.getTableName() + "." + firstModelField.getColName() + " " + intraFieldOperation + " " + secondModelEntity.getTableName() + "." +
            secondModelField.getColName());
        }
        int ix = 0;
        
        for (; ix < whereTables.size() - 1; ix++) {
            from.append(whereTables.get(ix) + ", ");
        }
        from.append(whereTables.get(ix));
        
        List whereFields = new ArrayList();
        List selectFields = new ArrayList();
        
        if (fields != null && fields.size() > 0) {
            Set keys = fields.keySet();
            
            for (int fi = 0; fi < modelEntity.getFieldsSize(); fi++) {
                ModelField curField = modelEntity.getField(fi);
                
                if (keys.contains(curField.getName())) {
                    whereFields.add(curField);
                    selectFields.add(curField);
                } else
                    selectFields.add(curField);
            }
        }
        
        String tableNamePrefix = modelEntity.getTableName() + ".";
        
        if (fields != null && fields.size() > 0) {
            test = where.toString();
            if (test.trim().length() > 0)
                where.append(" AND ");
            where.append(tableNamePrefix);
            where.append(modelEntity.colNameString(whereFields, "=? AND " + tableNamePrefix, "=?"));
        }
        
        select.append(tableNamePrefix);
        select.append(modelEntity.colNameString(selectFields, ", " + tableNamePrefix, ""));
        
        select.append(SqlJdbcUtil.makeOrderByClause(modelEntity, orderBy));
        
        String sql = "";
        
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            sql = select.toString() + " " + from.toString() + " " + where.toString() + (order.toString().trim().length() > 0 ? order.toString() : "");
            sqlP.prepareStatement(sql);
            
            GenericValue dummyValue;
            
            if (fields != null && fields.size() > 0) {
                dummyValue = new GenericValue(modelEntity, fields);
                SqlJdbcUtil.setValuesWhereClause(sqlP, whereFields, dummyValue, modelFieldTypeReader);
            } else {
                dummyValue = new GenericValue(modelEntity);
            }
            sqlP.executeQuery();
            
            while (sqlP.next()) {
                GenericValue value = new GenericValue(dummyValue);
                
                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.get(j);
                    SqlJdbcUtil.getValue(sqlP.getResultSet(), j + 1, curField, value, modelFieldTypeReader);
                }
                
                value.modified = false;
                collection.add(value);
            }
        } finally {
            sqlP.close();
        }
        
        return collection;
    }
    
    /* ====================================================================== */
    
    /* ====================================================================== */
    
    public int delete(GenericEntity entity) throws GenericEntityException {
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            return delete(entity, sqlP.getConnection());
        } catch (GenericDataSourceException e) {
            sqlP.rollback();
            throw new GenericDataSourceException("Exception while deleting the following entity: " + entity.toString(), e);
        } finally {
            sqlP.close();
        }
    }
    
    public int delete(GenericEntity entity, Connection connection) throws GenericEntityException {
        ModelEntity modelEntity = entity.getModelEntity();
        
        if (modelEntity == null) {
            throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
        }
        
        if (modelEntity instanceof ModelViewEntity) {
            throw new org.ofbiz.core.entity.GenericNotImplementedException("Operation delete not supported yet for view entities");
        }
        
        String sql = "DELETE FROM " + modelEntity.getTableName() + " WHERE " + SqlJdbcUtil.makeWhereStringAnd(modelEntity.getPksCopy(), entity);
        
        SQLProcessor sqlP = new SQLProcessor(helperName, connection);
        
        int retVal;
        try {
            sqlP.prepareStatement(sql);
            SqlJdbcUtil.setPkValues(sqlP, modelEntity, entity, modelFieldTypeReader);
            retVal = sqlP.executeUpdate();
            entity.modified = true;
        } finally {
            sqlP.close();
        }
        return retVal;
    }
    
    public int deleteByAnd(ModelEntity modelEntity, Map fields) throws GenericEntityException {
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            return deleteByAnd(modelEntity, fields, sqlP.getConnection());
        } catch (GenericDataSourceException e) {
            sqlP.rollback();
            throw new GenericDataSourceException("Generic Entity Exception occured in deleteByAnd", e);
        } finally {
            sqlP.close();
        }
    }
    
    public int deleteByAnd(ModelEntity modelEntity, Map fields, Connection connection) throws GenericEntityException {
        if (modelEntity == null || fields == null)
            return 0;
        if (modelEntity instanceof ModelViewEntity) {
            throw new org.ofbiz.core.entity.GenericNotImplementedException("Operation deleteByAnd not supported yet for view entities");
        }
        
        //make two ArrayLists of fields, one for fields to select and the other for where clause fields (to find by)
        List whereFields = new ArrayList();
        
        if (fields != null || fields.size() > 0) {
            Set keys = fields.keySet();
            
            for (int fi = 0; fi < modelEntity.getFieldsSize(); fi++) {
                ModelField curField = modelEntity.getField(fi);
                
                if (keys.contains(curField.getName()))
                    whereFields.add(curField);
            }
        }
        
        GenericValue dummyValue = new GenericValue(modelEntity, fields);
        String sql = "DELETE FROM " + modelEntity.getTableName();
        
        if (fields != null || fields.size() > 0) {
            sql += " WHERE " + SqlJdbcUtil.makeWhereStringAnd(whereFields, dummyValue);
        }
        
        SQLProcessor sqlP = new SQLProcessor(helperName);
        try {
            sqlP.prepareStatement(sql);
            
            if (fields != null || fields.size() > 0) {
                SqlJdbcUtil.setValuesWhereClause(sqlP, whereFields, dummyValue, modelFieldTypeReader);
            }
            
            return sqlP.executeUpdate();
        } finally {
            sqlP.close();
        }
    }
    
    /** Called dummyPKs because they can be invalid PKs, doing a deleteByAnd instead of a normal delete */
    public int deleteAll(Collection dummyPKs) throws GenericEntityException {
        if (dummyPKs == null || dummyPKs.size() == 0) {
            return 0;
        }
        
        SQLProcessor sqlP = new SQLProcessor(helperName);
        
        try {
            Iterator iter = dummyPKs.iterator();
            
            int numDeleted = 0;
            while (iter.hasNext()) {
                GenericEntity entity = (GenericEntity) iter.next();
                
                //if it contains a complete primary key, delete the one, otherwise deleteByAnd
                if (entity.containsPrimaryKey()) {
                    numDeleted += delete(entity, sqlP.getConnection());
                } else {
                    numDeleted += deleteByAnd(entity.getModelEntity(), entity.getAllFields(), sqlP.getConnection());
                }
            }
            return numDeleted;
        } catch (GenericDataSourceException e) {
            sqlP.rollback();
            throw new GenericDataSourceException("Generic Entity Exception occured in deleteAll", e);
        } finally {
            sqlP.close();
        }
    }

    /* ====================================================================== */
    
    public void checkDb(Map modelEntities, Collection messages, boolean addMissing) {
        DatabaseUtil dbUtil = new DatabaseUtil(this.helperName);
        dbUtil.checkDb(modelEntities, messages, addMissing);
    }
    
    /** Creates a list of ModelEntity objects based on meta data from the database */
    public List induceModelFromDb(Collection messages) {
        DatabaseUtil dbUtil = new DatabaseUtil(this.helperName);
        return dbUtil.induceModelFromDb(messages);
    }
}
