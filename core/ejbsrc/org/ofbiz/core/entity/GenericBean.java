package org.ofbiz.core.entity;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.entity.model.*;

/**
 * <p><b>Title:</b> Generic Entity EJB Bean Implementation Class
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
 *@created    Sat Aug 11 2001
 *@version    1.0
 */
public class GenericBean extends GenericEntity implements EntityBean
{
  GenericDAO genericDAO = null;
  ModelReader modelReader = null;
  EntityContext entityContext;

  /** Sets the values from ValueObject attribute of the GenericBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(GenericValue valueObject) { this.setNonPKFields(valueObject.getAllFields()); }

  /** Gets the ValueObject attribute of the GenericBean object
   *@return    The ValueObject value
   */
  public GenericValue getValueObject() { return new GenericValue(modelEntity, fields); }

  /** Find a Generic Entity by its Primary Key
   *@param primaryKey The primary key to find by.
   *@return The GenericValue corresponding to the primaryKey
   */
  public GenericPK ejbFindByPrimaryKey(GenericPK primaryKey) throws FinderException
  {
    if(primaryKey == null) { return null; }
    GenericValue genericValue = new GenericValue(primaryKey);
    try { genericDAO.select(genericValue); }
    catch(GenericEntityException e) {
      throw new ObjectNotFoundException("GenericValue not found with primary key: " + primaryKey.toString());
    }
    return primaryKey;
  }
  /** Finds Generic Entity records by all of the specified fields (ie: combined using AND)
   *@param entityName The Name of the Entity as defined in the entity XML file
   *@param fields The fields of the named entity to query by with their corresponging values
   *@param order The fields of the named entity to order the query by; optionally add a " ASC" for ascending or " DESC" for descending
   *@return Collection of GenericValue instances that match the query
   */
  public Collection ejbFindByAnd(String entityName, Map fields, List orderBy) throws FinderException
  {
    ModelEntity modelEntity = modelReader.getModelEntity(entityName);
    try { return entitiesToPKs(genericDAO.selectByAnd(modelEntity, fields, orderBy)); }
    catch(GenericEntityException e) {
      throw new FinderException("FindByAnd failed for entity: " + entityName);
    }
  }
  private Collection entitiesToPKs(Collection col)
  {
    if(col == null) return null;
    Collection newCol = new LinkedList();
    Iterator iter = col.iterator();
    while(iter.hasNext())
    {
      GenericValue value = (GenericValue)iter.next();
      newCol.add(value.getPrimaryKey());
    }
    return newCol;
  }

  public GenericPK ejbCreate(GenericPK primaryKey) throws CreateException 
  { return ejbCreate(primaryKey.entityName, primaryKey.fields); }
  public GenericPK ejbCreate(GenericValue value) throws CreateException 
  { return ejbCreate(value.entityName, value.fields); }
  public GenericPK ejbCreate(String entityName, Map fields) throws CreateException
  {
    this.entityName = entityName;
    this.modelEntity = modelReader.getModelEntity(entityName);
    this.fields = new HashMap(fields);
    if(genericDAO == null) throw new CreateException("Could not get default JDBC Data Access Object.");
    try { genericDAO.insert(this); }
    catch(GenericEntityException e) {
      throw new CreateException("DAO Insert call failed.");
    }
    return this.getPrimaryKey();
  }

  public void ejbPostCreate(GenericPK primaryKey) throws CreateException { ejbPostCreate(primaryKey.entityName, primaryKey.fields); }
  public void ejbPostCreate(GenericValue value) throws CreateException { ejbPostCreate(value.entityName, value.fields); }
  public void ejbPostCreate(String entityName, Map fields) throws CreateException {}

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException
  {
    try { genericDAO.delete(this); } 
    catch(GenericEntityException e) {
      throw new RemoveException("DAO Delete call failed.");
    }
  }

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() 
  { 
    try { 
      genericDAO.select(this); 
      modified = false; 
    }
    catch(GenericEntityException e) { }
  }

  /** Called when the entity bean is stored. */
  public void ejbStore()
  {
    try { 
      if(this.isModified()) { genericDAO.update(this); }
      modified = false; 
    }
    catch(GenericEntityException e) { }
  }

  /** Sets the EntityContext attribute of the GenericBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) 
  { 
    //we really need to find a way of passing the serverName into this bean in the context, look more later
    if(genericDAO == null) genericDAO = GenericDAO.getGenericDAO("default");
    if(modelReader == null) modelReader = ModelReader.getModelReader("default");
    this.entityContext = entityContext; 
  }
  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
