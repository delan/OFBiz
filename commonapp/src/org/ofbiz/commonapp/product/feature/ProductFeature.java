
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;

import org.ofbiz.commonapp.product.cost.*;
import org.ofbiz.commonapp.product.price.*;

/**
 * <p><b>Title:</b> Product Feature Entity
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
 *@created    Fri Jul 27 01:18:27 MDT 2001
 *@version    1.0
 */

public interface ProductFeature extends EJBObject
{
  /** Get the primary key of the PRODUCT_FEATURE_ID column of the PRODUCT_FEATURE table. */
  public String getProductFeatureId() throws RemoteException;
  
  /** Get the value of the PRODUCT_FEATURE_TYPE_ID column of the PRODUCT_FEATURE table. */
  public String getProductFeatureTypeId() throws RemoteException;
  /** Set the value of the PRODUCT_FEATURE_TYPE_ID column of the PRODUCT_FEATURE table. */
  public void setProductFeatureTypeId(String productFeatureTypeId) throws RemoteException;
  
  /** Get the value of the PRODUCT_FEATURE_CATEGORY_ID column of the PRODUCT_FEATURE table. */
  public String getProductFeatureCategoryId() throws RemoteException;
  /** Set the value of the PRODUCT_FEATURE_CATEGORY_ID column of the PRODUCT_FEATURE table. */
  public void setProductFeatureCategoryId(String productFeatureCategoryId) throws RemoteException;
  
  /** Get the value of the DESCRIPTION column of the PRODUCT_FEATURE table. */
  public String getDescription() throws RemoteException;
  /** Set the value of the DESCRIPTION column of the PRODUCT_FEATURE table. */
  public void setDescription(String description) throws RemoteException;
  
  /** Get the value of the UOM_ID column of the PRODUCT_FEATURE table. */
  public String getUomId() throws RemoteException;
  /** Set the value of the UOM_ID column of the PRODUCT_FEATURE table. */
  public void setUomId(String uomId) throws RemoteException;
  
  /** Get the value of the NUMBER_SPECIFIED column of the PRODUCT_FEATURE table. */
  public Long getNumberSpecified() throws RemoteException;
  /** Set the value of the NUMBER_SPECIFIED column of the PRODUCT_FEATURE table. */
  public void setNumberSpecified(Long numberSpecified) throws RemoteException;
  

  /** Get the value object of this ProductFeature class. */
  public ProductFeature getValueObject() throws RemoteException;
  /** Set the values in the value object of this ProductFeature class. */
  public void setValueObject(ProductFeature productFeatureValue) throws RemoteException;


  /** Get the  ProductFeatureCategory entity corresponding to this entity. */
  public ProductFeatureCategory getProductFeatureCategory() throws RemoteException;
  /** Remove the  ProductFeatureCategory entity corresponding to this entity. */
  public void removeProductFeatureCategory() throws RemoteException;  

  /** Get the  ProductFeatureType entity corresponding to this entity. */
  public ProductFeatureType getProductFeatureType() throws RemoteException;
  /** Remove the  ProductFeatureType entity corresponding to this entity. */
  public void removeProductFeatureType() throws RemoteException;  

  /** Get a collection of  ProductFeatureAppl related entities. */
  public Collection getProductFeatureAppls() throws RemoteException;
  /** Get the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureAppl getProductFeatureAppl(String productId) throws RemoteException;
  /** Remove  ProductFeatureAppl related entities. */
  public void removeProductFeatureAppls() throws RemoteException;
  /** Remove the  ProductFeatureAppl keyed by member(s) of this class, and other passed parameters. */
  public void removeProductFeatureAppl(String productId) throws RemoteException;

  /** Get a collection of Main ProductFeatureIactn related entities. */
  public Collection getMainProductFeatureIactns() throws RemoteException;
  /** Get the Main ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureIactn getMainProductFeatureIactn(String productFeatureIdTo) throws RemoteException;
  /** Remove Main ProductFeatureIactn related entities. */
  public void removeMainProductFeatureIactns() throws RemoteException;
  /** Remove the Main ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public void removeMainProductFeatureIactn(String productFeatureIdTo) throws RemoteException;

  /** Get a collection of Assoc ProductFeatureIactn related entities. */
  public Collection getAssocProductFeatureIactns() throws RemoteException;
  /** Get the Assoc ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public ProductFeatureIactn getAssocProductFeatureIactn(String productFeatureId) throws RemoteException;
  /** Remove Assoc ProductFeatureIactn related entities. */
  public void removeAssocProductFeatureIactns() throws RemoteException;
  /** Remove the Assoc ProductFeatureIactn keyed by member(s) of this class, and other passed parameters. */
  public void removeAssocProductFeatureIactn(String productFeatureId) throws RemoteException;

  /** Get a collection of  FeatureDataObject related entities. */
  public Collection getFeatureDataObjects() throws RemoteException;
  /** Get the  FeatureDataObject keyed by member(s) of this class, and other passed parameters. */
  public FeatureDataObject getFeatureDataObject(String dataObjectId) throws RemoteException;
  /** Remove  FeatureDataObject related entities. */
  public void removeFeatureDataObjects() throws RemoteException;
  /** Remove the  FeatureDataObject keyed by member(s) of this class, and other passed parameters. */
  public void removeFeatureDataObject(String dataObjectId) throws RemoteException;

  /** Get a collection of  CostComponent related entities. */
  public Collection getCostComponents() throws RemoteException;
  /** Get the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public CostComponent getCostComponent(String costComponentId) throws RemoteException;
  /** Remove  CostComponent related entities. */
  public void removeCostComponents() throws RemoteException;
  /** Remove the  CostComponent keyed by member(s) of this class, and other passed parameters. */
  public void removeCostComponent(String costComponentId) throws RemoteException;

  /** Get a collection of  PriceComponent related entities. */
  public Collection getPriceComponents() throws RemoteException;
  /** Get the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public PriceComponent getPriceComponent(String priceComponentId) throws RemoteException;
  /** Remove  PriceComponent related entities. */
  public void removePriceComponents() throws RemoteException;
  /** Remove the  PriceComponent keyed by member(s) of this class, and other passed parameters. */
  public void removePriceComponent(String priceComponentId) throws RemoteException;

}
