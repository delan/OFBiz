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


/**
 * Generic Entity Data Access Object - Handles persisntence for any defined entity.
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:chris_maurer@altavista.com">Chris Maurer</a>
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
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
    
    public Connection getConnection() throws SQLException, GenericEntityException {
        Connection connection = ConnectionFactory.getConnection(helperName);

        return connection;
    }
    
    public void insert(GenericEntity entity) throws GenericEntityException {
        ModelEntity modelEntity = entity.getModelEntity();

        if (modelEntity == null) {
            throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
        }
        
        boolean manualTX = true;
        Connection connection = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to establish a connection with the database.", sqle);
        }
        
        try {
            connection.setAutoCommit(false);
        } catch (SQLException sqle) {
            manualTX = false;
        }
        if (TransactionUtil.getStatus() == TransactionUtil.STATUS_ACTIVE) {
            manualTX = false;
        }
        
        try {
            singleInsert(entity, modelEntity, modelEntity.getFieldsCopy(), connection);
            if (manualTX) {
                try {
                    connection.commit();
                } catch (SQLException sqle) {
                    try {
                        if (manualTX)
                            connection.rollback();
                    } catch (SQLException sqle2) {
                        Debug.logWarning("[GenericDAO.insert]: SQL Exception while rolling back insert. Error was:", module);
                        Debug.logWarning(sqle2, module);
                    }
                    throw new GenericDataSourceException("SQL Exception occured on commit of insert", sqle);
                }
            }
        } catch (GenericDataSourceException e) {
            try {
                if (manualTX)
                    connection.rollback();
            } catch (SQLException sqle2) {
                Debug.logWarning("[GenericDAO.insert]: SQL Exception while rolling back insert. Error was:", module);
                Debug.logWarning(sqle2, module);
            }
            throw new GenericDataSourceException("Exception occured in insert", e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
    }
    
    private void singleInsert(GenericEntity entity, ModelEntity modelEntity, Vector fieldsToSave, Connection connection) throws GenericEntityException {
        if (modelEntity instanceof ModelViewEntity) {
            throw new GenericNotImplementedException("Operation insert not supported yet for view entities");
        }
        
        // if we have a STAMP_FIELD then set it with NOW.
        if (modelEntity.isField(ModelEntity.STAMP_FIELD))
            entity.set(ModelEntity.STAMP_FIELD, UtilDateTime.nowTimestamp());
        
        PreparedStatement ps = null;
        String sql = "INSERT INTO " + modelEntity.getTableName() + " (" + modelEntity.colNameString(fieldsToSave) + ") VALUES (" +
            modelEntity.fieldsStringList(fieldsToSave, "?", ", ") + ")";

        Debug.logVerbose("[GenericDAO.singleInsert] sql=" + sql + "\nEntity=" + entity, module);
        try {
            ps = connection.prepareStatement(sql);
            
            for (int i = 0; i < fieldsToSave.size(); i++) {
                ModelField curField = (ModelField) fieldsToSave.elementAt(i);

                setValue(ps, i + 1, curField, entity);
            }
            
            ps.executeUpdate();
            entity.modified = false;
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //Debug.logWarning(sqle.getMessage());
            throw new GenericDataSourceException("SQL Exception while executing the following:\n" + sql + "\nOn the following entity:\n" + entity.toString(), sqle);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
    }
    
    public void updateAll(GenericEntity entity) throws GenericEntityException {
        ModelEntity modelEntity = entity.getModelEntity();

        if (modelEntity == null) {
            throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
        }
        
        customUpdate(entity, modelEntity, modelEntity.getNopksCopy());
    }
    
    public void update(GenericEntity entity) throws GenericEntityException {
        ModelEntity modelEntity = entity.getModelEntity();

        if (modelEntity == null) {
            throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
        }
        //we don't want to update ALL fields, just the nonpk fields that are in the passed GenericEntity
        Vector partialFields = new Vector();
        Collection keys = entity.getAllKeys();

        for (int fi = 0; fi < modelEntity.getNopksSize(); fi++) {
            ModelField curField = modelEntity.getNopk(fi);

            if (keys.contains(curField.getName()))
                partialFields.add(curField);
        }
        
        customUpdate(entity, modelEntity, partialFields);
    }
    
    private void customUpdate(GenericEntity entity, ModelEntity modelEntity, Vector fieldsToSave) throws GenericEntityException {
        boolean manualTX = true;
        Connection connection = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
        try {
            connection.setAutoCommit(false);
        } catch (SQLException sqle) {
            manualTX = false;
        }
        if (TransactionUtil.getStatus() == TransactionUtil.STATUS_ACTIVE) {
            manualTX = false;
        }
        
        try {
            singleUpdate(entity, modelEntity, fieldsToSave, connection);
            if (manualTX) {
                Debug.logVerbose("Committing transaction on connection, not JTA commit");
                try {
                    connection.commit();
                } catch (SQLException sqle) {
                    try {
                        if (manualTX) {
                            Debug.logVerbose("Rolling back transaction on connection, not JTA rollback");
                            connection.rollback();
                        }
                    } catch (SQLException sqle2) {
                        Debug.logWarning("[GenericDAO.customUpdate]: SQL Exception while rolling back update. Error was:", module);
                        Debug.logWarning(sqle2, module);
                    }
                    throw new GenericDataSourceException("SQL Exception occured on commit of update", sqle);
                }
            }
        } catch (GenericDataSourceException e) {
            try {
                if (manualTX) {
                    Debug.logVerbose("Rolling back transaction on connection, not JTA rollback");
                    connection.rollback();
                }
            } catch (SQLException sqle2) {
                Debug.logWarning("[GenericDAO.customUpdate]: SQL Exception while rolling back update. Error was:", module);
                Debug.logWarning(sqle2, module);
            }
            throw new GenericDataSourceException("Exception occured in update", e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
    }
    
    private void singleUpdate(GenericEntity entity, ModelEntity modelEntity, Vector fieldsToSave, Connection connection) throws GenericEntityException {
        if (modelEntity instanceof ModelViewEntity) {
            throw new org.ofbiz.core.entity.GenericNotImplementedException("Operation update not supported yet for view entities");
        }
        
        //no non-primaryKey fields, update doesn't make sense, so don't do it
        if (fieldsToSave.size() <= 0) {
            Debug.logInfo("Trying to do an update on an entity with no non-PK fields, returning having done nothing; entity=" + entity);
            return;
        }
        
        if (modelEntity.lock()) {
            GenericEntity entityCopy = new GenericEntity(entity);

            select(entityCopy, connection);
            if ((entity.get(ModelEntity.STAMP_FIELD) != null) &&
                (!entity.get(ModelEntity.STAMP_FIELD).equals(entityCopy.get(ModelEntity.STAMP_FIELD)))) {
                String lockedTime = entityCopy.getTimestamp(ModelEntity.STAMP_FIELD).toString();

                throw new EntityLockedException("Version locked (" + lockedTime + ")");
            }
        }
        
        // if we have a STAMP_FIELD then update it with NOW.
        if (modelEntity.isField(ModelEntity.STAMP_FIELD))
            entity.set(ModelEntity.STAMP_FIELD, UtilDateTime.nowTimestamp());
        
        String sql = "UPDATE " + modelEntity.getTableName() + " SET " + modelEntity.colNameString(fieldsToSave, "=?, ", "=?") + " WHERE " +
            makeWhereStringAnd(modelEntity.getPksCopy(), entity);

        Debug.logVerbose("[GenericDAO.singleUpdate] sql=" + sql + "\nEntity=" + entity, module);

        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(sql);
            
            int ind = 1;
            for (int i = 0; i < fieldsToSave.size(); i++) {
                ModelField curField = (ModelField) fieldsToSave.elementAt(i);

                setValue(ps, ind, curField, entity);
                ind++;
            }
            for (int j = 0; j < modelEntity.getPksSize(); j++) {
                ModelField curField = modelEntity.getPk(j);

                //for where clause variables only setValue if not null...
                if (entity.get(curField.getName()) != null) {
                    setValue(ps, ind, curField, entity);
                    ind++;
                }
            }
            
            ps.executeUpdate();
            entity.modified = false;
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //Debug.logWarning(sqle.getMessage());
            throw new GenericDataSourceException("SQL Exception while executing the following:\n" + sql + "\nOn the following entity:\n" + entity.toString(), sqle);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
    }
    
    /** Store the passed entity - insert if does not exist, otherwise update */
    private void singleStore(GenericEntity entity, Connection connection) throws GenericEntityException {
        GenericPK tempPK = entity.getPrimaryKey();

        try {
            //must use same connection for select or it won't be in the same transaction...
            select(tempPK, connection);
        } catch (GenericEntityNotFoundException e) {
            //Debug.logInfo(e);
            //select failed, does not exist, insert
            singleInsert(entity, entity.getModelEntity(), entity.getModelEntity().getFieldsCopy(), connection);
            return;
        }
        //select did not fail, so exists, update
        singleUpdate(entity, entity.getModelEntity(), entity.getModelEntity().getNopksCopy(), connection);
    }
    
    /* ====================================================================== */
    
    /* ====================================================================== */
    
    public void storeAll(Collection entities) throws GenericEntityException {
        if (entities == null || entities.size() <= 0) {
            return;
        }
        
        boolean manualTX = true;
        Connection connection = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
        try {
            connection.setAutoCommit(false);
        } catch (SQLException sqle) {
            manualTX = false;
        }
        if (TransactionUtil.getStatus() == TransactionUtil.STATUS_ACTIVE) {
            manualTX = false;
        }
        
        try {
            Iterator entityIter = entities.iterator();

            while (entityIter != null && entityIter.hasNext()) {
                GenericEntity curEntity = (GenericEntity) entityIter.next();

                singleStore(curEntity, connection);
            }
            if (manualTX) {
                Debug.logVerbose("Committing transaction on connection, not JTA commit");
                try {
                    connection.commit();
                } catch (SQLException sqle) {
                    try {
                        if (manualTX)
                            connection.rollback();
                    } catch (SQLException sqle2) {
                        Debug.logWarning("[GenericDAO.storeAll]: SQL Exception while rolling back storeAll. Error was:", module);
                        Debug.logWarning(sqle2, module);
                    }
                    throw new GenericDataSourceException("SQL Exception occured on commit of storeAllr", sqle);
                }
            }
        } catch (GenericDataSourceException e) {
            try {
                if (manualTX) {
                    Debug.logVerbose("Rolling back transaction on connection, not JTA rollback");
                    connection.rollback();
                }
            } catch (SQLException sqle2) {
                Debug.logWarning("[GenericDAO.storeAll]: SQL Exception while rolling back store. Error was:", module);
                Debug.logWarning(sqle2, module);
            }
            throw new GenericDataSourceException("Exception occured in storeAll", e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
    }
    
    /* ====================================================================== */
    
    /* ====================================================================== */
    
    public void select(GenericEntity entity) throws GenericEntityException {
        Connection connection = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
        try {
            select(entity, connection);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
    }
    
    public void select(GenericEntity entity, Connection connection) throws GenericEntityException {
        ModelEntity modelEntity = entity.getModelEntity();

        if (modelEntity == null) {
            throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
        }
        
        if (modelEntity.getPksSize() <= 0)
            throw new GenericEntityException("Entity has no primary keys, cannot select by primary key");
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        String sql = "SELECT ";

        if (modelEntity.getNopksSize() > 0)
            sql += modelEntity.colNameString(modelEntity.getNopksCopy(), ", ", "");
        else
            sql += "*";
        
        sql += makeFromClause(modelEntity);
        sql += makeWhereClauseAnd(modelEntity, modelEntity.getPksCopy(), entity);
        
        Debug.logVerbose("[GenericDAO.select] sql=" + sql, module);
        try {
            ps = connection.prepareStatement(sql);
            
            int ind = 1;
            for (int i = 0; i < modelEntity.getPksSize(); i++) {
                ModelField curField = modelEntity.getPk(i);

                //for where clause variables only setValue if not null...
                if (entity.get(curField.getName()) != null) {
                    //Debug.logInfo(" setting field " + curField.getName() + " to " + (i+1) + " entity: " + entity.toString());
                    setValue(ps, ind, curField, entity);
                    ind++;
                }
            }
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                for (int j = 0; j < modelEntity.getNopksSize(); j++) {
                    ModelField curField = modelEntity.getNopk(j);

                    getValue(rs, j + 1, curField, entity);
                }
                
                entity.modified = false;
            } else {
                //Debug.logWarning("[GenericDAO.select]: select failed, result set was empty for entity: " + entity.toString());
                throw new GenericEntityNotFoundException("Result set was empty for entity: " + entity.toString());
            }
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //Debug.logWarning(sqle.getMessage());
            throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
        } finally { 
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
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
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
        //we don't want to select ALL fields, just the nonpk fields that are in the passed GenericEntity
        Vector partialFields = new Vector();

        for (int fi = 0; fi < modelEntity.getNopksSize(); fi++) {
            ModelField curField = modelEntity.getNopk(fi);

            if (keys.contains(curField.getName()))
                partialFields.add(curField);
        }
        
        String sql = "SELECT ";

        if (partialFields.size() > 0)
            sql += modelEntity.colNameString(partialFields, ", ", "");
        else
            sql += "*";
        sql += makeFromClause(modelEntity);
        sql += makeWhereClauseAnd(modelEntity, modelEntity.getPksCopy(), entity);
        
        try {
            ps = connection.prepareStatement(sql);
            
            int ind = 1;
            for (int i = 0; i < modelEntity.getPksSize(); i++) {
                ModelField curField = modelEntity.getPk(i);

                //for where clause variables only setValue if not null...
                if (entity.get(curField.getName()) != null) {
                    setValue(ps, ind, curField, entity);
                    ind++;
                }
            }
            
            rs = ps.executeQuery();
            
            if (rs.next()) {
                for (int j = 0; j < partialFields.size(); j++) {
                    ModelField curField = (ModelField) partialFields.elementAt(j);

                    getValue(rs, j + 1, curField, entity);
                }
                
                entity.modified = false;
            } else {
                //Debug.logWarning("[GenericDAO.select]: select failed, result set was empty.");
                throw new GenericEntityNotFoundException("Result set was empty for entity: " + entity.toString());
            }
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //Debug.logWarning(sqle.getMessage());
            throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
    }
    
    public Collection selectByAnd(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        if (modelEntity == null)
            return null;
        
        Collection collection = new LinkedList();
        
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
        //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
        Vector whereFields = new Vector();
        Vector selectFields = new Vector();

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
        
        sql += makeFromClause(modelEntity);
        sql += makeWhereClauseAnd(modelEntity, whereFields, dummyValue);
        sql += makeOrderByClause(modelEntity, orderBy);
        
        Debug.logVerbose("[GenericDAO.selectByAnd] sql=" + sql, module);
        try {
            ps = connection.prepareStatement(sql);
            
            if (fields != null && fields.size() > 0) {
                int ind = 1;
                for (int i = 0; i < whereFields.size(); i++) {
                    ModelField curField = (ModelField) whereFields.elementAt(i);

                    //for where clause variables only setValue if not null...
                    if (dummyValue.get(curField.getName()) != null) {
                        setValue(ps, ind, curField, dummyValue);
                        ind++;
                    }
                }
            }
            Debug.logVerbose("[GenericDAO.selectByAnd] ps=" + ps.toString(), module);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                GenericValue value = new GenericValue(dummyValue);
                
                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.elementAt(j);

                    getValue(rs, j + 1, curField, value);
                }
                
                value.modified = false;
                collection.add(value);
            }
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO.selectByAnd]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //sqle.printStackTrace();
            throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
        return collection;
    }
    
    public Collection selectByOr(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        if (modelEntity == null)
            return null;
        
        Collection collection = new LinkedList();
        
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
        //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
        Vector whereFields = new Vector();
        Vector selectFields = new Vector();

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
        
        sql += makeFromClause(modelEntity);
        sql += makeWhereClauseOr(modelEntity, whereFields, dummyValue);
        sql += makeOrderByClause(modelEntity, orderBy);
        
        Debug.logVerbose("[GenericDAO.selectByOr] sql=" + sql, module);
        try {
            ps = connection.prepareStatement(sql);
            
            if (fields != null && fields.size() > 0) {
                int ind = 1;
                for (int i = 0; i < whereFields.size(); i++) {
                    ModelField curField = (ModelField) whereFields.elementAt(i);

                    //for where clause variables only setValue if not null...
                    if (dummyValue.get(curField.getName()) != null) {
                        setValue(ps, ind, curField, dummyValue);
                        ind++;
                    }
                }
            }
            Debug.logVerbose("[GenericDAO.selectByOr] ps=" + ps.toString(), module);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                GenericValue value = new GenericValue(dummyValue);
                
                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.elementAt(j);

                    getValue(rs, j + 1, curField, value);
                }
                
                value.modified = false;
                collection.add(value);
            }
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO.selectByOr]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //sqle.printStackTrace();
            throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
        return collection;
    }
    
    public Collection selectByAnd(ModelEntity modelEntity, List expressions, List orderBy) throws GenericEntityException {
        if (modelEntity == null)
            return null;
        
        if (expressions == null)
            return null;
        Collection collection = new LinkedList();
        
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to establish a connection with the database.", sqle);
        }
        
        //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
        Vector selectFields = modelEntity.getFieldsCopy();
        
        StringBuffer sqlBuffer = new StringBuffer("SELECT ");

        if (selectFields.size() > 0) {
            sqlBuffer.append(modelEntity.colNameString(selectFields, ", ", ""));
        } else {
            sqlBuffer.append("*");
        }
        sqlBuffer.append(" ");
        sqlBuffer.append(makeFromClause(modelEntity));
        
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
        
        String viewClause = makeViewWhereClause(modelEntity);

        if (viewClause.length() > 0) {
            whereString.append(" AND ");
            whereString.append(viewClause);
        }
        
        if (whereString.length() > 0) {
            sqlBuffer.append(" WHERE ");
            sqlBuffer.append(whereString.toString());
        }
        
        sqlBuffer.append(makeOrderByClause(modelEntity, orderBy));
        String sql = sqlBuffer.toString();

        Debug.logVerbose("[GenericDAO.selectByAnd] sql=" + sql, module);
        
        try {
            ps = connection.prepareStatement(sql);
            GenericValue dummyValue = new GenericValue(modelEntity);

            if (expressions != null && expressions.size() > 0) {
                int ind = 1;
                for (int i = 0; i < expressions.size(); i++) {
                    EntityExpr expr = (EntityExpr) expressions.get(i);

                    if (expr.getRhs() != null) {
                        ModelField field = (ModelField) modelEntity.getField((String) expr.getLhs());

                        //set the field in the dummyValue so that the setValue method can get it out
                        dummyValue.set(field.getName(), expr.getRhs());
                        setValue(ps, ind, field, dummyValue);
                        ind++;
                    }
                }
            }
            Debug.logVerbose("[GenericDAO.selectByAnd] ps=" + ps.toString(), module);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                GenericValue value = new GenericValue(modelEntity);
                
                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.elementAt(j);

                    getValue(rs, j + 1, curField, value);
                }
                
                value.modified = false;
                collection.add(value);
            }
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO.selectByAnd]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //sqle.printStackTrace();
            throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
        return collection;
    }
    
    public Collection selectByOr(ModelEntity modelEntity, List expressions, List orderBy) throws GenericEntityException {
        if (modelEntity == null)
            return null;
        
        if (expressions == null)
            return null;
        Collection collection = new LinkedList();
        
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to establish a connection with the database.", sqle);
        }
        
        //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
        Vector selectFields = modelEntity.getFieldsCopy();
        
        StringBuffer sqlBuffer = new StringBuffer("SELECT ");

        if (selectFields.size() > 0)
            sqlBuffer.append(modelEntity.colNameString(selectFields, ", ", ""));
        else
            sqlBuffer.append("*");
        sqlBuffer.append(" ");
        sqlBuffer.append(makeFromClause(modelEntity));
        
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
        
        String viewClause = makeViewWhereClause(modelEntity);

        if (viewClause.length() > 0) {
            whereString.append(" AND ");
            whereString.append(viewClause);
        }
        
        if (whereString.length() > 0) {
            sqlBuffer.append(" WHERE ");
            sqlBuffer.append(whereString);
        }
        
        sqlBuffer.append(makeOrderByClause(modelEntity, orderBy));
        String sql = sqlBuffer.toString();

        Debug.logVerbose("[GenericDAO.selectByOr] sql=" + sql, module);
        
        try {
            ps = connection.prepareStatement(sql);
            GenericValue dummyValue = new GenericValue(modelEntity);

            if (expressions != null && expressions.size() > 0) {
                int ind = 1;
                for (int i = 0; i < expressions.size(); i++) {
                    EntityExpr expr = (EntityExpr) expressions.get(i);

                    if (expr.getRhs() != null) {
                        ModelField field = (ModelField) modelEntity.getField((String) expr.getLhs());

                        //set the field in the dummyValue so that the setValue method can get it out
                        dummyValue.set(field.getName(), expr.getRhs());
                        setValue(ps, ind, field, dummyValue);
                        ind++;
                    }
                }
            }
            Debug.logVerbose("[GenericDAO.selectByOr] ps=" + ps.toString(), module);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                GenericValue value = new GenericValue(modelEntity);
                
                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.elementAt(j);

                    getValue(rs, j + 1, curField, value);
                }
                
                value.modified = false;
                collection.add(value);
            }
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO.selectByOr]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //sqle.printStackTrace();
            throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
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
        
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
        //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
        Vector whereFields = new Vector();
        Vector selectFields = new Vector();

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
        
        sql += makeOrderByClause(modelEntity, orderBy);
        Debug.logVerbose("[GenericDAO.selectByLike] sql=" + sql, module);
        
        try {
            ps = connection.prepareStatement(sql);
            
            GenericValue dummyValue;

            if (fields != null && fields.size() > 0) {
                dummyValue = new GenericValue(modelEntity, fields);
                for (int i = 0; i < whereFields.size(); i++) {
                    ModelField curField = (ModelField) whereFields.elementAt(i);

                    setValue(ps, i + 1, curField, dummyValue);
                }
            } else
                dummyValue = new GenericValue(modelEntity);
            Debug.logVerbose("[GenericDAO.selectByLike] ps=" + ps.toString(), module);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                GenericValue value = new GenericValue(dummyValue);

                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.elementAt(j);

                    getValue(rs, j + 1, curField, value);
                }
                value.modified = false;
                collection.add(value);
            }
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO.selectByLike]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //sqle.printStackTrace();
            throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
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
        
        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
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
        
        Vector whereFields = new Vector();
        Vector selectFields = new Vector();

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
        
        select.append(makeOrderByClause(modelEntity, orderBy));
        
        String sql = "";

        try {
            sql = select.toString() + " " + from.toString() + " " + where.toString() + (order.toString().trim().length() > 0 ? order.toString() : "");
            ps = connection.prepareStatement(sql);
            
            GenericValue dummyValue;

            if (fields != null && fields.size() > 0) {
                dummyValue = new GenericValue(modelEntity, fields);
                for (int i = 0; i < whereFields.size(); i++) {
                    ModelField curField = (ModelField) whereFields.elementAt(i);

                    setValue(ps, i + 1, curField, dummyValue);
                }
            } else {
                dummyValue = new GenericValue(modelEntity);
            }
            rs = ps.executeQuery();
            
            while (rs.next()) {
                GenericValue value = new GenericValue(dummyValue);
                
                for (int j = 0; j < selectFields.size(); j++) {
                    ModelField curField = (ModelField) selectFields.elementAt(j);

                    getValue(rs, j + 1, curField, value);
                }
                
                value.modified = false;
                collection.add(value);
            }
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO.selectByAnd]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //sqle.printStackTrace();
            throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
        return collection;
    }
    
    /* ====================================================================== */
    
    /* ====================================================================== */
    
    public void delete(GenericEntity entity) throws GenericEntityException {
        Connection connection = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
        boolean manualTX = true;

        try {
            connection.setAutoCommit(false);
        } catch (SQLException sqle) {
            manualTX = false;
        }
        if (TransactionUtil.getStatus() == TransactionUtil.STATUS_ACTIVE) {
            manualTX = false;
        }

        try {
            delete(entity, connection);
            if (manualTX)
                connection.commit();
        } catch (SQLException sqle) {
            try {
                if (manualTX)
                    connection.rollback();
            } catch (SQLException sqle2) {
                Debug.logWarning("SQL Exception while rolling back delete. Error was:", module);
                Debug.logWarning(sqle2, module);
            }
            throw new GenericDataSourceException("SQL Exception occured in delete", sqle);
        } catch (GenericDataSourceException e) {
            try {
                if (manualTX)
                    connection.rollback();
            } catch (SQLException sqle2) {
                Debug.logWarning("SQL Exception while rolling back delete. Error was:", module);
                Debug.logWarning(sqle2, module);
            }
            throw new GenericDataSourceException("Generic Entity Exception occured in delete", e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
    }
    
    public void delete(GenericEntity entity, Connection connection) throws GenericEntityException {
        ModelEntity modelEntity = entity.getModelEntity();

        if (modelEntity == null) {
            throw new GenericModelException("Could not find ModelEntity record for entityName: " + entity.getEntityName());
        }
        
        if (modelEntity instanceof ModelViewEntity) {
            throw new org.ofbiz.core.entity.GenericNotImplementedException("Operation delete not supported yet for view entities");
        }
        
        PreparedStatement ps = null;
        String sql = "DELETE FROM " + modelEntity.getTableName() + " WHERE " + makeWhereStringAnd(modelEntity.getPksCopy(), entity);

        try {
            ps = connection.prepareStatement(sql);
            
            int ind = 1;
            for (int i = 0; i < modelEntity.getPksSize(); i++) {
                ModelField curField = modelEntity.getPk(i);

                //for where clause variables only setValue if not null...
                if (entity.get(curField.getName()) != null) {
                    setValue(ps, ind, curField, entity);
                    ind++;
                }
            }
            
            ps.executeUpdate();
            entity.modified = true;
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO.delete]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //Debug.logWarning(sqle.getMessage());
            throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
    }
    
    public void deleteByAnd(ModelEntity modelEntity, Map fields) throws GenericEntityException {
        Connection connection = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
        boolean manualTX = true;

        try {
            connection.setAutoCommit(false);
        } catch (SQLException sqle) {
            manualTX = false;
        }
        if (TransactionUtil.getStatus() == TransactionUtil.STATUS_ACTIVE) {
            manualTX = false;
        }
        
        try {
            deleteByAnd(modelEntity, fields, connection);
            if (manualTX)
                connection.commit();
        } catch (SQLException sqle) {
            try {
                if (manualTX)
                    connection.rollback();
            } catch (SQLException sqle2) {
                Debug.logWarning("SQL Exception while rolling back delete. Error was:", module);
                Debug.logWarning(sqle2, module);
            }
            throw new GenericDataSourceException("SQL Exception occured in deleteByAnd", sqle);
        } catch (GenericDataSourceException e) {
            try {
                if (manualTX)
                    connection.rollback();
            } catch (SQLException sqle2) {
                Debug.logWarning("SQL Exception while rolling back delete. Error was:", module);
                Debug.logWarning(sqle2, module);
            }
            throw new GenericDataSourceException("Generic Entity Exception occured in deleteByAnd", e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
    }
    
    public void deleteByAnd(ModelEntity modelEntity, Map fields, Connection connection) throws GenericEntityException {
        if (modelEntity == null || fields == null)
            return;
        if (modelEntity instanceof ModelViewEntity) {
            throw new org.ofbiz.core.entity.GenericNotImplementedException("Operation deleteByAnd not supported yet for view entities");
        }
        
        PreparedStatement ps = null;
        
        //make two Vectors of fields, one for fields to select and the other for where clause fields (to find by)
        Vector whereFields = new Vector();

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

        if (fields != null || fields.size() > 0)
            sql += " WHERE " + makeWhereStringAnd(whereFields, dummyValue);
        
        try {
            ps = connection.prepareStatement(sql);
            
            if (fields != null || fields.size() > 0) {
                int ind = 1;
                for (int i = 0; i < whereFields.size(); i++) {
                    ModelField curField = (ModelField) whereFields.elementAt(i);

                    //for where clause variables only setValue if not null...
                    if (dummyValue.get(curField.getName()) != null) {
                        setValue(ps, ind, curField, dummyValue);
                        ind++;
                    }
                }
            }
            ps.executeUpdate();
        } catch (SQLException sqle) {
            //Debug.logWarning("[GenericDAO.selectByAnd]: SQL Exception while executing the following:\n" + sql + "\nError was:");
            //sqle.printStackTrace();
            throw new GenericDataSourceException("SQL Exception while executing the following:" + sql, sqle);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
        }
    }
    
    /** Called dummyPKs because they can be invalid PKs, doing a deleteByAnd instead of a normal delete */
    public void deleteAll(Collection dummyPKs) throws GenericEntityException {
        if (dummyPKs == null || dummyPKs.size() == 0) {
            return;
        }
        
        Connection connection = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("Unable to esablish a connection with the database.", sqle);
        }
        
        boolean manualTX = true;

        try {
            connection.setAutoCommit(false);
        } catch (SQLException sqle) {
            manualTX = false;
        }
        if (TransactionUtil.getStatus() == TransactionUtil.STATUS_ACTIVE) {
            manualTX = false;
        }
        
        try {
            Iterator iter = dummyPKs.iterator();

            while (iter.hasNext()) {
                GenericEntity entity = (GenericEntity) iter.next();
                
                //if it contains a complete primary key, delete the one, otherwise deleteByAnd
                if (entity.containsPrimaryKey()) {
                    delete(entity, connection);
                } else {
                    deleteByAnd(entity.getModelEntity(), entity.getAllFields(), connection);
                }
            }
            if (manualTX)
                connection.commit();
        } catch (SQLException sqle) {
            try {
                if (manualTX)
                    connection.rollback();
            } catch (SQLException sqle2) {
                Debug.logWarning("SQL Exception while rolling back delete. Error was:", module);
                Debug.logWarning(sqle2, module);
            }
            throw new GenericDataSourceException("SQL Exception occured in deleteAll", sqle);
        } catch (GenericDataSourceException e) {
            try {
                if (manualTX)
                    connection.rollback();
            } catch (SQLException sqle2) {
                Debug.logWarning("SQL Exception while rolling back delete. Error was:", module);
                Debug.logWarning(sqle2, module);
            }
            throw new GenericDataSourceException("Generic Entity Exception occured in deleteAll", e);
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {
                Debug.logWarning(sqle.getMessage(), module);
            }
            
        }
    }
    
    /* ====================================================================== */
    
    /* ====================================================================== */
    
    protected String makeFromClause(ModelEntity modelEntity) {
        StringBuffer sql = new StringBuffer("");

        if (modelEntity instanceof ModelViewEntity) {
            ModelViewEntity modelViewEntity = (ModelViewEntity) modelEntity;

            sql.append(" FROM ");
            Iterator meIter = modelViewEntity.getMemberEntityNames().entrySet().iterator();

            while (meIter.hasNext()) {
                Map.Entry entry = (Map.Entry) meIter.next();
                ModelEntity fromEntity = modelViewEntity.getMemberModelEntity((String) entry.getKey());

                sql.append(fromEntity.getTableName());
                sql.append(" ");
                sql.append((String) entry.getKey());
                if (meIter.hasNext())
                    sql.append(", ");
            }
        } else {
            sql.append(" FROM ");
            sql.append(modelEntity.getTableName());
        }
        return sql.toString();
    }
    
    /** Makes a WHERE clause String with "<col name>=?" if not null or "<col name> IS null" if null, all AND separated */
    protected String makeWhereStringAnd(Vector modelFields, GenericEntity entity) {
        StringBuffer returnString = new StringBuffer("");

        if (modelFields.size() < 1) {
            return "";
        }
        
        int i = 0;

        for (; i < modelFields.size() - 1; i++) {
            ModelField modelField = (ModelField) modelFields.elementAt(i);

            returnString.append(modelField.getColName());
            if (entity.get(modelField.getName()) != null)
                returnString.append("=? AND ");
            else
                returnString.append(" IS NULL AND ");
        }
        ModelField modelField2 = (ModelField) modelFields.elementAt(i);

        returnString.append(modelField2.getColName());
        if (entity.get(modelField2.getName()) != null)
            returnString.append("=?");
        else
            returnString.append(" IS NULL");
        return returnString.toString();
    }
    
    protected String makeWhereClauseAnd(ModelEntity modelEntity, Vector modelFields, GenericEntity entity) {
        StringBuffer whereString = new StringBuffer("");

        if (modelFields != null && modelFields.size() > 0) {
            whereString.append(makeWhereStringAnd(modelFields, entity));
        }
        
        String viewClause = makeViewWhereClause(modelEntity);

        if (viewClause.length() > 0) {
            if (whereString.length() > 0)
                whereString.append(" AND ");
            whereString.append(viewClause);
        }
        
        if (whereString.length() > 0)
            return " WHERE " + whereString.toString();
        else
            return "";
    }
    
    protected String makeWhereStringOr(Vector modelFields, GenericEntity entity) {
        StringBuffer returnString = new StringBuffer("");

        if (modelFields.size() < 1) {
            return "";
        }
        
        int i = 0;

        for (; i < modelFields.size() - 1; i++) {
            ModelField modelField = (ModelField) modelFields.elementAt(i);

            returnString.append(modelField.getColName());
            if (entity.get(modelField.getName()) != null)
                returnString.append("=? OR ");
            else
                returnString.append(" IS NULL OR ");
        }
        ModelField modelField2 = (ModelField) modelFields.elementAt(i);

        returnString.append(modelField2.getColName());
        if (entity.get(modelField2.getName()) != null)
            returnString.append("=?");
        else
            returnString.append(" IS NULL");
        return returnString.toString();
    }
    
    protected String makeWhereClauseOr(ModelEntity modelEntity, Vector modelFields, GenericEntity entity) {
        StringBuffer whereString = new StringBuffer("");

        if (modelFields != null && modelFields.size() > 0) {
            whereString.append(makeWhereStringOr(modelFields, entity));
        }
        
        String viewClause = makeViewWhereClause(modelEntity);

        if (viewClause.length() > 0) {
            if (whereString.length() > 0)
                whereString.append(" AND ");
            whereString.append(viewClause);
        }
        
        if (whereString.length() > 0)
            return " WHERE " + whereString.toString();
        else
            return "";
    }
    
    protected String makeViewWhereClause(ModelEntity modelEntity) {
        StringBuffer whereString = new StringBuffer("");

        if (modelEntity instanceof ModelViewEntity) {
            ModelViewEntity modelViewEntity = (ModelViewEntity) modelEntity;
            
            for (int i = 0; i < modelViewEntity.getViewLinksSize(); i++) {
                ModelViewEntity.ModelViewLink viewLink = modelViewEntity.getViewLink(i);
                
                ModelEntity linkEntity = (ModelEntity) modelViewEntity.getMemberModelEntity(viewLink.getEntityAlias());
                ModelEntity relLinkEntity = (ModelEntity) modelViewEntity.getMemberModelEntity(viewLink.getRelEntityAlias());
                
                for (int j = 0; j < viewLink.getKeyMapsSize(); j++) {
                    ModelKeyMap keyMap = viewLink.getKeyMap(j);
                    ModelField linkField = linkEntity.getField(keyMap.getFieldName());
                    ModelField relLinkField = relLinkEntity.getField(keyMap.getRelFieldName());
                    
                    if (whereString.length() > 0)
                        whereString.append(" AND ");
                    whereString.append(viewLink.getEntityAlias());
                    whereString.append(".");
                    whereString.append(linkField.getColName());
                    whereString.append("=");
                    whereString.append(viewLink.getRelEntityAlias());
                    whereString.append(".");
                    whereString.append(relLinkField.getColName());
                }
            }
        }
        return whereString.toString();
    }
    
    protected String makeOrderByClause(ModelEntity modelEntity, List orderBy) {
        StringBuffer sql = new StringBuffer("");

        if (orderBy != null && orderBy.size() > 0) {
            Debug.logVerbose("Order by list contains: " + orderBy.size() + " entries.");
            List orderByStrings = new LinkedList();
            
            for (int oi = 0; oi < orderBy.size(); oi++) {
                String keyName = (String) orderBy.get(oi);
                String ext = null;
                
                // check for ASC/DESC
                int spaceIdx = keyName.indexOf(" ");

                if (spaceIdx > 0) {
                    ext = keyName.substring(spaceIdx);
                    keyName = keyName.substring(0, spaceIdx);
                }
                // optional way -/+
                if (keyName.startsWith("-") || keyName.startsWith("+")) {
                    ext = keyName.startsWith("-") ? " DESC" : " ASC";
                    keyName = keyName.substring(1);
                }
                
                for (int fi = 0; fi < modelEntity.getFieldsSize(); fi++) {
                    ModelField curField = modelEntity.getField(fi);

                    if (curField.getName().equals(keyName)) {
                        if (ext != null)
                            orderByStrings.add(curField.getColName() + ext);
                        else
                            orderByStrings.add(curField.getColName());
                    }
                }
            }
            
            if (orderByStrings.size() > 0) {
                sql.append(" ORDER BY ");
                
                Iterator iter = orderByStrings.iterator();

                while (iter.hasNext()) {
                    String curString = (String) iter.next();

                    sql.append(curString);
                    if (iter.hasNext())
                        sql.append(", ");
                }
            }
        }
        Debug.logVerbose("makeOrderByClause: " + sql.toString(), module);
        return sql.toString();
    }
    
    /* ====================================================================== */
    
    /* ====================================================================== */
    
    public void getValue(ResultSet rs, int ind, ModelField curField, GenericEntity entity) throws SQLException, GenericEntityException {
        ModelFieldType mft = modelFieldTypeReader.getModelFieldType(curField.getType());

        if (mft == null) {
            throw new GenericModelException("definition fieldType " + curField.getType() + " not found, cannot getValue for field " +
                    entity.getEntityName() + "." + curField.getName() + ".");
        }
        String fieldType = mft.getJavaType();
        
        if (fieldType.equals("java.lang.String") || fieldType.equals("String")) {
            entity.set(curField.getName(), rs.getString(ind));
        } else if (fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp")) {
            entity.set(curField.getName(), rs.getTimestamp(ind));
        } else if (fieldType.equals("java.sql.Time") || fieldType.equals("Time")) {
            entity.set(curField.getName(), rs.getTime(ind));
        } else if (fieldType.equals("java.sql.Date") || fieldType.equals("Date")) {
            entity.set(curField.getName(), rs.getDate(ind));
        } else if (fieldType.equals("java.lang.Integer") || fieldType.equals("Integer")) {
            if (rs.getObject(ind) == null)
                entity.set(curField.getName(), null);
            else
                entity.set(curField.getName(), new Integer(rs.getInt(ind)));
        } else if (fieldType.equals("java.lang.Long") || fieldType.equals("Long")) {
            if (rs.getObject(ind) == null)
                entity.set(curField.getName(), null);
            else
                entity.set(curField.getName(), new Long(rs.getLong(ind)));
        } else if (fieldType.equals("java.lang.Float") || fieldType.equals("Float")) {
            if (rs.getObject(ind) == null)
                entity.set(curField.getName(), null);
            else
                entity.set(curField.getName(), new Float(rs.getFloat(ind)));
        } else if (fieldType.equals("java.lang.Double") || fieldType.equals("Double")) {
            if (rs.getObject(ind) == null)
                entity.set(curField.getName(), null);
            else
                entity.set(curField.getName(), new Double(rs.getDouble(ind)));
        } else {
            throw new GenericNotImplementedException("Java type " + fieldType + " not currently supported. Sorry.");
        }
    }
    
    public void setValue(PreparedStatement ps, int ind, ModelField curField, GenericEntity entity) throws SQLException, GenericEntityException {
        Object field = entity.get(curField.getName());
        //there should be no parameter for null fields, so we can just return and do nothing
        
        ModelFieldType mft = modelFieldTypeReader.getModelFieldType(curField.getType());

        if (mft == null) {
            throw new GenericModelException("GenericDAO.getValue: definition fieldType " + curField.getType() + " not found, cannot setValue for field " +
                    entity.getEntityName() + "." + curField.getName() + ".");
        }
        
        String fieldType = mft.getJavaType();

        if (field != null) {
            Class fieldClass = field.getClass();
            String fieldClassName = fieldClass.getName();

            if (!fieldClassName.equals(mft.getJavaType()) && fieldClassName.indexOf(mft.getJavaType()) < 0) {
                Debug.logWarning("type of field " + entity.getEntityName() + "." + curField.getName() +
                    " is " + fieldClassName + ", was expecting " + mft.getJavaType() + "; this may " +
                    "indicate an error in the configuration or in the class, and may result " +
                    "in an SQL-Java data conversion error. Will use the real field type: " +
                    fieldClassName + ", not the definition.", module);
                fieldType = fieldClassName;
            }
        }
        
        if (fieldType.equals("java.lang.String") || fieldType.equals("String")) {
            if (field != null)
                ps.setString(ind, (String) field);
            else
                ps.setNull(ind, Types.VARCHAR);
        } else if (fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp")) {
            if (field != null)
                ps.setTimestamp(ind, (java.sql.Timestamp) field);
            else
                ps.setNull(ind, Types.TIMESTAMP);
        } else if (fieldType.equals("java.sql.Time") || fieldType.equals("Time")) {
            if (field != null)
                ps.setTime(ind, (java.sql.Time) field);
            else
                ps.setNull(ind, Types.TIME);
        } else if (fieldType.equals("java.sql.Date") || fieldType.equals("Date")) {
            if (field != null)
                ps.setDate(ind, (java.sql.Date) field);
            else
                ps.setNull(ind, Types.DATE);
        } else if (fieldType.equals("java.lang.Integer") || fieldType.equals("Integer")) {
            if (field != null)
                ps.setInt(ind, ((java.lang.Integer) field).intValue());
            else
                ps.setNull(ind, Types.NUMERIC);
        } else if (fieldType.equals("java.lang.Long") || fieldType.equals("Long")) {
            if (field != null)
                ps.setLong(ind, ((java.lang.Long) field).longValue());
            else
                ps.setNull(ind, Types.NUMERIC);
        } else if (fieldType.equals("java.lang.Float") || fieldType.equals("Float")) {
            if (field != null)
                ps.setFloat(ind, ((java.lang.Float) field).floatValue());
            else
                ps.setNull(ind, Types.NUMERIC);
        } else if (fieldType.equals("java.lang.Double") || fieldType.equals("Double")) {
            if (field != null)
                ps.setDouble(ind, ((java.lang.Double) field).doubleValue());
            else
                ps.setNull(ind, Types.NUMERIC);
        } else {
            throw new GenericNotImplementedException("Java type " + fieldType + " not currently supported. Sorry.");
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
