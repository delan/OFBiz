
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Price Component Type Attribute Entity
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
 *@created    Fri Jul 27 01:18:30 MDT 2001
 *@version    1.0
 */
public class PriceComponentTypeAttrBean implements EntityBean
{
  /** The variable for the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT_TYPE_ATTR table. */
  public String priceComponentTypeId;
  /** The variable for the NAME column of the PRICE_COMPONENT_TYPE_ATTR table. */
  public String name;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the PriceComponentTypeAttrBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT_TYPE_ATTR table. */
  public String getPriceComponentTypeId() { return priceComponentTypeId; }

  /** Get the primary key NAME column of the PRICE_COMPONENT_TYPE_ATTR table. */
  public String getName() { return name; }

  /** Sets the values from ValueObject attribute of the PriceComponentTypeAttrBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(PriceComponentTypeAttr valueObject)
  {
  }

  /** Gets the ValueObject attribute of the PriceComponentTypeAttrBean object
   *@return    The ValueObject value
   */
  public PriceComponentTypeAttr getValueObject()
  {
    if(this.entityContext != null)
    {
      return new PriceComponentTypeAttrValue((PriceComponentTypeAttr)this.entityContext.getEJBObject(), priceComponentTypeId, name);
    }
    else { return null; }
  }


  /** Get the  PriceComponentType entity corresponding to this entity. */
  public PriceComponentType getPriceComponentType() { return PriceComponentTypeHelper.findByPrimaryKey(priceComponentTypeId); }
  /** Remove the  PriceComponentType entity corresponding to this entity. */
  public void removePriceComponentType() { PriceComponentTypeHelper.removeByPrimaryKey(priceComponentTypeId); }

  /** Get a collection of  PriceComponentAttribute related entities. */
  public Collection getPriceComponentAttributes() { return PriceComponentAttributeHelper.findByName(name); }
  /** Get the  PriceComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentAttribute getPriceComponentAttribute(String priceComponentId) { return PriceComponentAttributeHelper.findByPrimaryKey(priceComponentId, name); }
  /** Remove  PriceComponentAttribute related entities. */
  public void removePriceComponentAttributes() { PriceComponentAttributeHelper.removeByName(name); }
  /** Remove the  PriceComponentAttribute keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponentAttribute(String priceComponentId) { PriceComponentAttributeHelper.removeByPrimaryKey(priceComponentId, name); }

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() { return PriceComponentHelper.findByPriceComponentTypeId(priceComponentTypeId); }
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) { return PriceComponentHelper.findByPrimaryKey(priceComponentId); }
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() { PriceComponentHelper.removeByPriceComponentTypeId(priceComponentTypeId); }
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) { PriceComponentHelper.removeByPrimaryKey(priceComponentId); }


  /** Description of the Method
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public org.ofbiz.commonapp.product.price.PriceComponentTypeAttrPK ejbCreate(String priceComponentTypeId, String name) throws CreateException
  {
    this.priceComponentTypeId = priceComponentTypeId;
    this.name = name;
    return null;
  }

  /** Description of the Method
   *@param  priceComponentTypeId                  Field of the PRICE_COMPONENT_TYPE_ID column.
   *@param  name                  Field of the NAME column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String priceComponentTypeId, String name) throws CreateException {}

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
