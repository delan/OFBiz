/**
 * DataClass.java	Java 1.3.0 Fri Apr 27 15:06:23 EDT 2001
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

public class DataClass implements IDataClass
  {
  public String _Name = null;
  public String _Package = null;
  public IParent _Parent = null;
  public IFieldList _FieldList = null;
  public IMethodList _MethodList = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.dataclass.DataClass" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
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

  // element Package
  
  public String getPackage()
    {
    return _Package == null ? null : _Package;
    }
  
  public void setPackage( String arg0 )
    {
    _Package = arg0 == null ? null : new String( arg0 );
    }
  
  public void removePackage()
    {
    _Package = null;
    }

  // element Parent
  
  public IParent getParent()
    {
    return _Parent;
    }
  
  public void setParent( IParent arg0 )
    {
    _Parent = arg0;
    }
  
  public void removeParent()
    {
    _Parent = null;
    }

  // element FieldList
  
  public IFieldList getFieldList()
    {
    return _FieldList;
    }
  
  public void setFieldList( IFieldList arg0 )
    {
    _FieldList = arg0;
    }

  // element MethodList
  
  public IMethodList getMethodList()
    {
    return _MethodList;
    }
  
  public void setMethodList( IMethodList arg0 )
    {
    _MethodList = arg0;
    }
  }