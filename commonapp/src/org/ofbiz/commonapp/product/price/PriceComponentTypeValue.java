
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Price Component Type Entity
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
 *@created    Fri Jul 27 01:18:29 MDT 2001
 *@version    1.0
 */
public class PriceComponentTypeValue implements PriceComponentType
{
  /** The variable of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT_TYPE table. */
  private String priceComponentTypeId;
  /** The variable of the PARENT_TYPE_ID column of the PRICE_COMPONENT_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the PRICE_COMPONENT_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the PRICE_COMPONENT_TYPE table. */
  private String description;

  private PriceComponentType priceComponentType;

  public PriceComponentTypeValue()
  {
    this.priceComponentTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.priceComponentType = null;
  }

  public PriceComponentTypeValue(PriceComponentType priceComponentType) throws RemoteException
  {
    if(priceComponentType == null) return;
  
    this.priceComponentTypeId = priceComponentType.getPriceComponentTypeId();
    this.parentTypeId = priceComponentType.getParentTypeId();
    this.hasTable = priceComponentType.getHasTable();
    this.description = priceComponentType.getDescription();

    this.priceComponentType = priceComponentType;
  }

  public PriceComponentTypeValue(PriceComponentType priceComponentType, String priceComponentTypeId, String parentTypeId, String hasTable, String description)
  {
    if(priceComponentType == null) return;
  
    this.priceComponentTypeId = priceComponentTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.priceComponentType = priceComponentType;
  }


  /** Get the primary key of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT_TYPE table. */
  public String getPriceComponentTypeId()  throws RemoteException { return priceComponentTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the PRICE_COMPONENT_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the PRICE_COMPONENT_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(priceComponentType!=null) priceComponentType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the PRICE_COMPONENT_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the PRICE_COMPONENT_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(priceComponentType!=null) priceComponentType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the PRICE_COMPONENT_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PRICE_COMPONENT_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(priceComponentType!=null) priceComponentType.setDescription(description);
  }

  /** Get the value object of the PriceComponentType class. */
  public PriceComponentType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PriceComponentType class. */
  public void setValueObject(PriceComponentType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(priceComponentType!=null) priceComponentType.setValueObject(valueObject);

    if(priceComponentTypeId == null) priceComponentTypeId = valueObject.getPriceComponentTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent PriceComponentType entity corresponding to this entity. */
  public PriceComponentType getParentPriceComponentType() { return PriceComponentTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent PriceComponentType entity corresponding to this entity. */
  public void removeParentPriceComponentType() { PriceComponentTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child PriceComponentType related entities. */
  public Collection getChildPriceComponentTypes() { return PriceComponentTypeHelper.findByParentTypeId(priceComponentTypeId); }
  /** Get the Child PriceComponentType keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentType getChildPriceComponentType(String priceComponentTypeId) { return PriceComponentTypeHelper.findByPrimaryKey(priceComponentTypeId); }
  /** Remove Child PriceComponentType related entities. */
  public void removeChildPriceComponentTypes() { PriceComponentTypeHelper.removeByParentTypeId(priceComponentTypeId); }
  /** Remove the Child PriceComponentType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildPriceComponentType(String priceComponentTypeId) { PriceComponentTypeHelper.removeByPrimaryKey(priceComponentTypeId); }

  /** Get a collection of  PriceComponentTypeAttr related entities. */
  public Collection getPriceComponentTypeAttrs() { return PriceComponentTypeAttrHelper.findByPriceComponentTypeId(priceComponentTypeId); }
  /** Get the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentTypeAttr getPriceComponentTypeAttr(String name) { return PriceComponentTypeAttrHelper.findByPrimaryKey(priceComponentTypeId, name); }
  /** Remove  PriceComponentTypeAttr related entities. */
  public void removePriceComponentTypeAttrs() { PriceComponentTypeAttrHelper.removeByPriceComponentTypeId(priceComponentTypeId); }
  /** Remove the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponentTypeAttr(String name) { PriceComponentTypeAttrHelper.removeByPrimaryKey(priceComponentTypeId, name); }

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() { return PriceComponentHelper.findByPriceComponentTypeId(priceComponentTypeId); }
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) { return PriceComponentHelper.findByPrimaryKey(priceComponentId); }
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() { PriceComponentHelper.removeByPriceComponentTypeId(priceComponentTypeId); }
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) { PriceComponentHelper.removeByPrimaryKey(priceComponentId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(priceComponentType!=null) return priceComponentType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(priceComponentType!=null) return priceComponentType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(priceComponentType!=null) return priceComponentType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(priceComponentType!=null) return priceComponentType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(priceComponentType!=null) priceComponentType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
