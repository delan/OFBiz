
package org.ofbiz.commonapp.product.supplier;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Supplier Rating Type Entity
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
 *@created    Fri Jul 27 01:18:33 MDT 2001
 *@version    1.0
 */
public class SupplierRatingTypeValue implements SupplierRatingType
{
  /** The variable of the SUPPLIER_RATING_TYPE_ID column of the SUPPLIER_RATING_TYPE table. */
  private String supplierRatingTypeId;
  /** The variable of the DESCRIPTION column of the SUPPLIER_RATING_TYPE table. */
  private String description;

  private SupplierRatingType supplierRatingType;

  public SupplierRatingTypeValue()
  {
    this.supplierRatingTypeId = null;
    this.description = null;

    this.supplierRatingType = null;
  }

  public SupplierRatingTypeValue(SupplierRatingType supplierRatingType) throws RemoteException
  {
    if(supplierRatingType == null) return;
  
    this.supplierRatingTypeId = supplierRatingType.getSupplierRatingTypeId();
    this.description = supplierRatingType.getDescription();

    this.supplierRatingType = supplierRatingType;
  }

  public SupplierRatingTypeValue(SupplierRatingType supplierRatingType, String supplierRatingTypeId, String description)
  {
    if(supplierRatingType == null) return;
  
    this.supplierRatingTypeId = supplierRatingTypeId;
    this.description = description;

    this.supplierRatingType = supplierRatingType;
  }


  /** Get the primary key of the SUPPLIER_RATING_TYPE_ID column of the SUPPLIER_RATING_TYPE table. */
  public String getSupplierRatingTypeId()  throws RemoteException { return supplierRatingTypeId; }

  /** Get the value of the DESCRIPTION column of the SUPPLIER_RATING_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the SUPPLIER_RATING_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(supplierRatingType!=null) supplierRatingType.setDescription(description);
  }

  /** Get the value object of the SupplierRatingType class. */
  public SupplierRatingType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the SupplierRatingType class. */
  public void setValueObject(SupplierRatingType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(supplierRatingType!=null) supplierRatingType.setValueObject(valueObject);

    if(supplierRatingTypeId == null) supplierRatingTypeId = valueObject.getSupplierRatingTypeId();
    description = valueObject.getDescription();
  }


  /** Get a collection of  SupplierProduct related entities. */
  public Collection getSupplierProducts() { return SupplierProductHelper.findBySupplierRatingTypeId(supplierRatingTypeId); }
  /** Get the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public SupplierProduct getSupplierProduct(String productId, String partyId) { return SupplierProductHelper.findByPrimaryKey(productId, partyId); }
  /** Remove  SupplierProduct related entities. */
  public void removeSupplierProducts() { SupplierProductHelper.removeBySupplierRatingTypeId(supplierRatingTypeId); }
  /** Remove the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public void removeSupplierProduct(String productId, String partyId) { SupplierProductHelper.removeByPrimaryKey(productId, partyId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(supplierRatingType!=null) return supplierRatingType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(supplierRatingType!=null) return supplierRatingType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(supplierRatingType!=null) return supplierRatingType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(supplierRatingType!=null) return supplierRatingType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(supplierRatingType!=null) supplierRatingType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
