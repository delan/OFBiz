package org.ofbiz.entity.jdbc;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public class CursorConnection extends AbstractCursorHandler {

    public static Connection newCursorConnection(Connection con, String cursorName, int pageSize) throws Exception {
        return (Connection) newHandler(new CursorConnection(con, cursorName, pageSize), Connection.class);
    }

    protected Connection con;

    protected CursorConnection(Connection con, String cursorName, int fetchSize) {
        super(cursorName, fetchSize);
        this.con = con;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("prepareStatement")) {
System.err.println("prepareStatement");
            args[0] = "DECLARE " + cursorName + " CURSOR FOR " + args[0];
            PreparedStatement pstmt = (PreparedStatement) method.invoke(con, args);
            return CursorStatement.newCursorPreparedStatement(pstmt, cursorName, fetchSize);
        } else if (method.getName().equals("createStatement")) {
System.err.println("createStatement");
            Statement stmt = (Statement) method.invoke(con, args);
            return CursorStatement.newCursorStatement(stmt, cursorName, fetchSize);
        }
        return super.invoke(con, proxy, method, args);
    }
}
