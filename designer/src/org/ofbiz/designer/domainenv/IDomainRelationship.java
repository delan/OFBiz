/**
 * IDomainRelationship.java	Java 1.3.0 Fri Apr 27 15:06:33 EDT 2001
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

import java.util.Hashtable;
import org.ofbiz.wrappers.xml.IIDRefBinding;

public interface IDomainRelationship extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getLowDomainAttribute();
  public void setLowDomainAttribute( String value );
  public String removeLowDomainAttribute();
  public Object getLowDomainReference( IIDRefBinding xml );
  public String getHighDomainAttribute();
  public void setHighDomainAttribute( String value );
  public String removeHighDomainAttribute();
  public Object getHighDomainReference( IIDRefBinding xml );
  public String getIdAttribute();
  public void setIdAttribute( String value );
  public String removeIdAttribute();
  }