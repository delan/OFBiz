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

package org.ofbiz.core.entity.jdbc;


import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import org.w3c.dom.Element;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.config.*;
import org.ofbiz.core.entity.model.*;
import org.ofbiz.core.entity.*;


/**
 * GenericDAO Utility methods for general tasks
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href="mailto:chris_maurer@altavista.com">Chris Maurer</a>
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     <a href="mailto:jdonnerstag@eds.de">Juergen Donnerstag</a>
 *@created    Wed Aug 08 2001
 *@version    1.0
 */
public class SqlJdbcUtil {
    public static final String module = GenericDAO.class.getName();
    
    public static String makeFromClause(ModelEntity modelEntity) {
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
    public static String makeWhereStringAnd(List modelFields, GenericEntity entity) {
        StringBuffer returnString = new StringBuffer("");
        
        if (modelFields.size() < 1) {
            return "";
        }
        
        int i=0;
        for (; i < modelFields.size() - 1; i++) {
            ModelField modelField = (ModelField) modelFields.get(i);
            
            returnString.append(modelField.getColName());
            if (entity.get(modelField.getName()) != null)
                returnString.append("=? AND ");
            else
                returnString.append(" IS NULL AND ");
        }
        ModelField modelField2 = (ModelField) modelFields.get(i);
        
        returnString.append(modelField2.getColName());
        if (entity.get(modelField2.getName()) != null)
            returnString.append("=?");
        else
            returnString.append(" IS NULL");
        return returnString.toString();
    }
    
    public static String makeWhereClauseAnd(ModelEntity modelEntity, List modelFields, GenericEntity entity) {
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
    
    public static String makeWhereStringOr(List modelFields, GenericEntity entity) {
        StringBuffer returnString = new StringBuffer("");
        
        if (modelFields.size() < 1) {
            return "";
        }
        
        int i = 0;
        for (; i < modelFields.size() - 1; i++) {
            ModelField modelField = (ModelField) modelFields.get(i);
            
            returnString.append(modelField.getColName());
            if (entity.get(modelField.getName()) != null)
                returnString.append("=? OR ");
            else
                returnString.append(" IS NULL OR ");
        }
        ModelField modelField2 = (ModelField) modelFields.get(i);
        
        returnString.append(modelField2.getColName());
        if (entity.get(modelField2.getName()) != null)
            returnString.append("=?");
        else
            returnString.append(" IS NULL");
        return returnString.toString();
    }
    
    public static String makeWhereClauseOr(ModelEntity modelEntity, List modelFields, GenericEntity entity) {
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
    
    public static String makeViewWhereClause(ModelEntity modelEntity) {
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
    
    public static String makeOrderByClause(ModelEntity modelEntity, List orderBy) {
        StringBuffer sql = new StringBuffer("");
        
        if (orderBy != null && orderBy.size() > 0) {
            Debug.logVerbose("Order by list contains: " + orderBy.size() + " entries.", module);
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
    
    /**
     *  The elements (ModelFields) of the list are bound to an SQL statement
     *  (SQL-Processor)
     *
     * @param sqlP
     * @param list
     * @param entity
     * @throws GenericEntityException
     */
    public static void setValues(SQLProcessor sqlP, List list, GenericEntity entity, ModelFieldTypeReader modelFieldTypeReader) throws GenericEntityException {
        for (int i = 0; i < list.size(); i++) {
            ModelField curField = (ModelField) list.get(i);
            setValue(sqlP, curField, entity, modelFieldTypeReader);
        }
    }
    
    /**
     *  The elements (ModelFields) of the list are bound to an SQL statement
     *  (SQL-Processor), but values must not be null.
     *
     * @param sqlP
     * @param list
     * @param entity
     * @throws GenericEntityException
     */
    public static void setValuesWhereClause(SQLProcessor sqlP, List list, GenericValue dummyValue, ModelFieldTypeReader modelFieldTypeReader) throws GenericEntityException {
        
        for (int i = 0; i < list.size(); i++) {
            ModelField curField = (ModelField) list.get(i);
            
            //for where clause variables only setValue if not null...
            if (dummyValue.get(curField.getName()) != null) {
                setValue(sqlP, curField, dummyValue, modelFieldTypeReader);
            }
        }
    }
    
    /**
     *  Get all primary keys from the model entity and bind their values
     *  to the an SQL statement (SQL-Processor)
     *
     * @param sqlP
     * @param list
     * @param entity
     * @throws GenericEntityException
     */
    public static void setPkValues(SQLProcessor sqlP, ModelEntity modelEntity, GenericEntity entity, ModelFieldTypeReader modelFieldTypeReader) throws GenericEntityException {
        for (int j = 0; j < modelEntity.getPksSize(); j++) {
            ModelField curField = modelEntity.getPk(j);
            
            //for where clause variables only setValue if not null...
            if (entity.get(curField.getName()) != null) {
                setValue(sqlP, curField, entity, modelFieldTypeReader);
            }
        }
    }
    
    public static void getValue(ResultSet rs, int ind, ModelField curField, GenericEntity entity, ModelFieldTypeReader modelFieldTypeReader) throws GenericEntityException {
        ModelFieldType mft = modelFieldTypeReader.getModelFieldType(curField.getType());
        
        if (mft == null) {
            throw new GenericModelException("definition fieldType " + curField.getType() + " not found, cannot getValue for field " +
            entity.getEntityName() + "." + curField.getName() + ".");
        }
        String fieldType = mft.getJavaType();
        
        try {
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
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("SQL Exception: ", sqle);
        }
    }
    
    public static void setValue(SQLProcessor sqlP, ModelField curField, GenericEntity entity, ModelFieldTypeReader modelFieldTypeReader) throws GenericEntityException {
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
        
        try {
            if (fieldType.equals("java.lang.String") || fieldType.equals("String")) {
                sqlP.setValue((String) field);
            } else if (fieldType.equals("java.sql.Timestamp") || fieldType.equals("Timestamp")) {
                sqlP.setValue((java.sql.Timestamp) field);
            } else if (fieldType.equals("java.sql.Time") || fieldType.equals("Time")) {
                sqlP.setValue((java.sql.Time) field);
            } else if (fieldType.equals("java.sql.Date") || fieldType.equals("Date")) {
                sqlP.setValue((java.sql.Date) field);
            } else if (fieldType.equals("java.lang.Integer") || fieldType.equals("Integer")) {
                sqlP.setValue((java.lang.Integer) field);
            } else if (fieldType.equals("java.lang.Long") || fieldType.equals("Long")) {
                sqlP.setValue((java.lang.Long) field);
            } else if (fieldType.equals("java.lang.Float") || fieldType.equals("Float")) {
                sqlP.setValue((java.lang.Float) field);
            } else if (fieldType.equals("java.lang.Double") || fieldType.equals("Double")) {
                sqlP.setValue((java.lang.Double) field);
            } else {
                throw new GenericNotImplementedException("Java type " + fieldType + " not currently supported. Sorry.");
            }
        } catch (SQLException sqle) {
            throw new GenericDataSourceException( "SQL Exception: ", sqle);
        }
    }
}
