/*
 * $Id$
 */
package org.ofbiz.core.workflow.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.ofbiz.core.workflow.WfException;

/** 
 * <p>Describes a process
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
 * @author <a href='mailto:jonesde@ofbiz.org'>David E. Jones</a>
 * @created Sun Aug 12 13:22:40 GMT+02:00 2001
 * @version 1.0
 */
public class WorkflowProcess extends WfMetaObject implements Serializable  {
  // Attribute instance 'autoStart'
  private boolean autoStart;
  
  // Attribute instance 'autoFinish'
  private boolean autoFinish;
  
  // Attribute instance 'validFrom'
  private long validFrom;
  
  // Attribute instance 'validTo'
  private long validTo;
  
  // Link attribute of association 'Steps '
  private Collection steps;
  
  // Link attribute of association 'Container '
  //private WfEngine container;
  
  
  /** Empty constructor */
  WorkflowProcess() { super(); }
  
  /** Constructor with all attributes
   * @param pAutoStart Initial value for attribute 'autoStart'
   * @param pAutoFinish Initial value for attribute 'autoFinish'
   * @param pValidFrom Initial value for attribute 'validFrom'
   * @param pValidTo Initial value for attribute 'validTo'
   */
  WorkflowProcess(boolean pAutoStart, boolean pAutoFinish, long pValidFrom, long pValidTo) {
    autoStart = pAutoStart;
    autoFinish = pAutoFinish;
    validFrom = pValidFrom;
    validTo = pValidTo;
  }
  
  /** Getter for attribute 'autoStart' *
   * @return Value of attribute autoStart
   */
  public boolean getAutoStart() { return autoStart; }
  
  /** Setter for attribute 'autoStart'
   * @param pAutoStart Neuer Wert des Attributes autoStart
   */
  public void setAutoStart(boolean pAutoStart)  {
    if (autoStart == pAutoStart) return;
    if ( !notifyAttributeChangeAutoStart( pAutoStart ) ) return;
    autoStart = pAutoStart;
  }
  
  /** This method is called, before the attribute 'AutoStart' is set to a new value.
   * @param pAutoStart New Value for attribute 'AutoStart'
   * @return true, if change accepted, otherwise false. Default is true
   */
  private boolean notifyAttributeChangeAutoStart(boolean pAutoStart) {
    return true;
  }
  
  
  /** Getter for attribute 'autoFinish'
   * @return Value of attribute autoFinish
   */
  public boolean getAutoFinish() { return autoFinish; }
  
  /** Setter for attribute 'autoFinish'
   * @param pAutoFinish Neuer Wert des Attributes autoFinish
   */
  public void setAutoFinish(boolean pAutoFinish)  {
    if (autoFinish == pAutoFinish) return;
    if ( !notifyAttributeChangeAutoFinish( pAutoFinish ) ) return;
    autoFinish = pAutoFinish;
  }
  
  /** This method is called, before the attribute 'AutoFinish' is set to a new value.
   * @param pAutoFinish New Value for attribute 'AutoFinish'
   * @return true, if change accepted, otherwise false. Default is true
   */
  private boolean notifyAttributeChangeAutoFinish(boolean pAutoFinish) {
    return true;
  }
  
  
  /** Getter for attribute 'validFrom'
   * @return Value of attribute validFrom
   */
  public long getValidFrom() { return validFrom; }
  
  /** Setter for attribute 'validFrom'
   * @param pValidFrom Neuer Wert des Attributes validFrom
   */
  public void setValidFrom(long pValidFrom)  {
    if (validFrom == pValidFrom) return;
    if ( !notifyAttributeChangeValidFrom( pValidFrom ) ) return;
    validFrom = pValidFrom;
  }
  
  /** This method is called, before the attribute 'ValidFrom' is set to a new value.
   * @param pValidFrom New Value for attribute 'ValidFrom'
   * @return true, if change accepted, otherwise false. Default is true
   */
  private boolean notifyAttributeChangeValidFrom(long pValidFrom) {
    return true;
  }
  
  
  /** Getter for attribute 'validTo'
   * @return Value of attribute validTo
   */
  public long getValidTo() { return validTo; }
  
  /** Setter for attribute 'validTo'
   * @param pValidTo Neuer Wert des Attributes validTo
   */
  public void setValidTo(long pValidTo)  {
    if (validTo == pValidTo) return;
    if ( !notifyAttributeChangeValidTo( pValidTo ) ) return;
    validTo = pValidTo;
  }
  
  /** This method is called, before the attribute 'ValidTo' is set to a new value.
   * @param pValidTo New Value for attribute 'ValidTo'
   * @return true, if change accepted, otherwise false. Default is true
   */
  private boolean notifyAttributeChangeValidTo(long pValidTo) {
    return true;
  }
  
  
  /** Getter of association 'Steps'
   * @return Currents contents of association 'Steps'
   */
  public Collection getSteps() {
    return steps != null ? steps : java.util.Collections.EMPTY_LIST;
  }
  
  /** Setter of association  'Steps'. All existing elements are dropped. An null argument
   *  creates the same result as removeAllSteps
   * @param pSteps List containing the new elements for association  'Steps'.
   */
  public void setSteps(Collection pSteps) {
    removeAllSteps();
    if (pSteps != null ) {
      addAllToSteps( pSteps );
    }
  }
  
  /** Removes all elements from assoziation 'Steps' */
  public void removeAllSteps() {
    if (steps == null) return; // nothing to do
    
    for(Iterator it = steps.iterator(); it.hasNext();) {
      Activity lElement = (Activity) it.next();
      lElement.unlinkProcess( this );
      removeSteps( lElement );
    }
  }
  
  /** Removes pSteps from assoziation 'Steps'
   * @param pSteps element to remove
   */
  public void removeSteps(Activity pSteps) {
    if (steps != null) {
      steps.remove( pSteps );
      pSteps.unlinkProcess( this ); // notify other end
      notifyRemoveSteps( pSteps ); // notify ourselves
    }
  }
  
  /** Adds all elements in pStepsList to association 'Steps'. Invalid elements (e. g.
   *  wrong type) are ignored. Existing elements are kept.
   * @return Number of added elements (should be equivalent to <code>pStepsList.size()</code>)
   */
  public int addAllToSteps(Collection pStepsList) {
    if (pStepsList == null) {
      throw new RuntimeException("Attempted to add null container to WorkflowProcess#Steps!");
    }
    int lInserted=0;
    for(Iterator it = pStepsList.iterator(); it.hasNext(); ) {
      try {
        Activity lSteps = (Activity)it.next();
        addSteps( lSteps );
        ++lInserted;
      } catch(Throwable t) {
        continue;
      }
    }
    return lInserted;
  }
  
  /** Adds pSteps to association 'Steps'
   * @param pSteps Element to add
   */
  public void addSteps(Activity pSteps) {
    if (pSteps == null) {
      throw new RuntimeException("Attempted to add null object to WorkflowProcess#Steps!");
    }
    
    if (steps == null) {
      steps = new ArrayList();
    }
    steps.add(pSteps);
    
    pSteps.linkProcess(this); // notify other end
    
    notifyRemoveSteps( pSteps ); // notify ourselves
  }
  
  /** Hook for 'add' on association 'Steps' */
  private void notifyAddSteps(Activity pSteps) {
    //System.out.println("Add " + pSteps + " to WorkflowProcess#Steps");
  }
  
  /** Hook for 'remove' on association 'Steps'. This is the right place
   *  for cache updates or something else
   */
  private void notifyRemoveSteps(Activity pSteps) {
    //System.out.println("Remove " + pSteps + " from WorkflowProcess#Steps");
  }
  
  
  /** Internal use only */
  public void linkSteps(Activity pSteps) {
    if (steps == null) {
      steps = new ArrayList();
    }
    steps.add(pSteps);
    notifyAddSteps( pSteps ); // notify ourselves
  }
  
  /** Internal use only */
  public void unlinkSteps(Activity pSteps) {
    if (steps == null) return;steps.remove(pSteps);
    notifyRemoveSteps( pSteps ); // notify ourselves
  }
  
/*
  /** Getter of association 'Container'
   * @return Current value of association 'Container'.
   * @throws RuntimeException, if value is null
   * /
  public WfEngine getContainer() {
    if (container == null) {
      // This should never happen. If so, fix your code :-)
      throw new RuntimeException("Invalid aggregate: WorkflowProcess#Container is null!");
    }
    return container;
  }
  
  /** Setter of association 'Container'.
   * @param pContainer New value for association 'Container'
   * /
  public void setContainer(WfEngine pContainer) {
    if (pContainer == null && container != null) {
      container.unlinkProcesses( this );
    }
    container = pContainer;
    container.linkProcesses(this);
  }
  
  /** Checks, if aggregate 'Container' contains elements
   * @return true, if association contains no elements, otherwise false
   * /
  public boolean isContainerNull() { return container == null; }
  
  
  /** Internal use only * /
  public void linkContainer(WfEngine pContainer) {
    if (container != null) {
      container.unlinkProcesses(this); // Alte Beziehung löschen
    }
    container = pContainer;
  }
  
  /** Internal use only * /
  public void unlinkContainer(WfEngine pContainer) { container = null; }
*/
  /** String representation of WorkflowProcess */
  public String toString() {
    StringBuffer lRet = new StringBuffer("WorkflowProcess");
    return lRet.toString();
  }
}
