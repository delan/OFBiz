/*
 * $Id$
 */

package webapp.org.ofbiz.core.scheduler;

import java.util.*;
import org.ofbiz.core.entity.*;
import org.ofbiz.core.util.*;

/**
 *
 * @author  Andy Zeneski (jaz@zsolv.com)
 * @version 1.0
 * Created on November 6, 2001, 11:37 AM
 */
public class RecurrenceRule {
    
    public static final int MIN_SEC = 0;
    public static final int MAX_SEC = 59;
    public static final int MIN_MIN = 0;
    public static final int MAX_MIN = 59;
    public static final int MIN_HR = 0;
    public static final int MAX_HR = 23;
    public static final int MIN_MTH_DAY = -31;
    public static final int MAX_MTH_DAY = 31;
    public static final int MIN_YEAR_DAY = -366;
    public static final int MAX_YEAR_DAY = 366;
    public static final int MIN_WEEK_NBR = -53;
    public static final int MAX_WEEK_NBR = 53;
    public static final int MIN_MTH = 1;
    public static final int MAX_MTH = 12;
    
    GenericValue info;       
    GenericValue rRule;
    GenericValue eRule;
    List rDateList;
    List eDateList;
    
    /** Creates a new RecurrenceRule object from a RecurrenceInfo entity. */
    public RecurrenceRule(GenericValue info) throws RecurrenceRuleException {
        this.info = info;
        init();
    }
    
    /** Initializes the rules for this RecurrenceInfo object. */
    public void init() throws RecurrenceRuleException {
        // Initialize the rules
        try {
            rRule = info.getRelatedOne("RecurrenceRule");
            testRule(rRule);
        }
        catch ( GenericEntityException gee ) {
            throw new RecurrenceRuleException("No recurrence rule associated with this entity.");
        }
        catch ( RuntimeException re ) {
            throw new RecurrenceRuleException("Invalid RecurrenceRule.",rre);
        }
        try {
            eRule = info.getRelatedOne("ExceptionRecurrenceRule");
            testRule(eRule);
        }
        catch ( GenericEntityException gee ) {
            eRule = null;
        }
        catch ( RuntimeException re ) {
            throw new RecurrenceRuleException("Invalid RecurrenceRule.",rre);
        }
        // Initialize the date/time lists.
        rDateList = split(info.getString("recurrenceDateTimes"),",");
        eDateList = split(info.getString("exceptionDateTimes"),",");
        
    }
    
    /** Tests the rule entites for proper configuration */
    private void testRule(GenericValue rule) throws RuntimeException {
        String freq = rule.getString("frequency");
        if ( !checkFreq(freq) )
            throw new RuntimeException("Recurrence FREQUENCY is a required parameter.");
        if ( rule.getDate("until") != null && rule.getInteger("count") > 0 )
            throw new RuntimeException("Recurrence cannot have both UNTIL and COUNT properties.");
        if ( rule.getInteger("interval") < 1 )
            throw new RuntimeException("Recurrence INTERVAL must be a positive integer.");
    }
    
    /** Gets the end time of the recurrence rule or 0 if none. */
    public long getRecurrenceEndTime() {
        return getTime(rRule);
    }
    
    /** Gets the end time of the exception rule or 0 of none, -1 if no exception rule defined. */
    public long getExceptionEndTime() {
        return getTime(eRule);
    }
    
    /** Converts Timestamp to a long. */
    private long getTime(GenericValue rule) {
        if ( rule == null )
            return -1;
        long time = 0;
        java.sql.Timestamp stamp = null;
        stamp = rule.getTimestamp("until");
        if ( stamp != null ) {
            long nanos = (long) stamp.getNanos();
            time = stamp.getTime();
            time += (nanos / 1000000);
        }
        return time;
    }
    
    /** Uses StringTokenizer to split the string. */
    private List split(String str, String delim) {
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
            
    /** Gets the current date/time. */
    public long now() {
        return (new Date()).getTime();
    }
    
    /** Checks for a valid frequency property. */
    private boolean checkFreq(String freq) {
        if ( freq == null )
            return false;
        if ( freq.equalsIgnoreCase("SECONDLY") )
            return true;
        if ( freq.equalsIgnoreCase("MINUTELY") )
            return true;
        if ( freq.equalsIgnoreCase("HOURLY") )
            return true;
        if ( freq.equalsIgnoreCase("DAILY") )
            return true;
        if ( freq.equalsIgnoreCase("WEEKLY") )
            return true;
        if ( freq.equalsIgnoreCase("MONTHLY") )
            return true;
        if ( freq.equalsIgnoreCase("YEARLY") )
            return true;
        return false;
    }
    
    /** Returns the next recurrence from now. */
    public long next() throws RecurrenceRuleException {
        return next(now());
    }
    
    /** Returns the next recurrence from the specified time. */
    public long next(long previous) throws RecurrenceRuleException {
        return 0;
    }
    
}
