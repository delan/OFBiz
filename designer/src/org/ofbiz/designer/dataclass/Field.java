/**
 * Field.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
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

public class Field implements IField
  {
  public IType _Type = null;
  public String _Name = null;
  public String _DefaultValue = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.dataclass.Field" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element Type
  
  public IType getType()
    {
    return _Type;
    }
  
  public void setType( IType arg0 )
    {
    _Type = arg0;
    }

  // element Name
  
  public String getName()
    {
    return _Name == null ? null : _Name;
    }
  
  public void setName( String arg0 )
    {
    _Name = arg0 == null ? null : new String( arg0 );
    }

  // element DefaultValue
  
  public String getDefaultValue()
    {
    return _DefaultValue == null ? null : _DefaultValue;
    }
  
  public void setDefaultValue( String arg0 )
    {
    _DefaultValue = arg0 == null ? null : new String( arg0 );
    }
  
  public void removeDefaultValue()
    {
    _DefaultValue = null;
    }
  }