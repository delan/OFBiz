
package org.ofbiz.commonapp.product.supplier;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Preference Type Entity
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
 *@created    Fri Jul 27 01:18:34 MDT 2001
 *@version    1.0
 */
public class SupplierPrefOrderValue implements SupplierPrefOrder
{
  /** The variable of the SUPPLIER_PREF_ORDER_ID column of the SUPPLIER_PREF_ORDER table. */
  private String supplierPrefOrderId;
  /** The variable of the DESCRIPTION column of the SUPPLIER_PREF_ORDER table. */
  private String description;

  private SupplierPrefOrder supplierPrefOrder;

  public SupplierPrefOrderValue()
  {
    this.supplierPrefOrderId = null;
    this.description = null;

    this.supplierPrefOrder = null;
  }

  public SupplierPrefOrderValue(SupplierPrefOrder supplierPrefOrder) throws RemoteException
  {
    if(supplierPrefOrder == null) return;
  
    this.supplierPrefOrderId = supplierPrefOrder.getSupplierPrefOrderId();
    this.description = supplierPrefOrder.getDescription();

    this.supplierPrefOrder = supplierPrefOrder;
  }

  public SupplierPrefOrderValue(SupplierPrefOrder supplierPrefOrder, String supplierPrefOrderId, String description)
  {
    if(supplierPrefOrder == null) return;
  
    this.supplierPrefOrderId = supplierPrefOrderId;
    this.description = description;

    this.supplierPrefOrder = supplierPrefOrder;
  }


  /** Get the primary key of the SUPPLIER_PREF_ORDER_ID column of the SUPPLIER_PREF_ORDER table. */
  public String getSupplierPrefOrderId()  throws RemoteException { return supplierPrefOrderId; }

  /** Get the value of the DESCRIPTION column of the SUPPLIER_PREF_ORDER table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the SUPPLIER_PREF_ORDER table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(supplierPrefOrder!=null) supplierPrefOrder.setDescription(description);
  }

  /** Get the value object of the SupplierPrefOrder class. */
  public SupplierPrefOrder getValueObject() throws RemoteException { return this; }
  /** Set the value object of the SupplierPrefOrder class. */
  public void setValueObject(SupplierPrefOrder valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(supplierPrefOrder!=null) supplierPrefOrder.setValueObject(valueObject);

    if(supplierPrefOrderId == null) supplierPrefOrderId = valueObject.getSupplierPrefOrderId();
    description = valueObject.getDescription();
  }


  /** Get a collection of  SupplierProduct related entities. */
  public Collection getSupplierProducts() { return SupplierProductHelper.findBySupplierPrefOrderId(supplierPrefOrderId); }
  /** Get the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public SupplierProduct getSupplierProduct(String productId, String partyId) { return SupplierProductHelper.findByPrimaryKey(productId, partyId); }
  /** Remove  SupplierProduct related entities. */
  public void removeSupplierProducts() { SupplierProductHelper.removeBySupplierPrefOrderId(supplierPrefOrderId); }
  /** Remove the  SupplierProduct keyed by member(s) of this class, and other passed parameters. */
  public void removeSupplierProduct(String productId, String partyId) { SupplierProductHelper.removeByPrimaryKey(productId, partyId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(supplierPrefOrder!=null) return supplierPrefOrder.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(supplierPrefOrder!=null) return supplierPrefOrder.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(supplierPrefOrder!=null) return supplierPrefOrder.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(supplierPrefOrder!=null) return supplierPrefOrder.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(supplierPrefOrder!=null) supplierPrefOrder.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
