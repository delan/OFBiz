
package org.ofbiz.commonapp.product.product;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Good Identification Type Entity
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
public class GoodIdentificationTypeValue implements GoodIdentificationType
{
  /** The variable of the GOOD_IDENTIFICATION_TYPE_ID column of the GOOD_IDENTIFICATION_TYPE table. */
  private String goodIdentificationTypeId;
  /** The variable of the PARENT_TYPE_ID column of the GOOD_IDENTIFICATION_TYPE table. */
  private String parentTypeId;
  /** The variable of the HAS_TABLE column of the GOOD_IDENTIFICATION_TYPE table. */
  private String hasTable;
  /** The variable of the DESCRIPTION column of the GOOD_IDENTIFICATION_TYPE table. */
  private String description;

  private GoodIdentificationType goodIdentificationType;

  public GoodIdentificationTypeValue()
  {
    this.goodIdentificationTypeId = null;
    this.parentTypeId = null;
    this.hasTable = null;
    this.description = null;

    this.goodIdentificationType = null;
  }

  public GoodIdentificationTypeValue(GoodIdentificationType goodIdentificationType) throws RemoteException
  {
    if(goodIdentificationType == null) return;
  
    this.goodIdentificationTypeId = goodIdentificationType.getGoodIdentificationTypeId();
    this.parentTypeId = goodIdentificationType.getParentTypeId();
    this.hasTable = goodIdentificationType.getHasTable();
    this.description = goodIdentificationType.getDescription();

    this.goodIdentificationType = goodIdentificationType;
  }

  public GoodIdentificationTypeValue(GoodIdentificationType goodIdentificationType, String goodIdentificationTypeId, String parentTypeId, String hasTable, String description)
  {
    if(goodIdentificationType == null) return;
  
    this.goodIdentificationTypeId = goodIdentificationTypeId;
    this.parentTypeId = parentTypeId;
    this.hasTable = hasTable;
    this.description = description;

    this.goodIdentificationType = goodIdentificationType;
  }


  /** Get the primary key of the GOOD_IDENTIFICATION_TYPE_ID column of the GOOD_IDENTIFICATION_TYPE table. */
  public String getGoodIdentificationTypeId()  throws RemoteException { return goodIdentificationTypeId; }

  /** Get the value of the PARENT_TYPE_ID column of the GOOD_IDENTIFICATION_TYPE table. */
  public String getParentTypeId() throws RemoteException { return parentTypeId; }
  /** Set the value of the PARENT_TYPE_ID column of the GOOD_IDENTIFICATION_TYPE table. */
  public void setParentTypeId(String parentTypeId) throws RemoteException
  {
    this.parentTypeId = parentTypeId;
    if(goodIdentificationType!=null) goodIdentificationType.setParentTypeId(parentTypeId);
  }

  /** Get the value of the HAS_TABLE column of the GOOD_IDENTIFICATION_TYPE table. */
  public String getHasTable() throws RemoteException { return hasTable; }
  /** Set the value of the HAS_TABLE column of the GOOD_IDENTIFICATION_TYPE table. */
  public void setHasTable(String hasTable) throws RemoteException
  {
    this.hasTable = hasTable;
    if(goodIdentificationType!=null) goodIdentificationType.setHasTable(hasTable);
  }

  /** Get the value of the DESCRIPTION column of the GOOD_IDENTIFICATION_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the GOOD_IDENTIFICATION_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(goodIdentificationType!=null) goodIdentificationType.setDescription(description);
  }

  /** Get the value object of the GoodIdentificationType class. */
  public GoodIdentificationType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the GoodIdentificationType class. */
  public void setValueObject(GoodIdentificationType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(goodIdentificationType!=null) goodIdentificationType.setValueObject(valueObject);

    if(goodIdentificationTypeId == null) goodIdentificationTypeId = valueObject.getGoodIdentificationTypeId();
    parentTypeId = valueObject.getParentTypeId();
    hasTable = valueObject.getHasTable();
    description = valueObject.getDescription();
  }


  /** Get the Parent GoodIdentificationType entity corresponding to this entity. */
  public GoodIdentificationType getParentGoodIdentificationType() { return GoodIdentificationTypeHelper.findByPrimaryKey(parentTypeId); }
  /** Remove the Parent GoodIdentificationType entity corresponding to this entity. */
  public void removeParentGoodIdentificationType() { GoodIdentificationTypeHelper.removeByPrimaryKey(parentTypeId); }

  /** Get a collection of Child GoodIdentificationType related entities. */
  public Collection getChildGoodIdentificationTypes() { return GoodIdentificationTypeHelper.findByParentTypeId(goodIdentificationTypeId); }
  /** Get the Child GoodIdentificationType keyed by member(s) of this class, and other passed parameters. */
  public GoodIdentificationType getChildGoodIdentificationType(String goodIdentificationTypeId) { return GoodIdentificationTypeHelper.findByPrimaryKey(goodIdentificationTypeId); }
  /** Remove Child GoodIdentificationType related entities. */
  public void removeChildGoodIdentificationTypes() { GoodIdentificationTypeHelper.removeByParentTypeId(goodIdentificationTypeId); }
  /** Remove the Child GoodIdentificationType keyed by member(s) of this class, and other passed parameters. */
  public void removeChildGoodIdentificationType(String goodIdentificationTypeId) { GoodIdentificationTypeHelper.removeByPrimaryKey(goodIdentificationTypeId); }

  /** Get a collection of  GoodIdentification related entities. */
  public Collection getGoodIdentifications() { return GoodIdentificationHelper.findByGoodIdentificationTypeId(goodIdentificationTypeId); }
  /** Get the  GoodIdentification keyed by member(s) of this class, and other passed parameters. */
  public GoodIdentification getGoodIdentification(String productId) { return GoodIdentificationHelper.findByPrimaryKey(goodIdentificationTypeId, productId); }
  /** Remove  GoodIdentification related entities. */
  public void removeGoodIdentifications() { GoodIdentificationHelper.removeByGoodIdentificationTypeId(goodIdentificationTypeId); }
  /** Remove the  GoodIdentification keyed by member(s) of this class, and other passed parameters. */
  public void removeGoodIdentification(String productId) { GoodIdentificationHelper.removeByPrimaryKey(goodIdentificationTypeId, productId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(goodIdentificationType!=null) return goodIdentificationType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(goodIdentificationType!=null) return goodIdentificationType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(goodIdentificationType!=null) return goodIdentificationType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(goodIdentificationType!=null) return goodIdentificationType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(goodIdentificationType!=null) goodIdentificationType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
