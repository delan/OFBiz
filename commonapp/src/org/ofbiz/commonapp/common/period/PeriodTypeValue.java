
package org.ofbiz.commonapp.common.period;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Period Type Entity
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
 *@created    Fri Jul 27 01:18:22 MDT 2001
 *@version    1.0
 */
public class PeriodTypeValue implements PeriodType
{
  /** The variable of the PERIOD_TYPE_ID column of the PERIOD_TYPE table. */
  private String periodTypeId;
  /** The variable of the DESCRIPTION column of the PERIOD_TYPE table. */
  private String description;

  private PeriodType periodType;

  public PeriodTypeValue()
  {
    this.periodTypeId = null;
    this.description = null;

    this.periodType = null;
  }

  public PeriodTypeValue(PeriodType periodType) throws RemoteException
  {
    if(periodType == null) return;
  
    this.periodTypeId = periodType.getPeriodTypeId();
    this.description = periodType.getDescription();

    this.periodType = periodType;
  }

  public PeriodTypeValue(PeriodType periodType, String periodTypeId, String description)
  {
    if(periodType == null) return;
  
    this.periodTypeId = periodTypeId;
    this.description = description;

    this.periodType = periodType;
  }


  /** Get the primary key of the PERIOD_TYPE_ID column of the PERIOD_TYPE table. */
  public String getPeriodTypeId()  throws RemoteException { return periodTypeId; }

  /** Get the value of the DESCRIPTION column of the PERIOD_TYPE table. */
  public String getDescription() throws RemoteException { return description; }
  /** Set the value of the DESCRIPTION column of the PERIOD_TYPE table. */
  public void setDescription(String description) throws RemoteException
  {
    this.description = description;
    if(periodType!=null) periodType.setDescription(description);
  }

  /** Get the value object of the PeriodType class. */
  public PeriodType getValueObject() throws RemoteException { return this; }
  /** Set the value object of the PeriodType class. */
  public void setValueObject(PeriodType valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(periodType!=null) periodType.setValueObject(valueObject);

    if(periodTypeId == null) periodTypeId = valueObject.getPeriodTypeId();
    description = valueObject.getDescription();
  }



  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(periodType!=null) return periodType.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(periodType!=null) return periodType.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(periodType!=null) return periodType.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(periodType!=null) return periodType.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(periodType!=null) periodType.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
