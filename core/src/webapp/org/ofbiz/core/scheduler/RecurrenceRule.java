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
    
    // byXXX constants
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
    
    // Frequency constants
    public static final int SECONDLY = 1;
    public static final int MINUTELY = 2;
    public static final int HOURLY = 3;
    public static final int DAILY = 4;
    public static final int WEEKLY = 5;
    public static final int MONTHLY = 6;
    public static final int YEARLY = 7;
    
    // GenericValue object
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
    
    /** Returns the frequency name of the recurrence. */
    public String getFrequencyName() {
        return rule.getString("frequency").toUpperCase();
    }
    
    /** Returns the frequency of this recurrence */
    public int getFrequency() {
        String freq = rule.getString("frequency");
        if ( freq == null )
            return 0;
        if ( freq.equalsIgnoreCase("SECONDLY") )
            return SECONDLY;
        if ( freq.equalsIgnoreCase("MINUTELY") )
            return MINUTELY;
        if ( freq.equalsIgnoreCase("HOURLY") )
            return HOURLY;
        if ( freq.equalsIgnoreCase("DAILY") )
            return DAILY;
        if ( freq.equalsIgnoreCase("WEEKLY") )
            return WEEKLY;
        if ( freq.equalsIgnoreCase("MONTHLY") )
            return MONTHLY;
        if ( freq.equalsIgnoreCase("YEARLY") )
            return YEARLY;
        return 0;
    }
    
    /** Returns the interval of the frequency. */
    public int getInterval() {
        if ( rule.get("interval") == null )
            return 1;
        return rule.getInteger("interval").intValue();
    }
    
    /** Returns the next recurrence of this rule.
     *@param startTime The time this recurrence first began.
     *@param fromTime The time to base the next recurrence on.
     *@param currentCount The total number of times the recurrence has run.
     *@returns The next recurrence as a long.
     */
    public long next(long startTime, long fromTime, int currentCount)  {
        // Set up the values
        if ( startTime == 0 )
            startTime = RecurrenceUtil.now();
        if ( fromTime == 0 )
            fromTime = startTime;
        if ( currentCount == 0 )
            currentCount = 1;
        
        // Test the end time of the recurrence.
        if ( getEndTime() >= RecurrenceUtil.now() )
            return 0;
        // Test the recurrence limit.
        if ( getCount() >= currentCount )
            return 0;
        
        return 0;
    }
    
    /** Tests the date to see if it falls within the rules
     *@param The date object to test
     *@returns True if the date is within the rules
     */
    public boolean isValid(Date startDate, Date date) {
        return false;
    }
    
    /** Tests the date to see if it falls within the rules
     *@param The date object to test
     *@returns True if the date is within the rules
     */
    public boolean isValid(long startTime, long dateTime) {
        return false;
    }
    
    /** Gets the next frequency/interval recurrence from specified time */
    private Date getNextFreq(long startTime, long fromTime) {
        // Build a Calendar object
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(startTime));
        
        long nextStartTime = startTime;
        while ( nextStartTime < fromTime ) {
            switch(getFrequency()) {
                case SECONDLY:
                    cal.add(Calendar.SECOND,getInterval());
                    break;
                case MINUTELY:
                    cal.add(Calendar.MINUTE,getInterval());
                    break;
                case HOURLY:
                    cal.add(Calendar.HOUR,getInterval());
                    break;
                case DAILY:
                    cal.add(Calendar.DATE,getInterval());
                    break;
                case WEEKLY:
                    cal.add(Calendar.DATE,(7*getInterval()));
                    break;
                case MONTHLY:
                    cal.add(Calendar.MONTH,getInterval());
                    break;
                case YEARLY:
                    cal.add(Calendar.YEAR,getInterval());
                    break;
                default:
                    return null; // should never happen
            }
            nextStartTime = cal.getTime().getTime();
        }
        return new Date(nextStartTime);
    }
    
    /** Checks to see if a date is valid by the byXXX rules */
    private boolean validByRule(Date date) {
        // Build a Calendar object
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        // Test each byXXX rule.
        if ( bySecondList != null && bySecondList.size() > 0 ) {
            if ( !bySecondList.contains(new Integer(cal.get(Calendar.SECOND))) )
                return false;
        }
        if ( byMinuteList != null && byMinuteList.size() > 0 ) {
            if ( !byMinuteList.contains(new Integer(cal.get(Calendar.MINUTE))) )
                return false;
        }
        if ( byHourList != null && byHourList.size() > 0 ) {
            if ( !byHourList.contains(new Integer(cal.get(Calendar.HOUR))) )
                return false;
        }
        if ( byDayList != null && byDayList.size() > 0 ) {
            Iterator iter = byDayList.iterator();
            boolean foundDay = false;
            while ( iter.hasNext() && !foundDay ) {
                String dayRule = (String) iter.next();
                String dayString = getDailyString(dayRule);
                if ( cal.DAY_OF_WEEK == getCalendarDay(dayString) ) {
                    if ( (hasNumber(dayRule)) && (getFrequency() == MONTHLY || getFrequency() == YEARLY) ) {
                        int modifier = getDailyNumber(dayRule);
                        if ( modifier == 0 )
                            foundDay = true;
                        
                        if ( getFrequency() == MONTHLY ) {
                            // figure if we are the nth xDAY if this month
                            int currentPos = cal.get(Calendar.WEEK_OF_MONTH);
                            int dayPosCalc = cal.get(Calendar.DAY_OF_MONTH) - ((currentPos - 1) * 7);
                            if ( dayPosCalc < 1 )
                                currentPos--;
                            if ( modifier > 0 ) {
                                if ( currentPos == modifier )
                                    foundDay = true;
                            }
                            else if ( modifier < 0 ) {
                                int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                                int firstDay = dayPosCalc > 0 ? dayPosCalc : dayPosCalc + 7;
                                int totalDay = ((maxDay - firstDay) / 7 ) + 1;
                                int thisDiff = (currentPos - totalDay) - 1;
                                if ( thisDiff == modifier )
                                    foundDay = true;
                            }
                        }
                        else if ( getFrequency() == YEARLY ) {
                            // figure if we are the nth xDAY if this year
                            int currentPos = cal.get(Calendar.WEEK_OF_YEAR);
                            int dayPosCalc = cal.get(Calendar.DAY_OF_YEAR) - ((currentPos - 1) * 7);
                            if ( dayPosCalc < 1 )
                                currentPos--;
                            if ( modifier > 0 ) {
                                if ( currentPos == modifier )
                                    foundDay = true;
                            }
                            else if ( modifier < 0 ) {
                                int maxDay = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
                                int firstDay = dayPosCalc > 0 ? dayPosCalc : dayPosCalc + 7;
                                int totalDay = ((maxDay - firstDay) / 7 ) + 1;
                                int thisDiff = (currentPos - totalDay) - 1;
                                if ( thisDiff == modifier )
                                    foundDay = true;
                            }
                        }
                    }
                    else {
                        // we are a DOW only rule
                        foundDay = true;
                    }
                }
            }
            if ( !foundDay )
                return false;
        }
        if ( byMonthDayList != null && byMonthDayList.size() > 0 ) {
            Iterator iter = byMonthDayList.iterator();
            boolean foundDay = false;
            while ( iter.hasNext() && !foundDay ) {
                int day = 0;
                String dayStr = (String) iter.next();
                try {
                    day = Integer.parseInt(dayStr);
                }
                catch ( NumberFormatException nfe ) { }
                int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                if ( day > 0 && day == currentDay )
                    foundDay = true;
                if ( day < 0  && day == ((currentDay - maxDay) - 1) ) 
                    foundDay = true;
            }
            if ( !foundDay )
                return false;
        }
        if ( byYearDayList != null && byYearDayList.size() > 0 ) {
            Iterator iter = byYearDayList.iterator();
            boolean foundDay = false;
            while ( iter.hasNext() && !foundDay ) {
                int day = 0;
                String dayStr = (String) iter.next();
                try {
                    day = Integer.parseInt(dayStr);
                }
                catch ( NumberFormatException nfe ) { }
                int maxDay = cal.getActualMaximum(Calendar.DAY_OF_YEAR);
                int currentDay = cal.get(Calendar.DAY_OF_YEAR);
                if ( day > 0 && day == currentDay )
                    foundDay = true;
                if ( day < 0  && day == ((currentDay - maxDay) - 1) ) 
                    foundDay = true;
            }
            if ( !foundDay )
                return false;
        }
        if ( byWeekNoList != null && byWeekNoList.size() > 0 ) {
            Iterator iter = byWeekNoList.iterator();
            boolean foundWeek = false;
            while ( iter.hasNext() && !foundWeek ) {
                int week = 0;
                String weekStr = (String) iter.next();
                try {
                    week = Integer.parseInt(weekStr);
                }
                catch ( NumberFormatException nfe ) { }
                int maxWeek = cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
                int currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
                if ( week > 0 && week == currentWeek )
                    foundWeek = true;
                if ( week < 0 && week == ((currentWeek - maxWeek) - 1) )
                    foundWeek = true;
            }
            if ( !foundWeek )
                return false;
        }
        if ( byMonthList != null && byMonthList.size() > 0 ) {            
            Iterator iter = byMonthList.iterator();
            boolean foundMonth = false; 
            while ( iter.hasNext() && !foundMonth ) {
                int month = 0;
                String monthStr = (String) iter.next();
                try {
                    month = Integer.parseInt(monthStr);
                }
                catch ( NumberFormatException nfe ) { }
                if ( month == cal.get(Calendar.MONTH) )
                    foundMonth = true;                    
            }
            if ( !foundMonth )                
                return false;
        }
        
        return true;
    }
    
    /** Tests a string for the contents of a number at the beginning */
    private boolean hasNumber(String str) {
        String list[] = {"+","-","1","2","3","4","5","6","7","8","9","0"};
        List numberList = Arrays.asList(list);
        String firstChar = str.substring(0,0);
        if ( numberList.contains(firstChar) )
            return true;
        return false;
    }
    
    /** Gets the numeric value of the number at the beginning of the string */
    private int getDailyNumber(String str) {
        int number = 0;
        StringBuffer numberBuf = new StringBuffer();
        for ( int i = 0; i < str.length(); i++ ) {
            String thisChar = str.substring(i,i);
            if ( hasNumber(thisChar) )
                numberBuf.append(thisChar);
        }
        String numberStr = numberBuf.toString();
        if ( numberStr.length() > 0 && (numberStr.length() > 1 || (numberStr.charAt(0) != '+' && numberStr.charAt(0) != '-' )) ) {
            try {
                number = Integer.parseInt(numberStr);
            }
            catch ( NumberFormatException nfe ) { }
        }
        return number;
    }
    
    /** Gets the string part of the combined number+string */
    private String getDailyString(String str) {
        StringBuffer sBuf = new StringBuffer();
        for ( int i = 0; i < str.length(); i++ ) {
            String thisChar = str.substring(i,i);
            if ( !hasNumber(thisChar) )
                sBuf.append(thisChar);
        }
        return sBuf.toString();
    }
    
    /** Returns the Calendar day of the rule day string */
    private int getCalendarDay(String day) {
        if ( day.equalsIgnoreCase("MO") )
            return Calendar.MONDAY;
        if ( day.equalsIgnoreCase("TU") )
            return Calendar.TUESDAY;
        if ( day.equalsIgnoreCase("WE") )
            return Calendar.WEDNESDAY;
        if ( day.equalsIgnoreCase("TH") )
            return Calendar.THURSDAY;
        if ( day.equalsIgnoreCase("FR") )
            return Calendar.FRIDAY;
        if ( day.equalsIgnoreCase("SA") )
            return Calendar.SATURDAY;
        if ( day.equalsIgnoreCase("SU") )
            return Calendar.SUNDAY;
        return 0;
    }
    
}
