/**
 * ICollaborationObject.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

import java.util.Hashtable;

public interface ICollaborationObject extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getReceiverroleAttribute();
  public void setReceiverroleAttribute( String value );
  public String removeReceiverroleAttribute();
  public String getObjectnameAttribute();
  public void setObjectnameAttribute( String value );
  public String removeObjectnameAttribute();
  public String getReceivernameAttribute();
  public void setReceivernameAttribute( String value );
  public String removeReceivernameAttribute();
  public String getObjecturlAttribute();
  public void setObjecturlAttribute( String value );
  public String removeObjecturlAttribute();
  }