/**
 * $Id$
 */

package org.ofbiz.core.util;

/**
 * <p><b>Title:</b> ObjectType
 * <p><b>Description:</b> None
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
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
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@created    December 3, 2001
 *@version    1.0
 */
public class ObjectType {
    
    /** Loads a class with the current thread's context classloader 
     * @param className The name of the class to load
     */
    public static Class loadClass(String className) throws ClassNotFoundException {
        ClassLoader loader = null;
        Class c = null;
        try {
            loader = Thread.currentThread().getContextClassLoader();
            c = loader.loadClass(className);
        }
        catch ( Exception e ) {
            c = Class.forName(className);
        }            
        return c;
    }
    
    /** Returns an instance of the specified class
     * @param className Name of the class to instantiate
     */
    public static Object getInstance(String className) throws ClassNotFoundException,
    InstantiationException, IllegalAccessException {
        Class c = loadClass(className);
        Object o = c.newInstance();
        return o;
    }
    
    /** Tests if an object properly implements the specified interface
     * @param obj Object to test
     * @param interfaceName Name of the interface to test against
     */
    public static boolean interfaceOf(Object obj, String interfaceName) throws ClassNotFoundException {
        Class interfaceClass = loadClass(interfaceName);
        return interfaceOf(obj,interfaceClass);
    }

        /** Tests if an object properly implements the specified interface
     * @param obj Object to test
     * @param interfaceObject to test against
     */
    public static boolean interfaceOf(Object obj, Object interfaceObject) {
        Class interfaceClass = interfaceObject.getClass();
        return interfaceOf(obj,interfaceClass);
    }
    
    /** Tests if an object properly implements the specified interface
     * @param obj Object to test
     * @param interfaceClass Class to test against
     */   
    public static boolean interfaceOf(Object obj, Class interfaceClass) {
        Class objectClass = obj.getClass();
        while ( objectClass != null ) {
            Class[] ifaces = objectClass.getInterfaces();
            for ( int i = 0; i < ifaces.length; i++ ) {
                if ( ifaces[i] == interfaceClass ) return true;
            }
            objectClass = objectClass.getSuperclass();
        }
        return false;
    }

    /** Tests if an object is an instance of or a sub-class of the parent
     * @param obj Object to test
     * @param parentName Name of the parent class to test against
     */    
    public static boolean isOrSubOf(Object obj, String parentName) throws ClassNotFoundException {
        Class parentClass = loadClass(parentName);
        return isOrSubOf(obj,parentClass);
    }

    /** Tests if an object is an instance of or a sub-class of the parent
     * @param obj Object to test
     * @param parentObject Object to test against
     */       
    public static boolean isOrSubOf(Object obj, Object parentObject) {
        Class parentClass = parentObject.getClass();
        return isOrSubOf(obj,parentClass);
    }

    /** Tests if an object is an instance of or a sub-class of the parent
     * @param obj Object to test
     * @param parentClass Class to test against
     */        
    public static boolean isOrSubOf(Object obj, Class parentClass) {
        Class objectClass = obj.getClass();
        while ( objectClass != null ) {
            if ( objectClass == parentClass ) return true;
            objectClass = objectClass.getSuperclass();
        }
        return false;
    }
        
    /** Tests if an object is an instance of a sub-class of or properly implements an interface
     * @param obj Object to test
     * @param typeName Name of the class to test against
     */
    public static boolean instanceOf(Object obj, String typeName) throws ClassNotFoundException {
        Class typeClass = loadClass(typeName);
        return instanceOf(obj,typeClass);
    }

    /** Tests if an object is an instance of a sub-class of or properly implements an interface
     * @param obj Object to test
     * @param typeObject Object to test against
     */    
    public static boolean instanceOf(Object obj, Object typeObject) {
        Class typeClass = typeObject.getClass();
        return instanceOf(obj,typeClass);
    }

    /** Tests if an object is an instance of a sub-class of or properly implements an interface
     * @param obj Object to test
     * @param typeClass Class to test against
     */    
    public static boolean instanceOf(Object obj, Class typeClass) {
        if (obj == null) return true;
        Class objectClass = obj.getClass();
        if ( typeClass.isInterface() )
            return interfaceOf(obj,typeClass);
        else
            return isOrSubOf(obj,typeClass);
    }
}
