<%@ include file="EntitySetup.jsp" %>
package <%=entity.packageName%>;

import java.util.*;
import javax.naming.InitialContext;
import javax.ejb.*;

import org.ofbiz.commonapp.common.*;

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
  /**
   *  A cache object with named <%=entity.ejbName%>Cache, maxSize and expireTime can be set with this name in the conf/cache.properties file
   */
  public static UtilCache valueCache = new UtilCache("<%=entity.ejbName%>Cache");
<%}%>
  /**
   *  A static variable to cache the Home object for the <%=entity.ejbName%> EJB
   */
  public static <%=entity.ejbName%>Home <%=GenUtil.lowerFirstChar(entity.ejbName)%>Home = null;

  /**
   *  Initializes the <%=GenUtil.lowerFirstChar(entity.ejbName)%>Home, from a JNDI lookup, with a cached result,
   *  checking for null each time.
   */
  public static void init()
  {
    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Home == null)
    {
      JNDIContext myJNDIContext = new JNDIContext();
      InitialContext initialContext = myJNDIContext.getInitialContext();
      try
      {
        Object homeObject = MyNarrow.lookup(initialContext, "<%=entity.packageName%>.<%=entity.ejbName%>Home");
        <%=GenUtil.lowerFirstChar(entity.ejbName)%>Home = (<%=entity.ejbName%>Home)MyNarrow.narrow(homeObject, <%=entity.ejbName%>Home.class);
      }
      catch(Exception e1)
      {
        e1.printStackTrace();
      }

      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
      {
        System.out.println("<%=GenUtil.lowerFirstChar(entity.ejbName)%> home obtained " + <%=GenUtil.lowerFirstChar(entity.ejbName)%>Home);
      }
    }
  }


<%if(entity.pks.size()>1){%>
  /**
   *  Description of the Method
   *
<%for(i=0;i<entity.pks.size();i++){%>
   *@param  <%=((Field)entity.pks.elementAt(i)).fieldName%>                  Field of the <%=((Field)entity.pks.elementAt(i)).columnName%> column.<%}%>
   */
  public static void removeByPrimaryKey(<%=entity.primKeyClassNameString()%>)
  {
    if(<%=entity.pkNameString(" == null || ", " == null")%>)
    {
      return;
    }
    <%=entity.ejbName%>PK primaryKey = new <%=entity.ejbName%>PK(<%=entity.pkNameString()%>);
    removeByPrimaryKey(primaryKey);
  }
<%}%>

  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key of the entity to remove.
   */
  public static void removeByPrimaryKey(<%=entity.primKeyClass%> primaryKey)
  {
    if(primaryKey == null)
    {
      return;
    }
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = findByPrimaryKey(primaryKey);
    try
    {
      if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null)
      {
        <%=GenUtil.lowerFirstChar(entity.ejbName)%>.remove();
      }
    }
    catch(Exception e)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.warning", "true"))
      {
        e.printStackTrace();
      }
    }

<%if(entity.useCache){%>
    valueCache.remove(primaryKey);
<%}%>
  }

<%if(entity.pks.size()>1){%>
  /**
   *  Description of the Method
   *
<%for(i=0;i<entity.pks.size();i++){%>
   *@param  <%=((Field)entity.pks.elementAt(i)).fieldName%>                  Field of the <%=((Field)entity.pks.elementAt(i)).columnName%> column.<%}%>
   *@return       Description of the Returned Value
   */
  public static <%=entity.ejbName%> findByPrimaryKey(<%=entity.primKeyClassNameString()%>)
  {
    if(<%=entity.pkNameString(" == null || ", " == null")%>)
    {
      return null;
    }
    <%=entity.ejbName%>PK primaryKey = new <%=entity.ejbName%>PK(<%=entity.pkNameString()%>);
    return findByPrimaryKey(primaryKey);
  }
<%}%>

  /**
   *  Description of the Method
   *
   *@param  primaryKey  The primary key to find by.
   *@return             The <%=entity.ejbName%> of primaryKey
   */
  public static <%=entity.ejbName%> findByPrimaryKey(<%=entity.primKeyClass%> primaryKey)
  {
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("<%=entity.ejbName%>Helper.findByPrimaryKey: Field is:" + primaryKey);
    }

    if(primaryKey == null)
    {
      return null;
    }
<%if(entity.useCache){%>
    <%=GenUtil.lowerFirstChar(entity.ejbName)%> = (<%=entity.ejbName%>)valueCache.get(primaryKey);
    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null)
    {
      return <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
    }
<%}%>
    init();

    try
    {
      <%=GenUtil.lowerFirstChar(entity.ejbName)%> = (<%=entity.ejbName%>)MyNarrow.narrow(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Home.findByPrimaryKey(primaryKey), <%=entity.ejbName%>.class);
      if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> != null)
      {
        <%=GenUtil.lowerFirstChar(entity.ejbName)%> = <%=GenUtil.lowerFirstChar(entity.ejbName)%>.getValueObject();
<%if(entity.useCache){%>
        addCacheValue(<%=GenUtil.lowerFirstChar(entity.ejbName)%>);
<%}%>
      }
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
  }

  /**
   *  Description of the Method
   *
   *@return    Description of the Returned Value
   */
  public static Iterator findAllIterator()
  {
    Collection collection = findAll();
    if(collection != null)
    {
      return collection.iterator();
    }
    else
    {
      return null;
    }
  }

  /**
   *  Description of the Method
   *
   *@return    Description of the Returned Value
   */
  public static Collection findAll()
  {
    Collection collection = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("<%=entity.ejbName%>Helper.findAll");
    }
    init();

    try
    {
      collection = (Collection)MyNarrow.narrow(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Home.findAll(), Collection.class);
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return collection;
  }

  /**
   *  Description of the Method
   *
<%for(i=0;i<entity.fields.size();i++){%>
   *@param  <%=((Field)entity.fields.elementAt(i)).fieldName%>                  Field of the <%=((Field)entity.fields.elementAt(i)).columnName%> column.<%}%>
   *@return                Description of the Returned Value
   */
  public static <%=entity.ejbName%> create(<%=entity.fieldTypeNameString()%>)
  {
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = null;
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("<%=entity.ejbName%>Helper.create: <%=entity.pkNameString()%>: " + <%=entity.pkNameString(" + \", \" + ", "")%>);
    }
    if(<%=entity.pkNameString(" == null || ", " == null")%>)
    {
      return null;
    }
    init();

    try
    {
      <%=GenUtil.lowerFirstChar(entity.ejbName)%> = (<%=entity.ejbName%>)MyNarrow.narrow(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Home.create(<%=entity.fieldNameString()%>), <%=entity.ejbName%>.class);
    }
    catch(CreateException ce)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        System.out.println("Could not create <%=GenUtil.lowerFirstChar(entity.ejbName)%> with <%=entity.pkNameString()%>: " + <%=entity.pkNameString(" + \", \" + ", "")%>);
        ce.printStackTrace();
      }
      <%=GenUtil.lowerFirstChar(entity.ejbName)%> = null;
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }
    return <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
  }

  /**
   *  Description of the Method
   *
<%for(i=0;i<entity.fields.size();i++){%>
   *@param  <%=((Field)entity.fields.elementAt(i)).fieldName%>                  Field of the <%=((Field)entity.fields.elementAt(i)).columnName%> column.<%}%>
   *@return                Description of the Returned Value
   */
  public static <%=entity.ejbName%> update(<%=entity.fieldTypeNameString()%>) throws java.rmi.RemoteException
  {
    if(<%=entity.pkNameString(" == null || ", " == null")%>)
    {
      return null;
    }
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = findByPrimaryKey(<%=entity.pkNameString()%>);
    //Do not pass the value object to set on creation, we only want to populate it not attach it to the passed object
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value = new <%=entity.ejbName%>Value();

<%for(i=0;i<entity.fields.size();i++){%>
  <%if(!((Field)entity.fields.elementAt(i)).isPk){%>
    if(<%=((Field)entity.fields.elementAt(i)).fieldName%> != null)
    {
      <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value.set<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>(<%=((Field)entity.fields.elementAt(i)).fieldName%>);
    }<%}%><%}%>

    <%=GenUtil.lowerFirstChar(entity.ejbName)%>.setValueObject(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Value);
    return <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
  }

<%for(i=0;i<entity.finders.size();i++){%>
  <%Finder finderDesc = (Finder)entity.finders.elementAt(i);%>
  /**
   *  Description of the Method
   *
<%for(int j=0;j<finderDesc.fields.size();j++){%>
   *@param  <%=((Field)finderDesc.fields.elementAt(j)).fieldName%>                  Field of the <%=((Field)finderDesc.fields.elementAt(j)).columnName%> column.<%}%>
   */
  public static void removeBy<%=entity.classNameString(finderDesc.fields,"And","")%>(<%=entity.typeNameString(finderDesc.fields)%>)
  {
    if(<%=entity.nameString(finderDesc.fields, " == null || ", " == null")%>)
    {
      return;
    }
    Iterator iterator = findBy<%=entity.classNameString(finderDesc.fields,"And","")%>Iterator(<%=entity.nameString(finderDesc.fields)%>);

    while(iterator.hasNext())
    {
      try
      {
        <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = (<%=entity.ejbName%>) iterator.next();
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
        {
          System.out.println("Removing <%=GenUtil.lowerFirstChar(entity.ejbName)%> with <%=entity.nameString(finderDesc.fields)%>:" + <%=entity.nameString(finderDesc.fields, " + \", \" + ", "")%>);
        }
        <%=GenUtil.lowerFirstChar(entity.ejbName)%>.remove();
      }
      catch(Exception e)
      {
        if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
        {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   *  Description of the Method
   *
<%for(int j=0;j<finderDesc.fields.size();j++){%>
   *@param  <%=((Field)finderDesc.fields.elementAt(j)).fieldName%>                  Field of the <%=((Field)finderDesc.fields.elementAt(j)).columnName%> column.<%}%>
   *@return      Description of the Returned Value
   */
  public static Iterator findBy<%=entity.classNameString(finderDesc.fields,"And","")%>Iterator(<%=entity.typeNameString(finderDesc.fields)%>)
  {
    Collection collection = findBy<%=entity.classNameString(finderDesc.fields,"And","")%>(<%=entity.nameString(finderDesc.fields)%>);
    if(collection != null)
    {
      return collection.iterator();
    }
    else
    {
      return null;
    }
  }

  /**
   *  Finds <%=entity.ejbName%> records by the following fieldters:
   *
<%for(int j=0;j<finderDesc.fields.size();j++){%>
   *@param  <%=((Field)finderDesc.fields.elementAt(j)).fieldName%>                  Field of the <%=((Field)finderDesc.fields.elementAt(j)).columnName%> column.<%}%>
   *@return      Description of the Returned Value
   */
  public static Collection findBy<%=entity.classNameString(finderDesc.fields,"And","")%>(<%=entity.typeNameString(finderDesc.fields)%>)
  {
    init();
    if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.info", "true"))
    {
      System.out.println("findBy<%=entity.classNameString(finderDesc.fields,"And","")%>: <%=entity.nameString(finderDesc.fields)%>:" + <%=entity.nameString(finderDesc.fields, " + \", \" + ", "")%>);
    }

    Collection collection = null;
    if(<%=entity.nameString(finderDesc.fields, " == null || ", " == null")%>)
    {
      return null;
    }

    try
    {
      collection = (Collection) MyNarrow.narrow(<%=GenUtil.lowerFirstChar(entity.ejbName)%>Home.findBy<%=entity.classNameString(finderDesc.fields,"And","")%>(<%=entity.nameString(finderDesc.fields)%>), Collection.class);
    }
    catch(ObjectNotFoundException onfe)
    {
    }
    catch(Exception fe)
    {
      if(UtilProperties.propertyValueEqualsIgnoreCase("debug", "print.error", "true"))
      {
        fe.printStackTrace();
      }
    }

    return collection;
  }
<%}%>

<%if(entity.useCache){%>
  /**
   *  A method to quickly pre-load the cache from the database
   *
   *@exception  java.rmi.RemoteException  Exception thrown when a remote call fails, contains the remote exception
   */
  public static void fastLoadValues() throws java.rmi.RemoteException
  {
    Iterator iterator = findAllIterator();
    if(iterator == null)
    {
      return;
    }
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>;

    while(iterator.hasNext())
    {
      <%=GenUtil.lowerFirstChar(entity.ejbName)%> = (<%=entity.ejbName%>)iterator.next();
      addCacheValue(<%=GenUtil.lowerFirstChar(entity.ejbName)%>);
    }
  }

  /**
   *  Adds a value object to the valueCache of the <%=entity.ejbName%>Helper class
   *
   *@param  <%=GenUtil.lowerFirstChar(entity.ejbName)%>  The feature to be added to the <%=entity.ejbName%>CacheValue attribute
   *@return          Description of the Returned Value
   */
  public static <%=entity.ejbName%> addCacheValue(<%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>)
  {
    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null)
    {
      return null;
    }
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value = null;
    try
    {
      <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value = new <%=entity.ejbName%>Value(<%=GenUtil.lowerFirstChar(entity.ejbName)%>);
      valueCache.put(<%=GenUtil.lowerFirstChar(entity.ejbName)%>.getPrimaryKey(), <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value);
    }
    catch(java.rmi.RemoteException re)
    {
    }
    return <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value;
  }

  /**
   *  Description of the Method
   *
   *@param  primaryKey  Description of Field
   */
  public static void invalidateCacheLine(<%=entity.primKeyClass%> primaryKey)
  {
    if(primaryKey == null)
    {
      return;
    }
    valueCache.remove(primaryKey);
  }
<%}%>
}
