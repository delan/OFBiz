/*
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.entity;


import java.util.*;
import org.ofbiz.core.entity.model.*;


/**
 * Generic Entity Helper Class
 *
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@author     <a href='mailto:chris_maurer@altavista.com'>Chris Maurer</a>
 *@created    Tue Aug 07 01:10:32 MDT 2001
 *@version    1.0
 */
public interface GenericHelper {

    /** Gets the name of the server configuration that corresponds to this helper
     *@return server configuration name
     */
    public String getHelperName();

    /** Creates a Entity in the form of a GenericValue and write it to the database
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(GenericValue value) throws GenericEntityException;

    /** Find a Generic Entity by its Primary Key
     *@param primaryKey The primary key to find by.
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKey(GenericPK primaryKey) throws GenericEntityException;

    /** Find a Generic Entity by its Primary Key and only returns the values requested by the passed keys (names)
     *@param primaryKey The primary key to find by.
     *@param keys The keys, or names, of the values to retrieve; only these values will be retrieved
     *@return The GenericValue corresponding to the primaryKey
     */
    public GenericValue findByPrimaryKeyPartial(GenericPK primaryKey, Set keys) throws GenericEntityException;

    /** Find a number of Generic Value objects by their Primary Keys, all at once
     *@param primaryKeys A List of primary keys to find by.
     *@return List of GenericValue objects corresponding to the passed primaryKey objects
     */
    public List findAllByPrimaryKeys(List primaryKeys) throws GenericEntityException;

    /** Remove a Generic Entity corresponding to the primaryKey
     *@param  primaryKey  The primary key of the entity to remove.
     *@return int representing number of rows effected by this operation
     */
    public int removeByPrimaryKey(GenericPK primaryKey) throws GenericEntityException;

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
     *@param modelEntity The ModelEntity of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@param orderBy The fields of the named entity to order the query by;
     *       optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue instances that match the query
     */
    public List findByAnd(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException;

    /* tentatively removing by clause methods, unless there are really big complaints... because it is a kludge
     * public List findByClause(ModelEntity modelEntity, List entityClauses, Map fields, List orderBy) throws GenericEntityException;
     */

    /** Finds Generic Entity records by all of the specified fields (ie: combined using OR)
     *@param modelEntity The ModelEntity of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@param orderBy The fields of the named entity to order the query by;
     *       optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue instances that match the query
     */
    public List findByOr(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException;

    /** Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     *@param modelEntity The ModelEntity of the Entity as defined in the entity XML file
     *@param entityCondition The EntityCondition object that specifies how to constrain this query
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return List of GenericValue objects representing the result
     */
    public List findByCondition(ModelEntity modelEntity, EntityCondition entityCondition,
        Collection fieldsToSelect, List orderBy) throws GenericEntityException;

    public List findByMultiRelation(GenericValue value, ModelRelation modelRelationOne, ModelEntity modelEntityOne,
        ModelRelation modelRelationTwo, ModelEntity modelEntityTwo, List orderBy) throws GenericEntityException;

    /** Finds GenericValues by the conditions specified in the EntityCondition object, the the EntityCondition javadoc for more details.
     *@param modelEntity The ModelEntity of the Entity as defined in the entity XML file
     *@param whereEntityCondition The EntityCondition object that specifies how to constrain this query before any groupings are done (if this is a view entity with group-by aliases)
     *@param havingEntityCondition The EntityCondition object that specifies how to constrain this query after any groupings are done (if this is a view entity with group-by aliases)
     *@param fieldsToSelect The fields of the named entity to get from the database; if empty or null all fields will be retreived
     *@param orderBy The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@param findOptions An instance of EntityFindOptions that specifies advanced query options. See the EntityFindOptions JavaDoc for more details.
     *@return EntityListIterator representing the result of the query: NOTE THAT THIS MUST BE CLOSED WHEN YOU ARE 
     *      DONE WITH IT, AND DON'T LEAVE IT OPEN TOO LONG BEACUSE IT WILL MAINTAIN A DATABASE CONNECTION.
     */
    public EntityListIterator findListIteratorByCondition(ModelEntity modelEntity, EntityCondition whereEntityCondition,
        EntityCondition havingEntityCondition, Collection fieldsToSelect, List orderBy, EntityFindOptions findOptions)
        throws GenericEntityException;

    /** Removes/deletes Generic Entity records found by all of the specified fields (ie: combined using AND)
     *@param modelEntity The ModelEntity of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@return int representing number of rows effected by this operation
     */
    public int removeByAnd(ModelEntity modelEntity, Map fields) throws GenericEntityException;

    /** Store the Entity from the GenericValue to the persistent store
     *@param value GenericValue instance containing the entity
     *@return int representing number of rows effected by this operation
     */
    public int store(GenericValue value) throws GenericEntityException;

    /** Store the Entities from the List GenericValue instances to the persistent store.
     *  This is different than the normal store method in that the store method only does
     *  an update, while the storeAll method checks to see if each entity exists, then
     *  either does an insert or an update as appropriate.
     *  These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions. This is just like to othersToStore feature
     *  of the GenericEntity on a create or store.
     *@param values List of GenericValue instances containing the entities to store
     *@return int representing number of rows effected by this operation
     */
    public int storeAll(List values) throws GenericEntityException;

    /** Remove the Entities from the List from the persistent store.
     *  <br>The List contains GenericEntity objects, can be either GenericPK or GenericValue.
     *  <br>If a certain entity contains a complete primary key, the entity in the datasource corresponding
     *  to that primary key will be removed, this is like a removeByPrimary Key.
     *  <br>On the other hand, if a certain entity is an incomplete or non primary key,
     *  if will behave like the removeByAnd method.
     *  <br>These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions.
     *@param dummyPKs List of GenericEntity instances containing the entities or by and fields to remove
     *@return int representing number of rows effected by this operation
     */
    public int removeAll(List dummyPKs) throws GenericEntityException;

    /** Check the datasource to make sure the entity definitions are correct, optionally adding missing entities or fields on the server
     *@param modelEntities Map of entityName names and ModelEntity values
     *@param messages Collection to put any result messages in
     *@param addMissing Flag indicating whether or not to add missing entities and fields on the server
     */
    public void checkDataSource(Map modelEntities, Collection messages, boolean addMissing) throws GenericEntityException;
}
