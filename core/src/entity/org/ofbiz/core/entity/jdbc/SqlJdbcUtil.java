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

    /** Makes the FROM clause and when necessary the JOIN clause(s) as well */
    public static String makeFromClause(ModelEntity modelEntity, String joinStyle) throws GenericEntityException {
        StringBuffer sql = new StringBuffer(" FROM ");
        
        if (modelEntity instanceof ModelViewEntity) {
            ModelViewEntity modelViewEntity = (ModelViewEntity) modelEntity;
            
            if ("ansi".equals(joinStyle)) {
                String firstMemberAlias = null;
                
                //FROM clause: very simple, just use the first required member entity
                if (modelViewEntity.getRequiredModelMemberEntities().size() == 0) {
                    throw new GenericModelException("The " + modelViewEntity.getEntityName() + " view-entity has no required member entities but must have at least one (ie full joins not supported)");
                } else {
                    ModelViewEntity.ModelMemberEntity modelMemberEntity = (ModelViewEntity.ModelMemberEntity) modelViewEntity.getRequiredModelMemberEntities().get(0);
                    ModelEntity fromEntity = modelViewEntity.getMemberModelEntity(modelMemberEntity.getEntityAlias());
                    sql.append(fromEntity.getTableName());
                    sql.append(" ");
                    sql.append(modelMemberEntity.getEntityAlias());
                    
                    firstMemberAlias = modelMemberEntity.getEntityAlias();
                }
                
                //JOIN clause(s): a bit more complex, must have join conditions, etc

                //BIG NOTE on the JOIN clauses: not sure if all databases will support this syntax, may have
                //  to build a tree of view-links and nest the JOINs appropriately...
                
                //first required members, except the first one (JOIN)
                Iterator requiredIter = modelViewEntity.getRequiredModelMemberEntities().iterator();
                //skip the first, if no first an exception will be thrown above:
                requiredIter.next();
                while (requiredIter.hasNext()) {
                    ModelViewEntity.ModelMemberEntity modelMemberEntity = (ModelViewEntity.ModelMemberEntity) requiredIter.next();
                    ModelEntity fromEntity = modelViewEntity.getMemberModelEntity(modelMemberEntity.getEntityAlias());
                    String currentMemberAlias = modelMemberEntity.getEntityAlias();
                    
                    sql.append(" JOIN ");
                    sql.append(fromEntity.getTableName());
                    sql.append(" ");
                    sql.append(currentMemberAlias);
                    sql.append(" ON ");
                    
                    //get the view maps going from any other entity to this entity OR 
                    //  between this member entity and the first/from member entity
                    StringBuffer condBuffer = new StringBuffer();
                    for (int i = 0; i < modelViewEntity.getViewLinksSize(); i++) {
                        ModelViewEntity.ModelViewLink viewLink = modelViewEntity.getViewLink(i);

                        if (currentMemberAlias.equals(viewLink.getRelEntityAlias()) || 
                                (currentMemberAlias.equals(viewLink.getEntityAlias()) && firstMemberAlias.equals(viewLink.getRelEntityAlias()))) {
                        
                            ModelEntity linkEntity = modelViewEntity.getMemberModelEntity(viewLink.getEntityAlias());
                            ModelEntity relLinkEntity = modelViewEntity.getMemberModelEntity(viewLink.getRelEntityAlias());

                            for (int j = 0; j < viewLink.getKeyMapsSize(); j++) {
                                ModelKeyMap keyMap = viewLink.getKeyMap(j);
                                ModelField linkField = linkEntity.getField(keyMap.getFieldName());
                                ModelField relLinkField = relLinkEntity.getField(keyMap.getRelFieldName());

                                if (condBuffer.length() > 0) {
                                    condBuffer.append(" AND ");
                                }
                                condBuffer.append(viewLink.getEntityAlias());
                                condBuffer.append(".");
                                condBuffer.append(linkField.getColName());
                                condBuffer.append("=");
                                condBuffer.append(viewLink.getRelEntityAlias());
                                condBuffer.append(".");
                                condBuffer.append(relLinkField.getColName());
                            }
                        }
                    }
                    
                    if (condBuffer.length() == 0) {
                        throw new GenericModelException("No join conditions (view-links) found for the " + modelMemberEntity.getEntityAlias() + " aliased member-entity of the " + modelViewEntity.getEntityName() + " view-entity.");
                    }
                    
                    sql.append(condBuffer.toString());
                }
                
                //then optional members (LEFT JOIN)
                Iterator optionalIter = modelViewEntity.getOptionalModelMemberEntities().iterator();
                while (optionalIter.hasNext()) {
                    ModelViewEntity.ModelMemberEntity modelMemberEntity = (ModelViewEntity.ModelMemberEntity) optionalIter.next();
                    ModelEntity fromEntity = modelViewEntity.getMemberModelEntity(modelMemberEntity.getEntityAlias());
                    String currentMemberAlias = modelMemberEntity.getEntityAlias();
                    
                    sql.append(" LEFT JOIN ");
                    sql.append(fromEntity.getTableName());
                    sql.append(" ");
                    sql.append(currentMemberAlias);
                    sql.append(" ON ");
                    
                    //get the view maps going from any other entity to this entity OR 
                    //  between this member entity and the first/from member entity
                    StringBuffer condBuffer = new StringBuffer();
                    for (int i = 0; i < modelViewEntity.getViewLinksSize(); i++) {
                        ModelViewEntity.ModelViewLink viewLink = modelViewEntity.getViewLink(i);

                        if (currentMemberAlias.equals(viewLink.getRelEntityAlias()) || 
                                (currentMemberAlias.equals(viewLink.getEntityAlias()) && firstMemberAlias.equals(viewLink.getRelEntityAlias()))) {
                        
                            ModelEntity linkEntity = modelViewEntity.getMemberModelEntity(viewLink.getEntityAlias());
                            ModelEntity relLinkEntity = modelViewEntity.getMemberModelEntity(viewLink.getRelEntityAlias());

                            for (int j = 0; j < viewLink.getKeyMapsSize(); j++) {
                                ModelKeyMap keyMap = viewLink.getKeyMap(j);
                                ModelField linkField = linkEntity.getField(keyMap.getFieldName());
                                ModelField relLinkField = relLinkEntity.getField(keyMap.getRelFieldName());

                                if (condBuffer.length() > 0) {
                                    condBuffer.append(" AND ");
                                }
                                condBuffer.append(viewLink.getEntityAlias());
                                condBuffer.append(".");
                                condBuffer.append(linkField.getColName());
                                condBuffer.append("=");
                                condBuffer.append(viewLink.getRelEntityAlias());
                                condBuffer.append(".");
                                condBuffer.append(relLinkField.getColName());
                            }
                        }
                    }
                    
                    if (condBuffer.length() == 0) {
                        throw new GenericModelException("No join conditions (view-links) found for the " + modelMemberEntity.getEntityAlias() + " aliased member-entity of the " + modelViewEntity.getEntityName() + " view-entity.");
                    }
                    
                    sql.append(condBuffer.toString());
                }
                
            } else if ("theta-oracle".equals(joinStyle) || "theta-mssql".equals(joinStyle)) {
                //FROM clause
                Iterator meIter = modelViewEntity.getMemberModelMemberEntities().entrySet().iterator();
                while (meIter.hasNext()) {
                    Map.Entry entry = (Map.Entry) meIter.next();
                    ModelEntity fromEntity = modelViewEntity.getMemberModelEntity((String) entry.getKey());

                    sql.append(fromEntity.getTableName());
                    sql.append(" ");
                    sql.append((String) entry.getKey());
                    if (meIter.hasNext()) sql.append(", ");
                }
                
                //JOIN clause(s): none needed, all the work done in the where clause for theta-oracle
            } else {
                throw new GenericModelException("The join-style " + joinStyle + " is not yet supported");
            }
        } else {
            sql.append(modelEntity.getTableName());
        }
        return sql.toString();
    }
    
    /** Makes a WHERE clause String with "<col name>=?" if not null or "<col name> IS null" if null, all AND separated */
    public static String makeWhereStringFromFields(List modelFields, Map fields, String operator) {
        return makeWhereStringFromFields(modelFields, fields, operator, null);
    }
    
    /** Makes a WHERE clause String with "<col name>=?" if not null or "<col name> IS null" if null, all AND separated */
    public static String makeWhereStringFromFields(List modelFields, Map fields, String operator, List entityConditionParams) {
        StringBuffer returnString = new StringBuffer("");

        if (modelFields.size() < 1) {
            return "";
        }
        Iterator iter = modelFields.iterator();
        while (iter.hasNext()) {
            ModelField modelField = (ModelField) iter.next();

            returnString.append(modelField.getColName());
            Object fieldValue = fields.get(modelField.getName());
            if (fieldValue != null) {
                returnString.append("=?");
                if (entityConditionParams != null) {
                    entityConditionParams.add(new EntityConditionParam(modelField, fieldValue));
                }
            } else {
                returnString.append(" IS NULL");
            }

            if (iter.hasNext()) {
                returnString.append(' ');
                returnString.append( operator );
                returnString.append(' ');
            }
        }

        return returnString.toString();
    }

    public static String makeWhereClause(ModelEntity modelEntity, List modelFields, Map fields, String operator, String joinStyle) throws GenericEntityException {
        StringBuffer whereString = new StringBuffer("");

        if (modelFields != null && modelFields.size() > 0) {
            whereString.append(makeWhereStringFromFields(modelFields, fields, "AND"));
        }

        String viewClause = makeViewWhereClause(modelEntity, joinStyle);

        if (viewClause.length() > 0) {
            if (whereString.length() > 0) {
                whereString.append(' ');
                whereString.append(operator);
                whereString.append(' ');
            }

            whereString.append(viewClause);
        }

        if (whereString.length() > 0) {
            return " WHERE " + whereString.toString();
        }

        return "";
    }

    public static String makeViewWhereClause(ModelEntity modelEntity, String joinStyle) throws GenericEntityException {
        if (modelEntity instanceof ModelViewEntity) {
            StringBuffer whereString = new StringBuffer("");
            ModelViewEntity modelViewEntity = (ModelViewEntity) modelEntity;
            
            if ("ansi".equals(joinStyle)) {
                //nothing to do here, all done in the JOIN clauses
            } else if ("theta-oracle".equals(joinStyle) || "theta-mssql".equals(joinStyle)) {
                boolean isOracleStyle = "theta-oracle".equals(joinStyle);
                boolean isMssqlStyle = "theta-mssql".equals(joinStyle);
                
                for (int i = 0; i < modelViewEntity.getViewLinksSize(); i++) {
                    ModelViewEntity.ModelViewLink viewLink = modelViewEntity.getViewLink(i);

                    ModelEntity linkEntity = modelViewEntity.getMemberModelEntity(viewLink.getEntityAlias());
                    ModelEntity relLinkEntity = modelViewEntity.getMemberModelEntity(viewLink.getRelEntityAlias());

                    ModelViewEntity.ModelMemberEntity linkMemberEntity = modelViewEntity.getMemberModelMemberEntity(viewLink.getEntityAlias());
                    ModelViewEntity.ModelMemberEntity relLinkMemberEntity = modelViewEntity.getMemberModelMemberEntity(viewLink.getRelEntityAlias());
                    
                    for (int j = 0; j < viewLink.getKeyMapsSize(); j++) {
                        ModelKeyMap keyMap = viewLink.getKeyMap(j);
                        ModelField linkField = linkEntity.getField(keyMap.getFieldName());
                        ModelField relLinkField = relLinkEntity.getField(keyMap.getRelFieldName());

                        if (whereString.length() > 0) {
                            whereString.append(" AND ");
                        }
                        whereString.append(viewLink.getEntityAlias());
                        whereString.append(".");
                        whereString.append(linkField.getColName());
                        
                        //check to see whether the left or right members are optional, if so:
                        //  oracle: use the (+) on the optional side
                        //  mssql: use the * on the required side
                        
                        //NOTE: not testing if original table is optional, ONLY if related table is optional; otherwise things get really ugly...
                        //if (isOracleStyle && linkMemberEntity.getOptional()) whereString.append(" (+) ");
                        if (isMssqlStyle && relLinkMemberEntity.getOptional()) whereString.append("*");
                        whereString.append("=");
                        //if (isMssqlStyle && linkMemberEntity.getOptional()) whereString.append("*");
                        if (isOracleStyle && relLinkMemberEntity.getOptional()) whereString.append(" (+) ");
                        
                        whereString.append(viewLink.getRelEntityAlias());
                        whereString.append(".");
                        whereString.append(relLinkField.getColName());
                    }
                }
            } else {
                throw new GenericModelException("The join-style " + joinStyle + " is not yet supported");
            }
            
            if (whereString.length() > 0) {
                return "(" + whereString.toString() + ")";
            }
        }
        return "";
    }
    
    public static String makeOrderByClause(ModelEntity modelEntity, List orderBy) {
        StringBuffer sql = new StringBuffer("");
        
        if (orderBy != null && orderBy.size() > 0) {
            if (Debug.verboseOn()) Debug.logVerbose("Order by list contains: " + orderBy.size() + " entries.", module);
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
        if (Debug.verboseOn()) Debug.logVerbose("makeOrderByClause: " + sql.toString(), module);
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
            if (entity.dangerousGetNoCheckButFast(curField) != null) {
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
            //checking to see if the object is null is really only necessary for the numbers
            int typeValue = getType(fieldType);
            if (typeValue <= 4 || typeValue == 10) {
                switch (typeValue) {
                    case 1: entity.dangerousSetNoCheckButFast(curField, rs.getString(ind)); break;
                    case 2: entity.dangerousSetNoCheckButFast(curField, rs.getTimestamp(ind)); break;
                    case 3: entity.dangerousSetNoCheckButFast(curField, rs.getTime(ind)); break;
                    case 4: entity.dangerousSetNoCheckButFast(curField, rs.getDate(ind)); break;
                    case 10: entity.dangerousSetNoCheckButFast(curField, rs.getObject(ind)); break;
                }
            } else {
                switch (typeValue) {
                    case 5:
                        int intValue = rs.getInt(ind);
                        if (rs.wasNull()) {
                            entity.dangerousSetNoCheckButFast(curField, null);
                        } else {
                            entity.dangerousSetNoCheckButFast(curField, new Integer(intValue));
                        }
                        break;
                    case 6:
                        long longValue = rs.getLong(ind);
                        if (rs.wasNull()) {
                            entity.dangerousSetNoCheckButFast(curField, null);
                        } else {
                            entity.dangerousSetNoCheckButFast(curField, new Long(longValue));
                        }
                        break;
                    case 7:
                        float floatValue = rs.getFloat(ind);
                        if (rs.wasNull()) {
                            entity.dangerousSetNoCheckButFast(curField, null);
                        } else {
                            entity.dangerousSetNoCheckButFast(curField, new Float(floatValue));
                        }
                        break;
                    case 8:
                        double doubleValue = rs.getDouble(ind);
                        if (rs.wasNull()) {
                            entity.dangerousSetNoCheckButFast(curField, null);
                        } else {
                            entity.dangerousSetNoCheckButFast(curField, new Double(doubleValue));
                        }
                        break;
                    case 9:
                        boolean booleanValue = rs.getBoolean(ind);
                        if (rs.wasNull()) {
                            entity.dangerousSetNoCheckButFast(curField, null);
                        } else {
                            entity.dangerousSetNoCheckButFast(curField, new Boolean(booleanValue));
                        }
                        break;
                }
            }
        } catch (SQLException sqle) {
            throw new GenericDataSourceException("SQL Exception while getting value: ", sqle);
        }
    }
    
    public static void setValue(SQLProcessor sqlP, ModelField modelField, GenericEntity entity, ModelFieldTypeReader modelFieldTypeReader) throws GenericEntityException {
        Object fieldValue = entity.dangerousGetNoCheckButFast(modelField);
        setValue(sqlP, modelField, entity.getEntityName(), fieldValue, modelFieldTypeReader);
    }
    
    public static void setValue(SQLProcessor sqlP, ModelField modelField, String entityName, Object fieldValue, ModelFieldTypeReader modelFieldTypeReader) throws GenericEntityException {
        ModelFieldType mft = modelFieldTypeReader.getModelFieldType(modelField.getType());
        
        if (mft == null) {
            throw new GenericModelException("GenericDAO.getValue: definition fieldType " + modelField.getType() + " not found, cannot setValue for field " +
            entityName + "." + modelField.getName() + ".");
        }
        
        String fieldType = mft.getJavaType();
        
        if (fieldValue != null) {
            Class fieldClass = fieldValue.getClass();
            String fieldClassName = fieldClass.getName();
            
            if (!fieldClassName.equals(mft.getJavaType()) && fieldClassName.indexOf(mft.getJavaType()) < 0) {
                Debug.logWarning("type of field " + entityName + "." + modelField.getName() +
                " is " + fieldClassName + ", was expecting " + mft.getJavaType() + "; this may " +
                "indicate an error in the configuration or in the class, and may result " +
                "in an SQL-Java data conversion error. Will use the real field type: " +
                fieldClassName + ", not the definition.", module);
                fieldType = fieldClassName;
            }
        }
        
        try {
            int typeValue = getType(fieldType);
            switch (typeValue) {
                case 1: sqlP.setValue((String) fieldValue); break;
                case 2: sqlP.setValue((java.sql.Timestamp) fieldValue); break;
                case 3: sqlP.setValue((java.sql.Time) fieldValue); break;
                case 4: sqlP.setValue((java.sql.Date) fieldValue); break;
                case 5: sqlP.setValue((java.lang.Integer) fieldValue); break;
                case 6: sqlP.setValue((java.lang.Long) fieldValue); break;
                case 7: sqlP.setValue((java.lang.Float) fieldValue); break;
                case 8: sqlP.setValue((java.lang.Double) fieldValue); break;
                case 9: sqlP.setValue((java.lang.Boolean) fieldValue); break;
                case 10: sqlP.setValue(fieldValue); break;
            }
        } catch (SQLException sqle) {
            throw new GenericDataSourceException( "SQL Exception while setting value: ", sqle);
        }
    }
    
    protected static HashMap fieldTypeMap = new HashMap();
    static {
        fieldTypeMap.put( "java.lang.String",   new Integer( 1 ));
        fieldTypeMap.put( "String",             new Integer( 1 ));
        fieldTypeMap.put( "java.sql.Timestamp", new Integer( 2 ));
        fieldTypeMap.put( "Timestamp",          new Integer( 2 ));
        fieldTypeMap.put( "java.sql.Time",      new Integer( 3 ));
        fieldTypeMap.put( "Time",               new Integer( 3 ));
        fieldTypeMap.put( "java.sql.Date",      new Integer( 4 ));
        fieldTypeMap.put( "Date",               new Integer( 4 ));
        fieldTypeMap.put( "java.lang.Integer",  new Integer( 5 ));
        fieldTypeMap.put( "Integer",            new Integer( 5 ));
        fieldTypeMap.put( "java.lang.Long",     new Integer( 6 ));
        fieldTypeMap.put( "Long",               new Integer( 6 ));
        fieldTypeMap.put( "java.lang.Float",    new Integer( 7 ));
        fieldTypeMap.put( "Float",              new Integer( 7 ));
        fieldTypeMap.put( "java.lang.Double",   new Integer( 8 ));
        fieldTypeMap.put( "Double",             new Integer( 8 ));
        fieldTypeMap.put( "java.lang.Boolean",  new Integer( 9 ));
        fieldTypeMap.put( "Boolean",            new Integer( 9 ));
        fieldTypeMap.put( "java.lang.Object",   new Integer( 10 ));
        fieldTypeMap.put( "Object",             new Integer( 10 ));
    }

    public static int getType(String fieldType) throws GenericNotImplementedException {
        Integer val = (Integer) fieldTypeMap.get(fieldType);
        if (val == null) {
            throw new GenericNotImplementedException("Java type " + fieldType + " not currently supported. Sorry.");
        }
        return val.intValue();
    }
}
