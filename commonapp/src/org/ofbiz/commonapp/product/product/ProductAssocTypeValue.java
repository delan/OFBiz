
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Product Association Type Entity
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
public class ProductAssocTypeValue implements ProductAssocType
{
  /** The variable of the PRODUCT_ASSOC_TYPE_ID column of the PRODUCT_ASSOC_TYPE table. */
  private String productAssocTypeId;
  /** The variable of the PARENT_TYPE_ID column of the PRODUCT_ASSOC_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the PRODUCT_ASSOC_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the PRODUCT_ASSOC_TYPE table. */
  private String description;

  private ProductAssocType productAssocType;

  public ProductAssocTypeValue()
  {
    this.productAssocTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.productAssocType = null;
  }

  public ProductAssocTypeValue(ProductAssocType productAssocType) throws RemoteException
  {
    if(productAssocType == null) return;
  
    this.productAssocTypeId = productAssocType.getProductAssocTypeId();
    this.parentTypeId = productAssocType.getParentTypeId();
    this.hasTable = productAssocType.getHasTable();
    this.description = productAssocType.getDescription();

    this.productAssocType = productAssocType;
  }

  public ProductAssocTypeValue(ProductAssocType productAssocType, String productAssocTypeId, String parentTypeId, String hasTable, String description)
  {
    if(productAssocType == null) return;
  
    this.productAssocTypeId = productAssocTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.productAssocType = productAssocType;
  }


  /** Get the primary key of the PRODUCT_ASSOC_TYPE_ID column of the PRODUCT_ASSOC_TYPE table. */
  public String getProductAssocTypeId()  throws RemoteException { return productAssocTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the PRODUCT_ASSOC_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the PRODUCT_ASSOC_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(productAssocType!=null) productAssocType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the PRODUCT_ASSOC_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the PRODUCT_ASSOC_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(productAssocType!=null) productAssocType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the PRODUCT_ASSOC_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PRODUCT_ASSOC_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(productAssocType!=null) productAssocType.setDescription(description);
  }

  /** Get the value object of the ProductAssocType class. */
  public ProductAssocType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the ProductAssocType class. */
  public void setValueObject(ProductAssocType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(productAssocType!=null) productAssocType.setValueObject(valueObject);

    if(productAssocTypeId == null) productAssocTypeId = valueObject.getProductAssocTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent ProductAssocType entity corresponding to this entity. */
  public ProductAssocType getParentProductAssocType() { return ProductAssocTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent ProductAssocType entity corresponding to this entity. */
  public void removeParentProductAssocType() { ProductAssocTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child ProductAssocType related entities. */
  public Collection getChildProductAssocTypes() { return ProductAssocTypeHelper.findByParentTypeId(productAssocTypeId); }
  /** Get the Child ProductAssocType keyed by member(s) of this class, and other passed parameters. */
  public ProductAssocType getChildProductAssocType(String productAssocTypeId) { return ProductAssocTypeHelper.findByPrimaryKey(productAssocTypeId); }
  /** Remove Child ProductAssocType related entities. */
  public void removeChildProductAssocTypes() { ProductAssocTypeHelper.removeByParentTypeId(productAssocTypeId); }
  /** Remove the Child ProductAssocType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildProductAssocType(String productAssocTypeId) { ProductAssocTypeHelper.removeByPrimaryKey(productAssocTypeId); }

  /** Get a collection of  ProductAssoc related entities. */
  public Collection getProductAssocs() { return ProductAssocHelper.findByProductAssocTypeId(productAssocTypeId); }
  /** Get the  ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public ProductAssoc getProductAssoc(String productId, String productIdTo) { return ProductAssocHelper.findByPrimaryKey(productId, productIdTo, productAssocTypeId); }
  /** Remove  ProductAssoc related entities. */
  public void removeProductAssocs() { ProductAssocHelper.removeByProductAssocTypeId(productAssocTypeId); }
  /** Remove the  ProductAssoc keyed by member(s) of this class, and other passed parameters. */
  public void removeProductAssoc(String productId, String productIdTo) { ProductAssocHelper.removeByPrimaryKey(productId, productIdTo, productAssocTypeId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(productAssocType!=null) return productAssocType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(productAssocType!=null) return productAssocType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(productAssocType!=null) return productAssocType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(productAssocType!=null) return productAssocType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(productAssocType!=null) productAssocType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
