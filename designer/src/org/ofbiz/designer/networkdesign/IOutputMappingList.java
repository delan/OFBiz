/**
 * IOutputMappingList.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public interface IOutputMappingList extends org.ofbiz.wrappers.xml.IDXMLInterface
  {

  // element Mapping
  public void addMapping( IMapping arg0  );
  public int getMappingCount();
  public void setMappings( Vector arg0 );
  public IMapping[] getMappings();
  public void setMappings( IMapping[] arg0 );
  public Enumeration getMappingElements();
  public IMapping getMappingAt( int arg0 );
  public void insertMappingAt( IMapping arg0, int arg1 );
  public void setMappingAt( IMapping arg0, int arg1 );
  public boolean removeMapping( IMapping arg0 );
  public void removeMappingAt( int arg0 );
  public void removeAllMappings();
  }