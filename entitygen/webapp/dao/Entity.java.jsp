<%@ include file="EntitySetup.jsp"%>
package <%=entity.packageName%>;

import java.util.*;
import java.sql.*;

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
 *@author   <%=entity.author%>
 *@created  <%=(new java.util.Date()).toString()%>
 *@version  <%=entity.version%>
 */
public class <%=entity.ejbName%> implements java.io.Serializable
{<%for(i=0;i<entity.fields.size();i++){%>
  public <%=((EgField)entity.fields.elementAt(i)).javaType%> <%=((EgField)entity.fields.elementAt(i)).fieldName%>;<%}%>
  
  public boolean modified = false;

  public <%=entity.ejbName%>() { }
<%if(entity.fields.size() != entity.pks.size()){%>
  public <%=entity.ejbName%>(<%=entity.primKeyClassNameString()%>)
  {<%for(i=0;i<entity.fields.size();i++){%><%if(((EgField)entity.fields.elementAt(i)).isPk){%>
    this.<%=((EgField)entity.fields.elementAt(i)).fieldName%> = <%=((EgField)entity.fields.elementAt(i)).fieldName%>;<%}%><%}%>
  }<%}%>

  public <%=entity.ejbName%>(<%=entity.fieldTypeNameString()%>)
  {<%for(i=0;i<entity.fields.size();i++){%>
    this.<%=((EgField)entity.fields.elementAt(i)).fieldName%> = <%=((EgField)entity.fields.elementAt(i)).fieldName%>;<%}%>
  }

  public <%=entity.ejbName%>(<%=entity.ejbName%> valueObject) { setValueObject(valueObject); }
<%if(entity.pks.size() > 1){%>
  public <%=entity.ejbName%>PK getPrimaryKey() { return new <%=entity.ejbName%>PK(<%=entity.pkNameString()%>); }<%}%>
<%for(i=0;i<entity.fields.size();i++){%>
  public <%=((EgField)entity.fields.elementAt(i)).javaType%> get<%=GenUtil.upperFirstChar(((EgField)entity.fields.elementAt(i)).fieldName)%>() { return <%=((EgField)entity.fields.elementAt(i)).fieldName%>; }
  public void set<%=GenUtil.upperFirstChar(((EgField)entity.fields.elementAt(i)).fieldName)%>(<%=((EgField)entity.fields.elementAt(i)).javaType%> <%=((EgField)entity.fields.elementAt(i)).fieldName%>) { this.<%=((EgField)entity.fields.elementAt(i)).fieldName%> = <%=((EgField)entity.fields.elementAt(i)).fieldName%>; modified = true; }<%}%>

  /** Get the value object of the <%=entity.ejbName%> class. */
  public <%=entity.ejbName%> getValueObject() { return this; }
  /** Set the value object of the <%=entity.ejbName%> class. */
  public void setValueObject(<%=entity.ejbName%> valueObject)
  {
    if(valueObject == null) return;
  <%for(i=0;i<entity.pks.size();i++){%>
    <%=((EgField)entity.pks.elementAt(i)).fieldName%> = valueObject.get<%=GenUtil.upperFirstChar(((EgField)entity.pks.elementAt(i)).fieldName)%>();<%}%>
    mergeValueObject(valueObject);
  }
  /** Merge the value object of the <%=entity.ejbName%> class, setting only non primary key fields. */
  public void mergeValueObject(<%=entity.ejbName%> valueObject)
  {
    if(valueObject == null) return;
<%for(i=0;i<entity.fields.size();i++){%><%if(((EgField)entity.fields.elementAt(i)).isPk){%><%}else{%>
    <%=((EgField)entity.fields.elementAt(i)).fieldName%> = valueObject.get<%=GenUtil.upperFirstChar(((EgField)entity.fields.elementAt(i)).fieldName)%>();<%}%><%}%>
  }

  public boolean isModified() { return modified; }
}
