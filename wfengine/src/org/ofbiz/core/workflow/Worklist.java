/* $Id$
 * @(#)Worklist.java   Fri Aug 17 12:18:05 GMT+02:00 2001
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
import org.ofbiz.service.workflow.WFActivity;
import org.ofbiz.service.workflow.WFException;



/**
 * Class Worklist - no documentation available
 * @author Oliver Wieland
 * @version 1.0
 */

public class Worklist implements Serializable  {

	
	// Attribute instance 'filter'
	private WFActivity filter;
	
	
		
	/**
	 * Empty constructor
	 */
	Worklist() {
			}

	/**
	 * Constructor with all attributes 
	 * @param pFilter Initial value for attribute 'filter'
	 */
	Worklist(
		WFActivity pFilter) {		
				
		filter = pFilter;
	}
		
	/**
	 * Getter for attribute 'filter'
	 * 
	 * @return Value of attribute filter
	 */
	public WFActivity getFilter()  {
		return filter;
	}
	
	/**
	 * Setter for attribute 'filter'
	 * 
	 * @param pFilter new value for attribute filter
	 */
	public void setFilter(WFActivity pFilter)  {
		if (filter == pFilter) return;		
		if ( !notifyAttributeChangeFilter( pFilter ) ) return;
		filter = pFilter;
	}
	
	/**
	 * This method is called, before the attribute 'Filter' is set to a new
	 * value.
	 * @param pFilter New Value for attribute 'Filter'
	 * @return true, if change accepted, otherwise false. Default is true
	 */
	private boolean notifyAttributeChangeFilter(WFActivity pFilter) {		
		return true;
	}
	

	// Link attribute of association 'Contents '
			
	private Collection contents;

	/**
	 * Getter of association 'Contents'
	 * @return Currents contents of association 'Contents'
	 */
	public Collection getContents() {
		return contents != null ? contents : java.util.Collections.EMPTY_LIST;
	}

	/**
	 * Setter of association  'Contents'. All existing elements are dropped. An null argument
	 * creates the same result as removeAllContents
	 * @param pContents List containing the new elements for association  'Contents'. 
	 */
	public void setContents (Collection pContents) {
		removeAllContents();	
		if (pContents != null ) {
			addAllToContents( pContents );
		}
	}

	/**
	 * Removes all elements from association 'Contents'
	 */
	public void removeAllContents() {
		if (contents == null) return; // nothing to do
		
		for(Iterator it = contents.iterator(); it.hasNext();) {
			WFActivityImpl lElement = (WFActivityImpl) it.next();
			lElement.unlinkWorklists( this );
			removeContents( lElement );				
		}		
	}

	/**
	 * Removes pContents from association 'Contents'
	 * @param pContents element to remove
	 */
	public void removeContents(WFActivityImpl pContents) {
		if (contents != null) {
			contents.remove( pContents );
			pContents.unlinkWorklists( this ); // notify other end
			notifyRemoveContents( pContents ); // notify ourselves
		}		
	}

	/**
	 * Adds all elements in pContentsList to association 'Contents'. Invalid elements (e. g.
	 * wrong type) are ignored. Existing elements are kept.
	 * @return Number of added elements (should be equivalent to <code>pContentsList.size()</code>)
	 */
	public int addAllToContents (Collection pContentsList) {
		if (pContentsList == null) {
			throw new RuntimeException("Attempted to add null container to Worklist#Contents!");
		}
		int lInserted=0;
		for(Iterator it = pContentsList.iterator(); it.hasNext(); ) {
			try {
				WFActivityImpl lContents = (WFActivityImpl)it.next();				
				addContents( lContents );
				++lInserted;
			} catch(Throwable t) {			
				continue;
			}
		}
		return lInserted;
	}
	
	/**
	 * Adds pContents to association 'Contents'
	 * @param pContents Element to add
	 */
	public void addContents (WFActivityImpl pContents) {
		if (pContents == null) {
			throw new RuntimeException("Attempted to add null object to Worklist#Contents!");
		}
		
		if (contents == null) {
			contents = new ArrayList();
		}
		contents.add(pContents);
		
		pContents.linkWorklists(this); // notify other end
		
		notifyRemoveContents( pContents ); // notify ourselves
	}
	
	/**
	 * Hook for 'add' on association 'Contents'
	 */
	private void notifyAddContents(WFActivityImpl pContents) {
		//System.out.println("Add " + pContents + " to Worklist#Contents");
	}		
	
	/**
	 * Hook for 'remove' on association 'Contents'. This is the right place
	 * for cache updates or something else
	 */
	private void notifyRemoveContents(WFActivityImpl pContents) {
		//System.out.println("Remove " + pContents + " from Worklist#Contents");		
	}
		
	
	/**
	 * Internal use only
	 */
	public void linkContents(WFActivityImpl pContents) {		
		if (contents == null) {
			contents = new ArrayList();
		}
		contents.add(pContents);
		notifyAddContents( pContents ); // notify ourselves
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkContents(WFActivityImpl pContents) {
		if (contents == null) return;contents.remove(pContents);
		notifyRemoveContents( pContents ); // notify ourselves
	}	

	// Link attribute of association 'Resource '
			
	private WFResource resource;

	/**
	 * Getter of association 'Resource'
	 * @return Current value of association 'Resource'.
	 * @throws RuntimeException, if value is null
	 */
	public WFResource getResource() {
		if (resource == null) {	
			// This should never happen. If so, fix your code :-)			
			throw new RuntimeException("Invalid aggregate: Worklist#Resource is null!");
		}
		return resource;
	}

	/**
	 * Setter of association 'Resource'.
	 * @param pResource New value for association 'Resource'
	 */
	public void setResource (WFResource pResource) {
		if (pResource == null && resource != null) {
			resource.unlinkWorklist( this );
		}
		resource = pResource;		 
		resource.linkWorklist(this);
	}
		
	/**
	 * Checks, if aggregate 'Resource' contains elements
	 * @return true, if association contains no elements, otherwise false
	 */
	public boolean isResourceNull() {		
		return resource == null;
	}
	
	
	
	/**
	 * Internal use only
	 */
	public void linkResource(WFResource pResource) {		
		
		if (resource != null) {
			resource.unlinkWorklist(this); // Alte Beziehung löschen
		}
		resource = pResource;
		
	}
	
	/**
	 * Internal use only
	 */
	public void unlinkResource(WFResource pResource) {
		resource = null;
		
	}	

	/**
	 * String representation of Worklist
	 */
	public String toString() {
		StringBuffer lRet = new StringBuffer("Worklist");	
		return lRet.toString();
	}
}








