/**
 * IRole.java	Java 1.3.0 Fri Apr 27 15:06:26 EDT 2001
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

package org.ofbiz.designer.roledomain;

import java.util.Hashtable;

public interface IRole extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getIdAttribute();
  public void setIdAttribute( String value );
  public String removeIdAttribute();

  // element Name
  public String getName();
  public void setName( String arg0 );

  // element Description
  public String getDescription();
  public void setDescription( String arg0 );

  // element Privileges
  public String getPrivileges();
  public void setPrivileges( String arg0 );

  // element Color
  public IColor getColor();
  public void setColor( IColor arg0 );

  // element Position
  public IPosition getPosition();
  public void setPosition( IPosition arg0 );
  }