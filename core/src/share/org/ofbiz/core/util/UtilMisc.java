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


import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * UtilMisc - Misc Utility Functions
 *
 * @author     <a href="mailto:jonesde@ofbiz.org">David E. Jones</a>
 * @author     <a href="mailto:jaz@jflow.net">Andy Zeneski</a>
 * @created    July 26, 2001
 * @version    1.0
 */
public class UtilMisc {

    /** 
     * Get an iterator from a collection, returning null if collection is null
     * @param col The collection to be turned in to an iterator
     * @return The resulting Iterator
     */
    public static Iterator toIterator(Collection col) {
        if (col == null)
            return null;
        else
            return col.iterator();
    }

    /** 
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static Map toMap(String name1, Object value1) {
        return new UtilMisc.SimpleMap(name1, value1);

        /* Map fields = new HashMap();
         fields.put(name1, value1);
         return fields;*/
    }

    /** 
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static Map toMap(String name1, Object value1, String name2, Object value2) {
        return new UtilMisc.SimpleMap(name1, value1, name2, value2);

        /* Map fields = new HashMap();
         fields.put(name1, value1);
         fields.put(name2, value2);
         return fields;*/
    }

    /** 
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static Map toMap(String name1, Object value1, String name2, Object value2, String name3, Object value3) {
        return new UtilMisc.SimpleMap(name1, value1, name2, value2, name3, value3);

        /* Map fields = new HashMap();
         fields.put(name1, value1);
         fields.put(name2, value2);
         fields.put(name3, value3);
         return fields;*/
    }

    /** 
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static Map toMap(String name1, Object value1, String name2, Object value2, String name3,
        Object value3, String name4, Object value4) {
        return new UtilMisc.SimpleMap(name1, value1, name2, value2, name3, value3, name4, value4);

        /* Map fields = new HashMap();
         fields.put(name1, value1);
         fields.put(name2, value2);
         fields.put(name3, value3);
         fields.put(name4, value4);
         return fields;*/
    }

    /** 
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static Map toMap(String name1, Object value1, String name2, Object value2, String name3, Object value3,
        String name4, Object value4, String name5, Object value5) {
        Map fields = new HashMap();

        fields.put(name1, value1);
        fields.put(name2, value2);
        fields.put(name3, value3);
        fields.put(name4, value4);
        fields.put(name5, value5);
        return fields;
    }

    /** 
     * Create a map from passed nameX, valueX parameters
     * @return The resulting Map
     */
    public static Map toMap(String name1, Object value1, String name2, Object value2, String name3, Object value3,
        String name4, Object value4, String name5, Object value5, String name6, Object value6) {
        Map fields = new HashMap();

        fields.put(name1, value1);
        fields.put(name2, value2);
        fields.put(name3, value3);
        fields.put(name4, value4);
        fields.put(name5, value5);
        fields.put(name6, value6);
        return fields;
    }
    
    /**
     * Sort a List of Maps by specified consistent keys.
     * @param listOfMaps List of Map objects to sort.
     * @param sortKeys List of Map keys to sort by.
     * @return a new List of sorted Maps.
     */
    public static List sortMaps(List listOfMaps, List sortKeys) {
        if (listOfMaps == null || sortKeys == null)
            return null;
        List toSort = new LinkedList(listOfMaps);
        try {
            MapComparator mc = new MapComparator(sortKeys);
            Collections.sort(toSort, mc);
        } catch (Exception e) {
            Debug.logError(e, "Problems sorting list of maps; returning null.");
            return null;
        }
        return toSort;
    }

    /** 
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static List toList(Object obj1) {
        List list = new ArrayList(1);

        list.add(obj1);
        return list;
    }

    /** 
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static List toList(Object obj1, Object obj2) {
        List list = new ArrayList(2);

        list.add(obj1);
        list.add(obj2);
        return list;
    }

    /** 
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static List toList(Object obj1, Object obj2, Object obj3) {
        List list = new ArrayList(3);

        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        return list;
    }

    /** 
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static List toList(Object obj1, Object obj2, Object obj3, Object obj4) {
        List list = new ArrayList(4);

        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        list.add(obj4);
        return list;
    }

    /** 
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static List toList(Object obj1, Object obj2, Object obj3, Object obj4, Object obj5) {
        List list = new ArrayList(5);

        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        list.add(obj4);
        list.add(obj5);
        return list;
    }

    /** 
     * Create a list from passed objX parameters
     * @return The resulting List
     */
    public static List toList(Object obj1, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        List list = new ArrayList(6);

        list.add(obj1);
        list.add(obj2);
        list.add(obj3);
        list.add(obj4);
        list.add(obj5);
        list.add(obj6);
        return list;
    }

    public static List toList(Collection collection) {
        if (collection == null) return null;
        if (collection instanceof List) {
            return (List) collection;
        } else {
            return new ArrayList(collection);
        }
    }

    /** 
     * Create a map from an HttpServletRequest object
     * @return The resulting Map
     */
    public static Map getParameterMap(HttpServletRequest request) {
        HashMap paramMap = new OrderedMap();
        java.util.Enumeration e = request.getParameterNames();

        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();

            paramMap.put(name, request.getParameter(name));
        }
        return (Map) paramMap;
    }

    /** 
     * Given a request, returns the application name or "root" if deployed on root      * @param request     * @return String     */
    public static String getApplicationName(HttpServletRequest request) {
        String appName = "root";

        if (request.getContextPath().length() > 1) {
            appName = request.getContextPath().substring(1);
        }
        return appName;
    }

    public static StringBuffer getServerRootUrl(HttpServletRequest request) {
        StringBuffer requestUrl = new StringBuffer();

        requestUrl.append(request.getScheme());
        requestUrl.append("://" + request.getServerName());
        if (request.getServerPort() != 80 && request.getServerPort() != 443)
            requestUrl.append(":" + request.getServerPort());
        return requestUrl;
    }

    public static StringBuffer getFullRequestUrl(HttpServletRequest request) {
        StringBuffer requestUrl = getServerRootUrl(request);

        requestUrl.append(request.getRequestURI());
        if (request.getQueryString() != null) {
            requestUrl.append("?" + request.getQueryString());
        }
        return requestUrl;
    }


    /** This is meant to be very quick to create and use for small sized maps, perfect for how we usually use UtilMisc.toMap */
    protected static class SimpleMap implements Map, java.io.Serializable {
        protected Map realMapIfNeeded = null;

        int sizeValue;
        String name1 = null;
        String name2 = null;
        String name3 = null;
        String name4 = null;
        Object value1 = null;
        Object value2 = null;
        Object value3 = null;
        Object value4 = null;

        public SimpleMap(String name1, Object value1) {
            sizeValue = 1;
            this.name1 = name1;
            this.value1 = value1;
        }

        public SimpleMap(String name1, Object value1, String name2, Object value2) {
            sizeValue = 2;
            this.name1 = name1;
            this.value1 = value1;
            this.name2 = name2;
            this.value2 = value2;
        }

        public SimpleMap(String name1, Object value1, String name2, Object value2, String name3, Object value3) {
            sizeValue = 3;
            this.name1 = name1;
            this.value1 = value1;
            this.name2 = name2;
            this.value2 = value2;
            this.name3 = name3;
            this.value3 = value3;
        }

        public SimpleMap(String name1, Object value1, String name2, Object value2, String name3, Object value3, String name4, Object value4) {
            sizeValue = 4;
            this.name1 = name1;
            this.value1 = value1;
            this.name2 = name2;
            this.value2 = value2;
            this.name3 = name3;
            this.value3 = value3;
            this.name4 = name4;
            this.value4 = value4;
        }

        protected void makeRealMap() {
            realMapIfNeeded = new HashMap();
            if (name1 != null) realMapIfNeeded.put(name1, value1);
            if (name2 != null) realMapIfNeeded.put(name2, value2);
            if (name3 != null) realMapIfNeeded.put(name3, value3);
            if (name4 != null) realMapIfNeeded.put(name4, value4);
        }

        public void clear() {
            if (realMapIfNeeded != null) {
                realMapIfNeeded.clear();
            } else {
                realMapIfNeeded = new HashMap();
                name1 = null;
                name2 = null;
                name3 = null;
                name4 = null;
                value1 = null;
                value2 = null;
                value3 = null;
                value4 = null;
            }
        }

        public boolean containsKey(Object obj) {
            if (realMapIfNeeded != null) {
                return realMapIfNeeded.containsKey(obj);
            } else {
                if (name1 != null && name1.equals(obj)) return true;
                if (name2 != null && name2.equals(obj)) return true;
                if (name3 != null && name3.equals(obj)) return true;
                if (name4 != null && name4.equals(obj)) return true;
                return false;
            }
        }

        public boolean containsValue(Object obj) {
            if (realMapIfNeeded != null) {
                return realMapIfNeeded.containsValue(obj);
            } else {
                if (value1 != null && value1.equals(obj)) return true;
                if (value2 != null && value2.equals(obj)) return true;
                if (value3 != null && value3.equals(obj)) return true;
                if (value4 != null && value4.equals(obj)) return true;
                return false;
            }
        }

        public java.util.Set entrySet() {
            if (realMapIfNeeded != null) {
                return realMapIfNeeded.entrySet();
            } else {
                this.makeRealMap();
                return realMapIfNeeded.entrySet();
            }
        }

        public Object get(Object obj) {
            if (realMapIfNeeded != null) {
                return realMapIfNeeded.get(obj);
            } else {
                if (name1 != null && name1.equals(obj)) return value1;
                if (name2 != null && name2.equals(obj)) return value2;
                if (name3 != null && name3.equals(obj)) return value3;
                if (name4 != null && name4.equals(obj)) return value4;
                return null;
            }
        }

        public boolean isEmpty() {
            if (realMapIfNeeded != null) {
                return realMapIfNeeded.isEmpty();
            } else {
                if (this.sizeValue == 0) return true;
                return false;
            }
        }

        public java.util.Set keySet() {
            if (realMapIfNeeded != null) {
                return realMapIfNeeded.keySet();
            } else {
                this.makeRealMap();
                return realMapIfNeeded.keySet();
            }
        }

        public Object put(Object obj, Object obj1) {
            if (realMapIfNeeded != null) {
                return realMapIfNeeded.put(obj, obj1);
            } else {
                this.makeRealMap();
                return realMapIfNeeded.put(obj, obj1);
            }
        }

        public void putAll(java.util.Map map) {
            if (realMapIfNeeded != null) {
                realMapIfNeeded.putAll(map);
            } else {
                this.makeRealMap();
                realMapIfNeeded.putAll(map);
            }
        }

        public Object remove(Object obj) {
            if (realMapIfNeeded != null) {
                return realMapIfNeeded.remove(obj);
            } else {
                this.makeRealMap();
                return realMapIfNeeded.remove(obj);
            }
        }

        public int size() {
            if (realMapIfNeeded != null) {
                return realMapIfNeeded.size();
            } else {
                return this.sizeValue;
            }
        }

        public java.util.Collection values() {
            if (realMapIfNeeded != null) {
                return realMapIfNeeded.values();
            } else {
                this.makeRealMap();
                return realMapIfNeeded.values();
            }
        }

        public String toString() {
            StringBuffer outString = new StringBuffer("{");

            if (name1 != null) {
                outString.append('{');
                outString.append(name1);
                outString.append(',');
                outString.append(value1);
                outString.append('}');
            }
            if (name2 != null) {
                if (outString.length() > 1) outString.append(',');
                outString.append('{');
                outString.append(name2);
                outString.append(',');
                outString.append(value2);
                outString.append('}');
            }
            if (name3 != null) {
                if (outString.length() > 1) outString.append(',');
                outString.append('{');
                outString.append(name3);
                outString.append(',');
                outString.append(value3);
                outString.append('}');
            }
            if (name4 != null) {
                if (outString.length() > 1) outString.append(',');
                outString.append('{');
                outString.append(name4);
                outString.append(',');
                outString.append(value4);
                outString.append('}');
            }
            outString.append('}');
            return outString.toString();
        }
    }
}
