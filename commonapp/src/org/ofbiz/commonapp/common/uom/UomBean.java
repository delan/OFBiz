
package org.ofbiz.commonapp.common.uom;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Unit Of Measure Entity
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
 *@created    Fri Jul 27 01:18:22 MDT 2001
 *@version    1.0
 */
public class UomBean implements EntityBean
{
  /** The variable for the UOM_ID column of the UOM table. */
  public String uomId;
  /** The variable for the UOM_TYPE_ID column of the UOM table. */
  public String uomTypeId;
  /** The variable for the ABBREVIATION column of the UOM table. */
  public String abbreviation;
  /** The variable for the DESCRIPTION column of the UOM table. */
  public String description;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the UomBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key UOM_ID column of the UOM table. */
  public String getUomId() { return uomId; }

  /** Get the value of the UOM_TYPE_ID column of the UOM table. */
  public String getUomTypeId() { return uomTypeId; }
  /** Set the value of the UOM_TYPE_ID column of the UOM table. */
  public void setUomTypeId(String uomTypeId)
  {
    this.uomTypeId = uomTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the ABBREVIATION column of the UOM table. */
  public String getAbbreviation() { return abbreviation; }
  /** Set the value of the ABBREVIATION column of the UOM table. */
  public void setAbbreviation(String abbreviation)
  {
    this.abbreviation = abbreviation;
    ejbIsModified = true;
  }

  /** Get the value of the DESCRIPTION column of the UOM table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the UOM table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the UomBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(Uom valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getUomTypeId() != null)
      {
        this.uomTypeId = valueObject.getUomTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getAbbreviation() != null)
      {
        this.abbreviation = valueObject.getAbbreviation();
        ejbIsModified = true;
      }
      if(valueObject.getDescription() != null)
      {
        this.description = valueObject.getDescription();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the UomBean object
   *@return    The ValueObject value
   */
  public Uom getValueObject()
  {
    if(this.entityContext != null)
    {
      return new UomValue((Uom)this.entityContext.getEJBObject(), uomId, uomTypeId, abbreviation, description);
    }
    else { return null; }
  }


  /** Get the  UomType entity corresponding to this entity. */
  public UomType getUomType() { return UomTypeHelper.findByPrimaryKey(uomTypeId); }
  /** Remove the  UomType entity corresponding to this entity. */
  public void removeUomType() { UomTypeHelper.removeByPrimaryKey(uomTypeId); }

  /** Get a collection of Main UomConversion related entities. */
  public Collection getMainUomConversions() { return UomConversionHelper.findByUomId(uomId); }
  /** Get the Main UomConversion keyed by member(s) of this class, and other passed parameters. */
  public UomConversion getMainUomConversion(String uomIdTo) { return UomConversionHelper.findByPrimaryKey(uomId, uomIdTo); }
  /** Remove Main UomConversion related entities. */
  public void removeMainUomConversions() { UomConversionHelper.removeByUomId(uomId); }
  /** Remove the Main UomConversion keyed by member(s) of this class, and other passed parameters. */
  public void removeMainUomConversion(String uomIdTo) { UomConversionHelper.removeByPrimaryKey(uomId, uomIdTo); }

  /** Get a collection of ConvTo UomConversion related entities. */
  public Collection getConvToUomConversions() { return UomConversionHelper.findByUomIdTo(uomId); }
  /** Get the ConvTo UomConversion keyed by member(s) of this class, and other passed parameters. */
  public UomConversion getConvToUomConversion(String uomId) { return UomConversionHelper.findByPrimaryKey(uomId, uomId); }
  /** Remove ConvTo UomConversion related entities. */
  public void removeConvToUomConversions() { UomConversionHelper.removeByUomIdTo(uomId); }
  /** Remove the ConvTo UomConversion keyed by member(s) of this class, and other passed parameters. */
  public void removeConvToUomConversion(String uomId) { UomConversionHelper.removeByPrimaryKey(uomId, uomId); }


  /** Description of the Method
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomTypeId                  Field of the UOM_TYPE_ID column.
   *@param  abbreviation                  Field of the ABBREVIATION column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String uomId, String uomTypeId, String abbreviation, String description) throws CreateException
  {
    this.uomId = uomId;
    this.uomTypeId = uomTypeId;
    this.abbreviation = abbreviation;
    this.description = description;
    return null;
  }

  /** Description of the Method
   *@param  uomId                  Field of the UOM_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String uomId) throws CreateException
  {
    return ejbCreate(uomId, null, null, null);
  }

  /** Description of the Method
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomTypeId                  Field of the UOM_TYPE_ID column.
   *@param  abbreviation                  Field of the ABBREVIATION column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String uomId, String uomTypeId, String abbreviation, String description) throws CreateException {}

  /** Description of the Method
   *@param  uomId                  Field of the UOM_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String uomId) throws CreateException
  {
    ejbPostCreate(uomId, null, null, null);
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
