/**
 * IMethodList.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
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

public interface IMethodList extends org.ofbiz.wrappers.xml.IDXMLInterface
  {

  // element Method
  public void addMethod( IMethod arg0  );
  public int getMethodCount();
  public void setMethods( Vector arg0 );
  public IMethod[] getMethods();
  public void setMethods( IMethod[] arg0 );
  public Enumeration getMethodElements();
  public IMethod getMethodAt( int arg0 );
  public void insertMethodAt( IMethod arg0, int arg1 );
  public void setMethodAt( IMethod arg0, int arg1 );
  public boolean removeMethod( IMethod arg0 );
  public void removeMethodAt( int arg0 );
  public void removeAllMethods();
  }