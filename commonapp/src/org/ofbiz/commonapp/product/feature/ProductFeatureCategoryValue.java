
package org.ofbiz.commonapp.product.feature;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Product Feature Category Entity
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
public class ProductFeatureCategoryValue implements ProductFeatureCategory
{
  /** The variable of the PRODUCT_FEATURE_CATEGORY_ID column of the PRODUCT_FEATURE_CATEGORY table. */
  private String productFeatureCategoryId;
  /** The variable of the PARENT_CATEGORY_ID column of the PRODUCT_FEATURE_CATEGORY table. */
  private String parentCategoryId;
  /** The variable of the DESCRIPTION column of the PRODUCT_FEATURE_CATEGORY table. */
  private String description;

  private ProductFeatureCategory productFeatureCategory;

  public ProductFeatureCategoryValue()
  {
    this.productFeatureCategoryId = null;
    this.parentCategoryId = null;
    this.description = null;

    this.productFeatureCategory = null;
  }

  public ProductFeatureCategoryValue(ProductFeatureCategory productFeatureCategory) throws RemoteException
  {
    if(productFeatureCategory == null) return;
  
    this.productFeatureCategoryId = productFeatureCategory.getProductFeatureCategoryId();
    this.parentCategoryId = productFeatureCategory.getParentCategoryId();
    this.description = productFeatureCategory.getDescription();

    this.productFeatureCategory = productFeatureCategory;
  }

  public ProductFeatureCategoryValue(ProductFeatureCategory productFeatureCategory, String productFeatureCategoryId, String parentCategoryId, String description)
  {
    if(productFeatureCategory == null) return;
  
    this.productFeatureCategoryId = productFeatureCategoryId;
    this.parentCategoryId = parentCategoryId;
    this.description = description;

    this.productFeatureCategory = productFeatureCategory;
  }


  /** Get the primary key of the PRODUCT_FEATURE_CATEGORY_ID column of the PRODUCT_FEATURE_CATEGORY table. */
  public String getProductFeatureCategoryId()  throws RemoteException { return productFeatureCategoryId; }

  /** Get the value of the PARENT_CATEGORY_ID column of the PRODUCT_FEATURE_CATEGORY table. */
  public String getParentCategoryId() throws RemoteException { return parentCategoryId; }
  /** Set the value of the PARENT_CATEGORY_ID column of the PRODUCT_FEATURE_CATEGORY table. */
  public void setParentCategoryId(String parentCategoryId) throws RemoteException
  {
    this.parentCategoryId = parentCategoryId;
    if(productFeatureCategory!=null) productFeatureCategory.setParentCategoryId(parentCategoryId);
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT_FEATURE_CATEGORY table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_FEATURE_CATEGORY table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(productFeatureCategory!=null) productFeatureCategory.setDescription(description);
  }

  /** Get the value object of the ProductFeatureCategory class. */
  public ProductFeatureCategory getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductFeatureCategory class. */
  public void setValueObject(ProductFeatureCategory valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productFeatureCategory!=null) productFeatureCategory.setValueObject(valueObject);

    if(productFeatureCategoryId == null) productFeatureCategoryId = valueObject.getProductFeatureCategoryId();
    parentCategoryId = valueObject.getParentCategoryId();
    description = valueObject.getDescription();
  }


  /** Get the  ProductFeatureCategory entity corresponding to this entity. */
  public ProductFeatureCategory getProductFeatureCategory() { return ProductFeatureCategoryHelper.findByPrimaryKey(parentCategoryId); }
  /** Remove the  ProductFeatureCategory entity corresponding to this entity. */
  public void removeProductFeatureCategory() { ProductFeatureCategoryHelper.removeByPrimaryKey(parentCategoryId); }

  /** Get a collection of  ProductFeature related entities. */
  public Collection getProductFeatures() { return ProductFeatureHelper.findByProductFeatureCategoryId(productFeatureCategoryId); }
  /** Get the  ProductFeature keyed by member(s) of this class, and other passed parameters. */
  public ProductFeature getProductFeature(String productFeatureId) { return ProductFeatureHelper.findByPrimaryKey(productFeatureId); }
  /** Remove  ProductFeature related entities. */
  public void removeProductFeatures() { ProductFeatureHelper.removeByProductFeatureCategoryId(productFeatureCategoryId); }
  /** Remove the  ProductFeature keyed by member(s) of this class, and other passed parameters. */
  public void removeProductFeature(String productFeatureId) { ProductFeatureHelper.removeByPrimaryKey(productFeatureId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productFeatureCategory!=null) return productFeatureCategory.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productFeatureCategory!=null) return productFeatureCategory.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productFeatureCategory!=null) return productFeatureCategory.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productFeatureCategory!=null) return productFeatureCategory.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productFeatureCategory!=null) productFeatureCategory.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
