/**
 * IDomain.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public interface IDomain extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getXAttribute();
  public void setXAttribute( String value );
  public String removeXAttribute();
  public String getHeightAttribute();
  public void setHeightAttribute( String value );
  public String removeHeightAttribute();
  public String getUrlAttribute();
  public void setUrlAttribute( String value );
  public String removeUrlAttribute();
  public String getWidthAttribute();
  public void setWidthAttribute( String value );
  public String removeWidthAttribute();
  public String getIdAttribute();
  public void setIdAttribute( String value );
  public String removeIdAttribute();
  public String getTasksAttribute();
  public void setTasksAttribute( String value );
  public String removeTasksAttribute();
  public Vector getTasksReference( IIDRefBinding xml );
  public String getYAttribute();
  public void setYAttribute( String value );
  public String removeYAttribute();

  // element Compartment
  public void addCompartment( ICompartment arg0  );
  public int getCompartmentCount();
  public void setCompartments( Vector arg0 );
  public ICompartment[] getCompartments();
  public void setCompartments( ICompartment[] arg0 );
  public Enumeration getCompartmentElements();
  public ICompartment getCompartmentAt( int arg0 );
  public void insertCompartmentAt( ICompartment arg0, int arg1 );
  public void setCompartmentAt( ICompartment arg0, int arg1 );
  public boolean removeCompartment( ICompartment arg0 );
  public void removeCompartmentAt( int arg0 );
  public void removeAllCompartments();
  }