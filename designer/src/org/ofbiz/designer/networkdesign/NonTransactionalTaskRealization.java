/**
 * NonTransactionalTaskRealization.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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

import java.util.Hashtable;
import org.ofbiz.wrappers.xml.xgen.ClassDecl;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class NonTransactionalTaskRealization implements INonTransactionalTaskRealization
  {
  public Hashtable _Attributes = new Hashtable();
  public String _TaskInvocation = null;
  public ICorbaInvocation _CorbaInvocation = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.NonTransactionalTaskRealization" );
    }
  
  public IClassDeclaration getDXMLInfo()
    {
    return getStaticDXMLInfo();
    }

  // element Attributes
  
  public String getAttribute( String name )
    {
    String value = (String) _Attributes.get( name );

    if( value != null ) 
      return value;

    return null;
    }
  
  public Hashtable getAttributes()
    {
    Hashtable clone = (Hashtable) _Attributes.clone();

    return clone;
    }
  
  public void setAttribute( String name, String value )
    {
    _Attributes.put( name, value );
    }
  
  public String removeAttribute( String name )
    {
    return (String) _Attributes.remove( name );
    }
  
  public String getTypeAttribute()
    {
    return getAttribute( "type" );
    }
  
  public void setTypeAttribute( String value )
    {
    setAttribute( "type", value );
    }
  
  public String removeTypeAttribute()
    {
    return removeAttribute( "type" );
    }

  // element TaskInvocation
  
  public String getTaskInvocation()
    {
    return _TaskInvocation == null ? null : _TaskInvocation;
    }
  
  public void setTaskInvocation( String arg0 )
    {
    _CorbaInvocation = null;

    _TaskInvocation = arg0 == null ? null : new String( arg0 );
    }

  // element CorbaInvocation
  
  public ICorbaInvocation getCorbaInvocation()
    {
    return _CorbaInvocation;
    }
  
  public void setCorbaInvocation( ICorbaInvocation arg0 )
    {
    _TaskInvocation = null;

    _CorbaInvocation = arg0;
    }
  }