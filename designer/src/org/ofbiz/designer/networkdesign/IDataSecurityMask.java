/**
 * IDataSecurityMask.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

public interface IDataSecurityMask extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getDatanameAttribute();
  public void setDatanameAttribute( String value );
  public String removeDatanameAttribute();

  // element FieldMask
  public void addFieldMask( IFieldMask arg0  );
  public int getFieldMaskCount();
  public void setFieldMasks( Vector arg0 );
  public IFieldMask[] getFieldMasks();
  public void setFieldMasks( IFieldMask[] arg0 );
  public Enumeration getFieldMaskElements();
  public IFieldMask getFieldMaskAt( int arg0 );
  public void insertFieldMaskAt( IFieldMask arg0, int arg1 );
  public void setFieldMaskAt( IFieldMask arg0, int arg1 );
  public boolean removeFieldMask( IFieldMask arg0 );
  public void removeFieldMaskAt( int arg0 );
  public void removeAllFieldMasks();
  }