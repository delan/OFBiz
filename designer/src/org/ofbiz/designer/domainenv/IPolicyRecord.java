/**
 * IPolicyRecord.java	Java 1.3.0 Fri Apr 27 15:06:33 EDT 2001
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

public interface IPolicyRecord extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getPolicyTypeAttribute();
  public void setPolicyTypeAttribute( String value );
  public String removePolicyTypeAttribute();
  public String getFromDomainAttribute();
  public void setFromDomainAttribute( String value );
  public String removeFromDomainAttribute();
  public Object getFromDomainReference( IIDRefBinding xml );
  public String getIdAttribute();
  public void setIdAttribute( String value );
  public String removeIdAttribute();
  public String getToDomainAttribute();
  public void setToDomainAttribute( String value );
  public String removeToDomainAttribute();
  public Object getToDomainReference( IIDRefBinding xml );

  // element PCDATA
  public String getPCDATA();
  public void setPCDATA( String arg0 );
  }