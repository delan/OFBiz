/* $Id$
 * @(#)WFLightweightActivity.java   Fri Aug 17 12:18:04 GMT+02:00 2001
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
 * @created Fri Aug 17 12:18:04 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 */
package org.ofbiz.core.workflow;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.service.workflow.WFActivity;
import org.ofbiz.service.workflow.WFContext;
import org.ofbiz.service.workflow.WFDurationUnit;
import org.ofbiz.service.workflow.WFState;



/**
 * Transfer object for an activity. Clients do not have direct access to an activity instance
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFLightweightActivity implements WFActivity {

	// Attribute instance 'name'
	private String name;
	
	// Attribute instance 'id'
	private String id;
	
	// Attribute instance 'initiator'
	private String initiator;
	
	// Attribute instance 'context'
	private WFContext context;
	
	// Attribute instance 'state'
	private WFState state;
	
	// Attribute instance 'creationTime'
	private long creationTime;
	
	// Attribute instance 'lastModified'
	private long lastModified;
	
	// Attribute instance 'completionTime'
	private long completionTime;
	
	// Attribute instance 'startTime'
	private long startTime;
	
	// Attribute instance 'durationUnit'
	private WFDurationUnit durationUnit;
	
	// Attribute instance 'duration'
	private long duration;
	
	
	//==> Begin Protected Area global
   	//==> End Protected Area global

   	
	/**
	 * Konstruktor ohne Argumente
	 */
	WFLightweightActivity() {
			}

	/**
	 * Konstruktor mit allen Attributen 
	 * @param pName Wert für Attibut 'name'
	 * @param pId Wert für Attibut 'id'
	 * @param pInitiator Wert für Attibut 'initiator'
	 * @param pContext Wert für Attibut 'context'
	 * @param pState Wert für Attibut 'state'
	 * @param pCreationTime Wert für Attibut 'creationTime'
	 * @param pLastModified Wert für Attibut 'lastModified'
	 * @param pCompletionTime Wert für Attibut 'completionTime'
	 * @param pStartTime Wert für Attibut 'startTime'
	 * @param pDurationUnit Wert für Attibut 'durationUnit'
	 * @param pDuration Wert für Attibut 'duration'
	 */
	WFLightweightActivity(
		String pName,
		String pId,
		String pInitiator,
		WFContext pContext,
		WFState pState,
		long pCreationTime,
		long pLastModified,
		long pCompletionTime,
		long pStartTime,
		WFDurationUnit pDurationUnit,
		long pDuration) {
		
		name = pName;
		id = pId;
		initiator = pInitiator;
		context = pContext;
		state = pState;
		creationTime = pCreationTime;
		lastModified = pLastModified;
		completionTime = pCompletionTime;
		startTime = pStartTime;
		durationUnit = pDurationUnit;
		duration = pDuration;
	}
		
	/**
	 * Getter for attribute 'name'
	 * 
	 * @return Value of attribute name
	 */
	public String getName()  {
		return name;
	}
	
	/**
	 * Setter for attribute 'name'
	 * 
	 * @param pName Neuer Wert des Attributes name
	 */
	public void setName(String pName)  {
		if (name == pName) return;		
		if ( !notifyAttributeChangeName( pName ) ) return;
		name = pName;
	}
	
	/**
	 * This method is called, before the attribute 'Name' is set to a new
	 * value.
	 * @param pName New Value for attribute 'Name'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeName(String pName) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'id'
	 * 
	 * @return Value of attribute id
	 */
	public String getId()  {
		return id;
	}
	
	/**
	 * Setter for attribute 'id'
	 * 
	 * @param pId Neuer Wert des Attributes id
	 */
	public void setId(String pId)  {
		if (id == pId) return;		
		if ( !notifyAttributeChangeId( pId ) ) return;
		id = pId;
	}
	
	/**
	 * This method is called, before the attribute 'Id' is set to a new
	 * value.
	 * @param pId New Value for attribute 'Id'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeId(String pId) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'initiator'
	 * 
	 * @return Value of attribute initiator
	 */
	public String getInitiator()  {
		return initiator;
	}
	
	/**
	 * Setter for attribute 'initiator'
	 * 
	 * @param pInitiator Neuer Wert des Attributes initiator
	 */
	public void setInitiator(String pInitiator)  {
		if (initiator == pInitiator) return;		
		if ( !notifyAttributeChangeInitiator( pInitiator ) ) return;
		initiator = pInitiator;
	}
	
	/**
	 * This method is called, before the attribute 'Initiator' is set to a new
	 * value.
	 * @param pInitiator New Value for attribute 'Initiator'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeInitiator(String pInitiator) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'context'
	 * 
	 * @return Value of attribute context
	 */
	public WFContext getContext()  {
		return context;
	}
	
	/**
	 * Setter for attribute 'context'
	 * 
	 * @param pContext Neuer Wert des Attributes context
	 */
	public void setContext(WFContext pContext)  {
		if (context == pContext) return;		
		if ( !notifyAttributeChangeContext( pContext ) ) return;
		context = pContext;
	}
	
	/**
	 * This method is called, before the attribute 'Context' is set to a new
	 * value.
	 * @param pContext New Value for attribute 'Context'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeContext(WFContext pContext) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'state'
	 * 
	 * @return Value of attribute state
	 */
	public WFState getState()  {
		return state;
	}
	
	/**
	 * Setter for attribute 'state'
	 * 
	 * @param pState Neuer Wert des Attributes state
	 */
	public void setState(WFState pState)  {
		if (state == pState) return;		
		if ( !notifyAttributeChangeState( pState ) ) return;
		state = pState;
	}
	
	/**
	 * This method is called, before the attribute 'State' is set to a new
	 * value.
	 * @param pState New Value for attribute 'State'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeState(WFState pState) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'creationTime'
	 * 
	 * @return Value of attribute creationTime
	 */
	public long getCreationTime()  {
		return creationTime;
	}
	
	/**
	 * Setter for attribute 'creationTime'
	 * 
	 * @param pCreationTime Neuer Wert des Attributes creationTime
	 */
	public void setCreationTime(long pCreationTime)  {
		if (creationTime == pCreationTime) return;		
		if ( !notifyAttributeChangeCreationTime( pCreationTime ) ) return;
		creationTime = pCreationTime;
	}
	
	/**
	 * This method is called, before the attribute 'CreationTime' is set to a new
	 * value.
	 * @param pCreationTime New Value for attribute 'CreationTime'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeCreationTime(long pCreationTime) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'lastModified'
	 * 
	 * @return Value of attribute lastModified
	 */
	public long getLastModified()  {
		return lastModified;
	}
	
	/**
	 * Setter for attribute 'lastModified'
	 * 
	 * @param pLastModified Neuer Wert des Attributes lastModified
	 */
	public void setLastModified(long pLastModified)  {
		if (lastModified == pLastModified) return;		
		if ( !notifyAttributeChangeLastModified( pLastModified ) ) return;
		lastModified = pLastModified;
	}
	
	/**
	 * This method is called, before the attribute 'LastModified' is set to a new
	 * value.
	 * @param pLastModified New Value for attribute 'LastModified'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeLastModified(long pLastModified) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'completionTime'
	 * 
	 * @return Value of attribute completionTime
	 */
	public long getCompletionTime()  {
		return completionTime;
	}
	
	/**
	 * Setter for attribute 'completionTime'
	 * 
	 * @param pCompletionTime Neuer Wert des Attributes completionTime
	 */
	public void setCompletionTime(long pCompletionTime)  {
		if (completionTime == pCompletionTime) return;		
		if ( !notifyAttributeChangeCompletionTime( pCompletionTime ) ) return;
		completionTime = pCompletionTime;
	}
	
	/**
	 * This method is called, before the attribute 'CompletionTime' is set to a new
	 * value.
	 * @param pCompletionTime New Value for attribute 'CompletionTime'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeCompletionTime(long pCompletionTime) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'startTime'
	 * Timestamp of activity start (resource accepted and started this activity)
	 * @return Value of attribute startTime
	 */
	public long getStartTime()  {
		return startTime;
	}
	
	/**
	 * Setter for attribute 'startTime'
	 * Timestamp of activity start (resource accepted and started this activity)
	 * @param pStartTime Neuer Wert des Attributes startTime
	 */
	public void setStartTime(long pStartTime)  {
		if (startTime == pStartTime) return;		
		if ( !notifyAttributeChangeStartTime( pStartTime ) ) return;
		startTime = pStartTime;
	}
	
	/**
	 * This method is called, before the attribute 'StartTime' is set to a new
	 * value.
	 * @param pStartTime New Value for attribute 'StartTime'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeStartTime(long pStartTime) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'durationUnit'
	 * 
	 * @return Value of attribute durationUnit
	 */
	public WFDurationUnit getDurationUnit()  {
		return durationUnit;
	}
	
	/**
	 * Setter for attribute 'durationUnit'
	 * 
	 * @param pDurationUnit Neuer Wert des Attributes durationUnit
	 */
	public void setDurationUnit(WFDurationUnit pDurationUnit)  {
		if (durationUnit == pDurationUnit) return;		
		if ( !notifyAttributeChangeDurationUnit( pDurationUnit ) ) return;
		durationUnit = pDurationUnit;
	}
	
	/**
	 * This method is called, before the attribute 'DurationUnit' is set to a new
	 * value.
	 * @param pDurationUnit New Value for attribute 'DurationUnit'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeDurationUnit(WFDurationUnit pDurationUnit) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'duration'
	 * 
	 * @return Value of attribute duration
	 */
	public long getDuration()  {
		return duration;
	}
	
	/**
	 * Setter for attribute 'duration'
	 * 
	 * @param pDuration Neuer Wert des Attributes duration
	 */
	public void setDuration(long pDuration)  {
		if (duration == pDuration) return;		
		if ( !notifyAttributeChangeDuration( pDuration ) ) return;
		duration = pDuration;
	}
	
	/**
	 * This method is called, before the attribute 'Duration' is set to a new
	 * value.
	 * @param pDuration New Value for attribute 'Duration'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeDuration(long pDuration) {		
		return true;
	}
	

	/**
	 * Stringrepräsentation der Klasse WFLightweightActivity
	 */
	public String toString() {
		return "WFLightweightActivity (LW)";
	}
}




