/* $Id$
 * @(#)WFEventAudit.java   Fri Aug 17 12:18:07 GMT+02:00 2001
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
 * Revision 1.1.1.1  2001/08/10 10:33:40  owieland
 * Initial import
 *
 */
package org.ofbiz.service.workflow;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.core.workflow.WFExecutionObject;



/**
 * Class WFEventAudit - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFEventAudit implements Serializable  {

	
	// Attribute instance 'timestamp'
	private long timestamp;
	
	// Attribute instance 'event'
	private WFEvent event;
	
	// Attribute instance 'activityID'
	private String activityID;
	
	// Attribute instance 'activityName'
	private String activityName;
	
	// Attribute instance 'processID'
	private String processID;
	
	// Attribute instance 'processName'
	private String processName;
	
	
		
	/**
	 * Empty constructor
	 */
	WFEventAudit() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pTimestamp Initial value for attribute 'timestamp'
	 * @param pEvent Initial value for attribute 'event'
	 * @param pActivityID Initial value for attribute 'activityID'
	 * @param pActivityName Initial value for attribute 'activityName'
	 * @param pProcessID Initial value for attribute 'processID'
	 * @param pProcessName Initial value for attribute 'processName'
	 */
	WFEventAudit(
		long pTimestamp,
		WFEvent pEvent,
		String pActivityID,
		String pActivityName,
		String pProcessID,
		String pProcessName) {		
				
		timestamp = pTimestamp;		
		event = pEvent;		
		activityID = pActivityID;		
		activityName = pActivityName;		
		processID = pProcessID;		
		processName = pProcessName;
	}
		
	/**
	 * Getter for attribute 'timestamp'
	 * 
	 * @return Value of attribute timestamp
	 */
	public long getTimestamp()  {
		return timestamp;
	}
	
	/**
	 * Setter for attribute 'timestamp'
	 * 
	 * @param pTimestamp new value for attribute timestamp
	 */
	public void setTimestamp(long pTimestamp)  {
		if (timestamp == pTimestamp) return;		
		if ( !notifyAttributeChangeTimestamp( pTimestamp ) ) return;
		timestamp = pTimestamp;
	}
	
	/**
	 * This method is called, before the attribute 'Timestamp' is set to a new
	 * value.
	 * @param pTimestamp New Value for attribute 'Timestamp'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeTimestamp(long pTimestamp) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'event'
	 * 
	 * @return Value of attribute event
	 */
	public WFEvent getEvent()  {
		return event;
	}
	
	/**
	 * Setter for attribute 'event'
	 * 
	 * @param pEvent new value for attribute event
	 */
	public void setEvent(WFEvent pEvent)  {
		if (event == pEvent) return;		
		if ( !notifyAttributeChangeEvent( pEvent ) ) return;
		event = pEvent;
	}
	
	/**
	 * This method is called, before the attribute 'Event' is set to a new
	 * value.
	 * @param pEvent New Value for attribute 'Event'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeEvent(WFEvent pEvent) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'activityID'
	 * 
	 * @return Value of attribute activityID
	 */
	public String getActivityID()  {
		return activityID;
	}
	
	/**
	 * Setter for attribute 'activityID'
	 * 
	 * @param pActivityID new value for attribute activityID
	 */
	public void setActivityID(String pActivityID)  {
		if (activityID == pActivityID) return;		
		if ( !notifyAttributeChangeActivityID( pActivityID ) ) return;
		activityID = pActivityID;
	}
	
	/**
	 * This method is called, before the attribute 'ActivityID' is set to a new
	 * value.
	 * @param pActivityID New Value for attribute 'ActivityID'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeActivityID(String pActivityID) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'activityName'
	 * 
	 * @return Value of attribute activityName
	 */
	public String getActivityName()  {
		return activityName;
	}
	
	/**
	 * Setter for attribute 'activityName'
	 * 
	 * @param pActivityName new value for attribute activityName
	 */
	public void setActivityName(String pActivityName)  {
		if (activityName == pActivityName) return;		
		if ( !notifyAttributeChangeActivityName( pActivityName ) ) return;
		activityName = pActivityName;
	}
	
	/**
	 * This method is called, before the attribute 'ActivityName' is set to a new
	 * value.
	 * @param pActivityName New Value for attribute 'ActivityName'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeActivityName(String pActivityName) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'processID'
	 * 
	 * @return Value of attribute processID
	 */
	public String getProcessID()  {
		return processID;
	}
	
	/**
	 * Setter for attribute 'processID'
	 * 
	 * @param pProcessID new value for attribute processID
	 */
	public void setProcessID(String pProcessID)  {
		if (processID == pProcessID) return;		
		if ( !notifyAttributeChangeProcessID( pProcessID ) ) return;
		processID = pProcessID;
	}
	
	/**
	 * This method is called, before the attribute 'ProcessID' is set to a new
	 * value.
	 * @param pProcessID New Value for attribute 'ProcessID'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeProcessID(String pProcessID) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'processName'
	 * 
	 * @return Value of attribute processName
	 */
	public String getProcessName()  {
		return processName;
	}
	
	/**
	 * Setter for attribute 'processName'
	 * 
	 * @param pProcessName new value for attribute processName
	 */
	public void setProcessName(String pProcessName)  {
		if (processName == pProcessName) return;		
		if ( !notifyAttributeChangeProcessName( pProcessName ) ) return;
		processName = pProcessName;
	}
	
	/**
	 * This method is called, before the attribute 'ProcessName' is set to a new
	 * value.
	 * @param pProcessName New Value for attribute 'ProcessName'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeProcessName(String pProcessName) {		
		return true;
	}
	

	// Link attribute of association 'Source '
			
	private WFExecutionObject source;

	/**
	 * Getter of association 'Source'
	 * @return Current value of association 'Source'.
	 * @throws RuntimeException, if value is null
	 */
	public WFExecutionObject getSource() {
		if (source == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFEventAudit#Source is null!");
		}
		return source;
	}

	/**
	 * Setter of association 'Source'.
	 * @param pSource New value for association 'Source'
	 */
	public void setSource (WFExecutionObject pSource) {
		if (pSource == null && source != null) {
			source.unlinkHistory( this );
		}
		source = pSource;		 
		source.linkHistory(this);
	}
		
	/**
	 * Checks, if aggregate 'Source' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isSourceNull() {		
		return source == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkSource(WFExecutionObject pSource) {		
		
		if (source != null) {
			source.unlinkHistory(this); // Alte Beziehung löschen
		}
		source = pSource;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkSource(WFExecutionObject pSource) {
		source = null;
		
	}	

	/**
	 * String representation of WFEventAudit
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFEventAudit");	
		return lRet.toString();
	}
}








