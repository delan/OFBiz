/**
 * Realization.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

public class Realization implements IRealization
  {
  public INetworkTaskRealization _NetworkTaskRealization = null;
  public ISimpleRealization _SimpleRealization = null;
  public ISyncRealization _SyncRealization = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.Realization" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element NetworkTaskRealization
  
  public INetworkTaskRealization getNetworkTaskRealization()
    {
    return _NetworkTaskRealization;
    }
  
  public void setNetworkTaskRealization( INetworkTaskRealization arg0 )
    {
    _SimpleRealization = null;
    _SyncRealization = null;

    _NetworkTaskRealization = arg0;
    }

  // element SimpleRealization
  
  public ISimpleRealization getSimpleRealization()
    {
    return _SimpleRealization;
    }
  
  public void setSimpleRealization( ISimpleRealization arg0 )
    {
    _NetworkTaskRealization = null;
    _SyncRealization = null;

    _SimpleRealization = arg0;
    }

  // element SyncRealization
  
  public ISyncRealization getSyncRealization()
    {
    return _SyncRealization;
    }
  
  public void setSyncRealization( ISyncRealization arg0 )
    {
    _NetworkTaskRealization = null;
    _SimpleRealization = null;

    _SyncRealization = arg0;
    }
  }