/*
 * $Id$
 */

package org.ofbiz.core.scheduler;

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
    public static final int MIN_WEEK_NO = -53;
    public static final int MAX_WEEK_NO = 53;
    public static final int MIN_MTH = 1;
    public static final int MAX_MTH = 12;
    
    // GenericValue objects
    protected GenericValue rule;       
            
    // Parsed byXXX lists
    protected List bySecondList;
    protected List byMinuteList;
    protected List byHourList;
    protected List byDayList;
    protected List byMonthDayList;
    protected List byYearDayList;
    protected List byWeekNoList;
    protected List byMonthList;
    protected List bySetPosList;
    
    /** Creates a new RecurrenceRule object from a RecurrenceInfo entity. */
    public RecurrenceRule(GenericValue rule) throws RecurrenceRuleException {
        this.rule = rule;
        init();
    }
    
    /** Initializes the rules for this RecurrenceInfo object. */
    public void init() throws RecurrenceRuleException {        
        // Check the validity of the rule
        String freq = rule.getString("frequency");
        if ( !checkFreq(freq) )
            throw new RecurrenceRuleException("Recurrence FREQUENCY is a required parameter.");
        if ( rule.get("until") != null && rule.getInteger("count").intValue() > 0 )
            throw new RecurrenceRuleException("Recurrence cannot have both UNTIL and COUNT properties.");
        if ( rule.getInteger("interval").intValue() < 1 )
            throw new RecurrenceRuleException("Recurrence INTERVAL must be a positive integer.");    
                                       
        // Initialize the byXXX lists
        bySecondList = RecurrenceUtil.split(rule.getString("bySecondList"),",");
        byMinuteList = RecurrenceUtil.split(rule.getString("byMinuteList"),",");
        byHourList = RecurrenceUtil.split(rule.getString("byHourList"),",");
        byDayList = RecurrenceUtil.split(rule.getString("byDayList"),",");
        byMonthDayList = RecurrenceUtil.split(rule.getString("byMonthDayList"),",");
        byYearDayList = RecurrenceUtil.split(rule.getString("byYearDayList"),",");
        byWeekNoList = RecurrenceUtil.split(rule.getString("byWeekNoList"),",");
        byMonthList = RecurrenceUtil.split(rule.getString("byMonthList"),",");
        bySetPosList = RecurrenceUtil.split(rule.getString("bySetPosList"),",");        
    }
                                         
    /** Gets the current date/time. */
    private long now() {
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
    
    /** Gets the end time of the recurrence rule or 0 if none. */
    public long getEndTime() {
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
    
    /** Get the number of times this recurrence will run. */
    public int getCount() {
        if ( rule.get("count") != null ) 
            return rule.getInteger("count").intValue();
        return 0;
    }
    
    /** Returns the frequency of the recurrence. */
    public String getFrequency() {
        return rule.getString("frequency").toUpperCase();
    }
    
    /** Returns the interval of the frequency. */
    public int getInterval() {
        if ( rule.get("interval") == null )
            return 1;
        return rule.getInteger("interval").intValue();
    }
    
}
