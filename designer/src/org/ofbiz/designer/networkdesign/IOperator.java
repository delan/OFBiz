/**
 * IOperator.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public interface IOperator extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getTypeAttribute();
  public void setTypeAttribute( String value );
  public String removeTypeAttribute();
  public String getIdAttribute();
  public void setIdAttribute( String value );
  public String removeIdAttribute();

  // element Field
  public void addField( IField arg0  );
  public int getFieldCount();
  public void setFields( Vector arg0 );
  public IField[] getFields();
  public void setFields( IField[] arg0 );
  public Enumeration getFieldElements();
  public IField getFieldAt( int arg0 );
  public void insertFieldAt( IField arg0, int arg1 );
  public void setFieldAt( IField arg0, int arg1 );
  public boolean removeField( IField arg0 );
  public void removeFieldAt( int arg0 );
  public void removeAllFields();
  }