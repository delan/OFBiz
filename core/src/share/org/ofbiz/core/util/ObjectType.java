/**
 * $Id$
 */

package org.ofbiz.core.util;

import java.text.*;
import java.util.Locale;

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
 *@author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
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
        } catch ( Exception e ) {
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
        
        String fromType = null;
        if (obj instanceof java.lang.String) {
            fromType = "String";
            String str = (String) obj;
            if ("String".equals(type))
                return obj;
            
            if (str.length() == 0)
                return null;
            if ("Double".equals(type)) {
                try {
                    NumberFormat nf = null;
                    if (locale == null) nf = NumberFormat.getNumberInstance();
                    else nf = NumberFormat.getNumberInstance(locale);
                    Number tempNum = nf.parse(str);
                    return new Double(tempNum.doubleValue());
                } catch (ParseException e) {
                    throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                }
            } else if ("Float".equals(type)) {
                try {
                    NumberFormat nf = null;
                    if (locale == null) nf = NumberFormat.getNumberInstance();
                    else nf = NumberFormat.getNumberInstance(locale);
                    Number tempNum = nf.parse(str);
                    return new Float(tempNum.floatValue());
                } catch (ParseException e) {
                    throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                }
            } else if ("Long".equals(type)) {
                try {
                    NumberFormat nf = null;
                    if (locale == null) nf = NumberFormat.getNumberInstance();
                    else nf = NumberFormat.getNumberInstance(locale);
                    nf.setMaximumFractionDigits(0);
                    Number tempNum = nf.parse(str);
                    return new Long(tempNum.longValue());
                } catch (ParseException e) {
                    throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                }
            } else if ("Integer".equals(type)) {
                try {
                    NumberFormat nf = null;
                    if (locale == null) nf = NumberFormat.getNumberInstance();
                    else nf = NumberFormat.getNumberInstance(locale);
                    nf.setMaximumFractionDigits(0);
                    Number tempNum = nf.parse(str);
                    return new Integer(tempNum.intValue());
                } catch (ParseException e) {
                    throw new GeneralException("Could not convert " + str + " to " + type + ": ", e);
                }
            } else if ("Date".equals(type)) {
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
            } else if ("Time".equals(type)) {
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
            } else if ("Timestamp".equals(type)) {
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
            if ("String".equals(type)) {
                NumberFormat nf = null;
                if (locale == null) nf = NumberFormat.getNumberInstance();
                else nf = NumberFormat.getNumberInstance(locale);
                return nf.format(dbl.doubleValue());
            } else if ("Double".equals(type)) {
                return obj;
            } else if ("Float".equals(type)) {
                return new Float(dbl.floatValue());
            } else if ("Long".equals(type)) {
                return new Long(Math.round(dbl.doubleValue()));
            } else if ("Integer".equals(type)) {
                return new Integer((int)Math.round(dbl.doubleValue()));
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.lang.Float) {
            fromType = "Float";
            Float flt = (Float) obj;
            if ("String".equals(type)) {
                NumberFormat nf = null;
                if (locale == null) nf = NumberFormat.getNumberInstance();
                else nf = NumberFormat.getNumberInstance(locale);
                return nf.format(flt.doubleValue());
            } else if ("Double".equals(type)) {
                return new Double(flt.doubleValue());
            } else if ("Float".equals(type)) {
                return obj;
            } else if ("Long".equals(type)) {
                return new Long(Math.round(flt.doubleValue()));
            } else if ("Integer".equals(type)) {
                return new Integer((int)Math.round(flt.doubleValue()));
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.lang.Long) {
            fromType = "Long";
            Long lng = (Long) obj;
            if ("String".equals(type)) {
                NumberFormat nf = null;
                if (locale == null) nf = NumberFormat.getNumberInstance();
                else nf = NumberFormat.getNumberInstance(locale);
                return nf.format(lng.longValue());
            } else if ("Double".equals(type)) {
                return new Double(lng.doubleValue());
            } else if ("Float".equals(type)) {
                return new Float(lng.floatValue());
            } else if ("Long".equals(type)) {
                return obj;
            } else if ("Integer".equals(type)) {
                return new Integer(lng.intValue());
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.lang.Integer) {
            fromType = "Integer";
            Integer intgr = (Integer) obj;
            if ("String".equals(type)) {
                NumberFormat nf = null;
                if (locale == null) nf = NumberFormat.getNumberInstance();
                else nf = NumberFormat.getNumberInstance(locale);
                return nf.format(intgr.longValue());
            } else if ("Double".equals(type)) {
                return new Double(intgr.doubleValue());
            } else if ("Float".equals(type)) {
                return new Float(intgr.floatValue());
            } else if ("Long".equals(type)) {
                return new Long(intgr.longValue());
            } else if ("Integer".equals(type)) {
                return obj;
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.sql.Date) {
            fromType = "Date";
            java.sql.Date dte = (java.sql.Date) obj;
            if ("String".equals(type)) {
                if (format == null || format.length() == 0) {
                    return dte.toString();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    return sdf.format(new java.util.Date(dte.getTime()));
                }
            } else if ("Date".equals(type)) {
                return obj;
            } else if ("Time".equals(type)) {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            } else if ("Timestamp".equals(type)) {
                return new java.sql.Timestamp(dte.getTime());
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.sql.Time) {
            fromType = "Time";
            java.sql.Time tme = (java.sql.Time) obj;
            if ("String".equals(type)) {
                if (format == null || format.length() == 0) {
                    return tme.toString();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    return sdf.format(new java.util.Date(tme.getTime()));
                }
            } else if ("Date".equals(type)) {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            } else if ("Time".equals(type)) {
                return obj;
            } else if ("Timestamp".equals(type)) {
                return new java.sql.Timestamp(tme.getTime());
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else if (obj instanceof java.sql.Timestamp) {
            fromType = "Timestamp";
            java.sql.Timestamp tme = (java.sql.Timestamp) obj;
            if ("String".equals(type)) {
                if (format == null || format.length() == 0) {
                    return tme.toString();
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(format);
                    return sdf.format(new java.util.Date(tme.getTime()));
                }
            } else if ("Date".equals(type)) {
                return new java.sql.Date(tme.getTime());
            } else if ("Time".equals(type)) {
                return new java.sql.Time(tme.getTime());
            } else if ("Timestamp".equals(type)) {
                return obj;
            } else {
                throw new GeneralException("Conversion from " + fromType + " to " + type + " not currently supported");
            }
        } else {
            throw new GeneralException("Conversion from " + obj.getClass().getName() + " to " + type + " not currently supported");
        }
    }
}
