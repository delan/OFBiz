/**
 * IRoleRelationship.java	Java 1.3.0 Fri Apr 27 15:06:26 EDT 2001
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
import org.ofbiz.wrappers.xml.IIDRefBinding;

public interface IRoleRelationship extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getLowRoleAttribute();
  public void setLowRoleAttribute( String value );
  public String removeLowRoleAttribute();
  public Object getLowRoleReference( IIDRefBinding xml );
  public String getHighRoleAttribute();
  public void setHighRoleAttribute( String value );
  public String removeHighRoleAttribute();
  public Object getHighRoleReference( IIDRefBinding xml );
  public String getIdAttribute();
  public void setIdAttribute( String value );
  public String removeIdAttribute();
  }