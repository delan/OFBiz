/**
 * IArc.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public interface IArc extends org.ofbiz.wrappers.xml.IDXMLInterface ,org.ofbiz.wrappers.xml.IAttributeContainer
  {

  // element Attributes
  public String getDestinationAttribute();
  public void setDestinationAttribute( String value );
  public String removeDestinationAttribute();
  public Object getDestinationReference( IIDRefBinding xml );
  public String getArctypeAttribute();
  public void setArctypeAttribute( String value );
  public String removeArctypeAttribute();
  public String getExceptionAttribute();
  public void setExceptionAttribute( String value );
  public String removeExceptionAttribute();
  public String getSourceAttribute();
  public void setSourceAttribute( String value );
  public String removeSourceAttribute();
  public Object getSourceReference( IIDRefBinding xml );
  public String getAlternativetransitionAttribute();
  public void setAlternativetransitionAttribute( String value );
  public String removeAlternativetransitionAttribute();
  public Object getAlternativetransitionReference( IIDRefBinding xml );
  public String getIdAttribute();
  public void setIdAttribute( String value );
  public String removeIdAttribute();

  // element BoundaryInfo
  public IBoundaryInfo getBoundaryInfo();
  public void setBoundaryInfo( IBoundaryInfo arg0 );
  public void removeBoundaryInfo();

  // element Mapping
  public void addMapping( IMapping arg0  );
  public int getMappingCount();
  public void setMappings( Vector arg0 );
  public IMapping[] getMappings();
  public void setMappings( IMapping[] arg0 );
  public Enumeration getMappingElements();
  public IMapping getMappingAt( int arg0 );
  public void insertMappingAt( IMapping arg0, int arg1 );
  public void setMappingAt( IMapping arg0, int arg1 );
  public boolean removeMapping( IMapping arg0 );
  public void removeMappingAt( int arg0 );
  public void removeAllMappings();
  }