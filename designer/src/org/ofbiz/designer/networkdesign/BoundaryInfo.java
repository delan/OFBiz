/**
 * BoundaryInfo.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class BoundaryInfo implements IBoundaryInfo
  {
  public IPumpBoundaryInfo _PumpBoundaryInfo = null;
  public IGenericBoundaryInfo _GenericBoundaryInfo = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.BoundaryInfo" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element PumpBoundaryInfo
  
  public IPumpBoundaryInfo getPumpBoundaryInfo()
    {
    return _PumpBoundaryInfo;
    }
  
  public void setPumpBoundaryInfo( IPumpBoundaryInfo arg0 )
    {
    _GenericBoundaryInfo = null;

    _PumpBoundaryInfo = arg0;
    }

  // element GenericBoundaryInfo
  
  public IGenericBoundaryInfo getGenericBoundaryInfo()
    {
    return _GenericBoundaryInfo;
    }
  
  public void setGenericBoundaryInfo( IGenericBoundaryInfo arg0 )
    {
    _PumpBoundaryInfo = null;

    _GenericBoundaryInfo = arg0;
    }
  }