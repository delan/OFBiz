/*
 * $Id$
 */

package org.ofbiz.core.scheduler;

import java.util.*;
import java.text.*;

/**
 * <p><b>Title:</b> Recurrence Utilities
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
 * @author  Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on November 6, 2001
 */
public class RecurrenceUtil {
    
    /** Returns a Date object from a String. */
    public static Date parseDate(String dateStr) {
        String formatString = new String();
        if ( dateStr.length() == 16 )
            dateStr = dateStr.substring(0,14);
        if ( dateStr.length() == 15 )
            formatString = "yyyyMMdd'T'hhmmss";
        if ( dateStr.length() == 8 )
            formatString = "yyyyMMdd";
        
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        ParsePosition pos = new ParsePosition(0);
        return formatter.parse(dateStr,pos);
    }
    
    /** Returns a List of parsed date strings. */
    public static List parseDateList(List dateList) {
        List newList = new ArrayList();
        Iterator i = dateList.iterator();
        while ( i.hasNext() )
            newList.add(parseDate((String)i.next()));
        return newList;
    }
    
    /** Returns a String from a Date object */
    public static String formatDate(Date date) {
        String formatString = new String();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if ( cal.isSet(Calendar.MINUTE) )
            formatString = "yyyyMMdd'T'hhmmss";
        else
            formatString = "yyyyMMdd";
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        return formatter.format(date);
    }
    
    /** Returns a Llist of date strings from a List of Date objects */
    public static List formatDateList(List dateList) {
        List newList = new ArrayList();
        Iterator i = dateList.iterator();
        while ( i.hasNext() )
            newList.add(formatDate((Date)i.next()));
        return newList;
    }
    
    /** Creates a string seperated by delim from a List of strings */
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
    
    /** Uses StringTokenizer to split the string. */
    public static List split(String str, String delim) {
        List splitList = null;
        StringTokenizer st;
        
        if ( delim != null )
            st = new StringTokenizer(str,delim);
        else
            st = new StringTokenizer(str);
        if ( st.hasMoreTokens() )
            splitList = new ArrayList();
        
        while ( st.hasMoreTokens() )
            splitList.add(st.nextToken());
        return splitList;
    }
    
    /** Returns the time as of now. */
    public static long now() {
        return (new Date()).getTime();
    }
    
}

