
package org.ofbiz.commonapp.common.geo;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Geographic Boundary Entity
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
 *@created    Fri Jul 27 01:18:21 MDT 2001
 *@version    1.0
 */
public class GeoBean implements EntityBean
{
  /** The variable for the GEO_ID column of the GEO table. */
  public String geoId;
  /** The variable for the GEO_TYPE_ID column of the GEO table. */
  public String geoTypeId;
  /** The variable for the NAME column of the GEO table. */
  public String name;
  /** The variable for the GEO_CODE column of the GEO table. */
  public String geoCode;
  /** The variable for the ABBREVIATION column of the GEO table. */
  public String abbreviation;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the GeoBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key GEO_ID column of the GEO table. */
  public String getGeoId() { return geoId; }

  /** Get the value of the GEO_TYPE_ID column of the GEO table. */
  public String getGeoTypeId() { return geoTypeId; }
  /** Set the value of the GEO_TYPE_ID column of the GEO table. */
  public void setGeoTypeId(String geoTypeId)
  {
    this.geoTypeId = geoTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the NAME column of the GEO table. */
  public String getName() { return name; }
  /** Set the value of the NAME column of the GEO table. */
  public void setName(String name)
  {
    this.name = name;
    ejbIsModified = true;
  }

  /** Get the value of the GEO_CODE column of the GEO table. */
  public String getGeoCode() { return geoCode; }
  /** Set the value of the GEO_CODE column of the GEO table. */
  public void setGeoCode(String geoCode)
  {
    this.geoCode = geoCode;
    ejbIsModified = true;
  }

  /** Get the value of the ABBREVIATION column of the GEO table. */
  public String getAbbreviation() { return abbreviation; }
  /** Set the value of the ABBREVIATION column of the GEO table. */
  public void setAbbreviation(String abbreviation)
  {
    this.abbreviation = abbreviation;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the GeoBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(Geo valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getGeoTypeId() != null)
      {
        this.geoTypeId = valueObject.getGeoTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getName() != null)
      {
        this.name = valueObject.getName();
        ejbIsModified = true;
      }
      if(valueObject.getGeoCode() != null)
      {
        this.geoCode = valueObject.getGeoCode();
        ejbIsModified = true;
      }
      if(valueObject.getAbbreviation() != null)
      {
        this.abbreviation = valueObject.getAbbreviation();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the GeoBean object
   *@return    The ValueObject value
   */
  public Geo getValueObject()
  {
    if(this.entityContext != null)
    {
      return new GeoValue((Geo)this.entityContext.getEJBObject(), geoId, geoTypeId, name, geoCode, abbreviation);
    }
    else { return null; }
  }


  /** Get the  GeoType entity corresponding to this entity. */
  public GeoType getGeoType() { return GeoTypeHelper.findByPrimaryKey(geoTypeId); }
  /** Remove the  GeoType entity corresponding to this entity. */
  public void removeGeoType() { GeoTypeHelper.removeByPrimaryKey(geoTypeId); }

  /** Get a collection of Main GeoAssoc related entities. */
  public Collection getMainGeoAssocs() { return GeoAssocHelper.findByGeoId(geoId); }
  /** Get the Main GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public GeoAssoc getMainGeoAssoc(String geoIdTo) { return GeoAssocHelper.findByPrimaryKey(geoId, geoIdTo); }
  /** Remove Main GeoAssoc related entities. */
  public void removeMainGeoAssocs() { GeoAssocHelper.removeByGeoId(geoId); }
  /** Remove the Main GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeMainGeoAssoc(String geoIdTo) { GeoAssocHelper.removeByPrimaryKey(geoId, geoIdTo); }

  /** Get a collection of Assoc GeoAssoc related entities. */
  public Collection getAssocGeoAssocs() { return GeoAssocHelper.findByGeoIdTo(geoId); }
  /** Get the Assoc GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public GeoAssoc getAssocGeoAssoc(String geoId) { return GeoAssocHelper.findByPrimaryKey(geoId, geoId); }
  /** Remove Assoc GeoAssoc related entities. */
  public void removeAssocGeoAssocs() { GeoAssocHelper.removeByGeoIdTo(geoId); }
  /** Remove the Assoc GeoAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeAssocGeoAssoc(String geoId) { GeoAssocHelper.removeByPrimaryKey(geoId, geoId); }


  /** Description of the Method
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoTypeId                  Field of the GEO_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@param  geoCode                  Field of the GEO_CODE column.
   *@param  abbreviation                  Field of the ABBREVIATION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String geoId, String geoTypeId, String name, String geoCode, String abbreviation) throws CreateException
  {
    this.geoId = geoId;
    this.geoTypeId = geoTypeId;
    this.name = name;
    this.geoCode = geoCode;
    this.abbreviation = abbreviation;
    return null;
  }

  /** Description of the Method
   *@param  geoId                  Field of the GEO_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String geoId) throws CreateException
  {
    return ejbCreate(geoId, null, null, null, null);
  }

  /** Description of the Method
   *@param  geoId                  Field of the GEO_ID column.
   *@param  geoTypeId                  Field of the GEO_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@param  geoCode                  Field of the GEO_CODE column.
   *@param  abbreviation                  Field of the ABBREVIATION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String geoId, String geoTypeId, String name, String geoCode, String abbreviation) throws CreateException {}

  /** Description of the Method
   *@param  geoId                  Field of the GEO_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String geoId) throws CreateException
  {
    ejbPostCreate(geoId, null, null, null, null);
  }

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
