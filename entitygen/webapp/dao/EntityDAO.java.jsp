<%@ include file="EntitySetup.jsp"%><%Vector nopks = new Vector(entity.fields); for(int ind=0;ind<entity.pks.size();ind++){ nopks.removeElement(entity.pks.elementAt(ind)); }%>
package <%=entity.packageName%>;

import java.sql.*;
import java.util.*;
import org.ofbiz.core.util.*;

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
public class <%=entity.ejbName%>DAO
{
  public static Connection getConnection() throws SQLException { return ConnectionFactory.getConnection(); }
  
  public static boolean insert(<%=entity.ejbName%> value)
  {
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.insert]: Cannot insert <%=entity.ejbName%>: required primary key field(s) missing.");
      return false;
    }
    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.insert]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "INSERT INTO <%=entity.tableName%> (<%=entity.colNameString(entity.fields)%>) VALUES (<%=entity.fieldsStringList(entity.fields, "?", ", ")%>)";
    try {
      ps = connection.prepareStatement(sql);
    <%for(i=0;i<entity.fields.size();i++){EgField curField=(EgField)entity.fields.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
      ps.setString(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
      ps.setTimestamp(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
      ps.setTime(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
      ps.setDate(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
      if(value.<%=curField.fieldName%> != null) ps.setInt(<%=i+1%>, value.<%=curField.fieldName%>.intValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
      if(value.<%=curField.fieldName%> != null) ps.setLong(<%=i+1%>, value.<%=curField.fieldName%>.longValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
      if(value.<%=curField.fieldName%> != null) ps.setFloat(<%=i+1%>, value.<%=curField.fieldName%>.floatValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
      if(value.<%=curField.fieldName%> != null) ps.setDouble(<%=i+1%>, value.<%=curField.fieldName%>.doubleValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}%><%}%>

      ps.executeUpdate();
      value.modified = false;
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.insert]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }    
    return true;
  }

  public static boolean update(<%=entity.ejbName%> value)
  {
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.update]: Cannot update <%=entity.ejbName%>: required primary key field(s) missing.");
      return false;
    }
    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.update]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }

    String sql = "UPDATE <%=entity.tableName%> SET <%=entity.colNameString(nopks, "=?, ", "=?")%> WHERE <%=entity.colNameString(entity.pks, "=? AND ", "=?")%>";
    try {
      ps = connection.prepareStatement(sql);

    <%for(i=0;i<nopks.size();i++){EgField curField=(EgField)nopks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
      ps.setString(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
      ps.setTimestamp(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
      ps.setTime(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
      ps.setDate(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
      if(value.<%=curField.fieldName%> != null) ps.setInt(<%=i+1%>, value.<%=curField.fieldName%>.intValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
      if(value.<%=curField.fieldName%> != null) ps.setLong(<%=i+1%>, value.<%=curField.fieldName%>.longValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
      if(value.<%=curField.fieldName%> != null) ps.setFloat(<%=i+1%>, value.<%=curField.fieldName%>.floatValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
      if(value.<%=curField.fieldName%> != null) ps.setDouble(<%=i+1%>, value.<%=curField.fieldName%>.doubleValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}%><%}%>
    <%for(i=0;i<entity.pks.size();i++){EgField curField=(EgField)entity.pks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
      ps.setString(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
      ps.setTimestamp(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
      ps.setTime(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
      ps.setDate(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
      if(value.<%=curField.fieldName%> != null) ps.setInt(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>.intValue()); else ps.setNull(<%=i+1+nopks.size()%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
      if(value.<%=curField.fieldName%> != null) ps.setLong(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>.longValue()); else ps.setNull(<%=i+1+nopks.size()%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
      if(value.<%=curField.fieldName%> != null) ps.setFloat(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>.floatValue()); else ps.setNull(<%=i+1+nopks.size()%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
      if(value.<%=curField.fieldName%> != null) ps.setDouble(<%=i+1+nopks.size()%>, value.<%=curField.fieldName%>.doubleValue()); else ps.setNull(<%=i+1+nopks.size()%>, Types.NULL);<%}%><%}%>

      ps.executeUpdate();
      value.modified = false;
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.update]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }    
    return true;
  }

  public static boolean select(<%=entity.ejbName%> value)
  {
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.select]: Cannot select <%=entity.ejbName%>: required primary key field(s) missing.");
      return false;
    }
    
    Connection connection = null; 
    PreparedStatement ps = null; 
    ResultSet rs = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.select]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "SELECT <%=entity.colNameString(nopks, ", ", "")%> FROM <%=entity.tableName%> WHERE <%=entity.colNameString(entity.pks, "=? AND ", "=?")%>";
    try {
      ps = connection.prepareStatement(sql);
    <%for(i=0;i<entity.pks.size();i++){EgField curField=(EgField)entity.pks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
      ps.setString(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
      ps.setTimestamp(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
      ps.setTime(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
      ps.setDate(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
      if(value.<%=curField.fieldName%> != null) ps.setInt(<%=i+1%>, value.<%=curField.fieldName%>.intValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
      if(value.<%=curField.fieldName%> != null) ps.setLong(<%=i+1%>, value.<%=curField.fieldName%>.longValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
      if(value.<%=curField.fieldName%> != null) ps.setFloat(<%=i+1%>, value.<%=curField.fieldName%>.floatValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
      if(value.<%=curField.fieldName%> != null) ps.setDouble(<%=i+1%>, value.<%=curField.fieldName%>.doubleValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}%><%}%>

      rs = ps.executeQuery();
      
      if(rs.next())
      {
      <%for(i=0;i<nopks.size();i++){EgField curField=(EgField)nopks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
        value.<%=curField.fieldName%> = rs.getString("<%=curField.columnName%>");<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
        value.<%=curField.fieldName%> = rs.getTimestamp("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
        value.<%=curField.fieldName%> = rs.getTime("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
        value.<%=curField.fieldName%> = rs.getDate("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Integer(rs.getInt("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Long(rs.getLong("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Float(rs.getFloat("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Double(rs.getDouble("<%=curField.columnName%>"));<%}%><%}%>
        value.modified = false;
      } else {
        Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.select]: select failed, result set was empty.");
        return false;
      }
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    return true;
  }
    
  public static boolean delete(<%=entity.ejbName%> value)
  {
    if(value == null || value.<%=entity.pkNameString(" == null || value."," == null")%>) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.delete]: Cannot delete <%=entity.ejbName%>: required primary key field(s) missing.");
      return false;
    }
    
    Connection connection = null;
    PreparedStatement ps = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.delete]: Unable to esablish a connection with the database... Error was:\n"); Debug.logWarning(sqle); }
    
    String sql = "DELETE FROM <%=entity.tableName%> WHERE <%=entity.colNameString(entity.pks, "=? AND ", "=?")%>";
    try {
      ps = connection.prepareStatement(sql);
    <%for(i=0;i<entity.pks.size();i++){EgField curField=(EgField)entity.pks.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
      ps.setString(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
      ps.setTimestamp(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
      ps.setTime(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
      ps.setDate(<%=i+1%>, value.<%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
      if(value.<%=curField.fieldName%> != null) ps.setInt(<%=i+1%>, value.<%=curField.fieldName%>.intValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
      if(value.<%=curField.fieldName%> != null) ps.setLong(<%=i+1%>, value.<%=curField.fieldName%>.longValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
      if(value.<%=curField.fieldName%> != null) ps.setFloat(<%=i+1%>, value.<%=curField.fieldName%>.floatValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
      if(value.<%=curField.fieldName%> != null) ps.setDouble(<%=i+1%>, value.<%=curField.fieldName%>.doubleValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}%><%}%>
      ps.executeUpdate();
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.delete]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      Debug.logWarning(sqle);
      return false;
    } finally {
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }    
    return true;
  }

  public static <%=entity.ejbName%> create(<%=entity.primKeyClassNameString()%>)
  {
    if(<%=entity.pkNameString(" == null || "," == null")%>) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.create]: Cannot create <%=entity.ejbName%>: required primary key field(s) missing.");
      return null;
    }
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = new <%=entity.ejbName%>(<%=entity.pkNameString()%>);
    if(insert(<%=GenUtil.lowerFirstChar(entity.ejbName)%>)) return <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
    else return null;    
  }

  public static <%=entity.ejbName%> create(<%=entity.fieldTypeNameString()%>)
  {
    if(<%=entity.pkNameString(" == null || "," == null")%>) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.create]: Cannot create <%=entity.ejbName%>: required primary key field(s) missing.");
      return null;
    }
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = new <%=entity.ejbName%>(<%=entity.fieldNameString()%>);
    if(insert(<%=GenUtil.lowerFirstChar(entity.ejbName)%>)) return <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
    else return null;    
  }

  public static <%=entity.ejbName%> findByPrimaryKey(<%=entity.primKeyClassNameString()%>)
  {
    if(<%=entity.pkNameString(" == null || "," == null")%>) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.findByPrimaryKey]: Cannot findByPrimaryKey <%=entity.ejbName%>: required primary key field(s) missing.");
      return null;
    }
    <%=entity.ejbName%> <%=GenUtil.lowerFirstChar(entity.ejbName)%> = new <%=entity.ejbName%>(<%=entity.pkNameString()%>);
    if(select(<%=GenUtil.lowerFirstChar(entity.ejbName)%>)) return <%=GenUtil.lowerFirstChar(entity.ejbName)%>;
    else return null;    
  }


  /** Finds All <%=entity.ejbName%>s
   *@return      Collection containing the found <%=entity.ejbName%>s
   */
  public static Collection findAll()
  {
    Collection collection = new LinkedList();
    
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.findAll]: Unable to esablish a connection with the database... Error was:\n" + sqle.toString() ); }
    
    String sql = "SELECT <%=entity.colNameString(entity.fields, ", ", "")%> FROM <%=entity.tableName%>";
    try {
      ps = connection.prepareStatement(sql);
      rs = ps.executeQuery();
      
      while(rs.next())
      {        
        <%=entity.ejbName%> value = new <%=entity.ejbName%>();
      <%for(i=0;i<entity.fields.size();i++){EgField curField=(EgField)entity.fields.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
        value.<%=curField.fieldName%> = rs.getString("<%=curField.columnName%>");<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
        value.<%=curField.fieldName%> = rs.getTimestamp("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
        value.<%=curField.fieldName%> = rs.getTime("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
        value.<%=curField.fieldName%> = rs.getDate("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Integer(rs.getInt("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Long(rs.getLong("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Float(rs.getFloat("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Double(rs.getDouble("<%=curField.columnName%>"));<%}%><%}%>

        value.modified = false;
        collection.add(value);
      }
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.findAll]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      sqle.printStackTrace();
      return null;
    } finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    return collection;
  }

<%for(int fi=0;fi<entity.finders.size();fi++){%><%EgFinder finderDesc = (EgFinder)entity.finders.elementAt(fi);%><%Vector nofindby = new Vector(entity.fields); for(int nfind=0;nfind<finderDesc.fields.size();nfind++){ nofindby.removeElement(finderDesc.fields.elementAt(nfind)); }%>
  /** Finds <%=entity.ejbName%>s by the following fields:<%for(int j=0;j<finderDesc.fields.size();j++){%>
   *@param  <%=((EgField)finderDesc.fields.elementAt(j)).fieldName%>                  EgField for the <%=((EgField)finderDesc.fields.elementAt(j)).columnName%> column.<%}%>
   *@return      Collection containing the found <%=entity.ejbName%>s
   */
  public static Collection findBy<%=entity.classNameString(finderDesc.fields,"And","")%>(<%=entity.typeNameString(finderDesc.fields)%>)
  {
    Collection collection = new LinkedList();
    if(<%=entity.nameString(finderDesc.fields, " == null || ", " == null")%>) {
      Debug.logWarning ("ERROR [<%=entity.ejbName%>DAO.findBy<%=entity.classNameString(finderDesc.fields,"And","")%>]: Cannot load <%=entity.ejbName%>DAO: parameter missing.");
      return null;
    }
    
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try { connection = getConnection(); } 
    catch (SQLException sqle) { Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.findBy<%=entity.classNameString(finderDesc.fields,"And","")%>]: Unable to esablish a connection with the database... Error was:\n" + sqle.toString() ); }
    
    String sql = "SELECT <%=entity.colNameString(nofindby, ", ", "")%> FROM <%=entity.tableName%> WHERE <%=entity.colNameString(finderDesc.fields, " like ? AND ", " like ?")%>";
    try {
      ps = connection.prepareStatement(sql);
    <%for(i=0;i<finderDesc.fields.size();i++){EgField curField=(EgField)finderDesc.fields.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
      ps.setString(<%=i+1%>, <%=curField.fieldName%>);<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
      ps.setTimestamp(<%=i+1%>, <%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
      ps.setTime(<%=i+1%>, <%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
      ps.setDate(<%=i+1%>, <%=curField.fieldName%>);<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
      if(<%=curField.fieldName%> != null) ps.setInt(<%=i+1%>, <%=curField.fieldName%>.intValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
      if(<%=curField.fieldName%> != null) ps.setLong(<%=i+1%>, <%=curField.fieldName%>.longValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
      if(<%=curField.fieldName%> != null) ps.setFloat(<%=i+1%>, <%=curField.fieldName%>.floatValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
      if(<%=curField.fieldName%> != null) ps.setDouble(<%=i+1%>, <%=curField.fieldName%>.doubleValue()); else ps.setNull(<%=i+1%>, Types.NULL);<%}%><%}%>
      rs = ps.executeQuery();
      
      while(rs.next())
      {        
        <%=entity.ejbName%> value = new <%=entity.ejbName%>();
      <%for(i=0;i<finderDesc.fields.size();i++){EgField curField=(EgField)finderDesc.fields.elementAt(i);%>
        value.<%=curField.fieldName%> = <%=curField.fieldName%>;<%}%>
      <%for(i=0;i<nofindby.size();i++){EgField curField=(EgField)nofindby.elementAt(i);%><%if(curField.javaType.compareTo("java.lang.String") == 0 || curField.javaType.compareTo("String") == 0){%>
        value.<%=curField.fieldName%> = rs.getString("<%=curField.columnName%>");<%}else if(curField.javaType.indexOf("Timestamp") >= 0){%>
        value.<%=curField.fieldName%> = rs.getTimestamp("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.sql.Time") || curField.javaType.equals("Time")){%>
        value.<%=curField.fieldName%> = rs.getTime("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.sql.Date") || curField.javaType.equals("Date")){%>
        value.<%=curField.fieldName%> = rs.getDate("<%=curField.columnName%>");<%}else if(curField.javaType.equals("java.lang.Integer") || curField.javaType.equals("Integer")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Integer(rs.getInt("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Long") || curField.javaType.equals("Long")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Long(rs.getLong("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Float") || curField.javaType.equals("Float")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Float(rs.getFloat("<%=curField.columnName%>"));<%}else if(curField.javaType.equals("java.lang.Double") || curField.javaType.equals("Double")){%>
        if(rs.getObject("<%=curField.columnName%>") == null) value.<%=curField.fieldName%> = null; else value.<%=curField.fieldName%> = new Double(rs.getDouble("<%=curField.columnName%>"));<%}%><%}%>

        value.modified = false;
        collection.add(value);
      }
    } catch (SQLException sqle) {
      Debug.logWarning("ERROR [<%=entity.ejbName%>DAO.findBy<%=entity.classNameString(finderDesc.fields,"And","")%>]: SQL Exception while executing the following:\n" + sql + "\nError was:\n");
      sqle.printStackTrace();
      return null;
    } finally {
      try { if (rs != null) rs.close(); } catch (SQLException sqle) { }
      try { if (ps != null) ps.close(); } catch (SQLException sqle) { }
      try { if (connection != null) connection.close(); } catch (SQLException sqle) { }
    }
    return collection;
  }<%}%>
}
