<%@ include file="EntitySetup.jsp"%>
package <%=entity.packageName%>;

import java.rmi.*;
import javax.ejb.*;
import org.ofbiz.commonapp.common.*;

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
public class <%=entity.ejbName%>Value implements <%=entity.ejbName%>
{
<%for(i=0;i<entity.fields.size();i++){%>
  /**
   *  The variable of the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  private <%=((Field)entity.fields.elementAt(i)).javaType%> <%=((Field)entity.fields.elementAt(i)).fieldName%>;
<%}%>

  private <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>;

  public <%=entity.ejbName%>Value()
  {
<%for(i=0;i<entity.fields.size();i++){%>
    this.<%=((Field)entity.fields.elementAt(i)).fieldName%> = null;<%}%>

    this.<%=GenUtil.lowerFirstChar(entity.ejbName)%> = null;
  }

  public <%=entity.ejbName%>Value(<%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>) throws RemoteException
  {
    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null) return;

<%for(i=0;i<entity.fields.size();i++){%>
    this.<%=((Field)entity.fields.elementAt(i)).fieldName%> = <%=GenUtil.lowerFirstChar(entity.ejbName)%>.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>();<%}%>

    this.<%=GenUtil.lowerFirstChar(entity.ejbName)%> = <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
  }

  public <%=entity.ejbName%>Value(<%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>, <%=entity.fieldTypeNameString()%>)
  {
    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%> == null) return;

<%for(i=0;i<entity.fields.size();i++){%>
    this.<%=((Field)entity.fields.elementAt(i)).fieldName%> = <%=((Field)entity.fields.elementAt(i)).fieldName%>;<%}%>

    this.<%=GenUtil.lowerFirstChar(entity.ejbName)%> = <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
  }

<%for(i=0;i<entity.fields.size();i++){%><%if(((Field)entity.fields.elementAt(i)).isPk){%>
  /**
   *  Get the primary key of the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  public <%=((Field)entity.fields.elementAt(i)).javaType%> get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>()  throws RemoteException
  {
    return <%=((Field)entity.fields.elementAt(i)).fieldName%>;
  }
  <%}else{%>
  /**
   *  Get the value of the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  public <%=((Field)entity.fields.elementAt(i)).javaType%> get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>() throws RemoteException
  {
    return <%=((Field)entity.fields.elementAt(i)).fieldName%>;
  }
  /**
   *  Set the value of the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  public void set<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>(<%=((Field)entity.fields.elementAt(i)).javaType%> <%=((Field)entity.fields.elementAt(i)).fieldName%>) throws RemoteException
  {
    this.<%=((Field)entity.fields.elementAt(i)).fieldName%> = <%=((Field)entity.fields.elementAt(i)).fieldName%>;
    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null) <%=GenUtil.lowerFirstChar(entity.ejbName)%>.set<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>(<%=((Field)entity.fields.elementAt(i)).fieldName%>);
  }
  <%}%><%}%>

  /**
   *  Get the value object of the <%=entity.ejbName%> class.
   */
  public <%=entity.ejbName%> getValueObject() throws RemoteException { return this; }
  /**
   *  Set the value object of the <%=entity.ejbName%> class.
   */
  public void setValueObject(<%=entity.ejbName%> valueObject) throws RemoteException
  {
    if(valueObject == null) return;

    if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null) <%=GenUtil.lowerFirstChar(entity.ejbName)%>.setValueObject(valueObject);
<%for(i=0;i<entity.fields.size();i++){%>
  <%if(((Field)entity.fields.elementAt(i)).isPk){%>  if(<%=((Field)entity.fields.elementAt(i)).fieldName%> == null) <%=((Field)entity.fields.elementAt(i)).fieldName%> = valueObject.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>();
  <%}else{%>  <%=((Field)entity.fields.elementAt(i)).fieldName%> = valueObject.get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>();<%}%>
  <%}%>
  }

  //These are from the EJBObject interface, and must at least have thrower implementations, although we do more if the EJBObject is set...
  public EJBHome getEJBHome() throws RemoteException { if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null) return <%=GenUtil.lowerFirstChar(entity.ejbName)%>.getEJBHome(); else throw new ValueException("Cannot call getEJBHome, EJBObject is null."); }
  public Handle getHandle() throws RemoteException { if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null) return <%=GenUtil.lowerFirstChar(entity.ejbName)%>.getHandle(); else throw new ValueException("Cannot call getHandle, EJBObject is null."); }
  public Object getPrimaryKey() throws RemoteException { if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null) return <%=GenUtil.lowerFirstChar(entity.ejbName)%>.getEJBHome(); else throw new ValueException("Cannot call getPrimaryKey, EJBObject is null."); }
  public boolean isIdentical(EJBObject p0) throws RemoteException { if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null) return <%=GenUtil.lowerFirstChar(entity.ejbName)%>.isIdentical(p0); else throw new ValueException("Cannot call isIdentical(EJBObject p0), EJBObject is null."); }
  public void remove() throws RemoteException, RemoveException { if(<%=GenUtil.lowerFirstChar(entity.ejbName)%>!=null) <%=GenUtil.lowerFirstChar(entity.ejbName)%>.remove(); else throw new ValueException("Cannot call getPrimaryKey, remove is null."); }
}
