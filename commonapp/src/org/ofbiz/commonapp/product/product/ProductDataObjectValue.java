
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Product Data Object Entity
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
 *@created    Fri Jul 27 01:18:26 MDT 2001
 *@version    1.0
 */
public class ProductDataObjectValue implements ProductDataObject
{
  /** The variable of the DATA_OBJECT_ID column of the PRODUCT_DATA_OBJECT table. */
  private String dataObjectId;
  /** The variable of the PRODUCT_ID column of the PRODUCT_DATA_OBJECT table. */
  private String productId;

  private ProductDataObject productDataObject;

  public ProductDataObjectValue()
  {
    this.dataObjectId = null;
    this.productId = null;

    this.productDataObject = null;
  }

  public ProductDataObjectValue(ProductDataObject productDataObject) throws RemoteException
  {
    if(productDataObject == null) return;
  
    this.dataObjectId = productDataObject.getDataObjectId();
    this.productId = productDataObject.getProductId();

    this.productDataObject = productDataObject;
  }

  public ProductDataObjectValue(ProductDataObject productDataObject, String dataObjectId, String productId)
  {
    if(productDataObject == null) return;
  
    this.dataObjectId = dataObjectId;
    this.productId = productId;

    this.productDataObject = productDataObject;
  }


  /** Get the primary key of the DATA_OBJECT_ID column of the PRODUCT_DATA_OBJECT table. */
  public String getDataObjectId()  throws RemoteException { return dataObjectId; }

  /** Get the primary key of the PRODUCT_ID column of the PRODUCT_DATA_OBJECT table. */
  public String getProductId()  throws RemoteException { return productId; }

  /** Get the value object of the ProductDataObject class. */
  public ProductDataObject getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductDataObject class. */
  public void setValueObject(ProductDataObject valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productDataObject!=null) productDataObject.setValueObject(valueObject);

    if(dataObjectId == null) dataObjectId = valueObject.getDataObjectId();
    if(productId == null) productId = valueObject.getProductId();
  }


  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productDataObject!=null) return productDataObject.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productDataObject!=null) return productDataObject.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productDataObject!=null) return productDataObject.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productDataObject!=null) return productDataObject.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productDataObject!=null) productDataObject.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
