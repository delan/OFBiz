/* $Id$
 * @(#)WFProcessDescription.java   Fri Aug 17 12:18:05 GMT+02:00 2001
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
 * @created Fri Aug 17 12:18:05 GMT+02:00 2001
 * @version 1.0
 */

/*
 * $Log$
 * Revision 1.1  2001/08/10 10:43:15  owieland
 * Add missing files from import
 *
 */
package org.ofbiz.core.workflow;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.service.workflow.WFException;



/**
 * Describes a process
 * @author Oliver Wieland
 * @version 1.0
 */

public class WFProcessDescription extends WFMetaObject  implements Serializable  {

	
	// Attribute instance 'autoStart'
	private boolean autoStart;
	
	// Attribute instance 'autoFinish'
	private boolean autoFinish;
	
	// Attribute instance 'validFrom'
	private long validFrom;
	
	// Attribute instance 'validTo'
	private long validTo;
	
	
		
	/**
	 * Empty constructor
	 */
	WFProcessDescription() {
		super();	
	}

	/**
	 * Constructor with all attributes 
	 * @param pAutoStart Initial value for attribute 'autoStart'
	 * @param pAutoFinish Initial value for attribute 'autoFinish'
	 * @param pValidFrom Initial value for attribute 'validFrom'
	 * @param pValidTo Initial value for attribute 'validTo'
	 */
	WFProcessDescription(
		boolean pAutoStart,
		boolean pAutoFinish,
		long pValidFrom,
		long pValidTo) {		
				
		autoStart = pAutoStart;		
		autoFinish = pAutoFinish;		
		validFrom = pValidFrom;		
		validTo = pValidTo;
	}
		
	/**
	 * Getter for attribute 'autoStart'
	 * 
	 * @return Value of attribute autoStart
	 */
	public boolean getAutoStart()  {
		return autoStart;
	}
	
	/**
	 * Setter for attribute 'autoStart'
	 * 
	 * @param pAutoStart new value for attribute autoStart
	 */
	public void setAutoStart(boolean pAutoStart)  {
		if (autoStart == pAutoStart) return;		
		if ( !notifyAttributeChangeAutoStart( pAutoStart ) ) return;
		autoStart = pAutoStart;
	}
	
	/**
	 * This method is called, before the attribute 'AutoStart' is set to a new
	 * value.
	 * @param pAutoStart New Value for attribute 'AutoStart'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeAutoStart(boolean pAutoStart) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'autoFinish'
	 * 
	 * @return Value of attribute autoFinish
	 */
	public boolean getAutoFinish()  {
		return autoFinish;
	}
	
	/**
	 * Setter for attribute 'autoFinish'
	 * 
	 * @param pAutoFinish new value for attribute autoFinish
	 */
	public void setAutoFinish(boolean pAutoFinish)  {
		if (autoFinish == pAutoFinish) return;		
		if ( !notifyAttributeChangeAutoFinish( pAutoFinish ) ) return;
		autoFinish = pAutoFinish;
	}
	
	/**
	 * This method is called, before the attribute 'AutoFinish' is set to a new
	 * value.
	 * @param pAutoFinish New Value for attribute 'AutoFinish'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeAutoFinish(boolean pAutoFinish) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'validFrom'
	 * 
	 * @return Value of attribute validFrom
	 */
	public long getValidFrom()  {
		return validFrom;
	}
	
	/**
	 * Setter for attribute 'validFrom'
	 * 
	 * @param pValidFrom new value for attribute validFrom
	 */
	public void setValidFrom(long pValidFrom)  {
		if (validFrom == pValidFrom) return;		
		if ( !notifyAttributeChangeValidFrom( pValidFrom ) ) return;
		validFrom = pValidFrom;
	}
	
	/**
	 * This method is called, before the attribute 'ValidFrom' is set to a new
	 * value.
	 * @param pValidFrom New Value for attribute 'ValidFrom'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeValidFrom(long pValidFrom) {		
		return true;
	}
	
		
	/**
	 * Getter for attribute 'validTo'
	 * 
	 * @return Value of attribute validTo
	 */
	public long getValidTo()  {
		return validTo;
	}
	
	/**
	 * Setter for attribute 'validTo'
	 * 
	 * @param pValidTo new value for attribute validTo
	 */
	public void setValidTo(long pValidTo)  {
		if (validTo == pValidTo) return;		
		if ( !notifyAttributeChangeValidTo( pValidTo ) ) return;
		validTo = pValidTo;
	}
	
	/**
	 * This method is called, before the attribute 'ValidTo' is set to a new
	 * value.
	 * @param pValidTo New Value for attribute 'ValidTo'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeValidTo(long pValidTo) {		
		return true;
	}
	

	// Link attribute of association 'Steps '
			
	private Collection steps;

	/**
	 * Getter of association 'Steps'
	 * @return Currents contents of association 'Steps'
	 */
	public Collection getSteps() {
		return steps != null ? steps : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Steps'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllSteps
	 * @param pSteps List containing the new elements for association  'Steps'. 
	 */
	public void setSteps (Collection pSteps) {
		removeAllSteps();	
		if (pSteps != null ) {
			addAllToSteps( pSteps );
		}
	}

	/**
	 * Removes all elements from association 'Steps'
	 */
	public void removeAllSteps() {
		if (steps == null) return; // nothing to do
		
		for(Iterator it = steps.iterator(); it.hasNext();) {
			WFActivityDescription lElement = (WFActivityDescription) it.next();
			lElement.unlinkProcess( this );
			removeSteps( lElement );				
		}		
	}

	/**
	 * Removes pSteps from association 'Steps'
	 * @param pSteps element to remove
	 */
	public void removeSteps(WFActivityDescription pSteps) {
		if (steps != null) {
			steps.remove( pSteps );
			pSteps.unlinkProcess( this ); // notify other end
			notifyRemoveSteps( pSteps ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pStepsList to association 'Steps'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pStepsList.size()</code>)
	 */
	public int addAllToSteps (Collection pStepsList) {
		if (pStepsList == null) {
			throw new RuntimeException("Attempted to add null container to WFProcessDescription#Steps!");
		}
		int lInserted=0;
		for(Iterator it = pStepsList.iterator(); it.hasNext(); ) {
			try {
				WFActivityDescription lSteps = (WFActivityDescription)it.next();				
				addSteps( lSteps );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pSteps to association 'Steps'
	 * @param pSteps Element to add
	 */
	public void addSteps (WFActivityDescription pSteps) {
		if (pSteps == null) {
			throw new RuntimeException("Attempted to add null object to WFProcessDescription#Steps!");
		}
		
		if (steps == null) {
			steps = new ArrayList();
		}
		steps.add(pSteps);
		
		pSteps.linkProcess(this); // notify other end
		
		notifyRemoveSteps( pSteps ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Steps'
	 */
	private void notifyAddSteps(WFActivityDescription pSteps) {
		//System.out.println("Add " + pSteps + " to WFProcessDescription#Steps");
	}		
	
	/**
	 * Hook for 'remove' on association 'Steps'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveSteps(WFActivityDescription pSteps) {
		//System.out.println("Remove " + pSteps + " from WFProcessDescription#Steps");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkSteps(WFActivityDescription pSteps) {		
		if (steps == null) {
			steps = new ArrayList();
		}
		steps.add(pSteps);
		notifyAddSteps( pSteps ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkSteps(WFActivityDescription pSteps) {
		if (steps == null) return;steps.remove(pSteps);
		notifyRemoveSteps( pSteps ); // notify ourselves
	}	

	// Link attribute of association 'Container '
			
	private WFEngine container;

	/**
	 * Getter of association 'Container'
	 * @return Current value of association 'Container'.
	 * @throws RuntimeException, if value is null
	 */
	public WFEngine getContainer() {
		if (container == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: WFProcessDescription#Container is null!");
		}
		return container;
	}

	/**
	 * Setter of association 'Container'.
	 * @param pContainer New value for association 'Container'
	 */
	public void setContainer (WFEngine pContainer) {
		if (pContainer == null && container != null) {
			container.unlinkProcesses( this );
		}
		container = pContainer;		 
		container.linkProcesses(this);
	}
		
	/**
	 * Checks, if aggregate 'Container' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isContainerNull() {		
		return container == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkContainer(WFEngine pContainer) {		
		
		if (container != null) {
			container.unlinkProcesses(this); // Alte Beziehung l�schen
		}
		container = pContainer;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkContainer(WFEngine pContainer) {
		container = null;
		
	}	

	/**
	 * String representation of WFProcessDescription
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("WFProcessDescription");	
		return lRet.toString();
	}
}








