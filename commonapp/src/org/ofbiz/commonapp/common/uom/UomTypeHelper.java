
package org.ofbiz.commonapp.common.uom;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> Unit Of Measure Type Entity
 * <p><b>Description:</b> None
 * <p>The Helper class from the UomType Entity EJB; acts as a proxy for the Home interface
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
public class UomTypeHelper
{

  /** A static variable to cache the Home object for the UomType EJB */
  private static UomTypeHome uomTypeHome = null;

  /** Initializes the uomTypeHome, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The UomTypeHome instance for the default EJB server
   */
  public static UomTypeHome getUomTypeHome()
  {
    if(uomTypeHome == null) //don't want to block here
    {
      synchronized(UomTypeHelper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(uomTypeHome == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "org.ofbiz.commonapp.common.uom.UomTypeHome");
            uomTypeHome = (UomTypeHome)MyNarrow.narrow(homeObject, UomTypeHome.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("uomType home obtained " + uomTypeHome);
        }
      }
    }
    return uomTypeHome;
  }




  /** Remove the UomType corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(java.lang.String primaryKey)
  {
    if(primaryKey == null) return;
    UomType uomType = findByPrimaryKey(primaryKey);
    try
    {
      if(uomType != null)
      {
        uomType.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }

  }


  /** Find a UomType by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The UomType corresponding to the primaryKey
   */
  public static UomType findByPrimaryKey(java.lang.String primaryKey)
  {
    UomType uomType = null;
    Debug.logInfo("UomTypeHelper.findByPrimaryKey: Field is:" + primaryKey);

    if(primaryKey == null) { return null; }


    try
    {
      uomType = (UomType)MyNarrow.narrow(getUomTypeHome().findByPrimaryKey(primaryKey), UomType.class);
      if(uomType != null)
      {
        uomType = uomType.getValueObject();
      
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return uomType;
  }

  /** Finds all UomType entities
   *@return    Collection containing all UomType entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("UomTypeHelper.findAll");

    try { collection = (Collection)MyNarrow.narrow(getUomTypeHome().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a UomType
   *@param  uomTypeId                  Field of the UOM_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static UomType create(String uomTypeId, String parentTypeId, String hasTable, String description)
  {
    UomType uomType = null;
    Debug.logInfo("UomTypeHelper.create: uomTypeId: " + uomTypeId);
    if(uomTypeId == null) { return null; }

    try { uomType = (UomType)MyNarrow.narrow(getUomTypeHome().create(uomTypeId, parentTypeId, hasTable, description), UomType.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create uomType with uomTypeId: " + uomTypeId);
      Debug.logError(ce);
      uomType = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return uomType;
  }

  /** Updates the corresponding UomType
   *@param  uomTypeId                  Field of the UOM_TYPE_ID column.
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@param  description                  Field of the DESCRIPTION column.
   *@return                Description of the Returned Value
   */
  public static UomType update(String uomTypeId, String parentTypeId, String hasTable, String description) throws java.rmi.RemoteException
  {
    if(uomTypeId == null) { return null; }
    UomType uomType = findByPrimaryKey(uomTypeId);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    UomType uomTypeValue = new UomTypeValue();

    if(parentTypeId != null) { uomTypeValue.setParentTypeId(parentTypeId); }
    if(hasTable != null) { uomTypeValue.setHasTable(hasTable); }
    if(description != null) { uomTypeValue.setDescription(description); }

    uomType.setValueObject(uomTypeValue);
    return uomType;
  }

  /** Removes/deletes the specified  UomType
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   */
  public static void removeByParentTypeId(String parentTypeId)
  {
    if(parentTypeId == null) return;
    Iterator iterator = UtilMisc.toIterator(findByParentTypeId(parentTypeId));

    while(iterator.hasNext())
    {
      try
      {
        UomType uomType = (UomType) iterator.next();
        Debug.logInfo("Removing uomType with parentTypeId:" + parentTypeId);
        uomType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds UomType records by the following parameters:
   *@param  parentTypeId                  Field of the PARENT_TYPE_ID column.
   *@return      Description of the Returned Value
   */
  public static Collection findByParentTypeId(String parentTypeId)
  {
    Debug.logInfo("findByParentTypeId: parentTypeId:" + parentTypeId);

    Collection collection = null;
    if(parentTypeId == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getUomTypeHome().findByParentTypeId(parentTypeId), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }

  /** Removes/deletes the specified  UomType
   *@param  hasTable                  Field of the HAS_TABLE column.
   */
  public static void removeByHasTable(String hasTable)
  {
    if(hasTable == null) return;
    Iterator iterator = UtilMisc.toIterator(findByHasTable(hasTable));

    while(iterator.hasNext())
    {
      try
      {
        UomType uomType = (UomType) iterator.next();
        Debug.logInfo("Removing uomType with hasTable:" + hasTable);
        uomType.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds UomType records by the following parameters:
   *@param  hasTable                  Field of the HAS_TABLE column.
   *@return      Description of the Returned Value
   */
  public static Collection findByHasTable(String hasTable)
  {
    Debug.logInfo("findByHasTable: hasTable:" + hasTable);

    Collection collection = null;
    if(hasTable == null) { return null; }

    try { collection = (Collection) MyNarrow.narrow(getUomTypeHome().findByHasTable(hasTable), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }


}
