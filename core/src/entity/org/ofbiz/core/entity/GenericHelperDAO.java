/*
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
 */

package org.ofbiz.core.entity;

import java.util.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.model.*;

/**
 * Generic Entity Helper Class
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href='mailto:chris_maurer@altavista.com'>Chris Maurer</a>
 *@created    Tue Aug 07 01:10:32 MDT 2001
 *@version    1.0
 */
public class GenericHelperDAO implements GenericHelper {

    public static final String module = GenericHelperDAO.class.getName();

    protected GenericDAO genericDAO;
    protected String helperName;
    
    public GenericHelperDAO(String helperName) {
        this.helperName = helperName;
        genericDAO = GenericDAO.getGenericDAO(helperName);
    }
    
    public String getHelperName() {
        return helperName;
    }
    
    /** Creates a Entity in the form of a GenericValue and write it to the database
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(GenericValue value) throws GenericEntityException {
        if (value == null) {
            return null;
        }
        genericDAO.insert(value);
        return value;
    }
    
    /** Creates a Entity in the form of a GenericValue and write it to the database
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(GenericPK primaryKey) throws GenericEntityException {
        if (primaryKey == null) {
            return null;
        }
        GenericValue genericValue = new GenericValue(primaryKey);
        genericDAO.insert(genericValue);
        return genericValue;
    }
    
    /** Find a Generic Entity by its Primary Key
     *@param primaryKey The primary key to find by.
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKey(GenericPK primaryKey) throws GenericEntityException {
        if (primaryKey == null) {
            return null;
        }
        GenericValue genericValue = new GenericValue(primaryKey);
        genericDAO.select(genericValue);
        return genericValue;
    }
    
    /** Find a Generic Entity by its Primary Key and only returns the values requested by the passed keys (names)
     *@param primaryKey The primary key to find by.
     *@param keys The keys, or names, of the values to retrieve; only these values will be retrieved
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKeyPartial(GenericPK primaryKey, Set keys) throws GenericEntityException {
        if (primaryKey == null) {
            return null;
        }
        GenericValue genericValue = new GenericValue(primaryKey);
        genericDAO.partialSelect(genericValue, keys);
        return genericValue;
    }
    
    /** Find a number of Generic Value objects by their Primary Keys, all at once
     * This is done here for the DAO GenericHelper; for a client-server helper it
     * would be done on the server side to reduce network round trips.
     *@param primaryKeys A Collection of primary keys to find by.
     *@return Collection of GenericValue objects corresponding to the passed primaryKey objects
     */
    public Collection findAllByPrimaryKeys(Collection primaryKeys) throws GenericEntityException {
        if (primaryKeys == null) return null;
        Collection results = new LinkedList();
        
        Iterator pkiter = primaryKeys.iterator();
        while (pkiter.hasNext()) {
            GenericPK primaryKey = (GenericPK) pkiter.next();
            GenericValue result = this.findByPrimaryKey(primaryKey);
            if (result != null) results.add(result);
        }
        return results;
    }
    
    /** Remove a Generic Entity corresponding to the primaryKey
     *@param  primaryKey  The primary key of the entity to remove.
     *@return int representing number of rows effected by this operation
     */
    public int removeByPrimaryKey(GenericPK primaryKey) throws GenericEntityException {
        if (primaryKey == null) return 0;
        if (Debug.verboseOn()) Debug.logVerbose("Removing GenericPK: " + primaryKey.toString(), module);
        return genericDAO.delete(primaryKey);
    }
    
    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
     *@param modelEntity The ModelEntity of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return Collection of GenericValue instances that match the query
     */
    public Collection findByAnd(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        return genericDAO.selectByAnd(modelEntity, fields, orderBy);
    }
    public Collection findByAnd(ModelEntity modelEntity, List expressions, List orderBy) throws GenericEntityException {
        return genericDAO.selectByAnd(modelEntity, expressions, orderBy);
    }
    public Collection findByLike(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        return genericDAO.selectByLike(modelEntity, fields, orderBy);
    }
    public Collection findByClause(ModelEntity modelEntity, List entityClauses, Map fields, List orderBy) throws GenericEntityException {
        return genericDAO.selectByClause(modelEntity, entityClauses, fields, orderBy);
    }
    
    /** Finds Generic Entity records by all of the specified fields (ie: combined using OR)
     *@param modelEntity The ModelEntity of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return Collection of GenericValue instances that match the query
     */
    public Collection findByOr(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        return genericDAO.selectByOr(modelEntity, fields, orderBy);
    }
    public Collection findByOr(ModelEntity modelEntity, List expressions, List orderBy) throws GenericEntityException {
        return genericDAO.selectByOr(modelEntity, expressions, orderBy);
    }
    
    /** Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     *@param modelEntity The ModelEntity of the Entity as defined in the entity XML file
     *@param entityCondition The EntityCondition object that specifies how to constrain this query
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return Collection of GenericValue objects representing the result
     */
    public Collection findByCondition(ModelEntity modelEntity, EntityCondition entityCondition, 
            Collection fieldsToSelect, List orderBy) throws GenericEntityException {
        return genericDAO.selectByCondition(modelEntity, entityCondition, fieldsToSelect, orderBy);
    }
    
    /** Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     *@param modelEntity The ModelEntity of the Entity as defined in the entity XML file
     *@param entityCondition The EntityCondition object that specifies how to constrain this query
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@param specifyTypeAndConcur If true the following two parameters (resultSetType and resultSetConcurrency) will be used to specify 
     *      how the results will be used; if false the default values for the JDBC driver will be used
     *@param resultSetType Specified how the ResultSet will be traversed. Available values: ResultSet.TYPE_FORWARD_ONLY, 
     *      ResultSet.TYPE_SCROLL_INSENSITIVE or ResultSet.TYPE_SCROLL_SENSITIVE. See the java.sql.ResultSet JavaDoc for 
     *      more information. If you want it to be fast, use the common default: ResultSet.TYPE_FORWARD_ONLY.
     *@param resultSetConcurrency Specifies whether or not the ResultSet can be updated. Available values: 
     *      ResultSet.CONCUR_READ_ONLY or ResultSet.CONCUR_UPDATABLE. Should pretty much always be 
     *      ResultSet.CONCUR_READ_ONLY with the Entity Engine.
     *@param distinct Specifies whether the values returned should be filtered to remove duplicate values.
     *@return EntityListIterator representing the result of the query: NOTE THAT THIS MUST BE CLOSED WHEN YOU ARE 
     *      DONE WITH IT, AND DON'T LEAVE IT OPEN TOO LONG BEACUSE IT WILL MAINTAIN A DATABASE CONNECTION.
     */
    public EntityListIterator findListIteratorByCondition(ModelEntity modelEntity, EntityCondition entityCondition, 
            Collection fieldsToSelect, List orderBy, boolean specifyTypeAndConcur, int resultSetType, 
            int resultSetConcurrency, boolean distinct) throws GenericEntityException {
        return genericDAO.selectListIteratorByCondition(modelEntity, entityCondition, fieldsToSelect, orderBy, 
                specifyTypeAndConcur, resultSetType, resultSetConcurrency, distinct);
    }
    
    /** Removes/deletes Generic Entity records found by all of the specified fields (ie: combined using AND)
     *@param modelEntity The ModelEntity of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@return int representing number of rows effected by this operation
     */
    public int removeByAnd(ModelEntity modelEntity, Map fields) throws GenericEntityException {
        if (modelEntity == null || fields == null) {
            return 0;
        }
        return genericDAO.deleteByAnd(modelEntity, fields);
    }
    
    /** Store the Entity from the GenericValue to the persistent store
     *@param value GenericValue instance containing the entity
     *@return int representing number of rows effected by this operation
     */
    public int store(GenericValue value) throws GenericEntityException {
        if (value == null) {
            return 0;
        }
        return genericDAO.update(value);
    }
    
    /** Store the Entities from the Collection GenericValue instances to the persistent store.
     *  This is different than the normal store method in that the store method only does
     *  an update, while the storeAll method checks to see if each entity exists, then
     *  either does an insert or an update as appropriate.
     *  These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions. This is just like to othersToStore feature
     *  of the GenericEntity on a create or store.
     *@param values Collection of GenericValue instances containing the entities to store
     *@return int representing number of rows effected by this operation
     */
    public int storeAll(Collection values) throws GenericEntityException {
        return genericDAO.storeAll(values);
    }
    
    /** Remove the Entities from the Collection from the persistent store.
     *  <br>The Collection contains GenericEntity objects, can be either GenericPK or GenericValue.
     *  <br>If a certain entity contains a complete primary key, the entity in the datasource corresponding
     *  to that primary key will be removed, this is like a removeByPrimary Key.
     *  <br>On the other hand, if a certain entity is an incomplete or non primary key,
     *  if will behave like the removeByAnd method.
     *  <br>These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions.
     *@param dummyPKs Collection of GenericEntity instances containing the entities or by and fields to remove
     *@return int representing number of rows effected by this operation
     */
    public int removeAll(Collection dummyPKs) throws GenericEntityException {
        return genericDAO.deleteAll(dummyPKs);
    }
    
    /** Check the datasource to make sure the entity definitions are correct, optionally adding missing entities or fields on the server
     *@param modelEntities Map of entityName names and ModelEntity values
     *@param messages Collection to put any result messages in
     *@param addMissing Flag indicating whether or not to add missing entities and fields on the server
     */
    public void checkDataSource(Map modelEntities, Collection messages, boolean addMissing) throws GenericEntityException {
        genericDAO.checkDb(modelEntities, messages, addMissing);
    }
}
