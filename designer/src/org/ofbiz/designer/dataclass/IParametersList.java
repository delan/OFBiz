/**
 * IParametersList.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
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

public interface IParametersList extends org.ofbiz.wrappers.xml.IDXMLInterface
  {

  // element Parameter
  public void addParameter( IParameter arg0  );
  public int getParameterCount();
  public void setParameters( Vector arg0 );
  public IParameter[] getParameters();
  public void setParameters( IParameter[] arg0 );
  public Enumeration getParameterElements();
  public IParameter getParameterAt( int arg0 );
  public void insertParameterAt( IParameter arg0, int arg1 );
  public void setParameterAt( IParameter arg0, int arg1 );
  public boolean removeParameter( IParameter arg0 );
  public void removeParameterAt( int arg0 );
  public void removeAllParameters();
  }