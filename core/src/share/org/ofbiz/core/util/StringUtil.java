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

import java.net.*;
import java.text.*;
import java.util.*;

/**
 * Misc String Utility Functions
 *
 * @author     <a href="mailto:jaz@zsolv.com">Andy Zeneski</a>
 * @version    1.0
 * @created    November 8, 2001
 */
public class StringUtil {

    /**
     * Creates a single string from a List of strings seperated by a delimiter.
     * @param list a list of strings to join
     * @param delim the delimiter character(s) to use. (null value will join with no delimiter)
     * @return a String of all values in the list seperated by the delimiter
     */
    public static String join(List list, String delim) {
        if (list == null || list.size() < 1)
            return null;
        StringBuffer buf = new StringBuffer();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            buf.append((String) i.next());
            if (i.hasNext())
                buf.append(delim);
        }
        return buf.toString();
    }

    /**
     * Splits a String on a delimiter into a List of Strings.
     * @param str the String to split
     * @param delim the delimiter character(s) to join on (null will split on whitespace)
     * @return a list of Strings
     */
    public static List split(String str, String delim) {
        List splitList = null;
        StringTokenizer st = null;

        if (str == null)
            return splitList;

        if (delim != null)
            st = new StringTokenizer(str, delim);
        else
            st = new StringTokenizer(str);

        if (st != null && st.hasMoreTokens()) {
            splitList = new ArrayList();

            while (st.hasMoreTokens())
                splitList.add(st.nextToken());
        }
        return splitList;
    }

    /**
     * Encloses each of a List of Strings in quotes.
     * @param list List of String(s) to quote.
     */
    public static List quoteStrList(List list) {
        List tmpList = list;
        list = new ArrayList();
        Iterator i = tmpList.iterator();
        while (i.hasNext()) {
            String str = (String) i.next();
            str = "'" + str + "''";
            list.add(str);
        }
        return list;
    }

    /**
     * Creates a Map from an encoded name/value pair string
     * @param str The string to decode and format
     * @return a Map of name/value pairs
     */
    public static Map strToMap(String str) {
        if (str == null) return null;
        Map decodedMap = new HashMap();
        List elements = split(str, "|");
        Iterator i = elements.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            List e = split(s, "=");
            if (e.size() != 2)
                continue;
            String name = (String) e.get(0);
            String value = (String) e.get(1);
            decodedMap.put(URLDecoder.decode(name), URLDecoder.decode(value));
        }
        return decodedMap;
    }

    /**
     * Creates an encoded String from a Map of name/value pairs (MUST BE STRINGS!)
     * @param map The Map of name/value pairs
     * @return String The encoded String
     */
    public static String mapToStr(Map map) {
        if (map == null) return null;
        StringBuffer buf = new StringBuffer();
        Set keySet = map.keySet();
        Iterator i = keySet.iterator();
        boolean first = true;
        while (i.hasNext()) {
            Object key = i.next();
            Object value = map.get(key);
            if (!(key instanceof String) || !(value instanceof String))
                continue;
            String encodedName = URLEncoder.encode((String) key);
            String encodedValue = URLEncoder.encode((String) value);

            if (first)
                first = false;
            else
                buf.append("|");

            buf.append(encodedName);
            buf.append("=");
            buf.append(encodedValue);
        }
        return buf.toString();
    }

}
