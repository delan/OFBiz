package org.ofbiz.core.entity;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;
import org.ofbiz.core.util.*;

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
public class GenericHelperEJB extends GenericHelperCache
{
  /** A variable to cache the Home object for the Generic EJB */
  private GenericHome genericHome;

  public GenericHelperEJB(String serverName) 
  { 
    getGenericHome(serverName);
    primaryKeyCache = new UtilCache("FindByPrimaryKeyEJB-" + serverName);
    allCache = new UtilCache("FindAllEJB-" + serverName);
    andCache = new UtilCache("FindByAndEJB-" + serverName);
  }
  
  /** Initializes the genericHome, from a JNDI lookup */
  public void getGenericHome(String serverName)
  {
    JNDIContext myJNDIContext;
    myJNDIContext = new JNDIContext(serverName);
    InitialContext initialContext = myJNDIContext.getInitialContext();
    try
    {
      Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.core.entity.GenericHome");
      genericHome = (GenericHome)MyNarrow.narrow(homeObject, GenericHome.class);
    }
    catch(Exception e1) { Debug.logError(e1); }
    Debug.logInfo("generic home obtained " + genericHome);
  }

  /** Creates a Entity in the form of a GenericValue and write it to the database
   *@return GenericValue instance containing the new instance
   */
  public GenericValue create(String entityName, Map fields)
  {
    if(entityName == null || fields == null) { return null; }
    GenericValue genericValue = null;

    try
    { 
      GenericRemote generic = (GenericRemote)MyNarrow.narrow(genericHome.create(entityName, fields), GenericRemote.class);
      genericValue = generic.getValueObject();
      genericValue.helper = this;
    }
    catch(CreateException ce) { Debug.logError(ce); }
    catch(Exception fe) { Debug.logError(fe); }
    return genericValue;
  }

  /** Creates a Entity in the form of a GenericValue and write it to the database
   *@return GenericValue instance containing the new instance
   */
  public GenericValue create(GenericValue value)
  {
    if(value == null) { return null; }
    GenericValue genericValue = null;

    try
    { 
      GenericRemote generic = (GenericRemote)MyNarrow.narrow(genericHome.create(value), GenericRemote.class);
      genericValue = generic.getValueObject();
      genericValue.helper = this;
    }
    catch(CreateException ce) { Debug.logError(ce); }
    catch(Exception fe) { Debug.logError(fe); }
    return genericValue;
  }

  /** Creates a Entity in the form of a GenericValue and write it to the database
   *@return GenericValue instance containing the new instance
   */
  public GenericValue create(GenericPK primaryKey)
  {
    if(primaryKey == null) { return null; }
    GenericValue genericValue = null;
    try
    { 
      GenericRemote generic = (GenericRemote)MyNarrow.narrow(genericHome.create(primaryKey), GenericRemote.class);
      genericValue = generic.getValueObject();
      genericValue.helper = this;
    }
    catch(CreateException ce) { Debug.logError(ce); }
    catch(Exception fe) { Debug.logError(fe); }
    return genericValue;
  }

  /** Find a Generic Entity by its Primary Key
   *@param primaryKey The primary key to find by.
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericValue findByPrimaryKey(GenericPK primaryKey)
  {
    if(primaryKey == null) { return null; }
    GenericValue genericValue = null;

    try
    {
      GenericRemote generic = (GenericRemote)MyNarrow.narrow(genericHome.findByPrimaryKey(primaryKey), GenericRemote.class);
      genericValue = generic.getValueObject();
      genericValue.helper = this;
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return genericValue;
  }

  /** Remove a Generic Entity corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public void removeByPrimaryKey(GenericPK primaryKey)
  {
    if(primaryKey == null) return;
    GenericValue generic = findByPrimaryKey(primaryKey);
    try 
    { 
      Debug.logInfo("Removing GenericValue: " + generic.toString());
      if(generic != null) generic.remove();
    }
    catch(Exception e) { Debug.logWarning(e); }
  }

  /** Finds all Generic entities
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return    Collection containing all Generic entities
   */
  public Collection findAll(String entityName, List orderBy)
  {
    if(entityName == null) return null;
    Collection collection = null;
    Debug.logInfo("GenericHelper.findAll");

    try 
    { 
      Collection remoteCol = (Collection)MyNarrow.narrow(genericHome.findAll(entityName, orderBy), Collection.class); 
      collection = remoteToValue(remoteCol);
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@param order The fields of the named entity to order the query by; optionall add a " ASC" for ascending or " DESC" for descending
   *@return Collection of GenericValue instances that match the query
   */
  public Collection findByAnd(String entityName, Map fields, List orderBy)
  {
    if(entityName == null || fields == null) { return null; }
    Collection collection = null;

    try 
    { 
      Collection remoteCol = (Collection)MyNarrow.narrow(genericHome.findByAnd(entityName, fields, orderBy), Collection.class);
      collection = remoteToValue(remoteCol);
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }
  
  /** Removes/deletes Generic Entity records found by all of the specified fields (ie: combined using AND)
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@return Collection of GenericValue instances that match the query
   */
  public void removeByAnd(String entityName, Map fields)
  {
    if(entityName == null || fields == null) { return; }
    Collection remoteCol = null;
    
    try 
    { 
      remoteCol = (Collection) MyNarrow.narrow(genericHome.findByAnd(entityName, fields, null), Collection.class);
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    Iterator iterator = UtilMisc.toIterator(remoteCol);
    while(iterator != null && iterator.hasNext())
    {
      try
      {
        GenericRemote generic = (GenericRemote)iterator.next();
        Debug.logInfo("Removing GenericValue: " + generic.toString());
        generic.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }
  
  public Collection remoteToValue(Collection remoteCol)
  {
    Iterator iter = UtilMisc.toIterator(remoteCol);
    Collection col = new LinkedList();
    
    while(iter != null && iter.hasNext())
    {
      GenericRemote generic = (GenericRemote)iter.next();
      try 
      { 
        GenericValue value = generic.getValueObject();
        value.helper = this;
        col.add(value);
      }
      catch(Exception e) {}
    }
    return col;
  }
  
  /** Store the Entity from the GenericValue to the persistent store
   *@param value GenericValue instance containing the entity
   */
  public void store(GenericValue value)
  {
    GenericRemote remote = null;
    try { remote = (GenericRemote)MyNarrow.narrow(genericHome.findByPrimaryKey(value.getPrimaryKey()), GenericRemote.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    try { remote.setValueObject(value); }
    catch(java.rmi.RemoteException re) { Debug.logError(re); }
  }  
  
  /** Get the named Related Entity for the GenericValue from the persistent store
   *@param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   *@param value GenericValue instance containing the entity
   *@return Collection of GenericValue instances as specified in the relation definition
   */
  public Collection getRelated(String relationName, GenericValue value)
  {
    GenericRemote remote = null;
    Collection collection = null;
    try { remote = (GenericRemote)MyNarrow.narrow(genericHome.findByPrimaryKey(value.getPrimaryKey()), GenericRemote.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    try 
    { 
      Collection remoteCol = remote.getRelated(relationName);
      collection = remoteToValue(remoteCol);
    }
    catch(java.rmi.RemoteException re) { Debug.logError(re); }
    return collection;
  }  
  
  /** Remove the named Related Entity for the GenericValue from the persistent store
   * @param relationName String containing the relation name which is the combination of relation.title and relation.rel-entity-name as specified in the entity XML definition file
   * @param value GenericValue instance containing the entity
   */
  public void removeRelated(String relationName, GenericValue value)
  {
    GenericRemote remote = null;
    try { remote = (GenericRemote)MyNarrow.narrow(genericHome.findByPrimaryKey(value.getPrimaryKey()), GenericRemote.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    try { remote.removeRelated(relationName); }
    catch(java.rmi.RemoteException re) { Debug.logError(re); }
  }  
}
