
package org.ofbiz.commonapp.product.inventory;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Variance Reason Entity
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
 *@created    Fri Jul 27 01:18:31 MDT 2001
 *@version    1.0
 */
public class VarianceReasonValue implements VarianceReason
{
  /** The variable of the VARIANCE_REASON_ID column of the VARIANCE_REASON table. */
  private String varianceReasonId;
  /** The variable of the DESCRIPTION column of the VARIANCE_REASON table. */
  private String description;

  private VarianceReason varianceReason;

  public VarianceReasonValue()
  {
    this.varianceReasonId = null;
    this.description = null;

    this.varianceReason = null;
  }

  public VarianceReasonValue(VarianceReason varianceReason) throws RemoteException
  {
    if(varianceReason == null) return;
  
    this.varianceReasonId = varianceReason.getVarianceReasonId();
    this.description = varianceReason.getDescription();

    this.varianceReason = varianceReason;
  }

  public VarianceReasonValue(VarianceReason varianceReason, String varianceReasonId, String description)
  {
    if(varianceReason == null) return;
  
    this.varianceReasonId = varianceReasonId;
    this.description = description;

    this.varianceReason = varianceReason;
  }


  /** Get the primary key of the VARIANCE_REASON_ID column of the VARIANCE_REASON table. */
  public String getVarianceReasonId()  throws RemoteException { return varianceReasonId; }

  /** Get the value of the DESCRIPTION column of the VARIANCE_REASON table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the VARIANCE_REASON table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(varianceReason!=null) varianceReason.setDescription(description);
  }

  /** Get the value object of the VarianceReason class. */
  public VarianceReason getValueObject() throws RemoteException { return this; }
  /** Set the value object of the VarianceReason class. */
  public void setValueObject(VarianceReason valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(varianceReason!=null) varianceReason.setValueObject(valueObject);

    if(varianceReasonId == null) varianceReasonId = valueObject.getVarianceReasonId();
    description = valueObject.getDescription();
  }


  /** Get a collection of  InventoryItemVariance related entities. */
  public Collection getInventoryItemVariances() { return InventoryItemVarianceHelper.findByVarianceReasonId(varianceReasonId); }
  /** Get the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public InventoryItemVariance getInventoryItemVariance(String inventoryItemId, String physicalInventoryId) { return InventoryItemVarianceHelper.findByPrimaryKey(inventoryItemId, physicalInventoryId); }
  /** Remove  InventoryItemVariance related entities. */
  public void removeInventoryItemVariances() { InventoryItemVarianceHelper.removeByVarianceReasonId(varianceReasonId); }
  /** Remove the  InventoryItemVariance keyed by member(s) of this class, and other passed parameters. */
  public void removeInventoryItemVariance(String inventoryItemId, String physicalInventoryId) { InventoryItemVarianceHelper.removeByPrimaryKey(inventoryItemId, physicalInventoryId); }


  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(varianceReason!=null) return varianceReason.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(varianceReason!=null) return varianceReason.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(varianceReason!=null) return varianceReason.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(varianceReason!=null) return varianceReason.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(varianceReason!=null) varianceReason.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
