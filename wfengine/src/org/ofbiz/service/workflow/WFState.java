/* $Id$
 * @(#)WFState.java   Fri Aug 17 12:18:06 GMT+02:00 2001
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
 * @created Fri Aug 17 12:18:06 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 */

package org.ofbiz.service.workflow;


import java.io.Serializable;




/**
 * Current status of a process
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFState implements Serializable, Comparable {

	
	/** Index of value 'terminated' */
	public static final int TERMINATED = 1;	
	/**
	 * Process finished with a system error
	 */
	public static final WFState Terminated = new WFState(TERMINATED);
	/** String representation of 'terminated' */
	
	public static final String _Terminated = "Terminated";
	
	/** Index of value 'aborted' */
	public static final int ABORTED = 2;	
	/**
	 * Process cancelled by user
	 */
	public static final WFState Aborted = new WFState(ABORTED);
	/** String representation of 'aborted' */
	
	public static final String _Aborted = "Aborted";
	
	/** Index of value 'completed' */
	public static final int COMPLETED = 3;	
	/**
	 * Process finished normally
	 */
	public static final WFState Completed = new WFState(COMPLETED);
	/** String representation of 'completed' */
	
	public static final String _Completed = "Completed";
	
	/** Index of value 'not_started' */
	public static final int NOT_STARTED = 4;	
	/**
	 * Process not started (e, g, preconditions net met)
	 */
	public static final WFState Not_started = new WFState(NOT_STARTED);
	/** String representation of 'not_started' */
	
	public static final String _Not_started = "Not_started";
	
	/** Index of value 'running' */
	public static final int RUNNING = 5;	
	/**
	 * Process is running
	 */
	public static final WFState Running = new WFState(RUNNING);
	/** String representation of 'running' */
	
	public static final String _Running = "Running";
	
	/** Index of value 'suspended' */
	public static final int SUSPENDED = 6;	
	/**
	 * Process is suspended
	 */
	public static final WFState Suspended = new WFState(SUSPENDED);
	/** String representation of 'suspended' */
	
	public static final String _Suspended = "Suspended";
	
	
	public static final WFState NO_VALUE = new WFState();
	public static final int NOT_INITIALIZED = 0;
	public static final int MAXIMUM = 6;
	
	private int value;
	private static final WFState[] INSTANCES = {
		NO_VALUE,		
		Terminated,		
		Aborted,		
		Completed,		
		Not_started,		
		Running,		
		Suspended
	};

	private static final String[] VALUES = {
		"?",
    		_Terminated,
    		_Aborted,
    		_Completed,
    		_Not_started,
    		_Running,
    		_Suspended
	};

	/**
	 * Creates an uninitialized enumeration of WFState
	 */
	private WFState() {
		this( NOT_INITIALIZED );
	}

	/**
	 * Constructor with predefined value
	 * @param pValue index of instance (equal to INSTANCES[ pValue ])
	 */
	private WFState(int pValue) {
		if (pValue < 0 || pValue > MAXIMUM) {
			throw new IllegalArgumentException("Value"+pValue+" is not allowed for enumeration WFState, valid range is 0.."+MAXIMUM);
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
	 * String representation of enumeration 'WFState'
	 * @return 'WFState: <value as string>'
	 */
	public String toString() {
		return "WFState:" + getValue();
	}

	/**
     	 * Returns the verbose value of enumeration WFState
     	 * @return String value of current WFState instance
     	 */
	public String getValue() {
		if (value > 0 && value <= MAXIMUM) {
			return VALUES[ value ];
		} else {
			throw new IllegalStateException("Value "+
				value+" is not allowed for enumeration WFState, valid range is 0.."+MAXIMUM);
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
	public static WFState[] getInstances() {
		return INSTANCES;
	}
	
	/**
	 * Gets the corresponding instance of pValue. If pValue is invalid, an
	 * IllegalArgumentException is thrown.
	 * @return corresponding instance (equal to INSTANCES[ pValue ])
	 */
	public static WFState getInstance(int pValue) {
		if (pValue > 0 && pValue <= MAXIMUM) {
			return INSTANCES[ pValue ];
		} else {
			throw new IllegalArgumentException("Value "+
				pValue+" is not allowed for enumeration WFState, valid range is 0.."+MAXIMUM);
		}
	}
	
	/*
	 * Compare this instance with another object
	 * @param pObject to compare with
	 * @return true, if 1) Object is of type WFState and 2) values are equal
	 */
	public boolean equals(Object pObject) {
		return pObject != null &&
		   getClass().getName().equals( pObject.getClass().getName() ) ?
		       value == ( (WFState) pObject ).value:
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
		       value - ( (WFState) pObject ).value:
		       -1;
	}
}





