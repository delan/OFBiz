/**
 * IType.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
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

package org.ofbiz.designer.dataclass;

import java.util.Vector;
import java.util.Enumeration;

public interface IType extends org.ofbiz.wrappers.xml.IDXMLInterface
  {

  // element SimpleTypeOrUrl
  public ISimpleTypeOrUrl getSimpleTypeOrUrl();
  public void setSimpleTypeOrUrl( ISimpleTypeOrUrl arg0 );

  // element Dimension
  public void addDimension( String arg0  );
  public int getDimensionCount();
  public void setDimensions( Vector arg0 );
  public String[] getDimensions();
  public void setDimensions( String[] arg0 );
  public Enumeration getDimensionElements();
  public String getDimensionAt( int arg0 );
  public void insertDimensionAt( String arg0, int arg1 );
  public void setDimensionAt( String arg0, int arg1 );
  public boolean removeDimension( String arg0 );
  public void removeDimensionAt( int arg0 );
  public void removeAllDimensions();
  }