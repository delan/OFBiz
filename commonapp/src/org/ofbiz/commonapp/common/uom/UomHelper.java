
package org.ofbiz.commonapp.common.uom;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Unit Of Measure Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the Uom Entity EJB; acts as a proxy for the Home interface
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
public class UomHelper
{

  /** A static variable to cache the Home object for the Uom EJB */
  private static UomHome uomHome = null;

  /** Initializes the uomHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The UomHome instance for the default EJB server
   */
  public static UomHome getUomHome()
  {
    if(uomHome == null) //don't want to block here
    {
      synchronized(UomHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(uomHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.common.uom.UomHome");
            uomHome = (UomHome)MyNarrow.narrow(homeObject, UomHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("uom home obtained " + uomHome);
        }
      }
    }
    return uomHome;
  }




  /** Remove the Uom corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    Uom uom = findByPrimaryKey(primaryKey);
    try
    {
      if(uom != null)
      {
        uom.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a Uom by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The Uom corresponding to the primaryKey
   */
  public static Uom findByPrimaryKey(java.lang.String primaryKey)
  {
    Uom uom = null;
    Debug.logInfo("UomHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      uom = (Uom)MyNarrow.narrow(getUomHome().findByPrimaryKey(primaryKey), Uom.class);
      if(uom != null)
      {
        uom = uom.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return uom;
  }

  /** Finds all Uom entities
   *@return    Collection containing all Uom entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("UomHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getUomHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a Uom
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomTypeId                  Field of the UOM_TYPE_ID column.
   *@param  abbreviation                  Field of the ABBREVIATION column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static Uom create(String uomId, String uomTypeId, String abbreviation, String description)
  {
    Uom uom = null;
    Debug.logInfo("UomHelper.create: uomId: " + uomId);
    if(uomId == null) { return null; }

    try { uom = (Uom)MyNarrow.narrow(getUomHome().create(uomId, uomTypeId, abbreviation, description), Uom.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create uom with uomId: " + uomId);
      Debug.logError(ce);
      uom = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return uom;
  }

  /** Updates the corresponding Uom
   *@param  uomId                  Field of the UOM_ID column.
   *@param  uomTypeId                  Field of the UOM_TYPE_ID column.
   *@param  abbreviation                  Field of the ABBREVIATION column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static Uom update(String uomId, String uomTypeId, String abbreviation, String description) throws java.rmi.RemoteException
  {
    if(uomId == null) { return null; }
    Uom uom = findByPrimaryKey(uomId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    Uom uomValue = new UomValue();

    if(uomTypeId != null) { uomValue.setUomTypeId(uomTypeId); }
    if(abbreviation != null) { uomValue.setAbbreviation(abbreviation); }
    if(description != null) { uomValue.setDescription(description); }

    uom.setValueObject(uomValue);
    return uom;
  }

  /** Removes/deletes the specified  Uom
   *@param  uomTypeId                  Field of the UOM_TYPE_ID column.
   */
  public static void removeByUomTypeId(String uomTypeId)
  {
    if(uomTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByUomTypeId(uomTypeId));

    while(iterator.hasNext())
    {
      try
      {
        Uom uom = (Uom) iterator.next();
        Debug.logInfo("Removing uom with uomTypeId:" + uomTypeId);
        uom.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds Uom records by the following parameters:
   *@param  uomTypeId                  Field of the UOM_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByUomTypeId(String uomTypeId)
  {
    Debug.logInfo("findByUomTypeId: uomTypeId:" + uomTypeId);

    Collection collection = null;
    if(uomTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getUomHome().findByUomTypeId(uomTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
