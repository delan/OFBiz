/**
 * SimpleTypeOrUrl.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
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

package org.ofbiz.designer.dataclass;

import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class SimpleTypeOrUrl implements ISimpleTypeOrUrl
  {
  public String _SimpleType = null;
  public IUrl _Url = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.dataclass.SimpleTypeOrUrl" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element SimpleType
  
  public String getSimpleType()
    {
    return _SimpleType == null ? null : _SimpleType;
    }
  
  public void setSimpleType( String arg0 )
    {
    _Url = null;

    _SimpleType = arg0 == null ? null : new String( arg0 );
    }

  // element Url
  
  public IUrl getUrl()
    {
    return _Url;
    }
  
  public void setUrl( IUrl arg0 )
    {
    _SimpleType = null;

    _Url = arg0;
    }
  }