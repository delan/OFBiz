package org.ofbiz.core.entity;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;
import org.ofbiz.core.util.*;
import org.ofbiz.core.entity.model.*;

/**
 * <p><b>Title:</b> Generic Entity Helper Class
 * <p><b>Description:</b> None
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
 *@created    Tue Aug 07 01:10:32 MDT 2001
 *@version    1.0
 */
public class GenericHelperEJB implements GenericHelper {
    /** A variable to cache the Home object for the Generic EJB */
    private GenericHome genericHome;
    String helperName;

    public GenericHelperEJB(String helperName) {
        this.helperName = helperName;
        getGenericHome(helperName);
    }

    public String getHelperName() {
        return helperName;
    }

    /** Initializes the genericHome, from a JNDI lookup */
    public GenericHome getGenericHome(String helperName) {
        InitialContext initialContext = JNDIContextFactory.getInitialContext(helperName);
        try {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.core.entity.GenericHome");
            genericHome = (GenericHome) MyNarrow.narrow(homeObject, GenericHome.class);
        } catch (Exception e1) {
            Debug.logError(e1);
        }
        Debug.logInfo("generic home obtained " + genericHome);
        return genericHome;
    }

    /** Creates a Entity in the form of a GenericValue and write it to the database
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(GenericValue value) throws GenericEntityException {
        if (value == null) {
            return null;
        }
        GenericValue genericValue = null;

        try {
            GenericRemote generic = (GenericRemote) MyNarrow.narrow(genericHome.create(value), GenericRemote.class);
            genericValue = generic.getValueObject();
        } catch (CreateException ce) {
            Debug.logError(ce);
        } catch (Exception fe) {
            Debug.logError(fe);
        }
        return genericValue;
    }

    /** Creates a Entity in the form of a GenericValue and write it to the database
     *@return GenericValue instance containing the new instance
     */
    public GenericValue create(GenericPK primaryKey) throws GenericEntityException {
        if (primaryKey == null) {
            return null;
        }
        GenericValue genericValue = null;
        try {
            GenericRemote generic = (GenericRemote) MyNarrow.narrow(genericHome.create(primaryKey), GenericRemote.class);
            genericValue = generic.getValueObject();
        } catch (CreateException ce) {
            Debug.logError(ce);
        } catch (Exception fe) {
            Debug.logError(fe);
        }
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
        GenericValue genericValue = null;

        try {
            GenericRemote generic = (GenericRemote) MyNarrow.narrow(genericHome.findByPrimaryKey(primaryKey), GenericRemote.class);
            genericValue = generic.getValueObject();
        } catch (ObjectNotFoundException onfe) {
            Debug.logError(onfe);
        } catch (Exception fe) {
            Debug.logError(fe);
        }
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
        GenericValue genericValue = null;

        try {
            GenericRemote generic = (GenericRemote) MyNarrow.narrow(genericHome.findByPrimaryKeyPartial(primaryKey, keys), GenericRemote.class);
            genericValue = generic.getValueObject();
        } catch (ObjectNotFoundException onfe) {
            Debug.logError(onfe);
        } catch (Exception fe) {
            Debug.logError(fe);
        }
        return genericValue;
    }

    /** Remove a Generic Entity corresponding to the primaryKey
     *@param  primaryKey  The primary key of the entity to remove.
     */
    public void removeByPrimaryKey(GenericPK primaryKey) throws GenericEntityException {
        if (primaryKey == null)
            return;
        GenericValue generic = findByPrimaryKey(primaryKey);
        try {
            Debug.logInfo("Removing GenericValue: " + generic.toString());
            if (generic != null)
                generic.remove();
        } catch (Exception e) {
            Debug.logWarning(e);
        }
    }

    /** Find a number of Generic Value objects by their Primary Keys, all at once
     *@param primaryKeys A Collection of primary keys to find by.
     *@return Collection of GenericValue objects corresponding to the passed primaryKey objects
     */
    public Collection findAllByPrimaryKeys(Collection primaryKeys) throws GenericEntityException {
        //this should be implemented through the home interface to do the entire process on the server side
        throw new GenericNotImplementedException("Store All not yet implemented for EJB data source.");
    }

    /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@param order The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
     *@return Collection of GenericValue instances that match the query
     */
    public Collection findByAnd(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        if (modelEntity == null || fields == null) {
            return null;
        }
        Collection collection = null;

        try {
            Collection remoteCol = (Collection) MyNarrow.narrow(genericHome.findByAnd(modelEntity.entityName, fields, orderBy), Collection.class);
            collection = remoteToValue(remoteCol);
        } catch (ObjectNotFoundException onfe) {
            Debug.logError(onfe);
        } catch (Exception fe) {
            Debug.logError(fe);
        }

        return collection;
    }

    public Collection findByAnd(ModelEntity modelEntity, List expressions, List orderBy) throws GenericEntityException {
        if (modelEntity == null || expressions == null) {
            return null;
        }
        Collection collection = null;

        try {
            Collection remoteCol = (Collection) MyNarrow.narrow(genericHome.findByAnd(modelEntity.entityName, expressions, orderBy), Collection.class);
            collection = remoteToValue(remoteCol);
        } catch (ObjectNotFoundException onfe) {
            Debug.logError(onfe);
        } catch (Exception fe) {
            Debug.logError(fe);
        }

        return collection;
    }

    public Collection findByLike(ModelEntity modelEntity, Map fields, List orderBy) throws GenericEntityException {
        if (modelEntity == null || fields == null) {
            return null;
        }
        Collection collection = null;

        try {
            Collection remoteCol = (Collection) MyNarrow.narrow(genericHome.findByLike(modelEntity.entityName, fields, orderBy), Collection.class);
            collection = remoteToValue(remoteCol);
        } catch (ObjectNotFoundException onfe) {
            Debug.logError(onfe);
        } catch (Exception fe) {
            Debug.logError(fe);
        }

        return collection;
    }

    public Collection findByClause(ModelEntity modelEntity, List entityClauses, Map fields, List orderBy) throws GenericEntityException {
        if (modelEntity == null || entityClauses == null) {
            return null;
        }
        Collection collection = null;

        try {
            Collection remoteCol = (Collection) MyNarrow.narrow(genericHome.findByClause(modelEntity.entityName, entityClauses, fields, orderBy), Collection.class);
            collection = remoteToValue(remoteCol);
        } catch (ObjectNotFoundException onfe) {
            Debug.logError(onfe);
        } catch (Exception fe) {
            Debug.logError(fe);
        }

        return collection;
    }

    /** Removes/deletes Generic Entity records found by all of the specified fields (ie: combined using AND)
     *@param entityName The Name of the Entity as defined in the entity XML file
     *@param fields The fields of the named entity to query by with their corresponging values
     *@return Collection of GenericValue instances that match the query
     */
    public void removeByAnd(ModelEntity modelEntity, Map fields) throws GenericEntityException {
        if (modelEntity == null || fields == null) {
            return;
        }
        Collection remoteCol = null;

        try {
            remoteCol = (Collection) MyNarrow.narrow(genericHome.findByAnd(modelEntity.entityName, fields, null), Collection.class);
        } catch (ObjectNotFoundException onfe) {
            Debug.logError(onfe);
        } catch (Exception fe) {
            Debug.logError(fe);
        }

        Iterator iterator = UtilMisc.toIterator(remoteCol);
        while (iterator != null && iterator.hasNext()) {
            try {
                GenericRemote generic = (GenericRemote) iterator.next();
                Debug.logInfo("Removing GenericValue: " + generic.toString());
                generic.remove();
            } catch (Exception e) {
                Debug.logError(e);
            }
        }
    }

    /** Store the Entity from the GenericValue to the persistent store
     *@param value GenericValue instance containing the entity
     */
    public void store(GenericValue value) throws GenericEntityException {
        GenericRemote remote = null;
        try {
            remote = (GenericRemote) MyNarrow.narrow(genericHome.findByPrimaryKey(value.getPrimaryKey()), GenericRemote.class);
        } catch (ObjectNotFoundException onfe) {
            Debug.logError(onfe);
        } catch (Exception fe) {
            Debug.logError(fe);
        }
        try {
            remote.setValueObject(value);
        } catch (java.rmi.RemoteException re) {
            Debug.logError(re);
        }
    }

    /** Store the Entities from the Collection GenericValue instances to the persistent store.
     *  This is different than the normal store method in that the store method only does
     *  an update, while the storeAll method checks to see if each entity exists, then
     *  either does an insert or an update as appropriate.
     *  These updates all happen in one transaction, so they will either all succeed or all fail,
     *  if the data source supports transactions. This is just like to othersToStore feature
     *  of the GenericEntity on a create or store.
     *@param values Collection of GenericValue instances containing the entities to store
     */
    public void storeAll(Collection values) throws GenericEntityException {
        //this should be implemented through the home interface to do the entire process on the server side
        if (values == null || values.size() == 0) {
            return;
        }

        try {
            genericHome.storeAll(values);
        } catch (CreateException onfe) {
            throw new GenericEntityException("Error creating value", onfe);
        } catch (Exception e) {
            throw new GenericEntityException("Error storing values", e);
        }
    }

    private Collection remoteToValue(Collection remoteCol) {
        Iterator iter = UtilMisc.toIterator(remoteCol);
        Collection col = new LinkedList();

        while (iter != null && iter.hasNext()) {
            GenericRemote generic = (GenericRemote) iter.next();
            try {
                GenericValue value = generic.getValueObject();
                col.add(value);
            } catch (Exception e) {}
        }
        return col;
    }

    /** Check the datasource to make sure the entity definitions are correct, optionally adding missing entities or fields on the server
     *@param modelEntities Map of entityName names and ModelEntity values
     *@param messages Collection to put any result messages in
     *@param addMissing Flag indicating whether or not to add missing entities and fields on the server
     */
    public void checkDataSource(Map modelEntities, Collection messages, boolean addMissing) throws GenericEntityException {
        throw new GenericNotImplementedException("Check Data Source not supported for EJB data source, this is done on the EJB server.");
    }
}

