/**
 * IPumpBoundaryInfo.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public interface IPumpBoundaryInfo extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getPumphostAttribute();
  public void setPumphostAttribute( String value );
  public String removePumphostAttribute();
  public String getReceiverportAttribute();
  public void setReceiverportAttribute( String value );
  public String removeReceiverportAttribute();
  public String getSenderportAttribute();
  public void setSenderportAttribute( String value );
  public String removeSenderportAttribute();
  public String getReceiverhostAttribute();
  public void setReceiverhostAttribute( String value );
  public String removeReceiverhostAttribute();
  public String getPumpportAttribute();
  public void setPumpportAttribute( String value );
  public String removePumpportAttribute();
  public String getSenderhostAttribute();
  public void setSenderhostAttribute( String value );
  public String removeSenderhostAttribute();
  }