<%@ include file="EntitySetup.jsp" %>
package <%=entity.packageName%>;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;

/**
 * <p><b>Title:</b> <%=entity.title%>
 * <p><b>Description:</b> <%=entity.description%>
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
public class <%=entity.ejbName%>Bean implements EntityBean
{
<%for(i=0;i<entity.fields.size();i++){%>
  /**
   *  The variable for the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  public <%=((Field)entity.fields.elementAt(i)).javaType%> <%=((Field)entity.fields.elementAt(i)).fieldName%>;
<%}%>

  EntityContext entityContext;

  /**
   *  Sets the EntityContext attribute of the <%=entity.ejbName%>Bean object
   *
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext)
  {
    this.entityContext = entityContext;
  }


<%for(i=0;i<entity.fields.size();i++){%>
  <%if(((Field)entity.fields.elementAt(i)).isPk){%>
  /**
   *  Get the primary key <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  public <%=((Field)entity.fields.elementAt(i)).javaType%> get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>()
  {
    return <%=((Field)entity.fields.elementAt(i)).fieldName%>;
  }
  <%}else{%>
  /**
   *  Get the value of the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  public <%=((Field)entity.fields.elementAt(i)).javaType%> get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>()
  {
    return <%=((Field)entity.fields.elementAt(i)).fieldName%>;
  }
  /**
   *  Set the value of the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  public void set<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>(<%=((Field)entity.fields.elementAt(i)).javaType%> <%=((Field)entity.fields.elementAt(i)).fieldName%>)
  {
    this.<%=((Field)entity.fields.elementAt(i)).fieldName%> = <%=((Field)entity.fields.elementAt(i)).fieldName%>;
  }
  <%}%>
<%}%>

  /**
   *  Sets the values from ValueObject attribute of the <%=entity.ejbName%>Bean object
   *
   *@param  valueObject  The new ValueObject value
   */
  public void setValueObject(<%=entity.ejbName%> valueObject)
  {
<%if(entity.fields.size() != entity.pks.size()){%>
    try
    {
<%for(i=0;i<entity.fields.size();i++){%>
  <%if(!((Field)entity.fields.elementAt(i)).isPk){%>    this.<%=((Field)entity.fields.elementAt(i)).fieldName%> = valueObject.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>();<%}%><%}%>
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }
<%}%>
  }

  /**
   *  Gets the ValueObject attribute of the <%=entity.ejbName%>Bean object
   *
   *@return    The ValueObject value
   */
  public <%=entity.ejbName%> getValueObject()
  {
    if(this.entityContext != null)
    {
      return new <%=entity.ejbName%>Value((<%=entity.ejbName%>)this.entityContext.getEJBObject(), <%=entity.fieldNameString()%>);
    }
    else
    {
      return null;
    }
  }

  /**
   *  Description of the Method
   *
<%for(i=0;i<entity.fields.size();i++){%>
   *@param  <%=((Field)entity.fields.elementAt(i)).fieldName%>                  Field of the <%=((Field)entity.fields.elementAt(i)).columnName%> column.<%}%>
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public <%=entity.primKeyClass%> ejbCreate(<%=entity.fieldTypeNameString()%>) throws CreateException
  {
<%for(i=0;i<entity.fields.size();i++){%>
    this.<%=((Field)entity.fields.elementAt(i)).fieldName%> = <%=((Field)entity.fields.elementAt(i)).fieldName%>;<%}%>
    return null;
  }
<%if(entity.fields.size() != entity.pks.size()){%>
  /**
   *  Description of the Method
   *
<%for(i=0;i<entity.pks.size();i++){%>
   *@param  <%=((Field)entity.pks.elementAt(i)).fieldName%>                  Field of the <%=((Field)entity.pks.elementAt(i)).columnName%> column.<%}%>
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public <%=entity.primKeyClass%> ejbCreate(<%=entity.primKeyClassNameString()%>) throws CreateException
  {
    return ejbCreate(<%=entity.pkNameString()%>, <%=entity.nonPkNullList()%>);
  }
<%}%>
  /**
   *  Description of the Method
   *
<%for(i=0;i<entity.fields.size();i++){%>
   *@param  <%=((Field)entity.fields.elementAt(i)).fieldName%>                  Field of the <%=((Field)entity.fields.elementAt(i)).columnName%> column.<%}%>
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(<%=entity.fieldTypeNameString()%>) throws CreateException
  {
  }
<%if(entity.fields.size() != entity.pks.size()){%>
  /**
   *  Description of the Method
   *
<%for(i=0;i<entity.pks.size();i++){%>
   *@param  <%=((Field)entity.pks.elementAt(i)).fieldName%>                  Field of the <%=((Field)entity.pks.elementAt(i)).columnName%> column.<%}%>
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(<%=entity.primKeyClassNameString()%>) throws CreateException
  {
    ejbPostCreate(<%=entity.pkNameString()%>, <%=entity.nonPkNullList()%>);
  }
<%}%>
  /**
   *  Called when the entity bean is removed.
   *
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException
  {
  }

  /**
   *  Called when the entity bean is activated.
   */
  public void ejbActivate()
  {
  }

  /**
   *  Called when the entity bean is passivated.
   */
  public void ejbPassivate()
  {
  }

  /**
   *  Called when the entity bean is loaded.
   */
  public void ejbLoad()
  {
  }

  /**
   *  Called when the entity bean is stored.
   */
  public void ejbStore()
  {
  }

  /**
   *  Unsets the EntityContext, ie sets it to null.
   */
  public void unsetEntityContext()
  {
    entityContext = null;
  }
}
