
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


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
public class PriceComponentTypeAttrValue implements PriceComponentTypeAttr
{
  /** The variable of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT_TYPE_ATTR table. */
  private String priceComponentTypeId;
  /** The variable of the NAME column of the PRICE_COMPONENT_TYPE_ATTR table. */
  private String name;

  private PriceComponentTypeAttr priceComponentTypeAttr;

  public PriceComponentTypeAttrValue()
  {
    this.priceComponentTypeId = null;
    this.name = null;

    this.priceComponentTypeAttr = null;
  }

  public PriceComponentTypeAttrValue(PriceComponentTypeAttr priceComponentTypeAttr) throws RemoteException
  {
    if(priceComponentTypeAttr == null) return;
  
    this.priceComponentTypeId = priceComponentTypeAttr.getPriceComponentTypeId();
    this.name = priceComponentTypeAttr.getName();

    this.priceComponentTypeAttr = priceComponentTypeAttr;
  }

  public PriceComponentTypeAttrValue(PriceComponentTypeAttr priceComponentTypeAttr, String priceComponentTypeId, String name)
  {
    if(priceComponentTypeAttr == null) return;
  
    this.priceComponentTypeId = priceComponentTypeId;
    this.name = name;

    this.priceComponentTypeAttr = priceComponentTypeAttr;
  }


  /** Get the primary key of the PRICE_COMPONENT_TYPE_ID column of the PRICE_COMPONENT_TYPE_ATTR table. */
  public String getPriceComponentTypeId()  throws RemoteException { return priceComponentTypeId; }

  /** Get the primary key of the NAME column of the PRICE_COMPONENT_TYPE_ATTR table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value object of the PriceComponentTypeAttr class. */
  public PriceComponentTypeAttr getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PriceComponentTypeAttr class. */
  public void setValueObject(PriceComponentTypeAttr valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(priceComponentTypeAttr!=null) priceComponentTypeAttr.setValueObject(valueObject);

    if(priceComponentTypeId == null) priceComponentTypeId = valueObject.getPriceComponentTypeId();
    if(name == null) name = valueObject.getName();
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


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(priceComponentTypeAttr!=null) return priceComponentTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(priceComponentTypeAttr!=null) return priceComponentTypeAttr.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(priceComponentTypeAttr!=null) return priceComponentTypeAttr.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(priceComponentTypeAttr!=null) return priceComponentTypeAttr.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(priceComponentTypeAttr!=null) priceComponentTypeAttr.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
