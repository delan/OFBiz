<%@ include file="EntitySetup.jsp" %>
package <%=entity.packageName%>;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;
<%@ page import="java.util.*" %><%Hashtable importNames = new Hashtable(); importNames.put(entity.packageName,"");%><%for(int relIndex=0;relIndex<entity.relations.size();relIndex++){%><%Relation relation = (Relation)entity.relations.elementAt(relIndex);%><%Entity relatedEntity = DefReader.getEntity(defFileName,relation.relatedEjbName);%><%if(!importNames.containsKey(relatedEntity.packageName)){ importNames.put(relatedEntity.packageName,"");%>
import <%=relatedEntity.packageName%>.*;<%}%><%}%>

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
{<%for(i=0;i<entity.fields.size();i++){%><%if(((Field)entity.fields.elementAt(i)).isPk){%>
  /** Get the primary key of the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table. */
  public <%=((Field)entity.fields.elementAt(i)).javaType%> get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>() throws RemoteException;
  <%}else{%>
  /** Get the value of the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table. */
  public <%=((Field)entity.fields.elementAt(i)).javaType%> get<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>() throws RemoteException;
  /** Set the value of the <%=((Field)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table. */
  public void set<%=GenUtil.upperFirstChar(((Field)entity.fields.elementAt(i)).fieldName)%>(<%=((Field)entity.fields.elementAt(i)).javaType%> <%=((Field)entity.fields.elementAt(i)).fieldName%>) throws RemoteException;
  <%}%><%}%>

  /** Get the value object of this <%=entity.ejbName%> class. */
  public <%=entity.ejbName%> getValueObject() throws RemoteException;
  /** Set the values in the value object of this <%=entity.ejbName%> class. */
  public void setValueObject(<%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%>Value) throws RemoteException;

<%for(int relIndex=0;relIndex<entity.relations.size();relIndex++){%><%Relation relation = (Relation)entity.relations.elementAt(relIndex);%><%Entity relatedEntity = DefReader.getEntity(defFileName,relation.relatedEjbName);%><%if(relation.relationType.equalsIgnoreCase("one")){%>
  /** Get the <%=relation.relationTitle%> <%=relatedEntity.ejbName%> entity corresponding to this entity. */
  public <%=relatedEntity.ejbName%> get<%=relation.relationTitle%><%=relatedEntity.ejbName%>() throws RemoteException;
  /** Remove the <%=relation.relationTitle%> <%=relatedEntity.ejbName%> entity corresponding to this entity. */
  public void remove<%=relation.relationTitle%><%=relatedEntity.ejbName%>() throws RemoteException;  
<%}else if(relation.relationType.equalsIgnoreCase("many")){%>
  /** Get a collection of <%=relation.relationTitle%> <%=relatedEntity.ejbName%> related entities. */
  public Collection get<%=relation.relationTitle%><%=relatedEntity.ejbName%>s() throws RemoteException;
  /** Get the <%=relation.relationTitle%> <%=relatedEntity.ejbName%> keyed by member(s) of this class, and other passed parameters. */
  public <%=relatedEntity.ejbName%> get<%=relation.relationTitle%><%=relatedEntity.ejbName%>(<%=relatedEntity.typeNameStringRelatedNoMapped(relatedEntity.pks, relation)%>) throws RemoteException;
  /** Remove <%=relation.relationTitle%> <%=relatedEntity.ejbName%> related entities. */
  public void remove<%=relation.relationTitle%><%=relatedEntity.ejbName%>s() throws RemoteException;
  /** Remove the <%=relation.relationTitle%> <%=relatedEntity.ejbName%> keyed by member(s) of this class, and other passed parameters. */
  public void remove<%=relation.relationTitle%><%=relatedEntity.ejbName%>(<%=relatedEntity.typeNameStringRelatedNoMapped(relatedEntity.pks, relation)%>) throws RemoteException;
<%}%><%}%>
}
