
package org.ofbiz.commonapp.common.period;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Standard Time Period Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the StandardTimePeriod Entity EJB; acts as a proxy for the Home interface
 *
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
public class StandardTimePeriodHelper
{

  /** A static variable to cache the Home object for the StandardTimePeriod EJB */
  private static StandardTimePeriodHome standardTimePeriodHome = null;

  /** Initializes the standardTimePeriodHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The StandardTimePeriodHome instance for the default EJB server
   */
  public static StandardTimePeriodHome getStandardTimePeriodHome()
  {
    if(standardTimePeriodHome == null) //don't want to block here
    {
      synchronized(StandardTimePeriodHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(standardTimePeriodHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.common.period.StandardTimePeriodHome");
            standardTimePeriodHome = (StandardTimePeriodHome)MyNarrow.narrow(homeObject, StandardTimePeriodHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("standardTimePeriod home obtained " + standardTimePeriodHome);
        }
      }
    }
    return standardTimePeriodHome;
  }




  /** Remove the StandardTimePeriod corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    StandardTimePeriod standardTimePeriod = findByPrimaryKey(primaryKey);
    try
    {
      if(standardTimePeriod != null)
      {
        standardTimePeriod.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a StandardTimePeriod by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The StandardTimePeriod corresponding to the primaryKey
   */
  public static StandardTimePeriod findByPrimaryKey(java.lang.String primaryKey)
  {
    StandardTimePeriod standardTimePeriod = null;
    Debug.logInfo("StandardTimePeriodHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      standardTimePeriod = (StandardTimePeriod)MyNarrow.narrow(getStandardTimePeriodHome().findByPrimaryKey(primaryKey), StandardTimePeriod.class);
      if(standardTimePeriod != null)
      {
        standardTimePeriod = standardTimePeriod.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return standardTimePeriod;
  }

  /** Finds all StandardTimePeriod entities
   *@return    Collection containing all StandardTimePeriod entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("StandardTimePeriodHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getStandardTimePeriodHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a StandardTimePeriod
   *@param  standardTimePeriodId                  Field of the STANDARD_TIME_PERIOD_ID column.
   *@param  periodTypeId                  Field of the PERIOD_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static StandardTimePeriod create(String standardTimePeriodId, String periodTypeId, java.util.Date fromDate, java.util.Date thruDate)
  {
    StandardTimePeriod standardTimePeriod = null;
    Debug.logInfo("StandardTimePeriodHelper.create: standardTimePeriodId: " + standardTimePeriodId);
    if(standardTimePeriodId == null) { return null; }

    try { standardTimePeriod = (StandardTimePeriod)MyNarrow.narrow(getStandardTimePeriodHome().create(standardTimePeriodId, periodTypeId, fromDate, thruDate), StandardTimePeriod.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create standardTimePeriod with standardTimePeriodId: " + standardTimePeriodId);
      Debug.logError(ce);
      standardTimePeriod = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return standardTimePeriod;
  }

  /** Updates the corresponding StandardTimePeriod
   *@param  standardTimePeriodId                  Field of the STANDARD_TIME_PERIOD_ID column.
   *@param  periodTypeId                  Field of the PERIOD_TYPE_ID column.
   *@param  fromDate                  Field of the FROM_DATE column.
   *@param  thruDate                  Field of the THRU_DATE column.
   *@return                Description of the Returned Value
   */
  public static StandardTimePeriod update(String standardTimePeriodId, String periodTypeId, java.util.Date fromDate, java.util.Date thruDate) throws java.rmi.RemoteException
  {
    if(standardTimePeriodId == null) { return null; }
    StandardTimePeriod standardTimePeriod = findByPrimaryKey(standardTimePeriodId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    StandardTimePeriod standardTimePeriodValue = new StandardTimePeriodValue();

    if(periodTypeId != null) { standardTimePeriodValue.setPeriodTypeId(periodTypeId); }
    if(fromDate != null) { standardTimePeriodValue.setFromDate(fromDate); }
    if(thruDate != null) { standardTimePeriodValue.setThruDate(thruDate); }

    standardTimePeriod.setValueObject(standardTimePeriodValue);
    return standardTimePeriod;
  }

  /** Removes/deletes the specified  StandardTimePeriod
   *@param  periodTypeId                  Field of the PERIOD_TYPE_ID column.
   */
  public static void removeByPeriodTypeId(String periodTypeId)
  {
    if(periodTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByPeriodTypeId(periodTypeId));

    while(iterator.hasNext())
    {
      try
      {
        StandardTimePeriod standardTimePeriod = (StandardTimePeriod) iterator.next();
        Debug.logInfo("Removing standardTimePeriod with periodTypeId:" + periodTypeId);
        standardTimePeriod.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds StandardTimePeriod records by the following parameters:
   *@param  periodTypeId                  Field of the PERIOD_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByPeriodTypeId(String periodTypeId)
  {
    Debug.logInfo("findByPeriodTypeId: periodTypeId:" + periodTypeId);

    Collection collection = null;
    if(periodTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getStandardTimePeriodHome().findByPeriodTypeId(periodTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
