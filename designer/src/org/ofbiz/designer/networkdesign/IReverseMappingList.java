/**
 * IReverseMappingList.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public interface IReverseMappingList extends org.ofbiz.wrappers.xml.IDXMLInterface
  {

  // element CorbaMapping
  public void addCorbaMapping( ICorbaMapping arg0  );
  public int getCorbaMappingCount();
  public void setCorbaMappings( Vector arg0 );
  public ICorbaMapping[] getCorbaMappings();
  public void setCorbaMappings( ICorbaMapping[] arg0 );
  public Enumeration getCorbaMappingElements();
  public ICorbaMapping getCorbaMappingAt( int arg0 );
  public void insertCorbaMappingAt( ICorbaMapping arg0, int arg1 );
  public void setCorbaMappingAt( ICorbaMapping arg0, int arg1 );
  public boolean removeCorbaMapping( ICorbaMapping arg0 );
  public void removeCorbaMappingAt( int arg0 );
  public void removeAllCorbaMappings();
  }