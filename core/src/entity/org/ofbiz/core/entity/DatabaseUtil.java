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
 * Utilities for Entity Database Maintenance
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    April 14 2002
 *@version    1.0
 */
public class DatabaseUtil {
    
    public static final String module = DatabaseUtil.class.getName();
    
    protected String helperName;
    protected ModelFieldTypeReader modelFieldTypeReader = null;
    
    public DatabaseUtil(String helperName) {
        this.helperName = helperName;
        modelFieldTypeReader = ModelFieldTypeReader.getModelFieldTypeReader(helperName);
    }
    
    public Connection getConnection() throws SQLException, GenericEntityException {
        Connection connection = ConnectionFactory.getConnection(helperName);
        return connection;
    }
    
    /* ====================================================================== */
    
    /* ====================================================================== */
    
    public void checkDb(Map modelEntities, Collection messages, boolean addMissing) {
        Element rootElement = null;
        Element datasourceElement = null;

        try {
            rootElement = EntityConfigUtil.getXmlRootElement();
            datasourceElement = UtilXml.firstChildElement(rootElement, "datasource", "name", helperName);
        } catch (GenericEntityConfException e) {
            Debug.logError(e, "Error loading entity config XML file");
        }
        boolean useFks = true;

        if (datasourceElement == null) {
            Debug.logWarning("datasource def not found with name " + helperName + ", using defaults for use-foreign-keys (true)");
        } else {
            //anything but false is true
            useFks = !"false".equals(datasourceElement.getAttribute("use-foreign-keys"));
        }
        
        UtilTimer timer = new UtilTimer();

        timer.timerString("Start - Before Get Database Meta Data");
        
        //get ALL tables from this database
        TreeSet tableNames = this.getTableNames(messages);

        if (tableNames == null) {
            String message = "Could not get table name information from the database, aborting.";

            if (messages != null)
                messages.add(message);
            Debug.logError(message, module);
            return;
        }
        timer.timerString("After Get All Table Names");
        
        //get ALL column info, put into hashmap by table name
        Map colInfo = this.getColumnInfo(tableNames, messages);

        if (colInfo == null) {
            String message = "Could not get column information from the database, aborting.";

            if (messages != null)
                messages.add(message);
            Debug.logError(message, module);
            return;
        }
        timer.timerString("After Get All Column Info");
        
        //-make sure all entities have a corresponding table
        //-list all tables that do not have a corresponding entity
        //-display message if number of table columns does not match number of entity fields
        //-list all columns that do not have a corresponding field
        //-make sure each corresponding column is of the correct type
        //-list all fields that do not have a corresponding column
        
        timer.timerString("Before Individual Table/Column Check");
        
        ArrayList modelEntityList = new ArrayList(modelEntities.values());

        //sort using compareTo method on ModelEntity
        Collections.sort(modelEntityList);

        Iterator modelEntityIter = modelEntityList.iterator();
        int curEnt = 0;
        int totalEnt = modelEntityList.size();
        List entitiesAdded = new LinkedList();

        while (modelEntityIter.hasNext()) {
            curEnt++;
            ModelEntity entity = (ModelEntity) modelEntityIter.next();
            String entityName = entity.getEntityName();
            
            //if this is a view entity, do not check it...
            if (entity instanceof ModelViewEntity) {
                String entMessage = "(" + timer.timeSinceLast() + "ms) NOT Checking #" + curEnt + "/" + totalEnt + " View Entity " + entity.getEntityName();

                Debug.logVerbose(entMessage, module);
                if (messages != null)
                    messages.add(entMessage);
                continue;
            }
            
            String entMessage = "(" + timer.timeSinceLast() + "ms) Checking #" + curEnt + "/" + totalEnt +
                " Entity " + entity.getEntityName() + " with table " + entity.getTableName();

            Debug.logVerbose(entMessage, module);
            if (messages != null)
                messages.add(entMessage);
            
                //-make sure all entities have a corresponding table
            if (tableNames.contains(entity.getTableName().toUpperCase())) {
                tableNames.remove(entity.getTableName().toUpperCase());
                
                if (colInfo != null) {
                    Map fieldColNames = new HashMap();

                    for (int fnum = 0; fnum < entity.getFieldsSize(); fnum++) {
                        ModelField field = entity.getField(fnum);

                        fieldColNames.put(field.getColName().toUpperCase(), field);
                    }
                    
                    List colList = (List) colInfo.get(entity.getTableName().toUpperCase());
                    int numCols = 0;

                    for (; numCols < colList.size(); numCols++) {
                        ColumnCheckInfo ccInfo = (ColumnCheckInfo) colList.get(numCols);

                        //-list all columns that do not have a corresponding field
                        if (fieldColNames.containsKey(ccInfo.columnName)) {
                            ModelField field = null;

                            field = (ModelField) fieldColNames.remove(ccInfo.columnName);
                            ModelFieldType modelFieldType = modelFieldTypeReader.getModelFieldType(field.getType());
                            
                            if (modelFieldType != null) {
                                //make sure each corresponding column is of the correct type
                                String fullTypeStr = modelFieldType.getSqlType();
                                String typeName;
                                int columnSize = -1;
                                int decimalDigits = -1;
                                
                                int openParen = fullTypeStr.indexOf('(');
                                int closeParen = fullTypeStr.indexOf(')');
                                int comma = fullTypeStr.indexOf(',');
                                
                                if (openParen > 0 && closeParen > 0 && closeParen > openParen) {
                                    typeName = fullTypeStr.substring(0, openParen);
                                    if (comma > 0 && comma > openParen && comma < closeParen) {
                                        String csStr = fullTypeStr.substring(openParen + 1, comma);

                                        try {
                                            columnSize = Integer.parseInt(csStr);
                                        } catch (NumberFormatException e) {
                                            Debug.logError(e, module);
                                        }
                                        
                                        String ddStr = fullTypeStr.substring(comma + 1, closeParen);

                                        try {
                                            decimalDigits = Integer.parseInt(ddStr);
                                        } catch (NumberFormatException e) {
                                            Debug.logError(e, module);
                                        }
                                    } else {
                                        String csStr = fullTypeStr.substring(openParen + 1, closeParen);

                                        try {
                                            columnSize = Integer.parseInt(csStr);
                                        } catch (NumberFormatException e) {
                                            Debug.logError(e, module);
                                        }
                                    }
                                } else {
                                    typeName = fullTypeStr;
                                }
                                
                                if (!ccInfo.typeName.equals(typeName.toUpperCase())) {
                                    String message = "WARNING: Column \"" + ccInfo.columnName + "\" of table \"" + entity.getTableName() + "\" of entity \"" +
                                        entity.getEntityName() + "\" is of type \"" + ccInfo.typeName + "\" in the database, but is defined as type \"" +
                                        typeName + "\" in the entity definition.";

                                    Debug.logError(message, module);
                                    if (messages != null)
                                        messages.add(message);
                                }
                                if (columnSize != -1 && ccInfo.columnSize != -1 && columnSize != ccInfo.columnSize) {
                                    String message = "WARNING: Column \"" + ccInfo.columnName + "\" of table \"" + entity.getTableName() + "\" of entity \"" +
                                        entity.getEntityName() + "\" has a column size of \"" + ccInfo.columnSize +
                                        "\" in the database, but is defined to have a column size of \"" + columnSize + "\" in the entity definition.";

                                    Debug.logWarning(message, module);
                                    if (messages != null)
                                        messages.add(message);
                                }
                                if (decimalDigits != -1 && decimalDigits != ccInfo.decimalDigits) {
                                    String message = "WARNING: Column \"" + ccInfo.columnName + "\" of table \"" + entity.getTableName() + "\" of entity \"" +
                                        entity.getEntityName() + "\" has a decimalDigits of \"" + ccInfo.decimalDigits +
                                        "\" in the database, but is defined to have a decimalDigits of \"" + decimalDigits + "\" in the entity definition.";

                                    Debug.logWarning(message, module);
                                    if (messages != null)
                                        messages.add(message);
                                }
                            } else {
                                String message = "Column \"" + ccInfo.columnName + "\" of table \"" + entity.getTableName() + "\" of entity \"" + entity.getEntityName() +
                                    "\" has a field type name of \"" + field.getType() + "\" which is not found in the field type definitions";

                                Debug.logError(message, module);
                                if (messages != null)
                                    messages.add(message);
                            }
                        } else {
                            String message = "Column \"" + ccInfo.columnName + "\" of table \"" + entity.getTableName() + "\" of entity \"" + entity.getEntityName() + "\" exists in the database but has no corresponding field";

                            Debug.logWarning(message, module);
                            if (messages != null)
                                messages.add(message);
                        }
                    }
                    
                    //-display message if number of table columns does not match number of entity fields
                    if (numCols != entity.getFieldsSize()) {
                        String message = "Entity \"" + entity.getEntityName() + "\" has " + entity.getFieldsSize() + " fields but table \"" + entity.getTableName() + "\" has " +
                            numCols + " columns.";

                        Debug.logWarning(message, module);
                        if (messages != null)
                            messages.add(message);
                    }
                    
                    //-list all fields that do not have a corresponding column
                    Iterator fcnIter = fieldColNames.keySet().iterator();

                    while (fcnIter.hasNext()) {
                        String colName = (String) fcnIter.next();
                        ModelField field = (ModelField) fieldColNames.get(colName);
                        String message =
                            "Field \"" + field.getName() + "\" of entity \"" + entity.getEntityName() + "\" is missing its corresponding column \"" + field.getColName() + "\"";

                        Debug.logWarning(message, module);
                        if (messages != null)
                            messages.add(message);
                        
                        if (addMissing) {
                            //add the column
                            String errMsg = addColumn(entity, field);

                            if (errMsg != null && errMsg.length() > 0) {
                                message = "Could not add column \"" + field.getColName() + "\" to table \"" + entity.getTableName() + "\"";
                                Debug.logError(message, module);
                                if (messages != null) messages.add(message);
                                Debug.logError(errMsg, module);
                                if (messages != null) messages.add(errMsg);
                            } else {
                                message = "Added column \"" + field.getColName() + "\" to table \"" + entity.getTableName() + "\"";
                                Debug.logImportant(message, module);
                                if (messages != null) messages.add(message);
                            }
                        }
                    }
                }
            } else {
                String message = "Entity \"" + entity.getEntityName() + "\" has no table in the database";

                Debug.logWarning(message, module);
                if (messages != null)
                    messages.add(message);
                
                if (addMissing) {
                    //create the table
                    String errMsg = createTable(entity, modelEntities, false);

                    if (errMsg != null && errMsg.length() > 0) {
                        message = "Could not create table \"" + entity.getTableName() + "\"";
                        Debug.logError(message, module);
                        if (messages != null) messages.add(message);
                        Debug.logError(errMsg, module);
                        if (messages != null) messages.add(errMsg);
                    } else {
                        entitiesAdded.add(entity);
                        message = "Created table \"" + entity.getTableName() + "\"";
                        Debug.logImportant(message, module);
                        if (messages != null) messages.add(message);
                    }
                }
            }
        }
        
        timer.timerString("After Individual Table/Column Check");
        
        //-list all tables that do not have a corresponding entity
        Iterator tableNamesIter = tableNames.iterator();

        while (tableNamesIter != null && tableNamesIter.hasNext()) {
            String tableName = (String) tableNamesIter.next();
            String message = "Table named \"" + tableName + "\" exists in the database but has no corresponding entity";

            Debug.logWarning(message, module);
            if (messages != null)
                messages.add(message);
        }

        // for each newly added table, add fks
        if (useFks) {
            /* THIS IS NO LONGER NEEDED NOW THAT EACH FK/RELATIONSHIP IS CHECKED BELOW
            Iterator eaIter = entitiesAdded.iterator();

            while (eaIter.hasNext()) {
                ModelEntity curEntity = (ModelEntity) eaIter.next();
                String errMsg = this.createForeignKeys(curEntity, modelEntities);

                if (errMsg != null && errMsg.length() > 0) {
                    String message = "Could not create foreign keys for entity \"" + curEntity.getEntityName() + "\"";

                    Debug.logError(message, module);
                    if (messages != null) messages.add(message);
                    Debug.logError(errMsg, module);
                    if (messages != null) messages.add(errMsg);
                } else {
                    String message = "Created foreign keys for entity \"" + curEntity.getEntityName() + "\"";

                    Debug.logImportant(message, module);
                    if (messages != null) messages.add(message);
                }
            }
            */
        }
        
        //make sure each one-relation has an FK
        if (useFks) {
            //TODO: check each key-map to make sure it exists in the FK, if any differences warn and then remove FK and recreate it
            
            //get ALL column info, put into hashmap by table name
            Map refTableInfoMap = this.getReferenceInfo(tableNames, messages);
            
            Iterator refModelEntityIter = modelEntityList.iterator();
            while (refModelEntityIter.hasNext()) {
                ModelEntity entity = (ModelEntity) refModelEntityIter.next();
                String entityName = entity.getEntityName();
                //if this is a view entity, do not check it...
                if (entity instanceof ModelViewEntity) {
                    String entMessage = "NOT Checking View Entity " + entity.getEntityName();

                    Debug.logVerbose(entMessage, module);
                    if (messages != null) {
                        messages.add(entMessage);
                    }
                    continue;
                }
                
                //get existing FK map for this table
                Map rcInfoMap = (Map) refTableInfoMap.get(entity.getTableName());
                
                //go through each relation to see if an FK already exists
                Iterator relations = entity.getRelationsIterator();
                boolean createdConstraints = false;
                while (relations.hasNext()) {
                    ModelRelation modelRelation = (ModelRelation) relations.next();
                    if (!"one".equals(modelRelation.getType())) {
                        continue;
                    }
                    
                    ModelEntity relModelEntity = (ModelEntity) modelEntities.get(modelRelation.getRelEntityName());
                    
                    String relConstraintName = makeFkConstraintName(modelRelation.getTitle(), modelRelation.getRelEntityName());
                    ReferenceCheckInfo rcInfo = null;
                    if (rcInfoMap != null) {
                        rcInfo = (ReferenceCheckInfo) rcInfoMap.get(relConstraintName);
                    }
                    
                    if (rcInfo != null) {
                        rcInfoMap.remove(relConstraintName);
                    } else {
                        //if not, create one
                        Debug.logVerbose("No Foreign Key Constraint " + relConstraintName + " found in table " + entityName);
                        String errMsg = createForeignKey(entity, modelRelation, relModelEntity);
                        if (errMsg != null && errMsg.length() > 0) {
                            String message = "Could not create foreign key " + relConstraintName + " for entity \"" + entity.getEntityName() + "\"";

                            Debug.logError(message, module);
                            if (messages != null) messages.add(message);
                            Debug.logError(errMsg, module);
                            if (messages != null) messages.add(errMsg);
                        } else {
                            String message = "Created foreign key " + relConstraintName + " for entity \"" + entity.getEntityName() + "\"";

                            Debug.logVerbose(message, module);
                            if (messages != null) messages.add(message);
                            
                            createdConstraints = true;
                        }
                    }
                }
                if (createdConstraints) {
                    String message = "Created foreign key(s) for entity \"" + entity.getEntityName() + "\"";
                    Debug.logImportant(message, module);
                    if (messages != null) messages.add(message);
                }
                
                //show foreign key references that exist but are unknown
                if (rcInfoMap != null) {
                    Iterator rcInfoKeysLeft = rcInfoMap.keySet().iterator();
                    while (rcInfoKeysLeft.hasNext()) {
                        String rcKeyLeft = (String) rcInfoKeysLeft.next();
                        Debug.logImportant("Unknown Foreign Key Constraint " + rcKeyLeft + " found in table " + entityName);
                    }
                }
            }
        }
        
        timer.timerString("Finished Checking Entity Database");
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

        while (tableNamesIter.hasNext()) {
            String tableName = (String) tableNamesIter.next();
            Vector colList = (Vector) colInfo.get(tableName);
            
            ModelEntity newEntity = new ModelEntity(tableName, colList, modelFieldTypeReader);

            newEntList.add(newEntity);
        }
        
        return newEntList;
    }
    
    public TreeSet getTableNames(Collection messages) {
        Connection connection = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            String message = "Unable to esablish a connection with the database... Error was:" + sqle.toString();

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            return null;
        } catch (GenericEntityException e) {
            String message = "Unable to esablish a connection with the database... Error was:" + e.toString();

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            return null;
        }
        
        DatabaseMetaData dbData = null;

        try {
            dbData = connection.getMetaData();
        } catch (SQLException sqle) {
            String message = "Unable to get database meta data... Error was:" + sqle.toString();

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            return null;
        }
        
        try {
            Debug.logInfo("Database Product Name is " + dbData.getDatabaseProductName(), module);
            Debug.logInfo("Database Product Version is " + dbData.getDatabaseProductVersion(), module);
        } catch (SQLException sqle) {
            Debug.logWarning("Unable to get Database name & version information", module);
        }
        try {
            Debug.logInfo("Database Driver Name is " + dbData.getDriverName(), module);
            Debug.logInfo("Database Driver Version is " + dbData.getDriverVersion(), module);
        } catch (SQLException sqle) {
            Debug.logWarning("Unable to get Driver name & version information", module);
        }
        
        //get ALL tables from this database
        TreeSet tableNames = new TreeSet();
        ResultSet tableSet = null;

        try {
            tableSet = dbData.getTables(null, dbData.getUserName(), null, null);
        } catch (SQLException sqle) {
            String message = "Unable to get list of table information... Error was:" + sqle.toString();

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            
            try {
                connection.close();
            } catch (SQLException sqle2) {
                String message2 = "Unable to close database connection, continuing anyway... Error was:" + sqle2.toString();

                Debug.logError(message2, module);
                if (messages != null)
                    messages.add(message2);
            }
            return null;
        }
        
        try {
            while (tableSet.next()) {
                try {
                    String tableName = tableSet.getString("TABLE_NAME");

                    tableName = (tableName == null) ? null : tableName.toUpperCase();
                    String tableType = tableSet.getString("TABLE_TYPE");

                    tableType = (tableType == null) ? null : tableType.toUpperCase();
                    //only allow certain table types
                    if (tableType != null && !"TABLE".equals(tableType) && !"VIEW".equals(tableType) && !"ALIAS".equals(tableType) && !"SYNONYM".equals(tableType))
                        continue;
                    
                        //String remarks = tableSet.getString("REMARKS");
                    tableNames.add(tableName);
                    //Debug.logInfo("Found table named \"" + tableName + "\" of type \"" + tableType + "\" with remarks: " + remarks);
                } catch (SQLException sqle) {
                    String message = "Error getting table information... Error was:" + sqle.toString();

                    Debug.logError(message, module);
                    if (messages != null)
                        messages.add(message);
                    continue;
                }
            }
        } catch (SQLException sqle) {
            String message = "Error getting next table information... Error was:" + sqle.toString();

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
        } finally {
            try {
                tableSet.close();
            } catch (SQLException sqle) {
                String message = "Unable to close ResultSet for table list, continuing anyway... Error was:" + sqle.toString();

                Debug.logError(message, module);
                if (messages != null) messages.add(message);
            }
            
            try {
                connection.close();
            } catch (SQLException sqle) {
                String message = "Unable to close database connection, continuing anyway... Error was:" + sqle.toString();

                Debug.logError(message, module);
                if (messages != null) messages.add(message);
            }
        }
        return tableNames;
    }
    
    public Map getColumnInfo(Set tableNames, Collection messages) {
        Connection connection = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            String message = "Unable to esablish a connection with the database... Error was:" + sqle.toString();

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            return null;
        } catch (GenericEntityException e) {
            String message = "Unable to esablish a connection with the database... Error was:" + e.toString();

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            return null;
        }
        
        DatabaseMetaData dbData = null;

        try {
            dbData = connection.getMetaData();
        } catch (SQLException sqle) {
            String message = "Unable to get database meta data... Error was:" + sqle.toString();

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            
            try {
                connection.close();
            } catch (SQLException sqle2) {
                String message2 = "Unable to close database connection, continuing anyway... Error was:" + sqle2.toString();

                Debug.logError(message2, module);
                if (messages != null)
                    messages.add(message2);
            }
            return null;
        }
        /*
        try {
            Debug.logInfo("Database Product Name is " + dbData.getDatabaseProductName(), module);
            Debug.logInfo("Database Product Version is " + dbData.getDatabaseProductVersion(), module);
        } catch (SQLException sqle) {
            Debug.logWarning("Unable to get Database name & version information", module);
        }
        try {
            Debug.logInfo("Database Driver Name is " + dbData.getDriverName(), module);
            Debug.logInfo("Database Driver Version is " + dbData.getDriverVersion(), module);
        } catch (SQLException sqle) {
            Debug.logWarning("Unable to get Driver name & version information", module);
        }
        */
        Map colInfo = new HashMap();

        try {
            ResultSet rsCols = dbData.getColumns(null, dbData.getUserName(), null, null);

            while (rsCols.next()) {
                try {
                    ColumnCheckInfo ccInfo = new ColumnCheckInfo();

                    ccInfo.tableName = rsCols.getString("TABLE_NAME");
                    ccInfo.tableName = (ccInfo.tableName == null) ? null : ccInfo.tableName.toUpperCase();
                    //ignore the column info if the table name is not in the list we are concerned with
                    if (!tableNames.contains(ccInfo.tableName))
                        continue;
                    
                    ccInfo.columnName = rsCols.getString("COLUMN_NAME");
                    ccInfo.columnName = (ccInfo.columnName == null) ? null : ccInfo.columnName.toUpperCase();
                    
                    ccInfo.typeName = rsCols.getString("TYPE_NAME");
                    ccInfo.typeName = (ccInfo.typeName == null) ? null : ccInfo.typeName.toUpperCase();
                    ccInfo.columnSize = rsCols.getInt("COLUMN_SIZE");
                    ccInfo.decimalDigits = rsCols.getInt("DECIMAL_DIGITS");
                    
                    ccInfo.isNullable = rsCols.getString("IS_NULLABLE");
                    ccInfo.isNullable = (ccInfo.isNullable == null) ? null : ccInfo.isNullable.toUpperCase();
                    
                    List tableColInfo = (List) colInfo.get(ccInfo.tableName);

                    if (tableColInfo == null) {
                        tableColInfo = new Vector();
                        colInfo.put(ccInfo.tableName, tableColInfo);
                    }
                    tableColInfo.add(ccInfo);
                } catch (SQLException sqle) {
                    String message = "Error getting column info for column. Error was:" + sqle.toString();

                    Debug.logError(message, module);
                    if (messages != null)
                        messages.add(message);
                    continue;
                }
            }
            
            try {
                rsCols.close();
            } catch (SQLException sqle) {
                String message = "Unable to close ResultSet for column list, continuing anyway... Error was:" + sqle.toString();

                Debug.logError(message, module);
                if (messages != null)
                    messages.add(message);
            }
        } catch (SQLException sqle) {
            String message = "Error getting column meta data for Error was:" + sqle.toString() + ". Not checking columns.";

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            colInfo = null;
        } finally {
            try {
                connection.close();
            } catch (SQLException sqle) {
                String message = "Unable to close database connection, continuing anyway... Error was:" + sqle.toString();

                Debug.logError(message, module);
                if (messages != null)
                    messages.add(message);
            }
        }
        return colInfo;
    }
    
    public Map getReferenceInfo(Set tableNames, Collection messages) {
        Connection connection = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            String message = "Unable to esablish a connection with the database... Error was:" + sqle.toString();

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            return null;
        } catch (GenericEntityException e) {
            String message = "Unable to esablish a connection with the database... Error was:" + e.toString();

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            return null;
        }
        
        DatabaseMetaData dbData = null;

        try {
            dbData = connection.getMetaData();
        } catch (SQLException sqle) {
            String message = "Unable to get database meta data... Error was:" + sqle.toString();

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            
            try {
                connection.close();
            } catch (SQLException sqle2) {
                String message2 = "Unable to close database connection, continuing anyway... Error was:" + sqle2.toString();

                Debug.logError(message2, module);
                if (messages != null)
                    messages.add(message2);
            }
            return null;
        }
        /*
        try {
            Debug.logInfo("Database Product Name is " + dbData.getDatabaseProductName(), module);
            Debug.logInfo("Database Product Version is " + dbData.getDatabaseProductVersion(), module);
        } catch (SQLException sqle) {
            Debug.logWarning("Unable to get Database name & version information", module);
        }
        try {
            Debug.logInfo("Database Driver Name is " + dbData.getDriverName(), module);
            Debug.logInfo("Database Driver Version is " + dbData.getDriverVersion(), module);
        } catch (SQLException sqle) {
            Debug.logWarning("Unable to get Driver name & version information", module);
        }
        */
        Map refInfo = new HashMap();

        try {
            ResultSet rsCols = dbData.getImportedKeys(null, null, null);

            while (rsCols.next()) {
                try {
                    //Debug.log("FK Import for table " + rsCols.getString("PKTABLE_NAME") + " and col " + rsCols.getString("PKCOLUMN_NAME") +
                    //        " from fktable " + rsCols.getString("FKTABLE_NAME") + " and fkcol " + rsCols.getString("FKCOLUMN_NAME"));
                    
                    ReferenceCheckInfo rcInfo = new ReferenceCheckInfo();

                    rcInfo.pkTableName = rsCols.getString("PKTABLE_NAME");
                    rcInfo.pkTableName = (rcInfo.pkTableName == null) ? null : rcInfo.pkTableName.toUpperCase();
                    rcInfo.pkColumnName = rsCols.getString("PKCOLUMN_NAME");
                    rcInfo.pkColumnName = (rcInfo.pkColumnName == null) ? null : rcInfo.pkColumnName.toUpperCase();
                    
                    rcInfo.fkTableName = rsCols.getString("FKTABLE_NAME");
                    rcInfo.fkTableName = (rcInfo.fkTableName == null) ? null : rcInfo.fkTableName.toUpperCase();
                    //ignore the column info if the FK table name is not in the list we are concerned with
                    if (!tableNames.contains(rcInfo.fkTableName))
                        continue;
                    rcInfo.fkColumnName = rsCols.getString("FKCOLUMN_NAME");
                    rcInfo.fkColumnName = (rcInfo.fkColumnName == null) ? null : rcInfo.fkColumnName.toUpperCase();
                    
                    rcInfo.fkName = rsCols.getString("FK_NAME");
                    rcInfo.fkName = (rcInfo.fkName == null) ? null : rcInfo.fkName.toUpperCase();

                    Map tableRefInfo = (Map) refInfo.get(rcInfo.fkTableName);

                    if (tableRefInfo == null) {
                        tableRefInfo = new HashMap();
                        refInfo.put(rcInfo.fkTableName, tableRefInfo);
                    }
                    tableRefInfo.put(rcInfo.fkName, rcInfo);
                } catch (SQLException sqle) {
                    String message = "Error getting column info for column. Error was:" + sqle.toString();

                    Debug.logError(message, module);
                    if (messages != null)
                        messages.add(message);
                    continue;
                }
            }
            
            try {
                rsCols.close();
            } catch (SQLException sqle) {
                String message = "Unable to close ResultSet for column list, continuing anyway... Error was:" + sqle.toString();

                Debug.logError(message, module);
                if (messages != null)
                    messages.add(message);
            }
        } catch (SQLException sqle) {
            String message = "Error getting column meta data for Error was:" + sqle.toString() + ". Not checking columns.";

            Debug.logError(message, module);
            if (messages != null)
                messages.add(message);
            refInfo = null;
        } finally {
            try {
                connection.close();
            } catch (SQLException sqle) {
                String message = "Unable to close database connection, continuing anyway... Error was:" + sqle.toString();

                Debug.logError(message, module);
                if (messages != null)
                    messages.add(message);
            }
        }
        return refInfo;
    }

    /* ====================================================================== */
    /* ====================================================================== */
    public String createTable(ModelEntity entity, Map modelEntities, boolean addFks) {
        if (entity == null) {
            return "ModelEntity was null and is required to create a table";
        }
        if (entity instanceof ModelViewEntity) {
            return "ERROR: Cannot create table for a view entity";
        }
        
        Connection connection = null;
        Statement stmt = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            return "Unable to esablish a connection with the database... Error was: " + sqle.toString();
        } catch (GenericEntityException e) {
            return "Unable to esablish a connection with the database... Error was: " + e.toString();
        }
        
        StringBuffer sqlBuf = new StringBuffer("CREATE TABLE ");

        sqlBuf.append(entity.getTableName());
        sqlBuf.append(" (");
        for (int i = 0; i < entity.getFieldsSize(); i++) {
            ModelField field = entity.getField(i);
            ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.getType());

            if (type == null) {
                return "Field type [" + type + "] not found for field [" + field.getName() + "] of entity [" + entity.getEntityName() + "], not creating table.";
            }
            
            sqlBuf.append(field.getColName());
            sqlBuf.append(" ");
            sqlBuf.append(type.getSqlType());
            if (field.getIsPk()) {
                sqlBuf.append(" NOT NULL, ");
            } else {
                sqlBuf.append(", ");
            }
        }
        String pkName = "PK_" + entity.getTableName();

        if (pkName.length() > 30) {
            pkName = pkName.substring(0, 30);
        }
        sqlBuf.append("CONSTRAINT ");
        sqlBuf.append(pkName);
        sqlBuf.append(" PRIMARY KEY (");
        sqlBuf.append(entity.colNameString(entity.getPksCopy()));
        sqlBuf.append(")");
        
        if (addFks) {
            //go through the relationships to see if any foreign keys need to be added
            Iterator relationsIter = entity.getRelationsIterator();

            while (relationsIter.hasNext()) {
                ModelRelation modelRelation = (ModelRelation) relationsIter.next();

                if ("one".equals(modelRelation.getType())) {
                    ModelEntity relModelEntity = (ModelEntity) modelEntities.get(modelRelation.getRelEntityName());

                    if (relModelEntity == null) {
                        Debug.logError("Error adding foreign key: ModelEntity was null for related entity name " + modelRelation.getRelEntityName());
                        continue;
                    }
                    if (relModelEntity instanceof ModelViewEntity) {
                        Debug.logError("Error adding foreign key: related entity is a view entity for related entity name " + modelRelation.getRelEntityName());
                        continue;
                    }

                    sqlBuf.append(", ");
                    sqlBuf.append(makeFkConstraintClause(entity, modelRelation, relModelEntity));
                }
            }
        }

        sqlBuf.append(")");
        Debug.logVerbose("[createTable] sql=" + sqlBuf.toString());
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(sqlBuf.toString());
        } catch (SQLException sqle) {
            return "SQL Exception while executing the following:\n" + sqlBuf.toString() + "\nError was: " + sqle.toString();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException sqle) {}
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {}
        }
        return null;
    }
    
    public String addColumn(ModelEntity entity, ModelField field) {
        if (entity == null || field == null)
            return "ModelEntity or ModelField where null, cannot add column";
        if (entity instanceof ModelViewEntity) {
            return "ERROR: Cannot add column for a view entity";
        }
        
        Connection connection = null;
        Statement stmt = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            return "Unable to esablish a connection with the database... Error was: " + sqle.toString();
        } catch (GenericEntityException e) {
            return "Unable to esablish a connection with the database... Error was: " + e.toString();
        }
        
        ModelFieldType type = modelFieldTypeReader.getModelFieldType(field.getType());

        if (type == null) {
            return "Field type [" + type + "] not found for field [" + field.getName() + "] of entity [" + entity.getEntityName() + "], not adding column.";
        }
        
        String sql = "ALTER TABLE " + entity.getTableName() + " ADD " + field.getColName() + " " + type.getSqlType();
        
        Debug.logInfo("[addColumn] sql=" + sql);
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException sqle) {
            return "SQL Exception while executing the following:\n" + sql + "\nError was: " + sqle.toString();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException sqle) {}
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {}
        }
        return null;
    }

    public String makeFkConstraintName(String title, String relEntityName) {
        String relConstraintName = title + relEntityName;

        if (relConstraintName.length() > 30) {
            relConstraintName = relConstraintName.substring(0, 30);
        }
        
        return relConstraintName.toUpperCase();
    }
    
    public String createForeignKeys(ModelEntity entity, Map modelEntities) {
        if (entity == null) {
            return "ModelEntity was null and is required to create foreign keys for a table";
        }
        if (entity instanceof ModelViewEntity) {
            return "ERROR: Cannot create foreign keys for a view entity";
        }
        
        //go through the relationships to see if any foreign keys need to be added
        Iterator relationsIter = entity.getRelationsIterator();

        while (relationsIter.hasNext()) {
            ModelRelation modelRelation = (ModelRelation) relationsIter.next();
            
            if ("one".equals(modelRelation.getType())) {
                ModelEntity relModelEntity = (ModelEntity) modelEntities.get(modelRelation.getRelEntityName());

                if (relModelEntity == null) {
                    Debug.logError("Error adding foreign key: ModelEntity was null for related entity name " + modelRelation.getRelEntityName());
                    continue;
                }
                if (relModelEntity instanceof ModelViewEntity) {
                    Debug.logError("Error adding foreign key: related entity is a view entity for related entity name " + modelRelation.getRelEntityName());
                    continue;
                }
                
                String retMsg = createForeignKey(entity, modelRelation, relModelEntity);

                if (retMsg != null) {
                    return retMsg;
                }
            }
        }
        return null;
    }
        
    public String createForeignKey(ModelEntity entity, ModelRelation modelRelation, ModelEntity relModelEntity) {
        Connection connection = null;
        Statement stmt = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            return "Unable to esablish a connection with the database... Error was: " + sqle.toString();
        } catch (GenericEntityException e) {
            return "Unable to esablish a connection with the database... Error was: " + e.toString();
        }

        //now add constraint clause
        StringBuffer sqlBuf = new StringBuffer("ALTER TABLE ");

        sqlBuf.append(entity.getTableName());
        sqlBuf.append(" ADD ");
        sqlBuf.append(makeFkConstraintClause(entity, modelRelation, relModelEntity));
    
        Debug.logVerbose("[createTable] sql=" + sqlBuf.toString());
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(sqlBuf.toString());
        } catch (SQLException sqle) {
            return "SQL Exception while executing the following:\n" + sqlBuf.toString() + "\nError was: " + sqle.toString();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException sqle) {}
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {}
        }
        return null;
    }

    public String makeFkConstraintClause(ModelEntity entity, ModelRelation modelRelation, ModelEntity relModelEntity) {
        //make the two column lists
        Iterator keyMapsIter = modelRelation.getKeyMapsIterator();
        StringBuffer mainCols = new StringBuffer();
        StringBuffer relCols = new StringBuffer();

        while (keyMapsIter.hasNext()) {
            ModelKeyMap keyMap = (ModelKeyMap) keyMapsIter.next();

            ModelField mainField = entity.getField(keyMap.getFieldName());

            if (mainCols.length() > 0) {
                mainCols.append(", ");
            }
            mainCols.append(mainField.getColName());

            ModelField relField = relModelEntity.getField(keyMap.getRelFieldName());

            if (relCols.length() > 0) {
                relCols.append(", ");
            }
            relCols.append(relField.getColName());
        }

        String relConstraintName = makeFkConstraintName(modelRelation.getTitle(), modelRelation.getRelEntityName());
        StringBuffer sqlBuf = new StringBuffer("CONSTRAINT ");

        sqlBuf.append(relConstraintName);
        sqlBuf.append(" FOREIGN KEY (");
        sqlBuf.append(mainCols.toString());
        sqlBuf.append(") REFERENCES ");
        sqlBuf.append(relModelEntity.getTableName());
        sqlBuf.append(" (");
        sqlBuf.append(relCols.toString());
        sqlBuf.append(")");
        
        return sqlBuf.toString();
    }
    
    public String deleteForeignKeys(ModelEntity entity, Map modelEntities) {
        if (entity == null) {
            return "ModelEntity was null and is required to delete foreign keys for a table";
        }
        if (entity instanceof ModelViewEntity) {
            return "ERROR: Cannot delete foreign keys for a view entity";
        }
        
        //go through the relationships to see if any foreign keys need to be added
        Iterator relationsIter = entity.getRelationsIterator();

        while (relationsIter.hasNext()) {
            ModelRelation modelRelation = (ModelRelation) relationsIter.next();
            
            if ("one".equals(modelRelation.getType())) {
                ModelEntity relModelEntity = (ModelEntity) modelEntities.get(modelRelation.getRelEntityName());

                if (relModelEntity == null) {
                    Debug.logError("Error removing foreign key: ModelEntity was null for related entity name " + modelRelation.getRelEntityName());
                    continue;
                }
                if (relModelEntity instanceof ModelViewEntity) {
                    Debug.logError("Error removing foreign key: related entity is a view entity for related entity name " + modelRelation.getRelEntityName());
                    continue;
                }
                
                String retMsg = deleteForeignKey(entity, modelRelation, relModelEntity);

                if (retMsg != null) {
                    return retMsg;
                }
            }
        }
        return null;
    }
        
    public String deleteForeignKey(ModelEntity entity, ModelRelation modelRelation, ModelEntity relModelEntity) {
        Connection connection = null;
        Statement stmt = null;

        try {
            connection = getConnection();
        } catch (SQLException sqle) {
            return "Unable to esablish a connection with the database... Error was: " + sqle.toString();
        } catch (GenericEntityException e) {
            return "Unable to esablish a connection with the database... Error was: " + e.toString();
        }

        String relConstraintName = makeFkConstraintName(modelRelation.getTitle(), modelRelation.getRelEntityName());
        
        //now add constraint clause
        StringBuffer sqlBuf = new StringBuffer("ALTER TABLE ");

        sqlBuf.append(entity.getTableName());
        sqlBuf.append(" DROP CONSTRAINT ");
        sqlBuf.append(relConstraintName);
    
        Debug.logVerbose("[createTable] sql=" + sqlBuf.toString());
        try {
            stmt = connection.createStatement();
            stmt.executeUpdate(sqlBuf.toString());
        } catch (SQLException sqle) {
            return "SQL Exception while executing the following:\n" + sqlBuf.toString() + "\nError was: " + sqle.toString();
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException sqle) {}
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException sqle) {}
        }
        return null;
    }

    public static class ColumnCheckInfo {
        public String tableName;
        public String columnName;
        public String typeName;
        public int columnSize;
        public int decimalDigits;
        public String isNullable; //YES/NO or "" = ie nobody knows
    }
    
    public static class ReferenceCheckInfo {
        public String pkTableName;
        /** Comma separated list of column names in the related tables primary key */
        public String pkColumnName;
        public String fkName;
        public String fkTableName;
        /** Comma separated list of column names in the primary tables foreign keys */
        public String fkColumnName;
    }
}
