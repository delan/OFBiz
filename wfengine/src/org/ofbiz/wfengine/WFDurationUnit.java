/* $Id$
 * @(#)WFDurationUnit.java   Sun Aug 12 13:22:41 GMT+02:00 2001
 *
 * <p>Copyright (c) 2001 The Open For Business Project - www.ofbiz.org
 *
 * <p>Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * <p>The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Oliver Wieland (wieland.oliver@t-online.de)
 * @created Sun Aug 12 13:22:41 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 */

package org.ofbiz.wfengine;


import java.io.Serializable;




/**
 * Enumeration WFDurationUnit
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFDurationUnit implements Serializable, Comparable {

	
	/** Index of value 'minute' */
	public static final int MINUTE = 1;	
	/**
	 * 
	 */
	public static final WFDurationUnit Minute = new WFDurationUnit(MINUTE);
	/** String representation of 'minute' */
	
	public static final String _Minute = "Minute";
	
	/** Index of value 'second' */
	public static final int SECOND = 2;	
	/**
	 * 
	 */
	public static final WFDurationUnit Second = new WFDurationUnit(SECOND);
	/** String representation of 'second' */
	
	public static final String _Second = "Second";
	
	/** Index of value 'hour' */
	public static final int HOUR = 3;	
	/**
	 * 
	 */
	public static final WFDurationUnit Hour = new WFDurationUnit(HOUR);
	/** String representation of 'hour' */
	
	public static final String _Hour = "Hour";
	
	/** Index of value 'day' */
	public static final int DAY = 4;	
	/**
	 * 
	 */
	public static final WFDurationUnit Day = new WFDurationUnit(DAY);
	/** String representation of 'day' */
	
	public static final String _Day = "Day";
	
	/** Index of value 'week' */
	public static final int WEEK = 5;	
	/**
	 * 
	 */
	public static final WFDurationUnit Week = new WFDurationUnit(WEEK);
	/** String representation of 'week' */
	
	public static final String _Week = "Week";
	
	/** Index of value 'month' */
	public static final int MONTH = 6;	
	/**
	 * 
	 */
	public static final WFDurationUnit Month = new WFDurationUnit(MONTH);
	/** String representation of 'month' */
	
	public static final String _Month = "Month";
	
	/** Index of value 'year' */
	public static final int YEAR = 7;	
	/**
	 * 
	 */
	public static final WFDurationUnit Year = new WFDurationUnit(YEAR);
	/** String representation of 'year' */
	
	public static final String _Year = "Year";
	
	
	public static final WFDurationUnit NO_VALUE = new WFDurationUnit();
	public static final int NOT_INITIALIZED = 0;
	public static final int MAXIMUM = 7;
	
	private int value;
	private static final WFDurationUnit[] INSTANCES = {
		NO_VALUE,		
		Minute,		
		Second,		
		Hour,		
		Day,		
		Week,		
		Month,		
		Year
	};

	private static final String[] VALUES = {
		"?",
    		_Minute,
    		_Second,
    		_Hour,
    		_Day,
    		_Week,
    		_Month,
    		_Year
	};

	/**
	 * Creates an uninitialized enumeration of WFDurationUnit
	 */
	private WFDurationUnit() {
		this( NOT_INITIALIZED );
	}

	/**
	 * Constructor with predefined value
	 * @param pValue index of instance (equal to INSTANCES[ pValue ])
	 */
	private WFDurationUnit(int pValue) {
		if (pValue < 0 || pValue > MAXIMUM) {
			throw new IllegalArgumentException("Value"+pValue+" is not allowed for enumeration WFDurationUnit, valid range is 0.."+MAXIMUM);
		}
		value = pValue;
	}

	/**
	 * Returns the actual index of the enumeration type
	 * @return actual index of the enumeration type
	 */
	public int getIndex() {
		return value;
	}

	/**
	 * String representation of enumeration 'WFDurationUnit'
	 * @return 'WFDurationUnit: <value as string>'
	 */
	public String toString() {
		return "WFDurationUnit:" + getValue();
	}

	/**
     	 * Returns the verbose value of enumeration WFDurationUnit
     	 * @return String value of current WFDurationUnit instance
     	 */
	public String getValue() {
		if (value > 0 && value <= MAXIMUM) {
			return VALUES[ value ];
		} else {
			throw new IllegalStateException("Value "+
				value+" is not allowed for enumeration WFDurationUnit, valid range is 0.."+MAXIMUM);
		}
	}

	/**
 	 * List of all values as text
	 * @return List of all values 
	 */
	public static String[] getValues() {
		return VALUES;
	}

	/**
 	 * List of all instances
 	 * @return List of all instances
	 */
	public static WFDurationUnit[] getInstances() {
		return INSTANCES;
	}
	
	/**
	 * Gets the corresponding instance of pValue. If pValue is invalid, an
	 * IllegalArgumentException is thrown.
	 * @return corresponding instance (equal to INSTANCES[ pValue ])
	 */
	public static WFDurationUnit getInstance(int pValue) {
		if (pValue > 0 && pValue <= MAXIMUM) {
			return INSTANCES[ pValue ];
		} else {
			throw new IllegalArgumentException("Value "+
				pValue+" is not allowed for enumeration WFDurationUnit, valid range is 0.."+MAXIMUM);
		}
	}
	
	/*
	 * Compare this instance with another object
	 * @param pObject to compare with
	 * @return true, if 1) Object is of type WFDurationUnit and 2) values are equal
	 */
	public boolean equals(Object pObject) {
		return pObject != null &&
		   getClass().getName().equals( pObject.getClass().getName() ) ?
		       value == ( (WFDurationUnit) pObject ).value:
		       false;
	}
	
	/*
	 * Compare this instance with another object
	 * @param pObject to compare with
	 * @return 0, if pObject is equal to 'this', <p>>0 , if this.value > pObject.value <p>else a negative value
	 */
	public int compareTo(Object pObject) {
		return pObject != null &&
		   getClass().getName().equals( pObject.getClass().getName() ) ?
		       value - ( (WFDurationUnit) pObject ).value:
		       -1;
	}
}





