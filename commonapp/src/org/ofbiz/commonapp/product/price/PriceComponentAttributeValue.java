
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Price Component Attribute Entity
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
public class PriceComponentAttributeValue implements PriceComponentAttribute
{
  /** The variable of the PRICE_COMPONENT_ID column of the PRICE_COMPONENT_ATTRIBUTE table. */
  private String priceComponentId;
  /** The variable of the NAME column of the PRICE_COMPONENT_ATTRIBUTE table. */
  private String name;
  /** The variable of the VALUE column of the PRICE_COMPONENT_ATTRIBUTE table. */
  private String value;

  private PriceComponentAttribute priceComponentAttribute;

  public PriceComponentAttributeValue()
  {
    this.priceComponentId = null;
    this.name = null;
    this.value = null;

    this.priceComponentAttribute = null;
  }

  public PriceComponentAttributeValue(PriceComponentAttribute priceComponentAttribute) throws RemoteException
  {
    if(priceComponentAttribute == null) return;
  
    this.priceComponentId = priceComponentAttribute.getPriceComponentId();
    this.name = priceComponentAttribute.getName();
    this.value = priceComponentAttribute.getValue();

    this.priceComponentAttribute = priceComponentAttribute;
  }

  public PriceComponentAttributeValue(PriceComponentAttribute priceComponentAttribute, String priceComponentId, String name, String value)
  {
    if(priceComponentAttribute == null) return;
  
    this.priceComponentId = priceComponentId;
    this.name = name;
    this.value = value;

    this.priceComponentAttribute = priceComponentAttribute;
  }


  /** Get the primary key of the PRICE_COMPONENT_ID column of the PRICE_COMPONENT_ATTRIBUTE table. */
  public String getPriceComponentId()  throws RemoteException { return priceComponentId; }

  /** Get the primary key of the NAME column of the PRICE_COMPONENT_ATTRIBUTE table. */
  public String getName()  throws RemoteException { return name; }

  /** Get the value of the VALUE column of the PRICE_COMPONENT_ATTRIBUTE table. */
  public String getValue() throws RemoteException { return value; }
  /** Set the value of the VALUE column of the PRICE_COMPONENT_ATTRIBUTE table. */
  public void setValue(String value) throws RemoteException
  {
    this.value = value;
    if(priceComponentAttribute!=null) priceComponentAttribute.setValue(value);
  }

  /** Get the value object of the PriceComponentAttribute class. */
  public PriceComponentAttribute getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PriceComponentAttribute class. */
  public void setValueObject(PriceComponentAttribute valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(priceComponentAttribute!=null) priceComponentAttribute.setValueObject(valueObject);

    if(priceComponentId == null) priceComponentId = valueObject.getPriceComponentId();
    if(name == null) name = valueObject.getName();
    value = valueObject.getValue();
  }


  /** Get the  PriceComponent entity corresponding to this entity. */
  public PriceComponent getPriceComponent() { return PriceComponentHelper.findByPrimaryKey(priceComponentId); }
  /** Remove the  PriceComponent entity corresponding to this entity. */
  public void removePriceComponent() { PriceComponentHelper.removeByPrimaryKey(priceComponentId); }

  /** Get a collection of  PriceComponentTypeAttr related entities. */
  public Collection getPriceComponentTypeAttrs() { return PriceComponentTypeAttrHelper.findByName(name); }
  /** Get the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public PriceComponentTypeAttr getPriceComponentTypeAttr(String priceComponentTypeId) { return PriceComponentTypeAttrHelper.findByPrimaryKey(priceComponentTypeId, name); }
  /** Remove  PriceComponentTypeAttr related entities. */
  public void removePriceComponentTypeAttrs() { PriceComponentTypeAttrHelper.removeByName(name); }
  /** Remove the  PriceComponentTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponentTypeAttr(String priceComponentTypeId) { PriceComponentTypeAttrHelper.removeByPrimaryKey(priceComponentTypeId, name); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(priceComponentAttribute!=null) return priceComponentAttribute.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(priceComponentAttribute!=null) return priceComponentAttribute.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(priceComponentAttribute!=null) return priceComponentAttribute.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(priceComponentAttribute!=null) return priceComponentAttribute.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(priceComponentAttribute!=null) priceComponentAttribute.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
