package org.ofbiz.entity.jdbc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;

public abstract class AbstractCursorHandler implements InvocationHandler {
    protected String cursorName;
    protected int fetchSize;

    protected AbstractCursorHandler(String cursorName, int fetchSize) {
        this.cursorName = cursorName;
        this.fetchSize = fetchSize;
    }

    public void setCursorName(String cursorName) {
        this.cursorName = cursorName;
    }

    public String getCursorName() {
        return cursorName;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    protected Object invoke(Object obj, Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            String str = obj.toString();
            return getClass().getName() + "{" + str + "}";
        }
        return method.invoke(obj, args);
    }

    protected static Object newHandler(InvocationHandler handler, Class implClass) throws IllegalAccessException, IllegalArgumentException, InstantiationException, InvocationTargetException, NoSuchMethodException, SecurityException{
        ClassLoader loader = implClass.getClassLoader();
        if (loader == null) loader = ClassLoader.getSystemClassLoader();
        Class proxyClass = Proxy.getProxyClass(loader, new Class[] {implClass});
        Constructor constructor = proxyClass.getConstructor(new Class[] {InvocationHandler.class});
        return constructor.newInstance(new Object[] {handler});
    }
}
