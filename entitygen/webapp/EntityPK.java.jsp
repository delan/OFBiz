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
{

<%for(i=0;i<entity.pks.size();i++){%>
  /**
   *  The variable of the <%=((Field)entity.pks.elementAt(i)).columnName%> column of the <%=entity.tableName%> table.
   */
  public <%=((Field)entity.pks.elementAt(i)).javaType%> <%=((Field)entity.pks.elementAt(i)).fieldName%>;
<%}%>

  /**
   *  Constructor for the <%=entity.ejbName%>PK object
   */
  public <%=entity.ejbName%>PK()
  {
  }

  /**
   *  Constructor for the <%=entity.ejbName%>PK object
   *
<%for(i=0;i<entity.pks.size();i++){%>
   *@param  <%=((Field)entity.pks.elementAt(i)).fieldName%>                  Field of the <%=((Field)entity.pks.elementAt(i)).columnName%> column.<%}%>
   */
  public <%=entity.ejbName%>PK(<%=entity.primKeyClassNameString()%>)
  {
<%for(i=0;i<entity.pks.size();i++){%>
    this.<%=((Field)entity.pks.elementAt(i)).fieldName%> = <%=((Field)entity.pks.elementAt(i)).fieldName%>;<%}%>
  }

  /**
   *  Description of the Method
   *
   *@param  obj  Description of Field
   *@return      Description of the Returned Value
   */
  public boolean equals(Object obj)
  {
    if(this.getClass().equals(obj.getClass()))
    {
      <%=entity.ejbName%>PK that = (<%=entity.ejbName%>PK)obj;
      return
<%for(i=0;i<entity.pks.size();i++){%>
            this.<%=((Field)entity.pks.elementAt(i)).fieldName%>.equals(that.<%=((Field)entity.pks.elementAt(i)).fieldName%>) &&<%}%>
            true; //This "true" is a dummy thing to take care of the last &&, just for laziness sake.
    }
    return false;
  }

  /**
   *  Description of the Method
   *
   *@return    Description of the Returned Value
   */
  public int hashCode()
  {
    return (<%=entity.pkNameString(" + ","")%>).hashCode();
  }
}
