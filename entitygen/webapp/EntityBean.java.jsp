<%@ include file="EntitySetup.jsp" %>
package <%=entity.packageName%>;

import java.rmi.*;
import javax.ejb.*;
import java.math.*;
import java.util.*;
<%@ page import="java.util.*" %><%Hashtable importNames = new Hashtable(); importNames.put(entity.packageName,"");%><%for(int relIndex=0;relIndex<entity.relations.size();relIndex++){%><%EgRelation relation = (EgRelation)entity.relations.elementAt(relIndex);%><%EgEntity relatedEntity = DefReader.getEgEntity(defFileName,relation.relatedEjbName);%><%if(!importNames.containsKey(relatedEntity.packageName)){ importNames.put(relatedEntity.packageName,"");%>
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
public class <%=entity.ejbName%>Bean implements EntityBean
{<%for(i=0;i<entity.fields.size();i++){%>
  /** The variable for the <%=((EgField)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table. */
  public <%=((EgField)entity.fields.elementAt(i)).javaType%> <%=((EgField)entity.fields.elementAt(i)).fieldName%>;<%}%>

  EntityContext entityContext;
  boolean ejbIsModified = false;

  /** Sets the EntityContext attribute of the <%=entity.ejbName%>Bean object
   *@param  entityContext  The new EntityContext value
   */
  public void setEntityContext(EntityContext entityContext) { this.entityContext = entityContext; }
<%for(i=0;i<entity.fields.size();i++){%><%if(((EgField)entity.fields.elementAt(i)).isPk){%>
  /** Get the primary key <%=((EgField)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table. */
  public <%=((EgField)entity.fields.elementAt(i)).javaType%> get<%=GenUtil.upperFirstChar(((EgField)entity.fields.elementAt(i)).fieldName)%>() { return <%=((EgField)entity.fields.elementAt(i)).fieldName%>; }
<%}else{%>
  /** Get the value of the <%=((EgField)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table. */
  public <%=((EgField)entity.fields.elementAt(i)).javaType%> get<%=GenUtil.upperFirstChar(((EgField)entity.fields.elementAt(i)).fieldName)%>() { return <%=((EgField)entity.fields.elementAt(i)).fieldName%>; }
  /** Set the value of the <%=((EgField)entity.fields.elementAt(i)).columnName%> column of the <%=entity.tableName%> table. */
  public void set<%=GenUtil.upperFirstChar(((EgField)entity.fields.elementAt(i)).fieldName)%>(<%=((EgField)entity.fields.elementAt(i)).javaType%> <%=((EgField)entity.fields.elementAt(i)).fieldName%>)
  {
    this.<%=((EgField)entity.fields.elementAt(i)).fieldName%> = <%=((EgField)entity.fields.elementAt(i)).fieldName%>;
    ejbIsModified = true;
  }
<%}%><%}%>
  /** Sets the values from ValueObject attribute of the <%=entity.ejbName%>Bean object
   *@param  valueObject  The new ValueObject value 
   */
  public void setValueObject(<%=entity.ejbName%> valueObject)
  {<%if(entity.fields.size() != entity.pks.size()){%>
    try
    {
      //check for null and if null do not set; this is the method for not setting certain fields while setting the rest quickly
      // to set a field to null, use the individual setters<%for(i=0;i<entity.fields.size();i++){%><%if(!((EgField)entity.fields.elementAt(i)).isPk){%>
      if(valueObject.get<%=GenUtil.upperFirstChar(((EgField)entity.fields.elementAt(i)).fieldName)%>() != null)
      {
        this.<%=((EgField)entity.fields.elementAt(i)).fieldName%> = valueObject.get<%=GenUtil.upperFirstChar(((EgField)entity.fields.elementAt(i)).fieldName)%>();
        ejbIsModified = true;
      }<%}%><%}%>
    }
    catch(java.rmi.RemoteException re)
    {
      //This should NEVER happen just calling getters on a value object, so do nothing.
      //The only reason these methods are declated to throw a RemoteException is to implement the corresponding EJBObject interface.
    }<%}%>
  }

  /** Gets the ValueObject attribute of the <%=entity.ejbName%>Bean object
   *@return    The ValueObject value
   */
  public <%=entity.ejbName%> getValueObject()
  {
    if(this.entityContext != null)
    {
      return new <%=entity.ejbName%>Value((<%=entity.ejbName%>)this.entityContext.getEJBObject(), <%=entity.fieldNameString()%>);
    }
    else { return null; }
  }

<%for(int relIndex=0;relIndex<entity.relations.size();relIndex++){%><%EgRelation relation = (EgRelation)entity.relations.elementAt(relIndex);%><%EgEntity relatedEntity = DefReader.getEgEntity(defFileName,relation.relatedEjbName);%><%if(relation.relationType.equalsIgnoreCase("one")){%>
  /** Get the <%=relation.relationTitle%> <%=relatedEntity.ejbName%> entity corresponding to this entity. */
  public <%=relatedEntity.ejbName%> get<%=relation.relationTitle%><%=relatedEntity.ejbName%>() { return <%=relatedEntity.ejbName%>Helper.findByPrimaryKey(<%=relation.keyMapString(", ", "")%>); }
  /** Remove the <%=relation.relationTitle%> <%=relatedEntity.ejbName%> entity corresponding to this entity. */
  public void remove<%=relation.relationTitle%><%=relatedEntity.ejbName%>() { <%=relatedEntity.ejbName%>Helper.removeByPrimaryKey(<%=relation.keyMapString(", ", "")%>); }
<%}else if(relation.relationType.equalsIgnoreCase("many")){%>
  /** Get a collection of <%=relation.relationTitle%> <%=relatedEntity.ejbName%> related entities. */
  public Collection get<%=relation.relationTitle%><%=relatedEntity.ejbName%>s() { return <%=relatedEntity.ejbName%>Helper.findBy<%=relation.keyMapRelatedUpperString("And","")%>(<%=relation.keyMapString(", ", "")%>); }
  /** Get the <%=relation.relationTitle%> <%=relatedEntity.ejbName%> keyed by member(s) of this class, and other passed parameters. */
  public <%=relatedEntity.ejbName%> get<%=relation.relationTitle%><%=relatedEntity.ejbName%>(<%=relatedEntity.typeNameStringRelatedNoMapped(relatedEntity.pks, relation)%>) { return <%=relatedEntity.ejbName%>Helper.findByPrimaryKey(<%=relatedEntity.typeNameStringRelatedAndMain(relatedEntity.pks, relation)%>); }
  /** Remove <%=relation.relationTitle%> <%=relatedEntity.ejbName%> related entities. */
  public void remove<%=relation.relationTitle%><%=relatedEntity.ejbName%>s() { <%=relatedEntity.ejbName%>Helper.removeBy<%=relation.keyMapRelatedUpperString("And","")%>(<%=relation.keyMapString(", ", "")%>); }
  /** Remove the <%=relation.relationTitle%> <%=relatedEntity.ejbName%> keyed by member(s) of this class, and other passed parameters. */
  public void remove<%=relation.relationTitle%><%=relatedEntity.ejbName%>(<%=relatedEntity.typeNameStringRelatedNoMapped(relatedEntity.pks, relation)%>) { <%=relatedEntity.ejbName%>Helper.removeByPrimaryKey(<%=relatedEntity.typeNameStringRelatedAndMain(relatedEntity.pks, relation)%>); }
<%}%><%}%>

  /** Description of the Method<%for(i=0;i<entity.fields.size();i++){%>
   *@param  <%=((EgField)entity.fields.elementAt(i)).fieldName%>                  EgField of the <%=((EgField)entity.fields.elementAt(i)).columnName%> column.<%}%>
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public <%=entity.primKeyClass%> ejbCreate(<%=entity.fieldTypeNameString()%>) throws CreateException
  {<%for(i=0;i<entity.fields.size();i++){%>
    this.<%=((EgField)entity.fields.elementAt(i)).fieldName%> = <%=((EgField)entity.fields.elementAt(i)).fieldName%>;<%}%>
    return null;
  }
<%if(entity.fields.size() != entity.pks.size()){%>
  /** Description of the Method<%for(i=0;i<entity.pks.size();i++){%>
   *@param  <%=((EgField)entity.pks.elementAt(i)).fieldName%>                  EgField of the <%=((EgField)entity.pks.elementAt(i)).columnName%> column.<%}%>
   *@return                      Description of the Returned Value
   *@exception  CreateException  Description of Exception
   */
  public <%=entity.primKeyClass%> ejbCreate(<%=entity.primKeyClassNameString()%>) throws CreateException
  {
    return ejbCreate(<%=entity.pkNameString()%>, <%=entity.nonPkNullList()%>);
  }
<%}%>
  /** Description of the Method<%for(i=0;i<entity.fields.size();i++){%>
   *@param  <%=((EgField)entity.fields.elementAt(i)).fieldName%>                  EgField of the <%=((EgField)entity.fields.elementAt(i)).columnName%> column.<%}%>
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(<%=entity.fieldTypeNameString()%>) throws CreateException {}
<%if(entity.fields.size() != entity.pks.size()){%>
  /** Description of the Method<%for(i=0;i<entity.pks.size();i++){%>
   *@param  <%=((EgField)entity.pks.elementAt(i)).fieldName%>                  EgField of the <%=((EgField)entity.pks.elementAt(i)).columnName%> column.<%}%>
   *@exception  CreateException  Description of Exception
   */
  public void ejbPostCreate(<%=entity.primKeyClassNameString()%>) throws CreateException
  {
    ejbPostCreate(<%=entity.pkNameString()%>, <%=entity.nonPkNullList()%>);
  }
<%}%>
  /** Called when the entity bean is removed.
   *@exception  RemoveException  Description of Exception
   */
  public void ejbRemove() throws RemoveException {}

  /** Called when the entity bean is activated. */
  public void ejbActivate() {}

  /** Called when the entity bean is passivated. */
  public void ejbPassivate() {}

  /** Called when the entity bean is loaded. */
  public void ejbLoad() { ejbIsModified = false; }

  /** Called when the entity bean is stored. */
  public void ejbStore() { ejbIsModified = false; }

  /** Called to check if the entity bean needs to be stored. */
  public boolean isModified() { return ejbIsModified; }

  /** Unsets the EntityContext, ie sets it to null. */
  public void unsetEntityContext() { entityContext = null; }
}
