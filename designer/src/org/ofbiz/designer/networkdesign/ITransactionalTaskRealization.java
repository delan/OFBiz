/**
 * ITransactionalTaskRealization.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public interface ITransactionalTaskRealization extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getUrlAttribute();
  public void setUrlAttribute( String value );
  public String removeUrlAttribute();
  public String getUserpasswordAttribute();
  public void setUserpasswordAttribute( String value );
  public String removeUserpasswordAttribute();
  public String getDatabaseAttribute();
  public void setDatabaseAttribute( String value );
  public String removeDatabaseAttribute();
  public String getQueryAttribute();
  public void setQueryAttribute( String value );
  public String removeQueryAttribute();
  public String getUsernameAttribute();
  public void setUsernameAttribute( String value );
  public String removeUsernameAttribute();

  // element TransactionalInput
  public void addTransactionalInput( String arg0  );
  public int getTransactionalInputCount();
  public void setTransactionalInputs( Vector arg0 );
  public String[] getTransactionalInputs();
  public void setTransactionalInputs( String[] arg0 );
  public Enumeration getTransactionalInputElements();
  public String getTransactionalInputAt( int arg0 );
  public void insertTransactionalInputAt( String arg0, int arg1 );
  public void setTransactionalInputAt( String arg0, int arg1 );
  public boolean removeTransactionalInput( String arg0 );
  public void removeTransactionalInputAt( int arg0 );
  public void removeAllTransactionalInputs();

  // element TransactionalOutput
  public void addTransactionalOutput( String arg0  );
  public int getTransactionalOutputCount();
  public void setTransactionalOutputs( Vector arg0 );
  public String[] getTransactionalOutputs();
  public void setTransactionalOutputs( String[] arg0 );
  public Enumeration getTransactionalOutputElements();
  public String getTransactionalOutputAt( int arg0 );
  public void insertTransactionalOutputAt( String arg0, int arg1 );
  public void setTransactionalOutputAt( String arg0, int arg1 );
  public boolean removeTransactionalOutput( String arg0 );
  public void removeTransactionalOutputAt( int arg0 );
  public void removeAllTransactionalOutputs();
  }