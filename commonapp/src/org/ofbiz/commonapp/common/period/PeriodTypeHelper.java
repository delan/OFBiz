
package org.ofbiz.commonapp.common.period;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Period Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the PeriodType Entity EJB; acts as a proxy for the Home interface
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
public class PeriodTypeHelper
{

  /** A static variable to cache the Home object for the PeriodType EJB */
  private static PeriodTypeHome periodTypeHome = null;

  /** Initializes the periodTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The PeriodTypeHome instance for the default EJB server
   */
  public static PeriodTypeHome getPeriodTypeHome()
  {
    if(periodTypeHome == null) //don't want to block here
    {
      synchronized(PeriodTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(periodTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.common.period.PeriodTypeHome");
            periodTypeHome = (PeriodTypeHome)MyNarrow.narrow(homeObject, PeriodTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("periodType home obtained " + periodTypeHome);
        }
      }
    }
    return periodTypeHome;
  }




  /** Remove the PeriodType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    PeriodType periodType = findByPrimaryKey(primaryKey);
    try
    {
      if(periodType != null)
      {
        periodType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a PeriodType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The PeriodType corresponding to the primaryKey
   */
  public static PeriodType findByPrimaryKey(java.lang.String primaryKey)
  {
    PeriodType periodType = null;
    Debug.logInfo("PeriodTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      periodType = (PeriodType)MyNarrow.narrow(getPeriodTypeHome().findByPrimaryKey(primaryKey), PeriodType.class);
      if(periodType != null)
      {
        periodType = periodType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return periodType;
  }

  /** Finds all PeriodType entities
   *@return    Collection containing all PeriodType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("PeriodTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getPeriodTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a PeriodType
   *@param  periodTypeId                  Field of the PERIOD_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static PeriodType create(String periodTypeId, String description)
  {
    PeriodType periodType = null;
    Debug.logInfo("PeriodTypeHelper.create: periodTypeId: " + periodTypeId);
    if(periodTypeId == null) { return null; }

    try { periodType = (PeriodType)MyNarrow.narrow(getPeriodTypeHome().create(periodTypeId, description), PeriodType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create periodType with periodTypeId: " + periodTypeId);
      Debug.logError(ce);
      periodType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return periodType;
  }

  /** Updates the corresponding PeriodType
   *@param  periodTypeId                  Field of the PERIOD_TYPE_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static PeriodType update(String periodTypeId, String description) throws java.rmi.RemoteException
  {
    if(periodTypeId == null) { return null; }
    PeriodType periodType = findByPrimaryKey(periodTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    PeriodType periodTypeValue = new PeriodTypeValue();

    if(description != null) { periodTypeValue.setDescription(description); }

    periodType.setValueObject(periodTypeValue);
    return periodType;
  }


}
