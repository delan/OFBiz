
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Feature Data Object Entity
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
 *@created    Fri Jul 27 01:18:28 MDT 2001
 *@version    1.0
 */
public class FeatureDataObjectValue implements FeatureDataObject
{
  /** The variable of the DATA_OBJECT_ID column of the FEATURE_DATA_OBJECT table. */
  private String dataObjectId;
  /** The variable of the PRODUCT_FEATURE_ID column of the FEATURE_DATA_OBJECT table. */
  private String productFeatureId;

  private FeatureDataObject featureDataObject;

  public FeatureDataObjectValue()
  {
    this.dataObjectId = null;
    this.productFeatureId = null;

    this.featureDataObject = null;
  }

  public FeatureDataObjectValue(FeatureDataObject featureDataObject) throws RemoteException
  {
    if(featureDataObject == null) return;
  
    this.dataObjectId = featureDataObject.getDataObjectId();
    this.productFeatureId = featureDataObject.getProductFeatureId();

    this.featureDataObject = featureDataObject;
  }

  public FeatureDataObjectValue(FeatureDataObject featureDataObject, String dataObjectId, String productFeatureId)
  {
    if(featureDataObject == null) return;
  
    this.dataObjectId = dataObjectId;
    this.productFeatureId = productFeatureId;

    this.featureDataObject = featureDataObject;
  }


  /** Get the primary key of the DATA_OBJECT_ID column of the FEATURE_DATA_OBJECT table. */
  public String getDataObjectId()  throws RemoteException { return dataObjectId; }

  /** Get the primary key of the PRODUCT_FEATURE_ID column of the FEATURE_DATA_OBJECT table. */
  public String getProductFeatureId()  throws RemoteException { return productFeatureId; }

  /** Get the value object of the FeatureDataObject class. */
  public FeatureDataObject getValueObject() throws RemoteException { return this; }
  /** Set the value object of the FeatureDataObject class. */
  public void setValueObject(FeatureDataObject valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(featureDataObject!=null) featureDataObject.setValueObject(valueObject);

    if(dataObjectId == null) dataObjectId = valueObject.getDataObjectId();
    if(productFeatureId == null) productFeatureId = valueObject.getProductFeatureId();
  }


  /** Get the  ProductFeature entity corresponding to this entity. */
  public ProductFeature getProductFeature() { return ProductFeatureHelper.findByPrimaryKey(productFeatureId); }
  /** Remove the  ProductFeature entity corresponding to this entity. */
  public void removeProductFeature() { ProductFeatureHelper.removeByPrimaryKey(productFeatureId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(featureDataObject!=null) return featureDataObject.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(featureDataObject!=null) return featureDataObject.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(featureDataObject!=null) return featureDataObject.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(featureDataObject!=null) return featureDataObject.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(featureDataObject!=null) featureDataObject.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
