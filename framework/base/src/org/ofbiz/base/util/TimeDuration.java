/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.base.util;

import java.io.Serializable;
import java.util.Calendar;

/** A representation of a period of time. */
@SuppressWarnings("serial")
public class TimeDuration implements Serializable {
    /** A <code>TimeDuration</code> instance that represents a zero time duration. */
    public static final TimeDuration ZeroTimeDuration = new NullDuration();

    protected int millis = 0;
    protected int seconds = 0;
    protected int minutes = 0;
    protected int hours = 0;
    protected int days = 0;
    protected int months = 0;
    protected int years = 0;
    protected TimeDuration() {}

    /**
     * @param years The number of years in this duration
     * @param months The number of months in this duration
     * @param days The number of days in this duration
     * @param hours The number of hours in this duration
     * @param minutes The number of minutes in this duration
     * @param seconds The number of years in this duration
     * @param millis The number of milliseconds in this duration
     */
    public TimeDuration(int years, int months, int days, int hours, int minutes, int seconds, int millis) {
        this.millis = millis;
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
        this.days = days;
        this.months = months;
        this.years = years;
    }

    /** Elapsed time constructor. The time duration will be computed from the
     * two <code>Calendar</code> instances.
     * @param cal1
     * @param cal2
     */
    public TimeDuration(Calendar cal1, Calendar cal2) {
        this.set(cal1, cal2);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        try {
            TimeDuration that = (TimeDuration) obj;
            return this.years == that.years && this.months == that.months && this.days == that.days
            && this.hours == that.hours && this.minutes == that.minutes && this.seconds == that.seconds
            && this.millis == that.millis;
        } catch (Exception e) {}
        return false;
    }

    public String toString() {
        return this.years + ":" + this.months + ":" + this.days + ":" + this.hours + ":" + this.minutes + ":" + this.seconds + ":" + this.millis;
    }

    /** Returns the milliseconds in this time duration. */
    public int millis() {
        return this.millis;
    }

    /** Returns the seconds in this time duration. */
    public int seconds() {
        return this.seconds;
    }

    /** Returns the minutes in this time duration. */
    public int minutes() {
        return this.minutes;
    }

    /** Returns the hours in this time duration. */
    public int hours() {
        return this.hours;
    }

    /** Returns the days in this time duration. */
    public int days() {
        return this.days;
    }

    /** Returns the months in this time duration. */
    public int months() {
        return this.months;
    }

    /** Returns the years in this time duration. */
    public int years() {
        return this.years;
    }

    /** Add this time duration to a Calendar instance. Returns the original
     * Calendar instance.
     * @param cal
     * @return <code>cal</code>
     */
    public Calendar addToCalendar(Calendar cal) {
        cal.add(Calendar.MILLISECOND, this.millis);
        cal.add(Calendar.SECOND, this.seconds);
        cal.add(Calendar.MINUTE, this.minutes);
        cal.add(Calendar.HOUR, this.hours);
        cal.add(Calendar.DAY_OF_MONTH, this.days);
        cal.add(Calendar.MONTH, this.months);
        cal.add(Calendar.YEAR, this.years);
        return cal;
    }

    /** Subtract this time duration to a Calendar instance. Returns the original
     * Calendar instance.
     * @param cal
     * @return <code>cal</code>
     */
    public Calendar subtractFromCalendar(Calendar cal) {
        cal.add(Calendar.MILLISECOND, -this.millis);
        cal.add(Calendar.SECOND, -this.seconds);
        cal.add(Calendar.MINUTE, -this.minutes);
        cal.add(Calendar.HOUR, -this.hours);
        cal.add(Calendar.DAY_OF_MONTH, -this.days);
        cal.add(Calendar.MONTH, -this.months);
        cal.add(Calendar.YEAR, -this.years);
        return cal;
    }

    
    protected void set(Calendar cal1, Calendar cal2) {
        // set up Calendar objects
        Calendar calStart = null;
        Calendar calEnd = null;
        if (cal1.before(cal2)) {
            calStart = (Calendar) cal1.clone();
            calEnd = (Calendar) cal2.clone();
        } else {
            calStart = (Calendar) cal2.clone();
            calEnd = (Calendar) cal1.clone();
        }
        
        // this will be used to speed up time comparisons
        long targetMillis = calEnd.getTimeInMillis();
        long deltaMillis = targetMillis - calStart.getTimeInMillis();
        
        // shortcut for equal dates
        if (deltaMillis == 0) {
            return;
        }
        
        // compute elapsed years
        long yearMillis = 86400000 * calStart.getMinimum(Calendar.DAY_OF_YEAR);
        float units = deltaMillis / yearMillis;
        this.years = advanceCalendar(calStart, calEnd, (int) units, Calendar.YEAR);
        deltaMillis = targetMillis - calStart.getTimeInMillis();

        // compute elapsed months
        long monthMillis = 86400000 * calStart.getMinimum(Calendar.DAY_OF_MONTH);
        units = deltaMillis / monthMillis;
        this.months = advanceCalendar(calStart, calEnd, (int) units, Calendar.MONTH);
        deltaMillis = targetMillis - calStart.getTimeInMillis();

        // compute elapsed days
        units = deltaMillis / 86400000;
        this.days = advanceCalendar(calStart, calEnd, (int) units, Calendar.DAY_OF_MONTH);
        deltaMillis = targetMillis - calStart.getTimeInMillis();

        // compute elapsed hours
        units = deltaMillis / 3600000;
        this.hours = advanceCalendar(calStart, calEnd, (int) units, Calendar.HOUR);
        deltaMillis = targetMillis - calStart.getTimeInMillis();

        // compute elapsed minutes
        units = deltaMillis / 60000;
        this.minutes = advanceCalendar(calStart, calEnd, (int) units, Calendar.MINUTE);
        deltaMillis = targetMillis - calStart.getTimeInMillis();

        // compute elapsed seconds
        units = deltaMillis / 1000;
        this.seconds = advanceCalendar(calStart, calEnd, (int) units, Calendar.SECOND);
        deltaMillis = targetMillis - calStart.getTimeInMillis();
        
        this.millis = (int) deltaMillis;
    }
    
    protected int advanceCalendar(Calendar start, Calendar end, int units, int type) {
        if (units >= 1) {
            start.add(type, units);
            while (start.after(end)) {
                start.add(type, -1);
                units--;
            }
        }
        return units;
    }

    protected static class NullDuration extends TimeDuration {
        protected NullDuration() {}
        public Calendar addToCalendar(Calendar cal) {
            return cal;
        }
        public Calendar subtractFromCalendar(Calendar cal) {
            return cal;
        }
    }
}
