/**
 * IField.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
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

public interface IField extends org.ofbiz.wrappers.xml.IDXMLInterface
  {

  // element Type
  public IType getType();
  public void setType( IType arg0 );

  // element Name
  public String getName();
  public void setName( String arg0 );

  // element DefaultValue
  public String getDefaultValue();
  public void setDefaultValue( String arg0 );
  public void removeDefaultValue();
  }