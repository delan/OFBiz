
package org.ofbiz.commonapp.product.price;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Sale Type Entity
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
public class SaleTypeValue implements SaleType
{
  /** The variable of the SALE_TYPE_ID column of the SALE_TYPE table. */
  private String saleTypeId;
  /** The variable of the DESCRIPTION column of the SALE_TYPE table. */
  private String description;

  private SaleType saleType;

  public SaleTypeValue()
  {
    this.saleTypeId = null;
    this.description = null;

    this.saleType = null;
  }

  public SaleTypeValue(SaleType saleType) throws RemoteException
  {
    if(saleType == null) return;
  
    this.saleTypeId = saleType.getSaleTypeId();
    this.description = saleType.getDescription();

    this.saleType = saleType;
  }

  public SaleTypeValue(SaleType saleType, String saleTypeId, String description)
  {
    if(saleType == null) return;
  
    this.saleTypeId = saleTypeId;
    this.description = description;

    this.saleType = saleType;
  }


  /** Get the primary key of the SALE_TYPE_ID column of the SALE_TYPE table. */
  public String getSaleTypeId()  throws RemoteException { return saleTypeId; }

  /** Get the value of the DESCRIPTION column of the SALE_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the SALE_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(saleType!=null) saleType.setDescription(description);
  }

  /** Get the value object of the SaleType class. */
  public SaleType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the SaleType class. */
  public void setValueObject(SaleType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(saleType!=null) saleType.setValueObject(valueObject);

    if(saleTypeId == null) saleTypeId = valueObject.getSaleTypeId();
    description = valueObject.getDescription();
  }


  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() { return PriceComponentHelper.findBySaleTypeId(saleTypeId); }
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) { return PriceComponentHelper.findByPrimaryKey(priceComponentId); }
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() { PriceComponentHelper.removeBySaleTypeId(saleTypeId); }
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) { PriceComponentHelper.removeByPrimaryKey(priceComponentId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(saleType!=null) return saleType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(saleType!=null) return saleType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(saleType!=null) return saleType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(saleType!=null) return saleType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(saleType!=null) saleType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
