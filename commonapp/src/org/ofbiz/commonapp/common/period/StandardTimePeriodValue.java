
package org.ofbiz.commonapp.common.period;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
import org.ofbiz.core.util.*;


/**
 * <p><b>Title:</b> Standard Time Period Entity
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
public class StandardTimePeriodValue implements StandardTimePeriod
{
  /** The variable of the STANDARD_TIME_PERIOD_ID column of the STANDARD_TIME_PERIOD table. */
  private String standardTimePeriodId;
  /** The variable of the PERIOD_TYPE_ID column of the STANDARD_TIME_PERIOD table. */
  private String periodTypeId;
  /** The variable of the FROM_DATE column of the STANDARD_TIME_PERIOD table. */
  private java.util.Date fromDate;
  /** The variable of the THRU_DATE column of the STANDARD_TIME_PERIOD table. */
  private java.util.Date thruDate;

  private StandardTimePeriod standardTimePeriod;

  public StandardTimePeriodValue()
  {
    this.standardTimePeriodId = null;
    this.periodTypeId = null;
    this.fromDate = null;
    this.thruDate = null;

    this.standardTimePeriod = null;
  }

  public StandardTimePeriodValue(StandardTimePeriod standardTimePeriod) throws RemoteException
  {
    if(standardTimePeriod == null) return;
  
    this.standardTimePeriodId = standardTimePeriod.getStandardTimePeriodId();
    this.periodTypeId = standardTimePeriod.getPeriodTypeId();
    this.fromDate = standardTimePeriod.getFromDate();
    this.thruDate = standardTimePeriod.getThruDate();

    this.standardTimePeriod = standardTimePeriod;
  }

  public StandardTimePeriodValue(StandardTimePeriod standardTimePeriod, String standardTimePeriodId, String periodTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    if(standardTimePeriod == null) return;
  
    this.standardTimePeriodId = standardTimePeriodId;
    this.periodTypeId = periodTypeId;
    this.fromDate = fromDate;
    this.thruDate = thruDate;

    this.standardTimePeriod = standardTimePeriod;
  }


  /** Get the primary key of the STANDARD_TIME_PERIOD_ID column of the STANDARD_TIME_PERIOD table. */
  public String getStandardTimePeriodId()  throws RemoteException { return standardTimePeriodId; }

  /** Get the value of the PERIOD_TYPE_ID column of the STANDARD_TIME_PERIOD table. */
  public String getPeriodTypeId() throws RemoteException { return periodTypeId; }
  /** Set the value of the PERIOD_TYPE_ID column of the STANDARD_TIME_PERIOD table. */
  public void setPeriodTypeId(String periodTypeId) throws RemoteException
  {
    this.periodTypeId = periodTypeId;
    if(standardTimePeriod!=null) standardTimePeriod.setPeriodTypeId(periodTypeId);
  }

  /** Get the value of the FROM_DATE column of the STANDARD_TIME_PERIOD table. */
  public java.util.Date getFromDate() throws RemoteException { return fromDate; }
  /** Set the value of the FROM_DATE column of the STANDARD_TIME_PERIOD table. */
  public void setFromDate(java.util.Date fromDate) throws RemoteException
  {
    this.fromDate = fromDate;
    if(standardTimePeriod!=null) standardTimePeriod.setFromDate(fromDate);
  }

  /** Get the value of the THRU_DATE column of the STANDARD_TIME_PERIOD table. */
  public java.util.Date getThruDate() throws RemoteException { return thruDate; }
  /** Set the value of the THRU_DATE column of the STANDARD_TIME_PERIOD table. */
  public void setThruDate(java.util.Date thruDate) throws RemoteException
  {
    this.thruDate = thruDate;
    if(standardTimePeriod!=null) standardTimePeriod.setThruDate(thruDate);
  }

  /** Get the value object of the StandardTimePeriod class. */
  public StandardTimePeriod getValueObject() throws RemoteException { return this; }
  /** Set the value object of the StandardTimePeriod class. */
  public void setValueObject(StandardTimePeriod valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(standardTimePeriod!=null) standardTimePeriod.setValueObject(valueObject);

    if(standardTimePeriodId == null) standardTimePeriodId = valueObject.getStandardTimePeriodId();
    periodTypeId = valueObject.getPeriodTypeId();
    fromDate = valueObject.getFromDate();
    thruDate = valueObject.getThruDate();
  }



  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(standardTimePeriod!=null) return standardTimePeriod.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(standardTimePeriod!=null) return standardTimePeriod.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(standardTimePeriod!=null) return standardTimePeriod.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(standardTimePeriod!=null) return standardTimePeriod.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(standardTimePeriod!=null) standardTimePeriod.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
