/**
 * INetworkDesign.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
 *
 * Copyright 1999 by ObjectSpace, Inc.,
 * 14850 Quorum Dr., Dallas, TX, 75240 U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of ObjectSpace, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with ObjectSpace.
 */

package org.ofbiz.designer.networkdesign;

import java.util.Vector;
import java.util.Enumeration;

public interface INetworkDesign extends org.ofbiz.wrappers.xml.IDXMLInterface
  {

  // element Task
  public void addTask( ITask arg0  );
  public int getTaskCount();
  public void setTasks( Vector arg0 );
  public ITask[] getTasks();
  public void setTasks( ITask[] arg0 );
  public Enumeration getTaskElements();
  public ITask getTaskAt( int arg0 );
  public void insertTaskAt( ITask arg0, int arg1 );
  public void setTaskAt( ITask arg0, int arg1 );
  public boolean removeTask( ITask arg0 );
  public void removeTaskAt( int arg0 );
  public void removeAllTasks();

  // element Arc
  public void addArc( IArc arg0  );
  public int getArcCount();
  public void setArcs( Vector arg0 );
  public IArc[] getArcs();
  public void setArcs( IArc[] arg0 );
  public Enumeration getArcElements();
  public IArc getArcAt( int arg0 );
  public void insertArcAt( IArc arg0, int arg1 );
  public void setArcAt( IArc arg0, int arg1 );
  public boolean removeArc( IArc arg0 );
  public void removeArcAt( int arg0 );
  public void removeAllArcs();
  }