/**
 * IPosition.java	Java 1.3.0 Fri Apr 27 15:06:33 EDT 2001
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

package org.ofbiz.designer.domainenv;

public interface IPosition extends org.ofbiz.wrappers.xml.IDXMLInterface
  {

  // element Number1
  public String getNumber1();
  public void setNumber1( String arg0 );

  // element Number2
  public String getNumber2();
  public void setNumber2( String arg0 );
  }