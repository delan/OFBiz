
package org.ofbiz.commonapp.product.category;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;


/**
 * <p><b>Title:</b> Product Category Type Entity
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
public class ProductCategoryTypeBean implements EntityBean
{
  /** The variable for the PRODUCT_CATEGORY_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  public String productCategoryTypeId;
  /** The variable for the PARENT_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  public String parentTypeId;
  /** The variable for the HAS_TABLE column of the PRODUCT_CATEGORY_TYPE table. */
  public String hasTable;
  /** The variable for the DESCRIPTION column of the PRODUCT_CATEGORY_TYPE table. */
  public String description;

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the ProductCategoryTypeBean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }

  /** Get the primary key PRODUCT_CATEGORY_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  public String getProductCategoryTypeId() { return productCategoryTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  public String getParentTypeId() { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_CATEGORY_TYPE table. */
  public void setParentTypeId(String parentTypeId)
  {
    this.parentTypeId = parentTypeId;
    ejbIsModified = true;
  }

  /** Get the value of the HAS_TABLE column of the PRODUCT_CATEGORY_TYPE table. */
  public String getHasTable() { return hasTable; }
  /** Set the value of the HAS_TABLE column of the PRODUCT_CATEGORY_TYPE table. */
  public void setHasTable(String hasTable)
  {
    this.hasTable = hasTable;
    ejbIsModified = true;
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT_CATEGORY_TYPE table. */
  public String getDescription() { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_CATEGORY_TYPE table. */
  public void setDescription(String description)
  {
    this.description = description;
    ejbIsModified = true;
  }

  /** Sets the values from ValueObject attribute of the ProductCategoryTypeBean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(ProductCategoryType valueObject)
  {
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters
      if(valueObject.getParentTypeId() != null)
      {
        this.parentTypeId = valueObject.getParentTypeId();
        ejbIsModified = true;
      }
      if(valueObject.getHasTable() != null)
      {
        this.hasTable = valueObject.getHasTable();
        ejbIsModified = true;
      }
      if(valueObject.getDescription() != null)
      {
        this.description = valueObject.getDescription();
        ejbIsModified = true;
      }
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
  }

  /** Gets the ValueObject attribute of the ProductCategoryTypeBean object
   *@return    The ValueObject value
   */
  public ProductCategoryType getValueObject()
  {
    if(this.entityContext != null)
    {
      return new ProductCategoryTypeValue((ProductCategoryType)this.entityContext.getEJBObject(), productCategoryTypeId, parentTypeId, hasTable, description);
    }
    else { return null; }
  }


  /** Get the Parent ProductCategoryType entity corresponding to this entity. */
  public ProductCategoryType getParentProductCategoryType() { return ProductCategoryTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent ProductCategoryType entity corresponding to this entity. */
  public void removeParentProductCategoryType() { ProductCategoryTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child ProductCategoryType related entities. */
  public Collection getChildProductCategoryTypes() { return ProductCategoryTypeHelper.findByParentTypeId(productCategoryTypeId); }
  /** Get the Child ProductCategoryType keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryType getChildProductCategoryType(String productCategoryTypeId) { return ProductCategoryTypeHelper.findByPrimaryKey(productCategoryTypeId); }
  /** Remove Child ProductCategoryType related entities. */
  public void removeChildProductCategoryTypes() { ProductCategoryTypeHelper.removeByParentTypeId(productCategoryTypeId); }
  /** Remove the Child ProductCategoryType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductCategoryType(String productCategoryTypeId) { ProductCategoryTypeHelper.removeByPrimaryKey(productCategoryTypeId); }

  /** Get a collection of  ProductCategoryTypeAttr related entities. */
  public Collection getProductCategoryTypeAttrs() { return ProductCategoryTypeAttrHelper.findByProductCategoryTypeId(productCategoryTypeId); }
  /** Get the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryTypeAttr getProductCategoryTypeAttr(String name) { return ProductCategoryTypeAttrHelper.findByPrimaryKey(productCategoryTypeId, name); }
  /** Remove  ProductCategoryTypeAttr related entities. */
  public void removeProductCategoryTypeAttrs() { ProductCategoryTypeAttrHelper.removeByProductCategoryTypeId(productCategoryTypeId); }
  /** Remove the  ProductCategoryTypeAttr keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryTypeAttr(String name) { ProductCategoryTypeAttrHelper.removeByPrimaryKey(productCategoryTypeId, name); }

  /** Get a collection of  ProductCategoryClass related entities. */
  public Collection getProductCategoryClasss() { return ProductCategoryClassHelper.findByProductCategoryTypeId(productCategoryTypeId); }
  /** Get the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public ProductCategoryClass getProductCategoryClass(String productCategoryId) { return ProductCategoryClassHelper.findByPrimaryKey(productCategoryId, productCategoryTypeId); }
  /** Remove  ProductCategoryClass related entities. */
  public void removeProductCategoryClasss() { ProductCategoryClassHelper.removeByProductCategoryTypeId(productCategoryTypeId); }
  /** Remove the  ProductCategoryClass keyed by member(s) of this class, and other passed parameters. */
  public void removeProductCategoryClass(String productCategoryId) { ProductCategoryClassHelper.removeByPrimaryKey(productCategoryId, productCategoryTypeId); }


  /** Description of the Method
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String productCategoryTypeId, String parentTypeId, String hasTable, String description) throws CreateException
  {
    this.productCategoryTypeId = productCategoryTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;
    return null;
  }

  /** Description of the Method
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public java.lang.String ejbCreate(String productCategoryTypeId) throws CreateException
  {
    return ejbCreate(productCategoryTypeId, null, null, null);
  }

  /** Description of the Method
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productCategoryTypeId, String parentTypeId, String hasTable, String description) throws CreateException {}

  /** Description of the Method
   *@param  productCategoryTypeId                  Field of the PRODUCT_CATEGORY_TYPE_ID column.
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(String productCategoryTypeId) throws CreateException
  {
    ejbPostCreate(productCategoryTypeId, null, null, null);
  }

  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
