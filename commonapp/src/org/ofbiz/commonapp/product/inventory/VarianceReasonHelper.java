
package org.ofbiz.commonapp.product.inventory;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Variance Reason Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the VarianceReason Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:31 MDT 2001
 *@version    1.0
 */
public class VarianceReasonHelper
{

  /** A static variable to cache the Home object for the VarianceReason EJB */
  private static VarianceReasonHome varianceReasonHome = null;

  /** Initializes the varianceReasonHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The VarianceReasonHome instance for the default EJB server
   */
  public static VarianceReasonHome getVarianceReasonHome()
  {
    if(varianceReasonHome == null) //don't want to block here
    {
      synchronized(VarianceReasonHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(varianceReasonHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.product.inventory.VarianceReasonHome");
            varianceReasonHome = (VarianceReasonHome)MyNarrow.narrow(homeObject, VarianceReasonHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("varianceReason home obtained " + varianceReasonHome);
        }
      }
    }
    return varianceReasonHome;
  }




  /** Remove the VarianceReason corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    VarianceReason varianceReason = findByPrimaryKey(primaryKey);
    try
    {
      if(varianceReason != null)
      {
        varianceReason.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a VarianceReason by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The VarianceReason corresponding to the primaryKey
   */
  public static VarianceReason findByPrimaryKey(java.lang.String primaryKey)
  {
    VarianceReason varianceReason = null;
    Debug.logInfo("VarianceReasonHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      varianceReason = (VarianceReason)MyNarrow.narrow(getVarianceReasonHome().findByPrimaryKey(primaryKey), VarianceReason.class);
      if(varianceReason != null)
      {
        varianceReason = varianceReason.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return varianceReason;
  }

  /** Finds all VarianceReason entities
   *@return    Collection containing all VarianceReason entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("VarianceReasonHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getVarianceReasonHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a VarianceReason
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static VarianceReason create(String varianceReasonId, String description)
  {
    VarianceReason varianceReason = null;
    Debug.logInfo("VarianceReasonHelper.create: varianceReasonId: " + varianceReasonId);
    if(varianceReasonId == null) { return null; }

    try { varianceReason = (VarianceReason)MyNarrow.narrow(getVarianceReasonHome().create(varianceReasonId, description), VarianceReason.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create varianceReason with varianceReasonId: " + varianceReasonId);
      Debug.logError(ce);
      varianceReason = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return varianceReason;
  }

  /** Updates the corresponding VarianceReason
   *@param  varianceReasonId                  Field of the VARIANCE_REASON_ID column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static VarianceReason update(String varianceReasonId, String description) throws java.rmi.RemoteException
  {
    if(varianceReasonId == null) { return null; }
    VarianceReason varianceReason = findByPrimaryKey(varianceReasonId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    VarianceReason varianceReasonValue = new VarianceReasonValue();

    if(description != null) { varianceReasonValue.setDescription(description); }

    varianceReason.setValueObject(varianceReasonValue);
    return varianceReason;
  }


}
