/*
 * $Id$
 */

package org.ofbiz.core.scheduler;

import java.util.*;
import java.text.*;

/**
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
}
    
