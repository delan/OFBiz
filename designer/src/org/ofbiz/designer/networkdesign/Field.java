/**
 * Field.java	Java 1.3.0 Fri Apr 27 15:06:28 EDT 2001
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
import org.ofbiz.wrappers.xml.IIDRefBinding;
import org.ofbiz.wrappers.xml.IClassDeclaration;

public class Field implements IField
  {
  public Hashtable _Attributes = new Hashtable();
  public IOperator _Operator = null;
  
  public static IClassDeclaration getStaticDXMLInfo()
    {
    return ClassDecl.find( "org.ofbiz.designer.networkdesign.Field" );
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
  
  public String getTaskAttribute()
    {
    return getAttribute( "org.ofbiz.designer.task" );
    }
  
  public void setTaskAttribute( String value )
    {
    setAttribute( "org.ofbiz.designer.task", value );
    }
  
  public String removeTaskAttribute()
    {
    return removeAttribute( "org.ofbiz.designer.task" );
    }
  
  public Object getTaskReference( IIDRefBinding xml )
    {
    String key = (String) _Attributes.get( "org.ofbiz.designer.task" );
    return key == null ? null : xml.getIdRef( key );
    }
  
  public String getIdAttribute()
    {
    return getAttribute( "id" );
    }
  
  public void setIdAttribute( String value )
    {
    setAttribute( "id", value );
    }
  
  public String removeIdAttribute()
    {
    return removeAttribute( "id" );
    }
  
  public String getConditionAttribute()
    {
    return getAttribute( "condition" );
    }
  
  public void setConditionAttribute( String value )
    {
    setAttribute( "condition", value );
    }
  
  public String removeConditionAttribute()
    {
    return removeAttribute( "condition" );
    }

  // element Operator
  
  public IOperator getOperator()
    {
    return _Operator;
    }
  
  public void setOperator( IOperator arg0 )
    {
    _Operator = arg0;
    }
  
  public void removeOperator()
    {
    _Operator = null;
    }
  }