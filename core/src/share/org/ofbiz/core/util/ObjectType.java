/*
 * $Id$
 *
 *  Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *  IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *  CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 *  OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 *  THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.ofbiz.core.util;

import java.text.*;
import java.util.*;

/**
 * Utilities for analyzing and converting Object types in Java - takes advantage of a lot of reflection and other stuff
 *
 *@author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 *@created    December 3, 2001
 *@version    1.0
 */
public class ObjectType {
    public static final String module = ObjectType.class.getName();

    protected static Map classCache = new HashMap();
    
    public static final String LANG_PACKAGE = "java.lang."; // We will test both the raw value and this + raw value
    public static final String SQL_PACKAGE = "java.sql.";   // We will test both the raw value and this + raw value

    public static Map classNameClassMap = new HashMap();
    
    static {
        //setup some commonly used classes...
        classNameClassMap.put("String", java.lang.String.class);
        classNameClassMap.put("java.lang.String", java.lang.String.class);

        classNameClassMap.put("Double", java.lang.Double.class);
        classNameClassMap.put("java.lang.Double", java.lang.Double.class);
        classNameClassMap.put("Float", java.lang.Float.class);
        classNameClassMap.put("java.lang.Float", java.lang.Float.class);
        classNameClassMap.put("Long", java.lang.Long.class);
        classNameClassMap.put("java.lang.Long", java.lang.Long.class);
        classNameClassMap.put("Integer", java.lang.Integer.class);
        classNameClassMap.put("java.lang.Integer", java.lang.Integer.class);
        
        classNameClassMap.put("Timestamp", java.sql.Timestamp.class);
        classNameClassMap.put("java.sql.Timestamp", java.sql.Timestamp.class);
        classNameClassMap.put("Time", java.sql.Time.class);
        classNameClassMap.put("java.sql.Time", java.sql.Time.class);
        classNameClassMap.put("Date", java.sql.Date.class);
        classNameClassMap.put("java.sql.Date", java.sql.Date.class);
        
        classNameClassMap.put("java.util.Date", java.util.Date.class);
        classNameClassMap.put("Collection", java.util.Collection.class);
        classNameClassMap.put("java.util.Collection", java.util.Collection.class);
        classNameClassMap.put("List", java.util.List.class);
        classNameClassMap.put("java.util.List", java.util.List.class);
        classNameClassMap.put("Set", java.util.Set.class);
        classNameClassMap.put("java.util.Set", java.util.Set.class);
        classNameClassMap.put("Map", java.util.Map.class);
        classNameClassMap.put("java.util.Map", java.util.Map.class);
        classNameClassMap.put("HashMap", java.util.HashMap.class);
        classNameClassMap.put("java.util.HashMap", java.util.HashMap.class);
        
        try {
            //note: loadClass is necessary for these since the ObjectType class doesn't know anything about the Entity Engine at compile time
            classNameClassMap.put("GenericValue", loadClass("org.ofbiz.core.entity.GenericValue"));
            classNameClassMap.put("org.ofbiz.core.entity.GenericValue", loadClass("org.ofbiz.core.entity.GenericValue"));
            classNameClassMap.put("GenericPK", loadClass("org.ofbiz.core.entity.GenericPK"));
            classNameClassMap.put("org.ofbiz.core.entity.GenericPK", loadClass("org.ofbiz.core.entity.GenericPK"));
            classNameClassMap.put("GenericEntity", loadClass("org.ofbiz.core.entity.GenericEntity"));
            classNameClassMap.put("org.ofbiz.core.entity.GenericEntity", loadClass("org.ofbiz.core.entity.GenericEntity"));
        } catch (ClassNotFoundException e) {
            Debug.logError(e, "Could not pre-initialize dynamically loaded class: ");
        }
    }
    
    /** Loads a class with the current thread's context classloader
     * @param className The name of the class to load
     */
    public static Class loadClass(String className) throws ClassNotFoundException {
        //small block to speed things up by putting using preloaded classes for common objects, this turns out to help quite a bit...
        Class theClass = (Class) classNameClassMap.get(className);
        if (theClass != null) return theClass;

        return loadClass(className, null);
    }
    
    /** Loads a class with the current thread's context classloader
     * @param className The name of the class to load
     */
    public static Class loadClass(String className, ClassLoader loader) throws ClassNotFoundException {
        //small block to speed things up by putting using preloaded classes for common objects, this turns out to help quite a bit...
        Class theClass = (Class) classNameClassMap.get(className);
        if (theClass != null) return theClass;

        if (loader == null) loader = Thread.currentThread().getContextClassLoader();
        
        try {
            theClass = loader.loadClass(className);
        } catch (Exception e) {
            theClass = (Class) classCache.get(className);
            if (theClass == null) {
                synchronized (ObjectType.class) {
                    theClass = (Class) classCache.get(className);
                    if (theClass == null) {
                        theClass = Class.forName(className);
                        if (theClass != null) {
                            if (Debug.verboseOn()) Debug.logVerbose("Loaded Class: " + theClass.getName(), module);
                            classCache.put(className, theClass);
                        }
                    }
                }
            }
        }
        
        return theClass;
    }

    /** Returns an instance of the specified class
     * @param className Name of the class to instantiate
     */
    public static Object getInstance(String className) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        Class c = loadClass(className);
        Object o = c.newInstance();
        if (Debug.verboseOn()) Debug.logVerbose("Instantiated object: " + o.toString(), module);
        return o;
    }

    /** Tests if an object properly implements the specified interface
     * @param obj Object to test
     * @param interfaceName Name of the interface to test against
     */
    public static boolean interfaceOf(Object obj, String interfaceName) throws ClassNotFoundException {
        Class interfaceClass = loadClass(interfaceName);
        return interfaceOf(obj, interfaceClass);
    }

    /** Tests if an object properly implements the specified interface
     * @param obj Object to test
     * @param interfaceObject to test against
     */
    public static boolean interfaceOf(Object obj, Object interfaceObject) {
        Class interfaceClass = interfaceObject.getClass();
        return interfaceOf(obj, interfaceClass);
    }

    /** Tests if an object properly implements the specified interface
     * @param obj Object to test
     * @param interfaceClass Class to test against
     */
    public static boolean interfaceOf(Object obj, Class interfaceClass) {
        Class objectClass = obj.getClass();
        while (objectClass != null) {
            Class[] ifaces = objectClass.getInterfaces();
            for (int i = 0; i < ifaces.length; i++) {
                if (ifaces[i] == interfaceClass) return true;
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
        return isOrSubOf(obj, parentClass);
    }

    /** Tests if an object is an instance of or a sub-class of the parent
     * @param obj Object to test
     * @param parentObject Object to test against
     */
    public static boolean isOrSubOf(Object obj, Object parentObject) {
        Class parentClass = parentObject.getClass();
        return isOrSubOf(obj, parentClass);
    }

    /** Tests if an object is an instance of or a sub-class of the parent
     * @param obj Object to test
     * @param parentClass Class to test against
     */
    public static boolean isOrSubOf(Object obj, Class parentClass) {
        Class objectClass = obj.getClass();
        while (objectClass != null) {
            if (objectClass == parentClass) return true;
            objectClass = objectClass.getSuperclass();
        }
        return false;
    }

    /** Tests if an object is an instance of a sub-class of or properly implements an interface
     * @param obj Object to test
     * @param typeObject Object to test against
     */
    public static boolean instanceOf(Object obj, Object typeObject) {
        Class typeClass = typeObject.getClass();
        return instanceOf(obj, typeClass);
    }

    /** Tests if an object is an instance of a sub-class of or properly implements an interface
     * @param obj Object to test
     * @param typeObject Object to test against
     */
    public static boolean instanceOf(Object obj, String typeName) {
        return instanceOf(obj, typeName, null);
    }
    /** Tests if an object is an instance of a sub-class of or properly implements an interface
     * @param obj Object to test
     * @param typeObject Object to test against
     */
    public static boolean instanceOf(Object obj, String typeName, ClassLoader loader) {
        Class infoClass = null;
        try {
            infoClass = ObjectType.loadClass(typeName, loader);
        } catch (SecurityException se1) {
            throw new IllegalArgumentException("Problems with classloader: security exception (" +
                    se1.getMessage() + ")");
        } catch (ClassNotFoundException e1) {
            try {
                infoClass = ObjectType.loadClass(LANG_PACKAGE + typeName, loader);
            } catch (SecurityException se2) {
                throw new IllegalArgumentException("Problems with classloader: security exception (" +
                        se2.getMessage() + ")");
            } catch (ClassNotFoundException e2) {
                try {
                    infoClass = ObjectType.loadClass(SQL_PACKAGE + typeName, loader);
                } catch (SecurityException se3) {
                    throw new IllegalArgumentException("Problems with classloader: security exception (" +
                            se3.getMessage() + ")");
                } catch (ClassNotFoundException e3) {
                    throw new IllegalArgumentException("Cannot find and load the class of type: " + typeName +
                            " or of type: " + LANG_PACKAGE + typeName + " or of type: " + SQL_PACKAGE + typeName +
                            ":  (" + e3.getMessage() + ")");
                }
            }
        }

        if (infoClass == null)
            throw new IllegalArgumentException("Illegal type found in info map (could not load class for specified type)");

        return instanceOf(obj, infoClass);
    }

    /** Tests if an object is an instance of a sub-class of or properly implements an interface
     * @param obj Object to test
     * @param typeClass Class to test against
     */
    public static boolean instanceOf(Object obj, Class typeClass) {
        if (obj == null) return true;
        Class objectClass = obj.getClass();
        if (typeClass.isInterface()) {
            return interfaceOf(obj, typeClass);
        } else {
            return isOrSubOf(obj, typeClass);
        }
    }

    /** Converts the passed object to the named simple type; supported types
     * include: String, Double, Float, Long, Integer, Date (java.sql.Date),
     * Time, Timestamp;
     * @param obj Object to convert
     * @param type Name of type to convert to
     * @param format Optional (can be null) format string for Date, Time, Timestamp
     * @param locale Optional (can be null) Locale for formatting and parsing Double, Float, Long, Integer
     */
    public static Object simpleTypeConvert(Object obj, String type, String format, Locale locale) throws GeneralException {
        if (obj == null)
            return null;

        if ("PlainString".equals(type)) {
            return obj.toString();
        }

        String fromType = null;
        if (obj instanceof java.lang.String) {
            fromType = "String";
            String str = (String) obj;
            if ("String".equals(type) || "java.lang.String".equals(type)) {
                return obj;
            }

            if (str.length() == 0) {
                return null;
            }

            if ("Boolean".equals(type) || "java.lang.Boolean".equals(type)) {
                Boolean value = null;
                if (str.equalsIgnoreCase("TRUE"))
                    value = new Boolean(true);
                else
                    value = new Boolean(false);
                return value;
            } else if ("Double".equals(type) || "java.lang.Double".equals(type)) {
                try {
                    NumberFormat nf = null;
                    if (locale == null)
                        nf = NumberFormat.getNumberInstance();
                    else
                        nf = NumberFormat.getNumberInstance(locale);
                    Number tempNum = nf.parse(str);
                    return new Double(tempNum.doubleValue());
                } catch (ParseException e) {
                    throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                }
            } else if ("Float".equals(type) || "java.lang.Float".equals(type)) {
                try {
                    NumberFormat nf = null;
                    if (locale == null)
                        nf = NumberFormat.getNumberInstance();
                    else
                        nf = NumberFormat.getNumberInstance(locale);
                    Number tempNum = nf.parse(str);
                    return new Float(tempNum.floatValue());
                } catch (ParseException e) {
                    throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                }
            } else if ("Long".equals(type) || "java.lang.Long".equals(type)) {
                try {
                    NumberFormat nf = null;
                    if (locale == null)
                        nf = NumberFormat.getNumberInstance();
                    else
                        nf = NumberFormat.getNumberInstance(locale);
                    nf.setMaximumFractionDigits(0);
                    Number tempNum = nf.parse(str);
                    return new Long(tempNum.longValue());
                } catch (ParseException e) {
                    throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                }
            } else if ("Integer".equals(type) || "java.lang.Integer".equals(type)) {
                try {
                    NumberFormat nf = null;
                    if (locale == null)
                        nf = NumberFormat.getNumberInstance();
                    else
                        nf = NumberFormat.getNumberInstance(locale);
                    nf.setMaximumFractionDigits(0);
                    Number tempNum = nf.parse(str);
                    return new Integer(tempNum.intValue());
                } catch (ParseException e) {
                    throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                }
            } else if ("Date".equals(type) || "java.sql.Date".equals(type)) {
                if (format == null || format.length() == 0) {
                    try {
                        return java.sql.Date.valueOf(str);
                    } catch (Exception e) {
                        throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                    }
                } else {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(format);
                        java.util.Date fieldDate = sdf.parse(str);
                        return new java.sql.Date(fieldDate.getTime());
                    } catch (ParseException e) {
                        throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                    }
                }
            } else if ("Time".equals(type) || "java.sql.Time".equals(type)) {
                if (format == null || format.length() == 0) {
                    try {
                        return java.sql.Time.valueOf(str);
                    } catch (Exception e) {
                        throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                    }
                } else {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(format);
                        java.util.Date fieldDate = sdf.parse(str);
                        return new java.sql.Time(fieldDate.getTime());
                    } catch (ParseException e) {
                        throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                    }
                }
            } else if ("Timestamp".equals(type) || "java.sql.Timestamp".equals(type)) {
                if (format == null || format.length() == 0) {
                    try {
                        return java.sql.Timestamp.valueOf(str);
                    } catch (Exception e) {
                        throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                    }
                } else {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(format);
                        java.util.Date fieldDate = sdf.parse(str);
                        return new java.sql.Timestamp(fieldDate.getTime());
                    } catch (ParseException e) {
                        throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                    }
                }
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.lang.Double) {
            fromType = "Double";
            Double dbl = (Double) obj;
            if ("String".equals(type) || "java.lang.String".equals(type)) {
                NumberFormat nf = null;
                if (locale == null)
                    nf = NumberFormat.getNumberInstance();
                else
                    nf = NumberFormat.getNumberInstance(locale);
                return nf.format(dbl.doubleValue());
            } else if ("Double".equals(type) || "java.lang.Double".equals(type)) {
                return obj;
            } else if ("Float".equals(type) || "java.lang.Float".equals(type)) {
                return new Float(dbl.floatValue());
            } else if ("Long".equals(type) || "java.lang.Long".equals(type)) {
                return new Long(Math.round(dbl.doubleValue()));
            } else if ("Integer".equals(type) || "java.lang.Integer".equals(type)) {
                return new Integer((int) Math.round(dbl.doubleValue()));
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.lang.Float) {
            fromType = "Float";
            Float flt = (Float) obj;
            if ("String".equals(type)) {
                NumberFormat nf = null;
                if (locale == null)
                    nf = NumberFormat.getNumberInstance();
                else
                    nf = NumberFormat.getNumberInstance(locale);
                return nf.format(flt.doubleValue());
            } else if ("Double".equals(type)) {
                return new Double(flt.doubleValue());
            } else if ("Float".equals(type)) {
                return obj;
            } else if ("Long".equals(type)) {
                return new Long(Math.round(flt.doubleValue()));
            } else if ("Integer".equals(type)) {
                return new Integer((int) Math.round(flt.doubleValue()));
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.lang.Long) {
            fromType = "Long";
            Long lng = (Long) obj;
            if ("String".equals(type) || "java.lang.String".equals(type)) {
                NumberFormat nf = null;
                if (locale == null)
                    nf = NumberFormat.getNumberInstance();
                else
                    nf = NumberFormat.getNumberInstance(locale);
                return nf.format(lng.longValue());
            } else if ("Double".equals(type) || "java.lang.Double".equals(type)) {
                return new Double(lng.doubleValue());
            } else if ("Float".equals(type) || "java.lang.Float".equals(type)) {
                return new Float(lng.floatValue());
            } else if ("Long".equals(type) || "java.lang.Long".equals(type)) {
                return obj;
            } else if ("Integer".equals(type) || "java.lang.Integer".equals(type)) {
                return new Integer(lng.intValue());
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.lang.Integer) {
            fromType = "Integer";
            Integer intgr = (Integer) obj;
            if ("String".equals(type) || "java.lang.String".equals(type)) {
                NumberFormat nf = null;
                if (locale == null)
                    nf = NumberFormat.getNumberInstance();
                else
                    nf = NumberFormat.getNumberInstance(locale);
                return nf.format(intgr.longValue());
            } else if ("Double".equals(type) || "java.lang.Double".equals(type)) {
                return new Double(intgr.doubleValue());
            } else if ("Float".equals(type) || "java.lang.Float".equals(type)) {
                return new Float(intgr.floatValue());
            } else if ("Long".equals(type) || "java.lang.Long".equals(type)) {
                return new Long(intgr.longValue());
            } else if ("Integer".equals(type) || "java.lang.Integer".equals(type)) {
                return obj;
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.sql.Date) {
            fromType = "Date";
            java.sql.Date dte = (java.sql.Date) obj;
            if ("String".equals(type) || "java.lang.String".equals(type)) {
                if (format == null || format.length() == 0) {
                    return dte.toString();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    return sdf.format(new java.util.Date(dte.getTime()));
                }
            } else if ("Date".equals(type) || "java.sql.Date".equals(type)) {
                return obj;
            } else if ("Time".equals(type) || "java.sql.Time".equals(type)) {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            } else if ("Timestamp".equals(type) || "java.sql.Timestamp".equals(type)) {
                return new java.sql.Timestamp(dte.getTime());
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.sql.Time) {
            fromType = "Time";
            java.sql.Time tme = (java.sql.Time) obj;
            if ("String".equals(type) || "java.lang.String".equals(type)) {
                if (format == null || format.length() == 0) {
                    return tme.toString();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    return sdf.format(new java.util.Date(tme.getTime()));
                }
            } else if ("Date".equals(type) || "java.sql.Date".equals(type)) {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            } else if ("Time".equals(type) || "java.sql.Time".equals(type)) {
                return obj;
            } else if ("Timestamp".equals(type) || "java.sql.Timestamp".equals(type)) {
                return new java.sql.Timestamp(tme.getTime());
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.sql.Timestamp) {
            fromType = "Timestamp";
            java.sql.Timestamp tme = (java.sql.Timestamp) obj;
            if ("String".equals(type) || "java.lang.String".equals(type)) {
                if (format == null || format.length() == 0) {
                    return tme.toString();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    return sdf.format(new java.util.Date(tme.getTime()));
                }
            } else if ("Date".equals(type) || "java.sql.Date".equals(type)) {
                return new java.sql.Date(tme.getTime());
            } else if ("Time".equals(type) || "java.sql.Time".equals(type)) {
                return new java.sql.Time(tme.getTime());
            } else if ("Timestamp".equals(type) || "java.sql.Timestamp".equals(type)) {
                return obj;
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else {
            throw new GeneralException("Conversion from " + obj.getClass().getName() + " to " + type + " not currently supported");
        }
    }
}
