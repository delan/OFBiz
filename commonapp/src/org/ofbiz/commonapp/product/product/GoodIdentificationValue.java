
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Good Identification Entity
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
 *@created    Fri Jul 27 01:18:25 MDT 2001
 *@version    1.0
 */
public class GoodIdentificationValue implements GoodIdentification
{
  /** The variable of the GOOD_IDENTIFICATION_TYPE_ID column of the GOOD_IDENTIFICATION table. */
  private String goodIdentificationTypeId;
  /** The variable of the PRODUCT_ID column of the GOOD_IDENTIFICATION table. */
  private String productId;
  /** The variable of the ID_VALUE column of the GOOD_IDENTIFICATION table. */
  private String idValue;

  private GoodIdentification goodIdentification;

  public GoodIdentificationValue()
  {
    this.goodIdentificationTypeId = null;
    this.productId = null;
    this.idValue = null;

    this.goodIdentification = null;
  }

  public GoodIdentificationValue(GoodIdentification goodIdentification) throws RemoteException
  {
    if(goodIdentification == null) return;
  
    this.goodIdentificationTypeId = goodIdentification.getGoodIdentificationTypeId();
    this.productId = goodIdentification.getProductId();
    this.idValue = goodIdentification.getIdValue();

    this.goodIdentification = goodIdentification;
  }

  public GoodIdentificationValue(GoodIdentification goodIdentification, String goodIdentificationTypeId, String productId, String idValue)
  {
    if(goodIdentification == null) return;
  
    this.goodIdentificationTypeId = goodIdentificationTypeId;
    this.productId = productId;
    this.idValue = idValue;

    this.goodIdentification = goodIdentification;
  }


  /** Get the primary key of the GOOD_IDENTIFICATION_TYPE_ID column of the GOOD_IDENTIFICATION table. */
  public String getGoodIdentificationTypeId()  throws RemoteException { return goodIdentificationTypeId; }

  /** Get the primary key of the PRODUCT_ID column of the GOOD_IDENTIFICATION table. */
  public String getProductId()  throws RemoteException { return productId; }

  /** Get the value of the ID_VALUE column of the GOOD_IDENTIFICATION table. */
  public String getIdValue() throws RemoteException { return idValue; }
  /** Set the value of the ID_VALUE column of the GOOD_IDENTIFICATION table. */
  public void setIdValue(String idValue) throws RemoteException
  {
    this.idValue = idValue;
    if(goodIdentification!=null) goodIdentification.setIdValue(idValue);
  }

  /** Get the value object of the GoodIdentification class. */
  public GoodIdentification getValueObject() throws RemoteException { return this; }
  /** Set the value object of the GoodIdentification class. */
  public void setValueObject(GoodIdentification valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(goodIdentification!=null) goodIdentification.setValueObject(valueObject);

    if(goodIdentificationTypeId == null) goodIdentificationTypeId = valueObject.getGoodIdentificationTypeId();
    if(productId == null) productId = valueObject.getProductId();
    idValue = valueObject.getIdValue();
  }


  /** Get the  GoodIdentificationType entity corresponding to this entity. */
  public GoodIdentificationType getGoodIdentificationType() { return GoodIdentificationTypeHelper.findByPrimaryKey(goodIdentificationTypeId); }
  /** Remove the  GoodIdentificationType entity corresponding to this entity. */
  public void removeGoodIdentificationType() { GoodIdentificationTypeHelper.removeByPrimaryKey(goodIdentificationTypeId); }

  /** Get the  Product entity corresponding to this entity. */
  public Product getProduct() { return ProductHelper.findByPrimaryKey(productId); }
  /** Remove the  Product entity corresponding to this entity. */
  public void removeProduct() { ProductHelper.removeByPrimaryKey(productId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(goodIdentification!=null) return goodIdentification.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(goodIdentification!=null) return goodIdentification.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(goodIdentification!=null) return goodIdentification.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(goodIdentification!=null) return goodIdentification.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(goodIdentification!=null) goodIdentification.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
