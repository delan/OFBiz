/* $Id$
 * @(#)WFEventType.java   Fri Aug 17 12:18:07 GMT+02:00 2001
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
 * @created Fri Aug 17 12:18:07 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 */

package org.ofbiz.service.workflow;


import java.io.Serializable;




/**
 * Repr‰sentiert alle vorkommenden Ereignistypen
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFEventType implements Serializable, Comparable {

	
	/** Index of value 'userLogin' */
	public static final int USERLOGIN = 1;	
	/**
	 * Benutzer hat sich angemeldet
	 */
	public static final WFEventType UserLogin = new WFEventType(USERLOGIN);
	/** String representation of 'userLogin' */
	
	public static final String _UserLogin = "UserLogin";
	
	/** Index of value 'userLogoff' */
	public static final int USERLOGOFF = 2;	
	/**
	 * Benutzer hat sich abgemeldet
	 */
	public static final WFEventType UserLogoff = new WFEventType(USERLOGOFF);
	/** String representation of 'userLogoff' */
	
	public static final String _UserLogoff = "UserLogoff";
	
	/** Index of value 'activityTerminated' */
	public static final int ACTIVITYTERMINATED = 3;	
	/**
	 * Eine Aktivit‰t wurde aufgrund eines Systemfehlers beendet
	 */
	public static final WFEventType ActivityTerminated = new WFEventType(ACTIVITYTERMINATED);
	/** String representation of 'activityTerminated' */
	
	public static final String _ActivityTerminated = "ActivityTerminated";
	
	/** Index of value 'activityCompleted' */
	public static final int ACTIVITYCOMPLETED = 4;	
	/**
	 * Eine Aktivit‰t wurde ordnungsgem‰ﬂ beendet
	 */
	public static final WFEventType ActivityCompleted = new WFEventType(ACTIVITYCOMPLETED);
	/** String representation of 'activityCompleted' */
	
	public static final String _ActivityCompleted = "ActivityCompleted";
	
	/** Index of value 'activityAborted' */
	public static final int ACTIVITYABORTED = 5;	
	/**
	 * Eine Aktivit‰t wurde vom Benutzer abgebrochen
	 */
	public static final WFEventType ActivityAborted = new WFEventType(ACTIVITYABORTED);
	/** String representation of 'activityAborted' */
	
	public static final String _ActivityAborted = "ActivityAborted";
	
	/** Index of value 'activityStarted' */
	public static final int ACTIVITYSTARTED = 6;	
	/**
	 * Eine Aktivit‰t wurde gestartet
	 */
	public static final WFEventType ActivityStarted = new WFEventType(ACTIVITYSTARTED);
	/** String representation of 'activityStarted' */
	
	public static final String _ActivityStarted = "ActivityStarted";
	
	/** Index of value 'processStarted' */
	public static final int PROCESSSTARTED = 7;	
	/**
	 * Ein Prozess wurde gestartet
	 */
	public static final WFEventType ProcessStarted = new WFEventType(PROCESSSTARTED);
	/** String representation of 'processStarted' */
	
	public static final String _ProcessStarted = "ProcessStarted";
	
	/** Index of value 'processTerminated' */
	public static final int PROCESSTERMINATED = 8;	
	/**
	 * Ein Prozess wurde aufgrund eines Systemfehlers abgebrochen
	 */
	public static final WFEventType ProcessTerminated = new WFEventType(PROCESSTERMINATED);
	/** String representation of 'processTerminated' */
	
	public static final String _ProcessTerminated = "ProcessTerminated";
	
	/** Index of value 'processAborted' */
	public static final int PROCESSABORTED = 9;	
	/**
	 * Ein Prozess wurde vom benutzer abgebrochen
	 */
	public static final WFEventType ProcessAborted = new WFEventType(PROCESSABORTED);
	/** String representation of 'processAborted' */
	
	public static final String _ProcessAborted = "ProcessAborted";
	
	/** Index of value 'processCompleted' */
	public static final int PROCESSCOMPLETED = 10;	
	/**
	 * Ein Prozess wurde ordnungsgem‰ﬂ beendet
	 */
	public static final WFEventType ProcessCompleted = new WFEventType(PROCESSCOMPLETED);
	/** String representation of 'processCompleted' */
	
	public static final String _ProcessCompleted = "ProcessCompleted";
	
	/** Index of value 'processSuspended' */
	public static final int PROCESSSUSPENDED = 11;	
	/**
	 * Ein Prozess wurde suspendiert
	 */
	public static final WFEventType ProcessSuspended = new WFEventType(PROCESSSUSPENDED);
	/** String representation of 'processSuspended' */
	
	public static final String _ProcessSuspended = "ProcessSuspended";
	
	/** Index of value 'processResumed' */
	public static final int PROCESSRESUMED = 12;	
	/**
	 * Ein Prozess wurde wiederaufgnommen
	 */
	public static final WFEventType ProcessResumed = new WFEventType(PROCESSRESUMED);
	/** String representation of 'processResumed' */
	
	public static final String _ProcessResumed = "ProcessResumed";
	
	/** Index of value 'newWorkItem' */
	public static final int NEWWORKITEM = 13;	
	/**
	 * In der Worklist ist ein neuer Eintrag hinzugekommen
	 */
	public static final WFEventType NewWorkItem = new WFEventType(NEWWORKITEM);
	/** String representation of 'newWorkItem' */
	
	public static final String _NewWorkItem = "NewWorkItem";
	
	/** Index of value 'internalError' */
	public static final int INTERNALERROR = 14;	
	/**
	 * Interner Fehler innerhalb der WorkflowEngine
	 */
	public static final WFEventType InternalError = new WFEventType(INTERNALERROR);
	/** String representation of 'internalError' */
	
	public static final String _InternalError = "InternalError";
	
	
	public static final WFEventType NO_VALUE = new WFEventType();
	public static final int NOT_INITIALIZED = 0;
	public static final int MAXIMUM = 14;
	
	private int value;
	private static final WFEventType[] INSTANCES = {
		NO_VALUE,		
		UserLogin,		
		UserLogoff,		
		ActivityTerminated,		
		ActivityCompleted,		
		ActivityAborted,		
		ActivityStarted,		
		ProcessStarted,		
		ProcessTerminated,		
		ProcessAborted,		
		ProcessCompleted,		
		ProcessSuspended,		
		ProcessResumed,		
		NewWorkItem,		
		InternalError
	};

	private static final String[] VALUES = {
		"?",
    		_UserLogin,
    		_UserLogoff,
    		_ActivityTerminated,
    		_ActivityCompleted,
    		_ActivityAborted,
    		_ActivityStarted,
    		_ProcessStarted,
    		_ProcessTerminated,
    		_ProcessAborted,
    		_ProcessCompleted,
    		_ProcessSuspended,
    		_ProcessResumed,
    		_NewWorkItem,
    		_InternalError
	};

	/**
	 * Creates an uninitialized enumeration of WFEventType
	 */
	private WFEventType() {
		this( NOT_INITIALIZED );
	}

	/**
	 * Constructor with predefined value
	 * @param pValue index of instance (equal to INSTANCES[ pValue ])
	 */
	private WFEventType(int pValue) {
		if (pValue < 0 || pValue > MAXIMUM) {
			throw new IllegalArgumentException("Value"+pValue+" is not allowed for enumeration WFEventType, valid range is 0.."+MAXIMUM);
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
	 * String representation of enumeration 'WFEventType'
	 * @return 'WFEventType: <value as string>'
	 */
	public String toString() {
		return "WFEventType:" + getValue();
	}

	/**
     	 * Returns the verbose value of enumeration WFEventType
     	 * @return String value of current WFEventType instance
     	 */
	public String getValue() {
		if (value > 0 && value <= MAXIMUM) {
			return VALUES[ value ];
		} else {
			throw new IllegalStateException("Value "+
				value+" is not allowed for enumeration WFEventType, valid range is 0.."+MAXIMUM);
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
	public static WFEventType[] getInstances() {
		return INSTANCES;
	}
	
	/**
	 * Gets the corresponding instance of pValue. If pValue is invalid, an
	 * IllegalArgumentException is thrown.
	 * @return corresponding instance (equal to INSTANCES[ pValue ])
	 */
	public static WFEventType getInstance(int pValue) {
		if (pValue > 0 && pValue <= MAXIMUM) {
			return INSTANCES[ pValue ];
		} else {
			throw new IllegalArgumentException("Value "+
				pValue+" is not allowed for enumeration WFEventType, valid range is 0.."+MAXIMUM);
		}
	}
	
	/*
	 * Compare this instance with another object
	 * @param pObject to compare with
	 * @return true, if 1) Object is of type WFEventType and 2) values are equal
	 */
	public boolean equals(Object pObject) {
		return pObject != null &&
		   getClass().getName().equals( pObject.getClass().getName() ) ?
		       value == ( (WFEventType) pObject ).value:
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
		       value - ( (WFEventType) pObject ).value:
		       -1;
	}
}





