/**
 * INetworkTaskRealization.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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
import org.ofbiz.wrappers.xml.IIDRefBinding;

public interface INetworkTaskRealization extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getLasttaskAttribute();
  public void setLasttaskAttribute( String value );
  public String removeLasttaskAttribute();
  public Object getLasttaskReference( IIDRefBinding xml );
  public String getRealizationtypeAttribute();
  public void setRealizationtypeAttribute( String value );
  public String removeRealizationtypeAttribute();
  public String getFirsttaskAttribute();
  public void setFirsttaskAttribute( String value );
  public String removeFirsttaskAttribute();
  public Object getFirsttaskReference( IIDRefBinding xml );

  // element Domain
  public void addDomain( IDomain arg0  );
  public int getDomainCount();
  public void setDomains( Vector arg0 );
  public IDomain[] getDomains();
  public void setDomains( IDomain[] arg0 );
  public Enumeration getDomainElements();
  public IDomain getDomainAt( int arg0 );
  public void insertDomainAt( IDomain arg0, int arg1 );
  public void setDomainAt( IDomain arg0, int arg1 );
  public boolean removeDomain( IDomain arg0 );
  public void removeDomainAt( int arg0 );

  // element InputMappingList
  public IInputMappingList getInputMappingList();
  public void setInputMappingList( IInputMappingList arg0 );

  // element OutputMappingList
  public IOutputMappingList getOutputMappingList();
  public void setOutputMappingList( IOutputMappingList arg0 );
  }