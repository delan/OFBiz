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
 * <p>Describes an activity
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
public class Activity extends WfMetaObject implements Serializable  {  
  // Attribute instance 'isInitial'
  private boolean isInitial;
  
  // Attribute instance 'isFinish'
  private boolean isFinish;
  
  // Link attribute of association 'Process '
  private WorkflowProcess process;
  
  // Link attribute of association 'In '
  private Collection in;
  
  // Link attribute of association 'Out '
  private Collection out;
  
  // Link attribute of association 'Type '
  private int type = INVALID;
  
  // Link attribute of association 'Subflow '
  private WorkflowProcess subflow;

  
  //Activity Types - see WfMC XPDL Spec 0.03a pages 19-20
  public static final int INVALID = -1;
  public static final int NONE = 1;
  public static final int APPLICATION = 2;
  public static final int SUBFLOW = 3;
  public static final int LOOP = 4;
  public static final int ROUTE = 5;

  /** Empty constructor */
  Activity() { super(); }
  
  /**
   * Constructor with all attributes
   * @param pIsInitial Initial value for attribute 'isInitial'
   * @param pIsFinish Initial value for attribute 'isFinish'
   */
  Activity(boolean pIsInitial, boolean pIsFinish) {
    isInitial = pIsInitial;
    isFinish = pIsFinish;
  }
  
  /** Getter for attribute 'isInitial'
   * @return Value of attribute isInitial
   */
  public boolean getIsInitial() { return isInitial; }
  
  /** Setter for attribute 'isInitial'
   * @param pIsInitial Neuer Wert des Attributes isInitial
   */
  public void setIsInitial(boolean pIsInitial)  {
    if (isInitial == pIsInitial) return;
    if ( !notifyAttributeChangeIsInitial( pIsInitial ) ) return;
    isInitial = pIsInitial;
  }
  
  /** This method is called, before the attribute 'IsInitial' is set to a new value.
   * @param pIsInitial New Value for attribute 'IsInitial'
   * @return true, if change accepted, otherwise false. Default is true
   */
  private boolean notifyAttributeChangeIsInitial(boolean pIsInitial) {
    return true;
  }
  
  
  /** Getter for attribute 'isFinish'
   * @return Value of attribute isFinish
   */
  public boolean getIsFinish() { return isFinish; }
  
  /** Setter for attribute 'isFinish'
   * @param pIsFinish Neuer Wert des Attributes isFinish
   */
  public void setIsFinish(boolean pIsFinish)  {
    if (isFinish == pIsFinish) return;
    if ( !notifyAttributeChangeIsFinish( pIsFinish ) ) return;
    isFinish = pIsFinish;
  }
  
  /** This method is called, before the attribute 'IsFinish' is set to a new value.
   * @param pIsFinish New Value for attribute 'IsFinish'
   * @return true, if change accepted, otherwise false. Default is true
   */
  private boolean notifyAttributeChangeIsFinish(boolean pIsFinish) { return true; }
  
  /** Getter of association 'Process'
   * @return Current value of association 'Process'.
   * @throws RuntimeException, if value is null
   */
  public WorkflowProcess getProcess() {
    if (process == null) {
      // This should never happen. If so, fix your code :-)
      throw new RuntimeException("Invalid aggregate: Activity#Process is null!");
    }
    return process;
  }
  
  /** Setter of association 'Process'.
   * @param pProcess New value for association 'Process'
   */
  public void setProcess(WorkflowProcess pProcess) {
    if (pProcess == null && process != null) {
      process.unlinkSteps( this );
    }
    process = pProcess;
    process.linkSteps(this);
  }
  
  /**
   * Checks, if aggregate 'Process' contains elements
   * @return true, if association contains no elements, otherwise false
   */
  public boolean isProcessNull() {
    return process == null;
  }
  
  /** Internal use only */
  public void linkProcess(WorkflowProcess pProcess) {
    
    if (process != null) {
      process.unlinkSteps(this); // Alte Beziehung löschen
    }
    process = pProcess;
    
  }
  
  /** Internal use only */
  public void unlinkProcess(WorkflowProcess pProcess) {
    process = null;
    
  }
  
  /**
   * Getter of association 'In'
   * @return Currents contents of association 'In'
   */
  public Collection getIn() {
    return in != null ? in : java.util.Collections.EMPTY_LIST;
  }
  
  /**
   * Setter of association  'In'. All existing elements are dropped. An null argument
   * creates the same result as removeAllIn
   * @param pIn List containing the new elements for association  'In'.
   */
  public void setIn(Collection pIn) {
    removeAllIn();
    if (pIn != null ) {
      addAllToIn( pIn );
    }
  }
  
  /**
   * Removes all elements from assoziation 'In'
   */
  public void removeAllIn() {
    if (in == null) return; // nothing to do
    
    for(Iterator it = in.iterator(); it.hasNext();) {
      Transition lElement = (Transition) it.next();
      lElement.unlinkTo( this );
      removeIn( lElement );
    }
  }
  
  /**
   * Removes pIn from assoziation 'In'
   * @param pIn element to remove
   */
  public void removeIn(Transition pIn) {
    if (in != null) {
      in.remove( pIn );
      pIn.unlinkTo( this ); // notify other end
      notifyRemoveIn( pIn ); // notify ourselves
    }
  }
  
  /**
   * Adds all elements in pInList to association 'In'. Invalid elements (e. g.
   * wrong type) are ignored. Existing elements are kept.
   * @return Number of added elements (should be equivalent to <code>pInList.size()</code>)
   */
  public int addAllToIn(Collection pInList) {
    if (pInList == null) {
        throw new RuntimeException("Attempted to add null container to Activity#In!");
    }
    int lInserted=0;
    for(Iterator it = pInList.iterator(); it.hasNext(); ) {
      try {
        Transition lIn = (Transition)it.next();
        addIn( lIn );
        ++lInserted;
      } catch(Throwable t) {
        continue;
      }
    }
    return lInserted;
  }
  
  /**
   * Adds pIn to association 'In'
   * @param pIn Element to add
   */
  public void addIn(Transition pIn) {
    if (pIn == null) {
      throw new RuntimeException("Attempted to add null object to Activity#In!");
    }
    
    if (in == null) {
      in = new ArrayList();
    }
    in.add(pIn);
    
    pIn.linkTo(this); // notify other end
    
    notifyRemoveIn( pIn ); // notify ourselves
  }
  
  /** Hook for 'add' on association 'In' */
  private void notifyAddIn(Transition pIn) {
    //System.out.println("Add " + pIn + " to Activity#In");
  }
  
  /**
   * Hook for 'remove' on association 'In'. This is the right place
   * for cache updates or something else
   */
  private void notifyRemoveIn(Transition pIn) {
    //System.out.println("Remove " + pIn + " from Activity#In");
  }
  
  
  /** Internal use only */
  public void linkIn(Transition pIn) {
    if (in == null) {
      in = new ArrayList();
    }
    in.add(pIn);
    notifyAddIn( pIn ); // notify ourselves
  }
  
  /** Internal use only */
  public void unlinkIn(Transition pIn) {
    if (in == null) return;in.remove(pIn);
    notifyRemoveIn( pIn ); // notify ourselves
  }
  
  /**
   * Getter of association 'Out'
   * @return Currents contents of association 'Out'
   */
  public Collection getOut() { return out != null ? out : java.util.Collections.EMPTY_LIST; }
  
  /**
   * Setter of association  'Out'. All existing elements are dropped. An null argument
   * creates the same result as removeAllOut
   * @param pOut List containing the new elements for association  'Out'.
   */
  public void setOut(Collection pOut) {
    removeAllOut();
    if (pOut != null ) {
      addAllToOut( pOut );
    }
  }
  
  /**
   * Removes all elements from assoziation 'Out'
   */
  public void removeAllOut() {
    if (out == null) return; // nothing to do
    
    for(Iterator it = out.iterator(); it.hasNext();) {
      Transition lElement = (Transition) it.next();
      lElement.unlinkFrom( this );
      removeOut( lElement );
    }
  }
  
  /**
   * Removes pOut from assoziation 'Out'
   * @param pOut element to remove
   */
  public void removeOut(Transition pOut) {
    if (out != null) {
      out.remove( pOut );
      pOut.unlinkFrom( this ); // notify other end
      notifyRemoveOut( pOut ); // notify ourselves
    }
  }
  
  /**
   * Adds all elements in pOutList to association 'Out'. Invalid elements (e. g.
   * wrong type) are ignored. Existing elements are kept.
   * @return Number of added elements (should be equivalent to <code>pOutList.size()</code>)
   */
  public int addAllToOut(Collection pOutList) {
    if (pOutList == null) {
      throw new RuntimeException("Attempted to add null container to Activity#Out!");
    }
    int lInserted=0;
    for(Iterator it = pOutList.iterator(); it.hasNext(); ) {
      try {
        Transition lOut = (Transition)it.next();
        addOut( lOut );
        ++lInserted;
      } catch(Throwable t) {
        continue;
      }
    }
    return lInserted;
  }
  
  /** Adds pOut to association 'Out'
   * @param pOut Element to add
   */
  public void addOut(Transition pOut) {
    if (pOut == null) {
      throw new RuntimeException("Attempted to add null object to Activity#Out!");
    }
    
    if (out == null) {
      out = new ArrayList();
    }
    out.add(pOut);
    
    pOut.linkFrom(this); // notify other end
    
    notifyRemoveOut( pOut ); // notify ourselves
  }
  
  /** Hook for 'add' on association 'Out' */
  private void notifyAddOut(Transition pOut) {
    //System.out.println("Add " + pOut + " to Activity#Out");
  }
  
  /** Hook for 'remove' on association 'Out'. This is the right place
   * for cache updates or something else
   */
  private void notifyRemoveOut(Transition pOut) {
    //System.out.println("Remove " + pOut + " from Activity#Out");
  }
  
  
  /** Internal use only */
  public void linkOut(Transition pOut) {
    if (out == null) {
      out = new ArrayList();
    }
    out.add(pOut);
    notifyAddOut( pOut ); // notify ourselves
  }
  
  /** Internal use only */
  public void unlinkOut(Transition pOut) {
    if (out == null) return;out.remove(pOut);
    notifyRemoveOut( pOut ); // notify ourselves
  }
  
  /** Getter of association 'Type'
   * @return Current value of association 'Type'.
   */
  public int getType() { return type; }
  
  /** Setter of association 'Type'.
   * @param pType New value for association 'Type'
   */
  public void setType(int pType) { type = pType; }
  
  /** Checks, if aggregate 'Type' contains elements
   * @return true, if association contains no elements, otherwise false
   */
  public boolean isTypeValid() { return type != INVALID; }
  
  /** Internal use only */
  public void linkType(int pType) { type = pType; }
  
  /** Internal use only */
  public void unlinkType(int pType) { type = INVALID; }
  

  /** Getter of association 'Subflow'
   * @return Current value of association 'Subflow'.
   */
  public WorkflowProcess getSubflow() { return subflow; }
  
  /** Setter of association 'Subflow'.
   * @param pSubflow New value for association 'Subflow'
   */
  public void setSubflow(WorkflowProcess pSubflow) { subflow = pSubflow; }
  
  /** Internal use only */
  public void linkSubflow(WorkflowProcess pSubflow) { subflow = pSubflow; }
  
  /** Internal use only */
  public void unlinkSubflow(WorkflowProcess pSubflow) { subflow = null; }
  
  /** String representation of Activity */
  public String toString() {
    StringBuffer lRet = new StringBuffer("Activity");
    return lRet.toString();
  }
}
