package org.ofbiz.entity.jdbc;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class CursorResultSet extends AbstractCursorHandler {
    protected ResultSet rs;
    protected Statement stmt;
    protected String query;

    protected CursorResultSet(Statement stmt, String cursorName, int fetchSize) throws SQLException {
        super(cursorName, fetchSize);
        this.stmt = stmt;
        query = "FETCH FORWARD " + fetchSize + " IN " + cursorName;
System.err.println("executing page fetch(1)");
        rs = stmt.executeQuery(query);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("close".equals(method.getName())) {
            close();
            return null;
        } else if ("next".equals(method.getName())) {
            return next() ? Boolean.TRUE : Boolean.FALSE;
        }
        return super.invoke(rs, proxy, method, args);
    }

    protected boolean next() throws SQLException {
        if (rs.next()) return true;
System.err.println("executing page fetch(2)");
        rs = stmt.executeQuery(query);
        return rs.next();
    }

    protected void close() throws SQLException {
        stmt.executeUpdate("CLOSE " + cursorName);
        rs.close();
    }

    public static ResultSet newCursorResultSet(Statement stmt, String cursorName, int fetchSize) throws SQLException, Exception {
        return (ResultSet) newHandler(new CursorResultSet(stmt, cursorName, fetchSize), ResultSet.class);
    }
}
