<%@ include file="EntitySetup.jsp" %>
package <%=entity.packageName%>;

import java.rmi.*;
import javax.ejb.*;
import java.util.*;
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

public interface <%=entity.ejbName%>Home extends EJBHome
{
<%if(entity.fields.size() != entity.pks.size()){%>
  public <%=entity.ejbName%> create(<%=entity.fieldTypeNameString()%>) throws RemoteException, CreateException;<%}%>
  public <%=entity.ejbName%> create(<%=entity.primKeyClassNameString()%>) throws RemoteException, CreateException;
  public <%=entity.ejbName%> findByPrimaryKey(<%=entity.primKeyClass%> primaryKey) throws RemoteException, FinderException;
  public Collection findAll() throws RemoteException, FinderException;

<%for(i=0;i<entity.finders.size();i++){%><%EgFinder finderDesc = (EgFinder)entity.finders.elementAt(i);%>
  /** Finds <%=entity.ejbName%>s by the following fields:<%for(int j=0;j<finderDesc.fields.size();j++){%>
   *@param  <%=((EgField)finderDesc.fields.elementAt(j)).fieldName%>                  EgField for the <%=((EgField)finderDesc.fields.elementAt(j)).columnName%> column.<%}%>
   *@return      Collection containing the found <%=entity.ejbName%>s
   */
  public Collection findBy<%=entity.classNameString(finderDesc.fields,"And","")%>(<%=entity.typeNameString(finderDesc.fields)%>) throws RemoteException, FinderException;
<%}%>
}
