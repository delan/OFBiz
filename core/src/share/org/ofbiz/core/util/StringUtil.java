/*
 * $Id$
 */

package org.ofbiz.core.util;

import java.text.*;
import java.util.*;

/**
 * <p><b>Title:</b> String Utility
 * <p><b>Description:</b> Misc String Utility Functions
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
 * @author  Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on November 8, 2001
 */
public class StringUtil {
    
    /** Creates a string seperated by delimimiter from a List of strings
     *@param list a list of strings to join
     *@param delim the delimiter character(s) to use. (null value will join with no delimiter)
     *@return a String of all values in the list seperated by the delimiter
     */
    public static String join(List list, String delim) {
        if ( list == null || list.size() < 1 )
            return null;
        StringBuffer buf = new StringBuffer();
        Iterator i = list.iterator();
        while ( i.hasNext() ) {
            buf.append((String)i.next());
            if ( i.hasNext() )
                buf.append(delim);
        }
        return buf.toString();
    }
    
    /** Splits a String on a delimiter into a List of Strings.
     *@param str the String to split
     *@param delim the delimiter character(s) to join on (null will split on whitespace)
     *@return a list of Strings
     */
    public static List split(String str, String delim) {
        List splitList = null;
        StringTokenizer st = null;
                
        if ( str == null )
            return splitList;
        
        if ( delim != null )
            st = new StringTokenizer(str,delim);
        else
            st = new StringTokenizer(str);
        
        if ( st != null && st.hasMoreTokens() ) {
            splitList = new ArrayList();
            
            while ( st.hasMoreTokens() )
                splitList.add(st.nextToken());
        }
        return splitList;
    }
}
