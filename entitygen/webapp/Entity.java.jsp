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

public interface <%=entity.ejbName%> extends EJBObject
{
<%for(i=0;i<entity.fields.size();i++){%>
  <%if(((Field)entity.fields.elementAt(i)).isPk){%>
  /**
   *  Get the primary key of the <%=((Field)entity.pks.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  public <%=((Field)entity.pks.elementAt(i)).javaType%> get<%=GenUtil.upperFirstChar(((Field)entity.pks.elementAt(i)).fieldName)%>() throws RemoteException;
  <%}else{%>
  /**
   *  Get the value of the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  public <%=((Field)entity.fields.elementAt(i)).javaType%> get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>() throws RemoteException;
  /**
   *  Set the value of the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  public void set<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>(<%=((Field)entity.fields.elementAt(i)).javaType%> <%=((Field)entity.fields.elementAt(i)).fieldName%>) throws RemoteException;
  <%}%>
<%}%>

  /**
   *  Get the value object of this <%=entity.ejbName%> class.
   */
  public <%=entity.ejbName%> getValueObject() throws RemoteException;
  /**
   *  Set the values in the value object of this <%=entity.ejbName%> class.
   */
  public void setValueObject(<%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value) throws RemoteException;
}
