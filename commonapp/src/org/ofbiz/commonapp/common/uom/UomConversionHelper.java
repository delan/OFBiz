
package org.ofbiz.commonapp.common.uom;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Unit Of Measure Conversion Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the UomConversion Entity EJB; acts as a proxy for the Home interface
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
 *@created    Fri Jul 27 01:18:23 MDT 2001
 *@version    1.0
 */
public class UomConversionHelper
{

  /** A static variable to cache the Home object for the UomConversion EJB */
  private static UomConversionHome uomConversionHome = null;

  /** Initializes the uomConversionHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The UomConversionHome instance for the default EJB server
   */
  public static UomConversionHome getUomConversionHome()
  {
    if(uomConversionHome == null) //don't want to block here
    {
      synchronized(UomConversionHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(uomConversionHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.common.uom.UomConversionHome");
            uomConversionHome = (UomConversionHome)MyNarrow.narrow(homeObject, UomConversionHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("uomConversion home obtained " + uomConversionHome);
        }
      }
    }
    return uomConversionHome;
  }



  /** Remove the UomConversion corresponding to the primaryKey specified by fields
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomIdTo                  Field of the UOM_ID_TO column.
   */
  public static void removeByPrimaryKey(String uomId, String uomIdTo)
  {
    if(uomId == null || uomIdTo == null)
    {
      return;
    }
    UomConversionPK primaryKey = new UomConversionPK(uomId, uomIdTo);
    removeByPrimaryKey(primaryKey);
  }

  /** Remove the UomConversion corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(org.ofbiz.commonapp.common.uom.UomConversionPK primaryKey)
  {
    if(primaryKey == null) return;
    UomConversion uomConversion = findByPrimaryKey(primaryKey);
    try
    {
      if(uomConversion != null)
      {
        uomConversion.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }

  /** Find a UomConversion by its Primary Key, specified by individual fields
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomIdTo                  Field of the UOM_ID_TO column.
   *@return       The UomConversion corresponding to the primaryKey
   */
  public static UomConversion findByPrimaryKey(String uomId, String uomIdTo)
  {
    if(uomId == null || uomIdTo == null) return null;
    UomConversionPK primaryKey = new UomConversionPK(uomId, uomIdTo);
    return findByPrimaryKey(primaryKey);
  }

  /** Find a UomConversion by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The UomConversion corresponding to the primaryKey
   */
  public static UomConversion findByPrimaryKey(org.ofbiz.commonapp.common.uom.UomConversionPK primaryKey)
  {
    UomConversion uomConversion = null;
    Debug.logInfo("UomConversionHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      uomConversion = (UomConversion)MyNarrow.narrow(getUomConversionHome().findByPrimaryKey(primaryKey), UomConversion.class);
      if(uomConversion != null)
      {
        uomConversion = uomConversion.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return uomConversion;
  }

  /** Finds all UomConversion entities
   *@return    Collection containing all UomConversion entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("UomConversionHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getUomConversionHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a UomConversion
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomIdTo                  Field of the UOM_ID_TO column.
   *@param  conversionFactor                  Field of the CONVERSION_FACTOR column.
   *@return                Description of the Returned Value
   */
  public static UomConversion create(String uomId, String uomIdTo, Double conversionFactor)
  {
    UomConversion uomConversion = null;
    Debug.logInfo("UomConversionHelper.create: uomId, uomIdTo: " + uomId + ", " + uomIdTo);
    if(uomId == null || uomIdTo == null) { return null; }

    try { uomConversion = (UomConversion)MyNarrow.narrow(getUomConversionHome().create(uomId, uomIdTo, conversionFactor), UomConversion.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create uomConversion with uomId, uomIdTo: " + uomId + ", " + uomIdTo);
      Debug.logError(ce);
      uomConversion = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return uomConversion;
  }

  /** Updates the corresponding UomConversion
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomIdTo                  Field of the UOM_ID_TO column.
   *@param  conversionFactor                  Field of the CONVERSION_FACTOR column.
   *@return                Description of the Returned Value
   */
  public static UomConversion update(String uomId, String uomIdTo, Double conversionFactor) throws java.rmi.RemoteException
  {
    if(uomId == null || uomIdTo == null) { return null; }
    UomConversion uomConversion = findByPrimaryKey(uomId, uomIdTo);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    UomConversion uomConversionValue = new UomConversionValue();

    if(conversionFactor != null) { uomConversionValue.setConversionFactor(conversionFactor); }

    uomConversion.setValueObject(uomConversionValue);
    return uomConversion;
  }

  /** Removes/deletes the specified  UomConversion
   *@param  uomId                  Field of the UOM_ID column.
   */
  public static void removeByUomId(String uomId)
  {
    if(uomId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByUomId(uomId));

    while(iterator.hasNext())
    {
      try
      {
        UomConversion uomConversion = (UomConversion) iterator.next();
        Debug.logInfo("Removing uomConversion with uomId:" + uomId);
        uomConversion.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds UomConversion records by the following parameters:
   *@param  uomId                  Field of the UOM_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUomId(String uomId)
  {
    Debug.logInfo("findByUomId: uomId:" + uomId);

    Collection collection = null;
    if(uomId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getUomConversionHome().findByUomId(uomId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  UomConversion
   *@param  uomIdTo                  Field of the UOM_ID_TO column.
   */
  public static void removeByUomIdTo(String uomIdTo)
  {
    if(uomIdTo == null) return;
    Iterator iterator = UtilMisc.toIterator(findByUomIdTo(uomIdTo));

    while(iterator.hasNext())
    {
      try
      {
        UomConversion uomConversion = (UomConversion) iterator.next();
        Debug.logInfo("Removing uomConversion with uomIdTo:" + uomIdTo);
        uomConversion.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds UomConversion records by the following parameters:
   *@param  uomIdTo                  Field of the UOM_ID_TO column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUomIdTo(String uomIdTo)
  {
    Debug.logInfo("findByUomIdTo: uomIdTo:" + uomIdTo);

    Collection collection = null;
    if(uomIdTo == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getUomConversionHome().findByUomIdTo(uomIdTo), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
