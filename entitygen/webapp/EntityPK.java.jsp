<%@ include file="EntitySetup.jsp" %>
package <%=entity.packageName%>;

import java.io.*;

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
public class <%=entity.ejbName%>PK implements Serializable
{<%for(i=0;i<entity.pks.size();i++){%>
  /** The variable of the <%=((EgField)entity.pks.elementAt(i)).columnName%> column of the <%=entity.tableName%> table. */
  public <%=((EgField)entity.pks.elementAt(i)).javaType%> <%=((EgField)entity.pks.elementAt(i)).fieldName%>;<%}%>

  /** Constructor for the <%=entity.ejbName%>PK object */
  public <%=entity.ejbName%>PK() { }

  /** Constructor for the <%=entity.ejbName%>PK object
<%for(i=0;i<entity.pks.size();i++){%>
   *@param  <%=((EgField)entity.pks.elementAt(i)).fieldName%>                  EgField of the <%=((EgField)entity.pks.elementAt(i)).columnName%> column.<%}%>
   */
  public <%=entity.ejbName%>PK(<%=entity.primKeyClassNameString()%>)
  {<%for(i=0;i<entity.pks.size();i++){%>
    this.<%=((EgField)entity.pks.elementAt(i)).fieldName%> = <%=((EgField)entity.pks.elementAt(i)).fieldName%>;<%}%>
  }

  /** Determines the equality of two <%=entity.ejbName%>PK objects, overrides the default equals
   *@param  obj  The object (<%=entity.ejbName%>PK) to compare this two
   *@return      boolean stating if the two objects are equal
   */
  public boolean equals(Object obj)
  {
    if(this.getClass().equals(obj.getClass()))
    {
      <%=entity.ejbName%>PK that = (<%=entity.ejbName%>PK)obj;
      return<%for(i=0;i<entity.pks.size();i++){%>
            this.<%=((EgField)entity.pks.elementAt(i)).fieldName%>.equals(that.<%=((EgField)entity.pks.elementAt(i)).fieldName%>) &&<%}%>
            true; //This "true" is a dummy thing to take care of the last &&, just for laziness sake.
    }
    return false;
  }

  /** Creates a hashCode for the combined primary keys, using the default String hashCode, overrides the default hashCode
   *@return    Hashcode corresponding to this primary key
   */
  public int hashCode() { return (<%=entity.pkNameString(" + \"::\" + ","")%>).hashCode(); }

  /** Creates a String for the combined primary keys, overrides the default toString
   *@return    String corresponding to this primary key
   */
  public String toString() { return <%=entity.pkNameString(" + \"::\" + ","")%>; }
}
