<%@ include file="EntitySetup.jsp" %>
package <%=entity.packageName%>;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.core.util.*;

/**
 * <p><b>Title:</b> <%=entity.title%>
 * <p><b>Description:</b> <%=entity.description%>
 * <p>The Helper class from the <%=entity.ejbName%> Entity EJB; acts as a proxy for the Home interface
 *
 * <p><%=entity.copyright%>
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
 *@author     <%=entity.author%>
 *@created    <%=(new java.util.Date()).toString()%>
 *@version    <%=entity.version%>
 */
public class <%=entity.ejbName%>Helper
{
<%if(entity.useCache){%>
  /** A cache object with named <%=entity.ejbName%>Cache, maxSize and expireTime can be set with this name in the conf/cache.properties file */
  public static UtilCache valueCache = new UtilCache("<%=entity.ejbName%>Cache");<%}%>
  /** A static variable to cache the Home object for the <%=entity.ejbName%> EJB */
  private static <%=entity.ejbName%>Home <%=GenUtil.lowerFirstChar(entity.ejbName)%>Home = null;

  /** Initializes the <%=GenUtil.lowerFirstChar(entity.ejbName)%>Home, from a JNDI lookup, with a cached result, checking for null each time. 
   *@return The <%=entity.ejbName%>Home instance for the default EJB server
   */
  public static <%=entity.ejbName%>Home get<%=entity.ejbName%>Home()
  {
    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Home == null) //don't want to block here
    {
      synchronized(<%=entity.ejbName%>Helper.class) 
      { 
        //must check if null again as one of the blocked threads can still enter 
        if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Home == null) //now it's safe
        {
          JNDIContext myJNDIContext = new JNDIContext();
          InitialContext initialContext = myJNDIContext.getInitialContext();
          try
          {
            Object homeObject = MyNarrow.lookup(initialContext, "<%=entity.packageName%>.<%=entity.ejbName%>Home");
            <%=GenUtil.lowerFirstChar(entity.ejbName)%>Home = (<%=entity.ejbName%>Home)MyNarrow.narrow(homeObject, <%=entity.ejbName%>Home.class);
          }
          catch(Exception e1) { Debug.logError(e1); }
          Debug.logInfo("<%=GenUtil.lowerFirstChar(entity.ejbName)%> home obtained " + <%=GenUtil.lowerFirstChar(entity.ejbName)%>Home);
        }
      }
    }
    return <%=GenUtil.lowerFirstChar(entity.ejbName)%>Home;
  }


<%if(entity.pks.size()>1){%>
  /** Remove the <%=entity.ejbName%> corresponding to the primaryKey specified by fields<%for(i=0;i<entity.pks.size();i++){%>
   *@param  <%=((EgField)entity.pks.elementAt(i)).fieldName%>                  EgField of the <%=((EgField)entity.pks.elementAt(i)).columnName%> column.<%}%>
   */
  public static void removeByPrimaryKey(<%=entity.primKeyClassNameString()%>)
  {
    if(<%=entity.pkNameString(" == null || ", " == null")%>)
    {
      return;
    }
    <%=entity.ejbName%>PK primaryKey = new <%=entity.ejbName%>PK(<%=entity.pkNameString()%>);
    removeByPrimaryKey(primaryKey);
  }<%}%>

  /** Remove the <%=entity.ejbName%> corresponding to the primaryKey
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(<%=entity.primKeyClass%> primaryKey)
  {
    if(primaryKey == null) return;
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = findByPrimaryKey(primaryKey);
    try
    {
      if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null)
      {
        <%=GenUtil.lowerFirstChar(entity.ejbName)%>.remove();
      }
    }
    catch(Exception e) { Debug.logWarning(e); }
<%if(entity.useCache){%>
    valueCache.remove(primaryKey);<%}%>
  }
<%if(entity.pks.size()>1){%>
  /** Find a <%=entity.ejbName%> by its Primary Key, specified by individual fields<%for(i=0;i<entity.pks.size();i++){%>
   *@param  <%=((EgField)entity.pks.elementAt(i)).fieldName%>                  EgField of the <%=((EgField)entity.pks.elementAt(i)).columnName%> column.<%}%>
   *@return       The <%=entity.ejbName%> corresponding to the primaryKey
   */
  public static <%=entity.ejbName%> findByPrimaryKey(<%=entity.primKeyClassNameString()%>)
  {
    if(<%=entity.pkNameString(" == null || ", " == null")%>) return null;
    <%=entity.ejbName%>PK primaryKey = new <%=entity.ejbName%>PK(<%=entity.pkNameString()%>);
    return findByPrimaryKey(primaryKey);
  }<%}%>

  /** Find a <%=entity.ejbName%> by its Primary Key
   *@param  primaryKey  The primary key to find by.
   *@return             The <%=entity.ejbName%> corresponding to the primaryKey
   */
  public static <%=entity.ejbName%> findByPrimaryKey(<%=entity.primKeyClass%> primaryKey)
  {
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = null;
    Debug.logInfo("<%=entity.ejbName%>Helper.findByPrimaryKey: EgField is:" + primaryKey);

    if(primaryKey == null) { return null; }
<%if(entity.useCache){%>
    <%=GenUtil.lowerFirstChar(entity.ejbName)%> = (<%=entity.ejbName%>)valueCache.get(primaryKey);
    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null) return <%=GenUtil.lowerFirstChar(entity.ejbName)%>;<%}%>

    try
    {
      <%=GenUtil.lowerFirstChar(entity.ejbName)%> = (<%=entity.ejbName%>)MyNarrow.narrow(get<%=entity.ejbName%>Home().findByPrimaryKey(primaryKey), <%=entity.ejbName%>.class);
      if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null)
      {
        <%=GenUtil.lowerFirstChar(entity.ejbName)%> = <%=GenUtil.lowerFirstChar(entity.ejbName)%>.getValueObject();
      <%if(entity.useCache){%>
        addCacheValue(<%=GenUtil.lowerFirstChar(entity.ejbName)%>);<%}%>
      }
    }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
  }

  /** Finds all <%=entity.ejbName%> entities
   *@return    Collection containing all <%=entity.ejbName%> entities
   */
  public static Collection findAll()
  {
    Collection collection = null;
    Debug.logInfo("<%=entity.ejbName%>Helper.findAll");

    try { collection = (Collection)MyNarrow.narrow(get<%=entity.ejbName%>Home().findAll(), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }
    return collection;
  }

  /** Creates a <%=entity.ejbName%><%for(i=0;i<entity.fields.size();i++){%>
   *@param  <%=((EgField)entity.fields.elementAt(i)).fieldName%>                  EgField of the <%=((EgField)entity.fields.elementAt(i)).columnName%> column.<%}%>
   *@return                Description of the Returned Value
   */
  public static <%=entity.ejbName%> create(<%=entity.fieldTypeNameString()%>)
  {
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = null;
    Debug.logInfo("<%=entity.ejbName%>Helper.create: <%=entity.pkNameString()%>: " + <%=entity.pkNameString(" + \", \" + ", "")%>);
    if(<%=entity.pkNameString(" == null || ", " == null")%>) { return null; }

    try { <%=GenUtil.lowerFirstChar(entity.ejbName)%> = (<%=entity.ejbName%>)MyNarrow.narrow(get<%=entity.ejbName%>Home().create(<%=entity.fieldNameString()%>), <%=entity.ejbName%>.class); }
    catch(CreateException ce)
    {
      Debug.logError("Could not create <%=GenUtil.lowerFirstChar(entity.ejbName)%> with <%=entity.pkNameString()%>: " + <%=entity.pkNameString(" + \", \" + ", "")%>);
      Debug.logError(ce);
      <%=GenUtil.lowerFirstChar(entity.ejbName)%> = null;
    }
    catch(Exception fe) { Debug.logError(fe); }
    return <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
  }

  /** Updates the corresponding <%=entity.ejbName%><%for(i=0;i<entity.fields.size();i++){%>
   *@param  <%=((EgField)entity.fields.elementAt(i)).fieldName%>                  EgField of the <%=((EgField)entity.fields.elementAt(i)).columnName%> column.<%}%>
   *@return                Description of the Returned Value
   */
  public static <%=entity.ejbName%> update(<%=entity.fieldTypeNameString()%>) throws java.rmi.RemoteException
  {
    if(<%=entity.pkNameString(" == null || ", " == null")%>) { return null; }
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = findByPrimaryKey(<%=entity.pkNameString()%>);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value = new <%=entity.ejbName%>Value();
<%for(i=0;i<entity.fields.size();i++){%><%if(!((EgField)entity.fields.elementAt(i)).isPk){%>
    if(<%=((EgField)entity.fields.elementAt(i)).fieldName%> != null) { <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value.set<%=GenUtil.upperFirstChar(((EgField)entity.fields.elementAt(i)).fieldName)%>(<%=((EgField)entity.fields.elementAt(i)).fieldName%>); }<%}%><%}%>

    <%=GenUtil.lowerFirstChar(entity.ejbName)%>.setValueObject(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Value);
    return <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
  }
<%for(i=0;i<entity.finders.size();i++){%><%EgFinder finderDesc = (EgFinder)entity.finders.elementAt(i);%>
  /** Removes/deletes the specified  <%=entity.ejbName%><%for(int j=0;j<finderDesc.fields.size();j++){%>
   *@param  <%=((EgField)finderDesc.fields.elementAt(j)).fieldName%>                  EgField of the <%=((EgField)finderDesc.fields.elementAt(j)).columnName%> column.<%}%>
   */
  public static void removeBy<%=entity.classNameString(finderDesc.fields,"And","")%>(<%=entity.typeNameString(finderDesc.fields)%>)
  {
    if(<%=entity.nameString(finderDesc.fields, " == null || ", " == null")%>) return;
    Iterator iterator = UtilMisc.toIterator(findBy<%=entity.classNameString(finderDesc.fields,"And","")%>(<%=entity.nameString(finderDesc.fields)%>));

    while(iterator.hasNext())
    {
      try
      {
        <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = (<%=entity.ejbName%>) iterator.next();
        Debug.logInfo("Removing <%=GenUtil.lowerFirstChar(entity.ejbName)%> with <%=entity.nameString(finderDesc.fields)%>:" + <%=entity.nameString(finderDesc.fields, " + \", \" + ", "")%>);
        <%=GenUtil.lowerFirstChar(entity.ejbName)%>.remove();
      }
      catch(Exception e) { Debug.logError(e); }
    }
  }

  /** Finds <%=entity.ejbName%> records by the following parameters:<%for(int j=0;j<finderDesc.fields.size();j++){%>
   *@param  <%=((EgField)finderDesc.fields.elementAt(j)).fieldName%>                  EgField of the <%=((EgField)finderDesc.fields.elementAt(j)).columnName%> column.<%}%>
   *@return      Description of the Returned Value
   */
  public static Collection findBy<%=entity.classNameString(finderDesc.fields,"And","")%>(<%=entity.typeNameString(finderDesc.fields)%>)
  {
    Debug.logInfo("findBy<%=entity.classNameString(finderDesc.fields,"And","")%>: <%=entity.nameString(finderDesc.fields)%>:" + <%=entity.nameString(finderDesc.fields, " + \", \" + ", "")%>);

    Collection collection = null;
    if(<%=entity.nameString(finderDesc.fields, " == null || ", " == null")%>) { return null; }

    try { collection = (Collection) MyNarrow.narrow(get<%=entity.ejbName%>Home().findBy<%=entity.classNameString(finderDesc.fields,"And","")%>(<%=entity.nameString(finderDesc.fields)%>), Collection.class); }
    catch(ObjectNotFoundException onfe) { }
    catch(Exception fe) { Debug.logError(fe); }

    return collection;
  }
<%}%>
<%if(entity.useCache){%>
  /** A method to quickly pre-load the cache from the database
   *@exception  java.rmi.RemoteException  Exception thrown when a remote call fails, contains the remote exception
   */
  public static void fastLoadValues() throws java.rmi.RemoteException
  {
    Iterator iterator = UtilMisc.toIterator(findAll());
    if(iterator == null) { return; }
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>;

    while(iterator.hasNext())
    {
      <%=GenUtil.lowerFirstChar(entity.ejbName)%> = (<%=entity.ejbName%>)iterator.next();
      addCacheValue(<%=GenUtil.lowerFirstChar(entity.ejbName)%>);
    }
  }

  /** Adds a value object to the valueCache of the <%=entity.ejbName%>Helper class
   *@param  <%=GenUtil.lowerFirstChar(entity.ejbName)%>  The feature to be added to the <%=entity.ejbName%>CacheValue attribute
   *@return          Description of the Returned Value
   */
  public static <%=entity.ejbName%> addCacheValue(<%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>)
  {
    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null) { return null; }
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value = null;
    try
    {
      <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value = new <%=entity.ejbName%>Value(<%=GenUtil.lowerFirstChar(entity.ejbName)%>);
      valueCache.put(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.getPrimaryKey(), <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value);
    }
    catch(java.rmi.RemoteException re) { }
    return <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value;
  }

  /** Removes the object from the cache which corresponds to the primaryKey
   *@param  primaryKey  key of cache line to remove
   */
  public static void invalidateCacheLine(<%=entity.primKeyClass%> primaryKey)
  {
    if(primaryKey == null) { return; }
    valueCache.remove(primaryKey);
  }
<%}%>
}
